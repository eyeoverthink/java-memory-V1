package com.eyeoverthink.fraymus;

import com.eyeoverthink.fraymus.lang.FrayCompiler;
import com.eyeoverthink.fraymus.lang.FrayVM;

public class SystemZero {

    public static void main(String[] args) {
        System.out.println("🌌 SYSTEM ZERO: THE ROOT.");

        String sourceCode =
            "Particle AdminUser\n" +
            "Particle Database\n" +
            "Entangle(AdminUser, Database)\n" +
            "Singularity CalculateFib { fold: true }\n";

        FrayCompiler compiler = new FrayCompiler();
        var binary = compiler.compile(sourceCode);

        FrayVM cpu = new FrayVM();
        cpu.execute(binary);
    }
}
