package fraymus.orchestration;

import fraymus.neural.AuboLedger;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Hydra Fault Injection Test
 *
 * Simulates:
 * - peer joins via HEARTBEAT
 * - heartbeat loss / lease expiry
 * - split + rejoin
 *
 * Assertions:
 * - quorum witness changes after topology change
 * - failover + regenerated shards emitted
 * - regeneration hash remains stable when recomputed from same anchor + witness
 * - control state persists across protocol restart
 */
public class HydraFaultInjectionTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Hydra Fault Injection Test ===");

        Path workspace = Files.createTempDirectory("hydra-fixture-");
        Files.createDirectories(workspace.resolve(".northstar-kernel"));
        Files.createDirectories(workspace.resolve("release-capsules/offline-production/reports"));

        AuboLedger ledger = AuboLedger.getInstance();
        int swarmPort = ledger.getSwarmPort();

        // Initial live peers join.
        sendHeartbeat(swarmPort, 43101, 11, "aa11bb22cc33dd44");
        sendHeartbeat(swarmPort, 43102, 12, "bb22cc33dd44ee55");
        Thread.sleep(180L);

        HydraProtocol hydra = new HydraProtocol(workspace, 350L, 100L, false);
        CommandParser.ParsedCommand command = CommandParser.parse(
            "ai intent=transcend target=aubo_ledger method=mind_coalescence "
                + "features=dynamic_zk_snarks,wisdom_of_crowds "
                + "constraints=incremental_proof_updates output=distributed_execution_graph"
        );
        String initial = hydra.execute(command, 16384);
        if (!initial.contains("HYDRA_PROTOCOL_PLAN")) {
            throw new IllegalStateException("Initial Hydra run failed");
        }

        HydraProtocol.RunSnapshot firstSnapshot = hydra.getLastSnapshotForTest();
        if (firstSnapshot == null || firstSnapshot.quorumWitness == null || firstSnapshot.quorumWitness.isBlank()) {
            throw new IllegalStateException("Initial quorum witness missing");
        }
        String firstWitness = firstSnapshot.quorumWitness;

        String droppedPeerId = firstSnapshot.assignments.stream()
            .map(a -> a.peerId)
            .filter(id -> id != null && id.startsWith("peer@"))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No non-local shard peer found to fault inject"));
        int droppedPort = Integer.parseInt(droppedPeerId.substring("peer@".length()));
        Set<Integer> retainedPorts = new HashSet<>();
        for (HydraProtocol.ShardAssignment assignment : firstSnapshot.assignments) {
            if (assignment.peerId != null && assignment.peerId.startsWith("peer@")) {
                int port = Integer.parseInt(assignment.peerId.substring("peer@".length()));
                if (port != droppedPort) {
                    retainedPorts.add(port);
                }
            }
        }

        // Simulate split/loss: peer 43101 disappears; 43103 joins.
        Thread.sleep(460L); // > lease duration, so 43101 lease should expire
        for (Integer retained : retainedPorts) {
            sendHeartbeat(swarmPort, retained, 13, "bb22cc33dd44ee66");
        }
        sendHeartbeat(swarmPort, 43103, 14, "cc33dd44ee55ff66");
        Thread.sleep(180L);

        HydraProtocol.RunSnapshot failoverSnapshot = hydra.triggerAutoRebalanceForTest();
        if (failoverSnapshot == null) {
            throw new IllegalStateException("Auto rebalance did not run on lease expiry");
        }
        if (failoverSnapshot.failovers.isEmpty()) {
            throw new IllegalStateException("No failover emitted during lease expiry");
        }
        if (failoverSnapshot.regenerated.isEmpty()) {
            throw new IllegalStateException("No regenerated shard emitted during failover");
        }
        if (firstWitness.equals(failoverSnapshot.quorumWitness)) {
            throw new IllegalStateException("Quorum witness did not change after topology change");
        }

        HydraProtocol.RegeneratedShard firstRegen = failoverSnapshot.regenerated.get(0);
        String recomputed = HydraProtocol.computeRegenerationHash(
            firstRegen.role,
            firstRegen.previousPeerId,
            firstRegen.replacementPeerId,
            firstRegen.quorum,
            failoverSnapshot.anchorUsed,
            firstRegen.quorumWitness
        );
        if (!recomputed.equals(firstRegen.regenerationHash)) {
            throw new IllegalStateException("Regeneration hash is not stable for the same anchor + witness");
        }

        // Rejoin previously lost peer and verify no forced churn when leases are healthy.
        sendHeartbeat(swarmPort, 43101, 15, "aa11bb22cc33dd99");
        sendHeartbeat(swarmPort, 43102, 15, "bb22cc33dd44ee99");
        sendHeartbeat(swarmPort, 43103, 15, "cc33dd44ee55ff99");
        Thread.sleep(180L);
        HydraProtocol.RunSnapshot postRejoin = hydra.triggerAutoRebalanceForTest();
        if (postRejoin != null && !postRejoin.failovers.isEmpty()) {
            throw new IllegalStateException("Unexpected failover churn on stable rejoin");
        }

        // Restart protocol and ensure control state rehydrates deterministically.
        String lastHashBeforeRestart = hydra.getLastVerifiedFrameHashForTest();
        HydraProtocol restarted = new HydraProtocol(workspace, 350L, 100L, false);
        if (lastHashBeforeRestart == null || !lastHashBeforeRestart.equals(restarted.getLastVerifiedFrameHashForTest())) {
            throw new IllegalStateException("lastVerifiedFrameHash did not persist across restart");
        }
        if (restarted.snapshotRoleToPeerForTest().isEmpty()) {
            throw new IllegalStateException("roleToPeer map did not persist across restart");
        }

        long proofArtifacts = Files.list(workspace.resolve("release-capsules/offline-production/reports"))
            .filter(p -> p.getFileName().toString().startsWith("hydra-proof-epoch-"))
            .count();
        if (proofArtifacts == 0) {
            throw new IllegalStateException("No proof artifacts were written to the release surface");
        }

        restarted.shutdown();
        hydra.shutdown();

        // Cleanup temporary fixture.
        deleteRecursively(workspace);
        System.out.println("Hydra fault injection assertion: PASS");
    }

    private static void sendHeartbeat(int swarmPort, int peerPort, int height, String hashPrefix) throws IOException {
        String msg = "HEARTBEAT|" + peerPort + "|" + height + "|" + hashPrefix + "|" + System.currentTimeMillis();
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(
                data,
                data.length,
                InetAddress.getByName("127.0.0.1"),
                swarmPort
            );
            socket.send(packet);
        }
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (root == null || !Files.exists(root)) {
            return;
        }
        Files.walk(root)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // best effort cleanup
                }
            });
    }
}
