package repl;

import java.lang.System;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class Evolved_backgroundunixgo {
    public static class SysProcAttr {
        public boolean Setpgid;
    }

    public static SysProcAttr backgroundServerSysProcAttr() {
        return new SysProcAttr();
    }
}