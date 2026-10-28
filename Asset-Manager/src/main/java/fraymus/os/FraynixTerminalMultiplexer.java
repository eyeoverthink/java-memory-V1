package fraymus.os;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FRAYNIX TERMINAL MULTIPLEXER — Zero-Dependency tmux Replacement
 * ===============================================================
 *
 * Self-contained terminal session manager. No external tmux, screen,
 * or any dependency. Each session is a persistent OS process (shell)
 * that the AI or user can send commands to and read output from.
 *
 * CAPABILITIES:
 *   - Create named persistent shell sessions
 *   - Send commands to any session (writes to process stdin)
 *   - Read output from any session (scrollback buffer, last N lines)
 *   - Kill sessions
 *   - List all sessions with status
 *   - Sessions survive independently — detach/re-attach
 *
 * AI INTEGRATION:
 *   [TERMINAL: new|session_name]
 *   [TERMINAL: send|session_name|command]
 *   [TERMINAL: read|session_name]
 *   [TERMINAL: kill|session_name]
 *   [TERMINAL: list]
 *
 * This is the bridge between thought and action.
 * The AI can now compile, test, deploy, and improve its own codebase.
 */
public class FraynixTerminalMultiplexer {

    private final ConcurrentHashMap<String, TerminalSession> sessions;
    private final ExecutorService sessionExecutor;
    private final AtomicInteger totalCommandsExecuted;
    private volatile boolean running = true;

    // Maximum lines of scrollback per session
    private static final int SCROLLBACK_LINES = 500;
    // Timeout for reading command output
    private static final long READ_TIMEOUT_MS = 30_000;

    public FraynixTerminalMultiplexer() {
        this.sessions = new ConcurrentHashMap<>();
        this.sessionExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "fraynix-term");
            t.setDaemon(true);
            return t;
        });
        this.totalCommandsExecuted = new AtomicInteger(0);
    }

    // -------------------------------------------------------------------------
    // PUBLIC API
    // -------------------------------------------------------------------------

    /**
     * Create a new named terminal session with a persistent shell process.
     * Returns the session name on success, or error message.
     */
    public String newSession(String name) {
        if (name == null || name.isBlank()) return "ERROR: session name required";
        if (sessions.containsKey(name)) return "ERROR: session '" + name + "' already exists";

        try {
            TerminalSession session = new TerminalSession(name);
            sessions.put(name, session);
            return "CREATED: session '" + name + "' (pid=" + session.getPid() + ")";
        } catch (IOException e) {
            return "ERROR: failed to create session '" + name + "': " + e.getMessage();
        }
    }

    /**
     * Send a command to a session's shell. The command executes in the
     * session's persistent working directory. Returns confirmation.
     */
    public String sendCommand(String sessionName, String command) {
        TerminalSession session = sessions.get(sessionName);
        if (session == null) return "ERROR: session '" + sessionName + "' not found";
        if (session.isDead()) return "ERROR: session '" + sessionName + "' is dead";

        try {
            session.sendCommand(command);
            totalCommandsExecuted.incrementAndGet();
            return "SENT: '" + command + "' → session '" + sessionName + "'";
        } catch (IOException e) {
            return "ERROR: failed to send to '" + sessionName + "': " + e.getMessage();
        }
    }

    /**
     * Read the last N lines of output from a session's scrollback buffer.
     * Waits briefly for new output if a command was just sent.
     */
    public String readOutput(String sessionName, int lines) {
        TerminalSession session = sessions.get(sessionName);
        if (session == null) return "ERROR: session '" + sessionName + "' not found";

        // Brief wait for output to accumulate
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}

        List<String> output = session.getScrollback(lines);
        if (output.isEmpty()) {
            return session.isDead()
                ? "SESSION '" + sessionName + "' IS DEAD (exit=" + session.getExitCode() + ")"
                : "SESSION '" + sessionName + "' — no output yet";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- ").append(sessionName).append(" (last ").append(output.size()).append(" lines) ---\n");
        for (String line : output) {
            sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Kill a session, terminating its shell process.
     */
    public String killSession(String sessionName) {
        TerminalSession session = sessions.remove(sessionName);
        if (session == null) return "ERROR: session '" + sessionName + "' not found";

        session.kill();
        return "KILLED: session '" + sessionName + "'";
    }

    /**
     * List all sessions with their status.
     */
    public String listSessions() {
        if (sessions.isEmpty()) return "No active terminal sessions.";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %-8s %-10s %s\n", "NAME", "STATUS", "PID", "CMDS"));
        sb.append("-".repeat(60)).append("\n");

        for (TerminalSession s : sessions.values()) {
            sb.append(String.format("%-20s %-8s %-10s %d\n",
                s.name, s.isDead() ? "DEAD" : "ALIVE",
                s.getPid() > 0 ? String.valueOf(s.getPid()) : "?",
                s.commandCount));
        }
        return sb.toString().trim();
    }

    /**
     * Get stats for status display.
     */
    public String getStats() {
        long alive = sessions.values().stream().filter(s -> !s.isDead()).count();
        return String.format(
            "FraynixTerminalMultiplexer: %d sessions (%d alive), %d commands executed",
            sessions.size(), alive, totalCommandsExecuted.get()
        );
    }

    /**
     * Shutdown all sessions.
     */
    public void shutdown() {
        running = false;
        for (TerminalSession s : sessions.values()) {
            s.kill();
        }
        sessions.clear();
        sessionExecutor.shutdownNow();
    }

    // -------------------------------------------------------------------------
    // TERMINAL SESSION — One persistent shell process
    // -------------------------------------------------------------------------

    private class TerminalSession {
        final String name;
        final Process process;
        final BufferedWriter stdin;
        final BufferedReader stdout;
        final BufferedReader stderr;
        final File workingDir;
        final long createdAt;
        final List<String> scrollback;
        int commandCount = 0;
        volatile boolean dead = false;
        volatile int exitCode = -1;

        TerminalSession(String name) throws IOException {
            this.name = name;
            this.workingDir = new File(System.getProperty("user.dir"));
            this.createdAt = System.currentTimeMillis();
            this.scrollback = Collections.synchronizedList(new ArrayList<>());

            // Detect OS and pick shell
            String shellCmd;
            String[] shellArgs;
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("win")) {
                shellCmd = "cmd.exe";
                shellArgs = new String[]{};
            } else {
                shellCmd = "/bin/bash";
                shellArgs = new String[]{"--norc", "--noprofile"};
            }

            List<String> cmd = new ArrayList<>();
            cmd.add(shellCmd);
            cmd.addAll(Arrays.asList(shellArgs));

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(workingDir);
            pb.redirectErrorStream(false);  // Keep stderr separate

            this.process = pb.start();
            this.stdin = new BufferedWriter(new OutputStreamWriter(
                process.getOutputStream(), StandardCharsets.UTF_8));
            this.stdout = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8));
            this.stderr = new BufferedReader(new InputStreamReader(
                process.getErrorStream(), StandardCharsets.UTF_8));

            // Start reader threads
            startReader(stdout, "[stdout]");
            startReader(stderr, "[stderr]");

            // Monitor process death
            sessionExecutor.submit(() -> {
                try {
                    int code = process.waitFor();
                    this.exitCode = code;
                    this.dead = true;
                    scrollback.add("[Process exited with code " + code + "]");
                } catch (InterruptedException ignored) {}
            });

            scrollback.add("[Session '" + name + "' started — " + new Date() + "]");
        }

        private void startReader(BufferedReader reader, String prefix) {
            sessionExecutor.submit(() -> {
                try {
                    String line;
                    while (running && (line = reader.readLine()) != null) {
                        synchronized (scrollback) {
                            scrollback.add(line);
                            // Trim scrollback to max size
                            while (scrollback.size() > SCROLLBACK_LINES) {
                                scrollback.remove(0);
                            }
                        }
                    }
                } catch (IOException e) {
                    if (running) {
                        scrollback.add("[Reader error: " + e.getMessage() + "]");
                    }
                }
            });
        }

        void sendCommand(String command) throws IOException {
            if (dead) throw new IOException("Session is dead");
            stdin.write(command);
            stdin.newLine();
            stdin.flush();
            commandCount++;
            scrollback.add("$ " + command);
        }

        List<String> getScrollback(int lines) {
            synchronized (scrollback) {
                int size = scrollback.size();
                if (size == 0) return Collections.emptyList();
                int from = Math.max(0, size - lines);
                return new ArrayList<>(scrollback.subList(from, size));
            }
        }

        long getPid() {
            try {
                // Java 9+ has Process.pid()
                return process.pid();
            } catch (Exception e) {
                return -1;
            }
        }

        boolean isDead() {
            return dead || !process.isAlive();
        }

        int getExitCode() {
            return exitCode;
        }

        void kill() {
            dead = true;
            try { stdin.close(); } catch (IOException ignored) {}
            process.destroyForcibly();
            try { process.waitFor(2, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        }
    }
}
