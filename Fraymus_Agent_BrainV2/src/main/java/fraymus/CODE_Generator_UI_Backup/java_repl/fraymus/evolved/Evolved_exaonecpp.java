import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class Evolved_exaonecpp {
  public static native void llm_build_exaone(long model, long params);

  public static native int build_inp_embd(long tokEmbD);
  public static native long build_inp_pos();
  public static native long build_attn_inp_kv();

  public static native long build_inp_out_ids();
  public static native long build_norm(long inpL, long attnNorm, long other, long normType, int il);

  public static native long cb(long tensor, String name, int il);
  public static native long ggml_tensor(long tensor);
  public static native long ggml_add(long ctx0, long tensor1, long tensor2);
  public static native long build_lora_mm(long wq, long cur);
  public static native long ggml_reshape_3d(long ctx0, long tensor, int n_embdHead, int nHead, int nTokens);

  public static native long ggml_rope_ext(long ctx0, long Qcur, long inpPos, long ropeFactors, int nRot, String ropeType, int nCtxOrig, float freqBase, float freqScale, double extFactor, double attnFactor, double betaFast, double betaSlow);
  public static native long build_attn(long attnInp, long wo, long bo, long Qcur, long Kcur, long Vcur, long other1, long other2, float alpha);

  public static native long ggml_get_rows(long ctx0, long tensor, int inpOutIds);

  public static native long ggml_build_forward_expand(long gf, long res);
}