package brain_architecture.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SentientCoreBridge
 */
public class SentientCoreBridgeTest {
    
    @Test
    public void testInitialization() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        assertFalse(bridge.isWired());
        assertNull(bridge.getSentientCore());
    }
    
    @Test
    public void testWireSentientCore() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        bridge.wireSentientCore();
        assertTrue(bridge.isWired());
        assertNotNull(bridge.getSentientCore());
        assertTrue(bridge.isAvailable());
    }
    
    @Test
    public void testUnwireSentientCore() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        bridge.wireSentientCore();
        bridge.unwireSentientCore();
        assertFalse(bridge.isWired());
        assertNull(bridge.getSentientCore());
        assertFalse(bridge.isAvailable());
    }
    
    @Test
    public void testIdempotentWire() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        bridge.wireSentientCore();
        bridge.wireSentientCore(); // Should not error
        assertTrue(bridge.isWired());
        assertNotNull(bridge.getSentientCore());
    }
    
    @Test
    public void testIdempotentUnwire() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        bridge.unwireSentientCore(); // Should not error
        assertFalse(bridge.isWired());
    }
    
    @Test
    public void testProcessQueryWhenWired() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        bridge.wireSentientCore();
        String response = bridge.processQuery("What is 2+2?");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
    
    @Test
    public void testProcessQueryWhenNotWired() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        String response = bridge.processQuery("What is 2+2?");
        assertNull(response);
    }
    
    @Test
    public void testLearnWhenWired() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        bridge.wireSentientCore();
        String response = bridge.learn("The sky is blue.");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
    
    @Test
    public void testLearnWhenNotWired() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        String response = bridge.learn("The sky is blue.");
        assertNull(response);
    }
    
    @Test
    public void testGetStatusWhenWired() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        bridge.wireSentientCore();
        String status = bridge.getStatus();
        assertNotNull(status);
        assertFalse(status.equals("SentientCore: NOT WIRED"));
    }
    
    @Test
    public void testGetStatusWhenNotWired() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        String status = bridge.getStatus();
        assertNotNull(status);
        assertEquals("SentientCore: NOT WIRED", status);
    }
    
    @Test
    public void testIsAvailableWhenWired() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        bridge.wireSentientCore();
        assertTrue(bridge.isAvailable());
    }
    
    @Test
    public void testIsAvailableWhenNotWired() {
        SentientCoreBridge bridge = new SentientCoreBridge();
        assertFalse(bridge.isAvailable());
    }
}
