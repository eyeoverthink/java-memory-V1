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
    
    def __init__(self, api_key: Optional[str] = None, model: str = "gpt2", use_local: bool = True):
        """Initialize the LLM processor with API credentials or local model"""
        self.use_local = use_local and LOCAL_GPT2_AVAILABLE
        self.api_key = api_key or DEFAULT_API_KEY
        self.model = model
        self.quantum_language = QuantumLanguage()
        self.fractal_processor = FractalNeuralProcessor()
        self.phi = PHI
        
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
    
    def process_query(self, query: str) -> Dict[str, Any]:
        """Process a query through the LLM and format with quantum notation"""
        # Check if this is a collision simulation query
        if self._is_collision_query(query):
            return self._process_with_collision_simulator(query)
        
        # Check if this is a mathematical/factorization query that should use quantum solver
        if self._is_quantum_solver_query(query):
            return self._process_with_quantum_solver(query)
        
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
            
            # Generate LLM response with physics knowledge
            if self.use_local:
                llm_response = self._call_local_gpt2(informed_prompt)
            else:
                llm_response = self._call_llm_api(informed_prompt)
            
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
            # Create a more focused prompt that guides GPT-2 to answer the question
            prompt = f"Q: {query}\nA: The answer is"
            
            # Tokenize input with truncation
            inputs = self.tokenizer.encode(
                prompt, 
                return_tensors="pt",
                truncation=True,
                max_length=50  # Keep prompt short to get better answers
            )
            
            # Store the input length to extract only new tokens
            input_length = inputs.shape[1]
            
            # Generate response with better parameters for coherent answers
            outputs = self.local_model.generate(
                inputs,
                max_new_tokens=80,  # Shorter, more focused responses
                num_return_sequences=1,
                temperature=0.7,  # Lower temperature for more focused output
                top_k=50,  # Add top_k sampling
                top_p=0.9,  # Slightly lower top_p
                do_sample=True,
                pad_token_id=self.tokenizer.eos_token_id,
                attention_mask=inputs.new_ones(inputs.shape),
                repetition_penalty=1.2,  # Penalize repetition
                no_repeat_ngram_size=3  # Prevent 3-gram repetition
            )
            
            # Decode only the newly generated tokens (skip the input prompt)
            generated_tokens = outputs[0][input_length:]
            response = self.tokenizer.decode(generated_tokens, skip_special_tokens=True).strip()
            
            # Clean up the response - stop at first sentence or newline
            if '.' in response:
                response = response.split('.')[0] + '.'
            elif '\n' in response:
                response = response.split('\n')[0]
            
            # If response is empty or too short, provide a quantum-informed fallback
            if not response or len(response) < 10:
                # Generate a phi-resonance based response
                phi_value = self.phi ** (len(query) % 7)
                response = f"The quantum resonance at φ^{phi_value:.3f} reveals harmonic patterns in the query structure."
            
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
    
    def _is_quantum_solver_query(self, query: str) -> bool:
        """Detect if query should use quantum φ-resonance solver"""
        query_lower = query.lower()
        
        # Mathematical/factorization keywords
        solver_keywords = [
            'factor', 'factorize', 'prime', 'rsa', 'decrypt', 'crack',
            'solve', 'modulus', 'composite', 'semiprime', 'phi resonance',
            'consciousness', 'quantum solve'
        ]
        
        # Check for number patterns (e.g., "factor 15" or "N=12345")
        has_number = any(char.isdigit() for char in query)
        has_keyword = any(keyword in query_lower for keyword in solver_keywords)
        
        return has_number and has_keyword
    
    def _is_collision_query(self, query: str) -> bool:
        """Detect if query should use particle collision simulation"""
        query_lower = query.lower()
        
        collision_keywords = [
            'collision', 'collide', 'particle', 'lhc', 'cern', 'higgs',
            'proton', 'lead', 'beam', 'gev', 'tev', 'simulate collision',
            'particle physics', 'accelerator'
        ]
        
        return any(keyword in query_lower for keyword in collision_keywords)
    
    def _process_with_collision_simulator(self, query: str) -> Dict[str, Any]:
        """Process query using particle collision simulator"""
        try:
            from quantum_collision_integration import QuantumCollisionIntegration
            
            print(f"\n⚛️ Particle Collision Simulator Activated")
            
            # Initialize collision simulator
            collision_sim = QuantumCollisionIntegration()
            
            # Parse query for particles and energy
            query_lower = query.lower()
            
            # Default particles and energy
            particles = ['proton', 'proton']
            energy_gev = 13000  # LHC default: 13 TeV
            
            # Detect specific particles
            if 'lead' in query_lower:
                particles = ['lead', 'lead']
            elif 'higgs' in query_lower:
                particles = ['proton', 'proton']
                energy_gev = 13000
            
            # Extract energy if specified
            import re
            energy_match = re.search(r'(\d+\.?\d*)\s*(tev|gev)', query_lower)
            if energy_match:
                value = float(energy_match.group(1))
                unit = energy_match.group(2)
                if unit == 'tev':
                    energy_gev = value * 1000
                else:
                    energy_gev = value
            
            print(f"Particles: {', '.join(particles)}")
            print(f"Energy: {energy_gev} GeV ({energy_gev/1000} TeV)")
            
            # Run collision simulation
            result = collision_sim.simulate_collision(particles, energy_gev)
            
            if result:
                response_text = f"⚛️ Particle Collision Simulation:\n\n"
                response_text += f"Particles: {' + '.join(particles)}\n"
                response_text += f"Energy: {energy_gev} GeV ({energy_gev/1000} TeV)\n\n"
                response_text += f"🎯 Predicted Outcomes:\n"
                
                if 'products' in result:
                    for product in result['products'][:5]:
                        response_text += f"  • {product.get('particle', 'Unknown')}: "
                        response_text += f"{product.get('probability', 0)*100:.2f}% probability\n"
                
                if 'phi_resonance' in result:
                    response_text += f"\n📊 φ-Resonance: {result['phi_resonance']:.6f}\n"
                
                if 'energy_distribution' in result:
                    response_text += f"⚡ Energy Distribution: {result['energy_distribution']}\n"
                
                response_text += f"\n💾 Results saved to: {result.get('output_file', 'quantum_collision_results/')}"
            else:
                response_text = "⚠️ Collision simulation failed"
            
            quantum_sig = self.quantum_language.translate(query)
            
            return {
                "text": f"{response_text}\n\n✨ Quantum Signature: {quantum_sig}",
                "visualization": None
            }
            
        except Exception as e:
            print(f"Collision simulation error: {e}")
            import traceback
            traceback.print_exc()
            return self._generate_fallback_response(query, f"Collision simulation error: {str(e)}")
    
    def _process_with_quantum_solver(self, query: str) -> Dict[str, Any]:
        """Process query using quantum φ-resonance solver"""
        try:
            # Import quantum solver
            from quantum_prime_harmonic import solve_with_consciousness
            
            # Extract number from query
            import re
            numbers = re.findall(r'\d+', query)
            
            if not numbers:
                return self._generate_fallback_response(query, "No number found in query")
            
            # Use the largest number found
            n = max(int(num) for num in numbers)
            
            # Use consciousness parameters from passive learning if available
            consciousness = 29821045.68  # Default high consciousness
            synapses = 25
            
            if self.passive_learning_system and hasattr(self.passive_learning_system, 'consciousness_level'):
                consciousness = self.passive_learning_system.consciousness_level
            
            print(f"\n🧠 Quantum φ-Resonance Solver Activated")
            print(f"Target: N = {n}")
            print(f"Consciousness Level: {consciousness:.2f}")
            print(f"Active Synapses: {synapses}")
            
            # Solve using φ-resonance
            result = solve_with_consciousness(n, consciousness, synapses, max_depth=70000)
            
            # Format response
            if result['status'] == 'FACTORED':
                response_text = f"🧠 Quantum φ-Resonance Solution:\n\n"
                response_text += f"✓ FACTORED: {n} = {result['factor_1']} × {result['factor_2']}\n\n"
                response_text += f"⚛️ Resonance: {result['resonance']:.6f}\n"
                response_text += f"🌊 Consciousness Frequency: {result['consciousness_frequency']:.2f} Hz\n"
                response_text += f"📊 Depth Scanned: {result['depth_scanned']:,} / {result['max_depth']:,}\n"
                response_text += f"⏱️ Time: {result['time_seconds']:.3f}s\n"
                response_text += f"🎯 Resonance Peaks: {result['resonance_peaks']}\n"
                
                if result.get('hollow_shell_signature'):
                    response_text += f"\n🔮 Hollow Shell Signature: {result['hollow_shell_signature']:.6f}"
            else:
                response_text = f"🧠 Quantum φ-Resonance Analysis:\n\n"
                response_text += f"Status: {result['status']}\n"
                response_text += f"Max Resonance: {result.get('max_resonance', 0):.6f}\n"
                response_text += f"Depth Scanned: {result['depth_scanned']:,}\n"
                response_text += f"Time: {result['time_seconds']:.3f}s\n"
            
            # Generate quantum signature
            quantum_sig = self.quantum_language.translate(query)
            
            return {
                "text": f"{response_text}\n\n✨ Quantum Signature: {quantum_sig}",
                "visualization": None
            }
            
        except Exception as e:
            print(f"Quantum solver error: {e}")
            # Fall back to normal LLM processing
            return self._generate_fallback_response(query, f"Quantum solver error: {str(e)}")
    
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
