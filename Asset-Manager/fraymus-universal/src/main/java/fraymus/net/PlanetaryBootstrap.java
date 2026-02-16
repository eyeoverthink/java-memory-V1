package fraymus.net;

import fraymus.bio.NeuroQuant;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PlanetaryBootstrap {

    private final PlanetaryNode localNode;

    public PlanetaryBootstrap(PlanetaryNode node) {
        this.localNode = node;
    }

    /**
     * JOIN THE HIVE: Connect to a known seed node.
     */
    public void connectToSeed(String seedIp, int seedPort) {
        System.out.println("🚀 BOOTSTRAP: Attempting handshake with " + seedIp + ":" + seedPort + "...");

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(seedIp, seedPort), 5000);
            socket.setSoTimeout(10000);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            ObjectInputFilter filter = info -> {
                if (info.serialClass() == null) return ObjectInputFilter.Status.UNDECIDED;
                String name = info.serialClass().getName();
                if (name.startsWith("fraymus.bio.") || name.startsWith("fraymus.net.")) return ObjectInputFilter.Status.ALLOWED;
                if (name.startsWith("java.lang.") || name.startsWith("java.util.")) return ObjectInputFilter.Status.ALLOWED;
                return ObjectInputFilter.Status.REJECTED;
            };
            in.setObjectInputFilter(filter);

            // 1. SEND MY IDENTITY (Who I Am) + listen port
            System.out.println("   > Sending Vector Identity: " + localNode.self.id);
            out.writeObject(new PlanetaryNode.Hello(localNode.self, localNode.getPort()));
            out.flush();

            // 2. RECEIVE SEED IDENTITY (Who They Are)
            Object response = in.readObject();
            if (response instanceof NeuroQuant seedIdentity) {
                System.out.println("   < Handshake Accepted: Connected to " + seedIdentity.id);

                localNode.registerPeer(seedIdentity, seedIp + ":" + seedPort);
            }

            // 3. DOWNLOAD THE HIVE MAP (Gossip)
            System.out.println("   > Requesting Peer Table...");
            out.writeObject("SYNC_PEERS");
            out.flush();

            Object peerData = in.readObject();
            if (peerData instanceof Map<?, ?> map) {
                int count = 0;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (!(entry.getKey() instanceof NeuroQuant nq)) continue;
                    if (!(entry.getValue() instanceof String addr)) continue;

                    if (!nq.id.equals(localNode.self.id)) {
                        localNode.registerPeer(nq, addr);
                        count++;
                    }
                }
                System.out.println("   < HIVE SYNC: Learned " + count + " new vectors.");
            }

        } catch (Exception e) {
            System.err.println("❌ BOOTSTRAP FAILED: " + e.getMessage());
            System.err.println("   (Is the Genesis Node running?)");
        }
    }
}
