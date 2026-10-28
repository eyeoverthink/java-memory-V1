package fraymus.binary;

import java.io.*;
import java.nio.file.*;

/**
 * 🔬 BARE-METAL XOR NEURAL NETWORK BUILDER
 * 
 * Generates C code for a hyper-dimensional binary neural network (BNN)
 * that runs on bare-metal hardware using only XOR and POPCNT operations.
 * 
 * Mathematical Foundation:
 * - XOR Synapse: x ^ w (measures "surprise" or difference)
 * - Integration: popcount(x ^ w) (counts energy of surprise)
 * - Activation: threshold comparison (collapse to 0 or 1)
 * - Learning: Hebbian via XOR (neurons that fire together, XOR together)
 * 
 * Architecture:
 * - 256 neurons
 * - 64-bit input vectors
 * - Threshold-based activation
 * - Pure bitwise operations (no floating-point)
 * 
 * Philosophy: "Simplicity is where complication lies."
 */
public class XORNeuralNetworkBuilder {
    
    private String outputDir;
    
    public XORNeuralNetworkBuilder(String outputDir) {
        this.outputDir = outputDir;
    }
    
    /**
     * Generate all C code files for the XOR neural network
     */
    public void build() throws IOException {
        // Create output directory
        Path outputPath = Paths.get(outputDir);
        Files.createDirectories(outputPath);
        
        System.out.println("🔬 [XOR NEURAL NETWORK] GENERATING BARE-METAL C CODE...");
        
        // Generate xor_neural_network.c
        generateXORNeuralNetwork();
        
        // Generate xor_neuron.h
        generateXORNeuronHeader();
        
        // Generate test_harness.c
        generateTestHarness();
        
        // Generate Makefile
        generateMakefile();
        
        System.out.println("✓ XOR Neural Network C code generated successfully");
        System.out.println("  - xor_neural_network.c");
        System.out.println("  - xor_neuron.h");
        System.out.println("  - test_harness.c");
        System.out.println("  - Makefile");
    }
    
    /**
     * Generate the main XOR neural network C implementation
     */
    private void generateXORNeuralNetwork() throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("/**\n");
        sb.append(" * 🔬 BARE-METAL XOR NEURAL NETWORK\n");
        sb.append(" * \n");
        sb.append(" * Hyper-Dimensional Binary Neural Network (BNN)\n");
        sb.append(" * Uses only XOR and POPCNT operations for maximum efficiency\n");
        sb.append(" * \n");
        sb.append(" * Mathematical Foundation:\n");
        sb.append(" * - XOR Synapse: x ^ w (measures \"surprise\")\n");
        sb.append(" * - Integration: popcount(x ^ w) (counts energy)\n");
        sb.append(" * - Activation: threshold comparison\n");
        sb.append(" * - Learning: Hebbian via XOR\n");
        sb.append(" */\n\n");
        sb.append("#include <stdint.h>\n");
        sb.append("#include \"xor_neuron.h\"\n\n");
        sb.append("// Initialize the brain with random \"DNA\" (seed weights)\n");
        sb.append("void init_brain(uint64_t seed) {\n");
        sb.append("    uint64_t lfsr = seed;\n");
        sb.append("    for(int i = 0; i < NUM_NEURONS; i++) {\n");
        sb.append("        // SplitMix64 PRNG for bare-metal\n");
        sb.append("        lfsr += 0x9E3779B97F4A7C15ULL;\n");
        sb.append("        uint64_t z = lfsr;\n");
        sb.append("        z = (z ^ (z >> 30)) * 0xBF58476D1CE4E5B9ULL;\n");
        sb.append("        z = (z ^ (z >> 27)) * 0x94D049BB133111EBULL;\n");
        sb.append("        weights[i] = z ^ (z >> 31);\n");
        sb.append("        state[i] = 0;\n");
        sb.append("    }\n");
        sb.append("}\n\n");
        sb.append("// The Cognitive Loop: Process a 64-bit sensory input\n");
        sb.append("void tick(uint64_t sensory_input) {\n");
        sb.append("    for(int i = 0; i < NUM_NEURONS; i++) {\n");
        sb.append("        // 1. XOR Synapse (measures surprise)\n");
        sb.append("        uint64_t surprise = sensory_input ^ weights[i];\n");
        sb.append("        \n");
        sb.append("        // 2. Integration (count energy of surprise)\n");
        sb.append("        int energy = popcount64(surprise);\n");
        sb.append("        \n");
        sb.append("        // 3. Activation (collapse to 1 or 0)\n");
        sb.append("        if (energy > THRESHOLD) {\n");
        sb.append("            state[i] = 1;  // Neuron fires\n");
        sb.append("        } else {\n");
        sb.append("            state[i] = 0;  // Neuron rests\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n\n");
        sb.append("// Bare-metal Hebbian Learning via XOR\n");
        sb.append("// \"Neurons that fire together, XOR together\"\n");
        sb.append("void learn(uint64_t sensory_input) {\n");
        sb.append("    for(int i = 0; i < NUM_NEURONS; i++) {\n");
        sb.append("        if (state[i] == 1) {\n");
        sb.append("            // If neuron fired, adapt weight closer to input\n");
        sb.append("            // By XORing, we invert the differing bits\n");
        sb.append("            weights[i] = weights[i] ^ sensory_input;\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n\n");
        sb.append("// Get the current brain state (for debugging/monitoring)\n");
        sb.append("int get_active_neuron_count() {\n");
        sb.append("    int count = 0;\n");
        sb.append("    for(int i = 0; i < NUM_NEURONS; i++) {\n");
        sb.append("        if (state[i] == 1) count++;\n");
        sb.append("    }\n");
        sb.append("    return count;\n");
        sb.append("}\n\n");
        sb.append("// Get a specific neuron's state\n");
        sb.append("uint8_t get_neuron_state(int index) {\n");
        sb.append("    if (index >= 0 && index < NUM_NEURONS) {\n");
        sb.append("        return state[index];\n");
        sb.append("    }\n");
        sb.append("    return 0;\n");
        sb.append("}\n\n");
        sb.append("// Get a specific neuron's weight\n");
        sb.append("uint64_t get_neuron_weight(int index) {\n");
        sb.append("    if (index >= 0 && index < NUM_NEURONS) {\n");
        sb.append("        return weights[index];\n");
        sb.append("    }\n");
        sb.append("    return 0;\n");
        sb.append("}\n");
        
        writeFile(outputDir + "/xor_neural_network.c", sb.toString());
    }
    
    /**
     * Generate the XOR neuron header file
     */
    private void generateXORNeuronHeader() throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("/**\n");
        sb.append(" * 🔬 XOR NEURON HEADER\n");
        sb.append(" * \n");
        sb.append(" * Defines the architecture and constants for the XOR neural network\n");
        sb.append(" */\n\n");
        sb.append("#ifndef XOR_NEURON_H\n");
        sb.append("#define XOR_NEURON_H\n\n");
        sb.append("#include <stdint.h>\n\n");
        sb.append("// Architecture constants\n");
        sb.append("#define NUM_NEURONS 256\n");
        sb.append("#define INPUT_SIZE 64     // 64-bit input vector\n");
        sb.append("#define THRESHOLD 32      // Fire if more than 32 bits differ\n\n");
        sb.append("// The Connectome\n");
        sb.append("extern uint64_t weights[NUM_NEURONS];\n");
        sb.append("extern uint8_t state[NUM_NEURONS];\n\n");
        sb.append("// Bare-metal hardware population count\n");
        sb.append("// On x86, this compiles to the single `popcnt` instruction\n");
        sb.append("static inline int popcount64(uint64_t x) {\n");
        sb.append("    return __builtin_popcountll(x);\n");
        sb.append("}\n\n");
        sb.append("// API Functions\n");
        sb.append("void init_brain(uint64_t seed);\n");
        sb.append("void tick(uint64_t sensory_input);\n");
        sb.append("void learn(uint64_t sensory_input);\n");
        sb.append("int get_active_neuron_count(void);\n");
        sb.append("uint8_t get_neuron_state(int index);\n");
        sb.append("uint64_t get_neuron_weight(int index);\n\n");
        sb.append("#endif // XOR_NEURON_H\n");
        
        writeFile(outputDir + "/xor_neuron.h", sb.toString());
    }
    
    /**
     * Generate a test harness for the XOR neural network
     */
    private void generateTestHarness() throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("/**\n");
        sb.append(" * 🔬 XOR NEURAL NETWORK TEST HARNESS\n");
        sb.append(" * \n");
        sb.append(" * Tests the bare-metal XOR neural network\n");
        sb.append(" */\n\n");
        sb.append("#include <stdio.h>\n");
        sb.append("#include <stdint.h>\n");
        sb.append("#include \"xor_neuron.h\"\n\n");
        sb.append("// The Connectome (global memory)\n");
        sb.append("uint64_t weights[NUM_NEURONS];\n");
        sb.append("uint8_t state[NUM_NEURONS];\n\n");
        sb.append("int main() {\n");
        sb.append("    printf(\"🔬 XOR NEURAL NETWORK TEST HARNESS\\n\");\n");
        sb.append("    printf(\"==================================\\n\\n\");\n");
        sb.append("    printf(\"Architecture:\\n\");\n");
        sb.append("    - Neurons: %d\\n\", NUM_NEURONS);\n");
        sb.append("    - Input Size: %d bits\\n\", INPUT_SIZE);\n");
        sb.append("    - Threshold: %d\\n\\n\", THRESHOLD);\n");
        sb.append("    // Initialize brain with seed\n");
        sb.append("    printf(\"Initializing brain with seed 0x1337...\\n\");\n");
        sb.append("    init_brain(0x1337);\n");
        sb.append("    printf(\"Brain initialized.\\n\\n\");\n");
        sb.append("    // Test pattern 1: All zeros\n");
        sb.append("    printf(\"Test 1: Input = 0x0000000000000000\\n\");\n");
        sb.append("    tick(0x0000000000000000ULL);\n");
        sb.append("    printf(\"Active neurons: %d/%d\\n\", get_active_neuron_count(), NUM_NEURONS);\n");
        sb.append("    printf(\"\\n\");\n");
        sb.append("    // Test pattern 2: All ones\n");
        sb.append("    printf(\"Test 2: Input = 0xFFFFFFFFFFFFFFFF\\n\");\n");
        sb.append("    tick(0xFFFFFFFFFFFFFFFFULL);\n");
        sb.append("    printf(\"Active neurons: %d/%d\\n\", get_active_neuron_count(), NUM_NEURONS);\n");
        sb.append("    printf(\"\\n\");\n");
        sb.append("    // Test pattern 3: Alternating\n");
        sb.append("    printf(\"Test 3: Input = 0xAAAAAAAAAAAAAAAA\\n\");\n");
        sb.append("    tick(0xAAAAAAAAAAAAAAAAULL);\n");
        sb.append("    printf(\"Active neurons: %d/%d\\n\", get_active_neuron_count(), NUM_NEURONS);\n");
        sb.append("    printf(\"\\n\");\n");
        sb.append("    // Test learning\n");
        sb.append("    printf(\"Test 4: Learning with pattern 0x5555555555555555\\n\");\n");
        sb.append("    tick(0x5555555555555555ULL);\n");
        sb.append("    int before_learn = get_active_neuron_count();\n");
        sb.append("    learn(0x5555555555555555ULL);\n");
        sb.append("    printf(\"Before learn: %d active neurons\\n\", before_learn);\n");
        sb.append("    printf(\"After learn: weights adapted\\n\");\n");
        sb.append("    printf(\"\\n\");\n");
        sb.append("    printf(\"==================================\\n\");\n");
        sb.append("    printf(\"TEST HARNESS COMPLETE\\n\");\n");
        sb.append("    printf(\"==================================\\n\");\n");
        sb.append("    return 0;\n");
        sb.append("}\n");
        
        writeFile(outputDir + "/test_harness.c", sb.toString());
    }
    
    /**
     * Generate a Makefile for compiling the XOR neural network
     */
    private void generateMakefile() throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# 🔬 XOR NEURAL NETWORK MAKEFILE\n\n");
        sb.append("CC = gcc\n");
        sb.append("CFLAGS = -O3 -march=native -Wall -Wextra\n");
        sb.append("TARGET = xor_neural_network\n\n");
        sb.append("SRCS = xor_neural_network.c test_harness.c\n");
        sb.append("OBJS = $(SRCS:.c=.o)\n\n");
        sb.append("all: $(TARGET)\n\n");
        sb.append("$(TARGET): $(OBJS)\n");
        sb.append("\t$(CC) $(CFLAGS) -o $@ $^\n\n");
        sb.append("%.o: %.c xor_neuron.h\n");
        sb.append("\t$(CC) $(CFLAGS) -c $< -o $@\n\n");
        sb.append("clean:\n");
        sb.append("\trm -f $(OBJS) $(TARGET)\n\n");
        sb.append("run: $(TARGET)\n");
        sb.append("\t./$(TARGET)\n\n");
        sb.append(".PHONY: all clean run\n");
        
        writeFile(outputDir + "/Makefile", sb.toString());
    }
    
    /**
     * Write content to a file
     */
    private void writeFile(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes());
    }
    
    /**
     * Main method to run the builder
     */
    public static void main(String[] args) {
        try {
            String outputDir = args.length > 0 ? args[0] : "xor_neural_network";
            XORNeuralNetworkBuilder builder = new XORNeuralNetworkBuilder(outputDir);
            builder.build();
        } catch (IOException e) {
            System.err.println("Error building XOR neural network: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
