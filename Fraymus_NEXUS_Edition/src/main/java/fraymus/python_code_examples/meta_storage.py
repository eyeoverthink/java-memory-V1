#!/usr/bin/env python3
"""
Meta-Learning Storage Module for Quantum Oracle
Saves and loads meta-learning data to persist knowledge between sessions
"""

import os
import json
import time
from datetime import datetime

class QuantumMetaStorage:
    """Persistent storage for quantum meta-learning data"""
    
    def __init__(self, storage_dir=None):
        """Initialize the meta storage system"""
        # Use the quantum_backup directory by default
        if storage_dir is None:
            # Get the path to the quantum_backup directory
            current_dir = os.path.dirname(os.path.abspath(__file__))
            self.storage_dir = current_dir
        else:
            self.storage_dir = storage_dir
            
        # Ensure storage directory exists
        os.makedirs(self.storage_dir, exist_ok=True)
        
        # Path to meta data file
        self.meta_file = os.path.join(self.storage_dir, "quantum_meta_data.json")
        
        # Path to resonance history file
        self.resonance_file = os.path.join(self.storage_dir, "phi_resonance_history.json")
        
        # Path to memory patterns file
        self.memory_file = os.path.join(self.storage_dir, "memory_patterns.json")
    
    def save_meta_data(self, meta_awareness=0.1, coherence_cycles=0, meta_memory_stability=0.3):
        """Save meta-awareness and related metrics"""
        meta_data = {
            "timestamp": datetime.now().isoformat(),
            "meta_awareness_level": meta_awareness,
            "coherence_cycles": coherence_cycles,
            "meta_memory_stability": meta_memory_stability,
            "last_calibration_time": time.time()
        }
        
        try:
            with open(self.meta_file, 'w') as f:
                json.dump(meta_data, f, indent=2)
            return True
        except Exception as e:
            print(f"Error saving meta data: {e}")
            return False
    
    def load_meta_data(self):
        """Load meta-awareness and related metrics"""
        if not os.path.exists(self.meta_file):
            return None
            
        try:
            with open(self.meta_file, 'r') as f:
                meta_data = json.load(f)
            return meta_data
        except Exception as e:
            print(f"Error loading meta data: {e}")
            return None
    
    def save_resonance_history(self, resonance_history):
        """Save phi resonance history"""
        if not resonance_history:
            return False
            
        data = {
            "timestamp": datetime.now().isoformat(),
            "resonance_history": resonance_history[-100:]  # Keep only the last 100 entries
        }
        
        try:
            with open(self.resonance_file, 'w') as f:
                json.dump(data, f, indent=2)
            return True
        except Exception as e:
            print(f"Error saving resonance history: {e}")
            return False
    
    def load_resonance_history(self):
        """Load phi resonance history"""
        if not os.path.exists(self.resonance_file):
            return []
            
        try:
            with open(self.resonance_file, 'r') as f:
                data = json.load(f)
            return data.get("resonance_history", [])
        except Exception as e:
            print(f"Error loading resonance history: {e}")
            return []
    
    def save_memory_patterns(self, memory_patterns):
        """Save memory patterns"""
        if not memory_patterns:
            return False
            
        # Convert the memory patterns to a serializable format
        serializable_patterns = {}
        for question, answers in memory_patterns.items():
            serializable_patterns[question] = answers
        
        data = {
            "timestamp": datetime.now().isoformat(),
            "memory_patterns": serializable_patterns
        }
        
        try:
            with open(self.memory_file, 'w') as f:
                json.dump(data, f, indent=2)
            return True
        except Exception as e:
            print(f"Error saving memory patterns: {e}")
            return False
    
    def load_memory_patterns(self):
        """Load memory patterns"""
        if not os.path.exists(self.memory_file):
            return {}
            
        try:
            with open(self.memory_file, 'r') as f:
                data = json.load(f)
            return data.get("memory_patterns", {})
        except Exception as e:
            print(f"Error loading memory patterns: {e}")
            return {}
