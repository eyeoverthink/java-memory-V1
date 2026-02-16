package repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Evolved_parsergo {

    public static void main(String[] args) {
        // your code here
    }

}

class Modelfile {
    List\u003cCommand\u003e commands = new ArrayList\u003c\u003e();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Command c : commands) {
            sb.append(c.toString());
        }
        return sb.toString();
    }
}

class Command {

}