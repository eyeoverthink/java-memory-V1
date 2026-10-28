package brain_architecture.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Kernel-owned substrate adapter for local offline model runtimes.
 */
public final class LocalModelRuntimeAdapter implements SubstrateAdapter {
    private static final Path ROOT_RUNTIME_MODULE = Path.of(
        "Java-Memory",
        "Asset-Manager",
        "brain_architecture",
        "runtime",
        "local_transformers_runtime.py"
    );
    private static final Path PROJECT_RUNTIME_MODULE = Path.of("runtime", "local_transformers_runtime.py");
    private static final String GGUF_RUNTIME_MAIN_CLASS = "brain_architecture.kernel.GgufRuntimeModule";

    private final Path workspaceRoot;
    private final UserSpaceSubstrateAdapter filesystemAdapter;
    private final KernelExecutionBridgeAdapter executionAdapter;
    private final FoundationalSubstrateBootstrapper substrateBootstrapper;

    public LocalModelRuntimeAdapter(KernelConfiguration configuration) {
        this.workspaceRoot = configuration.getWorkspaceRoot().toAbsolutePath().normalize();
        this.filesystemAdapter = new UserSpaceSubstrateAdapter(this.workspaceRoot);
        this.executionAdapter = new KernelExecutionBridgeAdapter();
        this.substrateBootstrapper = new FoundationalSubstrateBootstrapper(configuration);
    }

    @Override
    public String getAdapterId() {
        return "local-model";
    }

    @Override
    public SubstrateSession loadContext(TaskEnvelope task, List<MemoryHit> context) {
        SubstrateSession session = new SubstrateSession(workspaceRoot);
        session.getState().put("taskId", task.getId().toString());
        session.getState().put("goal", task.getGoal());
        session.getState().put("contextHits", Integer.toString(context.size()));

        try {
            LocalModelSubstrateCatalog catalog = substrateBootstrapper.buildCatalog(resolveModelRoots(task));
            LocalModelDescriptor descriptor = selectDescriptor(task, catalog);
            LocalModelRuntimeDescriptor runtime = resolveRuntime(task, catalog, descriptor);
            TokenizerDigest tokenizerDigest = descriptor == null ? null : catalog.tokenizerFor(descriptor.modelId());

            if (descriptor != null) {
                session.getState().put("modelId", descriptor.modelId());
                session.getState().put("modelFamily", descriptor.family());
                session.getState().put("modelSourcePath", descriptor.sourcePath().toString());
            }
            if (tokenizerDigest != null) {
                session.getState().put("tokenizerFormat", tokenizerDigest.format());
                session.getState().put("tokenizerVocabSize", Integer.toString(tokenizerDigest.vocabSize()));
            }
            if (runtime != null) {
                session.getState().put("runtimeBackend", runtime.backend());
                session.getState().put("runtimeAvailable", Boolean.toString(runtime.available()));
                session.getState().put("runtimeReason", runtime.selectionReason());
                session.getState().put("runtimeTarget", runtime.targetReference());
                if (runtime.executablePath() != null) {
                    session.getState().put("runtimeExecutable", runtime.executablePath().toString());
                }
            } else {
                session.getState().put("runtimeAvailable", "false");
                session.getState().put("runtimeReason", "No runtime binding resolved for task.");
            }
        } catch (IOException e) {
            session.getState().put("runtimeAvailable", "false");
            session.getState().put("runtimeReason", "Failed to build local model catalog: " + e.getMessage());
        }

        return session;
    }

    @Override
    public SubstrateResult step(SubstrateSession session, String stepInstruction) {
        session.getState().put("lastStep", stepInstruction);
        String modelId = session.getState().getOrDefault("modelId", "unresolved-model");
        String backend = session.getState().getOrDefault("runtimeBackend", "none");
        String observation = "[local-model:" + modelId + ":" + backend + "] " + stepInstruction;
        return new SubstrateResult(true, observation, 0, Map.of(
            "adapter", getAdapterId(),
            "modelId", modelId,
            "backend", backend
        ));
    }

    @Override
    public SubstrateResult act(SubstrateSession session, SubstrateAction action) {
        return switch (action.getType()) {
            case READ_FILE, LIST_DIRECTORY, RUN_COMMAND -> filesystemAdapter.act(session, action);
            case RUN_INFERENCE -> runInference(session, action);
        };
    }

    @Override
    public SubstrateSnapshot snapshot(SubstrateSession session) {
        return new SubstrateSnapshot(session.getSessionId(), session.getWorkspaceRoot(), session.getState());
    }

    @Override
    public SubstrateSession restore(SubstrateSnapshot snapshot) {
        return new SubstrateSession(snapshot.getSessionId(), snapshot.getWorkspaceRoot(), new HashMap<>(snapshot.getState()));
    }

    private SubstrateResult runInference(SubstrateSession session, SubstrateAction action) {
        String cachedPrompt = session.getState().get("cachedInferencePrompt");
        String requestedPrompt = action.getPrompt() == null ? "" : action.getPrompt();
        String cachedOutput = session.getState().get("cachedInferenceOutput");
        if (cachedOutput != null && cachedPrompt != null && cachedPrompt.equals(requestedPrompt)) {
            Map<String, String> cached = metadata(session.getState().get("modelId"), session.getState().get("runtimeBackend"), session.getState().get("runtimeTarget"));
            cached.put("latency.ms", "0");
            cached.put("validation.status", "cached");
            return new SubstrateResult(true, cachedOutput, 0, cached);
        }

        String modelId = session.getState().get("modelId");
        String backend = session.getState().getOrDefault("runtimeBackend", "none");
        boolean available = Boolean.parseBoolean(session.getState().getOrDefault("runtimeAvailable", "false"));
        if (!available) {
            Map<String, String> failMeta = metadata(modelId, backend, null);
            failMeta.put("validation.status", "unavailable");
            return new SubstrateResult(false, session.getState().getOrDefault("runtimeReason", "No local runtime available."), -1, failMeta);
        }

        String executable = session.getState().get("runtimeExecutable");
        if (executable == null || executable.isBlank()) {
            Map<String, String> failMeta = metadata(modelId, backend, null);
            failMeta.put("validation.status", "no-executable");
            return new SubstrateResult(false, "No local model executable resolved.", -1, failMeta);
        }

        String prompt = requestedPrompt;
        int maxTokens = action.getMaxTokens() > 0 ? action.getMaxTokens() : 128;
        String targetReference = session.getState().getOrDefault("runtimeTarget", modelId == null ? "" : modelId);
        long startNanos = System.nanoTime();
        if ("ollama_cli".equalsIgnoreCase(backend) || "ollama_http".equalsIgnoreCase(backend)) {
            SubstrateResult httpResult = runOllamaHttpInference(modelId, targetReference, prompt, maxTokens);
            if (httpResult.isSuccess()) {
                session.getState().put("cachedInferencePrompt", prompt);
                session.getState().put("cachedInferenceOutput", truncate(httpResult.getOutput()));
                return withStandardDiagnostics(httpResult, startNanos, maxTokens, "success");
            }
        }
        if ("docker_llama_server".equalsIgnoreCase(backend)) {
            SubstrateResult serverResult = runDockerLlamaServerInference(modelId, executable, targetReference, prompt, maxTokens);
            if (serverResult.isSuccess()) {
                session.getState().put("cachedInferencePrompt", prompt);
                session.getState().put("cachedInferenceOutput", truncate(serverResult.getOutput()));
            }
            return withStandardDiagnostics(serverResult, startNanos, maxTokens, serverResult.isSuccess() ? "success" : "failed");
        }
        List<String> command = buildInferenceCommand(backend, executable, targetReference, session.getState().get("modelSourcePath"), prompt, maxTokens);
        if (command.isEmpty()) {
            Map<String, String> failMeta = metadata(modelId, backend, null);
            failMeta.put("validation.status", "not-executable");
            return new SubstrateResult(false, "Resolved runtime backend is not executable: " + backend, -1, failMeta);
        }

        SubstrateResult commandResult = executionAdapter.runCommand(getAdapterId(), session.getWorkspaceRoot(), command);
        Map<String, String> mergedMeta = metadata(modelId, backend, targetReference);
        mergedMeta.putAll(commandResult.getMetadata());
        mergedMeta.put("maxTokens", Integer.toString(maxTokens));
        if (commandResult.isSuccess()) {
            session.getState().put("cachedInferencePrompt", prompt);
            session.getState().put("cachedInferenceOutput", truncate(commandResult.getOutput()));
        }
        SubstrateResult merged = new SubstrateResult(commandResult.isSuccess(), truncate(commandResult.getOutput()), commandResult.getExitCode(), mergedMeta);
        return withStandardDiagnostics(merged, startNanos, maxTokens, commandResult.isSuccess() ? "success" : "failed");
    }

    private SubstrateResult runDockerLlamaServerInference(
            String modelId,
            String executable,
            String targetReference,
            String prompt,
            int maxTokens) {
        int port = allocateEphemeralPort();
        if (port <= 0) {
            return new SubstrateResult(false, "Failed to allocate an ephemeral port for docker_llama_server.", -1, metadata(modelId, "docker_llama_server", targetReference));
        }
        List<String> command = List.of(
            executable,
            "-m",
            targetReference,
            "--host",
            "127.0.0.1",
            "--port",
            Integer.toString(port),
            "-n",
            Integer.toString(Math.max(1, maxTokens))
        );
        Process process = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(workspaceRoot.toFile());
            builder.redirectErrorStream(true);
            process = builder.start();
            if (!waitForServerReady(process, port)) {
                String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                return new SubstrateResult(false, truncate(output.isBlank() ? "docker_llama_server did not become healthy in time." : output), -1, dockerMetadata(modelId, targetReference, command));
            }

            String body = "{"
                + "\"prompt\":\"" + escapeJson(prompt) + "\","
                + "\"n_predict\":" + Math.max(1, maxTokens) + ","
                + "\"temperature\":0.0,"
                + "\"stream\":false"
                + "}";
            HttpURLConnection connection = (HttpURLConnection) URI.create("http://127.0.0.1:" + port + "/completion").toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5_000);
            connection.setReadTimeout(180_000);
            connection.setDoOutput(true);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(body.getBytes(StandardCharsets.UTF_8));
            }
            int statusCode = connection.getResponseCode();
            InputStream stream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String payload = stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            String content = extractJsonString(payload, "content");
            if (statusCode >= 400) {
                return new SubstrateResult(false, truncate(payload), statusCode, dockerMetadata(modelId, targetReference, command));
            }
            return new SubstrateResult(true, truncate(content == null ? payload : content), statusCode, dockerMetadata(modelId, targetReference, command));
        } catch (IOException e) {
            return new SubstrateResult(false, e.getMessage(), -1, dockerMetadata(modelId, targetReference, command));
        } finally {
            if (process != null && process.isAlive()) {
                process.destroy();
                try {
                    process.waitFor();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    process.destroyForcibly();
                }
            }
        }
    }

    private List<Path> resolveModelRoots(TaskEnvelope task) {
        Set<Path> roots = new LinkedHashSet<>();
        String explicitRoot = task.getRequestedModelRoot();
        if (explicitRoot != null && !explicitRoot.isBlank()) {
            addRootIfExists(roots, Path.of(explicitRoot));
        }
        if (!task.getArtifacts().isEmpty()) {
            for (Path artifact : task.getArtifacts()) {
                if (artifact != null) {
                    Path absolute = artifact.isAbsolute() ? artifact : workspaceRoot.resolve(artifact).normalize();
                    Path parent = absolute.getParent();
                    if (parent != null) {
                        addRootIfExists(roots, parent);
                    }
                }
            }
        }
        if (roots.isEmpty()) {
            roots.addAll(substrateBootstrapper.defaultModelRoots());
            if (roots.isEmpty()) {
                roots.add(workspaceRoot);
            }
        }
        return new ArrayList<>(roots);
    }

    private void addRootIfExists(Set<Path> roots, Path candidate) {
        if (candidate == null) {
            return;
        }
        Path resolved = candidate.isAbsolute() ? candidate.toAbsolutePath().normalize() : workspaceRoot.resolve(candidate).toAbsolutePath().normalize();
        if (Files.exists(resolved)) {
            roots.add(resolved);
        }
    }

    private LocalModelDescriptor selectDescriptor(TaskEnvelope task, LocalModelSubstrateCatalog catalog) {
        String requestedModelId = task.getRequestedModelId();
        if (requestedModelId != null && !requestedModelId.isBlank()) {
            LocalModelDescriptor descriptor = catalog.descriptorFor(requestedModelId);
            if (descriptor != null) {
                return descriptor;
            }
        }

        for (LocalModelRuntimeDescriptor runtime : catalog.runtimeDescriptors()) {
            if (runtime.available()) {
                LocalModelDescriptor descriptor = catalog.descriptorFor(runtime.modelId());
                if (descriptor != null) {
                    return descriptor;
                }
            }
        }

        return catalog.modelDescriptors().isEmpty() ? null : catalog.modelDescriptors().get(0);
    }

    private LocalModelRuntimeDescriptor resolveRuntime(
            TaskEnvelope task,
            LocalModelSubstrateCatalog catalog,
            LocalModelDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }

        LocalModelRuntimeDescriptor discoveredRuntime = catalog.runtimeFor(descriptor.modelId());
        Path runnerOverride = resolveOptionalPath(task.getRequestedModelRunner());
        if (runnerOverride != null) {
            String backend = task.getRequestedModelBackend();
            if (backend == null || backend.isBlank()) {
                backend = discoveredRuntime == null ? inferBackend(descriptor.family()) : discoveredRuntime.backend();
            }
            String targetReference = discoveredRuntime == null || discoveredRuntime.targetReference().isBlank()
                ? inferTargetReference(descriptor)
                : discoveredRuntime.targetReference();
            boolean available = Files.exists(runnerOverride);
            return new LocalModelRuntimeDescriptor(
                descriptor.modelId(),
                backend,
                available,
                runnerOverride,
                targetReference,
                available ? "Task override runner selected." : "Task override runner path missing."
            );
        }

        return discoveredRuntime;
    }

    private String inferBackend(String family) {
        if ("ollama_modelfile".equalsIgnoreCase(family)) {
            return "ollama_cli";
        }
        if ("ollama_gguf_model".equalsIgnoreCase(family)) {
            return "docker_llama_server";
        }
        if ("gguf_model".equalsIgnoreCase(family)) {
            return "llama_cpp_cli";
        }
        return "metadata_only";
    }

    private String inferTargetReference(LocalModelDescriptor descriptor) {
        String from = descriptor.attributes().get("from");
        if (from != null && !from.isBlank()) {
            return normalizeOllamaTargetReference(from);
        }
        return descriptor.sourcePath().toString();
    }

    private Path resolveOptionalPath(String pathValue) {
        if (pathValue == null || pathValue.isBlank()) {
            return null;
        }
        Path path = Path.of(pathValue);
        return path.isAbsolute() ? path.normalize() : workspaceRoot.resolve(path).normalize();
    }

    private List<String> buildInferenceCommand(
            String backend,
            String executable,
            String targetReference,
            String modelSourcePath,
            String prompt,
            int maxTokens) {
        if ("ollama_cli".equalsIgnoreCase(backend)) {
            if (targetReference == null || targetReference.isBlank()) {
                return List.of();
            }
            return List.of(executable, "run", targetReference, prompt);
        }
        if ("llama_cpp_cli".equalsIgnoreCase(backend)) {
            if (modelSourcePath == null || modelSourcePath.isBlank()) {
                return List.of();
            }
            return List.of(executable, "-m", modelSourcePath, "-p", prompt, "-n", Integer.toString(maxTokens));
        }
        if ("python_transformers".equalsIgnoreCase(backend)) {
            String bundleRoot = targetReference == null || targetReference.isBlank() ? modelSourcePath : targetReference;
            if (bundleRoot == null || bundleRoot.isBlank()) {
                return List.of();
            }
            return List.of(
                executable,
                resolveTransformersRuntimeModule().toString(),
                bundleRoot,
                prompt,
                Integer.toString(maxTokens)
            );
        }
        if ("java_gguf_loader".equalsIgnoreCase(backend) || "java_gguf_primitives".equalsIgnoreCase(backend)) {
            String ggufPath = targetReference == null || targetReference.isBlank() ? modelSourcePath : targetReference;
            String classPath = System.getProperty("java.class.path", "");
            if (ggufPath == null || ggufPath.isBlank() || classPath.isBlank()) {
                return List.of();
            }
            return List.of(
                executable,
                "-cp",
                classPath,
                GGUF_RUNTIME_MAIN_CLASS,
                ggufPath,
                prompt,
                Integer.toString(maxTokens)
            );
        }
        return List.of();
    }

    private boolean waitForServerReady(Process process, int port) {
        for (int attempt = 0; attempt < 180; attempt++) {
            if (process != null && !process.isAlive()) {
                return false;
            }
            try {
                Thread.sleep(1_000L);
                HttpURLConnection connection = (HttpURLConnection) URI.create("http://127.0.0.1:" + port + "/health").toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1_000);
                connection.setReadTimeout(1_000);
                int statusCode = connection.getResponseCode();
                if (statusCode >= 200 && statusCode < 300) {
                    return true;
                }
            } catch (IOException ignored) {
                // Server is still starting.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private int allocateEphemeralPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            return -1;
        }
    }

    private Path resolveTransformersRuntimeModule() {
        Path rootCandidate = workspaceRoot.resolve(ROOT_RUNTIME_MODULE).normalize();
        if (Files.exists(rootCandidate)) {
            return rootCandidate;
        }
        Path projectCandidate = workspaceRoot.resolve(PROJECT_RUNTIME_MODULE).normalize();
        if (Files.exists(projectCandidate)) {
            return projectCandidate;
        }
        return rootCandidate;
    }

    private SubstrateResult runOllamaHttpInference(String modelId, String targetReference, String prompt, int maxTokens) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create("http://127.0.0.1:11434/api/generate").toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5_000);
            connection.setReadTimeout(120_000);
            connection.setDoOutput(true);
            String body = "{"
                + "\"model\":\"" + escapeJson(targetReference) + "\","
                + "\"prompt\":\"" + escapeJson(prompt) + "\","
                + "\"stream\":false,"
                + "\"options\":{\"num_predict\":" + Math.max(1, maxTokens) + "}"
                + "}";
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int statusCode = connection.getResponseCode();
            InputStream stream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String payload = stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            String response = extractJsonString(payload, "response");
            String error = extractJsonString(payload, "error");
            if (statusCode >= 400 || (error != null && !error.isBlank())) {
                return new SubstrateResult(false, error == null || error.isBlank() ? payload : error, statusCode, metadata(modelId, "ollama_http", targetReference));
            }
            return new SubstrateResult(true, response == null ? payload : response, statusCode, metadata(modelId, "ollama_http", targetReference));
        } catch (IOException e) {
            return new SubstrateResult(false, e.getMessage(), -1, metadata(modelId, "ollama_http", targetReference));
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\t", "\\t");
    }

    private String extractJsonString(String json, String key) {
        if (json == null || key == null) {
            return null;
        }
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) {
            return null;
        }
        start += pattern.length();
        StringBuilder value = new StringBuilder();
        boolean escaped = false;
        for (int index = start; index < json.length(); index++) {
            char current = json.charAt(index);
            if (escaped) {
                value.append(current);
                escaped = false;
                continue;
            }
            if (current == '\\') {
                escaped = true;
                continue;
            }
            if (current == '"') {
                break;
            }
            value.append(current);
        }
        return value.toString()
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }

    private String normalizeOllamaTargetReference(String targetReference) {
        if (targetReference == null || targetReference.isBlank()) {
            return "";
        }
        String normalized = targetReference.trim();
        if (normalized.contains(":")
                || normalized.contains("/")
                || normalized.contains("\\")
                || normalized.toLowerCase(java.util.Locale.ROOT).endsWith(".gguf")) {
            return normalized;
        }
        return normalized + ":latest";
    }

    private Map<String, String> metadata(String modelId, String backend, String targetReference) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("adapter", getAdapterId());
        metadata.put("modelId", modelId == null ? "" : modelId);
        metadata.put("backend", backend == null ? "" : backend);
        metadata.put("targetReference", targetReference == null ? "" : targetReference);
        return metadata;
    }

    private Map<String, String> dockerMetadata(String modelId, String targetReference, List<String> command) {
        Map<String, String> metadata = metadata(modelId, "docker_llama_server", targetReference);
        metadata.put("executor", "process-builder");
        metadata.put("command", String.join(" ", command));
        return metadata;
    }

    private SubstrateResult withStandardDiagnostics(SubstrateResult result, long startNanos, int maxTokens, String validationStatus) {
        long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
        Map<String, String> enriched = new LinkedHashMap<>(result.getMetadata());
        enriched.put("latency.ms", Long.toString(elapsedMs));
        enriched.put("maxTokens", Integer.toString(maxTokens));
        enriched.put("validation.status", validationStatus);
        return new SubstrateResult(result.isSuccess(), result.getOutput(), result.getExitCode(), enriched);
    }

    private String truncate(String output) {
        if (output == null) {
            return "";
        }
        return output.length() > 4_000 ? output.substring(0, 4_000) : output;
    }
}
