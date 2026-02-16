package repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Evolved_starcoder2cpp {

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}