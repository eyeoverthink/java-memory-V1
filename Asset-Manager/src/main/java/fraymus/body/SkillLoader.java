package fraymus.body;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * SkillLoader - OpenClaw Markdown Skill Parser
 * 
 * Reads OpenClaw-style skill.md files and makes them available to Fraymus.
 * Skills are Markdown files with structure:
 * 
 * # Skill Name
 * > Description
 * 
 * ## Usage
 * ...
 * 
 * ## Examples
 * ...
 */
public class SkillLoader {
    
    public static class Skill {
        public final String name;
        public final String description;
        public final String usage;
        public final String fullContent;
        
        public Skill(String name, String description, String usage, String fullContent) {
            this.name = name;
            this.description = description;
            this.usage = usage;
            this.fullContent = fullContent;
        }
        
        @Override
        public String toString() {
            return name + ": " + description;
        }
    }
    
    private final Map<String, Skill> skills = new HashMap<>();
    private final List<String> skillPaths = new ArrayList<>();

    /**
     * Ingest a single skill file
     */
    public void ingest(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                System.err.println("   ⚠️  SKILL NOT FOUND: " + path);
                return;
            }
            
            String content = Files.readString(f.toPath());
            Skill skill = parseSkill(f.getName(), content);
            
            skills.put(skill.name, skill);
            skillPaths.add(path);
            
            System.out.println("   🦞 SKILL ABSORBED: " + skill.name);
            
        } catch (Exception e) {
            System.err.println("   ❌ SKILL REJECTED: " + path + " - " + e.getMessage());
        }
    }
    
    /**
     * Ingest all .md files from a directory
     */
    public void ingestDirectory(String dirPath) {
        try {
            File dir = new File(dirPath);
            if (!dir.exists() || !dir.isDirectory()) {
                System.err.println("   ⚠️  SKILL DIRECTORY NOT FOUND: " + dirPath);
                return;
            }
            
            File[] files = dir.listFiles((d, name) -> name.endsWith(".md"));
            if (files == null || files.length == 0) {
                System.out.println("   ℹ️  No skill files found in: " + dirPath);
                return;
            }
            
            System.out.println("   📚 Loading skills from: " + dirPath);
            for (File f : files) {
                ingest(f.getAbsolutePath());
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ DIRECTORY INGEST FAILED: " + e.getMessage());
        }
    }
    
    /**
     * Parse Markdown skill file into structured Skill object
     */
    private Skill parseSkill(String filename, String content) {
        String name = filename.replace(".md", "").replace("_", " ");
        String description = "";
        String usage = "";
        
        // Extract first H1 as name
        Pattern h1Pattern = Pattern.compile("^#\\s+(.+)$", Pattern.MULTILINE);
        Matcher h1Matcher = h1Pattern.matcher(content);
        if (h1Matcher.find()) {
            name = h1Matcher.group(1).trim();
        }
        
        // Extract blockquote as description
        Pattern descPattern = Pattern.compile("^>\\s+(.+)$", Pattern.MULTILINE);
        Matcher descMatcher = descPattern.matcher(content);
        if (descMatcher.find()) {
            description = descMatcher.group(1).trim();
        }
        
        // Extract Usage section
        Pattern usagePattern = Pattern.compile("##\\s+Usage\\s*\\n([\\s\\S]*?)(?=##|$)", Pattern.MULTILINE);
        Matcher usageMatcher = usagePattern.matcher(content);
        if (usageMatcher.find()) {
            usage = usageMatcher.group(1).trim();
        }
        
        return new Skill(name, description, usage, content);
    }
    
    /**
     * Get skill by name
     */
    public Skill getSkill(String name) {
        return skills.get(name);
    }
    
    /**
     * Get all skill names
     */
    public Set<String> getSkillNames() {
        return skills.keySet();
    }
    
    /**
     * Get compact skill context for LLM prompts
     */
    public String getSkillContext() {
        if (skills.isEmpty()) {
            return "No skills loaded.";
        }
        
        StringBuilder sb = new StringBuilder("AVAILABLE SKILLS:\n");
        skills.values().forEach(skill -> {
            sb.append("- ").append(skill.name);
            if (!skill.description.isEmpty()) {
                sb.append(": ").append(skill.description);
            }
            sb.append("\n");
        });
        return sb.toString();
    }
    
    /**
     * Get detailed skill context including usage
     */
    public String getDetailedSkillContext() {
        if (skills.isEmpty()) {
            return "No skills loaded.";
        }
        
        StringBuilder sb = new StringBuilder("SKILL LIBRARY:\n\n");
        skills.values().forEach(skill -> {
            sb.append("## ").append(skill.name).append("\n");
            if (!skill.description.isEmpty()) {
                sb.append(skill.description).append("\n");
            }
            if (!skill.usage.isEmpty()) {
                sb.append("\nUsage:\n").append(skill.usage).append("\n");
            }
            sb.append("\n---\n\n");
        });
        return sb.toString();
    }
    
    /**
     * Search skills by keyword
     */
    public List<Skill> searchSkills(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return skills.values().stream()
            .filter(skill -> 
                skill.name.toLowerCase().contains(lowerKeyword) ||
                skill.description.toLowerCase().contains(lowerKeyword) ||
                skill.fullContent.toLowerCase().contains(lowerKeyword)
            )
            .collect(Collectors.toList());
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format("Skills loaded: %d | Paths: %d", 
            skills.size(), skillPaths.size());
    }
    
    /**
     * Clear all loaded skills
     */
    public void clear() {
        skills.clear();
        skillPaths.clear();
        System.out.println("   🗑️  All skills cleared");
    }
}
