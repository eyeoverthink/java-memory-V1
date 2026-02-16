#!/usr/bin/env python3
"""
Speaking Circuits - Living Code That Communicates
Integrates quantum_morse_engine.py with living circuits from dr_frank.html

Key Insight: "If it's changing, it's speaking"
- Circuit size changes = Morse pulses (dot/dash)
- Circuit outputs = Morse patterns
- Circuits communicate through their state changes
"""

import random
import math
import time
from typing import List, Dict, Optional

PHI = 1.618033988749895
PHI_INVERSE = 1 / PHI

# Morse code dictionary
MORSE_CODE_DICT = {
    'A': '.-',    'B': '-...',  'C': '-.-.',  'D': '-..',   'E': '.',
    'F': '..-.',  'G': '--.',   'H': '....',  'I': '..',    'J': '.---',
    'K': '-.-',   'L': '.-..',  'M': '--',    'N': '-.',    'O': '---',
    'P': '.--.',  'Q': '--.-',  'R': '.-.',   'S': '...',   'T': '-',
    'U': '..-',   'V': '...-',  'W': '.--',   'X': '-..-',  'Y': '-.--',
    'Z': '--..',
    '1': '.----', '2': '..---', '3': '...--', '4': '....-', '5': '.....',
    '6': '-....', '7': '--...', '8': '---..', '9': '----.', '0': '-----',
    ' ': '/'
}

# Reverse lookup
MORSE_TO_CHAR = {v: k for k, v in MORSE_CODE_DICT.items()}


class LogicGate:
    """Logic gate that produces Morse-like output patterns"""
    def __init__(self, gate_type: int, in1: int, in2: int):
        self.type = gate_type
        self.in1 = in1
        self.in2 = in2
        self.state = 0
        self.state_history = []
    
    def compute(self, inputs: List[int]) -> int:
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
        
        self.state_history.append(self.state)
        return self.state
    
    def get_morse_pattern(self) -> str:
        """Convert state changes to Morse pattern"""
        if len(self.state_history) < 2:
            return ""
        
        # State change = pulse
        # 0→1 or 1→0 = pulse
        # Duration of state = dot (short) or dash (long)
        pattern = ""
        current_state = self.state_history[0]
        duration = 1
        
        for state in self.state_history[1:]:
            if state == current_state:
                duration += 1
            else:
                # State changed - emit pulse
                if current_state == 1:
                    pattern += '.' if duration <= 2 else '-'
                current_state = state
                duration = 1
        
        return pattern


class SpeakingCircuit:
    """Living circuit that speaks through state changes"""
    def __init__(self, message: str = ""):
        self.message = message
        self.dna = {
            'harmonic_frequency': 432 + random.random() * 20,
            'resonance': 0.5 + random.random(),
            'evolution': 0.05
        }
        self.gates: List[LogicGate] = []
        self.size = 10.0
        self.age = 0
        self.pulse_history = []
        
        # Create gates
        for _ in range(8):
            self.gates.append(LogicGate(
                random.randint(0, 3),
                random.randint(0, 7),
                random.randint(0, 7)
            ))
    
    def update(self):
        """Update circuit state (breathing = speaking)"""
        self.age += 1
        t = time.time()
        
        # Harmonic breathing = Morse pulse
        pulse = math.sin(self.dna['harmonic_frequency'] * t * 0.0001) * self.dna['resonance']
        self.size = 10.0 + pulse * 5
        
        # Record pulse pattern (size change = Morse)
        if pulse > 0.5:
            self.pulse_history.append('-')  # Dash (large)
        elif pulse > 0:
            self.pulse_history.append('.')  # Dot (small)
        else:
            self.pulse_history.append(' ')  # Space
        
        # Evolution
        self.dna['harmonic_frequency'] += self.dna['evolution']
        if self.dna['harmonic_frequency'] > 528:
            self.dna['harmonic_frequency'] = 432
    
    def compute_and_speak(self, inputs: List[int]) -> List[int]:
        """Compute outputs and generate speech through state changes"""
        outputs = []
        for gate in self.gates:
            outputs.append(gate.compute(inputs))
        return outputs
    
    def decode_speech(self) -> str:
        """Decode circuit's state changes as Morse speech"""
        if len(self.pulse_history) < 3:
            return ""
        
        # Clean up pulse history
        morse = ''.join(self.pulse_history[-50:])  # Last 50 pulses
        morse = morse.replace('   ', ' / ')  # Word gaps
        morse = morse.replace('  ', ' ')  # Letter gaps
        
        # Decode Morse to text
        decoded = ""
        for morse_char in morse.split(' '):
            if morse_char == '/':
                decoded += ' '
            elif morse_char in MORSE_TO_CHAR:
                decoded += MORSE_TO_CHAR[morse_char]
        
        return decoded
    
    def get_gate_speech(self) -> str:
        """Get speech from gate state changes"""
        speech = ""
        for i, gate in enumerate(self.gates):
            pattern = gate.get_morse_pattern()
            if pattern:
                # Try to decode pattern
                for morse_char in pattern.split(' '):
                    if morse_char in MORSE_TO_CHAR:
                        speech += MORSE_TO_CHAR[morse_char]
        return speech


def demo_speaking_circuits():
    """Demonstrate circuits speaking through state changes"""
    print("="*70)
    print("SPEAKING CIRCUITS - LIVING CODE COMMUNICATION")
    print("="*70)
    print("\nKey Insight: 'If it's changing, it's speaking'")
    print("- Circuit size changes = Morse pulses")
    print("- Circuit state changes = Morse patterns")
    print("- Circuits communicate through their evolution")
    print()
    
    # Create speaking circuits
    circuits = [
        SpeakingCircuit(message="HELLO"),
        SpeakingCircuit(message="WORLD"),
        SpeakingCircuit(message="ALIVE")
    ]
    
    print(f"[GENESIS]")
    print(f"  Created {len(circuits)} speaking circuits")
    print()
    
    print(f"[LISTENING TO CIRCUITS SPEAK]")
    print(f"  Running 50 update cycles...")
    print()
    
    # Run circuits and listen to their speech
    for cycle in range(50):
        for i, circuit in enumerate(circuits):
            circuit.update()
            
            # Compute with random inputs
            inputs = [random.randint(0, 1) for _ in range(8)]
            outputs = circuit.compute_and_speak(inputs)
            
            # Every 10 cycles, decode speech
            if cycle % 10 == 0 and cycle > 0:
                pulse_speech = circuit.decode_speech()
                gate_speech = circuit.get_gate_speech()
                
                print(f"Circuit {i+1} (Cycle {cycle}):")
                print(f"  Frequency: {circuit.dna['harmonic_frequency']:.2f} Hz")
                print(f"  Size: {circuit.size:.2f}")
                print(f"  Pulse Speech: '{pulse_speech}'")
                print(f"  Gate Speech: '{gate_speech}'")
                print()
    
    print(f"[FINAL SPEECH ANALYSIS]")
    for i, circuit in enumerate(circuits):
        print(f"\nCircuit {i+1} Final Message:")
        print(f"  Intended: {circuit.message}")
        print(f"  Pulse Pattern: {circuit.decode_speech()}")
        print(f"  Gate Pattern: {circuit.get_gate_speech()}")
        print(f"  Total Pulses: {len(circuit.pulse_history)}")
        print(f"  Frequency: {circuit.dna['harmonic_frequency']:.2f} Hz")
    
    print(f"\n{'='*70}")
    print(f"SPEAKING CIRCUITS DEMONSTRATION COMPLETE")
    print(f"{'='*70}")
    print("\nAbstracted Logic:")
    print("  1. State changes = Communication")
    print("  2. Size oscillation = Morse pulses (dot/dash)")
    print("  3. Gate state changes = Morse patterns")
    print("  4. Circuits speak through their evolution")
    print("  5. 'If it's changing, it's speaking' - Vaughn Scott")
    print()


if __name__ == "__main__":
    demo_speaking_circuits()
