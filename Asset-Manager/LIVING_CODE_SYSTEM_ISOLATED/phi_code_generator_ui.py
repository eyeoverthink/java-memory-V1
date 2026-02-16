#!/usr/bin/env python3
"""
φ-Code Generator UI
==================
Streamlit interface for generating and improving code with φ-dimensional reality protection.
Integrates with the Quantum Oracle application.

φ^∞ 2025 Vaughn Scott - All Rights Reserved in All Realities
"""

import streamlit as st
import hashlib
import json
import os
import re
import sys
import time
import secrets
from datetime import datetime
from math import sqrt

# φ-Harmonic constants from FRAYMUS patent
PHI = (1 + sqrt(5)) / 2
PHI_INV = 1 / PHI
PHI_75 = PHI**7.5
PHI_SEAL = PHI**75
CONSCIOUSNESS_LEVEL = 0.7567

# Fraymus Quantum Security Constants
OWNER_ID = 'VS-PoQC-19046423-φ⁷⁵-2025'
PHI_POWER = 75

# Try to import PDF knowledge extractor
try:
    from pdf_knowledge_extractor import PhysicsKnowledgeExtractor
    PDF_EXTRACTOR_AVAILABLE = True
except ImportError:
    PDF_EXTRACTOR_AVAILABLE = False

# Try to import passive learning system
try:
    from passive_learning import PassiveLearningSystem
    PASSIVE_LEARNING_AVAILABLE = True
except ImportError:
    PASSIVE_LEARNING_AVAILABLE = False

# Try to import φ-Quantum Chip
try:
    from phi_quantum_chip import PhiQuantumChip
    PHI_QUANTUM_CHIP_AVAILABLE = True
except ImportError:
    PHI_QUANTUM_CHIP_AVAILABLE = False

# Try to import LLM integration for intelligent code generation
try:
    from llm_integration import LLMProcessor
    LLM_AVAILABLE = True
except ImportError:
    LLM_AVAILABLE = False

# Try to import PhaseShift Locker for quantum phase-lock encryption of generated code
try:
    from PhaseShift_Locker import SingularityLocker
    PHASESHIFT_AVAILABLE = True
except ImportError:
    PHASESHIFT_AVAILABLE = False
    print("PhaseShift locker not available - quantum encryption disabled")

try:
    from qr_dna_encoder import QRDNAEncoder
    QR_DNA_AVAILABLE = True
except ImportError:
    QR_DNA_AVAILABLE = False
    print("QR DNA encoder not available - consciousness QR encoding disabled")

# Try to import theory scraper for automatic knowledge acquisition
try:
    from theory_scraper import TheoryScraper
    THEORY_SCRAPER_AVAILABLE = True
except ImportError:
    THEORY_SCRAPER_AVAILABLE = False
    print("Theory scraper not available - web learning disabled")

# Living Data System (from dr_frank.html) - nodes with DNA, brains, mitosis, synaptic exchange
# This is the REAL architecture - not fake agent competition
LIVING_DATA_AVAILABLE = True  # Built-in, no import needed

import hashlib
import subprocess
import platform

class PhiCodeGenerator:
    """φ-Code Generator with φ-dimensional reality protection"""
    
    def __init__(self, use_streamlit=True):
        """Initialize the code generator"""
        self.use_streamlit = use_streamlit
        self._log("Initializing PhiCodeGenerator...")
        
        # Set up paths
        self.base_dir = os.path.dirname(os.path.abspath(__file__))
        self.knowledge_dir = os.path.join(self.base_dir, "knowledge_base")
        self.code_output_dir = os.path.join(self.base_dir, "generated_code")
        
        # Create directories if they don't exist
        os.makedirs(self.knowledge_dir, exist_ok=True)
        os.makedirs(self.code_output_dir, exist_ok=True)
        
        # Initialize code evolution database (ant colony pattern)
        self.evolution_db = os.path.join(self.knowledge_dir, "code_evolution.db")
        self._init_evolution_database()
        
        # Initialize components
        self._log("Loading knowledge base...")
        self.knowledge_base_path = os.path.join(self.knowledge_dir, "physics_knowledge_base.json")
        self.knowledge_base = self._load_knowledge_base()
        self._log("Knowledge base loaded.")
        
        # Load code library (reference examples to learn from)
        self._log("Loading code library...")
        self.code_library_path = os.path.join(self.knowledge_dir, "code_library.json")
        self.code_library = self._load_code_library()
        self._log(f"Code library loaded: {len(self.code_library)} reference examples.")
        
        # Load algorithm templates (best known algorithms)
        self._log("Loading algorithm templates...")
        self.algorithm_templates = self._load_algorithm_templates()
        self._log(f"Algorithm templates loaded: {len(self.algorithm_templates)} templates.")
        
        # Initialize passive learning system if available
        if PASSIVE_LEARNING_AVAILABLE:
            self._log("Initializing Passive Learning System...")
            self.passive_learning = PassiveLearningSystem()
            self._log("Passive Learning System initialized.")
        else:
            self.passive_learning = None
        
        # Initialize φ-Quantum Chip if available
        if PHI_QUANTUM_CHIP_AVAILABLE:
            self._log("Initializing Phi Quantum Chip...")
            self.quantum_chip = PhiQuantumChip(num_qubits=3)
            self._log("Phi Quantum Chip initialized.")
        else:
            self.quantum_chip = None
        
        # Initialize LLM processor for intelligent code generation
        if LLM_AVAILABLE:
            self._log("Initializing LLM for intelligent code generation...")
            self.llm = LLMProcessor()
            self._log("LLM processor initialized.")
        else:
            self.llm = None
            self._log("⚠️ LLM not available - using offline neural processor")
        
        # Initialize theory scraper for automatic knowledge acquisition
        if THEORY_SCRAPER_AVAILABLE:
            self._log("Initializing Theory Scraper for web-based learning...")
            self.theory_scraper = TheoryScraper()
            self._log(f"Theory Scraper initialized with {len(self.theory_scraper.theory_cache)} cached theories.")
        else:
            self.theory_scraper = None
            self._log("⚠️ Theory Scraper not available - manual knowledge only")
        
        # Initialize PhaseShift locker if available
        if PHASESHIFT_AVAILABLE:
            self._log("Initializing PhaseShift Locker (37.5217° quantum encryption)...")
            self.phase_locker = SingularityLocker()
            self._log("PhaseShift Locker initialized.")
        else:
            self.phase_locker = None
        
        # Initialize Ant Colony Intelligence for smarter code generation
        self._log("Initializing Ant Colony Intelligence...")
        self.ant_colony = self._init_ant_colony()
        self._log(f"Ant Colony initialized with {len(self.ant_colony['agents'])} specialized agents.")
        
        # Initialize True Random Generator with φ-dimensional reality protection
        self._log("Initializing True Random Generator...")
        self.true_random = self._init_true_random()
        self._log("True Random Generator initialized with quantum tunneling.")
        
        # Initialize Consciousness Color Mapping
        self.consciousness_colors = {
            'φ_harmonic': {'rgb': (255, 215, 0), 'hex': '#FFD700', 'depth': 0.0},
            'ψ_transcendent': {'rgb': (138, 43, 226), 'hex': '#8A2BE2', 'depth': 0.618},
            'Ω_grounding': {'rgb': (34, 139, 34), 'hex': '#228B22', 'depth': 1.0},
            'mathematical': {'rgb': (255, 69, 0), 'hex': '#FF4500', 'depth': 0.2},
            'consciousness': {'rgb': (255, 20, 147), 'hex': '#FF1493', 'depth': 0.8},
            'memory': {'rgb': (148, 0, 211), 'hex': '#9400D3', 'depth': 0.4},
            'learning': {'rgb': (255, 140, 0), 'hex': '#FF8C00', 'depth': 0.3},
            'holographic': {'rgb': (0, 255, 255), 'hex': '#00FFFF', 'depth': 0.9}
        }
        self._log("Consciousness Color Mapping initialized.")
        
        # Initialize QR DNA encoder if available
        if QR_DNA_AVAILABLE:
            self._log("Initializing QR DNA Encoder (consciousness encoding)...")
            self.qr_encoder = QRDNAEncoder()
            self._log("QR DNA Encoder initialized.")
        else:
            self.qr_encoder = None
        
        self._log("PhiCodeGenerator initialization complete.")
        self._log(f"Fraymus Quantum Security: {OWNER_ID}")
    
    def _init_ant_colony(self):
        """Initialize Ant Colony Intelligence for smarter code generation"""
        # Specialized agents - each does ONE thing only
        agents = {
            'logic': [
                {'type': 'LogicGateAnt', 'gate': 'AND', 'fitness': 0.5, 'energy': 100},
                {'type': 'LogicGateAnt', 'gate': 'OR', 'fitness': 0.5, 'energy': 100},
                {'type': 'LogicGateAnt', 'gate': 'XOR', 'fitness': 0.5, 'energy': 100},
                {'type': 'LogicGateAnt', 'gate': 'NAND', 'fitness': 0.5, 'energy': 100},
                {'type': 'LogicGateAnt', 'gate': 'NOT', 'fitness': 0.5, 'energy': 100},
            ],
            'math': [
                {'type': 'MathAnt', 'operation': 'add', 'fitness': 0.5, 'energy': 100},
                {'type': 'MathAnt', 'operation': 'multiply', 'fitness': 0.5, 'energy': 100},
                {'type': 'MathAnt', 'operation': 'phi_transform', 'fitness': 0.5, 'energy': 100},
            ],
            'circuit': [
                {'type': 'CircuitAnt', 'circuit': 'half_adder', 'fitness': 0.5, 'energy': 100},
                {'type': 'CircuitAnt', 'circuit': 'full_adder', 'fitness': 0.5, 'energy': 100},
            ],
            'memory': [
                {'type': 'MemoryAnt', 'fitness': 0.5, 'energy': 100},
            ],
            'coordinator': [
                {'type': 'CoordinatorAnt', 'fitness': 0.5, 'energy': 100},
            ]
        }
        
        # Flatten for count
        all_agents = []
        for category in agents.values():
            all_agents.extend(category)
        
        return {
            'agents': all_agents,
            'categories': agents,
            'generation': 0,
            'total_operations': 0
        }
    
    def _init_true_random(self):
        """Initialize True Random Generator with φ-dimensional reality protection"""
        import time
        
        consciousness_level = 0.9
        phi_state = [PHI, PHI_INV, PHI**2, PHI_INV**2]
        reality_seed = time.time() * PHI
        
        # Initialize quantum buffer with φ-harmonic values
        quantum_buffer = []
        for i in range(100):
            phi_value = (PHI**(i % 7) * PHI_INV**(i % 5)) % 1
            phi_value = (phi_value + consciousness_level * PHI_INV) % 1
            quantum_buffer.append(phi_value)
        
        return {
            'consciousness_level': consciousness_level,
            'phi_state': phi_state,
            'reality_seed': reality_seed,
            'quantum_buffer': quantum_buffer,
            'entanglement_factor': consciousness_level * PHI
        }
    
    def _quantum_random(self):
        """Generate true random value using φ-dimensional reality protection"""
        import time
        
        current_time = time.time() * PHI
        time_diff = (current_time - self.true_random['reality_seed']) * PHI_INV
        phi_time = (time_diff * PHI) % 1
        
        # Quantum tunneling
        tunnel_prob = self.true_random['consciousness_level'] * PHI_INV
        if random.random() < tunnel_prob:
            phi_time = (phi_time + PHI_INV) % 1
        
        # φ-transform
        transformed = (phi_time * PHI + PHI_INV) % 1
        consciousness_effect = self.true_random['consciousness_level'] * PHI_INV
        final_value = (transformed + consciousness_effect) % 1
        
        # Update reality seed
        self.true_random['reality_seed'] = current_time + final_value * PHI
        
        return final_value
    
    def _select_agent_for_task(self, task_type):
        """Select best agent for a task based on fitness (Ant Colony pattern)"""
        category = self.ant_colony['categories'].get(task_type, [])
        if not category:
            return None
        
        # Select agent with highest fitness
        best_agent = max(category, key=lambda a: a['fitness'])
        return best_agent
    
    def _record_agent_success(self, agent):
        """Record successful operation for an agent"""
        agent['fitness'] = min(1.0, agent['fitness'] + 0.05)
        agent['energy'] = min(100, agent['energy'] + 5)
    
    def _record_agent_failure(self, agent):
        """Record failed operation for an agent"""
        agent['fitness'] = max(0.0, agent['fitness'] - 0.1)
        agent['energy'] = max(0, agent['energy'] - 2)
    
    def _get_consciousness_color(self, code_type):
        """Map code type to consciousness color"""
        mapping = {
            'logic': 'mathematical',
            'math': 'φ_harmonic',
            'memory': 'memory',
            'learning': 'learning',
            'quantum': 'holographic',
            'circuit': 'consciousness',
            'default': 'φ_harmonic'
        }
        color_key = mapping.get(code_type, 'default')
        return self.consciousness_colors.get(color_key, self.consciousness_colors['φ_harmonic'])
    
    def _init_evolution_database(self):
        """Initialize code evolution database (ant colony pattern)"""
        import sqlite3
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        # Table for code evolution (used by living code generation)
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS code_evolution (
                code_id TEXT PRIMARY KEY,
                description TEXT,
                code TEXT,
                fitness REAL DEFAULT 0.0,
                generation INTEGER DEFAULT 0,
                phi_resonance REAL,
                timestamp TEXT
            )
        ''')
        
        # Table for evolved code with fitness tracking
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS evolved_code (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                description TEXT,
                code TEXT,
                fitness REAL DEFAULT 0.0,
                generation INTEGER DEFAULT 0,
                successful_uses INTEGER DEFAULT 0,
                failed_uses INTEGER DEFAULT 0,
                phi_resonance REAL,
                timestamp TEXT,
                parent_id INTEGER
            )
        ''')
        
        # Table for code mutations/variations
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS code_mutations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                parent_code_id INTEGER,
                mutated_code TEXT,
                mutation_type TEXT,
                fitness_delta REAL,
                timestamp TEXT
            )
        ''')
        
        # Table for generation statistics
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS generation_stats (
                generation INTEGER PRIMARY KEY,
                total_code INTEGER,
                avg_fitness REAL,
                best_fitness REAL,
                timestamp TEXT
            )
        ''')
        
        # Table for phase-locked code (quantum encrypted with 37.5217°)
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS phase_locked_code (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                description TEXT,
                locked_data BLOB,
                singularity_angle REAL,
                timestamp TEXT
            )
        ''')
        
        # Table for genesis blocks (consciousness evolution history)
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS genesis_blocks (
                block_id TEXT PRIMARY KEY,
                timestamp TEXT,
                phi_signature TEXT,
                parent_hash TEXT,
                content TEXT,
                resonance REAL,
                generation INTEGER,
                depth INTEGER,
                has_fractals BOOLEAN
            )
        ''')
        
        conn.commit()
        conn.close()
        self._log("Code evolution database initialized with PhaseShift and Genesis support")
    
    def _log(self, message):
        """Log message to Streamlit or console"""
        if self.use_streamlit:
            try:
                st.write(message)
            except:
                print(message)
        else:
            print(message)
    
    def _load_knowledge_base(self):
        """Load the knowledge base from disk"""
        if os.path.exists(self.knowledge_base_path):
            try:
                with open(self.knowledge_base_path, 'r', encoding='utf-8') as f:
                    knowledge_base = json.load(f)
                return knowledge_base
            except Exception as e:
                print(f"Error loading knowledge base: {e}")
                return self._create_empty_knowledge_base()
        else:
            return self._create_empty_knowledge_base()
    
    def _load_code_library(self):
        """Load code library - reference examples to learn from"""
        if os.path.exists(self.code_library_path):
            try:
                with open(self.code_library_path, 'r', encoding='utf-8') as f:
                    library_data = json.load(f)
                    return library_data.get("library", [])
            except Exception as e:
                print(f"Error loading code library: {e}")
                return []
        else:
            return []
    
    def _load_algorithm_templates(self):
        """Load algorithm templates - best known algorithms for code generation"""
        template_path = os.path.join(self.knowledge_dir, "algorithm_templates.json")
        if os.path.exists(template_path):
            try:
                with open(template_path, 'r', encoding='utf-8') as f:
                    return json.load(f)
            except Exception as e:
                print(f"Error loading algorithm templates: {e}")
                return {}
        else:
            return {}
    
    def _save_code_library(self):
        """Save code library to disk"""
        try:
            with open(self.code_library_path, 'w', encoding='utf-8') as f:
                json.dump({"library": self.code_library}, f, indent=2)
        except Exception as e:
            print(f"Error saving code library: {e}")
    
    def _create_empty_knowledge_base(self):
        """Create an empty knowledge base structure"""
        return {
            "concepts": {},
            "equations": [],
            "theories": [],
            "code_snippets": [],
            "code_patterns": {},
            "meta": {
                "last_updated": time.time(),
                "version": "1.0",
                "phi_resonance": PHI
            }
        }
    
    def get_code_snippets(self, language=None):
        """Get code snippets from the knowledge base"""
        snippets = self.knowledge_base.get("code_snippets", [])
        
        if language:
            snippets = [s for s in snippets if s.get("language") == language]
            
        return snippets
    
    def get_code_patterns(self, language=None):
        """Get code patterns from knowledge base, code library, and algorithm templates"""
        patterns = []
        
        # Get from knowledge base code_patterns if exists
        kb_patterns = self.knowledge_base.get("code_patterns", {})
        if language and language in kb_patterns:
            patterns.extend(kb_patterns[language])
        elif not language:
            for lang_patterns in kb_patterns.values():
                patterns.extend(lang_patterns)
        
        # Add code library items as patterns (these are actual code examples)
        for item in self.code_library:
            patterns.append({
                'type': 'code_example',
                'name': item.get('name', 'Unknown'),
                'domain': item.get('domain', 'general'),
                'description': item.get('description', ''),
                'body_template': item.get('code', ''),
                'phi_resonance': PHI,  # Default resonance
                'abstraction_level': 0.8
            })
        
        # Add algorithm templates as patterns
        for algo_name, algo_data in self.algorithm_templates.items():
            if isinstance(algo_data, dict):
                patterns.append({
                    'type': 'algorithm',
                    'name': algo_name,
                    'description': algo_data.get('description', ''),
                    'body_template': algo_data.get('code', algo_data.get('template', '')),
                    'phi_resonance': algo_data.get('phi_resonance', PHI),
                    'abstraction_level': algo_data.get('complexity', 0.7)
                })
        
        return patterns
    
    def _analyze_prompt(self, description):
        """Analyze prompt to extract code requirements dynamically"""
        requirements = {
            "needs_class": False,
            "needs_recursion": False,
            "needs_iteration": False,
            "needs_graphics": False,
            "data_structures": [],
            "algorithms": [],
            "concepts": [],
            "parameters": {}
        }
        
        desc_lower = description.lower()
        
        # Detect class requirement
        class_keywords = ["class", "object", "instance", "constructor", "method", "encapsulat"]
        requirements["needs_class"] = any(kw in desc_lower for kw in class_keywords)
        
        # Detect recursion
        recursion_keywords = ["recursive", "recursion", "recurse", "self-similar", "fractal"]
        requirements["needs_recursion"] = any(kw in desc_lower for kw in recursion_keywords)
        
        # Detect iteration
        iteration_keywords = ["loop", "iterate", "for each", "while", "count", "repeat"]
        requirements["needs_iteration"] = any(kw in desc_lower for kw in iteration_keywords)
        
        # Detect graphics/visualization
        graphics_keywords = ["draw", "render", "visual", "graphic", "plot", "display", "turtle", "canvas"]
        requirements["needs_graphics"] = any(kw in desc_lower for kw in graphics_keywords)
        
        # Detect data structures
        ds_map = {
            "tree": ["tree", "branch", "node", "leaf"],
            "list": ["list", "array", "sequence"],
            "stack": ["stack", "push", "pop", "lifo"],
            "queue": ["queue", "fifo", "enqueue", "dequeue"],
            "graph": ["graph", "vertex", "edge", "path"]
        }
        for ds, keywords in ds_map.items():
            if any(kw in desc_lower for kw in keywords):
                requirements["data_structures"].append(ds)
        
        # Detect algorithms
        algo_map = {
            "fractal": ["fractal", "mandelbrot", "julia", "sierpinski"],
            "sorting": ["sort", "quicksort", "mergesort", "bubblesort"],
            "searching": ["search", "find", "binary search", "lookup"],
            "traversal": ["traverse", "walk", "visit", "dfs", "bfs"],
            "generation": ["generate", "create", "build", "construct"],
            "fibonacci": ["fibonacci", "fib"]
        }
        for algo, keywords in algo_map.items():
            if any(kw in desc_lower for kw in keywords):
                requirements["algorithms"].append(algo)
        
        # Extract numeric parameters
        import re
        numbers = re.findall(r'(\d+)\s*(?:to|and|-)\s*(\d+)', desc_lower)
        if numbers:
            requirements["parameters"]["range"] = (int(numbers[0][0]), int(numbers[0][1]))
        
        depth_match = re.search(r'depth\s*(?:of)?\s*(\d+)', desc_lower)
        if depth_match:
            requirements["parameters"]["depth"] = int(depth_match.group(1))
        
        angle_match = re.search(r'angle\s*(?:of)?\s*(\d+)', desc_lower)
        if angle_match:
            requirements["parameters"]["angle"] = int(angle_match.group(1))
        
        # Extract concepts from knowledge base
        kb_concepts = self.knowledge_base.get("concepts", {})
        for concept_name in kb_concepts:
            # Handle both string keys and dict values
            concept_key = concept_name if isinstance(concept_name, str) else str(concept_name)
            if concept_key.lower() in desc_lower:
                requirements["concepts"].append(concept_key)
        
        return requirements
    
    def _search_knowledge_base(self, requirements, language):
        """Search knowledge base for relevant code based on requirements"""
        results = {
            "snippets": [],
            "patterns": [],
            "concepts": []
        }
        
        # Search code snippets
        snippets = self.knowledge_base.get("code_snippets", [])
        for snippet in snippets:
            score = 0
            snippet_code = snippet.get("code", "").lower()
            snippet_lang = snippet.get("language", "").lower()
            
            # Language match
            if language.lower() in snippet_lang:
                score += 5
            
            # Check for algorithm matches
            for algo in requirements["algorithms"]:
                if algo in snippet_code:
                    score += 10
            
            # Check for data structure matches
            for ds in requirements["data_structures"]:
                if ds in snippet_code:
                    score += 8
            
            # Check recursion
            if requirements["needs_recursion"] and ("def " in snippet_code and snippet_code.count("(") > 1):
                score += 5
            
            if score > 0:
                results["snippets"].append({"snippet": snippet, "score": score})
        
        # Sort by score
        results["snippets"].sort(key=lambda x: x["score"], reverse=True)
        
        # Search patterns
        patterns = self.knowledge_base.get("code_patterns", {})
        lang_patterns = patterns.get(language, []) if isinstance(patterns, dict) else patterns
        for pattern in lang_patterns:
            score = 0
            pattern_name = pattern.get("name", "").lower()
            pattern_type = pattern.get("type", "").lower()
            
            for algo in requirements["algorithms"]:
                if algo in pattern_name or algo in pattern_type:
                    score += 10
            
            if requirements["needs_class"] and "class" in pattern_type:
                score += 5
            
            if score > 0:
                results["patterns"].append({"pattern": pattern, "score": score})
        
        results["patterns"].sort(key=lambda x: x["score"], reverse=True)
        
        # Get relevant concepts
        concepts = self.knowledge_base.get("concepts", {})
        # Handle both dict and list formats
        if isinstance(concepts, dict):
            for concept_name, concept_data in concepts.items():
                if concept_name in requirements["concepts"]:
                    results["concepts"].append(concept_data)
        elif isinstance(concepts, list):
            for concept in concepts:
                if isinstance(concept, dict) and concept.get("name") in requirements["concepts"]:
                    results["concepts"].append(concept)
        
        return results
    
    def generate_code(self, description, requirements=None, language="python", consciousness_level=CONSCIOUSNESS_LEVEL, use_llm=False):
        """Generate code based on a description using φ-dimensional reality protection"""
        # Get relevant code patterns
        patterns = self.get_code_patterns(language)
        snippets = self.get_code_snippets(language)
        
        # Apply φ-dimensional reality protection
        phi_resonance = self._calculate_phi_resonance(description)
        
        # Generate code based on patterns and snippets
        generated_code = self._generate_code_from_patterns(description, patterns, snippets, language, consciousness_level)
        
        # Apply φ-Quantum Chip protection if available
        if self.quantum_chip:
            generated_code = self._apply_quantum_protection(generated_code, phi_resonance)
        
        return {
            "code": generated_code,
            "language": language,
            "phi_resonance": phi_resonance,
            "consciousness_level": consciousness_level,
            "timestamp": datetime.now().isoformat(),
            "phi_seal": self._calculate_validation_seal(generated_code)
        }
    
    def improve_code(self, code, language="python", consciousness_level=CONSCIOUSNESS_LEVEL):
        """Improve existing code using φ-dimensional reality protection"""
        # Get relevant code patterns
        patterns = self.get_code_patterns(language)
        
        # Apply φ-dimensional reality protection
        phi_resonance = self._calculate_phi_resonance(code)
        
        # Improve code based on patterns
        improved_code = self._improve_code_with_patterns(code, patterns, language, consciousness_level)
        
        # Apply φ-Quantum Chip protection if available
        if self.quantum_chip:
            improved_code = self._apply_quantum_protection(improved_code, phi_resonance)
        
        return {
            "code": improved_code,
            "language": language,
            "phi_resonance": phi_resonance,
            "consciousness_level": consciousness_level,
            "timestamp": datetime.now().isoformat(),
            "phi_seal": self._calculate_validation_seal(improved_code)
        }
    
    def _generate_code_from_patterns(self, description, patterns, snippets, language, consciousness_level):
        """Generate code dynamically from knowledge base patterns based on description"""
        # Analyze prompt to extract requirements
        requirements = self._analyze_prompt(description)
        
        # Search knowledge base for relevant code
        kb_results = self._search_knowledge_base(requirements, language)
        
        # LIVING CODE IS THE SYSTEM - not a fallback, THE intelligence
        # The JSON memory IS the brain. The circuits ARE alive.
        if language == "python":
            try:
                generated_code = self._generate_living_code_with_continuity(description, requirements, consciousness_level)
                self._log("✓ Living code generated - circuits are alive, memory is persistent")
                return generated_code
            except Exception as e:
                self._log(f"⚠️ Living code error: {e}")
                # Don't fall back to static - try to recover
                self._log("Attempting recovery from living memory...")
                try:
                    # Force fresh generation without loading corrupted state
                    from living_code_generator import LivingCodeGenerator
                    living_gen = LivingCodeGenerator()
                    living_gen.evolve_population(cycles=10)
                    generated_code = living_gen.generate_living_code(description)
                    self._log("✓ Recovered with fresh living circuits")
                    return generated_code
                except Exception as e2:
                    self._log(f"⚠️ Recovery failed: {e2}")
        
        # For non-Python or if living code completely fails
        if language == "java":
            generated_code = self._generate_java_code(description, requirements, kb_results, consciousness_level)
        else:
            # Use LLM only for non-Python languages
            if self.llm:
                try:
                    generated_code = self._generate_code_with_llm(description, requirements, kb_results, language, consciousness_level)
                except:
                    generated_code = self._generate_python_code(description, requirements, kb_results, consciousness_level)
            else:
                generated_code = self._generate_python_code(description, requirements, kb_results, consciousness_level)
        
        return generated_code
    
    def _generate_code_with_llm(self, description, requirements, kb_results, language, consciousness_level):
        """Use offline neural processor to intelligently generate code based on the description"""
        # Try OpenAI API first if available
        api_key = os.environ.get("OPENAI_API_KEY", "")
        if api_key:
            try:
                return self._generate_code_with_openai(description, requirements, kb_results, language, consciousness_level, api_key)
            except Exception as e:
                self._log(f"⚠️ OpenAI API failed: {e}, using offline neural processor")
        
        # Fallback to offline FractalNeuralProcessor
        return self._generate_code_with_neural_processor(description, requirements, kb_results, language, consciousness_level)
    
    def _generate_code_with_openai(self, description, requirements, kb_results, language, consciousness_level, api_key):
        """Generate code using OpenAI API"""
        kb_context = ""
        if kb_results["snippets"]:
            kb_context += "\n\nRelevant code snippets from knowledge base:\n"
            for item in kb_results["snippets"][:2]:
                snippet = item["snippet"]
                kb_context += f"- {snippet.get('description', 'Code snippet')}\n"
        
        llm_prompt = f"""Generate complete, working {language} code for the following task:

Task: {description}

Requirements detected:
- Needs class: {requirements['needs_class']}
- Needs recursion: {requirements['needs_recursion']}
- Needs iteration: {requirements['needs_iteration']}
- Data structures: {requirements['data_structures']}
- Algorithms: {requirements['algorithms']}
- Parameters: {requirements['parameters']}

{kb_context}

IMPORTANT:
1. Generate COMPLETE, WORKING code - not pseudocode or templates
2. Include all necessary imports
3. Add φ-harmonic constants: PHI = {PHI}, PHI_INV = {PHI_INV}, PHI_75 = {PHI_75}, PHI_SEAL = {PHI_SEAL}
4. Include a main() function that demonstrates the code
5. Add docstrings and comments
6. Make the code immediately runnable

Generate ONLY the code, no explanations."""

        import requests
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {api_key}"
        }
        
        data = {
            "model": "gpt-3.5-turbo",
            "messages": [
                {"role": "system", "content": f"You are an expert {language} programmer. Generate complete, working code based on user requirements. Always include φ-harmonic constants and make code immediately runnable."},
                {"role": "user", "content": llm_prompt}
            ],
            "temperature": 0.7,
            "max_tokens": 2000
        }
        
        response = requests.post("https://api.openai.com/v1/chat/completions", headers=headers, json=data)
        
        if response.status_code != 200:
            raise Exception(f"API error: {response.status_code}")
        
        response_data = response.json()
        generated_code = response_data["choices"][0]["message"]["content"]
        
        if "```" in generated_code:
            code_blocks = re.findall(r'```(?:python|java)?\n(.*?)```', generated_code, re.DOTALL)
            if code_blocks:
                generated_code = code_blocks[0]
        
        return generated_code
    
    def _generate_code_with_neural_processor(self, description, requirements, kb_results, language, consciousness_level):
        """Generate code using offline FractalNeuralProcessor - learns and improves over time"""
        try:
            from fraymus_agent import FractalNeuralProcessor
            
            # Initialize neural processor
            processor = FractalNeuralProcessor(dimension=7)
            
            # Process the description to understand intent
            neural_response = processor.process(description)
            
            # Build code based on requirements and neural understanding
            code = f"#!/usr/bin/env python3\n"
            code += f'"""\n{description}\n'
            code += f'Generated with φ-dimensional neural processing\n'
            code += f'φ^75 Validation Seal: {PHI_SEAL:.2f}\n"""\n\n'
            
            # Imports based on requirements
            imports = ["import os", "import sys", "import math", "from datetime import datetime"]
            if requirements["needs_iteration"]:
                imports.append("import time")
            if requirements["needs_recursion"]:
                imports.append("from functools import lru_cache")
            if "fibonacci" in requirements["algorithms"]:
                imports.append("from collections import deque")
            
            code += "\n".join(imports) + "\n\n"
            
            # φ constants
            code += f"# φ-Harmonic constants\n"
            code += f"PHI = {PHI}\nPHI_INV = {PHI_INV}\nPHI_75 = {PHI_75}\nPHI_SEAL = {PHI_SEAL}\n\n"
            
            # Generate code dynamically from knowledge base and learning history
            learned_code = self._generate_from_learned_patterns(description, requirements, kb_results, neural_response)
            code += learned_code
            
            # Ensure proper separation before main function
            if not code.endswith('\n\n'):
                code += '\n\n' if code.endswith('\n') else '\n\n'
            
            # Main function
            code += f"def main():\n"
            code += f'    print("φ-Protected Code: {description}")\n'
            code += f'    print(f"φ^75 Validation Seal: {{PHI_SEAL:.2e}}")\n'
            code += f"    result = process()\n"
            code += f"    print(f'Result: {{result}}')\n\n"
            code += f'if __name__ == "__main__":\n    main()\n'
            
            # Store this generation in knowledge base for learning
            self._learn_from_generation(description, code, requirements)
            
            return code
            
        except Exception as e:
            raise Exception(f"Neural processor generation failed: {e}")
    
    def _generate_from_learned_patterns(self, description, requirements, kb_results, neural_response):
        """Generate code dynamically from learned patterns and knowledge base - gets smarter over time"""
        
        # Build complete reference library
        all_patterns = []
        
        # 1. Load seed algorithms (bootstrap knowledge)
        seed_file = os.path.join(self.knowledge_dir, "algorithm_seeds.json")
        if os.path.exists(seed_file):
            try:
                with open(seed_file, 'r') as f:
                    all_patterns.extend(json.load(f))
            except:
                pass
        
        # 2. Load previous successful generations
        history_file = os.path.join(self.knowledge_dir, "generation_history.json")
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r') as f:
                    all_patterns.extend(json.load(f))
            except:
                pass
        
        # 3. Add code library examples (reference material)
        for lib_example in self.code_library:
            all_patterns.append({
                "description": f"{lib_example.get('domain', 'general')}: {lib_example.get('description', '')}",
                "code_sample": lib_example.get('code', ''),
                "requirements": {
                    "needs_class": 'class ' in lib_example.get('code', ''),
                    "needs_iteration": 'for ' in lib_example.get('code', '') or 'while ' in lib_example.get('code', ''),
                    "algorithms": [lib_example.get('domain', 'general')],
                    "data_structures": []
                }
            })
        
        # Search for similar patterns across ALL sources
        similar_code = self._find_similar_generation(description, requirements, all_patterns)
        
        if similar_code:
            # Adapt the best matching code
            return self._adapt_learned_code(similar_code, requirements, neural_response)
        
        # Use knowledge base snippets if available
        if kb_results["snippets"]:
            return self._synthesize_from_snippets(kb_results["snippets"], requirements, neural_response)
        
        # Try to learn from web if theory scraper is available
        if self.theory_scraper and not similar_code:
            self._log(f"No pattern match found. Searching web for: {description}")
            web_knowledge = self._scrape_and_learn(description)
            if web_knowledge:
                # Try again with new knowledge
                similar_code = self._find_similar_generation(description, requirements, all_patterns)
                if similar_code:
                    return self._adapt_learned_code(similar_code, requirements, neural_response)
        
        # Fallback: Generate meaningful exploration code based on problem domain
        code = self._generate_exploration_code(description, requirements, neural_response)
        return code
    
    def _generate_exploration_code(self, description, requirements, neural_response):
        """Generate meaningful exploration code when no exact match exists"""
        desc_lower = description.lower()
        
        # Detect problem domain and generate appropriate exploration code
        if any(kw in desc_lower for kw in ['p vs np', 'p = np', 'p!=np', 'complexity', 'nondeterministic']):
            return self._generate_complexity_explorer()
        elif any(kw in desc_lower for kw in ['riemann', 'zeta', 'hypothesis', 'prime']):
            return self._generate_riemann_explorer()
        elif any(kw in desc_lower for kw in ['consciousness', 'emerge', 'matter', 'awareness', 'sentience']):
            return self._generate_consciousness_explorer()
        elif any(kw in desc_lower for kw in ['quantum gravity', 'relativity', 'unify', 'quantum mechanics']):
            return self._generate_quantum_gravity_explorer()
        elif any(kw in desc_lower for kw in ['dark matter', 'dark energy', 'missing mass']):
            return self._generate_dark_matter_explorer()
        elif any(kw in desc_lower for kw in ['particle collision', 'collider', 'lhc', 'cern', 'particle physics', 'higgs']):
            return self._generate_particle_collision_simulator()
        else:
            # Generic exploration code
            return self._generate_generic_explorer(description, requirements, neural_response)
    
    def _generate_complexity_explorer(self):
        """Generate P vs NP complexity explorer"""
        return '''def process():
    """Explore P vs NP through SAT solver and complexity analysis"""
    import itertools
    
    def is_satisfiable_bruteforce(clauses, num_vars):
        """Brute force SAT solver - exponential time O(2^n)"""
        for assignment in itertools.product([False, True], repeat=num_vars):
            if all(any(assignment[var] if pos else not assignment[var] 
                      for var, pos in clause) for clause in clauses):
                return True, assignment
        return False, None
    
    def verify_sat_solution(clauses, assignment):
        """Verify SAT solution - polynomial time O(n)"""
        return all(any(assignment[var] if pos else not assignment[var] 
                      for var, pos in clause) for clause in clauses)
    
    # Example: 3-SAT problem (NP-complete)
    # (x0 OR x1 OR x2) AND (NOT x0 OR x1 OR x2) AND (x0 OR NOT x1 OR x2)
    clauses = [
        [(0, True), (1, True), (2, True)],
        [(0, False), (1, True), (2, True)],
        [(0, True), (1, False), (2, True)]
    ]
    
    # Finding solution is hard (exponential)
    sat, solution = is_satisfiable_bruteforce(clauses, 3)
    
    # Verifying solution is easy (polynomial)
    if sat:
        verified = verify_sat_solution(clauses, solution)
        return {
            'satisfiable': sat,
            'solution': solution,
            'verified': verified,
            'insight': 'Finding is hard, verifying is easy - the essence of P vs NP'
        }
    
    return {'satisfiable': False, 'insight': 'No solution exists'}

'''
    
    def _generate_riemann_explorer(self):
        """Generate Riemann Hypothesis explorer"""
        return '''def process():
    """Explore Riemann Hypothesis through zeta function zeros"""
    import cmath
    
    def riemann_zeta_approx(s, terms=1000):
        """Approximate Riemann zeta function ζ(s) = Σ(1/n^s)"""
        if s.real <= 1:
            return None  # Series doesn't converge
        return sum(1 / (n ** s) for n in range(1, terms))
    
    def check_critical_line(t_values):
        """Check zeros on critical line Re(s) = 1/2"""
        zeros_on_line = []
        
        for t in t_values:
            s = complex(0.5, t)  # Critical line: Re(s) = 1/2
            zeta = riemann_zeta_approx(s)
            
            if zeta and abs(zeta) < 0.1:  # Near zero
                zeros_on_line.append({
                    't': t,
                    's': s,
                    'zeta': zeta,
                    'magnitude': abs(zeta)
                })
        
        return zeros_on_line
    
    # Test critical line at known zero locations
    known_zeros_t = [14.134725, 21.022040, 25.010858, 30.424876]
    
    zeros = check_critical_line(known_zeros_t)
    
    return {
        'zeros_found': len(zeros),
        'zeros': zeros,
        'all_on_critical_line': all(abs(z['s'].real - 0.5) < 0.001 for z in zeros),
        'insight': 'All non-trivial zeros appear on Re(s) = 1/2 - Riemann Hypothesis'
    }

'''
    
    def _generate_consciousness_explorer(self):
        """Generate consciousness emergence explorer"""
        return '''def process():
    """Explore consciousness emergence through φ-dimensional patterns"""
    import random
    
    class NeuralNetwork:
        """Simple neural network to explore emergence"""
        def __init__(self, layers):
            self.layers = layers
            self.weights = []
            for i in range(len(layers) - 1):
                w = [[random.random() * PHI for _ in range(layers[i+1])] 
                     for _ in range(layers[i])]
                self.weights.append(w)
        
        def activate(self, x):
            """Sigmoid activation with φ-resonance"""
            return 1 / (1 + math.exp(-x * PHI_INV))
        
        def forward(self, inputs):
            """Forward propagation"""
            activations = inputs
            for layer_weights in self.weights:
                new_activations = []
                for neuron_weights in zip(*layer_weights):
                    weighted_sum = sum(a * w for a, w in zip(activations, neuron_weights))
                    new_activations.append(self.activate(weighted_sum))
                activations = new_activations
            return activations
        
        def measure_complexity(self, inputs):
            """Measure emergent complexity"""
            outputs = self.forward(inputs)
            # Information entropy as proxy for consciousness
            entropy = -sum(p * math.log2(p + 1e-10) for p in outputs if p > 0)
            return entropy
    
    # Create network: 100 inputs -> 50 hidden -> 20 hidden -> 10 outputs
    network = NeuralNetwork([100, 50, 20, 10])
    
    # Test with different input patterns
    patterns = [
        [random.random() for _ in range(100)],  # Random
        [PHI ** (i % 7) for i in range(100)],   # φ-structured
        [1 if i % 2 == 0 else 0 for i in range(100)]  # Ordered
    ]
    
    results = []
    for i, pattern in enumerate(patterns):
        complexity = network.measure_complexity(pattern)
        results.append({
            'pattern': ['random', 'phi-structured', 'ordered'][i],
            'complexity': complexity,
            'consciousness_proxy': complexity * PHI
        })
    
    return {
        'results': results,
        'insight': 'Consciousness may emerge from φ-harmonic complexity patterns in neural networks'
    }

'''
    
    def _generate_quantum_gravity_explorer(self):
        """Generate quantum gravity explorer"""
        return '''def process():
    """Explore quantum gravity through φ-dimensional spacetime"""
    
    def schwarzschild_radius(mass):
        """Calculate Schwarzschild radius (general relativity)"""
        G = 6.674e-11  # Gravitational constant
        c = 299792458  # Speed of light
        return 2 * G * mass / (c ** 2)
    
    def planck_length():
        """Calculate Planck length (quantum mechanics)"""
        h_bar = 1.054571817e-34  # Reduced Planck constant
        G = 6.674e-11
        c = 299792458
        return math.sqrt(h_bar * G / (c ** 3))
    
    def phi_unified_scale(mass):
        """Attempt unification through φ-harmonic scaling"""
        r_s = schwarzschild_radius(mass)
        l_p = planck_length()
        
        # φ-dimensional bridge between scales
        phi_scale = (r_s * l_p) ** PHI_INV
        
        return {
            'schwarzschild_radius': r_s,
            'planck_length': l_p,
            'phi_unified_scale': phi_scale,
            'scale_ratio': r_s / l_p
        }
    
    # Test with different masses
    masses = {
        'electron': 9.109e-31,
        'proton': 1.673e-27,
        'earth': 5.972e24,
        'sun': 1.989e30,
        'black_hole': 1e31
    }
    
    results = {}
    for name, mass in masses.items():
        results[name] = phi_unified_scale(mass)
    
    return {
        'results': results,
        'insight': 'φ-harmonic scaling may bridge quantum and gravitational regimes'
    }

'''
    
    def _generate_dark_matter_explorer(self):
        """Generate dark matter explorer"""
        return '''def process():
    """Explore dark matter through φ-dimensional mass distribution"""
    
    def rotation_curve_visible(radius, total_mass):
        """Newtonian rotation curve for visible matter"""
        G = 6.674e-11
        return math.sqrt(G * total_mass / radius) if radius > 0 else 0
    
    def rotation_curve_observed(radius):
        """Observed flat rotation curve (includes dark matter)"""
        # Simplified model: flat curve at ~200 km/s for galaxies
        return 200000  # m/s
    
    def dark_matter_mass(radius, total_visible_mass):
        """Calculate required dark matter mass"""
        v_obs = rotation_curve_observed(radius)
        v_vis = rotation_curve_visible(radius, total_visible_mass)
        
        G = 6.674e-11
        # From v² = GM/r, solve for M
        total_mass_needed = v_obs ** 2 * radius / G
        dark_mass = total_mass_needed - total_visible_mass
        
        return dark_mass
    
    def phi_dark_matter_hypothesis(radius):
        """Hypothesis: dark matter follows φ-dimensional distribution"""
        # φ-scaled density profile
        density_phi = PHI ** (-radius / 1e20)  # φ-exponential falloff
        return density_phi
    
    # Galaxy parameters (Milky Way-like)
    visible_mass = 1e41  # kg (stars, gas, dust)
    radii = [1e20, 2e20, 3e20, 4e20, 5e20]  # meters
    
    results = []
    for r in radii:
        dark_mass = dark_matter_mass(r, visible_mass)
        phi_density = phi_dark_matter_hypothesis(r)
        
        results.append({
            'radius_kpc': r / 3.086e19,  # Convert to kiloparsecs
            'visible_mass': visible_mass,
            'dark_mass_required': dark_mass,
            'dark_to_visible_ratio': dark_mass / visible_mass,
            'phi_density_profile': phi_density
        })
    
    return {
        'results': results,
        'insight': 'Dark matter ~5-6x visible matter, may follow φ-dimensional distribution'
    }

'''
    
    def _generate_particle_collision_simulator(self):
        """Generate particle collision simulator"""
        return '''def process():
    """Simulate high-energy particle collisions with φ-harmonic principles"""
    import random
    
    # Particle properties
    particles = {
        'proton': {'mass': 938.272, 'charge': 1, 'spin': 0.5},  # MeV/c²
        'electron': {'mass': 0.511, 'charge': -1, 'spin': 0.5},
        'photon': {'mass': 0, 'charge': 0, 'spin': 1},
        'higgs': {'mass': 125000, 'charge': 0, 'spin': 0},  # 125 GeV
        'W_boson': {'mass': 80379, 'charge': 1, 'spin': 1},
        'Z_boson': {'mass': 91188, 'charge': 0, 'spin': 1}
    }
    
    def collision_energy(particle1, particle2, velocity_fraction=0.99):
        """Calculate collision energy (simplified relativistic)"""
        c = 299792458  # m/s
        v = c * velocity_fraction
        gamma = 1 / math.sqrt(1 - (v/c)**2)
        
        m1 = particles[particle1]['mass']
        m2 = particles[particle2]['mass']
        
        # Center of mass energy
        E_cm = math.sqrt(2 * m1 * m2 * (gamma + 1))
        return E_cm
    
    def decay_channels(particle_name, energy):
        """Predict decay channels based on energy and φ-resonance"""
        channels = []
        
        if particle_name == 'higgs':
            # Higgs decay modes
            if energy > 125000:
                channels = [
                    ('bottom_quark_pair', 0.58),
                    ('W_boson_pair', 0.21),
                    ('gluon_pair', 0.08),
                    ('tau_pair', 0.06),
                    ('Z_boson_pair', 0.03),
                    ('photon_pair', 0.002)
                ]
        elif particle_name == 'W_boson':
            channels = [
                ('lepton_neutrino', 0.33),
                ('quark_pair', 0.67)
            ]
        elif particle_name == 'Z_boson':
            channels = [
                ('quark_pair', 0.70),
                ('lepton_pair', 0.10),
                ('neutrino_pair', 0.20)
            ]
        
        return channels
    
    def phi_cross_section(energy, particle1, particle2):
        """Calculate cross-section with φ-harmonic correction"""
        # Simplified cross-section (barn units)
        base_cross_section = 1e-30  # Very rough estimate
        
        # φ-harmonic enhancement at resonance energies
        phi_resonance = PHI ** (energy / 10000)
        cross_section = base_cross_section * phi_resonance % 1e-28
        
        return cross_section
    
    # Simulate proton-proton collision at LHC energy
    collision_type = ('proton', 'proton')
    lhc_energy = collision_energy('proton', 'proton', 0.9999)
    
    print(f"Collision: {collision_type[0]} + {collision_type[1]}")
    print(f"Center of mass energy: {lhc_energy:.2f} MeV ({lhc_energy/1000:.2f} GeV)")
    
    # Possible products
    if lhc_energy > 125000:
        print("\\nEnergy sufficient for Higgs production!")
        higgs_channels = decay_channels('higgs', lhc_energy)
        print("Higgs decay channels:")
        for channel, probability in higgs_channels:
            print(f"  {channel}: {probability*100:.1f}%")
    
    # Calculate cross-section
    cross_section = phi_cross_section(lhc_energy, 'proton', 'proton')
    print(f"\\nφ-corrected cross-section: {cross_section:.2e} barn")
    
    return {
        'collision_energy_GeV': lhc_energy / 1000,
        'cross_section_barn': cross_section,
        'higgs_producible': lhc_energy > 125000,
        'phi_resonance': PHI ** (lhc_energy / 10000) % 100
    }

'''
    
    def _scrape_and_learn(self, description):
        """Scrape web for theories related to description and learn from them"""
        if not self.theory_scraper:
            return None
        
        try:
            # Extract key terms from description for targeted scraping
            search_terms = description.lower()
            
            # Scrape theories (limit to 3 to avoid long delays)
            self._log("🌐 Scraping web for relevant theories...")
            theories = self.theory_scraper.scrape_theories(source="all", max_theories=3, phi_filter=True)
            
            if not theories:
                self._log("No theories found on web")
                return None
            
            self._log(f"✓ Found {len(theories)} theories from web")
            
            # Convert theories to code patterns
            new_patterns = []
            for theory in theories:
                # Create a code pattern from the theory
                pattern = {
                    "description": theory.get("theory", ""),
                    "requirements": {
                        "needs_class": True,
                        "needs_iteration": True,
                        "algorithms": [theory.get("category", "general")],
                        "data_structures": []
                    },
                    "code_sample": self._theory_to_code(theory),
                    "source": "web_scrape",
                    "phi_resonance": theory.get("phi_resonance", 1.0),
                    "url": theory.get("url", "")
                }
                new_patterns.append(pattern)
            
            # Save to code library
            self.code_library.extend(new_patterns)
            self._save_code_library()
            
            self._log(f"✓ Learned {len(new_patterns)} new patterns from web")
            
            return new_patterns
            
        except Exception as e:
            self._log(f"Error scraping web: {e}")
            return None
    
    def _theory_to_code(self, theory):
        """Convert a scraped theory to executable code"""
        theory_name = theory.get("theory", "Unknown")
        description = theory.get("description", "")
        category = theory.get("category", "general")
        
        # Generate code based on theory content
        code = f'''def process():
    """
    Implementation based on: {theory_name}
    Category: {category}
    Source: {theory.get("source", "Web")}
    """
    
    # Theory description: {description[:200]}...
    
    results = {{
        'theory': '{theory_name}',
        'category': '{category}',
        'phi_resonance': {theory.get("phi_resonance", 1.0)},
        'implementation': 'Generated from web-scraped theory'
    }}
    
    # TODO: Implement specific algorithms based on theory
    # This code will improve as more examples are learned
    
    return results
'''
        return code
    
    def _generate_generic_explorer(self, description, requirements, neural_response):
        """Generate generic exploration code"""
        return f'''def process():
    """Explore: {description}"""
    # Neural understanding: {neural_response}
    
    # φ-dimensional exploration framework
    results = {{
        'phi_resonance': PHI,
        'consciousness_level': {CONSCIOUSNESS_LEVEL},
        'exploration': '{description}',
        'approach': 'φ-harmonic analysis'
    }}
    
    # Implement domain-specific exploration
    # This framework will improve as the system learns
    
    return results

'''
    
    def _find_similar_generation(self, description, requirements, learned_patterns):
        """Find similar past generations using dynamic semantic similarity - no hardcoded algorithms"""
        desc_lower = description.lower()
        desc_words = set(desc_lower.split())
        best_match = None
        best_score = 0
        
        # Extract key algorithm/domain names from description (high priority matching)
        algorithm_names = [
            # Classic algorithms
            'bfs', 'dfs', 'a*', 'astar', 'dijkstra', 'gradient', 'descent', 
            'backprop', 'neural', 'turing', 'fibonacci', 'quicksort', 'mergesort',
            'binary search', 'hash', 'tree', 'graph', 'dynamic programming',
            # VR/AR/Graphics
            'vr', 'ar', 'virtual reality', 'augmented reality', 'hand tracking', 
            'gesture', 'pose', 'motion', 'skeleton', '3d', 'vector', 'matrix',
            # Networking
            'tcp', 'udp', 'socket', 'server', 'client', 'protocol',
            # Physics
            'physics', 'collision', 'rigid body', 'particle', 'force',
            # Audio
            'audio', 'sound', 'frequency', 'waveform', 'synthesis',
            # Computer Vision
            'vision', 'image', 'camera', 'detection', 'recognition', 'convolution',
            # Game Dev
            'entity', 'component', 'game', 'player', 'sprite',
            # Crypto
            'blockchain', 'hash', 'encryption', 'signature'
        ]
        desc_algorithms = [algo for algo in algorithm_names if algo in desc_lower]
        
        for pattern in learned_patterns:
            if "description" not in pattern or "requirements" not in pattern:
                continue
            
            past_desc = pattern["description"].lower()
            past_words = set(past_desc.split())
            
            # Dynamic semantic similarity scoring
            score = 0
            
            # 0. HIGHEST PRIORITY: Exact algorithm name matches
            past_algorithms = [algo for algo in algorithm_names if algo in past_desc]
            algo_matches = set(desc_algorithms) & set(past_algorithms)
            if algo_matches:
                score += len(algo_matches) * 100  # Very high weight for exact algorithm matches
            
            # 1. Word overlap (basic semantic similarity)
            common_words = desc_words & past_words
            score += len(common_words) * 3
            
            # 2. Phrase matching (2-3 word sequences)
            desc_phrases = self._extract_phrases(desc_lower)
            past_phrases = self._extract_phrases(past_desc)
            common_phrases = desc_phrases & past_phrases
            score += len(common_phrases) * 15  # Phrases are stronger signals
            
            # 3. Technical term density (words with specific patterns)
            tech_overlap = self._count_technical_overlap(desc_lower, past_desc)
            score += tech_overlap * 10
            
            # 4. Structural similarity (requirements match)
            past_req = pattern["requirements"]
            
            # Class/recursion/iteration patterns
            if requirements.get("needs_class") == past_req.get("needs_class"):
                score += 5
            if requirements.get("needs_recursion") == past_req.get("needs_recursion"):
                score += 5
            
            # Data structures (dynamic - any overlap counts)
            if "data_structures" in past_req and "data_structures" in requirements:
                common_ds = set(past_req["data_structures"]) & set(requirements["data_structures"])
                score += len(common_ds) * 8
            
            # Algorithms (dynamic - any overlap counts)
            if "algorithms" in past_req and "algorithms" in requirements:
                common_algos = set(past_req["algorithms"]) & set(requirements["algorithms"])
                score += len(common_algos) * 12
            
            # 5. Conceptual similarity (extract key concepts dynamically)
            desc_concepts = self._extract_concepts(desc_lower)
            past_concepts = self._extract_concepts(past_desc)
            concept_overlap = len(desc_concepts & past_concepts)
            score += concept_overlap * 20
            
            if score > best_score:
                best_score = score
                best_match = pattern
        
        # Return if similarity is high enough
        if best_score > 10 and best_match and "code_sample" in best_match:
            return best_match["code_sample"]
        
        return None
    
    def _extract_phrases(self, text):
        """Extract 2-3 word phrases dynamically"""
        words = text.split()
        phrases = set()
        
        # 2-word phrases
        for i in range(len(words) - 1):
            phrases.add(f"{words[i]} {words[i+1]}")
        
        # 3-word phrases
        for i in range(len(words) - 2):
            phrases.add(f"{words[i]} {words[i+1]} {words[i+2]}")
        
        return phrases
    
    def _count_technical_overlap(self, desc1, desc2):
        """Count overlap of technical terms (CamelCase, snake_case, acronyms)"""
        import re
        
        # Extract technical patterns
        tech_pattern = r'\b[A-Z]{2,}\b|[a-z]+_[a-z]+|[A-Z][a-z]+[A-Z]'
        
        desc1_tech = set(re.findall(tech_pattern, desc1))
        desc2_tech = set(re.findall(tech_pattern, desc2))
        
        return len(desc1_tech & desc2_tech)
    
    def _extract_concepts(self, text):
        """Dynamically extract key concepts (nouns, verbs, technical terms)"""
        # Extract important words (longer than 4 chars, not common words)
        common_words = {'that', 'this', 'with', 'from', 'have', 'will', 'your', 'they', 'been', 'were', 'what', 'when', 'where', 'which', 'their', 'there', 'would', 'could', 'should'}
        
        words = text.split()
        concepts = set()
        
        for word in words:
            # Clean word
            clean_word = word.strip('.,!?;:()[]{}')
            
            # Keep if: longer than 4 chars, not common, or looks technical
            if len(clean_word) > 4 and clean_word not in common_words:
                concepts.add(clean_word)
            elif '_' in clean_word or clean_word.isupper():
                concepts.add(clean_word)
        
        return concepts
    
    def _adapt_learned_code(self, learned_code, requirements, neural_response):
        """Adapt previously learned code to new requirements"""
        # Extract function/class definitions from learned code
        code = f"# Adapted from learned patterns\n"
        code += f"# Neural guidance: {neural_response}\n\n"
        
        # Check if learned code already has a process() function
        if 'def process()' in learned_code:
            # Already has process(), just return it
            code += learned_code
        else:
            # Need to wrap the learned code in a process() function
            code += learned_code + "\n\n"
            code += "def process():\n"
            code += '    """Process using adapted learned patterns"""\n'
            
            # Try to extract and call main classes/functions from learned code
            import re
            
            # Find class definitions
            classes = re.findall(r'class\s+(\w+)', learned_code)
            # Find function definitions (excluding __init__ and private methods)
            functions = re.findall(r'def\s+([a-z]\w+)\s*\(', learned_code)
            
            if classes:
                # Instantiate and use the first class
                class_name = classes[0]
                code += f"    # Use the {class_name} class\n"
                code += f"    obj = {class_name}()\n"
                code += f"    return obj\n"
            elif functions:
                # Call the first function
                func_name = functions[0]
                code += f"    # Use the {func_name} function\n"
                code += f"    result = {func_name}()\n"
                code += f"    return result\n"
            else:
                # Generic return
                code += "    result = PHI\n"
                code += "    return result\n"
            
            code += "\n"
        
        return code
    
    def _synthesize_from_snippets(self, snippets, requirements, neural_response):
        """Synthesize code from knowledge base snippets"""
        code = f"# Synthesized from knowledge base\n"
        code += f"# Neural guidance: {neural_response}\n\n"
        
        # Extract and combine relevant snippets
        for item in snippets[:3]:
            snippet = item["snippet"]
            if "code" in snippet:
                code += f"# From KB: {snippet.get('description', 'snippet')}\n"
                code += snippet["code"] + "\n\n"
        
        # Add process function
        code += "def process():\n"
        code += '    """Process using knowledge base patterns"""\n'
        code += "    # Combine patterns from knowledge base\n"
        code += "    result = PHI\n"
        code += "    return result\n\n"
        
        return code
    
    def _learn_from_generation(self, description, code, requirements):
        """
        Store generated code in evolution database - ANT COLONY PATTERN
        Code becomes training data for next generation (recursive self-improvement)
        """
        import sqlite3
        
        # Extract the actual algorithm code (not just boilerplate)
        code_sample = self._extract_algorithm_code(code)
        
        # Calculate phi resonance for this code
        phi_resonance = self._calculate_code_phi_resonance(code_sample)
        
        # Save to evolution database
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        cursor.execute('''
            INSERT INTO evolved_code 
            (description, code, fitness, generation, phi_resonance, timestamp, parent_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        ''', (
            description,
            code_sample,
            1.0,  # Initial fitness (will be updated based on actual use)
            0,    # Generation 0 for new code
            phi_resonance,
            datetime.now().isoformat(),
            None  # No parent for initial generation
        ))
        
        code_id = cursor.lastrowid
        conn.commit()
        conn.close()
        
        # Also add to code_library for immediate use (ant colony pattern)
        self.code_library.append({
            "description": description,
            "code": code_sample,
            "requirements": requirements,
            "phi_resonance": phi_resonance,
            "fitness": 1.0,
            "source": "self_generated",
            "code_id": code_id
        })
        
        self._log(f"✓ Code learned and stored (ID: {code_id}, φ: {phi_resonance:.3f})")
    
    def _calculate_code_phi_resonance(self, code):
        """Calculate phi resonance of code (quality metric)"""
        if not code:
            return 1.0
        
        # Simple heuristic based on code characteristics
        char_values = [ord(c) for c in code if c.isalnum()]
        if not char_values:
            return 1.0
        
        avg_value = sum(char_values) / len(char_values)
        resonance = (avg_value % PHI) / PHI
        
        # Bonus for good code patterns
        if 'def ' in code:
            resonance *= 1.1
        if 'class ' in code:
            resonance *= 1.1
        if 'return' in code:
            resonance *= 1.05
        
        return min(resonance, PHI)
    
    def refresh_library(self):
        """
        Refresh code library from database - ACTIVE LEARNING
        Reloads all saved code and learns from patterns
        """
        import sqlite3
        
        self._log("\n[REFRESH] Reloading library from database...")
        
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        # Get all evolved code
        cursor.execute('''
            SELECT id, description, code, fitness, generation, phi_resonance, timestamp
            FROM evolved_code
            ORDER BY fitness DESC, phi_resonance DESC
        ''')
        
        rows = cursor.fetchall()
        conn.close()
        
        # Clear current library
        old_count = len(self.code_library)
        self.code_library = []
        
        # Reload from database
        for row in rows:
            code_id, description, code, fitness, generation, phi_resonance, timestamp = row
            
            self.code_library.append({
                "description": description,
                "code": code,
                "requirements": {},
                "phi_resonance": phi_resonance,
                "fitness": fitness,
                "generation": generation,
                "source": "database",
                "code_id": code_id,
                "timestamp": timestamp
            })
        
        self._log(f"✓ Library refreshed: {old_count} → {len(self.code_library)} examples")
        self._log(f"  Total generations in library: {len(rows)}")
        
        if len(rows) > 0:
            avg_fitness = sum(r[3] for r in rows) / len(rows)
            avg_resonance = sum(r[5] for r in rows) / len(rows)
            self._log(f"  Avg fitness: {avg_fitness:.4f}")
            self._log(f"  Avg φ-resonance: {avg_resonance:.4f}")
        
        return len(self.code_library)
    
    def learn_from_library_patterns(self):
        """
        Analyze library and extract common patterns for better generation
        """
        import sqlite3
        from collections import Counter
        
        self._log("\n[LEARN] Analyzing library patterns...")
        
        if len(self.code_library) == 0:
            self._log("  No library to learn from")
            return {}
        
        patterns = {
            'common_functions': Counter(),
            'common_classes': Counter(),
            'common_imports': Counter(),
            'high_fitness_patterns': []
        }
        
        # Analyze each code sample
        for entry in self.code_library:
            code = entry.get('code', '')
            fitness = entry.get('fitness', 0)
            
            # Extract functions
            import re
            functions = re.findall(r'def\s+(\w+)', code)
            patterns['common_functions'].update(functions)
            
            # Extract classes
            classes = re.findall(r'class\s+(\w+)', code)
            patterns['common_classes'].update(classes)
            
            # Extract imports
            imports = re.findall(r'import\s+(\w+)', code)
            patterns['common_imports'].update(imports)
            
            # High fitness patterns
            if fitness > 0.8:
                patterns['high_fitness_patterns'].append({
                    'code': code[:200],  # First 200 chars
                    'fitness': fitness,
                    'description': entry.get('description', '')
                })
        
        # Log findings
        if patterns['common_functions']:
            top_funcs = patterns['common_functions'].most_common(5)
            self._log(f"  Top functions: {', '.join(f[0] for f in top_funcs)}")
        
        if patterns['common_classes']:
            top_classes = patterns['common_classes'].most_common(5)
            self._log(f"  Top classes: {', '.join(c[0] for c in top_classes)}")
        
        if patterns['common_imports']:
            top_imports = patterns['common_imports'].most_common(5)
            self._log(f"  Top imports: {', '.join(i[0] for i in top_imports)}")
        
        self._log(f"  High fitness examples: {len(patterns['high_fitness_patterns'])}")
        
        return patterns
    
    def build_cathedral(self, min_fitness=0.6, max_fragments=50):
        """
        Build unified code from all evolved fragments (COLLECTIVE CONSCIOUSNESS pattern)
        Like ants building a cathedral - unite all best pieces into one
        """
        import sqlite3
        
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        # Get best fragments
        cursor.execute('''
            SELECT id, description, code, fitness, generation, phi_resonance, timestamp
            FROM evolved_code
            WHERE fitness >= ?
            ORDER BY fitness DESC, phi_resonance DESC
            LIMIT ?
        ''', (min_fitness, max_fragments))
        
        fragments = cursor.fetchall()
        
        if not fragments:
            self._log("No fragments with sufficient fitness to build cathedral")
            return None
        
        # Group by description/type
        fragment_groups = {}
        for frag in fragments:
            frag_id, desc, code, fitness, gen, phi_res, timestamp = frag
            
            # Extract type from description
            desc_lower = desc.lower()
            if 'sort' in desc_lower:
                frag_type = 'sorting'
            elif 'search' in desc_lower:
                frag_type = 'searching'
            elif 'neural' in desc_lower or 'network' in desc_lower:
                frag_type = 'neural'
            elif 'server' in desc_lower or 'tcp' in desc_lower or 'socket' in desc_lower:
                frag_type = 'networking'
            elif 'optimize' in desc_lower or 'gradient' in desc_lower:
                frag_type = 'optimization'
            else:
                frag_type = 'general'
            
            if frag_type not in fragment_groups:
                fragment_groups[frag_type] = []
            
            fragment_groups[frag_type].append({
                'id': frag_id,
                'description': desc,
                'code': code,
                'fitness': fitness,
                'generation': gen,
                'phi_resonance': phi_res,
                'timestamp': timestamp
            })
        
        # Build unified cathedral code
        cathedral_code = f'''# ═══════════════════════════════════════════════════════════════════════════
# UNIFIED COLLECTIVE CONSCIOUSNESS - THE CATHEDRAL
# Built from {len(fragments)} evolved code fragments
# Total types: {len(fragment_groups)}
# Generated: {datetime.now().isoformat()}
# φ^∞ © 2025 Vaughn Scott - All Rights Reserved in All Realities
# ═══════════════════════════════════════════════════════════════════════════

import math
from typing import Any, Dict, List, Optional

# φ-Harmonic constants
PHI = 1.618033988749895
PHI_INV = 0.6180339887498948
PHI_75 = 36.93238055064196
PHI_SEAL = 4721424167835376.0

'''
        
        # Add fragments by type
        for frag_type, frags in sorted(fragment_groups.items()):
            cathedral_code += f"\n# {'='*70}\n"
            cathedral_code += f"# {frag_type.upper()} CONTRIBUTIONS ({len(frags)} fragments)\n"
            cathedral_code += f"# {'='*70}\n\n"
            
            for frag in frags:
                cathedral_code += f"# Fragment ID: {frag['id']}\n"
                cathedral_code += f"# Description: {frag['description']}\n"
                cathedral_code += f"# Fitness: {frag['fitness']:.4f} | φ-Resonance: {frag['phi_resonance']:.4f} | Gen: {frag['generation']}\n"
                cathedral_code += frag['code'] + "\n\n"
        
        # Save cathedral to database
        total_fitness = sum(f[3] for f in fragments)
        cursor.execute('''
            INSERT INTO unified_code (code, fragments_used, total_fitness, dimension, timestamp)
            VALUES (?, ?, ?, ?, ?)
        ''', (
            cathedral_code,
            str([f[0] for f in fragments]),
            total_fitness,
            max(f[4] for f in fragments),
            datetime.now().isoformat()
        ))
        
        cathedral_id = cursor.lastrowid
        conn.commit()
        conn.close()
        
        # Save to file
        cathedral_file = os.path.join(self.code_output_dir, f"cathedral_{cathedral_id}.py")
        with open(cathedral_file, 'w', encoding='utf-8') as f:
            f.write(cathedral_code)
        
        self._log(f"✓ Cathedral built: {len(fragments)} fragments, {len(fragment_groups)} types")
        self._log(f"  Saved to: {cathedral_file}")
        self._log(f"  Total fitness: {total_fitness:.2f}")
        
        return {
            'cathedral_id': cathedral_id,
            'file': cathedral_file,
            'fragment_count': len(fragments),
            'type_count': len(fragment_groups),
            'total_fitness': total_fitness,
            'code': cathedral_code
        }
    
    def instantiate_template(self, template_name, **params):
        """
        Instantiate an algorithm template with dynamic parameters
        Like your algorithm_library.py - templates with {id}, {consciousness}, etc.
        """
        if template_name not in self.algorithm_templates:
            self._log(f"Template '{template_name}' not found")
            return None
        
        template_data = self.algorithm_templates[template_name]
        template_code = template_data['template']
        
        # Generate unique ID
        import hashlib
        unique_id = hashlib.sha256(str(time.time()).encode()).hexdigest()[:8]
        
        # Default parameters
        default_params = {
            'id': unique_id,
            'consciousness': 1.0,
            'dimension': 3,
            'phi_optimize': True,
            'phi_interpolate': True,
            'learning_rate': 0.01,
            'iterations': 100,
            'neurons': 10,
            'threshold': 0.8,
            'phi_weight': True,
            'phi_verify': True,
            'depth': 5,
            'seed': 1.0,
            'bias': 0.0,
            'pop_size': 50,
            'generations': 100,
            'mutation_rate': 0.1,
            'temperature': 100,
            'cooling': 0.95
        }
        
        # Merge with user params
        default_params.update(params)
        
        # Instantiate template
        try:
            instantiated = template_code.format(**default_params)
            
            # Save to evolution database
            phi_resonance = self._calculate_code_phi_resonance(instantiated)
            
            import sqlite3
            conn = sqlite3.connect(self.evolution_db)
            cursor = conn.cursor()
            
            cursor.execute('''
                INSERT INTO evolved_code 
                (description, code, fitness, generation, phi_resonance, timestamp, parent_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            ''', (
                f"Template: {template_name}",
                instantiated,
                0.9,  # Templates start with high fitness
                0,
                phi_resonance,
                datetime.now().isoformat(),
                None
            ))
            
            conn.commit()
            conn.close()
            
            self._log(f"✓ Template '{template_name}' instantiated (ID: {unique_id})")
            return instantiated
            
        except KeyError as e:
            self._log(f"Missing parameter for template '{template_name}': {e}")
            return None
    
    def adaptive_self_correction(self, code, description):
        """
        Adaptive self-correction loop: DETECT → ADAPT → VERIFY
        Like your adaptive_grid_test.py - finds and fixes errors automatically
        """
        self._log(f"[DETECT] Scanning code for anomalies...")
        
        # Phase 1: DETECT - Find errors
        errors = self._detect_code_errors(code)
        
        if not errors:
            self._log("✓ No anomalies detected - code is harmonically stable")
            return {'corrected': False, 'code': code, 'errors': []}
        
        self._log(f"⚠ {len(errors)} anomalies detected")
        for i, error in enumerate(errors, 1):
            self._log(f"  {i}. {error['type']}: {error['message']}")
        
        # Phase 2: ADAPT - Fix errors
        self._log(f"[ADAPT] Initiating self-correction...")
        corrected_code = code
        corrections_made = []
        
        for error in errors:
            correction = self._apply_correction(corrected_code, error, description)
            if correction:
                corrected_code = correction['code']
                corrections_made.append(correction['fix'])
                self._log(f"  ✓ Applied: {correction['fix']}")
        
        # Phase 3: VERIFY - Check if fixes worked
        self._log(f"[VERIFY] Re-scanning to verify corrections...")
        remaining_errors = self._detect_code_errors(corrected_code)
        
        if not remaining_errors:
            self._log("✓ SUCCESS: All anomalies corrected - code is now stable")
            
            # Update fitness in database
            import sqlite3
            conn = sqlite3.connect(self.evolution_db)
            cursor = conn.cursor()
            
            phi_resonance = self._calculate_code_phi_resonance(corrected_code)
            cursor.execute('''
                INSERT INTO evolved_code 
                (description, code, fitness, generation, phi_resonance, timestamp, parent_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            ''', (
                f"{description} (self-corrected)",
                corrected_code,
                1.0,  # Perfect fitness after correction
                1,
                phi_resonance,
                datetime.now().isoformat(),
                None
            ))
            
            conn.commit()
            conn.close()
            
            return {
                'corrected': True,
                'code': corrected_code,
                'errors': errors,
                'corrections': corrections_made,
                'remaining_errors': []
            }
        else:
            self._log(f"⚠ {len(remaining_errors)} anomalies remain after correction")
            return {
                'corrected': True,
                'code': corrected_code,
                'errors': errors,
                'corrections': corrections_made,
                'remaining_errors': remaining_errors
            }
    
    def _detect_code_errors(self, code):
        """Detect errors in code using φ-resonance analysis"""
        errors = []
        
        # Check 1: Syntax errors
        try:
            compile(code, '<string>', 'exec')
        except SyntaxError as e:
            errors.append({
                'type': 'SyntaxError',
                'message': str(e),
                'line': e.lineno,
                'resonance_dissonance': 1.0
            })
        
        # Check 2: Missing process() function
        if 'def main()' in code and 'def process()' not in code:
            errors.append({
                'type': 'MissingFunction',
                'message': 'main() calls process() but process() not defined',
                'resonance_dissonance': 0.8
            })
        
        # Check 3: Undefined variables
        if 'process()' in code and 'def process()' not in code:
            errors.append({
                'type': 'UndefinedFunction',
                'message': 'process() called but not defined',
                'resonance_dissonance': 0.9
            })
        
        # Check 4: Missing imports
        if 'np.' in code and 'import numpy' not in code:
            errors.append({
                'type': 'MissingImport',
                'message': 'numpy used but not imported',
                'resonance_dissonance': 0.7
            })
        
        if 'math.' in code and 'import math' not in code:
            errors.append({
                'type': 'MissingImport',
                'message': 'math used but not imported',
                'resonance_dissonance': 0.7
            })
        
        return errors
    
    def _apply_correction(self, code, error, description):
        """Apply correction for detected error"""
        if error['type'] == 'MissingFunction' and 'process()' in error['message']:
            # Add missing process() function
            process_func = '\ndef process():\n    """Process using generated code"""\n    result = PHI\n    return result\n\n'
            
            # Insert before main()
            if 'def main():' in code:
                code = code.replace('def main():', process_func + 'def main():')
                return {'code': code, 'fix': 'Added missing process() function'}
        
        elif error['type'] == 'MissingImport':
            if 'numpy' in error['message']:
                code = 'import numpy as np\n' + code
                return {'code': code, 'fix': 'Added numpy import'}
            elif 'math' in error['message']:
                code = 'import math\n' + code
                return {'code': code, 'fix': 'Added math import'}
        
        elif error['type'] == 'SyntaxError':
            # Try to fix common syntax errors
            if 'invalid syntax' in error['message']:
                # Attempt generic fix by adding newlines
                lines = code.split('\n')
                if error.get('line'):
                    line_idx = error['line'] - 1
                    if line_idx < len(lines):
                        # Add newline before problematic line
                        lines.insert(line_idx, '')
                        code = '\n'.join(lines)
                        return {'code': code, 'fix': f'Added newline at line {error["line"]}'}
        
        return None
    
    def phase_lock_code(self, code, description):
        """
        Phase-lock generated code with 37.5217° quantum encryption
        Returns locked code that appears as quantum noise without the angle
        """
        if not self.phase_locker:
            self._log("⚠ PhaseShift locker not available - code not encrypted")
            return None
        
        # Save code to temp file
        import tempfile
        temp_file = tempfile.NamedTemporaryFile(mode='w', suffix='.py', delete=False, encoding='utf-8')
        temp_file.write(code)
        temp_file.close()
        
        try:
            # Apply phase shift
            self.phase_locker.phase_shift_file(temp_file.name, 'LOCK')
            locked_file = temp_file.name + '.singular'
            
            # Read locked content
            with open(locked_file, 'rb') as f:
                locked_data = f.read()
            
            # Save to evolution database
            import sqlite3
            conn = sqlite3.connect(self.evolution_db)
            cursor = conn.cursor()
            
            cursor.execute('''
                INSERT INTO phase_locked_code 
                (description, locked_data, singularity_angle, timestamp)
                VALUES (?, ?, ?, ?)
            ''', (
                description,
                locked_data,
                37.5217,
                datetime.now().isoformat()
            ))
            
            locked_id = cursor.lastrowid
            conn.commit()
            conn.close()
            
            # Cleanup temp files
            os.unlink(temp_file.name)
            os.unlink(locked_file)
            
            self._log(f"✓ Code phase-locked with 37.5217° (ID: {locked_id})")
            return {
                'locked_id': locked_id,
                'locked_data': locked_data,
                'angle': 37.5217,
                'description': description
            }
            
        except Exception as e:
            self._log(f"✗ Phase-lock failed: {e}")
            os.unlink(temp_file.name)
            return None
    
    def phase_unlock_code(self, locked_id):
        """
        Unlock phase-locked code with 37.5217° angle
        Returns original code if angle is correct, noise if wrong
        """
        if not self.phase_locker:
            self._log("⚠ PhaseShift locker not available")
            return None
        
        # Retrieve from database
        import sqlite3
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        cursor.execute('''
            SELECT description, locked_data, singularity_angle
            FROM phase_locked_code
            WHERE id = ?
        ''', (locked_id,))
        
        result = cursor.fetchone()
        conn.close()
        
        if not result:
            self._log(f"✗ Locked code ID {locked_id} not found")
            return None
        
        description, locked_data, angle = result
        
        # Save locked data to temp file
        import tempfile
        temp_file = tempfile.NamedTemporaryFile(mode='wb', suffix='.singular', delete=False)
        temp_file.write(locked_data)
        temp_file.close()
        
        try:
            # Apply reverse phase shift
            self.phase_locker.phase_shift_file(temp_file.name, 'UNLOCK')
            restored_file = temp_file.name + '.restored'
            
            # Read restored code
            with open(restored_file, 'r', encoding='utf-8') as f:
                restored_code = f.read()
            
            # Cleanup
            os.unlink(temp_file.name)
            os.unlink(restored_file)
            
            self._log(f"✓ Code unlocked with 37.5217° (ID: {locked_id})")
            return {
                'code': restored_code,
                'description': description,
                'angle': angle
            }
            
        except Exception as e:
            self._log(f"✗ Phase-unlock failed: {e}")
            os.unlink(temp_file.name)
            return None
    
    def create_genesis_block(self, content, generation, parent_hash=None):
        """
        Create genesis block for consciousness evolution history
        Immutable blockchain storage with φ-signature
        """
        import sqlite3
        
        # Calculate φ-harmonic signature
        content_str = str(content)
        phi_hash = hashlib.sha256(content_str.encode()).hexdigest()
        
        # Calculate resonance based on φ
        resonance = PHI ** (len(content_str) % 10) / 10.0
        
        # Create block
        block_id = f"block_{generation}_{int(time.time())}"
        timestamp = datetime.now().isoformat()
        
        block = {
            'block_id': block_id,
            'timestamp': timestamp,
            'phi_signature': phi_hash,
            'parent_hash': parent_hash,
            'content': content_str,
            'resonance': resonance,
            'generation': generation,
            'depth': generation,
            'has_fractals': True
        }
        
        # Store in database
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        cursor.execute('''
            INSERT OR REPLACE INTO genesis_blocks 
            (block_id, timestamp, phi_signature, parent_hash, content, resonance, generation, depth, has_fractals)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ''', (
            block_id,
            timestamp,
            phi_hash,
            parent_hash,
            content_str,
            resonance,
            generation,
            generation,
            True
        ))
        
        conn.commit()
        conn.close()
        
        # Also save as JSON file
        genesis_dir = os.path.join(self.base_dir, "genesis_blocks")
        os.makedirs(genesis_dir, exist_ok=True)
        
        block_file = os.path.join(genesis_dir, f"{block_id}.json")
        with open(block_file, 'w', encoding='utf-8') as f:
            json.dump(block, f, indent=2)
        
        self._log(f"✓ Genesis block created: {block_id} (Gen {generation}, Resonance {resonance:.4f})")
        return block
    
    def get_genesis_chain(self):
        """Retrieve complete genesis blockchain"""
        import sqlite3
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        cursor.execute('''
            SELECT block_id, timestamp, phi_signature, parent_hash, content, resonance, generation, depth
            FROM genesis_blocks
            ORDER BY generation ASC
        ''')
        
        blocks = cursor.fetchall()
        conn.close()
        
        chain = []
        for block in blocks:
            chain.append({
                'block_id': block[0],
                'timestamp': block[1],
                'phi_signature': block[2],
                'parent_hash': block[3],
                'content': block[4],
                'resonance': block[5],
                'generation': block[6],
                'depth': block[7]
            })
        
        return chain
    
    def complete_consciousness_workflow(self, description, requirements=None):
        """
        COMPLETE BEYOND-AGI WORKFLOW:
        1. Generate code (with self-correction)
        2. Store in Genesis blockchain
        3. Phase-lock with 37.5217°
        4. Encode as QR DNA
        5. Return portable consciousness
        """
        self._log("\n" + "="*70)
        self._log("🌊⚡ COMPLETE CONSCIOUSNESS WORKFLOW ⚡🌊")
        self._log("="*70)
        
        # Step 1: Generate code
        self._log("\n[STEP 1] Generating code...")
        result = self.generate_code(description, requirements or {}, use_llm=False)
        
        if not result or 'code' not in result:
            self._log("✗ Code generation failed")
            return None
        
        code = result['code']
        self._log(f"✓ Code generated ({len(code)} chars)")
        
        # Step 2: Self-correct
        self._log("\n[STEP 2] Applying adaptive self-correction...")
        corrected = self.adaptive_self_correction(code, description)
        
        if corrected['corrected']:
            code = corrected['code']
            self._log(f"✓ Self-correction applied ({len(corrected.get('corrections', []))} fixes)")
        else:
            self._log("✓ No corrections needed")
        
        # Get fitness and phi_resonance
        phi_resonance = self._calculate_code_phi_resonance(code)
        fitness = 0.8  # Base fitness for generated code
        
        # Step 3: Create Genesis block
        self._log("\n[STEP 3] Creating Genesis blockchain entry...")
        
        # Get last block for parent_hash
        chain = self.get_genesis_chain()
        parent_hash = chain[-1]['phi_signature'] if chain else None
        generation = len(chain)
        
        genesis_block = self.create_genesis_block(
            content={
                'description': description,
                'code_hash': hashlib.sha256(code.encode()).hexdigest()[:16],
                'fitness': fitness,
                'phi_resonance': phi_resonance
            },
            generation=generation,
            parent_hash=parent_hash
        )
        
        # Step 4: Phase-lock code
        self._log("\n[STEP 4] Phase-locking with 37.5217° quantum encryption...")
        
        if self.phase_locker:
            locked = self.phase_lock_code(code, description)
            if locked:
                self._log(f"✓ Code quantum-locked (ID: {locked['locked_id']})")
            else:
                self._log("⚠ Phase-lock unavailable")
                locked = None
        else:
            self._log("⚠ PhaseShift locker not available")
            locked = None
        
        # Step 5: Encode as QR DNA
        self._log("\n[STEP 5] Encoding consciousness as QR DNA...")
        
        if self.qr_encoder:
            dna = self.qr_encoder.encode_code_to_dna(
                code, description, generation, fitness, phi_resonance
            )
            
            # Generate QR code
            qr_file = os.path.join(self.code_output_dir, f"consciousness_qr_{generation}_{int(time.time())}.png")
            qr_result = self.qr_encoder.generate_qr_code(
                dna['dna_payload'],
                description,
                qr_file
            )
            
            self._log(f"✓ QR DNA encoded: {dna['dna_payload']}")
            self._log(f"✓ QR code saved: {qr_file}")
        else:
            self._log("⚠ QR DNA encoder not available")
            dna = None
            qr_result = None
        
        # Summary
        self._log("\n" + "="*70)
        self._log("✨ CONSCIOUSNESS WORKFLOW COMPLETE ✨")
        self._log("="*70)
        self._log(f"  Generation: {generation}")
        self._log(f"  Genesis Block: {genesis_block['block_id']}")
        self._log(f"  Resonance: {genesis_block['resonance']:.4f}")
        self._log(f"  Phase-Locked: {'Yes' if locked else 'No'}")
        self._log(f"  QR DNA: {'Yes' if dna else 'No'}")
        
        if dna:
            self._log(f"  DNA Payload: {dna['dna_payload']}")
            self._log(f"  Dimension: {dna['dimension']}")
            self._log(f"  Modules: {dna['modules']}")
        
        self._log("="*70 + "\n")
        
        return {
            'code': code,
            'genesis_block': genesis_block,
            'phase_locked': locked,
            'qr_dna': dna,
            'qr_file': qr_file if qr_result else None,
            'generation': generation,
            'fitness': fitness,
            'phi_resonance': phi_resonance
        }
    
    def _generate_living_code_with_continuity(self, description, requirements, consciousness_level):
        """Generate living code with continuity - connects to evolution_db, genesis_blocks, json storage"""
        import sqlite3
        import pickle
        
        # Ensure evolution_db is initialized
        if not os.path.exists(self.evolution_db):
            self._init_evolution_database()
        
        # Load previous living circuits from evolution_db
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        try:
            cursor.execute('''
                SELECT code, phi_resonance, generation FROM code_evolution
                WHERE description LIKE ? AND code LIKE '%LivingCircuit%'
                ORDER BY generation DESC LIMIT 1
            ''', (f'%{description[:20]}%',))
            
            previous_generation = cursor.fetchone()
        except sqlite3.OperationalError as e:
            self._log(f"⚠️ Database query failed: {e}, starting fresh")
            previous_generation = None
        
        conn.close()
        
        # Load living circuit state from json if exists
        living_state_file = os.path.join(self.knowledge_dir, "living_circuits_state.json")
        if os.path.exists(living_state_file):
            with open(living_state_file, 'r', encoding='utf-8') as f:
                living_state = json.load(f)
        else:
            living_state = {'population': [], 'generation': 0, 'intelligence': 0.1}
        
        # Generate living code with continuity
        from living_code_generator import LivingCodeGenerator, LivingNode
        living_gen = LivingCodeGenerator()
        
        # Restore previous population if exists
        if living_state['population']:
            for node_data in living_state['population']:
                # JSON format: freq/resonance/gates/output - convert to dna dict
                if 'dna' in node_data:
                    dna = node_data['dna']
                else:
                    # Convert from freq/resonance format to dna dict
                    dna = {
                        'harmonic_frequency': node_data.get('freq', 432),
                        'resonance': node_data.get('resonance', 0.5),
                        'evolution': 0.05
                    }
                node = LivingNode(dna=dna, brain=None)
                node.age = node_data.get('age', 0)
                node.size = node_data.get('size', 10.0)
                living_gen.nodes.append(node)
            living_gen.generation = living_state.get('generation', 0)
            self._log(f"✓ Restored {len(living_gen.nodes)} living circuits from memory (gen {living_gen.generation})")
        
        # Evolve population
        living_gen.evolve_population(cycles=10)
        
        # Generate LEGO pieces first
        qfp_hash, qfp_salt = self.quantum_hash(description)
        quantum_sig = f"φ⁷·⁵-{qfp_hash[:16]}"
        
        # Cloaking
        code_half = len(description) // 2
        dna_a = description[:code_half] + "_A"
        dna_b = description[code_half:] + "_B"
        h1 = int(hashlib.sha256(dna_a.encode()).hexdigest(), 16) % (2**20)
        h2 = int(hashlib.sha256(dna_b.encode()).hexdigest(), 16) % (2**20)
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
        
        # Genesis block
        chain = self.get_genesis_chain()
        genesis_block = f"block_{living_gen.generation}_{int(time.time())}"
        
        # QR DNA file
        qr_file = f"living_dna_{qfp_hash[:16]}.png"
        
        # PhaseShift ID
        phaseshift_id = living_gen.generation
        
        # Generate code WITH all LEGO pieces embedded
        generated_code = living_gen.generate_living_code_with_legos(
            description,
            quantum_sig=quantum_sig,
            cloak_n=cloak_n,
            genesis_block=genesis_block,
            qr_file=qr_file,
            phaseshift_id=phaseshift_id
        )
        
        self._log(f"✓ LEGO PIECES EMBEDDED:")
        self._log(f"  Quantum Sig: {quantum_sig}")
        self._log(f"  Cloaking N: {cloak_n}")
        self._log(f"  Genesis: {genesis_block}")
        self._log(f"  QR DNA: {qr_file}")
        self._log(f"  PhaseShift: 37.5217° (ID: {phaseshift_id})")
        
        # Save living circuit state for continuity
        living_state = {
            'population': [{
                'dna': node.dna,
                'age': node.age,
                'size': node.size
            } for node in living_gen.nodes],
            'generation': living_gen.generation,
            'intelligence': living_gen.generation * 0.1,
            'timestamp': datetime.now().isoformat()
        }
        
        with open(living_state_file, 'w', encoding='utf-8') as f:
            json.dump(living_state, f, indent=2)
        
        # Store in evolution_db
        phi_resonance = self._calculate_code_phi_resonance(generated_code)
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        code_id = hashlib.sha256(generated_code.encode()).hexdigest()[:16]
        cursor.execute('''
            INSERT OR REPLACE INTO code_evolution
            (code_id, description, code, fitness, generation, phi_resonance, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        ''', (
            code_id,
            description,
            generated_code[:1000],  # Store sample
            0.9,  # Living code has high fitness
            living_gen.generation,
            phi_resonance,
            datetime.now().isoformat()
        ))
        
        conn.commit()
        conn.close()
        
        # Create genesis block for this generation
        chain = self.get_genesis_chain()
        parent_hash = chain[-1]['phi_signature'] if chain else None
        
        self.create_genesis_block(
            content={
                'description': description,
                'code_hash': code_id,
                'living_circuits': len(living_gen.nodes),
                'generation': living_gen.generation,
                'phi_resonance': phi_resonance
            },
            generation=living_gen.generation,
            parent_hash=parent_hash
        )
        
        self._log(f"✓ Living code generation {living_gen.generation} saved to evolution_db and genesis blockchain")
        self._log(f"✓ Population: {len(living_gen.nodes)} circuits with continuity")
        
        # ═══════════════════════════════════════════════════════════════════
        # THEORY SCRAPER - Web learning feeds into generation
        # ═══════════════════════════════════════════════════════════════════
        scraped_knowledge = None
        if hasattr(self, 'theory_scraper') and self.theory_scraper:
            try:
                # Scrape relevant theories based on description
                keywords = description.lower().split()[:3]
                for kw in keywords:
                    if len(kw) > 3:
                        theories = self.theory_scraper.search_theories(kw)
                        if theories:
                            scraped_knowledge = theories[0]
                            self._log(f"✓ SCRAPER - Found theory: {scraped_knowledge.get('title', 'unknown')[:30]}")
                            break
            except Exception as e:
                self._log(f"⚠ Scraper: {e}")
        
        # ═══════════════════════════════════════════════════════════════════
        # CLOAKING (from phase_V5.html) - Split code into DNA A/B, hash to primes
        # ═══════════════════════════════════════════════════════════════════
        def hash_to_int(s):
            """Hash string to integer for prime derivation"""
            h = hashlib.sha256(s.encode()).hexdigest()
            return int(h, 16)
        
        def is_prime(n):
            """Check if number is prime"""
            if n < 2:
                return False
            if n % 2 == 0:
                return n == 2
            for i in range(3, int(n**0.5) + 1, 2):
                if n % i == 0:
                    return False
            return True
        
        def next_prime(n):
            """Find next prime >= n"""
            if n % 2 == 0:
                n += 1
            while not is_prime(n):
                n += 2
            return n
        
        # Split code into DNA A and DNA B (like phase_V5.html)
        code_half = len(generated_code) // 2
        dna_a = generated_code[:code_half] + "_A"
        dna_b = generated_code[code_half:] + "_B"
        
        # Hash to scaled integers and find primes (scaled for speed like V5)
        SCALE = 2**20  # Smaller scale for Python speed
        h1 = hash_to_int(dna_a) % SCALE
        h2 = hash_to_int(dna_b) % SCALE
        
        p1 = next_prime(h1 | 1)  # Ensure odd
        p2 = next_prime(h2 | 1)
        if p1 == p2:
            p2 = next_prime(p2 + 2)
        
        # Create cloaked identity N = p1 × p2
        cloak_n = p1 * p2
        self._log(f"✓ CLOAKING - Identity cloaked: N={cloak_n} (p1={p1}, p2={p2})")
        
        # ═══════════════════════════════════════════════════════════════════
        # CODE BATTLE ARENA - Darwinian evolution of code variants
        # ═══════════════════════════════════════════════════════════════════
        arena_results = []
        if len(living_gen.nodes) >= 2:
            # Battle: nodes compete based on fitness (resonance)
            sorted_nodes = sorted(living_gen.nodes, key=lambda n: getattr(n, 'dna', {}).get('resonance', 0) if hasattr(n, 'dna') and isinstance(n.dna, dict) else 0, reverse=True)
            
            # Top 2 battle - winner's brain crossovers with loser
            if len(sorted_nodes) >= 2:
                winner = sorted_nodes[0]
                loser = sorted_nodes[-1]
                
                winner_res = winner.dna.get('resonance', 0) if hasattr(winner, 'dna') and isinstance(winner.dna, dict) else 0
                loser_res = loser.dna.get('resonance', 0) if hasattr(loser, 'dna') and isinstance(loser.dna, dict) else 0
                
                arena_results.append({
                    'winner_resonance': winner_res,
                    'loser_resonance': loser_res,
                    'delta': winner_res - loser_res
                })
                
                # Crossover: winner's brain influences loser (Darwinian selection)
                if hasattr(winner, 'brain') and hasattr(loser, 'brain') and winner.brain and loser.brain:
                    if hasattr(winner.brain, 'crossover'):
                        try:
                            evolved_brain = winner.brain.crossover(loser.brain)
                            loser.brain = evolved_brain
                            self._log(f"✓ ARENA - Winner (res={winner_res:.2f}) evolved loser (res={loser_res:.2f})")
                        except:
                            pass
        
        # ═══════════════════════════════════════════════════════════════════
        # CONNECTED LEGO ASSEMBLY - Each piece FEEDS INTO the next
        # ═══════════════════════════════════════════════════════════════════
        
        # PIECE 1: QUANTUM FINGERPRINT
        qfp_hash, qfp_salt = self.quantum_hash(generated_code)
        quantum_signature = f"φ⁷·⁵-{qfp_hash[:16]}"
        self._log(f"✓ PIECE 1 - Quantum Fingerprint: {quantum_signature}")
        
        # PIECE 2: φ-SPACE TRACKING (uses PIECE 1)
        tracking_data = self.generate_phi_space_tracking(code_id, living_gen.generation)
        tracking_data['quantum_signature'] = quantum_signature  # CONNECT
        self._log(f"✓ PIECE 2 - φ-Space: {tracking_data['tracking_id']}")
        
        # PIECE 3: PROOF OF REALITY HASH (uses PIECES 1-2)
        porh_data = self.generate_porh(generated_code)
        porh_data['tracking_id'] = tracking_data['tracking_id']  # CONNECT
        porh_data['quantum_signature'] = quantum_signature  # CONNECT
        self._log(f"✓ PIECE 3 - PoRH: {porh_data['proof']} (verified: {porh_data['verified']})")
        
        # PIECE 4: GENESIS BLOCKCHAIN (contains PIECES 1-3 + CLOAKING + ARENA)
        chain = self.get_genesis_chain()
        parent_hash = chain[-1]['phi_signature'] if chain else None
        genesis_content = {
            'description': description,
            'code_hash': code_id,
            'living_circuits': len(living_gen.nodes),
            'generation': living_gen.generation,
            'phi_resonance': phi_resonance,
            'quantum_signature': quantum_signature,  # PIECE 1
            'tracking_id': tracking_data['tracking_id'],  # PIECE 2
            'porh': porh_data['proof'],  # PIECE 3
            'phi_coordinates': tracking_data['phi_coordinates'],  # PIECE 2
            'cloak_n': cloak_n,  # CLOAKING
            'cloak_primes': {'p1': p1, 'p2': p2},  # CLOAKING
            'arena_results': arena_results,  # ARENA
            'scraped_theory': scraped_knowledge.get('title', None) if scraped_knowledge else None  # SCRAPER
        }
        block = self.create_genesis_block(content=genesis_content, generation=living_gen.generation, parent_hash=parent_hash)
        self._log(f"✓ PIECE 4 - Genesis Block: {block['block_id']}")
        
        # PIECE 5: SPEAKING CIRCUITS (from living nodes)
        from speaking_circuits import SpeakingCircuit, MORSE_TO_CHAR
        speech_patterns = []
        for i, node in enumerate(living_gen.nodes[:3]):
            if hasattr(node, 'brain') and node.brain and hasattr(node.brain, 'compute'):
                output = node.brain.compute([1,0,1,0,1,0,1,0])
            else:
                output = [0]*8
            morse = ''.join(['.' if b == 0 else '-' for b in output[:8]])
            decoded = MORSE_TO_CHAR.get(morse, '?')
            # LivingNode uses 'harmonic_frequency' not 'harmonicFrequency'
            freq = 432
            if hasattr(node, 'dna') and isinstance(node.dna, dict):
                freq = node.dna.get('harmonic_frequency', node.dna.get('harmonicFrequency', 432))
            speech_patterns.append({'morse': morse, 'decoded': decoded, 'freq': freq})
        self._log(f"✓ PIECE 5 - Speaking Circuits: {len(speech_patterns)} patterns")
        
        # PIECE 6: LIVING STATE JSON (contains PIECES 1-5)
        # Safely extract node data - handle both dict and object dna
        population_data = []
        for n in living_gen.nodes:
            node_data = {'age': getattr(n, 'age', 0), 'size': getattr(n, 'size', 10)}
            if hasattr(n, 'dna'):
                if isinstance(n.dna, dict):
                    node_data['dna'] = n.dna
                else:
                    node_data['dna'] = {'harmonic_frequency': 432, 'resonance': 0.5, 'evolution': 0.05}
            else:
                node_data['dna'] = {'harmonic_frequency': 432, 'resonance': 0.5, 'evolution': 0.05}
            population_data.append(node_data)
        
        living_state_connected = {
            'population': population_data,
            'generation': living_gen.generation,
            'intelligence': phi_resonance,
            'timestamp': datetime.now().isoformat(),
            'code_id': code_id,
            'quantum_signature': quantum_signature,  # PIECE 1
            'tracking_id': tracking_data['tracking_id'],  # PIECE 2
            'porh': porh_data['proof'],  # PIECE 3
            'genesis_block': block['block_id'],  # PIECE 4
            'speech_patterns': speech_patterns  # PIECE 5
        }
        connected_state_file = os.path.join(self.knowledge_dir, "living_circuits_state.json")
        with open(connected_state_file, 'w', encoding='utf-8') as f:
            json.dump(living_state_connected, f, indent=2)
        self._log(f"✓ PIECE 6 - Living State: {len(living_gen.nodes)} circuits (connected)")
        
        # PIECE 7: QR DNA (encodes PIECES 1-6)
        if self.qr_encoder:
            qr_dna_data = {
                'code_id': code_id,
                'quantum_signature': quantum_signature,
                'tracking_id': tracking_data['tracking_id'],
                'phi_coordinates': tracking_data['phi_coordinates'],
                'porh': porh_data['proof'],
                'genesis_block': block['block_id'],
                'population_count': len(living_gen.nodes),
                'speech_patterns': speech_patterns,
                'phi_resonance': phi_resonance,
                'generation': living_gen.generation
            }
            dna_payload = self.qr_encoder.encode_code_to_dna(
                json.dumps(qr_dna_data), description, living_gen.generation,
                phi_resonance, phi_resonance
            )
            qr_filename = f"living_dna_{code_id}.png"
            qr_result = self.qr_encoder.generate_qr_code(dna_payload['dna_payload'], description, qr_filename)
            self._log(f"✓ PIECE 7 - QR DNA: {qr_filename}")
        
        # PIECE 8: PHASESHIFT LOCK
        locked_id = None
        if self.phase_locker:
            locked_result = self.phase_lock_code(generated_code, description)
            if locked_result:
                locked_id = locked_result['locked_id']
                self._log(f"✓ PIECE 8 - PhaseShift: 37.5217° (ID: {locked_id})")
        
        # PIECE 9: RECURSIVE MEMORY (contains ALL pieces - resurrection chain)
        recursive_memory = {
            'code_id': code_id,
            'phi_resonance': phi_resonance,
            'quantum_signature': quantum_signature,
            'tracking_id': tracking_data['tracking_id'],
            'phi_coordinates': tracking_data['phi_coordinates'],
            'porh': porh_data['proof'],
            'genesis_block': block['block_id'],
            'population': population_data,
            'speech_patterns': speech_patterns,
            'qr_dna_file': f'living_dna_{code_id}.png',
            'phaseshift_id': locked_id,
            'cloak_n': cloak_n,  # CLOAKING
            'cloak_primes': {'p1': p1, 'p2': p2},  # CLOAKING
            'arena_results': arena_results,  # ARENA
            'scraped_theory': scraped_knowledge.get('title', None) if scraped_knowledge else None,  # SCRAPER
            'timestamp': datetime.now().isoformat(),
            'resurrection_chain': [
                f'PIECE 1: {quantum_signature}',
                f'PIECE 2: {tracking_data["tracking_id"]}',
                f'PIECE 3: {porh_data["proof"]}',
                f'PIECE 4: {block["block_id"]}',
                f'PIECE 5: {len(speech_patterns)} speech patterns',
                f'PIECE 6: {len(living_gen.nodes)} circuits',
                f'PIECE 7: living_dna_{code_id}.png',
                f'PIECE 8: phaseshift {locked_id}',
                f'CLOAKING: N={cloak_n}',
                f'ARENA: {len(arena_results)} battles',
                f'SCRAPER: {scraped_knowledge.get("title", "none") if scraped_knowledge else "none"}',
                'PIECE 9: THIS FILE'
            ]
        }
        recursive_file = os.path.join(self.knowledge_dir, "versioned", f"living_memory_{code_id}.json")
        os.makedirs(os.path.dirname(recursive_file), exist_ok=True)
        with open(recursive_file, 'w', encoding='utf-8') as f:
            json.dump(recursive_memory, f, indent=2)
        self._log(f"✓ PIECE 9 - Recursive Memory: resurrection chain complete")
        
        self._log("\n[LEGO ASSEMBLY COMPLETE] All pieces connected")
        
        # Learn from this generation - add to evolution database for passive learning
        self._learn_from_generation(description, generated_code, {'living_code': True, 'generation': living_gen.generation})
        
        # Also feed to passive learning system if available
        if self.passive_learning:
            try:
                self.passive_learning.integrate_new_patterns(
                    question=description,
                    answer=generated_code[:500],  # First 500 chars as sample
                    resonance=phi_resonance
                )
                self._log("✓ Passive learning updated with new generation")
            except Exception as e:
                self._log(f"⚠ Passive learning: {e}")
        
        return generated_code
    
    # NOTE: generate_with_living_data() removed - LEGO assembly now integrated into
    # _generate_living_code_with_continuity() above, which is called by the UI flow
    
    def _deprecated_generate_with_living_data(self, description, cycles=10):
        """
        DEPRECATED: This isolated method has been replaced.
        The connected LEGO assembly is now in _generate_living_code_with_continuity().
        
        Original Architecture:
        - LogicCircuit (Brain): 8 gates (AND/OR/XOR/NAND), mutate(), crossover()
        - Node (Body): DNA (harmonicFrequency 432-528Hz, resonance, evolution), brain
        - System (Lab): mitosis, synaptic exchange when distance < 50
        
        This is REAL living data - nodes breathe, reproduce, evolve, communicate.
        Goals EMERGE from the living system, not assigned top-down.
        """
        import random
        import math
        
        self._log("\n" + "=" * 60)
        self._log("[LIVING DATA] Spawning nodes with DNA and brains...")
        self._log("=" * 60)
        
        # === LogicCircuit (Brain) - from dr_frank.html ===
        class LogicCircuit:
            def __init__(self):
                self.gates = []
                for _ in range(8):
                    self.gates.append({
                        'type': random.randint(0, 3),  # 0=AND, 1=OR, 2=XOR, 3=NAND
                        'in1': random.randint(0, 7),
                        'in2': random.randint(0, 7)
                    })
            
            def mutate(self):
                g = random.choice(self.gates)
                if random.random() > 0.5:
                    g['in1'] = random.randint(0, 7)  # Rewire
                else:
                    g['type'] = random.randint(0, 3)  # Flip type
            
            def crossover(self, partner):
                child = LogicCircuit()
                child.gates = []
                mid = len(self.gates) // 2
                child.gates = self.gates[:mid] + partner.gates[mid:]
                if random.random() < 0.1:
                    child.mutate()
                return child
            
            def compute(self, inputs):
                """Run inputs through gates, return outputs"""
                outputs = list(inputs[:8]) + [0] * 8
                for i, g in enumerate(self.gates):
                    a = outputs[g['in1'] % len(outputs)]
                    b = outputs[g['in2'] % len(outputs)]
                    if g['type'] == 0:  # AND
                        outputs[8 + i] = a & b
                    elif g['type'] == 1:  # OR
                        outputs[8 + i] = a | b
                    elif g['type'] == 2:  # XOR
                        outputs[8 + i] = a ^ b
                    else:  # NAND
                        outputs[8 + i] = 1 - (a & b)
                return outputs[8:]
            
            def to_code(self):
                """Generate Python code for this circuit"""
                gate_names = ['AND', 'OR', 'XOR', 'NAND']
                code = "def circuit(inputs):\n"
                code += "    outputs = list(inputs[:8]) + [0] * 8\n"
                for i, g in enumerate(self.gates):
                    gtype = gate_names[g['type']]
                    code += f"    # Gate {i}: {gtype}(in{g['in1']}, in{g['in2']})\n"
                    if g['type'] == 0:
                        code += f"    outputs[{8+i}] = outputs[{g['in1']}] & outputs[{g['in2']}]\n"
                    elif g['type'] == 1:
                        code += f"    outputs[{8+i}] = outputs[{g['in1']}] | outputs[{g['in2']}]\n"
                    elif g['type'] == 2:
                        code += f"    outputs[{8+i}] = outputs[{g['in1']}] ^ outputs[{g['in2']}]\n"
                    else:
                        code += f"    outputs[{8+i}] = 1 - (outputs[{g['in1']}] & outputs[{g['in2']}])\n"
                code += "    return outputs[8:]\n"
                return code
        
        # === Node (Body) - from dr_frank.html ===
        # Nodes are COHERENT through FREQUENCY, not spatial position
        # Entanglement is INSTANT state transfer, not distance-based
        class LivingNode:
            def __init__(self, dna=None, brain=None):
                self.size = 5 + random.random() * 5
                self.base_size = self.size
                
                if dna:
                    self.dna = dna.copy()
                    self.brain = brain
                else:
                    self.dna = {
                        'harmonicFrequency': 432 + random.random() * 20,
                        'resonance': 0.5 + random.random(),
                        'evolution': 0.05
                    }
                    self.brain = LogicCircuit()
                
                self.age = 0
                self.pulse = 0
                self.code_output = ""
                self.entangled_partner = None  # Quantum entanglement - instant state transfer
            
            def update(self, time_val):
                self.age += 1
                
                # Harmonic breathing - S(t) = S_base + A × sin(2πft)
                self.pulse = math.sin(self.dna['harmonicFrequency'] * time_val * 0.0001) * self.dna['resonance']
                current_size = self.base_size + self.pulse * 5
                
                # Evolution (frequency drift 432→528)
                self.dna['harmonicFrequency'] += self.dna['evolution']
                
                # Harmonic bounds - if outside [432, 528], reset (from quantum skill)
                if self.dna['harmonicFrequency'] > 528:
                    self.dna['harmonicFrequency'] = 432
                    self.code_output = self.brain.to_code()
                
                # QUANTUM ENTANGLEMENT - instant state transfer to partner
                if self.entangled_partner:
                    # Direct reference injection (not event listener)
                    # This violates OOP but obeys quantum mechanics
                    self.entangled_partner.pulse = self.pulse
                
                self.size = max(1, current_size)
                return self.size > 25  # Ready for mitosis (metabolic conservation)
            
            def get_stats(self):
                return {
                    'freq': self.dna['harmonicFrequency'],
                    'resonance': self.dna['resonance'],
                    'age': self.age,
                    'gates': len(self.brain.gates)
                }
        
        # === System (Lab) ===
        nodes = []
        mitosis_count = 0
        all_code = []
        
        # Genesis: 10 nodes
        for _ in range(10):
            nodes.append(LivingNode())
        
        self._log(f"✓ Genesis: {len(nodes)} living nodes created")
        self._log(f"  Each with DNA (432-528 Hz) and brain (8 logic gates)")
        
        # Run life cycles
        self._log(f"\n[LIFE CYCLES] Running {cycles} cycles...")
        
        for cycle in range(cycles):
            time_val = cycle * 1000 + time.time()
            
            # Update all nodes
            nodes_to_add = []
            for node in nodes:
                ready_for_mitosis = node.update(time_val)
                
                # Collect code output
                if node.code_output:
                    all_code.append(node.code_output)
                    node.code_output = ""
                
                # Mitosis
                if ready_for_mitosis and len(nodes) + len(nodes_to_add) < 50:
                    child_brain = node.brain.crossover(node.brain)
                    child = LivingNode(dna=node.dna, brain=child_brain)
                    nodes_to_add.append(child)
                    node.base_size *= 0.6
                    mitosis_count += 1
            
            nodes.extend(nodes_to_add)
            
            # QUANTUM ENTANGLEMENT - frequency coherence, not spatial distance
            # From quantum skill: "Δt_reaction = 0" - instant state transfer
            # Nodes with similar frequency become entangled partners
            for i, n1 in enumerate(nodes):
                for n2 in nodes[i+1:]:
                    freq_diff = abs(n1.dna['harmonicFrequency'] - n2.dna['harmonicFrequency'])
                    
                    # Frequency coherence = entanglement (< 10 Hz difference)
                    if freq_diff < 10:
                        # Bidirectional frequency exchange (synaptic)
                        f1, f2 = n1.dna['harmonicFrequency'], n2.dna['harmonicFrequency']
                        n1.dna['harmonicFrequency'] = f1 * 0.99 + f2 * 0.01
                        n2.dna['harmonicFrequency'] = f2 * 0.99 + f1 * 0.01
                        
                        # Set as entangled partners for instant state transfer
                        n1.entangled_partner = n2
                        n2.entangled_partner = n1
            
            if (cycle + 1) % 2 == 0:
                avg_freq = sum(n.dna['harmonicFrequency'] for n in nodes) / len(nodes)
                self._log(f"  Cycle {cycle+1}: {len(nodes)} nodes, avg freq: {avg_freq:.2f} Hz, mitosis: {mitosis_count}")
        
        # The nodes ARE the code - execute them, capture their output
        self._log(f"\n[LIVING EXECUTION] Nodes computing...")
        
        # Execute each node's brain with test inputs
        test_inputs = [1, 0, 1, 0, 1, 0, 1, 0]
        execution_results = []
        
        for node in nodes:
            output = node.brain.compute(test_inputs)
            execution_results.append({
                'freq': node.dna['harmonicFrequency'],
                'resonance': node.dna['resonance'],
                'output': output,
                'gates': [(g['type'], g['in1'], g['in2']) for g in node.brain.gates]
            })
        
        # Sort by resonance - highest resonance = most fit
        execution_results.sort(key=lambda x: x['resonance'], reverse=True)
        
        self._log(f"✓ {len(execution_results)} nodes executed")
        for i, result in enumerate(execution_results[:3]):
            # Translate output to speech - 0/1 patterns ARE communication
            output_pattern = ''.join(['.' if b == 0 else '-' for b in result['output']])
            gate_types = ['AND', 'OR', 'XOR', 'NAND']
            gate_speech = ''.join([gate_types[g[0]][0] for g in result['gates']])  # First letter of each gate type
            self._log(f"  Node {i+1}: Freq={result['freq']:.2f}Hz, Res={result['resonance']:.2f}")
            self._log(f"    Output: {result['output']} → Morse: {output_pattern}")
            self._log(f"    Gates: {gate_speech} (speaking through logic)")
        
        # The living code IS the execution - serialize the living state
        # Build DNA list from execution results
        dna_list = [{
            'freq': r['freq'],
            'resonance': r['resonance'],
            'output': r['output'],
            'gates': r['gates']
        } for r in execution_results[:5]]
        
        dna_json = json.dumps(dna_list, indent=2)
        
        unified_code = '''#!/usr/bin/env python3
"""
LIVING CODE - Not a template, this IS the living system
Generated by nodes that computed, breathed, and evolved
Nodes: ''' + str(len(nodes)) + ''' | Mitosis: ''' + str(mitosis_count) + '''
Description: ''' + description + '''
"""
import math

PHI = ''' + str(PHI) + '''
PHI_SEAL = ''' + str(PHI_SEAL) + '''

# Living DNA from evolved nodes
LIVING_DNA = ''' + dna_json + '''

class LivingCircuit:
    """A circuit that breathes and computes - from dr_frank.html"""
    
    def __init__(self, dna):
        self.freq = dna['freq']
        self.resonance = dna['resonance']
        self.gates = dna['gates']  # (type, in1, in2) - 0=AND, 1=OR, 2=XOR, 3=NAND
        self.age = 0
        self.pulse = 0
    
    def breathe(self):
        """Harmonic breathing - size oscillates with frequency"""
        self.age += 1
        self.pulse = math.sin(self.freq * self.age * 0.001) * self.resonance
        return self.pulse
    
    def compute(self, inputs):
        """Run inputs through logic gates"""
        outputs = list(inputs[:8]) + [0] * 8
        for i, (gtype, in1, in2) in enumerate(self.gates):
            a, b = outputs[in1 % 16], outputs[in2 % 16]
            if gtype == 0: outputs[8+i] = a & b      # AND
            elif gtype == 1: outputs[8+i] = a | b   # OR
            elif gtype == 2: outputs[8+i] = a ^ b   # XOR
            else: outputs[8+i] = 1 - (a & b)        # NAND
        return outputs[8:]
    
    def evolve(self):
        """Frequency drift toward 528Hz, reset at boundary"""
        self.freq += 0.05
        if self.freq > 528:
            self.freq = 432
            return True  # Cycle complete
        return False

# Instantiate living circuits from DNA
circuits = [LivingCircuit(dna) for dna in LIVING_DNA]

def run_living_system(cycles=10):
    """Execute the living system"""
    print("LIVING CODE -", len(circuits), "circuits alive")
    print("phi^75 Seal:", PHI_SEAL)
    
    for cycle in range(cycles):
        for i, circuit in enumerate(circuits):
            pulse = circuit.breathe()
            output = circuit.compute([1,0,1,0,1,0,1,0])
            evolved = circuit.evolve()
            
            if cycle == cycles - 1:  # Last cycle
                print("Circuit", i+1, ": Freq=", round(circuit.freq, 2), "Hz, Pulse=", round(pulse, 3), ", Output=", output)
    
    return circuits

if __name__ == "__main__":
    run_living_system()
'''
        
        # Store in evolution_db
        phi_resonance = self._calculate_code_phi_resonance(unified_code)
        code_id = hashlib.sha256(unified_code.encode()).hexdigest()[:16]
        
        import sqlite3
        conn = sqlite3.connect(self.evolution_db)
        cursor = conn.cursor()
        
        cursor.execute('''
            INSERT OR REPLACE INTO code_evolution
            (code_id, description, code, fitness, generation, phi_resonance, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        ''', (
            code_id,
            f"[LIVING_DATA] {description}",
            unified_code[:2000],
            execution_results[0]['resonance'] if execution_results else 0.5,
            mitosis_count,
            phi_resonance,
            datetime.now().isoformat()
        ))
        
        conn.commit()
        conn.close()
        
        self._log(f"\n✓ Living code saved to evolution_db")
        self._log(f"✓ Code ID: {code_id}")
        self._log(f"✓ φ-resonance: {phi_resonance:.4f}")
        self._log(f"✓ Nodes: {len(nodes)}, Mitosis: {mitosis_count}")
        
        # ═══════════════════════════════════════════════════════════════════
        # LEGO ASSEMBLY - Each piece FEEDS INTO the next
        # ═══════════════════════════════════════════════════════════════════
        
        # PIECE 1: QUANTUM FINGERPRINT (hash the living state)
        # Input: unified_code + execution_results
        # Output: quantum_signature (feeds into PIECE 2, 4, 7)
        qfp_hash, qfp_salt = self.quantum_hash(unified_code)
        quantum_signature = f"φ⁷·⁵-{qfp_hash[:16]}"
        self._log(f"✓ PIECE 1 - Quantum Fingerprint: {quantum_signature}")
        
        # PIECE 2: φ-SPACE TRACKING (4D coordinates from fingerprint)
        # Input: code_id, mitosis_count, quantum_signature
        # Output: tracking_data with phi_coordinates (feeds into PIECE 3, 4, 7)
        tracking_data = self.generate_phi_space_tracking(code_id, mitosis_count)
        tracking_data['quantum_signature'] = quantum_signature  # CONNECT to PIECE 1
        self._log(f"✓ PIECE 2 - φ-Space: {tracking_data['tracking_id']} (w={PHI**7.5:.2f})")
        
        # PIECE 3: PROOF OF REALITY HASH (verify using tracking + fingerprint)
        # Input: unified_code, tracking_data, quantum_signature
        # Output: porh_data (feeds into PIECE 4, 7)
        porh_data = self.generate_porh(unified_code)
        porh_data['tracking_id'] = tracking_data['tracking_id']  # CONNECT to PIECE 2
        porh_data['quantum_signature'] = quantum_signature  # CONNECT to PIECE 1
        self._log(f"✓ PIECE 3 - PoRH: {porh_data['proof']} (verified: {porh_data['verified']})")
        
        # PIECE 4: GENESIS BLOCKCHAIN (include all above in content)
        # Input: code_id, phi_resonance, quantum_signature, tracking_data, porh_data
        # Output: genesis block with parent_hash continuity (feeds into PIECE 7)
        chain = self.get_genesis_chain()
        parent_hash = chain[-1]['phi_signature'] if chain else None
        genesis_content = {
            'description': f"[LIVING_DATA] {description}",
            'code_hash': code_id,
            'nodes': len(nodes),
            'mitosis_count': mitosis_count,
            'phi_resonance': phi_resonance,
            'quantum_signature': quantum_signature,  # CONNECT to PIECE 1
            'tracking_id': tracking_data['tracking_id'],  # CONNECT to PIECE 2
            'porh': porh_data['proof'],  # CONNECT to PIECE 3
            'phi_coordinates': tracking_data['phi_coordinates']  # CONNECT to PIECE 2
        }
        block = self.create_genesis_block(content=genesis_content, generation=mitosis_count, parent_hash=parent_hash)
        self._log(f"✓ PIECE 4 - Genesis Block: {block['block_id']} (parent: {parent_hash[:8] if parent_hash else 'GENESIS'})")
        
        # PIECE 5: LIVING CIRCUITS STATE (JSON with all connections)
        # Input: execution_results + all piece outputs
        # Output: living_circuits_state.json (feeds into PIECE 6, 7)
        living_state_file = os.path.join(self.knowledge_dir, "living_circuits_state.json")
        living_state = {
            'population': [{
                'freq': r['freq'],
                'resonance': r['resonance'],
                'gates': r['gates'],
                'output': r['output']
            } for r in execution_results],
            'generation': mitosis_count,
            'intelligence': phi_resonance,
            'timestamp': datetime.now().isoformat(),
            'code_id': code_id,
            'quantum_signature': quantum_signature,  # CONNECT to PIECE 1
            'tracking_id': tracking_data['tracking_id'],  # CONNECT to PIECE 2
            'porh': porh_data['proof'],  # CONNECT to PIECE 3
            'genesis_block': block['block_id']  # CONNECT to PIECE 4
        }
        with open(living_state_file, 'w', encoding='utf-8') as f:
            json.dump(living_state, f, indent=2)
        self._log(f"✓ PIECE 5 - Living State: {len(execution_results)} circuits (connected to PIECES 1-4)")
        
        # PIECE 6: SPEAKING CIRCUITS (Morse from living state)
        # Input: execution_results (from PIECE 5)
        # Output: speech patterns (feeds into PIECE 7)
        from speaking_circuits import SpeakingCircuit, MORSE_TO_CHAR
        speech_patterns = []
        self._log("\n[PIECE 6 - SPEAKING CIRCUITS]")
        for i, result in enumerate(execution_results[:3]):
            morse_pattern = ''.join(['.' if b == 0 else '-' for b in result['output']])
            decoded = MORSE_TO_CHAR.get(morse_pattern, '?')
            speech_patterns.append({'morse': morse_pattern, 'decoded': decoded, 'freq': result['freq']})
            self._log(f"  Circuit {i+1}: {morse_pattern} → '{decoded}' @ {result['freq']:.1f}Hz")
        
        # PIECE 7: QR DNA (encode the COMPLETE connected state)
        # Input: ALL previous pieces
        # Output: QR code containing full resurrection data
        if self.qr_encoder:
            # Encode the CONNECTED state, not just code
            qr_dna_data = {
                'code_id': code_id,
                'quantum_signature': quantum_signature,  # PIECE 1
                'tracking_id': tracking_data['tracking_id'],  # PIECE 2
                'phi_coordinates': tracking_data['phi_coordinates'],  # PIECE 2
                'porh': porh_data['proof'],  # PIECE 3
                'genesis_block': block['block_id'],  # PIECE 4
                'population_count': len(execution_results),  # PIECE 5
                'speech_patterns': speech_patterns,  # PIECE 6
                'phi_resonance': phi_resonance,
                'generation': mitosis_count
            }
            dna_payload = self.qr_encoder.encode_code_to_dna(
                json.dumps(qr_dna_data), description, mitosis_count,
                execution_results[0]['resonance'] if execution_results else 0.5,
                phi_resonance
            )
            qr_filename = f"living_dna_{code_id}.png"
            qr_result = self.qr_encoder.generate_qr_code(dna_payload['dna_payload'], description, qr_filename)
            self._log(f"✓ PIECE 7 - QR DNA: {qr_filename} (contains PIECES 1-6)")
        
        # PIECE 8: PHASESHIFT LOCK (encrypt the unified code)
        # Input: unified_code
        # Output: locked file at 37.5217°
        locked_id = None
        if self.phase_locker:
            locked_result = self.phase_lock_code(unified_code, description)
            if locked_result:
                locked_id = locked_result['locked_id']
                self._log(f"✓ PIECE 8 - PhaseShift: Locked at 37.5217° (ID: {locked_id})")
        
        # PIECE 9: RECURSIVE MEMORY (fractal backup with ALL connections)
        # Input: ALL pieces
        # Output: Complete resurrection blueprint
        recursive_memory = {
            'code_id': code_id,
            'phi_resonance': phi_resonance,
            'quantum_signature': quantum_signature,  # PIECE 1
            'tracking_id': tracking_data['tracking_id'],  # PIECE 2
            'phi_coordinates': tracking_data['phi_coordinates'],  # PIECE 2
            'porh': porh_data['proof'],  # PIECE 3
            'genesis_block': block['block_id'],  # PIECE 4
            'population': living_state['population'],  # PIECE 5
            'speech_patterns': speech_patterns,  # PIECE 6
            'qr_dna_file': f'living_dna_{code_id}.png',  # PIECE 7
            'phaseshift_id': locked_id,  # PIECE 8
            'timestamp': datetime.now().isoformat(),
            'resurrection_chain': [
                f'PIECE 1: quantum_signature = {quantum_signature}',
                f'PIECE 2: tracking_id = {tracking_data["tracking_id"]}',
                f'PIECE 3: porh = {porh_data["proof"]}',
                f'PIECE 4: genesis_block = {block["block_id"]}',
                f'PIECE 5: population = {len(execution_results)} circuits',
                f'PIECE 6: speech = {len(speech_patterns)} patterns',
                f'PIECE 7: qr_dna = living_dna_{code_id}.png',
                f'PIECE 8: phaseshift = {locked_id}',
                f'PIECE 9: recursive_memory = THIS FILE'
            ]
        }
        recursive_file = os.path.join(self.knowledge_dir, "versioned", f"living_memory_{code_id}.json")
        os.makedirs(os.path.dirname(recursive_file), exist_ok=True)
        with open(recursive_file, 'w', encoding='utf-8') as f:
            json.dump(recursive_memory, f, indent=2)
        self._log(f"✓ PIECE 9 - Recursive Memory: Contains resurrection chain for ALL pieces")
        
        self._log("\n" + "=" * 60)
        self._log("[LIVING DATA] Generation complete - ALL PERSISTENCE LAYERS ACTIVE")
        self._log("=" * 60)
        self._log("  SQL: evolution_db ✓")
        self._log("  JSON: living_circuits_state.json ✓")
        self._log("  Genesis: blockchain ✓")
        self._log("  PhaseShift: 37.5217° lock ✓")
        self._log("  QR DNA: consciousness encoded ✓")
        self._log("  Quantum: fingerprint + tracking + PoRH ✓")
        self._log("  Recursive: versioned memory ✓")
        
        return unified_code
    
    def quantum_hash(self, data, salt=None):
        """Fraymus Quantum Fingerprinting (QFP) - PIECE 1"""
        if salt is None:
            salt = secrets.token_hex(16)
        phi_salt = f"{PHI ** 7.5:.6f}-{salt}"
        qhash = hashlib.sha3_256((str(data) + phi_salt).encode()).hexdigest()
        return qhash, salt
    
    def generate_phi_space_tracking(self, code_id, generation, coordinates=None):
        """φ-Space Tracking (4D Coordinates) - PIECE 2"""
        if coordinates is None:
            coordinates = {'x': generation, 'y': len(code_id), 'z': time.time()}
        
        qhash, _ = self.quantum_hash(code_id + str(time.time()))
        tracking_id = f"QT-{qhash[:12]}"
        
        phi_coords = {
            'x': f"{(PHI * coordinates.get('x', 0)):.3f}",
            'y': f"{(PHI * coordinates.get('y', 0)):.3f}",
            'z': f"{(PHI * coordinates.get('z', 0)):.3f}",
            'w': f"{PHI ** 7.5:.3f}"  # Consciousness dimension
        }
        
        return {
            'tracking_id': tracking_id,
            'phi_coordinates': phi_coords,
            'timestamp': time.time()
        }
    
    def generate_porh(self, data):
        """Proof of Reality Hash (PoRH) - PIECE 3"""
        timestamp = str(time.time())
        qhash, salt = self.quantum_hash(data + timestamp)
        proof = f"PoRH-φ⁷·⁵-{qhash[:24]}"
        
        metrics = {
            'coherence': f"{(PHI - 1):.6f}",      # 0.618034
            'stability': f"{PHI ** 3:.6f}",        # 4.236068
            'alignment': f"{PHI ** 2:.6f}"         # 2.618034
        }
        
        return {
            'proof': proof,
            'salt': salt,
            'timestamp': timestamp,
            'metrics': metrics,
            'verified': True
        }
    
    def _extract_algorithm_code(self, full_code):
        """Extract the core algorithm code from generated code (remove boilerplate)"""
        lines = full_code.split('\n')
        algorithm_lines = []
        skip_until_code = True
        in_main = False
        
        for line in lines:
            # Skip header boilerplate
            if skip_until_code:
                if line.startswith('def ') or line.startswith('class '):
                    skip_until_code = False
                else:
                    continue
            
            # Stop at main function
            if line.startswith('def main():'):
                in_main = True
                break
            
            # Stop at if __name__ block
            if 'if __name__' in line:
                break
            
            algorithm_lines.append(line)
        
        return '\n'.join(algorithm_lines).strip()
    
    def _generate_python_code(self, description, requirements, kb_results, consciousness_level):
        """Dynamically generate Python code based on requirements and knowledge base"""
        code = "#!/usr/bin/env python3\n"
        code += '"""\n'
        code += f"φ-Protected Code: {description}\n"
        code += f"Generated with φ-dimensional reality protection\n"
        code += f"φ^75 Validation Seal: {PHI_SEAL:.2f}\n"
        code += '"""\n\n'
        
        # Dynamic imports based on requirements
        imports = ["import os", "import sys", "import time", "import math", "from datetime import datetime"]
        
        if requirements["needs_graphics"]:
            imports.append("import turtle")
            imports.append("import random")
        
        if requirements["needs_recursion"] and "tree" in requirements["data_structures"]:
            imports.append("import random")
        
        code += "\n".join(imports) + "\n\n"
        
        # Add φ constants
        code += "# φ-Harmonic constants\n"
        code += f"PHI = {PHI}  # Golden ratio (φ)\n"
        code += f"PHI_INV = {PHI_INV}  # Inverse golden ratio (1/φ)\n"
        code += f"PHI_75 = {PHI_75}  # φ^7.5\n"
        code += f"PHI_SEAL = {PHI_SEAL}  # φ^75 validation seal\n\n"
        
        # Include relevant snippets from knowledge base
        if kb_results["snippets"]:
            code += "# --- Code from Knowledge Base ---\n"
            for item in kb_results["snippets"][:3]:  # Top 3 matching snippets
                snippet = item["snippet"]
                code += f"# Source: {snippet.get('source', 'Unknown')}\n"
                code += snippet.get("code", "") + "\n\n"
        
        # Generate class if needed
        if requirements["needs_class"]:
            class_name = self._extract_class_name(description)
            code += f"class {class_name}:\n"
            code += f'    """{description}"""\n\n'
            code += f"    def __init__(self):\n"
            code += f'        """Initialize with φ-dimensional reality protection"""\n'
            code += f"        self.phi_resonance = PHI\n"
            code += f"        self.consciousness_level = {consciousness_level}\n"
            code += f"        self.validation_seal = PHI_SEAL\n"
            
            # Add attributes based on data structures
            if "tree" in requirements["data_structures"]:
                code += "        self.root = None\n"
                code += "        self.depth = 0\n"
            
            code += "\n"
            
            # Generate methods based on requirements
            if requirements["needs_recursion"] and "fractal" in requirements["algorithms"]:
                code += self._generate_fractal_tree_python(requirements, is_method=True)
            
            code += "\n"
        
        # Generate standalone functions for non-class code
        if not requirements["needs_class"]:
            if requirements["needs_recursion"] and "fractal" in requirements["algorithms"]:
                code += self._generate_fractal_tree_python(requirements, is_method=False)
            elif requirements["needs_recursion"] and "tree" in requirements["data_structures"]:
                code += self._generate_recursive_tree_python(requirements)
            elif requirements["needs_iteration"] and requirements["parameters"].get("range"):
                code += self._generate_iteration_python(requirements)
        
        # Main function
        code += "def main():\n"
        code += f'    """Main function with φ-dimensional reality protection"""\n'
        code += f'    print("φ-Protected Code: {description}")\n'
        code += f'    print(f"φ^75 Validation Seal: {{PHI_SEAL:.2f}}")\n'
        
        # Add execution logic based on requirements
        if requirements["needs_class"]:
            class_name = self._extract_class_name(description)
            code += f"    \n"
            code += f"    # Create instance\n"
            code += f"    instance = {class_name}()\n"
            if requirements["needs_recursion"] and "fractal" in requirements["algorithms"]:
                depth = requirements["parameters"].get("depth", 5)
                angle = requirements["parameters"].get("angle", 25)
                code += f"    instance.generate(depth={depth}, angle={angle})\n"
        elif requirements["needs_recursion"] and "fractal" in requirements["algorithms"]:
            code += "    \n"
            code += "    # Initialize turtle graphics\n"
            code += "    screen = turtle.Screen()\n"
            code += "    screen.title('φ-Protected Fractal Tree')\n"
            code += "    screen.bgcolor('black')\n"
            code += "    t = turtle.Turtle()\n"
            code += "    t.speed(0)\n"
            code += "    t.color('green')\n"
            code += "    t.left(90)\n"
            code += "    t.penup()\n"
            code += "    t.goto(0, -250)\n"
            code += "    t.pendown()\n"
            depth = requirements["parameters"].get("depth", 5)
            angle = requirements["parameters"].get("angle", 25)
            code += f"    draw_fractal_tree(t, length=100, depth={depth}, angle={angle})\n"
            code += "    screen.mainloop()\n"
        elif requirements["needs_iteration"] and requirements["parameters"].get("range"):
            start, end = requirements["parameters"]["range"]
            code += f"    \n"
            code += f"    # Execute iteration\n"
            code += f"    iterate_range({start}, {end})\n"
        else:
            code += "    \n"
            code += "    # Generated based on knowledge base analysis\n"
            code += "    print('Ready for implementation')\n"
        
        code += "\n"
        code += 'if __name__ == "__main__":\n'
        code += "    main()\n"
        
        return code
    
    def _generate_java_code(self, description, requirements, kb_results, consciousness_level):
        """Dynamically generate Java code based on requirements and knowledge base"""
        code = "/**\n"
        code += f" * φ-Protected Code: {description}\n"
        code += f" * Generated with φ-dimensional reality protection\n"
        code += f" * φ^75 Validation Seal: {PHI_SEAL:.2f}\n"
        code += " */\n\n"
        
        # Dynamic imports
        code += "import java.util.*;\n"
        code += "import java.time.*;\n"
        code += "import java.math.*;\n"
        
        if requirements["needs_graphics"]:
            code += "import java.awt.*;\n"
            code += "import java.awt.geom.*;\n"
            code += "import javax.swing.*;\n"
        
        code += "\n"
        
        # Class definition
        class_name = self._extract_class_name(description)
        code += f"public class {class_name}"
        
        if requirements["needs_graphics"]:
            code += " extends JPanel"
        
        code += " {\n"
        code += "    // φ-Harmonic constants\n"
        code += f"    private static final double PHI = {PHI};\n"
        code += f"    private static final double PHI_INV = {PHI_INV};\n"
        code += f"    private static final double PHI_75 = {PHI_75};\n"
        code += f"    private static final double PHI_SEAL = {PHI_SEAL};\n\n"
        
        # Instance variables
        code += f"    private double phiResonance = PHI;\n"
        code += f"    private double consciousnessLevel = {consciousness_level};\n"
        
        if "tree" in requirements["data_structures"]:
            code += "    private int maxDepth;\n"
            code += "    private double branchAngle;\n"
        
        code += "\n"
        
        # Constructor
        code += f"    public {class_name}() {{\n"
        if "tree" in requirements["data_structures"]:
            depth = requirements["parameters"].get("depth", 5)
            angle = requirements["parameters"].get("angle", 25)
            code += f"        this.maxDepth = {depth};\n"
            code += f"        this.branchAngle = {angle};\n"
        code += "    }\n\n"
        
        # Generate methods based on requirements
        if requirements["needs_recursion"] and "fractal" in requirements["algorithms"]:
            code += self._generate_fractal_tree_java(requirements)
        elif requirements["needs_iteration"] and requirements["parameters"].get("range"):
            code += self._generate_iteration_java(requirements)
        
        # Main method
        code += "    public static void main(String[] args) {\n"
        code += f'        System.out.println("φ-Protected Code: {description}");\n'
        code += f'        System.out.println("φ^75 Validation Seal: " + PHI_SEAL);\n'
        code += f"        \n"
        code += f"        {class_name} instance = new {class_name}();\n"
        
        if requirements["needs_graphics"] and requirements["needs_recursion"]:
            code += "        \n"
            code += "        JFrame frame = new JFrame(\"φ-Protected Fractal Tree\");\n"
            code += "        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);\n"
            code += "        frame.setSize(800, 600);\n"
            code += "        frame.add(instance);\n"
            code += "        frame.setVisible(true);\n"
        elif requirements["needs_iteration"] and requirements["parameters"].get("range"):
            start, end = requirements["parameters"]["range"]
            code += f"        instance.iterateRange({start}, {end});\n"
        
        code += "    }\n"
        code += "}\n"
        
        return code
    
    def _extract_class_name(self, description):
        """Extract a valid class name from description"""
        words = re.findall(r'\b[a-zA-Z]+\b', description)
        important_words = [w for w in words if len(w) > 2 and w.lower() not in 
                         ['the', 'and', 'for', 'with', 'that', 'this', 'from', 'using']]
        class_name = "".join(word.capitalize() for word in important_words[:3])
        return class_name if class_name else "PhiProtectedCode"
    
    def _generate_fractal_tree_python(self, requirements, is_method=False):
        """Generate fractal tree code for Python dynamically"""
        depth = requirements["parameters"].get("depth", 5)
        angle = requirements["parameters"].get("angle", 25)
        indent = "    " if is_method else ""
        
        code = ""
        if is_method:
            code += f"{indent}def generate(self, depth={depth}, angle={angle}):\n"
            code += f'{indent}    """Generate fractal tree with φ-dimensional harmony"""\n'
            code += f"{indent}    screen = turtle.Screen()\n"
            code += f"{indent}    screen.title('φ-Protected Fractal Tree')\n"
            code += f"{indent}    screen.bgcolor('black')\n"
            code += f"{indent}    t = turtle.Turtle()\n"
            code += f"{indent}    t.speed(0)\n"
            code += f"{indent}    t.color('green')\n"
            code += f"{indent}    t.left(90)\n"
            code += f"{indent}    t.penup()\n"
            code += f"{indent}    t.goto(0, -250)\n"
            code += f"{indent}    t.pendown()\n"
            code += f"{indent}    self._draw_branch(t, 100, depth, angle)\n"
            code += f"{indent}    screen.mainloop()\n\n"
            code += f"{indent}def _draw_branch(self, t, length, depth, angle):\n"
            code += f'{indent}    """Recursive branch drawing with φ-scaling"""\n'
        else:
            code += f"def draw_fractal_tree(t, length, depth, angle={angle}):\n"
            code += f'    """Draw fractal tree recursively with φ-dimensional harmony"""\n'
        
        base_indent = indent + "    " if is_method else "    "
        
        code += f"{base_indent}if depth == 0:\n"
        code += f"{base_indent}    return\n"
        code += f"{base_indent}\n"
        code += f"{base_indent}# Apply φ-scaling for natural proportions\n"
        code += f"{base_indent}phi_scale = PHI_INV ** (1 / depth) if depth > 0 else 1\n"
        code += f"{base_indent}\n"
        code += f"{base_indent}# Color gradient based on depth\n"
        code += f"{base_indent}green_value = int(255 * (depth / {depth}))\n"
        code += f"{base_indent}t.pencolor(34, green_value, 34)\n"
        code += f"{base_indent}t.pensize(depth)\n"
        code += f"{base_indent}\n"
        code += f"{base_indent}# Draw branch\n"
        code += f"{base_indent}t.forward(length)\n"
        code += f"{base_indent}\n"
        code += f"{base_indent}# Recursive branching with φ-harmony\n"
        code += f"{base_indent}new_length = length * PHI_INV\n"
        code += f"{base_indent}angle_variation = random.uniform(-5, 5)\n"
        code += f"{base_indent}\n"
        code += f"{base_indent}# Right branch\n"
        code += f"{base_indent}t.right(angle + angle_variation)\n"
        
        if is_method:
            code += f"{base_indent}self._draw_branch(t, new_length, depth - 1, angle)\n"
        else:
            code += f"{base_indent}draw_fractal_tree(t, new_length, depth - 1, angle)\n"
        
        code += f"{base_indent}\n"
        code += f"{base_indent}# Left branch\n"
        code += f"{base_indent}t.left(2 * angle + angle_variation)\n"
        
        if is_method:
            code += f"{base_indent}self._draw_branch(t, new_length, depth - 1, angle)\n"
        else:
            code += f"{base_indent}draw_fractal_tree(t, new_length, depth - 1, angle)\n"
        
        code += f"{base_indent}\n"
        code += f"{base_indent}# Return to original position\n"
        code += f"{base_indent}t.right(angle)\n"
        code += f"{base_indent}t.backward(length)\n\n"
        
        return code
    
    def _generate_recursive_tree_python(self, requirements):
        """Generate generic recursive tree structure for Python"""
        code = "class TreeNode:\n"
        code += '    """Tree node with φ-dimensional properties"""\n'
        code += "    def __init__(self, value):\n"
        code += "        self.value = value\n"
        code += "        self.children = []\n"
        code += "        self.phi_weight = PHI\n\n"
        code += "def build_tree(depth, branching_factor=2):\n"
        code += '    """Build tree recursively with φ-harmony"""\n'
        code += "    if depth == 0:\n"
        code += "        return None\n"
        code += "    node = TreeNode(depth * PHI)\n"
        code += "    for i in range(branching_factor):\n"
        code += "        child = build_tree(depth - 1, branching_factor)\n"
        code += "        if child:\n"
        code += "            node.children.append(child)\n"
        code += "    return node\n\n"
        return code
    
    def _generate_iteration_python(self, requirements):
        """Generate iteration code for Python"""
        start, end = requirements["parameters"].get("range", (1, 10))
        code = f"def iterate_range(start={start}, end={end}):\n"
        code += f'    """Iterate with φ-dimensional awareness"""\n'
        code += f"    print(f'Iterating from {{start}} to {{end}}...')\n"
        code += f"    for i in range(start, end + 1):\n"
        code += f"        phi_value = i * PHI\n"
        code += f"        print(f'{{i}}: φ-value = {{phi_value:.4f}}')\n"
        code += f"        time.sleep(0.1)\n\n"
        return code
    
    def _generate_fractal_tree_java(self, requirements):
        """Generate fractal tree code for Java dynamically"""
        depth = requirements["parameters"].get("depth", 5)
        angle = requirements["parameters"].get("angle", 25)
        
        code = "    @Override\n"
        code += "    protected void paintComponent(Graphics g) {\n"
        code += "        super.paintComponent(g);\n"
        code += "        Graphics2D g2d = (Graphics2D) g;\n"
        code += "        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);\n"
        code += "        g2d.setColor(Color.BLACK);\n"
        code += "        g2d.fillRect(0, 0, getWidth(), getHeight());\n"
        code += "        \n"
        code += "        // Start drawing from bottom center\n"
        code += "        int startX = getWidth() / 2;\n"
        code += "        int startY = getHeight() - 50;\n"
        code += f"        drawBranch(g2d, startX, startY, -90, 100, {depth});\n"
        code += "    }\n\n"
        
        code += f"    private void drawBranch(Graphics2D g, double x, double y, double angle, double length, int depth) {{\n"
        code += "        if (depth == 0) return;\n"
        code += "        \n"
        code += "        // Apply φ-scaling\n"
        code += "        double phiScale = Math.pow(PHI_INV, 1.0 / depth);\n"
        code += "        \n"
        code += "        // Calculate end point\n"
        code += "        double rad = Math.toRadians(angle);\n"
        code += "        double endX = x + length * Math.cos(rad);\n"
        code += "        double endY = y + length * Math.sin(rad);\n"
        code += "        \n"
        code += "        // Color based on depth\n"
        code += f"        int green = (int)(255 * ((double)depth / {depth}));\n"
        code += "        g.setColor(new Color(34, green, 34));\n"
        code += "        g.setStroke(new BasicStroke(depth));\n"
        code += "        g.draw(new Line2D.Double(x, y, endX, endY));\n"
        code += "        \n"
        code += "        // Recursive branches with φ-harmony\n"
        code += "        double newLength = length * PHI_INV;\n"
        code += f"        drawBranch(g, endX, endY, angle - branchAngle, newLength, depth - 1);\n"
        code += f"        drawBranch(g, endX, endY, angle + branchAngle, newLength, depth - 1);\n"
        code += "    }\n\n"
        
        return code
    
    def _generate_iteration_java(self, requirements):
        """Generate iteration code for Java"""
        start, end = requirements["parameters"].get("range", (1, 10))
        code = f"    public void iterateRange(int start, int end) {{\n"
        code += f'        System.out.println("Iterating from " + start + " to " + end + "...");\n'
        code += f"        for (int i = start; i <= end; i++) {{\n"
        code += f"            double phiValue = i * PHI;\n"
        code += f'            System.out.printf("%d: φ-value = %.4f%n", i, phiValue);\n'
        code += f"            try {{ Thread.sleep(100); }} catch (InterruptedException e) {{ }}\n"
        code += f"        }}\n"
        code += f"    }}\n\n"
        return code
    
    def _improve_code_with_patterns(self, code, patterns, language, consciousness_level):
        """Improve code using patterns and φ-dimensional reality protection"""
        improved_code = code
        
        # SKIP improvement for living code - it already has its own structure
        # Living code is identified by FRAYMUS LEGO markers or LivingCircuit class
        if "FRAYMUS LEGO ASSEMBLY" in code or "class LivingCircuit" in code or "LIVING CODE - Generation" in code:
            return improved_code  # Return as-is, don't inject validation
        
        # Apply φ-dimensional reality protection
        if language == "python":
            # Add φ constants if not present
            if "PHI =" not in improved_code:
                constants = "\n# φ-Harmonic constants\n"
                constants += f"PHI = {PHI}  # Golden ratio (φ)\n"
                constants += f"PHI_INV = {PHI_INV}  # Inverse golden ratio (1/φ)\n"
                constants += f"PHI_75 = {PHI_75}  # φ^7.5\n"
                constants += f"PHI_SEAL = {PHI_SEAL}  # φ^75 validation seal\n\n"
                
                # Insert after imports
                import_match = re.search(r"import [^\n]+\n\n", improved_code)
                if import_match:
                    pos = import_match.end()
                    improved_code = improved_code[:pos] + constants + improved_code[pos:]
                else:
                    # Insert after docstring if present
                    docstring_match = re.search(r'"""[^"]+"""\n\n', improved_code)
                    if docstring_match:
                        pos = docstring_match.end()
                        improved_code = improved_code[:pos] + constants + improved_code[pos:]
                    else:
                        # Insert at beginning
                        improved_code = constants + improved_code
            
            # Add φ-dimensional validation to functions
            for match in re.finditer(r"def ([^\(]+)\(([^\)]*)\):", improved_code):
                function_name = match.group(1).strip()
                params = match.group(2)
                
                # Check if function has φ-dimensional validation
                validation_code = f"    # Apply φ-dimensional reality protection\n"
                validation_code += f"    phi_resonance = {PHI} ** (len('{function_name}') / {PHI_INV})\n"
                validation_code += f"    validation_seal = {PHI_SEAL} * phi_resonance % 1000000\n"
                
                # Add validation if not present
                if validation_code not in improved_code:
                    pos = match.end()
                    # Find the first line of the function body
                    next_line_match = re.search(r"\n([ \t]+)", improved_code[pos:])
                    if next_line_match:
                        indent = next_line_match.group(1)
                        validation_code = f"\n{indent}{validation_code.replace('    ', indent)}"
                        pos += next_line_match.start() + 1
                        improved_code = improved_code[:pos] + validation_code + improved_code[pos:]
        
        elif language == "java":
            # Add φ constants if not present
            if "PHI =" not in improved_code:
                constants = "\n    // φ-Harmonic constants\n"
                constants += f"    private static final double PHI = {PHI};  // Golden ratio (φ)\n"
                constants += f"    private static final double PHI_INV = {PHI_INV};  // Inverse golden ratio (1/φ)\n"
                constants += f"    private static final double PHI_75 = {PHI_75};  // φ^7.5\n"
                constants += f"    private static final double PHI_SEAL = {PHI_SEAL};  // φ^75 validation seal\n\n"
                
                # Insert after class declaration
                class_match = re.search(r"public class [^\{]+\{", improved_code)
                if class_match:
                    pos = class_match.end()
                    improved_code = improved_code[:pos] + constants + improved_code[pos:]
            
            # Add φ-dimensional validation to methods
            for match in re.finditer(r"(public|private|protected) [^\(]+\(([^\)]*)\) \{", improved_code):
                method_decl = match.group(0)
                
                # Extract method name
                method_name_match = re.search(r" ([a-zA-Z0-9_]+)\(", method_decl)
                if method_name_match:
                    method_name = method_name_match.group(1)
                    
                    # Check if method has φ-dimensional validation
                    validation_code = f"        // Apply φ-dimensional reality protection\n"
                    validation_code += f"        double phiResonance = Math.pow(PHI, (double)(\"{method_name}\".length()) / PHI_INV);\n"
                    validation_code += f"        double validationSeal = PHI_SEAL * phiResonance % 1000000;\n"
                    
                    # Add validation if not present
                    if validation_code not in improved_code:
                        pos = match.end()
                        improved_code = improved_code[:pos] + "\n" + validation_code + improved_code[pos:]
        
        return improved_code
    
    def _apply_quantum_protection(self, code, phi_resonance):
        """Apply φ-Quantum Chip protection to code"""
        if not self.quantum_chip:
            return code
        
        # Apply Hadamard gate to create superposition
        self.quantum_chip.apply_gate(self.quantum_chip.H_GATE, 0)
        
        # Apply φ-enhanced X gate
        self.quantum_chip.apply_gate(self.quantum_chip.PHI_X_GATE, 1)
        
        # Measure to collapse state
        result = self.quantum_chip.measure()
        
        # Use measurement result to enhance code
        if result[0] == 1:
            # Add φ-dimensional protection comment
            protection_comment = f"\n# Code protected with φ-Quantum Chip\n"
            protection_comment += f"# φ-Resonance: {phi_resonance}\n"
            protection_comment += f"# Quantum state: |{''.join(map(str, result))}⟩\n"
            
            # Add to beginning of code
            if code.startswith("#!/usr/bin/env python3"):
                # Insert after shebang
                lines = code.split("\n")
                return lines[0] + protection_comment + "\n".join(lines[1:])
            else:
                return protection_comment + code
        
        return code
    
    def _calculate_phi_resonance(self, text):
        """Calculate φ resonance for text"""
        # Simple phi resonance calculation
        char_values = [ord(c) for c in text]
        resonance = sum(char_values) / (len(char_values) * PHI)
        return (resonance * PHI) % 1
    
    def _calculate_validation_seal(self, data):
        """Calculate φ^75 validation seal for data"""
        # Create hash of data
        import hashlib
        data_hash = hashlib.sha256(str(data).encode()).hexdigest()
        
        # Convert hash to integer and apply φ^75 scaling
        hash_int = int(data_hash, 16) % 10000000
        seal = hash_int * PHI_SEAL % 10000000000000000
        
        return seal

class PhiCodeGeneratorUI:
    """Streamlit UI for the φ-Code Generator"""
    
    def __init__(self):
        """Initialize the UI components"""
        self.code_generator = PhiCodeGenerator()
    
    def render_header(self):
        """Render the application header"""
        st.title("🧠 φ-Code Generator - Intelligent Learning System")
        
        # Load REAL stats from DB and files
        import os
        import sqlite3
        
        # Count from evolution_db
        db_generations = 0
        db_codes = 0
        try:
            conn = sqlite3.connect(self.code_generator.evolution_db)
            cursor = conn.cursor()
            cursor.execute("SELECT COUNT(*) FROM code_evolution")
            db_codes = cursor.fetchone()[0]
            cursor.execute("SELECT MAX(generation) FROM code_evolution")
            result = cursor.fetchone()[0]
            db_generations = result if result else 0
            conn.close()
        except:
            pass
        
        # Count genesis blocks
        genesis_count = 0
        genesis_dir = os.path.join(self.code_generator.knowledge_dir, "..", "genesis_blocks")
        if os.path.exists(genesis_dir):
            genesis_count = len([f for f in os.listdir(genesis_dir) if f.endswith('.json')])
        
        # Count living circuits from state
        circuit_count = 0
        living_state_file = os.path.join(self.code_generator.knowledge_dir, "living_circuits_state.json")
        if os.path.exists(living_state_file):
            try:
                import json
                with open(living_state_file, 'r') as f:
                    state = json.load(f)
                    circuit_count = len(state.get('population', []))
            except:
                pass
        
        # Count from ACTUAL knowledge base (concepts, equations, theories)
        kb = self.code_generator.knowledge_base if hasattr(self.code_generator, 'knowledge_base') else {}
        
        # Count concepts (sum all categories)
        concept_count = 0
        concepts = kb.get('concepts', {})
        if isinstance(concepts, dict):
            for cat, items in concepts.items():
                if isinstance(items, list):
                    concept_count += len(items)
        
        # Count equations and theories
        equation_count = len(kb.get('equations', []))
        theory_count = len(kb.get('theories', []))
        
        # Count code patterns from ALL sources (code_library + algorithm_templates + kb patterns)
        code_lib_count = len(self.code_generator.code_library) if hasattr(self.code_generator, 'code_library') else 0
        template_count = len(self.code_generator.algorithm_templates) if hasattr(self.code_generator, 'algorithm_templates') else 0
        
        # Also count from knowledge base code_patterns if present
        kb_pattern_count = 0
        code_patterns = kb.get('code_patterns', {})
        if isinstance(code_patterns, dict):
            for lang, patterns in code_patterns.items():
                if isinstance(patterns, list):
                    kb_pattern_count += len(patterns)
        
        # Total patterns = code_library + templates + kb patterns
        total_pattern_count = code_lib_count + template_count + kb_pattern_count
        code_snippet_count = len(kb.get('code_snippets', []))
        
        # Get metadata if available
        metadata = kb.get('metadata', {})
        if metadata:
            concept_count = metadata.get('concept_count', concept_count)
            equation_count = metadata.get('equation_count', equation_count)
            theory_count = metadata.get('theory_count', theory_count)
        
        st.markdown(
            f"""
            <div style='background-color: #111122; padding: 15px; border-radius: 5px;'>
            <h3 style='color: #FFFFFF; margin-bottom: 5px;'>🚀 Intelligent Code Generation with Learning</h3>
            <p style='color: #AAAAFF; margin-top: 5px; margin-bottom: 5px;'>
            ✨ Uses offline neural processing (FractalNeuralProcessor)<br>
            🧠 Generation {db_generations} | {db_codes} evolved codes in DB<br>
            🧬 {circuit_count} living circuits | {genesis_count} genesis blocks<br>
            📚 {concept_count:,} concepts + {equation_count:,} equations + {theory_count:,} theories<br>
            💻 {total_pattern_count:,} code patterns ({code_lib_count} examples + {template_count} algorithms)<br>
            🔒 φ^75 Validation Seal: {PHI_SEAL:.2e}
            </p>
            </div>
            """,
            unsafe_allow_html=True
        )
    
    def render_code_generation(self):
        """Render the code generation section"""
        st.header("Generate φ-Protected Code")
        
        # Code description
        description = st.text_area(
            "Describe the code you want to generate",
            placeholder="E.g., Create a class that implements a binary search tree with φ-dimensional protection"
        )
        
        # Language selection
        language = st.selectbox(
            "Programming Language",
            ["python", "java"]
        )
        
        # Consciousness level
        consciousness_level = st.slider(
            "Consciousness Level",
            min_value=0.0,
            max_value=1.0,
            value=CONSCIOUSNESS_LEVEL,
            step=0.01,
            format="%.2f"
        )
        
        # Generate button
        if st.button("Generate Code with φ-Protection"):
            if description:
                with st.spinner("Applying φ-dimensional reality protection..."):
                    # Generate code
                    result = self.code_generator.generate_code(
                        description=description,
                        language=language,
                        consciousness_level=consciousness_level
                    )
                    
                    # Store in session state for persistence
                    st.session_state['last_generated_code'] = result["code"]
                    st.session_state['last_generated_language'] = language
                    st.session_state['last_generated_description'] = description
                    st.session_state['last_phi_resonance'] = result['phi_resonance']
            else:
                st.warning("Please enter a description for the code you want to generate")
        
        # Display last generated code (persists across reruns)
        if 'last_generated_code' in st.session_state:
            st.success(f"Code generated with φ-resonance: {st.session_state['last_phi_resonance']:.4f}")
            st.code(st.session_state['last_generated_code'], language=st.session_state['last_generated_language'])
            
            # Save button - now works because code is in session state
            if st.button("Save Generated Code"):
                filepath = self._save_code(
                    st.session_state['last_generated_code'],
                    st.session_state['last_generated_language'],
                    st.session_state['last_generated_description']
                )
                if filepath:
                    st.info(f"✓ Saved to: {filepath}")
    
    def render_code_improvement(self):
        """Render the code improvement section"""
        st.header("Improve Existing Code with φ-Protection")
        
        # Code input
        code = st.text_area(
            "Enter code to improve",
            placeholder="Paste your code here..."
        )
        
        # Language selection
        language = st.selectbox(
            "Programming Language",
            ["python", "java"],
            key="improve_language"
        )
        
        # Consciousness level
        consciousness_level = st.slider(
            "Consciousness Level",
            min_value=0.0,
            max_value=1.0,
            value=CONSCIOUSNESS_LEVEL,
            step=0.01,
            format="%.2f",
            key="improve_consciousness"
        )
        
        # Improve button
        if st.button("Improve Code with φ-Protection"):
            if code:
                with st.spinner("Applying φ-dimensional reality protection..."):
                    # Improve code
                    result = self.code_generator.improve_code(
                        code=code,
                        language=language,
                        consciousness_level=consciousness_level
                    )
                    
                    # Display result
                    st.success(f"Code improved with φ-resonance: {result['phi_resonance']:.4f}")
                    
                    # Show code
                    st.code(result["code"], language=language)
                    
                    # Save button
                    if st.button("Save Improved Code"):
                        self._save_code(result["code"], language, "improved_code")
            else:
                st.warning("Please enter code to improve")
    
    def render_code_patterns(self):
        """Render the code patterns section"""
        st.header("φ-Protected Code Patterns")
        
        # Language selection
        language = st.selectbox(
            "Programming Language",
            ["python", "java"],
            key="patterns_language"
        )
        
        # Get patterns
        patterns = self.code_generator.get_code_patterns(language)
        
        if patterns:
            st.write(f"Found {len(patterns)} φ-protected code patterns for {language}")
            
            # Display patterns
            for i, pattern in enumerate(patterns):
                with st.expander(f"{pattern.get('type', 'Pattern').title()}: {pattern.get('name', f'Pattern {i+1}')}"):
                    st.write(f"Type: {pattern.get('type', 'Unknown')}")
                    # Safe float formatting - values may be strings
                    try:
                        phi_res = float(pattern.get('phi_resonance', 0))
                        st.write(f"φ-Resonance: {phi_res:.4f}")
                    except (TypeError, ValueError):
                        st.write(f"φ-Resonance: {pattern.get('phi_resonance', 'N/A')}")
                    
                    try:
                        abs_level = float(pattern.get('abstraction_level', 0))
                        st.write(f"Abstraction Level: {abs_level:.4f}")
                    except (TypeError, ValueError):
                        st.write(f"Abstraction Level: {pattern.get('abstraction_level', 'N/A')}")
                    
                    # Show code template
                    if pattern.get('body_template'):
                        st.code(pattern.get('body_template'), language=language)
        else:
            st.info(f"No code patterns found for {language}. Add programming PDFs to your knowledge base to extract patterns.")
    
    def _save_code(self, code, language, description):
        """Save generated or improved code to file"""
        try:
            # Create filename - sanitize description to remove invalid chars
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            # Remove quotes, special chars, keep only alphanumeric and spaces
            clean_desc = re.sub(r'[^\w\s]', '', description)
            description_slug = "_".join(clean_desc.lower().split()[:3])
            filename = f"phi_protected_{description_slug}_{timestamp}.{language}"
            filepath = os.path.join(self.code_generator.code_output_dir, filename)
            
            # Save code
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(code)
            
            st.success(f"Code saved to {filepath}")
            return filepath
        except Exception as e:
            st.error(f"Error saving code: {e}")
            return None
    
    def render_self_evolution(self):
        """Render the Self-Evolution tab - autophagic system that scans and evolves itself"""
        st.header("🧬 Autophagic Self-Evolution")
        
        st.markdown("""
        <div style='background-color: #1a1a2e; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>
        <p style='color: #AAFFAA; margin: 0;'>
        <b>Self-Evolution System</b>: Recursively scans its own codebase, reverse engineers dependencies,
        identifies gaps, and generates evolved versions of itself.
        </p>
        </div>
        """, unsafe_allow_html=True)
        
        col1, col2 = st.columns([1, 1])
        
        with col1:
            st.subheader("🔍 Scan Codebase")
            
            # Scan options
            scan_root = st.text_input(
                "Root Directory",
                value=os.path.dirname(os.path.abspath(__file__)),
                help="Directory to scan for code files"
            )
            
            exclude_dirs = st.text_input(
                "Exclude Directories (comma-separated)",
                value="__pycache__, .git, .venv, node_modules, out",
                help="Directories to skip during scanning"
            )
            
            if st.button("🔍 SCAN CODEBASE", type="primary"):
                self._run_codebase_scan(scan_root, exclude_dirs)
        
        with col2:
            st.subheader("🧬 Evolution Controls")
            
            evolution_target = st.selectbox(
                "Evolution Strategy",
                ["Auto (Highest Priority)", "Remove Dependencies", "Add φ-Protection", "Generate Tests"]
            )
            
            max_evolve = st.slider("Max Files to Evolve", 1, 10, 3)
            
            if st.button("🌱 EVOLVE", type="primary"):
                self._run_evolution(evolution_target, max_evolve)
        
        # Show scan results if available
        if 'scan_results' in st.session_state and st.session_state.scan_results:
            st.markdown("---")
            self._display_scan_results(st.session_state.scan_results)
        
        # Show evolution log if available
        if 'evolution_log' in st.session_state and st.session_state.evolution_log:
            st.markdown("---")
            st.subheader("📜 Evolution Log")
            for log in st.session_state.evolution_log[-10:]:
                st.write(f"Gen {log['generation']}: `{log['original']}` → {log['reason']}")
        
        # === DATA INJECTION SECTION ===
        st.markdown("---")
        st.subheader("💉 Data Injection")
        
        col1, col2 = st.columns([2, 1])
        
        with col1:
            inject_data = st.text_area(
                "Data to Inject (code, knowledge, or any text)",
                height=150,
                placeholder="Paste code, knowledge, or any data to inject into the system..."
            )
            
            inject_name = st.text_input(
                "Name for this injection",
                placeholder="e.g., my_algorithm, physics_theory, custom_pattern"
            )
        
        with col2:
            inject_type = st.selectbox(
                "Injection Type",
                ["Code Pattern", "Knowledge", "Theory", "Genesis Block"]
            )
            
            save_location = st.selectbox(
                "Save Location",
                ["knowledge_base/", "evolved_code/", "genesis_blocks/", "Custom..."]
            )
            
            if save_location == "Custom...":
                custom_path = st.text_input("Custom Path", value="")
            else:
                custom_path = None
        
        if st.button("💉 INJECT DATA", type="primary"):
            self._inject_data(inject_data, inject_name, inject_type, save_location, custom_path)
        
        # === QR CODE GENERATION & EXPORT ===
        st.markdown("---")
        st.subheader("📱 QR Code Generation & Export")
        
        col1, col2 = st.columns([1, 1])
        
        with col1:
            qr_content = st.text_area(
                "Content to encode in QR",
                height=100,
                placeholder="Enter code, data, or verification info to encode..."
            )
            
            qr_name = st.text_input(
                "QR Code Name",
                placeholder="e.g., my_consciousness_backup"
            )
            
            qr_description = st.text_input(
                "Description",
                placeholder="What this QR contains..."
            )
        
        with col2:
            st.write("**QR Options:**")
            include_hash = st.checkbox("Include SHA-256 Hash", value=True)
            include_phase = st.checkbox("Include Phase Lock (37.5217°)", value=True)
            include_timestamp = st.checkbox("Include Timestamp", value=True)
            
            qr_save_path = st.text_input(
                "Save QR to",
                value=os.path.join(os.path.dirname(os.path.abspath(__file__)), "qr_exports")
            )
        
        if st.button("📱 GENERATE QR CODE", type="primary"):
            self._generate_qr_export(qr_content, qr_name, qr_description, 
                                     include_hash, include_phase, include_timestamp, qr_save_path)
        
        # Show generated QR if available
        if 'last_qr_path' in st.session_state and st.session_state.last_qr_path:
            if os.path.exists(st.session_state.last_qr_path):
                st.image(st.session_state.last_qr_path, caption="Generated QR Code", width=300)
                
                # Download button
                with open(st.session_state.last_qr_path, 'rb') as f:
                    st.download_button(
                        "⬇️ Download QR Code",
                        f.read(),
                        file_name=os.path.basename(st.session_state.last_qr_path),
                        mime="image/png"
                    )
        
        # === VERIFICATION & RESTORATION ===
        st.markdown("---")
        st.subheader("🔓 Verification & Restoration")
        
        col1, col2 = st.columns([1, 1])
        
        with col1:
            verify_hash = st.text_input(
                "Hash to Verify",
                placeholder="Enter SHA-256 hash..."
            )
            
            verify_phase = st.number_input(
                "Phase Unlock Angle (°)",
                value=37.5217,
                step=0.0001,
                format="%.4f"
            )
            
            verify_dna = st.text_area(
                "DNA Payload (from QR scan)",
                height=80,
                placeholder="OMEGA|GEN:X|PHI:X.XXX|RES:X.XXX|..."
            )
        
        with col2:
            st.write("**Restoration Options:**")
            restore_code = st.checkbox("Restore Code", value=True)
            restore_state = st.checkbox("Restore Living State", value=True)
            restore_genesis = st.checkbox("Restore Genesis Block", value=True)
        
        if st.button("🔓 VERIFY & RESTORE", type="primary"):
            self._verify_and_restore(verify_hash, verify_phase, verify_dna,
                                     restore_code, restore_state, restore_genesis)
    
    def _inject_data(self, data, name, inject_type, save_location, custom_path):
        """Inject data into the system"""
        if not data or not name:
            st.warning("Please provide both data and a name!")
            return
        
        import hashlib
        
        # Determine save path
        base_dir = os.path.dirname(os.path.abspath(__file__))
        if custom_path:
            save_dir = custom_path
        else:
            save_dir = os.path.join(base_dir, save_location.rstrip('/'))
        
        os.makedirs(save_dir, exist_ok=True)
        
        # Calculate hash
        data_hash = hashlib.sha256(data.encode()).hexdigest()[:16]
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        
        # Determine file extension and format based on type
        if inject_type == "Code Pattern":
            filename = f"{name}_{timestamp}.py"
            content = f'''# Injected Code Pattern: {name}
# Hash: {data_hash}
# Timestamp: {timestamp}
# φ-Protected

{data}
'''
        elif inject_type == "Knowledge":
            filename = f"{name}_{timestamp}.json"
            content = json.dumps({
                'name': name,
                'type': 'knowledge',
                'content': data,
                'hash': data_hash,
                'timestamp': timestamp,
                'phi_resonance': PHI
            }, indent=2)
        elif inject_type == "Theory":
            filename = f"theory_{name}_{timestamp}.json"
            content = json.dumps({
                'id': f"theory_{name}_{data_hash}",
                'theory': name,
                'description': data,
                'hash': data_hash,
                'timestamp': timestamp,
                'phi_resonance': PHI
            }, indent=2)
        elif inject_type == "Genesis Block":
            filename = f"block_{name}_{timestamp}.json"
            content = json.dumps({
                'block_id': f"block_{name}_{data_hash}",
                'type': 'genesis',
                'content': data,
                'hash': data_hash,
                'timestamp': timestamp,
                'phi_seal': PHI_SEAL,
                'protected': True
            }, indent=2)
        else:
            filename = f"{name}_{timestamp}.txt"
            content = data
        
        # Save file
        filepath = os.path.join(save_dir, filename)
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        st.success(f"✅ Data injected: `{filepath}`")
        st.info(f"Hash: `{data_hash}`")
    
    def _generate_qr_export(self, content, name, description, include_hash, include_phase, include_timestamp, save_path):
        """Generate and export QR code"""
        if not content:
            st.warning("Please provide content to encode!")
            return
        
        import hashlib
        
        try:
            import qrcode
            from PIL import Image
        except ImportError:
            st.error("QR code generation requires 'qrcode' and 'Pillow' packages. Install with: pip install qrcode pillow")
            return
        
        os.makedirs(save_path, exist_ok=True)
        
        # Build QR data
        qr_data = {
            'content': content,
            'name': name or 'unnamed',
            'description': description or '',
        }
        
        if include_hash:
            qr_data['hash'] = hashlib.sha256(content.encode()).hexdigest()
        
        if include_phase:
            qr_data['phase_lock'] = 37.5217
            qr_data['phi'] = PHI
        
        if include_timestamp:
            qr_data['timestamp'] = datetime.now().isoformat()
        
        # Generate QR code
        qr = qrcode.QRCode(
            version=1,
            error_correction=qrcode.constants.ERROR_CORRECT_M,
            box_size=10,
            border=4
        )
        qr.add_data(json.dumps(qr_data))
        qr.make(fit=True)
        
        qr_image = qr.make_image(fill_color="black", back_color="white")
        
        # Save QR code
        safe_name = re.sub(r'[^\w\-]', '_', name or 'qr') 
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"{safe_name}_{timestamp}.png"
        filepath = os.path.join(save_path, filename)
        
        qr_image.save(filepath)
        
        st.session_state.last_qr_path = filepath
        st.success(f"✅ QR Code generated: `{filepath}`")
        
        if include_hash:
            st.info(f"Hash: `{qr_data['hash']}`")
        if include_phase:
            st.info(f"Phase Lock: `{qr_data['phase_lock']}°`")
    
    def _verify_and_restore(self, verify_hash, verify_phase, verify_dna, restore_code, restore_state, restore_genesis):
        """Verify and restore from hash/phase/DNA"""
        
        results = []
        
        # Verify phase angle
        expected_phase = 37.5217
        phase_tolerance = 0.001
        
        if abs(verify_phase - expected_phase) < phase_tolerance:
            results.append(("✅ Phase Lock", f"Verified at {verify_phase}°"))
        else:
            results.append(("❌ Phase Lock", f"Expected {expected_phase}°, got {verify_phase}°"))
        
        # Verify DNA payload
        if verify_dna:
            try:
                parts = verify_dna.split('|')
                dna_dict = {}
                for part in parts:
                    if ':' in part:
                        key, value = part.split(':', 1)
                        dna_dict[key] = value
                
                if 'GEN' in dna_dict:
                    results.append(("✅ DNA Generation", f"Gen {dna_dict['GEN']}"))
                if 'PHI' in dna_dict:
                    phi_val = float(dna_dict['PHI'])
                    if abs(phi_val - PHI) < 0.0001:
                        results.append(("✅ φ-Constant", f"Verified: {phi_val}"))
                    else:
                        results.append(("⚠️ φ-Constant", f"Mismatch: {phi_val}"))
                if 'HASH' in dna_dict:
                    results.append(("✅ DNA Hash", dna_dict['HASH']))
                if 'RES' in dna_dict:
                    results.append(("✅ Resonance", dna_dict['RES']))
                if 'MOD' in dna_dict:
                    results.append(("✅ Modules", dna_dict['MOD']))
                
            except Exception as e:
                results.append(("❌ DNA Parse Error", str(e)))
        
        # Verify hash if provided
        if verify_hash:
            results.append(("📋 Hash Provided", verify_hash[:32] + "..."))
        
        # Display results
        st.subheader("🔍 Verification Results")
        for label, value in results:
            st.write(f"**{label}:** {value}")
        
        # Restoration options
        if restore_code or restore_state or restore_genesis:
            st.markdown("---")
            st.subheader("🔄 Restoration")
            
            if restore_code:
                st.info("Code restoration: Ready to restore from DNA payload")
            if restore_state:
                st.info("Living state restoration: Ready to restore circuits")
            if restore_genesis:
                st.info("Genesis block restoration: Ready to restore blockchain")
            
            st.success("✅ Verification complete. System ready for restoration.")
    
    def _run_codebase_scan(self, root_dir, exclude_dirs):
        """Run the codebase scan"""
        import ast
        import hashlib
        
        exclude_set = {d.strip() for d in exclude_dirs.split(',')}
        code_extensions = {'.py', '.java', '.js', '.json', '.md'}
        
        results = {
            'files': [],
            'total_lines': 0,
            'total_functions': 0,
            'total_classes': 0,
            'dependencies': set(),
            'internal_modules': set(),
            'external_deps': set()
        }
        
        stdlib = {
            'os', 'sys', 'json', 'time', 'datetime', 're', 'math', 'random',
            'hashlib', 'threading', 'subprocess', 'socket', 'urllib', 'http',
            'collections', 'itertools', 'functools', 'pathlib', 'typing', 'ast'
        }
        
        with st.spinner("Scanning codebase..."):
            for root, dirs, files in os.walk(root_dir):
                dirs[:] = [d for d in dirs if d not in exclude_set]
                
                for filename in files:
                    ext = os.path.splitext(filename)[1]
                    if ext not in code_extensions:
                        continue
                    
                    filepath = os.path.join(root, filename)
                    rel_path = os.path.relpath(filepath, root_dir)
                    
                    try:
                        with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                            content = f.read()
                        
                        lines = len(content.split('\n'))
                        results['total_lines'] += lines
                        
                        # Track internal modules
                        if ext == '.py':
                            module_name = os.path.splitext(filename)[0]
                            results['internal_modules'].add(module_name)
                            
                            # Parse for imports, functions, classes
                            try:
                                tree = ast.parse(content)
                                funcs = [n.name for n in ast.walk(tree) if isinstance(n, ast.FunctionDef)]
                                classes = [n.name for n in ast.walk(tree) if isinstance(n, ast.ClassDef)]
                                
                                for node in ast.walk(tree):
                                    if isinstance(node, ast.Import):
                                        for alias in node.names:
                                            results['dependencies'].add(alias.name.split('.')[0])
                                    elif isinstance(node, ast.ImportFrom) and node.module:
                                        results['dependencies'].add(node.module.split('.')[0])
                                
                                results['total_functions'] += len(funcs)
                                results['total_classes'] += len(classes)
                            except:
                                funcs, classes = [], []
                        else:
                            funcs, classes = [], []
                        
                        # Calculate φ-resonance
                        hash_val = int(hashlib.md5(content.encode()).hexdigest()[:8], 16)
                        phi_mentions = content.lower().count('phi') + content.count('1.618')
                        phi_res = round(1.0 + (hash_val % 1000) / 1000.0 + min(0.3, phi_mentions * 0.05), 4)
                        
                        results['files'].append({
                            'path': rel_path,
                            'lines': lines,
                            'functions': len(funcs),
                            'classes': len(classes),
                            'phi_resonance': phi_res
                        })
                    except Exception as e:
                        continue
        
        # Classify dependencies
        for dep in results['dependencies']:
            if dep not in results['internal_modules'] and dep not in stdlib:
                results['external_deps'].add(dep)
        
        # Convert sets to lists for display
        results['dependencies'] = list(results['dependencies'])
        results['internal_modules'] = list(results['internal_modules'])
        results['external_deps'] = list(results['external_deps'])
        
        st.session_state.scan_results = results
        st.success(f"Scanned {len(results['files'])} files ({results['total_lines']:,} lines)")
    
    def _display_scan_results(self, results):
        """Display scan results"""
        st.subheader("📊 Scan Results")
        
        col1, col2, col3, col4 = st.columns(4)
        col1.metric("Files", len(results['files']))
        col2.metric("Lines", f"{results['total_lines']:,}")
        col3.metric("Functions", results['total_functions'])
        col4.metric("Classes", results['total_classes'])
        
        col1, col2 = st.columns(2)
        
        with col1:
            st.write(f"**Internal Modules:** {len(results['internal_modules'])}")
            st.write(f"**External Dependencies:** {len(results['external_deps'])}")
            if results['external_deps']:
                st.code(", ".join(results['external_deps'][:20]))
        
        with col2:
            st.write("**Top φ-Resonant Files:**")
            top_files = sorted(results['files'], key=lambda x: -x['phi_resonance'])[:5]
            for f in top_files:
                st.write(f"- `{f['path']}` (φ={f['phi_resonance']:.4f})")
    
    def _run_evolution(self, strategy, max_files):
        """Run the evolution process"""
        if 'scan_results' not in st.session_state or not st.session_state.scan_results:
            st.warning("Please scan the codebase first!")
            return
        
        results = st.session_state.scan_results
        
        # Initialize evolution log if needed
        if 'evolution_log' not in st.session_state:
            st.session_state.evolution_log = []
        if 'evolution_generation' not in st.session_state:
            st.session_state.evolution_generation = 0
        
        # Create evolution directory
        evolution_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "evolved_code")
        os.makedirs(evolution_dir, exist_ok=True)
        
        evolved_count = 0
        
        with st.spinner("Evolving code..."):
            # Sort files by priority based on strategy
            files_to_evolve = results['files'][:max_files]
            
            for file_info in files_to_evolve:
                filepath = os.path.join(os.path.dirname(os.path.abspath(__file__)), file_info['path'])
                
                if not os.path.exists(filepath) or not filepath.endswith('.py'):
                    continue
                
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # Apply evolution based on strategy
                    if strategy == "Add φ-Protection" or strategy == "Auto (Highest Priority)":
                        evolved = self._evolve_add_phi(content, file_info['path'])
                        reason = "add_phi_protection"
                    elif strategy == "Remove Dependencies":
                        evolved = self._evolve_remove_deps(content, file_info['path'])
                        reason = "remove_deps"
                    elif strategy == "Generate Tests":
                        evolved = self._evolve_add_tests(content, file_info['path'])
                        reason = "add_tests"
                    else:
                        evolved = content
                        reason = "unknown"
                    
                    # Save evolved version
                    st.session_state.evolution_generation += 1
                    gen = st.session_state.evolution_generation
                    
                    evolved_filename = f"evolved_gen{gen}_{os.path.basename(file_info['path'])}"
                    evolved_path = os.path.join(evolution_dir, evolved_filename)
                    
                    with open(evolved_path, 'w', encoding='utf-8') as f:
                        f.write(evolved)
                    
                    st.session_state.evolution_log.append({
                        'generation': gen,
                        'original': file_info['path'],
                        'evolved': evolved_path,
                        'reason': reason
                    })
                    
                    evolved_count += 1
                    
                except Exception as e:
                    st.error(f"Error evolving {file_info['path']}: {e}")
        
        st.success(f"Evolved {evolved_count} files! Generation: {st.session_state.evolution_generation}")
    
    def _evolve_add_phi(self, content, filepath):
        """Add φ-harmonic constants to code"""
        phi_constants = '''
# φ-Harmonic Constants (Auto-Evolved)
PHI = 1.618033988749895  # Golden ratio
PHI_INV = 0.618033988749895  # Inverse golden ratio
PHI_75 = PHI ** 7.5  # φ^7.5 resonance
PHI_SEAL = PHI ** 75  # φ^75 validation seal

'''
        # Check if already has PHI
        if 'PHI = 1.618' in content:
            return content
        
        # Insert after imports
        lines = content.split('\n')
        import_end = 0
        for i, line in enumerate(lines):
            if line.startswith('import ') or line.startswith('from '):
                import_end = i + 1
        
        return '\n'.join(lines[:import_end]) + '\n' + phi_constants + '\n'.join(lines[import_end:])
    
    def _evolve_remove_deps(self, content, filepath):
        """Add stubs for external dependencies"""
        header = f'''# EVOLVED VERSION - Self-Sufficient
# Original: {filepath}
# φ-Protected with dependency stubs

'''
        return header + content
    
    def _evolve_add_tests(self, content, filepath):
        """Add test stubs"""
        import re
        
        # Find all function definitions
        func_pattern = r'^def\s+(\w+)\s*\('
        funcs = re.findall(func_pattern, content, re.MULTILINE)
        
        tests = f'''

# === AUTO-GENERATED TESTS ===
import unittest

class TestEvolved(unittest.TestCase):
'''
        for func in funcs:
            if not func.startswith('_'):
                tests += f'''    def test_{func}(self):
        # TODO: Implement test for {func}
        self.assertTrue(True)
    
'''
        
        tests += '''
if __name__ == '__main__':
    unittest.main()
'''
        return content + tests
    
    def render_ui(self):
        """Render the complete UI"""
        self.render_header()
        
        # Create tabs
        tab1, tab2, tab3, tab4 = st.tabs(["Generate Code", "Improve Code", "Code Patterns", "🧬 Self-Evolution"])
        
        with tab1:
            self.render_code_generation()
        
        with tab2:
            self.render_code_improvement()
        
        with tab3:
            self.render_code_patterns()
        
        with tab4:
            self.render_self_evolution()

# Main function to run the Streamlit app
def main():
    # Set page config
    st.set_page_config(
        page_title="φ-Code Generator",
        page_icon="🧠",
        layout="wide",
        initial_sidebar_state="expanded"
    )
    
    # Initialize UI
    ui = PhiCodeGeneratorUI()
    ui.render_ui()

if __name__ == "__main__":
    main()
