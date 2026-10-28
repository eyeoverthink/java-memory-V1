package brain_architecture.integration.unified;

import brain_architecture.kernel.MerkleAppendOnlyJournal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Unified view of one kernel journal entry.
 */
public final class UnifiedJournalEntry {
    private final int index;
    private final long timestamp;
    private final String type;
    private final String message;
    private final Map<String, String> metadata;
    private final String entryHash;
    private final String merkleRoot;

    public UnifiedJournalEntry(
            int index,
            long timestamp,
            String type,
            String message,
            Map<String, String> metadata,
            String entryHash,
            String merkleRoot) {
        this.index = index;
        this.timestamp = timestamp;
        this.type = type == null ? "" : type;
        this.message = message == null ? "" : message;
        this.metadata = Collections.unmodifiableMap(new LinkedHashMap<>(metadata == null ? Map.of() : metadata));
        this.entryHash = entryHash == null ? "" : entryHash;
        this.merkleRoot = merkleRoot == null ? "" : merkleRoot;
    }

    public static UnifiedJournalEntry fromJournalEntry(MerkleAppendOnlyJournal.JournalEntry entry) {
        if (entry == null) {
            return new UnifiedJournalEntry(-1, 0L, "", "", Map.of(), "", "");
        }
        return new UnifiedJournalEntry(
            entry.index(),
            entry.timestamp(),
            entry.type(),
            entry.message(),
            entry.metadata(),
            entry.entryHash(),
            entry.merkleRoot()
        );
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getEntryHash() {
        return entryHash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }
}
