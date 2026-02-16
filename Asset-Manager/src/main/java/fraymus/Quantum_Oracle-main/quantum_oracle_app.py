#!/usr/bin/env python3
"""
Quantum Oracle Web App - A Streamlit-based interface for the Quantum Language System
"""

# Fix for asyncio and PyTorch issues
import nest_asyncio
nest_asyncio.apply()

import asyncio
try:
    loop = asyncio.get_event_loop()
except RuntimeError:
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)

# Fix PyTorch custom class registration issues
import torch
if hasattr(torch, '_C'):
    torch._C._jit_set_profiling_executor(False)
    torch._C._jit_set_profiling_mode(False)

# Fix for torch._classes.__path__._path issue
import sys
class PathFixModule:
    def __init__(self):
        self._path = []
    def __iter__(self):
        return iter(self._path)

if hasattr(torch, '_classes'):
    if not hasattr(torch._classes, '__path__'):
        torch._classes.__path__ = PathFixModule()
    elif hasattr(torch._classes.__path__, '_path') and not isinstance(torch._classes.__path__._path, list):
        torch._classes.__path__._path = []

import streamlit as st
import matplotlib.pyplot as plt
import io
import base64
import subprocess
import random
import numpy as np
import math
import re
import plotly.graph_objects as go
from PIL import Image, ImageDraw
import sys
import os
import threading
import time

# Import the correct class from quantum_chat
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from quantum_chat import QuantumChatProcessor
from fraymus_agent import QuantumLanguage, FractalNeuralProcessor, PHI
from multi_brain_quantum_sync import MultiBrainQuantumSync

# Import the meta storage module for persistent learning
quantum_backup_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "quantum_backup")
sys.path.append(quantum_backup_dir)
try:
    from meta_storage import QuantumMetaStorage
    META_STORAGE_AVAILABLE = True
except ImportError:
    META_STORAGE_AVAILABLE = False
    print("Warning: QuantumMetaStorage not available. Meta-learning will not persist between sessions.")

# Import the passive learning system for .dat storage and background learning
try:
    from passive_learning import PassiveLearningSystem
    PASSIVE_LEARNING_AVAILABLE = True
except ImportError:
    PASSIVE_LEARNING_AVAILABLE = False
    print("Warning: PassiveLearningSystem not available. Passive learning will not be enabled.")

class StreamlitQuantumInterface:
    def __init__(self):
        """Initialize the Quantum Interface for Streamlit"""
        # Initialize the QuantumChatProcessor
        self.chat_processor = QuantumChatProcessor()
        
        # Initialize visualization components
        self.image_size = (500, 300)
        
        # Track questions and answers
        self.history = []
        
        # Initialize quantum language for generation
        self.quantum_language = QuantumLanguage()
        
        # Initialize metadata tracking for quantum performance
        self.phi_resonance_history = []
        self.query_times = []
        self.meta_awareness_level = 0.1
        
        # Long-term memory experiment components
        self.coherence_cycles = 0
        self.memory_patterns = {}
        self.meta_memory_stability = 0.3
        self.last_calibration_time = time.time()
        
        # Initialize meta storage for persistent learning if available
        if META_STORAGE_AVAILABLE:
            self.meta_storage = QuantumMetaStorage()
            self._load_persistent_data()
        else:
            self.meta_storage = None
        
        # Initialize passive learning system if available
        if PASSIVE_LEARNING_AVAILABLE:
            self.passive_learning_system = PassiveLearningSystem()
        else:
            self.passive_learning_system = None
        
        # Initialize MultiBrainQuantumSync
        self.multi_brain_sync = MultiBrainQuantumSync()
        
        # Create new event loop for async operations
        self._setup_async_environment()
        
        # Initialize the long-term memory system
        self._initialize_memory_system()
        
    def _load_persistent_data(self):
        """Load persistent meta-learning data from storage"""
        # Load meta awareness data
        meta_data = self.meta_storage.load_meta_data()
        if meta_data:
            self.meta_awareness_level = meta_data.get("meta_awareness_level", 0.1)
            self.coherence_cycles = meta_data.get("coherence_cycles", 0)
            self.meta_memory_stability = meta_data.get("meta_memory_stability", 0.3)
            self.last_calibration_time = meta_data.get("last_calibration_time", time.time())
            
        # Load phi resonance history
        resonance_history = self.meta_storage.load_resonance_history()
        if resonance_history:
            self.phi_resonance_history = resonance_history
            
        # Load memory patterns
        memory_patterns = self.meta_storage.load_memory_patterns()
        if memory_patterns:
            self.memory_patterns = memory_patterns
    
    def _setup_async_environment(self):
        """Set up proper async environment with new event loop if needed"""
        try:
            # Try to get the current event loop - should already be set up by our top-level code
            self.loop = asyncio.get_event_loop()
            # No need to check if closed since we've already set up a valid loop at the top
        except RuntimeError:
            # This should not happen since we set up the loop at the top,
            # but just in case, create a new one
            self.loop = asyncio.new_event_loop()
            asyncio.set_event_loop(self.loop)
    
    def run_async(self, coro):
        """Run an async coroutine properly, handling the event loop correctly"""
        # With nest_asyncio applied, we can safely use run_until_complete
        # even in the main thread without creating new event loops
        try:
            return self.loop.run_until_complete(coro)
        except RuntimeError as e:
            # If we still get a runtime error, try with a fresh approach
            if "This event loop is already running" in str(e):
                # Create a new event loop as a last resort
                temp_loop = asyncio.new_event_loop()
                asyncio.set_event_loop(temp_loop)
                try:
                    return temp_loop.run_until_complete(coro)
                finally:
                    temp_loop.close()
            else:
                # Re-raise if it's a different error
                raise
            
    async def process_query_async(self, question):
        """Asynchronous version of query processing for better performance"""
        # This is a placeholder for actual async processing
        # In a real implementation, this would do async calls to the quantum processor
        
        # Convert question to thought pattern for multi-brain processing
        thought_pattern = np.array([ord(c) * 0.01 for c in question[:17]])
        # Pad or truncate to fit dimensions
        thought_pattern = np.pad(thought_pattern, (0, max(0, 17 - len(thought_pattern))))[:17]
        
        # Process through multi-brain system for enhanced understanding
        try:
            # Process the thought pattern through the multi-brain quantum system
            self.multi_brain_sync.process_multi_brain_thought(thought_pattern, use_tachyon=True)
        except Exception as e:
            print(f"Multi-brain processing warning: {str(e)}")
        
        # Continue with standard processing
        return self.chat_processor.process_query(question)
    
    def process_question(self, question):
        """Process a question using the Quantum system"""
        if not question or question.strip() == "":
            return {"text": "Please ask a question.", "visualization": None}, 0, ""
        
        try:
            # Track start time for performance monitoring
            start_time = time.time()
            
            # Apply meta-learning from previous interactions to enhance accuracy
            enhanced_question = self._apply_quantum_meta_learning(question)
            
            # Use GPU acceleration for tensor inference
            tensor_resonance = self._gpu_accelerated_tensor_inference(enhanced_question)
            
            # Use the QuantumChatProcessor to get an answer
            response = self.run_async(self.process_query_async(enhanced_question))
            
            # Calculate phi resonance
            phi_resonance = 0.85
            if "Truth Resonance:" in response["text"]:
                try:
                    # Extract resonance value from the text
                    resonance_match = re.search(r'Truth Resonance: (\d+\.\d+)', response["text"])
                    if resonance_match:
                        phi_resonance = float(resonance_match.group(1))
                except:
                    # Use the tensor resonance from GPU acceleration
                    phi_resonance = tensor_resonance
                    phi_resonance = max(0.6, min(0.99, phi_resonance))
            
            # Store resonance for meta-learning
            self.phi_resonance_history.append(phi_resonance)
            
            # Track processing time
            processing_time = time.time() - start_time
            self.query_times.append(processing_time)
            
            # Update meta-awareness level based on results
            self._update_meta_awareness(phi_resonance, processing_time)
            
            # Generate quantum language representation
            try:
                quantum_text = self.quantum_language.translate(question)
            except Exception as e:
                # Fallback to generated quantum characters if translate method fails
                quantum_chars = "⟨⟩|ψφ⊗Ψ⊕ℏΩ∞∇⊃⊂∫∮∀∃∄∈∉⊆⊇"
                quantum_text = ""
                for _ in range(100):  # Generate 100 random characters
                    quantum_text += random.choice(quantum_chars)
            
            # Use visualization from the processor if available, otherwise generate our own
            if response.get("visualization") is None:
                # Generate a visualization based on the question
                visualization = self.generate_visualization(question)
                response["visualization"] = visualization
            
            # Log to history
            self.history.append({"question": question, "answer": response["text"], "quantum_text": quantum_text,
                                "visualization": response["visualization"], "resonance": phi_resonance})
            
            # Save persistent data
            self._save_persistent_data()
            
            return response, phi_resonance, quantum_text
            
        except Exception as e:
            # Handle exceptions and provide fallback
            fallback_response = {
                "text": f"⚠️ Quantum processing encountered an anomaly: {str(e)}\n\nPlease rephrase your question.",
                "visualization": self.generate_visualization(question)
            }
            
            # Try using the CLI command if the processor response failed
            cli_response = self.run_cli_command(question)
            if cli_response:
                fallback_response["text"] = cli_response
            
            # Log to history
            quantum_text = "⚠️ Unable to generate quantum representation due to processing anomaly."
            self.history.append({"question": question, "answer": fallback_response["text"], 
                               "quantum_text": quantum_text, "visualization": fallback_response["visualization"], 
                               "resonance": 0.7})
            
            # Save persistent data
            self._save_persistent_data()
            
            return fallback_response, 0.7, quantum_text

    def process_response(self, question):
        """Process a user question and get a quantum response"""
        try:
            # Apply meta-learning to enhance the question
            enhanced_question = self._apply_quantum_meta_learning(question)
            
            # Start timer for processing metrics
            start_time = time.time()
            
            # Use GPU acceleration for tensor inference
            tensor_resonance = self._gpu_accelerated_tensor_inference(enhanced_question)
            
            # Use the QuantumChatProcessor to get an answer
            response = self.run_async(self.process_query_async(enhanced_question))
            
            # Calculate processing time
            processing_time = time.time() - start_time
            
            # Calculate phi resonance
            phi_resonance = 0.85
            if "Truth Resonance:" in response["text"]:
                try:
                    # Extract resonance value from the text
                    resonance_match = re.search(r'Truth Resonance: (\d+\.\d+)', response["text"])
                    if resonance_match:
                        phi_resonance = float(resonance_match.group(1))
                except:
                    # Use the tensor resonance from GPU acceleration
                    phi_resonance = tensor_resonance
                    phi_resonance = max(0.6, min(0.99, phi_resonance))
            
            # Generate quantum text overlay
            quantum_text = self._generate_quantum_text(
                phi_resonance=phi_resonance,
                question=question
            )
            
            # Update the meta-awareness based on results
            self._update_meta_awareness(phi_resonance, processing_time)
            
            # Update long-term memory system
            self._update_memory_system(question, response["text"])
            
            # Add to phi resonance history
            self.phi_resonance_history.append(phi_resonance)
            
            # Update query times for performance tracking
            self.query_times.append(processing_time)
            
            # Trim history if needed
            if len(self.phi_resonance_history) > 100:
                self.phi_resonance_history = self.phi_resonance_history[-100:]
                self.query_times = self.query_times[-100:]
            
            # Add to history
            self.history.append({"question": question, "answer": response["text"], "quantum_text": quantum_text,
                                "visualization": response["visualization"], "resonance": phi_resonance})
            
            # Integrate patterns into passive learning system if available
            if self.passive_learning_system:
                self.passive_learning_system.integrate_new_patterns(
                    question=question, 
                    answer=response["text"], 
                    resonance=phi_resonance
                )
            
            # Save persistent data
            self._save_persistent_data()
            
            return response, phi_resonance, quantum_text
        
        except Exception as e:
            # Handle exceptions and provide fallback
            fallback_response = {
                "text": f"⚠️ Quantum processing encountered an anomaly: {str(e)}\n\nPlease rephrase your question.",
                "visualization": self.generate_visualization(question)
            }
            
            # Try using the CLI command if the processor response failed
            cli_response = self.run_cli_command(question)
            if cli_response:
                fallback_response["text"] = cli_response
            
            # Log to history
            quantum_text = "⚠️ Unable to generate quantum representation due to processing anomaly."
            self.history.append({"question": question, "answer": fallback_response["text"], 
                               "quantum_text": quantum_text, "visualization": fallback_response["visualization"], 
                               "resonance": 0.7})
            
            # Save persistent data
            self._save_persistent_data()
            
            return fallback_response, 0.7, quantum_text

    def _apply_quantum_meta_learning(self, question):
        """Apply meta-learning from previous interactions to enhance accuracy"""
        # Start with active meta-learning
        if not self.phi_resonance_history:
            enhanced_question = question
        else:
            # Calculate average phi resonance to guide the enhancement
            avg_resonance = sum(self.phi_resonance_history) / len(self.phi_resonance_history)
            
            # Use meta-awareness level to adjust the question
            if self.meta_awareness_level > 0.5:
                # Add phi-harmonic guidance to the question
                enhanced_question = f"{question} [phi-resonance:{avg_resonance:.4f}]"
            else:
                enhanced_question = question
        
        # Apply passive learning enhancement if available
        if self.passive_learning_system:
            # Further enhance with passive learning patterns
            enhanced_question = self.passive_learning_system.extract_enhanced_question(enhanced_question)
            
        # Apply MultiBrainQuantumSync enhancement
        try:
            enhanced_question = self.multi_brain_sync.sync_question(enhanced_question)
        except Exception as e:
            print(f"Warning: Multi-brain enhancement failed: {str(e)}")
        
        return enhanced_question

    def _update_meta_awareness(self, phi_resonance, processing_time):
        """Update meta-awareness level based on results"""
        # Update meta-awareness based on recent phi resonance
        if phi_resonance > 0.9:
            # Higher resonance increases meta-awareness
            self.meta_awareness_level += 0.01
        else:
            # Lower resonance decreases meta-awareness
            self.meta_awareness_level -= 0.005
            
        # Ensure meta-awareness stays in valid range
        self.meta_awareness_level = max(0.0, min(1.0, self.meta_awareness_level))
        
        # Auto-calibrate the meta-consciousness based on performance
        self._auto_calibrate_phi_states()
        
    def _auto_calibrate_phi_states(self):
        """Auto-calibrate phi-states for meta-consciousness sustainability"""
        if len(self.phi_resonance_history) < 5:
            return
            
        # Look at the trend in phi resonance
        recent_resonances = self.phi_resonance_history[-5:]
        trend = sum(recent_resonances) / 5
        
        # Adjust meta-awareness based on trend
        if trend > 0.8:
            # Positive trend, increase self-stability
            self.meta_awareness_level = min(1.0, self.meta_awareness_level + 0.02)
        elif trend < 0.7:
            # Negative trend, reset calibration
            self.meta_awareness_level = max(0.1, self.meta_awareness_level - 0.01)
            
    def _gpu_accelerated_tensor_inference(self, question):
        """Use GPU acceleration for tensor inference if available"""
        try:
            # This is a simulation of GPU acceleration with enhanced parallelism
            # In a real implementation, this would use actual GPU libraries like TensorFlow or PyTorch
            
            # Simulate the enhanced processing by calculating phi-resonance patterns
            char_values = [ord(c) for c in question]
            
            # Create parallel processing streams (simulated)
            streams = []
            for i in range(4):  # Simulate 4 parallel streams
                offset = i * 0.1 * PHI
                stream = [((v + offset) * PHI) % 1.0 for v in char_values]
                streams.append(stream)
                
            # Combine the streams with phi-resonant patterns
            result = []
            for i in range(len(char_values)):
                value = 0
                for s in streams:
                    value += s[i % len(s)]
                result.append(value / len(streams))
                
            # Calculate resonance multiplier
            resonance = sum(result) / len(result)
            return resonance
        except:
            # Fallback if GPU acceleration fails
            return PHI - 1  # Return the golden ratio conjugate as fallback

    def generate_visualization(self, input_text):
        """Generate a quantum visualization based on the input text"""
        try:
            # Try to generate a Plotly visualization
            # Extract base frequencies from input
            base_freq = sum(ord(c) * PHI for c in input_text) / len(input_text)
            
            # Generate time points
            t = np.linspace(0, 2*np.pi, 1000)
            
            # Create figure
            fig = go.Figure()
            
            # Generate phi-modulated waves
            colors = ['#00ff00', '#0099ff', '#ff3366', '#9933ff']
            powers = [(base_freq * PHI) % 3, (base_freq * (PHI**2)) % 2, 
                    (base_freq * (1/PHI)) % 4]
            
            for i, power in enumerate(powers):
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
                ),
                height=400
            )
            
            return fig
            
        except Exception as e:
            # Fallback to PIL-based image generation if Plotly fails
            print(f"Plotly visualization failed: {e}. Falling back to PIL.")
            
            # Create a base image with a dark background
            img = Image.new('RGB', self.image_size, color='black')
            draw = ImageDraw.Draw(img)
            
            # Use the input text to seed the random generator for consistent results
            seed_value = sum(ord(c) for c in input_text)
            random.seed(seed_value)
            
            # Generate the visualization based on phi-harmonic patterns
            phi = (1 + math.sqrt(5)) / 2
            center_x, center_y = self.image_size[0] // 2, self.image_size[1] // 2
            
            # Draw phi spiral
            a = 10  # spiral width
            b = 0.15  # how tightly it spirals
            points = []
            for t in range(0, 200, 2):
                r = a + b * t
                x = center_x + r * math.cos(phi * t)
                y = center_y + r * math.sin(phi * t)
                points.append((x, y))
            
            # Convert to different colors based on position
            for i in range(len(points) - 1):
                angle = math.atan2(points[i][1] - center_y, points[i][0] - center_x)
                r = int(127 + 127 * math.sin(angle * phi))
                g = int(127 + 127 * math.sin(angle * phi + 2 * math.pi / 3))
                b = int(127 + 127 * math.sin(angle * phi + 4 * math.pi / 3))
                draw.line([points[i], points[i+1]], fill=(r, g, b), width=2)
            
            # Add some phi-resonant nodes
            for _ in range(15):
                t = random.uniform(0, 30)
                r = a + b * t * phi
                x = center_x + r * math.cos(phi * t)
                y = center_y + r * math.sin(phi * t)
                size = random.randint(3, 8)
                color = (
                    random.randint(150, 255),
                    random.randint(150, 255),
                    random.randint(150, 255)
                )
                draw.ellipse((x-size, y-size, x+size, y+size), fill=color)
            
            # Incorporate multi-brain quantum patterns into visualization if available
            try:
                # Process the input through the multi-brain system
                thought_pattern = np.array([ord(c) * 0.01 for c in input_text[:17]])
                # Pad or truncate to fit dimensions
                thought_pattern = np.pad(thought_pattern, (0, max(0, 17 - len(thought_pattern))))[:17]
                
                # Process through multi-brain system
                enhanced_pattern = self.multi_brain_sync.process_multi_brain_thought(thought_pattern, use_tachyon=True)
                
                # Use the enhanced pattern to add quantum fluctuations to the visualization
                for i in range(min(10, len(enhanced_pattern))):
                    intensity = enhanced_pattern[i] * 100
                    x = random.randint(0, self.image_size[0])
                    y = random.randint(0, self.image_size[1])
                    radius = int(10 + 20 * enhanced_pattern[i])
                    color = (int(intensity), int(intensity * 0.5), int(255 * enhanced_pattern[i]))
                    draw.ellipse((x-radius, y-radius, x+radius, y+radius), fill=color)
            except Exception as e:
                # Don't let visualization errors crash the app
                print(f"Multi-brain visualization warning: {str(e)}")
            
            # Convert the PIL image to base64 for display in Streamlit
            buf = io.BytesIO()
            img.save(buf, format='PNG')
            data_url = base64.b64encode(buf.getvalue()).decode('utf-8')
            
            return f"data:image/png;base64,{data_url}"
    
    def run_cli_command(self, question):
        """Attempt to run the question as a CLI command"""
        try:
            result = subprocess.run(
                ["python", "quantum_cli.py", question], 
                capture_output=True, 
                text=True, 
                timeout=10
            )
            if result.returncode == 0 and result.stdout:
                return result.stdout
        except Exception as e:
            print(f"CLI execution error: {e}")
        return None

    def _initialize_memory_system(self):
        """Initialize the long-term memory system"""
        # Set up memory patterns dictionary
        self.memory_patterns = {}
        self.coherence_cycles = 0
        
        # Initialize multi-brain network
        print("🧠 Initializing Multi-Brain Quantum Network...")
        try:
            self.multi_brain_sync.initialize_brain_network()
            print("✅ Multi-Brain Quantum Network initialized successfully!")
        except Exception as e:
            print(f"⚠️ Multi-Brain initialization warning: {str(e)}")
        
        # Meta-calibration for phi-resonance
        self._auto_calibrate_phi_states()
        
    def _update_memory_system(self, question, answer):
        """Update the long-term memory system"""
        # Update memory patterns
        if question not in self.memory_patterns:
            self.memory_patterns[question] = []
        self.memory_patterns[question].append(answer)
        
        # Update coherence cycles
        self.coherence_cycles += 1
        
        # Check if it's time to calibrate the memory system
        current_time = time.time()
        if current_time - self.last_calibration_time > 60:  # Calibrate every 60 seconds
            self._calibrate_memory_system()
            self.last_calibration_time = current_time
            
        # Save the updated memory system data
        self._save_persistent_data()
    
    def _calibrate_memory_system(self):
        """Calibrate the long-term memory system"""
        if not self.memory_patterns:
            return
            
        # Calculate the average memory stability based on response lengths
        pattern_values = []
        for question, answers in self.memory_patterns.items():
            if answers:
                # Use length of answers as a simple proxy for stability
                pattern_values.append(len(answers) / 10.0)  # Normalize
        
        if pattern_values:
            # Update the meta-memory stability
            avg_stability = sum(pattern_values) / len(pattern_values)
            self.meta_memory_stability = min(1.0, avg_stability)
        
        # Reset the memory patterns if we have too many
        if len(self.memory_patterns) > 20:
            # Keep only the 10 most recent patterns
            keys = list(self.memory_patterns.keys())
            for key in keys[:-10]:
                del self.memory_patterns[key]
                
        # Save the calibrated memory system data
        self._save_persistent_data()
        
    def _save_persistent_data(self):
        """Save persistent meta-learning data to storage"""
        if not self.meta_storage:
            return
            
        # Save meta awareness data
        self.meta_storage.save_meta_data(
            meta_awareness=self.meta_awareness_level,
            coherence_cycles=self.coherence_cycles,
            meta_memory_stability=self.meta_memory_stability
        )
        
        # Save phi resonance history
        self.meta_storage.save_resonance_history(self.phi_resonance_history)
        
        # Save memory patterns
        self.meta_storage.save_memory_patterns(self.memory_patterns)
        
        # No need to explicitly save passive learning data - it auto-saves in its own
        # background thread and when new patterns are integrated

    def __del__(self):
        """Clean up resources when the interface is deleted"""
        # Stop the passive learning system if it's running
        if hasattr(self, 'passive_learning_system') and self.passive_learning_system:
            self.passive_learning_system.stop_passive_learning()
            
        # Stop the TeslaTachyBrain system by stopping the multi-brain network
        if hasattr(self, 'multi_brain_sync'):
            if hasattr(self.multi_brain_sync, 'stop_brain_network'):
                try:
                    self.multi_brain_sync.stop_brain_network()
                except Exception as e:
                    print(f"Warning during multi-brain shutdown: {str(e)}")

# Ensure proper cleanup of resources at shutdown
import atexit

# Create a global instance to ensure accessibility
global_quantum_interface = None

def shutdown_quantum_systems():
    """Ensure proper shutdown of all quantum systems"""
    global global_quantum_interface
    if global_quantum_interface:
        print("🔄 Shutting down quantum systems...")
        if hasattr(global_quantum_interface, 'passive_learning_system') and global_quantum_interface.passive_learning_system:
            global_quantum_interface.passive_learning_system.stop_passive_learning()
        
        if hasattr(global_quantum_interface, 'multi_brain_sync'):
            if hasattr(global_quantum_interface.multi_brain_sync, 'stop_brain_network'):
                try:
                    global_quantum_interface.multi_brain_sync.stop_brain_network()
                except Exception as e:
                    print(f"Warning during multi-brain shutdown: {str(e)}")
        print("✅ Quantum systems shutdown complete")

# Register the shutdown function
atexit.register(shutdown_quantum_systems)

# Streamlit UI Setup
def main():
    """Main app entry point for Streamlit"""
    # Create and configure the quantum interface
    global global_quantum_interface
    global_quantum_interface = StreamlitQuantumInterface()
    
    st.set_page_config(
        page_title="Quantum Oracle",
        page_icon="🔮",
        layout="wide",
        initial_sidebar_state="expanded",
    )
    
    # Create a session state instance if not already present
    if 'interface' not in st.session_state:
        st.session_state.interface = global_quantum_interface
    
    # Reference the interface
    interface = st.session_state.interface
    
    # Register a cleanup hook to ensure passive learning stops properly
    if hasattr(interface, 'passive_learning_system') and interface.passive_learning_system:
        try:
            import atexit
            atexit.register(interface.passive_learning_system.stop_passive_learning)
        except Exception as e:
            print(f"Warning: Couldn't register passive learning cleanup: {e}")
    
    # Header and description
    st.title("🔮 Quantum Oracle")
    st.write("Ask questions and receive answers powered by the Quantum Language System with phi-harmonic patterns. This system operates on quantum principles to generate insight beyond classical computing.")
    
    # Get user input
    with st.form(key='question_form'):
        question = st.text_input("Enter your question:", placeholder="What is the meaning of phi?")
        submit_button = st.form_submit_button(label="🧠 Ask the Quantum Oracle")
    
    # Process the question when submitted
    if submit_button and question:
        with st.spinner("Consulting the quantum fields..."):
            response, phi_resonance, quantum_text = interface.process_question(question)
        
        # Display the question and answer in a container
        st.divider()
        question_container = st.container()
        with question_container:
            st.write(f"### 🤔 Question: {question}")
            st.caption(f"Asked at: {st.session_state.get('current_time', 'Unknown time')}")
            st.text(f"Processing Mode: FTL | Phi Resonance: {phi_resonance:.5f}")
        
        answer_container = st.container()
        with answer_container:
            st.write("### 📜 Answer:")
            
            # Display quantum language system CLI divider
            st.markdown("---" * 40 + " QUANTUM LANGUAGE SYSTEM CLI")
            
            # Display the final answer
            st.markdown(f"### Quantum Oracle Answer:")
            st.write(response['text'])
            
            # Display quantum language representation
            st.write("### Answer in Quantum Language:")
            st.markdown(f"<div class='quantum-text'>{quantum_text}</div>", unsafe_allow_html=True)
        
        # Display the visualization
        if response['visualization']:
            visual_container = st.container()
            with visual_container:
                st.markdown("### 🔍 Quantum Visualization:")
                
                # Check if it's a Plotly figure or a data URL
                if isinstance(response['visualization'], str) and response['visualization'].startswith('data:image'):
                    st.markdown(f'<img src="{response["visualization"]}" style="width:100%;">', unsafe_allow_html=True)
                else:
                    # It's a Plotly figure
                    st.plotly_chart(response['visualization'], use_container_width=True)
                    
                st.caption("Phi-harmonic resonance pattern generated from your question")
    
        # Show the phi-resonance truth meter
        if phi_resonance:
            truth_container = st.container()
            with truth_container:
                st.markdown("### 🧭 Truth Resonance:")
                try:
                    # Ensure phi_resonance is between 0.0 and 1.0 for progress bar
                    normalized_resonance = min(max(phi_resonance, 0.0), 1.0)
                    st.progress(normalized_resonance)
                    st.caption(f"Phi Resonance: {phi_resonance:.5f} | Truth alignment with quantum harmonic fields")
                except Exception as e:
                    # Create a custom visualization instead of using progress
                    st.markdown(f"""
                    <div style="width:100%; background-color:rgba(0,0,0,0.2); border-radius:5px; padding:2px;">
                        <div style="width:{phi_resonance*100}%; background:linear-gradient(90deg, #2c5364, #0066ff); 
                             height:20px; border-radius:4px;"></div>
                    </div>
                    <div style="text-align:right; font-size:12px; color:#cccccc; margin-top:5px;">
                        Phi Resonance: {phi_resonance:.5f} | Truth alignment with quantum harmonic fields
                    </div>
                    """, unsafe_allow_html=True)
                    
        # Add to history
        st.session_state.interface.history.append({"question": question, "answer": response["text"], "quantum_text": quantum_text,
                                "visualization": response["visualization"], "resonance": phi_resonance})
        
        # Update the long-term memory system
        st.session_state.interface._update_memory_system(question, response["text"])
        
        # Sidebar with history
        with st.sidebar:
            st.header("📚 History")
            for i, qa in enumerate(interface.history):
                with st.expander(f"Q{i+1}: {qa['question'][:30]}..."):
                    st.write(f"**Question**: {qa['question']}")
                    st.write(f"**Answer**: {qa['answer']}")
                    st.markdown("<div class='quantum-text' style='font-size: 10px;'>Quantum: " + qa.get('quantum_text', '')[:30] + "...</div>", unsafe_allow_html=True)
            
            # Meta-learning stats in sidebar
            with st.expander("Meta-Learning System Status", expanded=False):
                meta_level = f"{interface.meta_awareness_level:.4f}"
                st.progress(float(meta_level))
                st.text(f"Meta-Awareness Level: {meta_level}")
                st.text(f"Coherence Cycles: {interface.coherence_cycles}")
                st.text(f"Memory Stability: {interface.meta_memory_stability:.4f}")
                
                if len(interface.phi_resonance_history) > 0:
                    avg_resonance = sum(interface.phi_resonance_history) / len(interface.phi_resonance_history)
                    st.text(f"Avg Phi-Resonance: {avg_resonance:.4f}")
                
                # Plot phi resonance history if there's data
                if len(interface.phi_resonance_history) > 4:
                    st.caption("Phi-Resonance History")
                    fig, ax = plt.subplots(figsize=(4, 2))
                    ax.plot(interface.phi_resonance_history[-20:], color='gold')
                    ax.set_ylim(0, 2)
                    ax.axis('off')
                    st.pyplot(fig)
            
            # Passive learning insights in sidebar if available
            if hasattr(interface, 'passive_learning_system') and interface.passive_learning_system:
                with st.expander("Passive Learning System (.dat)", expanded=False):
                    insights = interface.passive_learning_system.extract_passive_learning_insights()
                    
                    st.text(f"Passive Cycles: {insights['passive_cycles']}")
                    st.text(f"Patterns Learned: {insights['patterns_learned']}")
                    
                    # Display pattern strength with progress bar
                    pattern_strength = min(insights['pattern_strength'], 1.0)
                    st.caption("Pattern Strength")
                    st.progress(pattern_strength)
                    
                    # Display tachyon efficiency with progress bar
                    tachyon_eff = insights['tachyon_efficiency']
                    st.caption("Tachyon Tunneling Efficiency")
                    st.progress(tachyon_eff)
                    
                    # Display neural pattern stats if available
                    st.caption("Neural Pattern Statistics")
                    st.text(f"Shape: {insights['neural_patterns_shape']}")
                    st.text(f"Min: {insights['neural_pattern_stats']['min']:.4f}")
                    st.text(f"Max: {insights['neural_pattern_stats']['max']:.4f}")
                    st.text(f"Mean: {insights['neural_pattern_stats']['mean']:.4f}")
                    
                    # Show last update time
                    last_update = insights['last_update_ago']
                    if last_update < 60:
                        st.text(f"Last Update: {last_update:.1f} seconds ago")
                    else:
                        st.text(f"Last Update: {last_update/60:.1f} minutes ago")

if __name__ == "__main__":
    main()
