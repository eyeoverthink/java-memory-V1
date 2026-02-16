package repl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Evolved_jsongo {
    public static byte[] marshalWithSpaces(Object v) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = jsonEncoder(v).getBytes(StandardCharsets.UTF_8);
        for (byte c : b) {
            if (c == '\\') {
                out.write((char)c);
            } else if (c == ':') {
                out.write(':');
                out.write(' ');
            } else if (c == ',') {
                out.write(',');
                out.write(' ');
            }
        }
        return out.toByteArray();
    }

    public static String jsonEncoder(Object v) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(out);
        writer.write(v.toString());
        writer.flush();
        return out.toString(StandardCharsets.UTF_8);
    }
}