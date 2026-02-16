package com.eyeoverthink.fraymus.lang;

import java.util.*;

public class FrayCompiler {

    public enum OpCode {
        SPAWN,
        FUSE,
        FOLD,
        ENTANGLE,
        HALT
    }

    public static class Instruction {
        public OpCode op;
        public String arg1;
        public String arg2;

        public Instruction(OpCode o, String a1, String a2) {
            op = o;
            arg1 = a1;
            arg2 = a2;
        }
    }

    public List<Instruction> compile(String sourceCode) {
        System.out.println("⚡ FRAYMUS COMPILER: Analyzing Root...");
        List<Instruction> program = new ArrayList<>();

        String[] lines = sourceCode.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.contains("Singularity") && line.contains("fold:") && line.contains("true")) {
                System.out.println("   ⏳ DETECTED TIME FOLD: Pre-calculating...");
                program.add(new Instruction(OpCode.FOLD, "Fibonacci(100)", "354224848179261915075"));
            } else if (line.startsWith("Particle")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String name = parts[1].trim();
                    program.add(new Instruction(OpCode.SPAWN, name, "MEMORY_ALLOC"));
                }
            } else if (line.startsWith("Entangle")) {
                int open = line.indexOf('(');
                int close = line.indexOf(')');
                if (open > 0 && close > open) {
                    String args = line.substring(open + 1, close);
                    String[] parts = args.split(",");
                    if (parts.length == 2) {
                        program.add(new Instruction(OpCode.ENTANGLE, parts[0].trim(), parts[1].trim()));
                    }
                }
            }
        }

        program.add(new Instruction(OpCode.HALT, "", ""));
        System.out.println("   ✅ COMPILATION COMPLETE: " + program.size() + " instructions generated.");
        return program;
    }
}
