package fraymus.transformation;

import java.util.*;

/**
 * 🔬 Constraint Evaluator - Expression Parser
 * 
 * Parses and evaluates constraint expressions against state.
 * Supports arithmetic, logical, and comparison operations.
 */
public class ConstraintEvaluator {
    
    /**
     * Evaluate constraint expression against state
     */
    public boolean evaluate(String expression, UnifiedStateTransformationEngine.State state) {
        try {
            // Simple expression evaluation
            // This is a basic implementation - could be extended with a full parser
            
            // Handle simple comparisons
            if (expression.contains("<=")) {
                return evaluateComparison(expression, "<=", state);
            } else if (expression.contains(">=")) {
                return evaluateComparison(expression, ">=", state);
            } else if (expression.contains("<")) {
                return evaluateComparison(expression, "<", state);
            } else if (expression.contains(">")) {
                return evaluateComparison(expression, ">", state);
            } else if (expression.contains("==")) {
                return evaluateComparison(expression, "==", state);
            } else if (expression.contains("!=")) {
                return evaluateComparison(expression, "!=", state);
            } else if (expression.contains("&&")) {
                return evaluateLogicalAnd(expression, state);
            } else if (expression.contains("||")) {
                return evaluateLogicalOr(expression, state);
            }
            
            // Default to true for simple expressions
            return true;
        } catch (Exception e) {
            // On evaluation error, be conservative
            return false;
        }
    }
    
    /**
     * Evaluate comparison expression
     */
    private boolean evaluateComparison(String expression, String operator, 
                                       UnifiedStateTransformationEngine.State state) {
        String[] parts = expression.split(operator);
        if (parts.length != 2) return true;
        
        double left = evaluateExpression(parts[0].trim(), state);
        double right = evaluateExpression(parts[1].trim(), state);
        
        switch (operator) {
            case "<=": return left <= right;
            case ">=": return left >= right;
            case "<": return left < right;
            case ">": return left > right;
            case "==": return left == right;
            case "!=": return left != right;
            default: return true;
        }
    }
    
    /**
     * Evaluate logical AND
     */
    private boolean evaluateLogicalAnd(String expression, 
                                      UnifiedStateTransformationEngine.State state) {
        String[] parts = expression.split("&&");
        for (String part : parts) {
            if (!evaluate(part.trim(), state)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Evaluate logical OR
     */
    private boolean evaluateLogicalOr(String expression, 
                                     UnifiedStateTransformationEngine.State state) {
        String[] parts = expression.split("\\|\\|");
        for (String part : parts) {
            if (evaluate(part.trim(), state)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Evaluate arithmetic expression
     */
    private double evaluateExpression(String expr, 
                                     UnifiedStateTransformationEngine.State state) {
        try {
            // Try to parse as number
            return Double.parseDouble(expr);
        } catch (NumberFormatException e) {
            // Handle variables
            if (expr.equals("MAX")) return Double.MAX_VALUE;
            if (expr.equals("stack_depth")) return state.getStackDepth();
            if (expr.startsWith("memory_")) {
                // Extract memory address
                String addrStr = expr.substring(7);
                try {
                    int addr = Integer.parseInt(addrStr);
                    return state.getMemoryValue(addr);
                } catch (NumberFormatException ex) {
                    return 0;
                }
            }
            
            // Default to 0 for unknown variables
            return 0;
        }
    }
}
