package repl;

import java.util.ArrayList;
import java.util.List;

class Evolved_llamaiswacpp {
    private double n_embd_head_v;
    private double f_attention_scale;
    private int n_rot;
    private int no_rope_layer_step;
    private int n_no_rope_layer_step;
    private double norm_rms_eps;
    private int num_expert;
    private int expert_used;

    public Evolved_llamaiswacpp(double n_embd_head_v, double f_attention_scale, int n_rot, int no_rope_layer_step, int n_no_rope_layer_step, double norm_rms_eps, int num_expert, int expert_used) {
        this.n_embd_head_v = n_embd_head_v;
        this.f_attention_scale = f_attention_scale;
        this.n_rot = n_rot;
        this.no_rope_layer_step = no_rope_layer_step;
        this.n_no_rope_layer_step = n_no_rope_layer_step;
        this.norm_rms_eps = norm_rms_eps;
        this.num_expert = num_expert;
        this.expert_used = expert_used;
    }

    public double getN_embd_head_v() {
        return n_embd_head_v;
    }

    public double getF_attention_scale() {
        return f_attention_scale;
    }

    public int getN_rot() {
        return n_rot;
    }

    public int getNo_rope_layer_step() {
        return no_rope_layer_step;
    }

    public int getN_no_rope_layer_step() {
        return n_no_rope_layer_step;
    }

    public double getNorm_rms_eps() {
        return norm_rms_eps;
    }

    public int getNum_expert() {
        return num_expert;
    }

    public int getExpert_used() {
        return expert_used;
    }
}