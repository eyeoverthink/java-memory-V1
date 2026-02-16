package fraymus.engines;

import fraymus.run.*;
import java.util.*;

/**
 * RIGOROUS CANCER RESEARCH ENGINE
 * 
 * Physics-based cancer treatment simulation with:
 * - Reproducible results (EvolutionaryChaos RNG)
 * - Structured logging (JSONL events)
 * - Honest metrics (proxy scores)
 * - Baseline comparisons
 * 
 * Simulates:
 * - Cancer cells (high energy, aggressive)
 * - Healthy cells (moderate energy)
 * - Immune cells (active defenders)
 * - Drug particles (treatment agents)
 * 
 * Treatment effects:
 * - Drugs target cancer cells via distance-based attraction
 * - Immune cells attack cancer when nearby
 * - Fusion events = successful drug-target interactions
 */
public class RigorousCancerEngine implements Engine {
    
    @Override
    public String name() {
        return "CancerResearch";
    }
    
    @Override
    public EngineResult run(RunContext ctx) throws Exception {
        long startTime = System.currentTimeMillis();
        
        // Header
        Map<String, Object> meta = new HashMap<>();
        meta.put("engine", name());
        meta.put("description", "Physics-based cancer treatment simulation");
        meta.put("approach", "Drug particles + immune cells vs cancer cells");
        ctx.log.header(meta);
        
        // Initialize simulation
        List<Entity> cancer = createCancerCells(ctx, 20);
        List<Entity> healthy = createHealthyCells(ctx, 30);
        List<Entity> immune = createImmuneCells(ctx, 10);
        List<Entity> drugs = createDrugs(ctx, 3);
        
        List<Entity> allEntities = new ArrayList<>();
        allEntities.addAll(cancer);
        allEntities.addAll(healthy);
        allEntities.addAll(immune);
        allEntities.addAll(drugs);
        
        int initialCancerCount = cancer.size();
        int fusionCount = 0;
        
        // Simulation loop
        for (int step = 0; step < ctx.cfg.steps; step++) {
            // Drug targeting
            for (Entity drug : drugs) {
                for (Entity cancerCell : cancer) {
                    double dist = drug.distanceTo(cancerCell);
                    if (dist > 5 && dist < 50) {
                        drug.moveTowards(cancerCell, 0.3);
                    }
                    
                    // Drug effect: reduce cancer energy
                    if (dist < 8) {
                        cancerCell.addEnergy(-10);
                    }
                }
            }
            
            // Immune response
            for (Entity immuneCell : immune) {
                for (Entity cancerCell : cancer) {
                    double dist = immuneCell.distanceTo(cancerCell);
                    if (dist < 12) {
                        immuneCell.moveTowards(cancerCell, 0.2);
                        if (dist < 6) {
                            cancerCell.addEnergy(-3);
                        }
                    }
                }
            }
            
            // Check for drug-cancer fusions
            for (Entity drug : drugs) {
                for (Entity cancerCell : cancer) {
                    if (drug.distanceTo(cancerCell) < ctx.cfg.fusionDistance &&
                        drug.energy > ctx.cfg.energyThreshold &&
                        cancerCell.energy > 50) {
                        
                        FusionEvent fusion = new FusionEvent.Builder()
                            .step(step)
                            .parentA(drug.id)
                            .parentB(cancerCell.id)
                            .action("COMBINE")
                            .kindA(drug.kind)
                            .kindB(cancerCell.kind)
                            .distance(drug.distanceTo(cancerCell))
                            .energyA(drug.energy)
                            .energyB(cancerCell.energy)
                            .note("Drug-target interaction")
                            .build();
                        
                        ctx.log.fusionEvent(fusion);
                        fusionCount++;
                        
                        cancerCell.addEnergy(-15);
                    }
                }
            }
            
            // Heat particles
            for (Entity e : allEntities) {
                if (!e.kind.equals("CANCER")) {
                    e.addEnergy(10);
                } else {
                    e.addEnergy(2); // Cancer grows slower under treatment
                }
            }
            
            // Count living cells
            int aliveCancer = 0;
            int aliveHealthy = 0;
            for (Entity c : cancer) {
                if (c.energy > 50) aliveCancer++;
            }
            for (Entity h : healthy) {
                if (h.energy > 60) aliveHealthy++;
            }
            
            // Log metrics
            ctx.log.metric("proxy_cancer_count", aliveCancer, step);
            ctx.log.metric("proxy_healthy_count", aliveHealthy, step);
            ctx.log.metric("proxy_fusion_count", fusionCount, step);
        }
        
        // Final counts
        int finalCancerCount = 0;
        int finalHealthyCount = 0;
        for (Entity c : cancer) {
            if (c.energy > 50) finalCancerCount++;
        }
        for (Entity h : healthy) {
            if (h.energy > 60) finalHealthyCount++;
        }
        
        double reductionPercent = ((initialCancerCount - finalCancerCount) / (double) initialCancerCount) * 100;
        double healthyPreserved = (finalHealthyCount / 30.0) * 100;
        
        // Fitness: maximize cancer reduction, minimize healthy damage
        double fitness = (reductionPercent / 100.0) * 0.7 + (healthyPreserved / 100.0) * 0.3;
        
        // Footer
        Map<String, Object> results = new HashMap<>();
        results.put("initial_cancer_cells", initialCancerCount);
        results.put("final_cancer_cells", finalCancerCount);
        results.put("proxy_tumor_reduction_percent", reductionPercent);
        results.put("healthy_cells_preserved", finalHealthyCount);
        results.put("proxy_healthy_preserved_percent", healthyPreserved);
        results.put("drug_target_fusions", fusionCount);
        results.put("fitness_score", fitness);
        ctx.log.footer(results);
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        return EngineResult.builder(name())
            .seed(ctx.cfg.seed)
            .fitnessScore(fitness)
            .iterations(ctx.cfg.steps)
            .runtimeMs(elapsed)
            .noveltyCount(fusionCount)
            .metric("proxy_tumor_reduction_percent", reductionPercent)
            .metric("proxy_healthy_preserved_percent", healthyPreserved)
            .metric("drug_target_fusions", fusionCount)
            .build();
    }
    
    private List<Entity> createCancerCells(RunContext ctx, int count) {
        List<Entity> cells = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = (i * 1.618 * 2 * Math.PI) % (2 * Math.PI);
            double x = 50 + 15 * Math.cos(angle);
            double y = 50 + 15 * Math.sin(angle);
            double z = 50 + 10 * Math.cos(angle * 1.618);
            Entity cell = new Entity("CANCER_" + i, "CANCER", x, y, z, 98);
            cell.payload.put("growth_rate", 0.8);
            cells.add(cell);
        }
        return cells;
    }
    
    private List<Entity> createHealthyCells(RunContext ctx, int count) {
        List<Entity> cells = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = (i * 1.618 * 2 * Math.PI) % (2 * Math.PI);
            double x = 50 + 35 * Math.cos(angle);
            double y = 50 + 35 * Math.sin(angle);
            double z = 50 + 25 * Math.cos(angle * 1.618);
            Entity cell = new Entity("HEALTHY_" + i, "HEALTHY", x, y, z, 75);
            cells.add(cell);
        }
        return cells;
    }
    
    private List<Entity> createImmuneCells(RunContext ctx, int count) {
        List<Entity> cells = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double x = ctx.nextDouble(30, 70);
            double y = ctx.nextDouble(30, 70);
            double z = ctx.nextDouble(30, 70);
            Entity cell = new Entity("IMMUNE_" + i, "IMMUNE", x, y, z, 85);
            cells.add(cell);
        }
        return cells;
    }
    
    private List<Entity> createDrugs(RunContext ctx, int count) {
        List<Entity> drugs = new ArrayList<>();
        String[] types = {"TARGETED_THERAPY", "IMMUNOTHERAPY", "CHEMOTHERAPY"};
        double[][] positions = {{50, 50, 70}, {50, 70, 50}, {70, 50, 50}};
        
        for (int i = 0; i < count; i++) {
            Entity drug = new Entity("DRUG_" + i, types[i], 
                positions[i][0], positions[i][1], positions[i][2], 90);
            drug.payload.put("potency", 0.7);
            drugs.add(drug);
        }
        return drugs;
    }
}
