package brain_architecture.connectome;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Unit tests for RichClub.
 */
public class RichClubTest {
    
    @Test
    public void testDefaultConstructor() {
        RichClub richClub = new RichClub();
        
        assertEquals(0.3, richClub.getThreshold(), 0.001);
        assertEquals(0, richClub.getMemberCount());
        assertEquals(0.0, richClub.getRichClubCoefficient(), 0.001);
    }
    
    @Test
    public void testConstructorWithThreshold() {
        RichClub richClub = new RichClub(0.5);
        
        assertEquals(0.5, richClub.getThreshold(), 0.001);
    }
    
    @Test
    public void testConstructorThresholdClampsToMax() {
        RichClub richClub = new RichClub(2.0);
        
        assertEquals(1.0, richClub.getThreshold(), 0.001);
    }
    
    @Test
    public void testConstructorThresholdClampsToMin() {
        RichClub richClub = new RichClub(0.0);
        
        assertEquals(0.1, richClub.getThreshold(), 0.001);
    }
    
    @Test
    public void testSetThreshold() {
        RichClub richClub = new RichClub();
        richClub.setThreshold(0.7);
        
        assertEquals(0.7, richClub.getThreshold(), 0.001);
    }
    
    @Test
    public void testSetThresholdClamps() {
        RichClub richClub = new RichClub();
        richClub.setThreshold(2.0);
        assertEquals(1.0, richClub.getThreshold(), 0.001);
        
        richClub.setThreshold(0.0);
        assertEquals(0.1, richClub.getThreshold(), 0.001);
    }
    
    @Test
    public void testDetectRichClub() {
        RichClub richClub = new RichClub(0.3);
        
        // Create adjacency list with varying degrees
        Map<UUID, List<UUID>> adjacencyList = new HashMap<>();
        UUID n1 = UUID.randomUUID();
        UUID n2 = UUID.randomUUID();
        UUID n3 = UUID.randomUUID();
        UUID n4 = UUID.randomUUID();
        UUID n5 = UUID.randomUUID();
        
        // n1 has 4 connections (highest)
        adjacencyList.put(n1, Arrays.asList(n2, n3, n4, n5));
        // n2 has 3 connections
        adjacencyList.put(n2, Arrays.asList(n1, n3, n4));
        // n3 has 3 connections
        adjacencyList.put(n3, Arrays.asList(n1, n2, n5));
        // n4 has 2 connections
        adjacencyList.put(n4, Arrays.asList(n1, n2));
        // n5 has 2 connections
        adjacencyList.put(n5, Arrays.asList(n1, n3));
        
        richClub.detectRichClub(adjacencyList);
        
        // Top 30% of 5 = 1.5 -> 2 members
        assertEquals(2, richClub.getMemberCount());
        assertTrue(richClub.isMember(n1));
    }
    
    @Test
    public void testDetectRichClubEmptyGraph() {
        RichClub richClub = new RichClub();
        Map<UUID, List<UUID>> adjacencyList = new HashMap<>();
        
        richClub.detectRichClub(adjacencyList);
        
        assertEquals(0, richClub.getMemberCount());
    }
    
    @Test
    public void testDetectRichClubSingleNeuron() {
        RichClub richClub = new RichClub();
        Map<UUID, List<UUID>> adjacencyList = new HashMap<>();
        UUID n1 = UUID.randomUUID();
        adjacencyList.put(n1, new ArrayList<>());
        
        richClub.detectRichClub(adjacencyList);
        
        assertEquals(1, richClub.getMemberCount());
    }
    
    @Test
    public void testIsMember() {
        RichClub richClub = new RichClub();
        UUID neuronId = UUID.randomUUID();
        
        richClub.addMember(neuronId);
        
        assertTrue(richClub.isMember(neuronId));
    }
    
    @Test
    public void testIsMemberNotInClub() {
        RichClub richClub = new RichClub();
        UUID neuronId = UUID.randomUUID();
        
        assertFalse(richClub.isMember(neuronId));
    }
    
    @Test
    public void testAddMember() {
        RichClub richClub = new RichClub();
        UUID neuronId = UUID.randomUUID();
        
        richClub.addMember(neuronId);
        
        assertEquals(1, richClub.getMemberCount());
        assertTrue(richClub.isMember(neuronId));
    }
    
    @Test
    public void testAddMemberDuplicate() {
        RichClub richClub = new RichClub();
        UUID neuronId = UUID.randomUUID();
        
        richClub.addMember(neuronId);
        richClub.addMember(neuronId);
        
        assertEquals(1, richClub.getMemberCount());
    }
    
    @Test
    public void testRemoveMember() {
        RichClub richClub = new RichClub();
        UUID neuronId = UUID.randomUUID();
        richClub.addMember(neuronId);
        
        richClub.removeMember(neuronId);
        
        assertEquals(0, richClub.getMemberCount());
        assertFalse(richClub.isMember(neuronId));
    }
    
    @Test
    public void testRemoveMemberNotInClub() {
        RichClub richClub = new RichClub();
        UUID neuronId = UUID.randomUUID();
        
        richClub.removeMember(neuronId);
        
        assertEquals(0, richClub.getMemberCount());
    }
    
    @Test
    public void testClear() {
        RichClub richClub = new RichClub();
        richClub.addMember(UUID.randomUUID());
        richClub.addMember(UUID.randomUUID());
        
        richClub.clear();
        
        assertEquals(0, richClub.getMemberCount());
        assertEquals(0.0, richClub.getRichClubCoefficient(), 0.001);
    }
    
    @Test
    public void testGetStatistics() {
        RichClub richClub = new RichClub(0.3);
        richClub.addMember(UUID.randomUUID());
        
        RichClub.RichClubStatistics stats = richClub.getStatistics();
        
        assertEquals(1, stats.memberCount);
        assertEquals(0.3, stats.threshold, 0.001);
    }
    
    @Test
    public void testRichClubCoefficient() {
        RichClub richClub = new RichClub(0.5);
        
        // Create complete graph among 3 neurons
        Map<UUID, List<UUID>> adjacencyList = new HashMap<>();
        UUID n1 = UUID.randomUUID();
        UUID n2 = UUID.randomUUID();
        UUID n3 = UUID.randomUUID();
        
        adjacencyList.put(n1, Arrays.asList(n2, n3));
        adjacencyList.put(n2, Arrays.asList(n1, n3));
        adjacencyList.put(n3, Arrays.asList(n1, n2));
        
        richClub.detectRichClub(adjacencyList);
        
        // Top 50% of 3 = 2 members
        // In complete graph, coefficient should be 1.0
        assertTrue(richClub.getRichClubCoefficient() > 0.5);
    }
    
    @Test
    public void testToString() {
        RichClub richClub = new RichClub(0.3);
        richClub.addMember(UUID.randomUUID());
        
        String str = richClub.toString();
        
        assertTrue(str.contains("members"));
        assertTrue(str.contains("threshold"));
        assertTrue(str.contains("coefficient"));
    }
    
    @Test
    public void testRichClubStatisticsToString() {
        RichClub.RichClubStatistics stats = new RichClub.RichClubStatistics(10, 0.3, 0.75);
        String str = stats.toString();
        
        assertTrue(str.contains("memberCount"));
        assertTrue(str.contains("threshold"));
        assertTrue(str.contains("coefficient"));
    }
}
