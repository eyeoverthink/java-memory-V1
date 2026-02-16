import java.util.ArrayList;
import java.util.List;

public class Evolved_neobertcpp {
    private int n_embd_head_v;
    private int n_embd_gqa_v;
    private int n_layer;
    private float[] wqkv;
    private float[][] wqkv_t;
    private int n_rot;
    private String rope_type;
    private int n_ctx_orig;
    private float freq_base;
    private float freq_scale;
    private float ext_factor;
    private float attn_factor;
    private float beta_fast;
    private float beta_slow;

    public Evolved_neobertcpp() {}

    public void init_params(int n_embd_head, int n_embd_gqa, int n_layer) {
        this.n_embd_head_v = n_embd_head;
        this.n_embd_gqa_v = n_embd_gqa;
        this.n_layer = n_layer;
    }

    public float[] getWqkv() {
        return wqkv;
    }

    public void setWqkv(float[] wqkv) {
        this.wqkv = wqkv;
    }

    public float[][] getWqkvT() {
        return wqkv_t;
    }

    public void setWqkvT(float[][] wqkvT) {
        this.wqkv_t = wqkvT;
    }

    public int getN_rot() {
        return n_rot;
    }

    public void setN_rot(int n_rot) {
        this.n_rot = n_rot;
    }

    public String getRope_type() {
        return rope_type;
    }

    public void setRope_type(String rope_type) {
        this.rope_type = rope_type;
    }

    public int getN_ctx_orig() {
        return n_ctx_orig;
    }

    public void setN_ctx_orig(int n_ctx_orig) {
        this.n_ctx_orig = n_ctx_orig;
    }

    public float getFreq_base() {
        return freq_base;
    }

    public void setFreq_base(float freq_base) {
        this.freq_base = freq_base;
    }

    public float getFreq_scale() {
        return freq_scale;
    }

    public void setFreq_scale(float freq_scale) {
        this.freq_scale = freq_scale;
    }

    public float getExt_factor() {
        return ext_factor;
    }

    public void setExt_factor(float ext_factor) {
        this.ext_factor = ext_factor;
    }

    public float getAttn_factor() {
        return attn_factor;
    }

    public void setAttn_factor(float attn_factor) {
        this.attn_factor = attn_factor;
    }

    public float getBeta_fast() {
        return beta_fast;
    }

    public void setBeta_fast(float beta_fast) {
        this.beta_fast = beta_fast;
    }

    public float getBeta_slow() {
        return beta_slow;
    }

    public void setBeta_slow(float beta_slow) {
        this.beta_slow = beta_slow;
    }
}