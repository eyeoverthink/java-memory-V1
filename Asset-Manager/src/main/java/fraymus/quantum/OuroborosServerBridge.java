package fraymus.quantum;

import fraymus.knowledge.AkashicRecord;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * ouroboros-server start|stop|status [port]
 * Runs OuroborosQuantumServer as a CHILD PROCESS (so nothing it does can touch the FraynixBoot JVM), bound to 127.0.0.1 only,
 * default port 8765, data under ~/Fraymus_Master_System/ouroboros/server-data (override OUROBOROS_SERVER_DATA).
 * The child gets a clean working directory (the data dir), a 256 MB heap cap, and is killed when FraynixBoot exits.
 */
public class OuroborosServerBridge {
    static final int DEFAULT_PORT = 8765;
    static Process child;
    static int childPort = -1;
    static boolean hookAdded;

    static Path dataDir() {
        String e = System.getenv("OUROBOROS_SERVER_DATA");
        return Path.of(e != null && !e.isBlank() ? e : System.getProperty("user.home") + "/Fraymus_Master_System/ouroboros/server-data").toAbsolutePath().normalize();
    }

    static boolean portFree(int port) {
        try (ServerSocket s = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"))) { return true; }
        catch (Exception e) { return false; }
    }

    static String get(int port, String path) {
        try {
            HttpClient c = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
            HttpResponse<String> r = c.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).timeout(Duration.ofSeconds(3)).GET().build(), HttpResponse.BodyHandlers.ofString());
            return r.statusCode() == 200 ? r.body() : null;
        } catch (Exception e) { return null; }
    }

    /** The child runs with the data dir as its working directory, so relative classpath entries must be made absolute. */
    static String absoluteClasspath() {
        StringBuilder sb = new StringBuilder();
        for (String e : System.getProperty("java.class.path").split(java.io.File.pathSeparator)) {
            if (e.isEmpty()) continue;
            if (sb.length() > 0) sb.append(java.io.File.pathSeparator);
            sb.append(Path.of(e).toAbsolutePath().normalize());
        }
        return sb.toString();
    }

    static boolean running() { return child != null && child.isAlive(); }

    public static void run(String arg, AkashicRecord ak) {
        String[] t = (arg == null ? "" : arg.trim()).split("\\s+");
        String sub = t[0].isEmpty() ? "status" : t[0].toLowerCase();
        int port = DEFAULT_PORT;
        if (t.length > 1) {
            try { port = Integer.parseInt(t[1]); } catch (NumberFormatException e) { System.out.println("\u001B[31m[!] port must be a number\u001B[0m"); return; }
            if (port < 1024 || port > 65535) { System.out.println("\u001B[31m[!] port must be 1024-65535\u001B[0m"); return; }
        }
        switch (sub) {
            case "start" -> start(port, ak);
            case "stop" -> stop(ak);
            case "status" -> status(t.length > 1 ? port : (running() ? childPort : DEFAULT_PORT));
            default -> {
                System.out.println("ouroboros-server start [port]   start the Ouroboros HTTP API on 127.0.0.1 (default 8765)");
                System.out.println("ouroboros-server stop           stop it");
                System.out.println("ouroboros-server status [port]  running state + /api/health");
                System.out.println("Loopback only. /api/evolve works on a toy in-memory kernel; it never touches project source. Data: " + dataDir());
            }
        }
    }

    static void start(int port, AkashicRecord ak) {
        if (running()) { System.out.println("already running on 127.0.0.1:" + childPort + " (use 'ouroboros-server stop' first)"); return; }
        if (!portFree(port)) { System.out.println("\u001B[31m[!] port " + port + " on 127.0.0.1 is already in use; pick another: ouroboros-server start <port>\u001B[0m"); return; }
        try {
            Path data = dataDir();
            Files.createDirectories(data);
            String javaBin = Path.of(System.getProperty("java.home"), "bin", "java").toString();
            List<String> cmd = List.of(javaBin, "-Xmx256m", "-cp", absoluteClasspath(),
                "fraymus.quantum.OuroborosQuantumServer", "--port=" + port, "--data=" + data);
            Path log = data.resolve("server.log");
            ProcessBuilder pb = new ProcessBuilder(cmd).directory(data.toFile()).redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.appendTo(log.toFile()));
            child = pb.start();
            childPort = port;
            if (!hookAdded) { hookAdded = true; Runtime.getRuntime().addShutdownHook(new Thread(() -> { if (child != null && child.isAlive()) child.destroyForcibly(); })); }
            String health = null;
            for (int i = 0; i < 40 && health == null && child.isAlive(); i++) {
                try { Thread.sleep(250); } catch (InterruptedException e) { break; }
                health = get(port, "/api/health");
            }
            if (health == null) {
                System.out.println("\u001B[31m[!] server did not become healthy; see " + log + "\u001B[0m");
                if (child.isAlive()) child.destroyForcibly();
                child = null; return;
            }
            System.out.println("\u001B[32m[+] ouroboros-server up on 127.0.0.1:" + port + " (pid " + child.pid() + ", loopback only)\u001B[0m");
            System.out.println("    " + health);
            System.out.println("    data: " + data + "   log: " + log);
            System.out.println("    try:  curl -s http://127.0.0.1:" + port + "/api/health");
            if (ak != null) ak.addBlock("OUROBOROS_SERVER", "started on 127.0.0.1:" + port + " pid " + child.pid() + " data " + data);
        } catch (Exception e) {
            System.out.println("\u001B[31m[!] could not start server: " + e + "\u001B[0m");
        }
    }

    static void stop(AkashicRecord ak) {
        if (!running()) { System.out.println("not running (started from this FraynixBoot session)"); child = null; return; }
        long pid = child.pid();
        child.destroy();
        try { if (!child.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) child.destroyForcibly(); } catch (InterruptedException ignored) {}
        System.out.println("\u001B[32m[+] ouroboros-server stopped (pid " + pid + ", port " + childPort + " released)\u001B[0m");
        if (ak != null) ak.addBlock("OUROBOROS_SERVER", "stopped pid " + pid + " port " + childPort);
        child = null; childPort = -1;
    }

    public static void stopIfRunning(AkashicRecord ak) {
        if (running()) {
            stop(ak);
        }
    }

    static void status(int port) {
        boolean mine = running();
        String health = get(port, "/api/health");
        System.out.println("ouroboros-server: " + (mine ? "running (started by this session, pid " + child.pid() + ")" : "not started by this session")
            + " | 127.0.0.1:" + port + " " + (health != null ? "RESPONDING" : (portFree(port) ? "free" : "port busy (not responding as ouroboros)")));
        if (health != null) System.out.println("  health: " + health);
        System.out.println("  data: " + dataDir() + " | bind: 127.0.0.1 only");
    }
}

