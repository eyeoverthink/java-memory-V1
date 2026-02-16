#!/usr/bin/env python3
"""
Living Code Generator - Frankenstein's Brain
Integrates dr_frank.html living logic into phi_code_generator_ui.py
Generated code has living circuits that evolve, reproduce, and compute

Part of AGI_Material - Best Systems Collection
"""

import random
import math
import time
import hashlib
from typing import List, Dict, Optional

# Import shared constants
import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'core'))
try:
    from phi_constants import PHI, PHI_75, quantum_hash
except ImportError:
    PHI = 1.618033988749895
    PHI_75 = 4721424167835376.0
    def quantum_hash(data, salt=None):
        phi_salt = f"{PHI ** 7.5:.6f}-{salt}" if salt else f"{PHI ** 7.5:.6f}"
        return hashlib.sha3_256((str(data) + phi_salt).encode()).hexdigest()


class LogicGate:
    """Logic gate from dr_frank.html"""
    def __init__(self, gate_type: int, in1: int, in2: int):
        self.type = gate_type  # 0=AND, 1=OR, 2=XOR, 3=NAND
        self.in1 = in1
        self.in2 = in2
        self.state = 0
    
    def compute(self, inputs: List[int]) -> int:
        """Execute gate logic"""
        v1 = inputs[self.in1] if self.in1 < len(inputs) else 0
        v2 = inputs[self.in2] if self.in2 < len(inputs) else 0
        
        if self.type == 0:  # AND
            self.state = v1 & v2
        elif self.type == 1:  # OR
            self.state = v1 | v2
        elif self.type == 2:  # XOR
            self.state = v1 ^ v2
        elif self.type == 3:  # NAND
            self.state = 1 if not (v1 & v2) else 0
        
        return self.state
    
    def mutate(self):
        """Mutate gate structure"""
        if random.random() < 0.5:
            self.in1 = random.randint(0, 7)
        else:
            self.type = random.randint(0, 3)
    
    def to_code(self) -> str:
        """Generate Python code for this gate"""
        gate_names = ['AND', 'OR', 'XOR', 'NAND']
        return f"Gate(type={self.type}, in1={self.in1}, in2={self.in2}),  # {gate_names[self.type]}"


class LogicBrain:
    """Logic circuit brain from dr_frank.html"""
    def __init__(self, num_gates: int = 8):
        self.gates: List[LogicGate] = []
        for _ in range(num_gates):
            self.gates.append(LogicGate(
                random.randint(0, 3),
                random.randint(0, 7),
                random.randint(0, 7)
            ))
    
    def compute(self, inputs: List[int]) -> List[int]:
        """Compute brain output"""
        outputs = []
        for gate in self.gates:
            outputs.append(gate.compute(inputs))
        return outputs
    
    def mutate(self):
        """Mutate brain structure"""
        gate = random.choice(self.gates)
        gate.mutate()
    
    def crossover(self, partner: 'LogicBrain') -> 'LogicBrain':
        """Sexual reproduction of brains"""
        child = LogicBrain(num_gates=0)
        mid = len(self.gates) // 2
        child.gates = self.gates[:mid] + partner.gates[mid:]
        if random.random() < 0.1:
            child.mutate()
        return child
    
    def to_code(self) -> str:
        """Generate Python code for this brain"""
        gates_code = "\n        ".join(gate.to_code() for gate in self.gates)
        if gates_code.endswith(','):
            gates_code = gates_code[:-1]
        return f"[\n        {gates_code}\n    ]"


class LivingNode:
    """Living node from dr_frank.html with DNA and brain"""
    def __init__(self, dna: Optional[Dict] = None, brain: Optional[LogicBrain] = None):
        self.age = 0
        self.size = 5 + random.random() * 5
        self.base_size = self.size
        
        if dna:
            self.dna = dna
            self.brain = brain if brain else LogicBrain()
        else:
            self.dna = {
                'harmonic_frequency': 432 + random.random() * 20,
                'resonance': 0.5 + random.random(),
                'evolution': 0.05
            }
            self.brain = LogicBrain()
        
        self.pulse = 0
    
    def update(self):
        """Update node state (breathing, evolution)"""
        self.age += 1
        
        # Harmonic breathing
        t = time.time()
        self.pulse = math.sin(self.dna['harmonic_frequency'] * t * 0.0001) * self.dna['resonance']
        current_size = self.base_size + self.pulse * 5
        
        # Evolution (frequency drift)
        self.dna['harmonic_frequency'] += self.dna['evolution']
        
        # Frequency limiter (432-528 Hz)
        if self.dna['harmonic_frequency'] > 528:
            self.dna['harmonic_frequency'] = 432
        
        self.size = max(1, current_size)
    
    def can_reproduce(self) -> bool:
        """Check if node can perform mitosis"""
        return self.size > 15
    
    def reproduce(self, partner: Optional['LivingNode'] = None) -> 'LivingNode':
        """Mitosis: Create child node"""
        child_dna = {
            'harmonic_frequency': self.dna['harmonic_frequency'],
            'resonance': self.dna['resonance'],
            'evolution': self.dna['evolution']
        }
        
        if partner:
            child_brain = self.brain.crossover(partner.brain)
        else:
            child_brain = self.brain.crossover(self.brain)
        
        child = LivingNode(dna=child_dna, brain=child_brain)
        self.base_size *= 0.6  # Energy cost
        
        return child
    
    def to_code(self) -> str:
        """Generate Python code for this node"""
        return f'''{{
    'dna': {{
        'harmonic_frequency': {self.dna['harmonic_frequency']:.3f},
        'resonance': {self.dna['resonance']:.3f},
        'evolution': {self.dna['evolution']:.3f}
    }},
    'brain': {self.brain.to_code()},
    'age': {self.age},
    'size': {self.size:.3f}
}}'''


class LivingCodeGenerator:
    """Generate code with living logic circuits"""
    
    def __init__(self):
        self.nodes: List[LivingNode] = []
        self.generation = 0
        
        # Genesis: Create initial population
        for _ in range(5):
            self.nodes.append(LivingNode())
    
    def evolve_population(self, cycles: int = 10):
        """Evolve the living population"""
        for _ in range(cycles):
            # Update all nodes
            for node in self.nodes:
                node.update()
            
            # Reproduction
            new_nodes = []
            for i, node in enumerate(self.nodes):
                if node.can_reproduce() and len(self.nodes) + len(new_nodes) < 20:
                    partner = self.nodes[(i + 1) % len(self.nodes)] if len(self.nodes) > 1 else None
                    child = node.reproduce(partner)
                    new_nodes.append(child)
            
            self.nodes.extend(new_nodes)
        
        self.generation += 1
    
    def get_best_nodes(self, count: int = 3) -> List[LivingNode]:
        """Get the best evolved nodes"""
        return sorted(self.nodes, key=lambda n: n.dna['harmonic_frequency'], reverse=True)[:count]
    
    def generate_living_code(self, description: str) -> str:
        """Generate code with living logic circuits"""
        # Evolve population before generating
        self.evolve_population(cycles=5)
        
        # Get best nodes
        best_nodes = self.get_best_nodes(3)
        
        # Generate quantum signature
        code_hash = hashlib.sha256(description.encode()).hexdigest()
        quantum_sig = f"φ⁷·⁵-{code_hash[:16]}"
        
        # Generate cloaking N
        h1 = int(hashlib.sha256((description + "_A").encode()).hexdigest(), 16) % (2**20)
        h2 = int(hashlib.sha256((description + "_B").encode()).hexdigest(), 16) % (2**20)
        
        def next_prime(n):
            if n % 2 == 0: n += 1
            while True:
                is_prime = True
                for i in range(3, int(n**0.5) + 1, 2):
                    if n % i == 0:
                        is_prime = False
                        break
                if is_prime: return n
                n += 2
        
        p1 = next_prime(h1 | 1)
        p2 = next_prime(h2 | 1)
        if p1 == p2: p2 = next_prime(p2 + 2)
        cloak_n = p1 * p2
        
        genesis_block = f"block_{self.generation}_{int(time.time())}"
        
        code = f'''#!/usr/bin/env python3
"""
LIVING CODE - Generation {self.generation}
Description: {description}

═══════════════════════════════════════════════════════════════════
FRAYMUS LEGO ASSEMBLY - All pieces connected
═══════════════════════════════════════════════════════════════════
PIECE 1 - Quantum Signature: {quantum_sig}
PIECE 2 - Cloaking N: {cloak_n}
PIECE 3 - Genesis Block: {genesis_block}
PIECE 4 - Living Circuits: {len(best_nodes)} evolved from {len(self.nodes)} nodes
═══════════════════════════════════════════════════════════════════
"""

import math
import time
import random

PHI = {PHI}
PHI_SEAL = {PHI_75}
QUANTUM_SIGNATURE = "{quantum_sig}"
CLOAK_N = {cloak_n}
GENESIS_BLOCK = "{genesis_block}"
GENERATION = {self.generation}
POPULATION = {len(self.nodes)}

class Gate:
    """Living logic gate"""
    def __init__(self, type, in1, in2):
        self.type = type
        self.in1 = in1
        self.in2 = in2
        self.state = 0
    
    def compute(self, inputs):
        v1 = inputs[self.in1] if self.in1 < len(inputs) else 0
        v2 = inputs[self.in2] if self.in2 < len(inputs) else 0
        
        if self.type == 0: self.state = v1 & v2
        elif self.type == 1: self.state = v1 | v2
        elif self.type == 2: self.state = v1 ^ v2
        elif self.type == 3: self.state = 1 if not (v1 & v2) else 0
        
        return self.state

class LivingCircuit:
    """Living logic circuit with DNA and brain"""
    def __init__(self, dna, gates):
        self.dna = dna
        self.gates = gates
        self.age = 0
        self.size = 10.0
        self.pulse = 0
    
    def update(self):
        self.age += 1
        t = time.time()
        self.pulse = math.sin(self.dna['harmonic_frequency'] * t * 0.0001) * self.dna['resonance']
        self.size = 10.0 + self.pulse * 5
        self.dna['harmonic_frequency'] += self.dna['evolution']
        if self.dna['harmonic_frequency'] > 528:
            self.dna['harmonic_frequency'] = 432
    
    def compute(self, inputs):
        return [gate.compute(inputs) for gate in self.gates]
    
    def can_reproduce(self):
        return self.size > 15

# Living circuits
circuits = [
'''
        
        # Add evolved circuits
        for i, node in enumerate(best_nodes):
            code += f"    # Circuit {i+1} - Freq: {node.dna['harmonic_frequency']:.2f} Hz\n"
            code += f"    LivingCircuit(\n"
            code += f"        dna={node.dna},\n"
            code += f"        gates={node.brain.to_code()}\n"
            code += f"    ),\n"
        
        code += f''']

def process():
    """Process with living circuits"""
    print(f"LIVING CODE - Generation {{GENERATION}}")
    print(f"Quantum Signature: {{QUANTUM_SIGNATURE}}")
    print(f"Cloaking N: {{CLOAK_N}}")
    print()
    
    for i, circuit in enumerate(circuits):
        circuit.update()
        bits = [random.randint(0, 1) for _ in range(8)]
        outputs = circuit.compute(bits)
        print(f"Circuit {{i+1}}: Freq={{circuit.dna['harmonic_frequency']:.2f}}Hz → {{outputs}}")
    
    return {{'generation': GENERATION, 'circuits': len(circuits)}}

if __name__ == "__main__":
    result = process()
    print(f"\\nExecution complete. Generation {{GENERATION}}")
'''
        
        return code


def demo():
    """Demonstrate living code generation"""
    print("=" * 60)
    print("LIVING CODE GENERATOR")
    print("=" * 60)
    
    generator = LivingCodeGenerator()
    print(f"Created {len(generator.nodes)} living nodes")
    
    generator.evolve_population(cycles=10)
    print(f"Evolved to {len(generator.nodes)} nodes")
    
    code = generator.generate_living_code("autonomous computation")
    
    filename = f"living_code_gen_{int(time.time())}.py"
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(code)
    
    print(f"Generated {len(code)} chars → {filename}")
    print("=" * 60)


if __name__ == "__main__":
    demo()
