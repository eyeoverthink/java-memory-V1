package fraymus;

import java.util.Random;

public enum AntRole {
    LOGIC_GATE(
        "Logic Gate",
        new float[]{0.0f, 1.0f, 0.4f},
        0.8,
        new String[]{
            "public static int and(int a, int b) { return a & b; }",
            "public static int or(int a, int b) { return a | b; }",
            "public static int xor(int a, int b) { return a ^ b; }",
            "public static int nand(int a, int b) { return ~(a & b) & 1; }",
            "public static int nor(int a, int b) { return ~(a | b) & 1; }",
            "public static int not(int a) { return ~a & 1; }",
            "public static int mux(int sel, int a, int b) { return sel == 1 ? a : b; }",
            "public static int halfAdder(int a, int b) { return (a ^ b) | ((a & b) << 1); }"
        }
    ),
    MATH_PROCESSOR(
        "Math Processor",
        new float[]{0.4f, 0.6f, 1.0f},
        0.7,
        new String[]{
            "public static double fibonacci(int n) { double a=0,b=1; for(int i=0;i<n;i++){double t=a+b;a=b;b=t;} return a; }",
            "public static double goldenRatio(int depth) { double r=1; for(int i=0;i<depth;i++) r=1+1/r; return r; }",
            "public static double phiPower(double phi, int n) { double r=1; for(int i=0;i<n;i++) r*=phi; return r; }",
            "public static int gcd(int a, int b) { while(b!=0){int t=b;b=a%b;a=t;} return a; }",
            "public static boolean isPrime(int n) { if(n<2) return false; for(int i=2;i*i<=n;i++) if(n%i==0) return false; return true; }",
            "public static double harmonicMean(double a, double b) { return 2*a*b/(a+b); }",
            "public static double resonanceFreq(double f, double phi) { return f * (1 + 1/phi); }",
            "public static double phaseAlign(double p1, double p2) { double d=p1-p2; while(d>Math.PI)d-=2*Math.PI; while(d<-Math.PI)d+=2*Math.PI; return d; }"
        }
    ),
    CIRCUIT_BUILDER(
        "Circuit Builder",
        new float[]{1.0f, 0.5f, 0.0f},
        0.9,
        new String[]{
            "public static int[] pipeline(int[] inputs, int stages) { int[] out=new int[inputs.length]; for(int s=0;s<stages;s++) for(int i=0;i<out.length;i++) out[i]=inputs[i]^(s&1); return out; }",
            "public static int cascade(int a, int b, int c) { return (a & b) | (b & c) | (a & c); }",
            "public static int[] register(int[] state, int input, int clock) { if(clock==1) { int[] next=new int[state.length]; System.arraycopy(state,1,next,0,state.length-1); next[state.length-1]=input; return next; } return state; }",
            "public static int alu(int a, int b, int op) { switch(op){case 0:return a+b;case 1:return a-b;case 2:return a&b;case 3:return a|b;default:return a^b;} }",
            "public static boolean[] decoder(int input, int bits) { boolean[] out=new boolean[1<<bits]; if(input<out.length) out[input]=true; return out; }",
            "public static int multiplexer(int[] inputs, int sel) { return sel<inputs.length ? inputs[sel] : 0; }"
        }
    ),
    MEMORY_KEEPER(
        "Memory Keeper",
        new float[]{0.8f, 0.0f, 1.0f},
        0.6,
        new String[]{
            "public static int[] stack_push(int[] stack, int sp, int val) { if(sp<stack.length) stack[sp]=val; return stack; }",
            "public static int stack_pop(int[] stack, int sp) { return sp>0 ? stack[sp-1] : -1; }",
            "public static int hash(String key) { int h=0; for(char c:key.toCharArray()) h=31*h+c; return h&0x7fffffff; }",
            "public static int[] ringBuffer(int[] buf, int pos, int val) { buf[pos%buf.length]=val; return buf; }",
            "public static String encode(int[] data) { StringBuilder sb=new StringBuilder(); for(int d:data) sb.append(Integer.toHexString(d)); return sb.toString(); }",
            "public static int[] decode(String hex) { int[] data=new int[hex.length()/2]; for(int i=0;i<data.length;i++) data[i]=Integer.parseInt(hex.substring(i*2,i*2+2),16); return data; }"
        }
    ),
    COMMUNICATOR(
        "Communicator",
        new float[]{1.0f, 1.0f, 0.0f},
        0.75,
        new String[]{
            "public static String signal(String from, String to, double freq) { return from+\"->[\"+String.format(\"%.2f\",freq)+\"]->\"+to; }",
            "public static double syncPhase(double p1, double p2, double strength) { return p1+(p2-p1)*strength; }",
            "public static boolean handshake(double freqA, double freqB, double tolerance) { return Math.abs(freqA-freqB)<tolerance; }",
            "public static String broadcast(String sender, String[] receivers, String msg) { StringBuilder sb=new StringBuilder(sender+\" >> \"); for(String r:receivers) sb.append(r).append(\",\"); return sb.append(\": \").append(msg).toString(); }",
            "public static double[] frequency_encode(String msg) { double[] freqs=new double[msg.length()]; for(int i=0;i<msg.length();i++) freqs[i]=432+((msg.charAt(i)%96)*1.0); return freqs; }",
            "public static String frequency_decode(double[] freqs) { StringBuilder sb=new StringBuilder(); for(double f:freqs) sb.append((char)(((int)(f-432))%96+32)); return sb.toString(); }"
        }
    );

    private static final Random rng = new Random();

    public final String displayName;
    public final float[] color;
    public final double concentrationFactor;
    public final String[] codeTemplates;

    AntRole(String displayName, float[] color, double concentrationFactor, String[] codeTemplates) {
        this.displayName = displayName;
        this.color = color;
        this.concentrationFactor = concentrationFactor;
        this.codeTemplates = codeTemplates;
    }

    public String generateCodeFragment(PhiNode node) {
        if (codeTemplates.length == 0) return "";

        // Try self-evolving code 20% of the time
        SelfCodeEvolver evolver = jade.Window.getSelfCodeEvolver();
        if (evolver != null && rng.nextDouble() < 0.2) {
            int idx = Math.abs(node.signature.hashCode()) % codeTemplates.length;
            String baseTemplate = codeTemplates[idx];
            
            SelfCodeEvolver.EvolutionResult evolved = evolver.replicateAndImprove(baseTemplate);
            if (evolved.resurrectionReady && evolved.phiIntegrity > 0.5) {
                return "// φ-EVOLVED [" + evolved.corticalRegion + "] integrity=" + 
                       String.format("%.3f", evolved.phiIntegrity) + "\n" + evolved.evolvedSource;
            }
        }

        // Try to use scraped knowledge 30% of the time
        KnowledgeScraper scraper = jade.Window.getKnowledgeScraper();
        if (scraper != null && rng.nextDouble() < 0.3) {
            String topic = getPreferredTopic();
            String knowledge = scraper.queryKnowledge(topic);
            if (knowledge != null && knowledge.length() > 50) {
                // Extract code-like patterns from scraped knowledge
                String extracted = extractCodePattern(knowledge);
                if (extracted != null && extracted.length() > 20) {
                    return "// From scraped knowledge: " + topic + "\n" + extracted;
                }
            }
        }

        int idx = Math.abs(node.signature.hashCode() + node.age) % codeTemplates.length;
        String template = codeTemplates[idx];

        double mutationChance = 1.0 - concentrationFactor;
        if (rng.nextDouble() < mutationChance && codeTemplates.length > 1) {
            int altIdx = rng.nextInt(codeTemplates.length);
            String alt = codeTemplates[altIdx];
            int splitPoint = Math.min(template.length(), alt.length()) / 2;
            template = template.substring(0, splitPoint) + alt.substring(splitPoint);
        }

        return template;
    }
    
    private String getPreferredTopic() {
        switch (this) {
            case LOGIC_GATE: return "programming";
            case MATH_PROCESSOR: return "mathematics";
            case CIRCUIT_BUILDER: return "programming";
            case MEMORY_KEEPER: return "programming";
            case COMMUNICATOR: return "programming";
            default: return "programming";
        }
    }
    
    private String extractCodePattern(String knowledge) {
        // Look for function definitions, class definitions, or code blocks
        String[] lines = knowledge.split("\n");
        StringBuilder code = new StringBuilder();
        boolean inCodeBlock = false;
        int linesAdded = 0;
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            // Detect code patterns
            if (trimmed.startsWith("def ") || trimmed.startsWith("class ") || 
                trimmed.startsWith("public ") || trimmed.startsWith("private ") ||
                trimmed.startsWith("function ") || trimmed.contains("=>") ||
                trimmed.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(.*\\).*")) {
                inCodeBlock = true;
            }
            
            if (inCodeBlock) {
                code.append(line).append("\n");
                linesAdded++;
                
                // Stop after reasonable amount
                if (linesAdded > 10 || (trimmed.isEmpty() && linesAdded > 3)) {
                    break;
                }
            }
        }
        
        String result = code.toString().trim();
        return result.length() > 20 ? result : null;
    }

    public static AntRole assignFromFrequency(float frequency) {
        double normalized = (frequency - 432.0) / (528.0 - 432.0);
        normalized = Math.max(0, Math.min(1, normalized));

        if (normalized < 0.2) return LOGIC_GATE;
        if (normalized < 0.4) return MATH_PROCESSOR;
        if (normalized < 0.6) return CIRCUIT_BUILDER;
        if (normalized < 0.8) return MEMORY_KEEPER;
        return COMMUNICATOR;
    }

    public static AntRole random() {
        AntRole[] roles = values();
        return roles[rng.nextInt(roles.length)];
    }

    public int getBrainBias() {
        switch (this) {
            case LOGIC_GATE: return LogicBrain.MUTATE;
            case MATH_PROCESSOR: return LogicBrain.EVOLVE_DNA;
            case CIRCUIT_BUILDER: return LogicBrain.ENERGY_BURST;
            case MEMORY_KEEPER: return LogicBrain.CONSERVE;
            case COMMUNICATOR: return LogicBrain.ENTANGLE_SEEK;
            default: return LogicBrain.SEEK;
        }
    }

    public double getSpecializationBonus(int[] brainOutputs) {
        int biasOutput = getBrainBias();
        if (biasOutput < brainOutputs.length && brainOutputs[biasOutput] == 1) {
            return concentrationFactor * 0.1;
        }
        return 0.0;
    }
}
