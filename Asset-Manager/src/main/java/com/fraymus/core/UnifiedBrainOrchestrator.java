package com.fraymus.core;

import com.fraymus.omega.*;

/**
 * Unified Brain Orchestrator - Central nervous system coordinator
 * Author: Vaughn Scott
 * 
 * @deprecated This orchestrator is deprecated. Use {@link fraymus.brain.unified.UnifiedBrainOrchestrator} instead.
 * Canonical composition now happens in the FraynixBoot runtime using the unified orchestrator.
 * This class exists only for backward compatibility.
 * 
 * Migration guide:
 * - Replace imports: com.fraymus.core.UnifiedBrainOrchestrator -> fraymus.brain.unified.UnifiedBrainOrchestrator
 * - Route boot-time wiring through FraynixBoot's unified composition root path
 * 
 */
@Deprecated
public class UnifiedBrainOrchestrator {
    private PrefrontalCortex prefrontal;
    private MidbrainThalamus midbrain;
    private ParietalLobe parietal;
    private Hippocampus hippocampus;
    private SensoryCortex sensory;
    private MotorCortex motorCortex;
    private Amygdala amygdala;
    
    public UnifiedBrainOrchestrator() {
        System.out.println("[ORCHESTRATOR][DEPRECATED] Quarantined legacy coordinator - migrate to fraymus.brain.unified.UnifiedBrainOrchestrator");
        System.out.println("[ORCHESTRATOR] Unified Brain Orchestrator initialized");
    }
    
    public void setPrefrontal(PrefrontalCortex prefrontal) {
        this.prefrontal = prefrontal;
    }
    
    public void setMidbrain(MidbrainThalamus midbrain) {
        this.midbrain = midbrain;
    }
    
    public void setParietal(ParietalLobe parietal) {
        this.parietal = parietal;
    }
    
    public void setHippocampus(Hippocampus hippocampus) {
        this.hippocampus = hippocampus;
    }
    
    public void setSensory(SensoryCortex sensory) {
        this.sensory = sensory;
    }
    
    public void setMotorCortex(MotorCortex motorCortex) {
        this.motorCortex = motorCortex;
    }
    
    public void setAmygdala(Amygdala amygdala) {
        this.amygdala = amygdala;
    }
    
    public PrefrontalCortex getPrefrontal() {
        return prefrontal;
    }
    
    public MidbrainThalamus getMidbrain() {
        return midbrain;
    }
    
    public ParietalLobe getParietal() {
        return parietal;
    }
    
    public Hippocampus getHippocampus() {
        return hippocampus;
    }
    
    public SensoryCortex getSensory() {
        return sensory;
    }
    
    public MotorCortex getMotorCortex() {
        return motorCortex;
    }
    
    public Amygdala getAmygdala() {
        return amygdala;
    }
    
    public void onThermalInjection(String source, double resonance) {
        System.out.println("[ORCHESTRATOR] Thermal injection from " + source + ": " + resonance);
    }
}

