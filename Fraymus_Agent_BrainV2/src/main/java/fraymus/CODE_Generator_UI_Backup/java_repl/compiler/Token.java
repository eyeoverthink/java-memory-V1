/**
 * Token.java - Lexer Token with φ-Harmonic Resonance
 * 
 * Represents a single token from the lexer with:
 * - Type classification (keyword, operator, identifier, literal)
 * - Lexeme (actual text)
 * - Position tracking (line, column)
 * - φ-Harmonic weight for optimization
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

/**
 * Token representation with φ-harmonic properties.
 */
public class Token {
    
    private static final double PHI = 1.618033988749895;
    
    private final TokenType type;
    private final String lexeme;
    private final Object literal;
    private final int line;
    private final int column;
    private final double phiWeight;
    
    /**
     * Create a token.
     * 
     * @param type Token type
     * @param lexeme Source text
     * @param literal Literal value (for numbers, strings)
     * @param line Line number
     * @param column Column number
     */
    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
        this.phiWeight = calculatePhiWeight();
    }
    
    /**
     * Calculate φ-harmonic weight for optimization.
     * Higher weight = higher priority in execution.
     */
    private double calculatePhiWeight() {
        double baseWeight = type.getBaseWeight();
        double lengthFactor = Math.pow(PHI, lexeme.length() % 7);
        double positionFactor = 1.0 / (1.0 + line * 0.01);
        return baseWeight * lengthFactor * positionFactor;
    }
    
    public TokenType getType() { return type; }
    public String getLexeme() { return lexeme; }
    public Object getLiteral() { return literal; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public double getPhiWeight() { return phiWeight; }
    
    @Override
    public String toString() {
        return String.format("Token{%s, '%s', φ=%.4f, @%d:%d}", 
            type, lexeme, phiWeight, line, column);
    }
    
    /**
     * Debug representation with full details.
     */
    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("╭─────────────────────────────────────────╮\n"));
        sb.append(String.format("│ TOKEN: %-32s │\n", type));
        sb.append(String.format("├─────────────────────────────────────────┤\n"));
        sb.append(String.format("│ Lexeme:   %-29s │\n", "'" + lexeme + "'"));
        if (literal != null) {
            sb.append(String.format("│ Literal:  %-29s │\n", literal));
        }
        sb.append(String.format("│ Position: Line %d, Col %-15d │\n", line, column));
        sb.append(String.format("│ φ-Weight: %-29.6f │\n", phiWeight));
        sb.append(String.format("╰─────────────────────────────────────────╯"));
        return sb.toString();
    }
}
