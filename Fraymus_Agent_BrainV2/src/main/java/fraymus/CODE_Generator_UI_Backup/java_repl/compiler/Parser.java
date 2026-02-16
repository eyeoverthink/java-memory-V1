/**
 * Parser.java - Recursive Descent Parser with φ-Harmonic AST
 * 
 * Parses tokens into Abstract Syntax Tree using recursive descent.
 * 
 * Grammar:
 *   program    → statement* EOF
 *   statement  → varDecl | exprStmt | ifStmt | whileStmt | block | returnStmt
 *   varDecl    → "var" IDENTIFIER ("=" expression)? ";"
 *   exprStmt   → expression ";"
 *   ifStmt     → "if" "(" expression ")" statement ("else" statement)?
 *   whileStmt  → "while" "(" expression ")" statement
 *   block      → "{" statement* "}"
 *   returnStmt → "return" expression? ";"
 *   expression → assignment
 *   assignment → IDENTIFIER "=" assignment | logic_or
 *   logic_or   → logic_and ("||" logic_and)*
 *   logic_and  → equality ("&&" equality)*
 *   equality   → comparison (("==" | "!=") comparison)*
 *   comparison → term (("<" | "<=" | ">" | ">=") term)*
 *   term       → factor (("+" | "-") factor)*
 *   factor     → unary (("*" | "/" | "%") unary)*
 *   unary      → ("!" | "-") unary | power
 *   power      → call ("^" unary)*
 *   call       → primary ("(" arguments? ")")*
 *   primary    → NUMBER | STRING | "true" | "false" | "null" | IDENTIFIER | "(" expression ")"
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import java.util.*;

/**
 * Recursive descent parser.
 */
public class Parser {
    
    private static final double PHI = 1.618033988749895;
    
    private final List<Token> tokens;
    private int current;
    private final List<String> errors;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
        this.errors = new ArrayList<>();
    }
    
    /**
     * Parse tokens into AST.
     */
    public ProgramNode parse() {
        List<ASTNode> statements = new ArrayList<>();
        
        while (!isAtEnd()) {
            try {
                statements.add(statement());
            } catch (ParseException e) {
                errors.add(e.getMessage());
                synchronize();
            }
        }
        
        return new ProgramNode(statements);
    }
    
    /**
     * Parse statement.
     */
    private ASTNode statement() throws ParseException {
        if (match(TokenType.VAR)) return varDeclaration();
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.LEFT_BRACE)) return block();
        if (match(TokenType.RETURN)) return returnStatement();
        return expressionStatement();
    }
    
    /**
     * Parse variable declaration.
     */
    private ASTNode varDeclaration() throws ParseException {
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name");
        
        ASTNode initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = expression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");
        return new VarDeclNode(name, initializer);
    }
    
    /**
     * Parse if statement.
     */
    private ASTNode ifStatement() throws ParseException {
        int line = previous().getLine();
        int column = previous().getColumn();
        
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'");
        ASTNode condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition");
        
        ASTNode thenBranch = statement();
        ASTNode elseBranch = null;
        
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        
        return new IfNode(condition, thenBranch, elseBranch, line, column);
    }
    
    /**
     * Parse while statement.
     */
    private ASTNode whileStatement() throws ParseException {
        int line = previous().getLine();
        int column = previous().getColumn();
        
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'");
        ASTNode condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition");
        
        ASTNode body = statement();
        
        return new WhileNode(condition, body, line, column);
    }
    
    /**
     * Parse block.
     */
    private ASTNode block() throws ParseException {
        int line = previous().getLine();
        int column = previous().getColumn();
        
        List<ASTNode> statements = new ArrayList<>();
        
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(statement());
        }
        
        consume(TokenType.RIGHT_BRACE, "Expected '}' after block");
        return new BlockNode(statements, line, column);
    }
    
    /**
     * Parse return statement.
     */
    private ASTNode returnStatement() throws ParseException {
        int line = previous().getLine();
        int column = previous().getColumn();
        
        ASTNode value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }
        
        consume(TokenType.SEMICOLON, "Expected ';' after return value");
        return new ReturnNode(value, line, column);
    }
    
    /**
     * Parse expression statement.
     */
    private ASTNode expressionStatement() throws ParseException {
        ASTNode expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression");
        return expr;
    }
    
    /**
     * Parse expression.
     */
    private ASTNode expression() throws ParseException {
        return assignment();
    }
    
    /**
     * Parse assignment.
     */
    private ASTNode assignment() throws ParseException {
        ASTNode expr = logicOr();
        
        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            ASTNode value = assignment();
            
            if (expr instanceof IdentifierNode) {
                Token name = new Token(TokenType.IDENTIFIER, ((IdentifierNode) expr).getName(), null, expr.getLine(), expr.getColumn());
                return new AssignmentNode(name, value);
            }
            
            throw new ParseException("Invalid assignment target", equals.getLine(), equals.getColumn());
        }
        
        return expr;
    }
    
    /**
     * Parse logical OR.
     */
    private ASTNode logicOr() throws ParseException {
        ASTNode expr = logicAnd();
        
        while (match(TokenType.OR)) {
            Token operator = previous();
            ASTNode right = logicAnd();
            expr = new BinaryExprNode(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Parse logical AND.
     */
    private ASTNode logicAnd() throws ParseException {
        ASTNode expr = equality();
        
        while (match(TokenType.AND)) {
            Token operator = previous();
            ASTNode right = equality();
            expr = new BinaryExprNode(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Parse equality.
     */
    private ASTNode equality() throws ParseException {
        ASTNode expr = comparison();
        
        while (match(TokenType.EQUAL, TokenType.NOT_EQUAL)) {
            Token operator = previous();
            ASTNode right = comparison();
            expr = new BinaryExprNode(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Parse comparison.
     */
    private ASTNode comparison() throws ParseException {
        ASTNode expr = term();
        
        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            ASTNode right = term();
            expr = new BinaryExprNode(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Parse term (addition/subtraction).
     */
    private ASTNode term() throws ParseException {
        ASTNode expr = factor();
        
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = factor();
            expr = new BinaryExprNode(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Parse factor (multiplication/division/modulo).
     */
    private ASTNode factor() throws ParseException {
        ASTNode expr = unary();
        
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token operator = previous();
            ASTNode right = unary();
            expr = new BinaryExprNode(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Parse unary expression.
     */
    private ASTNode unary() throws ParseException {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            ASTNode right = unary();
            return new UnaryExprNode(operator, right);
        }
        
        return power();
    }
    
    /**
     * Parse power (exponentiation).
     */
    private ASTNode power() throws ParseException {
        ASTNode expr = call();
        
        if (match(TokenType.POWER)) {
            Token operator = previous();
            ASTNode right = unary();
            expr = new BinaryExprNode(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Parse function call.
     */
    private ASTNode call() throws ParseException {
        ASTNode expr = primary();
        
        while (match(TokenType.LEFT_PAREN)) {
            expr = finishCall(expr);
        }
        
        return expr;
    }
    
    /**
     * Finish parsing function call.
     */
    private ASTNode finishCall(ASTNode callee) throws ParseException {
        List<ASTNode> arguments = new ArrayList<>();
        
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        
        Token paren = consume(TokenType.RIGHT_PAREN, "Expected ')' after arguments");
        
        if (callee instanceof IdentifierNode) {
            Token name = new Token(TokenType.IDENTIFIER, ((IdentifierNode) callee).getName(), null, callee.getLine(), callee.getColumn());
            return new CallNode(name, arguments);
        }
        
        throw new ParseException("Can only call functions", paren.getLine(), paren.getColumn());
    }
    
    /**
     * Parse primary expression.
     */
    private ASTNode primary() throws ParseException {
        if (match(TokenType.TRUE)) {
            return new LiteralNode(new Token(TokenType.NUMBER, "true", true, previous().getLine(), previous().getColumn()));
        }
        if (match(TokenType.FALSE)) {
            return new LiteralNode(new Token(TokenType.NUMBER, "false", false, previous().getLine(), previous().getColumn()));
        }
        if (match(TokenType.NULL)) {
            return new LiteralNode(new Token(TokenType.NUMBER, "null", null, previous().getLine(), previous().getColumn()));
        }
        
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new LiteralNode(previous());
        }
        
        if (match(TokenType.IDENTIFIER)) {
            return new IdentifierNode(previous());
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            ASTNode expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression");
            return expr;
        }
        
        throw new ParseException("Expected expression", peek().getLine(), peek().getColumn());
    }
    
    /**
     * Match token types.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check current token type.
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }
    
    /**
     * Advance to next token.
     */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    /**
     * Check if at end.
     */
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
    
    /**
     * Peek current token.
     */
    private Token peek() {
        return tokens.get(current);
    }
    
    /**
     * Get previous token.
     */
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    /**
     * Consume expected token.
     */
    private Token consume(TokenType type, String message) throws ParseException {
        if (check(type)) return advance();
        throw new ParseException(message, peek().getLine(), peek().getColumn());
    }
    
    /**
     * Synchronize after error.
     */
    private void synchronize() {
        advance();
        
        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;
            
            switch (peek().getType()) {
                case IF:
                case WHILE:
                case FOR:
                case RETURN:
                case VAR:
                    return;
            }
            
            advance();
        }
    }
    
    /**
     * Get parse errors.
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Check if parse had errors.
     */
    public boolean hadErrors() {
        return !errors.isEmpty();
    }
}

/**
 * Parse exception.
 */
class ParseException extends Exception {
    private final int line;
    private final int column;
    
    public ParseException(String message, int line, int column) {
        super(String.format("[Line %d:%d] Parse error: %s", line, column, message));
        this.line = line;
        this.column = column;
    }
    
    public int getLine() { return line; }
    public int getColumn() { return column; }
}
