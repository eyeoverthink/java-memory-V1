package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Verifies repair memory lessons persist and score similar proposals higher than unrelated ones.
 */
public final class RepairMemoryStoreTest {
    public static void main(String[] args) throws Exception {
        Path stateRoot = java.nio.file.Files.createTempDirectory("repair-memory-store");

        TaskEnvelope learnedTask = TaskEnvelope.builder("repair unresolved method buildValue and keep the build offline")
            .addArtifact(Path.of("src/main/java/demo/UseTool.java"))
            .mode(TaskMode.MUTATION)
            .build();
        MutationProposal learnedProposal = missingMethodProposal(Path.of("src/main/java/demo/UseTool.java"), "UseTool", "buildValue");
        VerifierResult baseline = missingMethodVerifier(Path.of("src/main/java/demo/UseTool.java"), "buildValue", "UseTool");

        RepairMemoryStore store = new RepairMemoryStore(stateRoot);
        store.recordSuccessfulRepair(learnedTask, learnedProposal, baseline);
        assertTrue(store.size() == 1, "Expected one recorded repair lesson");
        assertTrue(store.templateCount() == 1, "Expected one recorded repair template");

        RepairMemoryStore reloaded = new RepairMemoryStore(stateRoot);
        assertTrue(reloaded.size() == 1, "Expected repair lesson to persist across reload");
        assertTrue(reloaded.templateCount() == 1, "Expected repair template to persist across reload");

        TaskEnvelope similarTask = TaskEnvelope.builder("repair unresolved method buildValue in another tool and stay offline")
            .addArtifact(Path.of("src/main/java/demo/UseAgain.java"))
            .mode(TaskMode.MUTATION)
            .build();
        MutationProposal similarProposal = missingMethodProposal(Path.of("src/main/java/demo/UseAgain.java"), "UseAgain", "buildValue");
        RepairMemoryStore.RepairMatch similarMatch = reloaded.bestMatch(similarTask, similarProposal);
        assertTrue(similarMatch.matched(), "Expected repair memory to match a similar method repair");
        assertTrue(similarMatch.bonus() >= 0.18, "Expected a meaningful repair-memory bonus for similar repair");
        RepairMemoryStore.TemplateMatch templateMatch = reloaded.bestTemplate(
            similarTask,
            "verifier.compiler.method_missing",
            "src/main/java/demo/UseAgain.java loadValue cannot find symbol method loadValue()"
        );
        assertTrue(templateMatch.matched(), "Expected a method repair template match");
        assertTrue("String".equals(templateMatch.template().attributes().get("returnType")), "Expected template to carry String return type");

        TaskEnvelope unrelatedTask = TaskEnvelope.builder("align package declaration drift and keep the build offline")
            .addArtifact(Path.of("src/main/java/demo/Helper.java"))
            .mode(TaskMode.MUTATION)
            .build();
        MutationProposal unrelatedProposal = new MutationProposal(
            "Align package declaration for Helper.java",
            "Rewrite package declaration to match the source path.",
            0.95,
            0.65,
            List.of(Path.of("src/main/java/demo/Helper.java")),
            new MutationCandidate(
                MutationCandidate.Scope.WHOLE_CODEBASE,
                List.of(MutationOperation.writeFile(Path.of("src/main/java/demo/Helper.java"), "package demo;")),
                "Repair Java package and source-path mismatch",
                List.of("Bounded verifier-guided repair: align-package-declaration")
            ),
            List.of("verifier.failure", "verifier.compiler.package", "verifier.diagnostic")
        );
        RepairMemoryStore.RepairMatch unrelatedMatch = reloaded.bestMatch(unrelatedTask, unrelatedProposal);
        assertTrue(similarMatch.bonus() > unrelatedMatch.bonus(), "Expected similar repair to score above unrelated repair");

        RepairMemoryStore.RepairLesson lesson = reloaded.snapshotLessons().get(0);
        assertTrue("verifier.compiler.method_missing".equals(lesson.repairKind()), "Expected stored repair kind to match method repair");
        assertTrue(lesson.successCount() == 1, "Expected first stored lesson to have one success");

        System.out.println("RepairMemoryStoreTest passed");
    }

    private static MutationProposal missingMethodProposal(Path path, String className, String methodName) {
        return new MutationProposal(
            "Add missing method stub " + methodName + " to " + className,
            "Add a bounded method stub and re-validate.",
            0.92,
            0.63,
            List.of(path),
            new MutationCandidate(
                MutationCandidate.Scope.WHOLE_CODEBASE,
                List.of(MutationOperation.writeFile(path, """
                    package demo;

                    public final class %s {
                        public String render() {
                            return %s();
                        }

                        private String %s() {
                            return "";
                        }
                    }
                    """.formatted(className, methodName, methodName))),
                "Repair unresolved method symbol by generating a bounded stub",
                List.of("Bounded verifier-guided repair: add-missing-method-stub")
            ),
            List.of("verifier.failure", "verifier.compiler.method_missing", "verifier.diagnostic")
        );
    }

    private static VerifierResult missingMethodVerifier(Path path, String methodName, String className) {
        return new VerifierResult(
            false,
            List.of(),
            List.of("command.kernel.verify.exit_1"),
            List.of(),
            List.of(new VerifierDiagnostic(
                "kernel.verify",
                path,
                5,
                16,
                VerifierDiagnostic.Severity.ERROR,
                "cannot find symbol",
                "  symbol:   method " + methodName + "()\n  location: class " + className
            )),
            Map.of("cmd.kernel.verify", 12L)
        );
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
