package repl;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Evolved_binaryopsh {

    public interface GGMLComputeParams {
        void computeForwardAddNonQuantized();
        void computeForwardSub();
        void computeForwardMul();
        void computeForwardDiv();
    }

    public static class GgmlTensor {
        // no fields
    }

    public static class StructGgmlComputeParams implements GGMLComputeParams {
        private int[] params;
        private String name;

        public StructGgmlComputeParams(int[] params, String name) {
            this.params = params;
            this.name = name;
        }

        @Override
        public void computeForwardAddNonQuantized() {}
        @Override
        public void computeForwardSub() {}
        @Override
        public void computeForwardMul() {}
        @Override
        public void computeForwardDiv() {}
    }
}