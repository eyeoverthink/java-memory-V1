package repl;

import java.util.Vector;

public class Evolved_mtmdaudioh {

    public static class mtmd_audio_mel {
        public int n_len;
        public int n_len_org;
        public int n_mel;

        public Vector\u003cFloat\u003e data = new Vector\u003c\u003e();
    }

    public interface mtmd_audio_preprocessor {
        void initialize();
        boolean preprocess(float[] samples, int n_samples, Vector\u003cmtmd_audio_mel\u003e output);
    }

    public class mtmd_audio_preprocessor_whisper implements mtmd_audio_preprocessor {

        public mtmd_audio_preprocessor_whisper(clip_ctx ctx) {}

        @Override
        public void initialize() {}

        @Override
        public boolean preprocess(float[] samples, int n_samples, Vector\u003cmtmd_audio_mel\u003e output) {
            return false;
        }
    }
}

class clip_ctx {}