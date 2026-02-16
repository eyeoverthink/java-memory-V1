package fraymus.llm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

/**
 * 🧬 NATIVE BRIDGE - Gen 131
 * Panama Foreign Function Interface to llama.cpp
 * 
 * This is the membrane between Java and native C++ inference.
 * Uses Java 21 Foreign Function & Memory API (Project Panama).
 * 
 * "The bridge between worlds. Java speaks to the metal."
 */
public class NativeBridge implements AutoCloseable {
    
    private static final String LLAMA_LIB = "llama";
    
    private final Arena arena;
    private final Linker linker;
    private final SymbolLookup lookup;
    private boolean loaded = false;
    
    // Native function handles
    private MethodHandle llama_backend_init;
    private MethodHandle llama_backend_free;
    private MethodHandle llama_model_load;
    private MethodHandle llama_context_new;
    private MethodHandle llama_tokenize;
    private MethodHandle llama_decode;
    private MethodHandle llama_sample;
    private MethodHandle llama_token_to_str;
    
    public NativeBridge() {
        this.arena = Arena.ofShared();
        this.linker = Linker.nativeLinker();
        this.lookup = SymbolLookup.loaderLookup();
    }
    
    /**
     * LOAD - Initialize the native library
     */
    public boolean load(Path libraryPath) {
        try {
            System.load(libraryPath.toAbsolutePath().toString());
            bindFunctions();
            loaded = true;
            System.out.println("🔗 NATIVE BRIDGE: Loaded " + libraryPath);
            return true;
        } catch (UnsatisfiedLinkError e) {
            System.err.println("⚠️ NATIVE BRIDGE: Failed to load library: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * LOAD DEFAULT - Try standard library locations
     */
    public boolean loadDefault() {
        try {
            System.loadLibrary(LLAMA_LIB);
            bindFunctions();
            loaded = true;
            System.out.println("🔗 NATIVE BRIDGE: Loaded system llama library");
            return true;
        } catch (UnsatisfiedLinkError e) {
            // Try common locations
            String[] paths = {
                "C:/llama.cpp/build/Release/llama.dll",
                "C:/llama.cpp/llama.dll",
                "/usr/local/lib/libllama.so",
                "/opt/llama.cpp/libllama.so",
                System.getProperty("user.home") + "/.local/lib/libllama.so"
            };
            
            for (String path : paths) {
                try {
                    System.load(path);
                    bindFunctions();
                    loaded = true;
                    System.out.println("🔗 NATIVE BRIDGE: Loaded " + path);
                    return true;
                } catch (UnsatisfiedLinkError ignored) {}
            }
            
            System.err.println("⚠️ NATIVE BRIDGE: No llama library found");
            return false;
        }
    }
    
    private void bindFunctions() {
        // Panama FFI bindings would go here
        // For now, we use JNI-style native methods as fallback
    }
    
    public boolean isLoaded() { return loaded; }
    
    @Override
    public void close() {
        arena.close();
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // NATIVE METHOD DECLARATIONS (JNI fallback)
    // ═══════════════════════════════════════════════════════════════════════
    
    public static native long llamaBackendInit();
    public static native void llamaBackendFree();
    public static native long llamaModelLoad(String path, int nCtx, int nGpuLayers);
    public static native void llamaModelFree(long model);
    public static native long llamaContextNew(long model);
    public static native void llamaContextFree(long ctx);
    public static native int[] llamaTokenize(long ctx, String text, boolean addBos);
    public static native float[] llamaEval(long ctx, int[] tokens);
    public static native int llamaSample(long ctx, float temperature, float topP);
    public static native String llamaTokenToStr(long ctx, int token);
}
