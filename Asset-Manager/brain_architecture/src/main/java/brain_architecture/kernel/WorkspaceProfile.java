package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Describes the local workspace and its reproducible validation surface.
 */
public final class WorkspaceProfile {
    private final Path workspaceRoot;
    private final Set<String> languages;
    private final boolean hasOfflineToolchainManifest;
    private final boolean hasGradleBuild;
    private final boolean hasCargoProject;
    private final boolean hasJavaSources;
    private final ValidationSuite recommendedValidationSuite;
    private final Map<String, Boolean> toolsAvailable;

    public WorkspaceProfile(
            Path workspaceRoot,
            Set<String> languages,
            boolean hasOfflineToolchainManifest,
            boolean hasGradleBuild,
            boolean hasCargoProject,
            boolean hasJavaSources,
            ValidationSuite recommendedValidationSuite,
            Map<String, Boolean> toolsAvailable) {
        this.workspaceRoot = workspaceRoot;
        this.languages = Collections.unmodifiableSet(languages);
        this.hasOfflineToolchainManifest = hasOfflineToolchainManifest;
        this.hasGradleBuild = hasGradleBuild;
        this.hasCargoProject = hasCargoProject;
        this.hasJavaSources = hasJavaSources;
        this.recommendedValidationSuite = recommendedValidationSuite;
        this.toolsAvailable = Map.copyOf(toolsAvailable);
    }

    public Path getWorkspaceRoot() {
        return workspaceRoot;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public boolean hasOfflineToolchainManifest() {
        return hasOfflineToolchainManifest;
    }

    public boolean hasGradleBuild() {
        return hasGradleBuild;
    }

    public boolean hasCargoProject() {
        return hasCargoProject;
    }

    public boolean hasJavaSources() {
        return hasJavaSources;
    }

    public ValidationSuite getRecommendedValidationSuite() {
        return recommendedValidationSuite;
    }

    public Map<String, Boolean> getToolsAvailable() {
        return toolsAvailable;
    }
}
