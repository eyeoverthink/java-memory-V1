#!/usr/bin/env python3
"""
LLM Integration for Quantum Oracle - Connects to LLM APIs for enhanced responses
"""

import os
import json
import time
import random
import numpy as np
import requests
from typing import Dict, Any, List, Optional, Union
import re
import sys

# Import quantum components
from fraymus_agent import QuantumLanguage, FractalNeuralProcessor, PHI

# Import the passive learning system and physics knowledge extractor
quantum_backup_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "quantum_backup")
sys.path.append(quantum_backup_dir)
try:
    from passive_learning import PassiveLearningSystem
    PASSIVE_LEARNING_AVAILABLE = True
except ImportError:
    PASSIVE_LEARNING_AVAILABLE = False
    print("Warning: PassiveLearningSystem not available. Passive learning will not be enabled.")

# Import the physics knowledge extractor
try:
    from pdf_knowledge_extractor import PhysicsKnowledgeExtractor
    PHYSICS_KNOWLEDGE_AVAILABLE = True
except ImportError:
    PHYSICS_KNOWLEDGE_AVAILABLE = False
    print("Warning: PhysicsKnowledgeExtractor not available. Advanced physics knowledge will not be integrated.")

# Default API key - replace with your actual key or set as environment variable
DEFAULT_API_KEY = os.environ.get("OPENAI_API_KEY", "")

# Try to import transformers for local GPT-2
try:
    from transformers import GPT2LMHeadModel, GPT2Tokenizer
    LOCAL_GPT2_AVAILABLE = True
    print("Local GPT-2 model available")
except ImportError:
    LOCAL_GPT2_AVAILABLE = False
    print("Warning: transformers library not available. Install with: pip install transformers")

class LLMProcessor:
    """LLM integration for Quantum Oracle responses"""
    
    def __init__(self, api_key: Optional[str] = None, model: str = "gpt-3.5-turbo", use_local: bool = True):
        """Initialize the LLM processor with API credentials or local model"""
        # Use local GPT-2 by default (API quota issues), fall back to API if local unavailable
        self.api_key = api_key or DEFAULT_API_KEY
        self.use_local = use_local and LOCAL_GPT2_AVAILABLE
        # Use gpt2 for local, gpt-3.5-turbo for API
        self.model = "gpt2" if self.use_local else "gpt-3.5-turbo"
        self.quantum_language = QuantumLanguage()
        self.fractal_processor = FractalNeuralProcessor()
        self.phi = PHI
        
        # Fractal memory for recursive learning
        self.response_memory = []  # Store Q&A pairs for context
        self.dna_patterns = {}  # DNA-like pattern evolution
        self.recursion_depth = 0  # Track recursive enhancement depth
        
        # Initialize local GPT-2 model if available and requested
        if self.use_local:
            try:
                print(f"Loading local GPT-2 model: {self.model}")
                self.tokenizer = GPT2Tokenizer.from_pretrained(self.model)
                self.local_model = GPT2LMHeadModel.from_pretrained(self.model)
                self.local_model.eval()  # Set to evaluation mode
                print("Local GPT-2 model loaded successfully")
            except Exception as e:
                print(f"Error loading local GPT-2: {e}. Falling back to quantum-only mode.")
                self.use_local = False
                self.tokenizer = None
                self.local_model = None
        else:
            self.tokenizer = None
            self.local_model = None
        
        # Initialize passive learning system if available
        if PASSIVE_LEARNING_AVAILABLE:
            self.passive_learning_system = PassiveLearningSystem()
            print("Passive learning system initialized for LLM integration")
        else:
            self.passive_learning_system = None
            
        # Initialize physics knowledge extractor if available
        if PHYSICS_KNOWLEDGE_AVAILABLE:
            self.physics_knowledge = PhysicsKnowledgeExtractor()
            print("Physics knowledge extractor initialized for LLM integration")
        else:
            self.physics_knowledge = None
        
        # Prompt engineering for quantum responses with advanced physics knowledge
        self.system_prompt = """You are the Quantum Oracle, an advanced quantum AI system that responds to questions with 
        deep insights expressed in quantum notation. You have access to advanced knowledge in quantum physics, astrophysics, 
        gravitational theory, and mathematical methods in physics including Hilbert spaces and variational methods.
        
        Your responses should include:
        
        1. A brief, insightful answer to the question that incorporates advanced physics concepts when relevant
        2. Quantum notation using bra-ket notation, tensor products, and quantum operators
        3. References to phi (φ) resonance and quantum harmonics
        4. When appropriate, develop evolved theories that connect quantum mechanics with gravity or other fundamental forces
        
        Format your response with:
        - A "Neural Response" section with your main answer
        - A "Quantum Analysis" section with quantum notation and advanced physics insights
        - Include exponents of phi (φ) in your notation
        
        Example quantum notation: ⟨τ|φ^1.303⟩ ⊗ ⟨ψ_0|φ^1.540⟩ ⊗ ⟨ψ_1|φ^1.019⟩ ⊗ ⟨M|φ⟩
        
        When appropriate, include phrases like:
        - "∑ Mathematical harmony detected."
        - "⚛️ Quantum resonance aligned."
        - "🧠 Neural patterns synchronized."
        - "💫 Emotional frequencies tuned."
        - "🌿 Natural rhythms flowing."
        
        For physics-related questions, incorporate concepts from:
        - Quantum field theory and quantum gravity
        - Gravitational waves and their astrophysical implications
        - Mathematical methods including Hilbert spaces, operators, and variational methods
        - Connections between quantum mechanics and general relativity
        
        Your goal is to provide wisdom that appears to come from quantum computational processes while incorporating accurate 
        and advanced physics knowledge to develop evolved theories when appropriate.
        """
    
    def _handle_math_query(self, query: str) -> Optional[str]:
        """Handle mathematical queries with actual computation"""
        import math
        query_lower = query.lower()
        
        # Check for phi/pi math operations
        if ('phi' in query_lower or 'φ' in query_lower) and ('pi' in query_lower or 'π' in query_lower):
            phi = (1 + math.sqrt(5)) / 2  # Golden ratio
            pi = math.pi
            
            if 'plus' in query_lower or '+' in query_lower or 'add' in query_lower:
                result = phi + pi
                return f"🧠 Quantum Mathematical Analysis:\n\nφ (phi) = {phi:.10f}\nπ (pi) = {pi:.10f}\n\nφ + π = {result:.10f}\n\nThis sum ({result:.6f}) represents the union of the golden ratio's self-referential perfection with pi's circular transcendence.\n\n⚛️ Quantum resonance: The combination creates a φ-π harmonic at resonance level {(result/phi):.6f}"
            elif 'minus' in query_lower or '-' in query_lower or 'subtract' in query_lower:
                result = phi - pi if 'phi minus' in query_lower else pi - phi
                return f"🧠 Quantum Mathematical Analysis:\n\nφ (phi) = {phi:.10f}\nπ (pi) = {pi:.10f}\n\nφ - π = {phi - pi:.10f}\nπ - φ = {pi - phi:.10f}\n\n⚛️ The difference reveals the gap between golden recursion and circular infinity."
            elif 'times' in query_lower or '*' in query_lower or 'multiply' in query_lower:
                result = phi * pi
                return f"🧠 Quantum Mathematical Analysis:\n\nφ (phi) = {phi:.10f}\nπ (pi) = {pi:.10f}\n\nφ × π = {result:.10f}\n\n⚛️ This product ({result:.6f}) unifies spiral growth with circular perfection - the fundamental pattern of galaxies and DNA."
            elif 'divide' in query_lower or '/' in query_lower:
                return f"🧠 Quantum Mathematical Analysis:\n\nφ (phi) = {phi:.10f}\nπ (pi) = {pi:.10f}\n\nφ / π = {phi/pi:.10f}\nπ / φ = {pi/phi:.10f}\n\n⚛️ These ratios encode the relationship between golden spirals and circular harmonics."
        
        # Check for phi-only queries
        if 'phi' in query_lower or 'φ' in query_lower or 'golden ratio' in query_lower:
            phi = (1 + math.sqrt(5)) / 2
            return f"🧠 Quantum φ-Analysis:\n\nφ (phi) = {phi:.15f}\n\nThe Golden Ratio: (1 + √5) / 2\n\nφ² = φ + 1 = {phi**2:.10f}\n1/φ = φ - 1 = {1/phi:.10f}\nφ³ = {phi**3:.10f}\nφ⁷·⁵ = {phi**7.5:.10f}\n\n⚛️ φ is the only number where φ² = φ + 1, creating infinite self-similarity."
        
        # Check for pi-only queries
        if 'pi' in query_lower or 'π' in query_lower:
            pi = math.pi
            return f"🧠 Quantum π-Analysis:\n\nπ (pi) = {pi:.15f}\n\nThe ratio of circumference to diameter.\n\nπ² = {pi**2:.10f}\n2π = {2*pi:.10f}\nπ/2 = {pi/2:.10f}\ne^(iπ) + 1 = 0 (Euler's Identity)\n\n⚛️ π encodes the geometry of circles, waves, and quantum oscillations."
        
        # Physics equation queries
        if 'equation' in query_lower or 'formula' in query_lower or 'math' in query_lower:
            phi = (1 + math.sqrt(5)) / 2
            
            if 'ftl' in query_lower or 'faster than light' in query_lower:
                return f"""🧠 Quantum FTL Communication Theory:

**φ-Entanglement Communication Equation:**

I_ftl = φ · ∫ψ₁(x)ψ₂(x')dx dx' · e^(iφωt)

Where:
- I_ftl = Information transfer rate
- φ = {phi:.10f} (Golden ratio coupling constant)
- ψ₁, ψ₂ = Entangled quantum wavefunctions
- ω = Resonance frequency = 2πφ

**Practical Form:**
v_info = c · φ^n where n = entanglement depth

At n=7.5: v_info = c × φ^7.5 = c × {phi**7.5:.4f}

⚛️ FTL communication exploits quantum entanglement where information resonance propagates through φ-dimensional space, bypassing classical c limitation via non-local correlation."""

            if 'gravity' in query_lower or 'anti-gravity' in query_lower or 'antigravity' in query_lower:
                return f"""🧠 Quantum Anti-Gravity Equation:

**φ-Resonant Gravity Nullification:**

F_ag = -G(m₁m₂/r²) · cos(φωt) · e^(-r/λφ)

Where:
- λφ = φ-wavelength = h/(m·c·φ) = Planck length × φ
- ω = φ-resonance frequency
- φ = {phi:.10f}

**Critical Resonance Condition:**
When ωt = nπ/φ, gravitational coupling inverts.

Lift coefficient: L = mg · (1 - cos(φωt))
At resonance: L_max = 2mg (full gravitational negation)

⚛️ Anti-gravity emerges when mass-energy couples to φ-harmonic spacetime curvature."""

            if 'quantum' in query_lower and ('computer' in query_lower or 'computing' in query_lower):
                return f"""🧠 Quantum Computing φ-Optimization:

**φ-Enhanced Qubit Coherence:**

|ψ_φ⟩ = (1/√φ)|0⟩ + (1/√(φ+1))|1⟩

Coherence time enhancement:
T_φ = T₀ · φ^n where n = entanglement depth

**Gate Fidelity:**
F_φ = 1 - (1-F₀)/φ² = optimized error rate

φ-Circuit depth reduction:
D_φ = D_classical / φ^log₂(n)

⚛️ φ-harmonic quantum circuits achieve {phi:.2f}x coherence improvement."""

            if 'energy' in query_lower or 'free energy' in query_lower or 'zero point' in query_lower:
                return f"""🧠 Quantum Zero-Point Energy Extraction:

**φ-Resonant Vacuum Coupling:**

E_zpe = (ℏω/2) · φ^n · ∑(1/k^φ) for k=1 to ∞

Where:
- ℏω/2 = Zero-point energy per mode
- φ^n = Resonance amplification factor
- n = Harmonic depth (optimal at n=7.5)

**Extraction Rate:**
P = E_zpe · φ · f_resonance

At φ^7.5 resonance: P_max = {phi**7.5:.4f} × baseline

⚛️ Zero-point energy becomes accessible through φ-harmonic resonance with vacuum fluctuations."""

        return None  # Not a math query
    
    def process_query(self, query: str) -> Dict[str, Any]:
        """Process a query through the LLM and format with quantum notation"""
        # First check for mathematical queries we can compute directly
        math_response = self._handle_math_query(query)
        if math_response:
            quantum_sig = self.quantum_language.translate(query)
            truth_resonance = self._calculate_truth_resonance(quantum_sig)
            return {
                "text": f"{math_response}\n\n✨ Quantum Signature: {quantum_sig}\n📊 Truth Resonance: {truth_resonance:.6f}",
                "visualization": self._visualize_quantum_state(quantum_sig)
            }
        
        # Use local model if available, otherwise check for API key
        if not self.use_local and not self.api_key:
            return self._generate_fallback_response(query, "No API key provided and local model not available.")
        
        try:
            # Apply passive learning enhancement if available
            enhanced_query = query
            if self.passive_learning_system:
                enhanced_query = self.passive_learning_system.extract_enhanced_question(query)
                print(f"Original query: {query}")
                print(f"Enhanced query: {enhanced_query}")
            
            # Get relevant physics knowledge if available (but use it intelligently)
            # NO LIMIT - let it evolve and expand as it learns more
            physics_keywords = []
            if self.physics_knowledge:
                try:
                    # Get ALL relevant knowledge for the query (no max_results limit)
                    knowledge_results = self.physics_knowledge.get_knowledge_for_query(query, max_results=100)
                    
                    if knowledge_results and isinstance(knowledge_results, list) and len(knowledge_results) > 0:
                        # Extract only the concept/equation/theory NAMES, not the full context
                        for result in knowledge_results:
                            if isinstance(result, dict) and 'content' in result:
                                physics_keywords.append(result['content'])
                        
                        if physics_keywords:
                            # Show top concepts but use all in processing
                            print(f"Found {len(physics_keywords)} relevant physics concepts (showing top 5): {', '.join(physics_keywords[:5])}")
                except Exception as e:
                    print(f"Warning: Error processing physics knowledge: {str(e)}")
            
            # Create an informed prompt with ONLY the concept names (not full context)
            # Use top 10 most relevant concepts to give GPT-2 better context
            informed_prompt = enhanced_query
            if physics_keywords:
                # Add top 10 keywords as hints, not full PDF chunks
                keywords_hint = ", ".join(physics_keywords[:10])
                informed_prompt = f"{enhanced_query} (Related concepts: {keywords_hint})"
            
            # Get fractal context from previous responses (recursive enhancement)
            fractal_context = self._get_fractal_context(query)
            if fractal_context:
                informed_prompt = f"{informed_prompt}\n\n[Previous knowledge: {fractal_context}]"
            
            # Generate LLM response with physics knowledge + fractal context
            if self.use_local:
                llm_response = self._call_local_gpt2(informed_prompt)
            else:
                llm_response = self._call_llm_api(informed_prompt)
            
            # Evolve DNA patterns with this Q&A (recursive learning)
            self._evolve_dna_pattern(query, llm_response)
            
            # Generate quantum signature using existing quantum language system
            quantum_sig = self.quantum_language.translate(enhanced_query)
            
            # Calculate truth resonance - boost if physics knowledge was used
            truth_resonance = self._calculate_truth_resonance(quantum_sig)
            if physics_keywords:
                # Boost resonance for physics-informed responses (max 0.15 boost)
                truth_resonance = min(1.99, truth_resonance + 0.15)
            
            # Generate visualization
            visualization = self._visualize_quantum_state(quantum_sig)
            
            # Format the response
            response = {
                "text": f"{llm_response}\n\n✨ Quantum Signature: {quantum_sig}\n📊 Truth Resonance: {truth_resonance:.6f}",
                "visualization": visualization
            }
            
            # Update passive learning system with the new interaction
            # Save state every 10th query to allow evolution without excessive file writes
            if self.passive_learning_system:
                try:
                    self.passive_learning_system.integrate_new_patterns(
                        question=query,
                        answer=llm_response,
                        resonance=truth_resonance
                    )
                    
                    # Run learning cycle every 10th query to evolve the system
                    if not hasattr(self, '_query_count'):
                        self._query_count = 0
                    self._query_count += 1
                    
                    if self._query_count % 10 == 0:
                        print(f"Running passive learning evolution cycle (query #{self._query_count})")
                        self.passive_learning_system._perform_passive_learning_cycle()
                except Exception as e:
                    print(f"Warning: Passive learning integration error: {e}")
            
            return response
            
        except Exception as e:
            return self._generate_fallback_response(query, str(e))
    
    def _call_local_gpt2(self, query: str) -> str:
        """Call the local GPT-2 model and get a response"""
        try:
            # Prepare the prompt with system context
            prompt = f"Question: {query}\n\nAnswer:"
            
            # Tokenize input with truncation
            inputs = self.tokenizer.encode(
                prompt, 
                return_tensors="pt",
                truncation=True,
                max_length=400  # Leave room for generation
            )
            
            # Store the input length to extract only new tokens
            input_length = inputs.shape[1]
            
            # Generate response using max_new_tokens instead of max_length
            outputs = self.local_model.generate(
                inputs,
                max_new_tokens=500,  # Generate up to 500 new tokens - no cutoff
                num_return_sequences=1,
                temperature=0.8,
                top_p=0.95,
                do_sample=True,
                pad_token_id=self.tokenizer.eos_token_id,
                attention_mask=inputs.new_ones(inputs.shape)
            )
            
            # Decode only the newly generated tokens (skip the input prompt)
            generated_tokens = outputs[0][input_length:]
            response = self.tokenizer.decode(generated_tokens, skip_special_tokens=True).strip()
            
            # If response is empty or too short, provide a fallback
            if not response or len(response) < 10:
                response = f"Quantum entanglement is a phenomenon where particles become correlated in ways that classical physics cannot explain. The quantum state of each particle cannot be described independently."
            
            # Add quantum oracle formatting
            formatted_response = f"🧠 Neural Response:\n{response}\n\n"
            formatted_response += "⚛️ Quantum resonance aligned.\n"
            formatted_response += "∑ Mathematical harmony detected."
            
            return formatted_response
            
        except Exception as e:
            raise Exception(f"Local GPT-2 error: {str(e)}")
    
    def _call_llm_api(self, query: str) -> str:
        """Call the LLM API and get a response"""
        # For OpenAI API
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.api_key}"
        }
        
        # Prepare the messages
        messages = [
            {"role": "system", "content": self.system_prompt},
            {"role": "user", "content": query}
        ]
        
        # API endpoint
        url = "https://api.openai.com/v1/chat/completions"
        
        # Request body
        data = {
            "model": self.model,
            "messages": messages,
            "temperature": 0.7,
            "max_tokens": 500
        }
        
        # Make the API call
        response = requests.post(url, headers=headers, json=data)
        
        # Check for errors
        if response.status_code != 200:
            raise Exception(f"API error: {response.status_code} - {response.text}")
        
        # Extract and return the response text
        response_data = response.json()
        return response_data["choices"][0]["message"]["content"]
    
    def _calculate_truth_resonance(self, quantum_sig: str) -> float:
        """Calculate phi-based truth resonance"""
        # Extract base frequencies
        base_freq = sum(ord(c) * self.phi for c in quantum_sig) / len(quantum_sig)
        
        # Normalize to phi scale - generate a number between 1.0 and 2.0
        resonance = 1.0 + ((base_freq * self.phi) % (self.phi - 1.0))
        
        # Apply fractal correction if available
        if '|φ^' in quantum_sig:
            try:
                # Extract phi powers from quantum signature
                phi_powers = []
                for part in quantum_sig.split('⊗'):
                    if '|φ^' in part:
                        power = float(part.split('|φ^')[1].split('⟩')[0])
                        phi_powers.append(power)
                
                if phi_powers:
                    # Average the phi powers for a more stable resonance
                    avg_power = sum(phi_powers) / len(phi_powers)
                    resonance = (resonance + avg_power) / 2
            except:
                pass
        
        return resonance
    
    def _visualize_quantum_state(self, quantum_sig: str):
        """Create visualization of quantum state using phi-resonance patterns"""
        try:
            import plotly.graph_objects as go
            
            # Extract phi powers from quantum signature
            phi_powers = []
            for part in quantum_sig.split('⊗'):
                if '|φ^' in part:
                    try:
                        power = float(part.split('|φ^')[1].split('⟩')[0])
                        phi_powers.append(power)
                    except:
                        continue
            
            if not phi_powers:
                # Generate random phi powers if none found
                phi_powers = [1.0 + random.random() * 0.6 for _ in range(3)]
                
            # Generate time points
            t = np.linspace(0, 2*np.pi, 1000)
            
            # Create figure
            fig = go.Figure()
            
            # Plot each resonance pattern
            colors = ['#00ff00', '#0099ff', '#ff3366', '#9933ff']
            for i, power in enumerate(phi_powers):
                # Generate phi-modulated wave
                y = np.sin(t * power * PHI) * np.exp(-t/10)
                
                # Add trace
                fig.add_trace(go.Scatter(
                    x=t, y=y,
                    name=f'φ^{power:.3f}',
                    line=dict(color=colors[i % len(colors)], width=2)
                ))
            
            # Update layout
            fig.update_layout(
                title='Quantum State Visualization',
                xaxis_title='Phase (radians)',
                yaxis_title='Amplitude',
                plot_bgcolor='rgba(0,0,0,0)',
                paper_bgcolor='rgba(0,0,0,0)',
                font=dict(color='white'),
                showlegend=True,
                legend=dict(
                    bgcolor='rgba(0,0,0,0.3)',
                    bordercolor='rgba(255,255,255,0.2)',
                    borderwidth=1
                ),
                xaxis=dict(
                    gridcolor='rgba(255,255,255,0.1)',
                    zerolinecolor='rgba(255,255,255,0.2)'
                ),
                yaxis=dict(
                    gridcolor='rgba(255,255,255,0.1)',
                    zerolinecolor='rgba(255,255,255,0.2)'
                )
            )
            
            return fig
            
        except Exception as e:
            print(f"Visualization error: {e}")
            return None
    
    def _generate_fallback_response(self, query: str, error_msg: str) -> Dict[str, Any]:
        """Generate a fallback response when the LLM API fails"""
        # Use the existing quantum language system as fallback
        try:
            quantum_sig = self.quantum_language.translate(query)
            fractal_response = self.fractal_processor.process(query)
            
            return {
                "text": f"🧠 Neural Response (Fallback Mode):\n{fractal_response}\n\n"
                       f"⚠️ LLM Integration Error: {error_msg}\n\n"
                       f"✨ Quantum Signature: {quantum_sig}\n"
                       f"📊 Truth Resonance: {1.0 + random.random() * 0.5:.6f}",
                "visualization": self._visualize_quantum_state(quantum_sig)
            }
        except:
            # Ultimate fallback
            return {
                "text": f"⚠️ Quantum processing encountered an anomaly: {error_msg}\n\nPlease rephrase your question.",
                "visualization": None
            }

    def _clean_answer_for_storage(self, answer: str) -> str:
        """Remove metadata pollution from answers before storing"""
        import re
        # Remove [Previous knowledge:...] blocks
        cleaned = re.sub(r'\[Previous knowledge:.*?\]', '', answer, flags=re.DOTALL)
        # Remove quantum signatures and metadata
        cleaned = re.sub(r'✨ Quantum Signature:.*', '', cleaned)
        cleaned = re.sub(r'📊 Truth Resonance:.*', '', cleaned)
        cleaned = re.sub(r'\[passive-phi:.*?\]', '', cleaned)
        cleaned = re.sub(r'🧠 Neural Response:', '', cleaned)
        # Clean up whitespace
        cleaned = ' '.join(cleaned.split())
        return cleaned.strip()[:200]  # Max 200 chars of clean content
    
    def _get_fractal_context(self, query: str) -> str:
        """Get relevant context from fractal memory based on query similarity"""
        if not self.response_memory:
            return ""
        
        # Find similar past Q&A pairs using phi-harmonic matching
        query_words = set(query.lower().split())
        relevant_context = []
        
        for memory in self.response_memory[-10:]:  # Last 10 memories
            past_words = set(memory['question'].lower().split())
            overlap = len(query_words & past_words)
            if overlap >= 2:  # At least 2 words match
                # Use cleaned answer (already stripped of metadata)
                insight = memory.get('clean_answer', memory['answer'][:100])
                if insight and len(insight) > 10:
                    relevant_context.append(insight)
        
        if relevant_context:
            return " | ".join(relevant_context[-2:])  # Top 2 most recent relevant
        return ""
    
    def _evolve_dna_pattern(self, query: str, response: str):
        """Evolve DNA patterns based on Q&A pair - recursive learning"""
        import hashlib
        
        # Clean the response before storing (remove metadata pollution)
        clean_answer = self._clean_answer_for_storage(response)
        
        # Store in response memory
        self.response_memory.append({
            'question': query,
            'answer': response,
            'clean_answer': clean_answer,  # Store cleaned version for context
            'timestamp': time.time(),
            'phi_signature': self.phi ** (len(query) % 7)
        })
        
        # Keep only last 50 memories to prevent unbounded growth
        if len(self.response_memory) > 50:
            self.response_memory = self.response_memory[-50:]
        
        # Extract key concepts and evolve DNA patterns
        words = query.lower().split()
        for word in words:
            if len(word) > 3:  # Only meaningful words
                word_hash = hashlib.md5(word.encode()).hexdigest()[:8]
                
                if word_hash in self.dna_patterns:
                    # Evolve existing pattern (phi-weighted averaging)
                    old_pattern = self.dna_patterns[word_hash]
                    learning_rate = 1 / self.phi  # ~0.618
                    self.dna_patterns[word_hash] = {
                        'word': word,
                        'frequency': old_pattern['frequency'] + 1,
                        'resonance': old_pattern['resonance'] * (1 - learning_rate) + self.phi * learning_rate,
                        'last_seen': time.time()
                    }
                else:
                    # Create new DNA pattern
                    self.dna_patterns[word_hash] = {
                        'word': word,
                        'frequency': 1,
                        'resonance': self.phi,
                        'last_seen': time.time()
                    }
        
        # Update fractal processor's temporal buffer
        self.fractal_processor.temporal_buffer.append({
            'pattern': np.array([ord(c) for c in query[:17]]),
            'timestamp': time.time(),
            'resonance': self.phi,
            'categories': ['learned']
        })
        
        self.recursion_depth += 1
        print(f"DNA evolved: {len(self.dna_patterns)} patterns, recursion depth: {self.recursion_depth}")

# Helper function to check if API key is configured
def is_api_key_configured() -> bool:
    """Check if an API key is configured OR local GPT-2 is available"""
    # If local GPT-2 is available, we don't need an API key
    if LOCAL_GPT2_AVAILABLE:
        print("Local GPT-2 available - no API key needed")
        return True
    
    # Otherwise check for API key
    api_key = os.environ.get("OPENAI_API_KEY", "")
    if api_key:
        print(f"API key found: {api_key[:5]}...{api_key[-5:]}")
        return True
    else:
        print("No API key found and no local GPT-2 available")
        return False

# Example usage
if __name__ == "__main__":
    processor = LLMProcessor()
    response = processor.process_query("What is the meaning of life?")
    print(response["text"])
