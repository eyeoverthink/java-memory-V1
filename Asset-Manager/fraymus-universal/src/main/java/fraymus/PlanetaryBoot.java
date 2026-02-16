package fraymus;

import fraymus.bio.NeuroQuant;
import fraymus.net.PlanetaryBootstrap;
import fraymus.net.PlanetaryNode;

import java.net.InetAddress;

public class PlanetaryBoot {

    public static void main(String[] args) throws Exception {
        System.out.println("\n🌍 FRAYMUS PLANETARY CORTEX v1.0");
        System.out.println("--------------------------------");

        String myName = "NODE_" + (System.currentTimeMillis() % 1000);
        NeuroQuant self = new NeuroQuant(myName);

        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8888;

        PlanetaryNode node = new PlanetaryNode(self, port);
        new Thread(node, "PlanetaryNode-" + port).start();

        String localIp = InetAddress.getLocalHost().getHostAddress();
        System.out.println("📍 LOCAL ADDRESS: " + localIp + ":" + port);

        if (args.length > 1) {
            String seedIp = args[1];
            int seedPort = (args.length > 2) ? Integer.parseInt(args[2]) : 8888;

            System.out.println("🔗 MODE: JOINING CLUSTER at " + seedIp + ":" + seedPort);
            PlanetaryBootstrap boot = new PlanetaryBootstrap(node);
            boot.connectToSeed(seedIp, seedPort);
        } else {
            System.out.println("👑 MODE: GENESIS (Waiting for peers...)");
        }

        while (true) Thread.sleep(1000);
    }
}
