/**
 * Lexer.java - φ-Harmonic Lexical Analyzer
 * 
 * Tokenizes source code into structured tokens with:
 * - Keyword recognition
 * - Operator parsing
 * - Number/string literal extraction
 * - Position tracking (line, column)
 * - φ-harmonic weight calculation
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import java.util.*;

/**
 * Lexical analyzer with φ-harmonic tokenization.
 */
public class Lexer {
    
    private static final double PHI = 1.618033988749895;
    
    private final String source;
    private final List<Token> tokens;
    private int start;
    private int current;
    private int line;
    private int column;
    
    /**
     * Create a lexer for source code.
     * 
     * @param source Source code to tokenize
     */
    public Lexer(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
        this.start = 0;
        this.current = 0;
        this.line = 1;
        this.column = 1;
    }
    
    /**
     * Tokenize the entire source.
     * 
     * @return List of tokens
     */
    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        
        tokens.add(new Token(TokenType.EOF, "", null, line, column));
        return tokens;
    }
    
    /**
     * Scan a single token.
     */
    private void scanToken() {
        char c = advance();
        
        switch (c) {
            // Single-character tokens
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case '[': addToken(TokenType.LEFT_BRACKET); break;
            case ']': addToken(TokenType.RIGHT_BRACKET); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case ':': addToken(TokenType.COLON); break;
            case '^': addToken(TokenType.POWER); break;
            case '%': addToken(TokenType.PERCENT); break;
            
            // Operators (may be multi-character)
            case '+':
                if (match('+')) addToken(TokenType.INCREMENT);
                else if (match('=')) addToken(TokenType.PLUS_ASSIGN);
                else addToken(TokenType.PLUS);
                break;
            case '-':
                if (match('-')) addToken(TokenType.DECREMENT);
                else if (match('=')) addToken(TokenType.MINUS_ASSIGN);
                else if (match('>')) addToken(TokenType.ARROW);
                else addToken(TokenType.MINUS);
                break;
            case '*':
                if (match('=')) addToken(TokenType.STAR_ASSIGN);
                else addToken(TokenType.STAR);
                break;
            case '/':
                if (match('=')) addToken(TokenType.SLASH_ASSIGN);
                else if (match('/')) {
                    // Line comment
                    while (peek() != '\n' && !isAtEnd()) advance();
                    addToken(TokenType.COMMENT);
                } else if (match('*')) {
                    // Block comment
                    blockComment();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case '!':
                addToken(match('=') ? TokenType.NOT_EQUAL : TokenType.NOT);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL : TokenType.ASSIGN);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '&':
                if (match('&')) addToken(TokenType.AND);
                else error("Unexpected character: &");
                break;
            case '|':
                if (match('|')) addToken(TokenType.OR);
                else error("Unexpected character: |");
                break;
            
            // Whitespace
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace
                break;
            case '\n':
                line++;
                column = 1;
                break;
            
            // String literals
            case '"':
                string();
                break;
            case '\'':
                charLiteral();
                break;
            
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    error("Unexpected character: " + c);
                }
                break;
        }
    }
    
    /**
     * Parse identifier or keyword.
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        
        String text = source.substring(start, current);
        TokenType type = TokenType.getKeyword(text);
        
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        
        addToken(type);
    }
    
    /**
     * Parse number literal.
     */
    private void number() {
        while (isDigit(peek())) advance();
        
        // Look for decimal point
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consume '.'
            while (isDigit(peek())) advance();
        }
        
        // Scientific notation
        if (peek() == 'e' || peek() == 'E') {
            advance();
            if (peek() == '+' || peek() == '-') advance();
            while (isDigit(peek())) advance();
        }
        
        String text = source.substring(start, current);
        double value = Double.parseDouble(text);
        addToken(TokenType.NUMBER, value);
    }
    
    /**
     * Parse string literal.
     */
    private void string() {
        StringBuilder value = new StringBuilder();
        
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            if (peek() == '\\') {
                advance();
                char escaped = advance();
                switch (escaped) {
                    case 'n': value.append('\n'); break;
                    case 't': value.append('\t'); break;
                    case 'r': value.append('\r'); break;
                    case '\\': value.append('\\'); break;
                    case '"': value.append('"'); break;
                    default: value.append(escaped); break;
                }
            } else {
                value.append(advance());
            }
        }
        
        if (isAtEnd()) {
            error("Unterminated string");
            return;
        }
        
        advance(); // closing "
        addToken(TokenType.STRING, value.toString());
    }
    
    /**
     * Parse character literal.
     */
    private void charLiteral() {
        if (isAtEnd()) {
            error("Unterminated character literal");
            return;
        }
        
        char value = advance();
        if (value == '\\') {
            char escaped = advance();
            switch (escaped) {
                case 'n': value = '\n'; break;
                case 't': value = '\t'; break;
                case 'r': value = '\r'; break;
                case '\\': value = '\\'; break;
                case '\'': value = '\''; break;
                default: value = escaped; break;
            }
        }
        
        if (peek() != '\'') {
            error("Unterminated character literal");
            return;
        }
        
        advance(); // closing '
        addToken(TokenType.STRING, String.valueOf(value));
    }
    
    /**
     * Parse block comment.
     */
    private void blockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance(); // *
                advance(); // /
                break;
            }
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            advance();
        }
        addToken(TokenType.COMMENT);
    }
    
    /**
     * Add token without literal.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    
    /**
     * Add token with literal value.
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, column - (current - start)));
    }
    
    /**
     * Report error.
     */
    private void error(String message) {
        String text = source.substring(start, current);
        tokens.add(new Token(TokenType.ERROR, text, message, line, column));
    }
    
    /**
     * Match expected character.
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        column++;
        return true;
    }
    
    /**
     * Peek current character without consuming.
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    
    /**
     * Peek next character without consuming.
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    /**
     * Advance to next character.
     */
    private char advance() {
        column++;
        return source.charAt(current++);
    }
    
    /**
     * Check if at end of source.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }
    
    /**
     * Check if character is digit.
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    /**
     * Check if character is alphabetic.
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }
    
    /**
     * Check if character is alphanumeric.
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    /**
     * Get all tokens.
     */
    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
    
    /**
     * Get token statistics with φ-harmonic analysis.
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  LEXER STATISTICS (φ-Harmonic Analysis)                    ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        Map<TokenType, Integer> counts = new HashMap<>();
        double totalWeight = 0.0;
        
        for (Token token : tokens) {
            counts.put(token.getType(), counts.getOrDefault(token.getType(), 0) + 1);
            totalWeight += token.getPhiWeight();
        }
        
        sb.append(String.format("Total Tokens: %d\n", tokens.size()));
        sb.append(String.format("Total φ-Weight: %.4f\n", totalWeight));
        sb.append(String.format("Average φ-Weight: %.4f\n\n", totalWeight / tokens.size()));
        
        sb.append("Token Distribution:\n");
        counts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(entry -> {
                sb.append(String.format("  %-20s : %4d (%.1f%%)\n",
                    entry.getKey(),
                    entry.getValue(),
                    100.0 * entry.getValue() / tokens.size()));
            });
        
        return sb.toString();
    }
}
