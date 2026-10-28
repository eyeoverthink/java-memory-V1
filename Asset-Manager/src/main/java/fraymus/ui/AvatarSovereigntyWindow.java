package fraymus.ui;

import fraymus.identity.BiometricAvatarService;
import fraymus.vision.AvatarGenerator;
import fraymus.vision.FacialFingerprintExtractor;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dedicated identity/vision workspace for avatar sovereignty:
 * - OpenCV permission gate (operator-controlled)
 * - Scott extraction and avatar generation lane
 * - Local biometric vault enroll/verify/revoke/export/delete controls
 */
public final class AvatarSovereigntyWindow extends JFrame {

    private static final String MODE_BAREMETAL = "BAREMETAL_SCOTT";
    private static final String MODE_OPENCV = "OPENCV_ASSISTED";

    private final BiometricAvatarService service;
    private final Path openCvPermissionPath;

    private final JTextArea logArea;
    private final JTextField subjectField;
    private final JPasswordField secretField;
    private final JTextField consentField;
    private final JTextArea vectorArea;
    private final JTextField blinkField;
    private final JTextField depthField;
    private final JTextField parallaxField;
    private final JTextField textureField;
    private final JTextField thermalField;

    private final JComboBox<String> modeBox;
    private final JCheckBox openCvAllowed;
    private final JTextField imagePathField;

    private AvatarSovereigntyWindow(BiometricAvatarService service) {
        super("Fraymus Avatar Sovereignty Workbench");
        this.service = service;
        this.openCvPermissionPath = Paths.get("genesis_vault", "biometric_avatar", "opencv.permission");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(1120, 760));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        subjectField = new JTextField("alice");
        secretField = new JPasswordField("mySecret99");
        consentField = new JTextField("I CONSENT to enroll my biometric identity in the local vault");
        vectorArea = new JTextArea(defaultVectorCsv(), 6, 80);
        vectorArea.setLineWrap(true);
        vectorArea.setWrapStyleWord(true);

        blinkField = new JTextField("0.32");
        depthField = new JTextField("0.06");
        parallaxField = new JTextField("0.24");
        textureField = new JTextField("0.12");
        thermalField = new JTextField("0.08");

        modeBox = new JComboBox<>(new String[]{MODE_BAREMETAL, MODE_OPENCV});
        openCvAllowed = new JCheckBox("Allow OpenCV-assisted capture on this device");
        imagePathField = new JTextField("F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\green-apple.PNG");

        loadPermissionState();

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createTitledBorder("Workbench Log"));

        add(buildTabs(), BorderLayout.CENTER);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        log("Avatar workbench online.");
        if (service != null) {
            log("Service status: " + service.statusSummary());
        } else {
            log("Service unavailable. Boot with avatar service enabled.");
        }
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Avatar Service", buildServicePanel());
        tabs.add("OpenCV + Scott + Avatar", buildVisionPanel());
        return tabs;
    }

    private JPanel buildServicePanel() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.setBorder(BorderFactory.createTitledBorder("Identity Inputs"));
        fields.add(new JLabel("Subject ID"));
        fields.add(subjectField);
        fields.add(new JLabel("Secret"));
        fields.add(secretField);
        fields.add(new JLabel("Consent Text"));
        fields.add(consentField);
        fields.add(new JLabel("Mode"));
        fields.add(modeBox);
        fields.add(new JLabel("Blink"));
        fields.add(blinkField);
        fields.add(new JLabel("Depth"));
        fields.add(depthField);
        fields.add(new JLabel("Parallax"));
        fields.add(parallaxField);
        fields.add(new JLabel("Texture"));
        fields.add(textureField);
        fields.add(new JLabel("Thermal"));
        fields.add(thermalField);

        JPanel vectorPanel = new JPanel(new BorderLayout());
        vectorPanel.setBorder(BorderFactory.createTitledBorder("CSV Vector (32+ values)"));
        vectorPanel.add(new JScrollPane(vectorArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(0, 4, 8, 8));
        actions.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton statusBtn = new JButton("Status");
        statusBtn.addActionListener(e -> status());
        JButton consentBtn = new JButton("Record Consent");
        consentBtn.addActionListener(e -> recordConsent());
        JButton enrollSimBtn = new JButton("Enroll (Simulated)");
        enrollSimBtn.addActionListener(e -> enrollSimulated());
        JButton verifySimBtn = new JButton("Verify (Simulated)");
        verifySimBtn.addActionListener(e -> verifySimulated());
        JButton enrollLiveBtn = new JButton("Enroll (Live Metrics)");
        enrollLiveBtn.addActionListener(e -> enrollLive());
        JButton verifyLiveBtn = new JButton("Verify (Live Metrics)");
        verifyLiveBtn.addActionListener(e -> verifyLive());
        JButton revokeBtn = new JButton("Revoke");
        revokeBtn.addActionListener(e -> revoke());
        JButton exportBtn = new JButton("Export");
        exportBtn.addActionListener(e -> export());
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> delete());

        actions.add(statusBtn);
        actions.add(consentBtn);
        actions.add(enrollSimBtn);
        actions.add(verifySimBtn);
        actions.add(enrollLiveBtn);
        actions.add(verifyLiveBtn);
        actions.add(revokeBtn);
        actions.add(exportBtn);
        actions.add(deleteBtn);

        root.add(fields, BorderLayout.NORTH);
        root.add(vectorPanel, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildVisionPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel permissionPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        permissionPanel.setBorder(BorderFactory.createTitledBorder("OpenCV Permission Gate"));
        permissionPanel.add(new JLabel("Use OpenCV lane only with explicit permission."));
        permissionPanel.add(openCvAllowed);

        JButton savePermissionBtn = new JButton("Save Permission");
        savePermissionBtn.addActionListener(e -> savePermissionState());
        permissionPanel.add(savePermissionBtn);

        JPanel imagePanel = new JPanel(new GridLayout(0, 2, 8, 8));
        imagePanel.setBorder(BorderFactory.createTitledBorder("Scott + Avatar Lane"));
        imagePanel.add(new JLabel("Image Path"));
        imagePanel.add(imagePathField);

        JButton extractBtn = new JButton("Extract Scott Vector");
        extractBtn.addActionListener(e -> extractScottVector(false));
        JButton extractAndAvatarBtn = new JButton("Extract + Generate Avatar");
        extractAndAvatarBtn.addActionListener(e -> extractScottVector(true));
        imagePanel.add(extractBtn);
        imagePanel.add(extractAndAvatarBtn);

        JButton demoHintBtn = new JButton("Show Demo Command");
        demoHintBtn.addActionListener(e -> log("Run: java -cp \"build/classes/java/main;build/classes/java/test\" fraymus.DefensibleBiometricSystemDemo"));
        imagePanel.add(demoHintBtn);

        root.add(permissionPanel);
        root.add(imagePanel);
        return root;
    }

    private void status() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        log("Status: " + service.statusSummary());
    }

    private void recordConsent() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        BiometricAvatarService.OperationResult r = service.recordConsent(subject(), consentField.getText().trim());
        logResult(r.success(), r.message());
    }

    private void enrollSimulated() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        double[] vector = parseVectorOrWarn();
        if (vector == null) return;
        BiometricAvatarService.EnrollmentResult r = service.enroll(
            subject(), secret(), vector, BiometricAvatarService.LivenessSignals.simulatedLocal()
        );
        logResult(r.success(), r.message() + " profile=" + r.profilePath());
    }

    private void verifySimulated() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        double[] vector = parseVectorOrWarn();
        if (vector == null) return;
        BiometricAvatarService.VerificationResult r = service.verify(
            subject(), secret(), vector, BiometricAvatarService.LivenessSignals.simulatedLocal()
        );
        logResult(r.success(), r.message() + " similarity=" + format(r.similarity()));
    }

    private void enrollLive() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        double[] vector = parseVectorOrWarn();
        if (vector == null) return;
        BiometricAvatarService.LivenessSignals live = parseLiveSignals();
        if (live == null) return;
        BiometricAvatarService.EnrollmentResult r = service.enroll(subject(), secret(), vector, live);
        logResult(r.success(), r.message() + " profile=" + r.profilePath());
    }

    private void verifyLive() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        double[] vector = parseVectorOrWarn();
        if (vector == null) return;
        BiometricAvatarService.LivenessSignals live = parseLiveSignals();
        if (live == null) return;
        BiometricAvatarService.VerificationResult r = service.verify(subject(), secret(), vector, live);
        logResult(r.success(), r.message() + " similarity=" + format(r.similarity()));
    }

    private void revoke() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        BiometricAvatarService.OperationResult r = service.revoke(subject(), "operator_request_from_window");
        logResult(r.success(), r.message());
    }

    private void export() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        BiometricAvatarService.OperationResult r = service.exportSubject(subject(), "");
        logResult(r.success(), r.message());
    }

    private void delete() {
        if (service == null) {
            log("Service unavailable.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete all avatar data for subject '" + subject() + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;
        BiometricAvatarService.OperationResult r = service.deleteSubject(subject());
        logResult(r.success(), r.message());
    }

    private void extractScottVector(boolean generateAvatar) {
        if (MODE_OPENCV.equals(mode()) && !openCvAllowed.isSelected()) {
            log("OpenCV-assisted mode requires explicit permission. Enable and save permission first.");
            return;
        }
        try {
            BufferedImage image = ImageIO.read(Paths.get(imagePathField.getText().trim()).toFile());
            if (image == null) {
                log("Could not load image from path.");
                return;
            }

            FacialFingerprintExtractor extractor = new FacialFingerprintExtractor(MODE_OPENCV.equals(mode()));
            FacialFingerprintExtractor.FacialFingerprintResult result = extractor.extractFingerprint(image);

            String csv = buildVectorCsvFromScott(result);
            vectorArea.setText(csv);
            log("Scott vector extraction complete. Fingerprint=" + safePrefix(result.fingerprint));

            if (generateAvatar) {
                AvatarGenerator generator = new AvatarGenerator(512, 512, "build/avatars");
                AvatarGenerator.AvatarResult avatarResult = generator.generateAvatar(result.landmarks, result.fingerprint);
                log("Avatar generated. png=" + avatarResult.pngPath + " svg=" + avatarResult.svgPath + " verified=" + avatarResult.verified);
            }
        } catch (Exception e) {
            log("Scott/OpenCV lane failed: " + e.getMessage());
        }
    }

    private String buildVectorCsvFromScott(FacialFingerprintExtractor.FacialFingerprintResult r) {
        List<Double> values = new ArrayList<>();
        values.add(r.eyeSeparation);
        values.add(r.faceWidth);
        values.add(r.faceHeight);
        values.add(r.faceAspectRatio);
        values.add(r.symmetryScore);
        values.add(r.grayMean);
        values.add(r.grayStd);
        values.add(r.graySkewness);
        values.add(r.grayKurtosis);
        values.add(r.depthAvg);
        values.add(r.depthStd);
        values.add(r.nuanceScore);
        if (r.scottSignature != null) {
            values.add(r.scottSignature.phiResonance);
            values.add((double) r.scottSignature.vertexCount);
            values.add(r.scottSignature.perimeter);
            values.add(r.scottSignature.area);
            values.add(r.scottSignature.aspectRatio);
            values.add(r.scottSignature.compactness);
        }

        if (r.landmarks != null && r.landmarks.points != null) {
            int added = 0;
            for (int i = 0; i < r.landmarks.points.size() && added < 16; i++) {
                values.add(r.landmarks.points.get(i).x);
                added++;
            }
        }

        while (values.size() < 32) values.add(0.0);
        if (values.size() > 64) values = values.subList(0, 64);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(format(values.get(i)));
        }
        return sb.toString();
    }

    private void loadPermissionState() {
        try {
            if (!Files.exists(openCvPermissionPath)) {
                openCvAllowed.setSelected(false);
                modeBox.setSelectedItem(MODE_BAREMETAL);
                return;
            }
            List<String> lines = Files.readAllLines(openCvPermissionPath, StandardCharsets.UTF_8);
            boolean allowed = lines.stream().anyMatch(line -> line.trim().equalsIgnoreCase("allow=true"));
            openCvAllowed.setSelected(allowed);
            modeBox.setSelectedItem(allowed ? MODE_OPENCV : MODE_BAREMETAL);
        } catch (Exception e) {
            openCvAllowed.setSelected(false);
            modeBox.setSelectedItem(MODE_BAREMETAL);
            log("Permission load warning: " + e.getMessage());
        }
    }

    private void savePermissionState() {
        try {
            Files.createDirectories(openCvPermissionPath.getParent());
            String content = "allow=" + openCvAllowed.isSelected() + "\n" +
                "mode=" + mode() + "\n" +
                "savedAt=" + Instant.now() + "\n";
            Files.writeString(openCvPermissionPath, content, StandardCharsets.UTF_8);
            log("Permission saved: allow=" + openCvAllowed.isSelected() + " mode=" + mode());
        } catch (Exception e) {
            log("Permission save failed: " + e.getMessage());
        }
    }

    private double[] parseVectorOrWarn() {
        try {
            String[] parts = vectorArea.getText().split(",");
            List<Double> values = new ArrayList<>();
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) values.add(Double.parseDouble(t));
            }
            if (values.size() < 32) {
                log("Vector needs at least 32 values. Current=" + values.size());
                return null;
            }
            double[] out = new double[values.size()];
            for (int i = 0; i < values.size(); i++) out[i] = values.get(i);
            return out;
        } catch (Exception e) {
            log("Invalid vector: " + e.getMessage());
            return null;
        }
    }

    private BiometricAvatarService.LivenessSignals parseLiveSignals() {
        try {
            return new BiometricAvatarService.LivenessSignals(
                Double.parseDouble(blinkField.getText().trim()),
                Double.parseDouble(depthField.getText().trim()),
                Double.parseDouble(parallaxField.getText().trim()),
                Double.parseDouble(textureField.getText().trim()),
                Double.parseDouble(thermalField.getText().trim()),
                false
            );
        } catch (Exception e) {
            log("Invalid liveness metrics: " + e.getMessage());
            return null;
        }
    }

    private String subject() {
        return subjectField.getText().trim();
    }

    private String secret() {
        return new String(secretField.getPassword());
    }

    private String mode() {
        Object selected = modeBox.getSelectedItem();
        return selected == null ? MODE_BAREMETAL : selected.toString();
    }

    private void logResult(boolean success, String message) {
        log((success ? "[OK] " : "[FAIL] ") + message);
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private static String safePrefix(String value) {
        if (value == null || value.isEmpty()) return "none";
        return value.substring(0, Math.min(16, value.length())) + "...";
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }

    private static String defaultVectorCsv() {
        return "0.45,0.32,0.78,0.91,0.23,0.56,0.67,0.12,0.89,0.34,0.55,0.76,0.98,0.11,0.43,0.65," +
            "0.87,0.21,0.54,0.33,0.72,0.19,0.88,0.41,0.63,0.95,0.17,0.29,0.51,0.74,0.36,0.82";
    }

    public static void launch(BiometricAvatarService service) {
        SwingUtilities.invokeLater(() -> new AvatarSovereigntyWindow(service).setVisible(true));
    }
}

