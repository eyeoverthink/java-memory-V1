/**
 * ASTNode.java - Abstract Syntax Tree Node Base Class
 * 
 * Base class for all AST nodes with φ-harmonic properties.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import java.util.*;

/**
 * Base class for all AST nodes.
 */
public abstract class ASTNode {
    
    protected static final double PHI = 1.618033988749895;
    
    protected final int line;
    protected final int column;
    protected double phiResonance;
    
    public ASTNode(int line, int column) {
        this.line = line;
        this.column = column;
        this.phiResonance = 1.0;
    }
    
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public double getPhiResonance() { return phiResonance; }
    
    public abstract <R> R accept(ASTVisitor<R> visitor);
    public abstract String toDebugString(int indent);
    
    protected String indent(int level) {
        return "  ".repeat(level);
    }
}

/**
 * Program node (root of AST).
 */
class ProgramNode extends ASTNode {
    private final List<ASTNode> statements;
    
    public ProgramNode(List<ASTNode> statements) {
        super(1, 1);
        this.statements = statements;
        this.phiResonance = statements.size() * PHI;
    }
    
    public List<ASTNode> getStatements() { return statements; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitProgram(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("PROGRAM (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        for (ASTNode stmt : statements) {
            sb.append(stmt.toDebugString(indent + 1));
        }
        return sb.toString();
    }
}

/**
 * Binary expression node (e.g., x + y).
 */
class BinaryExprNode extends ASTNode {
    private final ASTNode left;
    private final Token operator;
    private final ASTNode right;
    
    public BinaryExprNode(ASTNode left, Token operator, ASTNode right) {
        super(operator.getLine(), operator.getColumn());
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.phiResonance = (left.getPhiResonance() + right.getPhiResonance()) * operator.getPhiWeight();
    }
    
    public ASTNode getLeft() { return left; }
    public Token getOperator() { return operator; }
    public ASTNode getRight() { return right; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBinaryExpr(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("BINARY (").append(operator.getLexeme()).append(", φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        sb.append(left.toDebugString(indent + 1));
        sb.append(right.toDebugString(indent + 1));
        return sb.toString();
    }
}

/**
 * Unary expression node (e.g., -x, !flag).
 */
class UnaryExprNode extends ASTNode {
    private final Token operator;
    private final ASTNode operand;
    
    public UnaryExprNode(Token operator, ASTNode operand) {
        super(operator.getLine(), operator.getColumn());
        this.operator = operator;
        this.operand = operand;
        this.phiResonance = operand.getPhiResonance() * operator.getPhiWeight();
    }
    
    public Token getOperator() { return operator; }
    public ASTNode getOperand() { return operand; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitUnaryExpr(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("UNARY (").append(operator.getLexeme()).append(", φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        sb.append(operand.toDebugString(indent + 1));
        return sb.toString();
    }
}

/**
 * Literal node (number, string, boolean, null).
 */
class LiteralNode extends ASTNode {
    private final Object value;
    
    public LiteralNode(Token token) {
        super(token.getLine(), token.getColumn());
        this.value = token.getLiteral();
        this.phiResonance = token.getPhiWeight();
    }
    
    public Object getValue() { return value; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteral(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        return indent(indent) + "LITERAL (" + value + ", φ=" + String.format("%.4f", phiResonance) + ")\n";
    }
}

/**
 * Identifier node (variable reference).
 */
class IdentifierNode extends ASTNode {
    private final String name;
    
    public IdentifierNode(Token token) {
        super(token.getLine(), token.getColumn());
        this.name = token.getLexeme();
        this.phiResonance = token.getPhiWeight();
    }
    
    public String getName() { return name; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitIdentifier(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        return indent(indent) + "IDENTIFIER (" + name + ", φ=" + String.format("%.4f", phiResonance) + ")\n";
    }
}

/**
 * Assignment node (x = value).
 */
class AssignmentNode extends ASTNode {
    private final String name;
    private final ASTNode value;
    
    public AssignmentNode(Token identifier, ASTNode value) {
        super(identifier.getLine(), identifier.getColumn());
        this.name = identifier.getLexeme();
        this.value = value;
        this.phiResonance = value.getPhiResonance() * PHI;
    }
    
    public String getName() { return name; }
    public ASTNode getValue() { return value; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitAssignment(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("ASSIGNMENT (").append(name).append(", φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        sb.append(value.toDebugString(indent + 1));
        return sb.toString();
    }
}

/**
 * Variable declaration node (var x = value).
 */
class VarDeclNode extends ASTNode {
    private final String name;
    private final ASTNode initializer;
    
    public VarDeclNode(Token identifier, ASTNode initializer) {
        super(identifier.getLine(), identifier.getColumn());
        this.name = identifier.getLexeme();
        this.initializer = initializer;
        this.phiResonance = (initializer != null ? initializer.getPhiResonance() : 1.0) * PHI;
    }
    
    public String getName() { return name; }
    public ASTNode getInitializer() { return initializer; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitVarDecl(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("VAR_DECL (").append(name).append(", φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        if (initializer != null) {
            sb.append(initializer.toDebugString(indent + 1));
        }
        return sb.toString();
    }
}

/**
 * Block node (sequence of statements).
 */
class BlockNode extends ASTNode {
    private final List<ASTNode> statements;
    
    public BlockNode(List<ASTNode> statements, int line, int column) {
        super(line, column);
        this.statements = statements;
        this.phiResonance = statements.size() * PHI;
    }
    
    public List<ASTNode> getStatements() { return statements; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBlock(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("BLOCK (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        for (ASTNode stmt : statements) {
            sb.append(stmt.toDebugString(indent + 1));
        }
        return sb.toString();
    }
}

/**
 * If statement node.
 */
class IfNode extends ASTNode {
    private final ASTNode condition;
    private final ASTNode thenBranch;
    private final ASTNode elseBranch;
    
    public IfNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.phiResonance = condition.getPhiResonance() * PHI;
    }
    
    public ASTNode getCondition() { return condition; }
    public ASTNode getThenBranch() { return thenBranch; }
    public ASTNode getElseBranch() { return elseBranch; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitIf(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("IF (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        sb.append(indent(indent + 1)).append("CONDITION:\n");
        sb.append(condition.toDebugString(indent + 2));
        sb.append(indent(indent + 1)).append("THEN:\n");
        sb.append(thenBranch.toDebugString(indent + 2));
        if (elseBranch != null) {
            sb.append(indent(indent + 1)).append("ELSE:\n");
            sb.append(elseBranch.toDebugString(indent + 2));
        }
        return sb.toString();
    }
}

/**
 * While loop node.
 */
class WhileNode extends ASTNode {
    private final ASTNode condition;
    private final ASTNode body;
    
    public WhileNode(ASTNode condition, ASTNode body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = body;
        this.phiResonance = condition.getPhiResonance() * PHI;
    }
    
    public ASTNode getCondition() { return condition; }
    public ASTNode getBody() { return body; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitWhile(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("WHILE (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        sb.append(indent(indent + 1)).append("CONDITION:\n");
        sb.append(condition.toDebugString(indent + 2));
        sb.append(indent(indent + 1)).append("BODY:\n");
        sb.append(body.toDebugString(indent + 2));
        return sb.toString();
    }
}

/**
 * Function call node.
 */
class CallNode extends ASTNode {
    private final String name;
    private final List<ASTNode> arguments;
    
    public CallNode(Token identifier, List<ASTNode> arguments) {
        super(identifier.getLine(), identifier.getColumn());
        this.name = identifier.getLexeme();
        this.arguments = arguments;
        this.phiResonance = arguments.size() * PHI;
    }
    
    public String getName() { return name; }
    public List<ASTNode> getArguments() { return arguments; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitCall(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("CALL (").append(name).append(", φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        for (ASTNode arg : arguments) {
            sb.append(arg.toDebugString(indent + 1));
        }
        return sb.toString();
    }
}

/**
 * Return statement node.
 */
class ReturnNode extends ASTNode {
    private final ASTNode value;
    
    public ReturnNode(ASTNode value, int line, int column) {
        super(line, column);
        this.value = value;
        this.phiResonance = (value != null ? value.getPhiResonance() : 1.0) * PHI;
    }
    
    public ASTNode getValue() { return value; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitReturn(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("RETURN (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        if (value != null) {
            sb.append(value.toDebugString(indent + 1));
        }
        return sb.toString();
    }
}
