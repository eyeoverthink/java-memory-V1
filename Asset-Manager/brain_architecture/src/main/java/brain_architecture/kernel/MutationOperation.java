package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A file-level mutation operation.
 */
public final class MutationOperation {
    public enum Type {
        WRITE_FILE,
        SEARCH_REPLACE,
        APPEND_FILE
    }

    private final Type type;
    private final Path relativePath;
    private final String content;
    private final String searchText;
    private final String replacementText;

    private MutationOperation(Type type, Path relativePath, String content, String searchText, String replacementText) {
        this.type = Objects.requireNonNull(type, "type");
        this.relativePath = Objects.requireNonNull(relativePath, "relativePath").normalize();
        this.content = content;
        this.searchText = searchText;
        this.replacementText = replacementText;
    }

    public static MutationOperation writeFile(Path relativePath, String content) {
        return new MutationOperation(Type.WRITE_FILE, relativePath, content, null, null);
    }

    public static MutationOperation appendFile(Path relativePath, String content) {
        return new MutationOperation(Type.APPEND_FILE, relativePath, content, null, null);
    }

    public static MutationOperation searchReplace(Path relativePath, String searchText, String replacementText) {
        return new MutationOperation(Type.SEARCH_REPLACE, relativePath, null, searchText, replacementText);
    }

    public Type getType() {
        return type;
    }

    public Path getRelativePath() {
        return relativePath;
    }

    public String getContent() {
        return content;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getReplacementText() {
        return replacementText;
    }
}
