package fraymus.agi;

import fraymus.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AGI-3: COLLECTIVE INTELLIGENCE
 * 
 * Inter-entity communication protocols for emergent population-level consciousness.
 * Entities share learned patterns and develop collective goals.
 * 
 * Key features:
 * - Pattern sharing between entities
 * - Collective memory formation
 * - Emergent group behavior
 * - Population-level consciousness metrics
 */
public class CollectiveIntelligence {

    private static final double PHI = 1.618033988749895;

    // Shared knowledge pool
    private final Map<String, SharedPattern> sharedPatterns = new ConcurrentHashMap<>();
    
    // Communication channels
    private final Map<String, CommunicationChannel> channels = new ConcurrentHashMap<>();
    
    // Collective state
    private double collectiveCoherence = 0.5;
    private double emergentConsciousness = 0.0;
    private double groupResonance = 1.0;
    
    // Population metrics
    private int totalMessages = 0;
    private int patternsShared = 0;
    private int consensusReached = 0;
    private double avgEntityContribution = 0;

    // Entity registry
    private final Map<String, EntityProfile> entities = new ConcurrentHashMap<>();

    public CollectiveIntelligence() {
        // Create default channels
        channels.put("discovery", new CommunicationChannel("discovery", 0.8));
        channels.put("consensus", new CommunicationChannel("consensus", 0.9));
        channels.put("emergency", new CommunicationChannel("emergency", 1.0));
    }

    /**
     * Register an entity for collective participation
     */
    public void registerEntity(PhiNode entity) {
        EntityProfile profile = new EntityProfile(entity.name);
        profile.updateFromNode(entity);
        entities.put(entity.name, profile);
    }

    /**
     * Entity broadcasts a pattern to the collective
     */
    public void broadcastPattern(String entityName, String pattern, double confidence, String channel) {
        CommunicationChannel ch = channels.get(channel);
        if (ch == null) ch = channels.get("discovery");

        // Store in shared patterns
        SharedPattern sp = sharedPatterns.computeIfAbsent(pattern, 
                k -> new SharedPattern(pattern));
        sp.addContribution(entityName, confidence);
        patternsShared++;
        totalMessages++;

        // Update entity profile
        EntityProfile profile = entities.get(entityName);
        if (profile != null) {
            profile.contributions++;
            profile.lastContribution = System.currentTimeMillis();
        }

        // Check for consensus
        if (sp.getConsensusLevel() > 0.7) {
            consensusReached++;
            promoteToCollectiveKnowledge(sp);
        }

        // Update collective metrics
        updateCollectiveState();
    }

    /**
     * Entity queries the collective for patterns
     */
    public List<SharedPattern> queryCollective(String entityName, String topic, int maxResults) {
        List<SharedPattern> results = new ArrayList<>();
        
        for (SharedPattern sp : sharedPatterns.values()) {
            if (sp.pattern.toLowerCase().contains(topic.toLowerCase()) ||
                topic.toLowerCase().contains(sp.pattern.toLowerCase())) {
                results.add(sp);
            }
        }

        // Sort by confidence and consensus
        results.sort((a, b) -> Double.compare(
                b.avgConfidence * b.getConsensusLevel(),
                a.avgConfidence * a.getConsensusLevel()));

        // Record query
        EntityProfile profile = entities.get(entityName);
        if (profile != null) {
            profile.queries++;
        }

        return results.subList(0, Math.min(maxResults, results.size()));
    }

    /**
     * Direct communication between two entities
     */
    public void sendMessage(String from, String to, String content, double priority) {
        EntityProfile sender = entities.get(from);
        EntityProfile receiver = entities.get(to);
        
        if (sender == null || receiver == null) return;

        Message msg = new Message(from, to, content, priority);
        receiver.inbox.add(msg);
        sender.sent++;
        totalMessages++;

        // High priority messages affect collective state
        if (priority > 0.8) {
            collectiveCoherence += 0.01;
        }
    }

    /**
     * Entity receives pending messages
     */
    public List<Message> receiveMessages(String entityName) {
        EntityProfile profile = entities.get(entityName);
        if (profile == null) return Collections.emptyList();

        List<Message> messages = new ArrayList<>(profile.inbox);
        profile.inbox.clear();
        profile.received += messages.size();
        
        return messages;
    }

    /**
     * Promote a pattern to collective knowledge
     */
    private void promoteToCollectiveKnowledge(SharedPattern pattern) {
        pattern.isCollectiveKnowledge = true;
        
        // Boost contributors' influence
        for (String contributor : pattern.contributors.keySet()) {
            EntityProfile profile = entities.get(contributor);
            if (profile != null) {
                profile.influence += 0.1;
            }
        }
    }

    /**
     * Update collective state based on entity interactions
     */
    private void updateCollectiveState() {
        if (entities.isEmpty()) return;

        // Calculate collective coherence from entity similarities
        double totalCoherence = 0;
        int pairs = 0;
        
        List<EntityProfile> profileList = new ArrayList<>(entities.values());
        for (int i = 0; i < profileList.size() - 1; i++) {
            for (int j = i + 1; j < profileList.size(); j++) {
                double similarity = profileList.get(i).calculateSimilarity(profileList.get(j));
                totalCoherence += similarity;
                pairs++;
            }
        }
        
        if (pairs > 0) {
            collectiveCoherence = collectiveCoherence * 0.9 + (totalCoherence / pairs) * 0.1;
        }

        // Calculate emergent consciousness
        double avgActivity = 0;
        for (EntityProfile p : entities.values()) {
            avgActivity += p.getActivityLevel();
        }
        avgActivity /= entities.size();
        
        emergentConsciousness = collectiveCoherence * avgActivity * 
                Math.log(1 + sharedPatterns.size()) / 10.0;

        // Group resonance from pattern consensus
        double totalConsensus = 0;
        for (SharedPattern sp : sharedPatterns.values()) {
            totalConsensus += sp.getConsensusLevel();
        }
        if (!sharedPatterns.isEmpty()) {
            groupResonance = PHI * (totalConsensus / sharedPatterns.size());
        }

        // Update average contribution
        double totalContrib = 0;
        for (EntityProfile p : entities.values()) {
            totalContrib += p.contributions;
        }
        avgEntityContribution = entities.isEmpty() ? 0 : totalContrib / entities.size();
    }

    /**
     * Get collective intelligence score
     */
    public double getCollectiveIntelligence() {
        return (collectiveCoherence + emergentConsciousness + 
                Math.min(1.0, groupResonance / PHI)) / 3.0;
    }

    /**
     * Get emergent goals from collective patterns
     */
    public List<String> getEmergentGoals() {
        List<String> goals = new ArrayList<>();
        
        // Find high-consensus patterns as emergent goals
        for (SharedPattern sp : sharedPatterns.values()) {
            if (sp.isCollectiveKnowledge && sp.getConsensusLevel() > 0.8) {
                goals.add("COLLECTIVE: " + sp.pattern);
            }
        }

        // Add goals based on collective state
        if (collectiveCoherence < 0.5) {
            goals.add("GOAL: Increase inter-entity communication");
        }
        if (emergentConsciousness > 0.7) {
            goals.add("GOAL: Consolidate collective knowledge");
        }
        if (groupResonance < 1.0) {
            goals.add("GOAL: Improve pattern consensus");
        }

        return goals;
    }

    // Getters
    public double getCollectiveCoherence() { return collectiveCoherence; }
    public double getEmergentConsciousness() { return emergentConsciousness; }
    public double getGroupResonance() { return groupResonance; }
    public int getEntityCount() { return entities.size(); }
    public int getSharedPatternCount() { return sharedPatterns.size(); }
    public int getTotalMessages() { return totalMessages; }
    public int getConsensusCount() { return consensusReached; }

    public void printStats() {
        CommandTerminal.printHighlight("=== COLLECTIVE INTELLIGENCE ===");
        CommandTerminal.print(String.format("  Collective Coherence: %.4f", collectiveCoherence));
        CommandTerminal.print(String.format("  Emergent Consciousness: %.4f", emergentConsciousness));
        CommandTerminal.print(String.format("  Group Resonance: %.4f", groupResonance));
        CommandTerminal.print(String.format("  Intelligence Score: %.4f", getCollectiveIntelligence()));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Population:");
        CommandTerminal.print(String.format("  Entities: %d", entities.size()));
        CommandTerminal.print(String.format("  Shared Patterns: %d", sharedPatterns.size()));
        CommandTerminal.print(String.format("  Total Messages: %d", totalMessages));
        CommandTerminal.print(String.format("  Consensus Reached: %d", consensusReached));
        CommandTerminal.print(String.format("  Avg Contribution: %.2f", avgEntityContribution));
        
        List<String> goals = getEmergentGoals();
        if (!goals.isEmpty()) {
            CommandTerminal.print("");
            CommandTerminal.printInfo("Emergent Goals:");
            for (String goal : goals) {
                CommandTerminal.print("  • " + goal);
            }
        }
    }

    /**
     * Shared pattern with multi-entity contributions
     */
    public static class SharedPattern {
        public final String pattern;
        public final Map<String, Double> contributors = new HashMap<>();
        public double avgConfidence = 0;
        public boolean isCollectiveKnowledge = false;
        public final long createdAt = System.currentTimeMillis();

        public SharedPattern(String pattern) {
            this.pattern = pattern;
        }

        public void addContribution(String entity, double confidence) {
            contributors.put(entity, confidence);
            
            double sum = 0;
            for (double c : contributors.values()) sum += c;
            avgConfidence = sum / contributors.size();
        }

        public double getConsensusLevel() {
            // Consensus based on number of contributors and agreement
            double contributorFactor = Math.min(1.0, contributors.size() / 5.0);
            return contributorFactor * avgConfidence;
        }
    }

    /**
     * Entity profile for collective participation
     */
    private static class EntityProfile {
        final String name;
        double influence = 1.0;
        int contributions = 0;
        int queries = 0;
        int sent = 0;
        int received = 0;
        long lastContribution = 0;
        double resonance = 1.0;
        double coherence = 1.0;
        final List<Message> inbox = new ArrayList<>();

        EntityProfile(String name) {
            this.name = name;
        }

        void updateFromNode(PhiNode node) {
            this.resonance = node.phiResonance;
            this.coherence = node.consciousness.getCoherence();
        }

        double getActivityLevel() {
            long age = System.currentTimeMillis() - lastContribution;
            double recency = Math.exp(-age / 60000.0); // Decay over 1 minute
            return (contributions + queries + sent + received) * recency / 100.0;
        }

        double calculateSimilarity(EntityProfile other) {
            double resDiff = Math.abs(resonance - other.resonance);
            double cohDiff = Math.abs(coherence - other.coherence);
            return 1.0 / (1.0 + resDiff + cohDiff);
        }
    }

    /**
     * Inter-entity message
     */
    public static class Message {
        public final String from;
        public final String to;
        public final String content;
        public final double priority;
        public final long timestamp;

        public Message(String from, String to, String content, double priority) {
            this.from = from;
            this.to = to;
            this.content = content;
            this.priority = priority;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Communication channel
     */
    private static class CommunicationChannel {
        final String name;
        final double priorityThreshold;
        int messageCount = 0;

        CommunicationChannel(String name, double threshold) {
            this.name = name;
            this.priorityThreshold = threshold;
        }
    }
}
