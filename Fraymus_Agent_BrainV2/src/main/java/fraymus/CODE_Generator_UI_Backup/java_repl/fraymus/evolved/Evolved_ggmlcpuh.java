package repl;

import java.lang.*;
import java.util.*;

public class Evolved_ggmlcpuh {
    public static class ggml_backend_t {
        // implementation details omitted for brevity
    }

    public interface ggml_abort_callback {
        void callback();
    }

    public static class ggml_type_traits_cpu {
        public float from_float;
        public long vec_dot; // changed to long
        public int vec_dot_type;
        public long nrows;

        public ggml_type_traits_cpu(float from_float, long vec_dot, int vec_dot_type, long nrows) { // changed size_t to long
            this.from_float = from_float;
            this.vec_dot = vec_dot;
            this.vec_dot_type = vec_dot_type;
            this.nrows = nrows;
        }
    }

    public interface ggml_backend_reg_t {
        // implementation details omitted for brevity
    }

    public static class ggml_cplan {
        public long work_size;
        public byte[] work_data;

        public ggml_cplan(long work_size, byte[] work_data) {
            this.work_size = work_size;
            this.work_data = work_data;
        }
    }

    public interface ggml_threadpool_t {
        // implementation details omitted for brevity
    }

    public static class ggml_cgraph {
        // implementation details omitted for brevity
    }

    public enum ggml_status {
        // implementation details omitted for brevity
    }

    public static class ggml_tensor {
        // implementation details omitted for brevity
    }

    public interface ggml_vec_dot_t {
        void dot(int n, float[] s, long bs, byte[] x, long bx, byte[] y, long by, int nrc);
    }
}