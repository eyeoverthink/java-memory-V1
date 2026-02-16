package repl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.CompletableFuture;
import java.util.Optional;

public class Evolved_webviewdts {
    public static interface ImageData {
        String filename();
        String path();
        String dataURL(); // base64 encoded file data
    }

    public static interface MenuItem {
        String label();
        Boolean enabled();
        Boolean separator();
    }

    public static interface WebviewAPI {
        CompletableFuture\u003cImageData\u003e loadImageAsync();
        CompletableFuture\u003cList\u003cImageData\u003e\u003e loadImagesAsync();
        CompletableFuture\u003cString\u003e getApiDataAsync();
        CompletableFuture\u003cString\u003e getOtherDataAsync();
    }

    public static void main(String[] args) {}
}