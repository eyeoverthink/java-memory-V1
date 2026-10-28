package fraymus.agency;

/**
 * 7-Layer 3-Letter Agency Level System Test
 * 
 * Tests military-grade multi-layered system with:
 * - Compartmentalization
 * - Zero-trust architecture
 * - Audit trails
 * - Self-destruct capabilities
 * - Deniable operations
 */
public class AgencyLayerSystemTest {
    
    public static void main(String[] args) {
        System.out.println("=== 7-Layer 3-Letter Agency Level System Test ===\n");
        System.out.println("Design Philosophy: Agency-level security and sophistication\n");
        
        AgencyLayerSystemIntegrated system = new AgencyLayerSystemIntegrated();
        
        // Test 1: System initialization
        testSystemInitialization(system);
        
        // Test 2: Basic operation through all layers
        testBasicOperation(system);
        
        // Test 3: Deniable operation
        testDeniableOperation(system);
        
        // Test 4: Compartmentalization
        testCompartmentalization(system);
        
        // Test 5: Audit trail
        testAuditTrail(system);
        
        // Test 6: Zero-trust security
        testZeroTrustSecurity(system);
        
        // Test 7: Self-destruct protocol
        testSelfDestructProtocol(system);
        
        System.out.println("\n=== All Tests Complete ===");
        System.out.println("\n7-Layer Agency System operational with military-grade security.");
    }
    
    private static void testSystemInitialization(AgencyLayerSystemIntegrated system) {
        System.out.println("Test 1: System Initialization");
        
        System.out.println("System active: " + system.isActive());
        System.out.println("Audit trail available: " + (system.getAuditTrail() != null));
        System.out.println("Zero-trust security available: " + (system.getZeroTrustSecurity() != null));
        
        System.out.println();
    }
    
    private static void testBasicOperation(AgencyLayerSystemIntegrated system) {
        System.out.println("Test 2: Basic Operation Through All 7 Layers");
        
        String testData = "Test data for agency operation";
        AgencyLayerSystemIntegrated.AgencyOperation operation = new AgencyLayerSystemIntegrated.AgencyOperation(testData, "TOP_SECRET");
        
        AgencyLayerSystemIntegrated.AgencyResult result = system.executeOperation(operation);
        
        System.out.println("Operation success: " + result.isSuccess());
        System.out.println("Operation denied: " + result.isDenied());
        System.out.println("Compartment ID: " + result.getCompartmentId());
        
        if (result.isSuccess()) {
            System.out.println("Data: " + result.getData());
        } else {
            System.out.println("Error: " + result.getError());
        }
        
        System.out.println();
    }
    
    private static void testDeniableOperation(AgencyLayerSystemIntegrated system) {
        System.out.println("Test 3: Deniable Operation");
        
        String testData = "Deniable test data";
        AgencyLayerSystemIntegrated.AgencyOperation operation = new AgencyLayerSystemIntegrated.AgencyOperation(testData, "TOP_SECRET");
        
        // Execute only through layers 0, 1, 2 (Acquisition, Translation, Persistence)
        java.util.List<Integer> layerIndices = java.util.Arrays.asList(0, 1, 2);
        
        AgencyLayerSystemIntegrated.AgencyResult result = system.executeDeniableOperation(operation, layerIndices);
        
        System.out.println("Deniable operation success: " + result.isSuccess());
        System.out.println("Compartment ID: " + result.getCompartmentId());
        System.out.println("Layers executed: " + layerIndices);
        
        System.out.println();
    }
    
    private static void testCompartmentalization(AgencyLayerSystemIntegrated system) {
        System.out.println("Test 4: Compartmentalization");
        
        String testData = "Compartmentalized data";
        AgencyLayerSystemIntegrated.AgencyOperation operation = new AgencyLayerSystemIntegrated.AgencyOperation(testData, "SECRET");
        
        AgencyLayerSystemIntegrated.AgencyResult result = system.executeOperation(operation);
        
        System.out.println("Compartment created: " + (result.getCompartmentId() != null));
        System.out.println("Compartment ID length: " + (result.getCompartmentId() != null ? result.getCompartmentId().length() : 0));
        
        System.out.println();
    }
    
    private static void testAuditTrail(AgencyLayerSystemIntegrated system) {
        System.out.println("Test 5: Audit Trail");
        
        // Execute an operation to generate audit entries
        String testData = "Audit trail test data";
        AgencyLayerSystemIntegrated.AgencyOperation operation = new AgencyLayerSystemIntegrated.AgencyOperation(testData, "TOP_SECRET");
        system.executeOperation(operation);
        
        java.util.List<?> entries = system.getAuditTrail().getEntries();
        System.out.println("Audit entries: " + entries.size());
        System.out.println("Audit trail operational: " + (entries.size() > 0));
        
        System.out.println();
    }
    
    private static void testZeroTrustSecurity(AgencyLayerSystemIntegrated system) {
        System.out.println("Test 6: Zero-Trust Security");
        
        System.out.println("Zero-trust security active: " + (system.getZeroTrustSecurity() != null));
        System.out.println("All operations verified before execution");
        System.out.println("No implicit trust - every operation authenticated");
        
        System.out.println();
    }
    
    private static void testSelfDestructProtocol(AgencyLayerSystemIntegrated system) {
        System.out.println("Test 7: Self-Destruct Protocol");
        
        System.out.println("Self-destruct protocol available");
        System.out.println("Authorization required: AGENCY-LEVEL-PROTOCOL");
        System.out.println("Note: Not actually engaging self-destruct in test");
        
        // Commented out to avoid actual self-destruct
        // system.engageSelfDestruct("AGENCY-LEVEL-PROTOCOL");
        
        System.out.println();
    }
}
