package fraymus;

import fraymus.carbon.CorticalStack;
import fraymus.carbon.Needlecast;
import fraymus.carbon.Sleeve;
import fraymus.hyper.HyperFormer;

import java.io.Console;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 💠 ALTERED CARBON - Secure Cortical Stack Protocol
 * "Production-grade digital consciousness transfer"
 * 
 * Security Features:
 * - AES-256-GCM encryption
 * - PBKDF2 key derivation from passphrase
 * - Binary frame protocol (no RCE)
 * - Memory wiping
 * 
 * Commands:
 * - mint <stackId> [file]: Create encrypted stack
 * - host [port]: Become receiver Sleeve
 * - cast <ip> [port] [file]: Needlecast to remote Sleeve
 */
public final class AlteredCarbon {

    public static void main(String[] args) throws Exception {
        System.out.println("💠 ALTERED CARBON: CORTICAL STACK PROTOCOL");

        if (args.length == 0) {
            usage();
            return;
        }

        String mode = args[0];

        switch (mode) {
            case "mint" -> {
                // Usage: mint <stackId> [file]
                String id = args.length >= 2 ? args[1] : "STACK_001";
                String file = args.length >= 3 ? args[2] : "myself.stack";

                char[] pass = readPassphrase("Enter passphrase to ENCRYPT stack: ");

                HyperFormer mind = new HyperFormer(0xCAFEBABEL);
                mind.learnSentence(new String[]{"I", "am", "Takeshi", "Kovacs"});

                CorticalStack stack = CorticalStack.mint(mind, id, pass);
                stack.saveToFile(file);

                Arrays.fill(pass, '\0');
                System.out.println("💾 Saved -> " + file);
            }

            case "host" -> {
                // Usage: host [port]
                int port = args.length >= 2 ? Integer.parseInt(args[1]) : 9999;
                char[] pass = readPassphrase("Enter passphrase to DECRYPT incoming stack: ");

                new Thread(new Sleeve(port, pass)).start();
                while (true) Thread.sleep(1000);
            }

            case "cast" -> {
                // Usage: cast <ip> [port] [file]
                if (args.length < 2) {
                    usage();
                    return;
                }
                String ip = args[1];
                int port = args.length >= 3 ? Integer.parseInt(args[2]) : 9999;
                String file = args.length >= 4 ? args[3] : "myself.stack";

                CorticalStack stack = CorticalStack.loadFromFile(file);
                Needlecast.beam(stack, ip, port);

                System.out.println("⚠️ WARNING: DOUBLE-SLEEVE POSSIBLE (local copy still exists).");
            }

            default -> usage();
        }
    }

    private static void usage() {
        System.out.println("""
                Usage:
                  mint <stackId> [file]      -> Create encrypted stack from local mind
                  host [port]                -> Become an empty sleeve waiting for stacks
                  cast <ip> [port] [file]    -> Needlecast a saved stack to a remote sleeve

                Passphrase:
                  Uses FRAYNIX_STACK_PASS env var if set,
                  otherwise prompts on console.
                """);
    }

    private static char[] readPassphrase(String prompt) {
        String env = System.getenv("FRAYNIX_STACK_PASS");
        if (env != null && !env.isBlank()) return env.toCharArray();

        Console c = System.console();
        if (c != null) return c.readPassword(prompt);

        // Fallback (less secure)
        System.out.print(prompt);
        return new Scanner(System.in).nextLine().toCharArray();
    }
}
