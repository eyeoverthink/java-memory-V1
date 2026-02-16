package fraynix;

import fraynix.brain.HyperTesseract;
import fraynix.core.*;
import fraynix.core.impl.DefaultIntentBus;
import fraynix.kernel.*;
import fraynix.observe.EventLogger;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KernelIntentFlowTest {

    @Test
    void kernel_healthCheck_returnsServices() throws Exception {
        try (EventLogger logger = new EventLogger(Path.of(".fraynix", "test-logs"))) {
            HyperTesseract brain = new HyperTesseract(42);
            DefaultIntentBus bus = new DefaultIntentBus();
            bus.setLogger(logger);

            Scheduler scheduler = new PriorityScheduler();
            FrayAbstractKernel kernel = new FrayAbstractKernel(brain, bus, logger, scheduler, 2);

            kernel.start();

            Intent health = Intent.builder()
                .type(Intent.Type.HEALTH_CHECK)
                .origin("test")
                .build();

            IntentBus.IntentResult result = bus.request(health, 2000).get();
            assertTrue(result.success());
            assertNotNull(result.data());

            kernel.stop();
        }
    }
}
