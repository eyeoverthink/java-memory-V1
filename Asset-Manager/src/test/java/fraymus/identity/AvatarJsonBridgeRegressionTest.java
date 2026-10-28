package fraymus.identity;

import java.nio.file.Files;
import java.nio.file.Path;

/** Run only in the disposable fixture created by scripts/verify-avatar-json.py. */
public final class AvatarJsonBridgeRegressionTest {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        check(Files.exists(Path.of("AVATAR_JSON_TEST_FIXTURE")), "Owned fixture marker required");
        var unavailable = OpenCvFaceAbstraction.captureFromCamera(0, true);
        check(!unavailable.success(), "Unavailable backend must fail normally");
        check(unavailable.message().equals("fixture: OpenCV unavailable"), "Backend error preserved");
        long calls = Files.readAllLines(Path.of("capture-invocations.txt")).size();
        check(!OpenCvFaceAbstraction.captureFromCamera(1, false).success(), "Permission gate");
        check(!OpenCvFaceAbstraction.captureFromCamera(-1, true).success(), "Invalid index gate");
        check(Files.readAllLines(Path.of("capture-invocations.txt")).size() == calls,
            "Blocked capture must not launch a process");

        var capture = OpenCvFaceAbstraction.captureFromCamera(1, 1, true);
        check(capture.success(), "Valid capture payload: " + capture.message());
        check(capture.message().equals("fixture \u03c6 at C:\\camera\\frame \"one\""), "Escapes and Unicode");
        check(capture.observation().interocularDistance() == 0.125, "Scalar observation");
        check(capture.observation().landmarkVector()[1] == 0.5, "Numeric string in vector");
        check(capture.livenessSignals().depthVariance() == 1e-7, "Scientific notation");
        check(capture.livenessSignals().simulated(), "Simulation flag remains explicit");
        check(OpenCvFaceAbstraction.toSovereignVector(capture.observation()).length == 11,
            "Observation vector dimensions");
        check(Files.readAllLines(Path.of("capture-invocations.txt")).getLast().equals("1,16"),
            "Minimum frame count passed to fixture");

        for (int camera : new int[] {2, 3, 4, 6, 7}) {
            var malformed = OpenCvFaceAbstraction.captureFromCamera(camera, true);
            check(!malformed.success() && malformed.message().startsWith("Capture parse failed:"),
                "Malformed response must fail without terminating: fixture=" + camera);
        }
        var optional = OpenCvFaceAbstraction.captureFromCamera(5, true);
        check(optional.success(), "Missing optional values keep defaults");
        check(optional.observation().interocularDistance() == 0.0, "Invalid optional scalar default");
        check(optional.observation().grayscaleHistogram().length == 0, "Invalid optional array default");

        var scan = DeepfakeScanService.scanFile("ok.png");
        check(scan.success(), "Valid scan payload: " + scan.message());
        check(scan.images().size() == 1 && scan.images().get(0).faces() == 1, "Scan image fields");
        check(scan.images().get(0).notes().equals("fixture one; fixture two"), "Scan notes");
        check(scan.images().get(0).provenanceSignals().equals("local fixture"), "Provenance notes");
        check(!DeepfakeScanService.scanFile("failure.png").success(), "Scan backend failure");
        check(!DeepfakeScanService.scanFile("malformed.png").success(), "Malformed scan failure");
        check(!DeepfakeScanService.scanFile("wrong-images.png").success(), "Invalid image array failure");
        check(DeepfakeScanService.scanFile("empty.png").images().isEmpty(), "Optional image list");
        check(!DeepfakeScanService.scanFile("missing.png").success(), "Missing file failure");
        System.out.println("AvatarJsonBridgeRegressionTest passed: " + checks + " checks; no camera or network used");
    }
}
