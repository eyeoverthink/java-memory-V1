package fraymus.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;

/**
 * Test suite for AlphaGoMath - MCTS + PUCT implementation
 */
public class AlphaGoMathTest {
    
    @Test
    public void testMCTSNodeCreation() {
        CognitiveISA.Program program = new CognitiveISA.Builder().halt().build();
        AlphaGoMath.CISAState state = new AlphaGoMath.CISAState(program);
        
        AlphaGoMath.MCTSNode node = new AlphaGoMath.MCTSNode(state, null, 1.0);
        
        assertNotNull(node);
        assertEquals(0, node.getVisitCount());
        assertEquals(0.0, node.getQ(), 0.001);
        assertFalse(node.isTerminal());
    }
    
    @Test
    public void testPUCTScoreCalculation() {
        CognitiveISA.Program program = new CognitiveISA.Builder().halt().build();
        AlphaGoMath.CISAState state = new AlphaGoMath.CISAState(program);
        
        AlphaGoMath.MCTSNode parent = new AlphaGoMath.MCTSNode(state, null, 1.0);
        parent.update(1.0); // Visit once with value 1.0
        
        AlphaGoMath.MCTSNode child = new AlphaGoMath.MCTSNode(state, parent, 0.5);
        child.update(0.8); // Visit once with value 0.8
        
        double puctScore = child.getPUCTScore();
        
        // PUCT = Q + U, where Q = 0.8 and U is positive
        assertTrue(puctScore > 0.8);
    }
    
    @Test
    public void testCISAStateLegalActions() {
        CognitiveISA.Program program = new CognitiveISA.Builder().halt().build();
        AlphaGoMath.CISAState state = new AlphaGoMath.CISAState(program);
        
        assertTrue(state.getLegalActions().size() > 0);
        assertTrue(state.getLegalActions().contains(CognitiveISA.PUSH));
        assertTrue(state.getLegalActions().contains(CognitiveISA.STORE));
    }
    
    @Test
    public void testCISAStateApplyAction() {
        CognitiveISA.Program program = new CognitiveISA.Builder().halt().build();
        AlphaGoMath.CISAState state = new AlphaGoMath.CISAState(program);
        
        AlphaGoMath.CISAState newState = state.applyAction(CognitiveISA.PUSH);
        
        assertNotNull(newState);
        assertEquals(state.getProgramCounter() + 1, newState.getProgramCounter());
    }
    
    @Test
    public void testCISAStateTerminal() {
        CognitiveISA.Program program = new CognitiveISA.Builder().halt().build();
        // Create state with programCounter at program size to simulate completion
        AlphaGoMath.CISAState state = new AlphaGoMath.CISAState(program, program.size(), new HashMap<>(), true);
        
        // State with terminal flag should be terminal
        assertTrue(state.isTerminal());
    }
    
    @Test
    public void testSelectBestAction() {
        AlphaGoMath alphaGo = new AlphaGoMath();
        
        CognitiveISA.Program program = new CognitiveISA.Builder().halt().build();
        AlphaGoMath.CISAState initialState = new AlphaGoMath.CISAState(program);
        
        // Simple prior function: equal probability for all actions
        java.util.function.Function<Integer, Double> priorFunction = action -> 0.1;
        
        // Simple evaluation function: return 1.0 for all states
        java.util.function.Function<AlphaGoMath.CISAState, Double> evaluationFunction = state -> 1.0;
        
        int bestAction = alphaGo.selectBestAction(initialState, priorFunction, evaluationFunction);
        
        // Should return a valid action
        assertTrue(bestAction >= 0);
    }
    
    @Test
    public void testTreeStats() {
        AlphaGoMath alphaGo = new AlphaGoMath();
        
        CognitiveISA.Program program = new CognitiveISA.Builder().halt().build();
        AlphaGoMath.CISAState state = new AlphaGoMath.CISAState(program);
        
        AlphaGoMath.MCTSNode root = new AlphaGoMath.MCTSNode(state, null, 1.0);
        
        String stats = alphaGo.getTreeStats(root);
        
        assertNotNull(stats);
        assertTrue(stats.contains("MCTS"));
    }
}
