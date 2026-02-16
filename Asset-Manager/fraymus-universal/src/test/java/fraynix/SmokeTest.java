package fraynix;

import fraynix.brain.HyperTesseract;
import fraynix.core.Intent;
import fraynix.core.impl.DefaultIntentBus;
import fraynix.kernel.PriorityScheduler;
import fraynix.kernel.RoundRobinScheduler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SmokeTest {

    @Test
    void reproducibility_sameSeed_sameState() {
        long seed = 12345L;
        HyperTesseract b1 = new HyperTesseract(seed);
        HyperTesseract b2 = new HyperTesseract(seed);

        b1.injectThought("hello", 0.5);
        b2.injectThought("hello", 0.5);

        for (int i = 0; i < 10; i++) {
            b1.tick();
            b2.tick();
        }

        var s1 = b1.captureState();
        var s2 = b2.captureState();

        assertEquals(s1.getActiveNodeCount(), s2.getActiveNodeCount());
        assertTrue(Math.abs(s1.getAverageActivation() - s2.getAverageActivation()) < 0.001);
    }

    @Test
    void intentBus_backpressure_doesNotDeadlock() throws Exception {
        DefaultIntentBus bus = new DefaultIntentBus(5, 100, 2);
        bus.start();

        for (int i = 0; i < 100; i++) {
            bus.publish(Intent.builder().type(Intent.Type.SHELL_COMMAND).origin("test").build());
        }

        // allow some processing
        Thread.sleep(100);

        assertTrue(bus.getPublishedCount() > 0);
        assertTrue(bus.getProcessedCount() + bus.getFailedCount() > 0);

        bus.stop();
    }
}
