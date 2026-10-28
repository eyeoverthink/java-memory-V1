package fraymus.ui;

/**
 * Fraynix ASCII/Unicode Symbol Manager
 * Handles proper rendering across Windows CMD, PowerShell, and Unix terminals
 */
public final class AsciiSymbols {
    
    // Auto-detect Unicode support at runtime
    public static final boolean USE_UNICODE = detectUnicodeSupport();
    
    private static boolean detectUnicodeSupport() {
        String term = System.getenv("TERM");
        String os = System.getProperty("os.name", "").toLowerCase();
        
        // Unix/Linux/Mac with proper TERM
        if (term != null && !term.equals("dumb")) {
            return true;
        }
        
        // Windows Terminal, VS Code, IntelliJ
        if (os.contains("win")) {
            String wt = System.getenv("WT_SESSION");
            String vscode = System.getenv("TERM_PROGRAM");
            if (wt != null || "vscode".equals(vscode)) {
                return true;
            }
        }
        
        return false;
    }
    
    // BOX DRAWING - Double Line
    public static final String BOX_DBL_TL  = USE_UNICODE ? "╔" : "+";  // Top Left
    public static final String BOX_DBL_TR  = USE_UNICODE ? "╗" : "+";  // Top Right
    public static final String BOX_DBL_BL  = USE_UNICODE ? "╚" : "+";  // Bottom Left
    public static final String BOX_DBL_BR  = USE_UNICODE ? "╝" : "+";  // Bottom Right
    public static final String BOX_DBL_H   = USE_UNICODE ? "═" : "="; // Horizontal
    public static final String BOX_DBL_V   = USE_UNICODE ? "║" : "|"; // Vertical
    
    // BOX DRAWING - Single Line
    public static final String BOX_SNG_TL  = USE_UNICODE ? "┌" : ",";  // Top Left
    public static final String BOX_SNG_TR  = USE_UNICODE ? "┐" : ".";  // Top Right
    public static final String BOX_SNG_BL  = USE_UNICODE ? "└" : "`";  // Bottom Left
    public static final String BOX_SNG_BR  = USE_UNICODE ? "┘" : "'";  // Bottom Right
    public static final String BOX_SNG_H   = USE_UNICODE ? "─" : "-"; // Horizontal
    public static final String BOX_SNG_V   = USE_UNICODE ? "│" : "|"; // Vertical
    
    // BOX DRAWING - Mixed
    public static final String BOX_L_T     = USE_UNICODE ? "╠" : "+";  // Left T
    public static final String BOX_R_T     = USE_UNICODE ? "╣" : "+";  // Right T
    public static final String BOX_T_T     = USE_UNICODE ? "╦" : "+";  // Top T
    public static final String BOX_B_T     = USE_UNICODE ? "╩" : "+";  // Bottom T
    public static final String BOX_CROSS   = USE_UNICODE ? "╬" : "+";  // Cross
    
    // MATHEMATICAL SYMBOLS
    public static final String PHI         = USE_UNICODE ? "φ" : "phi";
    public static final String PHI_CAP     = USE_UNICODE ? "Φ" : "Phi";
    public static final String PSI         = USE_UNICODE ? "Ψ" : "Psi";
    public static final String OMEGA       = USE_UNICODE ? "Ω" : "Omega";
    public static final String SIGMA       = USE_UNICODE ? "σ" : "sigma";
    public static final String SIGMA_CAP   = USE_UNICODE ? "Σ" : "Sigma";
    public static final String GAMMA       = USE_UNICODE ? "γ" : "gamma";
    public static final String THETA       = USE_UNICODE ? "θ" : "theta";
    public static final String DELTA       = USE_UNICODE ? "Δ" : "delta";
    public static final String LAMBDA      = USE_UNICODE ? "λ" : "lambda";
    public static final String ALPHA       = USE_UNICODE ? "α" : "alpha";
    public static final String BETA        = USE_UNICODE ? "β" : "beta";
    
    // Math Operators
    public static final String INF         = USE_UNICODE ? "∞" : "inf";
    public static final String APPROX      = USE_UNICODE ? "≈" : "~";
    public static final String NOT_EQ       = USE_UNICODE ? "≠" : "!=";
    public static final String LESS_EQ      = USE_UNICODE ? "≤" : "<=";
    public static final String GREATER_EQ   = USE_UNICODE ? "≥" : ">=";
    public static final String PLUS_MINUS   = USE_UNICODE ? "±" : "+/-";
    public static final String TIMES        = USE_UNICODE ? "×" : "x";
    public static final String DIVIDE       = USE_UNICODE ? "÷" : "/";
    public static final String INTEGRAL     = USE_UNICODE ? "∫" : "S";
    public static final String PARTIAL      = USE_UNICODE ? "∂" : "d";
    public static final String NABLA        = USE_UNICODE ? "∇" : "grad";
    public static final String SUM          = USE_UNICODE ? "∑" : "Sum";
    public static final String PROD         = USE_UNICODE ? "∏" : "Prod";
    public static final String SQRT         = USE_UNICODE ? "√" : "sqrt";
    public static final String PROPORTIONAL = USE_UNICODE ? "∝" : "~";
    public static final String INFINITY     = USE_UNICODE ? "∞" : "inf";
    
    // Arrows
    public static final String ARROW_R      = USE_UNICODE ? "→" : "->";
    public static final String ARROW_L      = USE_UNICODE ? "←" : "<-";
    public static final String ARROW_U      = USE_UNICODE ? "↑" : "^";
    public static final String ARROW_D      = USE_UNICODE ? "↓" : "v";
    public static final String ARROW_LR     = USE_UNICODE ? "↔" : "<->";
    public static final String ARROW_UD     = USE_UNICODE ? "↕" : "^v";
    public static final String ARROW_R_H    = USE_UNICODE ? "⇒" : "=>";
    public static final String ARROW_L_H    = USE_UNICODE ? "⇐" : "<=";
    
    // STATUS INDICATORS
    public static final String CHECK        = USE_UNICODE ? "✓" : "[OK]";
    public static final String CHECK_HEAVY  = USE_UNICODE ? "✔" : "[OK]";
    public static final String X_MARK       = USE_UNICODE ? "✗" : "[X]";
    public static final String X_HEAVY      = USE_UNICODE ? "✘" : "[X]";
    public static final String BULLET       = USE_UNICODE ? "•" : "*";
    public static final String BULLET_H     = USE_UNICODE ? "●" : "#";
    public static final String STAR         = USE_UNICODE ? "★" : "*";
    public static final String DIAMOND      = USE_UNICODE ? "◆" : ">";
    public static final String CIRCLE       = USE_UNICODE ? "○" : "o";
    public static final String CIRCLE_F     = USE_UNICODE ? "●" : "#";
    
    // EMOJI / SPECIAL
    public static final String LIGHTNING    = USE_UNICODE ? "⚡" : ">>>";
    public static final String GEAR         = USE_UNICODE ? "⚙" : "[G]";
    public static final String ATOM         = USE_UNICODE ? "⚛" : "[A]";
    public static final String BIOHAZARD    = USE_UNICODE ? "☢" : "[!]";
    public static final String RADIOACTIVE  = USE_UNICODE ? "☣" : "[!]";
    public static final String INFINITY_S   = USE_UNICODE ? "∞" : "oo";
    public static final String HOURGLASS    = USE_UNICODE ? "⌛" : "...";
    public static final String WATCH        = USE_UNICODE ? "⌚" : "[T]";
    public static final String DNA          = USE_UNICODE ? "🧬" : "[DNA]";
    public static final String BRAIN        = USE_UNICODE ? "🧠" : "[BRAIN]";
    public static final String RAINBOW      = USE_UNICODE ? "🌈" : "[RB]";
    public static final String BOOKS        = USE_UNICODE ? "📚" : "[LIB]";
    public static final String LINK         = USE_UNICODE ? "🔗" : "[LNK]";
    public static final String FOLDER       = USE_UNICODE ? "📁" : "[DIR]";
    public static final String FILE         = USE_UNICODE ? "📄" : "[FILE]";
    
    // FRAYNIX SPECIFIC
    public static final String PHI_SQUARED  = USE_UNICODE ? "φ²" : "phi^2";
    public static final String PHI_INV      = USE_UNICODE ? "1/φ" : "1/phi";
    public static final String PHI_OPTIMAL  = USE_UNICODE ? "φ⁻¹" : "1/phi";
    public static final String COHERENCE    = USE_UNICODE ? "Ψ" : "Psi";
    public static final String D_PHI        = USE_UNICODE ? "D_φ" : "D_phi";
    public static final String C_SIGMA      = USE_UNICODE ? "C_σ" : "C_sigma";
    
    // UTILITY METHODS
    
    /**
     * Create a double-line box header
     */
    public static String boxHeader(String text, int width) {
        StringBuilder sb = new StringBuilder();
        sb.append(BOX_DBL_TL);
        for (int i = 0; i < width - 2; i++) sb.append(BOX_DBL_H);
        sb.append(BOX_DBL_TR).append("\n");
        
        int padLeft = (width - 2 - text.length()) / 2;
        int padRight = width - 2 - text.length() - padLeft;
        
        sb.append(BOX_DBL_V);
        for (int i = 0; i < padLeft; i++) sb.append(" ");
        sb.append(text);
        for (int i = 0; i < padRight; i++) sb.append(" ");
        sb.append(BOX_DBL_V).append("\n");
        
        sb.append(BOX_DBL_BL);
        for (int i = 0; i < width - 2; i++) sb.append(BOX_DBL_H);
        sb.append(BOX_DBL_BR);
        
        return sb.toString();
    }
    
    /**
     * Create a simple separator line
     */
    public static String separator(int width) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) sb.append(BOX_DBL_H);
        return sb.toString();
    }
    
    /**
     * Format phi with proper symbol
     */
    public static String phiValue(double value) {
        return String.format("%s = %.15f", PHI, value);
    }
    
    /**
     * Check if current terminal supports Unicode
     */
    public static boolean isUnicodeSupported() {
        return USE_UNICODE;
    }
    
    /**
     * Print encoding info for debugging
     */
    public static void printEncodingInfo() {
        System.out.println("Unicode Support: " + USE_UNICODE);
        System.out.println("file.encoding: " + System.getProperty("file.encoding"));
        System.out.println("stdout.encoding: " + System.getProperty("sun.stdout.encoding", "default"));
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("TERM: " + System.getenv("TERM"));
    }
    
    private AsciiSymbols() {}
}
