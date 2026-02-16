package io.fraymus.ai.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DOCUMENT PROCESSOR - Text Extraction & Chunking
 * 
 * Handles PDF, TXT, MD, code files
 */
public class DocumentProcessor {

    public String readFileToText(String filePath) throws Exception {
        Path p = Path.of(filePath);
        String name = p.getFileName().toString().toLowerCase();

        if (name.endsWith(".pdf")) {
            return readPdf(filePath);
        }
        return Files.readString(p, StandardCharsets.UTF_8);
    }

    private String readPdf(String filePath) throws Exception {
        try (PDDocument doc = org.apache.pdfbox.Loader.loadPDF(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            return text == null ? "" : text;
        }
    }

    public String cleanse(String raw) {
        if (raw == null) return "";
        String clean = raw.replaceAll("[^\\x09\\x0A\\x0D\\x20-\\x7E]", " ");
        clean = clean.replaceAll("\\s+", " ").trim();
        return clean;
    }

    public List<String> chunk(String clean, int chunkSize, int overlap) {
        if (clean == null) return List.of();
        if (clean.length() <= chunkSize) return List.of(clean);

        List<String> chunks = new ArrayList<>();
        int i = 0;
        while (i < clean.length()) {
            int end = Math.min(clean.length(), i + chunkSize);
            String part = clean.substring(i, end);
            chunks.add(part);
            i = end - overlap;
            if (i < 0) i = 0;
            if (end == clean.length()) break;
        }
        return chunks;
    }

    public List<Path> walkIndexableFiles(String root, Set<String> allowedExt) throws Exception {
        Path base = Path.of(root);
        if (!Files.exists(base)) return List.of();

        return Files.walk(base)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    String n = p.getFileName().toString().toLowerCase();
                    int dot = n.lastIndexOf('.');
                    if (dot < 0) return false;
                    String ext = n.substring(dot + 1);
                    return allowedExt.contains(ext);
                })
                .collect(Collectors.toList());
    }
}
