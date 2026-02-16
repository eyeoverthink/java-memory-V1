/**
 * TokenType.java - Token Classification System
 * 
 * Defines all token types with φ-harmonic base weights.
 * Higher weight = higher execution priority.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

/**
 * Token type enumeration with φ-harmonic weights.
 */
public enum TokenType {
    
    // ============================================================
    // KEYWORDS (Weight: φ^2 = 2.618)
    // ============================================================
    IF("if", 2.618),
    ELSE("else", 2.618),
    WHILE("while", 2.618),
    FOR("for", 2.618),
    FUNCTION("function", 2.618),
    RETURN("return", 2.618),
    VAR("var", 2.618),
    LET("let", 2.618),
    CONST("const", 2.618),
    CLASS("class", 2.618),
    NEW("new", 2.618),
    THIS("this", 2.618),
    TRUE("true", 2.618),
    FALSE("false", 2.618),
    NULL("null", 2.618),
    PRINT("print", 2.618),
    
    // ============================================================
    // OPERATORS (Weight: φ = 1.618)
    // ============================================================
    PLUS("+", 1.618),
    MINUS("-", 1.618),
    STAR("*", 1.618),
    SLASH("/", 1.618),
    PERCENT("%", 1.618),
    POWER("^", 1.618),
    
    ASSIGN("=", 1.618),
    PLUS_ASSIGN("+=", 1.618),
    MINUS_ASSIGN("-=", 1.618),
    STAR_ASSIGN("*=", 1.618),
    SLASH_ASSIGN("/=", 1.618),
    
    EQUAL("==", 1.618),
    NOT_EQUAL("!=", 1.618),
    LESS("<", 1.618),
    LESS_EQUAL("<=", 1.618),
    GREATER(">", 1.618),
    GREATER_EQUAL(">=", 1.618),
    
    AND("&&", 1.618),
    OR("||", 1.618),
    NOT("!", 1.618),
    
    INCREMENT("++", 1.618),
    DECREMENT("--", 1.618),
    
    // ============================================================
    // DELIMITERS (Weight: 1.0)
    // ============================================================
    LEFT_PAREN("(", 1.0),
    RIGHT_PAREN(")", 1.0),
    LEFT_BRACE("{", 1.0),
    RIGHT_BRACE("}", 1.0),
    LEFT_BRACKET("[", 1.0),
    RIGHT_BRACKET("]", 1.0),
    COMMA(",", 1.0),
    DOT(".", 1.0),
    SEMICOLON(";", 1.0),
    COLON(":", 1.0),
    ARROW("->", 1.0),
    
    // ============================================================
    // LITERALS (Weight: φ^3 = 4.236)
    // ============================================================
    IDENTIFIER("<identifier>", 4.236),
    NUMBER("<number>", 4.236),
    STRING("<string>", 4.236),
    
    // ============================================================
    // SPECIAL (Weight: φ^-1 = 0.618)
    // ============================================================
    COMMENT("<comment>", 0.618),
    WHITESPACE("<whitespace>", 0.618),
    NEWLINE("<newline>", 0.618),
    EOF("<eof>", 0.618),
    ERROR("<error>", 0.618);
    
    private final String representation;
    private final double baseWeight;
    
    TokenType(String representation, double baseWeight) {
        this.representation = representation;
        this.baseWeight = baseWeight;
    }
    
    public String getRepresentation() { return representation; }
    public double getBaseWeight() { return baseWeight; }
    
    /**
     * Check if this is a keyword.
     */
    public boolean isKeyword() {
        return baseWeight == 2.618;
    }
    
    /**
     * Check if this is an operator.
     */
    public boolean isOperator() {
        return baseWeight == 1.618;
    }
    
    /**
     * Check if this is a literal.
     */
    public boolean isLiteral() {
        return baseWeight == 4.236;
    }
    
    /**
     * Get keyword by name.
     */
    public static TokenType getKeyword(String text) {
        switch (text) {
            case "if": return IF;
            case "else": return ELSE;
            case "while": return WHILE;
            case "for": return FOR;
            case "function": return FUNCTION;
            case "return": return RETURN;
            case "var": return VAR;
            case "let": return LET;
            case "const": return CONST;
            case "class": return CLASS;
            case "new": return NEW;
            case "this": return THIS;
            case "true": return TRUE;
            case "false": return FALSE;
            case "null": return NULL;
            case "print": return PRINT;
            default: return null;
        }
    }
}
