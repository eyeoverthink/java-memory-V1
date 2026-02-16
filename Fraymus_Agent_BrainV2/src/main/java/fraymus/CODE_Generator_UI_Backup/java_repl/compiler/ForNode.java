/**
 * ForNode.java - For Loop AST Node
 * 
 * Represents a for loop in the AST.
 * Supports: for (init; condition; update) { body }
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

/**
 * For loop node.
 */
class ForNode extends ASTNode {
    private final ASTNode initializer;
    private final ASTNode condition;
    private final ASTNode update;
    private final ASTNode body;
    
    public ForNode(ASTNode initializer, ASTNode condition, ASTNode update, ASTNode body, int line, int column) {
        super(line, column);
        this.initializer = initializer;
        this.condition = condition;
        this.update = update;
        this.body = body;
        this.phiResonance = (condition != null ? condition.getPhiResonance() : 1.0) * PHI;
    }
    
    public ASTNode getInitializer() { return initializer; }
    public ASTNode getCondition() { return condition; }
    public ASTNode getUpdate() { return update; }
    public ASTNode getBody() { return body; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFor(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("FOR (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        
        if (initializer != null) {
            sb.append(indent(indent + 1)).append("INIT:\n");
            sb.append(initializer.toDebugString(indent + 2));
        }
        
        if (condition != null) {
            sb.append(indent(indent + 1)).append("CONDITION:\n");
            sb.append(condition.toDebugString(indent + 2));
        }
        
        if (update != null) {
            sb.append(indent(indent + 1)).append("UPDATE:\n");
            sb.append(update.toDebugString(indent + 2));
        }
        
        sb.append(indent(indent + 1)).append("BODY:\n");
        sb.append(body.toDebugString(indent + 2));
        
        return sb.toString();
    }
}
