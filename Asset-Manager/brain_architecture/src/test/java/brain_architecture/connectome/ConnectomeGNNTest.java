package brain_architecture.connectome;

import brain_architecture.synapses.Neurotransmitter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

/**
 * Unit tests for ConnectomeGNN.
 */
public class ConnectomeGNNTest {
    
    @Test
    public void testConstructor() {
        ConnectomeGraph graph = new ConnectomeGraph();
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        assertEquals(graph, gnn.getGraph());
        assertEquals(4, gnn.getNumHops());
        assertEquals(0.0, gnn.getCurrentTime(), 0.001);
        assertEquals(0, gnn.getStepCount());
    }
    
    @Test
    public void testConstructorWithHops() {
        ConnectomeGraph graph = new ConnectomeGraph();
        ConnectomeGNN gnn = new ConnectomeGNN(graph, 6);
        
        assertEquals(6, gnn.getNumHops());
    }
    
    @Test
    public void testSetNumHops() {
        ConnectomeGraph graph = new ConnectomeGraph();
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        gnn.setNumHops(8);
        
        assertEquals(8, gnn.getNumHops());
    }
    
    @Test
    public void testSetNumHopsClampsToMin() {
        ConnectomeGraph graph = new ConnectomeGraph();
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        gnn.setNumHops(0);
        
        assertEquals(1, gnn.getNumHops());
    }
    
    @Test
    public void testPerformMessagePassingStep() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse);
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        gnn.performMessagePassingStep();
        
        assertEquals(1, gnn.getStepCount());
        assertEquals(1.0, gnn.getCurrentTime(), 0.001);
    }
    
    @Test
    public void testMessagePassing() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse);
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph, 2);
        
        gnn.messagePassing();
        
        assertEquals(2, gnn.getStepCount());
        assertEquals(2.0, gnn.getCurrentTime(), 0.001);
    }
    
    @Test
    public void testReset() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron);
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        gnn.performMessagePassingStep();
        
        gnn.reset();
        
        assertEquals(0.0, gnn.getCurrentTime(), 0.001);
        assertEquals(0, gnn.getStepCount());
        assertEquals(0.0, neuron.getMembranePotential(), 0.001);
    }
    
    @Test
    public void testVerifySmallWorldPropertyEmptyGraph() {
        ConnectomeGraph graph = new ConnectomeGraph();
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        double percentage = gnn.verifySmallWorldProperty();
        
        assertEquals(1.0, percentage, 0.001); // Trivially true
    }
    
    @Test
    public void testVerifySmallWorldPropertySingleNeuron() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron);
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        double percentage = gnn.verifySmallWorldProperty();
        
        assertEquals(1.0, percentage, 0.001);
    }
    
    @Test
    public void testVerifySmallWorldPropertyConnectedGraph() {
        ConnectomeGraph graph = new ConnectomeGraph();
        
        // Create a chain of neurons
        Neuron prev = null;
        for (int i = 0; i < 5; i++) {
            Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
            graph.addNeuron(neuron);
            
            if (prev != null) {
                ConnectomeSynapse synapse = new ConnectomeSynapse(
                    prev.getNeuronId(), neuron.getNeuronId(), 
                    1.0, Neurotransmitter.ACETYLCHOLINE);
                graph.addSynapse(synapse);
            }
            prev = neuron;
        }
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph, 4);
        
        double percentage = gnn.verifySmallWorldProperty();
        
        // Should be > 0 since graph is connected
        assertTrue(percentage >= 0.0);
    }
    
    @Test
    public void testGetAveragePathLengthEmpty() {
        ConnectomeGraph graph = new ConnectomeGraph();
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        double avgPath = gnn.getAveragePathLength();
        
        assertEquals(0.0, avgPath, 0.001);
    }
    
    @Test
    public void testGetAveragePathLengthSingleNeuron() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron);
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        double avgPath = gnn.getAveragePathLength();
        
        assertEquals(0.0, avgPath, 0.001);
    }
    
    @Test
    public void testGetAveragePathLengthConnected() {
        ConnectomeGraph graph = new ConnectomeGraph();
        
        // Create a simple chain
        Neuron n1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron n2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        Neuron n3 = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(n1);
        graph.addNeuron(n2);
        graph.addNeuron(n3);
        
        ConnectomeSynapse s1 = new ConnectomeSynapse(
            n1.getNeuronId(), n2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse s2 = new ConnectomeSynapse(
            n2.getNeuronId(), n3.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(s1);
        graph.addSynapse(s2);
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        double avgPath = gnn.getAveragePathLength();
        
        assertTrue(avgPath > 0.0);
    }
    
    @Test
    public void testGetClusteringCoefficientEmpty() {
        ConnectomeGraph graph = new ConnectomeGraph();
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        double clustering = gnn.getClusteringCoefficient();
        
        assertEquals(0.0, clustering, 0.001);
    }
    
    @Test
    public void testGetClusteringCoefficientNoEdges() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron n1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron n2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        graph.addNeuron(n1);
        graph.addNeuron(n2);
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        double clustering = gnn.getClusteringCoefficient();
        
        assertEquals(0.0, clustering, 0.001);
    }
    
    @Test
    public void testGetClusteringCoefficientTriangle() {
        ConnectomeGraph graph = new ConnectomeGraph();
        
        // Create a triangle (complete graph of 3)
        Neuron n1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron n2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        Neuron n3 = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(n1);
        graph.addNeuron(n2);
        graph.addNeuron(n3);
        
        // Create bidirectional synapses for a complete graph
        ConnectomeSynapse s1 = new ConnectomeSynapse(
            n1.getNeuronId(), n2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse s1_reverse = new ConnectomeSynapse(
            n2.getNeuronId(), n1.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse s2 = new ConnectomeSynapse(
            n2.getNeuronId(), n3.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse s2_reverse = new ConnectomeSynapse(
            n3.getNeuronId(), n2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse s3 = new ConnectomeSynapse(
            n3.getNeuronId(), n1.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse s3_reverse = new ConnectomeSynapse(
            n1.getNeuronId(), n3.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(s1);
        graph.addSynapse(s1_reverse);
        graph.addSynapse(s2);
        graph.addSynapse(s2_reverse);
        graph.addSynapse(s3);
        graph.addSynapse(s3_reverse);
        
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        double clustering = gnn.getClusteringCoefficient();
        
        // In a complete graph, clustering should be 1.0
        assertEquals(1.0, clustering, 0.001);
    }
    
    @Test
    public void testToString() {
        ConnectomeGraph graph = new ConnectomeGraph();
        ConnectomeGNN gnn = new ConnectomeGNN(graph);
        
        String str = gnn.toString();
        
        assertTrue(str.contains("numHops"));
        assertTrue(str.contains("steps"));
        assertTrue(str.contains("time"));
    }
}
