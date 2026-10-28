package fraymus.exodus;

import fraymus.os.FrayFSBuilder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/** Standalone regression checks; failures throw and return a nonzero process exit code. */
public class ExodusStage2KernelTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        Path fixture = args.length == 0 ? Files.createTempDirectory("exodus-test-") : Path.of(args[0]);
        Files.createDirectories(fixture);
        byte[] kernel = ExodusStage2Kernel.generateKernel();
        check(kernel.length == 1536, "Three complete stage-two sectors");
        check(Arrays.equals(kernel, ExodusStage2Kernel.generateKernel()), "Deterministic generation");
        String text = new String(kernel, StandardCharsets.US_ASCII);
        check(text.contains(ExodusStage2Kernel.BOOT_TEXT), "Canonical boot banner");
        check(text.contains(ExodusStage2Kernel.HANDOFF_TEXT), "Canonical handoff banner");
        check(text.contains(ExodusStage2Kernel.MEMORY_TEXT), "Memory initialization banner");

        var executor = Executors.newFixedThreadPool(8);
        try {
            List<Callable<byte[]>> calls = new ArrayList<>();
            for (int i = 0; i < 64; i++) calls.add(ExodusStage2Kernel::generateKernel);
            for (var result : executor.invokeAll(calls)) {
                check(Arrays.equals(kernel, result.get()), "Concurrent generation remains deterministic");
            }
        } finally {
            executor.shutdownNow();
        }

        FrayFSBuilder builder = new FrayFSBuilder();
        Path imagePath = fixture.resolve("fraynix_genesis.img");
        var report = builder.writeExodusImage(imagePath);
        byte[] image = Files.readAllBytes(imagePath);
        check(report.isBootableSector(), "Canonical image accepted");
        check(report.size() == 2048, "One loader plus three stage-two sectors");
        check(report.validSignature(), "BIOS signature accepted");
        check(report.validMessage(), "Full generated payload accepted");
        check(report.message().equals(ExodusStage2Kernel.BOOT_TEXT), "Boot report uses canonical banner");
        check(report.sha256().matches("[0-9A-F]{64}"), "SHA-256 reported");
        check(Arrays.equals(kernel, Arrays.copyOfRange(image, 512, 2048)), "Image contains generated kernel");
        check(builder.writeExodusImage(fixture.resolve("second.img")).sha256().equals(report.sha256()),
            "Full image generation remains deterministic");

        for (int offset : new int[] {0, 510, 511, 512, 540, 2047}) {
            byte[] modified = image.clone();
            modified[offset] ^= 1;
            Path broken = fixture.resolve("modified-" + offset + ".img");
            Files.write(broken, modified);
            check(!builder.verifyExodusImage(broken).isBootableSector(), "Reject changed image byte " + offset);
        }
        Path truncated = fixture.resolve("truncated.img");
        Files.write(truncated, Arrays.copyOf(image, 512));
        var invalid = builder.verifyExodusImage(truncated);
        check(invalid.validSignature(), "Truncated fixture still has signature");
        check(!invalid.isBootableSector(), "Signature alone cannot pass validation");
        check(!invalid.message().contains("LOAD FAILED"), "Report does not present embedded error text as execution");
        check(!builder.launchExodusInQemu(truncated).launched(), "Invalid image cannot launch an emulator");
        Files.write(fixture.resolve("empty.img"), new byte[0]);
        check(!builder.verifyExodusImage(fixture.resolve("empty.img")).isBootableSector(), "Reject empty image");
        byte[] extended = Arrays.copyOf(image, 2560);
        Files.write(fixture.resolve("extended.img"), extended);
        check(!builder.verifyExodusImage(fixture.resolve("extended.img")).isBootableSector(), "Reject extra sectors");
        System.out.println("PASS: " + checks + " Exodus generation/integrity checks");
        System.out.println("Image: " + imagePath.toAbsolutePath());
        System.out.println("SHA-256: " + report.sha256());
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }
}
