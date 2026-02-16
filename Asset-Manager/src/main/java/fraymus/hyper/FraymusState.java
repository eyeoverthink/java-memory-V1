package fraymus.hyper;

import java.io.Serializable;
import java.util.Map;

/**
 * Immutable snapshot of HyperFormer state.
 * Used for secure serialization in Cortical Stack Protocol.
 */
public record FraymusState(
        long globalSeed,
        Map<String, HyperVector> prototypes,
        MultiScaleMemory.State memory
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
