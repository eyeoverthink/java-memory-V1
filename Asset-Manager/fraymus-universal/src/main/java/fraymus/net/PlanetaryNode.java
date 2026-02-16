package fraymus.net;

import fraymus.bio.NeuroQuant;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class PlanetaryNode implements Runnable {

    public final NeuroQuant self;
    private final int port;

    private final ConcurrentHashMap<String, String> peerBook = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<NeuroQuant> peerVectors = new CopyOnWriteArrayList<>();

    private final ExecutorService acceptPool;
    private final ScheduledExecutorService scheduler;

    private volatile boolean running = true;

    // Bootstrap peers (host:port)
    private final CopyOnWriteArrayList<String> seedPeers = new CopyOnWriteArrayList<>();

    public PlanetaryNode(NeuroQuant identity, int port) {
        this.self = identity;
        this.port = port;
        this.acceptPool = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() / 2), r -> {
            Thread t = new Thread(r, "PlanetaryNode-Conn");
            t.setDaemon(true);
            return t;
        });
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "PlanetaryNode-Gossip");
            t.setDaemon(true);
            return t;
        });
    }

    public int getPort() {
        return port;
    }

    public void registerPeer(NeuroQuant identity, String address) {
        if (identity == null || address == null || address.isBlank()) return;
        if (identity.id.equals(self.id)) return;
        if (peerBook.containsKey(identity.id)) return;

        peerBook.put(identity.id, address);
        peerVectors.addIfAbsent(identity);
        System.out.println("   [+] NODE ADDED: " + identity.id + " (" + address + ")");
    }

    public void addSeedPeer(String hostPort) {
        if (hostPort != null && !hostPort.isBlank()) seedPeers.add(hostPort.trim());
    }

    @Override
    public void run() {
        System.out.println("🌍 PLANETARY NODE ONLINE on Port " + port);
        System.out.println("   VECTOR ID: " + self.id);

        acceptPool.submit(this::listen);

        scheduler.scheduleAtFixedRate(this::gossip, 2, 10, TimeUnit.SECONDS);
    }

    private void listen() {
        try (ServerSocket server = new ServerSocket(port)) {
            server.setReuseAddress(true);
            while (running) {
                Socket client = server.accept();
                acceptPool.submit(() -> handleConnection(client));
            }
        } catch (IOException e) {
            System.err.println("[PlanetaryNode] listen error: " + e.getMessage());
        }
    }

    private void handleConnection(Socket socket) {
        try (socket) {
            socket.setSoTimeout(10_000);

            // IMPORTANT: set output stream first to avoid deadlock when both sides create ObjectInputStream first
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Java serialization hardening
            ObjectInputFilter filter = info -> {
                if (info.serialClass() == null) return ObjectInputFilter.Status.UNDECIDED;
                String name = info.serialClass().getName();
                if (name.startsWith("fraymus.bio.") || name.startsWith("fraymus.net.")) {
                    return ObjectInputFilter.Status.ALLOWED;
                }
                if (name.startsWith("java.lang.") || name.startsWith("java.util.")) {
                    return ObjectInputFilter.Status.ALLOWED;
                }
                return ObjectInputFilter.Status.REJECTED;
            };
            in.setObjectInputFilter(filter);

            Object request = in.readObject();

            NeuroQuant visitorIdentity = null;

            // V1 handshake message (preferred): includes listening port
            if (request instanceof Hello hello) {
                visitorIdentity = hello.identity;
                String ip = socket.getInetAddress().getHostAddress();
                String address = ip + ":" + hello.listenPort;

                System.out.println("🤝 CONTACT: " + visitorIdentity.id + " from " + address);
                registerPeer(visitorIdentity, address);

                out.writeObject(this.self);
                out.flush();
            }

            // Legacy handshake: identity only (no port)
            if (request instanceof NeuroQuant nq) {
                visitorIdentity = nq;
                String ip = socket.getInetAddress().getHostAddress();
                System.out.println("🤝 CONTACT: " + visitorIdentity.id + " from " + ip);
                registerPeer(visitorIdentity, ip);

                out.writeObject(this.self);
                out.flush();
            }

            // Keep reading messages on this connection (bootstrap SYNC, queries)
            while (true) {
                Object next;
                try {
                    next = in.readObject();
                } catch (EOFException eof) {
                    break;
                }

                if (next instanceof String s && "SYNC_PEERS".equals(s)) {
                    Map<NeuroQuant, String> export = new HashMap<>();
                    for (NeuroQuant p : peerVectors) {
                        String addr = peerBook.get(p.id);
                        if (addr != null) export.put(p, addr);
                    }
                    // include myself so joiner can also store seed with port
                    export.put(self, socket.getLocalAddress().getHostAddress() + ":" + port);
                    out.writeObject(export);
                    out.flush();
                    continue;
                }

                if (next instanceof ThoughtQuery q) {
                    handleThoughtQuery(q, out);
                    continue;
                }
            }

        } catch (Exception e) {
            // connection dropped
        }
    }

    private void handleThoughtQuery(ThoughtQuery query, ObjectOutputStream out) throws IOException {
        System.out.println("🧠 QUERY RECEIVED: " + query.query.id + " ttl=" + query.ttl + " from=" + query.originId);

        VectorRouter router = new VectorRouter();
        String next = router.routeQuery(query.query, peerVectors, peerBook);

        ThoughtQuery.Response resp = new ThoughtQuery.Response(
            self.id,
            next,
            Instant.now().toString(),
            next != null ? "FORWARD" : "NO_ROUTE"
        );
        out.writeObject(resp);
        out.flush();
    }

    /**
     * GOSSIP: Minimal V1.
     * - Connect to seeds
     * - Handshake
     * - Learn their identity (vector)
     */
    private void gossip() {
        if (seedPeers.isEmpty()) return;

        String target = seedPeers.get(ThreadLocalRandom.current().nextInt(seedPeers.size()));
        String[] parts = target.split(":");
        if (parts.length != 2) return;

        String host = parts[0];
        int p;
        try {
            p = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return;
        }

        try {
            NeuroQuant peer = handshake(host, p);
            if (peer != null) {
                System.out.println("🗺️ GOSSIP LEARNED: " + peer.id + " @ " + host);
            }
        } catch (Exception ignored) {
        }
    }

    public NeuroQuant handshake(String host, int remotePort) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, remotePort), 5_000);
            socket.setSoTimeout(10_000);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            ObjectInputFilter filter = info -> {
                if (info.serialClass() == null) return ObjectInputFilter.Status.UNDECIDED;
                String name = info.serialClass().getName();
                if (name.startsWith("fraymus.bio.") || name.startsWith("fraymus.net.")) {
                    return ObjectInputFilter.Status.ALLOWED;
                }
                if (name.startsWith("java.lang.") || name.startsWith("java.util.")) {
                    return ObjectInputFilter.Status.ALLOWED;
                }
                return ObjectInputFilter.Status.REJECTED;
            };
            in.setObjectInputFilter(filter);

            out.writeObject(new Hello(self, port));
            out.flush();

            Object resp = in.readObject();
            if (resp instanceof NeuroQuant peer) {
                registerPeer(peer, host + ":" + remotePort);
                return peer;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public ThoughtQuery.Response queryNetwork(String concept) {
        NeuroQuant q = new NeuroQuant(concept);
        ThoughtQuery req = new ThoughtQuery(self.id, q, 3);

        String next = new VectorRouter().routeQuery(q, peerVectors, peerBook);
        if (next == null) {
            return new ThoughtQuery.Response(self.id, null, Instant.now().toString(), "NO_ROUTE");
        }

        HostPort hp = HostPort.parse(next, port);

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hp.host, hp.port), 5_000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(req);
            out.flush();

            Object resp = in.readObject();
            if (resp instanceof ThoughtQuery.Response r) {
                return r;
            }
        } catch (Exception e) {
            return new ThoughtQuery.Response(self.id, null, Instant.now().toString(), "ERROR: " + e.getMessage());
        }

        return new ThoughtQuery.Response(self.id, null, Instant.now().toString(), "NO_RESPONSE");
    }

    public void stop() {
        running = false;
        scheduler.shutdownNow();
        acceptPool.shutdownNow();
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 7777;
        String concept = args.length > 1 ? args[1] : "Java AI";
        PlanetaryNode node = new PlanetaryNode(new NeuroQuant(concept), port);

        if (args.length > 2) {
            node.addSeedPeer(args[2]);
        }

        node.run();
    }

    public static final class ThoughtQuery implements Serializable {
        @Serial private static final long serialVersionUID = 1L;

        public final String originId;
        public final NeuroQuant query;
        public final int ttl;

        public ThoughtQuery(String originId, NeuroQuant query, int ttl) {
            this.originId = originId;
            this.query = query;
            this.ttl = ttl;
        }

        public static final class Response implements Serializable {
            @Serial private static final long serialVersionUID = 1L;

            public final String responderId;
            public final String nextHopIp;
            public final String timestamp;
            public final String status;

            public Response(String responderId, String nextHopIp, String timestamp, String status) {
                this.responderId = responderId;
                this.nextHopIp = nextHopIp;
                this.timestamp = timestamp;
                this.status = status;
            }

            @Override
            public String toString() {
                return "Response[" + status + "] responder=" + responderId + " next=" + nextHopIp + " @" + timestamp;
            }
        }
    }

    public static final class Hello implements Serializable {
        @Serial private static final long serialVersionUID = 1L;
        public final NeuroQuant identity;
        public final int listenPort;

        public Hello(NeuroQuant identity, int listenPort) {
            this.identity = identity;
            this.listenPort = listenPort;
        }
    }

    private record HostPort(String host, int port) {
        static HostPort parse(String s, int defaultPort) {
            if (s == null) return new HostPort("127.0.0.1", defaultPort);
            String[] parts = s.split(":");
            if (parts.length == 2) {
                try {
                    return new HostPort(parts[0], Integer.parseInt(parts[1]));
                } catch (NumberFormatException ignored) {
                }
            }
            return new HostPort(s, defaultPort);
        }
    }
}
