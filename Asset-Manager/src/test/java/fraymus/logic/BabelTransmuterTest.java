package fraymus.logic;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Babel Transmuter Test Suite
 * 
 * Tests natural language to C-ISA compilation.
 */
public class BabelTransmuterTest {
    
    private BabelTransmuter transmuter;
    private LogicEngine engine;
    
    @BeforeEach
    public void setUp() {
        transmuter = new BabelTransmuter();
        transmuter.initialize();
        
        engine = new LogicEngine();
        engine.initialize();
    }
    
    @Test
    public void testInitialization() {
        assertNotNull(transmuter);
        assertTrue(transmuter.getStatus().contains("BabelTransmuter"));
    }
    
    @Test
    public void testWeatherCheckTransmutation() {
        String input = "Find out if it's raining and tell me, otherwise just wait";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Verify program has expected structure (STORE, LOAD, CMP, BRANCH)
        boolean hasStore = false;
        boolean hasLoad = false;
        boolean hasCmp = false;
        boolean hasBranch = false;
        
        for (CognitiveISA.Instruction instr : program.instructions) {
            if (instr.opcode == CognitiveISA.STORE) hasStore = true;
            if (instr.opcode == CognitiveISA.LOAD) hasLoad = true;
            if (instr.opcode == CognitiveISA.CMP) hasCmp = true;
            if (instr.opcode == CognitiveISA.BRANCH || 
                instr.opcode == CognitiveISA.BRANCH_IF_EQ) hasBranch = true;
        }
        
        assertTrue(hasStore, "Weather check should have STORE");
        assertTrue(hasLoad, "Weather check should have LOAD");
        assertTrue(hasCmp, "Weather check should have CMP");
        assertTrue(hasBranch, "Weather check should have BRANCH");
    }
    
    @Test
    public void testConditionalTransmutation() {
        String input = "If x > 5 then push yes else push no";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Check that program has branch instructions
        boolean hasBranch = false;
        for (CognitiveISA.Instruction instr : program.instructions) {
            if (instr.opcode == CognitiveISA.BRANCH || 
                instr.opcode == CognitiveISA.BRANCH_IF_EQ ||
                instr.opcode == CognitiveISA.BRANCH_IF_GT) {
                hasBranch = true;
                break;
            }
        }
        assertTrue(hasBranch, "Conditional program should have branch instructions");
    }
    
    @Test
    public void testStoreTransmutation() {
        String input = "Store 42 in x";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Execute
        engine.loadProgram(program);
        engine.run();
        
        // Check that x is stored in memory
        assertEquals(42, engine.getMemory().get("x"));
    }
    
    @Test
    public void testLoadTransmutation() {
        String input = "Load x";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Check that program has LOAD instruction
        boolean hasLoad = false;
        for (CognitiveISA.Instruction instr : program.instructions) {
            if (instr.opcode == CognitiveISA.LOAD) {
                hasLoad = true;
                break;
            }
        }
        assertTrue(hasLoad, "Load program should have LOAD instruction");
    }
    
    @Test
    public void testCompareTransmutation() {
        String input = "Compare x with 5";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Check that program has CMP instruction
        boolean hasCmp = false;
        for (CognitiveISA.Instruction instr : program.instructions) {
            if (instr.opcode == CognitiveISA.CMP) {
                hasCmp = true;
                break;
            }
        }
        assertTrue(hasCmp, "Compare program should have CMP instruction");
    }
    
    @Test
    public void testActuateTransmutation() {
        String input = "Actuate Weather_API";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Check that program has ACTUATE instruction
        boolean hasActuate = false;
        for (CognitiveISA.Instruction instr : program.instructions) {
            if (instr.opcode == CognitiveISA.ACTUATE) {
                hasActuate = true;
                break;
            }
        }
        assertTrue(hasActuate, "Actuate program should have ACTUATE instruction");
    }
    
    @Test
    public void testWaitTransmutation() {
        String input = "Wait 10 minutes";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Check that program has WAIT instruction
        boolean hasWait = false;
        for (CognitiveISA.Instruction instr : program.instructions) {
            if (instr.opcode == CognitiveISA.WAIT) {
                hasWait = true;
                break;
            }
        }
        assertTrue(hasWait, "Wait program should have WAIT instruction");
    }
    
    @Test
    public void testLoopTransmutation() {
        String input = "Loop while x < 10";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Check that program has loop structure (label and JMP)
        boolean hasLabel = !program.labels.isEmpty();
        boolean hasJmp = false;
        for (CognitiveISA.Instruction instr : program.instructions) {
            if (instr.opcode == CognitiveISA.JMP) {
                hasJmp = true;
                break;
            }
        }
        assertTrue(hasLabel, "Loop program should have labels");
        assertTrue(hasJmp, "Loop program should have JMP instruction");
    }
    
    @Test
    public void testDirectTransmutation() {
        String input = "Hello world";
        CognitiveISA.Program program = transmuter.transmute(input);
        
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Direct transmutation should push the text
        boolean hasPush = false;
        for (CognitiveISA.Instruction instr : program.instructions) {
            if (instr.opcode == CognitiveISA.PUSH) {
                hasPush = true;
                break;
            }
        }
        assertTrue(hasPush, "Direct transmutation should have PUSH instruction");
    }
    
    @Test
    public void testEndToEndPipeline() {
        // Full pipeline: Natural language -> C-ISA (execution tested separately)
        String input = "Find out if it's raining";
        
        // Transmute
        CognitiveISA.Program program = transmuter.transmute(input);
        assertNotNull(program);
        assertTrue(program.size() > 0);
        
        // Load program (don't execute to avoid potential loops in tests)
        engine.loadProgram(program);
        assertEquals(0, engine.getPC());
    }
    
    @Test
    public void testMultipleTransmutations() {
        String[] inputs = {
            "Store 42 in x",
            "Load x",
            "Compare x with 5",
            "Actuate Weather_API"
        };
        
        for (String input : inputs) {
            CognitiveISA.Program program = transmuter.transmute(input);
            assertNotNull(program, "Transmutation should succeed for: " + input);
            assertTrue(program.size() > 0, "Program should have instructions for: " + input);
        }
    }
    
    @Test
    public void testSetUseLLM() {
        transmuter.setUseLLM(true);
        assertTrue(transmuter.getStatus().contains("LLM=true"));
        
        transmuter.setUseLLM(false);
        assertTrue(transmuter.getStatus().contains("LLM=false"));
    }
}
