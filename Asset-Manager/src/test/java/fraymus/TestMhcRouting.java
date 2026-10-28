package fraymus;

import fraymus.living.TriMe;
import fraymus.neural.MoERouter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMhcRouting {

    @Test
    void testRoutingPlanIsNearDoublyStochastic() {
        MoERouter router = new MoERouter(8, 8, 2);
        MoERouter.RoutingPlan plan = router.analyze(new double[]{0.8, 0.1, 0.6, 0.2, 0.4, 0.9, 0.3, 0.7});

        assertTrue(plan.isDoublyStochastic(0.05), "Sinkhorn/Birkhoff routing should preserve near-unit row/col sums");
    }

    @Test
    void testMergedOutputRemainsBounded() {
        MoERouter router = new MoERouter(8, 8, 2);
        MoERouter.RoutingPlan plan = router.analyze(new double[]{10, -9, 7, -6, 5, -4, 3, -2});

        for (double value : plan.mergedOutput) {
            assertTrue(Math.abs(value) <= plan.config.boundedSignalMagnitude + 1e-9,
                    "Merged signal should stay inside bounded magnitude envelope");
        }
        assertTrue(plan.peakMagnitude <= plan.config.boundedSignalMagnitude + 1e-9,
                "Peak magnitude should match bounded envelope");
    }

    @Test
    void testRouterExposesMhcStatus() {
        MoERouter router = new MoERouter(8, 8, 2);
        router.analyze(new double[]{0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5});

        String status = router.getHyperStatus();
        assertTrue(status.contains("MHC-27B"));
        assertTrue(status.contains("streams="));
        assertTrue(status.contains("kernel=tailang"));
    }

    @Test
    void testRouterPreservesCustomExpertNames() {
        MoERouter router = new MoERouter(
                6,
                new String[]{"KRONOS", "OLLAMA", "OMEGA"},
                2,
                MoERouter.HyperConfig.mhc27B()
        );

        MoERouter.RoutingPlan plan = router.analyze(new double[]{1.2, 0.8, 0.4, 0.3, 0.2, 0.1});

        assertTrue(plan.selectedExpertNames.length >= 2);
        assertTrue(plan.selectedExpertNames[0].equals("KRONOS")
                        || plan.selectedExpertNames[0].equals("OLLAMA")
                        || plan.selectedExpertNames[0].equals("OMEGA"));
    }

    @Test
    void testTriMeSurfacesRoutingStatus() {
        TriMe triMe = new TriMe();
        triMe.deepThink(new double[]{0.2, 0.9, 0.3, 0.7, 0.1, 0.8, 0.4, 0.6});

        String status = triMe.getRoutingStatus();
        assertTrue(status.contains("MHC-27B"));
        assertTrue(status.contains("route"));
    }
}
