package repl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Scanner;

public class Evolved_appgo {

    public static String runAppHandler(String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(path))
                .GET() // Added GET method
                .build();
        HttpResponse\u003cString\u003e response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body(); // Added body() method
    }
}