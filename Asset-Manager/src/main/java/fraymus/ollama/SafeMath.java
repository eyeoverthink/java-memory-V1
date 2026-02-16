package fraymus.ollama;

import java.util.*;

/**
 * SAFE MATH - Deterministic Math Engine
 * 
 * Supports:
 * - Operators: + - * / ^
 * - Functions: sqrt, sin, cos, tan, abs, ln, log10
 * - Parentheses
 * 
 * Uses Shunting Yard algorithm for parsing
 */
public class SafeMath {

    public static String evalToString(String expr) {
        double v = eval(expr);
        if (Math.abs(v) < 1e12) return String.valueOf(v);
        return Double.toString(v);
    }

    public static double eval(String expr) {
        List<String> tokens = tokenize(expr);
        List<String> rpn = shuntingYard(tokens);
        return evalRpn(rpn);
    }

    private static List<String> tokenize(String s) {
        s = s.replaceAll("\\s+", "");
        List<String> out = new ArrayList<>();
        StringBuilder num = new StringBuilder();
        StringBuilder word = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                if (word.length() > 0) { out.add(word.toString()); word.setLength(0); }
                num.append(c);
            } else {
                if (num.length() > 0) { out.add(num.toString()); num.setLength(0); }

                if (Character.isLetter(c)) {
                    word.append(c);
                } else {
                    if (word.length() > 0) { out.add(word.toString()); word.setLength(0); }
                    out.add(String.valueOf(c));
                }
            }
        }
        if (num.length() > 0) out.add(num.toString());
        if (word.length() > 0) out.add(word.toString());
        return out;
    }

    private static int prec(String op) {
        return switch (op) {
            case "^" -> 4;
            case "*", "/" -> 3;
            case "+", "-" -> 2;
            default -> 0;
        };
    }

    private static boolean rightAssoc(String op) {
        return op.equals("^");
    }

    private static boolean isFunc(String t) {
        return switch (t) {
            case "sqrt","sin","cos","tan","abs","ln","log10" -> true;
            default -> false;
        };
    }

    private static List<String> shuntingYard(List<String> tokens) {
        List<String> out = new ArrayList<>();
        Deque<String> st = new ArrayDeque<>();
        String prev = null;

        for (String t : tokens) {
            if (isNumber(t)) {
                out.add(t);
            } else if (isFunc(t)) {
                st.push(t);
            } else if (t.equals(",")) {
                while (!st.isEmpty() && !st.peek().equals("(")) out.add(st.pop());
            } else if (isOp(t)) {
                if (t.equals("-") && (prev == null || (isOp(prev) || prev.equals("(")))) {
                    out.add("0");
                }
                while (!st.isEmpty() && (isOp(st.peek()) || isFunc(st.peek()))) {
                    String top = st.peek();
                    if (isFunc(top)) { out.add(st.pop()); continue; }
                    if ((rightAssoc(t) && prec(t) < prec(top)) || (!rightAssoc(t) && prec(t) <= prec(top))) {
                        out.add(st.pop());
                    } else break;
                }
                st.push(t);
            } else if (t.equals("(")) {
                st.push(t);
            } else if (t.equals(")")) {
                while (!st.isEmpty() && !st.peek().equals("(")) out.add(st.pop());
                if (st.isEmpty()) throw new IllegalArgumentException("Mismatched parentheses");
                st.pop();
                if (!st.isEmpty() && isFunc(st.peek())) out.add(st.pop());
            } else {
                throw new IllegalArgumentException("Unknown token: " + t);
            }
            prev = t;
        }

        while (!st.isEmpty()) {
            String t = st.pop();
            if (t.equals("(") || t.equals(")")) throw new IllegalArgumentException("Mismatched parentheses");
            out.add(t);
        }
        return out;
    }

    private static double evalRpn(List<String> rpn) {
        Deque<Double> st = new ArrayDeque<>();
        for (String t : rpn) {
            if (isNumber(t)) st.push(Double.parseDouble(t));
            else if (isOp(t)) {
                double b = st.pop(), a = st.pop();
                st.push(applyOp(t, a, b));
            } else if (isFunc(t)) {
                double a = st.pop();
                st.push(applyFunc(t, a));
            } else throw new IllegalArgumentException("Bad RPN token: " + t);
        }
        if (st.size() != 1) throw new IllegalArgumentException("Bad expression");
        return st.pop();
    }

    private static boolean isNumber(String t) {
        try { Double.parseDouble(t); return true; } catch (Exception e) { return false; }
    }

    private static boolean isOp(String t) {
        return t.equals("+") || t.equals("-") || t.equals("*") || t.equals("/") || t.equals("^");
    }

    private static double applyOp(String op, double a, double b) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("Bad op: " + op);
        };
    }

    private static double applyFunc(String fn, double a) {
        return switch (fn) {
            case "sqrt" -> Math.sqrt(a);
            case "sin" -> Math.sin(a);
            case "cos" -> Math.cos(a);
            case "tan" -> Math.tan(a);
            case "abs" -> Math.abs(a);
            case "ln" -> Math.log(a);
            case "log10" -> Math.log10(a);
            default -> throw new IllegalArgumentException("Bad fn: " + fn);
        };
    }
}
