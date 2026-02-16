package repl;

import java.util.concurrent.atomic.AtomicInteger;
import java.lang.System;

public class Evolved_llmwindowsgo {
    public static final int CREATE_DEFAULT_ERROR_MODE = 0x04000000;
    public static final int ABOVE_NORMAL_PRIORITY_CLASS = 0x00008000;
    public static final int CREATE_NO_WINDOW            = 0x08000000;

    private static class SysProcAttr {
        private int creationFlags;

        public SysProcAttr(int creationFlags) {
            this.creationFlags = creationFlags;
        }

        public int getCreationFlags() {
            return creationFlags;
        }
    }

    public static final SysProcAttr LlamaServerSysProcAttr = new SysProcAttr(
            CREATE_DEFAULT_ERROR_MODE | ABOVE_NORMAL_PRIORITY_CLASS | CREATE_NO_WINDOW
    );
}