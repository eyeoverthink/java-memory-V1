import java.io.IOException;
import java.util.List;

class OllamaServer {
    public CompletionResponse completion(Context context, CompletionRequest request, Consumer\u003cCompletionResponse\u003e consumer) throws Exception {
        // method body
        return null;  // added missing return statement
    }

    public EmbeddingResponse embedding(Context context, String input) throws Exception {
        // method body
        return null;  // added missing return statement
    }

    private void close() throws IOException {}

    public static void main(String[] args) {
        OllamaServer server = new OllamaServer();
    }
}

class Context {

}

interface CompletionRequest {
    void request(); // define method
}

interface Consumer\u003cT\u003e {  // \u003e--- added Unicode escape
    void accept(T t);
}

class CompletionResponse {

}

class EmbeddingResponse {

}