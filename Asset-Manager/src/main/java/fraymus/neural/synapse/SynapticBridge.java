package fraymus.neural.synapse;

import com.fraymus.core.UnifiedBrainOrchestrator;
import com.fraymus.omega.*;

/**
 * Synaptic Bridge - Wires biological components to orchestrator
 * Author: Vaughn Scott
 */
public class SynapticBridge {
    
    public static void wire(UnifiedBrainOrchestrator orchestrator, 
                          PrefrontalCortex prefrontal,
                          MidbrainThalamus midbrain,
                          ParietalLobe parietal,
                          Hippocampus hippocampus,
                          SensoryCortex sensory,
                          MotorCortex motorCortex,
                          Amygdala amygdala) {
        
        orchestrator.setPrefrontal(prefrontal);
        orchestrator.setMidbrain(midbrain);
        orchestrator.setParietal(parietal);
        orchestrator.setHippocampus(hippocampus);
        orchestrator.setSensory(sensory);
        orchestrator.setMotorCortex(motorCortex);
        orchestrator.setAmygdala(amygdala);
        
        System.out.println("[SYNAPTIC BRIDGE] All cortical regions wired to orchestrator");
    }
}
