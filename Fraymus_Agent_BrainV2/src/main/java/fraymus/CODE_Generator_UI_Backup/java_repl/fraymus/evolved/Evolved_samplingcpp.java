import java.util.*;
import java.util.function.Function;

class SamplerRing {
    private final int maxTokens;
    private final Map\u003cString, Integer\u003e samplingWeights;

    public SamplerRing(int maxTokens) {
        this.maxTokens = maxTokens;
        this.samplingWeights = new HashMap\u003c\u003e();
    }

    public void setWeight(String name, int weight) {
        if (weight \u003e 0) {
            samplingWeights.put(name, weight);
        }
    }

    public int getWeight(String name) {
        return samplingWeights.getOrDefault(name, 0);
    }

    public boolean containsToken(String tokenName) {
        return samplingWeights.containsKey(tokenName);
    }

    public String getToken(int index) {
        if (index \u003e= maxTokens \u0026\u0026 samplingWeights.containsKey(index)) {
            throw new IndexOutOfBoundsException();
        }
        return null;
    }
}