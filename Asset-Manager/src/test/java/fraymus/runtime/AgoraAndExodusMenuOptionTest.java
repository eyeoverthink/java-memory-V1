package fraymus.runtime;

import com.fraymus.simulation.GenesisSandbox;
import fraymus.os.FrayFSBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AgoraAndExodusMenuOptionTest {

    @Test
    void agoraForgeRoundTripsZeroWidthPayload() {
        String carrier = "Fraynix status nominal";
        String payload = "EXPECT_CREATE_BOOT_AND_DETECT";

        String infected = GenesisSandbox.forgeHyperText(carrier, payload);
        String extracted = GenesisSandbox.extractHyperText(infected);

        long zeroWidthCount = infected.chars()
            .filter(c -> c == 0x200B || c == 0x200C || c == 0x200D || c == 0x2060)
            .count();

        assertTrue(infected.length() > carrier.length(), "forged text should carry invisible payload characters");
        assertTrue(zeroWidthCount > 0, "forged text should contain Agora zero-width alphabet characters");
        assertEquals(payload, extracted);
    }

    @Test
    void exodusMenuBuilderCreatesValidBootSector(@TempDir Path tempDir) throws Exception {
        ProcessResult result = runHelperIn(tempDir);
        assertEquals(0, result.exitCode, result.output);

        Path image = tempDir.resolve("fraynix_genesis.img");
        assertTrue(Files.exists(image), "FrayFSBuilder.executeExodus should write fraynix_genesis.img");

        byte[] bytes = Files.readAllBytes(image);
        assertEquals(2048, bytes.length, "Exodus image should contain one MBR sector plus three stage-2 sectors");
        assertEquals((byte) 0x55, bytes[510], "MBR signature byte 510 should be 0x55");
        assertEquals((byte) 0xAA, bytes[511], "MBR signature byte 511 should be 0xAA");

        String ascii = new String(bytes, StandardCharsets.US_ASCII);
        assertTrue(ascii.contains("FRAYNIX STAGE2 KERNEL ONLINE"),
            "staged image should contain the expected stage-2 boot message");
        assertTrue(ascii.contains("BIOS MBR LOADER -> SECOND-STAGE HANDOFF VERIFIED"),
            "staged image should contain the expected loader handoff proof");

        FrayFSBuilder.ExodusImageReport report = new FrayFSBuilder().verifyExodusImage(image);
        assertTrue(report.isBootableSector(), "verified image should satisfy the boot-sector contract");
        assertEquals(2048, report.size());
        assertTrue(report.validSignature());
        assertTrue(report.validMessage());
        assertEquals("5E2316816CDE82424324B535CE5454C1CA3D664D9E4B81E06D042DC41D81BDBB", report.sha256());
    }

    @Test
    void exodusLaunchRejectsInvalidImageWithoutStartingQemu(@TempDir Path tempDir) throws Exception {
        Path invalidImage = tempDir.resolve("not-an-mbr.img");
        Files.write(invalidImage, "not a boot sector".getBytes(StandardCharsets.US_ASCII));

        FrayFSBuilder.QemuLaunchReport report = new FrayFSBuilder().launchExodusInQemu(invalidImage);

        assertFalse(report.launched());
        assertEquals("Image failed boot-sector verification.", report.message());
        assertTrue(report.command().isEmpty());
    }

    private static ProcessResult runHelperIn(Path workingDirectory) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
            Path.of(System.getProperty("java.home"), "bin", "java").toString(),
            "-cp",
            System.getProperty("java.class.path"),
            ExodusInvocationHelper.class.getName()
        )
            .directory(workingDirectory.toFile())
            .redirectErrorStream(true)
            .start();

        boolean completed = process.waitFor(20, TimeUnit.SECONDS);
        if (!completed) {
            process.destroyForcibly();
            fail("Exodus helper process did not complete");
        }

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return new ProcessResult(process.exitValue(), output);
    }

    private record ProcessResult(int exitCode, String output) {
    }
}
