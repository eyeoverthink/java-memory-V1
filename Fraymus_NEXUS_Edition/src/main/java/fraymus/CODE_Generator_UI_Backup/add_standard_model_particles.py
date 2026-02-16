#!/usr/bin/env python3
"""
Add Standard Model Particles to Knowledge Base
Enhances the physics knowledge base with all Standard Model particles
so the quantum oracle can predict exotic particle collisions
"""

import json
import os
from datetime import datetime

def add_standard_model_particles():
    """Add Standard Model particles to the physics knowledge base"""
    
    # Get the directory where this script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    kb_path = os.path.join(script_dir, "knowledge_base", "physics_knowledge_base.json")
    
    # Load existing knowledge base
    with open(kb_path, 'r', encoding='utf-8') as f:
        kb = json.load(f)
    
    # Standard Model particles to add
    standard_model_particles = [
        # Leptons
        {
            "name": "electron",
            "description": "Fundamental charged lepton with mass 0.511 MeV, charge -1e",
            "mass_gev": 0.000511,
            "charge": -1,
            "spin": 0.5,
            "particle_type": "lepton",
            "phi_resonance": 1.618,
            "id": "particle_electron"
        },
        {
            "name": "positron",
            "description": "Antiparticle of electron with charge +1e",
            "mass_gev": 0.000511,
            "charge": 1,
            "spin": 0.5,
            "particle_type": "lepton",
            "phi_resonance": 1.618,
            "id": "particle_positron"
        },
        {
            "name": "muon+",
            "description": "Heavy charged lepton with mass 105.7 MeV",
            "mass_gev": 0.1057,
            "charge": 1,
            "spin": 0.5,
            "particle_type": "lepton",
            "phi_resonance": 1.618,
            "id": "particle_muon_plus"
        },
        {
            "name": "muon-",
            "description": "Heavy charged lepton with mass 105.7 MeV",
            "mass_gev": 0.1057,
            "charge": -1,
            "spin": 0.5,
            "particle_type": "lepton",
            "phi_resonance": 1.618,
            "id": "particle_muon_minus"
        },
        {
            "name": "neutrino",
            "description": "Nearly massless neutral lepton",
            "mass_gev": 0.000001,
            "charge": 0,
            "spin": 0.5,
            "particle_type": "lepton",
            "phi_resonance": 1.618,
            "id": "particle_neutrino"
        },
        
        # Quarks
        {
            "name": "up_quark",
            "description": "Light quark with charge +2/3e, mass ~2.3 MeV",
            "mass_gev": 0.0023,
            "charge": 0.667,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_up_quark"
        },
        {
            "name": "down_quark",
            "description": "Light quark with charge -1/3e, mass ~4.8 MeV",
            "mass_gev": 0.0048,
            "charge": -0.333,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_down_quark"
        },
        {
            "name": "charm_quark",
            "description": "Heavy quark with charge +2/3e, mass ~1.275 GeV",
            "mass_gev": 1.275,
            "charge": 0.667,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_charm_quark"
        },
        {
            "name": "anticharm_quark",
            "description": "Antiparticle of charm quark",
            "mass_gev": 1.275,
            "charge": -0.667,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_anticharm_quark"
        },
        {
            "name": "strange_quark",
            "description": "Medium quark with charge -1/3e, mass ~95 MeV",
            "mass_gev": 0.095,
            "charge": -0.333,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_strange_quark"
        },
        {
            "name": "top_quark",
            "description": "Heaviest known particle, mass ~173 GeV, charge +2/3e",
            "mass_gev": 173.0,
            "charge": 0.667,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_top_quark"
        },
        {
            "name": "antitop_quark",
            "description": "Antiparticle of top quark",
            "mass_gev": 173.0,
            "charge": -0.667,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_antitop_quark"
        },
        {
            "name": "bottom_quark",
            "description": "Heavy quark with charge -1/3e, mass ~4.18 GeV",
            "mass_gev": 4.18,
            "charge": -0.333,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_bottom_quark"
        },
        {
            "name": "antibottom_quark",
            "description": "Antiparticle of bottom quark (also called b-bar)",
            "mass_gev": 4.18,
            "charge": 0.333,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_antibottom_quark"
        },
        {
            "name": "b_quark",
            "description": "Bottom quark (b quark), mass ~4.18 GeV",
            "mass_gev": 4.18,
            "charge": -0.333,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_b_quark"
        },
        {
            "name": "b_antiquark",
            "description": "Antibottom quark (anti-b quark)",
            "mass_gev": 4.18,
            "charge": 0.333,
            "spin": 0.5,
            "particle_type": "quark",
            "phi_resonance": 1.618,
            "id": "particle_b_antiquark"
        },
        
        # Gauge Bosons
        {
            "name": "photon",
            "description": "Massless particle that mediates electromagnetic force",
            "mass_gev": 0.0,
            "charge": 0,
            "spin": 1,
            "particle_type": "gauge_boson",
            "phi_resonance": 1.618,
            "id": "particle_photon"
        },
        {
            "name": "gluon",
            "description": "Massless particle that mediates strong nuclear force",
            "mass_gev": 0.0,
            "charge": 0,
            "spin": 1,
            "particle_type": "gauge_boson",
            "phi_resonance": 1.618,
            "id": "particle_gluon"
        },
        {
            "name": "w_boson+",
            "description": "Massive weak force carrier, mass 80.4 GeV, charge +1e",
            "mass_gev": 80.4,
            "charge": 1,
            "spin": 1,
            "particle_type": "gauge_boson",
            "phi_resonance": 1.618,
            "id": "particle_w_boson_plus"
        },
        {
            "name": "w_boson-",
            "description": "Massive weak force carrier, mass 80.4 GeV, charge -1e",
            "mass_gev": 80.4,
            "charge": -1,
            "spin": 1,
            "particle_type": "gauge_boson",
            "phi_resonance": 1.618,
            "id": "particle_w_boson_minus"
        },
        {
            "name": "z_boson",
            "description": "Neutral weak force carrier, mass 91.2 GeV",
            "mass_gev": 91.2,
            "charge": 0,
            "spin": 1,
            "particle_type": "gauge_boson",
            "phi_resonance": 1.618,
            "id": "particle_z_boson"
        },
        
        # Higgs Boson
        {
            "name": "higgs_boson",
            "description": "Particle that gives mass to other particles, discovered 2012 at 125.1 GeV",
            "mass_gev": 125.1,
            "charge": 0,
            "spin": 0,
            "particle_type": "scalar_boson",
            "phi_resonance": 1.618,
            "id": "particle_higgs_boson"
        },
        
        # Hadrons (composite particles)
        {
            "name": "proton",
            "description": "Composite particle made of two up quarks and one down quark, mass 938.3 MeV",
            "mass_gev": 0.9383,
            "charge": 1,
            "spin": 0.5,
            "particle_type": "baryon",
            "composition": "uud",
            "phi_resonance": 1.618,
            "id": "particle_proton"
        },
        {
            "name": "antiproton",
            "description": "Antiparticle of proton",
            "mass_gev": 0.9383,
            "charge": -1,
            "spin": 0.5,
            "particle_type": "baryon",
            "phi_resonance": 1.618,
            "id": "particle_antiproton"
        },
        {
            "name": "neutron",
            "description": "Composite particle made of one up quark and two down quarks, mass 939.6 MeV",
            "mass_gev": 0.9396,
            "charge": 0,
            "spin": 0.5,
            "particle_type": "baryon",
            "composition": "udd",
            "phi_resonance": 1.618,
            "id": "particle_neutron"
        },
        
        # Mesons
        {
            "name": "pion+",
            "description": "Lightest meson, up-antidown quark pair, mass 139.6 MeV",
            "mass_gev": 0.1396,
            "charge": 1,
            "spin": 0,
            "particle_type": "meson",
            "composition": "u-dbar",
            "phi_resonance": 1.618,
            "id": "particle_pion_plus"
        },
        {
            "name": "pion-",
            "description": "Lightest meson, down-antiup quark pair, mass 139.6 MeV",
            "mass_gev": 0.1396,
            "charge": -1,
            "spin": 0,
            "particle_type": "meson",
            "composition": "d-ubar",
            "phi_resonance": 1.618,
            "id": "particle_pion_minus"
        },
        {
            "name": "pion0",
            "description": "Neutral pion, mass 135.0 MeV",
            "mass_gev": 0.1350,
            "charge": 0,
            "spin": 0,
            "particle_type": "meson",
            "phi_resonance": 1.618,
            "id": "particle_pion_zero"
        },
        {
            "name": "kaon+",
            "description": "Strange meson, up-antistrange quark pair, mass 493.7 MeV",
            "mass_gev": 0.4937,
            "charge": 1,
            "spin": 0,
            "particle_type": "meson",
            "composition": "u-sbar",
            "phi_resonance": 1.618,
            "id": "particle_kaon_plus"
        },
        {
            "name": "kaon-",
            "description": "Strange meson, strange-antiup quark pair, mass 493.7 MeV",
            "mass_gev": 0.4937,
            "charge": -1,
            "spin": 0,
            "particle_type": "meson",
            "composition": "s-ubar",
            "phi_resonance": 1.618,
            "id": "particle_kaon_minus"
        },
        {
            "name": "jpsi_meson",
            "description": "Charm-anticharm bound state (charmonium), mass 3.097 GeV, discovered 1974",
            "mass_gev": 3.097,
            "charge": 0,
            "spin": 1,
            "particle_type": "meson",
            "composition": "c-cbar",
            "phi_resonance": 1.618,
            "id": "particle_jpsi_meson"
        },
        {
            "name": "upsilon_meson",
            "description": "Bottom-antibottom bound state (bottomonium), mass 9.46 GeV, discovered 1977",
            "mass_gev": 9.46,
            "charge": 0,
            "spin": 1,
            "particle_type": "meson",
            "composition": "b-bbar",
            "phi_resonance": 1.618,
            "id": "particle_upsilon_meson"
        },
        
        # Heavy ions and exotic states
        {
            "name": "lead_ion",
            "description": "Lead-208 nucleus with 82 protons and 126 neutrons",
            "mass_gev": 193.7,
            "charge": 82,
            "particle_type": "nucleus",
            "phi_resonance": 1.618,
            "id": "particle_lead_ion"
        },
        {
            "name": "quark_gluon_plasma",
            "description": "Exotic state of matter where quarks and gluons are deconfined, created at extreme temperatures",
            "particle_type": "exotic_state",
            "phi_resonance": 1.618,
            "id": "particle_qgp"
        },
        {
            "name": "lead_fragment",
            "description": "Fragment of lead nucleus after peripheral collision",
            "particle_type": "nuclear_fragment",
            "phi_resonance": 1.618,
            "id": "particle_lead_fragment"
        }
    ]
    
    # Add particles to concepts section
    print(f"Adding {len(standard_model_particles)} Standard Model particles to knowledge base...")
    
    for particle in standard_model_particles:
        # Check if particle already exists
        exists = False
        for concept in kb["concepts"]:
            if concept.get("name") == particle["name"]:
                exists = True
                break
        
        if not exists:
            kb["concepts"].append(particle)
            print(f"  Added: {particle['name']}")
        else:
            print(f"  Skipped (exists): {particle['name']}")
    
    # Update metadata
    kb["metadata"]["version"] = "3.0"
    kb["metadata"]["updated"] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    kb["metadata"]["description"] = "Contains physics concepts, equations, theories, and Standard Model particles"
    
    # Save updated knowledge base
    with open(kb_path, 'w', encoding='utf-8') as f:
        json.dump(kb, f, indent=2, ensure_ascii=False)
    
    print(f"\n✓ Knowledge base updated successfully!")
    print(f"  Total concepts: {len(kb['concepts'])}")
    print(f"  Version: {kb['metadata']['version']}")
    print(f"  Saved to: {kb_path}")

if __name__ == "__main__":
    add_standard_model_particles()
