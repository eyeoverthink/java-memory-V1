package fraymus.hyper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * FraymusState: The frozen soul.
 * Pure data transfer object. No logic. Safe for serialization.
 * Matches the new Association-based HyperFormer internals.
 */
public record FraymusState(
        Map<String, HyperVector> embeddings,
        List<AssociationDTO> associations,
        int contextWindow,
        double resonanceThreshold
) implements Serializable {

    /** Flat DTO for HyperFormer.Association (which is package-private). */
    public record AssociationDTO(
            HyperVector context,
            String nextWord,
            HyperVector nextVector
    ) implements Serializable {}
}
