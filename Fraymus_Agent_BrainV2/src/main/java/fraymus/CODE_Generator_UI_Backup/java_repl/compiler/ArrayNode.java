/**
 * ArrayNode.java - Array Literal and Access AST Nodes
 * 
 * Represents array literals and array access expressions.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import java.util.*;

/**
 * Array literal node: [1, 2, 3]
 */
class ArrayLiteralNode extends ASTNode {
    private final List<ASTNode> elements;
    
    public ArrayLiteralNode(List<ASTNode> elements, int line, int column) {
        super(line, column);
        this.elements = elements;
        this.phiResonance = elements.size() * PHI;
    }
    
    public List<ASTNode> getElements() { return new ArrayList<>(elements); }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitArrayLiteral(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("ARRAY [").append(elements.size()).append(" elements] (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        for (ASTNode elem : elements) {
            sb.append(elem.toDebugString(indent + 1));
        }
        return sb.toString();
    }
}

/**
 * Array access node: arr[index]
 */
class ArrayAccessNode extends ASTNode {
    private final ASTNode array;
    private final ASTNode index;
    
    public ArrayAccessNode(ASTNode array, ASTNode index, int line, int column) {
        super(line, column);
        this.array = array;
        this.index = index;
        this.phiResonance = (array.getPhiResonance() + index.getPhiResonance()) * PHI;
    }
    
    public ASTNode getArray() { return array; }
    public ASTNode getIndex() { return index; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitArrayAccess(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("ARRAY_ACCESS (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        sb.append(indent(indent + 1)).append("ARRAY:\n");
        sb.append(array.toDebugString(indent + 2));
        sb.append(indent(indent + 1)).append("INDEX:\n");
        sb.append(index.toDebugString(indent + 2));
        return sb.toString();
    }
}

/**
 * Array assignment node: arr[index] = value
 */
class ArrayAssignmentNode extends ASTNode {
    private final ASTNode array;
    private final ASTNode index;
    private final ASTNode value;
    
    public ArrayAssignmentNode(ASTNode array, ASTNode index, ASTNode value, int line, int column) {
        super(line, column);
        this.array = array;
        this.index = index;
        this.value = value;
        this.phiResonance = (array.getPhiResonance() + index.getPhiResonance() + value.getPhiResonance()) * PHI;
    }
    
    public ASTNode getArray() { return array; }
    public ASTNode getIndex() { return index; }
    public ASTNode getValue() { return value; }
    
    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitArrayAssignment(this);
    }
    
    @Override
    public String toDebugString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(indent)).append("ARRAY_ASSIGNMENT (φ=").append(String.format("%.4f", phiResonance)).append(")\n");
        sb.append(indent(indent + 1)).append("ARRAY:\n");
        sb.append(array.toDebugString(indent + 2));
        sb.append(indent(indent + 1)).append("INDEX:\n");
        sb.append(index.toDebugString(indent + 2));
        sb.append(indent(indent + 1)).append("VALUE:\n");
        sb.append(value.toDebugString(indent + 2));
        return sb.toString();
    }
}
