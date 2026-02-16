#!/usr/bin/env python3
"""
QR Codon Groups - Part of Animated QR Flipbook DNA System
By Vaughn Scott

Organizes QR frames into functional groups similar to DNA codons.
Each codon group performs a specific function in the execution chain.
Implements phi-harmonic relationships between codons.
"""

import os
import json
import uuid
import math
import time
import numpy as np
from datetime import datetime

# Phi (Golden Ratio) constant
PHI = (1 + np.sqrt(5)) / 2  # Approximately 1.618034

class QRCodon:
    """
    Represents a single QR codon (functional unit) in the DNA chain.
    """
    
    def __init__(self, codon_type, frame_data=None):
        """
        Initialize a QR Codon.
        
        Args:
            codon_type: Type of codon (e.g., 'setup', 'process', 'evolve', 'respond')
            frame_data: Optional frame data for this codon
        """
        self.codon_id = str(uuid.uuid4())
        self.codon_type = codon_type
        self.frame_data = frame_data or {}
        self.creation_time = datetime.now().isoformat()
        self.execution_count = 0
        self.success_count = 0
        self.resonance = 1.0
        self.phi_state = {
            "complexity": 0.5,
            "resonance": 1.0,
            "generation": 1
        }
    
    def update_from_execution(self, success, phi_metadata=None):
        """
        Update codon state based on execution results.
        
        Args:
            success: Whether execution was successful
            phi_metadata: Optional phi-harmonic metadata from execution
        """
        self.execution_count += 1
        if success:
            self.success_count += 1
        
        # Update phi state if provided
        if phi_metadata:
            self.phi_state = {
                "complexity": phi_metadata.get("complexity", self.phi_state["complexity"]),
                "resonance": phi_metadata.get("resonance", self.phi_state["resonance"]),
                "generation": phi_metadata.get("generation", self.phi_state["generation"])
            }
        
        # Calculate resonance based on success rate
        if self.execution_count > 0:
            success_rate = self.success_count / self.execution_count
            self.resonance = 1.0 / (1.0 + abs((success_rate * PHI) % 1.0 - 0.5))
    
    def to_dict(self):
        """Convert codon to dictionary for serialization."""
        return {
            "codon_id": self.codon_id,
            "codon_type": self.codon_type,
            "frame_data": self.frame_data,
            "creation_time": self.creation_time,
            "execution_count": self.execution_count,
            "success_count": self.success_count,
            "resonance": self.resonance,
            "phi_state": self.phi_state
        }
    
    @classmethod
    def from_dict(cls, data):
        """Create codon from dictionary."""
        codon = cls(data["codon_type"], data["frame_data"])
        codon.codon_id = data["codon_id"]
        codon.creation_time = data["creation_time"]
        codon.execution_count = data["execution_count"]
        codon.success_count = data["success_count"]
        codon.resonance = data["resonance"]
        codon.phi_state = data["phi_state"]
        return codon

class QRCodonGroup:
    """
    Represents a group of QR codons that work together as a functional unit.
    """
    
    def __init__(self, group_type, codons=None):
        """
        Initialize a QR Codon Group.
        
        Args:
            group_type: Type of group (e.g., 'gene', 'operon', 'chromosome')
            codons: Optional list of codons in this group
        """
        self.group_id = str(uuid.uuid4())
        self.group_type = group_type
        self.codons = codons or []
        self.creation_time = datetime.now().isoformat()
        self.execution_history = []
        self.phi_state = {
            "complexity": 0.5,
            "resonance": 1.0,
            "generation": 1
        }
    
    def add_codon(self, codon):
        """
        Add a codon to the group.
        
        Args:
            codon: QRCodon to add
        """
        self.codons.append(codon)
    
    def create_codon(self, codon_type, frame_data=None):
        """
        Create and add a new codon to the group.
        
        Args:
            codon_type: Type of codon
            frame_data: Optional frame data
            
        Returns:
            The created QRCodon
        """
        codon = QRCodon(codon_type, frame_data)
        self.add_codon(codon)
        return codon
    
    def get_codons_by_type(self, codon_type):
        """
        Get all codons of a specific type.
        
        Args:
            codon_type: Type of codons to get
            
        Returns:
            List of matching codons
        """
        return [c for c in self.codons if c.codon_type == codon_type]
    
    def update_phi_state(self):
        """Update group phi state based on constituent codons."""
        if not self.codons:
            return
        
        # Calculate average complexity and resonance
        complexity_sum = sum(c.phi_state["complexity"] for c in self.codons)
        resonance_sum = sum(c.phi_state["resonance"] for c in self.codons)
        max_generation = max(c.phi_state["generation"] for c in self.codons)
        
        # Update phi state
        self.phi_state = {
            "complexity": complexity_sum / len(self.codons),
            "resonance": resonance_sum / len(self.codons),
            "generation": max_generation
        }
    
    def record_execution(self, success, phi_metadata=None):
        """
        Record an execution of this codon group.
        
        Args:
            success: Whether execution was successful
            phi_metadata: Optional phi-harmonic metadata
        """
        timestamp = datetime.now().isoformat()
        
        # Record execution
        execution_record = {
            "timestamp": timestamp,
            "success": success,
            "phi_state": phi_metadata or self.phi_state
        }
        
        self.execution_history.append(execution_record)
        
        # Update phi state
        if phi_metadata:
            self.phi_state = phi_metadata
    
    def to_dict(self):
        """Convert group to dictionary for serialization."""
        return {
            "group_id": self.group_id,
            "group_type": self.group_type,
            "codons": [c.to_dict() for c in self.codons],
            "creation_time": self.creation_time,
            "execution_history": self.execution_history,
            "phi_state": self.phi_state
        }
    
    @classmethod
    def from_dict(cls, data):
        """Create group from dictionary."""
        codons = [QRCodon.from_dict(c) for c in data["codons"]]
        group = cls(data["group_type"], codons)
        group.group_id = data["group_id"]
        group.creation_time = data["creation_time"]
        group.execution_history = data["execution_history"]
        group.phi_state = data["phi_state"]
        return group

class QRGenome:
    """
    Represents a complete QR genome consisting of multiple codon groups.
    """
    
    def __init__(self, genome_name="qr_genome", groups=None):
        """
        Initialize a QR Genome.
        
        Args:
            genome_name: Name of the genome
            groups: Optional list of codon groups
        """
        self.genome_id = str(uuid.uuid4())
        self.genome_name = genome_name
        self.groups = groups or []
        self.creation_time = datetime.now().isoformat()
        self.generation = 1
        self.mutation_rate = 0.05
        self.phi_state = {
            "complexity": 0.5,
            "resonance": 1.0,
            "generation": 1
        }
    
    def add_group(self, group):
        """
        Add a codon group to the genome.
        
        Args:
            group: QRCodonGroup to add
        """
        self.groups.append(group)
    
    def create_group(self, group_type):
        """
        Create and add a new codon group to the genome.
        
        Args:
            group_type: Type of group
            
        Returns:
            The created QRCodonGroup
        """
        group = QRCodonGroup(group_type)
        self.add_group(group)
        return group
    
    def get_groups_by_type(self, group_type):
        """
        Get all groups of a specific type.
        
        Args:
            group_type: Type of groups to get
            
        Returns:
            List of matching groups
        """
        return [g for g in self.groups if g.group_type == group_type]
    
    def update_phi_state(self):
        """Update genome phi state based on constituent groups."""
        if not self.groups:
            return
        
        # Calculate average complexity and resonance
        complexity_sum = sum(g.phi_state["complexity"] for g in self.groups)
        resonance_sum = sum(g.phi_state["resonance"] for g in self.groups)
        max_generation = max(g.phi_state["generation"] for g in self.groups)
        
        # Update phi state
        self.phi_state = {
            "complexity": complexity_sum / len(self.groups),
            "resonance": resonance_sum / len(self.groups),
            "generation": max_generation
        }
        
        # Update generation
        self.generation = max_generation
    
    def mutate(self, mutation_rate=None):
        """
        Apply phi-harmonic mutation to the genome.
        
        Args:
            mutation_rate: Optional override for mutation rate
            
        Returns:
            Number of mutations applied
        """
        if mutation_rate is not None:
            self.mutation_rate = mutation_rate
        
        mutations = 0
        
        # Apply mutations to groups based on phi-harmonic principles
        for group in self.groups:
            # Calculate mutation probability for this group
            group_resonance = group.phi_state["resonance"]
            mutation_prob = self.mutation_rate * (1.0 / (1.0 + abs((group_resonance * PHI) % 1.0 - 0.5)))
            
            if np.random.random() < mutation_prob:
                # Mutate group phi state
                complexity_delta = (np.random.random() - 0.5) * 0.2
                resonance_delta = (np.random.random() - 0.5) * 0.2
                
                group.phi_state["complexity"] = max(0.1, min(1.0, group.phi_state["complexity"] + complexity_delta))
                group.phi_state["resonance"] = max(0.1, min(5.0, group.phi_state["resonance"] + resonance_delta))
                
                mutations += 1
        
        # If mutations occurred, update generation
        if mutations > 0:
            self.generation += 1
            self.phi_state["generation"] = self.generation
        
        return mutations
    
    def save_to_file(self, filepath="qr_genome.json"):
        """
        Save genome to file.
        
        Args:
            filepath: Path to save file
        """
        # Update phi state before saving
        self.update_phi_state()
        
        # Convert to dictionary
        genome_dict = {
            "genome_id": self.genome_id,
            "genome_name": self.genome_name,
            "groups": [g.to_dict() for g in self.groups],
            "creation_time": self.creation_time,
            "generation": self.generation,
            "mutation_rate": self.mutation_rate,
            "phi_state": self.phi_state
        }
        
        # Save to file
        with open(filepath, 'w') as f:
            json.dump(genome_dict, f, indent=2)
        
        print(f"Saved genome to {filepath}")
    
    @classmethod
    def load_from_file(cls, filepath="qr_genome.json"):
        """
        Load genome from file.
        
        Args:
            filepath: Path to genome file
            
        Returns:
            Loaded QRGenome
        """
        if not os.path.exists(filepath):
            print(f"Genome file {filepath} not found")
            return None
        
        try:
            with open(filepath, 'r') as f:
                genome_dict = json.load(f)
            
            # Create genome
            genome = cls(genome_dict["genome_name"])
            genome.genome_id = genome_dict["genome_id"]
            genome.creation_time = genome_dict["creation_time"]
            genome.generation = genome_dict["generation"]
            genome.mutation_rate = genome_dict["mutation_rate"]
            genome.phi_state = genome_dict["phi_state"]
            
            # Load groups
            for group_dict in genome_dict["groups"]:
                group = QRCodonGroup.from_dict(group_dict)
                genome.add_group(group)
            
            print(f"Loaded genome from {filepath}")
            return genome
            
        except Exception as e:
            print(f"Error loading genome: {e}")
            return None
    
    def print_summary(self):
        """Print a summary of the genome."""
        print("\n" + "="*60)
        print(f"QR GENOME: {self.genome_name}")
        print("="*60)
        
        print(f"\nGenome ID: {self.genome_id}")
        print(f"Created: {self.creation_time}")
        print(f"Generation: {self.generation}")
        print(f"Mutation Rate: {self.mutation_rate:.4f}")
        
        print("\nPHI STATE:")
        print(f"  Complexity: {self.phi_state['complexity']:.6f}")
        print(f"  Resonance: {self.phi_state['resonance']:.6f}")
        
        print(f"\nGROUPS ({len(self.groups)}):")
        group_types = {}
        for group in self.groups:
            if group.group_type not in group_types:
                group_types[group.group_type] = 0
            group_types[group.group_type] += 1
        
        for group_type, count in group_types.items():
            print(f"  {group_type}: {count}")
        
        print("\nCODON SUMMARY:")
        codon_types = {}
        total_codons = 0
        for group in self.groups:
            for codon in group.codons:
                if codon.codon_type not in codon_types:
                    codon_types[codon.codon_type] = 0
                codon_types[codon.codon_type] += 1
                total_codons += 1
        
        print(f"  Total Codons: {total_codons}")
        for codon_type, count in codon_types.items():
            print(f"  {codon_type}: {count}")
        
        print("\n" + "="*60)

def create_basic_genome():
    """Create a basic QR genome with standard codon groups."""
    genome = QRGenome("basic_qr_genome")
    
    # Create setup group
    setup_group = genome.create_group("setup")
    setup_group.create_codon("initialize", {"task_type": "execute_python"})
    setup_group.create_codon("import_libraries", {"task_type": "execute_python"})
    
    # Create processing group
    process_group = genome.create_group("process")
    process_group.create_codon("data_processing", {"task_type": "execute_python"})
    process_group.create_codon("calculation", {"task_type": "execute_python"})
    
    # Create evolution group
    evolve_group = genome.create_group("evolve")
    evolve_group.create_codon("mutate", {"task_type": "execute_python"})
    evolve_group.create_codon("select", {"task_type": "execute_python"})
    
    # Create response group
    response_group = genome.create_group("respond")
    response_group.create_codon("generate_output", {"task_type": "execute_python"})
    response_group.create_codon("visualize", {"task_type": "execute_python"})
    
    return genome

def main():
    """Main function to test QR Codon Groups."""
    import argparse
    
    parser = argparse.ArgumentParser(description="QR Codon Groups")
    parser.add_argument("--create", action="store_true", help="Create a new basic genome")
    parser.add_argument("--mutate", action="store_true", help="Apply mutation to genome")
    parser.add_argument("--genome", default="qr_genome.json", help="Genome file path")
    
    args = parser.parse_args()
    
    # Create new genome if requested
    if args.create:
        genome = create_basic_genome()
        genome.save_to_file(args.genome)
    else:
        # Load existing genome
        genome = QRGenome.load_from_file(args.genome)
        if not genome:
            print("Creating new basic genome")
            genome = create_basic_genome()
    
    # Apply mutation if requested
    if args.mutate:
        mutations = genome.mutate()
        print(f"Applied {mutations} mutations")
        genome.save_to_file(args.genome)
    
    # Print summary
    genome.print_summary()

if __name__ == "__main__":
    print("QR Codon Groups - By Vaughn Scott")
    main()
