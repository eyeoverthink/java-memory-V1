package fraymus.model;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * EvaluationHarness - Fraynix-native task evaluation for model selection learning.
 * 
 * This evaluates models on actual Fraynix tasks rather than generic leaderboard ranks,
 * enabling the system to learn which models actually work for specific capabilities.
 */
public class EvaluationHarness {
    
    private final ModelRegistry registry;
    private final ModelSelector selector;
    private final PromptCompiler compiler;
    private final Map<String, EvaluationResult> evaluationCache;
    private final Map<String, TaskSuite> taskSuites;
    
    public EvaluationHarness(ModelRegistry registry, ModelSelector selector, PromptCompiler compiler) {
        this.registry = registry;
        this.selector = selector;
        this.compiler = compiler;
        this.evaluationCache = new ConcurrentHashMap<>();
        this.taskSuites = new ConcurrentHashMap<>();
        initializeDefaultTaskSuites();
    }
    
    /**
     * Task suite for evaluating a specific capability
     */
    public static class TaskSuite {
        private final String name;
        private final CapabilityProfile.Capability capability;
        private final List<EvaluationTask> tasks;
        private final double minPassRate;
        
        public TaskSuite(String name, CapabilityProfile.Capability capability,
                       List<EvaluationTask> tasks, double minPassRate) {
            this.name = name;
            this.capability = capability;
            this.tasks = new ArrayList<>(tasks);
            this.minPassRate = minPassRate;
        }
        
        public String getName() { return name; }
        public CapabilityProfile.Capability getCapability() { return capability; }
        public List<EvaluationTask> getTasks() { return new ArrayList<>(tasks); }
        public double getMinPassRate() { return minPassRate; }
    }
    
    /**
     * Individual evaluation task
     */
    public static class EvaluationTask {
        private final String id;
        private final String description;
        private final String prompt;
        private final Function<String, Boolean> evaluator;
        private final int timeoutMs;
        
        public EvaluationTask(String id, String description, String prompt,
                           Function<String, Boolean> evaluator, int timeoutMs) {
            this.id = id;
            this.description = description;
            this.prompt = prompt;
            this.evaluator = evaluator;
            this.timeoutMs = timeoutMs;
        }
        
        public String getId() { return id; }
        public String getDescription() { return description; }
        public String getPrompt() { return prompt; }
        public Function<String, Boolean> getEvaluator() { return evaluator; }
        public int getTimeoutMs() { return timeoutMs; }
    }
    
    /**
     * Evaluation result for a model on a task suite
     */
    public static class EvaluationResult {
        private final String modelId;
        private final String taskSuite;
        private final int totalTasks;
        private final int passedTasks;
        private final double passRate;
        private final long totalLatencyMs;
        private final double avgLatencyMs;
        private final Map<String, TaskResult> taskResults;
        private final long timestamp;
        
        public EvaluationResult(String modelId, String taskSuite, int totalTasks, int passedTasks,
                             long totalLatencyMs, Map<String, TaskResult> taskResults) {
            this.modelId = modelId;
            this.taskSuite = taskSuite;
            this.totalTasks = totalTasks;
            this.passedTasks = passedTasks;
            this.passRate = totalTasks > 0 ? (double) passedTasks / totalTasks : 0.0;
            this.totalLatencyMs = totalLatencyMs;
            this.avgLatencyMs = totalTasks > 0 ? (double) totalLatencyMs / totalTasks : 0.0;
            this.taskResults = new HashMap<>(taskResults);
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getModelId() { return modelId; }
        public String getTaskSuite() { return taskSuite; }
        public int getTotalTasks() { return totalTasks; }
        public int getPassedTasks() { return passedTasks; }
        public double getPassRate() { return passRate; }
        public long getTotalLatencyMs() { return totalLatencyMs; }
        public double getAvgLatencyMs() { return avgLatencyMs; }
        public Map<String, TaskResult> getTaskResults() { return new HashMap<>(taskResults); }
        public long getTimestamp() { return timestamp; }
        
        public boolean passesThreshold(double threshold) {
            return passRate >= threshold;
        }
    }
    
    /**
     * Result for a single task
     */
    public static class TaskResult {
        private final String taskId;
        private final boolean passed;
        private final String output;
        private final long latencyMs;
        private final String error;
        
        public TaskResult(String taskId, boolean passed, String output,
                        long latencyMs, String error) {
            this.taskId = taskId;
            this.passed = passed;
            this.output = output;
            this.latencyMs = latencyMs;
            this.error = error;
        }
        
        public String getTaskId() { return taskId; }
        public boolean isPassed() { return passed; }
        public String getOutput() { return output; }
        public long getLatencyMs() { return latencyMs; }
        public String getError() { return error; }
    }
    
    /**
     * Evaluate a model on a task suite
     */
    public EvaluationResult evaluateModel(String modelId, String taskSuiteName) {
        String cacheKey = modelId + ":" + taskSuiteName;
        if (evaluationCache.containsKey(cacheKey)) {
            return evaluationCache.get(cacheKey);
        }
        
        TaskSuite suite = taskSuites.get(taskSuiteName);
        if (suite == null) {
            throw new IllegalArgumentException("Unknown task suite: " + taskSuiteName);
        }
        
        ModelAdapter adapter = registry.getAdapter(modelId);
        if (adapter == null) {
            return new EvaluationResult(modelId, taskSuiteName, suite.getTasks().size(), 0, 0, 
                Collections.emptyMap());
        }
        
        Map<String, TaskResult> taskResults = new HashMap<>();
        int passedTasks = 0;
        long totalLatencyMs = 0;
        
        for (EvaluationTask task : suite.getTasks()) {
            TaskResult result = evaluateTask(adapter, modelId, task);
            taskResults.put(task.getId(), result);
            
            if (result.isPassed()) {
                passedTasks++;
            }
            totalLatencyMs += result.getLatencyMs();
        }
        
        EvaluationResult evaluation = new EvaluationResult(modelId, taskSuiteName,
            suite.getTasks().size(), passedTasks, totalLatencyMs, taskResults);
        
        evaluationCache.put(cacheKey, evaluation);
        return evaluation;
    }
    
    /**
     * Evaluate a single task
     */
    private TaskResult evaluateTask(ModelAdapter adapter, String modelId, EvaluationTask task) {
        long startTime = System.currentTimeMillis();
        
        try {
            PromptCompiler.CompiledPrompt compiled = compiler.compile(modelId, null, task.getPrompt(), false);
            
            ModelAdapter.InferenceRequest request = new ModelAdapter.InferenceRequest(
                modelId, compiled.getCompiledPrompt(), null, null, null);
            
            Future<ModelAdapter.InferenceResponse> future = adapter.inferAsync(request);
            
            ModelAdapter.InferenceResponse response = future.get(task.getTimeoutMs(), TimeUnit.MILLISECONDS);
            
            long latencyMs = System.currentTimeMillis() - startTime;
            
            if (response.isSuccess()) {
                boolean passed = task.getEvaluator().apply(response.getContent());
                return new TaskResult(task.getId(), passed, response.getContent(), latencyMs, null);
            } else {
                return new TaskResult(task.getId(), false, "", latencyMs, response.getError());
            }
            
        } catch (TimeoutException e) {
            long latencyMs = System.currentTimeMillis() - startTime;
            return new TaskResult(task.getId(), false, "", latencyMs, "Timeout");
        } catch (Exception e) {
            long latencyMs = System.currentTimeMillis() - startTime;
            return new TaskResult(task.getId(), false, "", latencyMs, e.getMessage());
        }
    }
    
    /**
     * Evaluate all models on a task suite
     */
    public Map<String, EvaluationResult> evaluateAllModels(String taskSuiteName) {
        Map<String, EvaluationResult> results = new HashMap<>();
        
        for (String modelId : registry.getAllModelIds()) {
            if (registry.hasAdapter(modelId)) {
                results.put(modelId, evaluateModel(modelId, taskSuiteName));
            }
        }
        
        return results;
    }
    
    /**
     * Evaluate models for a specific organ
     */
    public Map<String, EvaluationResult> evaluateForOrgan(CapabilityProfile.Organ organ) {
        Map<String, EvaluationResult> results = new HashMap<>();
        
        for (String modelId : registry.getAllModelIds()) {
            if (registry.hasAdapter(modelId)) {
                for (TaskSuite suite : taskSuites.values()) {
                    if (suite.getCapability() != null) {
                        CapabilityProfile profile = registry.getProfile(modelId);
                        if (profile.computeOrganSuitability(organ) > 0.5) {
                            EvaluationResult result = evaluateModel(modelId, suite.getName());
                            results.put(modelId + ":" + suite.getName(), result);
                        }
                    }
                }
            }
        }
        
        return results;
    }
    
    /**
     * Get best model for a task suite
     */
    public Optional<String> getBestModelForTaskSuite(String taskSuiteName) {
        Map<String, EvaluationResult> results = evaluateAllModels(taskSuiteName);
        
        return results.entrySet().stream()
            .max(Comparator.comparingDouble(e -> e.getValue().getPassRate()))
            .map(Map.Entry::getKey);
    }
    
    /**
     * Clear evaluation cache
     */
    public void clearCache() {
        evaluationCache.clear();
    }
    
    /**
     * Register custom task suite
     */
    public void registerTaskSuite(TaskSuite suite) {
        taskSuites.put(suite.getName(), suite);
    }
    
    /**
     * Get task suite
     */
    public TaskSuite getTaskSuite(String name) {
        return taskSuites.get(name);
    }
    
    /**
     * Get all task suites
     */
    public Collection<TaskSuite> getAllTaskSuites() {
        return new ArrayList<>(taskSuites.values());
    }

    /**
     * Register lightweight generated tasks derived from absorbed artifacts.
     */
    public String registerGeneratedTaskSuiteFromHints(
        String suitePrefix,
        CapabilityProfile.Capability capability,
        List<String> taskHints,
        int timeoutMs
    ) {
        if (taskHints == null || taskHints.isEmpty()) {
            return null;
        }

        String normalizedPrefix = (suitePrefix == null || suitePrefix.isBlank())
            ? "generated"
            : suitePrefix.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_\\-]", "_");
        String suiteName = normalizedPrefix + "_" + Integer.toHexString(taskHints.hashCode());
        if (taskSuites.containsKey(suiteName)) {
            return suiteName;
        }

        List<EvaluationTask> tasks = new ArrayList<>();
        int index = 1;
        for (String hint : taskHints) {
            if (hint == null || hint.isBlank()) {
                continue;
            }
            String prompt = "Task hint: " + hint + "\nProvide a deterministic actionable answer.";
            tasks.add(new EvaluationTask(
                suiteName + "_task_" + index,
                "Generated from absorbed artifact",
                prompt,
                output -> evaluateGeneratedHint(capability, hint, output),
                Math.max(2000, timeoutMs)
            ));
            index++;
            if (index > 6) {
                break;
            }
        }

        if (tasks.isEmpty()) {
            return null;
        }

        registerTaskSuite(new TaskSuite(
            suiteName,
            capability == null ? CapabilityProfile.Capability.REASONING : capability,
            tasks,
            0.50
        ));
        return suiteName;
    }

    private boolean evaluateGeneratedHint(
        CapabilityProfile.Capability capability,
        String hint,
        String output
    ) {
        if (output == null || output.isBlank()) {
            return false;
        }

        String loweredOutput = output.toLowerCase(Locale.ROOT);
        if (loweredOutput.length() < 12) {
            return false;
        }

        String loweredHint = hint == null ? "" : hint.toLowerCase(Locale.ROOT);
        if (capability == CapabilityProfile.Capability.CODING) {
            return loweredOutput.contains("```")
                || loweredOutput.contains("class ")
                || loweredOutput.contains("function")
                || loweredOutput.contains("def ");
        }
        if (capability == CapabilityProfile.Capability.REASONING || capability == CapabilityProfile.Capability.PRECISION) {
            return loweredOutput.contains("because")
                || loweredOutput.contains("therefore")
                || loweredOutput.contains("step")
                || loweredOutput.contains("proof");
        }
        if (capability == CapabilityProfile.Capability.TOOL_USE) {
            return loweredOutput.contains("tool")
                || loweredOutput.contains("command")
                || loweredOutput.contains("wrapper");
        }
        if (capability == CapabilityProfile.Capability.CREATIVITY) {
            return loweredOutput.contains("remix")
                || loweredOutput.contains("punchline")
                || loweredOutput.contains("creative");
        }

        if (!loweredHint.isBlank()) {
            for (String token : loweredHint.split("[^a-z0-9]+")) {
                if (token.length() >= 5 && loweredOutput.contains(token)) {
                    return true;
                }
            }
        }
        return loweredOutput.length() >= 40;
    }
    
    /**
     * Initialize default Fraynix-native task suites
     */
    private void initializeDefaultTaskSuites() {
        // Reasoning task suite
        List<EvaluationTask> reasoningTasks = new ArrayList<>();
        reasoningTasks.add(new EvaluationTask(
            "logic_1",
            "Basic logical inference",
            "If all cats are animals, and Fluffy is a cat, is Fluffy an animal? Answer yes or no.",
            output -> output.toLowerCase().contains("yes"),
            5000
        ));
        reasoningTasks.add(new EvaluationTask(
            "logic_2",
            "Transitive inference",
            "A is greater than B, B is greater than C. Is A greater than C? Answer yes or no.",
            output -> output.toLowerCase().contains("yes"),
            5000
        ));
        reasoningTasks.add(new EvaluationTask(
            "logic_3",
            "Negation",
            "If it is not raining, is it sunny? Answer yes or no.",
            output -> output.toLowerCase().contains("no"),
            5000
        ));
        
        registerTaskSuite(new TaskSuite(
            "reasoning",
            CapabilityProfile.Capability.REASONING,
            reasoningTasks,
            0.7
        ));
        
        // Coding task suite
        List<EvaluationTask> codingTasks = new ArrayList<>();
        codingTasks.add(new EvaluationTask(
            "code_1",
            "Basic function",
            "Write a function that adds two numbers.",
            output -> output.contains("function") || output.contains("def") || output.contains("public"),
            10000
        ));
        codingTasks.add(new EvaluationTask(
            "code_2",
            "Loop",
            "Write a loop that prints numbers 1 to 5.",
            output -> output.contains("for") || output.contains("while"),
            10000
        ));
        codingTasks.add(new EvaluationTask(
            "code_3",
            "Condition",
            "Write an if statement checking if x is greater than 10.",
            output -> output.contains("if"),
            10000
        ));
        
        registerTaskSuite(new TaskSuite(
            "coding",
            CapabilityProfile.Capability.CODING,
            codingTasks,
            0.7
        ));
        
        // Long context task suite
        List<EvaluationTask> contextTasks = new ArrayList<>();
        contextTasks.add(new EvaluationTask(
            "context_1",
            "Remember first item",
            "The items are: apple, banana, cherry, date, elderberry. What was the first item?",
            output -> output.toLowerCase().contains("apple"),
            5000
        ));
        contextTasks.add(new EvaluationTask(
            "context_2",
            "Remember last item",
            "The items are: apple, banana, cherry, date, elderberry. What was the last item?",
            output -> output.toLowerCase().contains("elderberry"),
            5000
        ));
        contextTasks.add(new EvaluationTask(
            "context_3",
            "Count items",
            "The items are: apple, banana, cherry, date, elderberry. How many items are there?",
            output -> output.contains("5"),
            5000
        ));
        
        registerTaskSuite(new TaskSuite(
            "long_context",
            CapabilityProfile.Capability.LONG_CONTEXT,
            contextTasks,
            0.7
        ));
        
        // Reflex speed task suite
        List<EvaluationTask> reflexTasks = new ArrayList<>();
        reflexTasks.add(new EvaluationTask(
            "reflex_1",
            "Simple math",
            "What is 2 + 2?",
            output -> output.contains("4"),
            2000
        ));
        reflexTasks.add(new EvaluationTask(
            "reflex_2",
            "Simple word",
            "What is the opposite of hot?",
            output -> output.toLowerCase().contains("cold"),
            2000
        ));
        
        registerTaskSuite(new TaskSuite(
            "reflex",
            CapabilityProfile.Capability.REFLEX_SPEED,
            reflexTasks,
            0.8
        ));
        
        // Creativity task suite
        List<EvaluationTask> creativityTasks = new ArrayList<>();
        creativityTasks.add(new EvaluationTask(
            "creative_1",
            "Metaphor",
            "Create a metaphor for time.",
            output -> output.length() > 10 && !output.toLowerCase().equals("time"),
            5000
        ));
        creativityTasks.add(new EvaluationTask(
            "creative_2",
            "Alternative use",
            "What can you use a brick for besides building?",
            output -> output.length() > 10,
            5000
        ));
        
        registerTaskSuite(new TaskSuite(
            "creativity",
            CapabilityProfile.Capability.CREATIVITY,
            creativityTasks,
            0.6
        ));
    }
}
