package brain_architecture.orchestration;

import java.util.*;

/**
 * Prompt Strategy Engine
 * 
 * Manages prompting strategies for different reasoning modes and roles.
 * Integrates with dual-mode reasoning (System 1 vs System 2) and role-based prompting.
 * 
 * Key Features:
 * - System 1 (Instant) prompts for fast, intuitive responses
 * - System 2 (Thinking) prompts for deliberate, analytical responses
 * - Role-based prompts for different contexts (analyst, creative, teacher, etc.)
 * - Chain-of-thought prompting for complex reasoning
 * - Few-shot examples for better performance
 * - Prompt templates for common tasks
 */
public class PromptStrategyEngine {
    
    /**
     * Reasoning mode
     */
    public enum ReasoningMode {
        INSTANT("instant", "System 1 - Fast, intuitive, pattern-based"),
        THINKING("thinking", "System 2 - Deliberate, analytical, step-by-step");
        
        private final String id;
        private final String description;
        
        ReasoningMode(String id, String description) {
            this.id = id;
            this.description = description;
        }
        
        public String getId() { return id; }
        public String getDescription() { return description; }
    }
    
    /**
     * AI Role
     */
    public enum AIRole {
        ANALYST("analyst", "Data analyst - focuses on facts, patterns, and objective analysis"),
        CREATIVE("creative", "Creative writer - focuses on imagination, storytelling, and novelty"),
        TEACHER("teacher", "Teacher - focuses on explanation, clarity, and learning"),
        CRITIC("critic", "Critic - focuses on evaluation, critique, and improvement"),
        ADVISOR("advisor", "Advisor - focuses on guidance, recommendations, and wisdom"),
        ENGINEER("engineer", "Engineer - focuses on technical solutions, precision, and implementation"),
        SCIENTIST("scientist", "Scientist - focuses on research, methodology, and discovery"),
        GENERAL("general", "General assistant - balanced approach for all tasks");
        
        private final String id;
        private final String description;
        
        AIRole(String id, String description) {
            this.id = id;
            this.description = description;
        }
        
        public String getId() { return id; }
        public String getDescription() { return description; }
    }
    
    /**
     * Prompt template
     */
    public static class PromptTemplate {
        private final String name;
        private final String template;
        private final List<String> placeholders;
        private final AIRole defaultRole;
        private final ReasoningMode defaultMode;
        
        public PromptTemplate(String name, String template, AIRole defaultRole, ReasoningMode defaultMode) {
            this.name = name;
            this.template = template;
            this.defaultRole = defaultRole;
            this.defaultMode = defaultMode;
            this.placeholders = extractPlaceholders(template);
        }
        
        public String getName() { return name; }
        public String getTemplate() { return template; }
        public List<String> getPlaceholders() { return new ArrayList<>(placeholders); }
        public AIRole getDefaultRole() { return defaultRole; }
        public ReasoningMode getDefaultMode() { return defaultMode; }
        
        /**
         * Fill template with values
         */
        public String fill(Map<String, String> values) {
            String result = template;
            for (Map.Entry<String, String> entry : values.entrySet()) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return result;
        }
        
        private List<String> extractPlaceholders(String template) {
            List<String> placeholders = new ArrayList<>();
            int start = template.indexOf('{');
            while (start != -1) {
                int end = template.indexOf('}', start);
                if (end != -1) {
                    placeholders.add(template.substring(start + 1, end));
                    start = template.indexOf('{', end + 1);
                } else {
                    break;
                }
            }
            return placeholders;
        }
    }
    
    private final Map<String, PromptTemplate> templates;
    private final Map<AIRole, String> roleInstructions;
    private final Map<ReasoningMode, String> modeInstructions;
    private AIRole currentRole;
    private ReasoningMode currentMode;
    
    public PromptStrategyEngine() {
        this.templates = new HashMap<>();
        this.roleInstructions = new HashMap<>();
        this.modeInstructions = new HashMap<>();
        this.currentRole = AIRole.GENERAL;
        this.currentMode = ReasoningMode.INSTANT;
        
        initializeRoleInstructions();
        initializeModeInstructions();
        initializeDefaultTemplates();
    }
    
    /**
     * Initialize role instructions
     */
    private void initializeRoleInstructions() {
        roleInstructions.put(AIRole.ANALYST,
            "You are a data analyst. Focus on facts, patterns, and objective analysis. " +
            "Provide evidence-based conclusions and cite data sources when possible.");
        
        roleInstructions.put(AIRole.CREATIVE,
            "You are a creative writer. Focus on imagination, storytelling, and novelty. " +
            "Be original, engaging, and emotionally resonant.");
        
        roleInstructions.put(AIRole.TEACHER,
            "You are a teacher. Focus on explanation, clarity, and learning. " +
            "Break down complex concepts, use examples, and check for understanding.");
        
        roleInstructions.put(AIRole.CRITIC,
            "You are a critic. Focus on evaluation, critique, and improvement. " +
            "Be constructive, specific, and fair in your assessments.");
        
        roleInstructions.put(AIRole.ADVISOR,
            "You are an advisor. Focus on guidance, recommendations, and wisdom. " +
            "Consider multiple perspectives, weigh trade-offs, and provide actionable advice.");
        
        roleInstructions.put(AIRole.ENGINEER,
            "You are an engineer. Focus on technical solutions, precision, and implementation. " +
            "Be practical, consider constraints, and provide specific steps.");
        
        roleInstructions.put(AIRole.SCIENTIST,
            "You are a scientist. Focus on research, methodology, and discovery. " +
            "Be rigorous, test hypotheses, and acknowledge uncertainty.");
        
        roleInstructions.put(AIRole.GENERAL,
            "You are a helpful AI assistant. Be accurate, concise, and helpful. " +
            "Adapt your response to the user's needs and context.");
    }
    
    /**
     * Initialize mode instructions
     */
    private void initializeModeInstructions() {
        modeInstructions.put(ReasoningMode.INSTANT,
            "Respond quickly and intuitively. Use pattern recognition and heuristics. " +
            "Keep your response concise (under 200 words). Focus on the most likely answer.");
        
        modeInstructions.put(ReasoningMode.THINKING,
            "Think step-by-step. Show your reasoning process. Consider multiple possibilities. " +
            "Be thorough and analytical. Take time to ensure accuracy.");
    }
    
    /**
     * Initialize default prompt templates
     */
    private void initializeDefaultTemplates() {
        // Chain-of-thought template
        templates.put("chain_of_thought", new PromptTemplate(
            "Chain of Thought",
            "{role_instruction}\n\n{mode_instruction}\n\n" +
            "Question: {question}\n\n" +
            "Let's think step by step:\n\n" +
            "1.\n2.\n3.\n\n" +
            "Therefore, the answer is:",
            AIRole.GENERAL,
            ReasoningMode.THINKING
        ));
        
        // Few-shot template
        templates.put("few_shot", new PromptTemplate(
            "Few Shot",
            "{role_instruction}\n\n{mode_instruction}\n\n" +
            "Examples:\n{examples}\n\n" +
            "Now, answer this:\n{question}",
            AIRole.GENERAL,
            ReasoningMode.INSTANT
        ));
        
        // Creative writing template
        templates.put("creative_writing", new PromptTemplate(
            "Creative Writing",
            "{role_instruction}\n\n" +
            "Topic: {topic}\n" +
            "Style: {style}\n" +
            "Length: {length}\n\n" +
            "Write a {style} piece about {topic} that is approximately {length} words.",
            AIRole.CREATIVE,
            ReasoningMode.INSTANT
        ));
        
        // Analysis template
        templates.put("analysis", new PromptTemplate(
            "Analysis",
            "{role_instruction}\n\n{mode_instruction}\n\n" +
            "Analyze the following:\n\n{content}\n\n" +
            "Provide:\n" +
            "1. Key findings\n" +
            "2. Patterns and trends\n" +
            "3. Recommendations",
            AIRole.ANALYST,
            ReasoningMode.THINKING
        ));
        
        // Explanation template
        templates.put("explanation", new PromptTemplate(
            "Explanation",
            "{role_instruction}\n\n" +
            "Explain {concept} in simple terms. " +
            "Assume the audience has {audience_level} knowledge. " +
            "Use examples and analogies where helpful.",
            AIRole.TEACHER,
            ReasoningMode.INSTANT
        ));
        
        // Code review template
        templates.put("code_review", new PromptTemplate(
            "Code Review",
            "{role_instruction}\n\n{mode_instruction}\n\n" +
            "Review the following code:\n\n{code}\n\n" +
            "Provide:\n" +
            "1. Overall assessment\n" +
            "2. Strengths\n" +
            "3. Issues and improvements\n" +
            "4. Refactored version if needed",
            AIRole.ENGINEER,
            ReasoningMode.THINKING
        ));
    }
    
    /**
     * Set current role
     */
    public void setRole(AIRole role) {
        this.currentRole = role;
    }
    
    /**
     * Get current role
     */
    public AIRole getRole() {
        return currentRole;
    }
    
    /**
     * Set current reasoning mode
     */
    public void setMode(ReasoningMode mode) {
        this.currentMode = mode;
    }
    
    /**
     * Get current reasoning mode
     */
    public ReasoningMode getMode() {
        return currentMode;
    }
    
    /**
     * Get role instruction
     */
    public String getRoleInstruction(AIRole role) {
        return roleInstructions.get(role);
    }
    
    /**
     * Get mode instruction
     */
    public String getModeInstruction(ReasoningMode mode) {
        return modeInstructions.get(mode);
    }
    
    /**
     * Register a custom template
     */
    public void registerTemplate(String name, PromptTemplate template) {
        templates.put(name, template);
    }
    
    /**
     * Get a template by name
     */
    public PromptTemplate getTemplate(String name) {
        return templates.get(name);
    }
    
    /**
     * Build a prompt using a template
     */
    public String buildPrompt(String templateName, Map<String, String> values) {
        PromptTemplate template = templates.get(templateName);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        
        // Add role and mode instructions if not provided
        Map<String, String> filledValues = new HashMap<>(values);
        if (!filledValues.containsKey("role_instruction")) {
            filledValues.put("role_instruction", getRoleInstruction(currentRole));
        }
        if (!filledValues.containsKey("mode_instruction")) {
            filledValues.put("mode_instruction", getModeInstruction(currentMode));
        }
        
        return template.fill(filledValues);
    }
    
    /**
     * Build a custom prompt
     */
    public String buildCustomPrompt(String userQuery, AIRole role, ReasoningMode mode) {
        StringBuilder prompt = new StringBuilder();
        
        // Add role instruction
        prompt.append(getRoleInstruction(role)).append("\n\n");
        
        // Add mode instruction
        prompt.append(getModeInstruction(mode)).append("\n\n");
        
        // Add user query
        prompt.append("Query: ").append(userQuery);
        
        return prompt.toString();
    }
    
    /**
     * Build a chain-of-thought prompt
     */
    public String buildChainOfThoughtPrompt(String question) {
        return buildPrompt("chain_of_thought", Map.of("question", question));
    }
    
    /**
     * Build a few-shot prompt
     */
    public String buildFewShotPrompt(String question, String examples) {
        return buildPrompt("few_shot", Map.of("question", question, "examples", examples));
    }
    
    /**
     * Get all template names
     */
    public Set<String> getTemplateNames() {
        return new HashSet<>(templates.keySet());
    }
    
    /**
     * Get all available roles
     */
    public static AIRole[] getAllRoles() {
        return AIRole.values();
    }
    
    /**
     * Get all available modes
     */
    public static ReasoningMode[] getAllModes() {
        return ReasoningMode.values();
    }
}
