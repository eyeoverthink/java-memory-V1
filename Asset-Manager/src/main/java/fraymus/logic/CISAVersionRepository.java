package fraymus.logic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * C-ISA Version-Controlled Repository
 * 
 * Manages versioning of the Cognitive Instruction Set Architecture pipeline.
 * Supports rollback, branching, and metadata tracking for self-modification.
 * 
 * Phase 2.1: Version-Controlled Repository for C-ISA Pipeline
 * 
 * Key capabilities:
 * - Semantic versioning (MAJOR.MINOR.PATCH)
 * - Branch support for experimental modifications
 * - Rollback to any previous version
 * - Metadata tracking (performance, safety, stability)
 * - Diff between versions
 * - Merge capabilities
 */
public class CISAVersionRepository {
    
    // Version storage
    private final Map<String, ISAVersion> versions;
    private final Map<String, ISABranch> branches;
    
    // Current active version
    private String currentVersionId;
    private String currentBranchName;
    
    // Version counter
    private final AtomicLong versionCounter;
    
    // Repository metadata
    private final RepositoryMetadata metadata;
    
    public CISAVersionRepository() {
        this.versions = new ConcurrentHashMap<>();
        this.branches = new ConcurrentHashMap<>();
        this.versionCounter = new AtomicLong(0);
        this.metadata = new RepositoryMetadata();
        
        // Initialize main branch
        ISABranch mainBranch = new ISABranch("main", null);
        branches.put("main", mainBranch);
        currentBranchName = "main";
        
        // Create initial version
        CognitiveISA.Program initialProgram = new CognitiveISA.Program();
        createVersion("0.1.0", initialProgram, "Initial C-ISA baseline");
    }
    
    /**
     * Create a new version with semantic versioning.
     * 
     * @param version Semantic version (MAJOR.MINOR.PATCH)
     * @param program C-ISA program
     * @param description Version description
     * @return Version ID
     */
    public String createVersion(String version, CognitiveISA.Program program, String description) {
        String versionId = generateVersionId();
        
        ISAVersion isaVersion = new ISAVersion(
            versionId,
            version,
            program,
            description,
            System.currentTimeMillis(),
            getCurrentVersionId()
        );
        
        versions.put(versionId, isaVersion);
        
        // Update branch pointer
        ISABranch branch = branches.get(currentBranchName);
        if (branch != null) {
            branch.setHeadVersionId(versionId);
        }
        
        currentVersionId = versionId;
        versionCounter.incrementAndGet();
        
        return versionId;
    }
    
    /**
     * Create a new branch from current version.
     * 
     * @param branchName Branch name
     * @return Branch object
     */
    public ISABranch createBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            throw new IllegalArgumentException("Branch already exists: " + branchName);
        }
        
        ISABranch branch = new ISABranch(branchName, currentVersionId);
        branches.put(branchName, branch);
        
        return branch;
    }
    
    /**
     * Switch to a different branch.
     * 
     * @param branchName Branch name
     */
    public void switchBranch(String branchName) {
        ISABranch branch = branches.get(branchName);
        if (branch == null) {
            throw new IllegalArgumentException("Branch not found: " + branchName);
        }
        
        currentBranchName = branchName;
        currentVersionId = branch.getHeadVersionId();
    }
    
    /**
     * Rollback to a specific version.
     * 
     * @param versionId Version ID to rollback to
     * @return The version rolled back to
     */
    public ISAVersion rollback(String versionId) {
        ISAVersion version = versions.get(versionId);
        if (version == null) {
            throw new IllegalArgumentException("Version not found: " + versionId);
        }
        
        currentVersionId = versionId;
        
        // Update branch pointer
        ISABranch branch = branches.get(currentBranchName);
        if (branch != null) {
            branch.setHeadVersionId(versionId);
        }
        
        return version;
    }
    
    /**
     * Get a specific version by ID.
     */
    public ISAVersion getVersion(String versionId) {
        return versions.get(versionId);
    }
    
    /**
     * Get current version.
     */
    public ISAVersion getCurrentVersion() {
        return versions.get(currentVersionId);
    }
    
    /**
     * Get current version ID.
     */
    public String getCurrentVersionId() {
        return currentVersionId;
    }
    
    /**
     * Get all versions in current branch.
     */
    public List<ISAVersion> getBranchVersions() {
        ISABranch branch = branches.get(currentBranchName);
        if (branch == null) {
            return Collections.emptyList();
        }
        
        List<ISAVersion> branchVersions = new ArrayList<>();
        String versionId = branch.getHeadVersionId();
        
        while (versionId != null) {
            ISAVersion version = versions.get(versionId);
            if (version == null) break;
            
            branchVersions.add(version);
            versionId = version.getParentVersionId();
        }
        
        Collections.reverse(branchVersions);
        return branchVersions;
    }
    
    /**
     * Compute diff between two versions.
     * 
     * @param versionId1 First version ID
     * @param versionId2 Second version ID
     * @return Diff result
     */
    public VersionDiff diff(String versionId1, String versionId2) {
        ISAVersion v1 = versions.get(versionId1);
        ISAVersion v2 = versions.get(versionId2);
        
        if (v1 == null || v2 == null) {
            throw new IllegalArgumentException("One or both versions not found");
        }
        
        VersionDiff diff = new VersionDiff(v1, v2);
        
        // Compare instruction counts
        int size1 = v1.getProgram().size();
        int size2 = v2.getProgram().size();
        diff.setSizeChange(size2 - size1);
        
        // Compare instruction sequences
        List<CognitiveISA.Instruction> instrs1 = v1.getProgram().instructions;
        List<CognitiveISA.Instruction> instrs2 = v2.getProgram().instructions;
        
        int minSize = Math.min(instrs1.size(), instrs2.size());
        
        for (int i = 0; i < minSize; i++) {
            CognitiveISA.Instruction ins1 = instrs1.get(i);
            CognitiveISA.Instruction ins2 = instrs2.get(i);
            
            if (!instructionsEqual(ins1, ins2)) {
                diff.addModifiedInstruction(i, ins1, ins2);
            }
        }
        
        // Added instructions
        for (int i = minSize; i < instrs2.size(); i++) {
            diff.addAddedInstruction(i, instrs2.get(i));
        }
        
        // Removed instructions
        for (int i = minSize; i < instrs1.size(); i++) {
            diff.addRemovedInstruction(i, instrs1.get(i));
        }
        
        return diff;
    }
    
    /**
     * Merge two versions (simplified).
     * 
     * @param sourceVersionId Source version ID
     * @param targetBranchName Target branch name
     * @return Merged version ID
     */
    public String merge(String sourceVersionId, String targetBranchName) {
        ISAVersion source = versions.get(sourceVersionId);
        ISABranch targetBranch = branches.get(targetBranchName);
        
        if (source == null || targetBranch == null) {
            throw new IllegalArgumentException("Source version or target branch not found");
        }
        
        // Create new version in target branch with merged program
        String originalBranch = currentBranchName;
        switchBranch(targetBranchName);
        
        String mergedVersionId = createVersion(
            incrementPatchVersion(getCurrentVersion().semanticVersion),
            source.getProgram(),
            "Merge from " + sourceVersionId
        );
        
        switchBranch(originalBranch);
        
        return mergedVersionId;
    }
    
    /**
     * Update version metadata.
     * 
     * @param versionId Version ID
     * @param performance Performance score [0, 1]
     * @param safety Safety score [0, 1]
     * @param stability Stability score [0, 1]
     */
    public void updateMetadata(String versionId, Double performance, Double safety, Double stability) {
        ISAVersion version = versions.get(versionId);
        if (version == null) {
            throw new IllegalArgumentException("Version not found: " + versionId);
        }
        
        if (performance != null) version.setPerformance(performance);
        if (safety != null) version.setSafety(safety);
        if (stability != null) version.setStability(stability);
    }
    
    /**
     * Get repository statistics.
     */
    public RepositoryStats getStatistics() {
        int totalVersions = versions.size();
        int totalBranches = branches.size();
        int branchVersions = getBranchVersions().size();
        
        return new RepositoryStats(
            totalVersions,
            totalBranches,
            currentBranchName,
            currentVersionId,
            getCurrentVersion().semanticVersion,
            branchVersions,
            versionCounter.get()
        );
    }
    
    /**
     * Generate unique version ID.
     */
    private String generateVersionId() {
        return String.format("v%010d", versionCounter.incrementAndGet());
    }
    
    /**
     * Increment patch version.
     */
    private String incrementPatchVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            return version + ".1"; // Fallback
        }
        
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]) + 1;
            
            return major + "." + minor + "." + patch;
        } catch (NumberFormatException e) {
            return version + ".1";
        }
    }
    
    /**
     * Check if two instructions are equal.
     */
    private boolean instructionsEqual(CognitiveISA.Instruction a, CognitiveISA.Instruction b) {
        return a.opcode == b.opcode &&
               Objects.equals(a.operand1, b.operand1) &&
               Objects.equals(a.operand2, b.operand2) &&
               a.address == b.address;
    }
    
    /**
     * ISA Version class
     */
    public static class ISAVersion {
        private final String versionId;
        private final String semanticVersion;
        private final CognitiveISA.Program program;
        private final String description;
        private final long createdAt;
        private final String parentVersionId;
        
        private Double performance;
        private Double safety;
        private Double stability;
        
        public ISAVersion(String versionId, String semanticVersion, CognitiveISA.Program program,
                         String description, long createdAt, String parentVersionId) {
            this.versionId = versionId;
            this.semanticVersion = semanticVersion;
            this.program = program;
            this.description = description;
            this.createdAt = createdAt;
            this.parentVersionId = parentVersionId;
        }
        
        public String getVersionId() { return versionId; }
        public String getSemanticVersion() { return semanticVersion; }
        public CognitiveISA.Program getProgram() { return program; }
        public String getDescription() { return description; }
        public long getCreatedAt() { return createdAt; }
        public String getParentVersionId() { return parentVersionId; }
        
        public Double getPerformance() { return performance; }
        public void setPerformance(Double performance) { this.performance = performance; }
        public Double getSafety() { return safety; }
        public void setSafety(Double safety) { this.safety = safety; }
        public Double getStability() { return stability; }
        public void setStability(Double stability) { this.stability = stability; }
    }
    
    /**
     * ISA Branch class
     */
    public static class ISABranch {
        private final String name;
        private String headVersionId;
        private final String parentBranchName;
        private final long createdAt;
        
        public ISABranch(String name, String headVersionId) {
            this.name = name;
            this.headVersionId = headVersionId;
            this.parentBranchName = null;
            this.createdAt = System.currentTimeMillis();
        }
        
        public String getName() { return name; }
        public String getHeadVersionId() { return headVersionId; }
        public void setHeadVersionId(String headVersionId) { this.headVersionId = headVersionId; }
        public String getParentBranchName() { return parentBranchName; }
        public long getCreatedAt() { return createdAt; }
    }
    
    /**
     * Version Diff class
     */
    public static class VersionDiff {
        private final ISAVersion version1;
        private final ISAVersion version2;
        private int sizeChange;
        private final List<InstructionChange> modifiedInstructions;
        private final List<InstructionChange> addedInstructions;
        private final List<InstructionChange> removedInstructions;
        
        public VersionDiff(ISAVersion version1, ISAVersion version2) {
            this.version1 = version1;
            this.version2 = version2;
            this.sizeChange = 0;
            this.modifiedInstructions = new ArrayList<>();
            this.addedInstructions = new ArrayList<>();
            this.removedInstructions = new ArrayList<>();
        }
        
        public void setSizeChange(int change) { this.sizeChange = change; }
        public void addModifiedInstruction(int index, CognitiveISA.Instruction oldInstr, CognitiveISA.Instruction newInstr) {
            modifiedInstructions.add(new InstructionChange(index, oldInstr, newInstr, "MODIFIED"));
        }
        public void addAddedInstruction(int index, CognitiveISA.Instruction instr) {
            addedInstructions.add(new InstructionChange(index, null, instr, "ADDED"));
        }
        public void addRemovedInstruction(int index, CognitiveISA.Instruction instr) {
            removedInstructions.add(new InstructionChange(index, instr, null, "REMOVED"));
        }
        
        public ISAVersion getVersion1() { return version1; }
        public ISAVersion getVersion2() { return version2; }
        public int getSizeChange() { return sizeChange; }
        public List<InstructionChange> getModifiedInstructions() { return modifiedInstructions; }
        public List<InstructionChange> getAddedInstructions() { return addedInstructions; }
        public List<InstructionChange> getRemovedInstructions() { return removedInstructions; }
    }
    
    /**
     * Instruction Change class
     */
    public static class InstructionChange {
        private final int index;
        private final CognitiveISA.Instruction oldInstruction;
        private final CognitiveISA.Instruction newInstruction;
        private final String changeType;
        
        public InstructionChange(int index, CognitiveISA.Instruction oldInstr, CognitiveISA.Instruction newInstr, String changeType) {
            this.index = index;
            this.oldInstruction = oldInstr;
            this.newInstruction = newInstr;
            this.changeType = changeType;
        }
        
        public int getIndex() { return index; }
        public CognitiveISA.Instruction getOldInstruction() { return oldInstruction; }
        public CognitiveISA.Instruction getNewInstruction() { return newInstruction; }
        public String getChangeType() { return changeType; }
    }
    
    /**
     * Repository Metadata class
     */
    public static class RepositoryMetadata {
        private String author;
        private String description;
        private final Map<String, String> tags;
        
        public RepositoryMetadata() {
            this.tags = new ConcurrentHashMap<>();
        }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, String> getTags() { return new HashMap<>(tags); }
        public void addTag(String key, String value) { tags.put(key, value); }
    }
    
    /**
     * Repository Statistics class
     */
    public static class RepositoryStats {
        public final int totalVersions;
        public final int totalBranches;
        public final String currentBranch;
        public final String currentVersionId;
        public final String currentSemanticVersion;
        public final int currentBranchVersions;
        public final long totalVersionCount;
        
        public RepositoryStats(int totalVersions, int totalBranches, String currentBranch,
                             String currentVersionId, String currentSemanticVersion,
                             int currentBranchVersions, long totalVersionCount) {
            this.totalVersions = totalVersions;
            this.totalBranches = totalBranches;
            this.currentBranch = currentBranch;
            this.currentVersionId = currentVersionId;
            this.currentSemanticVersion = currentSemanticVersion;
            this.currentBranchVersions = currentBranchVersions;
            this.totalVersionCount = totalVersionCount;
        }
        
        @Override
        public String toString() {
            return String.format(
                "C-ISA Repository Stats:\n" +
                "  Total versions: %d\n" +
                "  Total branches: %d\n" +
                "  Current branch: %s\n" +
                "  Current version: %s (%s)\n" +
                "  Branch versions: %d\n" +
                "  Total version count: %d",
                totalVersions, totalBranches, currentBranch,
                currentVersionId, currentSemanticVersion,
                currentBranchVersions, totalVersionCount
            );
        }
    }
}
