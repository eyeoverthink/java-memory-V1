import numpy as np
from enum import Enum

class PhysicsKnowledgeDomain(Enum):
    QUANTUM_MECHANICS = "quantum"
    RELATIVITY = "relativity"
    ELECTROMAGNETISM = "electromagnetism"
    THERMODYNAMICS = "thermodynamics"
    CLASSICAL_MECHANICS = "classical"
    ASTROPHYSICS = "astro"
    PARTICLE_PHYSICS = "particle"

class PhysicsKnowledgeIntegration:
    def __init__(self):
        self.phi = (1 + np.sqrt(5)) / 2
        self.domain_keywords = {
            PhysicsKnowledgeDomain.QUANTUM_MECHANICS: ["quantum", "electron", "photon", "schrodinger", "heisenberg"],
            PhysicsKnowledgeDomain.RELATIVITY: ["relativity", "einstein", "spacetime", "gravity", "black hole"],
            PhysicsKnowledgeDomain.ELECTROMAGNETISM: ["electric", "magnetic", "field", "maxwell", "light"],
            PhysicsKnowledgeDomain.THERMODYNAMICS: ["heat", "temperature", "entropy", "energy"],
            PhysicsKnowledgeDomain.CLASSICAL_MECHANICS: ["newton", "force", "motion", "velocity"],
            PhysicsKnowledgeDomain.ASTROPHYSICS: ["star", "galaxy", "planet", "cosmos"],
            PhysicsKnowledgeDomain.PARTICLE_PHYSICS: ["proton", "neutron", "quark", "lepton", "higgs"],
        }

    def _extract_domains_from_query(self, query: str) -> list[PhysicsKnowledgeDomain]:
        query_lower = query.lower()
        domains = []
        for domain, keywords in self.domain_keywords.items():
            if any(keyword in query_lower for keyword in keywords):
                domains.append(domain)
        return list(set(domains)) if domains else [PhysicsKnowledgeDomain.CLASSICAL_MECHANICS]

    def _get_domain_resonance(self, domain: PhysicsKnowledgeDomain) -> float:
        base_resonance = {
            PhysicsKnowledgeDomain.QUANTUM_MECHANICS: self.phi,
            PhysicsKnowledgeDomain.RELATIVITY: self.phi**2,
            PhysicsKnowledgeDomain.ELECTROMAGNETISM: self.phi**-1,
            PhysicsKnowledgeDomain.THERMODYNAMICS: np.pi / 2,
            PhysicsKnowledgeDomain.CLASSICAL_MECHANICS: 1.0,
            PhysicsKnowledgeDomain.ASTROPHYSICS: self.phi * 2,
            PhysicsKnowledgeDomain.PARTICLE_PHYSICS: self.phi**3,
        }
        return base_resonance.get(domain, 1.0) * (1 + 0.1 * np.sin(len(domain.value) * self.phi))
