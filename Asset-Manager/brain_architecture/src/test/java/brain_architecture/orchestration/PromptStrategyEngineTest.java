package brain_architecture.orchestration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Test prompt strategy engine
 */
public class PromptStrategyEngineTest {
    
    private PromptStrategyEngine engine;
    
    @BeforeEach
    public void setUp() {
        engine = new PromptStrategyEngine();
    }
    
    @Test
    public void testEngineInitialization() {
        assertNotNull(engine);
        assertEquals(PromptStrategyEngine.AIRole.GENERAL, engine.getRole());
        assertEquals(PromptStrategyEngine.ReasoningMode.INSTANT, engine.getMode());
    }
    
    @Test
    public void testSetRole() {
        engine.setRole(PromptStrategyEngine.AIRole.ANALYST);
        assertEquals(PromptStrategyEngine.AIRole.ANALYST, engine.getRole());
        
        engine.setRole(PromptStrategyEngine.AIRole.CREATIVE);
        assertEquals(PromptStrategyEngine.AIRole.CREATIVE, engine.getRole());
    }
    
    @Test
    public void testSetMode() {
        engine.setMode(PromptStrategyEngine.ReasoningMode.THINKING);
        assertEquals(PromptStrategyEngine.ReasoningMode.THINKING, engine.getMode());
        
        engine.setMode(PromptStrategyEngine.ReasoningMode.INSTANT);
        assertEquals(PromptStrategyEngine.ReasoningMode.INSTANT, engine.getMode());
    }
    
    @Test
    public void testRoleInstructions() {
        String analystInstruction = engine.getRoleInstruction(PromptStrategyEngine.AIRole.ANALYST);
        assertNotNull(analystInstruction);
        assertTrue(analystInstruction.contains("data analyst"));
        
        String creativeInstruction = engine.getRoleInstruction(PromptStrategyEngine.AIRole.CREATIVE);
        assertNotNull(creativeInstruction);
        assertTrue(creativeInstruction.contains("creative writer"));
    }
    
    @Test
    public void testModeInstructions() {
        String instantInstruction = engine.getModeInstruction(PromptStrategyEngine.ReasoningMode.INSTANT);
        assertNotNull(instantInstruction);
        assertTrue(instantInstruction.contains("quickly"));
        
        String thinkingInstruction = engine.getModeInstruction(PromptStrategyEngine.ReasoningMode.THINKING);
        assertNotNull(thinkingInstruction);
        assertTrue(thinkingInstruction.contains("step-by-step"));
    }
    
    @Test
    public void testGetTemplate() {
        PromptStrategyEngine.PromptTemplate template = engine.getTemplate("chain_of_thought");
        assertNotNull(template);
        assertEquals("Chain of Thought", template.getName());
    }
    
    @Test
    public void testBuildChainOfThoughtPrompt() {
        String prompt = engine.buildChainOfThoughtPrompt("What is 2+2?");
        
        assertNotNull(prompt);
        assertTrue(prompt.contains("Let's think step by step"));
        assertTrue(prompt.contains("What is 2+2?"));
        assertTrue(prompt.contains("Therefore, the answer is:"));
    }
    
    @Test
    public void testBuildFewShotPrompt() {
        String examples = "Example 1: 1+1=2\nExample 2: 2+2=4";
        String prompt = engine.buildFewShotPrompt("3+3=?", examples);
        
        assertNotNull(prompt);
        assertTrue(prompt.contains("Examples:"));
        assertTrue(prompt.contains(examples));
        assertTrue(prompt.contains("3+3=?"));
    }
    
    @Test
    public void testBuildCustomPrompt() {
        String prompt = engine.buildCustomPrompt(
            "Explain quantum computing",
            PromptStrategyEngine.AIRole.TEACHER,
            PromptStrategyEngine.ReasoningMode.THINKING
        );
        
        assertNotNull(prompt);
        assertTrue(prompt.contains("teacher"));
        assertTrue(prompt.contains("step-by-step"));
        assertTrue(prompt.contains("Explain quantum computing"));
    }
    
    @Test
    public void testBuildPromptWithTemplate() {
        Map<String, String> values = Map.of(
            "question", "What is the meaning of life?",
            "role_instruction", "You are a philosopher.",
            "mode_instruction", "Think deeply and carefully."
        );
        
        String prompt = engine.buildPrompt("chain_of_thought", values);
        
        assertNotNull(prompt);
        assertTrue(prompt.contains("You are a philosopher"));
        assertTrue(prompt.contains("Think deeply and carefully"));
        assertTrue(prompt.contains("What is the meaning of life?"));
    }
    
    @Test
    public void testTemplatePlaceholders() {
        PromptStrategyEngine.PromptTemplate template = engine.getTemplate("chain_of_thought");
        assertNotNull(template);
        
        assertTrue(template.getPlaceholders().contains("question"));
    }
    
    @Test
    public void testTemplateFill() {
        PromptStrategyEngine.PromptTemplate template = engine.getTemplate("chain_of_thought");
        Map<String, String> values = Map.of("question", "Test question");
        
        String filled = template.fill(values);
        
        assertNotNull(filled);
        assertTrue(filled.contains("Test question"));
    }
    
    @Test
    public void testRegisterCustomTemplate() {
        PromptStrategyEngine.PromptTemplate customTemplate = new PromptStrategyEngine.PromptTemplate(
            "custom",
            "Custom template with {placeholder}",
            PromptStrategyEngine.AIRole.GENERAL,
            PromptStrategyEngine.ReasoningMode.INSTANT
        );
        
        engine.registerTemplate("custom", customTemplate);
        
        PromptStrategyEngine.PromptTemplate retrieved = engine.getTemplate("custom");
        assertNotNull(retrieved);
        assertEquals("custom", retrieved.getName());
    }
    
    @Test
    public void testGetTemplateNames() {
        Set<String> names = engine.getTemplateNames();
        
        assertNotNull(names);
        assertTrue(names.contains("chain_of_thought"));
        assertTrue(names.contains("few_shot"));
        assertTrue(names.contains("creative_writing"));
    }
    
    @Test
    public void testGetAllRoles() {
        PromptStrategyEngine.AIRole[] roles = PromptStrategyEngine.getAllRoles();
        
        assertNotNull(roles);
        assertTrue(roles.length > 0);
        assertTrue(Arrays.asList(roles).contains(PromptStrategyEngine.AIRole.ANALYST));
        assertTrue(Arrays.asList(roles).contains(PromptStrategyEngine.AIRole.CREATIVE));
    }
    
    @Test
    public void testGetAllModes() {
        PromptStrategyEngine.ReasoningMode[] modes = PromptStrategyEngine.getAllModes();
        
        assertNotNull(modes);
        assertEquals(2, modes.length);
        assertTrue(Arrays.asList(modes).contains(PromptStrategyEngine.ReasoningMode.INSTANT));
        assertTrue(Arrays.asList(modes).contains(PromptStrategyEngine.ReasoningMode.THINKING));
    }
    
    @Test
    public void testRoleDescriptions() {
        for (PromptStrategyEngine.AIRole role : PromptStrategyEngine.getAllRoles()) {
            assertNotNull(role.getDescription());
            assertFalse(role.getDescription().isEmpty());
        }
    }
    
    @Test
    public void testModeDescriptions() {
        for (PromptStrategyEngine.ReasoningMode mode : PromptStrategyEngine.getAllModes()) {
            assertNotNull(mode.getDescription());
            assertFalse(mode.getDescription().isEmpty());
        }
    }
}
