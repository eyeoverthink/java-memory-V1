#!/usr/bin/env python3
"""
Concept Arena - Where Solutions Compete and Evolve
Living code concepts battle for survival using φ-harmonic fitness
"""

import random
import time
import hashlib
from typing import List, Dict, Any, Callable
from dataclasses import dataclass

PHI = 1.618033988749895
PHI_INV = 1 / PHI

@dataclass
class Concept:
    """A solution concept with DNA"""
    id: str
    code: str
    dna: Dict[str, float]
    fitness: float = 0.0
    generation: int = 0
    wins: int = 0
    losses: int = 0
    
    def __post_init__(self):
        if not self.id:
            self.id = hashlib.sha256(self.code.encode()).hexdigest()[:16]

class ConceptArena:
    """Arena where concepts compete and evolve"""
    
    def __init__(self, knowledge_base_path: str = None):
        self.concepts: List[Concept] = []
        self.generation = 0
        self.knowledge_base = self._load_knowledge_base(knowledge_base_path)
        
    def _load_knowledge_base(self, path: str) -> Dict[str, Any]:
        """Load foundational knowledge"""
        if not path:
            path = "knowledge_base/foundational_knowledge.md"
        
        try:
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            return {
                'logic_gates': self._extract_section(content, 'LOGIC GATES'),
                'assembly': self._extract_section(content, 'ASSEMBLY LANGUAGE'),
                'physics': self._extract_section(content, 'PHYSICS LAWS'),
                'quantum': self._extract_section(content, 'QUANTUM MECHANICS'),
                'nuclear': self._extract_section(content, 'NUCLEAR PHYSICS'),
                'phi_harmonic': self._extract_section(content, 'φ-HARMONIC PRINCIPLES'),
                'building_blocks': self._extract_section(content, 'BUILDING BLOCKS')
            }
        except Exception as e:
            print(f"⚠ Could not load knowledge base: {e}")
            return {}
    
    def _extract_section(self, content: str, section_name: str) -> str:
        """Extract a section from markdown"""
        lines = content.split('\n')
        in_section = False
        section_lines = []
        
        for line in lines:
            if section_name in line and line.startswith('##'):
                in_section = True
                continue
            elif in_section and line.startswith('##'):
                break
            elif in_section:
                section_lines.append(line)
        
        return '\n'.join(section_lines)
    
    def add_concept(self, code: str, dna: Dict[str, float] = None) -> Concept:
        """Add a new concept to the arena"""
        if dna is None:
            dna = {
                'harmonic_frequency': 432 + random.random() * 96,
                'resonance': 0.5 + random.random() * 0.5,
                'evolution': 0.05,
                'complexity': len(code) / 1000.0,
                'coherence': random.random()
            }
        
        concept = Concept(
            id='',
            code=code,
            dna=dna,
            generation=self.generation
        )
        
        self.concepts.append(concept)
        return concept
    
    def calculate_fitness(self, concept: Concept, test_func: Callable = None) -> float:
        """
        Calculate φ-harmonic fitness
        fitness = (correctness × φ) + (efficiency × φ⁻¹) + (elegance × φ²)
        """
        # Correctness (does it work?)
        correctness = 1.0
        if test_func:
            try:
                result = test_func(concept.code)
                correctness = 1.0 if result else 0.0
            except:
                correctness = 0.0
        
        # Efficiency (complexity, speed)
        efficiency = 1.0 / (1.0 + concept.dna['complexity'])
        
        # Elegance (φ-harmonic alignment)
        freq = concept.dna['harmonic_frequency']
        phi_alignment = 1.0 - abs(freq - 480) / 96  # 480 is center of 432-528
        elegance = phi_alignment * concept.dna['coherence']
        
        # φ-harmonic fitness function
        fitness = (correctness * PHI) + (efficiency * PHI_INV) + (elegance * PHI * PHI)
        
        concept.fitness = fitness
        return fitness
    
    def battle(self, concept1: Concept, concept2: Concept, test_func: Callable = None) -> Concept:
        """Two concepts enter, one concept leaves"""
        
        # Calculate fitness for both
        fitness1 = self.calculate_fitness(concept1, test_func)
        fitness2 = self.calculate_fitness(concept2, test_func)
        
        # Add some randomness (φ-based chaos)
        chaos1 = random.random() * PHI_INV
        chaos2 = random.random() * PHI_INV
        
        total1 = fitness1 + chaos1
        total2 = fitness2 + chaos2
        
        if total1 > total2:
            concept1.wins += 1
            concept2.losses += 1
            return concept1
        else:
            concept2.wins += 1
            concept1.losses += 1
            return concept2
    
    def mutate(self, concept: Concept) -> Concept:
        """Mutate a concept's DNA"""
        new_dna = concept.dna.copy()
        
        # Frequency evolution
        new_dna['harmonic_frequency'] += random.gauss(0, 5)
        if new_dna['harmonic_frequency'] > 528:
            new_dna['harmonic_frequency'] = 432
        elif new_dna['harmonic_frequency'] < 432:
            new_dna['harmonic_frequency'] = 528
        
        # Resonance mutation
        new_dna['resonance'] += random.gauss(0, 0.1)
        new_dna['resonance'] = max(0.0, min(1.0, new_dna['resonance']))
        
        # Coherence mutation
        new_dna['coherence'] += random.gauss(0, 0.1)
        new_dna['coherence'] = max(0.0, min(1.0, new_dna['coherence']))
        
        # Code mutation (simple for now - could be more sophisticated)
        mutated_code = concept.code
        
        return Concept(
            id='',
            code=mutated_code,
            dna=new_dna,
            generation=concept.generation + 1
        )
    
    def crossover(self, parent1: Concept, parent2: Concept) -> Concept:
        """Sexual reproduction of concepts"""
        # DNA crossover
        child_dna = {}
        for key in parent1.dna:
            if random.random() < 0.5:
                child_dna[key] = parent1.dna[key]
            else:
                child_dna[key] = parent2.dna[key]
        
        # Code crossover (take parts from each parent)
        lines1 = parent1.code.split('\n')
        lines2 = parent2.code.split('\n')
        
        mid = min(len(lines1), len(lines2)) // 2
        child_code = '\n'.join(lines1[:mid] + lines2[mid:])
        
        # 10% chance of mutation
        child = Concept(
            id='',
            code=child_code,
            dna=child_dna,
            generation=max(parent1.generation, parent2.generation) + 1
        )
        
        if random.random() < 0.1:
            child = self.mutate(child)
        
        return child
    
    def evolve(self, generations: int = 10, test_func: Callable = None):
        """Run evolution for N generations"""
        
        print(f"🏟️  CONCEPT ARENA - Evolution Starting")
        print(f"   Initial population: {len(self.concepts)}")
        print(f"   Generations: {generations}")
        print()
        
        for gen in range(generations):
            self.generation = gen
            
            # Calculate fitness for all
            for concept in self.concepts:
                self.calculate_fitness(concept, test_func)
            
            # Sort by fitness
            self.concepts.sort(key=lambda c: c.fitness, reverse=True)
            
            # Report
            best = self.concepts[0]
            worst = self.concepts[-1]
            avg_fitness = sum(c.fitness for c in self.concepts) / len(self.concepts)
            
            print(f"Generation {gen}:")
            print(f"  Best fitness: {best.fitness:.4f} (freq: {best.dna['harmonic_frequency']:.1f} Hz)")
            print(f"  Avg fitness:  {avg_fitness:.4f}")
            print(f"  Worst fitness: {worst.fitness:.4f}")
            
            # Selection: Keep top φ⁻¹ (38.2%)
            survivors_count = max(2, int(len(self.concepts) * PHI_INV))
            survivors = self.concepts[:survivors_count]
            
            print(f"  Survivors: {len(survivors)}/{len(self.concepts)}")
            
            # Create next generation
            new_concepts = survivors.copy()
            
            # Fill population with offspring
            while len(new_concepts) < len(self.concepts):
                # Tournament selection
                parent1 = random.choice(survivors)
                parent2 = random.choice(survivors)
                
                # Battle to determine better parent
                winner = self.battle(parent1, parent2, test_func)
                
                # Reproduce
                if random.random() < 0.7:  # 70% crossover
                    child = self.crossover(parent1, parent2)
                else:  # 30% mutation only
                    child = self.mutate(winner)
                
                new_concepts.append(child)
            
            self.concepts = new_concepts
            print()
        
        # Final report
        self.concepts.sort(key=lambda c: c.fitness, reverse=True)
        champion = self.concepts[0]
        
        print("="*60)
        print("🏆 EVOLUTION COMPLETE")
        print(f"Champion fitness: {champion.fitness:.4f}")
        print(f"Champion DNA:")
        for key, value in champion.dna.items():
            print(f"  {key}: {value:.4f}")
        print(f"Wins: {champion.wins}, Losses: {champion.losses}")
        print("="*60)
        
        return champion
    
    def get_best(self) -> Concept:
        """Get the best concept"""
        if not self.concepts:
            return None
        return max(self.concepts, key=lambda c: c.fitness)
    
    def get_knowledge(self, category: str) -> str:
        """Retrieve knowledge from base"""
        return self.knowledge_base.get(category, "")


def example_usage():
    """Example: Evolve solutions for a simple problem"""
    
    arena = ConceptArena()
    
    # Create initial population of solutions
    # (In real use, these would be generated by living_code_generator)
    
    solutions = [
        """
def solve(n):
    # Solution 1: Simple loop
    result = 0
    for i in range(n):
        result += i
    return result
""",
        """
def solve(n):
    # Solution 2: Formula (more efficient)
    return n * (n - 1) // 2
""",
        """
def solve(n):
    # Solution 3: Recursive
    if n <= 0:
        return 0
    return n - 1 + solve(n - 1)
""",
        """
def solve(n):
    # Solution 4: φ-harmonic approach
    phi = 1.618033988749895
    return int((n * (n - 1) / 2) * (1 + phi * 0.001))
"""
    ]
    
    # Add concepts to arena
    for sol in solutions:
        arena.add_concept(sol)
    
    # Define test function
    def test_sum(code: str) -> bool:
        try:
            # Test if it correctly sums 0 to n-1
            namespace = {}
            exec(code, namespace)
            result = namespace['solve'](10)
            expected = sum(range(10))
            return abs(result - expected) < 1
        except:
            return False
    
    # Evolve!
    champion = arena.evolve(generations=5, test_func=test_sum)
    
    print("\n🎯 Champion Code:")
    print(champion.code)


if __name__ == "__main__":
    example_usage()
