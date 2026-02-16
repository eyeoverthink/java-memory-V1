package fraymus.temporal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private Gson gson;

    public TemporalArchive() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        File dir = new File(ARCHIVE_DIR);
        if (!dir.exists()) dir.mkdirs();
        System.out.println("⏳ TEMPORAL ARCHIVE ACTIVE. HISTORY IS BEING WRITTEN.");
    }

    public void preserve(String eventType, Object stateObject) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        long timestamp = Instant.now().getEpochSecond();
        TimeCapsule capsule = new TimeCapsule(id, timestamp, eventType, stateObject);
        
        // Save async to avoid blocking the main thread
        new Thread(() -> saveToDisk(capsule)).start();
    }

    private void saveToDisk(TimeCapsule capsule) {
        String filename = String.format("%s%d_%s_%s.json", 
            ARCHIVE_DIR, capsule.timestamp, capsule.type.replaceAll(" ", "_"), capsule.id);
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(capsule, writer);
            System.out.println("   [ARCHIVE] >> Fossilized: " + capsule.type);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static class TimeCapsule {
        String id;
        long timestamp;
        String type;
        Object data;
        public TimeCapsule(String id, long ts, String type, Object data) {
            this.id = id; this.timestamp = ts; this.type = type; this.data = data;
        }
    }
}