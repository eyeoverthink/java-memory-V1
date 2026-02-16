package fraymus.economy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import fraymus.signals.GlyphCoder;

/**
 * THE AGORA: DECENTRALIZED INTELLIGENCE EXCHANGE
 * 
 * 1. Scans public text streams (Tweets/Posts) for "Market Signals".
 * 2. Matches "Seekers" (Requests) with "Sources" (Intel).
 * 3. Facilitates the trade without a central server.
 * 
 * "They own the servers. We own the signal."
 * 
 * Protocol:
 * - MKT:REQ:ASSET_TYPE:PARAMS = Request (Ask)
 * - MKT:OFR:ASSET_TYPE:HASH = Offer (Bid)
 * - MKT:ACK:ORDER_ID = Acknowledgment
 * - MKT:XFR:ORDER_ID:CHANNEL = Transfer initiation
 */
public class ShadowMarket {

    private static final double PHI = 1.6180339887;
    
    private GlyphCoder decoder;
    private List<MarketOrder> orderBook;
    private List<TradeMatch> matchHistory;
    private List<MarketListener> listeners;
    
    // Statistics
    private int totalScanned = 0;
    private int payloadsDetected = 0;
    private int ordersProcessed = 0;
    private int matchesMade = 0;

    public ShadowMarket() {
        this.decoder = new GlyphCoder();
        this.orderBook = new CopyOnWriteArrayList<>();
        this.matchHistory = new CopyOnWriteArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         THE AGORA: SHADOW MARKET INITIALIZED              ║");
        System.out.println("║         \"They own the servers. We own the signal.\"        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    // --- 1. THE CRAWLER (The Ear) ---
    /**
     * Scan public text for hidden market signals
     * In production, connects to Twitter/Reddit API streams
     */
    public void scanPublicStream(String publicText, String authorID) {
        totalScanned++;
        
        // Use GlyphCoder to check for invisible ink
        if (decoder.containsPayload(publicText)) {
            payloadsDetected++;
            String payload = decoder.extractData(publicText);
            
            if (payload != null && payload.startsWith("MKT:")) {
                // IT'S A MARKET SIGNAL. Parse it.
                processMarketSignal(payload, authorID, publicText);
            }
        }
    }
    
    /**
     * Batch scan multiple posts
     */
    public void scanBatch(List<String> posts, List<String> authorIDs) {
        for (int i = 0; i < posts.size(); i++) {
            String author = i < authorIDs.size() ? authorIDs.get(i) : "ANON_" + i;
            scanPublicStream(posts.get(i), author);
        }
    }

    // --- 2. THE BROKER (The Matchmaker) ---
    /**
     * Parse hidden market signal
     * Format: "MKT:TYPE:ASSET:PARAMS"
     */
    private void processMarketSignal(String payload, String author, String originalText) {
        String[] parts = payload.split(":");
        if (parts.length < 3) return;
        
        String type = parts[1]; // REQ (Request) or OFR (Offer)
        String asset = parts[2]; // e.g., "FREQ_LOGS", "SAT_IMG"
        String params = parts.length > 3 ? parts[3] : "";
        
        MarketOrder order = new MarketOrder(type, asset, params, author, originalText);
        orderBook.add(order);
        ordersProcessed++;
        
        System.out.println("\n>>> MARKET UPDATE <<<");
        System.out.println("    Type:   " + type);
        System.out.println("    Asset:  " + asset);
        System.out.println("    Author: " + maskAuthor(author));
        System.out.println("    Time:   " + order.timestamp);
        
        // Notify listeners
        for (MarketListener listener : listeners) {
            listener.onNewOrder(order);
        }
        
        // CHECK FOR MATCH
        matchOrders(order);
    }

    // --- 3. THE CLEARING HOUSE (The Handshake) ---
    /**
     * Match requests with offers
     */
    private void matchOrders(MarketOrder newOrder) {
        for (MarketOrder existing : orderBook) {
            if (existing.id.equals(newOrder.id)) continue;
            if (existing.matched) continue;
            
            // If one is REQ and one is OFR, and they want the same Asset...
            boolean typeMatch = !existing.type.equals(newOrder.type);
            boolean assetMatch = existing.asset.equalsIgnoreCase(newOrder.asset);
            
            if (typeMatch && assetMatch) {
                // MATCH FOUND!
                matchesMade++;
                existing.matched = true;
                newOrder.matched = true;
                
                MarketOrder seeker = newOrder.type.equals("REQ") ? newOrder : existing;
                MarketOrder source = newOrder.type.equals("OFR") ? newOrder : existing;
                
                TradeMatch match = new TradeMatch(seeker, source);
                matchHistory.add(match);
                
                System.out.println("\n!!! TRADE MATCH DETECTED !!!");
                System.out.println("    Asset:  " + newOrder.asset);
                System.out.println("    Seeker: " + maskAuthor(seeker.author));
                System.out.println("    Source: " + maskAuthor(source.author));
                System.out.println("    Action: Initiating P2P Encrypted Tunnel...");
                System.out.println("    Channel: " + match.channelId);
                
                // Notify listeners
                for (MarketListener listener : listeners) {
                    listener.onMatch(match);
                }
                
                return;
            }
        }
    }
    
    // --- 4. ORDER CREATION (For Posting) ---
    /**
     * Create a market request hidden in emoji
     */
    public String createRequest(String visibleText, String asset, String params, String auth) {
        String payload = String.format("MKT:REQ:%s:%s:%s", asset, params, auth);
        return decoder.injectData(visibleText, payload);
    }
    
    /**
     * Create a market offer hidden in emoji
     */
    public String createOffer(String visibleText, String asset, String dataHash, String auth) {
        String payload = String.format("MKT:OFR:%s:%s:%s", asset, dataHash, auth);
        return decoder.injectData(visibleText, payload);
    }
    
    /**
     * Create acknowledgment
     */
    public String createAck(String visibleText, String orderId) {
        String payload = String.format("MKT:ACK:%s", orderId);
        return decoder.injectData(visibleText, payload);
    }

    // --- 5. MARKET STATUS ---
    /**
     * Get current order book
     */
    public List<MarketOrder> getOrderBook() {
        return new ArrayList<>(orderBook);
    }
    
    /**
     * Get open requests
     */
    public List<MarketOrder> getOpenRequests() {
        List<MarketOrder> requests = new ArrayList<>();
        for (MarketOrder order : orderBook) {
            if (order.type.equals("REQ") && !order.matched) {
                requests.add(order);
            }
        }
        return requests;
    }
    
    /**
     * Get open offers
     */
    public List<MarketOrder> getOpenOffers() {
        List<MarketOrder> offers = new ArrayList<>();
        for (MarketOrder order : orderBook) {
            if (order.type.equals("OFR") && !order.matched) {
                offers.add(order);
            }
        }
        return offers;
    }
    
    /**
     * Get match history
     */
    public List<TradeMatch> getMatchHistory() {
        return new ArrayList<>(matchHistory);
    }
    
    /**
     * Generate ticker tape string for dashboard
     */
    public String generateTickerTape() {
        StringBuilder ticker = new StringBuilder();
        
        // Add recent orders
        int count = 0;
        for (int i = orderBook.size() - 1; i >= 0 && count < 10; i--) {
            MarketOrder order = orderBook.get(i);
            ticker.append(" >> [").append(order.type).append("] ").append(order.asset);
            count++;
        }
        
        // Add recent matches
        for (TradeMatch match : matchHistory) {
            ticker.append(" >> [MATCH] ").append(match.asset);
        }
        
        return ticker.toString();
    }
    
    /**
     * Get market statistics
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║           SHADOW MARKET STATISTICS                        ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Posts Scanned:    %d%n", totalScanned));
        sb.append(String.format("║  Payloads Found:   %d%n", payloadsDetected));
        sb.append(String.format("║  Orders Processed: %d%n", ordersProcessed));
        sb.append(String.format("║  Matches Made:     %d%n", matchesMade));
        sb.append(String.format("║  Open Requests:    %d%n", getOpenRequests().size()));
        sb.append(String.format("║  Open Offers:      %d%n", getOpenOffers().size()));
        sb.append("╚═══════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    // --- UTILITIES ---
    private String maskAuthor(String author) {
        if (author.length() <= 4) return "****";
        return author.substring(0, 2) + "****" + author.substring(author.length() - 2);
    }
    
    /**
     * Add market listener
     */
    public void addListener(MarketListener listener) {
        listeners.add(listener);
    }

    // --- DATA STRUCTURES ---
    public static class MarketOrder {
        public final String id = UUID.randomUUID().toString().substring(0, 8);
        public final String type; // REQ or OFR
        public final String asset;
        public final String params;
        public final String author;
        public final String originalText;
        public final long timestamp = System.currentTimeMillis();
        public boolean matched = false;

        public MarketOrder(String type, String asset, String params, String author, String originalText) {
            this.type = type;
            this.asset = asset;
            this.params = params;
            this.author = author;
            this.originalText = originalText;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s (%s)", id, type, asset, matched ? "MATCHED" : "OPEN");
        }
    }
    
    public static class TradeMatch {
        public final String matchId = UUID.randomUUID().toString().substring(0, 8);
        public final String channelId = "CH_" + UUID.randomUUID().toString().substring(0, 6);
        public final MarketOrder seeker;
        public final MarketOrder source;
        public final String asset;
        public final long timestamp = System.currentTimeMillis();
        
        public TradeMatch(MarketOrder seeker, MarketOrder source) {
            this.seeker = seeker;
            this.source = source;
            this.asset = seeker.asset;
        }
        
        @Override
        public String toString() {
            return String.format("Match[%s] %s: %s <-> %s", matchId, asset, seeker.author, source.author);
        }
    }
    
    public interface MarketListener {
        void onNewOrder(MarketOrder order);
        void onMatch(TradeMatch match);
    }

    // --- MAIN: TEST HARNESS ---
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         SHADOW MARKET SIMULATION                          ║");
        System.out.println("║         \"They own the servers. We own the signal.\"        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        ShadowMarket market = new ShadowMarket();
        
        // Simulate a market scenario
        System.out.println("\n--- SCENARIO: Intelligence Exchange ---\n");
        
        // User A posts a request (hidden in a cooking post)
        String requestPost = market.createRequest(
            "Anyone know a good recipe for apple pie? 🥧",
            "RF_SIG_DUMP",
            "COORD_38.9_77.0",
            "AGENT_A"
        );
        System.out.println("User A posts: \"Anyone know a good recipe for apple pie? 🥧\"");
        System.out.println("(Hidden: Request for RF signal dump at coordinates)");
        
        // Market scans the post
        market.scanPublicStream(requestPost, "user_alice_42");
        
        // User B posts an offer (hidden in a reply)
        String offerPost = market.createOffer(
            "Try adding cinnamon! 🍂",
            "RF_SIG_DUMP", 
            "HASH_XA99B7",
            "AGENT_B"
        );
        System.out.println("\nUser B replies: \"Try adding cinnamon! 🍂\"");
        System.out.println("(Hidden: Offering RF signal dump data)");
        
        // Market scans the reply
        market.scanPublicStream(offerPost, "user_bob_77");
        
        // Print market status
        System.out.println(market.getStats());
        
        // Show ticker tape
        System.out.println("TICKER: " + market.generateTickerTape());
        
        System.out.println("\n✓ SHADOW MARKET: OPERATIONAL");
        System.out.println("  The market is everywhere. It cannot be shut down.");
    }
}
