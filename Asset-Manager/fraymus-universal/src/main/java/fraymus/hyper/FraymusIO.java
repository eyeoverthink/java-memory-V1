package fraymus.hyper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class FraymusIO {

    private FraymusIO() {}

    public static void save(FraymusState state, Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        try (OutputStream fos = Files.newOutputStream(file);
             GZIPOutputStream gz = new GZIPOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(gz)) {
            oos.writeObject(state);
        }
    }

    public static FraymusState load(Path file) throws IOException, ClassNotFoundException {
        try (InputStream fis = Files.newInputStream(file);
             GZIPInputStream gz = new GZIPInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(gz)) {
            return (FraymusState) ois.readObject();
        }
    }
}
