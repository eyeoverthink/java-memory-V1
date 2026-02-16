/**
 * ASTVisitor.java - Visitor Pattern for AST Traversal
 * 
 * Visitor interface for traversing and processing AST nodes.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

/**
 * Visitor interface for AST nodes.
 */
public interface ASTVisitor<R> {
    R visitProgram(ProgramNode node);
    R visitBinaryExpr(BinaryExprNode node);
    R visitUnaryExpr(UnaryExprNode node);
    R visitLiteral(LiteralNode node);
    R visitIdentifier(IdentifierNode node);
    R visitAssignment(AssignmentNode node);
    R visitVarDecl(VarDeclNode node);
    R visitBlock(BlockNode node);
    R visitIf(IfNode node);
    R visitWhile(WhileNode node);
    R visitCall(CallNode node);
    R visitReturn(ReturnNode node);
    R visitPrint(PrintNode node);
    R visitFor(ForNode node);
    R visitFunction(FunctionNode node);
    R visitArrayLiteral(ArrayLiteralNode node);
    R visitArrayAccess(ArrayAccessNode node);
    R visitArrayAssignment(ArrayAssignmentNode node);
}
