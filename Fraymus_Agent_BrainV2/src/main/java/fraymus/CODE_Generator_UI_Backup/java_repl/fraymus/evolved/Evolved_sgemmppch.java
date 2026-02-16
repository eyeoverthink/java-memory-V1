package repl;

import java.util.Arrays;

public class Evolved_sgemmppch {

    public static void main(String[] args) {
        // Your code here...
    }

    @FunctionalInterface
    interface Kernel4x8 {
        void execute(long ii, long jj);
    }

    @FunctionalInterface
    interface Kernel_4x8 {
        void execute(long ii, long jj);
    }

    public static class GemmSmall {

        private final long m0;
        private final long m;
        private final long n0;
        private final long n;

        public GemmSmall(long m0, long m, long n0, long n) {
            this.m0 = m0;
            this.m = m;
            this.n0 = n0;
            this.n = n;
        }

        public void gemm(long l, GemmSmall other) {
            // Your code here...
        }
    }

    public static class KernelQ0 {

        private final long k;
        private final long kc;
        private final long lda;
        private final long ldb; // Renamed from ldatab to ldb
        private final long ldc;
        private final int ith;
        private final int nth;

        public KernelQ0(long k, long kc, long lda, long ldb, long ldc, int ith, int nth) {
            this.k = k;
            this.kc = kc;
            this.lda = lda;
            this.ldb = ldb; // Renamed from ldatab to ldb
            this.ldc = ldc;
            this.ith = ith;
            this.nth = nth;
        }

        public void execute(long ii, long jj) {
            // Your code here...
        }
    }
}