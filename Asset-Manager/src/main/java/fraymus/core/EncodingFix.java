package fraymus.core;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Global output normalization for Fraynix.
 * Repairs mojibake and emits ASCII-safe console output across subsystems.
 */
public class EncodingFix {

    private static volatile boolean installed = false;

    public static void apply() {
        if (installed) {
            return;
        }
        installed = true;

        // Keep JVM internals in UTF-8.
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.stdout.encoding", "UTF-8");
        System.setProperty("sun.stderr.encoding", "UTF-8");
        System.setProperty("native.encoding", "UTF-8");

        // Wrap stdout/stderr once so all downstream prints are normalized.
        PrintStream baseOut = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
        PrintStream baseErr = new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8);
        System.setOut(new PrintStream(new AsciiNormalizingOutputStream(baseOut), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new AsciiNormalizingOutputStream(baseErr), true, StandardCharsets.UTF_8));
    }

    private static final class AsciiNormalizingOutputStream extends OutputStream {
        private final PrintStream target;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(256);

        private AsciiNormalizingOutputStream(PrintStream target) {
            this.target = target;
        }

        @Override
        public void write(int b) throws IOException {
            if (b == '\n') {
                flushLine(true);
                return;
            }
            if (b != '\r') {
                buffer.write(b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            for (int i = 0; i < len; i++) {
                write(b[off + i]);
            }
        }

        @Override
        public void flush() throws IOException {
            flushLine(false);
            target.flush();
        }

        private void flushLine(boolean newline) {
            if (buffer.size() == 0) {
                if (newline) {
                    target.println();
                }
                return;
            }

            String raw = buffer.toString(StandardCharsets.UTF_8);
            buffer.reset();

            String normalized = toAscii(raw);
            if (newline) {
                target.println(normalized);
            } else {
                target.print(normalized);
            }
        }
    }

    private static String toAscii(String input) {
        String value = repairMojibake(input);

        // High-value semantic replacements first.
        value = value
            .replace("φ²", "phi^2")
            .replace("1/φ", "1/phi")
            .replace("φ⁻¹", "1/phi")
            .replace("φ", "phi")
            .replace("Φ", "PHI")
            .replace("Ψ", "PSI")
            .replace("ψ", "psi")
            .replace("σ", "sigma")
            .replace("Σ", "SIGMA")
            .replace("Δ", "delta")
            .replace("δ", "delta")
            .replace("γ", "gamma")
            .replace("θ", "theta")
            .replace("λ", "lambda")
            .replace("Ω", "OMEGA")
            .replace("ω", "omega")
            .replace("²", "^2")
            .replace("³", "^3")
            .replace("¹", "^1")
            .replace("₁", "1")
            .replace("₂", "2")
            .replace("₃", "3")
            .replace("→", "->")
            .replace("←", "<-")
            .replace("↔", "<->")
            .replace("⇒", "=>")
            .replace("⇐", "<=")
            .replace("≥", ">=")
            .replace("≤", "<=")
            .replace("≠", "!=")
            .replace("≈", "~=")
            .replace("∞", "inf")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("∑", "sum")
            .replace("∫", "integral")
            .replace("∂", "d")
            .replace("√", "sqrt")
            .replace("✓", "[OK]")
            .replace("✔", "[OK]")
            .replace("✗", "[X]")
            .replace("✘", "[X]")
            .replace("•", "*")
            .replace("●", "*")
            .replace("◆", ">")
            .replace("⚡", "[BOOT]")
            .replace("💾", "[FS]")
            .replace("🌌", "[GRAVITY]")
            .replace("☢️", "[FUSION]")
            .replace("☢", "[FUSION]")
            .replace("🧬", "[DNA]")
            .replace("🤖", "[AI]")
            .replace("📚", "[KNOWLEDGE]")
            .replace("🧠", "[CORTEX]")
            .replace("🌀", "[PRIME]")
            .replace("📡", "[ABSOLUTE]")
            .replace("🚀", "[LAUNCH]")
            .replace("👁️", "[VISION]")
            .replace("👁", "[VISION]")
            .replace("⚙️", "[GEAR]")
            .replace("⚙", "[GEAR]")
            .replace("⌛", "[WAIT]")
            .replace("⌚", "[TIME]");

        // Common partially-decoded fallback tokens seen in Windows logs.
        value = value
            .replace("�?†", "phi")
            .replace("�?ƒ", "sigma")
            .replace("�?", "")
            .replace("Â²", "^2")
            .replace("Â³", "^3")
            .replace("Â¹", "^1")
            .replace("â‚�", "1")
            .replace("â‚‚", "2")
            .replace("â‚ƒ", "3")
            .replace("â‚„", "4")
            .replace("â†’", "->")
            .replace("â†", "<-")
            .replace("â†”", "<->")
            .replace("â‡’", "=>")
            .replace("â‰¤", "<=")
            .replace("â‰¥", ">=")
            .replace("â‰ ", "!=")
            .replace("â‰ˆ", "~=")
            .replace("âˆž", "inf")
            .replace("âˆ‘", "sum")
            .replace("âˆ«", "integral")
            .replace("âˆ‚", "d")
            .replace("âˆš", "sqrt")
            .replace("âœ“", "[OK]")
            .replace("âœ”", "[OK]")
            .replace("âœ—", "[X]")
            .replace("âœ˜", "[X]");

        // Question-mark runs from earlier replacement paths.
        value = value
            .replace("??-CONSTANTS", "phi-CONSTANTS")
            .replace("??-Constants", "phi-Constants")
            .replace("  ??  = ", "   phi  = ")
            .replace("  ???? = ", "   phi^2 = ")
            .replace("   [OK] = 2.618033988749895", "   phi^2 = 2.618033988749895")
            .replace("1/??", "1/phi")
            .replace("(?? = D_?? C_? F)", "(PSI = D_phi C_sigma F)")
            .replace("(?? = D_phi C_sigma F)", "(PSI = D_phi C_sigma F)")
            .replace("D_??", "D_phi")
            .replace("C_?", "C_sigma")
            .replace("???-CONSTANTS", "phi-CONSTANTS")
            .replace("???-Constants", "phi-Constants")
            .replace("1/???", "1/phi")
            .replace("D_???", "D_phi")
            .replace("C_??", "C_sigma")
            .replace("A???", "A1")
            .replace("A??????", "A2")
            .replace("A??", "A1")
            .replace("A1??", "A2")
            .replace("d??", "d^2")
            .replace("8?????8?????8?????8", "8x8x8x8")
            .replace("[rebuilt?tanh]", "[rebuilt->tanh]")
            .replace("[rebuilt?tanh])", "[rebuilt->tanh])")
            .replace("??-ready", "phi-ready")
            .replace("??=consciousness", "phi=consciousness")
            .replace("??(Tx+noise)", "phi(Tx+noise)")
            .replace("S=??L", "S=integral L dt")
            .replace("(IIT??/Braid/Lagrangian)", "(IIT-phi/Braid/Lagrangian)")
            .replace("F=Complexity?????Accuracy", "F=Complexity-Accuracy")
            .replace("PREFRONTAL??????SPINAL_CORD", "PREFRONTAL->SPINAL_CORD")
            .replace("F = [OK] [OK] (A1 [OK] A2) / d^2", "F = phi * (A1 * A2) / d^2")
            .replace(" [OK] [OK] ", " * ")
            .replace(" [OK] = ", " phi = ")
            .replace("->->?? ", "* ")
            .replace("->->??? ", "* ")
            .replace("6.4? cryptanalysis", "6.4x cryptanalysis")
            .replace("??=0.99", "gamma=0.99")
            .replace("??=0.01", "epsilon=0.01")
            .replace("??-ready", "phi-ready")
            .replace("??_split", "p_split")
            .replace("??_prune", "p_prune")
            .replace("??=consciousness", "phi=consciousness")
            .replace("S=?L dt", "S=integral L dt")
            .replace("?(Tx+noise)", "phi(Tx+noise)")
            .replace("IIT??/Braid/Lagrangian", "IIT-phi/Braid/Lagrangian")
            .replace("??????", "->")
            .replace("?????", "[OK]")
            .replace("????", "[OK]");
        value = value.replaceAll("\\(\\?\\?\\s*=\\s*D_phi\\s+C_sigma\\s+F\\)", "(PSI = D_phi C_sigma F)");
        value = value.replaceAll("^\\s*\\?+\\s+\\[", "[STEP] [");
        value = value.replaceAll("^\\?+$", "---------------------------------------------------------------");
        value = value.replaceAll("\\?{12,}", "===============================================================");
        value = value.replaceAll("(?<=\\s)\\?{2,}(?=\\s)", "[OK]");
        value = value.replaceAll("->->\\?{2,}\\s*", "* ");
        value = value.replaceAll("->\\?{2,}$", "->");
        value = value.replaceAll("\\?\\?=0\\.99", "gamma=0.99");
        value = value.replaceAll("\\?\\?=0\\.01", "epsilon=0.01");
        value = value.replaceAll("\\?\\?-ready", "phi-ready");
        value = value.replaceAll("\\?\\?_split", "p_split");
        value = value.replaceAll("\\?\\?_prune", "p_prune");
        value = value.replaceAll("\\?\\?=consciousness", "phi=consciousness");
        value = value.replaceAll("S=\\?L dt", "S=integral L dt");
        value = value.replaceAll("IIT\\?\\?/Braid/Lagrangian", "IIT-phi/Braid/Lagrangian");

        // Box drawing and heavy separators.
        value = value
            .replace("╔", "+")
            .replace("╗", "+")
            .replace("╚", "+")
            .replace("╝", "+")
            .replace("═", "=")
            .replace("║", "|")
            .replace("╠", "+")
            .replace("╣", "+")
            .replace("╦", "+")
            .replace("╩", "+")
            .replace("╬", "+")
            .replace("┌", "+")
            .replace("┐", "+")
            .replace("└", "+")
            .replace("┘", "+")
            .replace("─", "-")
            .replace("│", "|");

        // Remove remaining non-ASCII chars.
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch <= 127) {
                out.append(ch);
            }
        }
        return finalizeQuestionFixes(out.toString());
    }

    private static String repairMojibake(String input) {
        String value = input;
        for (int i = 0; i < 3; i++) {
            if (!looksMojibake(value)) {
                break;
            }
            String repaired = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            if (mojibakeScore(repaired) >= mojibakeScore(value)) {
                break;
            }
            value = repaired;
        }
        return value;
    }

    private static boolean looksMojibake(String value) {
        return value.indexOf('Ã') >= 0
            || value.indexOf('â') >= 0
            || value.indexOf('Â') >= 0
            || value.contains("�?")
            || value.contains("Ï")
            || value.contains("Î");
    }

    private static int mojibakeScore(String value) {
        int score = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == 'Ã' || c == 'â' || c == 'Â' || c == 'Ï' || c == 'Î') {
                score++;
            }
        }
        return score;
    }

    private static String finalizeQuestionFixes(String value) {
        return value
            .replace("? [", "[STEP] [")
            .replace("??? [", "[STEP] [")
            .replace("?? [", "[STEP] [")
            .replace("??[STEP]", "[STEP]")
            .replace("? FRAYNIX SHUTTING DOWN...", "[STEP] FRAYNIX SHUTTING DOWN...")
            .replace("(?? = D_phi C_sigma F)", "(PSI = D_phi C_sigma F)")
            .replace("??=0.99", "gamma=0.99")
            .replace("??=0.01", "epsilon=0.01")
            .replace("??-ready", "phi-ready")
            .replace("??_split", "p_split")
            .replace("??_prune", "p_prune")
            .replace("??=consciousness", "phi=consciousness")
            .replace("S=?L dt", "S=integral L dt")
            .replace("IIT??/Braid/Lagrangian", "IIT-phi/Braid/Lagrangian")
            .replace("phi  = 1.618033988749895\n   [OK] = 2.618033988749895", "phi  = 1.618033988749895\n   phi^2 = 2.618033988749895")
            .replace("F = [OK] [OK] (A1 [OK] A2) / d^2", "F = phi * (A1 * A2) / d^2");
    }
}
