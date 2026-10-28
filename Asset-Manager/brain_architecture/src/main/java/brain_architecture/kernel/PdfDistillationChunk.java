package brain_architecture.kernel;

import java.nio.file.Path;

/**
 * A single distilled and labeled chunk derived from a PDF corpus.
 */
public final class PdfDistillationChunk {
    public enum Label {
        SPEC("spec"),
        CODE_IDEA("code-idea"),
        TEST_IDEA("test-idea"),
        FAILURE("failure"),
        RESEARCH("research"),
        VISION("vision");

        private final String slug;

        Label(String slug) {
            this.slug = slug;
        }

        public String getSlug() {
            return slug;
        }
    }

    private final Path sourcePath;
    private final Path outputPath;
    private final Label label;
    private final int startPage;
    private final int endPage;
    private final String fingerprint;
    private final String preview;

    public PdfDistillationChunk(
            Path sourcePath,
            Path outputPath,
            Label label,
            int startPage,
            int endPage,
            String fingerprint,
            String preview) {
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.label = label;
        this.startPage = startPage;
        this.endPage = endPage;
        this.fingerprint = fingerprint;
        this.preview = preview;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public Label getLabel() {
        return label;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getPreview() {
        return preview;
    }
}
