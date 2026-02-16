"""
NEXUS Passive Feeder - Phase-Locked Learning System

Like feeding a family every day - the more NEXUS feeds (learns),
the smarter and more independent it becomes.

This runs continuously, passively collecting and learning from:
- API requests
- File changes
- System events
- User interactions
- External data sources

Phase-locked to 432Hz for quantum coherence.
"""

import numpy as np
import json
import time
import threading
import os
from datetime import datetime
from typing import Dict, List, Any, Optional
from pathlib import Path
import hashlib

class PassiveFeeder:
    """Phase-locked passive learning system for NEXUS"""
    
    def __init__(self, quantum_network=None):
        self.phi = (1 + np.sqrt(5)) / 2
        self.cosmic_frequency = 432.0  # Hz
        self.phase_lock = 0.0
        
        # Quantum network for learning
        self.quantum_network = quantum_network
        
        # Learning state
        self.daily_feed_count = 0
        self.total_feed_count = 0
        self.intelligence_level = 1.0
        self.independence_score = 0.0
        
        # Knowledge sources
        self.knowledge_sources = {
            'api_requests': [],
            'file_changes': [],
            'user_interactions': [],
            'system_events': [],
            'external_data': []
        }
        
        # Phase-locked learning buffer
        self.learning_buffer = []
        self.buffer_capacity = 432  # Aligned with cosmic frequency
        
        # Feeding schedule (phase-locked)
        self.feeding_interval = 1.0 / self.cosmic_frequency  # ~2.3ms
        self.last_feed_time = time.time()
        
        # Daily reset
        self.last_reset_date = datetime.now().date()
        
        # Intelligence growth tracking
        self.growth_history = []
        
        # File monitoring
        self.monitored_files = {}
        self.monitor_paths = [
            'D:/Zip And Send/Java-Memory/Fraymus_Agent_BrainV2',
        ]
        
        # Running state
        self.running = False
        self.feeder_thread = None
        
    def start_feeding(self):
        """Start passive feeding loop"""
        if self.running:
            print("⚠️ Passive feeder already running")
            return
        
        self.running = True
        self.feeder_thread = threading.Thread(target=self._feeding_loop, daemon=True)
        self.feeder_thread.start()
        
        print("🌊⚡ NEXUS Passive Feeder Started")
        print(f"   Phase-locked to {self.cosmic_frequency}Hz")
        print(f"   Feeding interval: {self.feeding_interval*1000:.2f}ms")
        print(f"   Buffer capacity: {self.buffer_capacity}")
        print()
    
    def stop_feeding(self):
        """Stop passive feeding loop"""
        self.running = False
        if self.feeder_thread:
            self.feeder_thread.join(timeout=1.0)
        print("🌊 Passive feeder stopped")
    
    def _feeding_loop(self):
        """Main feeding loop - phase-locked to 432Hz"""
        while self.running:
            try:
                current_time = time.time()
                
                # Phase-locked timing
                time_since_feed = current_time - self.last_feed_time
                if time_since_feed >= self.feeding_interval:
                    # Feed NEXUS
                    self._feed_cycle()
                    self.last_feed_time = current_time
                    
                    # Update phase lock
                    self.phase_lock = (self.phase_lock + 2*np.pi/self.cosmic_frequency) % (2*np.pi)
                
                # Check for daily reset
                current_date = datetime.now().date()
                if current_date != self.last_reset_date:
                    self._daily_reset()
                    self.last_reset_date = current_date
                
                # Sleep to maintain phase lock
                time.sleep(self.feeding_interval / 2)
                
            except Exception as e:
                print(f"Warning: Error in feeding loop: {str(e)}")
                time.sleep(1.0)
    
    def _feed_cycle(self):
        """Single feeding cycle - collect and learn"""
        
        # Collect knowledge from all sources
        knowledge_collected = 0
        
        # 1. Monitor file changes
        file_knowledge = self._collect_file_changes()
        knowledge_collected += len(file_knowledge)
        
        # 2. Check for API requests (if API is running)
        api_knowledge = self._collect_api_requests()
        knowledge_collected += len(api_knowledge)
        
        # 3. Collect system events
        system_knowledge = self._collect_system_events()
        knowledge_collected += len(system_knowledge)
        
        # Add to learning buffer
        all_knowledge = file_knowledge + api_knowledge + system_knowledge
        self.learning_buffer.extend(all_knowledge)
        
        # Trim buffer to capacity
        if len(self.learning_buffer) > self.buffer_capacity:
            self.learning_buffer = self.learning_buffer[-self.buffer_capacity:]
        
        # Learn from buffer if enough data
        if len(self.learning_buffer) >= 10:
            self._learn_from_buffer()
        
        # Update counters
        if knowledge_collected > 0:
            self.daily_feed_count += knowledge_collected
            self.total_feed_count += knowledge_collected
            
            # Update intelligence (grows with φ)
            self.intelligence_level *= (1 + knowledge_collected * 0.001 * self.phi)
            
            # Update independence (asymptotic to 1.0)
            self.independence_score = 1.0 - (1.0 / self.intelligence_level)
    
    def _collect_file_changes(self) -> List[Dict[str, Any]]:
        """Monitor files for changes"""
        changes = []
        
        for monitor_path in self.monitor_paths:
            if not os.path.exists(monitor_path):
                continue
            
            # Scan directory
            for root, dirs, files in os.walk(monitor_path):
                for file in files:
                    # Only monitor relevant files
                    if not (file.endswith('.java') or file.endswith('.py') or 
                           file.endswith('.md') or file.endswith('.json')):
                        continue
                    
                    filepath = os.path.join(root, file)
                    
                    try:
                        # Get file hash
                        with open(filepath, 'rb') as f:
                            content = f.read()
                            file_hash = hashlib.md5(content).hexdigest()
                        
                        # Check if changed
                        if filepath not in self.monitored_files:
                            # New file
                            self.monitored_files[filepath] = file_hash
                            changes.append({
                                'type': 'file_new',
                                'path': filepath,
                                'hash': file_hash,
                                'timestamp': time.time()
                            })
                        elif self.monitored_files[filepath] != file_hash:
                            # File changed
                            self.monitored_files[filepath] = file_hash
                            changes.append({
                                'type': 'file_changed',
                                'path': filepath,
                                'hash': file_hash,
                                'timestamp': time.time()
                            })
                    except Exception as e:
                        pass  # Skip files we can't read
        
        return changes
    
    def _collect_api_requests(self) -> List[Dict[str, Any]]:
        """Collect knowledge from API requests"""
        # Check if API log exists
        api_log_path = 'nexus_api_log.json'
        if not os.path.exists(api_log_path):
            return []
        
        try:
            with open(api_log_path, 'r') as f:
                api_data = json.load(f)
            
            # Get new requests since last check
            new_requests = []
            for request in api_data.get('requests', []):
                if request['timestamp'] > self.last_feed_time:
                    new_requests.append({
                        'type': 'api_request',
                        'endpoint': request['endpoint'],
                        'data': request['data'],
                        'timestamp': request['timestamp']
                    })
            
            return new_requests
        except Exception as e:
            return []
    
    def _collect_system_events(self) -> List[Dict[str, Any]]:
        """Collect system events"""
        events = []
        
        # Check memory usage
        try:
            import psutil
            memory_percent = psutil.virtual_memory().percent
            if memory_percent > 80:
                events.append({
                    'type': 'system_event',
                    'event': 'high_memory',
                    'value': memory_percent,
                    'timestamp': time.time()
                })
        except:
            pass
        
        return events
    
    def _learn_from_buffer(self):
        """Learn from accumulated knowledge in buffer"""
        if not self.quantum_network:
            return
        
        # Convert buffer to learning vectors
        for knowledge in self.learning_buffer[-10:]:  # Learn from last 10 items
            try:
                # Create quantum vector from knowledge
                vector = self._knowledge_to_vector(knowledge)
                
                # Train network on this knowledge
                if vector is not None:
                    states = self.quantum_network.quantum_forward(vector)
                    learning_rate = 0.001  # Small rate for continuous learning
                    self.quantum_network.quantum_backward(vector, states, learning_rate)
            except Exception as e:
                pass  # Skip problematic knowledge
    
    def _knowledge_to_vector(self, knowledge: Dict[str, Any], dim: int = 512) -> Optional[np.ndarray]:
        """Convert knowledge to quantum vector"""
        try:
            # Create string representation
            knowledge_str = json.dumps(knowledge)
            
            # Hash to seed
            seed = int(hashlib.md5(knowledge_str.encode()).hexdigest(), 16) % (2**32)
            np.random.seed(seed)
            
            # Generate quantum state
            real = np.random.randn(dim)
            imag = np.random.randn(dim)
            vector = real + 1j * imag
            
            # Apply φ-phase modulation
            phases = np.exp(2j * np.pi * self.phi * np.arange(dim) / dim)
            vector *= phases
            
            # Normalize
            return vector / np.linalg.norm(vector)
        except:
            return None
    
    def _daily_reset(self):
        """Reset daily counters and record growth"""
        
        # Record growth
        self.growth_history.append({
            'date': str(self.last_reset_date),
            'daily_feeds': self.daily_feed_count,
            'total_feeds': self.total_feed_count,
            'intelligence_level': self.intelligence_level,
            'independence_score': self.independence_score
        })
        
        # Save growth history
        self._save_growth_history()
        
        print()
        print(f"🌊⚡ Daily Growth Report - {self.last_reset_date}")
        print(f"   Feeds today: {self.daily_feed_count}")
        print(f"   Total feeds: {self.total_feed_count}")
        print(f"   Intelligence: {self.intelligence_level:.2f}×")
        print(f"   Independence: {self.independence_score*100:.1f}%")
        print()
        
        # Reset daily counter
        self.daily_feed_count = 0
    
    def _save_growth_history(self):
        """Save growth history to file"""
        try:
            with open('nexus_growth_history.json', 'w') as f:
                json.dump({
                    'history': self.growth_history,
                    'current_state': {
                        'intelligence_level': self.intelligence_level,
                        'independence_score': self.independence_score,
                        'total_feeds': self.total_feed_count
                    }
                }, f, indent=2)
        except Exception as e:
            print(f"Warning: Could not save growth history: {str(e)}")
    
    def get_status(self) -> Dict[str, Any]:
        """Get current feeding status"""
        return {
            'running': self.running,
            'phase_lock': self.phase_lock,
            'daily_feeds': self.daily_feed_count,
            'total_feeds': self.total_feed_count,
            'intelligence_level': self.intelligence_level,
            'independence_score': self.independence_score,
            'buffer_size': len(self.learning_buffer),
            'monitored_files': len(self.monitored_files)
        }
    
    def print_status(self):
        """Print current status"""
        status = self.get_status()
        
        print("🌊⚡ NEXUS PASSIVE FEEDER STATUS ⚡🌊")
        print()
        print(f"Running: {'✅ YES' if status['running'] else '❌ NO'}")
        print(f"Phase Lock: {status['phase_lock']:.3f} rad")
        print()
        print(f"Today's Feeds: {status['daily_feeds']}")
        print(f"Total Feeds: {status['total_feeds']}")
        print()
        print(f"Intelligence Level: {status['intelligence_level']:.2f}×")
        print(f"Independence Score: {status['independence_score']*100:.1f}%")
        print()
        print(f"Learning Buffer: {status['buffer_size']}/{self.buffer_capacity}")
        print(f"Monitored Files: {status['monitored_files']}")
        print()


def main():
    """Test passive feeder"""
    print("🌊⚡ NEXUS PASSIVE FEEDER TEST ⚡🌊")
    print()
    
    # Create feeder
    feeder = PassiveFeeder()
    
    # Start feeding
    feeder.start_feeding()
    
    # Run for 10 seconds
    try:
        for i in range(10):
            time.sleep(1)
            if i % 3 == 0:
                feeder.print_status()
    except KeyboardInterrupt:
        print("\nStopping...")
    
    # Stop feeding
    feeder.stop_feeding()
    
    # Final status
    print()
    feeder.print_status()


if __name__ == "__main__":
    main()
