/**
 * FunctionNode.java - Function Declaration AST Node
 * 
 * Represents a function declaration in the AST.
 * Supports: function name(param1, param2) { body }
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import java.util.*;

/**
 * Function declaration node.
 */
class FunctionNode extends ASTNode {
    private final String name;
    private final List<String> parameters;
    private final ASTNode body;
    
    public FunctionNode(String name, List<String> parameters, ASTNode body, int line, int column) {
        super(line, column);
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.phiResonance = (parameters.size() + 1) * PHI;
    }
    
    public String getName() { return name; }
    public List<String> getParameters() { return new ArrayList<>(parameters); }
    public ASTNode getBody() { return body; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitFunction(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("FUNCTION ").append(name).append("(");
        sb.append(String.join(", ", parameters));
        sb.append(") (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        sb.append(body.toDebugString(indent + 1));
        return sb.toString();
    }
}
