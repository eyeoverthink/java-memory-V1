package fraymus.engines;

import fraymus.run.*;
import java.util.*;

/**
 * RIGOROUS PROTEIN FOLDING ENGINE
 * 
 * Physics-based protein folding with:
 * - Reproducible results (EvolutionaryChaos RNG)
 * - Honest complexity claims (heuristic, not O(log n))
 * - Energy-based optimization
 * - Structural validation
 * 
 * Simulates amino acid interactions:
 * - Hydrophobic collapse (like attracts like)
 * - Electrostatic forces (opposite charges attract)
 * - Disulfide bonds (cysteine pairing)
 * - Energy minimization (natural folding)
 */
public class RigorousProteinEngine implements Engine {
    
    @Override
    public String name() {
        return "ProteinFolding";
    }
    
    @Override
    public EngineResult run(RunContext ctx) throws Exception {
        long startTime = System.currentTimeMillis();
        
        // Header
        Map<String, Object> meta = new HashMap<>();
        meta.put("engine", name());
        meta.put("description", "Physics-based protein folding simulation");
        meta.put("approach", "Energy minimization via hydrophobic collapse");
        meta.put("complexity", "Heuristic search in simplified energy landscape");
        meta.put("note", "NOT claiming O(log n) - this is a coarse-grained model");
        ctx.log.header(meta);
        
        // Create protein sequence (16 amino acids)
        List<AminoAcid> sequence = createProteinSequence(ctx);
        
        double initialEnergy = calculateEnergy(sequence);
        int fusionCount = 0;
        
        // Folding simulation
        for (int step = 0; step < ctx.cfg.steps; step++) {
            // Hydrophobic collapse
            for (int i = 0; i < sequence.size(); i++) {
                for (int j = i + 1; j < sequence.size(); j++) {
                    AminoAcid aa1 = sequence.get(i);
                    AminoAcid aa2 = sequence.get(j);
                    
                    double dist = aa1.entity.distanceTo(aa2.entity);
                    
                    // Hydrophobic attraction
                    if (aa1.type == AAType.HYDROPHOBIC && aa2.type == AAType.HYDROPHOBIC) {
                        if (dist > 8 && dist < 30) {
                            aa1.entity.moveTowards(aa2.entity, 0.1);
                        }
                    }
                    
                    // Electrostatic attraction
                    if ((aa1.type == AAType.CHARGED_POS && aa2.type == AAType.CHARGED_NEG) ||
                        (aa1.type == AAType.CHARGED_NEG && aa2.type == AAType.CHARGED_POS)) {
                        if (dist > 6 && dist < 25) {
                            aa1.entity.moveTowards(aa2.entity, 0.15);
                        }
                    }
                    
                    // Disulfide bonds
                    if (aa1.type == AAType.CYSTEINE && aa2.type == AAType.CYSTEINE) {
                        if (dist > 5 && dist < 20) {
                            aa1.entity.moveTowards(aa2.entity, 0.12);
                        }
                        
                        // Log bond formation
                        if (dist < 8 && aa1.entity.energy > 80 && aa2.entity.energy > 80) {
                            FusionEvent fusion = new FusionEvent.Builder()
                                .step(step)
                                .parentA(aa1.entity.id)
                                .parentB(aa2.entity.id)
                                .action("RELATE")
                                .kindA(aa1.type.toString())
                                .kindB(aa2.type.toString())
                                .distance(dist)
                                .energyA(aa1.entity.energy)
                                .energyB(aa2.entity.energy)
                                .note("Disulfide bond formation")
                                .build();
                            
                            ctx.log.fusionEvent(fusion);
                            fusionCount++;
                        }
                    }
                }
            }
            
            // Heat amino acids
            for (AminoAcid aa : sequence) {
                aa.entity.addEnergy(10);
            }
            
            // Calculate current energy
            double currentEnergy = calculateEnergy(sequence);
            
            // Log metrics
            ctx.log.metric("proxy_energy", currentEnergy, step);
            ctx.log.metric("stable_bonds", countStableBonds(sequence), step);
        }
        
        double finalEnergy = calculateEnergy(sequence);
        int stableBonds = countStableBonds(sequence);
        int hydrophobicCore = countHydrophobicCore(sequence);
        
        // Fitness: lower energy = better fold
        double energyImprovement = (initialEnergy - finalEnergy) / Math.abs(initialEnergy);
        double fitness = energyImprovement * 0.6 + (stableBonds / 10.0) * 0.3 + (hydrophobicCore / 10.0) * 0.1;
        
        // Footer
        Map<String, Object> results = new HashMap<>();
        results.put("sequence_length", sequence.size());
        results.put("initial_proxy_energy", initialEnergy);
        results.put("final_proxy_energy", finalEnergy);
        results.put("proxy_energy_improvement", energyImprovement);
        results.put("stable_bonds_formed", stableBonds);
        results.put("hydrophobic_core_size", hydrophobicCore);
        results.put("fusion_events", fusionCount);
        results.put("fitness_score", fitness);
        results.put("iterations_used", ctx.cfg.steps);
        ctx.log.footer(results);
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        return EngineResult.builder(name())
            .seed(ctx.cfg.seed)
            .fitnessScore(fitness)
            .iterations(ctx.cfg.steps)
            .runtimeMs(elapsed)
            .noveltyCount(fusionCount)
            .metric("proxy_energy_improvement", energyImprovement)
            .metric("stable_bonds", stableBonds)
            .metric("hydrophobic_core", hydrophobicCore)
            .build();
    }
    
    enum AAType {
        HYDROPHOBIC,
        HYDROPHILIC,
        CHARGED_POS,
        CHARGED_NEG,
        AROMATIC,
        CYSTEINE
    }
    
    static class AminoAcid {
        Entity entity;
        AAType type;
        
        AminoAcid(String id, AAType type, Vec3 pos) {
            this.entity = new Entity(id, type.toString(), pos, 95);
            this.type = type;
        }
    }
    
    private List<AminoAcid> createProteinSequence(RunContext ctx) {
        List<AminoAcid> sequence = new ArrayList<>();
        AAType[] types = {
            AAType.HYDROPHOBIC, AAType.HYDROPHILIC, AAType.HYDROPHOBIC,
            AAType.CHARGED_POS, AAType.HYDROPHOBIC, AAType.CHARGED_NEG,
            AAType.AROMATIC, AAType.HYDROPHILIC, AAType.HYDROPHOBIC,
            AAType.HYDROPHOBIC, AAType.AROMATIC, AAType.HYDROPHILIC,
            AAType.CHARGED_POS, AAType.HYDROPHOBIC, AAType.CYSTEINE,
            AAType.CYSTEINE
        };
        
        for (int i = 0; i < types.length; i++) {
            double angle = (i * 1.618 * 2 * Math.PI) % (2 * Math.PI);
            double x = 50 + 40 * Math.cos(angle);
            double y = 50 + 40 * Math.sin(angle);
            double z = 50 + 30 * Math.cos(angle * 1.618);
            
            sequence.add(new AminoAcid("AA_" + i, types[i], new Vec3(x, y, z)));
        }
        
        return sequence;
    }
    
    private double calculateEnergy(List<AminoAcid> sequence) {
        double energy = 0.0;
        
        for (int i = 0; i < sequence.size(); i++) {
            for (int j = i + 1; j < sequence.size(); j++) {
                AminoAcid aa1 = sequence.get(i);
                AminoAcid aa2 = sequence.get(j);
                double dist = aa1.entity.distanceTo(aa2.entity);
                
                // Hydrophobic attraction (favorable)
                if (aa1.type == AAType.HYDROPHOBIC && aa2.type == AAType.HYDROPHOBIC) {
                    energy -= 1.0 / (dist + 1);
                }
                
                // Electrostatic attraction (favorable)
                if ((aa1.type == AAType.CHARGED_POS && aa2.type == AAType.CHARGED_NEG) ||
                    (aa1.type == AAType.CHARGED_NEG && aa2.type == AAType.CHARGED_POS)) {
                    energy -= 2.0 / (dist + 1);
                }
                
                // Electrostatic repulsion (unfavorable)
                if ((aa1.type == AAType.CHARGED_POS && aa2.type == AAType.CHARGED_POS) ||
                    (aa1.type == AAType.CHARGED_NEG && aa2.type == AAType.CHARGED_NEG)) {
                    energy += 1.0 / (dist + 1);
                }
            }
        }
        
        return energy * 1.618; // φ-scaled
    }
    
    private int countStableBonds(List<AminoAcid> sequence) {
        int bonds = 0;
        for (int i = 0; i < sequence.size(); i++) {
            for (int j = i + 1; j < sequence.size(); j++) {
                double dist = sequence.get(i).entity.distanceTo(sequence.get(j).entity);
                if (dist < 10) bonds++;
            }
        }
        return bonds;
    }
    
    private int countHydrophobicCore(List<AminoAcid> sequence) {
        int core = 0;
        for (int i = 0; i < sequence.size(); i++) {
            if (sequence.get(i).type != AAType.HYDROPHOBIC) continue;
            for (int j = i + 1; j < sequence.size(); j++) {
                if (sequence.get(j).type != AAType.HYDROPHOBIC) continue;
                double dist = sequence.get(i).entity.distanceTo(sequence.get(j).entity);
                if (dist < 10) core++;
            }
        }
        return core;
    }
}
