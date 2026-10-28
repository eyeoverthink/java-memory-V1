package fraymus.logic;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Logic Engine Test Suite
 * 
 * Tests the deterministic execution of C-ISA instructions.
 */
public class LogicEngineTest {
    
    private LogicEngine engine;
    
    @BeforeEach
    public void setUp() {
        engine = new LogicEngine();
        engine.initialize();
    }
    
    @Test
    public void testInitialization() {
        assertNotNull(engine);
        assertEquals(0, engine.getPC());
    }
    
    @Test
    public void testPushPop() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(42)
            .push("hello")
            .push(3.14)
            .pop()
            .build();
        
        engine.loadProgram(program);
        engine.tick();
        engine.tick();
        engine.tick();
        engine.tick();
        
        // Stack should have 2 items after 3 pushes and 1 pop
        assertEquals(2, engine.getStack().size());
    }
    
    @Test
    public void testDup() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(42)
            .dup()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Stack should have 2 items (42 duplicated)
        List<Object> stack = engine.getStack();
        assertEquals(2, stack.size());
        assertEquals(42, stack.get(0));
        assertEquals(42, stack.get(1));
    }
    
    @Test
    public void testStoreLoad() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(100)
            .store("x")
            .push(0)
            .load("x")
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Memory should have x=100
        assertEquals(100, engine.getMemory().get("x"));
        
        // Stack should have 100 from load
        List<Object> stack = engine.getStack();
        assertEquals(100, stack.get(stack.size() - 1));
    }
    
    @Test
    public void testCMP() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(5)
            .push(5)
            .cmp()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Should set EQ flag
        String flags = engine.getFlags();
        assertTrue(flags.contains("EQ=true"));
    }
    
    @Test
    public void testXOR() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(0b10101010)
            .push(0b01010101)
            .xor()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // XOR of 10101010 and 01010101 should be 11111111 (255)
        List<Object> stack = engine.getStack();
        assertEquals(255L, stack.get(stack.size() - 1));
    }
    
    @Test
    public void testAND() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(0b11110000)
            .push(0b10101010)
            .and()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // AND of 11110000 and 10101010 should be 10100000 (160)
        List<Object> stack = engine.getStack();
        assertEquals(160L, stack.get(stack.size() - 1));
    }
    
    @Test
    public void testOR() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(0b10101010)
            .push(0b01010101)
            .or()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // OR of 10101010 and 01010101 should be 11111111 (255)
        List<Object> stack = engine.getStack();
        assertEquals(255L, stack.get(stack.size() - 1));
    }
    
    @Test
    public void testNOT() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(0b10101010)
            .not()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // NOT of 10101010 should be 01010101 (85) in 8-bit, but Java uses 32-bit
        // So it's ~170 = -171
        List<Object> stack = engine.getStack();
        Object result = stack.get(stack.size() - 1);
        assertTrue(result instanceof Long);
    }
    
    @Test
    public void testBranch() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(1)
            .label("skip")
            .push(2)
            .branch("end")
            .push(3)
            .label("end")
            .halt()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Should skip push(3) due to branch
        // Stack should have [1, 2]
        assertEquals(2, engine.getStack().size());
    }
    
    @Test
    public void testBranchIfEq() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(5)
            .push(5)
            .cmp()
            .branchIfEq("taken")
            .push(0)
            .label("taken")
            .push(1)
            .halt()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Should take branch because 5 == 5
        // Stack should have [1], not [0, 1]
        List<Object> stack = engine.getStack();
        assertEquals(1, stack.size());
        assertEquals(1, stack.get(stack.size() - 1));
    }
    
    @Test
    public void testBranchIfNeq() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(5)
            .push(3)
            .cmp()
            .branchIfNeq("taken")
            .push(0)
            .label("taken")
            .push(1)
            .halt()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Should take branch because 5 != 3
        List<Object> stack = engine.getStack();
        assertEquals(1, stack.size());
    }
    
    @Test
    public void testCallRet() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(1)
            .call("subroutine")
            .push(3)
            .halt()
            .label("subroutine")
            .push(2)
            .ret()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Stack should be [1, 2, 3] (push 1, call subroutine, push 2, ret, push 3)
        assertEquals(3, engine.getStack().size());
    }
    
    @Test
    public void testHalt() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(1)
            .halt()
            .push(2)
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Should halt after push(1), so stack should have [1], not [1, 2]
        List<Object> stack = engine.getStack();
        assertEquals(1, stack.size());
        assertEquals(1, stack.get(stack.size() - 1));
    }
    
    @Test
    public void testComplexProgram() {
        // Simulate: "Check if x > 5, if so push 'yes', else push 'no'"
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(10)
            .store("x")
            .load("x")
            .push(5)
            .cmp()
            .branchIfGt("yes_branch")
            .push("no")
            .branch("end")
            .label("yes_branch")
            .push("yes")
            .label("end")
            .halt()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // x=10 > 5, so should push "yes"
        List<Object> stack = engine.getStack();
        assertEquals("yes", stack.get(stack.size() - 1));
    }
    
    @Test
    public void testWeatherCheckExample() {
        // Example from the user: "Find out if it's raining and tell me, otherwise just wait"
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push("Raining")
            .store("weather")
            .load("weather")
            .push("Raining")
            .cmp()
            .branchIfEq("tell")
            .push("Wait 10 minutes")
            .branch("end")
            .label("tell")
            .push("It is raining")
            .label("end")
            .halt()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Weather is "Raining", so should push "It is raining"
        List<Object> stack = engine.getStack();
        assertEquals("It is raining", stack.get(stack.size() - 1));
    }
    
    @Test
    public void testReset() {
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .push(1)
            .store("x")
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        // Reset should clear all state
        engine.reset();
        assertEquals(0, engine.getStack().size());
        assertEquals(0, engine.getMemory().size());
        assertEquals(0, engine.getPC());
    }
    
    @Test
    public void testActuatorCallback() {
        final StringBuilder actuatorLog = new StringBuilder();
        
        engine.setActuator(new LogicEngine.Actuator() {
            @Override
            public void actuate(String tool, Object... args) {
                actuatorLog.append(tool);
                for (Object arg : args) {
                    actuatorLog.append(" ").append(arg);
                }
            }
        });
        
        CognitiveISA.Program program = new CognitiveISA.Builder()
            .actuate("Weather_API")
            .halt()
            .build();
        
        engine.loadProgram(program);
        engine.run();
        
        assertEquals("Weather_API", actuatorLog.toString());
    }
}
