import java.util.ArrayList;
import java.util.List;

class LlamaMemoryManager implements MemoryAllocator {
    private static final long serialVersionUID = 1L;

    @Override
    public void allocate() {}

    @Override
    public void deallocate() {}
}

class LlamaOptParams {
    private long loadTime;
    private long evalTime;
    private long numTokens;
    private boolean optimizeAlgorithm;
    private int batchSize;

    public LlamaOptParams(long loadTime, long evalTime, long numTokens, boolean optimizeAlgorithm, int batchSize) {
        this.loadTime = loadTime;
        this.evalTime = evalTime;
        this.numTokens = numTokens;
        this.optimizeAlgorithm = optimizeAlgorithm;
        this.batchSize = batchSize;
    }

    public List\u003cInteger\u003e getFilterList() {
        return new ArrayList\u003c\u003e();
    }
}

class PerfContextData {
    private long loadTime;
    private long evalTime;
    private long numTokensProcessed;

    public PerfContextData(long loadTime, long evalTime, long numTokensProcessed) {
        this.loadTime = loadTime;
        this.evalTime = evalTime;
        this.numTokensProcessed = numTokensProcessed;
    }

    public List\u003cInteger\u003e getFilterList() {
        return new ArrayList\u003c\u003e();
    }
}

interface MemoryAllocator {
    void allocate();
    void deallocate();
}