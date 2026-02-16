package repl;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Evolved_updaterdarwinh {
    private static final Logger logger = Logger.getLogger(Evolved_updaterdarwinh.class.getName());

    public void appLogInfo(String msg) {
        logger.info(msg);
    }

    public void appLogDebug(String msg) {
        logger.fine(msg);
    }

    public void goLogInfo(String msg) {
        System.out.println(msg);
    }

    public void goLogDebug(String msg) {
        System.out.println(msg);
    }

    public String getAuthorization(String authorizationPrompt, String right) {
        return authorizationPrompt + right;
    }
}