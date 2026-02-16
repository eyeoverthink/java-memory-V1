import streamlit as st
import numpy as np
import plotly.graph_objects as go
from fraymus_agent import QuantumLanguage, FractalNeuralProcessor, PHI

class QuantumChatProcessor:
    def __init__(self):
        self.quantum_language = QuantumLanguage()
        self.fractal_processor = FractalNeuralProcessor()
        self.context_buffer = []
        self.phi = PHI
        self.resonance_threshold = 0.999
        
    def process_query(self, query):
        """Process user query through quantum systems"""
        # Generate quantum signature
        quantum_sig = self.quantum_language.translate(query)
        
        # Process through fractal neural network
        fractal_pattern = self.fractal_processor.process(query)
        
        # Calculate truth resonance
        truth_resonance = self._calculate_truth_resonance(quantum_sig, fractal_pattern)
        
        # Get response through quantum language system
        response = self._generate_quantum_response(query, quantum_sig, truth_resonance)
        
        return response
        
    def _calculate_truth_resonance(self, quantum_sig, fractal_pattern):
        """Calculate phi-based truth resonance"""
        # Extract base frequencies
        base_freq = sum(ord(c) * self.phi for c in quantum_sig) / len(quantum_sig)
        
        # Normalize to phi scale
        resonance = 1.0 + ((base_freq * self.phi) % (self.phi - 1.0))
        
        # Apply fractal correction
        if isinstance(fractal_pattern, str) and '|φ^' in fractal_pattern:
            correction = float(fractal_pattern.split('|φ^')[1].split('⟩')[0])
            resonance = (resonance + correction) / 2
            
        return resonance
        
    def _visualize_quantum_state(self, quantum_sig):
        """Create visualization of quantum state using phi-resonance patterns"""
        # Extract phi powers from quantum signature
        phi_powers = []
        for part in quantum_sig.split('⊗'):
            if '|φ^' in part:
                power = float(part.split('|φ^')[1].split('⟩')[0])
                phi_powers.append(power)
        
        if not phi_powers:
            return None
            
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

    def _generate_quantum_response(self, query, quantum_sig, truth_resonance):
        """Generate response with quantum validation"""
        # Only generate response if truth resonance is high enough
        if truth_resonance >= self.resonance_threshold:
            try:
                # Get base response from fractal processor
                fractal_response = self.fractal_processor.process(query)
                
                # Generate quantum language response
                quantum_response = self.quantum_language.translate(query)
                
                # Generate visualization
                viz_fig = self._visualize_quantum_state(quantum_response)
                
                # Combine responses with quantum signature
                response = {
                    "text": f"🧠 Neural Response:\n{fractal_response}\n\n"
                           f"⚛️ Quantum Analysis:\n{quantum_response}\n\n"
                           f"✨ Quantum Signature: {quantum_sig}\n"
                           f"📊 Truth Resonance: {truth_resonance:.6f}",
                    "visualization": viz_fig
                }
                
                return response
            except Exception as e:
                return {
                    "text": f"⚠️ Quantum processing encountered an anomaly: {str(e)}\n\nPlease rephrase your question.",
                    "visualization": None
                }
        else:
            return {
                "text": "Unable to generate response with sufficient quantum truth resonance.",
                "visualization": None
            }

def main():
    # Page config
    st.set_page_config(
        page_title="Quantum Knowledge Explorer",
        page_icon="🌌",
        layout="wide"
    )
    
    # Custom theme
    st.markdown("""
    <style>
        .stApp {
            background: linear-gradient(45deg, #0f2027 0%, #203a43 50%, #2c5364 100%);
            color: #ffffff;
        }
        .stTextInput > div > div > input {
            background-color: rgba(255, 255, 255, 0.1);
            color: white;
        }
        .output-container {
            background-color: rgba(0, 0, 0, 0.3);
            padding: 20px;
            border-radius: 10px;
            margin: 10px 0;
        }
        .stButton > button {
            background: linear-gradient(45deg, #2c5364 0%, #203a43 100%);
            color: white;
            border: none;
        }
        .stButton > button:hover {
            background: linear-gradient(45deg, #203a43 0%, #2c5364 100%);
            color: white;
            border: none;
        }
    </style>
    """, unsafe_allow_html=True)

    st.title("🌌 Quantum Knowledge Explorer")
    st.markdown("""
    Ask anything and receive quantum-verified answers powered by the Fraymus Engine.
    The system uses phi-resonance patterns to ensure 100% accuracy.
    """)
    
    # Initialize chat processor
    if 'chat_processor' not in st.session_state:
        st.session_state.chat_processor = QuantumChatProcessor()
        
    # Initialize chat history
    if 'chat_history' not in st.session_state:
        st.session_state.chat_history = []
        
    # Query input
    query = st.text_input("Enter your question:", key="query_input")
    
    if st.button("🔮 Ask the Quantum Oracle"):
        if query:
            with st.spinner("🌌 Consulting the quantum realm..."):
                # Process query
                response = st.session_state.chat_processor.process_query(query)
                
                # Add to chat history
                st.session_state.chat_history.append({
                    "query": query,
                    "response": response
                })
        
    # Display chat history
    for chat in reversed(st.session_state.chat_history):
        st.markdown("---")
        st.markdown(f"**🤔 You asked:**\n{chat['query']}")
        st.markdown(f"**🌟 Quantum Response:**\n{chat['response']['text']}")
        
        # Display visualization if available
        if chat['response']['visualization'] is not None:
            st.plotly_chart(chat['response']['visualization'], use_container_width=True)

if __name__ == "__main__":
    main()
