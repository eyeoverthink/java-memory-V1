package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Optional local PDF extraction using a Python runtime when available.
 */
public final class OptionalPdfTextExtractor {
    private static final String READER_SETUP = String.join("\n",
        "import sys",
        "path = sys.argv[1]",
        "reader = None",
        "try:",
        "    from PyPDF2 import PdfReader",
        "    reader = PdfReader(path)",
        "except Exception:",
        "    try:",
        "        from pypdf import PdfReader",
        "        reader = PdfReader(path)",
        "    except Exception:",
        "        reader = None",
        "if reader is None:",
        "    print('')",
        "    raise SystemExit(0)"
    );
    private static final String SAMPLE_SCRIPT = String.join("\n",
        READER_SETUP,
        "text = ''",
        "limit = min(12, len(reader.pages))",
        "parts = []",
        "for i in range(limit):",
        "    try:",
        "        parts.append(reader.pages[i].extract_text() or '')",
        "    except Exception:",
        "        parts.append('')",
        "print('\\n'.join(parts))"
    );
    private static final String PAGE_COUNT_SCRIPT = String.join("\n",
        READER_SETUP,
        "print(len(reader.pages))"
    );
    private static final String RANGE_SCRIPT = String.join("\n",
        READER_SETUP,
        "start = int(sys.argv[2])",
        "end = int(sys.argv[3])",
        "parts = []",
        "for i in range(start, min(end, len(reader.pages))):",
        "    try:",
        "        text = reader.pages[i].extract_text() or ''",
        "    except Exception:",
        "        text = ''",
        "    if text.strip():",
        "        parts.append(f'[[PDF_PAGE_{i+1}]]\\n{text}')",
        "print('\\n\\n'.join(parts))"
    );

    public String extract(Path pdfPath) throws IOException {
        String output = runPython("python", SAMPLE_SCRIPT, 20_000, pdfPath.toAbsolutePath().toString());
        if (output != null && !output.isBlank()) {
            return output;
        }

        output = runPython("py", SAMPLE_SCRIPT, 20_000, pdfPath.toAbsolutePath().toString());
        if (output != null && !output.isBlank()) {
            return output;
        }

        return "";
    }

    public String extractAll(Path pdfPath) throws IOException {
        Integer pageCount = detectPageCount(pdfPath);
        if (pageCount == null || pageCount <= 0) {
            return extract(pdfPath);
        }

        StringBuilder builder = new StringBuilder();
        for (int start = 0; start < pageCount; start += 48) {
            int end = Math.min(pageCount, start + 48);
            String output = runPython("python", RANGE_SCRIPT, 350_000, pdfPath.toAbsolutePath().toString(), Integer.toString(start), Integer.toString(end));
            if (output == null || output.isBlank()) {
                output = runPython("py", RANGE_SCRIPT, 350_000, pdfPath.toAbsolutePath().toString(), Integer.toString(start), Integer.toString(end));
            }
            if (output != null && !output.isBlank()) {
                if (builder.length() > 0) {
                    builder.append(System.lineSeparator()).append(System.lineSeparator());
                }
                builder.append(output.trim());
            }
        }

        return builder.length() == 0 ? extract(pdfPath) : builder.toString();
    }

    private Integer detectPageCount(Path pdfPath) {
        String output = runPython("python", PAGE_COUNT_SCRIPT, 100, pdfPath.toAbsolutePath().toString());
        if (output == null || output.isBlank()) {
            output = runPython("py", PAGE_COUNT_SCRIPT, 100, pdfPath.toAbsolutePath().toString());
        }
        if (output == null || output.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(output.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String runPython(String command, String script, int maxChars, String... args) {
        try {
            java.util.List<String> commandLine = new java.util.ArrayList<>();
            commandLine.add(command);
            commandLine.add("-c");
            commandLine.add(script);
            java.util.Collections.addAll(commandLine, args);
            ProcessBuilder builder = new ProcessBuilder(commandLine);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            byte[] outputBytes = process.getInputStream().readAllBytes();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "";
            }
            String output = new String(outputBytes, StandardCharsets.UTF_8);
            return output.length() > maxChars ? output.substring(0, maxChars) : output;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        } catch (IOException e) {
            return "";
        }
    }
}
