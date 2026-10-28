package fraymus.factory.kernel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;

/** Executed as a real verifier subprocess; validates the exact artifact submitted by the studio. */
public final class ArtifactValidator {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("Expected artifact and SHA-256");
        byte[] data = Files.readAllBytes(Path.of(args[0]));
        if (data.length == 0) throw new IllegalArgumentException("Empty scene artifact");
        String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
        if (!hash.equals(args[1])) throw new IllegalStateException("Scene artifact changed during inspection");
        System.out.println("factory.artifact.sha256 passed: " + hash);
    }
}
