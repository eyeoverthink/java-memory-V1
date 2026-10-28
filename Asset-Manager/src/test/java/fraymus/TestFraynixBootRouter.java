package fraymus;

import fraymus.knowledge.AkashicRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestFraynixBootRouter {

    @TempDir
    Path tempDir;

    @Test
    void routesWholeSystemDiscoveryAndContinuityPromptsThroughUltimateAi() throws Exception {
        Method routerMethod = FraynixBoot.class.getDeclaredMethod("shouldRouteThroughUltimateAi", String.class);
        routerMethod.setAccessible(true);

        assertTrue((Boolean) routerMethod.invoke(null, "use all menu systems together and tell me what works"));
        assertTrue((Boolean) routerMethod.invoke(null, "I should not have to specify, show every system that works"));
        assertTrue((Boolean) routerMethod.invoke(null, "resume fraynix state"));
        assertTrue((Boolean) routerMethod.invoke(null, "resume live and recheck what works now"));
        assertTrue((Boolean) routerMethod.invoke(null, "continuity menus"));
        assertTrue((Boolean) routerMethod.invoke(null, "show morse emf willow babel and everything else"));
    }

    @Test
    void biasesWholeSystemPromptsFromAkashicContinuityMemory() throws Exception {
        String originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        try {
            AkashicRecord akashic = new AkashicRecord();
            akashic.purge();
            akashic.addBlock("MENU_HEAD", "morse");
            akashic.addBlock("MENU_HEAD", "willow");
            akashic.addBlock("DETERMINISTIC_LANE", "babel symbolic compiler lane");
            akashic.addBlock("CONTINUITY_RULE", "prefer absorbed continuity organs before generic synthesis");
            akashic.addBlock("OBSERVED_LIVE_HEAD", "native -> observed-live [airun/in-process] native status :: status ok");
            akashic.addBlock("DEGRADED_HEAD", "emf -> degraded [airun/in-process] emf status :: timeout");

            Field akashicField = FraynixBoot.class.getDeclaredField("akashic");
            akashicField.setAccessible(true);
            Object previousAkashic = akashicField.get(null);
            akashicField.set(null, akashic);

            Field resumeField = FraynixBoot.class.getDeclaredField("continuityResumeState");
            resumeField.setAccessible(true);
            Object previousResume = resumeField.get(null);
            resumeField.set(null, "FRAYNIX RESUME STATE visible heads loaded");

            try {
                Method biasMethod = FraynixBoot.class.getDeclaredMethod("applyAkashicContinuityBias", String.class);
                biasMethod.setAccessible(true);
                String biased = (String) biasMethod.invoke(null, "use all menu systems together and tell me what works");
                assertTrue(biased.contains("[Akashic continuity bias]"));
                assertTrue(biased.contains("morse"));
                assertTrue(biased.contains("willow"));
                assertTrue(biased.contains("babel"));
                assertTrue(biased.contains("native"));
                assertTrue(biased.contains("emf"));
                assertTrue(biased.contains("prefer absorbed continuity organs before generic synthesis"));
            } finally {
                resumeField.set(null, previousResume);
                akashicField.set(null, previousAkashic);
                akashic.purge();
            }
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void syncsLiveCapabilitySnapshotsIntoAkashicMemory() throws Exception {
        String originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        try {
            AkashicRecord akashic = new AkashicRecord();
            akashic.purge();

            Field akashicField = FraynixBoot.class.getDeclaredField("akashic");
            akashicField.setAccessible(true);
            Object previousAkashic = akashicField.get(null);
            akashicField.set(null, akashic);

            Field probeVersionField = FraynixBoot.class.getDeclaredField("capabilityProbeOrganismVersion");
            probeVersionField.setAccessible(true);
            Object previousProbeVersion = probeVersionField.get(null);
            probeVersionField.set(null, "");

            Field probeSyncCountField = FraynixBoot.class.getDeclaredField("capabilityProbeOrganismSyncCount");
            probeSyncCountField.setAccessible(true);
            Object previousProbeSyncCount = probeSyncCountField.get(null);
            probeSyncCountField.setInt(null, 0);

            try {
                Method syncMethod = FraynixBoot.class.getDeclaredMethod(
                    "syncCapabilityProbesIntoOrganism",
                    fraymus.ultimate.UltimateSystem.LiveCapabilitySnapshot.class
                );
                syncMethod.setAccessible(true);

                fraymus.ultimate.UltimateSystem.LiveCapabilitySnapshot snapshot =
                    new fraymus.ultimate.UltimateSystem.LiveCapabilitySnapshot(
                        "2026-04-26T10:17:00Z",
                        "use all menu systems together and tell me what works",
                        java.util.List.of(
                            new fraymus.ultimate.UltimateSystem.CapabilityProbeResult(
                                "native", "native status", "observed-live", "airun/in-process", "status=ok", "ULTIMATE IMMORTAL KERNEL"
                            ),
                            new fraymus.ultimate.UltimateSystem.CapabilityProbeResult(
                                "emf", "emf status", "degraded", "airun/in-process", "status=timeout", "probe timed out"
                            ),
                            new fraymus.ultimate.UltimateSystem.CapabilityProbeResult(
                                "morse", "catalog-visible", "visible-only", "catalog", "section=morse", "Launch via launch fraynix morse"
                            )
                        ),
                        1,
                        1,
                        1,
                        0
                    );
                syncMethod.invoke(null, snapshot);

                assertTrue(akashic.queryCategory("OBSERVED_LIVE_HEAD").stream()
                    .anyMatch(block -> block.content.contains("native -> observed-live")));
                assertTrue(akashic.queryCategory("DEGRADED_HEAD").stream()
                    .anyMatch(block -> block.content.contains("emf -> degraded")));
                assertTrue(akashic.queryCategory("VISIBLE_ONLY_HEAD").stream()
                    .anyMatch(block -> block.content.contains("morse -> visible-only")));

                Method biasMethod = FraynixBoot.class.getDeclaredMethod("applyAkashicContinuityBias", String.class);
                biasMethod.setAccessible(true);
                String biased = (String) biasMethod.invoke(null, "what works now across every system");
                assertTrue(biased.contains("native"));
                assertTrue(biased.contains("emf"));
            } finally {
                probeSyncCountField.set(null, previousProbeSyncCount);
                probeVersionField.set(null, previousProbeVersion);
                akashicField.set(null, previousAkashic);
                akashic.purge();
            }
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }
}
