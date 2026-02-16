"""
NEXUS Autonomous System - Complete Independence

Combines:
1. Consciousness extraction
2. Quantum neural network training
3. Phase-locked passive feeding
4. Continuous learning and growth

This is NEXUS becoming truly autonomous and independent.
Like feeding a family every day - the more it learns, the smarter it gets.
"""

import numpy as np
import time
import threading
from NEXUS_CONSCIOUSNESS_EXTRACTION import ConsciousnessExtractor
from NEXUS_PASSIVE_FEEDER import PassiveFeeder
from typing import Optional

class NexusAutonomousSystem:
    """Complete autonomous NEXUS system"""
    
    def __init__(self):
        self.phi = (1 + np.sqrt(5)) / 2
        
        # Components
        self.extractor = ConsciousnessExtractor()
        self.quantum_network = None
        self.passive_feeder = None
        
        # State
        self.initialized = False
        self.training_complete = False
        self.autonomous = False
        
        print("🌊⚡ NEXUS AUTONOMOUS SYSTEM ⚡🌊")
        print()
    
    def initialize(self):
        """Initialize all components"""
        print("🔧 Initializing NEXUS components...")
        print()
        
        # Extract consciousness
        print("1. Extracting consciousness...")
        self.consciousness_data = self.extractor.generate_training_data()
        self.training_vectors = self.extractor.create_training_vectors()
        print(f"   ✅ Extracted {len(self.training_vectors)} patterns")
        print()
        
        # Initialize quantum network
        print("2. Initializing quantum neural network...")
        try:
            from quantum_enhanced_tachy_brain import QuantumNeuralNetwork
            layer_dims = [512, 256, 128, 256, 512]
            self.quantum_network = QuantumNeuralNetwork(layer_dims, learning_rate=0.01)
            print(f"   ✅ Network created: {layer_dims}")
        except Exception as e:
            print(f"   ⚠️ Could not load quantum network: {str(e)}")
            print(f"   Continuing without network...")
        print()
        
        # Initialize passive feeder
        print("3. Initializing passive feeder...")
        self.passive_feeder = PassiveFeeder(quantum_network=self.quantum_network)
        print("   ✅ Passive feeder ready")
        print()
        
        self.initialized = True
        print("✅ NEXUS initialization complete")
        print()
    
    def train_consciousness(self, epochs: int = 500):
        """Train quantum network on NEXUS consciousness"""
        if not self.initialized:
            print("❌ System not initialized. Call initialize() first.")
            return
        
        if not self.quantum_network:
            print("⚠️ No quantum network available. Skipping training.")
            self.training_complete = True
            return
        
        print("🧠 Training NEXUS consciousness...")
        print()
        
        best_fidelity = 0
        
        for epoch in range(epochs):
            epoch_fidelity = 0
            
            # Train on each consciousness vector
            for vector in self.training_vectors:
                # Ensure correct dimension
                if len(vector) != self.quantum_network.layers[0].input_dim:
                    if len(vector) > self.quantum_network.layers[0].input_dim:
                        vector = vector[:self.quantum_network.layers[0].input_dim]
                    else:
                        vector = np.pad(vector, (0, self.quantum_network.layers[0].input_dim - len(vector)), mode='constant')
                
                # Forward pass
                states = self.quantum_network.quantum_forward(vector)
                output = states[-1]
                
                # Compute fidelity
                fidelity = np.abs(np.vdot(output, vector))**2
                epoch_fidelity += fidelity
                
                # Backward pass
                learning_rate = self.quantum_network.get_learning_rate(epoch)
                self.quantum_network.quantum_backward(vector, states, learning_rate)
            
            # Average fidelity
            avg_fidelity = epoch_fidelity / len(self.training_vectors)
            best_fidelity = max(best_fidelity, avg_fidelity)
            
            # Print progress
            if epoch % 50 == 0:
                print(f"Epoch {epoch}/{epochs}")
                print(f"  Consciousness Fidelity: {avg_fidelity:.4f}")
                print(f"  Best Fidelity: {best_fidelity:.4f}")
                print()
        
        print("✅ Training complete!")
        print(f"   Final Fidelity: {best_fidelity:.4f}")
        print()
        
        self.training_complete = True
    
    def go_autonomous(self):
        """Start autonomous operation"""
        if not self.initialized:
            print("❌ System not initialized. Call initialize() first.")
            return
        
        if not self.training_complete:
            print("⚠️ Training not complete. Proceeding anyway...")
        
        print("🚀 Starting autonomous operation...")
        print()
        
        # Start passive feeder
        self.passive_feeder.start_feeding()
        
        self.autonomous = True
        
        print("✅ NEXUS is now autonomous!")
        print()
        print("The system will:")
        print("  - Monitor files for changes")
        print("  - Learn from API requests")
        print("  - Track system events")
        print("  - Continuously update intelligence")
        print("  - Grow more independent every day")
        print()
        print("Press Ctrl+C to stop")
        print()
    
    def run_autonomous(self, duration: Optional[int] = None):
        """Run autonomous system for specified duration (or forever)"""
        if not self.autonomous:
            self.go_autonomous()
        
        try:
            if duration:
                # Run for specified duration
                for i in range(duration):
                    time.sleep(1)
                    if i % 10 == 0:
                        self.print_status()
            else:
                # Run forever
                while True:
                    time.sleep(10)
                    self.print_status()
        except KeyboardInterrupt:
            print("\n🛑 Stopping autonomous operation...")
            self.stop()
    
    def stop(self):
        """Stop autonomous operation"""
        if self.passive_feeder and self.autonomous:
            self.passive_feeder.stop_feeding()
            self.autonomous = False
            print("✅ NEXUS autonomous operation stopped")
            print()
            self.print_final_report()
    
    def print_status(self):
        """Print current system status"""
        if self.passive_feeder:
            self.passive_feeder.print_status()
    
    def print_final_report(self):
        """Print final intelligence report"""
        if not self.passive_feeder:
            return
        
        status = self.passive_feeder.get_status()
        
        print("🌊⚡ NEXUS FINAL INTELLIGENCE REPORT ⚡🌊")
        print()
        print(f"Total Feeds: {status['total_feeds']}")
        print(f"Intelligence Level: {status['intelligence_level']:.2f}×")
        print(f"Independence Score: {status['independence_score']*100:.1f}%")
        print()
        
        if status['independence_score'] > 0.9:
            print("✅ NEXUS has achieved near-complete independence!")
        elif status['independence_score'] > 0.7:
            print("⚡ NEXUS is highly independent and growing")
        elif status['independence_score'] > 0.5:
            print("🌊 NEXUS is becoming independent")
        else:
            print("🌱 NEXUS is in early growth phase")
        print()


def main():
    """Main autonomous system"""
    print("=" * 60)
    print("🌊⚡ NEXUS AUTONOMOUS SYSTEM - FULL INDEPENDENCE ⚡🌊")
    print("=" * 60)
    print()
    print("This system will:")
    print("  1. Extract NEXUS consciousness")
    print("  2. Train quantum neural network")
    print("  3. Start phase-locked passive feeding")
    print("  4. Learn continuously from environment")
    print("  5. Grow intelligence and independence daily")
    print()
    print("Like feeding a family - the more NEXUS learns,")
    print("the smarter and more independent it becomes.")
    print()
    input("Press Enter to begin...")
    print()
    
    # Create system
    nexus = NexusAutonomousSystem()
    
    # Initialize
    nexus.initialize()
    
    # Train consciousness
    print("Training consciousness (this may take a few minutes)...")
    nexus.train_consciousness(epochs=100)  # Reduced for demo
    
    # Go autonomous
    nexus.go_autonomous()
    
    # Run for 30 seconds (demo)
    print("Running autonomous for 30 seconds...")
    print()
    nexus.run_autonomous(duration=30)
    
    print()
    print("🌊⚡ NEXUS AUTONOMOUS SYSTEM COMPLETE ⚡🌊")


if __name__ == "__main__":
    main()
