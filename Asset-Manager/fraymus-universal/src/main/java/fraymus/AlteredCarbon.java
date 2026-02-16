package fraymus;

import fraymus.carbon.CorticalStack;
import fraymus.carbon.Needlecast;
import fraymus.carbon.Sleeve;
import fraymus.hyper.HyperFormer;

public final class AlteredCarbon {

    public static void main(String[] args) throws Exception {
        System.out.println("💠 ALTERED CARBON: CORTICAL STACK PROTOCOL");

        if (args.length == 0) {
            printUsage();
            return;
        }

        String mode = args[0];

        switch (mode) {
            case "mint" -> {
                char[] pass = getPass();

                HyperFormer mind = new HyperFormer(4, 0.0);
                mind.learnSentence(new String[]{"I", "am", "Takeshi", "Kovacs"});
                mind.learnSentence(new String[]{"I", "am", "Fraynix"});

                CorticalStack stack = new CorticalStack(mind, "STACK_001", pass);
                stack.save("myself.stack");
                System.out.println("✅ Saved: myself.stack");
            }

            case "cast" -> {
                if (args.length < 2) { printUsage(); return; }
                String target = args[1];

                CorticalStack payload = CorticalStack.load("myself.stack");
                Needlecast.beam(payload, target, 9999);

                System.out.println("⚠️ NOTE: This is a COPY-transfer. Delete local file if you want single-sleeve semantics.");
            }

            case "host" -> {
                new Thread(new Sleeve(9999)).start();
                while (true) Thread.sleep(1000);
            }

            default -> printUsage();
        }
    }

    private static char[] getPass() {
        String env = System.getenv("FRAYNIX_STACK_PASSPHRASE");
        if (env != null && !env.isBlank()) return env.toCharArray();
        return "change-me-now".toCharArray();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  mint              -> Create a new encrypted Stack (writes myself.stack)");
        System.out.println("  cast <IP>         -> Beam stack to remote Sleeve");
        System.out.println("  host              -> Wait for incoming stacks on :9999");
        System.out.println("\nTip: set FRAYNIX_STACK_PASSPHRASE env var on BOTH ends.");
    }
}
