package brain_architecture.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A candidate mutation that must be validated in shadow before promotion.
 */
public final class MutationCandidate {
    public enum Scope {
        GENERATED_MODULES_ONLY,
        CONFIG_PROMPTS_ONLY,
        WHOLE_CODEBASE
    }

    private final UUID id;
    private final Scope scope;
    private final List<MutationOperation> operations;
    private final String expectedGain;
    private final List<String> risks;

    public MutationCandidate(Scope scope, List<MutationOperation> operations, String expectedGain, List<String> risks) {
        this.id = UUID.randomUUID();
        this.scope = Objects.requireNonNull(scope, "scope");
        this.operations = Collections.unmodifiableList(new ArrayList<>(operations));
        this.expectedGain = expectedGain == null ? "" : expectedGain;
        this.risks = Collections.unmodifiableList(new ArrayList<>(risks == null ? List.of() : risks));
    }

    public UUID getId() {
        return id;
    }

    public Scope getScope() {
        return scope;
    }

    public List<MutationOperation> getOperations() {
        return operations;
    }

    public String getExpectedGain() {
        return expectedGain;
    }

    public List<String> getRisks() {
        return risks;
    }
}
