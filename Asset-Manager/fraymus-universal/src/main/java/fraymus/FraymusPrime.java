package fraymus;

import fraymus.brain.BicameralPrism;
import fraymus.carbon.CorticalStack;
import fraymus.carbon.Needlecast;
import fraymus.carbon.Sleeve;
import fraymus.hyper.HyperFormer;

import java.util.Arrays;
import java.util.Scanner;

/**
 * FRAYMUS PRIME: The Unified Entry Point.
 *
 * Hosts the HDC Brain, the LLM Spine, and the Network Sleeve
 * in a single interactive CLI.
 *
 * Commands:
 *   learn <words...>     - Teach the HDC brain a sentence
 *   predict <words...>   - Predict next word from context
 *   ask <prompt>         - Query the Bicameral LLM Prism
 *   mint                 - Encrypt brain into a CorticalStack
 *   load <file>          - Resleeve from a saved stack
 *   vocab                - Show brain stats
 *   exit                 - Quit
 *
 * CLI args:
 *   host <port>          - Start a Sleeve listener
 *   cast <ip> <file>     - Needlecast a stack to a remote Sleeve
 */
public final class FraymusPrime {

    private static HyperFormer HDC_MIND = new HyperFormer(4, 0.0);
    private static final BicameralPrism LLM_SPINE = new BicameralPrism();
    private static String ID = "PRIME_01";

    public static void main(String[] args) throws Exception {
        System.out.println("⚡ FRAYMUS PRIME: NEURO-SYMBOLIC HYBRID SYSTEM");
        System.out.println("----------------------------------------------");
        System.out.println("Modes: interactive (default), host <port>, cast <ip> <file>");

        if (args.length > 0) {
            handleArgs(args);
            return;
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\n" + ID + "> ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String cmd = tokens[0].toLowerCase();

            switch (cmd) {
                case "learn" -> {
                    String[] content = Arrays.copyOfRange(tokens, 1, tokens.length);
                    if (content.length < 2) {
                        System.out.println("   Usage: learn <word1> <word2> ...");
                    } else {
                        HDC_MIND.learnSentence(content);
                        System.out.println("   [HDC] Absorbed. Vocab: " + HDC_MIND.vocabSize()
                                + " | Associations: " + HDC_MIND.memorySize());
                    }
                }

                case "predict" -> {
                    String[] ctx = Arrays.copyOfRange(tokens, 1, tokens.length);
                    System.out.println("   [HDC] >> " + HDC_MIND.predictNext(ctx));
                }

                case "ask" -> {
                    if (tokens.length < 2) {
                        System.out.println("   Usage: ask <prompt>");
                    } else {
                        String prompt = input.substring(4);
                        System.out.println(LLM_SPINE.think(prompt));
                    }
                }

                case "mint" -> {
                    System.out.print("   Passphrase: ");
                    char[] pass = sc.nextLine().toCharArray();
                    CorticalStack stack = new CorticalStack(HDC_MIND, ID, pass);
                    stack.save(ID + ".stack");
                    Arrays.fill(pass, '\0');
                    System.out.println("   ✅ Saved: " + ID + ".stack");
                }

                case "load" -> {
                    if (tokens.length < 2) {
                        System.out.println("   Usage: load <filename>");
                    } else {
                        System.out.print("   Passphrase: ");
                        char[] loadPass = sc.nextLine().toCharArray();
                        CorticalStack loaded = CorticalStack.load(tokens[1]);
                        HDC_MIND = loaded.resleeve(loadPass);
                        Arrays.fill(loadPass, '\0');
                        System.out.println("   [SYS] Resleeved. Vocab: " + HDC_MIND.vocabSize()
                                + " | Associations: " + HDC_MIND.memorySize());
                    }
                }

                case "vocab" -> {
                    System.out.println("   Vocab: " + HDC_MIND.vocabSize()
                            + " | Associations: " + HDC_MIND.memorySize());
                }

                case "exit" -> {
                    LLM_SPINE.shutdown();
                    System.out.println("👁️ FRAYMUS PRIME OFFLINE.");
                    System.exit(0);
                }

                default -> {
                    System.out.println("   Unknown command: " + cmd);
                    System.out.println("   Commands: learn, predict, ask, mint, load, vocab, exit");
                }
            }
        }
    }

    private static void handleArgs(String[] args) throws Exception {
        switch (args[0]) {
            case "host" -> {
                int port = args.length > 1 ? Integer.parseInt(args[1]) : 9999;
                new Thread(new Sleeve(port)).start();
                System.out.println("Sleeve listening on :" + port);
                while (true) Thread.sleep(1000);
            }
            case "cast" -> {
                if (args.length < 3) {
                    System.out.println("Usage: cast <ip> <stackfile>");
                    return;
                }
                CorticalStack s = CorticalStack.load(args[2]);
                Needlecast.beam(s, args[1], 9999);
            }
            default -> {
                System.out.println("Unknown mode: " + args[0]);
                System.out.println("Modes: host <port>, cast <ip> <file>");
            }
        }
    }
}
