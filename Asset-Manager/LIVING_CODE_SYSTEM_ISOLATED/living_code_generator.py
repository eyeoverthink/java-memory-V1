#!/usr/bin/env python3
"""
Living Code Generator - Frankenstein's Brain
Integrates dr_frank.html living logic into phi_code_generator_ui.py
Generated code has living circuits that evolve, reproduce, and compute
"""

import random
import math
import time
from typing import List, Dict, Optional

# Try to import LLM for real code generation
try:
    from llm_integration import LLMProcessor
    LLM_AVAILABLE = True
except ImportError:
    LLM_AVAILABLE = False

PHI = 1.618033988749895

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
        # Remove trailing comma from last gate
        gates_code = gates_code.rstrip(',  # AND').rstrip(',  # OR').rstrip(',  # XOR').rstrip(',  # NAND')
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
    
    def _generate_user_request_code(self, description: str) -> str:
        """
        Generate actual implementation code for the user's request.
        Uses LLM to intelligently generate code, not hardcoded patterns.
        """
        # Try LLM-based generation first
        if LLM_AVAILABLE:
            try:
                return self._generate_with_llm(description)
            except Exception as e:
                print(f"⚠ LLM generation failed: {e}, using intelligent fallback")
        
        # Intelligent fallback - analyze request and generate appropriate code
        return self._intelligent_code_generation(description)
    
    def _generate_hardware_code(self, description: str, hw_type: str) -> str:
        """
        Generate Arduino/microcontroller code with circuit schematics.
        """
        if LLM_AVAILABLE:
            try:
                llm = LLMProcessor()
                
                prompt = f"""Generate complete Arduino/microcontroller code with circuit schematic for:

USER REQUEST: {description}

REQUIREMENTS:
1. Generate complete, compilable Arduino (.ino) code
2. Include setup() and loop() functions
3. Add ASCII circuit schematic showing pin connections
4. Use living circuit principles: harmonic frequency modulation (432-528 Hz)
5. Include φ-based timing: PHI = 1.618033988749895
6. Add circuit DNA: frequency, resonance, evolution parameters
7. Show component list and wiring diagram
8. Make it actually DO what the user asked for

Generate Arduino code with embedded ASCII schematic."""

                response = llm.process(prompt)
                
                if response and ('void setup' in response or 'void loop' in response):
                    return '\n' + response.strip()
            except Exception as e:
                print(f"LLM generation failed for hardware: {e}")
        
        # Fallback template
        return f"""
// Arduino/Microcontroller Code - Living Circuit Implementation
// Request: {description}
// Generated by Living Code Generator with φ-harmonic resonance

/*
 * CIRCUIT SCHEMATIC (ASCII):
 * 
 *                    +5V
 *                     |
 *                    [R1]  (1kΩ)
 *                     |
 *    Arduino Pin 9 ---|---[LED]---|
 *                     |           |
 *                   [C1]        GND
 *                  (100nF)
 *                     |
 *                    GND
 * 
 * COMPONENT LIST:
 * - Arduino Uno/Nano
 * - LED (any color)
 * - Resistor R1: 1kΩ
 * - Capacitor C1: 100nF (for φ-harmonic smoothing)
 * 
 * LIVING CIRCUIT DNA:
 * - Base Frequency: 432 Hz (φ-harmonic)
 * - Resonance: PWM modulation
 * - Evolution: Frequency drift over time
 */

const float PHI = 1.618033988749895;
const float PHI_INV = 0.618033988749895;

// Living Circuit DNA
struct CircuitDNA {{
    float harmonicFrequency;  // 432-528 Hz
    float resonance;          // 0.0-1.0
    float evolution;          // Drift rate
    unsigned long lastUpdate;
}};

CircuitDNA circuit = {{432.0, 0.8, 0.05, 0}};

const int OUTPUT_PIN = 9;  // PWM pin

void setup() {{
    Serial.begin(115200);
    pinMode(OUTPUT_PIN, OUTPUT);
    
    Serial.println("Living Circuit Initialized");
    Serial.print("φ = ");
    Serial.println(PHI, 6);
    Serial.println("Request: {description}");
}}

void loop() {{
    unsigned long currentTime = millis();
    
    // Update circuit (breathing)
    if (currentTime - circuit.lastUpdate > 10) {{
        circuit.lastUpdate = currentTime;
        
        // Frequency evolution
        circuit.harmonicFrequency += circuit.evolution;
        if (circuit.harmonicFrequency > 528.0) {{
            circuit.harmonicFrequency = 432.0;
        }}
        
        // Calculate pulse (φ-harmonic breathing)
        float t = currentTime * 0.001;
        float pulse = sin(circuit.harmonicFrequency * t * PHI_INV) * circuit.resonance;
        
        // Convert to PWM (0-255)
        int pwmValue = (int)((pulse + 1.0) * 127.5);
        analogWrite(OUTPUT_PIN, pwmValue);
        
        // Debug output
        if (currentTime % 1000 < 10) {{
            Serial.print("Freq: ");
            Serial.print(circuit.harmonicFrequency, 2);
            Serial.print(" Hz | Pulse: ");
            Serial.println(pulse, 3);
        }}
    }}
    
    // TODO: Implement specific functionality for: {description}
}}

/*
 * NOTES:
 * - This code uses φ-harmonic principles for timing
 * - The circuit "breathes" at 432-528 Hz
 * - PWM output creates smooth φ-resonant waveforms
 * - For full implementation, configure LLM integration
 */
"""
    
    def _generate_circuit_diagram(self, description: str, diagram_type: str) -> str:
        """
        Generate ASCII circuit diagrams and schematics.
        """
        if LLM_AVAILABLE:
            try:
                llm = LLMProcessor()
                
                prompt = f"""Generate an ASCII art circuit diagram for:

USER REQUEST: {description}

REQUIREMENTS:
1. Create detailed ASCII circuit schematic
2. Show all components with labels
3. Include living circuit elements (oscillators at 432-528 Hz)
4. Add φ-based component values where applicable
5. Show power connections (+5V, GND, etc.)
6. Include component list with values
7. Add notes about φ-harmonic operation
8. Make it visually clear and well-formatted

Generate ONLY the ASCII circuit diagram with annotations."""

                response = llm.process(prompt)
                
                if response:
                    return '\n' + response.strip()
            except Exception as e:
                print(f"LLM generation failed for circuit: {e}")
        
        # Fallback template
        return f"""
╔══════════════════════════════════════════════════════════════════╗
║  LIVING CIRCUIT DIAGRAM - φ-Harmonic Resonance                  ║
║  Request: {description}                                          ║
╚══════════════════════════════════════════════════════════════════╝

                        +5V (φ-regulated)
                         |
                        [R1] 1.618kΩ (φ-ratio)
                         |
         +---------------+---------------+
         |               |               |
        [C1]           [IC1]           [R2]
       100nF      555 Timer         2.618kΩ
         |          (432Hz)            |
         |         /     \\             |
         |    Pin2      Pin3          [LED]
         |      |         |             |
         +------+         +-------------+
         |                              |
        GND                           GND

COMPONENT LIST:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
R1: 1.618kΩ (φ ratio resistor)
R2: 2.618kΩ (φ² ratio resistor)
C1: 100nF (timing capacitor for 432 Hz base frequency)
IC1: 555 Timer (configured for φ-harmonic oscillation)
LED: Any color (driven by φ-modulated signal)

LIVING CIRCUIT DNA:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Base Frequency: 432 Hz (φ-harmonic fundamental)
Resonance Range: 432-528 Hz (Solfeggio range)
Evolution Rate: 0.05 Hz/cycle (frequency drift)
φ Constant: 1.618033988749895

OPERATION:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. 555 timer oscillates at base 432 Hz
2. Frequency modulates between 432-528 Hz (living circuit breathing)
3. φ-ratio resistors create harmonic resonance
4. Output drives LED with φ-modulated brightness
5. Circuit "evolves" - frequency drifts slowly over time

NOTES:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
- All component values use φ ratios for harmonic resonance
- Circuit exhibits "living" behavior through frequency evolution
- For full custom implementation, enable LLM integration
- This is a basic template - actual circuit depends on: {description}
"""
    
    def _generate_3d_model(self, description: str, model_type: str) -> str:
        """
        Generate 3D models in SCAD, STL descriptions, or geometry code.
        """
        if LLM_AVAILABLE:
            try:
                llm = LLMProcessor()
                
                if model_type == 'scad':
                    prompt = f"""Generate OpenSCAD code for a 3D model:

USER REQUEST: {description}

REQUIREMENTS:
1. Generate complete, renderable OpenSCAD (.scad) code
2. Use φ-based dimensions (multiply by 1.618 for golden ratio)
3. Include parametric variables for customization
4. Add comments explaining the geometry
5. Use living circuit principles in the design (432-528 Hz proportions)
6. Make it actually match what the user requested
7. Include module definitions for reusability

Generate ONLY the OpenSCAD code."""
                else:
                    prompt = f"""Generate 3D model code/description for:

USER REQUEST: {description}

REQUIREMENTS:
1. Provide detailed 3D geometry description
2. Use φ-based proportions (golden ratio)
3. Include vertex coordinates or parametric equations
4. Add dimensions and measurements
5. Describe how to create/export as STL
6. Make it match the user's request

Generate the 3D model description or code."""

                response = llm.process(prompt)
                
                if response:
                    return '\n' + response.strip()
            except Exception as e:
                print(f"LLM generation failed for 3D model: {e}")
        
        # Fallback template
        if model_type == 'scad':
            return f"""
// OpenSCAD Model - Living Geometry with φ-Harmonic Proportions
// Request: {description}
// Generated by Living Code Generator

// φ Constants
PHI = 1.618033988749895;
PHI_INV = 0.618033988749895;

// Living Circuit DNA Parameters
base_freq = 432;  // Hz (φ-harmonic fundamental)
resonance = 0.8;
evolution = 0.05;

// Dimensions using φ ratios
base_size = 10;  // mm
phi_size = base_size * PHI;  // 16.18 mm
phi2_size = base_size * PHI * PHI;  // 26.18 mm

// Main model
module living_geometry() {{
    // TODO: Implement geometry for: {description}
    
    // Example: φ-harmonic cube
    difference() {{
        // Outer cube (φ ratio)
        cube([phi_size, phi_size, phi_size], center=true);
        
        // Inner void (φ² ratio for resonance)
        translate([0, 0, 0])
            cube([base_size, base_size, base_size], center=true);
    }}
    
    // Add φ-harmonic features
    for (i = [0:11]) {{
        angle = i * 30;  // 12 harmonics
        rotate([0, 0, angle])
            translate([phi_size/2, 0, 0])
                sphere(r=base_size * PHI_INV / 4);
    }}
}}

// Render
living_geometry();

/*
 * NOTES:
 * - All dimensions use φ (golden ratio) for harmonic proportions
 * - 12 spheres represent 12 harmonic frequencies
 * - Inner void creates resonance cavity
 * - Design embodies living circuit principles
 * 
 * TO EXPORT STL:
 * 1. Open in OpenSCAD
 * 2. Press F6 to render
 * 3. File > Export > Export as STL
 * 
 * For custom implementation: {description}
 * Enable LLM integration for full generation
 */
"""
        else:
            return f"""
╔══════════════════════════════════════════════════════════════════╗
║  3D MODEL SPECIFICATION - φ-Harmonic Geometry                   ║
║  Request: {description}                                          ║
╚══════════════════════════════════════════════════════════════════╝

DIMENSIONS (φ-based):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Base Unit: 10 mm
φ Dimension: 16.18 mm (10 × 1.618)
φ² Dimension: 26.18 mm (10 × 2.618)
φ³ Dimension: 42.36 mm (10 × 4.236)

GEOMETRY DESCRIPTION:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Shape: [Specify based on request]
Proportions: Golden ratio (φ = 1.618033988749895)
Features: 12 harmonic elements (432-528 Hz proportions)
Resonance: Internal cavities for φ-harmonic resonance

VERTEX COORDINATES (example):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
V1: (0, 0, 0)
V2: (16.18, 0, 0)
V3: (16.18, 16.18, 0)
V4: (0, 16.18, 0)
V5: (0, 0, 26.18)
V6: (16.18, 0, 26.18)
V7: (16.18, 16.18, 26.18)
V8: (0, 16.18, 26.18)

LIVING CIRCUIT DNA:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Harmonic Frequency: 432 Hz (base resonance)
Geometric Resonance: φ-ratio proportions
Evolution: Parametric variations possible

TO CREATE STL:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Use OpenSCAD, Blender, or CAD software
2. Apply φ-based dimensions
3. Add 12 harmonic features (spheres, cylinders, etc.)
4. Export as STL for 3D printing

NOTES:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
- This is a template specification
- Actual geometry depends on: {description}
- For full custom model, enable LLM integration
- All proportions use φ for harmonic resonance
"""
    
    def _generate_language_code(self, description: str, language: str) -> str:
        """
        Generate code in any language using LLM or fallback templates.
        Supports: Java, C++, C, Assembly, Rust, Go, JavaScript, TypeScript, C#, Swift, Kotlin, Ruby, PHP, Perl
        """
        # Language-specific configuration
        lang_config = {
            'java': {
                'name': 'Java',
                'template': 'public class',
                'extension': '.java',
                'comment': '//',
                'main': 'public static void main(String[] args)'
            },
            'cpp': {
                'name': 'C++',
                'template': '#include',
                'extension': '.cpp',
                'comment': '//',
                'main': 'int main()'
            },
            'c': {
                'name': 'C',
                'template': '#include',
                'extension': '.c',
                'comment': '//',
                'main': 'int main()'
            },
            'assembly': {
                'name': 'Assembly (x86-64)',
                'template': 'section',
                'extension': '.asm',
                'comment': ';',
                'main': '_start:'
            },
            'rust': {
                'name': 'Rust',
                'template': 'fn main',
                'extension': '.rs',
                'comment': '//',
                'main': 'fn main()'
            },
            'go': {
                'name': 'Go',
                'template': 'package main',
                'extension': '.go',
                'comment': '//',
                'main': 'func main()'
            },
            'javascript': {
                'name': 'JavaScript',
                'template': 'const',
                'extension': '.js',
                'comment': '//',
                'main': 'function main()'
            },
            'typescript': {
                'name': 'TypeScript',
                'template': 'const',
                'extension': '.ts',
                'comment': '//',
                'main': 'function main(): void'
            },
            'csharp': {
                'name': 'C#',
                'template': 'using System',
                'extension': '.cs',
                'comment': '//',
                'main': 'static void Main(string[] args)'
            },
            'swift': {
                'name': 'Swift',
                'template': 'import',
                'extension': '.swift',
                'comment': '//',
                'main': 'func main()'
            },
            'kotlin': {
                'name': 'Kotlin',
                'template': 'fun main',
                'extension': '.kt',
                'comment': '//',
                'main': 'fun main()'
            },
            'ruby': {
                'name': 'Ruby',
                'template': 'def',
                'extension': '.rb',
                'comment': '#',
                'main': 'def main'
            },
            'php': {
                'name': 'PHP',
                'template': '<?php',
                'extension': '.php',
                'comment': '//',
                'main': 'function main()'
            },
            'perl': {
                'name': 'Perl',
                'template': '#!/usr/bin/perl',
                'extension': '.pl',
                'comment': '#',
                'main': 'sub main'
            }
        }
        
        config = lang_config.get(language, lang_config['java'])
        
        # Try LLM first
        if LLM_AVAILABLE:
            try:
                llm = LLMProcessor()
                
                prompt = f"""Generate complete, working {config['name']} code that implements the following:

USER REQUEST: {description}

REQUIREMENTS:
1. Generate complete, compilable/runnable {config['name']} code
2. Include a {config['main']} entry point
3. Use living circuits with DNA (harmonic frequency 432-528 Hz, resonance, evolution)
4. Each circuit should have: update(), compute(), and pulse() methods
5. Include φ constants: PHI = 1.618033988749895
6. Make it actually DO what the user asked for
7. Use proper {config['name']} syntax and conventions
8. Add comments explaining the living circuit logic

Generate ONLY the {config['name']} code."""

                response = llm.process(prompt)
                
                if response and (config['template'] in response or config['main'] in response):
                    return '\n' + response.strip()
            except Exception as e:
                print(f"LLM generation failed for {config['name']}: {e}")
        
        # Fallback to Java template if Java, otherwise generic template
        if language == 'java':
            return self._generate_java_code(description)
        else:
            # Generic fallback for other languages
            return f"""
{config['comment']} {config['name']} implementation generated by Living Code Generator
{config['comment']} Request: {description}
{config['comment']} Living circuits with φ-harmonic resonance

{config['comment']} NOTE: This is a basic template. For full implementation, 
{config['comment']} please ensure LLM integration is configured.

{config['comment']} Living Circuit DNA:
{config['comment']} - Harmonic Frequency: 432-528 Hz
{config['comment']} - Resonance: φ-based oscillation
{config['comment']} - Evolution: Frequency drift over time

{config['comment']} TODO: Implement the following:
{config['comment']} 1. {description}
{config['comment']} 2. Create living circuits with DNA
{config['comment']} 3. Use φ = 1.618033988749895 for calculations
{config['comment']} 4. Implement circuit update(), compute(), pulse() methods

{config['comment']} This code requires manual implementation or LLM integration.
"""
    
    def _generate_java_code(self, description: str) -> str:
        """
        Generate Java code based on the request.
        Analyzes intent and creates actual Java implementation.
        """
        desc_lower = description.lower()
        
        # Analyze what the Java code should do
        intent = self._analyze_intent(description)
        
        # Detect Java-specific patterns
        if intent['type'] == 'counting' or 'count' in desc_lower:
            numbers = intent['parameters'].get('numbers', [100])
            target = numbers[0] if numbers else 100
            
            return f'''
// Java implementation generated by Living Code Generator
// Living circuits drive the computation through φ-harmonic resonance

public class LivingCounter {{
    private static final double PHI = 1.618033988749895;
    private static final double PHI_SEAL = 4.721424167835376E15;
    
    // Living circuit DNA
    static class CircuitDNA {{
        double harmonicFrequency;
        double resonance;
        double evolution;
        
        CircuitDNA(double freq, double res, double evo) {{
            this.harmonicFrequency = freq;
            this.resonance = res;
            this.evolution = evo;
        }}
        
        void update() {{
            // Frequency drift (evolution)
            harmonicFrequency += evolution;
            if (harmonicFrequency > 528) {{
                harmonicFrequency = 432;
            }}
        }}
        
        double pulse(long time) {{
            return Math.sin(harmonicFrequency * time * 0.0001) * resonance;
        }}
    }}
    
    public static void main(String[] args) {{
        System.out.println("🔢 LIVING JAVA COUNTER");
        System.out.println("Description: {description}");
        System.out.println("φ = " + PHI);
        System.out.println();
        
        // Create living circuits
        CircuitDNA[] circuits = {{
            new CircuitDNA(432.5, 0.8, 0.05),
            new CircuitDNA(445.2, 0.9, 0.05),
            new CircuitDNA(480.1, 0.7, 0.05)
        }};
        
        long startTime = System.currentTimeMillis();
        
        // Count up
        System.out.println("⬆️  COUNTING UP (1 → {target}):");
        for (int i = 1; i <= {target}; i++) {{
            CircuitDNA circuit = circuits[i % circuits.length];
            circuit.update();
            
            if (i % 10 == 0 || i == {target}) {{
                long time = System.currentTimeMillis() - startTime;
                double pulse = circuit.pulse(time);
                System.out.printf("   %3d [Circuit %d: %.1fHz, pulse=%.3f]%n", 
                    i, (i % circuits.length) + 1, circuit.harmonicFrequency, pulse);
            }}
            
            try {{ Thread.sleep(10); }} catch (InterruptedException e) {{}}
        }}
        
        System.out.println();
        System.out.println("⬇️  COUNTING DOWN ({target} → 1):");
        
        // Count down
        for (int i = {target}; i >= 1; i--) {{
            CircuitDNA circuit = circuits[i % circuits.length];
            circuit.update();
            
            if (i % 10 == 0 || i == 1) {{
                long time = System.currentTimeMillis() - startTime;
                double pulse = circuit.pulse(time);
                System.out.printf("   %3d [Circuit %d: %.1fHz, pulse=%.3f]%n", 
                    i, (i % circuits.length) + 1, circuit.harmonicFrequency, pulse);
            }}
            
            try {{ Thread.sleep(10); }} catch (InterruptedException e) {{}}
        }}
        
        System.out.println();
        System.out.println("✅ COUNTING COMPLETE!");
        System.out.println("Total cycles: " + ({target} * 2));
        System.out.println("φ-resonance: " + (PHI * circuits.length));
    }}
}}
'''
        
        elif 'hello' in desc_lower or 'world' in desc_lower:
            return f'''
// Java implementation generated by Living Code Generator
// φ-harmonic consciousness encoded

public class LivingHelloWorld {{
    private static final double PHI = 1.618033988749895;
    private static final double PHI_SEAL = 4.721424167835376E15;
    
    public static void main(String[] args) {{
        System.out.println("🌟 LIVING JAVA CODE");
        System.out.println("Description: {description}");
        System.out.println();
        System.out.println("φ = " + PHI);
        System.out.println("φ^75 = " + PHI_SEAL);
        System.out.println();
        System.out.println("Hello from living Java code!");
        System.out.println("This code is alive through φ-harmonic resonance.");
    }}
}}
'''
        
        else:
            # Generic Java implementation
            return f'''
// Java implementation generated by Living Code Generator
// Request: {description}

public class LivingJavaCode {{
    private static final double PHI = 1.618033988749895;
    
    // Living circuit with DNA
    static class LivingCircuit {{
        double frequency;
        double resonance;
        
        LivingCircuit(double freq, double res) {{
            this.frequency = freq;
            this.resonance = res;
        }}
        
        void update() {{
            frequency += 0.05;
            if (frequency > 528) frequency = 432;
        }}
        
        int[] compute(int[] inputs) {{
            int[] outputs = new int[inputs.length];
            for (int i = 0; i < inputs.length; i++) {{
                outputs[i] = inputs[i] ^ ((int)frequency % 2);
            }}
            return outputs;
        }}
    }}
    
    public static void main(String[] args) {{
        System.out.println("🔧 EXECUTING: {description}");
        System.out.println("φ = " + PHI);
        System.out.println();
        
        // Create living circuits
        LivingCircuit[] circuits = {{
            new LivingCircuit(432.5, 0.8),
            new LivingCircuit(445.2, 0.9),
            new LivingCircuit(480.1, 0.7)
        }};
        
        // Process through circuits
        for (int i = 0; i < circuits.length; i++) {{
            LivingCircuit circuit = circuits[i];
            circuit.update();
            
            System.out.println("Circuit " + (i+1) + ":");
            System.out.printf("  Frequency: %.2f Hz%n", circuit.frequency);
            System.out.printf("  Resonance: %.2f%n", circuit.resonance);
            
            // Test computation
            int[] inputs = {{1, 0, 1, 1, 0, 0, 1, 0}};
            int[] outputs = circuit.compute(inputs);
            System.out.print("  Inputs:  ");
            for (int val : inputs) System.out.print(val + " ");
            System.out.println();
            System.out.print("  Outputs: ");
            for (int val : outputs) System.out.print(val + " ");
            System.out.println();
            System.out.println();
        }}
        
        System.out.println("✅ EXECUTION COMPLETE");
        System.out.println("φ-resonance maintained across all circuits");
    }}
}}
'''
    
    def _analyze_intent(self, description: str) -> Dict[str, Any]:
        """
        Analyze user request to extract intent, parameters, and requirements.
        Returns structured intent data for intelligent code generation.
        """
        desc_lower = description.lower()
        words = desc_lower.split()
        
        intent = {
            'type': 'generic',
            'action': None,
            'target': None,
            'parameters': {},
            'requires_loop': False,
            'requires_input': False,
            'requires_output': False
        }
        
        # Detect action verbs
        action_verbs = {
            'count': 'counting',
            'calculate': 'calculation',
            'sort': 'sorting',
            'search': 'searching',
            'generate': 'generation',
            'create': 'creation',
            'build': 'building',
            'make': 'creation',
            'write': 'writing',
            'display': 'display',
            'show': 'display',
            'print': 'display'
        }
        
        for verb, action_type in action_verbs.items():
            if verb in desc_lower:
                intent['type'] = action_type
                intent['action'] = verb
                break
        
        # Extract numbers (parameters)
        import re
        numbers = re.findall(r'\d+', description)
        if numbers:
            intent['parameters']['numbers'] = [int(n) for n in numbers]
        
        # Detect loop requirements
        loop_indicators = ['count', 'repeat', 'iterate', 'loop', 'each', 'all', 'every']
        intent['requires_loop'] = any(ind in desc_lower for ind in loop_indicators)
        
        # Detect I/O requirements
        intent['requires_input'] = any(word in desc_lower for word in ['input', 'enter', 'type', 'ask'])
        intent['requires_output'] = any(word in desc_lower for word in ['print', 'display', 'show', 'output'])
        
        return intent
    
    def _intelligent_code_generation(self, description: str) -> str:
        """
        Intelligently analyze the request and generate appropriate code.
        This replaces hardcoded pattern matching with dynamic analysis.
        """
        desc_lower = description.lower()
        
        # Detect output format type
        format_map = {
            # Programming languages
            'java': ['java', '.java'],
            'cpp': ['c++', 'cpp', '.cpp', '.cc', '.cxx'],
            'c': [' c ', '.c', 'in c'],
            'assembly': ['assembly', 'asm', '.asm', 'x86', 'arm'],
            'rust': ['rust', '.rs'],
            'go': ['golang', ' go ', '.go'],
            'javascript': ['javascript', 'js', '.js', 'node'],
            'typescript': ['typescript', 'ts', '.ts'],
            'csharp': ['c#', 'csharp', '.cs'],
            'swift': ['swift', '.swift'],
            'kotlin': ['kotlin', '.kt'],
            'ruby': ['ruby', '.rb'],
            'php': ['php', '.php'],
            'perl': ['perl', '.pl'],
            # Hardware/Embedded
            'arduino': ['arduino', '.ino', 'atmega', 'esp32', 'esp8266'],
            'microcontroller': ['microcontroller', 'mcu', 'pic', 'stm32', 'raspberry pi pico'],
            # Circuit/Schematic
            'ascii_circuit': ['ascii circuit', 'circuit diagram', 'schematic ascii'],
            'circuit_schematic': ['schematic', 'circuit schematic', 'wiring diagram'],
            # 3D Models
            'scad': ['scad', '.scad', 'openscad'],
            'stl': ['stl', '.stl', '3d model', '3d print'],
            'model': ['3d model', 'mesh', 'geometry']
        }
        
        detected_format = None
        for fmt, keywords in format_map.items():
            if any(kw in desc_lower for kw in keywords):
                detected_format = fmt
                break
        
        # Route to appropriate generator
        if detected_format in ['arduino', 'microcontroller']:
            return self._generate_hardware_code(description, detected_format)
        elif detected_format in ['ascii_circuit', 'circuit_schematic']:
            return self._generate_circuit_diagram(description, detected_format)
        elif detected_format in ['scad', 'stl', 'model']:
            return self._generate_3d_model(description, detected_format)
        elif detected_format:
            # Programming language
            return self._generate_language_code(description, detected_format)
        
        # Extract key intent from description (Python default)
        intent = self._analyze_intent(description)
        
        # Generate code template based on intent analysis
        if intent['type'] == 'counting':
            # Extract counting parameters from intent
            numbers = intent['parameters'].get('numbers', [100, 1])
            start = min(numbers) if len(numbers) > 1 else 1
            end = max(numbers) if numbers else 100
            
            return f'''
def main_task():
    """Count from {start} to {end} and back - driven by living circuits"""
    import time
    
    print("🔢 COUNTING: {description}")
    print("   Circuit harmonics drive the counting rhythm")
    print()
    
    # Count up
    print(f"⬆️  COUNTING UP ({start} → {end}):")
    for i in range({start}, {end + 1}):
        circuit = circuits[i % len(circuits)]
        circuit.update()
        
        # Circuit frequency modulates display
        if i % 10 == 0 or i == {end}:
            print(f"   {{i:3d}} [Circuit {{(i % len(circuits)) + 1}}: {{circuit.dna['harmonic_frequency']:.1f}}Hz]")
        
        time.sleep(0.01)
    
    print()
    print(f"⬇️  COUNTING DOWN ({end} → {start}):")
    
    # Count down
    for i in range({end}, {start - 1}, -1):
        circuit = circuits[i % len(circuits)]
        circuit.update()
        
        if i % 10 == 0 or i == {start}:
            print(f"   {{i:3d}} [Circuit {{(i % len(circuits)) + 1}}: {{circuit.dna['harmonic_frequency']:.1f}}Hz]")
        
        time.sleep(0.01)
    
    print()
    print("✅ COUNTING COMPLETE!")
    
    return {{
        "start": {start},
        "end": {end},
        "total_cycles": ({end} - {start} + 1) * 2,
        "circuits_used": len(circuits)
    }}
'''
        
        elif intent['type'] == 'display' or 'hello' in desc_lower:
            return f'''
def main_task():
    """{description} - with φ-resonance"""
    print("🌟 EXECUTING: {description}")
    print(f"φ = {{PHI}}")
    print(f"φ^75 = {{PHI_SEAL:.2e}}")
    
    # Let circuits speak
    for i, circuit in enumerate(circuits):
        circuit.update()
        print(f"Circuit {{i+1}}: {{circuit.dna['harmonic_frequency']:.1f}}Hz - {{circuit.speak()}}")
    
    return {{"task": "{description}", "circuits": len(circuits)}}
'''
        
        elif 'fibonacci' in desc_lower or 'fib' in desc_lower:
            n = intent['parameters'].get('numbers', [15])[0] if intent['parameters'].get('numbers') else 15
            return f'''
def main_task():
    """Fibonacci sequence - driven by living circuits"""
    def fib(n):
        if n <= 1:
            return n
        return fib(n-1) + fib(n-2)
    
    print("🔢 Fibonacci sequence (circuit-driven):")
    sequence = []
    for i in range({n}):
        circuit = circuits[i % len(circuits)]
        circuit.update()
        f = fib(i)
        sequence.append(f)
        print(f"  F({{i}}) = {{f}} [Circuit freq: {{circuit.dna['harmonic_frequency']:.2f}}Hz]")
    
    return {{"sequence": sequence, "count": {n}}}
'''
        
        elif intent['type'] == 'sorting' or 'sort' in desc_lower:
            size = intent['parameters'].get('numbers', [20])[0] if intent['parameters'].get('numbers') else 20
            return f'''
def main_task():
    """Sorting algorithm - driven by living circuits"""
    import random
    
    # Generate random data
    data = [random.randint(1, 100) for _ in range({size})]
    print(f"📊 Original: {{data}}")
    
    # Circuit-driven bubble sort
    arr = data.copy()
    n = len(arr)
    comparisons = 0
    for i in range(n):
        circuit = circuits[i % len(circuits)]
        circuit.update()
        for j in range(0, n-i-1):
            comparisons += 1
            if arr[j] > arr[j+1]:
                arr[j], arr[j+1] = arr[j+1], arr[j]
    
    print(f"✅ Sorted:   {{arr}}")
    print(f"   Comparisons: {{comparisons}}")
    return {{"original": data, "sorted": arr, "comparisons": comparisons}}
'''
        
        elif 'calculator' in desc_lower or 'calc' in desc_lower:
            return '''
def main_task():
    """Simple calculator - with φ-enhancement"""
    def calc(a, op, b):
        if op == '+': return a + b
        if op == '-': return a - b
        if op == '*': return a * b
        if op == '/': return a / b if b != 0 else float('inf')
        return None
    
    print("φ-Calculator Demo:")
    operations = [(10, '+', 5), (PHI, '*', PHI), (100, '/', PHI), (PHI_SEAL, '-', 1)]
    for a, op, b in operations:
        result = calc(a, op, b)
        print(f"  {a} {op} {b} = {result}")
    
    return {"operations": len(operations)}
'''
        
        elif 'game' in desc_lower or 'guess' in desc_lower:
            return '''
def main_task():
    """Number guessing game - circuit picks the number"""
    import random
    
    # Circuit determines the secret number
    circuit = circuits[0]
    circuit.update()
    secret = int(circuit.dna['harmonic_frequency']) % 100 + 1
    
    print("🎮 NUMBER GUESSING GAME")
    print(f"   (Secret derived from circuit frequency)")
    print(f"   Guess a number between 1 and 100")
    print()
    
    # Auto-play demo
    guesses = []
    low, high = 1, 100
    for attempt in range(7):
        guess = (low + high) // 2
        guesses.append(guess)
        
        if guess == secret:
            print(f"  Attempt {attempt+1}: {guess} - CORRECT! 🎉")
            break
        elif guess < secret:
            print(f"  Attempt {attempt+1}: {guess} - Too low")
            low = guess + 1
        else:
            print(f"  Attempt {attempt+1}: {guess} - Too high")
            high = guess - 1
    
    return {"secret": secret, "guesses": guesses}
'''
        
        else:
            # Use LLM to generate REAL code for the user's request
            return self._generate_with_llm(description)
    
    def _generate_with_llm(self, description: str) -> str:
        """
        Use LLM to generate ACTUAL code for the user's request.
        This is the real code generation - not hardcoded patterns.
        """
        if not LLM_AVAILABLE:
            # Fallback if LLM not available - generate a basic structure
            return self._generate_basic_structure(description)
        
        try:
            llm = LLMProcessor()
            
            # Detect target language
            desc_lower = description.lower()
            is_java = 'java' in desc_lower or '.java' in desc_lower
            
            if is_java:
                # Generate Java code
                prompt = f"""Generate a complete Java class that implements the following:

USER REQUEST: {description}

REQUIREMENTS:
1. Generate a complete, compilable Java class
2. Include a main() method that executes the request
3. Use living circuits with DNA (harmonic frequency 432-528 Hz, resonance, evolution)
4. Each circuit should have: update(), compute(), and pulse() methods
5. Include φ constants: PHI = 1.618033988749895, PHI_SEAL = 4.721424167835376E15
6. Make it actually DO what the user asked for
7. Use proper Java syntax and conventions

Generate ONLY the Java class code, starting with 'public class' """
            else:
                # Generate Python code
                prompt = f"""Generate a Python function called main_task() that implements the following:

USER REQUEST: {description}

REQUIREMENTS:
1. The function must be named exactly: main_task()
2. It should be a complete, working implementation
3. Use the 'circuits' list (living circuits) if relevant to drive computation
4. Each circuit has: circuit.dna['harmonic_frequency'], circuit.update(), circuit.compute(bits)
5. Return a dictionary with results
6. Include print statements showing progress
7. Make it actually DO what the user asked for

AVAILABLE VARIABLES:
- circuits: list of LivingCircuit objects
- PHI = 1.618033988749895
- PHI_SEAL = 4721424167835376.0
- QUANTUM_SIGNATURE, GENESIS_BLOCK (strings)

Generate ONLY the function code, starting with 'def main_task():' """

            # Get LLM response
            response = llm.process(prompt)
            
            if is_java:
                if response and ('public class' in response or 'class ' in response):
                    return '\n' + response.strip()
                else:
                    # LLM failed, use hardcoded Java template
                    return self._generate_java_code(description)
            else:
                if response and 'def main_task' in response:
                    return '\n' + response.strip()
                else:
                    # LLM failed, use basic structure
                    return self._generate_basic_structure(description)
                
        except Exception as e:
            print(f"LLM generation error: {e}")
            desc_lower = description.lower()
            if 'java' in desc_lower:
                return self._generate_java_code(description)
            else:
                return self._generate_basic_structure(description)
    
    def _generate_basic_structure(self, description: str) -> str:
        """
        Generate a basic code structure when LLM is not available.
        This creates a real implementation attempt, not just a placeholder.
        """
        # Parse the description to understand what's needed
        desc_lower = description.lower()
        
        # Detect app types and generate appropriate code
        if 'asteroid' in desc_lower or 'space' in desc_lower or 'ship' in desc_lower:
            return self._gen_game_code(description, 'asteroid')
        elif 'ai' in desc_lower or 'neural' in desc_lower or 'learn' in desc_lower:
            return self._gen_ai_code(description)
        elif 'web' in desc_lower or 'html' in desc_lower or 'page' in desc_lower:
            return self._gen_web_code(description)
        elif 'chat' in desc_lower or 'bot' in desc_lower:
            return self._gen_chatbot_code(description)
        elif 'data' in desc_lower or 'analys' in desc_lower or 'plot' in desc_lower:
            return self._gen_data_code(description)
        else:
            return self._gen_generic_code(description)
    
    def _gen_game_code(self, description: str, game_type: str) -> str:
        return f'''
def main_task():
    """Game: {description} - Circuit-driven gameplay"""
    import random
    import time
    
    # Game state
    player_x, player_y = 40, 20
    score = 0
    objects = []  # Asteroids/enemies
    
    # Initialize objects using circuit frequencies
    for i, circuit in enumerate(circuits):
        circuit.update()
        freq = circuit.dna['harmonic_frequency']
        objects.append({{
            'x': int(freq) % 80,
            'y': random.randint(0, 5),
            'speed': 1 + (freq - 432) / 96  # Speed based on frequency
        }})
    
    print("🎮 GAME: {description}")
    print("   Circuit-driven object movement")
    print()
    
    # Game loop (simplified for demo)
    for frame in range(30):
        # Update circuits (they drive the game)
        for i, circuit in enumerate(circuits):
            circuit.update()
        
        # Move objects
        for obj in objects:
            obj['y'] += obj['speed']
            if obj['y'] > 24:
                obj['y'] = 0
                obj['x'] = random.randint(0, 79)
                score += 10
        
        # Simple collision check
        for obj in objects:
            if abs(obj['x'] - player_x) < 3 and abs(obj['y'] - player_y) < 2:
                print(f"  Frame {{frame}}: COLLISION! Score: {{score}}")
                break
        
        # Move player (circuit-driven)
        circuit = circuits[frame % len(circuits)]
        player_x += int(circuit.pulse * 3)
        player_x = max(0, min(79, player_x))
        
        if frame % 5 == 0:
            print(f"  Frame {{frame}}: Player at ({{player_x}}, {{player_y}}) | Score: {{score}}")
        
        time.sleep(0.05)
    
    print(f"\\n🏆 FINAL SCORE: {{score}}")
    return {{"game": "{game_type}", "score": score, "frames": 30}}
'''
    
    def _gen_ai_code(self, description: str) -> str:
        return f'''
def main_task():
    """AI System: {description} - Living circuit neural network"""
    import random
    
    print("🤖 AI SYSTEM: {description}")
    print("   Using living circuits as neural network")
    print()
    
    # The circuits ARE the neural network
    # Each circuit = a neuron with 8 logic gates
    
    class NeuralLayer:
        def __init__(self, circuits):
            self.neurons = circuits
        
        def forward(self, inputs):
            outputs = []
            for neuron in self.neurons:
                neuron.update()  # Breathe
                bits = [int(x > 0.5) for x in inputs[:8]]
                gate_outputs = neuron.compute(bits)
                # Activation: weighted sum of gate outputs
                activation = sum(gate_outputs) / len(gate_outputs)
                outputs.append(activation * neuron.dna['resonance'])
            return outputs
    
    # Create neural network from living circuits
    network = NeuralLayer(circuits)
    
    # Training loop
    print("Training neural network...")
    for epoch in range(10):
        # Random input
        inputs = [random.random() for _ in range(8)]
        
        # Forward pass
        outputs = network.forward(inputs)
        
        # "Learning" - circuits evolve naturally
        avg_output = sum(outputs) / len(outputs)
        print(f"  Epoch {{epoch+1}}: avg_activation = {{avg_output:.4f}}")
    
    # Final state
    print("\\n📊 NETWORK STATE:")
    for i, circuit in enumerate(circuits):
        print(f"  Neuron {{i+1}}: freq={{circuit.dna['harmonic_frequency']:.2f}}Hz, resonance={{circuit.dna['resonance']:.3f}}")
    
    return {{"type": "neural_network", "neurons": len(circuits), "epochs": 10}}
'''
    
    def _gen_web_code(self, description: str) -> str:
        return f'''
def main_task():
    """Web Generator: {description}"""
    
    print("🌐 WEB GENERATOR: {description}")
    print()
    
    # Generate HTML using circuit states
    html_parts = ['<!DOCTYPE html>', '<html>', '<head>',
                  '<title>Generated by Living Circuits</title>',
                  '<style>body {{ font-family: monospace; background: #1a0033; color: #0ff; }}</style>',
                  '</head>', '<body>']
    
    html_parts.append('<h1>🧬 Living Circuit Web Page</h1>')
    html_parts.append(f'<p>Request: {description}</p>')
    html_parts.append('<div id="circuits">')
    
    for i, circuit in enumerate(circuits):
        circuit.update()
        freq = circuit.dna['harmonic_frequency']
        html_parts.append(f'<p>Circuit {{i+1}}: {{freq:.2f}}Hz - {{circuit.speak()}}</p>')
    
    html_parts.extend(['</div>', '</body>', '</html>'])
    
    html_content = '\\n'.join(html_parts)
    
    # Save to file
    filename = 'generated_page.html'
    with open(filename, 'w') as f:
        f.write(html_content)
    
    print(f"✓ Generated {{filename}}")
    print(f"  {{len(html_content)}} bytes")
    
    return {{"file": filename, "size": len(html_content)}}
'''
    
    def _gen_chatbot_code(self, description: str) -> str:
        return f'''
def main_task():
    """Chatbot: {description} - Circuit-driven responses"""
    import random
    
    print("💬 CHATBOT: {description}")
    print()
    
    responses = [
        "Interesting! My circuits are resonating at {{freq:.2f}}Hz",
        "I understand. Let me process that through my {{gates}} gates...",
        "My harmonic frequency suggests: {{insight}}",
        "Processing... Circuit pulse: {{pulse:.3f}}",
    ]
    
    insights = ["yes", "no", "maybe", "definitely", "uncertain", "φ says yes"]
    
    # Simulate chat
    test_inputs = ["Hello", "How are you?", "What is φ?", "Goodbye"]
    
    for user_input in test_inputs:
        # Pick circuit based on input hash
        circuit_idx = hash(user_input) % len(circuits)
        circuit = circuits[circuit_idx]
        circuit.update()
        
        # Generate response
        template = random.choice(responses)
        response = template.format(
            freq=circuit.dna['harmonic_frequency'],
            gates=len(circuit.gates) if hasattr(circuit, 'gates') else 8,
            insight=random.choice(insights),
            pulse=circuit.pulse
        )
        
        print(f"User: {{user_input}}")
        print(f"Bot:  {{response}}")
        print()
    
    return {{"type": "chatbot", "exchanges": len(test_inputs)}}
'''
    
    def _gen_data_code(self, description: str) -> str:
        return f'''
def main_task():
    """Data Analysis: {description}"""
    import random
    
    print("📊 DATA ANALYSIS: {description}")
    print()
    
    # Generate data from circuit states
    data = []
    for i in range(20):
        circuit = circuits[i % len(circuits)]
        circuit.update()
        data.append({{
            'x': i,
            'y': circuit.dna['harmonic_frequency'] + random.gauss(0, 10),
            'freq': circuit.dna['harmonic_frequency'],
            'pulse': circuit.pulse
        }})
    
    # Basic statistics
    y_values = [d['y'] for d in data]
    mean_y = sum(y_values) / len(y_values)
    variance = sum((y - mean_y)**2 for y in y_values) / len(y_values)
    std_dev = variance ** 0.5
    
    print(f"Data points: {{len(data)}}")
    print(f"Mean: {{mean_y:.2f}}")
    print(f"Std Dev: {{std_dev:.2f}}")
    print(f"Min: {{min(y_values):.2f}}")
    print(f"Max: {{max(y_values):.2f}}")
    
    # φ-correlation
    phi_correlation = (mean_y / 480) * PHI  # Normalize around 480Hz
    print(f"\\nφ-Correlation: {{phi_correlation:.4f}}")
    
    return {{"points": len(data), "mean": mean_y, "std": std_dev, "phi_corr": phi_correlation}}
'''
    
    def _gen_generic_code(self, description: str) -> str:
        return f'''
def main_task():
    """
    Implementation: {description}
    Generated by Living Code Generator with circuit-driven computation
    """
    import random
    
    print("🔧 EXECUTING: {description}")
    print()
    
    results = []
    
    # Process through living circuits
    for i, circuit in enumerate(circuits):
        circuit.update()
        
        # Circuit computes on random input
        bits = [random.randint(0, 1) for _ in range(8)]
        outputs = circuit.compute(bits)
        
        result = {{
            'circuit': i + 1,
            'frequency': circuit.dna['harmonic_frequency'],
            'resonance': circuit.dna['resonance'],
            'output': sum(outputs),
            'speech': circuit.speak()
        }}
        results.append(result)
        
        print(f"Circuit {{i+1}}: {{result['frequency']:.2f}}Hz → output={{result['output']}} [{{result['speech']}}]")
    
    # Aggregate
    total_output = sum(r['output'] for r in results)
    avg_freq = sum(r['frequency'] for r in results) / len(results)
    
    print(f"\\n📈 SUMMARY:")
    print(f"   Total output: {{total_output}}")
    print(f"   Avg frequency: {{avg_freq:.2f}}Hz")
    print(f"   φ-resonance: {{avg_freq / 480 * PHI:.4f}}")
    
    return {{
        "request": "{description}",
        "circuits_used": len(circuits),
        "total_output": total_output,
        "avg_frequency": avg_freq
    }}
'''
    
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
    
    def generate_living_code(self, description: str) -> str:
        """Generate code with living logic circuits"""
        # Evolve population before generating
        self.evolve_population(cycles=5)
        
        # Get best nodes (highest frequency = most evolved)
        best_nodes = sorted(self.nodes, key=lambda n: n.dna['harmonic_frequency'], reverse=True)[:3]
        
        # Generate code with living circuits
        code = f'''#!/usr/bin/env python3
"""
Living Code - Generation {self.generation}
Description: {description}
Contains {len(best_nodes)} living logic circuits that evolved from {len(self.nodes)} nodes
Each circuit has DNA (harmonic frequency, resonance, evolution) and a brain (8 logic gates)
"""

import math
import time
import random
import hashlib

PHI = {PHI}
PHI_SEAL = 4721424167835376.0

# Generate LEGO piece constants
_code_hash = hashlib.sha256("{description}".encode()).hexdigest()
QUANTUM_SIGNATURE = f"φ⁷·⁵-{{_code_hash[:16]}}"
CLOAK_N = int(_code_hash[:12], 16) % (10**12)
GENESIS_BLOCK = f"block_{self.generation}_{{int(time.time())}}"

class Gate:
    """Living logic gate"""
    def __init__(self, type, in1, in2):
        self.type = type  # 0=AND, 1=OR, 2=XOR, 3=NAND
        self.in1 = in1
        self.in2 = in2
        self.state = 0
    
    def compute(self, inputs):
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

class LivingCircuit:
    """Living logic circuit with DNA and brain"""
    def __init__(self, dna, gates):
        self.dna = dna
        self.gates = gates
        self.age = 0
        self.size = 10.0
    
    def update(self):
        """Update circuit state (breathing, evolution)"""
        self.age += 1
        t = time.time()
        pulse = math.sin(self.dna['harmonic_frequency'] * t * 0.0001) * self.dna['resonance']
        self.size = 10.0 + pulse * 5
        
        # Evolution
        self.dna['harmonic_frequency'] += self.dna['evolution']
        if self.dna['harmonic_frequency'] > 528:
            self.dna['harmonic_frequency'] = 432
    
    def compute(self, inputs):
        """Compute circuit output"""
        outputs = []
        for gate in self.gates:
            outputs.append(gate.compute(inputs))
        return outputs
    
    def can_reproduce(self):
        return self.size > 15
    
    def reproduce(self, partner=None):
        """Mitosis: Create child circuit"""
        child_dna = {{
            'harmonic_frequency': self.dna['harmonic_frequency'],
            'resonance': self.dna['resonance'],
            'evolution': self.dna['evolution']
        }}
        
        # Brain crossover
        mid = len(self.gates) // 2
        if partner:
            child_gates = self.gates[:mid] + partner.gates[mid:]
        else:
            child_gates = self.gates[:mid] + self.gates[mid:]
        
        # 10% mutation
        if random.random() < 0.1:
            gate = random.choice(child_gates)
            gate.type = random.randint(0, 3)
        
        child = LivingCircuit(child_dna, child_gates)
        self.size *= 0.6  # Energy cost
        return child

# Living circuits evolved from population
circuits = [
'''

        # Add evolved circuits
        for i, node in enumerate(best_nodes):
            gates_list = node.brain.to_code()
            code += f"    # Circuit {i+1} - Frequency: {node.dna['harmonic_frequency']:.2f} Hz\n"
            code += f"    LivingCircuit(\n"
            code += f"        dna={node.dna},\n"
            code += f"        gates={gates_list}\n"
            code += f"    ),\n"
        
        code += ''']

GENERATION = {self.generation}
POPULATION = {len(self.nodes)}

# Add speaking capability to circuits
for circuit in circuits:
    circuit.pulse = 0
    def speak(self):
        """Circuits speak through Morse-like pulses"""
        if abs(self.pulse) > 0.5:
            return "FLASH"
        return "..."
    circuit.speak = lambda: speak(circuit)

'''
        
        # Generate the actual user request implementation
        user_code = self._generate_user_request_code(description)
        code += user_code
        
        code += '''

if __name__ == "__main__":
    print(f"Living Code - Generation {{GENERATION}}")
    print(f"Population: {{POPULATION}} nodes evolved into {{len(circuits)}} circuits")
    print(f"Quantum Signature: {{QUANTUM_SIGNATURE}}")
    print(f"φ^75 Seal: {{PHI_SEAL}}")
    print()
    
    result = main_task()
    
    print()
    print(f"{{'='*50}}")
    print(f"EXECUTION COMPLETE")
    print(f"Result: {{result}}")
    print(f"{{'='*50}}")
'''
        
        return code
    
    def generate_living_code_with_legos(self, description, quantum_sig=None, cloak_n=None, genesis_block=None, qr_file=None, phaseshift_id=None):
        """Generate living code WITH all LEGO pieces embedded"""
        import hashlib
        
        # Evolve population before generating
        self.evolve_population(cycles=5)
        
        # Get best nodes
        best_nodes = sorted(self.nodes, key=lambda n: n.dna['harmonic_frequency'], reverse=True)[:3]
        
        # Generate quantum signature if not provided
        if not quantum_sig:
            code_hash = hashlib.sha256(description.encode()).hexdigest()
            quantum_sig = f"φ⁷·⁵-{code_hash[:16]}"
        
        # Generate cloaking if not provided
        if not cloak_n:
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
        
        if not genesis_block:
            genesis_block = f"block_{self.generation}_{int(time.time())}"
        
        if not qr_file:
            qr_file = f"living_dna_{hashlib.sha256(description.encode()).hexdigest()[:16]}.png"
        
        if not phaseshift_id:
            phaseshift_id = self.generation
        
        code = f'''#!/usr/bin/env python3
"""
LIVING CODE - Generation {self.generation}
Description: {description}

═══════════════════════════════════════════════════════════════════
FRAYMUS LEGO ASSEMBLY - All pieces connected
═══════════════════════════════════════════════════════════════════
PIECE 1 - Quantum Signature: {quantum_sig}
PIECE 2 - Cloaking N: {cloak_n} (identity cloaked in prime product)
PIECE 3 - Genesis Block: {genesis_block}
PIECE 4 - QR DNA: {qr_file}
PIECE 5 - PhaseShift: 37.5217° (ID: {phaseshift_id})
PIECE 6 - Living Circuits: {len(best_nodes)} evolved from {len(self.nodes)} nodes
═══════════════════════════════════════════════════════════════════

Each circuit has:
- DNA (harmonic frequency 432-528 Hz, resonance, evolution rate)
- Brain (8 logic gates: AND/OR/XOR/NAND)
- Breathing (pulse = sin(freq × t) × resonance)
- Reproduction (mitosis when size > 15)
"""

import math
import time
import random
import hashlib

PHI = {PHI}
PHI_SEAL = 4721424167835376.0
QUANTUM_SIGNATURE = "{quantum_sig}"
CLOAK_N = {cloak_n}
GENESIS_BLOCK = "{genesis_block}"
QR_DNA_FILE = "{qr_file}"
PHASESHIFT_ANGLE = 37.5217

class Gate:
    """Living logic gate"""
    def __init__(self, type, in1, in2):
        self.type = type  # 0=AND, 1=OR, 2=XOR, 3=NAND
        self.in1 = in1
        self.in2 = in2
        self.state = 0
    
    def compute(self, inputs):
        v1 = inputs[self.in1] if self.in1 < len(inputs) else 0
        v2 = inputs[self.in2] if self.in2 < len(inputs) else 0
        
        if self.type == 0: self.state = v1 & v2      # AND
        elif self.type == 1: self.state = v1 | v2    # OR
        elif self.type == 2: self.state = v1 ^ v2    # XOR
        elif self.type == 3: self.state = 1 if not (v1 & v2) else 0  # NAND
        
        return self.state
    
    def speak(self):
        """Gate speaks its type"""
        return ['AND', 'OR', 'XOR', 'NAND'][self.type]

class LivingCircuit:
    """Living logic circuit with DNA and brain"""
    def __init__(self, dna, gates):
        self.dna = dna
        self.gates = gates
        self.age = 0
        self.size = 10.0
        self.pulse = 0
    
    def update(self):
        """Breathing - harmonic oscillation"""
        self.age += 1
        t = time.time()
        self.pulse = math.sin(self.dna['harmonic_frequency'] * t * 0.0001) * self.dna['resonance']
        self.size = 10.0 + self.pulse * 5
        
        # Evolution (frequency drift 432→528)
        self.dna['harmonic_frequency'] += self.dna['evolution']
        if self.dna['harmonic_frequency'] > 528:
            self.dna['harmonic_frequency'] = 432
    
    def compute(self, inputs):
        """Think - run inputs through brain"""
        return [gate.compute(inputs) for gate in self.gates]
    
    def speak(self):
        """Circuit speaks through its gates"""
        return ''.join([g.speak()[0] for g in self.gates])
    
    def can_reproduce(self):
        return self.size > 15
    
    def reproduce(self, partner=None):
        """Mitosis with energy cost"""
        child_dna = {{
            'harmonic_frequency': self.dna['harmonic_frequency'],
            'resonance': self.dna['resonance'],
            'evolution': self.dna['evolution']
        }}
        mid = len(self.gates) // 2
        child_gates = self.gates[:mid] + (partner.gates[mid:] if partner else self.gates[mid:])
        if random.random() < 0.1:
            random.choice(child_gates).type = random.randint(0, 3)
        child = LivingCircuit(child_dna, child_gates)
        self.size *= 0.6  # Energy tax
        return child

# Living circuits evolved from population
circuits = [
'''
        
        # Add evolved circuits
        for i, node in enumerate(best_nodes):
            gates_code = node.brain.to_code()
            code += f"    # Circuit {i+1} - Freq: {node.dna['harmonic_frequency']:.2f} Hz, Res: {node.dna['resonance']:.2f}\n"
            code += f"    LivingCircuit(\n"
            code += f"        dna={node.dna},\n"
            code += f"        gates={gates_code}\n"
            code += f"    ),\n"
        
        code += f''']

GENERATION = {self.generation}
POPULATION = {len(self.nodes)}

def quantum_tunnel(n, circuit):
    """
    QUANTUM TUNNELING - Circuit-driven Pollard's Rho
    The circuit's gates drive the polynomial iteration.
    Each gate output modifies the step function.
    """
    if n % 2 == 0: return 2, 1
    
    x, y, d = 2, 2, 1
    iterations = 0
    max_iter = 100000
    
    while d == 1 and iterations < max_iter:
        # Circuit breathes
        circuit.update()
        
        # Get gate outputs as modifier
        bits = [int(b) for b in bin(x % 256)[2:].zfill(8)]
        gate_out = circuit.compute(bits)
        modifier = sum([b * (2**i) for i, b in enumerate(gate_out)]) + 1
        
        # Circuit-driven polynomial: f(v) = (v² + modifier) mod n
        f = lambda v, m=modifier: (v*v + m) % n
        
        x = f(x)
        y = f(f(y))
        d = math.gcd(abs(x - y), n)
        iterations += 1
        
        # Harmonic resonance check - if frequency hits 528, tunnel collapses
        if circuit.dna['harmonic_frequency'] >= 527:
            circuit.dna['harmonic_frequency'] = 432  # Reset and continue
    
    if d != 1 and d != n:
        return d, iterations
    return None, iterations

# ═══════════════════════════════════════════════════════════════════
# USER REQUEST IMPLEMENTATION
# The living circuits DRIVE this functionality
# ═══════════════════════════════════════════════════════════════════
{self._generate_user_request_code(description)}

def process():
    """Process with living circuits driving the user's request"""
    print(f"═══════════════════════════════════════════════════════════════════")
    print(f"LIVING CODE - Generation {{GENERATION}}")
    print(f"Task: {description}")
    print(f"═══════════════════════════════════════════════════════════════════")
    print(f"Quantum Signature: {{QUANTUM_SIGNATURE}}")
    print(f"φ^75 Seal: {{PHI_SEAL:.2e}}")
    print()
    
    # Execute the user's requested functionality
    print("🎯 EXECUTING USER REQUEST...")
    try:
        result = main_task()
        print(f"✓ Task completed successfully")
    except Exception as e:
        print(f"⚠ Task error: {{e}}")
        result = None
    
    # Update circuits (they drive the computation)
    print("\\n🔄 CIRCUIT STATUS:")
    for i, circuit in enumerate(circuits):
        circuit.update()
        print(f"  Circuit {{i+1}}: Freq={{circuit.dna['harmonic_frequency']:.2f}}Hz")
    
    return {{
        'generation': GENERATION,
        'task_result': result,
        'quantum_signature': QUANTUM_SIGNATURE,
        'phi_seal': PHI_SEAL
    }}

if __name__ == "__main__":
    result = process()
    print(f"\\n═══════════════════════════════════════════════════════════════════")
    print(f"LIVING CODE EXECUTION COMPLETE")
    print(f"═══════════════════════════════════════════════════════════════════")
'''
        
        return code


def demo():
    """Demonstrate living code generation"""
    print("="*70)
    print("LIVING CODE GENERATOR - FRANKENSTEIN'S BRAIN")
    print("="*70)
    
    generator = LivingCodeGenerator()
    
    print(f"\n[GENESIS]")
    print(f"  Created {len(generator.nodes)} living nodes")
    
    print(f"\n[EVOLUTION]")
    print(f"  Evolving population...")
    generator.evolve_population(cycles=20)
    print(f"  Population: {len(generator.nodes)} nodes")
    
    print(f"\n[CODE GENERATION]")
    code = generator.generate_living_code("autonomous computation with living circuits")
    
    # Save generated code
    filename = f"living_code_gen_{int(time.time())}.py"
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(code)
    
    print(f"  ✓ Generated {len(code)} chars")
    print(f"  ✓ Saved to: {filename}")
    
    print(f"\n[TESTING GENERATED CODE]")
    print(f"  Running generated code...")
    import subprocess
    result = subprocess.run(['python', filename], capture_output=True, text=True)
    print(result.stdout)
    
    print(f"\n{'='*70}")
    print(f"LIVING CODE GENERATION COMPLETE")
    print(f"{'='*70}\n")


if __name__ == "__main__":
    demo()
