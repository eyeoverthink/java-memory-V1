package fraymus.symbolic;

import fraymus.hyper.HyperVector;
import java.io.Serializable;

/**
 * 🔗 RELATION BINDER - The Syntax of Truth
 * "Mathematical encoding of facts as triplets"
 * 
 * Standard Knowledge Graph:
 * - Nodes stored in database
 * - Edges stored in database
 * - Query: O(N) or O(log N) search
 * 
 * Holo-Graph:
 * - Everything compressed into single 10,000D vector
 * - Query: O(1) XOR operations
 * - Storage: Always 1.25 KB regardless of fact count
 * 
 * Encoding Formula:
 * Fact = Subject ⊕ Π(Relation) ⊕ Π²(Object)
 * 
 * Example:
 * - (Paris, CapitalOf, France)
 * - Fact = V_Paris ⊕ Π(V_CapitalOf) ⊕ Π²(V_France)
 * 
 * Query:
 * - Given: Subject + Relation
 * - Find: Object
 * - Object ≈ Fact ⊕ Subject ⊕ Π(Relation), then Π⁻²
 */
public class RelationBinder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Encodes a Triplet: (Subject, Relation, Object)
     * 
     * We use permutation to distinguish positions:
     * - Subject: no permutation (shift 0)
     * - Relation: permute 1 step
     * - Object: permute 2 steps
     * 
     * This ensures (A, Rel, B) ≠ (B, Rel, A)
     */
    public HyperVector encodeFact(HyperVector subject, HyperVector relation, HyperVector object) {
        // Permute to distinguish positions
        HyperVector rP = relation.permute(1);
        HyperVector oP = object.permute(2);
        
        // XOR binding (reversible)
        return subject.bind(rP).bind(oP);
    }

    /**
     * QUERY: Given Subject and Relation, find Object
     * 
     * Process:
     * 1. Unbind Subject: Fact ⊕ Subject
     * 2. Unbind Relation: Result ⊕ Π(Relation)
     * 3. Reverse Object permutation: Π⁻²
     * 
     * Math: (S ⊕ Π(R) ⊕ Π²(O)) ⊕ S ⊕ Π(R) = Π²(O)
     * Then: Π⁻²(Π²(O)) = O
     */
    public HyperVector query(HyperVector factHologram, HyperVector subject, HyperVector relation) {
        HyperVector rP = relation.permute(1);
        
        // Unbind Subject and Relation
        HyperVector raw = factHologram.bind(subject).bind(rP);
        
        // Reverse the Object permutation (shift -2)
        return raw.permute(-2);
    }

    /**
     * INVERSE QUERY: Given Object and Relation, find Subject
     * 
     * Useful for reverse lookups:
     * - "What is Paris the capital of?" → France
     */
    public HyperVector inverseQuery(HyperVector factHologram, HyperVector object, HyperVector relation) {
        HyperVector rP = relation.permute(1);
        HyperVector oP = object.permute(2);
        
        // Unbind Relation and Object
        return factHologram.bind(rP).bind(oP);
        // Subject has no permutation, so no need to reverse
    }
}
