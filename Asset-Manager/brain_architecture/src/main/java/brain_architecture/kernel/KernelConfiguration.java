package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Runtime configuration for the offline kernel.
 */
public final class KernelConfiguration {
    private final Path workspaceRoot;
    private final Path stateRoot;
    private final long randomSeed;
    private final boolean offlineEnforced;
    private final boolean allowWholeCodebaseMutation;
    private final boolean causalPolicyEnabled;
    private final Set<String> ignoredNames;

    public KernelConfiguration(
            Path workspaceRoot,
            Path stateRoot,
            long randomSeed,
            boolean offlineEnforced,
            boolean allowWholeCodebaseMutation,
            boolean causalPolicyEnabled,
            Set<String> ignoredNames) {
        this.workspaceRoot = workspaceRoot.toAbsolutePath().normalize();
        this.stateRoot = stateRoot.toAbsolutePath().normalize();
        this.randomSeed = randomSeed;
        this.offlineEnforced = offlineEnforced;
        this.allowWholeCodebaseMutation = allowWholeCodebaseMutation;
        this.causalPolicyEnabled = causalPolicyEnabled;
        this.ignoredNames = Set.copyOf(ignoredNames);
    }

    public static KernelConfiguration defaultForWorkspace(Path workspaceRoot) {
        Set<String> ignored = new LinkedHashSet<>();
        ignored.add(".git");
        ignored.add(".gradle");
        ignored.add(".idea");
        ignored.add(".windsurf");
        ignored.add("build");
        ignored.add(".northstar-kernel");
        return new KernelConfiguration(
            workspaceRoot,
            workspaceRoot.resolve(".northstar-kernel"),
            0xF0A1B2C3D4L,
            true,
            true,
            true,
            ignored
        );
    }

    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }

    public Path getStateRoot() {
        return stateRoot;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public boolean isOfflineEnforced() {
        return offlineEnforced;
    }

    public boolean isAllowWholeCodebaseMutation() {
        return allowWholeCodebaseMutation;
    }

    public boolean isCausalPolicyEnabled() {
        return causalPolicyEnabled;
    }

    public Set<String> getIgnoredNames() {
        return ignoredNames;
    }

    public KernelConfiguration withWorkspaceRoot(Path workspaceRoot) {
        Path normalizedWorkspace = workspaceRoot.toAbsolutePath().normalize();
        return new KernelConfiguration(
            normalizedWorkspace,
            normalizedWorkspace.resolve(".northstar-kernel"),
            randomSeed,
            offlineEnforced,
            allowWholeCodebaseMutation,
            causalPolicyEnabled,
            ignoredNames
        );
    }
}
