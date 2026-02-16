#!/usr/bin/env python3
"""
Dynamic Circuit Generator for Quantum Phi-Brain
Generates and evolves quantum circuits based on brain architecture
"""

import numpy as np
import json
import os
from datetime import datetime

PHI = (1 + np.sqrt(5)) / 2

class DynamicCircuitGenerator:
    """
    Generates quantum circuits dynamically based on brain architecture
    Evolves circuits through φ-harmonic optimization
    """
    
    def __init__(self, quantum_brain):
        self.brain = quantum_brain
        self.phi = PHI
        self.circuits = {}
        self.evolution_history = []
        self.optimization_count = 0
        
        print(f"\n⚡ Dynamic Circuit Generator initialized")
        print(f"  Brain: {self.brain.name}")
        print(f"  Layers: {len(self.brain.layers)}")
        
    def generate_circuit_for_layer(self, layer_level):
        """Generate a quantum circuit for a specific brain layer"""
        layer = self.brain.layers[layer_level - 1]
        
        circuit = {
            'id': f"circuit_L{layer_level}_{datetime.now().strftime('%Y%m%d_%H%M%S')}",
            'layer': layer_level,
            'layer_name': layer['name'],
            'frequency': layer['frequency'],
            'phi_power': layer['phi_power'],
            'gates': [],
            'qubits': layer['size'],
            'depth': int(layer['size'] / self.phi),
            'created': datetime.now().isoformat()
        }
        
        # Generate φ-harmonic gates
        for i in range(circuit['depth']):
            gate_type = self._select_gate_type(layer_level)
            gate = {
                'type': gate_type,
                'target': i % circuit['qubits'],
                'phi_angle': (2 * np.pi * i) / (self.phi ** layer_level),
                'resonance': self.phi ** layer['phi_power']
            }
            circuit['gates'].append(gate)
        
        self.circuits[circuit['id']] = circuit
        return circuit
    
    def _select_gate_type(self, layer_level):
        """Select quantum gate type based on layer level"""
        if layer_level <= 3:
            # Lower layers: Input, Pattern, Neural
            return np.random.choice(['H', 'X', 'RY'])
        elif layer_level <= 6:
            # Middle layers: Logic, Memory, Process
            return np.random.choice(['CNOT', 'RZ', 'RX'])
        else:
            # Upper layers: Bridge, Q-State, Quantum Bridge
            return np.random.choice(['T', 'S', 'SWAP'])
    
    def generate_full_brain_circuit(self):
        """Generate circuits for all brain layers"""
        print("\n🔄 Generating full brain circuit architecture...")
        
        full_circuit = {
            'id': f"full_brain_{datetime.now().strftime('%Y%m%d_%H%M%S')}",
            'brain': self.brain.name,
            'layers': [],
            'total_gates': 0,
            'total_qubits': 0,
            'created': datetime.now().isoformat()
        }
        
        for layer_level in range(1, 10):
            circuit = self.generate_circuit_for_layer(layer_level)
            full_circuit['layers'].append(circuit['id'])
            full_circuit['total_gates'] += len(circuit['gates'])
            full_circuit['total_qubits'] += circuit['qubits']
            
            print(f"  Layer {layer_level} ({circuit['layer_name']}): {len(circuit['gates'])} gates, {circuit['qubits']} qubits")
        
        print(f"\n✅ Full circuit generated: {full_circuit['total_gates']} gates, {full_circuit['total_qubits']} qubits")
        
        return full_circuit
    
    def optimize_circuits(self, force_creation=False):
        """Optimize circuits through φ-harmonic evolution"""
        print("\n🧬 Optimizing circuits through φ-harmonic evolution...")
        
        if not self.circuits or force_creation:
            # Generate circuits if none exist
            full_circuit = self.generate_full_brain_circuit()
        
        optimization_results = {
            'timestamp': datetime.now().isoformat(),
            'circuits_optimized': 0,
            'gates_reduced': 0,
            'phi_resonance_improved': 0,
            'evolution_data': []
        }
        
        for circuit_id, circuit in self.circuits.items():
            # Apply φ-harmonic optimization
            original_gates = len(circuit['gates'])
            
            # Optimize gate sequence
            optimized_gates = self._optimize_gate_sequence(circuit['gates'], circuit['phi_power'])
            
            gates_saved = original_gates - len(optimized_gates)
            circuit['gates'] = optimized_gates
            circuit['optimized'] = True
            circuit['optimization_count'] = circuit.get('optimization_count', 0) + 1
            
            optimization_results['circuits_optimized'] += 1
            optimization_results['gates_reduced'] += gates_saved
            
            evolution_entry = {
                'circuit_id': circuit_id,
                'layer': circuit['layer'],
                'gates_before': original_gates,
                'gates_after': len(optimized_gates),
                'improvement': gates_saved / original_gates if original_gates > 0 else 0
            }
            optimization_results['evolution_data'].append(evolution_entry)
        
        self.optimization_count += 1
        self.evolution_history.append(optimization_results)
        
        print(f"  Optimized {optimization_results['circuits_optimized']} circuits")
        print(f"  Reduced {optimization_results['gates_reduced']} gates")
        print(f"  Optimization cycle: {self.optimization_count}")
        
        return optimization_results
    
    def _optimize_gate_sequence(self, gates, phi_power):
        """Optimize gate sequence using φ-harmonic principles"""
        if len(gates) <= 1:
            return gates
        
        # Remove redundant gates
        optimized = []
        skip_next = False
        
        for i, gate in enumerate(gates):
            if skip_next:
                skip_next = False
                continue
            
            # Check for gate cancellation (e.g., X-X, H-H)
            if i < len(gates) - 1:
                next_gate = gates[i + 1]
                if gate['type'] == next_gate['type'] and gate['target'] == next_gate['target']:
                    if gate['type'] in ['X', 'H', 'SWAP']:
                        # These gates are self-inverse
                        skip_next = True
                        continue
            
            optimized.append(gate)
        
        # Apply φ-scaling to remaining gates
        for gate in optimized:
            gate['phi_optimized'] = True
            gate['resonance'] *= self.phi
        
        return optimized
    
    def evolve_circuit_for_problem(self, problem_type):
        """Evolve circuit specifically for a problem type"""
        print(f"\n🎯 Evolving circuit for problem: {problem_type}")
        
        # Select relevant layers based on problem type
        if problem_type in ['rsa_decryption', 'quantum_encryption']:
            # Use upper quantum layers
            relevant_layers = [7, 8, 9]
        elif problem_type in ['pattern_learning', 'consciousness_transfer']:
            # Use middle processing layers
            relevant_layers = [4, 5, 6]
        else:
            # Use full brain
            relevant_layers = list(range(1, 10))
        
        evolved_circuit = {
            'id': f"evolved_{problem_type}_{datetime.now().strftime('%Y%m%d_%H%M%S')}",
            'problem_type': problem_type,
            'layers': relevant_layers,
            'gates': [],
            'phi_resonance': self.phi ** len(relevant_layers),
            'created': datetime.now().isoformat()
        }
        
        # Generate specialized gates for each layer
        for layer_level in relevant_layers:
            layer_circuit = self.generate_circuit_for_layer(layer_level)
            evolved_circuit['gates'].extend(layer_circuit['gates'])
        
        # Apply problem-specific optimization
        evolved_circuit['gates'] = self._optimize_gate_sequence(
            evolved_circuit['gates'], 
            -len(relevant_layers)
        )
        
        print(f"  Evolved circuit: {len(evolved_circuit['gates'])} gates across {len(relevant_layers)} layers")
        
        return evolved_circuit
    
    def get_circuit_statistics(self):
        """Get statistics about generated circuits"""
        stats = {
            'total_circuits': len(self.circuits),
            'optimization_cycles': self.optimization_count,
            'total_gates': sum(len(c['gates']) for c in self.circuits.values()),
            'total_qubits': sum(c['qubits'] for c in self.circuits.values()),
            'average_depth': np.mean([c['depth'] for c in self.circuits.values()]) if self.circuits else 0,
            'phi_resonance_total': sum(c.get('phi_resonance', self.phi ** c['phi_power']) 
                                      for c in self.circuits.values())
        }
        
        return stats
    
    def save_circuits(self, filepath):
        """Save all circuits to file"""
        data = {
            'brain': self.brain.name,
            'timestamp': datetime.now().isoformat(),
            'circuits': self.circuits,
            'evolution_history': self.evolution_history,
            'statistics': self.get_circuit_statistics()
        }
        
        with open(filepath, 'w') as f:
            json.dump(data, f, indent=2)
        
        print(f"\n💾 Circuits saved to {filepath}")
    
    def load_circuits(self, filepath):
        """Load circuits from file"""
        with open(filepath, 'r') as f:
            data = json.load(f)
        
        self.circuits = data['circuits']
        self.evolution_history = data.get('evolution_history', [])
        
        print(f"\n📥 Circuits loaded from {filepath}")
        print(f"  Loaded {len(self.circuits)} circuits")


if __name__ == "__main__":
    # Demo: Generate and optimize circuits
    from build_quantum_brain import build_phi_harmonic_brain
    
    brain = build_phi_harmonic_brain("TestBrain", base_size=7)
    generator = DynamicCircuitGenerator(brain)
    
    # Generate full brain circuit
    full_circuit = generator.generate_full_brain_circuit()
    
    # Optimize circuits
    optimization_results = generator.optimize_circuits()
    
    # Evolve circuit for specific problem
    evolved = generator.evolve_circuit_for_problem('quantum_encryption')
    
    # Get statistics
    stats = generator.get_circuit_statistics()
    print(f"\n📊 Circuit Statistics:")
    for key, value in stats.items():
        print(f"  {key}: {value}")
    
    # Save circuits
    generator.save_circuits("circuits.json")
