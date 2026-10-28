package fraymus.model;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PromptCompiler - Per-family prompt dialect, tool syntax, context packing.
 */
public class PromptCompiler {
    
    private final ModelRegistry registry;
    private final Map<String, PromptDialect> dialects;
    private final Map<String, ToolFormatter> toolFormatters;
    
    public PromptCompiler(ModelRegistry registry) {
        this.registry = registry;
        this.dialects = new HashMap<>();
        this.toolFormatters = new HashMap<>();
        initializeDefaultDialects();
    }
    
    public static class PromptDialect {
        private final String family;
        private final String systemPromptPrefix;
        private final String userPromptPrefix;
        private final String assistantPromptPrefix;
        private final String systemPromptSuffix;
        private final String userPromptSuffix;
        private final String assistantPromptSuffix;
        private final String chatSeparator;
        private final List<String> stopTokens;
        private final boolean supportsThinkingMode;
        private final String thinkingPrefix;
        private final String thinkingSuffix;
        private final boolean supportsToolCalling;
        private final String toolCallPrefix;
        private final String toolCallSuffix;
        
        public PromptDialect(String family, String systemPromptPrefix, String userPromptPrefix,
                          String assistantPromptPrefix, String systemPromptSuffix,
                          String userPromptSuffix, String assistantPromptSuffix,
                          String chatSeparator, List<String> stopTokens,
                          boolean supportsThinkingMode, String thinkingPrefix, String thinkingSuffix,
                          boolean supportsToolCalling, String toolCallPrefix, String toolCallSuffix) {
            this.family = family;
            this.systemPromptPrefix = systemPromptPrefix;
            this.userPromptPrefix = userPromptPrefix;
            this.assistantPromptPrefix = assistantPromptPrefix;
            this.systemPromptSuffix = systemPromptSuffix;
            this.userPromptSuffix = userPromptSuffix;
            this.assistantPromptSuffix = assistantPromptSuffix;
            this.chatSeparator = chatSeparator;
            this.stopTokens = stopTokens != null ? new ArrayList<>(stopTokens) : new ArrayList<>();
            this.supportsThinkingMode = supportsThinkingMode;
            this.thinkingPrefix = thinkingPrefix;
            this.thinkingSuffix = thinkingSuffix;
            this.supportsToolCalling = supportsToolCalling;
            this.toolCallPrefix = toolCallPrefix;
            this.toolCallSuffix = toolCallSuffix;
        }
        
        public String getFamily() { return family; }
        public String getSystemPromptPrefix() { return systemPromptPrefix; }
        public String getUserPromptPrefix() { return userPromptPrefix; }
        public String getAssistantPromptPrefix() { return assistantPromptPrefix; }
        public String getSystemPromptSuffix() { return systemPromptSuffix; }
        public String getUserPromptSuffix() { return userPromptSuffix; }
        public String getAssistantPromptSuffix() { return assistantPromptSuffix; }
        public String getChatSeparator() { return chatSeparator; }
        public List<String> getStopTokens() { return new ArrayList<>(stopTokens); }
        public boolean supportsThinkingMode() { return supportsThinkingMode; }
        public String getThinkingPrefix() { return thinkingPrefix; }
        public String getThinkingSuffix() { return thinkingSuffix; }
        public boolean supportsToolCalling() { return supportsToolCalling; }
        public String getToolCallPrefix() { return toolCallPrefix; }
        public String getToolCallSuffix() { return toolCallSuffix; }
    }
    
    public static class CompiledPrompt {
        private final String compiledPrompt;
        private final String modelId;
        private final List<String> stopTokens;
        private final boolean useThinkingMode;
        private final Map<String, Object> metadata;
        
        public CompiledPrompt(String compiledPrompt, String modelId, List<String> stopTokens,
                            boolean useThinkingMode, Map<String, Object> metadata) {
            this.compiledPrompt = compiledPrompt;
            this.modelId = modelId;
            this.stopTokens = stopTokens != null ? new ArrayList<>(stopTokens) : new ArrayList<>();
            this.useThinkingMode = useThinkingMode;
            this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
        }
        
        public String getCompiledPrompt() { return compiledPrompt; }
        public String getModelId() { return modelId; }
        public List<String> getStopTokens() { return new ArrayList<>(stopTokens); }
        public boolean isUseThinkingMode() { return useThinkingMode; }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    }
    
    public static class ToolFormatter {
        private final String family;
        private final String toolCallStart;
        private final String toolCallEnd;
        private final String toolResultStart;
        private final String toolResultEnd;
        private final String toolNameKey;
        private final String toolArgsKey;
        
        public ToolFormatter(String family, String toolCallStart, String toolCallEnd,
                           String toolResultStart, String toolResultEnd,
                           String toolNameKey, String toolArgsKey) {
            this.family = family;
            this.toolCallStart = toolCallStart;
            this.toolCallEnd = toolCallEnd;
            this.toolResultStart = toolResultStart;
            this.toolResultEnd = toolResultEnd;
            this.toolNameKey = toolNameKey;
            this.toolArgsKey = toolArgsKey;
        }

        public String getFamily() { return family; }
        
        public String formatToolCall(String toolName, Map<String, Object> arguments) {
            StringBuilder sb = new StringBuilder();
            sb.append(toolCallStart);
            sb.append(toolNameKey).append(": ").append(toolName);
            if (arguments != null && !arguments.isEmpty()) {
                sb.append(toolArgsKey).append(": {");
                boolean first = true;
                for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                    if (!first) sb.append(", ");
                    sb.append(entry.getKey()).append(": ").append(entry.getValue());
                    first = false;
                }
                sb.append("}");
            }
            sb.append(toolCallEnd);
            return sb.toString();
        }
        
        public String formatToolResult(String result) {
            return toolResultStart + result + toolResultEnd;
        }
    }
    
    public CompiledPrompt compile(String modelId, String systemPrompt, String userPrompt,
                                boolean useThinkingMode) {
        ModelDescriptor desc = registry.getDescriptor(modelId);
        if (desc == null) {
            throw new IllegalArgumentException("Unknown model: " + modelId);
        }
        
        PromptDialect dialect = dialects.get(desc.getFamily().toLowerCase());
        if (dialect == null) {
            dialect = dialects.get("default");
        }
        
        StringBuilder compiled = new StringBuilder();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            compiled.append(dialect.getSystemPromptPrefix());
            compiled.append(systemPrompt);
            compiled.append(dialect.getSystemPromptSuffix());
            compiled.append(dialect.getChatSeparator());
        }
        
        if (useThinkingMode && dialect.supportsThinkingMode()) {
            compiled.append(dialect.getThinkingPrefix());
            compiled.append(dialect.getThinkingSuffix());
        }
        
        compiled.append(dialect.getUserPromptPrefix());
        compiled.append(userPrompt);
        compiled.append(dialect.getUserPromptSuffix());
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("family", desc.getFamily());
        metadata.put("dialect", dialect.getFamily());
        
        return new CompiledPrompt(compiled.toString(), modelId, 
            dialect.getStopTokens(), useThinkingMode, metadata);
    }

    /**
     * Compile prompt and append abstraction-aware context packing when present.
     */
    public CompiledPrompt compileWithContext(
        String modelId,
        String systemPrompt,
        String userPrompt,
        Map<String, Object> context,
        boolean useThinkingMode
    ) {
        String packedContext = packContextForUserPrompt(context, 4);
        String enrichedUserPrompt = (userPrompt == null ? "" : userPrompt)
            + (packedContext.isBlank() ? "" : "\n\n" + packedContext);

        CompiledPrompt compiled = compile(modelId, systemPrompt, enrichedUserPrompt, useThinkingMode);
        Map<String, Object> metadata = new HashMap<>(compiled.getMetadata());
        metadata.put("contextPacked", !packedContext.isBlank());
        metadata.put("contextPackLength", packedContext.length());
        return new CompiledPrompt(
            compiled.getCompiledPrompt(),
            compiled.getModelId(),
            compiled.getStopTokens(),
            compiled.isUseThinkingMode(),
            metadata
        );
    }
    
    public CompiledPrompt compileChat(String modelId, String systemPrompt,
                                      List<ModelAdapter.Message> history,
                                      boolean useThinkingMode) {
        ModelDescriptor desc = registry.getDescriptor(modelId);
        if (desc == null) {
            throw new IllegalArgumentException("Unknown model: " + modelId);
        }
        
        PromptDialect dialect = dialects.get(desc.getFamily().toLowerCase());
        if (dialect == null) {
            dialect = dialects.get("default");
        }
        
        StringBuilder compiled = new StringBuilder();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            compiled.append(dialect.getSystemPromptPrefix());
            compiled.append(systemPrompt);
            compiled.append(dialect.getSystemPromptSuffix());
            compiled.append(dialect.getChatSeparator());
        }
        
        if (useThinkingMode && dialect.supportsThinkingMode()) {
            compiled.append(dialect.getThinkingPrefix());
            compiled.append(dialect.getThinkingSuffix());
        }
        
        for (ModelAdapter.Message message : history) {
            String prefix, suffix;
            switch (message.getRole()) {
                case SYSTEM:
                    prefix = dialect.getSystemPromptPrefix();
                    suffix = dialect.getSystemPromptSuffix();
                    break;
                case USER:
                    prefix = dialect.getUserPromptPrefix();
                    suffix = dialect.getUserPromptSuffix();
                    break;
                case ASSISTANT:
                    prefix = dialect.getAssistantPromptPrefix();
                    suffix = dialect.getAssistantPromptSuffix();
                    break;
                case TOOL:
                    prefix = "Tool: ";
                    suffix = "\n";
                    break;
                default:
                    prefix = "";
                    suffix = "";
            }
            compiled.append(prefix);
            compiled.append(message.getContent());
            compiled.append(suffix);
            compiled.append(dialect.getChatSeparator());
        }
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("family", desc.getFamily());
        metadata.put("dialect", dialect.getFamily());
        metadata.put("messageCount", history.size());
        
        return new CompiledPrompt(compiled.toString(), modelId,
            dialect.getStopTokens(), useThinkingMode, metadata);
    }
    
    public String formatToolCalls(String modelId, List<ModelAdapter.ToolCall> toolCalls) {
        ModelDescriptor desc = registry.getDescriptor(modelId);
        if (desc == null) {
            throw new IllegalArgumentException("Unknown model: " + modelId);
        }
        
        ToolFormatter formatter = toolFormatters.get(desc.getFamily().toLowerCase());
        if (formatter == null) {
            formatter = toolFormatters.get("default");
        }
        
        StringBuilder sb = new StringBuilder();
        for (ModelAdapter.ToolCall toolCall : toolCalls) {
            sb.append(formatter.formatToolCall(toolCall.getName(), toolCall.getArguments()));
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    public List<ModelAdapter.ToolCall> extractToolCalls(String modelId, String response) {
        ModelDescriptor desc = registry.getDescriptor(modelId);
        if (desc == null) {
            return Collections.emptyList();
        }
        
        ToolFormatter formatter = toolFormatters.get(desc.getFamily().toLowerCase());
        if (formatter == null) {
            formatter = toolFormatters.get("default");
        }
        
        List<ModelAdapter.ToolCall> toolCalls = new ArrayList<>();
        
        Pattern pattern = Pattern.compile(formatter.toolNameKey + ": (\\w+)");
        Matcher matcher = pattern.matcher(response);
        
        while (matcher.find()) {
            String toolName = matcher.group(1);
            toolCalls.add(new ModelAdapter.ToolCall(
                UUID.randomUUID().toString(),
                toolName,
                new HashMap<>()
            ));
        }
        
        return toolCalls;
    }
    
    public void registerDialect(PromptDialect dialect) {
        dialects.put(dialect.getFamily().toLowerCase(), dialect);
    }
    
    public void registerToolFormatter(ToolFormatter formatter) {
        toolFormatters.put(formatter.getFamily().toLowerCase(), formatter);
    }
    
    public PromptDialect getDialect(String modelId) {
        ModelDescriptor desc = registry.getDescriptor(modelId);
        if (desc == null) {
            return dialects.get("default");
        }
        
        PromptDialect dialect = dialects.get(desc.getFamily().toLowerCase());
        return dialect != null ? dialect : dialects.get("default");
    }

    /**
     * Build compact context block for prompt packing.
     */
    public String packContextForUserPrompt(Map<String, Object> context, int maxArtifacts) {
        if (context == null || context.isEmpty()) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        out.append("[Fraynix Context Pack]\n");
        boolean wroteAny = false;

        Object capabilitySignals = context.get("capabilitySignals");
        if (capabilitySignals instanceof Map<?, ?> map && !map.isEmpty()) {
            wroteAny = true;
            out.append("capability_signals: ");
            int written = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (written > 0) {
                    out.append(", ");
                }
                out.append(entry.getKey()).append("=").append(entry.getValue());
                written++;
                if (written >= 8) {
                    break;
                }
            }
            out.append('\n');
        }

        Object abstractions = context.get("abstractedArtifacts");
        if (abstractions instanceof List<?> list && !list.isEmpty()) {
            wroteAny = true;
            out.append("abstracted_artifacts:\n");
            int limit = Math.max(1, maxArtifacts);
            for (int i = 0; i < list.size() && i < limit; i++) {
                Object item = list.get(i);
                String summary = extractArtifactSummary(item);
                if (!summary.isBlank()) {
                    out.append("- ").append(summary).append('\n');
                }
            }
        }

        List<?> hints = null;
        Object taskHints = context.get("generatedTaskHints");
        if (taskHints instanceof List<?> directHints) {
            hints = directHints;
        } else {
            Object generatedHints = context.get("generatedEvaluationTasks");
            if (generatedHints instanceof List<?> generatedList) {
                hints = generatedList;
            }
        }
        if (hints != null && !hints.isEmpty()) {
            wroteAny = true;
            out.append("task_hints:\n");
            for (int i = 0; i < hints.size() && i < 4; i++) {
                out.append("- ").append(compact(hints.get(i))).append('\n');
            }
        }

        Object provenance = context.get("provenanceSignals");
        if (provenance instanceof List<?> list && !list.isEmpty()) {
            wroteAny = true;
            out.append("provenance:\n");
            for (int i = 0; i < list.size() && i < 3; i++) {
                out.append("- ").append(compact(list.get(i))).append('\n');
            }
        }

        if (!wroteAny) {
            return "";
        }
        return out.toString().trim();
    }

    private String extractArtifactSummary(Object item) {
        if (item == null) {
            return "";
        }
        if (item instanceof Map<?, ?> artifactMap) {
            Object sourceType = artifactMap.get("sourceType");
            Object summary = artifactMap.get("summary");
            Object sourceId = artifactMap.get("sourceId");
            String prefix = sourceType == null ? "" : "[" + sourceType + "] ";
            String body = summary == null ? compact(sourceId) : compact(summary);
            return (prefix + body).trim();
        }
        return compact(item);
    }

    private String compact(Object value) {
        if (value == null) {
            return "";
        }
        String text = value.toString().replaceAll("\\s+", " ").trim();
        if (text.length() <= 180) {
            return text;
        }
        return text.substring(0, 177) + "...";
    }
    
    private void initializeDefaultDialects() {
        String nl = "\n";
        String nnl = "\n\n";
        
        dialects.put("default", new PromptDialect(
            "default",
            "System: ", nnl + "User: ", nnl + "Assistant: ",
            "", "", "",
            nnl,
            Arrays.asList("<|end|>", "<|im_end|>", "<|eot_id|>"),
            false, "", "",
            true, "<|tool_call|>", "<|tool_end|>"
        ));
        
        dialects.put("glm", new PromptDialect(
            "glm",
            "sop:", nl + "user:", nl + "assistant:",
            "", "", "",
            nl,
            Arrays.asList("<|end|>", "<|im_end|>"),
            false, "", "",
            true, "<|tool_call|>", "<|tool_end|>"
        ));
        
        dialects.put("qwen", new PromptDialect(
            "qwen",
            "system:", nl + "user:", nl + "assistant:",
            "", "", "",
            nl,
            Arrays.asList("<|im_end|>", "<|eot_id|>"),
            true, "<think>", "</think>",
            true, "<|tool_call|>", "<|tool_end|>"
        ));
        
        dialects.put("deepseek", new PromptDialect(
            "deepseek",
            "system:", nl + "user:", nl + "assistant:",
            "", "", "",
            nl,
            Arrays.asList("<|end|>", "<|im_end|>"),
            false, "", "",
            true, "<|tool_call|>", "<|tool_end|>"
        ));
        
        dialects.put("kimi", new PromptDialect(
            "kimi",
            "system:", nl + "user:", nl + "assistant:",
            "", "", "",
            nl,
            Arrays.asList("<|end|>", "<|im_end|>"),
            false, "", "",
            true, "<|tool_call|>", "<|tool_end|>"
        ));
        
        dialects.put("gemma", new PromptDialect(
            "gemma",
            "system:", nl + "user:", nl + "assistant:",
            "", "", "",
            nl,
            Arrays.asList("<|end|>", "<|im_end|>"),
            false, "", "",
            true, "<|tool_call|>", "<|tool_end|>"
        ));
        
        dialects.put("minimax", new PromptDialect(
            "minimax",
            "system:", nl + "user:", nl + "assistant:",
            "", "", "",
            nl,
            Arrays.asList("<|end|>", "<|im_end|>"),
            false, "", "",
            true, "<|tool_call|>", "<|tool_end|>"
        ));
        
        dialects.put("step", new PromptDialect(
            "step",
            "system:", nl + "user:", nl + "assistant:",
            "", "", "",
            nl,
            Arrays.asList("<|end|>", "<|im_end|>"),
            false, "", "",
            true, "<|tool_call|>", "<|tool_end|>"
        ));
        
        toolFormatters.put("default", new ToolFormatter(
            "default",
            "<|tool_call|>", "<|tool_end|>",
            "<|tool_result|>", "<|tool_result_end|>",
            "name", "arguments"
        ));
        
        toolFormatters.put("glm", new ToolFormatter(
            "glm",
            "<|tool_call|>", "<|tool_end|>",
            "<|tool_result|>", "<|tool_result_end|>",
            "name", "arguments"
        ));
        
        toolFormatters.put("qwen", new ToolFormatter(
            "qwen",
            "<|tool_call|>", "<|tool_end|>",
            "<|tool_result|>", "<|tool_result_end|>",
            "name", "arguments"
        ));
        
        toolFormatters.put("deepseek", new ToolFormatter(
            "deepseek",
            "<|tool_call|>", "<|tool_end|>",
            "<|tool_result|>", "<|tool_result_end|>",
            "name", "arguments"
        ));
    }
}
