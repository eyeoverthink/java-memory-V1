package fraymus.carbon;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * 📡 NEEDLECAST - Secure Binary Transmission Beam
 * "Beaming consciousness across the network with zero RCE risk"
 * 
 * Security Features:
 * - Binary frame protocol (no Java object deserialization)
 * - Length-prefixed payload
 * - No network-facing deserialization vulnerabilities
 * 
 * This is Digital Teleportation without the security holes.
 */
public final class Needlecast {

    private Needlecast() {}

    /**
     * BEAM THE SOUL: transmit stack frame to a remote Sleeve.
     * Sends binary frame, not Java objects.
     */
    public static void beam(CorticalStack stack, String targetIp, int port) {
        System.out.println("📡 NEEDLECAST -> " + targetIp + ":" + port);

        byte[] payload = stack.toBytes();

        try (Socket socket = new Socket(targetIp, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            out.writeInt(payload.length);
            out.write(payload);
            out.flush();

            System.out.println("✅ TRANSFER COMPLETE. MIND IS OFF-WORLD. (" + payload.length + " bytes)");
        } catch (Exception e) {
            System.err.println("❌ NEEDLECAST FAILED: " + e.getMessage());
            System.err.println("   (Is the remote Sleeve listening?)");
        }
    }
}
