package fraymus.temporal;

import java.io.File;
import java.io.FileWriter;
import java.time.Instant;
import java.util.UUID;

/**
 * THE TEMPORAL ARCHIVE
 * "Every version is a victory."
 */
public class TemporalArchive {

    private static final String ARCHIVE_DIR = "fraymus_history/";

    public TemporalArchive() {
        File dir = new File(ARCHIVE_DIR);
        if (!dir.exists()) dir.mkdirs();
        System.out.println("⏳ TEMPORAL ARCHIVE ACTIVE. HISTORY IS BEING WRITTEN.");
    }

    public void preserve(String eventType, Object stateObject) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        long timestamp = Instant.now().getEpochSecond();
        
        new Thread(() -> saveToDisk(id, timestamp, eventType, stateObject)).start();
    }

    private void saveToDisk(String id, long timestamp, String type, Object data) {
        String filename = String.format("%s%d_%s_%s.txt", 
            ARCHIVE_DIR, timestamp, type.replaceAll(" ", "_"), id);
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("ID: " + id + "\n");
            writer.write("Timestamp: " + timestamp + "\n");
            writer.write("Type: " + type + "\n");
            writer.write("Data: " + data.toString() + "\n");
            System.out.println("   [ARCHIVE] >> Fossilized: " + type);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
