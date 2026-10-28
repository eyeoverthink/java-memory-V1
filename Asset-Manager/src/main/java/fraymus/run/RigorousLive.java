package fraymus.run;

import fraymus.baselines.GeneticBaseline;
import fraymus.baselines.GreedyBaseline;
import fraymus.baselines.RandomSearchBaseline;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RigorousLive {
    public static void main(String[] args) {
        runSession(new Scanner(System.in));
    }

    public static void runSession(Scanner scanner) {
        long seed = System.currentTimeMillis();
        int steps = 20;
        int population = 30;
        double fusionDistance = 5.0;
        double energyThreshold = 80.0;
        String outputDir = "build/runs/live";
        boolean prettyConsole = true;
        boolean jsonl = true;

        printHelp();

        while (true) {
            System.out.print("rigorous> ");
            if (!scanner.hasNextLine()) {
                return;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("stop")) {
                break;
            }
            if (line.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }
            if (line.equalsIgnoreCase("show")) {
                printConfig(seed, steps, population, fusionDistance, energyThreshold, outputDir, jsonl);
                continue;
            }
            if (line.equalsIgnoreCase("stats")) {
                printConfig(seed, steps, population, fusionDistance, energyThreshold, outputDir, jsonl);
                continue;
            }
            if (line.startsWith("seed ")) {
                Long value = parseLong(line.substring(5).trim(), "seed");
                if (value != null) {
                    seed = value;
                    System.out.println("   seed=" + seed);
                }
                continue;
            }
            if (line.startsWith("steps ")) {
                Integer value = parseInt(line.substring(6).trim(), "steps");
                if (value != null && value > 0) {
                    steps = value;
                    System.out.println("   steps=" + steps);
                }
                continue;
            }
            if (line.startsWith("population ")) {
                Integer value = parseInt(line.substring(11).trim(), "population");
                if (value != null && value > 0) {
                    population = value;
                    System.out.println("   population=" + population);
                }
                continue;
            }
            if (line.startsWith("fusion ")) {
                Double value = parseDouble(line.substring(7).trim(), "fusionDistance");
                if (value != null && value > 0) {
                    fusionDistance = value;
                    System.out.println("   fusionDistance=" + fusionDistance);
                }
                continue;
            }
            if (line.startsWith("energy ")) {
                Double value = parseDouble(line.substring(7).trim(), "energyThreshold");
                if (value != null && value > 0) {
                    energyThreshold = value;
                    System.out.println("   energyThreshold=" + energyThreshold);
                }
                continue;
            }
            if (line.startsWith("out ")) {
                outputDir = line.substring(4).trim();
                if (outputDir.isEmpty()) {
                    outputDir = "build/runs/live";
                }
                System.out.println("   outDir=" + outputDir);
                continue;
            }
            if (line.equalsIgnoreCase("jsonl on")) {
                jsonl = true;
                System.out.println("   jsonl=ON");
                continue;
            }
            if (line.equalsIgnoreCase("jsonl off")) {
                jsonl = false;
                System.out.println("   jsonl=OFF");
                continue;
            }
            if (line.equalsIgnoreCase("run")) {
                runOnce(seed, steps, population, fusionDistance, energyThreshold, outputDir, prettyConsole, jsonl);
                continue;
            }
            if (line.equalsIgnoreCase("demo")) {
                try {
                    fraymus.demo.RigorousDemo.main(new String[]{});
                } catch (Exception e) {
                    System.out.println("   [!] Demo failed: " + e.getMessage());
                }
                continue;
            }

            System.out.println("   Unknown command. Type 'help' for options.");
        }
    }

    private static void runOnce(long seed, int steps, int population, double fusionDistance, double energyThreshold,
                                String outputDir, boolean prettyConsole, boolean jsonl) {
        RunConfig cfg = RunConfig.builder()
            .seed(seed)
            .steps(steps)
            .populationSize(population)
            .fusionDistance(fusionDistance)
            .energyThreshold(energyThreshold)
            .outDir(outputDir)
            .prettyConsole(prettyConsole)
            .jsonl(jsonl)
            .build();

        EngineBudget budget = EngineBudget.fromConfig(cfg);

        try (EventLogger log = new EventLogger(cfg, "SimplePhysics")) {
            RunContext ctx = new RunContext(cfg, log);

            Map<String, Object> meta = new HashMap<>();
            meta.put("engine", "SimplePhysicsEngine");
            meta.put("description", "Fraynix physics-based optimization run");
            ctx.log.header(meta);

            EngineResult physicsResult = runSimplePhysics(ctx);

            Map<String, Object> results = new HashMap<>();
            results.put("fitness_score", physicsResult.fitnessScore);
            results.put("novelty_count", physicsResult.noveltyCount);
            ctx.log.footer(results);

            System.out.println();
            System.out.println("Physics Engine Result:");
            System.out.printf("  Fitness: %.4f%n", physicsResult.fitnessScore);
            System.out.printf("  Novelty: %d%n", physicsResult.noveltyCount);
            System.out.printf("  Runtime: %d ms%n", physicsResult.runtimeMs);
            System.out.println();

            System.out.println("Running baselines for comparison...");
            System.out.println();

            EngineResult randomResult = new RandomSearchBaseline().run(ctx, budget);
            EngineResult greedyResult = new GreedyBaseline().run(ctx, budget);
            EngineResult gaResult = new GeneticBaseline().run(ctx, budget);

            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║                    COMPARISON                                 ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.printf("%-20s %10s %10s %10s%n", "Method", "Fitness", "Novelty", "Time(ms)");
            System.out.println("─────────────────────────────────────────────────────────────");
            System.out.printf("%-20s %10.4f %10d %10d%n", "Physics (Fraynix)",
                physicsResult.fitnessScore, physicsResult.noveltyCount, physicsResult.runtimeMs);
            System.out.printf("%-20s %10.4f %10d %10d%n", "Random Search",
                randomResult.fitnessScore, randomResult.noveltyCount, randomResult.runtimeMs);
            System.out.printf("%-20s %10.4f %10d %10d%n", "Greedy HillClimb",
                greedyResult.fitnessScore, greedyResult.noveltyCount, greedyResult.runtimeMs);
            System.out.printf("%-20s %10.4f %10d %10d%n", "Genetic Algorithm",
                gaResult.fitnessScore, gaResult.noveltyCount, gaResult.runtimeMs);
            System.out.println();
            System.out.println(ctx.getRNGStats());
            System.out.println();
        } catch (Exception e) {
            System.out.println("   [!] Rigorous run failed: " + e.getMessage());
        }
    }

    private static EngineResult runSimplePhysics(RunContext ctx) throws Exception {
        long startTime = System.currentTimeMillis();

        java.util.List<Entity> entities = new java.util.ArrayList<>();
        for (int i = 0; i < ctx.cfg.populationSize; i++) {
            double x = ctx.nextDouble(0, 100);
            double y = ctx.nextDouble(0, 100);
            double z = ctx.nextDouble(0, 100);
            entities.add(new Entity("E_" + i, "PARTICLE", x, y, z, 90));
        }

        int fusionCount = 0;

        for (int step = 0; step < ctx.cfg.steps; step++) {
            for (int i = 0; i < entities.size(); i++) {
                for (int j = i + 1; j < entities.size(); j++) {
                    Entity a = entities.get(i);
                    Entity b = entities.get(j);

                    double dist = a.distanceTo(b);
                    if (dist > 0 && dist < 50) {
                        a.moveTowards(b, ctx.cfg.gravityConstant / dist * 0.01);
                    }
                }
            }

            for (int i = 0; i < entities.size(); i++) {
                for (int j = i + 1; j < entities.size(); j++) {
                    Entity a = entities.get(i);
                    Entity b = entities.get(j);

                    if (a.distanceTo(b) < ctx.cfg.fusionDistance &&
                        a.energy > ctx.cfg.energyThreshold &&
                        b.energy > ctx.cfg.energyThreshold) {

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
                        fusionCount++;

                        a.addEnergy(-10);
                        b.addEnergy(-10);
                    }
                }
            }

            for (Entity e : entities) {
                e.addEnergy(5);
            }

            ctx.log.metric("entity_count", entities.size(), step);
            ctx.log.metric("fusion_count", fusionCount, step);
        }

        double fitness = fusionCount / (double) ctx.cfg.steps;
        long elapsed = System.currentTimeMillis() - startTime;

        return EngineResult.builder("SimplePhysics")
            .seed(ctx.cfg.seed)
            .fitnessScore(fitness)
            .iterations(ctx.cfg.steps)
            .runtimeMs(elapsed)
            .noveltyCount(fusionCount)
            .metric("total_fusions", fusionCount)
            .build();
    }

    private static void printConfig(long seed, int steps, int population, double fusionDistance,
                                    double energyThreshold, String outDir, boolean jsonl) {
        System.out.println("Rigorous config:");
        System.out.println("  seed=" + seed);
        System.out.println("  steps=" + steps);
        System.out.println("  population=" + population);
        System.out.println("  fusionDistance=" + fusionDistance);
        System.out.println("  energyThreshold=" + energyThreshold);
        System.out.println("  outDir=" + outDir);
        System.out.println("  jsonl=" + (jsonl ? "ON" : "OFF"));
    }

    private static Long parseLong(String value, String label) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.out.println("   [!] Invalid " + label + ": " + value);
            return null;
        }
    }

    private static Integer parseInt(String value, String label) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("   [!] Invalid " + label + ": " + value);
            return null;
        }
    }

    private static Double parseDouble(String value, String label) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println("   [!] Invalid " + label + ": " + value);
            return null;
        }
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("RIGOROUS LIVE COMMANDS:");
        System.out.println("  run                 - Execute a rigorous run");
        System.out.println("  show|stats           - Show current configuration");
        System.out.println("  seed <n>             - Set RNG seed");
        System.out.println("  steps <n>            - Set steps per run");
        System.out.println("  population <n>       - Set population size");
        System.out.println("  fusion <d>           - Set fusion distance");
        System.out.println("  energy <t>           - Set energy threshold");
        System.out.println("  out <path>           - Set output directory");
        System.out.println("  jsonl on|off          - Toggle JSONL logging");
        System.out.println("  demo                - Optional demo script");
        System.out.println("  stop|exit             - Return to Fraynix");
        System.out.println();
    }
}
