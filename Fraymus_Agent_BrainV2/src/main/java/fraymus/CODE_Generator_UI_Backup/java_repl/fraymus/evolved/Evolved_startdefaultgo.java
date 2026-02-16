package repl;

import java.lang.Error;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Evolved_startdefaultgo {

    private static final Logger LOGGER = Logger.getLogger(Evolved_startdefaultgo.class.getName());

    public static void main(String[] args) {
        try {
            startApp(null, null);
        } catch (Error e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    private static void startApp(String... args) {}
}