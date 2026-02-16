package com.eyeoverthink.fraymus.genesis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenesisSandboxTest {

    @Test
    void verifyJavaSource_compilesValidClass() {
        GenesisSandbox sandbox = new GenesisSandbox();
        String src = "public class Hello { public static void main(String[] a){ System.out.println(\"hi\"); } }";

        GenesisSandbox.SandboxResult result = sandbox.verifyJavaSource("Hello", src);
        assertTrue(result.success(), result.error());
        assertNotNull(result.outputDir());
    }

    @Test
    void verifyJavaSource_rejectsInvalidJava() {
        GenesisSandbox sandbox = new GenesisSandbox();
        String src = "public class Broken { public void x( { }";

        GenesisSandbox.SandboxResult result = sandbox.verifyJavaSource("Broken", src);
        assertFalse(result.success());
        assertNotNull(result.error());
    }
}
