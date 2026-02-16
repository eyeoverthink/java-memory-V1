package com.eyeoverthink.fraymus;

import com.eyeoverthink.fraymus.evolution.FraynixDNA;

import java.nio.file.Path;

public class PhoenixBoot {

    public static void main(String[] args) throws Exception {
        System.out.println("🔥 PHOENIX PROTOCOL: Watching Fraynix...");

        Path seedPath = Path.of(".fraynix", "Fraynix_Seed.dna");
        int crashCount = 0;

        while (true) {
            ProcessBuilder pb;
            if (isWindows()) {
                pb = new ProcessBuilder("cmd.exe", "/c", "gradlew.bat", "runFraynixOrganism", "--no-daemon");
            } else {
                pb = new ProcessBuilder("./gradlew", "runFraynixOrganism", "--no-daemon");
            }
            pb.inheritIO();

            Process fraynix = pb.start();
            int exitCode = fraynix.waitFor();

            if (exitCode != 0) {
                crashCount++;
                System.err.println("💥 FRAYNIX DIED (Code " + exitCode + "). RESURRECTION " + crashCount);

                FraynixDNA seed;
                try {
                    seed = FraynixDNA.loadSeed(seedPath);
                } catch (Exception e) {
                    seed = new FraynixDNA();
                }

                System.out.println("🧬 RESURRECTING GEN " + seed.generation + " (PhysicsEngine=" + seed.genes.get("PhysicsEngine") + ")");

                long backoffMs = Math.min(30_000, 1_000L * crashCount);
                Thread.sleep(backoffMs);
                continue;
            }

            System.out.println("✅ Fraynix shut down peacefully.");
            break;
        }
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win");
    }
}
