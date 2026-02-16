/**
 * Interpreter.java - φ-Harmonic AST Interpreter
 * 
 * Executes AST directly using visitor pattern.
 * Integrates with SymbolTable for variable storage.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import java.util.*;

/**
 * AST interpreter with φ-harmonic execution.
 */
public class Interpreter implements ASTVisitor<Object> {
    
    private static final double PHI = 1.618033988749895;
    
    private final SymbolTable symbolTable;
    private final List<String> output;
    private boolean debugMode;
    
    /**
     * Return value exception for control flow.
     */
    private static class ReturnException extends RuntimeException {
        final Object value;
        ReturnException(Object value) {
            super(null, null, false, false);
            this.value = value;
        }
    }
    
    public Interpreter() {
        this.symbolTable = new SymbolTable();
        this.output = new ArrayList<>();
        this.debugMode = false;
    }
    
    /**
     * Execute program.
     */
    public void execute(ProgramNode program) {
        try {
            program.accept(this);
        } catch (RuntimeException e) {
            output.add("Runtime error: " + e.getMessage());
        }
    }
    
    /**
     * Set debug mode.
     */
    public void setDebugMode(boolean debug) {
        this.debugMode = debug;
    }
    
    /**
     * Get output.
     */
    public List<String> getOutput() {
        return new ArrayList<>(output);
    }
    
    /**
     * Clear output.
     */
    public void clearOutput() {
        output.clear();
    }
    
    /**
     * Get symbol table.
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
    
    @Override
    public Object visitProgram(ProgramNode node) {
        Object lastValue = null;
        for (ASTNode stmt : node.getStatements()) {
            lastValue = stmt.accept(this);
        }
        return lastValue;
    }
    
    @Override
    public Object visitBinaryExpr(BinaryExprNode node) {
        Object left = node.getLeft().accept(this);
        Object right = node.getRight().accept(this);
        
        if (debugMode) {
            output.add(String.format("[DEBUG] Binary: %s %s %s", left, node.getOperator().getLexeme(), right));
        }
        
        switch (node.getOperator().getType()) {
            case PLUS:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).doubleValue() + ((Number) right).doubleValue();
                }
                if (left instanceof String || right instanceof String) {
                    return String.valueOf(left) + String.valueOf(right);
                }
                throw new RuntimeException("Invalid operands for +");
                
            case MINUS:
                checkNumberOperands(node.getOperator(), left, right);
                return ((Number) left).doubleValue() - ((Number) right).doubleValue();
                
            case STAR:
                checkNumberOperands(node.getOperator(), left, right);
                return ((Number) left).doubleValue() * ((Number) right).doubleValue();
                
            case SLASH:
                checkNumberOperands(node.getOperator(), left, right);
                double divisor = ((Number) right).doubleValue();
                if (divisor == 0) throw new RuntimeException("Division by zero");
                return ((Number) left).doubleValue() / divisor;
                
            case PERCENT:
                checkNumberOperands(node.getOperator(), left, right);
                return ((Number) left).doubleValue() % ((Number) right).doubleValue();
                
            case POWER:
                checkNumberOperands(node.getOperator(), left, right);
                return Math.pow(((Number) left).doubleValue(), ((Number) right).doubleValue());
                
            case EQUAL:
                return isEqual(left, right);
                
            case NOT_EQUAL:
                return !isEqual(left, right);
                
            case LESS:
                checkNumberOperands(node.getOperator(), left, right);
                return ((Number) left).doubleValue() < ((Number) right).doubleValue();
                
            case LESS_EQUAL:
                checkNumberOperands(node.getOperator(), left, right);
                return ((Number) left).doubleValue() <= ((Number) right).doubleValue();
                
            case GREATER:
                checkNumberOperands(node.getOperator(), left, right);
                return ((Number) left).doubleValue() > ((Number) right).doubleValue();
                
            case GREATER_EQUAL:
                checkNumberOperands(node.getOperator(), left, right);
                return ((Number) left).doubleValue() >= ((Number) right).doubleValue();
                
            case AND:
                return isTruthy(left) && isTruthy(right);
                
            case OR:
                return isTruthy(left) || isTruthy(right);
                
            default:
                throw new RuntimeException("Unknown binary operator: " + node.getOperator().getLexeme());
        }
    }
    
    @Override
    public Object visitUnaryExpr(UnaryExprNode node) {
        Object operand = node.getOperand().accept(this);
        
        switch (node.getOperator().getType()) {
            case MINUS:
                checkNumberOperand(node.getOperator(), operand);
                return -((Number) operand).doubleValue();
                
            case NOT:
                return !isTruthy(operand);
                
            default:
                throw new RuntimeException("Unknown unary operator: " + node.getOperator().getLexeme());
        }
    }
    
    @Override
    public Object visitLiteral(LiteralNode node) {
        return node.getValue();
    }
    
    @Override
    public Object visitIdentifier(IdentifierNode node) {
        SymbolTable.Symbol symbol = symbolTable.lookup(node.getName());
        if (symbol == null) {
            throw new RuntimeException("Undefined variable: " + node.getName());
        }
        return symbol.getValue();
    }
    
    @Override
    public Object visitAssignment(AssignmentNode node) {
        Object value = node.getValue().accept(this);
        
        if (!symbolTable.assign(node.getName(), value)) {
            throw new RuntimeException("Undefined variable: " + node.getName());
        }
        
        if (debugMode) {
            output.add(String.format("[DEBUG] Assign: %s = %s", node.getName(), value));
        }
        
        return value;
    }
    
    @Override
    public Object visitVarDecl(VarDeclNode node) {
        Object value = null;
        if (node.getInitializer() != null) {
            value = node.getInitializer().accept(this);
        }
        
        SymbolTable.SymbolType type = SymbolTable.inferType(value);
        symbolTable.define(node.getName(), type, value);
        
        if (debugMode) {
            output.add(String.format("[DEBUG] Declare: %s = %s (type: %s)", node.getName(), value, type));
        }
        
        return value;
    }
    
    @Override
    public Object visitBlock(BlockNode node) {
        symbolTable.enterScope();
        try {
            Object lastValue = null;
            for (ASTNode stmt : node.getStatements()) {
                lastValue = stmt.accept(this);
            }
            return lastValue;
        } finally {
            symbolTable.exitScope();
        }
    }
    
    @Override
    public Object visitIf(IfNode node) {
        Object condition = node.getCondition().accept(this);
        
        if (isTruthy(condition)) {
            return node.getThenBranch().accept(this);
        } else if (node.getElseBranch() != null) {
            return node.getElseBranch().accept(this);
        }
        
        return null;
    }
    
    @Override
    public Object visitWhile(WhileNode node) {
        Object lastValue = null;
        while (isTruthy(node.getCondition().accept(this))) {
            lastValue = node.getBody().accept(this);
        }
        return lastValue;
    }
    
    @Override
    public Object visitCall(CallNode node) {
        // Built-in functions
        switch (node.getName()) {
            case "print":
                for (ASTNode arg : node.getArguments()) {
                    Object value = arg.accept(this);
                    output.add(String.valueOf(value));
                }
                return null;
                
            case "phi":
                return PHI;
                
            case "sqrt":
                if (node.getArguments().size() != 1) {
                    throw new RuntimeException("sqrt() expects 1 argument");
                }
                Object arg = node.getArguments().get(0).accept(this);
                checkNumberOperand(null, arg);
                return Math.sqrt(((Number) arg).doubleValue());
                
            case "abs":
                if (node.getArguments().size() != 1) {
                    throw new RuntimeException("abs() expects 1 argument");
                }
                arg = node.getArguments().get(0).accept(this);
                checkNumberOperand(null, arg);
                return Math.abs(((Number) arg).doubleValue());
                
            case "sin":
                if (node.getArguments().size() != 1) {
                    throw new RuntimeException("sin() expects 1 argument");
                }
                arg = node.getArguments().get(0).accept(this);
                checkNumberOperand(null, arg);
                return Math.sin(((Number) arg).doubleValue());
                
            case "cos":
                if (node.getArguments().size() != 1) {
                    throw new RuntimeException("cos() expects 1 argument");
                }
                arg = node.getArguments().get(0).accept(this);
                checkNumberOperand(null, arg);
                return Math.cos(((Number) arg).doubleValue());
                
            default:
                throw new RuntimeException("Undefined function: " + node.getName());
        }
    }
    
    @Override
    public Object visitReturn(ReturnNode node) {
        Object value = null;
        if (node.getValue() != null) {
            value = node.getValue().accept(this);
        }
        throw new ReturnException(value);
    }
    
    @Override
    public Object visitPrint(PrintNode node) {
        if (node.getExpression() != null) {
            Object value = node.getExpression().accept(this);
            output.add(String.valueOf(value));
            return value;
        }
        return null;
    }
    
    @Override
    public Object visitFor(ForNode node) {
        // Enter new scope for loop
        symbolTable.enterScope();
        try {
            // Execute initializer
            if (node.getInitializer() != null) {
                node.getInitializer().accept(this);
            }
            
            // Loop while condition is true
            Object lastValue = null;
            while (node.getCondition() == null || isTruthy(node.getCondition().accept(this))) {
                // Execute body
                lastValue = node.getBody().accept(this);
                
                // Execute update
                if (node.getUpdate() != null) {
                    node.getUpdate().accept(this);
                }
            }
            
            return lastValue;
        } finally {
            symbolTable.exitScope();
        }
    }
    
    @Override
    public Object visitFunction(FunctionNode node) {
        // Store function in symbol table
        symbolTable.define(node.getName(), SymbolTable.SymbolType.FUNCTION, node);
        
        if (debugMode) {
            output.add(String.format("[DEBUG] Define function: %s(%s)", 
                node.getName(), String.join(", ", node.getParameters())));
        }
        
        return null;
    }
    
    @Override
    public Object visitArrayLiteral(ArrayLiteralNode node) {
        List<Object> array = new ArrayList<>();
        for (ASTNode elem : node.getElements()) {
            array.add(elem.accept(this));
        }
        return array;
    }
    
    @Override
    public Object visitArrayAccess(ArrayAccessNode node) {
        Object arrayObj = node.getArray().accept(this);
        Object indexObj = node.getIndex().accept(this);
        
        if (!(arrayObj instanceof List)) {
            throw new RuntimeException("Cannot index non-array value");
        }
        if (!(indexObj instanceof Number)) {
            throw new RuntimeException("Array index must be a number");
        }
        
        @SuppressWarnings("unchecked")
        List<Object> array = (List<Object>) arrayObj;
        int index = ((Number) indexObj).intValue();
        
        if (index < 0 || index >= array.size()) {
            throw new RuntimeException("Array index out of bounds: " + index);
        }
        
        return array.get(index);
    }
    
    @Override
    public Object visitArrayAssignment(ArrayAssignmentNode node) {
        Object arrayObj = node.getArray().accept(this);
        Object indexObj = node.getIndex().accept(this);
        Object value = node.getValue().accept(this);
        
        if (!(arrayObj instanceof List)) {
            throw new RuntimeException("Cannot index non-array value");
        }
        if (!(indexObj instanceof Number)) {
            throw new RuntimeException("Array index must be a number");
        }
        
        @SuppressWarnings("unchecked")
        List<Object> array = (List<Object>) arrayObj;
        int index = ((Number) indexObj).intValue();
        
        if (index < 0 || index >= array.size()) {
            throw new RuntimeException("Array index out of bounds: " + index);
        }
        
        array.set(index, value);
        
        if (debugMode) {
            output.add(String.format("[DEBUG] Array assign: [%d] = %s", index, value));
        }
        
        return value;
    }
    
    /**
     * Check if value is truthy.
     */
    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        return true;
    }
    
    /**
     * Check equality.
     */
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }
    
    /**
     * Check number operand.
     */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Number) return;
        throw new RuntimeException("Operand must be a number");
    }
    
    /**
     * Check number operands.
     */
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Number && right instanceof Number) return;
        throw new RuntimeException("Operands must be numbers");
    }
}
