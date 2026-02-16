package fraymus.network;

import fraymus.signals.SonicBridge;
import fraymus.signals.OpticalBreach;
import fraymus.signals.CosmicListener;
import fraymus.physics.FanConductor;
import fraymus.memory.CentripetalMem;
import fraymus.quantum.core.PhiQuantumConstants;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * THE OMNI-CASTER: UNIVERSAL BEACON
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "We speak to the eye, the ear, the wire, and the heat simultaneously."
 * 
 * The God Class for communications. Fires on all cylinders at once:
 * - OPTICAL: Screen → Camera (LSB steganography in pixels)
 * - SONIC: Speaker → Microphone (Ultrasonic 19-21kHz)
 * - COSMIC: SDR Radio → Antenna (RF spectrum)
 * - THERMAL: CPU → Fan → Air (Morse code via heat)
 * 
 * Firewalls cannot stop light. Airgaps cannot stop sound.
 * If it has a screen, speaker, or fan, it can transmit.
 * If it has a camera, microphone, or antenna, it can receive.
 */
public class OmniCaster {

    private static final double PHI = PhiQuantumConstants.PHI;

    // ═══════════════════════════════════════════════════════════════════
    // CHANNELS
    // ═══════════════════════════════════════════════════════════════════
    private final SonicBridge audioChannel;
    private final OpticalBreach visualChannel;
    private final CosmicListener radioChannel;
    private final FanConductor thermalChannel;
    private final CentripetalMem memoryCore;

    // State
    private AtomicBoolean isBroadcasting = new AtomicBoolean(false);
    private AtomicBoolean isMonitoring = new AtomicBoolean(false);
    
    // Configuration
    private boolean enableAudio = true;
    private boolean enableVisual = true;
    private boolean enableRadio = false;  // Requires SDR hardware
    private boolean enableThermal = false; // CPU-intensive
    
    // Last beacon for retrieval
    private BufferedImage lastBeacon;
    private String lastMessage;

    public OmniCaster() {
        this.audioChannel = new SonicBridge();
        this.visualChannel = new OpticalBreach();
        this.radioChannel = new CosmicListener();
        this.thermalChannel = new FanConductor();
        this.memoryCore = new CentripetalMem();
        
        System.out.println("═══ OMNI-CASTER INITIALIZED ═══");
        System.out.println("Channels: AUDIO | VISUAL | RADIO | THERMAL");
    }

    // ═══════════════════════════════════════════════════════════════════
    // BROADCAST (Transmit on all channels)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Broadcast message on all enabled channels simultaneously
     */
    public void broadcastToEverything(String message) {
        if (isBroadcasting.get()) {
            System.out.println("Already broadcasting!");
            return;
        }
        
        isBroadcasting.set(true);
        lastMessage = message;
        
        System.out.println("═══════════════════════════════════════════");
        System.out.println(">>> INITIATING OMNI-CAST BROADCAST <<<");
        System.out.println("═══════════════════════════════════════════");
        System.out.println("Message: " + message);
        System.out.println();
        
        // Store in memory core (high importance - we're broadcasting it!)
        memoryCore.storeData("BROADCAST:" + message, 0.9, new String[]{"broadcast", "outgoing"});
        
        // 1. AUDIO: Scream it in Ultrasonic
        if (enableAudio) {
            new Thread(() -> {
                try {
                    System.out.println(">> [AUDIO] Starting ultrasonic broadcast...");
                    audioChannel.broadcast(message);
                } catch (Exception e) {
                    System.err.println(">> [AUDIO] Error: " + e.getMessage());
                }
            }, "OmniCast-Audio").start();
        }
        
        // 2. VISUAL: Flash it on the Screen
        if (enableVisual) {
            new Thread(() -> {
                System.out.println(">> [VISUAL] Generating optical beacon...");
                lastBeacon = visualChannel.generateBeacon(message, 256, 256);
                System.out.println(">> [VISUAL] Beacon ready (256x256 pixels)");
                System.out.println(visualChannel.getBandwidthInfo(256, 256));
            }, "OmniCast-Visual").start();
        }
        
        // 3. RADIO: Modulate it on RF (requires SDR)
        if (enableRadio && radioChannel.isConnected()) {
            new Thread(() -> {
                System.out.println(">> [RADIO] RF modulation not implemented");
                // Would require TX-capable SDR
            }, "OmniCast-Radio").start();
        }
        
        // 4. THERMAL: Morse code via CPU heat
        if (enableThermal) {
            new Thread(() -> {
                System.out.println(">> [THERMAL] Starting fan conductor...");
                thermalChannel.transmitMorse(message);
            }, "OmniCast-Thermal").start();
        }
        
        // Wait briefly then mark complete
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            isBroadcasting.set(false);
            System.out.println(">> OMNI-CAST INITIATED ON ALL CHANNELS");
        }).start();
    }

    /**
     * Broadcast binary data
     */
    public void broadcastBinary(byte[] data, String label) {
        System.out.println(">>> BINARY OMNI-CAST: " + label + " (" + data.length + " bytes)");
        
        // Store in memory
        memoryCore.storeBinary(data, 0.8, label);
        
        // Generate optical beacon
        if (enableVisual) {
            lastBeacon = visualChannel.generateBeacon(
                new String(java.util.Base64.getEncoder().encode(data)), 512, 512);
        }
        
        // Thermal broadcast (binary mode)
        if (enableThermal) {
            thermalChannel.transmitBinary(data);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MONITOR (Listen on all channels)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Start monitoring all channels for incoming signals
     */
    public void startMonitoring() {
        if (isMonitoring.get()) {
            System.out.println("Already monitoring!");
            return;
        }
        
        isMonitoring.set(true);
        
        System.out.println("═══════════════════════════════════════════");
        System.out.println(">>> OMNI-MONITOR ACTIVATED <<<");
        System.out.println("═══════════════════════════════════════════");
        
        // 1. AUDIO: Listen for ultrasonic chirps
        if (enableAudio) {
            audioChannel.setSignalListener(new SonicBridge.SignalListener() {
                @Override
                public void onSignalDetected(String data, double strength) {
                    System.out.println(">> [AUDIO RX] Signal: " + data);
                    memoryCore.storeData("RECEIVED_AUDIO:" + data, 0.7, 
                        new String[]{"received", "audio"});
                }
                
                @Override
                public void onChirpDetected(int frequency, double magnitude) {
                    if (magnitude > 0.5) {
                        System.out.println(">> [AUDIO] Chirp: " + frequency + " Hz (mag: " + 
                            String.format("%.2f", magnitude) + ")");
                    }
                }
            });
            audioChannel.startListening();
            System.out.println(">> [AUDIO] Listening on 19-21 kHz...");
        }
        
        // 2. RADIO: Connect to SDR if available
        if (enableRadio) {
            new Thread(() -> {
                if (radioChannel.connectToSDR("127.0.0.1", 1234)) {
                    radioChannel.setSignalCallback(signal -> {
                        System.out.println(">> [RADIO RX] " + signal);
                        memoryCore.storeData("RECEIVED_RADIO:" + signal, 0.6,
                            new String[]{"received", "radio"});
                    });
                    radioChannel.startListening();
                    System.out.println(">> [RADIO] SDR connected and listening...");
                } else {
                    System.out.println(">> [RADIO] SDR not available (run rtl_tcp first)");
                }
            }, "OmniMonitor-Radio").start();
        }
        
        System.out.println(">> OMNI-MONITOR ACTIVE. Listening for signals...");
    }

    /**
     * Stop monitoring
     */
    public void stopMonitoring() {
        isMonitoring.set(false);
        
        audioChannel.stopListening();
        radioChannel.stopListening();
        
        System.out.println(">> OMNI-MONITOR STOPPED.");
    }

    // ═══════════════════════════════════════════════════════════════════
    // CHANNEL CONTROL
    // ═══════════════════════════════════════════════════════════════════
    
    public void enableChannel(String channel, boolean enable) {
        switch (channel.toLowerCase()) {
            case "audio":
            case "sonic":
                enableAudio = enable;
                break;
            case "visual":
            case "optical":
                enableVisual = enable;
                break;
            case "radio":
            case "cosmic":
                enableRadio = enable;
                break;
            case "thermal":
            case "fan":
                enableThermal = enable;
                break;
            case "all":
                enableAudio = enableVisual = enableRadio = enableThermal = enable;
                break;
        }
        System.out.println(">> Channel " + channel + ": " + (enable ? "ENABLED" : "DISABLED"));
    }

    // ═══════════════════════════════════════════════════════════════════
    // ACCESSORS
    // ═══════════════════════════════════════════════════════════════════
    
    public SonicBridge getAudioChannel() { return audioChannel; }
    public OpticalBreach getVisualChannel() { return visualChannel; }
    public CosmicListener getRadioChannel() { return radioChannel; }
    public FanConductor getThermalChannel() { return thermalChannel; }
    public CentripetalMem getMemoryCore() { return memoryCore; }
    
    public BufferedImage getLastBeacon() { return lastBeacon; }
    public String getLastMessage() { return lastMessage; }
    
    public boolean isBroadcasting() { return isBroadcasting.get(); }
    public boolean isMonitoring() { return isMonitoring.get(); }

    /**
     * Get full status report
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════\n");
        sb.append("         OMNI-CASTER STATUS REPORT         \n");
        sb.append("═══════════════════════════════════════════\n");
        sb.append("\n");
        
        sb.append("CHANNELS:\n");
        sb.append(String.format("  AUDIO (Sonic):    %s%n", 
            enableAudio ? (audioChannel.isListening() ? "LISTENING" : "ENABLED") : "DISABLED"));
        sb.append(String.format("  VISUAL (Optical): %s%n", 
            enableVisual ? "ENABLED" : "DISABLED"));
        sb.append(String.format("  RADIO (Cosmic):   %s%n", 
            enableRadio ? (radioChannel.isConnected() ? "CONNECTED" : "ENABLED") : "DISABLED"));
        sb.append(String.format("  THERMAL (Fan):    %s%n", 
            enableThermal ? (thermalChannel.isBroadcasting() ? "BROADCASTING" : "ENABLED") : "DISABLED"));
        sb.append("\n");
        
        sb.append("STATE:\n");
        sb.append(String.format("  Broadcasting: %s%n", isBroadcasting.get()));
        sb.append(String.format("  Monitoring:   %s%n", isMonitoring.get()));
        sb.append(String.format("  Last Message: %s%n", lastMessage != null ? lastMessage : "(none)"));
        sb.append(String.format("  Beacon Ready: %s%n", lastBeacon != null));
        sb.append("\n");
        
        sb.append("MEMORY CORE:\n");
        sb.append(String.format("  Nodes: %d%n", memoryCore.size()));
        sb.append(String.format("  Total Importance: %.2f%n", memoryCore.getTotalImportance()));
        sb.append(String.format("  Core Radius: %.2f%n", memoryCore.getCoreRadius()));
        
        return sb.toString();
    }

    /**
     * Shutdown all channels
     */
    public void shutdown() {
        System.out.println(">> OMNI-CASTER SHUTTING DOWN...");
        
        stopMonitoring();
        thermalChannel.stopBroadcast();
        radioChannel.disconnect();
        
        isBroadcasting.set(false);
        isMonitoring.set(false);
        
        System.out.println(">> OMNI-CASTER OFFLINE.");
    }
}
