package fraymus.neural;

import java.util.*;
import java.util.regex.*;

/**
 * WebEntityExtractor - Named Entity Recognition
 * 
 * Extracts entities from text:
 * - People (PERSON)
 * - Places (PLACE)
 * - Organizations (ORGANIZATION)
 * - Dates (DATE)
 * - Numbers/Measurements (NUMBER)
 * 
 * Uses pattern-based extraction (heuristic NER)
 * Full ML-based NER can be added in future enhancement
 */
public class WebEntityExtractor {
    
    // Entity type constants
    public static final String TYPE_PERSON = "PERSON";
    public static final String TYPE_PLACE = "PLACE";
    public static final String TYPE_ORGANIZATION = "ORGANIZATION";
    public static final String TYPE_DATE = "DATE";
    public static final String TYPE_NUMBER = "NUMBER";
    public static final String TYPE_EMAIL = "EMAIL";
    public static final String TYPE_URL = "URL";
    
    // Common patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}[^\\s]*"
    );
    
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}|(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\.?\\s+\\d{1,2},?\\s+\\d{4})\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
        "\\b\\d{1,3}(,\\d{3})*(\\.\\d+)?\\b"
    );
    
    // Place indicators
    private static final Set<String> PLACE_INDICATORS = new HashSet<>(Arrays.asList(
        "city", "town", "village", "state", "country", "nation", "province",
        "region", "continent", "island", "river", "mountain", "ocean", "sea",
        "lake", "street", "road", "avenue", "boulevard", "lane", "drive"
    ));
    
    // Organization indicators
    private static final Set<String> ORG_INDICATORS = new HashSet<>(Arrays.asList(
        "corporation", "corp", "company", "co", "inc", "llc", "ltd", "limited",
        "university", "institute", "college", "school", "hospital", "clinic",
        "association", "society", "foundation", "bank", "agency", "department",
        "ministry", "government", "organization", "org", "group", "team"
    ));
    
    private final Map<String, Entity> extractedEntities = new HashMap<>();
    private final Map<String, Integer> entityFrequency = new HashMap<>();
    
    public WebEntityExtractor() {
        System.out.println("[WebEntityExtractor] Initialized");
    }
    
    /**
     * Extract all entities from text
     */
    public List<Entity> extractEntities(String text) {
        List<Entity> entities = new ArrayList<>();
        
        // Extract by type
        entities.addAll(extractPeople(text));
        entities.addAll(extractPlaces(text));
        entities.addAll(extractOrganizations(text));
        entities.addAll(extractDates(text));
        entities.addAll(extractNumbers(text));
        entities.addAll(extractEmails(text));
        entities.addAll(extractUrls(text));
        
        // Remove duplicates (keep first occurrence)
        Map<String, Entity> uniqueEntities = new LinkedHashMap<>();
        for (Entity entity : entities) {
            if (entity.getName() != null && !entity.getName().isEmpty()) {
                String key = entity.getName().toLowerCase() + "|" + entity.getType();
                if (!uniqueEntities.containsKey(key)) {
                    uniqueEntities.put(key, entity);
                }
            }
        }
        
        // Update frequency counts
        for (Entity entity : uniqueEntities.values()) {
            String key = entity.getName().toLowerCase();
            entityFrequency.put(key, entityFrequency.getOrDefault(key, 0) + 1);
        }
        
        extractedEntities.clear();
        for (Entity entity : uniqueEntities.values()) {
            extractedEntities.put(entity.getId(), entity);
        }
        
        return new ArrayList<>(uniqueEntities.values());
    }
    
    /**
     * Extract people (names)
     * Heuristic: Capitalized words that look like names
     */
    private List<Entity> extractPeople(String text) {
        List<Entity> people = new ArrayList<>();
        
        // Pattern for capitalized words (2-3 consecutive)
        Pattern namePattern = Pattern.compile("\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+){1,2})\\b");
        Matcher matcher = namePattern.matcher(text);
        
        Set<String> extracted = new HashSet<>();
        while (matcher.find()) {
            String name = matcher.group(1);
            
            // Filter out common non-name capitalized words
            if (!isCommonWord(name) && !extracted.contains(name.toLowerCase())) {
                Entity entity = new Entity(name, TYPE_PERSON);
                people.add(entity);
                extracted.add(name.toLowerCase());
            }
        }
        
        return people;
    }
    
    /**
     * Extract places
     * Heuristic: Capitalized words near place indicators
     */
    private List<Entity> extractPlaces(String text) {
        List<Entity> places = new ArrayList<>();
        
        // Look for capitalized words followed by place indicators
        for (String indicator : PLACE_INDICATORS) {
            Pattern pattern = Pattern.compile("\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\s+" + indicator + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            
            Set<String> extracted = new HashSet<>();
            while (matcher.find()) {
                String placeName = matcher.group(1);
                if (!extracted.contains(placeName.toLowerCase())) {
                    Entity entity = new Entity(placeName, TYPE_PLACE);
                    entity.setContext(indicator);
                    places.add(entity);
                    extracted.add(placeName.toLowerCase());
                }
            }
        }
        
        return places;
    }
    
    /**
     * Extract organizations
     * Heuristic: Capitalized words near organization indicators
     */
    private List<Entity> extractOrganizations(String text) {
        List<Entity> organizations = new ArrayList<>();
        
        // Look for capitalized words followed by organization indicators
        for (String indicator : ORG_INDICATORS) {
            Pattern pattern = Pattern.compile("\\b([A-Z][a-zA-Z]*(?:\\s+[A-Z][a-zA-Z]*)*)\\s+" + indicator + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            
            Set<String> extracted = new HashSet<>();
            while (matcher.find()) {
                String orgName = matcher.group(1);
                if (!extracted.contains(orgName.toLowerCase())) {
                    Entity entity = new Entity(orgName, TYPE_ORGANIZATION);
                    entity.setContext(indicator);
                    organizations.add(entity);
                    extracted.add(orgName.toLowerCase());
                }
            }
        }
        
        return organizations;
    }
    
    /**
     * Extract dates
     */
    private List<Entity> extractDates(String text) {
        List<Entity> dates = new ArrayList<>();
        Matcher matcher = DATE_PATTERN.matcher(text);
        
        Set<String> extracted = new HashSet<>();
        while (matcher.find()) {
            String dateStr = matcher.group(1);
            if (!extracted.contains(dateStr.toLowerCase())) {
                Entity entity = new Entity(dateStr, TYPE_DATE);
                dates.add(entity);
                extracted.add(dateStr.toLowerCase());
            }
        }
        
        return dates;
    }
    
    /**
     * Extract numbers
     */
    private List<Entity> extractNumbers(String text) {
        List<Entity> numbers = new ArrayList<>();
        Matcher matcher = NUMBER_PATTERN.matcher(text);
        
        Set<String> extracted = new HashSet<>();
        while (matcher.find()) {
            String numStr = matcher.group(1);
            if (!extracted.contains(numStr)) {
                Entity entity = new Entity(numStr, TYPE_NUMBER);
                numbers.add(entity);
                extracted.add(numStr);
            }
        }
        
        return numbers;
    }
    
    /**
     * Extract emails
     */
    private List<Entity> extractEmails(String text) {
        List<Entity> emails = new ArrayList<>();
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        
        Set<String> extracted = new HashSet<>();
        while (matcher.find()) {
            String email = matcher.group(0);
            if (!extracted.contains(email.toLowerCase())) {
                Entity entity = new Entity(email, TYPE_EMAIL);
                emails.add(entity);
                extracted.add(email.toLowerCase());
            }
        }
        
        return emails;
    }
    
    /**
     * Extract URLs
     */
    private List<Entity> extractUrls(String text) {
        List<Entity> urls = new ArrayList<>();
        Matcher matcher = URL_PATTERN.matcher(text);
        
        Set<String> extracted = new HashSet<>();
        while (matcher.find()) {
            String url = matcher.group(0);
            if (!extracted.contains(url.toLowerCase())) {
                Entity entity = new Entity(url, TYPE_URL);
                urls.add(entity);
                extracted.add(url.toLowerCase());
            }
        }
        
        return urls;
    }
    
    /**
     * Check if word is common (not a name)
     */
    private boolean isCommonWord(String word) {
        Set<String> commonWords = new HashSet<>(Arrays.asList(
            "The", "This", "That", "These", "Those", "A", "An", "Is", "Are", "Was",
            "Were", "Be", "Been", "Being", "Have", "Has", "Had", "Do", "Does", "Did",
            "Will", "Would", "Could", "Should", "May", "Might", "Must", "Can", "Cannot",
            "It", "Its", "They", "Their", "We", "Our", "You", "Your", "I", "My", "His",
            "Her", "Him", "She", "He", "What", "Which", "Who", "When", "Where", "Why",
            "How", "If", "Then", "Else", "Or", "And", "But", "So", "For", "Nor", "Yet"
        ));
        
        return commonWords.contains(word);
    }
    
    /**
     * Get entities by type
     */
    public List<Entity> getEntitiesByType(String type) {
        List<Entity> filtered = new ArrayList<>();
        for (Entity entity : extractedEntities.values()) {
            if (entity.getType().equals(type)) {
                filtered.add(entity);
            }
        }
        return filtered;
    }
    
    /**
     * Get entity frequency
     */
    public int getEntityFrequency(String entityName) {
        return entityFrequency.getOrDefault(entityName.toLowerCase(), 0);
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        int total = extractedEntities.size();
        int people = getEntitiesByType(TYPE_PERSON).size();
        int places = getEntitiesByType(TYPE_PLACE).size();
        int orgs = getEntitiesByType(TYPE_ORGANIZATION).size();
        int dates = getEntitiesByType(TYPE_DATE).size();
        int numbers = getEntitiesByType(TYPE_NUMBER).size();
        int emails = getEntitiesByType(TYPE_EMAIL).size();
        int urls = getEntitiesByType(TYPE_URL).size();
        
        return String.format(
            "[WebEntityExtractor] Total: %d, People: %d, Places: %d, Orgs: %d, Dates: %d, Numbers: %d, Emails: %d, URLs: %d",
            total, people, places, orgs, dates, numbers, emails, urls
        );
    }
}
