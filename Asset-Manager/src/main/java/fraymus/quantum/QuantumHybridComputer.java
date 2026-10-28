package fraymus.quantum;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Function;

/**
 * Quantum-Hybrid Computing with Entangled State Management
 * 
 * Implements a hybrid quantum-classical computing system that leverages
 * quantum entanglement for parallel computation while maintaining classical
 * interfaces for practical integration.
 * 
 * Phase 5.4: Quantum-Hybrid Computing Implementation
 * 
 * Key capabilities:
 * - Entangled state management and tracking
 * - Quantum gate simulation (Hadamard, CNOT, Phase)
 * - Superposition and interference patterns
 * - Quantum measurement collapse
 * - Hybrid quantum-classical optimization
 * 
 * Mathematical foundation:
 * - Quantum state: |ψ⟩ = Σ α_i |i⟩ where Σ |α_i|² = 1
 * - Entanglement: |ψ⟩ = Σ α_ij |i⟩_A ⊗ |j⟩_B (non-separable)
 * - Hadamard: H|0⟩ = (|0⟩ + |1⟩)/√2
 * - CNOT: CNOT|00⟩ = |00⟩, CNOT|01⟩ = |01⟩, CNOT|10⟩ = |11⟩, CNOT|11⟩ = |10⟩
 * - Measurement: P(i) = |α_i|²
 * 
 * Architecture:
 * - Classical control layer: Manages quantum operations
 * - Quantum simulation layer: Simulates quantum state evolution
 * - Entanglement manager: Tracks entangled qubit pairs
 * - Measurement interface: Collapses quantum states to classical values
 */
public class QuantumHybridComputer {
    
    // Quantum state representation
    private final Complex[] stateVector;
    private final int numQubits;
    private final int stateDimension;
    
    // Entanglement tracking
    private final EntanglementManager entanglementManager;
    
    // Gate operations
    private final QuantumGates gates;
    
    // Measurement statistics
    private final MeasurementStatistics statistics;
    
    // Hybrid optimization
    private final QuantumOptimizer optimizer;
    
    // Current state
    private volatile boolean stateCollapsed;
    private volatile long stateVersion;
    
    public QuantumHybridComputer(int numQubits) {
        this.numQubits = numQubits;
        this.stateDimension = 1 << numQubits; // 2^numQubits
        this.stateVector = new Complex[stateDimension];
        this.entanglementManager = new EntanglementManager(numQubits);
        this.gates = new QuantumGates();
        this.statistics = new MeasurementStatistics();
        this.optimizer = new QuantumOptimizer(numQubits);
        this.stateCollapsed = false;
        this.stateVersion = 0;
        
        // Initialize to |0...0⟩ state
        initializeState();
    }
    
    /**
     * Initialize quantum state to |0...0⟩.
     */
    private void initializeState() {
        Arrays.fill(stateVector, Complex.ZERO);
        stateVector[0] = Complex.ONE; // |0...0⟩
        stateVersion++;
    }
    
    /**
     * Apply Hadamard gate to qubit.
     */
    public void applyHadamard(int qubit) {
        if (stateCollapsed) {
            throw new IllegalStateException("State collapsed, must reset");
        }
        
        int mask = 1 << qubit;
        
        for (int i = 0; i < stateDimension; i++) {
            int pairedIndex = i ^ mask;
            
            if (i < pairedIndex) {
                Complex a = stateVector[i];
                Complex b = stateVector[pairedIndex];
                
                // H gate: (a+b)/√2, (a-b)/√2
                double sqrt2 = Math.sqrt(2);
                stateVector[i] = new Complex((a.real + b.real) / sqrt2, (a.imag + b.imag) / sqrt2);
                stateVector[pairedIndex] = new Complex((a.real - b.real) / sqrt2, (a.imag - b.imag) / sqrt2);
            }
        }
        
        stateVersion++;
    }
    
    /**
     * Apply CNOT gate (control qubit, target qubit).
     */
    public void applyCNOT(int control, int target) {
        if (stateCollapsed) {
            throw new IllegalStateException("State collapsed, must reset");
        }
        
        int controlMask = 1 << control;
        int targetMask = 1 << target;
        
        for (int i = 0; i < stateDimension; i++) {
            if ((i & controlMask) != 0) {
                int pairedIndex = i ^ targetMask;
                
                if (i < pairedIndex) {
                    Complex temp = stateVector[i];
                    stateVector[i] = stateVector[pairedIndex];
                    stateVector[pairedIndex] = temp;
                }
            }
        }
        
        // Track entanglement
        entanglementManager.recordEntanglement(control, target);
        stateVersion++;
    }
    
    /**
     * Apply phase gate to qubit.
     */
    public void applyPhase(int qubit, double phase) {
        if (stateCollapsed) {
            throw new IllegalStateException("State collapsed, must reset");
        }
        
        int mask = 1 << qubit;
        
        for (int i = 0; i < stateDimension; i++) {
            if ((i & mask) != 0) {
                Complex current = stateVector[i];
                double cos = Math.cos(phase);
                double sin = Math.sin(phase);
                stateVector[i] = new Complex(
                    current.real * cos - current.imag * sin,
                    current.real * sin + current.imag * cos
                );
            }
        }
        
        stateVersion++;
    }
    
    /**
     * Create superposition across all qubits.
     */
    public void createSuperposition() {
        for (int i = 0; i < numQubits; i++) {
            applyHadamard(i);
        }
    }
    
    /**
     * Create Bell state (entangled pair).
     */
    public void createBellState(int qubit1, int qubit2) {
        // Start with |00⟩
        initializeState();
        
        // Apply H to first qubit: (|0⟩ + |1⟩)/√2 ⊗ |0⟩
        applyHadamard(qubit1);
        
        // Apply CNOT: (|00⟩ + |11⟩)/√2
        applyCNOT(qubit1, qubit2);
    }
    
    /**
     * Measure quantum state (collapse to classical).
     */
    public int measure() {
        if (stateCollapsed) {
            throw new IllegalStateException("State already collapsed");
        }
        
        // Compute probabilities
        double[] probabilities = new double[stateDimension];
        for (int i = 0; i < stateDimension; i++) {
            probabilities[i] = stateVector[i].magnitudeSquared();
        }
        
        // Sample from probability distribution
        double rand = Math.random();
        double cumulative = 0.0;
        int result = 0;
        
        for (int i = 0; i < stateDimension; i++) {
            cumulative += probabilities[i];
            if (rand < cumulative) {
                result = i;
                break;
            }
        }
        
        // Collapse state
        collapseTo(result);
        
        // Update statistics
        statistics.recordMeasurement(result, probabilities);
        
        return result;
    }
    
    /**
     * Measure specific qubit.
     */
    public int measureQubit(int qubit) {
        if (stateCollapsed) {
            throw new IllegalStateException("State already collapsed");
        }
        
        int mask = 1 << qubit;
        
        // Compute marginal probability
        double probZero = 0.0;
        for (int i = 0; i < stateDimension; i++) {
            if ((i & mask) == 0) {
                probZero += stateVector[i].magnitudeSquared();
            }
        }
        
        // Sample
        int result = Math.random() < probZero ? 0 : 1;
        
        // Partial collapse
        partialCollapse(qubit, result);
        
        return result;
    }
    
    /**
     * Collapse state to specific basis state.
     */
    private void collapseTo(int basisState) {
        Arrays.fill(stateVector, Complex.ZERO);
        stateVector[basisState] = Complex.ONE;
        stateCollapsed = true;
        stateVersion++;
    }
    
    /**
     * Partial collapse for single qubit measurement.
     */
    private void partialCollapse(int qubit, int value) {
        int mask = 1 << qubit;
        double norm = 0.0;
        
        // Zero out amplitudes for incompatible states
        for (int i = 0; i < stateDimension; i++) {
            if (((i & mask) != 0) != (value != 0)) {
                stateVector[i] = Complex.ZERO;
            } else {
                norm += stateVector[i].magnitudeSquared();
            }
        }
        
        // Renormalize
        if (norm > 0) {
            double sqrtNorm = Math.sqrt(norm);
            for (int i = 0; i < stateDimension; i++) {
                stateVector[i] = stateVector[i].divide(sqrtNorm);
            }
        }
        
        stateVersion++;
    }
    
    /**
     * Reset quantum state.
     */
    public void reset() {
        initializeState();
        stateCollapsed = false;
        entanglementManager.reset();
        statistics.reset();
    }
    
    /**
     * Get current state vector (read-only).
     */
    public Complex[] getStateVector() {
        return stateVector.clone();
    }
    
    /**
     * Get entanglement information.
     */
    public EntanglementInfo getEntanglementInfo() {
        return entanglementManager.getInfo();
    }
    
    /**
     * Get measurement statistics.
     */
    public MeasurementStatistics getStatistics() {
        return statistics;
    }
    
    /**
     * Run quantum circuit.
     */
    public QuantumResult runCircuit(List<QuantumGate> circuit) {
        reset();
        
        for (QuantumGate gate : circuit) {
            applyGate(gate);
        }
        
        int measurement = measure();
        
        return new QuantumResult(measurement, stateVector.clone(), statistics);
    }
    
    /**
     * Apply generic quantum gate.
     */
    private void applyGate(QuantumGate gate) {
        switch (gate.type) {
            case HADAMARD:
                applyHadamard(gate.target);
                break;
            case CNOT:
                applyCNOT(gate.control, gate.target);
                break;
            case PHASE:
                applyPhase(gate.target, gate.phase);
                break;
            default:
                throw new IllegalArgumentException("Unknown gate type: " + gate.type);
        }
    }
    
    /**
     * Hybrid quantum-classical optimization.
     */
    public OptimizationResult optimize(Function<double[], Double> objectiveFunction, 
                                      int iterations) {
        return optimizer.optimize(this, objectiveFunction, iterations);
    }
    
    /**
     * Complex number class.
     */
    public static class Complex {
        public static final Complex ZERO = new Complex(0, 0);
        public static final Complex ONE = new Complex(1, 0);
        
        public final double real;
        public final double imag;
        
        public Complex(double real, double imag) {
            this.real = real;
            this.imag = imag;
        }
        
        public double magnitudeSquared() {
            return real * real + imag * imag;
        }
        
        public Complex divide(double scalar) {
            return new Complex(real / scalar, imag / scalar);
        }
        
        @Override
        public String toString() {
            return String.format("%.4f%+.4fi", real, imag);
        }
    }
    
    /**
     * Quantum Gate class.
     */
    public static class QuantumGate {
        public enum GateType { HADAMARD, CNOT, PHASE }
        
        public final GateType type;
        public final int target;
        public final int control;
        public final double phase;
        
        public QuantumGate(GateType type, int target) {
            this(type, target, -1, 0.0);
        }
        
        public QuantumGate(GateType type, int target, int control) {
            this(type, target, control, 0.0);
        }
        
        public QuantumGate(GateType type, int target, int control, double phase) {
            this.type = type;
            this.target = target;
            this.control = control;
            this.phase = phase;
        }
        
        public static QuantumGate hadamard(int target) {
            return new QuantumGate(GateType.HADAMARD, target);
        }
        
        public static QuantumGate cnot(int control, int target) {
            return new QuantumGate(GateType.CNOT, target, control);
        }
        
        public static QuantumGate phase(int target, double phase) {
            return new QuantumGate(GateType.PHASE, target, -1, phase);
        }
    }
    
    /**
     * Entanglement Manager class.
     */
    private static class EntanglementManager {
        private final boolean[][] entanglementMatrix;
        private final AtomicInteger entanglementCount;
        
        public EntanglementManager(int numQubits) {
            this.entanglementMatrix = new boolean[numQubits][numQubits];
            this.entanglementCount = new AtomicInteger(0);
        }
        
        public void recordEntanglement(int qubit1, int qubit2) {
            if (qubit1 != qubit2 && !entanglementMatrix[qubit1][qubit2]) {
                entanglementMatrix[qubit1][qubit2] = true;
                entanglementMatrix[qubit2][qubit1] = true;
                entanglementCount.incrementAndGet();
            }
        }
        
        public EntanglementInfo getInfo() {
            return new EntanglementInfo(entanglementCount.get(), entanglementMatrix);
        }
        
        public void reset() {
            for (int i = 0; i < entanglementMatrix.length; i++) {
                Arrays.fill(entanglementMatrix[i], false);
            }
            entanglementCount.set(0);
        }
    }
    
    /**
     * Entanglement Info class.
     */
    public static class EntanglementInfo {
        public final int entanglementCount;
        public final boolean[][] entanglementMatrix;
        
        public EntanglementInfo(int entanglementCount, boolean[][] entanglementMatrix) {
            this.entanglementCount = entanglementCount;
            this.entanglementMatrix = entanglementMatrix;
        }
        
        public boolean areEntangled(int qubit1, int qubit2) {
            return entanglementMatrix[qubit1][qubit2];
        }
    }
    
    /**
     * Measurement Statistics class.
     */
    private static class MeasurementStatistics {
        private final Map<Integer, Integer> measurementCounts;
        private final List<double[]> probabilityHistory;
        private long totalMeasurements;
        
        public MeasurementStatistics() {
            this.measurementCounts = new ConcurrentHashMap<>();
            this.probabilityHistory = new ArrayList<>();
            this.totalMeasurements = 0;
        }
        
        public void recordMeasurement(int result, double[] probabilities) {
            measurementCounts.merge(result, 1, Integer::sum);
            probabilityHistory.add(probabilities.clone());
            totalMeasurements++;
            
            // Keep history bounded
            if (probabilityHistory.size() > 1000) {
                probabilityHistory.remove(0);
            }
        }
        
        public double getProbability(int basisState) {
            if (probabilityHistory.isEmpty()) return 0.0;
            
            double sum = 0.0;
            for (double[] probs : probabilityHistory) {
                if (basisState < probs.length) {
                    sum += probs[basisState];
                }
            }
            
            return sum / probabilityHistory.size();
        }
        
        public void reset() {
            measurementCounts.clear();
            probabilityHistory.clear();
            totalMeasurements = 0;
        }
    }
    
    /**
     * Quantum Gates class.
     */
    private static class QuantumGates {
        // Precomputed gate matrices could be stored here
        // for optimized gate application
    }
    
    /**
     * Quantum Optimizer class.
     */
    private static class QuantumOptimizer {
        private final int numQubits;
        
        public QuantumOptimizer(int numQubits) {
            this.numQubits = numQubits;
        }
        
        public OptimizationResult optimize(QuantumHybridComputer computer,
                                         Function<double[], Double> objectiveFunction,
                                         int iterations) {
            double bestValue = Double.MAX_VALUE;
            double[] bestParameters = null;
            
            for (int i = 0; i < iterations; i++) {
                // Generate random circuit
                List<QuantumGate> circuit = generateRandomCircuit();
                
                // Run circuit
                QuantumResult result = computer.runCircuit(circuit);
                
                // Evaluate objective
                double[] parameters = result.stateVectorToParameters();
                double value = objectiveFunction.apply(parameters);
                
                if (value < bestValue) {
                    bestValue = value;
                    bestParameters = parameters;
                }
            }
            
            return new OptimizationResult(bestValue, bestParameters, iterations);
        }
        
        private List<QuantumGate> generateRandomCircuit() {
            List<QuantumGate> circuit = new ArrayList<>();
            int numGates = 5 + (int)(Math.random() * 10);
            
            for (int i = 0; i < numGates; i++) {
                int gateType = (int)(Math.random() * 3);
                int target = (int)(Math.random() * numQubits);
                
                switch (gateType) {
                    case 0:
                        circuit.add(QuantumGate.hadamard(target));
                        break;
                    case 1:
                        int control = (int)(Math.random() * numQubits);
                        if (control != target) {
                            circuit.add(QuantumGate.cnot(control, target));
                        }
                        break;
                    case 2:
                        double phase = Math.random() * 2 * Math.PI;
                        circuit.add(QuantumGate.phase(target, phase));
                        break;
                }
            }
            
            return circuit;
        }
    }
    
    /**
     * Quantum Result class.
     */
    public static class QuantumResult {
        public final int measurement;
        public final Complex[] stateVector;
        public final MeasurementStatistics statistics;
        
        public QuantumResult(int measurement, Complex[] stateVector, 
                           MeasurementStatistics statistics) {
            this.measurement = measurement;
            this.stateVector = stateVector;
            this.statistics = statistics;
        }
        
        public double[] stateVectorToParameters() {
            double[] params = new double[stateVector.length];
            for (int i = 0; i < stateVector.length; i++) {
                params[i] = stateVector[i].magnitudeSquared();
            }
            return params;
        }
    }
    
    /**
     * Optimization Result class.
     */
    public static class OptimizationResult {
        public final double bestValue;
        public final double[] bestParameters;
        public final int iterations;
        
        public OptimizationResult(double bestValue, double[] bestParameters, int iterations) {
            this.bestValue = bestValue;
            this.bestParameters = bestParameters;
            this.iterations = iterations;
        }
    }
}
