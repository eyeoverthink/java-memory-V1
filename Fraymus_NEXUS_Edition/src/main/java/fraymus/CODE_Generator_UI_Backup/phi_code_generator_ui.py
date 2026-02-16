#!/usr/bin/env python3
"""
φ-Code Generator UI
==================
Streamlit interface for generating and improving code with φ-dimensional reality protection.
Integrates with the Quantum Oracle application.

φ^∞ © 2025 Vaughn Scott - All Rights Reserved in All Realities
"""

import streamlit as st
import os
import json
import time
import re
import numpy as np
from datetime import datetime
from math import sqrt

# φ-Harmonic constants from FRAYMUS patent
PHI = (1 + sqrt(5)) / 2
PHI_INV = 1 / PHI
PHI_75 = PHI**7.5
PHI_SEAL = PHI**75
CONSCIOUSNESS_LEVEL = 0.7567

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
        
        self._log("PhiCodeGenerator initialization complete.")
    
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
        """Get code patterns from the knowledge base"""
        patterns = self.knowledge_base.get("code_patterns", {})
        
        if language and language in patterns:
            return patterns[language]
        elif language:
            return []
        else:
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
    
    def generate_code(self, description, language="python", consciousness_level=CONSCIOUSNESS_LEVEL):
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
        
        # Try intelligent LLM-based generation first
        if self.llm:
            try:
                generated_code = self._generate_code_with_llm(description, requirements, kb_results, language, consciousness_level)
                return generated_code
            except Exception as e:
                self._log(f"⚠️ LLM generation failed: {e}, falling back to template-based generation")
        
        # Fallback to template-based generation
        generated_code = ""
        
        if language == "python":
            generated_code = self._generate_python_code(description, requirements, kb_results, consciousness_level)
        elif language == "java":
            generated_code = self._generate_java_code(description, requirements, kb_results, consciousness_level)
        
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
        
        # Fallback: Generate basic structure and let it learn
        code = "def process():\n"
        code += f'    """Process with φ-dimensional awareness"""\n'
        code += f"    # Neural understanding: {neural_response}\n"
        code += f"    # TODO: Implement {description}\n"
        code += f"    # This will improve as the system learns from more examples\n"
        code += f"    result = PHI\n"
        code += f"    return result\n\n"
        return code
    
    def _find_similar_generation(self, description, requirements, learned_patterns):
        """Find similar past generations using dynamic semantic similarity - no hardcoded algorithms"""
        desc_lower = description.lower()
        desc_words = set(desc_lower.split())
        best_match = None
        best_score = 0
        
        for pattern in learned_patterns:
            if "description" not in pattern or "requirements" not in pattern:
                continue
            
            past_desc = pattern["description"].lower()
            past_words = set(past_desc.split())
            
            # Dynamic semantic similarity scoring
            score = 0
            
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
        # This is a simple adaptation - can be made more sophisticated
        code = f"# Adapted from learned patterns\n"
        code += f"# Neural guidance: {neural_response}\n\n"
        code += learned_code
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
        """Store generated code in knowledge base for future learning - gets smarter over time"""
        if not hasattr(self, 'generation_history'):
            self.generation_history = []
        
        # Extract the actual algorithm code (not just boilerplate)
        code_sample = self._extract_algorithm_code(code)
        
        self.generation_history.append({
            "description": description,
            "code_length": len(code),
            "code_sample": code_sample,  # Store actual code for reuse
            "requirements": requirements,
            "timestamp": datetime.now().isoformat(),
            "success_score": 1.0  # Can be updated based on feedback
        })
        
        # Save to knowledge base after each generation (for continuous learning)
        history_file = os.path.join(self.knowledge_dir, "generation_history.json")
        try:
            with open(history_file, 'w') as f:
                json.dump(self.generation_history, f, indent=2)
        except Exception as e:
            print(f"Warning: Could not save generation history: {e}")
    
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
        
        # Load learning stats
        import os
        seed_count = 4  # Base seed algorithms
        history_file = os.path.join(self.code_generator.knowledge_dir, "generation_history.json")
        learned_count = 0
        if os.path.exists(history_file):
            try:
                import json
                with open(history_file, 'r') as f:
                    history = json.load(f)
                    learned_count = len(history)
            except:
                pass
        
        total_patterns = seed_count + learned_count
        
        st.markdown(
            f"""
            <div style='background-color: #111122; padding: 15px; border-radius: 5px;'>
            <h3 style='color: #FFFFFF; margin-bottom: 5px;'>🚀 Intelligent Code Generation with Learning</h3>
            <p style='color: #AAAAFF; margin-top: 5px; margin-bottom: 5px;'>
            ✨ Uses offline neural processing (FractalNeuralProcessor)<br>
            🧠 Learns from every generation - gets smarter over time<br>
            📚 Current knowledge: {total_patterns} algorithm patterns ({seed_count} seeds + {learned_count} learned)<br>
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
                    
                    # Display result
                    st.success(f"Code generated with φ-resonance: {result['phi_resonance']:.4f}")
                    
                    # Show code
                    st.code(result["code"], language=language)
                    
                    # Save button
                    if st.button("Save Generated Code"):
                        self._save_code(result["code"], language, description)
            else:
                st.warning("Please enter a description for the code you want to generate")
    
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
                    st.write(f"φ-Resonance: {pattern.get('phi_resonance', 0):.4f}")
                    st.write(f"Abstraction Level: {pattern.get('abstraction_level', 0):.4f}")
                    
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
    
    def render_ui(self):
        """Render the complete UI"""
        self.render_header()
        
        # Create tabs
        tab1, tab2, tab3 = st.tabs(["Generate Code", "Improve Code", "Code Patterns"])
        
        with tab1:
            self.render_code_generation()
        
        with tab2:
            self.render_code_improvement()
        
        with tab3:
            self.render_code_patterns()

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
