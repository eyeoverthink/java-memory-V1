package brain_architecture.kernel;

import fraymus.hdc.SharedHypervectorCore;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KernelHypervectorSpaceUnificationTest {

    @Test
    void kernelHypervectorMatchesSharedCoreEncoding() {
        KernelHypervectorSpace kernelSpace = new KernelHypervectorSpace();
        SharedHypervectorCore sharedCore = new SharedHypervectorCore();

        String text = "repair chained parser and import failures offline";
        BitSet kernelVector = kernelSpace.encodeText(text);
        BitSet sharedVector = sharedCore.encodeText(text);

        assertEquals(sharedVector, kernelVector);
    }

    @Test
    void kernelTokenizationMatchesSharedCore() {
        KernelHypervectorSpace kernelSpace = new KernelHypervectorSpace();
        SharedHypervectorCore sharedCore = new SharedHypervectorCore();

        String text = "UseMultiStepChain.java parser-import repair";
        List<String> kernelTokens = kernelSpace.tokenize(text);
        List<String> sharedTokens = sharedCore.tokenize(text);

        assertEquals(sharedTokens, kernelTokens);
        assertTrue(kernelTokens.contains("usemultistepchain.java"));
    }

    @Test
    void kernelCosineSimilarityMatchesSharedCore() {
        KernelHypervectorSpace kernelSpace = new KernelHypervectorSpace();
        SharedHypervectorCore sharedCore = new SharedHypervectorCore();

        BitSet a = kernelSpace.encodeText("import failure chain");
        BitSet b = kernelSpace.encodeText("import failure fix");

        double kernelCosine = kernelSpace.cosineSimilarity(a, b);
        double sharedCosine = sharedCore.cosineSimilarity(a, b);

        assertEquals(sharedCosine, kernelCosine, 1e-12);
    }
}
