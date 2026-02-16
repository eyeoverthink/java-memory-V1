package repl;

import java.util.concurrent.atomic.AtomicLong;
import java.nio.LongBuffer;

public class Evolved_llamacparamscpp {
    private static final AtomicLong LLAMA_MAX_SEQ = new AtomicLong(0);
    public static native long llama_max_parallel_sequences();
}