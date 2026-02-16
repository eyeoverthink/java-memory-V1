package com.eyeoverthink.fraymus.lang;

import com.eyeoverthink.fraymus.lang.FrayCompiler.Instruction;
import com.eyeoverthink.fraymus.lang.FrayCompiler.OpCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrayVM {

    private final Map<String, Object> memory = new HashMap<>();
    private final Map<String, String> entanglementMap = new HashMap<>();

    public void execute(List<Instruction> program) {
        System.out.println("🚀 FRAYMUS VM: Booting Kernel...");
        long start = System.nanoTime();

        for (Instruction ins : program) {
            if (ins == null || ins.op == null) continue;

            switch (ins.op) {
                case SPAWN -> {
                    memory.put(ins.arg1, new Object());
                    System.out.println("   [MEM] Spawned " + ins.arg1);
                }
                case ENTANGLE -> {
                    entanglementMap.put(ins.arg1, ins.arg2);
                    System.out.println("   [QNT] Entangled " + ins.arg1 + " <==> " + ins.arg2);
                }
                case FOLD -> System.out.println("   [CPU] SKIPPED CALCULATION. Injected Result: " + ins.arg2);
                case HALT -> System.out.println("   [SYS] Process Terminated.");
                default -> {
                }
            }
        }

        long duration = System.nanoTime() - start;
        System.out.println("⚡ EXECUTION TIME: " + duration + "ns");
    }
}
