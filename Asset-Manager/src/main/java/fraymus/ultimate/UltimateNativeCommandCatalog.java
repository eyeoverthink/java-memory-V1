package fraymus.ultimate;

import java.util.List;

/**
 * Direct in-process command surface exposed by the immortal kernel.
 */
public final class UltimateNativeCommandCatalog implements UltimateCommandCatalog {

    private final List<CommandSection> sections;

    private UltimateNativeCommandCatalog(List<CommandSection> sections) {
        this.sections = List.copyOf(sections);
    }

    public static UltimateNativeCommandCatalog createDefault() {
        return new UltimateNativeCommandCatalog(List.of(
            section("runtime-native", "Runtime Native", "Kernel status, events, and shutdown controls.",
                cmd("native status", "Show the in-process runtime state"),
                cmd("native events", "Show recent immortal-kernel events"),
                cmd("native shutdown", "Stop native background systems")
            ),
            section("bridge-native", "Bridge Native", "Mapped-memory bridge, tensor sync, learning, pulse, and retrocausality.",
                cmd("aether", "Ignite the mapped-memory bridge"),
                cmd("braid", "Activate passive tensor synchronization"),
                cmd("learn <concept>", "Assimilate a concept into shared 16,384-D space"),
                cmd("pulse <concept>", "Broadcast a concept through the aether"),
                cmd("retro <present> <future>", "Solve the present->future catalyst"),
                cmd("emf <concept>", "Broadcast via EM side-channel")
            ),
            section("quantum-native", "Quantum Native", "Entanglement and dual-reality file operations executed in-process.",
                cmd("entangle create", "Create an entangled pair"),
                cmd("entangle up|down", "Observe a forced spin"),
                cmd("entangle kill", "Kill Alice and collapse Bob"),
                cmd("entangle network <n>", "Create an n-particle GHZ network"),
                cmd("entangle collapse", "Collapse the active pair or network"),
                cmd("qbox create <secret> | <decoy>", "Create a dual-reality container"),
                cmd("qbox secret", "Observe the truth path"),
                cmd("qbox decoy", "Observe the alibi path")
            ),
            section("genesis-native", "Genesis Native", "Collision, reality-forge, and chaos engines.",
                cmd("collide <A> <B>", "Smash concepts into a new element"),
                cmd("forge <concept>", "Manifest thought into physics"),
                cmd("forge list", "List known forgeable concepts"),
                cmd("echaos gen [n]", "Generate fractal chaos values"),
                cmd("echaos status", "Show chaos engine telemetry"),
                cmd("echaos mutate", "Force a chaos mutation")
            ),
            section("organism-native", "Organism Native", "NEXUS organism and omni-channel broadcast controls.",
                cmd("nexus awaken", "Awaken the organism"),
                cmd("nexus status", "Inspect NEXUS vital signs"),
                cmd("nexus sleep|wake", "Sleep or wake the organism"),
                cmd("nexus inject <text>", "Inject a thought into NEXUS"),
                cmd("nexus terminate", "Terminate the organism"),
                cmd("omnicast status", "Inspect omni-channel state"),
                cmd("omnicast monitor", "Start omni-channel monitoring"),
                cmd("omnicast <msg>", "Broadcast on all channels")
            ),
            section("spatial-native", "Spatial Native", "Spatial registry, gravity, fusion, PhiSuit, and Miving controls.",
                cmd("spatial status|map|nodes|fusions", "Inspect or render the spatial registry"),
                cmd("gravity start|stop|tick|status", "Control the gravity engine"),
                cmd("fusion start|stop|check|reset|status", "Control the fusion reactor"),
                cmd("suit wrap <data> <x> <y> <z> [label]", "Wrap data in a PhiSuit"),
                cmd("miving gen <n>", "Create a Miving brain"),
                cmd("miving evolve <n>", "Run Miving evolution"),
                cmd("miving pulse <n>", "Run Miving pulses"),
                cmd("miving chaos|status", "Mutate or inspect the Miving brain")
            )
        ));
    }

    @Override
    public String surfaceId() {
        return "native";
    }

    @Override
    public String surfaceTitle() {
        return "Ultimate Native Surface";
    }

    @Override
    public String surfaceDescription() {
        return "Direct in-process execution layer over the immortal kernel.";
    }

    @Override
    public String primarySubsystemId() {
        return "ultimate";
    }

    @Override
    public List<CommandSection> sections() {
        return sections;
    }

    @Override
    public List<String> linkedSubsystemIds(CommandSection section) {
        return List.of();
    }

    private static CommandSection section(String id, String title, String theme, CommandEntry... commands) {
        return new CommandSection(id, title, theme, List.of(commands));
    }

    private static CommandEntry cmd(String syntax, String description) {
        return new CommandEntry(syntax, description);
    }
}
