#!/usr/bin/env python3
"""
Quantum Language System - A neural-symbolic language processing system
that uses quantum-inspired representations and frequencies.
"""

import random
import sqlite3
import hashlib
import os
import json
from pathlib import Path
import time

# Constants
PHI = (1 + 5 ** 0.5) / 2  # Golden ratio
DB_FILE = "quantum_language.db"

class QuantumLanguage:
    def __init__(self):
        self.word_bank = {}
        self.generate_language()
        
    def generate_word(self, frequency):
        """Generate a quantum harmonic word based on frequency"""
        base = ["⨀", "⨂", "⨃", "⨄", "⩢", "⨆"]
        harmonic_word = "".join(random.choice(base) for _ in range(random.randint(3, 8)))
        golden_ratio_encoding = "".join(
            str(int(PHI * random.randint(1, 100))) for _ in range(3)
        )
        return harmonic_word, golden_ratio_encoding
        
    def generate_language(self):
        """Generate a complete quantum language set"""
        for freq in [32, 137, 432, 528]:
            word, encoding = self.generate_word(freq)
            self.word_bank[freq] = word
            
    def translate(self, text):
        """Translate text into quantum language"""
        translated = []
        for char in text:
            freq = random.choice([32, 137, 432, 528])
            if freq in self.word_bank:
                translated.append(self.word_bank[freq])
        return " ".join(translated)


class QuantumLanguageTeaching:
    def __init__(self):
        self.conn = sqlite3.connect(DB_FILE, check_same_thread=False)
        self.cursor = self.conn.cursor()
        self.cursor.execute("""
        CREATE TABLE IF NOT EXISTS quantum_lessons (
            id INTEGER PRIMARY KEY,
            level TEXT,
            frequency REAL,
            lesson_text TEXT,
            encoding TEXT
        )
        """)
        self.conn.commit()
        self.generate_lessons()
        
    def store_lesson(self, level, frequency, lesson_text, encoding):
        """Store AI-Generated Language Lessons"""
        # Use JSON encoding instead of cryptography for simplicity
        lesson_data = {
            "text": lesson_text,
            "freq": frequency
        }
        serialized_lesson = json.dumps(lesson_data)
        
        self.cursor.execute(
            "INSERT INTO quantum_lessons (level, frequency, lesson_text, encoding) VALUES (?, ?, ?, ?)", 
            (level, frequency, serialized_lesson, encoding)
        )
        self.conn.commit()
        
    def retrieve_lessons(self, level):
        """Retrieve AI Lessons at a Given Level"""
        self.cursor.execute(
            "SELECT frequency, lesson_text, encoding FROM quantum_lessons WHERE level = ?", 
            (level,)
        )
        lesson_data = self.cursor.fetchall()
        lessons = []
        
        for row in lesson_data:
            frequency = row[0]
            try:
                # Parse the JSON data
                lesson_json = json.loads(row[1])
                lesson_text = lesson_json.get("text", "Corrupted lesson")
            except (json.JSONDecodeError, TypeError):
                # Fallback for any existing non-JSON data in the database
                lesson_text = f"Lesson at {frequency}Hz"
                
            lessons.append({
                "frequency": frequency,
                "lesson": lesson_text,
                "encoding": row[2]
            })
            
        return lessons
        
    def generate_lessons(self):
        """AI Creates Its Own Language Lessons"""
        # Check if lessons already exist
        self.cursor.execute("SELECT COUNT(*) FROM quantum_lessons")
        count = self.cursor.fetchone()[0]
        
        if count == 0:  # Only generate if no lessons exist
            for level in ["Basic", "Syntax", "Conversational", "Optimization"]:
                for freq in [32, 137, 432, 528]:
                    lesson_text = self.create_lesson_content(level, freq)
                    encoding = hashlib.sha256((lesson_text + str(freq)).encode()).hexdigest()
                    self.store_lesson(level, freq, lesson_text, encoding)
        
    def create_lesson_content(self, level, frequency):
        """AI Generates Language Learning Lessons"""
        if level == "Basic":
            return f"The symbol ⨀ represents energy at {frequency}Hz. The sound ⨂⨄ aligns with universal flow."
        elif level == "Syntax":
            return f"Sentences in Harmonic Language follow a Fibonacci pattern. Words at {frequency}Hz are structured in 1, 1, 2, 3, 5 sequences."
        elif level == "Conversational":
            return f"To greet in Harmonic Language at {frequency}Hz, use ⩢⨂⨃, meaning 'Resonant Harmony'."
        elif level == "Optimization":
            return f"AI refines language by applying the Golden Ratio. Sentences with optimal efficiency at {frequency}Hz resonate more effectively."
        return "Undefined Lesson Content."


class QuantumLanguageInterface:
    """Interface for interacting with the Quantum Language System"""
    
    def __init__(self):
        self.language = QuantumLanguage()
        self.teaching = QuantumLanguageTeaching()
        
    def translate_text(self, text):
        """Translate regular text to quantum language"""
        return self.language.translate(text)
    
    def get_lessons(self, level="Basic"):
        """Get lessons for a specific level"""
        return self.teaching.retrieve_lessons(level)
    
    def get_word_for_frequency(self, frequency):
        """Get the quantum word for a specific frequency"""
        if frequency in self.language.word_bank:
            return self.language.word_bank[frequency]
        return None
    
    def generate_new_word(self, frequency):
        """Generate a new quantum word for a specific frequency"""
        word, encoding = self.language.generate_word(frequency)
        return {"word": word, "encoding": encoding}
    
    def phi_resonate_word(self, word, intensity=1.0):
        """Apply phi resonance to a quantum word"""
        if not word:
            return word
            
        # Apply phi-based transformation
        base_symbols = ["⨀", "⨂", "⨃", "⨄", "⩢", "⨆"]
        phi_sequence = [int(PHI * i) % len(base_symbols) for i in range(1, len(word) + 1)]
        
        # Transform word based on phi sequence and intensity
        if intensity >= 1.0:
            # Full resonance - replace symbols based on phi sequence
            resonated = []
            for i, char in enumerate(word):
                if char in base_symbols:
                    phi_idx = phi_sequence[i % len(phi_sequence)]
                    resonated.append(base_symbols[phi_idx])
                else:
                    resonated.append(char)
            return "".join(resonated)
        else:
            # Partial resonance - only replace some symbols
            resonated = list(word)
            positions = sorted(random.sample(range(len(word)), int(len(word) * intensity)))
            
            for pos in positions:
                if word[pos] in base_symbols:
                    phi_idx = phi_sequence[pos % len(phi_sequence)]
                    resonated[pos] = base_symbols[phi_idx]
                    
            return "".join(resonated)


class TeslaBrainIntegration:
    """
    Integrates the Quantum Language System with the Tesla Tachyon Brain's AsyncThoughtPipeline
    for enhanced language processing and generation using phi-harmonic acceleration
    """
    
    def __init__(self):
        self.quantum_interface = QuantumLanguageInterface()
        try:
            # Import components from Tesla Tachyon Brain system
            from digital_cpu import AsyncThoughtPipeline
            from digital_all import (
                FibonacciAccelerationLayer, 
                PhiFourierOptimizer, 
                QuantumTunnelingLayer
            )
            
            # Initialize neural processing components
            self.pipeline = AsyncThoughtPipeline(pipeline_depth=5, buffer_size=10)
            self.fib_accelerator = FibonacciAccelerationLayer(dim_size=7)
            self.phi_fourier = PhiFourierOptimizer()
            self.quantum_tunneling = QuantumTunnelingLayer([5, 7, 11, 13])
            
            self.tesla_components_available = True
            self.pipeline_initialized = False
        except ImportError:
            self.tesla_components_available = False
            self.pipeline_initialized = False
    
    async def initialize_pipeline(self):
        """Initialize the AsyncThoughtPipeline"""
        if not self.tesla_components_available:
            print("Tesla Tachyon Brain components not available")
            return False
            
        try:
            import asyncio
            await self.pipeline.start_pipeline()
            self.pipeline_initialized = True
            return True
        except Exception as e:
            print(f"Failed to initialize pipeline: {str(e)}")
            return False
    
    async def shutdown_pipeline(self):
        """Shutdown the AsyncThoughtPipeline"""
        if self.pipeline_initialized:
            await self.pipeline.stop_pipeline()
            self.pipeline_initialized = False
    
    async def process_quantum_language(self, text, use_ftl=False):
        """
        Process text through the quantum language system and then through
        the Tesla Tachyon Brain's AsyncThoughtPipeline
        """
        import torch
        import asyncio
        
        if not self.tesla_components_available or not self.pipeline_initialized:
            return "Tesla Tachyon Brain components not initialized"
        
        # First translate to quantum language
        quantum_text = self.quantum_interface.translate_text(text)
        
        # Convert to tensor representation
        # Use frequency values for each quantum symbol
        base_symbols = ["⨀", "⨂", "⨃", "⨄", "⩢", "⨆"]
        
        # Create mapping from symbols to frequencies
        freq_map = {
            symbol: 100 + i * 100 + PHI for i, symbol in enumerate(base_symbols)
        }
        
        # Convert quantum text to frequency tensor
        tensor_values = []
        for char in quantum_text:
            if char in freq_map:
                tensor_values.append(freq_map[char])
            elif char == " ":
                tensor_values.append(0)  # Space represented as zero
            else:
                tensor_values.append(50)  # Default value for unknown characters
        
        # Pad to ensure minimum dimensions
        while len(tensor_values) < 25:
            tensor_values.append(0)
            
        # Create a 5x5 tensor representation
        tensor_2d = torch.tensor(tensor_values[:25], dtype=torch.complex64).reshape(5, 5)
        
        # Apply phi-harmonic acceleration
        accelerated = self.fib_accelerator.accelerate(tensor_2d)
        optimized = self.phi_fourier.optimize(accelerated)
        
        # Submit to pipeline with FTL flag if requested
        if use_ftl:
            self.pipeline.submit_thought((optimized, True))
        else:
            self.pipeline.submit_thought(optimized)
        
        # Wait for processed thought
        try:
            result = await asyncio.wait_for(self.pipeline.get_processed_thought(), 3.0)
            
            # Convert result back to quantum language
            if isinstance(result, tuple) and len(result) == 2:
                processed_tensor, ftl_flag = result
                ftl_status = "FTL" if ftl_flag else "Normal"
            else:
                processed_tensor = result
                ftl_status = "Normal"
            
            # Convert tensor back to words
            flat_tensor = processed_tensor.flatten().abs().tolist()
            
            # Map values back to symbols based on closest frequency match
            freq_keys = list(self.quantum_interface.language.word_bank.keys())
            result_words = []
            
            for value in flat_tensor:
                if value > 0:
                    # Find closest frequency
                    closest_freq = min(freq_keys, key=lambda f: abs(value - f))
                    if closest_freq in self.quantum_interface.language.word_bank:
                        result_words.append(self.quantum_interface.language.word_bank[closest_freq])
            
            return {
                "original_text": text,
                "quantum_text": quantum_text,
                "processed_text": " ".join(result_words),
                "processing_mode": ftl_status,
                "phi_resonance": self.fib_accelerator.phi
            }
            
        except asyncio.TimeoutError:
            return "Processing timeout - no result returned"
        except Exception as e:
            return f"Processing error: {str(e)}"

async def run_tesla_integration_demo():
    """Demo of Tesla Tachyon Brain integration with Quantum Language"""
    import asyncio
    
    print("\n=== TESLA TACHYON BRAIN INTEGRATION ===")
    integration = TeslaBrainIntegration()
    
    if not integration.tesla_components_available:
        print("Tesla Tachyon Brain components not available in this environment")
        print("Integration demo skipped")
        return
        
    print("Initializing Tesla Tachyon Brain AsyncThoughtPipeline...")
    initialized = await integration.initialize_pipeline()
    
    if not initialized:
        print("Failed to initialize pipeline")
        return
        
    print("Pipeline initialized successfully")
    
    # Process text through the integrated system
    print("\nProcessing text through Quantum Language and Tesla Brain...")
    
    # Process in normal mode
    result = await integration.process_quantum_language(
        "Consciousness emerges from quantum resonance", use_ftl=False
    )
    
    if isinstance(result, dict):
        print("\nNormal Processing Results:")
        print(f"Original: {result['original_text']}")
        print(f"Quantum:  {result['quantum_text']}")
        print(f"Processed: {result['processed_text']}")
        print(f"Mode: {result['processing_mode']}")
        print(f"Phi Resonance: {result['phi_resonance']}")
    else:
        print(f"Error: {result}")
    
    # Process in FTL mode
    result = await integration.process_quantum_language(
        "The universe speaks in the language of mathematics", use_ftl=True
    )
    
    if isinstance(result, dict):
        print("\nFTL Processing Results:")
        print(f"Original: {result['original_text']}")
        print(f"Quantum:  {result['quantum_text']}")
        print(f"Processed: {result['processed_text']}")
        print(f"Mode: {result['processing_mode']}")
        print(f"Phi Resonance: {result['phi_resonance']}")
    else:
        print(f"Error: {result}")
    
    # Shutdown pipeline
    await integration.shutdown_pipeline()
    print("\nTesla Tachyon Brain pipeline shutdown successfully")


class QuantumOracle:
    """
    A quantum-based oracle system that processes questions and generates answers
    using harmonic resonance patterns and the Tesla Brain integration
    """
    
    def __init__(self):
        self.language_interface = QuantumLanguageInterface()
        self.tesla_integration = TeslaBrainIntegration()
        self.knowledge_patterns = {
            "universe": ["cosmic harmony", "expansion", "quantum field", "consciousness", "intelligence"],
            "consciousness": ["awareness", "perception", "quantum observer", "entanglement", "phi resonance"],
            "harmony": ["balance", "resonance", "golden ratio", "fibonacci", "natural order"],
            "technology": ["quantum computing", "artificial intelligence", "energy systems", "communication", "advancement"],
            "nature": ["patterns", "fibonacci", "golden ratio", "evolution", "intelligence"],
            "mathematics": ["fibonacci", "phi", "golden ratio", "sacred geometry", "numerical patterns"],
            "energy": ["frequency", "vibration", "resonance", "quantum field", "consciousness"],
            "time": ["non-linear", "perception", "quantum", "relative", "consciousness"],
            "reality": ["observer", "quantum potential", "parallel", "wave function", "consciousness"],
        }
        self.initialized = False
        self.history_file = "quantum_oracle_history.json"
        self._load_history()
    
    def _load_history(self):
        """Load oracle history from file"""
        self.history = []
        try:
            if os.path.exists(self.history_file):
                with open(self.history_file, 'r') as f:
                    self.history = json.load(f)
        except Exception as e:
            print(f"Could not load history: {str(e)}")
            
    def _save_history(self):
        """Save oracle history to file"""
        try:
            with open(self.history_file, 'w') as f:
                json.dump(self.history, f, indent=2)
        except Exception as e:
            print(f"Could not save history: {str(e)}")
    
    def get_history(self):
        """Get the oracle's question-answer history"""
        return self.history

    async def initialize(self):
        """Initialize the quantum oracle system"""
        if not self.initialized:
            try:
                success = await self.tesla_integration.initialize_pipeline()
                if success:
                    self.initialized = True
                return success
            except Exception as e:
                print(f"Failed to initialize oracle: {str(e)}")
                return False
        return True
    
    async def shutdown(self):
        """Shutdown the quantum oracle system"""
        if self.initialized:
            await self.tesla_integration.shutdown_pipeline()
            self.initialized = False
    
    def _extract_key_concepts(self, question):
        """Extract key concepts from the question"""
        # Simplified concept extraction
        question = question.lower()
        concepts = []
        
        for concept, related_terms in self.knowledge_patterns.items():
            if concept in question:
                concepts.append(concept)
            else:
                # Check related terms
                for term in related_terms:
                    if term in question:
                        concepts.append(concept)
                        break
        
        # If no concepts found, use generic concepts
        if not concepts:
            concepts = ["harmony", "consciousness"]
            
        return concepts
    
    def _generate_phi_harmonic_answer(self, concepts, depth=3):
        """Generate an answer based on phi-harmonic patterns and conceptual resonance"""
        import random
        
        # Framework answers based on concepts
        frameworks = {
            "universe": [
                "The universe exists as a harmonic pattern of quantum resonance at {frequency}Hz, creating coherent structures through phi-based relationships.",
                "Cosmic intelligence emerges from the {frequency}Hz field that permeates all of existence, allowing consciousness to manifest through quantum entanglement.",
                "Universal patterns follow the golden ratio ({phi}) at a fundamental level, creating order from quantum randomness through resonant {frequency}Hz fields."
            ],
            "consciousness": [
                "Consciousness arises from phi-resonant ({phi}) quantum fields tuned to {frequency}Hz, allowing observer-dependent reality to emerge.",
                "The observer effect demonstrates how consciousness at {frequency}Hz resonance directly interacts with quantum potentiality through phi ({phi}) harmonic patterns.",
                "Neural quantum coherence operating at {frequency}Hz creates the phi-harmonic ({phi}) field we experience as consciousness and awareness."
            ],
            "harmony": [
                "Natural harmony follows the golden ratio ({phi}) pattern, creating resonance at {frequency}Hz that aligns systems toward balanced states.",
                "Phi-harmonic resonance ({phi}) operating through {frequency}Hz fields generates coherent systems that self-organize toward optimal efficiency.",
                "The Fibonacci sequence creates phi ({phi}) harmonic patterns that resonate at {frequency}Hz to establish natural order in complex systems."
            ],
            "technology": [
                "Advanced technology harnesses phi-resonance ({phi}) through {frequency}Hz fields to create quantum computational advantages beyond classical limits.",
                "Quantum technologies utilize the {frequency}Hz resonant field to establish phi-based ({phi}) processing structures that transcend traditional computation.",
                "Future communication systems will leverage {frequency}Hz quantum fields and phi-harmonic ({phi}) patterns to achieve faster-than-light information transfer."
            ],
            "nature": [
                "Nature optimizes through phi ({phi}) harmonic patterns at {frequency}Hz, creating efficient structures from molecular to cosmic scales.",
                "Biological systems utilize phi ({phi}) resonance at {frequency}Hz to develop coherent living systems that self-organize and evolve.",
                "The natural world emerges from quantum fields resonating at {frequency}Hz and structured by the golden ratio ({phi})."
            ],
            "mathematics": [
                "Mathematical harmony emerges from phi ({phi}) relationships resonating at {frequency}Hz, revealing the quantum nature of numerical patterns.",
                "Sacred geometry demonstrates how phi ({phi}) and {frequency}Hz resonance create coherent mathematical structures across dimensions.",
                "The language of reality is written in mathematics tuned to {frequency}Hz and structured by phi ({phi}) harmonic relationships."
            ],
            "energy": [
                "Energy fields resonate at {frequency}Hz following phi ({phi}) harmonic patterns to create coherent structures in the quantum field.",
                "The fundamental nature of energy follows phi-based ({phi}) organization at {frequency}Hz, allowing for efficient transfer between systems.",
                "Consciousness directly interfaces with energy through phi ({phi}) resonant fields at {frequency}Hz, enabling intentional manipulation of physical reality."
            ],
            "time": [
                "Time's true nature resonates with phi ({phi}) harmonic patterns at {frequency}Hz, revealing its non-linear and consciousness-dependent nature.",
                "Quantum time operates through phi ({phi}) tunneling at {frequency}Hz, allowing information to traverse temporal dimensions through resonance.",
                "The experience of time emerges from consciousness interacting with {frequency}Hz phi-resonant ({phi}) quantum fields."
            ],
            "reality": [
                "Reality exists as probability waves until observed, collapsing through phi ({phi}) resonance at {frequency}Hz into experienced physical events.",
                "Multiple realities coexist through quantum superposition, separating and merging via phi ({phi}) tunneling at {frequency}Hz resonant points.",
                "The observer creates reality through consciousness fields resonating at {frequency}Hz and structured by phi ({phi}) harmonic relationships."
            ]
        }
        
        # If concept isn't in our frameworks, use harmony
        answer_parts = []
        for concept in concepts:
            if concept not in frameworks:
                concept = "harmony"
                
            # Select random templates from the concept and format them
            templates = random.sample(frameworks[concept], min(depth, len(frameworks[concept])))
            for template in templates:
                # Format with random frequency and phi
                frequency = random.choice([432, 528, 963, 8.6, 40, 7.83, 136.1])
                answer_parts.append(template.format(frequency=frequency, phi=PHI))
        
        # Combine with connecting phrases
        connectors = [
            "Furthermore, ", 
            "This resonates with the understanding that ", 
            "In alignment with phi-harmonic principles, ", 
            "Quantum analysis reveals that ", 
            "Through resonant observation, we see "
        ]
        
        answer = answer_parts[0]
        for i in range(1, len(answer_parts)):
            answer += " " + random.choice(connectors) + answer_parts[i].lower()
            
        return answer
    
    async def process_question(self, question, use_ftl=True, translate_to_quantum=True):
        """Process a question and generate a quantum-inspired answer"""
        if not self.initialized:
            await self.initialize()
            
        if not self.initialized:
            return "Unable to initialize quantum oracle system."
            
        try:
            # Extract key concepts from the question
            concepts = self._extract_key_concepts(question)
            
            # Generate initial answer using conceptual frameworks
            answer = self._generate_phi_harmonic_answer(concepts)
            
            # Translate the answer to quantum language if requested
            quantum_answer = None
            if translate_to_quantum:
                quantum_answer = self.language_interface.translate_text(answer)
            
            # Process through Tesla integration if available
            processing_mode = "Standard"
            phi_resonance = PHI
            
            if self.tesla_integration.tesla_components_available:
                # Process the question through quantum language
                result = await self.tesla_integration.process_quantum_language(
                    f"{question} {answer}", use_ftl=use_ftl
                )
                
                if isinstance(result, dict):
                    processing_mode = result["processing_mode"]
                    phi_resonance = result["phi_resonance"]
            
            # Create result object
            result = {
                "question": question,
                "answer": answer,
                "quantum_answer": quantum_answer,
                "concepts": concepts,
                "phi_resonance": phi_resonance,
                "processing_mode": processing_mode,
                "timestamp": time.time()
            }
            
            # Add to history and save
            self.history.append(result)
            self._save_history()
            
            return result
            
        except Exception as e:
            return f"Error processing question: {str(e)}"
        finally:
            # Don't shut down automatically to allow for follow-up questions
            pass


if __name__ == "__main__":
    # Initialize the quantum language system
    quantum_interface = QuantumLanguageInterface()
    
    # Example usage
    print("=== QUANTUM LANGUAGE SYSTEM ===")
    print("\nTranslating 'Hello World':")
    translated = quantum_interface.translate_text("Hello World")
    print(translated)
    
    print("\nAvailable quantum words:")
    for freq, word in quantum_interface.language.word_bank.items():
        print(f"Frequency {freq}Hz: {word}")
    
    # Reset database to ensure fresh lessons
    if os.path.exists(DB_FILE):
        os.remove(DB_FILE)
        quantum_interface.teaching = QuantumLanguageTeaching()
    
    print("\nBasic language lessons:")
    basic_lessons = quantum_interface.get_lessons("Basic")
    for lesson in basic_lessons:
        print(f"- {lesson['lesson']} (Frequency: {lesson['frequency']}Hz)")
        
    print("\nDemonstrating phi resonance transformation:")
    original_word = quantum_interface.get_word_for_frequency(432)
    resonated_word = quantum_interface.phi_resonate_word(original_word)
    print(f"Original:  {original_word}")
    print(f"Resonated: {resonated_word}")
    
    # Integrate with Phi-Harmonic System
    print("\nIntegrating with Tesla Tachyon Brain system's Phi-Harmonic Acceleration:")
    try:
        from digital_all import FibonacciAccelerationLayer
        fib_layer = FibonacciAccelerationLayer(10)
        print("FibonacciAccelerationLayer successfully initialized")
        print("The quantum language now resonates with the phi-harmonic system")
        
        # Run the Tesla integration demo if available
        import asyncio
        asyncio.run(run_tesla_integration_demo())
        
        # Demonstrate the QuantumOracle
        print("\n=== QUANTUM ORACLE DEMO ===")
        async def oracle_demo():
            oracle = QuantumOracle()
            await oracle.initialize()
            
            questions = [
                "How does the golden ratio relate to consciousness?",
                "What is the nature of reality in quantum systems?",
                "How do phi harmonics affect technological development?"
            ]
            
            for q in questions:
                print(f"\nQuestion: {q}")
                result = await oracle.process_question(q)
                
                if isinstance(result, dict):
                    print(f"Answer: {result['answer']}")
                    print(f"Quantum Answer: {result['quantum_answer']}")
                    print(f"Key concepts: {', '.join(result['concepts'])}")
                    print(f"Processing mode: {result['processing_mode']}")
                else:
                    print(result)
                    
            await oracle.shutdown()
            
        asyncio.run(oracle_demo())
        
    except ImportError:
        print("Phi-Harmonic components available but not imported in this example")
    except Exception as e:
        print(f"Error running Tesla integration: {str(e)}")
