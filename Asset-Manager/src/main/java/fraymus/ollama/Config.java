package fraymus.ollama;

/**
 * CONFIG - Command-line Arguments Parser
 * 
 * Supports:
 * --model <name>      Chat model (default: llama3)
 * --embed <name>      Embedding model (default: embeddinggemma)
 * --port <number>     WebSocket port (default: 8887)
 * --index <path>      Index path and exit
 */
public class Config {
    public String chatModel = "llama3";
    public String embedModel = "embeddinggemma";
    public int port = 8887;
    public String indexPath = null;

    public static Config fromArgs(String[] args) {
        Config c = new Config();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--model":
                    if (i + 1 < args.length) {
                        c.chatModel = args[++i];
                    }
                    break;
                case "--embed":
                    if (i + 1 < args.length) {
                        c.embedModel = args[++i];
                    }
                    break;
                case "--port":
                    if (i + 1 < args.length) {
                        c.port = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--index":
                    if (i + 1 < args.length) {
                        c.indexPath = args[++i];
                    }
                    break;
            }
        }
        return c;
    }

    @Override
    public String toString() {
        return "Config{chatModel='" + chatModel + "', embedModel='" + embedModel + 
               "', port=" + port + ", indexPath='" + indexPath + "'}";
    }
}
