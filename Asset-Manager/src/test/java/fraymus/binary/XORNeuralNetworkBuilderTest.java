package fraymus.binary;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XOR Neural Network Builder Test Suite
 * 
 * Tests the generation of bare-metal C code for the XOR neural network.
 */
public class XORNeuralNetworkBuilderTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    public void testBuildGeneratesAllFiles() throws IOException {
        XORNeuralNetworkBuilder builder = new XORNeuralNetworkBuilder(tempDir.toString());
        builder.build();
        
        // Verify all files were created
        assertTrue(Files.exists(tempDir.resolve("xor_neural_network.c")));
        assertTrue(Files.exists(tempDir.resolve("xor_neuron.h")));
        assertTrue(Files.exists(tempDir.resolve("test_harness.c")));
        assertTrue(Files.exists(tempDir.resolve("Makefile")));
    }
    
    @Test
    public void testXORNeuralNetworkCContent() throws IOException {
        XORNeuralNetworkBuilder builder = new XORNeuralNetworkBuilder(tempDir.toString());
        builder.build();
        
        String content = Files.readString(tempDir.resolve("xor_neural_network.c"));
        
        // Verify key functions are present
        assertTrue(content.contains("init_brain"));
        assertTrue(content.contains("tick"));
        assertTrue(content.contains("learn"));
        assertTrue(content.contains("popcount64"));
        
        // Verify mathematical operations
        assertTrue(content.contains("^"));  // XOR operation
        assertTrue(content.contains(">>")); // Bit shift
        assertTrue(content.contains("THRESHOLD"));
    }
    
    @Test
    public void testXORNeuronHeaderContent() throws IOException {
        XORNeuralNetworkBuilder builder = new XORNeuralNetworkBuilder(tempDir.toString());
        builder.build();
        
        String content = Files.readString(tempDir.resolve("xor_neuron.h"));
        
        // Verify constants
        assertTrue(content.contains("NUM_NEURONS 256"));
        assertTrue(content.contains("INPUT_SIZE 64"));
        assertTrue(content.contains("THRESHOLD 32"));
        
        // Verify API declarations
        assertTrue(content.contains("void init_brain"));
        assertTrue(content.contains("void tick"));
        assertTrue(content.contains("void learn"));
    }
    
    @Test
    public void testTestHarnessContent() throws IOException {
        XORNeuralNetworkBuilder builder = new XORNeuralNetworkBuilder(tempDir.toString());
        builder.build();
        
        String content = Files.readString(tempDir.resolve("test_harness.c"));
        
        // Verify test cases
        assertTrue(content.contains("Test 1"));
        assertTrue(content.contains("Test 2"));
        assertTrue(content.contains("Test 3"));
        assertTrue(content.contains("Test 4"));
        
        // Verify test patterns
        assertTrue(content.contains("0x0000000000000000"));
        assertTrue(content.contains("0xFFFFFFFFFFFFFFFF"));
        assertTrue(content.contains("0xAAAAAAAAAAAAAAAA"));
        assertTrue(content.contains("0x5555555555555555"));
    }
    
    @Test
    public void testMakefileContent() throws IOException {
        XORNeuralNetworkBuilder builder = new XORNeuralNetworkBuilder(tempDir.toString());
        builder.build();
        
        String content = Files.readString(tempDir.resolve("Makefile"));
        
        // Verify Makefile targets
        assertTrue(content.contains("all:"));
        assertTrue(content.contains("clean:"));
        assertTrue(content.contains("run:"));
        
        // Verify compiler flags
        assertTrue(content.contains("-O3"));
        assertTrue(content.contains("-march=native"));
    }
    
    @Test
    public void testXORNeuralNetworkCFileSize() throws IOException {
        XORNeuralNetworkBuilder builder = new XORNeuralNetworkBuilder(tempDir.toString());
        builder.build();
        
        long size = Files.size(tempDir.resolve("xor_neural_network.c"));
        
        // File should be reasonably sized (at least 1KB)
        assertTrue(size > 1000);
    }
    
    @Test
    public void testCustomOutputDirectory() throws IOException {
        Path customDir = tempDir.resolve("custom_output");
        XORNeuralNetworkBuilder builder = new XORNeuralNetworkBuilder(customDir.toString());
        builder.build();
        
        // Verify files were created in custom directory
        assertTrue(Files.exists(customDir.resolve("xor_neural_network.c")));
        assertTrue(Files.exists(customDir.resolve("xor_neuron.h")));
    }
}
