package fraymus.orchestration;

/**
 * Command Parser Test
 * Tests the Natural Language Instruction Bus for ai commands
 */
public class CommandParserTest {
    
    public static void main(String[] args) {
        System.out.println("=== Command Parser Test ===\n");
        
        // Test 1: Basic parsing
        testBasicParsing();
        
        // Test 2: Structured format
        testStructuredFormat();
        
        // Test 3: Free format
        testFreeFormat();
        
        // Test 4: Routing
        testRouting();
        
        // Test 5: Escape character detection
        testEscapeChars();
        
        // Test 6: Normalization
        testNormalization();

        // Test 7: Hydra transcend routing
        testHydraTranscendRouting();

        // Test 8: Hydra status routing
        testHydraStatusRouting();
        
        System.out.println("\n=== All Tests Complete ===");
    }
    
    private static void testBasicParsing() {
        System.out.println("Test 1: Basic Parsing");
        
        String command = "ai build field engine";
        CommandParser.ParsedCommand parsed = CommandParser.parse(command);
        
        System.out.println("Command: " + command);
        System.out.println("Parsed: " + parsed);
        System.out.println("Intent: " + parsed.intent);
        System.out.println("Target: " + parsed.target);
        System.out.println("Valid: " + parsed.isValid());
        System.out.println();
    }
    
    private static void testStructuredFormat() {
        System.out.println("Test 2: Structured Format");
        
        String command = "ai intent=build target=field_engine method=simd output=code";
        CommandParser.ParsedCommand parsed = CommandParser.parse(command);
        
        System.out.println("Command: " + command);
        System.out.println("Parsed: " + parsed);
        System.out.println("Intent: " + parsed.intent);
        System.out.println("Target: " + parsed.target);
        System.out.println("Output: " + parsed.outputFormat);
        System.out.println("Constraints: " + parsed.constraints);
        System.out.println();
    }
    
    private static void testFreeFormat() {
        System.out.println("Test 3: Free Format");
        
        String command = "ai generate rust code for field engine output code";
        CommandParser.ParsedCommand parsed = CommandParser.parse(command);
        
        System.out.println("Command: " + command);
        System.out.println("Parsed: " + parsed);
        System.out.println("Intent: " + parsed.intent);
        System.out.println("Target: " + parsed.target);
        System.out.println("Modifiers: " + parsed.modifiers);
        System.out.println("Output: " + parsed.outputFormat);
        System.out.println();
    }
    
    private static void testRouting() {
        System.out.println("Test 4: Command Routing");
        
        String[] commands = {
            "ai build engine",
            "ai analyze system",
            "ai generate code",
            "ai execute test",
            "ai fix bug",
            "ai optimize performance"
        };
        
        for (String cmd : commands) {
            String route = CommandParser.routeCommand(cmd);
            System.out.println(cmd + " → " + route);
        }
        System.out.println();
    }
    
    private static void testEscapeChars() {
        System.out.println("Test 5: Escape Character Detection");
        
        String[] commands = {
            "ai build engine",
            "ai build \"engine\"",
            "ai build 'engine'",
            "ai build `engine`",
            "ai build {engine}",
            "ai build [engine]"
        };
        
        for (String cmd : commands) {
            boolean hasEscapes = CommandParser.hasEscapeChars(cmd);
            System.out.println(cmd + " → has escapes: " + hasEscapes);
        }
        System.out.println();
    }
    
    private static void testNormalization() {
        System.out.println("Test 6: Command Normalization");
        
        String[] commands = {
            "ai build field engine",
            "ai build field engine method:simd output:code",
            "ai intent=build target=field_engine method=simd output=code"
        };
        
        for (String cmd : commands) {
            String normalized = CommandParser.normalize(cmd);
            System.out.println("Original: " + cmd);
            System.out.println("Normalized: " + normalized);
            System.out.println();
        }
    }

    private static void testHydraTranscendRouting() {
        System.out.println("Test 7: Hydra Transcend Routing");

        String command =
            "ai intent=transcend target=aubo_ledger method=mind_coalescence " +
            "features=dynamic_zk_snarks,wisdom_of_crowds constraints=incremental_proof_updates " +
            "output=distributed_execution_graph";
        CommandParser.ParsedCommand parsed = CommandParser.parse(command);
        String route = CommandParser.routeCommand(command);

        System.out.println("Command: " + command);
        System.out.println("Parsed intent: " + (parsed == null ? "null" : parsed.intent));
        System.out.println("Route: " + route);

        if (parsed == null || !"transcend".equals(parsed.intent)) {
            throw new IllegalStateException("Hydra command parse failed");
        }
        if (!"hydra".equals(route)) {
            throw new IllegalStateException("Hydra route expected, got: " + route);
        }
        System.out.println("Hydra routing assertion: PASS");
        System.out.println();
    }

    private static void testHydraStatusRouting() {
        System.out.println("Test 8: Hydra Status Routing");

        String commandA = "ai status hydra";
        CommandParser.ParsedCommand parsedA = CommandParser.parse(commandA);
        String routeA = CommandParser.routeCommand(commandA);

        String commandB = "ai hydra status";
        CommandParser.ParsedCommand parsedB = CommandParser.parse(commandB);
        String routeB = CommandParser.routeCommand(commandB);

        System.out.println("Command A: " + commandA + " -> " + routeA);
        System.out.println("Command B: " + commandB + " -> " + routeB);

        if (parsedA == null || !"status".equals(parsedA.intent) || !"hydra".equals(parsedA.target)) {
            throw new IllegalStateException("Hydra status parse failed for command A");
        }
        if (!"hydra_status".equals(routeA)) {
            throw new IllegalStateException("Hydra status route expected for command A");
        }
        if (parsedB == null || !"hydra".equals(parsedB.intent) || !"status".equals(parsedB.target)) {
            throw new IllegalStateException("Hydra status parse failed for command B");
        }
        if (!"hydra_status".equals(routeB)) {
            throw new IllegalStateException("Hydra status route expected for command B");
        }
        System.out.println("Hydra status routing assertion: PASS");
        System.out.println();
    }
}
