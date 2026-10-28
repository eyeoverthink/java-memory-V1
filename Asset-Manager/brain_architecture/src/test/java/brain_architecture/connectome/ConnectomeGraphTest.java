package brain_architecture.connectome;

import brain_architecture.synapses.Neurotransmitter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Unit tests for ConnectomeGraph.
 */
public class ConnectomeGraphTest {
    
    @Test
    public void testConstructor() {
        ConnectomeGraph graph = new ConnectomeGraph();
        
        assertEquals(0, graph.getNeuronCount());
        assertEquals(0, graph.getSynapseCount());
        assertEquals(0, graph.getNeuropilCount());
    }
    
    @Test
    public void testAddNeuron() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        graph.addNeuron(neuron);
        
        assertEquals(1, graph.getNeuronCount());
        assertEquals(neuron, graph.getNeuron(neuron.getNeuronId()));
    }
    
    @Test
    public void testAddSynapse() {
        ConnectomeGraph graph = new ConnectomeGraph();
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        
        graph.addSynapse(synapse);
        
        assertEquals(1, graph.getSynapseCount());
        assertEquals(synapse, graph.getSynapse(synapse.getSynapseId()));
    }
    
    @Test
    public void testRemoveNeuron() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron);
        
        graph.removeNeuron(neuron.getNeuronId());
        
        assertEquals(0, graph.getNeuronCount());
    }
    
    @Test
    public void testRemoveNeuronRemovesSynapses() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse);
        
        graph.removeNeuron(neuron1.getNeuronId());
        
        assertEquals(1, graph.getNeuronCount());
        assertEquals(0, graph.getSynapseCount());
    }
    
    @Test
    public void testRemoveSynapse() {
        ConnectomeGraph graph = new ConnectomeGraph();
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse);
        
        graph.removeSynapse(synapse.getSynapseId());
        
        assertEquals(0, graph.getSynapseCount());
    }
    
    @Test
    public void testGetNeuron() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron);
        
        Neuron retrieved = graph.getNeuron(neuron.getNeuronId());
        
        assertEquals(neuron, retrieved);
    }
    
    @Test
    public void testGetNeuronNotFound() {
        ConnectomeGraph graph = new ConnectomeGraph();
        
        assertNull(graph.getNeuron(UUID.randomUUID()));
    }
    
    @Test
    public void testGetSynapse() {
        ConnectomeGraph graph = new ConnectomeGraph();
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse);
        
        ConnectomeSynapse retrieved = graph.getSynapse(synapse.getSynapseId());
        
        assertEquals(synapse, retrieved);
    }
    
    @Test
    public void testGetAllNeurons() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        
        assertEquals(2, graph.getAllNeurons().size());
    }
    
    @Test
    public void testGetAllSynapses() {
        ConnectomeGraph graph = new ConnectomeGraph();
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        ConnectomeSynapse synapse1 = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse synapse2 = new ConnectomeSynapse(
            targetId, sourceId, 1.0, Neurotransmitter.GABA);
        graph.addSynapse(synapse1);
        graph.addSynapse(synapse2);
        
        assertEquals(2, graph.getAllSynapses().size());
    }
    
    @Test
    public void testAddNeuropil() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuropil neuropil = new Neuropil("AL", "Antennal Lobe", Neuropil.NeuropilType.SENSORY);
        
        graph.addNeuropil(neuropil);
        
        assertEquals(1, graph.getNeuropilCount());
        assertEquals(neuropil, graph.getNeuropil(neuropil.getNeuropilId()));
    }
    
    @Test
    public void testGetNeighbors() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        Neuron neuron3 = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        graph.addNeuron(neuron3);
        
        ConnectomeSynapse synapse1 = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse synapse2 = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron3.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse1);
        graph.addSynapse(synapse2);
        
        var neighbors = graph.getNeighbors(neuron1.getNeuronId());
        
        assertEquals(2, neighbors.size());
    }
    
    @Test
    public void testGetIncomingNeighbors() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        Neuron neuron3 = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        graph.addNeuron(neuron3);
        
        ConnectomeSynapse synapse1 = new ConnectomeSynapse(
            neuron2.getNeuronId(), neuron1.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse synapse2 = new ConnectomeSynapse(
            neuron3.getNeuronId(), neuron1.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse1);
        graph.addSynapse(synapse2);
        
        var neighbors = graph.getIncomingNeighbors(neuron1.getNeuronId());
        
        assertEquals(2, neighbors.size());
    }
    
    @Test
    public void testGetSynapses() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        
        ConnectomeSynapse synapse1 = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse synapse2 = new ConnectomeSynapse(
            neuron2.getNeuronId(), neuron1.getNeuronId(), 
            1.0, Neurotransmitter.GABA);
        graph.addSynapse(synapse1);
        graph.addSynapse(synapse2);
        
        var synapses = graph.getSynapses(neuron1.getNeuronId());
        
        assertEquals(2, synapses.size());
    }
    
    @Test
    public void testGetShortestPath() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        Neuron neuron3 = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        graph.addNeuron(neuron3);
        
        ConnectomeSynapse synapse1 = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse synapse2 = new ConnectomeSynapse(
            neuron2.getNeuronId(), neuron3.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse1);
        graph.addSynapse(synapse2);
        
        int distance = graph.getShortestPath(neuron1.getNeuronId(), neuron3.getNeuronId());
        
        assertEquals(2, distance);
    }
    
    @Test
    public void testGetShortestPathSameNode() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron1);
        
        int distance = graph.getShortestPath(neuron1.getNeuronId(), neuron1.getNeuronId());
        
        assertEquals(0, distance);
    }
    
    @Test
    public void testGetShortestPathNoPath() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        
        int distance = graph.getShortestPath(neuron1.getNeuronId(), neuron2.getNeuronId());
        
        assertEquals(-1, distance);
    }
    
    @Test
    public void testGetShortestPathInvalidNeuron() {
        ConnectomeGraph graph = new ConnectomeGraph();
        
        int distance = graph.getShortestPath(UUID.randomUUID(), UUID.randomUUID());
        
        assertEquals(-1, distance);
    }
    
    @Test
    public void testGetDegree() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        Neuron neuron3 = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        graph.addNeuron(neuron3);
        
        ConnectomeSynapse synapse1 = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse synapse2 = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron3.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse1);
        graph.addSynapse(synapse2);
        
        int degree = graph.getDegree(neuron1.getNeuronId());
        
        assertEquals(2, degree);
    }
    
    @Test
    public void testGetAverageDegree() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            neuron1.getNeuronId(), neuron2.getNeuronId(), 
            1.0, Neurotransmitter.ACETYLCHOLINE);
        graph.addSynapse(synapse);
        
        double avgDegree = graph.getAverageDegree();
        
        assertEquals(1.0, avgDegree, 0.001);
    }
    
    @Test
    public void testGetNeuronsInNeuropil() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuropil neuropil = new Neuropil("AL", "Antennal Lobe", Neuropil.NeuropilType.SENSORY);
        graph.addNeuropil(neuropil);
        
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY, neuropil);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.INHIBITORY, neuropil);
        Neuron neuron3 = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron1);
        graph.addNeuron(neuron2);
        graph.addNeuron(neuron3);
        
        var neurons = graph.getNeuronsInNeuropil(neuropil.getNeuropilId());
        
        assertEquals(2, neurons.size());
    }
    
    @Test
    public void testDetectRichClub() {
        ConnectomeGraph graph = new ConnectomeGraph();
        
        // Add neurons with varying connectivity
        for (int i = 0; i < 10; i++) {
            graph.addNeuron(new Neuron(Neuron.NeuronType.EXCITATORY));
        }
        
        // Create synapses (first neuron has most connections)
        var neurons = new ArrayList<>(graph.getAllNeurons());
        UUID hubId = neurons.get(0).getNeuronId();
        for (int i = 1; i < neurons.size(); i++) {
            ConnectomeSynapse synapse = new ConnectomeSynapse(
                hubId, neurons.get(i).getNeuronId(), 
                1.0, Neurotransmitter.ACETYLCHOLINE);
            graph.addSynapse(synapse);
        }
        
        graph.detectRichClub();
        
        assertTrue(graph.getRichClub().getMemberCount() > 0);
    }
    
    @Test
    public void testIsRichClubMember() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron);
        
        graph.detectRichClub();
        
        // With only one neuron, it should be in rich club
        assertTrue(graph.isRichClubMember(neuron.getNeuronId()));
    }
    
    @Test
    public void testResetAllNeurons() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron);
        neuron.activate(2.0, 0.0);
        
        graph.resetAllNeurons();
        
        assertEquals(0.0, neuron.getMembranePotential(), 0.001);
        assertEquals(0, neuron.getSpikeCount());
    }
    
    @Test
    public void testClear() {
        ConnectomeGraph graph = new ConnectomeGraph();
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        graph.addNeuron(neuron);
        
        graph.clear();
        
        assertEquals(0, graph.getNeuronCount());
        assertEquals(0, graph.getSynapseCount());
        assertEquals(0, graph.getNeuropilCount());
    }
    
    @Test
    public void testToString() {
        ConnectomeGraph graph = new ConnectomeGraph();
        String str = graph.toString();
        
        assertTrue(str.contains("neurons"));
        assertTrue(str.contains("synapses"));
        assertTrue(str.contains("neuropils"));
    }
}
