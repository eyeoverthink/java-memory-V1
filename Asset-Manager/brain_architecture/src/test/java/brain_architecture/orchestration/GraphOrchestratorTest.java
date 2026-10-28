package brain_architecture.orchestration;

import brain_architecture.backends.OllamaBackend;
import brain_architecture.core.BackendInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Test graph orchestrator for complex workflows
 */
public class GraphOrchestratorTest {
    
    private GraphOrchestrator orchestrator;
    private BackendInterface backend;
    
    @BeforeEach
    public void setUp() throws Exception {
        backend = new OllamaBackend();
        orchestrator = new GraphOrchestrator(backend);
    }
    
    @AfterEach
    public void tearDown() {
        if (backend != null) {
            backend.shutdown();
        }
    }
    
    @Test
    public void testWorkflowCreation() {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        assertEquals("test-workflow", workflow.getId());
        assertEquals("Test Workflow", workflow.getName());
        assertEquals("node1", workflow.getStartNodeId());
    }
    
    @Test
    public void testWorkflowNodeCreation() {
        GraphOrchestrator.WorkflowNode node = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        
        assertEquals("node1", node.getId());
        assertEquals("Node 1", node.getName());
        assertEquals(BackendInterface.TaskType.REASONING, node.getTaskType());
        assertFalse(node.isConditional());
    }
    
    @Test
    public void testWorkflowNodeWithDependencies() {
        GraphOrchestrator.WorkflowNode node = new GraphOrchestrator.WorkflowNode(
            "node2", "Node 2", BackendInterface.TaskType.GENERATION
        );
        
        node.addDependency("node1");
        node.addOutput("output1");
        
        assertEquals(1, node.getDependencies().size());
        assertEquals("node1", node.getDependencies().get(0));
        assertEquals(1, node.getOutputs().size());
        assertEquals("output1", node.getOutputs().get(0));
    }
    
    @Test
    public void testWorkflowNodeConditional() {
        GraphOrchestrator.WorkflowNode node = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        
        node.setConditional(true);
        node.setConditionExpression("node0.success == true");
        
        assertTrue(node.isConditional());
        assertEquals("node0.success == true", node.getConditionExpression());
    }
    
    @Test
    public void testWorkflowValidationValid() {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        
        GraphOrchestrator.WorkflowNode node2 = new GraphOrchestrator.WorkflowNode(
            "node2", "Node 2", BackendInterface.TaskType.GENERATION
        );
        node2.addDependency("node1");
        
        workflow.addNode(node1);
        workflow.addNode(node2);
        
        assertTrue(workflow.validate());
    }
    
    @Test
    public void testWorkflowValidationCycle() {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        node1.addDependency("node2"); // Creates cycle
        
        GraphOrchestrator.WorkflowNode node2 = new GraphOrchestrator.WorkflowNode(
            "node2", "Node 2", BackendInterface.TaskType.GENERATION
        );
        node2.addDependency("node1");
        
        workflow.addNode(node1);
        workflow.addNode(node2);
        
        assertFalse(workflow.validate());
    }
    
    @Test
    public void testWorkflowValidationMissingDependency() {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        node1.addDependency("nonexistent"); // Missing dependency
        
        workflow.addNode(node1);
        
        assertFalse(workflow.validate());
    }
    
    @Test
    public void testExecutionOrder() {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        
        GraphOrchestrator.WorkflowNode node2 = new GraphOrchestrator.WorkflowNode(
            "node2", "Node 2", BackendInterface.TaskType.GENERATION
        );
        node2.addDependency("node1");
        
        GraphOrchestrator.WorkflowNode node3 = new GraphOrchestrator.WorkflowNode(
            "node3", "Node 3", BackendInterface.TaskType.KNOWLEDGE
        );
        node3.addDependency("node2");
        
        workflow.addNode(node1);
        workflow.addNode(node2);
        workflow.addNode(node3);
        
        List<String> order = workflow.getExecutionOrder();
        
        // node1 should come before node2
        assertTrue(order.indexOf("node1") < order.indexOf("node2"));
        // node2 should come before node3
        assertTrue(order.indexOf("node2") < order.indexOf("node3"));
    }
    
    @Test
    public void testRegisterWorkflow() {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        
        workflow.addNode(node1);
        
        orchestrator.registerWorkflow(workflow);
        
        GraphOrchestrator.Workflow retrieved = orchestrator.getWorkflow("test-workflow");
        assertNotNull(retrieved);
        assertEquals("test-workflow", retrieved.getId());
    }
    
    @Test
    public void testUnregisterWorkflow() {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        
        workflow.addNode(node1);
        
        orchestrator.registerWorkflow(workflow);
        assertNotNull(orchestrator.getWorkflow("test-workflow"));
        
        orchestrator.unregisterWorkflow("test-workflow");
        assertNull(orchestrator.getWorkflow("test-workflow"));
    }
    
    @Test
    public void testExecuteWorkflow() throws Exception {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "What is 2+2?", BackendInterface.TaskType.REASONING
        );
        node1.addOutput("result1");
        
        workflow.addNode(node1);
        
        orchestrator.registerWorkflow(workflow);
        
        var future = orchestrator.executeWorkflow("test-workflow", Map.of());
        var result = future.join();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getNodeResults());
        assertTrue(result.getExecutionTimeMs() > 0);
    }
    
    @Test
    public void testExecuteWorkflowNotFound() throws Exception {
        var future = orchestrator.executeWorkflow("nonexistent", Map.of());
        var result = future.join();
        
        assertFalse(result.isSuccess());
        assertEquals("Workflow not found", result.getError());
    }
    
    @Test
    public void testExecuteWorkflowInvalid() {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        // Create invalid workflow with cycle
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Node 1", BackendInterface.TaskType.REASONING
        );
        node1.addDependency("node1"); // Self-cycle
        
        workflow.addNode(node1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            orchestrator.registerWorkflow(workflow);
        });
    }
    
    @Test
    public void testExecutionHistory() throws Exception {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Test query", BackendInterface.TaskType.REASONING
        );
        
        workflow.addNode(node1);
        orchestrator.registerWorkflow(workflow);
        
        // Execute workflow twice
        orchestrator.executeWorkflow("test-workflow", Map.of()).join();
        orchestrator.executeWorkflow("test-workflow", Map.of()).join();
        
        List<GraphOrchestrator.WorkflowResult> history = orchestrator.getExecutionHistory("test-workflow");
        
        assertEquals(2, history.size());
        assertTrue(history.get(0).getExecutionTimeMs() >= history.get(1).getExecutionTimeMs());
    }
    
    @Test
    public void testClearExecutionHistory() throws Exception {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Test query", BackendInterface.TaskType.REASONING
        );
        
        workflow.addNode(node1);
        orchestrator.registerWorkflow(workflow);
        
        orchestrator.executeWorkflow("test-workflow", Map.of()).join();
        
        assertEquals(1, orchestrator.getExecutionHistory("test-workflow").size());
        
        orchestrator.clearExecutionHistory();
        
        assertEquals(0, orchestrator.getExecutionHistory("test-workflow").size());
    }
    
    @Test
    public void testStatistics() throws Exception {
        GraphOrchestrator.Workflow workflow = new GraphOrchestrator.Workflow(
            "test-workflow", "Test Workflow", "node1"
        );
        
        GraphOrchestrator.WorkflowNode node1 = new GraphOrchestrator.WorkflowNode(
            "node1", "Test query", BackendInterface.TaskType.REASONING
        );
        
        workflow.addNode(node1);
        orchestrator.registerWorkflow(workflow);
        
        Map<String, Object> statsBefore = orchestrator.getStatistics();
        assertEquals(1, statsBefore.get("totalWorkflows"));
        assertEquals(0, statsBefore.get("totalExecutions"));
        
        orchestrator.executeWorkflow("test-workflow", Map.of()).join();
        
        Map<String, Object> statsAfter = orchestrator.getStatistics();
        assertEquals(1, statsAfter.get("totalWorkflows"));
        assertEquals(1, statsAfter.get("totalExecutions"));
        assertTrue((Double) statsAfter.get("avgExecutionTimeMs") >= 0);
    }
}
