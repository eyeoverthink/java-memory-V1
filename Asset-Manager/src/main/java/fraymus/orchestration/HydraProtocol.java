package fraymus.orchestration;

import fraymus.neural.AuboLedger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * HydraProtocol
 *
 * Distributed control plane for Fraynix "mind coalescence":
 * - Live shard assignment from AUBO peer telemetry
 * - Lease-based shard ownership with automatic failover/replay
 * - Shared ProofAdapter contract (hash-chain active, zk scaffold ready)
 * - Durable control-state persistence + proof artifact reporting
 */
public class HydraProtocol implements AutoCloseable {

    private static final AtomicLong EPOCH_COUNTER = new AtomicLong(0L);
    private static final DateTimeFormatter ARTIFACT_TS =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS").withZone(ZoneOffset.UTC);

    private static final long DEFAULT_LEASE_DURATION_MS = 10_000L;
    private static final long DEFAULT_MONITOR_INTERVAL_MS = 1_000L;
    private static final int MAX_FAILOVER_EVENT_MEMORY = 512;

    private static final String STATE_SUBDIR = ".northstar-kernel/hydra-state";
    private static final String REPORTS_SUBDIR = "release-capsules/offline-production/reports";
    private static final String CONTROL_STATE_FILE = "control-state.properties";
    private static final String FAILOVER_EVENTS_FILE = "failover-events.tsv";
    private static final String LATEST_PROOF_FILE = "hydra-proof-latest.txt";
    private static final int STATUS_MAX_EXPIRIES = 8;
    private static final int STATUS_MAX_FAILOVER_EVENTS = 12;

    private static final List<ShardRoleTemplate> ROLE_TEMPLATES = List.of(
        new ShardRoleTemplate("mind_core", "core", 30, List.of()),
        new ShardRoleTemplate("distributed_memory_anchor", "mem", 20, List.of("mind_core")),
        new ShardRoleTemplate("integrity_verifier", "verify", 15, List.of("mind_core")),
        new ShardRoleTemplate("crowd_synthesis_agent_a", "synth-a", 20, List.of("distributed_memory_anchor")),
        new ShardRoleTemplate("crowd_synthesis_agent_b", "synth-b", 15, List.of("distributed_memory_anchor"))
    );

    private final Object controlLock = new Object();
    private final ProofAdapter hashChainAdapter = new HashChainProofAdapter();
    private final ProofAdapter zkScaffoldAdapter = new ZkScaffoldProofAdapter();
    private final Map<String, String> roleToPeer = new ConcurrentHashMap<>();
    private final Map<String, ShardLease> leaseTable = new ConcurrentHashMap<>();
    private final Deque<String> failoverEventLog = new ArrayDeque<>();

    private final Path workspaceRoot;
    private final Path stateRoot;
    private final Path controlStatePath;
    private final Path failoverEventsPath;
    private final Path reportsRoot;
    private final long leaseDurationMs;
    private final long monitorIntervalMs;
    private final boolean autoMonitorEnabled;
    private final ScheduledExecutorService monitorExecutor;

    private volatile String lastVerifiedFrameHash = null;
    private volatile String lastQuorumWitness = "none";
    private volatile RunSnapshot lastSnapshot = null;

    static final class ShardRoleTemplate {
        final String role;
        final String shardSuffix;
        final int weightPercent;
        final List<String> dependencyRoles;

        ShardRoleTemplate(String role, String shardSuffix, int weightPercent, List<String> dependencyRoles) {
            this.role = role;
            this.shardSuffix = shardSuffix;
            this.weightPercent = weightPercent;
            this.dependencyRoles = List.copyOf(dependencyRoles);
        }
    }

    static final class ShardLease {
        final String role;
        final String peerId;
        final long leaseStartMs;
        final long leaseExpiresMs;
        final long observedHeartbeatMs;

        ShardLease(String role, String peerId, long leaseStartMs, long leaseExpiresMs, long observedHeartbeatMs) {
            this.role = role;
            this.peerId = peerId;
            this.leaseStartMs = leaseStartMs;
            this.leaseExpiresMs = leaseExpiresMs;
            this.observedHeartbeatMs = observedHeartbeatMs;
        }
    }

    static final class LedgerPeer {
        final String peerId;
        final int peerPort;
        final int latestHeight;
        final String hashPrefix;
        final long lastSeenMs;

        LedgerPeer(String peerId, int peerPort, int latestHeight, String hashPrefix, long lastSeenMs) {
            this.peerId = peerId;
            this.peerPort = peerPort;
            this.latestHeight = latestHeight;
            this.hashPrefix = hashPrefix;
            this.lastSeenMs = lastSeenMs;
        }
    }

    static final class FailoverReplay {
        final String role;
        final String previousPeerId;
        final String replacementPeerId;
        final String anchor;
        final int quorum;
        final String quorumWitness;

        FailoverReplay(
            String role,
            String previousPeerId,
            String replacementPeerId,
            String anchor,
            int quorum,
            String quorumWitness
        ) {
            this.role = role;
            this.previousPeerId = previousPeerId;
            this.replacementPeerId = replacementPeerId;
            this.anchor = anchor;
            this.quorum = quorum;
            this.quorumWitness = quorumWitness;
        }
    }

    static final class RegeneratedShard {
        final String role;
        final String shardId;
        final String previousPeerId;
        final String replacementPeerId;
        final int quorum;
        final String regenerationHash;
        final String anchor;
        final String quorumWitness;

        RegeneratedShard(
            String role,
            String shardId,
            String previousPeerId,
            String replacementPeerId,
            int quorum,
            String regenerationHash,
            String anchor,
            String quorumWitness
        ) {
            this.role = role;
            this.shardId = shardId;
            this.previousPeerId = previousPeerId;
            this.replacementPeerId = replacementPeerId;
            this.quorum = quorum;
            this.regenerationHash = regenerationHash;
            this.anchor = anchor;
            this.quorumWitness = quorumWitness;
        }
    }

    public static final class ShardAssignment {
        public final String shardId;
        public final String role;
        public final int weightPercent;
        public final String peerId;
        public final int peerPort;
        public final List<String> dependsOn;

        public ShardAssignment(
            String shardId,
            String role,
            int weightPercent,
            String peerId,
            int peerPort,
            List<String> dependsOn
        ) {
            this.shardId = shardId;
            this.role = role;
            this.weightPercent = weightPercent;
            this.peerId = peerId;
            this.peerPort = peerPort;
            this.dependsOn = List.copyOf(dependsOn);
        }
    }

    public static final class ProofFrame {
        public final long epoch;
        public final String shardId;
        public final String frameHash;
        public final String previousHash;

        public ProofFrame(long epoch, String shardId, String frameHash, String previousHash) {
            this.epoch = epoch;
            this.shardId = shardId;
            this.frameHash = frameHash;
            this.previousHash = previousHash;
        }
    }

    static final class RunSnapshot {
        final long epoch;
        final String trigger;
        final String target;
        final String method;
        final String output;
        final int fieldDimension;
        final List<String> features;
        final List<String> constraints;
        final String telemetrySource;
        final List<LedgerPeer> peers;
        final int quorum;
        final String consensusDigest;
        final String graphSeed;
        final String quorumWitness;
        final String anchorUsed;
        final ProofAdapter adapter;
        final ProofAdapter.ProofBundle proofBundle;
        final boolean verified;
        final List<ShardAssignment> assignments;
        final List<FailoverReplay> failovers;
        final List<RegeneratedShard> regenerated;

        RunSnapshot(
            long epoch,
            String trigger,
            String target,
            String method,
            String output,
            int fieldDimension,
            List<String> features,
            List<String> constraints,
            String telemetrySource,
            List<LedgerPeer> peers,
            int quorum,
            String consensusDigest,
            String graphSeed,
            String quorumWitness,
            String anchorUsed,
            ProofAdapter adapter,
            ProofAdapter.ProofBundle proofBundle,
            boolean verified,
            List<ShardAssignment> assignments,
            List<FailoverReplay> failovers,
            List<RegeneratedShard> regenerated
        ) {
            this.epoch = epoch;
            this.trigger = trigger;
            this.target = target;
            this.method = method;
            this.output = output;
            this.fieldDimension = fieldDimension;
            this.features = List.copyOf(features);
            this.constraints = List.copyOf(constraints);
            this.telemetrySource = telemetrySource;
            this.peers = List.copyOf(peers);
            this.quorum = quorum;
            this.consensusDigest = consensusDigest;
            this.graphSeed = graphSeed;
            this.quorumWitness = quorumWitness;
            this.anchorUsed = anchorUsed;
            this.adapter = adapter;
            this.proofBundle = proofBundle;
            this.verified = verified;
            this.assignments = List.copyOf(assignments);
            this.failovers = List.copyOf(failovers);
            this.regenerated = List.copyOf(regenerated);
        }
    }

    public HydraProtocol() {
        this(resolveWorkspaceRoot(), DEFAULT_LEASE_DURATION_MS, DEFAULT_MONITOR_INTERVAL_MS, true);
    }

    HydraProtocol(Path workspaceRoot, long leaseDurationMs, long monitorIntervalMs, boolean autoMonitorEnabled) {
        this.workspaceRoot = workspaceRoot.toAbsolutePath().normalize();
        this.stateRoot = this.workspaceRoot.resolve(STATE_SUBDIR).normalize();
        this.controlStatePath = this.stateRoot.resolve(CONTROL_STATE_FILE);
        this.failoverEventsPath = this.stateRoot.resolve(FAILOVER_EVENTS_FILE);
        this.reportsRoot = this.workspaceRoot.resolve(REPORTS_SUBDIR).normalize();
        this.leaseDurationMs = Math.max(100L, leaseDurationMs);
        this.monitorIntervalMs = Math.max(200L, monitorIntervalMs);
        this.autoMonitorEnabled = autoMonitorEnabled;
        this.monitorExecutor = autoMonitorEnabled
            ? Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "Hydra-Lease-Monitor");
                t.setDaemon(true);
                return t;
            })
            : null;

        ensureStateSurfaces();
        loadControlState();
        if (this.autoMonitorEnabled) {
            this.monitorExecutor.scheduleAtFixedRate(this::safeAutoRebalanceTick, this.monitorIntervalMs, this.monitorIntervalMs, TimeUnit.MILLISECONDS);
        }
    }

    public String execute(CommandParser.ParsedCommand parsed, int fieldDimension) {
        String target = normalize(parsed.target, "aubo_ledger");
        String method = readMethod(parsed.modifiers);
        if (method == null) {
            method = "mind_coalescence";
        }

        List<String> features = readCsv(parsed.constraints, "features");
        if (features.isEmpty()) {
            features = Arrays.asList("dynamic_zk_snarks", "wisdom_of_crowds");
        }
        List<String> constraints = readCsv(parsed.constraints, "constraints");
        boolean incrementalProofUpdates = containsToken(constraints, "incremental_proof_updates");
        boolean zkRequested = containsToken(features, "dynamic_zk_snarks") || containsToken(constraints, "dynamic_zk_snarks");
        String output = normalize(parsed.outputFormat, "distributed_execution_graph");

        synchronized (controlLock) {
            RunSnapshot snapshot = runEpochLocked(
                "command",
                parsed.raw,
                target,
                method,
                output,
                fieldDimension,
                features,
                constraints,
                incrementalProofUpdates,
                zkRequested,
                Collections.emptySet()
            );
            String rendered = render(snapshot);
            writeProofArtifacts(snapshot, rendered);
            return rendered;
        }
    }

    public String renderCompactStatus() {
        synchronized (controlLock) {
            long now = System.currentTimeMillis();
            List<LedgerPeer> peers = loadLivePeers("aubo_ledger");
            Map<String, LedgerPeer> byId = new HashMap<>();
            for (LedgerPeer peer : peers) {
                byId.put(peer.peerId, peer);
            }

            int leaseTotal = leaseTable.size();
            int leaseHealthy = 0;
            int leaseExpired = 0;
            for (ShardLease lease : leaseTable.values()) {
                LedgerPeer peer = byId.get(lease.peerId);
                boolean healthy = isLeaseValid(lease, peer, now);
                if (healthy) {
                    leaseHealthy++;
                } else {
                    leaseExpired++;
                }
            }

            List<ShardLease> orderedLeases = new ArrayList<>(leaseTable.values());
            orderedLeases.sort(Comparator.comparingLong(l -> l.leaseExpiresMs));

            List<String> events = recentFailoverEventsLocked(STATUS_MAX_FAILOVER_EVENTS);

            StringBuilder sb = new StringBuilder();
            sb.append("HYDRA_STATUS").append('\n');
            sb.append("workspace_root=").append(workspaceRoot).append('\n');
            sb.append("state_root=").append(stateRoot).append('\n');
            sb.append("reports_root=").append(reportsRoot).append('\n');
            sb.append("now_ms=").append(now).append('\n');
            sb.append("last_verified_frame=").append(abbreviate(lastVerifiedFrameHash, 24)).append('\n');
            sb.append("last_quorum_witness=").append(abbreviate(lastQuorumWitness, 24)).append('\n');
            sb.append("lease_duration_ms=").append(leaseDurationMs).append('\n');
            sb.append("monitor_interval_ms=").append(monitorIntervalMs).append('\n');
            sb.append("leases_total=").append(leaseTotal).append('\n');
            sb.append("leases_healthy=").append(leaseHealthy).append('\n');
            sb.append("leases_expired=").append(leaseExpired).append('\n');
            sb.append("active_peers=").append(peers.size()).append('\n');
            sb.append('\n');

            sb.append("lease_health:").append('\n');
            if (orderedLeases.isEmpty()) {
                sb.append("  none").append('\n');
            } else {
                for (ShardLease lease : orderedLeases) {
                    LedgerPeer peer = byId.get(lease.peerId);
                    long remaining = lease.leaseExpiresMs - now;
                    boolean healthy = isLeaseValid(lease, peer, now);
                    String peerState = peer == null ? "missing" : (isPeerAlive(peer, now) ? "alive" : "stale");
                    sb.append("  role=").append(lease.role)
                        .append(" peer=").append(lease.peerId)
                        .append(" health=").append(healthy ? "healthy" : "expired")
                        .append(" peer_state=").append(peerState)
                        .append(" remaining_ms=").append(remaining)
                        .append(" observed_heartbeat=").append(lease.observedHeartbeatMs)
                        .append('\n');
                }
            }
            sb.append('\n');

            sb.append("upcoming_expiries:").append('\n');
            if (orderedLeases.isEmpty()) {
                sb.append("  none").append('\n');
            } else {
                int count = Math.min(STATUS_MAX_EXPIRIES, orderedLeases.size());
                for (int i = 0; i < count; i++) {
                    ShardLease lease = orderedLeases.get(i);
                    long remaining = lease.leaseExpiresMs - now;
                    sb.append("  role=").append(lease.role)
                        .append(" peer=").append(lease.peerId)
                        .append(" expires_in_ms=").append(remaining)
                        .append('\n');
                }
            }
            sb.append('\n');

            sb.append("last_failover_chain:").append('\n');
            if (events.isEmpty()) {
                sb.append("  none").append('\n');
            } else {
                for (String event : events) {
                    String[] p = event.split("\\t");
                    if (p.length < 9) {
                        sb.append("  raw=").append(event).append('\n');
                        continue;
                    }
                    sb.append("  ts=").append(p[0])
                        .append(" epoch=").append(p[1])
                        .append(" trigger=").append(p[2])
                        .append(" role=").append(p[3])
                        .append(" from=").append(p[4])
                        .append(" to=").append(p[5])
                        .append(" quorum=").append(p[6])
                        .append(" anchor=").append(abbreviate(p[7], 16))
                        .append(" witness=").append(abbreviate(p[8], 16))
                        .append('\n');
                }
            }
            return sb.toString();
        }
    }

    private void safeAutoRebalanceTick() {
        try {
            autoRebalanceTick();
        } catch (Exception ignored) {
            // keep monitor loop alive
        }
    }

    private RunSnapshot autoRebalanceTick() {
        synchronized (controlLock) {
            if (roleToPeer.isEmpty()) {
                return null;
            }

            List<LedgerPeer> peers = loadLivePeers("aubo_ledger");
            if (peers.isEmpty()) {
                return null;
            }
            long now = System.currentTimeMillis();
            Set<String> forcedReassign = detectExpiredRoles(peers, now);
            if (forcedReassign.isEmpty()) {
                return null;
            }

            RunSnapshot snapshot = runEpochLocked(
                "auto_lease_expiry",
                "auto|lease_monitor",
                "aubo_ledger",
                "mind_coalescence",
                "distributed_execution_graph",
                16384,
                List.of("wisdom_of_crowds"),
                List.of("incremental_proof_updates", "auto_rebalance"),
                true,
                false,
                forcedReassign
            );
            String rendered = render(snapshot);
            writeProofArtifacts(snapshot, rendered);
            return snapshot;
        }
    }

    RunSnapshot triggerAutoRebalanceForTest() {
        return autoRebalanceTick();
    }

    RunSnapshot getLastSnapshotForTest() {
        return lastSnapshot;
    }

    String getLastVerifiedFrameHashForTest() {
        return lastVerifiedFrameHash;
    }

    Map<String, String> snapshotRoleToPeerForTest() {
        return new HashMap<>(roleToPeer);
    }

    private RunSnapshot runEpochLocked(
        String trigger,
        String rawSeedInput,
        String target,
        String method,
        String output,
        int fieldDimension,
        List<String> features,
        List<String> constraints,
        boolean incrementalProofUpdates,
        boolean zkRequested,
        Set<String> forcedReassignRoles
    ) {
        long epoch = EPOCH_COUNTER.incrementAndGet();
        long now = System.currentTimeMillis();
        String graphSeed = sha256Hex(rawSeedInput + "|" + target + "|" + method + "|" + epoch + "|" + trigger);
        int quorum = Math.max(2, (ROLE_TEMPLATES.size() / 2) + 1);
        String anchor = (lastVerifiedFrameHash == null || lastVerifiedFrameHash.isBlank()) ? "hydra_genesis" : lastVerifiedFrameHash;

        List<LedgerPeer> peers = loadLivePeers(target);
        String telemetrySource = peers.isEmpty() ? "fallback_local_peer" : "aubo_ledger_live";
        if (peers.isEmpty()) {
            peers = List.of(localFallbackPeer(graphSeed, now));
        }
        List<LedgerPeer> alivePeers = new ArrayList<>();
        for (LedgerPeer peer : peers) {
            if (isPeerAlive(peer, now)) {
                alivePeers.add(peer);
            }
        }
        if (alivePeers.isEmpty()) {
            alivePeers = List.of(localFallbackPeer(graphSeed, now));
        }

        String quorumWitness = computeQuorumWitness(alivePeers, quorum, anchor);
        List<FailoverReplay> failovers = new ArrayList<>();
        List<ShardAssignment> assignments = assignShards(
            peers,
            alivePeers,
            graphSeed,
            anchor,
            quorum,
            quorumWitness,
            now,
            forcedReassignRoles,
            failovers
        );
        List<RegeneratedShard> regenerated = regenerateFromFailover(assignments, failovers, quorum, anchor, quorumWitness);

        ProofAdapter adapter = zkRequested ? zkScaffoldAdapter : hashChainAdapter;
        ProofAdapter.ProofInput proofInput = new ProofAdapter.ProofInput(
            epoch,
            graphSeed,
            incrementalProofUpdates,
            lastVerifiedFrameHash,
            assignments
        );
        ProofAdapter.ProofBundle proofBundle = adapter.generate(proofInput);
        boolean verified = adapter.verify(proofBundle, proofInput);
        if (verified && proofBundle.latestFrameHash() != null && !proofBundle.latestFrameHash().isBlank()) {
            lastVerifiedFrameHash = proofBundle.latestFrameHash();
        }
        lastQuorumWitness = quorumWitness;

        String consensusDigest = sha256Hex(
            "wisdom_of_crowds|" + graphSeed + "|" + quorum + "|" + fieldDimension + "|"
                + assignments.stream().map(a -> a.peerId).sorted().reduce("", (a, b) -> a + "|" + b)
        );

        RunSnapshot snapshot = new RunSnapshot(
            epoch,
            trigger,
            target,
            method,
            output,
            fieldDimension,
            features,
            constraints,
            telemetrySource,
            peers,
            quorum,
            consensusDigest,
            graphSeed,
            quorumWitness,
            anchor,
            adapter,
            proofBundle,
            verified,
            assignments,
            failovers,
            regenerated
        );
        lastSnapshot = snapshot;

        appendFailoverEvents(snapshot);
        persistControlState();
        return snapshot;
    }

    private List<ShardAssignment> assignShards(
        List<LedgerPeer> peers,
        List<LedgerPeer> alivePeers,
        String seed,
        String anchor,
        int quorum,
        String quorumWitness,
        long nowMs,
        Set<String> forcedReassignRoles,
        List<FailoverReplay> failovers
    ) {
        Map<String, LedgerPeer> byId = new HashMap<>();
        for (LedgerPeer peer : peers) {
            byId.put(peer.peerId, peer);
        }
        Set<String> reservedThisEpoch = new HashSet<>();
        List<ShardAssignment> assignments = new ArrayList<>();
        List<LedgerPeer> candidatePeers = alivePeers.isEmpty() ? peers : alivePeers;

        for (ShardRoleTemplate template : ROLE_TEMPLATES) {
            String role = template.role;
            LedgerPeer selected;
            String previousPeerId = roleToPeer.get(role);
            ShardLease currentLease = leaseTable.get(role);
            LedgerPeer previousPeer = previousPeerId == null ? null : byId.get(previousPeerId);

            boolean keepExisting =
                previousPeerId != null
                    && previousPeer != null
                    && !forcedReassignRoles.contains(role)
                    && isLeaseValid(currentLease, previousPeer, nowMs);

            if (keepExisting) {
                selected = previousPeer;
            } else {
                selected = choosePeer(template, candidatePeers, seed, reservedThisEpoch);
                roleToPeer.put(role, selected.peerId);
                if (previousPeerId != null && !previousPeerId.equals(selected.peerId)) {
                    failovers.add(new FailoverReplay(
                        role,
                        previousPeerId,
                        selected.peerId,
                        anchor,
                        quorum,
                        quorumWitness
                    ));
                }
            }

            reservedThisEpoch.add(selected.peerId);
            leaseTable.put(role, new ShardLease(
                role,
                selected.peerId,
                nowMs,
                nowMs + leaseDurationMs,
                selected.lastSeenMs
            ));

            String shardId = seed.substring(0, 10) + "-" + template.shardSuffix;
            assignments.add(new ShardAssignment(
                shardId,
                role,
                template.weightPercent,
                selected.peerId,
                selected.peerPort,
                template.dependencyRoles
            ));
        }
        return assignments;
    }

    private List<RegeneratedShard> regenerateFromFailover(
        List<ShardAssignment> assignments,
        List<FailoverReplay> failovers,
        int quorum,
        String anchor,
        String quorumWitness
    ) {
        if (failovers.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, ShardAssignment> byRole = new HashMap<>();
        for (ShardAssignment assignment : assignments) {
            byRole.put(assignment.role, assignment);
        }
        List<RegeneratedShard> regenerated = new ArrayList<>();
        for (FailoverReplay failover : failovers) {
            ShardAssignment assignment = byRole.get(failover.role);
            if (assignment == null) {
                continue;
            }
            String regenHash = computeRegenerationHash(
                failover.role,
                failover.previousPeerId,
                failover.replacementPeerId,
                quorum,
                anchor,
                quorumWitness
            );
            regenerated.add(new RegeneratedShard(
                assignment.role,
                assignment.shardId,
                failover.previousPeerId,
                failover.replacementPeerId,
                quorum,
                regenHash,
                anchor,
                quorumWitness
            ));
        }
        return regenerated;
    }

    static String computeRegenerationHash(
        String role,
        String previousPeerId,
        String replacementPeerId,
        int quorum,
        String anchor,
        String quorumWitness
    ) {
        return sha256Hex(
            "replay|"
                + role + "|"
                + previousPeerId + "|"
                + replacementPeerId + "|"
                + quorum + "|"
                + anchor + "|"
                + quorumWitness
        );
    }

    private Set<String> detectExpiredRoles(List<LedgerPeer> peers, long nowMs) {
        Map<String, LedgerPeer> byId = new HashMap<>();
        for (LedgerPeer peer : peers) {
            byId.put(peer.peerId, peer);
        }
        Set<String> expired = new HashSet<>();
        for (ShardRoleTemplate template : ROLE_TEMPLATES) {
            String role = template.role;
            String peerId = roleToPeer.get(role);
            if (peerId == null) {
                continue;
            }
            ShardLease lease = leaseTable.get(role);
            LedgerPeer peer = byId.get(peerId);
            if (!isLeaseValid(lease, peer, nowMs)) {
                expired.add(role);
            }
        }
        return expired;
    }

    private boolean isLeaseValid(ShardLease lease, LedgerPeer peer, long nowMs) {
        if (lease == null || peer == null) {
            return false;
        }
        if (!lease.peerId.equals(peer.peerId)) {
            return false;
        }
        if (nowMs > lease.leaseExpiresMs) {
            return false;
        }
        return isPeerAlive(peer, nowMs);
    }

    private boolean isPeerAlive(LedgerPeer peer, long nowMs) {
        return (nowMs - peer.lastSeenMs) <= leaseDurationMs;
    }

    private static LedgerPeer choosePeer(
        ShardRoleTemplate template,
        List<LedgerPeer> peers,
        String seed,
        Set<String> reservedThisEpoch
    ) {
        if (peers.size() == 1) {
            return peers.get(0);
        }
        int start = Math.floorMod((template.role + "|" + seed).hashCode(), peers.size());
        for (int i = 0; i < peers.size(); i++) {
            LedgerPeer candidate = peers.get((start + i) % peers.size());
            if (!reservedThisEpoch.contains(candidate.peerId)) {
                return candidate;
            }
        }
        return peers.get(start);
    }

    private List<LedgerPeer> loadLivePeers(String target) {
        if (target == null || !target.toLowerCase(Locale.ROOT).contains("aubo")) {
            return Collections.emptyList();
        }
        try {
            AuboLedger ledger = AuboLedger.getInstance();
            List<AuboLedger.PeerTelemetry> telemetry = ledger.getActivePeerTelemetry();
            if (telemetry == null || telemetry.isEmpty()) {
                return Collections.emptyList();
            }
            List<LedgerPeer> peers = new ArrayList<>();
            for (AuboLedger.PeerTelemetry peer : telemetry) {
                peers.add(new LedgerPeer(
                    peer.getPeerId(),
                    peer.getPort(),
                    peer.getLatestHeight(),
                    peer.getHashPrefix(),
                    peer.getLastSeenMs()
                ));
            }
            peers.sort(Comparator.comparingInt((LedgerPeer p) -> p.latestHeight).reversed().thenComparingInt(p -> p.peerPort));
            return peers;
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private static LedgerPeer localFallbackPeer(String seed, long nowMs) {
        return new LedgerPeer("local-fallback@" + seed.substring(0, 8), 0, 0, seed.substring(0, 8), nowMs);
    }

    private String computeQuorumWitness(List<LedgerPeer> peers, int quorum, String anchor) {
        if (peers == null || peers.isEmpty()) {
            return sha256Hex("quorum|none|" + anchor + "|" + lastQuorumWitness);
        }
        List<LedgerPeer> sorted = new ArrayList<>(peers);
        sorted.sort(
            Comparator.comparingInt((LedgerPeer p) -> p.latestHeight).reversed()
                .thenComparingLong(p -> p.lastSeenMs)
                .thenComparingInt(p -> p.peerPort)
        );
        int count = Math.min(quorum, sorted.size());
        StringBuilder sb = new StringBuilder("quorum|").append(count).append('|').append(anchor);
        for (int i = 0; i < count; i++) {
            LedgerPeer peer = sorted.get(i);
            sb.append('|')
                .append(peer.peerId)
                .append('@')
                .append(peer.peerPort)
                .append(':')
                .append(peer.latestHeight)
                .append(':')
                .append(peer.lastSeenMs);
        }
        return sha256Hex(sb.toString());
    }

    private String render(RunSnapshot snapshot) {
        StringBuilder sb = new StringBuilder();
        sb.append("HYDRA_PROTOCOL_PLAN").append('\n');
        sb.append("trigger=").append(snapshot.trigger).append('\n');
        sb.append("target=").append(snapshot.target).append('\n');
        sb.append("method=").append(snapshot.method).append('\n');
        sb.append("output=").append(snapshot.output).append('\n');
        sb.append("field_dimension=").append(snapshot.fieldDimension).append('\n');
        sb.append("features=").append(String.join(",", snapshot.features)).append('\n');
        sb.append("constraints=").append(snapshot.constraints.isEmpty() ? "none" : String.join(",", snapshot.constraints)).append('\n');
        sb.append("telemetry_source=").append(snapshot.telemetrySource).append('\n');
        sb.append("proof_adapter=").append(snapshot.adapter.adapterId()).append('\n');
        sb.append("proof_mode=").append(snapshot.proofBundle.mode()).append('\n');
        sb.append("verification_status=").append(snapshot.verified ? "verified" : "verification_failed").append('\n');
        sb.append("graph_seed=").append(snapshot.graphSeed.substring(0, 24)).append('\n');
        sb.append("wisdom_of_crowds_quorum=").append(snapshot.quorum).append('\n');
        sb.append("quorum_witness=").append(abbreviate(snapshot.quorumWitness, 32)).append('\n');
        sb.append("consensus_digest=").append(snapshot.consensusDigest.substring(0, 32)).append('\n');
        sb.append("latest_verified_frame_anchor=").append(abbreviate(snapshot.proofBundle.latestFrameHash(), 24)).append('\n');
        sb.append('\n');

        sb.append("live_peer_telemetry:").append('\n');
        for (LedgerPeer peer : snapshot.peers) {
            sb.append("  peer_id=").append(peer.peerId)
                .append(" port=").append(peer.peerPort)
                .append(" height=").append(peer.latestHeight)
                .append(" hash=").append(abbreviate(peer.hashPrefix, 16))
                .append(" last_seen_ms=").append(peer.lastSeenMs)
                .append('\n');
        }
        sb.append('\n');

        sb.append("distributed_execution_graph:").append('\n');
        for (ShardAssignment assignment : snapshot.assignments) {
            sb.append("  shard=").append(assignment.shardId)
                .append(" role=").append(assignment.role)
                .append(" weight=").append(assignment.weightPercent).append('%')
                .append(" peer=").append(assignment.peerId)
                .append(" port=").append(assignment.peerPort);
            if (assignment.dependsOn.isEmpty()) {
                sb.append(" depends_on=none");
            } else {
                sb.append(" depends_on=").append(String.join("|", assignment.dependsOn));
            }
            sb.append('\n');
        }
        sb.append('\n');

        sb.append("lease_table:").append('\n');
        for (ShardRoleTemplate template : ROLE_TEMPLATES) {
            ShardLease lease = leaseTable.get(template.role);
            if (lease == null) {
                sb.append("  role=").append(template.role).append(" lease=none").append('\n');
            } else {
                sb.append("  role=").append(template.role)
                    .append(" peer=").append(lease.peerId)
                    .append(" lease_start=").append(lease.leaseStartMs)
                    .append(" lease_expires=").append(lease.leaseExpiresMs)
                    .append(" observed_heartbeat=").append(lease.observedHeartbeatMs)
                    .append('\n');
            }
        }
        sb.append('\n');

        sb.append("incremental_proof_frames:").append('\n');
        for (ProofFrame frame : snapshot.proofBundle.frames()) {
            sb.append("  epoch=").append(frame.epoch)
                .append(" shard=").append(frame.shardId)
                .append(" prev=").append(abbreviate(frame.previousHash, 16))
                .append(" hash=").append(abbreviate(frame.frameHash, 16))
                .append('\n');
        }
        sb.append('\n');

        sb.append("failover_replay:").append('\n');
        if (snapshot.failovers.isEmpty()) {
            sb.append("  none").append('\n');
        } else {
            for (FailoverReplay replay : snapshot.failovers) {
                sb.append("  role=").append(replay.role)
                    .append(" from=").append(replay.previousPeerId)
                    .append(" to=").append(replay.replacementPeerId)
                    .append(" quorum=").append(replay.quorum)
                    .append(" anchor=").append(abbreviate(replay.anchor, 16))
                    .append(" witness=").append(abbreviate(replay.quorumWitness, 16))
                    .append('\n');
            }
        }
        sb.append('\n');

        sb.append("regenerated_shards:").append('\n');
        if (snapshot.regenerated.isEmpty()) {
            sb.append("  none").append('\n');
        } else {
            for (RegeneratedShard shard : snapshot.regenerated) {
                sb.append("  role=").append(shard.role)
                    .append(" shard=").append(shard.shardId)
                    .append(" from=").append(shard.previousPeerId)
                    .append(" to=").append(shard.replacementPeerId)
                    .append(" quorum=").append(shard.quorum)
                    .append(" anchor=").append(abbreviate(shard.anchor, 16))
                    .append(" witness=").append(abbreviate(shard.quorumWitness, 16))
                    .append(" regen=").append(abbreviate(shard.regenerationHash, 20))
                    .append('\n');
            }
        }
        sb.append('\n');
        sb.append("notes=").append(snapshot.proofBundle.notes()).append('\n');
        return sb.toString();
    }

    private void ensureStateSurfaces() {
        try {
            Files.createDirectories(stateRoot);
            Files.createDirectories(reportsRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create Hydra state surfaces", e);
        }
    }

    private void loadControlState() {
        if (!Files.exists(controlStatePath)) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(controlStatePath, StandardOpenOption.READ)) {
            props.load(in);
        } catch (IOException ignored) {
            return;
        }

        String epoch = props.getProperty("epoch");
        if (epoch != null && !epoch.isBlank()) {
            try {
                long loadedEpoch = Long.parseLong(epoch);
                EPOCH_COUNTER.set(Math.max(EPOCH_COUNTER.get(), loadedEpoch));
            } catch (NumberFormatException ignored) {
                // ignore malformed value
            }
        }

        String loadedHash = props.getProperty("lastVerifiedFrameHash");
        if (loadedHash != null && !loadedHash.isBlank()) {
            lastVerifiedFrameHash = loadedHash;
        }

        String loadedWitness = props.getProperty("lastQuorumWitness");
        if (loadedWitness != null && !loadedWitness.isBlank()) {
            lastQuorumWitness = loadedWitness;
        }

        for (String name : props.stringPropertyNames()) {
            if (name.startsWith("role.")) {
                String role = name.substring("role.".length());
                String peerId = props.getProperty(name);
                if (peerId != null && !peerId.isBlank()) {
                    roleToPeer.put(role, peerId);
                }
            }
        }

        for (String templateRole : ROLE_TEMPLATES.stream().map(t -> t.role).toList()) {
            String peerId = props.getProperty("lease." + templateRole + ".peerId");
            if (peerId == null || peerId.isBlank()) {
                continue;
            }
            long leaseStart = parseLongOr(props.getProperty("lease." + templateRole + ".leaseStartMs"), 0L);
            long leaseExpires = parseLongOr(props.getProperty("lease." + templateRole + ".leaseExpiresMs"), 0L);
            long observedHeartbeat = parseLongOr(props.getProperty("lease." + templateRole + ".observedHeartbeatMs"), 0L);
            leaseTable.put(templateRole, new ShardLease(templateRole, peerId, leaseStart, leaseExpires, observedHeartbeat));
        }
        loadRecentFailoverEvents();
    }

    private void loadRecentFailoverEvents() {
        failoverEventLog.clear();
        if (!Files.exists(failoverEventsPath)) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(failoverEventsPath, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return;
        }
        int start = Math.max(0, lines.size() - MAX_FAILOVER_EVENT_MEMORY);
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line != null && !line.isBlank()) {
                failoverEventLog.addLast(line);
            }
        }
    }

    private void persistControlState() {
        Properties props = new Properties();
        props.setProperty("epoch", Long.toString(EPOCH_COUNTER.get()));
        props.setProperty("lastVerifiedFrameHash", lastVerifiedFrameHash == null ? "" : lastVerifiedFrameHash);
        props.setProperty("lastQuorumWitness", lastQuorumWitness == null ? "" : lastQuorumWitness);
        props.setProperty("leaseDurationMs", Long.toString(leaseDurationMs));

        for (Map.Entry<String, String> entry : roleToPeer.entrySet()) {
            props.setProperty("role." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ShardLease> entry : leaseTable.entrySet()) {
            ShardLease lease = entry.getValue();
            String prefix = "lease." + entry.getKey() + ".";
            props.setProperty(prefix + "peerId", lease.peerId);
            props.setProperty(prefix + "leaseStartMs", Long.toString(lease.leaseStartMs));
            props.setProperty(prefix + "leaseExpiresMs", Long.toString(lease.leaseExpiresMs));
            props.setProperty(prefix + "observedHeartbeatMs", Long.toString(lease.observedHeartbeatMs));
        }

        Path temp = controlStatePath.resolveSibling(controlStatePath.getFileName().toString() + ".tmp");
        try (OutputStream out = Files.newOutputStream(
            temp,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )) {
            props.store(out, "Hydra Control State");
        } catch (IOException e) {
            return;
        }
        try {
            Files.move(temp, controlStatePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(temp, controlStatePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {
                // best effort
            }
        }
    }

    private void appendFailoverEvents(RunSnapshot snapshot) {
        if (snapshot.failovers.isEmpty()) {
            return;
        }
        List<String> lines = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (FailoverReplay failover : snapshot.failovers) {
            String line = now
                + "\t" + snapshot.epoch
                + "\t" + snapshot.trigger
                + "\t" + failover.role
                + "\t" + failover.previousPeerId
                + "\t" + failover.replacementPeerId
                + "\t" + failover.quorum
                + "\t" + failover.anchor
                + "\t" + failover.quorumWitness;
            lines.add(line);
            failoverEventLog.addLast(line);
            while (failoverEventLog.size() > MAX_FAILOVER_EVENT_MEMORY) {
                failoverEventLog.removeFirst();
            }
        }
        try {
            Files.write(
                failoverEventsPath,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException ignored) {
            // best effort
        }
    }

    private List<String> recentFailoverEventsLocked(int limit) {
        if (failoverEventLog.isEmpty() && Files.exists(failoverEventsPath)) {
            loadRecentFailoverEvents();
        }
        if (failoverEventLog.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int skip = Math.max(0, failoverEventLog.size() - limit);
        int i = 0;
        for (String event : failoverEventLog) {
            if (i++ < skip) {
                continue;
            }
            result.add(event);
        }
        return result;
    }

    private void writeProofArtifacts(RunSnapshot snapshot, String rendered) {
        String ts = ARTIFACT_TS.format(Instant.now());
        String safeTrigger = sanitizeForFilename(snapshot.trigger);
        Path epochReport = reportsRoot.resolve(
            String.format(Locale.ROOT, "hydra-proof-epoch-%06d-%s-%s.txt", snapshot.epoch, safeTrigger, ts)
        );

        StringBuilder content = new StringBuilder();
        content.append("hydra_epoch=").append(snapshot.epoch).append('\n');
        content.append("timestamp_utc=").append(ts).append('\n');
        content.append("workspace_root=").append(workspaceRoot).append('\n');
        content.append("state_root=").append(stateRoot).append('\n');
        content.append("control_state=").append(controlStatePath).append('\n');
        content.append("failover_events=").append(failoverEventsPath).append('\n');
        content.append('\n');
        content.append(rendered);

        try {
            Files.writeString(
                epochReport,
                content.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
            Files.writeString(
                reportsRoot.resolve(LATEST_PROOF_FILE),
                content.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
        } catch (IOException ignored) {
            // best effort
        }
    }

    private static Path resolveWorkspaceRoot() {
        Path start = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        Path cursor = start;
        for (int i = 0; i < 7; i++) {
            if (Files.isDirectory(cursor.resolve(".northstar-kernel"))
                || Files.isDirectory(cursor.resolve("release-capsules"))
                || Files.isDirectory(cursor.resolve("Java-Memory"))) {
                return cursor;
            }
            if (cursor.getParent() == null) {
                break;
            }
            cursor = cursor.getParent();
        }
        return start;
    }

    private static long parseLongOr(String value, long fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static String sanitizeForFilename(String input) {
        if (input == null || input.isBlank()) {
            return "unknown";
        }
        return input.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static boolean containsToken(List<String> values, String token) {
        if (values == null || token == null) {
            return false;
        }
        String needle = token.toLowerCase(Locale.ROOT);
        for (String value : values) {
            if (value != null && value.toLowerCase(Locale.ROOT).contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> readCsv(Map<String, String> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return new ArrayList<>();
        }
        String value = map.get(key);
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        String[] parts = value.split(",");
        List<String> cleaned = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                cleaned.add(trimmed);
            }
        }
        return cleaned;
    }

    private static String readMethod(List<String> modifiers) {
        if (modifiers == null) {
            return null;
        }
        for (String mod : modifiers) {
            if (mod != null && mod.startsWith("method:")) {
                return mod.substring("method:".length());
            }
        }
        return null;
    }

    private static String normalize(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    static String abbreviate(String value, int max) {
        if (value == null || value.isBlank()) {
            return "none";
        }
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }

    static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    public void shutdown() {
        close();
    }

    @Override
    public void close() {
        if (monitorExecutor != null) {
            monitorExecutor.shutdownNow();
        }
    }
}
