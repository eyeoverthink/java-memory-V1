package repl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Evolved_logrotatetestgo {

    public static void Rotate(String logFile) {
        // Create rotated log file name
        String rotatedLogFile = getRotatedLogFileName(logFile);

        try {
            // Copy original log to new rotated log file
            Files.copy(Paths.get(rotatedLogFile), Paths.get(logFile));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getRotatedLogFileName(String logFile) {
        // Get directory path of log file
        Path logDir = Paths.get(logFile).getParent();

        // Generate rotated log file name
        return logDir + File.separator + logFile;
    }
}