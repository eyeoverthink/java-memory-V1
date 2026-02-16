package repl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Evolved_ggmlmetalh {

    public interface GGML_BACKEND_API {
        void init();
        boolean isMetal(ggml_backend_t backend);
        void setAbortCallback(ggml_backend_t backend, ggml_abort_callback abort_callback, Object user_data);
        boolean supportsFamily(ggml_backend_t backend, int family);
        void captureNextCompute(ggml_backend_t backend);
    }

    public static class ggml_backend_metal_init implements GGML_BACKEND_API {
        @Override
        public void init() {
            // implementation for init()
        }

        @Override
        public boolean isMetal(ggml_backend_t backend) {
            // implementation for isMetal()
            return false;
        }

        @Override
        public void setAbortCallback(ggml_backend_t backend, ggml_abort_callback abort_callback, Object user_data) {
            // implementation for setAbortCallback()
        }

        @Override
        public boolean supportsFamily(ggml_backend_t backend, int family) {
            // implementation for supportsFamily()
            return false;
        }

        @Override
        public void captureNextCompute(ggml_backend_t backend) {
            // implementation for captureNextCompute()
        }
    }

    public static class ggml_backend_metal_reg implements GGML_BACKEND_API {
        @Override
        public void init() {
            // implementation for init()
        }

        @Override
        public boolean isMetal(ggml_backend_t backend) {
            // implementation for isMetal()
            return false;
        }

        @Override
        public void setAbortCallback(ggml_backend_t backend, ggml_abort_callback abort_callback, Object user_data) {
            // implementation for setAbortCallback()
        }

        @Override
        public boolean supportsFamily(ggml_backend_t backend, int family) {
            // implementation for supportsFamily()
            return false;
        }

        @Override
        public void captureNextCompute(ggml_backend_t backend) {
            // implementation for captureNextCompute()
        }
    }
}

class ggml_backend_t {
    
}

class ggml_abort_callback {

}