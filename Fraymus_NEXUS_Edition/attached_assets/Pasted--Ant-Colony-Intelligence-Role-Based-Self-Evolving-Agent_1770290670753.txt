# Ant Colony Intelligence: Role-Based Self-Evolving Agents with Coach
# Author: Vaughn Scott
# Date: February 1, 2026
#
# Like ants with specialized roles:
# - Logic Gate Agents: AND, OR, XOR, NAND, NOR (the foundation)
# - Math Agents: arithmetic, algebra, calculus
# - Circuit Agents: combine gates into useful structures
# - Memory Agents: store and retrieve patterns
# - Communication Agents: coordinate between roles
#
# Behaviors:
# - REPLICATE: Create copies of successful agents
# - MUTATE: Evolve and improve
# - CONCENTRATE: Focus on specialization
#
# COACH: Oversees, evaluates, directs all agents

import os
import sys
import json
import time
import hashlib
import sqlite3
import random
import copy
from datetime import datetime
from typing import Dict, List, Any, Optional, Callable, Tuple
from dataclasses import dataclass, field
from abc import ABC, abstractmethod
import threading

# φψΩξλζ Constants
PHI = 1.6180339887498948482
PSI = 1.3247179572447460260
OMEGA = 0.5671432904097838730
XI = 2.7182818284590452354
ZETA = 1.2020569031595942854

# Database for colony memory
COLONY_DB = "ant_colony.db"


# ═══════════════════════════════════════════════════════════════════════════
# LOGIC GATES - The Foundation of Computation
# ═══════════════════════════════════════════════════════════════════════════

class LogicGate:
    """Base logic gate - the atomic unit of computation"""
    
    @staticmethod
    def AND(a: bool, b: bool) -> bool:
        return a and b
    
    @staticmethod
    def OR(a: bool, b: bool) -> bool:
        return a or b
    
    @staticmethod
    def NOT(a: bool) -> bool:
        return not a
    
    @staticmethod
    def XOR(a: bool, b: bool) -> bool:
        return a != b
    
    @staticmethod
    def NAND(a: bool, b: bool) -> bool:
        return not (a and b)
    
    @staticmethod
    def NOR(a: bool, b: bool) -> bool:
        return not (a or b)
    
    @staticmethod
    def XNOR(a: bool, b: bool) -> bool:
        return a == b


# ═══════════════════════════════════════════════════════════════════════════
# BASE ANT AGENT - Abstract class for all role-based agents
# ═══════════════════════════════════════════════════════════════════════════

@dataclass
class AntDNA:
    """Genetic code of an ant - determines behavior and specialization"""
    role: str
    generation: int = 0
    mutation_rate: float = 0.1
    concentration: float = 0.5  # How focused on role (0-1)
    fitness: float = 0.0
    energy: float = 100.0
    code_fragments: List[str] = field(default_factory=list)
    successful_operations: int = 0
    failed_operations: int = 0


class AntAgent(ABC):
    """
    Base class for all ant agents.
    Each ant has a role and can: REPLICATE, MUTATE, CONCENTRATE
    """
    
    def __init__(self, role: str, colony=None):
        self.id = f"{role}_{hashlib.sha256(str(time.time()).encode()).hexdigest()[:8]}"
        self.role = role
        self.colony = colony
        self.dna = AntDNA(role=role)
        self.output_buffer: List[Any] = []
        self.code_evolved: List[str] = []
        self.alive = True
        
    @abstractmethod
    def work(self, inputs: Any) -> Any:
        """Perform the agent's specialized work"""
        pass
    
    @abstractmethod
    def generate_code(self) -> str:
        """Generate code fragment based on role"""
        pass
    
    def replicate(self) -> 'AntAgent':
        """
        REPLICATE: Create a copy of this agent.
        Offspring inherits DNA with small mutations.
        """
        # Create new agent of same type
        offspring = self.__class__(self.role, self.colony)
        offspring.dna = copy.deepcopy(self.dna)
        offspring.dna.generation = self.dna.generation + 1
        
        # Small mutation during replication
        if random.random() < 0.1:
            offspring.mutate()
        
        # Energy cost for replication
        self.dna.energy -= 20
        offspring.dna.energy = 50
        
        return offspring
    
    def mutate(self):
        """
        MUTATE: Evolve and improve.
        Changes DNA parameters slightly.
        """
        # Mutate concentration
        self.dna.concentration += random.gauss(0, 0.1)
        self.dna.concentration = max(0.1, min(1.0, self.dna.concentration))
        
        # Mutate mutation rate itself (meta-mutation)
        self.dna.mutation_rate += random.gauss(0, 0.02)
        self.dna.mutation_rate = max(0.01, min(0.5, self.dna.mutation_rate))
        
        # Increment generation
        self.dna.generation += 1
    
    def concentrate(self):
        """
        CONCENTRATE: Focus more on specialization.
        Increases concentration, improves role performance.
        """
        self.dna.concentration = min(1.0, self.dna.concentration + 0.1)
        self.dna.energy -= 5  # Concentration takes energy
    
    def record_success(self):
        """Record a successful operation"""
        self.dna.successful_operations += 1
        self.dna.fitness = self.dna.successful_operations / max(1, self.dna.successful_operations + self.dna.failed_operations)
        self.dna.energy += 5  # Success gives energy
    
    def record_failure(self):
        """Record a failed operation"""
        self.dna.failed_operations += 1
        self.dna.fitness = self.dna.successful_operations / max(1, self.dna.successful_operations + self.dna.failed_operations)
        self.dna.energy -= 2  # Failure costs energy
    
    def is_fit_to_replicate(self) -> bool:
        """Check if agent is fit enough to replicate"""
        return self.dna.fitness > 0.6 and self.dna.energy > 40
    
    def should_die(self) -> bool:
        """Check if agent should die (low energy/fitness)"""
        return self.dna.energy <= 0 or (self.dna.fitness < 0.2 and self.dna.generation > 5)
    
    def get_stats(self) -> Dict:
        return {
            "id": self.id,
            "role": self.role,
            "generation": self.dna.generation,
            "fitness": self.dna.fitness,
            "energy": self.dna.energy,
            "concentration": self.dna.concentration,
            "operations": self.dna.successful_operations + self.dna.failed_operations
        }


# ═══════════════════════════════════════════════════════════════════════════
# SPECIALIZED ANT AGENTS
# ═══════════════════════════════════════════════════════════════════════════

class LogicGateAnt(AntAgent):
    """
    Logic Gate Agent - handles boolean operations.
    The foundation of all computation.
    """
    
    GATES = ['AND', 'OR', 'NOT', 'XOR', 'NAND', 'NOR', 'XNOR']
    
    def __init__(self, gate_type: str = None, colony=None):
        self.gate_type = gate_type or random.choice(self.GATES)
        super().__init__(f"logic_{self.gate_type}", colony)
        self.gate = getattr(LogicGate, self.gate_type)
    
    def work(self, inputs: Tuple[bool, ...]) -> bool:
        """Perform logic gate operation"""
        try:
            if self.gate_type == 'NOT':
                result = self.gate(inputs[0])
            else:
                result = self.gate(inputs[0], inputs[1])
            self.record_success()
            return result
        except Exception as e:
            self.record_failure()
            return False
    
    def generate_code(self) -> str:
        """Generate code for this logic gate"""
        gen = self.dna.generation
        gate = self.gate_type
        
        if gate == 'NOT':
            code = f'''
def logic_{gate.lower()}_{gen}(a):
    """Logic {gate} gate - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return not a
'''
        elif gate == 'AND':
            code = f'''
def logic_{gate.lower()}_{gen}(a, b):
    """Logic {gate} gate - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a and b
'''
        elif gate == 'OR':
            code = f'''
def logic_{gate.lower()}_{gen}(a, b):
    """Logic {gate} gate - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a or b
'''
        elif gate == 'XOR':
            code = f'''
def logic_{gate.lower()}_{gen}(a, b):
    """Logic {gate} gate - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a != b
'''
        elif gate == 'NAND':
            code = f'''
def logic_{gate.lower()}_{gen}(a, b):
    """Logic {gate} gate - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return not (a and b)
'''
        elif gate == 'NOR':
            code = f'''
def logic_{gate.lower()}_{gen}(a, b):
    """Logic {gate} gate - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return not (a or b)
'''
        else:  # XNOR
            code = f'''
def logic_{gate.lower()}_{gen}(a, b):
    """Logic {gate} gate - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a == b
'''
        self.code_evolved.append(code)
        return code
    
    def replicate(self) -> 'LogicGateAnt':
        """Replicate with possible gate mutation"""
        offspring = LogicGateAnt(self.gate_type, self.colony)
        offspring.dna = copy.deepcopy(self.dna)
        offspring.dna.generation = self.dna.generation + 1
        
        # Possible mutation to different gate type
        if random.random() < self.dna.mutation_rate:
            offspring.gate_type = random.choice(self.GATES)
            offspring.gate = getattr(LogicGate, offspring.gate_type)
            offspring.role = f"logic_{offspring.gate_type}"
        
        self.dna.energy -= 20
        offspring.dna.energy = 50
        return offspring


class MathAnt(AntAgent):
    """
    Math Agent - handles mathematical operations.
    Arithmetic, algebra, calculus patterns.
    """
    
    OPERATIONS = ['add', 'subtract', 'multiply', 'divide', 'power', 'sqrt', 'phi_transform']
    
    def __init__(self, operation: str = None, colony=None):
        self.operation = operation or random.choice(self.OPERATIONS)
        super().__init__(f"math_{self.operation}", colony)
    
    def work(self, inputs: Tuple[float, ...]) -> float:
        """Perform mathematical operation"""
        try:
            a = inputs[0]
            b = inputs[1] if len(inputs) > 1 else 1
            
            if self.operation == 'add':
                result = a + b
            elif self.operation == 'subtract':
                result = a - b
            elif self.operation == 'multiply':
                result = a * b
            elif self.operation == 'divide':
                result = a / b if b != 0 else 0
            elif self.operation == 'power':
                result = a ** min(b, 10)  # Limit power
            elif self.operation == 'sqrt':
                result = a ** 0.5 if a >= 0 else 0
            elif self.operation == 'phi_transform':
                result = a * PHI + b / PHI
            else:
                result = a
            
            self.record_success()
            return result
        except Exception as e:
            self.record_failure()
            return 0.0
    
    def generate_code(self) -> str:
        """Generate code for this math operation"""
        gen = self.dna.generation
        op = self.operation
        
        if op == 'add':
            code = f'''
def math_add_{gen}(a, b):
    """Math ADD - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a + b
'''
        elif op == 'subtract':
            code = f'''
def math_subtract_{gen}(a, b):
    """Math SUBTRACT - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a - b
'''
        elif op == 'multiply':
            code = f'''
def math_multiply_{gen}(a, b):
    """Math MULTIPLY - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a * b
'''
        elif op == 'divide':
            code = f'''
def math_divide_{gen}(a, b):
    """Math DIVIDE - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a / b if b != 0 else 0
'''
        elif op == 'power':
            code = f'''
def math_power_{gen}(a, b):
    """Math POWER - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a ** min(b, 10)
'''
        elif op == 'sqrt':
            code = f'''
def math_sqrt_{gen}(a):
    """Math SQRT - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return a ** 0.5 if a >= 0 else 0
'''
        else:  # phi_transform
            code = f'''
def math_phi_transform_{gen}(a, b=1):
    """Math PHI_TRANSFORM - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    PHI = 1.618033988749
    return a * PHI + b / PHI
'''
        self.code_evolved.append(code)
        return code
    
    def replicate(self) -> 'MathAnt':
        offspring = MathAnt(self.operation, self.colony)
        offspring.dna = copy.deepcopy(self.dna)
        offspring.dna.generation = self.dna.generation + 1
        
        if random.random() < self.dna.mutation_rate:
            offspring.operation = random.choice(self.OPERATIONS)
            offspring.role = f"math_{offspring.operation}"
        
        self.dna.energy -= 20
        offspring.dna.energy = 50
        return offspring


class CircuitAnt(AntAgent):
    """
    Circuit Agent - combines logic gates into useful circuits.
    Half adder, full adder, multiplexer, etc.
    """
    
    CIRCUITS = ['half_adder', 'full_adder', 'multiplexer', 'decoder', 'comparator']
    
    def __init__(self, circuit_type: str = None, colony=None):
        self.circuit_type = circuit_type or random.choice(self.CIRCUITS)
        super().__init__(f"circuit_{self.circuit_type}", colony)
        self.logic_gates = {}  # Cache of logic gate ants
    
    def work(self, inputs: Tuple[bool, ...]) -> Dict[str, bool]:
        """Build and execute circuit"""
        try:
            if self.circuit_type == 'half_adder':
                # Half adder: Sum = A XOR B, Carry = A AND B
                a, b = inputs[0], inputs[1]
                sum_bit = LogicGate.XOR(a, b)
                carry = LogicGate.AND(a, b)
                result = {"sum": sum_bit, "carry": carry}
            
            elif self.circuit_type == 'full_adder':
                # Full adder: A, B, Cin -> Sum, Cout
                a, b = inputs[0], inputs[1]
                cin = inputs[2] if len(inputs) > 2 else False
                
                xor1 = LogicGate.XOR(a, b)
                sum_bit = LogicGate.XOR(xor1, cin)
                and1 = LogicGate.AND(a, b)
                and2 = LogicGate.AND(xor1, cin)
                cout = LogicGate.OR(and1, and2)
                result = {"sum": sum_bit, "carry_out": cout}
            
            elif self.circuit_type == 'multiplexer':
                # 2-to-1 MUX: sel=0 -> a, sel=1 -> b
                a, b = inputs[0], inputs[1]
                sel = inputs[2] if len(inputs) > 2 else False
                
                not_sel = LogicGate.NOT(sel)
                and1 = LogicGate.AND(a, not_sel)
                and2 = LogicGate.AND(b, sel)
                out = LogicGate.OR(and1, and2)
                result = {"output": out, "selected": 1 if sel else 0}
            
            elif self.circuit_type == 'comparator':
                # A > B, A = B, A < B
                a, b = inputs[0], inputs[1]
                eq = LogicGate.XNOR(a, b)
                a_and_notb = LogicGate.AND(a, LogicGate.NOT(b))
                result = {"equal": eq, "a_greater": a_and_notb, "b_greater": LogicGate.AND(b, LogicGate.NOT(a))}
            
            else:  # decoder
                # 1-to-2 decoder
                a = inputs[0]
                result = {"out0": LogicGate.NOT(a), "out1": a}
            
            self.record_success()
            return result
        except Exception as e:
            self.record_failure()
            return {"error": True}
    
    def generate_code(self) -> str:
        """Generate code for this circuit"""
        gen = self.dna.generation
        circuit = self.circuit_type
        
        if circuit == 'half_adder':
            code = f'''
def circuit_half_adder_{gen}(a, b):
    """Half Adder Circuit - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    sum_bit = a != b  # XOR
    carry = a and b   # AND
    return {{"sum": sum_bit, "carry": carry}}
'''
        elif circuit == 'full_adder':
            code = f'''
def circuit_full_adder_{gen}(a, b, cin=False):
    """Full Adder Circuit - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    xor1 = a != b
    sum_bit = xor1 != cin
    cout = (a and b) or (xor1 and cin)
    return {{"sum": sum_bit, "carry_out": cout}}
'''
        elif circuit == 'multiplexer':
            code = f'''
def circuit_mux_{gen}(a, b, sel):
    """2-to-1 Multiplexer - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return b if sel else a
'''
        elif circuit == 'comparator':
            code = f'''
def circuit_comparator_{gen}(a, b):
    """Comparator Circuit - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return {{"equal": a == b, "a_greater": a and not b, "b_greater": b and not a}}
'''
        else:  # decoder
            code = f'''
def circuit_decoder_{gen}(a):
    """1-to-2 Decoder - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    return {{"out0": not a, "out1": a}}
'''
        self.code_evolved.append(code)
        return code
    
    def replicate(self) -> 'CircuitAnt':
        offspring = CircuitAnt(self.circuit_type, self.colony)
        offspring.dna = copy.deepcopy(self.dna)
        offspring.dna.generation = self.dna.generation + 1
        
        if random.random() < self.dna.mutation_rate:
            offspring.circuit_type = random.choice(self.CIRCUITS)
            offspring.role = f"circuit_{offspring.circuit_type}"
        
        self.dna.energy -= 20
        offspring.dna.energy = 50
        return offspring


class MemoryAnt(AntAgent):
    """
    Memory Agent - stores and retrieves patterns.
    Like a register or cache.
    """
    
    def __init__(self, role=None, colony=None):
        super().__init__("memory", colony)
        self.storage: Dict[str, Any] = {}
        self.access_count: Dict[str, int] = {}
    
    def work(self, inputs: Tuple[str, Any]) -> Any:
        """Store or retrieve from memory"""
        try:
            operation, key = inputs[0], inputs[1]
            
            if operation == 'store':
                value = inputs[2] if len(inputs) > 2 else None
                self.storage[key] = value
                self.access_count[key] = 0
                self.record_success()
                return {"stored": key, "value": value}
            
            elif operation == 'retrieve':
                if key in self.storage:
                    self.access_count[key] = self.access_count.get(key, 0) + 1
                    self.record_success()
                    return {"retrieved": key, "value": self.storage[key]}
                else:
                    self.record_failure()
                    return {"error": "key not found"}
            
            elif operation == 'delete':
                if key in self.storage:
                    del self.storage[key]
                    self.record_success()
                    return {"deleted": key}
                self.record_failure()
                return {"error": "key not found"}
            
            self.record_failure()
            return {"error": "unknown operation"}
        except Exception as e:
            self.record_failure()
            return {"error": str(e)}
    
    def generate_code(self) -> str:
        """Generate memory access code"""
        gen = self.dna.generation
        code = f'''
class Memory_{gen}:
    """Memory Unit - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    def __init__(self):
        self.storage = {{}}
    
    def store(self, key, value):
        self.storage[key] = value
        return True
    
    def retrieve(self, key):
        return self.storage.get(key)
    
    def delete(self, key):
        if key in self.storage:
            del self.storage[key]
            return True
        return False
'''
        self.code_evolved.append(code)
        return code


class AssemblerAnt(AntAgent):
    """
    ASSEMBLER Agent - understands LOW-LEVEL LANGUAGE
    Converts assembly mnemonics to machine code
    Learned from user's ai_8bit_gui.py
    """
    
    def __init__(self, role=None, colony=None):
        super().__init__("assembler", colony)
        self.opcode_map = {
            "NOP": 0x00, "LOAD_A": 0x01, "LOAD_B": 0x02, "ADD": 0x03,
            "SUB": 0x04, "MULTIPLY": 0x05, "STORE": 0x06, "OUT": 0x07,
            "JMP": 0x08, "JZ": 0x09, "JC": 0x0A, "HALT": 0x0F,
            "LDA": 0x10, "LDB": 0x11, "STA": 0x12, "STB": 0x13,
            "AND": 0x20, "OR": 0x21, "XOR": 0x22, "NOT": 0x23,
            "SHL": 0x30, "SHR": 0x31, "INC": 0x32, "DEC": 0x33,
        }
        self.programs_assembled: List[Dict] = []
    
    def work(self, inputs: Tuple = None) -> Any:
        """Assemble source code to machine code"""
        try:
            if inputs and len(inputs) > 0:
                source = inputs[0] if isinstance(inputs[0], list) else [inputs[0]]
                machine_code = self.assemble(source)
                self.record_success()
                return machine_code
            else:
                # Generate a sample program
                program = self.generate_program()
                self.record_success()
                return program
        except Exception as e:
            self.record_failure()
            return {"error": str(e)}
    
    def assemble(self, source_code: List[str]) -> List[int]:
        """Convert assembly to machine code"""
        machine_code = []
        labels = {}
        
        # First pass: find labels
        addr = 0
        for line in source_code:
            line = line.strip()
            if not line or line.startswith(';'):
                continue
            if line.endswith(':'):
                labels[line[:-1]] = addr
            else:
                parts = line.split()
                addr += 1
                if len(parts) > 1:
                    addr += 1
        
        # Second pass: generate code
        for line in source_code:
            line = line.strip()
            if not line or line.startswith(';') or line.endswith(':'):
                continue
            
            parts = line.split()
            mnemonic = parts[0].upper()
            
            if mnemonic in self.opcode_map:
                machine_code.append(self.opcode_map[mnemonic])
                if len(parts) > 1:
                    operand = parts[1]
                    if operand in labels:
                        machine_code.append(labels[operand])
                    else:
                        machine_code.append(int(operand, 0))
        
        self.programs_assembled.append({
            "source": source_code,
            "machine_code": machine_code,
            "labels": labels
        })
        return machine_code
    
    def generate_program(self) -> Dict:
        """Generate a sample assembly program"""
        import random
        programs = [
            {
                "name": "add_two_numbers",
                "source": ["LOAD_A 100", "LOAD_B 101", "ADD", "STORE 102", "OUT", "HALT"],
                "data": {100: 25, 101: 17}
            },
            {
                "name": "multiply",
                "source": ["LOAD_A 100", "LOAD_B 101", "MULTIPLY", "OUT", "HALT"],
                "data": {100: 6, 101: 7}
            },
            {
                "name": "count_loop",
                "source": ["LOAD_A 100", "loop:", "OUT", "INC", "JZ end", "JMP loop", "end:", "HALT"],
                "data": {100: 1}
            },
            {
                "name": "logic_test",
                "source": ["LOAD_A 100", "LOAD_B 101", "AND", "OUT", "LOAD_A 100", "LOAD_B 101", "OR", "OUT", "HALT"],
                "data": {100: 0b10101010, 101: 0b11001100}
            }
        ]
        return random.choice(programs)
    
    def generate_code(self) -> str:
        """Generate Python code for assembler"""
        gen = self.dna.generation
        code = f'''
class Assembler_{gen}:
    """Assembly Language Processor - Generation {gen}
    Converts human-readable mnemonics to machine code
    """
    OPCODES = {{
        "NOP": 0x00, "LOAD_A": 0x01, "LOAD_B": 0x02, "ADD": 0x03,
        "SUB": 0x04, "MULTIPLY": 0x05, "STORE": 0x06, "OUT": 0x07,
        "JMP": 0x08, "JZ": 0x09, "JC": 0x0A, "HALT": 0x0F,
        "AND": 0x20, "OR": 0x21, "XOR": 0x22, "NOT": 0x23,
    }}
    
    def assemble(self, source):
        machine_code = []
        for line in source:
            parts = line.strip().split()
            if not parts:
                continue
            mnemonic = parts[0].upper()
            if mnemonic in self.OPCODES:
                machine_code.append(self.OPCODES[mnemonic])
                if len(parts) > 1:
                    machine_code.append(int(parts[1]))
        return machine_code
    
    def disassemble(self, machine_code):
        reverse_map = {{v: k for k, v in self.OPCODES.items()}}
        assembly = []
        i = 0
        while i < len(machine_code):
            opcode = machine_code[i]
            if opcode in reverse_map:
                mnemonic = reverse_map[opcode]
                if mnemonic in ["LOAD_A", "LOAD_B", "STORE", "JMP", "JZ", "JC"]:
                    assembly.append(f"{{mnemonic}} {{machine_code[i+1]}}")
                    i += 2
                else:
                    assembly.append(mnemonic)
                    i += 1
            else:
                assembly.append(f"DB {{opcode}}")
                i += 1
        return assembly
'''
        self.code_evolved.append(code)
        return code


class ComputerArchitectAnt(AntAgent):
    """
    Builds REAL computer components like Ben Eater's breadboard computer:
    - Clock module
    - Program Counter
    - RAM
    - Control Unit
    - Bus
    - Instruction Register
    """
    
    def __init__(self, component_type="clock", colony=None):
        # Strip any existing "computer_" prefix to prevent stacking
        clean_type = component_type
        while clean_type.startswith("computer_"):
            clean_type = clean_type[9:]  # len("computer_") = 9
        super().__init__(f"computer_{clean_type}", colony)
        self.component_type = clean_type
        self.built_components: List[Dict] = []
    
    def work(self, inputs: Tuple = None) -> Any:
        """Build computer components"""
        try:
            if self.component_type == "clock":
                result = self._build_clock()
            elif self.component_type == "program_counter":
                result = self._build_program_counter()
            elif self.component_type == "ram":
                result = self._build_ram()
            elif self.component_type == "control_unit":
                result = self._build_control_unit()
            elif self.component_type == "bus":
                result = self._build_bus()
            elif self.component_type == "instruction_register":
                result = self._build_instruction_register()
            elif self.component_type == "cpu":
                result = self._build_cpu()
            else:
                result = {"error": "unknown component"}
            
            self.built_components.append(result)
            self.record_success()
            return result
        except Exception as e:
            self.record_failure()
            return {"error": str(e)}
    
    def _build_clock(self) -> Dict:
        """555 timer style clock - generates timing signals"""
        return {
            "type": "CLOCK",
            "frequency": "variable",
            "outputs": ["CLK", "CLK_INV"],
            "built_from": ["NOT", "NOT", "capacitor_sim"],
            "description": "Generates clock pulses for synchronization"
        }
    
    def _build_program_counter(self) -> Dict:
        """4-bit program counter using JK flip-flops"""
        return {
            "type": "PROGRAM_COUNTER",
            "bits": 4,
            "operations": ["COUNT", "LOAD", "RESET"],
            "built_from": ["JK_flipflop", "JK_flipflop", "JK_flipflop", "JK_flipflop"],
            "outputs": ["PC0", "PC1", "PC2", "PC3"],
            "description": "Points to current instruction address"
        }
    
    def _build_ram(self) -> Dict:
        """16 bytes RAM using SR latches"""
        return {
            "type": "RAM",
            "size": "16 bytes",
            "address_bits": 4,
            "data_bits": 8,
            "built_from": ["SR_latch"] * 128,  # 16 * 8 bits
            "operations": ["READ", "WRITE"],
            "description": "Stores program and data"
        }
    
    def _build_control_unit(self) -> Dict:
        """Microcode-based control unit"""
        return {
            "type": "CONTROL_UNIT",
            "microcode_steps": 5,
            "control_signals": ["HLT", "MI", "RI", "RO", "IO", "II", "AI", "AO", 
                               "EO", "SU", "BI", "OI", "CE", "CO", "J"],
            "built_from": ["decoder", "EEPROM_sim", "counter"],
            "description": "Decodes instructions and generates control signals"
        }
    
    def _build_bus(self) -> Dict:
        """8-bit shared data bus"""
        return {
            "type": "BUS",
            "width": 8,
            "connected": ["ALU", "RAM", "A_REG", "B_REG", "IR", "PC", "OUTPUT"],
            "built_from": ["tri_state_buffer"] * 8,
            "description": "Shared communication pathway"
        }
    
    def _build_instruction_register(self) -> Dict:
        """8-bit instruction register"""
        return {
            "type": "INSTRUCTION_REGISTER",
            "bits": 8,
            "fields": {"opcode": 4, "operand": 4},
            "built_from": ["D_flipflop"] * 8,
            "description": "Holds current instruction"
        }
    
    def _build_cpu(self) -> Dict:
        """Complete 8-bit CPU from components"""
        return {
            "type": "CPU",
            "architecture": "8-bit",
            "components": [
                "CLOCK", "PROGRAM_COUNTER", "RAM", "INSTRUCTION_REGISTER",
                "CONTROL_UNIT", "A_REGISTER", "B_REGISTER", "ALU", "BUS", "OUTPUT"
            ],
            "instruction_set": [
                {"NOP": 0x0, "desc": "No operation"},
                {"LDA": 0x1, "desc": "Load A from address"},
                {"ADD": 0x2, "desc": "Add to A"},
                {"SUB": 0x3, "desc": "Subtract from A"},
                {"STA": 0x4, "desc": "Store A to address"},
                {"LDI": 0x5, "desc": "Load immediate to A"},
                {"JMP": 0x6, "desc": "Jump to address"},
                {"JC": 0x7, "desc": "Jump if carry"},
                {"JZ": 0x8, "desc": "Jump if zero"},
                {"OUT": 0xE, "desc": "Output A"},
                {"HLT": 0xF, "desc": "Halt"}
            ],
            "description": "Ben Eater style 8-bit breadboard computer"
        }
    
    def generate_code(self) -> str:
        """Generate Python code for the component"""
        gen = self.dna.generation
        
        if self.component_type == "clock":
            code = f'''
class Clock_{gen}:
    """Clock Module - Generation {gen}
    Generates timing pulses like 555 timer
    """
    def __init__(self):
        self.state = False
        self.halted = False
    
    def tick(self):
        if not self.halted:
            self.state = not self.state
        return self.state
    
    def halt(self):
        self.halted = True
'''
        elif self.component_type == "program_counter":
            code = f'''
class ProgramCounter_{gen}:
    """4-bit Program Counter - Generation {gen}
    Points to next instruction
    """
    def __init__(self):
        self.value = 0
    
    def increment(self):
        self.value = (self.value + 1) & 0xF
        return self.value
    
    def load(self, addr):
        self.value = addr & 0xF
        return self.value
    
    def reset(self):
        self.value = 0
'''
        elif self.component_type == "ram":
            code = f'''
class RAM_{gen}:
    """16-byte RAM - Generation {gen}
    Stores program and data
    """
    def __init__(self):
        self.memory = [0] * 16
    
    def read(self, addr):
        return self.memory[addr & 0xF]
    
    def write(self, addr, data):
        self.memory[addr & 0xF] = data & 0xFF
'''
        elif self.component_type == "control_unit":
            code = f'''
class ControlUnit_{gen}:
    """Microcode Control Unit - Generation {gen}
    Decodes instructions, generates control signals
    """
    MICROCODE = {{
        0x0: [0],  # NOP
        0x1: ["CO|MI", "RO|II|CE", "IO|MI", "RO|AI"],  # LDA
        0x2: ["CO|MI", "RO|II|CE", "IO|MI", "RO|BI", "EO|AI"],  # ADD
        0x3: ["CO|MI", "RO|II|CE", "IO|MI", "RO|BI", "EO|AI|SU"],  # SUB
        0xE: ["CO|MI", "RO|II|CE", "AO|OI"],  # OUT
        0xF: ["CO|MI", "RO|II|CE", "HLT"],  # HLT
    }}
    
    def decode(self, opcode, step):
        if opcode in self.MICROCODE:
            steps = self.MICROCODE[opcode]
            if step < len(steps):
                return steps[step]
        return 0
'''
        elif self.component_type == "cpu":
            code = f'''
class CPU_{gen}:
    """8-bit CPU - Generation {gen}
    Ben Eater style breadboard computer
    Components: Clock, PC, RAM, IR, Control, ALU, Registers, Bus
    """
    def __init__(self):
        self.pc = 0          # Program counter
        self.a_reg = 0       # A register
        self.b_reg = 0       # B register
        self.ir = 0          # Instruction register
        self.ram = [0] * 16  # 16 bytes RAM
        self.output = 0      # Output register
        self.halted = False
        self.carry = False
        self.zero = False
    
    def load_program(self, program):
        for i, byte in enumerate(program[:16]):
            self.ram[i] = byte
    
    def fetch(self):
        self.ir = self.ram[self.pc & 0xF]
        self.pc = (self.pc + 1) & 0xF
    
    def execute(self):
        opcode = (self.ir >> 4) & 0xF
        operand = self.ir & 0xF
        
        if opcode == 0x0:    # NOP
            pass
        elif opcode == 0x1:  # LDA
            self.a_reg = self.ram[operand]
        elif opcode == 0x2:  # ADD
            result = self.a_reg + self.ram[operand]
            self.carry = result > 255
            self.a_reg = result & 0xFF
            self.zero = self.a_reg == 0
        elif opcode == 0x3:  # SUB
            result = self.a_reg - self.ram[operand]
            self.carry = result < 0
            self.a_reg = result & 0xFF
            self.zero = self.a_reg == 0
        elif opcode == 0x4:  # STA
            self.ram[operand] = self.a_reg
        elif opcode == 0x5:  # LDI
            self.a_reg = operand
        elif opcode == 0x6:  # JMP
            self.pc = operand
        elif opcode == 0x7:  # JC
            if self.carry:
                self.pc = operand
        elif opcode == 0x8:  # JZ
            if self.zero:
                self.pc = operand
        elif opcode == 0xE:  # OUT
            self.output = self.a_reg
            return self.output
        elif opcode == 0xF:  # HLT
            self.halted = True
        return None
    
    def run(self):
        outputs = []
        while not self.halted:
            self.fetch()
            result = self.execute()
            if result is not None:
                outputs.append(result)
            if self.pc > 15:
                break
        return outputs
'''
        else:
            code = f"# {self.component_type} - Generation {gen}"
        
        self.code_evolved.append(code)
        return code


class ArchitectAnt(AntAgent):
    """
    Architect Agent - COMBINES fragments into higher structures.
    Takes logic gates and builds ALUs.
    Takes circuits and builds processors.
    INTERPRETS what combinations mean.
    """
    
    def __init__(self, role=None, colony=None):
        super().__init__("architect", colony)
        self.blueprints: Dict[str, str] = {}
        self.built_structures: List[Dict] = []
    
    def work(self, inputs: Tuple[str, ...]) -> Any:
        """Combine fragments into higher structures"""
        try:
            action = inputs[0]
            
            if action == 'build_alu':
                # Combine half_adder + full_adder into ALU
                alu = self._build_alu()
                self.built_structures.append(alu)
                self.record_success()
                return alu
            
            elif action == 'build_register':
                # Combine logic gates into register
                reg = self._build_register()
                self.built_structures.append(reg)
                self.record_success()
                return reg
            
            elif action == 'interpret':
                # Interpret what a combination does
                structure = inputs[1] if len(inputs) > 1 else None
                interpretation = self._interpret(structure)
                self.record_success()
                return interpretation
            
            elif action == 'compose':
                # Compose multiple fragments
                fragments = inputs[1] if len(inputs) > 1 else []
                composed = self._compose(fragments)
                self.record_success()
                return composed
            
            self.record_failure()
            return {"error": "unknown action"}
        except Exception as e:
            self.record_failure()
            return {"error": str(e)}
    
    def _build_alu(self) -> Dict:
        """Build an ALU from adders"""
        return {
            "type": "ALU",
            "components": ["half_adder", "full_adder", "multiplexer", "comparator"],
            "operations": ["ADD", "SUB", "AND", "OR", "XOR", "NOT", "CMP"],
            "built_by": self.id,
            "generation": self.dna.generation
        }
    
    def _build_register(self) -> Dict:
        """Build a register from gates"""
        return {
            "type": "REGISTER",
            "components": ["NAND", "NAND", "NAND", "NAND"],  # SR latch from NANDs
            "bits": 8,
            "built_by": self.id
        }
    
    def _interpret(self, structure) -> Dict:
        """Interpret what a structure does"""
        interpretations = {
            "half_adder": "Adds two 1-bit numbers, outputs sum and carry",
            "full_adder": "Adds two 1-bit numbers with carry-in, outputs sum and carry-out",
            "ALU": "Arithmetic Logic Unit - performs math and logic on data",
            "REGISTER": "Stores data temporarily - memory cell",
            "multiplexer": "Selects one of many inputs based on select signal",
            "decoder": "Converts binary to one-hot encoding"
        }
        return {
            "structure": structure,
            "meaning": interpretations.get(structure, "Unknown structure"),
            "interpreter": self.id
        }
    
    def _compose(self, fragments: List[str]) -> Dict:
        """Compose fragments into new structure"""
        # Determine what can be built from fragments
        if "half_adder" in fragments and "full_adder" in fragments:
            return {"composed": "RIPPLE_CARRY_ADDER", "from": fragments}
        elif "NAND" in fragments and len([f for f in fragments if f == "NAND"]) >= 4:
            return {"composed": "SR_LATCH", "from": fragments}
        elif "AND" in fragments and "OR" in fragments and "NOT" in fragments:
            return {"composed": "LOGIC_UNIT", "from": fragments}
        else:
            return {"composed": "CUSTOM_CIRCUIT", "from": fragments}
    
    def generate_code(self) -> str:
        """Generate code for built structures"""
        gen = self.dna.generation
        code = f'''
class ALU_{gen}:
    """Arithmetic Logic Unit - Generation {gen}, Fitness {self.dna.fitness:.3f}
    Built by Architect: {self.id}
    Combines: half_adder, full_adder, multiplexer, comparator
    """
    def __init__(self):
        self.operations = ["ADD", "SUB", "AND", "OR", "XOR", "NOT", "CMP"]
    
    def execute(self, op, a, b):
        if op == "ADD": return a + b
        elif op == "SUB": return a - b
        elif op == "AND": return a & b
        elif op == "OR": return a | b
        elif op == "XOR": return a ^ b
        elif op == "NOT": return ~a
        elif op == "CMP": return {{"eq": a==b, "gt": a>b, "lt": a<b}}
        return None

class Register_{gen}:
    """8-bit Register - Generation {gen}
    Built from NAND gates as SR latches
    """
    def __init__(self):
        self.value = 0
    
    def store(self, val):
        self.value = val & 0xFF
        return self.value
    
    def load(self):
        return self.value
'''
        self.code_evolved.append(code)
        return code


class CoordinatorAnt(AntAgent):
    """
    Coordinator Agent - facilitates communication between agents.
    Routes messages, coordinates tasks.
    """
    
    def __init__(self, role=None, colony=None):
        super().__init__("coordinator", colony)
        self.message_queue: List[Dict] = []
        self.routing_table: Dict[str, str] = {}
    
    def work(self, inputs: Tuple[str, ...]) -> Any:
        """Route messages between agents"""
        try:
            action = inputs[0]
            
            if action == 'send':
                target_role, message = inputs[1], inputs[2]
                self.message_queue.append({
                    "to": target_role,
                    "message": message,
                    "timestamp": time.time()
                })
                self.record_success()
                return {"queued": True, "queue_size": len(self.message_queue)}
            
            elif action == 'receive':
                target_role = inputs[1]
                messages = [m for m in self.message_queue if m["to"] == target_role]
                self.message_queue = [m for m in self.message_queue if m["to"] != target_role]
                self.record_success()
                return {"messages": messages}
            
            self.record_failure()
            return {"error": "unknown action"}
        except Exception as e:
            self.record_failure()
            return {"error": str(e)}
    
    def generate_code(self) -> str:
        gen = self.dna.generation
        code = f'''
class Coordinator_{gen}:
    """Message Coordinator - Generation {gen}, Fitness {self.dna.fitness:.3f}"""
    def __init__(self):
        self.queue = []
    
    def send(self, target, message):
        self.queue.append({{"to": target, "msg": message}})
        return True
    
    def receive(self, target):
        msgs = [m for m in self.queue if m["to"] == target]
        self.queue = [m for m in self.queue if m["to"] != target]
        return msgs
'''
        self.code_evolved.append(code)
        return code


# ═══════════════════════════════════════════════════════════════════════════
# THE COACH - Oversees, evaluates, and directs all agents
# ═══════════════════════════════════════════════════════════════════════════

class Coach:
    """
    THE COACH - The overseer of all agents.
    
    Responsibilities:
    - Evaluate agent performance
    - Decide which agents replicate
    - Decide which agents die
    - Direct agents to concentrate
    - Combine agent outputs into unified code
    - Train the colony toward goals
    """
    
    def __init__(self):
        self.colony: List[AntAgent] = []
        self.generation = 0
        self.best_fitness = 0.0
        self.total_code_generated: List[str] = []
        self.statistics: List[Dict] = []
        self.goals: List[str] = []
        self.db_path = COLONY_DB
        
        self._init_database()
        print("\n[COACH] Initialized - Ready to train the colony")
    
    def _init_database(self):
        """Initialize colony database"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS agents (
                id TEXT PRIMARY KEY,
                role TEXT,
                generation INTEGER,
                fitness REAL,
                energy REAL,
                concentration REAL,
                code TEXT,
                timestamp TEXT
            )
        ''')
        
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS evolved_code (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                agent_id TEXT,
                code TEXT,
                fitness REAL,
                generation INTEGER,
                timestamp TEXT
            )
        ''')
        
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS colony_stats (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                generation INTEGER,
                population INTEGER,
                avg_fitness REAL,
                best_fitness REAL,
                total_code INTEGER,
                timestamp TEXT
            )
        ''')
        
        conn.commit()
        conn.close()
    
    def spawn_initial_colony(self, config: Dict = None):
        """Spawn the initial colony with diverse agents"""
        if config is None:
            config = {
                "logic_AND": 2, "logic_OR": 2, "logic_XOR": 2,
                "logic_NAND": 2, "logic_NOR": 1, "logic_NOT": 2,
                "math_add": 2, "math_multiply": 2, "math_phi_transform": 2,
                "circuit_half_adder": 2, "circuit_full_adder": 2,
                "memory": 2, "coordinator": 1,
                "architect": 3,
                # COMPUTER ARCHITECTURE - Ben Eater style
                "computer_clock": 2,
                "computer_program_counter": 2,
                "computer_ram": 2,
                "computer_control_unit": 2,
                "computer_bus": 1,
                "computer_cpu": 3,  # Full 8-bit CPUs
                "assembler": 3  # Assembly language processors
            }
        
        print(f"\n[COACH] Spawning initial colony...")
        
        for agent_type, count in config.items():
            for _ in range(count):
                if agent_type.startswith("logic_"):
                    gate = agent_type.split("_")[1]
                    agent = LogicGateAnt(gate, self)
                elif agent_type.startswith("math_"):
                    op = agent_type.split("_")[1]
                    agent = MathAnt(op, self)
                elif agent_type.startswith("circuit_"):
                    circuit = agent_type.split("_", 1)[1]
                    agent = CircuitAnt(circuit, self)
                elif agent_type == "memory":
                    agent = MemoryAnt(self)
                elif agent_type == "coordinator":
                    agent = CoordinatorAnt(self)
                elif agent_type == "architect":
                    agent = ArchitectAnt(self)
                elif agent_type.startswith("computer_"):
                    comp_type = agent_type.split("_", 1)[1]
                    agent = ComputerArchitectAnt(comp_type, self)
                elif agent_type == "assembler":
                    agent = AssemblerAnt(colony=self)
                else:
                    continue
                
                self.colony.append(agent)
        
        print(f"   [COACH] Colony spawned: {len(self.colony)} agents")
        self._display_colony_composition()
    
    def _display_colony_composition(self):
        """Display current colony composition"""
        roles = {}
        for agent in self.colony:
            role = agent.role
            roles[role] = roles.get(role, 0) + 1
        
        print(f"   [COACH] Colony composition:")
        for role, count in sorted(roles.items()):
            print(f"      - {role}: {count}")
    
    def train_generation(self):
        """
        Run one generation of training.
        Each agent works, evolves, possibly replicates or dies.
        """
        self.generation += 1
        print(f"\n[COACH] === GENERATION {self.generation} ===")
        
        # Phase 1: WORK - Each agent performs its role
        print(f"   [WORK] Agents performing tasks...")
        for agent in self.colony:
            self._assign_work(agent)
        
        # Phase 2: EVALUATE - Coach evaluates performance
        print(f"   [EVAL] Evaluating performance...")
        self._evaluate_colony()
        
        # Phase 3: CONCENTRATE - High performers focus more
        print(f"   [FOCUS] Top performers concentrating...")
        for agent in self.colony:
            if agent.dna.fitness > 0.7:
                agent.concentrate()
        
        # Phase 4: REPLICATE - Fit agents reproduce
        print(f"   [REPLICATE] Fit agents reproducing...")
        new_agents = []
        for agent in self.colony:
            if agent.is_fit_to_replicate():
                offspring = agent.replicate()
                new_agents.append(offspring)
                print(f"      {agent.role} replicated -> {offspring.id}")
        self.colony.extend(new_agents)
        
        # Phase 5: MUTATE - Random mutations
        print(f"   [MUTATE] Applying mutations...")
        for agent in self.colony:
            if random.random() < agent.dna.mutation_rate:
                agent.mutate()
        
        # Phase 6: DEATH - Unfit agents die
        print(f"   [DEATH] Removing unfit agents...")
        dead_count = 0
        self.colony = [a for a in self.colony if not a.should_die() or (dead_count := dead_count + 1) < 0]
        # Actually filter
        survivors = []
        for agent in self.colony:
            if agent.should_die():
                dead_count += 1
            else:
                survivors.append(agent)
        self.colony = survivors
        print(f"      {dead_count} agents died")
        
        # Phase 7: CODE GENERATION - Generate evolved code
        print(f"   [CODE] Generating evolved code...")
        for agent in self.colony:
            if agent.dna.fitness > 0.5:
                code = agent.generate_code()
                self.total_code_generated.append(code)
                self._save_evolved_code(agent, code)
        
        # Record statistics
        self._record_statistics()
        
        # Display generation summary
        self._display_generation_summary()
    
    def _assign_work(self, agent: AntAgent):
        """Assign appropriate work to an agent based on role"""
        try:
            if isinstance(agent, LogicGateAnt):
                # Test with random boolean inputs
                inputs = (random.choice([True, False]), random.choice([True, False]))
                agent.work(inputs)
            
            elif isinstance(agent, MathAnt):
                # Test with random numeric inputs
                inputs = (random.uniform(1, 100), random.uniform(1, 10))
                agent.work(inputs)
            
            elif isinstance(agent, CircuitAnt):
                # Test with random boolean inputs
                inputs = tuple(random.choice([True, False]) for _ in range(3))
                agent.work(inputs)
            
            elif isinstance(agent, MemoryAnt):
                # Test store/retrieve
                key = f"key_{random.randint(1, 100)}"
                agent.work(('store', key, random.randint(1, 1000)))
                agent.work(('retrieve', key))
            
            elif isinstance(agent, CoordinatorAnt):
                # Test messaging
                agent.work(('send', 'logic', f"msg_{random.randint(1, 100)}"))
            
            elif isinstance(agent, ArchitectAnt):
                # Build and interpret structures
                action = random.choice(['build_alu', 'build_register', 'interpret', 'compose'])
                if action == 'interpret':
                    agent.work(('interpret', random.choice(['half_adder', 'full_adder', 'ALU', 'REGISTER'])))
                elif action == 'compose':
                    agent.work(('compose', ['AND', 'OR', 'NOT', 'half_adder', 'full_adder']))
                else:
                    agent.work((action,))
            
            elif isinstance(agent, ComputerArchitectAnt):
                # Build computer components
                agent.work()
            
            elif isinstance(agent, AssemblerAnt):
                # Generate/assemble programs
                agent.work()
        
        except Exception as e:
            agent.record_failure()
    
    def _evaluate_colony(self):
        """Evaluate overall colony performance"""
        if not self.colony:
            return
        
        fitnesses = [a.dna.fitness for a in self.colony]
        self.best_fitness = max(fitnesses)
        avg_fitness = sum(fitnesses) / len(fitnesses)
        
        # Reward top performers
        for agent in self.colony:
            if agent.dna.fitness >= self.best_fitness * 0.9:
                agent.dna.energy += 10  # Bonus energy
    
    def _save_evolved_code(self, agent: AntAgent, code: str):
        """Save evolved code to database"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute('''
            INSERT INTO evolved_code (agent_id, code, fitness, generation, timestamp)
            VALUES (?, ?, ?, ?, ?)
        ''', (agent.id, code, agent.dna.fitness, self.generation, datetime.now().isoformat()))
        
        conn.commit()
        conn.close()
    
    def _record_statistics(self):
        """Record colony statistics"""
        if not self.colony:
            return
        
        fitnesses = [a.dna.fitness for a in self.colony]
        stats = {
            "generation": self.generation,
            "population": len(self.colony),
            "avg_fitness": sum(fitnesses) / len(fitnesses),
            "best_fitness": max(fitnesses),
            "total_code": len(self.total_code_generated),
            "timestamp": datetime.now().isoformat()
        }
        self.statistics.append(stats)
        
        # Save to database
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        cursor.execute('''
            INSERT INTO colony_stats (generation, population, avg_fitness, best_fitness, total_code, timestamp)
            VALUES (?, ?, ?, ?, ?, ?)
        ''', (stats["generation"], stats["population"], stats["avg_fitness"], 
              stats["best_fitness"], stats["total_code"], stats["timestamp"]))
        conn.commit()
        conn.close()
    
    def _display_generation_summary(self):
        """Display generation summary"""
        if not self.statistics:
            return
        
        stats = self.statistics[-1]
        print(f"\n   [COACH] Generation {self.generation} Summary:")
        print(f"      Population: {stats['population']}")
        print(f"      Avg Fitness: {stats['avg_fitness']:.4f}")
        print(f"      Best Fitness: {stats['best_fitness']:.4f}")
        print(f"      Total Code: {stats['total_code']} fragments")
    
    def train(self, generations: int = 10):
        """Train the colony for multiple generations"""
        print(f"\n[COACH] Starting training for {generations} generations...")
        
        for _ in range(generations):
            self.train_generation()
            
            # Prevent extinction
            if len(self.colony) < 5:
                print(f"   [COACH] Colony too small, spawning reinforcements...")
                self.spawn_initial_colony({"logic_AND": 2, "math_add": 2, "circuit_half_adder": 1})
        
        print(f"\n[COACH] Training complete!")
        self.witness()
    
    def build_unified_code(self) -> str:
        """Build unified code from all evolved fragments"""
        if not self.total_code_generated:
            return "# No code evolved yet"
        
        header = f'''# ═══════════════════════════════════════════════════════════════════════════
# ANT COLONY EVOLVED CODE
# Generation: {self.generation}
# Population: {len(self.colony)}
# Best Fitness: {self.best_fitness:.4f}
# Total Fragments: {len(self.total_code_generated)}
# Generated: {datetime.now().isoformat()}
# ═══════════════════════════════════════════════════════════════════════════

'''
        # Get unique, high-quality code
        seen = set()
        unique_code = []
        for code in self.total_code_generated:
            code_hash = hashlib.sha256(code.encode()).hexdigest()[:16]
            if code_hash not in seen:
                seen.add(code_hash)
                unique_code.append(code)
        
        return header + "\n".join(unique_code[-50:])  # Last 50 unique fragments
    
    def witness(self):
        """Witness the colony state"""
        print(f"\n{'='*60}")
        print(f"[COACH] COLONY WITNESS - Generation {self.generation}")
        print(f"{'='*60}")
        print(f"   Population: {len(self.colony)}")
        
        if self.colony:
            fitnesses = [a.dna.fitness for a in self.colony]
            print(f"   Avg Fitness: {sum(fitnesses)/len(fitnesses):.4f}")
            print(f"   Best Fitness: {max(fitnesses):.4f}")
        
        print(f"   Total Code: {len(self.total_code_generated)} fragments")
        
        # Role breakdown
        print(f"\n   Role Breakdown:")
        self._display_colony_composition()
        
        # Top performers
        top = sorted(self.colony, key=lambda a: a.dna.fitness, reverse=True)[:5]
        print(f"\n   Top Performers:")
        for agent in top:
            print(f"      - {agent.role}: fitness={agent.dna.fitness:.3f}, gen={agent.dna.generation}")
        
        print(f"{'='*60}\n")


# ═══════════════════════════════════════════════════════════════════════════
# DEMONSTRATION
# ═══════════════════════════════════════════════════════════════════════════

if __name__ == "__main__":
    sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    
    print("=" * 70)
    print("[ANT COLONY] Role-Based Self-Evolving Agents with Coach")
    print("=" * 70)
    print("Like ants: Logic gates, Math ops, Circuits")
    print("Behaviors: REPLICATE, MUTATE, CONCENTRATE")
    print("Overseer: THE COACH")
    print("=" * 70)
    
    # Create the coach
    coach = Coach()
    
    # Spawn initial colony
    coach.spawn_initial_colony()
    
    # Initial witness
    coach.witness()
    
    # Train for multiple generations
    coach.train(generations=15)
    
    # Build unified code
    print("\n" + "=" * 70)
    print("[COACH] BUILDING UNIFIED EVOLVED CODE")
    print("=" * 70)
    
    unified = coach.build_unified_code()
    
    # Save to file
    with open("ant_evolved_code.py", "w", encoding="utf-8") as f:
        f.write(unified)
    
    print(f"   Saved to ant_evolved_code.py")
    
    # Show preview
    print("\n[CODE PREVIEW]")
    print(unified[:1500] + "\n..." if len(unified) > 1500 else unified)
    
    # Final witness
    coach.witness()
    
    print("\n[ANT COLONY] THE COLONY LIVES.")
    print("[ANT COLONY] EACH ANT HAS A ROLE. TOGETHER THEY BUILD.")
