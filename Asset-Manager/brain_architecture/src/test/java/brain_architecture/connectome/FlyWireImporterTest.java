package brain_architecture.connectome;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

/**
 * Unit tests for FlyWireImporter.
 */
public class FlyWireImporterTest {
    
    @Test
    public void testConstructor() {
        FlyWireImporter importer = new FlyWireImporter();
        
        assertNotNull(importer.getGraph());
        assertEquals(0, importer.getNeuronsImported());
        assertEquals(0, importer.getSynapsesImported());
    }
    
    @Test
    public void testImportConnectome() {
        FlyWireImporter importer = new FlyWireImporter();
        
        ConnectomeGraph graph = importer.importConnectome(Paths.get("dummy.feather"));
        
        assertNotNull(graph);
        assertTrue(graph.getNeuronCount() > 0);
        assertTrue(graph.getSynapseCount() > 0);
        assertTrue(graph.getNeuropilCount() > 0);
    }
    
    @Test
    public void testImportConnectomeCreatesNeurons() {
        FlyWireImporter importer = new FlyWireImporter();
        
        importer.importConnectome(Paths.get("dummy.feather"));
        
        assertTrue(importer.getNeuronsImported() > 0);
    }
    
    @Test
    public void testImportConnectomeCreatesSynapses() {
        FlyWireImporter importer = new FlyWireImporter();
        
        importer.importConnectome(Paths.get("dummy.feather"));
        
        assertTrue(importer.getSynapsesImported() > 0);
    }
    
    @Test
    public void testImportConnectomeCreatesNeuropils() {
        FlyWireImporter importer = new FlyWireImporter();
        
        ConnectomeGraph graph = importer.importConnectome(Paths.get("dummy.feather"));
        
        assertTrue(graph.getNeuropilCount() > 0);
    }
    
    @Test
    public void testImportConnectomeDetectsRichClub() {
        FlyWireImporter importer = new FlyWireImporter();
        
        ConnectomeGraph graph = importer.importConnectome(Paths.get("dummy.feather"));
        
        assertTrue(graph.getRichClub().getMemberCount() > 0);
    }
    
    @Test
    public void testImportNeurotransmitterPredictions() {
        FlyWireImporter importer = new FlyWireImporter();
        
        // This is a placeholder - should not throw
        assertDoesNotThrow(() -> {
            importer.importNeurotransmitterPredictions(Paths.get("dummy.txt"));
        });
    }
    
    @Test
    public void testGetGraph() {
        FlyWireImporter importer = new FlyWireImporter();
        
        ConnectomeGraph graph = importer.getGraph();
        
        assertNotNull(graph);
    }
    
    @Test
    public void testGetNeuronsImported() {
        FlyWireImporter importer = new FlyWireImporter();
        
        assertEquals(0, importer.getNeuronsImported());
    }
    
    @Test
    public void testGetSynapsesImported() {
        FlyWireImporter importer = new FlyWireImporter();
        
        assertEquals(0, importer.getSynapsesImported());
    }
    
    @Test
    public void testGetStatistics() {
        FlyWireImporter importer = new FlyWireImporter();
        importer.importConnectome(Paths.get("dummy.feather"));
        
        FlyWireImporter.ImportStatistics stats = importer.getStatistics();
        
        assertTrue(stats.neuronsImported > 0);
        assertTrue(stats.synapsesImported > 0);
        assertTrue(stats.neuropilsCreated > 0);
    }
    
    @Test
    public void testImportFromCSV() {
        FlyWireImporter importer = new FlyWireImporter();
        
        // Placeholder - should not throw
        assertDoesNotThrow(() -> {
            ConnectomeGraph graph = importer.importFromCSV(Paths.get("dummy.csv"));
            assertNotNull(graph);
        });
    }
    
    @Test
    public void testExportToCSV() {
        FlyWireImporter importer = new FlyWireImporter();
        
        // Placeholder - should not throw
        assertDoesNotThrow(() -> {
            importer.exportToCSV(Paths.get("output.csv"));
        });
    }
    
    @Test
    public void testImportStatisticsToString() {
        FlyWireImporter.ImportStatistics stats = new FlyWireImporter.ImportStatistics(
            100, 500, 7, 30);
        
        String str = stats.toString();
        
        assertTrue(str.contains("neurons"));
        assertTrue(str.contains("synapses"));
        assertTrue(str.contains("neuropils"));
        assertTrue(str.contains("richClubMembers"));
    }
}
