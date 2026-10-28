package fraymus.media;

import com.google.gson.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.sound.sampled.*;

/** Durable output jobs shared by shell, brains, AI replies, and AEON dream events. */
public final class MediaOutputService implements AutoCloseable {
    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile MediaOutputService shared;
    private final Path root, jobs, configPath;
    private final JsonObject config;
    private final ThreadPoolExecutor executor;
    private final Map<String, JsonObject> records = new ConcurrentHashMap<>();
    private volatile Consumer<JsonObject> evidence = value -> {};
    private volatile Process active;
    private volatile String activeId;
    private final Set<String> cancelled = ConcurrentHashMap.newKeySet();
    private volatile Clip playing;
    private volatile boolean automaticVoice, playback;
    private volatile String dreamMode, voice;
    private volatile JsonObject lastDream;
    private long lastDreamRender;
    private volatile boolean closed;

    public static synchronized MediaOutputService shared() {
        if (shared == null || shared.closed) {
            try { shared = new MediaOutputService(Path.of(System.getProperty("fraynix.media.config", "media/media-config.json")),
                    Path.of(System.getProperty("fraynix.media.output", "media_output"))); }
            catch (Exception error) { throw new IllegalStateException("Media setup: " + error.getMessage(), error); }
        }
        return shared;
    }

    public MediaOutputService(Path configuration, Path output) throws Exception {
        configPath = configuration.toAbsolutePath().normalize();
        root = output.toAbsolutePath().normalize();
        jobs = root.resolve("jobs");
        Files.createDirectories(jobs);
        config = JsonParser.parseString(Files.readString(configPath)).getAsJsonObject();
        automaticVoice = true;
        playback = true;
        dreamMode = "speech";
        voice = "af_heart";
        Path settings = root.resolve("settings.json");
        if (Files.isRegularFile(settings)) {
            JsonObject value = JsonParser.parseString(Files.readString(settings)).getAsJsonObject();
            automaticVoice = value.get("automatic_voice").getAsBoolean();
            playback = value.get("playback").getAsBoolean();
            dreamMode = value.get("dream_mode").getAsString();
            voice = value.get("voice").getAsString();
        }
        Path dream = root.resolve("last-dream.json");
        if (Files.isRegularFile(dream)) lastDream = JsonParser.parseString(Files.readString(dream)).getAsJsonObject();
        executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(config.has("queue_capacity") ? config.get("queue_capacity").getAsInt() : 16),
            task -> { Thread t = new Thread(task, "Fraynix-MediaOutput"); t.setDaemon(true); return t; });
        try (var directories = Files.list(jobs)) {
            for (Path directory : directories.filter(Files::isDirectory).toList()) {
                Path file = directory.resolve("job.json");
                if (!Files.isRegularFile(file)) continue;
                JsonObject record = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                String status = record.get("status").getAsString();
                if (status.equals("completed") && record.has("result")) {
                    JsonObject result = record.getAsJsonObject("result");
                    Path original = Path.of(result.get("artifact").getAsString());
                    Path relocated = directory.resolve(original.getFileName());
                    if (!Files.isRegularFile(relocated) || Files.size(relocated) == 0) {
                        record.addProperty("status", "failed");
                        record.addProperty("error", "Saved artifact is missing from this job directory");
                    } else if (!checksum(relocated).equals(result.get("sha256").getAsString())) {
                        record.addProperty("status", "failed");
                        record.addProperty("error", "Saved artifact checksum mismatch");
                    } else {
                        result.addProperty("artifact", relocated.toAbsolutePath().toString());
                        record.addProperty("output_dir", directory.toAbsolutePath().toString());
                    }
                    write(file, record);
                }
                if (status.equals("queued") || status.equals("running")) {
                    record.addProperty("status", "interrupted");
                    record.addProperty("error", "Previous process ended before completion; submit explicitly to retry");
                    write(file, record);
                }
                records.put(record.get("id").getAsString(), record);
            }
        }
    }

    public void setEvidenceSink(Consumer<JsonObject> sink) {
        evidence = Objects.requireNonNull(sink);
        // Completed artifacts are independently durable, and can repopulate a fresh runtime.
        for (JsonObject record : records.values()) {
            if ("completed".equals(record.get("status").getAsString())) remember(record);
        }
    }

    private String resolve(String value) {
        String models = System.getProperty("fraynix.models",
            System.getenv().getOrDefault("FRAYNIX_MODEL_ROOT", "F:/Fraynix-Models"));
        value = value.replace("${MODEL_ROOT}", models);
        Path path = Path.of(value);
        return (path.isAbsolute() ? path : configPath.getParent().resolve(path)).normalize().toString();
    }

    private JsonObject backend(String action) {
        JsonObject result = config.getAsJsonObject("backends").getAsJsonObject(action).deepCopy();
        for (String key : List.of("python", "model", "voices", "ffmpeg", "ffprobe", "motion_adapter")) {
            if (result.has(key)) result.addProperty(key, resolve(result.get(key).getAsString()));
        }
        return result;
    }

    public synchronized String submit(String action, String text, JsonObject source, JsonObject options) throws Exception {
        if (!List.of("speech", "image", "video", "animation").contains(action)) throw new IllegalArgumentException("Unknown media action");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Nonempty text required");
        if (closed) throw new IllegalStateException("Media service is closed");
        String id = UUID.randomUUID().toString();
        Path directory = jobs.resolve(id);
        Files.createDirectory(directory);
        JsonObject request = new JsonObject();
        request.addProperty("id", id);
        request.addProperty("action", action);
        request.addProperty("text", text);
        request.addProperty("output_dir", directory.toString());
        request.add("source", source == null ? new JsonObject() : source.deepCopy());
        JsonObject params = options == null ? new JsonObject() : options.deepCopy();
        if (!params.has("voice")) params.addProperty("voice", voice);
        request.add("options", params);
        request.add("backend_config", backend(action));
        write(directory.resolve("request.json"), request);
        JsonObject record = request.deepCopy();
        record.addProperty("created_at", Instant.now().toString());
        record.addProperty("status", "queued");
        records.put(id, record);
        write(directory.resolve("job.json"), record);
        try { executor.execute(() -> execute(id)); }
        catch (RejectedExecutionException error) { update(id, "failed", "Media queue full; retry when pending jobs complete"); }
        return id;
    }

    private void execute(String id) {
        Path directory = jobs.resolve(id);
        try {
            update(id, "running", null);
            JsonObject request = JsonParser.parseString(Files.readString(directory.resolve("request.json"))).getAsJsonObject();
            JsonObject cfg = request.getAsJsonObject("backend_config");
            ProcessBuilder builder = new ProcessBuilder(cfg.get("python").getAsString(), resolve(config.get("worker").getAsString()),
                "--request", directory.resolve("request.json").toString());
            builder.redirectErrorStream(true).redirectOutput(directory.resolve("worker.log").toFile());
            builder.environment().put("PYTHONUTF8", "1");
            builder.environment().remove("PYTHONHOME");
            builder.environment().remove("PYTHONPATH");
            builder.environment().put("PYTHONNOUSERSITE", "1");
            builder.environment().put("HF_HUB_OFFLINE", "1");
            builder.environment().put("TRANSFORMERS_OFFLINE", "1");
            Process process = builder.start();
            active = process;
            activeId = id;
            if (closed) { stopProcess(process); throw new InterruptedException("Runtime shutdown"); }
            long timeout = cfg.has("timeout_seconds") ? cfg.get("timeout_seconds").getAsLong() : 1800;
            if (!process.waitFor(timeout, TimeUnit.SECONDS)) {
                stopProcess(process);
                throw new IllegalStateException("Media worker timed out after " + timeout + " seconds");
            }
            Path resultFile = directory.resolve("result.json");
            if (!Files.isRegularFile(resultFile)) throw new IllegalStateException("Worker produced no result; see worker.log (exit " + process.exitValue() + ")");
            JsonObject result = JsonParser.parseString(Files.readString(resultFile)).getAsJsonObject();
            if (process.exitValue() != 0 || !result.get("ok").getAsBoolean())
                throw new IllegalStateException(result.has("error") ? result.get("error").getAsString() : "Worker failed");
            Path artifact = Path.of(result.get("artifact").getAsString()).toRealPath();
            if (!artifact.startsWith(directory.toRealPath()) || Files.size(artifact) == 0)
                throw new IllegalStateException("Invalid artifact path or empty output");
            String digest = checksum(artifact);
            if (!digest.equals(result.get("sha256").getAsString())) throw new IllegalStateException("Artifact checksum mismatch");
            synchronized (this) {
                JsonObject record = records.get(id).deepCopy();
                record.add("result", result);
                record.addProperty("status", "completed");
                record.addProperty("finished_at", Instant.now().toString());
                records.put(id, record);
                write(directory.resolve("job.json"), record);
                remember(record);
            }
            System.out.println("[MEDIA] " + id + " completed: " + artifact);
            if (playback && !closed && request.get("action").getAsString().equals("speech")) {
                try { play(id); }
                catch (Exception error) { System.out.println("[MEDIA] WAV saved; playback unavailable: " + error.getMessage()); }
            }
        } catch (Exception error) { update(id, closed || cancelled.contains(id) ? "interrupted" : "failed", error.toString()); }
        finally { active = null; activeId = null; }
    }

    private static String checksum(Path artifact) throws Exception {
        var sha = java.security.MessageDigest.getInstance("SHA-256");
        try (var input = Files.newInputStream(artifact)) {
            byte[] buffer = new byte[65536]; int count;
            while ((count = input.read(buffer)) >= 0) if (count > 0) sha.update(buffer, 0, count);
        }
        return HexFormat.of().formatHex(sha.digest());
    }

    private synchronized void update(String id, String status, String error) {
        JsonObject record = records.get(id).deepCopy();
        record.addProperty("status", status);
        if (error != null) record.addProperty("error", error);
        records.put(id, record);
        try { write(jobs.resolve(id).resolve("job.json"), record); }
        catch (Exception failure) { System.err.println("[MEDIA] Cannot persist job: " + failure); }
    }

    private void remember(JsonObject record) {
        try { evidence.accept(record.deepCopy()); }
        catch (Exception error) { System.err.println("[MEDIA] Artifact saved; memory indexing failed: " + error.getMessage()); }
    }

    public void onResponse(String text, String sourceName) {
        if (!automaticVoice || text == null || text.isBlank()) return;
        JsonObject source = new JsonObject();
        source.addProperty("kind", "published_response");
        source.addProperty("component", sourceName);
        try { submit("speech", text, source, new JsonObject()); }
        catch (Exception error) { System.err.println("[MEDIA] Response speech: " + error.getMessage()); }
    }

    public synchronized void onDream(Map<String, Object> event) {
        JsonObject value = JSON.toJsonTree(event).getAsJsonObject();
        value.addProperty("event_id", UUID.randomUUID().toString());
        value.addProperty("created_at", Instant.now().toString());
        value.addProperty("kind", "synthetic_association");
        lastDream = value;
        try {
            write(root.resolve("last-dream.json"), value);
            Files.writeString(root.resolve("dream-events.jsonl"), new Gson().toJson(value) + "\n", StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            // Preserve every event; media cadence is independently configurable.
            long interval = config.has("dream_interval_seconds") ? config.get("dream_interval_seconds").getAsLong() : 60;
            if (!dreamMode.equals("off") && System.currentTimeMillis() - lastDreamRender >= interval * 1000) {
                lastDreamRender = System.currentTimeMillis();
                renderDream(dreamMode);
            }
        } catch (Exception error) { System.err.println("[MEDIA] Dream event: " + error.getMessage()); }
    }

    public String renderDream(String mode) throws Exception {
        JsonObject event = lastDream;
        if (event == null) return "No dream event recorded yet; run omni sleep or omega sleep first.";
        String text = "Dream association from " + event.get("component").getAsString() + ": "
            + event.get("a").getAsString() + " relates to " + event.get("b").getAsString()
            + " as " + event.get("c").getAsString() + " relates to " + event.get("result").getAsString()
            + ". This is a generated association to explore.";
        List<String> ids = new ArrayList<>();
        for (String action : mode.equals("all") ? List.of("speech", "image", "video") : List.of(mode))
            ids.add(submit(action, text, event, new JsonObject()));
        return "Queued dream media: " + String.join(", ", ids);
    }

    public synchronized JsonObject job(String id) {
        JsonObject result = records.get(id);
        if (result == null) throw new IllegalArgumentException("Unknown job id");
        return result.deepCopy();
    }

    public JsonObject await(String id, long seconds) throws Exception {
        long until = System.nanoTime() + TimeUnit.SECONDS.toNanos(seconds);
        while (System.nanoTime() < until) {
            JsonObject value = job(id);
            if (!List.of("queued", "running").contains(value.get("status").getAsString())) return value;
            Thread.sleep(100);
        }
        return job(id);
    }

    public void play(String id) throws Exception {
        JsonObject record = job(id);
        if (!record.get("status").getAsString().equals("completed") || !record.get("action").getAsString().equals("speech"))
            throw new IllegalArgumentException("Playback requires a completed speech job");
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(Path.of(record.getAsJsonObject("result").get("artifact").getAsString()).toFile())) {
            Clip clip = AudioSystem.getClip();
            playing = clip;
            try {
                clip.open(stream);
                clip.start();
                long until = System.currentTimeMillis() + clip.getMicrosecondLength() / 1000 + 2000;
                while (clip.isOpen() && clip.getMicrosecondPosition() < clip.getMicrosecondLength() && System.currentTimeMillis() < until) Thread.sleep(50);
            } finally { clip.close(); playing = null; }
        }
    }

    public synchronized JsonObject status() {
        JsonObject value = new JsonObject();
        value.addProperty("automatic_voice", automaticVoice);
        value.addProperty("playback", playback);
        value.addProperty("voice", voice);
        value.addProperty("dream_mode", dreamMode);
        value.addProperty("pending", executor.getQueue().size());
        value.addProperty("output_directory", root.toString());
        value.addProperty("readiness_note", "Files present is not an inference test; completed jobs contain verified artifacts.");
        JsonObject backends = new JsonObject();
        for (String action : List.of("speech", "image", "video", "animation")) {
            JsonObject b = backend(action);
            boolean exists = Files.isRegularFile(Path.of(b.get("python").getAsString()));
            for (String key : List.of("model", "voices", "ffmpeg", "ffprobe", "motion_adapter")) {
                if (b.has(key)) exists &= configuredPathReady(Path.of(b.get(key).getAsString()));
            }
            b.addProperty("configured_files_present", exists);
            backends.add(action, b);
        }
        value.add("backends", backends);
        return value;
    }

    private boolean configuredPathReady(Path path) {
        try {
            if (Files.isRegularFile(path)) return Files.size(path) > 0;
            if (!Files.isDirectory(path)) return false;
            if (!Files.isRegularFile(path.resolve("model_index.json")))
                return Files.isRegularFile(path.resolve("config.json")) && Files.isRegularFile(path.resolve("diffusion_pytorch_model.safetensors"));
            JsonObject index = JsonParser.parseString(Files.readString(path.resolve("model_index.json"))).getAsJsonObject();
            for (String component : List.of("text_encoder", "unet", "transformer", "vae", "safety_checker")) {
                if (!index.has(component) || index.get(component).isJsonNull()) continue;
                Path folder = path.resolve(component);
                if (!Files.isDirectory(folder)) return false;
                Path shards = folder.resolve("model.safetensors.index.json");
                if (Files.isRegularFile(shards)) {
                    JsonObject weights = JsonParser.parseString(Files.readString(shards)).getAsJsonObject().getAsJsonObject("weight_map");
                    for (JsonElement name : weights.asMap().values()) {
                        Path file = folder.resolve(name.getAsString());
                        if (!Files.isRegularFile(file) || Files.size(file) < 1000) return false;
                    }
                } else {
                    try (var files = Files.list(folder)) {
                        if (files.noneMatch(file -> file.toString().endsWith(".safetensors") && Files.isRegularFile(file))) return false;
                    }
                }
            }
            return true;
        } catch (Exception error) { return false; }
    }

    public synchronized String command(String command) throws Exception {
        String[] parts = command.trim().split("\\s+", 3);
        String first = parts.length == 0 ? "status" : parts[0];
        if (first.equals("voice")) {
            String mode = parts.length > 1 ? parts[1] : "status";
            if (mode.equals("on") || mode.equals("off")) automaticVoice = mode.equals("on");
            else if (mode.equals("playback")) playback = parts.length > 2 && parts[2].equals("on");
            else if (mode.equals("select") && parts.length > 2) voice = parts[2];
            else if (mode.equals("say") && parts.length > 2) return submit("speech", parts[2], source("shell"), new JsonObject());
            else if (!mode.equals("status")) return help();
            saveSettings();
            return JSON.toJson(status());
        }
        if (first.equals("dream")) {
            if (parts.length > 2 && parts[1].equals("output")) {
                if (!List.of("off", "speech", "image", "video", "animation", "all").contains(parts[2])) throw new IllegalArgumentException("Unknown dream output mode");
                dreamMode = parts[2]; saveSettings(); return "Dream output: " + dreamMode;
            }
            if (parts.length > 1 && parts[1].equals("render")) return renderDream(parts.length > 2 ? parts[2] : "speech");
            return lastDream == null ? "No dream event recorded" : JSON.toJson(lastDream);
        }
        if (first.equals("media")) return command(command.length() > 5 ? command.substring(6) : "status");
        if (first.equals("status") || first.isBlank()) return JSON.toJson(status());
        if (first.equals("jobs")) return JSON.toJson(records.values().stream()
            .sorted(Comparator.comparing(x -> x.get("created_at").getAsString())).toList());
        if (first.equals("show") && parts.length > 1) return JSON.toJson(job(parts[1]));
        if (first.equals("play") && parts.length > 1) { String id = parts[1]; executor.execute(() -> { try { play(id); } catch (Exception e) { System.err.println(e.getMessage()); } }); return "Playback queued"; }
        if (first.equals("stop")) { if (playing != null) playing.close(); if (activeId != null) cancelled.add(activeId); if (active != null) stopProcess(active); return "Active output stopped"; }
        if (List.of("speech", "image", "video", "animation").contains(first) && command.contains(" "))
            return submit(first, command.substring(command.indexOf(' ') + 1), source("shell"), new JsonObject());
        return help();
    }

    private JsonObject source(String component) { JsonObject value = new JsonObject(); value.addProperty("component", component); return value; }
    private void saveSettings() throws Exception {
        JsonObject value = new JsonObject();
        value.addProperty("automatic_voice", automaticVoice); value.addProperty("playback", playback);
        value.addProperty("dream_mode", dreamMode); value.addProperty("voice", voice);
        write(root.resolve("settings.json"), value);
    }
    public static String help() { return "voice on|off|status|say <text>|playback on|off|select <voice>; media status|jobs|show <id>|play <id>|stop|speech <text>|image <prompt>|video <prompt>|animation <text>; dream output off|speech|image|video|animation|all; dream render <mode>; dream status"; }
    private static void write(Path path, JsonObject value) throws Exception {
        Path temp = path.resolveSibling(path.getFileName() + ".tmp");
        Files.writeString(temp, JSON.toJson(value), StandardCharsets.UTF_8);
        try { Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
        catch (AtomicMoveNotSupportedException e) { Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING); }
    }
    private static void stopProcess(Process process) { process.descendants().forEach(ProcessHandle::destroyForcibly); process.destroyForcibly(); }
    @Override public void close() {
        closed = true;
        if (playing != null) playing.close();
        if (active != null) stopProcess(active);
        executor.shutdownNow();
        try { executor.awaitTermination(10, TimeUnit.SECONDS); }
        catch (InterruptedException error) { Thread.currentThread().interrupt(); }
        for (String id : records.keySet()) {
            if (List.of("queued", "running").contains(job(id).get("status").getAsString())) update(id, "interrupted", "Runtime shutdown");
        }
    }
}
