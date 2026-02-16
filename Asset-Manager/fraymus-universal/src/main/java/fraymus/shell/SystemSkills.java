package fraymus.shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class SystemSkills {

    public static void listFiles(String args) {
        File dir = new File(System.getProperty("user.dir"));
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                System.out.println(f.isDirectory() ? "📁 " + f.getName() : "📄 " + f.getName());
            }
        }
    }

    public static void printWorkingDir(String args) {
        System.out.println("📍 " + System.getProperty("user.dir"));
    }

    public static void echo(String args) {
        System.out.println("🗣️ " + args);
    }

    public static void cat(String args) {
        try {
            String[] parts = args.split("\\s+");
            String filename = parts[parts.length - 1];
            System.out.println(Files.readString(Path.of(filename)));
        } catch (Exception e) {
            System.out.println("❌ Could not read file.");
        }
    }
}
