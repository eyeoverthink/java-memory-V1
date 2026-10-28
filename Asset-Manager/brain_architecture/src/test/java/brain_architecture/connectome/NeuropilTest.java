package brain_architecture.connectome;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

/**
 * Unit tests for Neuropil.
 */
public class NeuropilTest {
    
    @Test
    public void testConstructor() {
        Neuropil neuropil = new Neuropil("AL", "Antennal Lobe", Neuropil.NeuropilType.SENSORY);
        
        assertEquals("AL", neuropil.getName());
        assertEquals("Antennal Lobe", neuropil.getDescription());
        assertEquals(Neuropil.NeuropilType.SENSORY, neuropil.getType());
        assertNotNull(neuropil.getNeuropilId());
    }
    
    @Test
    public void testGetNeuropilId() {
        Neuropil neuropil = new Neuropil("MB", "Mushroom Body", Neuropil.NeuropilType.MEMORY);
        UUID id = neuropil.getNeuropilId();
        
        assertNotNull(id);
    }
    
    @Test
    public void testGetName() {
        Neuropil neuropil = new Neuropil("CX", "Central Complex", Neuropil.NeuropilType.MOTOR);
        
        assertEquals("CX", neuropil.getName());
    }
    
    @Test
    public void testGetDescription() {
        Neuropil neuropil = new Neuropil("SEZ", "Subesophageal Zone", Neuropil.NeuropilType.HUB);
        
        assertEquals("Subesophageal Zone", neuropil.getDescription());
    }
    
    @Test
    public void testGetType() {
        Neuropil neuropil = new Neuropil("LH", "Lateral Horn", Neuropil.NeuropilType.ASSOCIATION);
        
        assertEquals(Neuropil.NeuropilType.ASSOCIATION, neuropil.getType());
    }
    
    @Test
    public void testNeuropilTypeSensory() {
        Neuropil.NeuropilType type = Neuropil.NeuropilType.SENSORY;
        
        assertEquals("Sensory processing region", type.getDescription());
    }
    
    @Test
    public void testNeuropilTypeMotor() {
        Neuropil.NeuropilType type = Neuropil.NeuropilType.MOTOR;
        
        assertEquals("Motor output region", type.getDescription());
    }
    
    @Test
    public void testNeuropilTypeAssociation() {
        Neuropil.NeuropilType type = Neuropil.NeuropilType.ASSOCIATION;
        
        assertEquals("Association and integration region", type.getDescription());
    }
    
    @Test
    public void testNeuropilTypeMemory() {
        Neuropil.NeuropilType type = Neuropil.NeuropilType.MEMORY;
        
        assertEquals("Memory storage region", type.getDescription());
    }
    
    @Test
    public void testNeuropilTypeModulation() {
        Neuropil.NeuropilType type = Neuropil.NeuropilType.MODULATION;
        
        assertEquals("Neuromodulation region", type.getDescription());
    }
    
    @Test
    public void testNeuropilTypeHub() {
        Neuropil.NeuropilType type = Neuropil.NeuropilType.HUB;
        
        assertEquals("Central hub for information routing", type.getDescription());
    }
    
    @Test
    public void testToString() {
        Neuropil neuropil = new Neuropil("AL", "Antennal Lobe", Neuropil.NeuropilType.SENSORY);
        String str = neuropil.toString();
        
        assertTrue(str.contains("AL"));
        assertTrue(str.contains("SENSORY"));
    }
    
    @Test
    public void testEqualsSameObject() {
        Neuropil neuropil = new Neuropil("MB", "Mushroom Body", Neuropil.NeuropilType.MEMORY);
        
        assertEquals(neuropil, neuropil);
    }
    
    @Test
    public void testEqualsDifferentObject() {
        Neuropil neuropil1 = new Neuropil("MB", "Mushroom Body", Neuropil.NeuropilType.MEMORY);
        Neuropil neuropil2 = new Neuropil("MB", "Mushroom Body", Neuropil.NeuropilType.MEMORY);
        
        assertNotEquals(neuropil1, neuropil2); // Different IDs
    }
    
    @Test
    public void testEqualsNull() {
        Neuropil neuropil = new Neuropil("AL", "Antennal Lobe", Neuropil.NeuropilType.SENSORY);
        
        assertNotEquals(neuropil, null);
    }
    
    @Test
    public void testHashCode() {
        Neuropil neuropil = new Neuropil("CX", "Central Complex", Neuropil.NeuropilType.MOTOR);
        
        assertNotNull(neuropil.hashCode());
    }
    
    @Test
    public void testHashCodeConsistent() {
        Neuropil neuropil = new Neuropil("SEZ", "Subesophageal Zone", Neuropil.NeuropilType.HUB);
        
        int hash1 = neuropil.hashCode();
        int hash2 = neuropil.hashCode();
        
        assertEquals(hash1, hash2);
    }
}
