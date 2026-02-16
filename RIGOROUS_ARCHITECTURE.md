# Fraynix Rigorous Architecture

## Foundation Classes (Complete)

All engines now share a common foundation for reproducibility and scientific rigor:

### Core Classes (`fraymus.run` package)

1. **`Vec3`** - 3D vector for positions (immutable)
2. **`Entity`** - Standardized particle/entity representation
3. **`FusionEvent`** - Structured fusion event logging
4. **`RunConfig`** - Immutable configuration (seed, steps, parameters)
5. **`RunContext`** - Runtime context with reproducible RNG
6. **`EventLogger`** - JSONL + console logging
7. **`Engine`** - Interface for all optimization engines
8. **`EngineResult`** - Standardized results for comparison
9. **`Baseline`** - Interface for baseline methods
10. **`EngineBudget`** - Fair comparison constraints

## Key Principles

### 1. Reproducibility (Non-Negotiable)

**NO `new Random()` ANYWHERE**
**NO `Math.random()` ANYWHERE**

All randomness comes from `ctx.rng` (SplittableRandom with seed).

```java
// ❌ WRONG
double r = Math.random();
Random rng = new Random();

// ✅ CORRECT
double r = ctx.nextDouble();
int i = ctx.nextInt(100);
```

### 2. Structured Logging

Every run produces:
- `run_summary.json` - Configuration + results
- `events.jsonl` - All fusion events (machine-readable)
- `metrics.csv` - Per-step metrics for plotting

### 3. Honest Metrics

All metrics are labeled as **proxy scores** until validated:

```java
// ❌ Misleading
result.metric("binding_affinity", 1.54);
result.metric("tumor_reduction", 100.0);

// ✅ Honest
result.metric("proxy_affinity_score", 1.54);
result.metric("proxy_tumor_reduction", 100.0);
result.metric("fitness_score", 0.87);
```

### 4. Baseline Comparisons

Every engine runs against 3 baselines:
- **RandomSearch** - Random sampling
- **GreedyBaseline** - Hill climbing
- **GeneticBaseline** - Simple GA

Same budget, same seed → fair comparison.

## Example Engine Structure

```java
public class MyEngine implements Engine {
    @Override
    public String name() {
        return "MyEngine";
    }
    
    @Override
    public EngineResult run(RunContext ctx) throws Exception {
        long startTime = System.currentTimeMillis();
        
        // Header
        Map<String, Object> meta = new HashMap<>();
        meta.put("engine", name());
        meta.put("description", "My optimization engine");
        ctx.log.header(meta);
        
        // Initialize entities
        List<Entity> entities = initializeEntities(ctx);
        
        // Main loop
        int fusionCount = 0;
        for (int step = 0; step < ctx.cfg.steps; step++) {
            // Update physics
            updatePhysics(entities, ctx);
            
            // Check for fusions
            fusionCount += checkFusions(entities, ctx, step);
            
            // Log metrics
            ctx.log.metric("entity_count", entities.size(), step);
            ctx.log.metric("fusion_count", fusionCount, step);
        }
        
        // Calculate fitness
        double fitness = calculateFitness(entities);
        
        // Footer
        Map<String, Object> results = new HashMap<>();
        results.put("final_entity_count", entities.size());
        results.put("total_fusions", fusionCount);
        results.put("fitness_score", fitness);
        ctx.log.footer(results);
        
        // Return result
        return EngineResult.builder(name())
            .seed(ctx.cfg.seed)
            .fitnessScore(fitness)
            .iterations(ctx.cfg.steps)
            .runtimeMs(System.currentTimeMillis() - startTime)
            .noveltyCount(fusionCount)
            .metric("final_entity_count", entities.size())
            .build();
    }
    
    private List<Entity> initializeEntities(RunContext ctx) {
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < ctx.cfg.populationSize; i++) {
            double x = ctx.nextDouble(0, 100);
            double y = ctx.nextDouble(0, 100);
            double z = ctx.nextDouble(0, 100);
            entities.add(new Entity("E_" + i, "PARTICLE", x, y, z, 90));
        }
        return entities;
    }
    
    private int checkFusions(List<Entity> entities, RunContext ctx, int step) throws Exception {
        int count = 0;
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Entity a = entities.get(i);
                Entity b = entities.get(j);
                
                if (a.distanceTo(b) < ctx.cfg.fusionDistance && 
                    a.energy > ctx.cfg.energyThreshold && 
                    b.energy > ctx.cfg.energyThreshold) {
                    
                    // Log fusion event
                    FusionEvent fusion = new FusionEvent.Builder()
                        .step(step)
                        .parentA(a.id)
                        .parentB(b.id)
                        .action("COMBINE")
                        .kindA(a.kind)
                        .kindB(b.kind)
                        .distance(a.distanceTo(b))
                        .energyA(a.energy)
                        .energyB(b.energy)
                        .build();
                    
                    ctx.log.fusionEvent(fusion);
                    count++;
                }
            }
        }
        return count;
    }
}
```

## Running an Engine

```java
public static void main(String[] args) throws Exception {
    // Configuration
    RunConfig cfg = RunConfig.builder()
        .seed(1337)
        .steps(30)
        .populationSize(50)
        .fusionDistance(5.0)
        .energyThreshold(80.0)
        .outDir("build/runs")
        .prettyConsole(true)
        .jsonl(true)
        .build();
    
    // Create logger
    try (EventLogger log = new EventLogger(cfg, "MyEngine")) {
        // Create context
        RunContext ctx = new RunContext(cfg, log);
        
        // Run engine
        Engine engine = new MyEngine();
        EngineResult result = engine.run(ctx);
        
        // Print result
        System.out.println("Fitness: " + result.fitnessScore);
        System.out.println("Runtime: " + result.runtimeMs + "ms");
    }
}
```

## Output Structure

```
build/runs/
  MyEngine/
    1337/
      run_summary.json
      events.jsonl
      metrics.csv
```

### run_summary.json
```json
{
  "config": {
    "seed": 1337,
    "steps": 30,
    "population_size": 50,
    "gravity_constant": 1.618,
    "fusion_distance": 5.0,
    "energy_threshold": 80.0
  },
  "meta": {
    "engine": "MyEngine",
    "description": "My optimization engine"
  },
  "start_time": 1707825600000,
  "end_time": 1707825601234,
  "results": {
    "final_entity_count": 50,
    "total_fusions": 127,
    "fitness_score": 0.87
  },
  "event_count": 127
}
```

### events.jsonl
```json
{"type":"fusion","timestamp":1707825600100,"step":5,"parent_a":"E_12","parent_b":"E_34","action":"COMBINE","kind_a":"PARTICLE","kind_b":"PARTICLE","distance":4.2,"energy_a":92.0,"energy_b":88.0}
{"type":"fusion","timestamp":1707825600150,"step":7,"parent_a":"E_5","parent_b":"E_19","action":"RELATE","kind_a":"PARTICLE","kind_b":"PARTICLE","distance":3.8,"energy_a":95.0,"energy_b":91.0}
```

### metrics.csv
```csv
step,name,value
0,entity_count,50.000000
0,fusion_count,0.000000
1,entity_count,50.000000
1,fusion_count,3.000000
```

## Baseline Implementation

Each baseline follows the same interface:

```java
public class RandomSearchBaseline implements Baseline {
    @Override
    public String name() {
        return "RandomSearch";
    }
    
    @Override
    public EngineResult run(RunContext ctx, EngineBudget budget) throws Exception {
        // Random sampling within budget
        double bestFitness = Double.NEGATIVE_INFINITY;
        
        for (int i = 0; i < budget.evaluations; i++) {
            // Generate random solution
            double fitness = evaluateRandom(ctx);
            if (fitness > bestFitness) {
                bestFitness = fitness;
            }
        }
        
        return EngineResult.builder(name())
            .seed(ctx.cfg.seed)
            .fitnessScore(bestFitness)
            .iterations(budget.evaluations)
            .build();
    }
}
```

## Benchmark Runner

```java
public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        long[] seeds = {1, 2, 3, 4, 5, 1337, 20260213};
        
        for (long seed : seeds) {
            RunConfig cfg = RunConfig.builder()
                .seed(seed)
                .steps(30)
                .populationSize(50)
                .build();
            
            EngineBudget budget = EngineBudget.fromConfig(cfg);
            
            // Run physics engine
            try (EventLogger log = new EventLogger(cfg, "MyEngine")) {
                RunContext ctx = new RunContext(cfg, log);
                Engine engine = new MyEngine();
                EngineResult physicsResult = engine.run(ctx);
                
                // Run baselines
                EngineResult randomResult = new RandomSearchBaseline().run(ctx, budget);
                EngineResult greedyResult = new GreedyBaseline().run(ctx, budget);
                EngineResult gaResult = new GeneticBaseline().run(ctx, budget);
                
                // Compare
                System.out.println("Seed: " + seed);
                System.out.println("Physics: " + physicsResult.fitnessScore);
                System.out.println("Random: " + randomResult.fitnessScore);
                System.out.println("Greedy: " + greedyResult.fitnessScore);
                System.out.println("GA: " + gaResult.fitnessScore);
                System.out.println();
            }
        }
    }
}
```

## Migration Guide

### Old Code (Ad-hoc)
```java
Random rng = new Random();
PhiSuit<String> particle = new PhiSuit<>("data", 50, 50, 50);
System.out.println("Binding Affinity: " + affinity);
```

### New Code (Rigorous)
```java
// Use context RNG
double x = ctx.nextDouble(0, 100);

// Use Entity
Entity particle = new Entity("P_1", "DRUG", 50, 50, 50, 90);

// Honest metrics
result.metric("proxy_affinity_score", affinity);
```

## Benefits

1. **Reproducibility** - Same seed → same results
2. **Comparability** - Baselines prove your method works
3. **Analyzability** - JSONL enables plotting and statistics
4. **Credibility** - Honest metrics prevent dismissal
5. **Debuggability** - Structured logs show what happened
6. **Extensibility** - Easy to add new engines/baselines

## Next Steps

1. Implement baselines (RandomSearch, Greedy, GA)
2. Refactor existing engines to use new architecture
3. Add CLI args support
4. Create benchmark runner Gradle task
5. Generate comparison plots from metrics.csv

## Key Takeaway

**This architecture transforms Fraynix from "impressive demo" to "defensible research tool".**

Every claim is backed by:
- Reproducible runs (seed)
- Structured evidence (JSONL)
- Fair comparisons (baselines)
- Honest metrics (proxy labels)
