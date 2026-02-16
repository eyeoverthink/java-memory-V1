package gemini.root;

public class Config {
    public String chatModel = "llama3";
    public String embedModel = "embeddinggemma";
    public int port = 8887;
    public String indexPath = null;

    public static Config fromArgs(String[] args) {
        Config c = new Config();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--model" -> c.chatModel = args[++i];
                case "--embed" -> c.embedModel = args[++i];
                case "--port" -> c.port = Integer.parseInt(args[++i]);
                case "--index" -> c.indexPath = args[++i];
            }
        }
        return c;
    }
}
