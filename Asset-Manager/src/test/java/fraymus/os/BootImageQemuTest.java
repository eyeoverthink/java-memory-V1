package fraymus.os;

import fraymus.exodus.ExodusStage2Kernel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BootImageQemuTest {
    public static void main(String[] args) throws Exception {
        // Generate boot image
        FrayFSBuilder builder = new FrayFSBuilder();
        Path imagePath = Paths.get("F:/Full-AI-System/test_exodus_boot.img");
        var report = builder.writeExodusImage(imagePath);
        
        System.out.println("Boot image: " + imagePath);
        System.out.println("Image size: " + Files.size(imagePath) + " bytes");
        System.out.println("Expected: 2048 bytes (512 MBR + 1536 stage2)");
        
        // Verify image structure
        byte[] image = Files.readAllBytes(imagePath);
        if (image.length != 2048) {
            System.out.println("FAIL: Image size wrong");
            return;
        }
        
        // Check boot signature at end of MBR (offset 510-511)
        boolean hasSignature = (image[510] & 0xFF) == 0x55 && (image[511] & 0xFF) == 0xAA;
        System.out.println("Boot signature 0x55AA: " + (hasSignature ? "PASS" : "FAIL"));
        
        // Verify stage-2 is non-zero
        boolean stage2NonZero = false;
        for (int i = 512; i < image.length; i++) {
            if (image[i] != 0) {
                stage2NonZero = true;
                break;
            }
        }
        System.out.println("Stage-2 non-zero: " + (stage2NonZero ? "PASS" : "FAIL"));
        
        // Try QEMU if available
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "qemu-system-x86_64",
                "-drive", "format=raw,file=" + imagePath,
                "-m", "32M",
                "-display", "none",
                "-serial", "stdio",
                "-no-reboot",
                "-nographic"
            );
            pb.inheritIO();
            Process p = pb.start();
            
            // Give QEMU 3 seconds to boot and print messages
            Thread.sleep(3000);
            p.destroyForcibly();
            System.out.println("QEMU test completed (check serial output above)");
        } catch (Exception e) {
            System.out.println("QEMU not available or failed: " + e.getMessage());
            System.out.println("Image written to: " + imagePath);
            System.out.println("Manual test: qemu-system-x86_64 -drive format=raw,file=" + imagePath);
        }
    }
}
