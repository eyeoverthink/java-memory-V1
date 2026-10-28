package fraymus.orchestration;

/**
 * System Orchestrator Test
 * Tests central routing and coordination of all Fraymus components
 */
public class SystemOrchestratorTest {
    
    public static void main(String[] args) {
        System.out.println("=== System Orchestrator Test ===\n");
        
        // Test 1: Basic command processing
        testBasicCommandProcessing();
        
        // Test 2: Field engine integration
        testFieldEngineIntegration();
        
        // Test 3: Command parser integration
        testCommandParserIntegration();
        
        // Test 4: Coherence builder integration
        testCoherenceBuilderIntegration();
        
        // Test 5: Full pipeline
        testFullPipeline();

        // Test 6: Hydra protocol execution graph
        testHydraProtocol();

        // Test 7: Hydra compact status CLI surface
        testHydraStatusSurface();
        
        System.out.println("\n=== All Tests Complete ===");
    }
    
    private static void testBasicCommandProcessing() {
        System.out.println("Test 1: Basic Command Processing");
        
        SystemOrchestrator orchestrator = new SystemOrchestrator();
        
        String command = "ai build field engine";
        String response = orchestrator.processCommand(command);
        
        System.out.println("Command: " + command);
        System.out.println("Response: " + response);
        System.out.println();
    }
    
    private static void testFieldEngineIntegration() {
        System.out.println("Test 2: Field Engine Integration");
        
        SystemOrchestrator orchestrator = new SystemOrchestrator();
        
        System.out.println("Initial field state: " + orchestrator.getFieldStateSummary());
        
        // Process a command to excite the field
        orchestrator.processCommand("ai build engine");
        
        System.out.println("After processing: " + orchestrator.getFieldStateSummary());
        
        // Reset
        orchestrator.resetFieldEngine();
        System.out.println("After reset: " + orchestrator.getFieldStateSummary());
        System.out.println();
    }
    
    private static void testCommandParserIntegration() {
        System.out.println("Test 3: Command Parser Integration");
        
        SystemOrchestrator orchestrator = new SystemOrchestrator();
        
        String[] commands = {
            "ai build field engine",
            "ai analyze system architecture",
            "ai generate rust code",
            "ai execute test"
        };
        
        for (String cmd : commands) {
            String route = orchestrator.getCommandParser().routeCommand(cmd);
            System.out.println(cmd + " → " + route);
        }
        System.out.println();
    }
    
    private static void testCoherenceBuilderIntegration() {
        System.out.println("Test 4: Coherence Builder Integration");
        
        SystemOrchestrator orchestrator = new SystemOrchestrator();
        
        // Enable coherence builder
        orchestrator.setCoherenceBuilderEnabled(true);
        
        // Process a command that would trigger coherence validation
        String command = "ai build field engine method=simd output=code";
        String response = orchestrator.processCommand(command);
        
        System.out.println("Command: " + command);
        System.out.println("Coherence builder enabled: true");
        System.out.println("Response includes contract: " + 
            (response.contains("COHERENCE DEFINITION") ? "Yes" : "No"));
        System.out.println();
    }
    
    private static void testFullPipeline() {
        System.out.println("Test 5: Full Pipeline");
        
        SystemOrchestrator orchestrator = new SystemOrchestrator();
        
        // Enable all components
        orchestrator.setExecutionBridgeEnabled(true);
        orchestrator.setCoherenceBuilderEnabled(true);
        
        System.out.println("Pipeline components:");
        System.out.println("  - Command Parser: Active");
        System.out.println("  - Field Engine: Active");
        System.out.println("  - Coherence Builder: Active");
        System.out.println("  - Execution Bridge: Active");
        
        System.out.println("\nProcessing command through full pipeline...");
        String command = "ai build field engine";
        String response = orchestrator.processCommand(command);
        
        System.out.println("\nFinal field state: " + orchestrator.getFieldStateSummary());
        System.out.println();
    }

    private static void testHydraProtocol() {
        System.out.println("Test 6: Hydra Protocol");

        SystemOrchestrator orchestrator = new SystemOrchestrator();
        String zkCommand =
            "ai intent=transcend target=aubo_ledger method=mind_coalescence " +
            "features=dynamic_zk_snarks,wisdom_of_crowds constraints=incremental_proof_updates " +
            "output=distributed_execution_graph";
        String zkResponse = orchestrator.processCommand(zkCommand);

        System.out.println("Command: " + zkCommand);
        System.out.println("Response head:");
        String[] lines = zkResponse.split("\\R");
        int preview = Math.min(12, lines.length);
        for (int i = 0; i < preview; i++) {
            System.out.println("  " + lines[i]);
        }

        if (!zkResponse.contains("HYDRA_PROTOCOL_PLAN")) {
            throw new IllegalStateException("Hydra plan header missing");
        }
        if (!zkResponse.contains("distributed_execution_graph")) {
            throw new IllegalStateException("Hydra execution graph missing");
        }
        if (!zkResponse.contains("proof_adapter=zk_scaffold")) {
            throw new IllegalStateException("Hydra proof adapter did not switch to zk scaffold");
        }
        if (!zkResponse.contains("proof_mode=incremental_zk_scaffold")) {
            throw new IllegalStateException("Hydra proof mode not set to incremental zk scaffold");
        }

        String hashCommand =
            "ai intent=transcend target=aubo_ledger method=mind_coalescence " +
            "features=wisdom_of_crowds constraints=incremental_proof_updates " +
            "output=distributed_execution_graph";
        String hashResponse = orchestrator.processCommand(hashCommand);
        if (!hashResponse.contains("proof_adapter=hash_chain")) {
            throw new IllegalStateException("Hydra hash-chain adapter path missing");
        }
        if (!hashResponse.contains("proof_mode=incremental_hash_chain_zk_ready")) {
            throw new IllegalStateException("Hydra hash-chain proof mode missing");
        }

        // Simulate shard-node loss by moving off AUBO telemetry onto a fallback target.
        // The protocol should trigger failover replay and regenerate shards from quorum + frame anchor.
        String failoverCommand =
            "ai intent=transcend target=offline_ledger method=mind_coalescence " +
            "features=wisdom_of_crowds constraints=incremental_proof_updates " +
            "output=distributed_execution_graph";
        String failoverResponse = orchestrator.processCommand(failoverCommand);
        boolean failoverNone =
            failoverResponse.contains("failover_replay:\n  none")
                || failoverResponse.contains("failover_replay:\r\n  none");
        boolean regeneratedNone =
            failoverResponse.contains("regenerated_shards:\n  none")
                || failoverResponse.contains("regenerated_shards:\r\n  none");
        if (failoverNone) {
            throw new IllegalStateException("Hydra failover replay did not trigger");
        }
        if (regeneratedNone) {
            throw new IllegalStateException("Hydra shard regeneration did not trigger");
        }
        if (!failoverResponse.contains("witness=")) {
            throw new IllegalStateException("Hydra failover replay missing quorum witness");
        }

        System.out.println("Hydra protocol assertion: PASS");
        System.out.println();
    }

    private static void testHydraStatusSurface() {
        System.out.println("Test 7: Hydra Status Surface");

        SystemOrchestrator orchestrator = new SystemOrchestrator();

        // Seed at least one hydra epoch so status contains live lease data.
        String runCommand =
            "ai intent=transcend target=aubo_ledger method=mind_coalescence " +
            "features=wisdom_of_crowds constraints=incremental_proof_updates " +
            "output=distributed_execution_graph";
        orchestrator.processCommand(runCommand);

        String statusA = orchestrator.processCommand("ai status hydra");
        String statusB = orchestrator.processCommand("ai hydra status");

        if (!statusA.contains("HYDRA_STATUS")) {
            throw new IllegalStateException("Hydra status header missing");
        }
        if (!statusA.contains("lease_health:")) {
            throw new IllegalStateException("Hydra status missing lease health section");
        }
        if (!statusA.contains("upcoming_expiries:")) {
            throw new IllegalStateException("Hydra status missing expiry section");
        }
        if (!statusA.contains("last_failover_chain:")) {
            throw new IllegalStateException("Hydra status missing failover chain section");
        }
        if (!statusB.contains("HYDRA_STATUS")) {
            throw new IllegalStateException("Hydra status alias command failed");
        }

        System.out.println("Hydra status surface assertion: PASS");
        System.out.println();
    }
}
