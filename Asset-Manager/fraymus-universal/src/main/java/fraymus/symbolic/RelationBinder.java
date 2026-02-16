package fraymus.symbolic;

import fraymus.hyper.HyperVector;

public class RelationBinder {

    /**
     * Encodes a Triplet: (Subject, Relation, Object)
     * e.g., (Sky, Is, Blue) -> V_Sky * V_Is * V_Blue
     */
    public HyperVector encodeFact(HyperVector subject, HyperVector relation, HyperVector object) {
        // We permute the relation to distinguish (A, Rel, B) from (B, Rel, A)
        // Fact = Subject * P(Relation) * P(P(Object))
        HyperVector rP = relation.permute(1);
        HyperVector oP = object.permute(2);

        return subject.bind(rP).bind(oP);
    }

    /**
     * QUERY: Given Subject and Relation, find Object?
     * Since XOR is its own inverse:
     * Object_Estimate = Fact * Subject * P(Relation)
     * Then we permute back (-2) to align with the Object prototype.
     */
    public HyperVector query(HyperVector factHologram, HyperVector subject, HyperVector relation) {
        HyperVector rP = relation.permute(1);

        // Unbind Subject and Relation
        HyperVector raw = factHologram.bind(subject).bind(rP);

        // Reverse the Object permutation (shift -2)
        return raw.permute(-2);
    }
}
