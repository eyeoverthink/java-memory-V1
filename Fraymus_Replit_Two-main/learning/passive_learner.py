
import json
import time
from datetime import datetime
import os
import requests
from bs4 import BeautifulSoup
import hashlib

class PassiveLearner:
    def __init__(self):
        self.data_file = "learning/data/passive_data.dat"
        self.threat_patterns = set()
        self.battle_knowledge = {}
        self.learning_rate = 1.618  # Golden ratio for learning optimization
        self.PHI = 1.618033988749895
        self.genesis_block = {
            'timestamp': None,
            'dna': None,
            'hash': None,
            'patterns': [],
            'resonance': self.PHI
        }
        self.dna_storage = {
            'mainChain': [],
            'replicaChains': {},
            'activeReplicas': 0,
            'maxReplicas': 17
        }
        self.load_data()
        self.initialize_genesis()

    def load_data(self):
        if os.path.exists(self.data_file):
            with open(self.data_file, 'rb') as f:
                self.memory = f.read().decode('utf-8').split('\n')
                self.memory = [x for x in self.memory if x]
        else:
            self.memory = []

    def save_data(self, data):
        with open(self.data_file, 'ab') as f:
            f.write(f"{json.dumps({'timestamp': datetime.now().isoformat(), 'data': data})}\n".encode('utf-8'))
        self.load_data()
        self.analyze_threats(data)

    def analyze_threats(self, data):
        # Extract potential threat patterns
        if isinstance(data, dict) and 'content' in data:
            content = str(data['content']).lower()
            threat_keywords = ['vulnerability', 'exploit', 'breach', 'attack', 'malware']
            
            for keyword in threat_keywords:
                if keyword in content:
                    threat_hash = hashlib.sha256(content.encode()).hexdigest()
                    self.threat_patterns.add(threat_hash)
                    self.update_battle_knowledge(keyword, content)

    def update_battle_knowledge(self, threat_type, context):
        if threat_type not in self.battle_knowledge:
            self.battle_knowledge[threat_type] = []
        
        # Learn from context
        knowledge = {
            'pattern': context[:100],  # Store first 100 chars as pattern
            'timestamp': datetime.now().isoformat(),
            'confidence': self.calculate_confidence(context)
        }
        self.battle_knowledge[threat_type].append(knowledge)

    def calculate_confidence(self, context):
        # Basic confidence calculation based on context length and uniqueness
        base_confidence = len(context) / 1000  # Longer context = higher confidence
        uniqueness = len(set(context.split())) / len(context.split())
        return min(1.0, base_confidence * uniqueness * self.learning_rate)

    def get_recent_learnings(self, limit=100):
        return self.memory[-limit:]

    def get_current_threats(self):
        return list(self.threat_patterns)

    def get_battle_insights(self):
        return self.battle_knowledge

    def initialize_genesis(self):
        if not self.genesis_block['timestamp']:
            self.genesis_block.update({
                'timestamp': time.time(),
                'dna': self.generate_dna_sequence(),
                'hash': self.calculate_hash(str(time.time())),
                'resonance': self.PHI
            })
            self.dna_storage['mainChain'].append(self.genesis_block.copy())

    def generate_dna_sequence(self):
        bases = ['A', 'T', 'C', 'G']
        creator = "FRAYMUS-VS"
        sequence = ''.join(bases[ord(c) % 4] for c in creator)
        while len(sequence) < 17:
            sequence += bases[int(random.random() * 4)]
        return sequence

    def calculate_hash(self, data):
        return hashlib.sha256(str(data).encode()).hexdigest()

    def push_to_genesis(self, data):
        data_hash = self.calculate_hash(data)
        dna = self.generate_dna_sequence()
        block = {
            'timestamp': time.time(),
            'data': data,
            'dna': dna,
            'hash': data_hash,
            'previousHash': self.dna_storage['mainChain'][-1]['hash'],
            'resonance': self.PHI * (1 + len(self.dna_storage['mainChain']))
        }
        self.dna_storage['mainChain'].append(block)
        return block

    def get_genesis_state(self):
        return {
            'block': self.genesis_block,
            'dna_storage': self.dna_storage,
            'chain_length': len(self.dna_storage['mainChain'])
        }
