package progress;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Evolved_stepbargo {
    private String message;
    private int total;
    private int current;

    public Evolved_stepbargo(String message, int total) {
        this.message = message;
        this.total = total;
        this.current = 0;
    }

    public void setCurrent(int current) {
        if (current \u003c 0 || current \u003e total) {
            throw new IllegalArgumentException();
        }
        this.current = current;
    }
}