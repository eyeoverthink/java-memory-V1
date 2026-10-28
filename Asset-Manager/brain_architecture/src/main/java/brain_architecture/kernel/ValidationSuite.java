package brain_architecture.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Validation bundle for builds, tests, benchmarks, and invariants.
 */
public final class ValidationSuite {
    private final List<ValidationCommand> buildChecks;
    private final List<ValidationCommand> tests;
    private final List<ValidationCommand> benchmarks;
    private final List<String> invariants;

    public ValidationSuite(
            List<ValidationCommand> buildChecks,
            List<ValidationCommand> tests,
            List<ValidationCommand> benchmarks,
            List<String> invariants) {
        this.buildChecks = Collections.unmodifiableList(new ArrayList<>(buildChecks == null ? List.of() : buildChecks));
        this.tests = Collections.unmodifiableList(new ArrayList<>(tests == null ? List.of() : tests));
        this.benchmarks = Collections.unmodifiableList(new ArrayList<>(benchmarks == null ? List.of() : benchmarks));
        this.invariants = Collections.unmodifiableList(new ArrayList<>(invariants == null ? List.of() : invariants));
    }

    public static ValidationSuite empty() {
        return new ValidationSuite(List.of(), List.of(), List.of(), List.of("offline"));
    }

    public List<ValidationCommand> getBuildChecks() {
        return buildChecks;
    }

    public List<ValidationCommand> getTests() {
        return tests;
    }

    public List<ValidationCommand> getBenchmarks() {
        return benchmarks;
    }

    public List<String> getInvariants() {
        return invariants;
    }

    public boolean isEmpty() {
        return buildChecks.isEmpty() && tests.isEmpty() && benchmarks.isEmpty();
    }

    public List<ValidationCommand> allCommands() {
        List<ValidationCommand> combined = new ArrayList<>();
        combined.addAll(buildChecks);
        combined.addAll(tests);
        combined.addAll(benchmarks);
        return combined;
    }

    public ValidationSuite merge(ValidationSuite overlay) {
        if (overlay == null) {
            return this;
        }
        List<ValidationCommand> mergedBuildChecks = mergeCommands(buildChecks, overlay.buildChecks);
        List<ValidationCommand> mergedTests = mergeCommands(tests, overlay.tests);
        List<ValidationCommand> mergedBenchmarks = mergeCommands(benchmarks, overlay.benchmarks);

        List<String> mergedInvariants = new ArrayList<>(invariants);
        for (String invariant : overlay.invariants) {
            if (!mergedInvariants.contains(invariant)) {
                mergedInvariants.add(invariant);
            }
        }

        return new ValidationSuite(mergedBuildChecks, mergedTests, mergedBenchmarks, mergedInvariants);
    }

    private List<ValidationCommand> mergeCommands(List<ValidationCommand> base, List<ValidationCommand> overlay) {
        List<ValidationCommand> merged = new ArrayList<>(base);
        for (ValidationCommand command : overlay) {
            boolean exists = merged.stream().anyMatch(existing -> existing.getLabel().equals(command.getLabel()));
            if (!exists) {
                merged.add(command);
            }
        }
        return merged;
    }
}
