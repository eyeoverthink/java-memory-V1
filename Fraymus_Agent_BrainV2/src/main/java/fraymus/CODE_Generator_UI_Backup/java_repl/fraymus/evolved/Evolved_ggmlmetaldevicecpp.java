class ggml_metal {
  public enum GGML_OP {
      ADD, SUB, MUL, DIV, L2_NORM, GROUP_NORM, ROPE, IM2COL, CONV_TRANSPOSE_1D, CONV_2D, UPSCALE, PAD, PAD_REFLECT_1D, ARANGE, TIMESTEP_EMBEDDING, OPT_STEP_ADAMW, OPT_STEP_SGD
  }

  public static class ggml_tensor {
      public int ne;
      public int type;
      public long size;

      public ggml_tensor(int type, int size) {
          this.type = type;
          this.size = size;
      }
  }

  public static class ggml_op {
      public GGML_OP op;
      public double[] params;

      public ggml_op(GGML_OP op) {
          this.op = op;
          this.params = new double[0];
      }

      public ggml_op(GGML_OP op, double[] params) {
          this.op = op;
          this.params = params;
      }
  }

  public static class ggml_pipeline {
      public ggml_op op;
      public ggml_tensor input;
      public ggml_tensor output;

      public ggml_pipeline(ggml_op op, ggml_tensor input, ggml_tensor output) {
          this.op = op;
          this.input = input;
          this.output = output;
      }
  }

  public interface ggml_metal_library {
      ggml_pipeline createPipeline(ggml_op op, ggml_tensor input, ggml_tensor output);
  }

  public enum GGML_ROPE_TYPE {
      NEOX, MROPE, IMROPE, VISION
  }
}