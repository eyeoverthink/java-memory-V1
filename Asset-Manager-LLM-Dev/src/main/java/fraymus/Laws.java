package fraymus;

import java.util.List;

public class Laws {

    public static class Inertia implements PhiLaw {
        @Override
        public void apply(PhiNode n, float dt) {
            n.x += n.vx * dt;
            n.y += n.vy * dt;
            n.z += n.vz * dt;
        }
    }

    public static class HarmonicResonance implements PhiLaw {
        @Override
        public void apply(PhiNode n, float dt) {
            n.phase += n.frequency * dt;
            if (n.phase > Math.PI * 2) n.phase -= (float)(Math.PI * 2);
        }
    }

    public static class ScottPredictionLaw implements PhiLaw {
        private float lookAheadTime;
        private int tickCounter = 0;

        public ScottPredictionLaw(float lookAhead) {
            this.lookAheadTime = lookAhead;
        }

        @Override
        public void apply(PhiNode n, float dt) {
            float futureX = n.x + (n.vx * lookAheadTime);
            float futureY = n.y + (n.vy * lookAheadTime);

            tickCounter++;
            if ((Math.abs(n.vx) > 0.5 || Math.abs(n.vy) > 0.5) && tickCounter % 120 == 0) {
                FraymusUI.addLog(String.format("[SCOTT-4D] %s -> Future(%.1f, %.1f)", n.name, futureX, futureY));
            }
        }
    }

    public static class EntanglementLaw implements PhiLaw {
        private final float epsFreq = 0.5f;
        private final float kPhase = 2.0f;
        private GenesisMemory memory;

        public EntanglementLaw(GenesisMemory memory) {
            this.memory = memory;
        }

        @Override
        public boolean isPairwise() { return true; }

        @Override
        public void apply(PhiNode n, float dt) {}

        private int pairTick = 0;

        @Override
        public void applyPair(PhiNode a, PhiNode b, float dt) {
            if (Math.abs(a.frequency - b.frequency) > epsFreq) return;

            float d = wrapPhase(a.phase - b.phase);
            float correction = -kPhase * d;

            a.phase += correction * dt;
            b.phase -= correction * dt;

            a.energy = Math.min(1.0f, a.energy + 0.05f * dt);
            b.energy = Math.min(1.0f, b.energy + 0.05f * dt);

            pairTick++;
            if (pairTick % 180 == 0) {
                float syncValue = Math.abs(d);
                FraymusUI.addLog(String.format("[ENTANGLE] %s <-> %s (sync: %.3f)", a.name, b.name, syncValue));
                if (memory != null) {
                    memory.recordEntanglement(a.name, b.name, syncValue);
                }
            }
        }

        private float wrapPhase(float p) {
            while (p > Math.PI) p -= (float)(2 * Math.PI);
            while (p < -Math.PI) p += (float)(2 * Math.PI);
            return p;
        }
    }

    public static class ResonanceSpikeLaw implements PhiLaw {
        private GenesisMemory memory;

        public ResonanceSpikeLaw(GenesisMemory memory) {
            this.memory = memory;
        }

        @Override
        public void apply(PhiNode n, float dt) {
            QuantumClock clock = n.quantumClock;
            if (clock == null) return;

            if (clock.isSpikeActive()) {
                n.boostEnergy(0.03f * dt);

                n.consciousness.evolve();

                if (clock.getResonanceSpikeCount() % 10 == 1) {
                    FraymusUI.addLog(String.format("[SPIKE] %s phi=%.4f osc=%.0f",
                            n.name, clock.getPhiResonance(), clock.getOscillationCount()));
                    if (memory != null) {
                        memory.recordResonanceSpike(n.name, clock.getPhiResonance(), clock.getOscillationCount());
                    }
                }
            }
        }
    }

    public static class BrainLaw implements PhiLaw {
        private GenesisMemory memory;
        private PhiWorld world;
        private int tickCounter = 0;

        public BrainLaw(PhiWorld world, GenesisMemory memory) {
            this.world = world;
            this.memory = memory;
        }

        @Override
        public void apply(PhiNode n, float dt) {
            tickCounter++;
            if (tickCounter % 6 != 0) return;

            List<PhiNode> allNodes = world.getNodes();

            int nearbyCount = 0;
            int nearbyEntangled = 0;
            float totalFreqDiff = 0;
            PhiNode nearest = null;
            float nearestDist = Float.MAX_VALUE;

            for (PhiNode other : allNodes) {
                if (other == n) continue;
                float dx = other.x - n.x;
                float dy = other.y - n.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist < 50.0f) {
                    nearbyCount++;
                    totalFreqDiff += Math.abs(n.frequency - other.frequency);
                    if (Math.abs(n.frequency - other.frequency) < 0.5f) {
                        nearbyEntangled++;
                    }
                }
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = other;
                }
            }

            float avgFreqDiff = nearbyCount > 0 ? totalFreqDiff / nearbyCount : 100.0f;
            boolean spikeActive = n.quantumClock != null && n.quantumClock.isSpikeActive();
            float coherence = (float) n.consciousness.getCoherence();

            int[] sensors = LogicBrain.buildSensorInputs(
                    nearbyCount, avgFreqDiff, n.energy, n.phiResonance,
                    coherence, n.phase, spikeActive, n.age
            );

            int[] outputs = n.brain.compute(sensors);
            String decision = n.brain.interpretOutputs(outputs);

            double roleBonus = n.getRole().getSpecializationBonus(outputs);
            if (roleBonus > 0) {
                n.boostEnergy((float)(roleBonus * dt));
            }

            n.adaptiveEngine.recordFitnessSample(n.energy, spikeActive, nearbyEntangled, false);

            AdaptiveLogicEngine.TrialResult trialResult = n.adaptiveEngine.tickTrial(n.brain);
            if (trialResult == AdaptiveLogicEngine.TrialResult.ADOPTED) {
                if (memory != null) {
                    memory.record("ADAPTATION", String.format("%s|adopted|fitness=%.3f", n.name, n.adaptiveEngine.getCurrentFitness()));
                }
                FraymusUI.addLog(String.format("[ADAPT] %s adopted new strategy (fitness=%.3f)", n.name, n.adaptiveEngine.getCurrentFitness()));
            } else if (trialResult == AdaptiveLogicEngine.TrialResult.REVERTED) {
                FraymusUI.addLog(String.format("[ADAPT] %s reverted trial (not fit enough)", n.name));
            }

            if (n.brain.wantsToSeek(outputs) && nearest != null) {
                float dx = nearest.x - n.x;
                float dy = nearest.y - n.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > 1.0f) {
                    n.vx += (dx / dist) * 2.0f * dt;
                    n.vy += (dy / dist) * 2.0f * dt;
                }
            }

            if (n.brain.wantsToFlee(outputs) && nearest != null) {
                float dx = n.x - nearest.x;
                float dy = n.y - nearest.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > 0.5f) {
                    n.vx += (dx / dist) * 3.0f * dt;
                    n.vy += (dy / dist) * 3.0f * dt;
                }
            }

            if (n.brain.wantsToConserve(outputs)) {
                n.vx *= 0.95f;
                n.vy *= 0.95f;
                n.boostEnergy(0.005f * dt);
            }

            if (n.brain.wantsToMutate(outputs) && spikeActive) {
                if (!n.adaptiveEngine.isInTrial()) {
                    n.adaptiveEngine.beginTrial(n.brain);
                    if (memory != null && tickCounter % 60 == 0) {
                        memory.recordMutation(n.name, "spike-trial-started");
                        FraymusUI.addLog(String.format("[TRIAL] %s began mutation trial during spike", n.name));
                    }
                }
            }

            if (n.brain.wantsEnergyBurst(outputs) && n.energy > 0.5f) {
                n.energy -= 0.02f;
                float burstVx = (float)(Math.cos(n.phase) * 5.0);
                float burstVy = (float)(Math.sin(n.phase) * 5.0);
                n.vx += burstVx * dt;
                n.vy += burstVy * dt;
            }

            if (n.brain.wantsEvolveDNA(outputs)) {
                n.dna.evolve();
                n.frequency = (float) n.dna.harmonicFrequency;
            }

            float maxVel = 15.0f;
            n.vx = Math.max(-maxVel, Math.min(maxVel, n.vx));
            n.vy = Math.max(-maxVel, Math.min(maxVel, n.vy));

            if (tickCounter % 300 == 0 && !decision.equals("IDLE")) {
                if (memory != null) {
                    memory.recordBrainDecision(n.name, decision);
                }
                FraymusUI.addLog(String.format("[BRAIN] %s -> %s", n.name, decision));
            }
        }
    }

    public static class ReproductionLaw implements PhiLaw {
        private PhiWorld world;
        private GenesisMemory memory;
        private int tickCounter = 0;
        private int childCount = 0;
        private static final int MAX_POPULATION = 30;

        public ReproductionLaw(PhiWorld world, GenesisMemory memory) {
            this.world = world;
            this.memory = memory;
        }

        @Override
        public void apply(PhiNode n, float dt) {
            tickCounter++;
            if (tickCounter % 120 != 0) return;
            if (world.getPopulation() >= MAX_POPULATION) return;

            if (!n.canReproduce()) return;

            int[] outputs = n.brain.getLastOutputs();
            if (!n.brain.wantsToReproduce(outputs)) return;

            boolean spikeActive = n.quantumClock != null && n.quantumClock.isSpikeActive();
            if (!spikeActive && n.energy < 0.9f) return;

            childCount++;
            String childName = n.name + "-" + childCount;

            float offsetX = (float)(Math.cos(n.phase) * 5.0);
            float offsetY = (float)(Math.sin(n.phase) * 5.0);

            PhiNode child = n.reproduce(null, childName, n.x + offsetX, n.y + offsetY);
            child.vx = offsetX * 0.5f;
            child.vy = offsetY * 0.5f;

            ColonyCoach coach = world.getCoach();
            if (coach != null) {
                child.setRole(coach.suggestRoleForChild(n));
            }

            world.addNode(child);

            if (memory != null) {
                memory.recordBirth(childName, n.name);
            }
            FraymusUI.addLog(String.format("[BIRTH] %s spawned %s", n.name, childName));
        }
    }

    public static class BoundaryLaw implements PhiLaw {
        private float minX, maxX, minY, maxY;

        public BoundaryLaw(float minX, float maxX, float minY, float maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        @Override
        public void apply(PhiNode n, float dt) {
            if (n.x < minX) { n.x = minX; n.vx = Math.abs(n.vx) * 0.5f; }
            if (n.x > maxX) { n.x = maxX; n.vx = -Math.abs(n.vx) * 0.5f; }
            if (n.y < minY) { n.y = minY; n.vy = Math.abs(n.vy) * 0.5f; }
            if (n.y > maxY) { n.y = maxY; n.vy = -Math.abs(n.vy) * 0.5f; }
        }
    }
}
