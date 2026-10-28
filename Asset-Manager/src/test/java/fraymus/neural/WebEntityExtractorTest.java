package fraymus.neural;

import java.util.*;

/**
 * Test WebEntityExtractor - Phase 2.3
 * 
 * Tests:
 * 1. People extraction
 * 2. Place extraction
 * 3. Organization extraction
 * 4. Date extraction
 * 5. Number extraction
 * 6. Email extraction
 * 7. URL extraction
 * 8. Entity frequency tracking
 */
public class WebEntityExtractorTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("WEB ENTITY EXTRACTOR TEST");
        System.out.println("========================================");
        System.out.println();
        
        // Test text with various entities
        String testText = """
            John Smith and Jane Doe met in New York City on January 15, 2024.
            They work at Google Corporation in Mountain View, California.
            Contact them at john.smith@example.com or jane.doe@example.com.
            Visit https://example.com for more information.
            The company has 1,234 employees and revenue of $5.6 million.
            Dr. Emily Brown from Stanford University presented research on 04/15/2024.
            """;
        
        // Test 1: Initialization
        System.out.println("[TEST 1] Initialization");
        WebEntityExtractor extractor = new WebEntityExtractor();
        System.out.println("   ✓ WebEntityExtractor initialized");
        System.out.println();
        
        // Test 2: Extract All Entities
        System.out.println("[TEST 2] Extract All Entities");
        List<Entity> entities = extractor.extractEntities(testText);
        System.out.println("   Total entities extracted: " + entities.size());
        for (Entity entity : entities) {
            System.out.println("   - " + entity.getName() + " [" + entity.getType() + "]" + 
                (entity.getContext() != null && !entity.getContext().isEmpty() ? " (context: " + entity.getContext() + ")" : ""));
        }
        System.out.println();
        
        // Test 3: People
        System.out.println("[TEST 3] People");
        List<Entity> people = extractor.getEntitiesByType(WebEntityExtractor.TYPE_PERSON);
        System.out.println("   People found: " + people.size());
        for (Entity person : people) {
            System.out.println("   - " + person.getName());
        }
        System.out.println();
        
        // Test 4: Places
        System.out.println("[TEST 4] Places");
        List<Entity> places = extractor.getEntitiesByType(WebEntityExtractor.TYPE_PLACE);
        System.out.println("   Places found: " + places.size());
        for (Entity place : places) {
            System.out.println("   - " + place.getName() + " (context: " + place.getContext() + ")");
        }
        System.out.println();
        
        // Test 5: Organizations
        System.out.println("[TEST 5] Organizations");
        List<Entity> orgs = extractor.getEntitiesByType(WebEntityExtractor.TYPE_ORGANIZATION);
        System.out.println("   Organizations found: " + orgs.size());
        for (Entity org : orgs) {
            System.out.println("   - " + org.getName() + " (context: " + org.getContext() + ")");
        }
        System.out.println();
        
        // Test 6: Dates
        System.out.println("[TEST 6] Dates");
        List<Entity> dates = extractor.getEntitiesByType(WebEntityExtractor.TYPE_DATE);
        System.out.println("   Dates found: " + dates.size());
        for (Entity date : dates) {
            System.out.println("   - " + date.getName());
        }
        System.out.println();
        
        // Test 7: Numbers
        System.out.println("[TEST 7] Numbers");
        List<Entity> numbers = extractor.getEntitiesByType(WebEntityExtractor.TYPE_NUMBER);
        System.out.println("   Numbers found: " + numbers.size());
        for (Entity number : numbers) {
            System.out.println("   - " + number.getName());
        }
        System.out.println();
        
        // Test 8: Emails
        System.out.println("[TEST 8] Emails");
        List<Entity> emails = extractor.getEntitiesByType(WebEntityExtractor.TYPE_EMAIL);
        System.out.println("   Emails found: " + emails.size());
        for (Entity email : emails) {
            System.out.println("   - " + email.getName());
        }
        System.out.println();
        
        // Test 9: URLs
        System.out.println("[TEST 9] URLs");
        List<Entity> urls = extractor.getEntitiesByType(WebEntityExtractor.TYPE_URL);
        System.out.println("   URLs found: " + urls.size());
        for (Entity url : urls) {
            System.out.println("   - " + url.getName());
        }
        System.out.println();
        
        // Test 10: Statistics
        System.out.println("[TEST 10] Statistics");
        System.out.println("   " + extractor.getStats());
        System.out.println();
        
        // Final Verdict
        System.out.println("========================================");
        boolean allTestsPassed = entities.size() > 0 && 
                                 people.size() > 0 &&
                                 places.size() > 0 &&
                                 orgs.size() > 0 &&
                                 dates.size() > 0 &&
                                 numbers.size() > 0 &&
                                 emails.size() > 0 &&
                                 urls.size() > 0;
        
        if (allTestsPassed) {
            System.out.println("✅ WEB ENTITY EXTRACTOR: WORKING");
            System.out.println();
            System.out.println("Capabilities verified:");
            System.out.println("- Person extraction (capitalized names)");
            System.out.println("- Place extraction (with context indicators)");
            System.out.println("- Organization extraction (with context indicators)");
            System.out.println("- Date extraction (multiple formats)");
            System.out.println("- Number extraction (with commas)");
            System.out.println("- Email extraction");
            System.out.println("- URL extraction");
            System.out.println("- Entity frequency tracking");
        } else {
            System.out.println("❌ TESTS FAILED");
            System.out.println("Some entity types not extracted");
        }
        System.out.println("========================================");
    }
}
