package fraymus.field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class F0StressRunnerTest {

    @TempDir
    Path tempDir;

    @Test
    void fallsBackToJavaExecutionWhenCargoLaneIsUnavailable() {
        F0StressRunner.StressResult result = new F0StressRunner().run(tempDir);

        assertTrue(result.javaFallback());
        assertEquals(1024, result.substrateSize());
        assertTrue(result.fisherTrace() >= 0.0);
        assertTrue(result.stabilityScore() > 0.0);
        assertTrue(result.meanActivity() >= 0.0);
    }
}
