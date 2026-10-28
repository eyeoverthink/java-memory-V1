package brain_architecture.examples;

import brain_architecture.FraynixSystem;
import brain_architecture.kernel.MemoryBenchmarkReport;
import brain_architecture.kernel.MutationCandidate;
import brain_architecture.kernel.MutationOperation;
import brain_architecture.kernel.ShadowMutationEngine;
import brain_architecture.kernel.TaskEnvelope;
import brain_architecture.kernel.TaskMode;
import brain_architecture.kernel.ValidationSuite;

import java.nio.file.Paths;
import java.util.List;

/**
 * FraynixSystem Example Usage
 * 
 * This class demonstrates the preferred north-star offline kernel flow.
 */
public class FraynixSystemExample {
    
    public static void main(String[] args) {
        // Create the FraynixSystem
        FraynixSystem fraynix = new FraynixSystem();
        
        // Example 1: Basic initialization and typed offline task
        example1OfflineTask(fraynix);
        
        // Example 2: Memory benchmark
        example2MemoryBenchmark(fraynix);
        
        // Example 3: Shadow mutation
        example3ShadowMutation(fraynix);
        
        // Shutdown the system
        fraynix.shutdown();
    }
    
    /**
     * Example 1: Basic initialization and offline kernel task
     */
    private static void example1OfflineTask(FraynixSystem fraynix) {
        System.out.println("\n=== Example 1: Offline Kernel Task ===");
        
        // Initialize the system
        fraynix.initialize();

        try {
            fraynix.ingestArtifactToKernel(Paths.get("README.md"));

            TaskEnvelope task = TaskEnvelope.builder("Inspect the local workspace and keep execution offline.")
                .addConstraint("offline only")
                .addArtifact(Paths.get("README.md"))
                .mode(TaskMode.SUPERVISED)
                .build();

            var result = fraynix.runOfflineTask(task, ValidationSuite.empty()).get();
            System.out.println("Strategy: " + result.getStrategy());
            System.out.println("Adapter: " + result.getAdapterId());
            System.out.println("Outcome: " + result.getTrace().getOutcome());
            System.out.println("Kernel Status: " + fraynix.getOfflineKernel().getStatus());
        } catch (Exception e) {
            System.err.println("Error running offline task: " + e.getMessage());
        }
    }
    
    /**
     * Example 2: Compare retrieval profiles.
     */
    private static void example2MemoryBenchmark(FraynixSystem fraynix) {
        System.out.println("\n=== Example 2: Memory Benchmark ===");
        try {
            MemoryBenchmarkReport report = fraynix.benchmarkOfflineMemory(
                "offline kernel",
                List.of(Paths.get("README.md")),
                3
            );
            System.out.println("Winner: " + report.getWinner());
            System.out.println(report.toSummaryMap());
        } catch (Exception e) {
            System.err.println("Error benchmarking memory: " + e.getMessage());
        }
    }
    
    /**
     * Example 3: Apply a mutation candidate through the shadow engine.
     */
    private static void example3ShadowMutation(FraynixSystem fraynix) {
        System.out.println("\n=== Example 3: Shadow Mutation ===");
        try {
            MutationCandidate candidate = new MutationCandidate(
                MutationCandidate.Scope.GENERATED_MODULES_ONLY,
                List.of(
                    MutationOperation.appendFile(
                        Paths.get(".northstar-kernel", "notes.txt"),
                        "mutation proof\n"
                    )
                ),
                "Record a local mutation proof",
                List.of("Touches kernel state only")
            );

            ShadowMutationEngine.MutationReport report = fraynix.applyMutationCandidate(
                candidate,
                ValidationSuite.empty()
            );
            System.out.println("Mutation promoted: " + report.promoted());
            System.out.println("Mutation verifier success: " + report.verifierResult().isSuccess());
        } catch (Exception e) {
            System.err.println("Error applying mutation candidate: " + e.getMessage());
        }
    }
}
