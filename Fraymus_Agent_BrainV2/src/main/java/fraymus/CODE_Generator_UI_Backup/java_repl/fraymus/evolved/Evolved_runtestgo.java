package repl;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class Evolved_runtestgo {

    public static boolean isLocalModel(String modelName) {
        // implementation of the original Go code
        return true; // replace with actual implementation
    }

    public static boolean isLocalServer() {
        // implementation of the original Go code
        return true; // replace with actual implementation
    }

    public static String truncateToolOutput(String output, String modelName) throws java.io.IOException {
        int modelTokenLimit = 4000;
        int defaultTokenLimit = 10000;

        if (modelName.isEmpty()) {
            modelTokenLimit = defaultTokenLimit;
        } else {
            boolean isLocalModel = isLocalModel(modelName);
            if (!isLocalModel) {
                modelTokenLimit = localModelTokenLimit;
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(output.getBytes());
        String trimmedOutput = bos.toString();

        return trimmedOutput;
    }

    private static int localModelTokenLimit = 4000;

    public static void main(String[] args) {
        // test cases and implementation of original Go code
    }
}