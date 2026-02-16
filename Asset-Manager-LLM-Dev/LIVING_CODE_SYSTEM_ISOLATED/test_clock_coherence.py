#!/usr/bin/env python3
"""
Isolated Test: System Time vs φ-Harmonic Clock
Tests whether a shared φ-harmonic clock improves node coherence and entanglement
compared to using system time.
"""

import time
import math
import random
from decimal import Decimal, getcontext
from phi_harmonic_quantum_clock import PhiHarmonicQuantumClock, PHI, RESONANCE_STACK

# Set precision
getcontext().prec = 100

class TestNode:
    """A simple node for testing coherence and entanglement."""
    
    def __init__(self, node_id, use_quantum_clock=False, quantum_clock=None):
        self.id = node_id
        self.use_quantum_clock = use_quantum_clock
        self.quantum_clock = quantum_clock
        
        # Initialize with random frequency near 432-528 Hz (harmonic range)
        self.base_frequency = 432 + random.random() * 96  # 432-528 Hz
        self.current_frequency = self.base_frequency
        self.phase = random.random() * 2 * math.pi
        
        # Phi-signature for resonance (unique per node)
        self.phi_signature = (float(PHI) ** (node_id % 7)) % 1
        
        # Determine resonant harmonic (1-12)
        self.resonant_harmonic = int(self.phi_signature * 12) + 1
        
        # 7D resonance coordinates
        self.dimension_coords = [(float(PHI) ** i) % 1 for i in range(1, 8)]
        
        # Tracking
        self.update_count = 0
        self.frequency_history = []
        self.phase_history = []
    
    def get_time(self):
        """Get time from either system or quantum clock."""
        if self.use_quantum_clock and self.quantum_clock:
            # Use 7D resonance matrix with 12 harmonics
            base_time = float(self.quantum_clock.get_oscillation_count())
            
            # Calculate resonance modulation across 7 dimensions
            resonance_factor = 0
            for i, coord in enumerate(self.dimension_coords):
                # R_ij = (φ^i % 1) * modulation
                dim_resonance = coord * math.sin(base_time * (i + 1) * 0.1)
                resonance_factor += dim_resonance
            
            # Normalize resonance factor
            resonance_factor = (resonance_factor / 7) % 1
            
            # Apply harmonic frequency modulation
            # f_i = f_base * (φ^(i/7) % 1) * (1 + R_ij)
            harmonic_mod = (float(PHI) ** (self.resonant_harmonic / 7)) % 1
            time_mod = 1 + (resonance_factor * harmonic_mod)
            
            # Return phi-dimensional time
            return base_time * time_mod
        else:
            # Use flat system time
            return time.time()
    
    def update(self, dt=None):
        """Update node state based on time source."""
        self.update_count += 1
        
        # Get current time
        t = self.get_time()
        
        # Calculate frequency drift (breathing)
        if self.use_quantum_clock and self.quantum_clock:
            # Sync to quantum clock's phi resonance
            phi_resonance = self.quantum_clock.quantum_state['phi_resonance']
            resonance_freq = self.quantum_clock.quantum_state['resonance_frequency']
            
            # Frequency modulated by φ-resonance
            self.current_frequency = self.base_frequency * (1 + phi_resonance * 0.1)
            
            # Phase aligned to quantum clock
            self.phase = (t * self.current_frequency * 2 * math.pi) % (2 * math.pi)
        else:
            # Independent drift using system time
            drift = 0.05 * math.sin(t * 0.1)
            self.current_frequency = self.base_frequency + drift
            
            # Phase evolves independently
            if dt:
                self.phase += 2 * math.pi * self.current_frequency * dt
            else:
                self.phase = (t * self.current_frequency * 2 * math.pi) % (2 * math.pi)
        
        # Track history
        self.frequency_history.append(self.current_frequency)
        self.phase_history.append(self.phase)
    
    def get_state(self):
        """Get current quantum state."""
        return {
            'frequency': self.current_frequency,
            'phase': self.phase,
            'amplitude': math.sin(self.phase)
        }


class CoherenceAnalyzer:
    """Analyzes coherence and entanglement metrics for node populations."""
    
    def __init__(self):
        self.metrics = {}
    
    def measure_frequency_coherence(self, nodes):
        """
        Measure how synchronized node frequencies are.
        Perfect coherence = 1.0 (all same frequency)
        No coherence = 0.0 (random frequencies)
        """
        if len(nodes) < 2:
            return 1.0
        
        frequencies = [node.current_frequency for node in nodes]
        mean_freq = sum(frequencies) / len(frequencies)
        variance = sum((f - mean_freq)**2 for f in frequencies) / len(frequencies)
        std_dev = math.sqrt(variance)
        
        # Normalize by mean frequency (coefficient of variation)
        if mean_freq > 0:
            cv = std_dev / mean_freq
            coherence = 1.0 / (1.0 + cv)  # Higher coherence = lower variation
        else:
            coherence = 0.0
        
        return coherence
    
    def measure_phase_alignment(self, nodes):
        """
        Measure how aligned node phases are.
        Perfect alignment = 1.0 (all in phase)
        No alignment = 0.0 (random phases)
        """
        if len(nodes) < 2:
            return 1.0
        
        # Calculate phase coherence using circular statistics
        phases = [node.phase for node in nodes]
        
        # Mean resultant vector
        cos_sum = sum(math.cos(p) for p in phases)
        sin_sum = sum(math.sin(p) for p in phases)
        
        # Resultant length (0 to 1)
        R = math.sqrt(cos_sum**2 + sin_sum**2) / len(phases)
        
        return R
    
    def measure_entanglement(self, nodes):
        """
        Measure quantum entanglement between nodes.
        Uses correlation of frequency and phase states.
        """
        if len(nodes) < 2:
            return 0.0
        
        # Calculate pairwise correlations
        correlations = []
        
        for i in range(len(nodes)):
            for j in range(i + 1, len(nodes)):
                node_a = nodes[i]
                node_b = nodes[j]
                
                # Frequency correlation
                freq_diff = abs(node_a.current_frequency - node_b.current_frequency)
                freq_correlation = 1.0 / (1.0 + freq_diff / 100)  # Normalize
                
                # Phase correlation
                phase_diff = abs(node_a.phase - node_b.phase) % (2 * math.pi)
                phase_diff = min(phase_diff, 2 * math.pi - phase_diff)  # Shortest angular distance
                phase_correlation = 1.0 - (phase_diff / math.pi)
                
                # Combined correlation
                correlation = (freq_correlation + phase_correlation) / 2
                correlations.append(correlation)
        
        # Average correlation across all pairs
        if correlations:
            return sum(correlations) / len(correlations)
        return 0.0
    
    def measure_resonance_stability(self, nodes):
        """
        Measure how stable the resonance is over time.
        Checks if nodes maintain harmonic relationships.
        """
        if len(nodes) < 2:
            return 1.0
        
        # Check if frequencies form harmonic ratios
        frequencies = sorted([node.current_frequency for node in nodes])
        
        harmonic_score = 0.0
        comparisons = 0
        
        for i in range(len(frequencies)):
            for j in range(i + 1, len(frequencies)):
                ratio = frequencies[j] / frequencies[i]
                
                # Check if ratio is close to φ, φ², 2, 3, etc.
                harmonic_ratios = [float(PHI), float(PHI**2), 2.0, 3.0, 1.5]
                
                for h_ratio in harmonic_ratios:
                    if abs(ratio - h_ratio) < 0.1:
                        harmonic_score += 1.0
                        break
                
                comparisons += 1
        
        if comparisons > 0:
            return harmonic_score / comparisons
        return 0.0
    
    def analyze(self, nodes, label=""):
        """Run full analysis on node population."""
        freq_coherence = self.measure_frequency_coherence(nodes)
        phase_alignment = self.measure_phase_alignment(nodes)
        entanglement = self.measure_entanglement(nodes)
        resonance_stability = self.measure_resonance_stability(nodes)
        
        # Overall coherence score
        overall = (freq_coherence + phase_alignment + entanglement + resonance_stability) / 4
        
        results = {
            'label': label,
            'frequency_coherence': freq_coherence,
            'phase_alignment': phase_alignment,
            'entanglement': entanglement,
            'resonance_stability': resonance_stability,
            'overall_coherence': overall
        }
        
        return results


def run_coherence_test(num_nodes=10, duration=10.0, update_interval=0.1):
    """
    Run isolated test comparing system time vs quantum clock.
    
    Args:
        num_nodes: Number of nodes to test
        duration: Test duration in seconds
        update_interval: Update interval in seconds
    
    Returns:
        dict: Test results
    """
    print("=" * 70)
    print("COHERENCE TEST: System Time vs φ-Harmonic Quantum Clock")
    print("=" * 70)
    print(f"Nodes: {num_nodes}")
    print(f"Duration: {duration}s")
    print(f"Update interval: {update_interval}s")
    print()
    
    # Create quantum clock
    quantum_clock = PhiHarmonicQuantumClock(pendulum_frequency=1.0)
    
    # Create two populations
    print("Creating node populations...")
    system_time_nodes = [TestNode(i, use_quantum_clock=False) for i in range(num_nodes)]
    quantum_clock_nodes = [TestNode(i, use_quantum_clock=True, quantum_clock=quantum_clock) for i in range(num_nodes)]
    
    # Create analyzer
    analyzer = CoherenceAnalyzer()
    
    # Track results over time
    system_time_results = []
    quantum_clock_results = []
    
    print("Running simulation...")
    start_time = time.time()
    last_update = start_time
    update_count = 0
    
    while time.time() - start_time < duration:
        current_time = time.time()
        dt = current_time - last_update
        
        if dt >= update_interval:
            # Update all nodes
            for node in system_time_nodes:
                node.update(dt)
            
            for node in quantum_clock_nodes:
                node.update(dt)
            
            # Analyze every 10 updates
            if update_count % 10 == 0:
                system_results = analyzer.analyze(system_time_nodes, "System Time")
                quantum_results = analyzer.analyze(quantum_clock_nodes, "Quantum Clock")
                
                system_time_results.append(system_results)
                quantum_clock_results.append(quantum_results)
                
                # Print progress
                print(f"\rUpdate {update_count}: System={system_results['overall_coherence']:.4f} | "
                      f"Quantum={quantum_results['overall_coherence']:.4f}", end="")
            
            last_update = current_time
            update_count += 1
    
    print("\n\nTest complete!")
    print()
    
    # Calculate averages
    avg_system = {
        'frequency_coherence': sum(r['frequency_coherence'] for r in system_time_results) / len(system_time_results),
        'phase_alignment': sum(r['phase_alignment'] for r in system_time_results) / len(system_time_results),
        'entanglement': sum(r['entanglement'] for r in system_time_results) / len(system_time_results),
        'resonance_stability': sum(r['resonance_stability'] for r in system_time_results) / len(system_time_results),
        'overall_coherence': sum(r['overall_coherence'] for r in system_time_results) / len(system_time_results)
    }
    
    avg_quantum = {
        'frequency_coherence': sum(r['frequency_coherence'] for r in quantum_clock_results) / len(quantum_clock_results),
        'phase_alignment': sum(r['phase_alignment'] for r in quantum_clock_results) / len(quantum_clock_results),
        'entanglement': sum(r['entanglement'] for r in quantum_clock_results) / len(quantum_clock_results),
        'resonance_stability': sum(r['resonance_stability'] for r in quantum_clock_results) / len(quantum_clock_results),
        'overall_coherence': sum(r['overall_coherence'] for r in quantum_clock_results) / len(quantum_clock_results)
    }
    
    # Print results
    print("=" * 70)
    print("RESULTS")
    print("=" * 70)
    print()
    print("SYSTEM TIME NODES:")
    print(f"  Frequency Coherence:   {avg_system['frequency_coherence']:.4f}")
    print(f"  Phase Alignment:       {avg_system['phase_alignment']:.4f}")
    print(f"  Entanglement:          {avg_system['entanglement']:.4f}")
    print(f"  Resonance Stability:   {avg_system['resonance_stability']:.4f}")
    print(f"  Overall Coherence:     {avg_system['overall_coherence']:.4f}")
    print()
    print("QUANTUM CLOCK NODES:")
    print(f"  Frequency Coherence:   {avg_quantum['frequency_coherence']:.4f}")
    print(f"  Phase Alignment:       {avg_quantum['phase_alignment']:.4f}")
    print(f"  Entanglement:          {avg_quantum['entanglement']:.4f}")
    print(f"  Resonance Stability:   {avg_quantum['resonance_stability']:.4f}")
    print(f"  Overall Coherence:     {avg_quantum['overall_coherence']:.4f}")
    print()
    
    # Calculate improvement
    improvement = {
        'frequency_coherence': ((avg_quantum['frequency_coherence'] - avg_system['frequency_coherence']) / avg_system['frequency_coherence'] * 100) if avg_system['frequency_coherence'] > 0 else 0,
        'phase_alignment': ((avg_quantum['phase_alignment'] - avg_system['phase_alignment']) / avg_system['phase_alignment'] * 100) if avg_system['phase_alignment'] > 0 else 0,
        'entanglement': ((avg_quantum['entanglement'] - avg_system['entanglement']) / avg_system['entanglement'] * 100) if avg_system['entanglement'] > 0 else 0,
        'resonance_stability': ((avg_quantum['resonance_stability'] - avg_system['resonance_stability']) / avg_system['resonance_stability'] * 100) if avg_system['resonance_stability'] > 0 else 0,
        'overall_coherence': ((avg_quantum['overall_coherence'] - avg_system['overall_coherence']) / avg_system['overall_coherence'] * 100) if avg_system['overall_coherence'] > 0 else 0
    }
    
    print("IMPROVEMENT (Quantum Clock vs System Time):")
    print(f"  Frequency Coherence:   {improvement['frequency_coherence']:+.2f}%")
    print(f"  Phase Alignment:       {improvement['phase_alignment']:+.2f}%")
    print(f"  Entanglement:          {improvement['entanglement']:+.2f}%")
    print(f"  Resonance Stability:   {improvement['resonance_stability']:+.2f}%")
    print(f"  Overall Coherence:     {improvement['overall_coherence']:+.2f}%")
    print()
    
    # Verdict
    if avg_quantum['overall_coherence'] > avg_system['overall_coherence']:
        print("✅ VERDICT: Quantum Clock IMPROVES coherence and entanglement")
        print(f"   Overall improvement: {improvement['overall_coherence']:.2f}%")
    else:
        print("⚠️  VERDICT: System Time performs better")
        print(f"   Quantum clock is {-improvement['overall_coherence']:.2f}% worse")
    
    print("=" * 70)
    
    return {
        'system_time': avg_system,
        'quantum_clock': avg_quantum,
        'improvement': improvement,
        'verdict': 'quantum_clock' if avg_quantum['overall_coherence'] > avg_system['overall_coherence'] else 'system_time'
    }


if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description="Test coherence: System Time vs Quantum Clock")
    parser.add_argument("--nodes", type=int, default=10, help="Number of nodes to test")
    parser.add_argument("--duration", type=float, default=10.0, help="Test duration in seconds")
    parser.add_argument("--interval", type=float, default=0.1, help="Update interval in seconds")
    
    args = parser.parse_args()
    
    results = run_coherence_test(args.nodes, args.duration, args.interval)
