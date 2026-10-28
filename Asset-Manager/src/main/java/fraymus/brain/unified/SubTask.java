package fraymus.brain.unified;

import java.util.List;
import java.util.Objects;

/**
 * A decomposed sub-task extracted from a complex multi-domain query.
 * Each sub-task targets a specific brain with a focused prompt.
 */
public class SubTask {
    public enum TaskType {
        MATH,
        PHYSICS,
        CODE,
        ANALOGY,
        EVOLUTION,
        CREATIVE,
        REASONING,
        MEMORY,
        VISION,
        QUANTUM,
        CONSCIOUSNESS,
        GENERAL
    }

    private final TaskType type;
    private final String prompt;
    private final String targetBrainName;
    private final List<BrainCapability> requiredCapabilities;
    private final int orderIndex;

    public SubTask(TaskType type, String prompt, String targetBrainName,
                   List<BrainCapability> requiredCapabilities, int orderIndex) {
        this.type = Objects.requireNonNull(type);
        this.prompt = Objects.requireNonNull(prompt);
        this.targetBrainName = targetBrainName;
        this.requiredCapabilities = requiredCapabilities != null
            ? List.copyOf(requiredCapabilities) : List.of();
        this.orderIndex = orderIndex;
    }

    public TaskType getType() { return type; }
    public String getPrompt() { return prompt; }
    public String getTargetBrainName() { return targetBrainName; }
    public List<BrainCapability> getRequiredCapabilities() { return requiredCapabilities; }
    public int getOrderIndex() { return orderIndex; }

    @Override
    public String toString() {
        return "SubTask{" + type + " → " + (targetBrainName != null ? targetBrainName : "auto")
            + " [" + prompt.substring(0, Math.min(60, prompt.length())) + "...]}";
    }
}
