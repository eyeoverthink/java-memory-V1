package io.fraymus.ai.tools;

/**
 * TOOL RESULT
 * Result from tool execution
 */
public class ToolResult {
    public final String tool;
    public final String output;

    public ToolResult(String tool, String output) {
        this.tool = tool;
        this.output = output;
    }

    @Override
    public String toString() {
        return "ToolResult{tool='" + tool + "', output='" + output + "'}";
    }
}
