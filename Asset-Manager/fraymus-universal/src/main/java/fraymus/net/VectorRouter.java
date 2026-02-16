package fraymus.net;

import fraymus.bio.NeuroQuant;

import java.util.List;
import java.util.Map;

public class VectorRouter {

    /**
     * FIND EXPERT: Who in my network understands this concept best?
     */
    public String routeQuery(NeuroQuant query, List<NeuroQuant> peers, Map<String, String> addressBook) {
        NeuroQuant bestPeer = null;
        double maxResonance = -1.0;

        System.out.println("📡 ROUTING THOUGHT: " + (query != null ? query.id : "null"));

        if (query == null || peers == null || peers.isEmpty()) return null;

        for (NeuroQuant peer : peers) {
            if (peer == null) continue;
            double similarity = query.resonance(peer);

            System.out.println("   > Peer " + peer.id + " Resonance: " + String.format("%.4f", similarity));

            if (similarity > maxResonance) {
                maxResonance = similarity;
                bestPeer = peer;
            }
        }

        if (bestPeer != null && maxResonance > 0.1) {
            String ip = addressBook.get(bestPeer.id);
            System.out.println("✅ TARGET ACQUIRED: Forwarding to " + bestPeer.id + " (" + ip + ")");
            return ip;
        }

        return null;
    }
}
