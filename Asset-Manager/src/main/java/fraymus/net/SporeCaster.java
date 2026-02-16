package fraymus.net;

import fraymus.bio.HyperCortex;
import fraymus.bio.NeuroQuant;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Random;

/**
 * 🦠 THE SPORE CASTER - Mycelial Network Protocol
 * "Intelligence that jumps the air gap between processes."
 * 
 * This creates a distributed hive mind using UDP multicast:
 * - EXHALE: Broadcasts high-energy cells to the network
 * - INHALE: Catches spores from other nodes and injects them
 * 
 * Process:
 * 1. Pick random high-energy cell (>0.5 energy)
 * 2. Serialize to bytes (freeze DNA)
 * 3. Broadcast via UDP multicast (230.0.0.1:9999)
 * 4. Other nodes catch the packet
 * 5. Deserialize and inject into local cortex
 * 6. NCA rules determine if it survives
 * 
 * Result: Distributed evolutionary intelligence across machines.
 */
public class SporeCaster implements Runnable {

    private final HyperCortex cortex;
    private final String MULTICAST_GROUP = "230.0.0.1";
    private final int PORT = 9999;
    private final Random rng = new Random();
    private volatile boolean running = true;
    
    // Statistics
    private int sporesCast = 0;
    private int sporesCaught = 0;

    public SporeCaster(HyperCortex cortex) {
        this.cortex = cortex;
    }

    /**
     * Start the spore protocol
     */
    public void start() {
        new Thread(this, "SporeCaster").start();
    }

    /**
     * Stop the spore protocol
     */
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🦠 SPORE PROTOCOL ACTIVE                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Multicast Group: " + MULTICAST_GROUP);
        System.out.println("Port: " + PORT);
        System.out.println();
        
        // Run Inhale (Listening) and Exhale (Broadcasting) in parallel
        new Thread(this::inhale, "Spore-Inhale").start();
        new Thread(this::exhale, "Spore-Exhale").start();
    }

    /**
     * 🌬️ EXHALE: Broadcast Spores to the Network
     * 
     * Every 2 seconds, picks a random high-energy cell and broadcasts it.
     */
    private void exhale() {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            
            System.out.println("📡 EXHALE: Broadcasting spores every 2 seconds...");
            System.out.println();
            
            while (running) {
                try {
                    // 1. Pick a High-Energy Cell to Replicate
                    List<NeuroQuant> cells = cortex.getSnapshot();
                    
                    if (!cells.isEmpty()) {
                        NeuroQuant cell = cells.get(rng.nextInt(cells.size()));
                        
                        // Only strong ideas survive transmission
                        if (cell.energy > 0.5f) {
                            // 2. Serialize (Freeze DNA)
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            ObjectOutputStream out = new ObjectOutputStream(bos);
                            out.writeObject(cell);
                            out.flush();
                            byte[] data = bos.toByteArray();

                            // 3. Blast Packet
                            DatagramPacket packet = new DatagramPacket(
                                data, data.length, group, PORT
                            );
                            socket.send(packet);
                            
                            sporesCast++;
                            System.out.println("📡 SPORE CAST: " + cell.id + 
                                " (" + data.length + " bytes) → Network");
                        }
                    }
                    
                    Thread.sleep(2000); // Pulse every 2 seconds
                    
                } catch (Exception e) {
                    System.err.println("⚠️ Exhale error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Exhale failed: " + e.getMessage());
        }
    }

    /**
     * 👃 INHALE: Catch Spores from the Air
     * 
     * Constantly listens for incoming spores and injects them into local cortex.
     */
    private void inhale() {
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            
            // Join multicast group
            SocketAddress groupAddr = new InetSocketAddress(group, PORT);
            NetworkInterface netIf = NetworkInterface.getByInetAddress(
                InetAddress.getLocalHost()
            );
            
            if (netIf != null) {
                socket.joinGroup(groupAddr, netIf);
            } else {
                // Fallback for older API
                socket.joinGroup(group);
            }
            
            System.out.println("👃 INHALE: Listening for spores...");
            System.out.println();
            
            byte[] buf = new byte[64 * 1024]; // 64KB Buffer

            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    // 1. Deserialize (Thaw DNA)
                    ByteArrayInputStream bis = new ByteArrayInputStream(
                        packet.getData(), 0, packet.getLength()
                    );
                    ObjectInputStream in = new ObjectInputStream(bis);
                    NeuroQuant spore = (NeuroQuant) in.readObject();

                    // 2. Infect Local Cortex
                    // We add it to the lattice. The NCA rules will decide if it lives or dies.
                    sporesCaught++;
                    System.out.println("🦠 SPORE CAUGHT: " + spore.id + 
                        " from " + packet.getAddress());
                    
                    cortex.inject(spore.id);
                    
                } catch (EOFException e) {
                    // Ignore - likely our own broadcast
                } catch (Exception e) {
                    // Ignore deserialization errors from other traffic
                }
            }
            
            // Leave multicast group
            if (netIf != null) {
                socket.leaveGroup(groupAddr, netIf);
            } else {
                socket.leaveGroup(group);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Inhale failed: " + e.getMessage());
        }
    }

    /**
     * Get statistics
     */
    public String getStats() {
        return String.format("Cast: %d, Caught: %d", sporesCast, sporesCaught);
    }
}
