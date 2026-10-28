package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Distills PDFs into de-duplicated, labeled local memory chunks.
 */
public final class PdfDistillationEngine {
    private static final int MIN_CHUNK_LENGTH = 120;

    private final KernelConfiguration configuration;
    private final HybridMemoryStore memoryStore;
    private final OptionalPdfTextExtractor pdfTextExtractor;

    public PdfDistillationEngine(KernelConfiguration configuration, HybridMemoryStore memoryStore) {
        this.configuration = configuration;
        this.memoryStore = memoryStore;
        this.pdfTextExtractor = new OptionalPdfTextExtractor();
    }

    public PdfDistillationReport distill(List<Path> pdfPaths) throws IOException {
        Map<Path, String> extractedTexts = new LinkedHashMap<>();
        for (Path pdfPath : pdfPaths == null ? List.<Path>of() : pdfPaths) {
            Path resolved = pdfPath.toAbsolutePath().normalize();
            String extracted = pdfTextExtractor.extractAll(resolved);
            if (extracted == null || extracted.isBlank()) {
                extracted = "Source: " + resolved + System.lineSeparator()
                    + "Type: pdf" + System.lineSeparator()
                    + "Status: extraction unavailable";
            }
            extractedTexts.put(resolved, extracted);
        }
        return distillTextSources(extractedTexts);
    }

    PdfDistillationReport distillTextSources(Map<Path, String> sourceTexts) throws IOException {
        Path distilledRoot = configuration.getStateRoot().resolve("distilled");
        Files.createDirectories(distilledRoot);

        Set<String> seenFingerprints = new LinkedHashSet<>();
        Map<PdfDistillationChunk.Label, Integer> labelCounts = new LinkedHashMap<>();
        List<PdfDistillationChunk> chunks = new ArrayList<>();
        int rawChunkCount = 0;
        int duplicateChunkCount = 0;

        for (Map.Entry<Path, String> entry : sourceTexts.entrySet()) {
            Path sourcePath = entry.getKey().toAbsolutePath().normalize();
            List<ChunkCandidate> candidates = splitIntoCandidates(sourcePath, entry.getValue());
            rawChunkCount += candidates.size();

            int chunkIndex = 1;
            for (ChunkCandidate candidate : candidates) {
                String normalized = normalizeForDedupe(candidate.content());
                if (normalized.length() < MIN_CHUNK_LENGTH) {
                    continue;
                }

                String fingerprint = fingerprint(normalized);
                if (!seenFingerprints.add(fingerprint)) {
                    duplicateChunkCount++;
                    continue;
                }

                PdfDistillationChunk.Label label = classify(candidate.content());
                labelCounts.merge(label, 1, Integer::sum);

                String sourceSlug = slugify(stripExtension(sourcePath.getFileName().toString()));
                String fileName = String.format(
                    Locale.ROOT,
                    "%04d-p%d-p%d-%s.md",
                    chunkIndex,
                    candidate.startPage(),
                    candidate.endPage(),
                    label.getSlug()
                );
                Path relativeOutputPath = Path.of(".northstar-kernel", "distilled", sourceSlug, fileName);
                Path absoluteOutputPath = configuration.getWorkspaceRoot().resolve(relativeOutputPath).normalize();
                Files.createDirectories(absoluteOutputPath.getParent());

                String rendered = renderChunk(sourcePath, label, candidate, fingerprint);
                Files.writeString(absoluteOutputPath, rendered, StandardCharsets.UTF_8);
                memoryStore.ingestText(relativeOutputPath.toString().replace('\\', '/'), rendered);

                chunks.add(new PdfDistillationChunk(
                    sourcePath,
                    relativeOutputPath,
                    label,
                    candidate.startPage(),
                    candidate.endPage(),
                    fingerprint,
                    preview(candidate.content())
                ));
                chunkIndex++;
            }
        }

        return new PdfDistillationReport(
            sourceTexts.keySet().stream().toList(),
            distilledRoot,
            rawChunkCount,
            chunks.size(),
            duplicateChunkCount,
            labelCounts,
            chunks
        );
    }

    private List<ChunkCandidate> splitIntoCandidates(Path sourcePath, String extractedText) {
        List<ChunkCandidate> candidates = new ArrayList<>();
        List<PageBlock> pages = parsePages(extractedText);
        if (pages.isEmpty()) {
            pages = List.of(new PageBlock(1, cleanChunk(extractedText)));
        }

        for (PageBlock page : pages) {
            List<String> blocks = splitParagraphs(page.text());
            for (String block : blocks) {
                String cleaned = cleanChunk(block);
                if (!cleaned.isBlank()) {
                    candidates.add(new ChunkCandidate(sourcePath, page.pageNumber(), page.pageNumber(), cleaned));
                }
            }
        }
        return candidates;
    }

    private List<PageBlock> parsePages(String extractedText) {
        List<PageBlock> pages = new ArrayList<>();
        if (extractedText == null || extractedText.isBlank()) {
            return pages;
        }

        String[] segments = extractedText.split("\\[\\[PDF_PAGE_");
        for (String segment : segments) {
            String trimmed = segment.trim();
            if (trimmed.isBlank()) {
                continue;
            }
            int markerEnd = trimmed.indexOf("]]");
            if (markerEnd > 0) {
                try {
                    int pageNumber = Integer.parseInt(trimmed.substring(0, markerEnd).trim());
                    String pageText = trimmed.substring(markerEnd + 2).trim();
                    if (!pageText.isBlank()) {
                        pages.add(new PageBlock(pageNumber, pageText));
                    }
                    continue;
                } catch (NumberFormatException ignored) {
                    // Fall through to raw text handling.
                }
            }
            pages.add(new PageBlock(pages.size() + 1, trimmed));
        }
        return pages;
    }

    private List<String> splitParagraphs(String pageText) {
        List<String> blocks = new ArrayList<>();
        if (pageText == null || pageText.isBlank()) {
            return blocks;
        }

        String[] paragraphs = pageText.split("(\\R\\s*\\R)+");
        StringBuilder accumulator = new StringBuilder();
        for (String paragraph : paragraphs) {
            String cleaned = cleanChunk(paragraph);
            if (cleaned.isBlank()) {
                continue;
            }
            if (cleaned.length() >= 260) {
                if (accumulator.length() > 0) {
                    blocks.add(accumulator.toString().trim());
                    accumulator.setLength(0);
                }
                blocks.add(cleaned);
                continue;
            }
            if (accumulator.length() > 0) {
                accumulator.append(System.lineSeparator()).append(System.lineSeparator());
            }
            accumulator.append(cleaned);
            if (accumulator.length() >= 260) {
                blocks.add(accumulator.toString().trim());
                accumulator.setLength(0);
            }
        }
        if (accumulator.length() > 0) {
            blocks.add(accumulator.toString().trim());
        }
        return blocks;
    }

    private PdfDistillationChunk.Label classify(String content) {
        String lowered = content.toLowerCase(Locale.ROOT);

        if (containsAny(lowered,
                "failure",
                "failed",
                "blocked",
                "diagnosis",
                "no actual working",
                "empirical validation failure",
                "incomplete pseudo-code",
                "does not work")) {
            return PdfDistillationChunk.Label.FAILURE;
        }
        if (containsAny(lowered,
                "test result",
                "regression",
                "smoke test",
                "benchmark",
                "validation plan",
                "verify",
                "assert",
                "unit test")) {
            return PdfDistillationChunk.Label.TEST_IDEA;
        }
        if (containsAny(lowered,
                "researching websites",
                "cross-referencing research",
                "researching",
                "archive.org",
                "reddit.com",
                "news.ycombinator.com",
                "paper",
                "study")) {
            return PdfDistillationChunk.Label.RESEARCH;
        }
        if (containsAny(lowered,
                "package ",
                "public class",
                "public interface",
                "implements ",
                "class ",
                "interface ",
                "rust simd",
                "java\n",
                "fn ",
                "struct ")) {
            return PdfDistillationChunk.Label.CODE_IDEA;
        }
        if (containsAny(lowered,
                "must ",
                "should ",
                "invariant",
                "contract",
                "validation suite",
                "taskenvelope",
                "substrateadapter",
                "single entry point",
                "air-gapped")) {
            return PdfDistillationChunk.Label.SPEC;
        }
        if (containsAny(lowered,
                "sovereign",
                "fruition",
                "lazarus engine",
                "manifestation",
                "consciousness",
                "moving forward",
                "synaptic")) {
            return PdfDistillationChunk.Label.VISION;
        }
        return PdfDistillationChunk.Label.SPEC;
    }

    private String renderChunk(
            Path sourcePath,
            PdfDistillationChunk.Label label,
            ChunkCandidate candidate,
            String fingerprint) {
        return """
            # Distilled PDF Chunk

            Source: %s
            Label: %s
            Pages: %d-%d
            Fingerprint: %s

            %s
            """.formatted(
            sourcePath,
            label.getSlug(),
            candidate.startPage(),
            candidate.endPage(),
            fingerprint,
            candidate.content().trim()
        );
    }

    private String cleanChunk(String raw) {
        if (raw == null) {
            return "";
        }
        String cleaned = raw
            .replace("\u0000", "")
            .replace("\r", "")
            .replaceAll("[ \\t]+", " ")
            .replaceAll("\\n{3,}", "\n\n")
            .trim();
        return cleaned;
    }

    private String normalizeForDedupe(String text) {
        return cleanChunk(text)
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", " ")
            .trim();
    }

    private String fingerprint(String normalized) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : bytes) {
                builder.append(String.format(Locale.ROOT, "%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private String preview(String content) {
        String compact = content.replaceAll("\\s+", " ").trim();
        return compact.length() > 220 ? compact.substring(0, 220) : compact;
    }

    private String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    private String slugify(String input) {
        String lowered = input.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        String compact = lowered.replaceAll("^-+", "").replaceAll("-+$", "");
        return compact.isBlank() ? "pdf" : compact;
    }

    private record PageBlock(int pageNumber, String text) {
    }

    private record ChunkCandidate(Path sourcePath, int startPage, int endPage, String content) {
    }
}
