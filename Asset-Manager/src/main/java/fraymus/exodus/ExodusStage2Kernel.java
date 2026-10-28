package fraymus.exodus;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Deterministic 16-bit BIOS console loaded at 1000:0000 by the Exodus MBR. */
public class ExodusStage2Kernel {
    public static final int KERNEL_SIZE = 1536;
    public static final String BOOT_TEXT = "FRAYNIX STAGE2 KERNEL ONLINE";
    public static final String HANDOFF_TEXT = "BIOS MBR LOADER -> SECOND-STAGE HANDOFF VERIFIED";
    public static final String MEMORY_TEXT = "HDC OK (16384 BYTES INITIALIZED)";

    public static byte[] generateKernel() {
        // All state belongs to this invocation, including labels and relocations.
        Assembler a = new Assembler();
        a.emit(0xFA,                         // CLI: set SS:SP atomically
               0x31, 0xC0,                   // XOR AX,AX
               0x8E, 0xD0,                   // MOV SS,AX
               0xBC, 0x00, 0x7C,             // MOV SP,7C00h
               0xFB, 0xFC,                   // STI; CLD
               0xB8, 0x03, 0x00, 0xCD, 0x10, // BIOS 80x25 text mode + cursor reset
               0x0E, 0x1F,                   // PUSH CS; POP DS (strings share code segment)
               0x1E, 0x07);                  // PUSH DS; POP ES
        a.print("banner");
        a.call("newline");
        a.print("handoff");
        a.call("newline");

        // Clear a separate 16 KiB memory region without touching code or stack.
        a.emit(0xB8, 0x00, 0x20, 0x8E, 0xC0, // ES = 2000h
               0x31, 0xC0, 0x31, 0xFF,       // AX = DI = 0
               0xB9, 0x00, 0x20,             // CX = 2000h words
               0xFC, 0xF3, 0xAB,             // CLD; REP STOSW
               0x1E, 0x07);                  // Restore ES = DS, not FS
        a.print("memory");
        a.call("newline");
        a.call("newline");

        a.label("prompt");
        a.print("prompt_text");
        a.emit(0x31, 0xC9);                   // CX counts characters for backspace
        a.label("read_key");
        a.emit(0x51, 0x1E, 0x06,              // Preserve count and segments across BIOS
               0x30, 0xE4, 0xCD, 0x16,       // AH = 0; INT 16h (blocking keyboard read)
               0x07, 0x1F, 0x59);
        a.emit(0x3C, 0x0D);                   // Enter
        a.branch(0x74, "enter");
        a.emit(0x3C, 0x08);                   // Backspace
        a.branch(0x74, "backspace");
        a.emit(0x3C, 0x20);
        a.branch(0x72, "read_key");           // Ignore controls and extended-key prefix
        a.emit(0x3C, 0x7E);
        a.branch(0x77, "read_key");           // Ignore non-ASCII BIOS key results
        a.call("put_char");
        a.emit(0x41);                         // INC CX
        a.jump("read_key");

        a.label("backspace");
        a.branch(0xE3, "read_key");           // JCXZ: do not erase the prompt
        a.emit(0x49, 0xB0, 0x08);             // DEC CX; cursor back
        a.call("put_char");
        a.emit(0xB0, 0x20);                   // Erase the previous character
        a.call("put_char");
        a.emit(0xB0, 0x08);
        a.call("put_char");
        a.jump("read_key");

        a.label("enter");
        a.call("newline");
        a.print("ok");                       // Console acknowledgement, not command evaluation
        a.call("newline");
        a.jump("prompt");

        a.label("print_string");
        a.emit(0x56);                         // Preserve SI for callers
        a.label("print_next");
        a.emit(0xAC, 0x84, 0xC0);             // LODSB; TEST AL,AL
        a.branch(0x74, "print_done");
        a.call("put_char");
        a.jump("print_next");
        a.label("print_done");
        a.emit(0x5E, 0xC3);                   // POP SI; RET

        a.label("newline");
        a.emit(0xB0, 0x0D);
        a.call("put_char");
        a.emit(0xB0, 0x0A);
        a.call("put_char");
        a.emit(0xC3);

        a.label("put_char");
        a.emit(0x60, 0x1E, 0x06,              // PUSHA; PUSH DS; PUSH ES
               0xB4, 0x0E, 0xBB, 0x07, 0x00, // Teletype on display page zero
               0xCD, 0x10,
               0x07, 0x1F, 0x61, 0xC3);      // Restore segments/registers; RET

        a.text("banner", BOOT_TEXT);
        a.text("handoff", HANDOFF_TEXT);
        a.text("memory", MEMORY_TEXT);
        a.text("prompt_text", "> ");
        a.text("ok", "OK");
        return a.finish();
    }

    /** A tiny emitter with explicit absolute/relative relocations, never byte-pattern scanning. */
    private static final class Assembler {
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private final Map<String, Integer> labels = new LinkedHashMap<>();
        private final List<Fixup> fixups = new ArrayList<>();

        void emit(int... values) {
            for (int value : values) bytes.write(value & 0xFF);
        }

        void label(String name) {
            if (labels.putIfAbsent(name, bytes.size()) != null) {
                throw new IllegalStateException("Duplicate label: " + name);
            }
        }

        void operand(String target, int width, boolean relative) {
            fixups.add(new Fixup(bytes.size(), target, width, relative));
            for (int i = 0; i < width; i++) emit(0);
        }

        void call(String target) {
            emit(0xE8);
            operand(target, 2, true);
        }

        void jump(String target) {
            emit(0xE9);
            operand(target, 2, true);
        }

        void branch(int opcode, String target) {
            emit(opcode);
            operand(target, 1, true);
        }

        void print(String target) {
            emit(0xBE);                       // MOV SI, label offset within DS = CS
            operand(target, 2, false);
            call("print_string");
        }

        void text(String name, String value) {
            label(name);
            for (byte b : value.getBytes(StandardCharsets.US_ASCII)) emit(b);
            emit(0);
        }

        byte[] finish() {
            byte[] code = bytes.toByteArray();
            if (code.length > KERNEL_SIZE) {
                throw new IllegalStateException("Stage two exceeds its three-sector boundary: " + code.length);
            }
            for (Fixup fixup : fixups) {
                Integer target = labels.get(fixup.target());
                if (target == null) throw new IllegalStateException("Unknown label: " + fixup.target());
                int value = fixup.relative() ? target - (fixup.offset() + fixup.width()) : target;
                int min = fixup.relative() ? (fixup.width() == 1 ? -128 : -32768) : 0;
                int max = fixup.width() == 1 ? 127 : (fixup.relative() ? 32767 : 65535);
                if (value < min || value > max) {
                    throw new IllegalStateException("Relocation out of range: " + fixup.target());
                }
                for (int i = 0; i < fixup.width(); i++) {
                    code[fixup.offset() + i] = (byte) (value >> (8 * i));
                }
            }
            return Arrays.copyOf(code, KERNEL_SIZE);
        }
    }

    private record Fixup(int offset, String target, int width, boolean relative) { }

    public static String toHex(byte[] kernel) {
        StringBuilder sb = new StringBuilder(kernel.length * 2);
        for (byte b : kernel) sb.append(String.format("%02X", b & 0xFF));
        return sb.toString();
    }

    public static void main(String[] args) {
        byte[] kernel = generateKernel();
        System.out.println("EXODUS STAGE-2 KERNEL GENERATOR");
        System.out.println("Kernel size: " + kernel.length + " bytes (3 sectors)");
    }
}
