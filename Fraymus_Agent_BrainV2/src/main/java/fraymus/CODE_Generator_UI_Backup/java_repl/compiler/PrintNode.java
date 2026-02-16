/**
 * PrintNode.java - Print Statement AST Node
 * 
 * Represents a print statement in the AST.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

/**
 * Print statement node.
 */
class PrintNode extends ASTNode {
    private final ASTNode expression;
    
    public PrintNode(ASTNode expression, int line, int column) {
        super(line, column);
        this.expression = expression;
        this.phiResonance = (expression != null ? expression.getPhiResonance() : 1.0) * PHI;
    }
    
    public ASTNode getExpression() { return expression; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitPrint(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("PRINT (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        if (expression != null) {
            sb.append(expression.toDebugString(indent + 1));
        }
        return sb.toString();
    }
}
