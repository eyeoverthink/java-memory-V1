package brain_architecture.kernel;

import java.io.File;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds kernel-owned symbolic substrates and local model manifests from offline assets.
 */
public final class FoundationalSubstrateBootstrapper {
    private static final int GGUF_MAGIC = 0x46554747;
    private static final int GGUF_TYPE_UINT8 = 0;
    private static final int GGUF_TYPE_INT8 = 1;
    private static final int GGUF_TYPE_UINT16 = 2;
    private static final int GGUF_TYPE_INT16 = 3;
    private static final int GGUF_TYPE_UINT32 = 4;
    private static final int GGUF_TYPE_INT32 = 5;
    private static final int GGUF_TYPE_FLOAT32 = 6;
    private static final int GGUF_TYPE_BOOL = 7;
    private static final int GGUF_TYPE_STRING = 8;
    private static final int GGUF_TYPE_ARRAY = 9;
    private static final int GGUF_TYPE_UINT64 = 10;
    private static final int GGUF_TYPE_INT64 = 11;
    private static final int GGUF_TYPE_FLOAT64 = 12;
    private static final long GGUF_MAX_STRING_BYTES = 16_000_000L;
    private static final Pattern MODEL_TYPE_PATTERN = Pattern.compile("\"model_type\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern INT_FIELD_PATTERN = Pattern.compile("\"([A-Za-z0-9_]+)\"\\s*:\\s*(\\d+)");
    private static final Pattern VOCAB_ENTRY_PATTERN = Pattern.compile("\"((?:\\\\.|[^\"\\\\])+)\"\\s*:\\s*-?\\d+");
    private static final Pattern CONTENT_FIELD_PATTERN = Pattern.compile("\"content\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])+)\"");
    private static final Pattern OLLAMA_LAYER_PATTERN = Pattern.compile("\\{\"mediaType\":\"application/vnd\\.ollama\\.image\\.model\",\"digest\":\"sha256:([a-f0-9]+)\",\"size\":(\\d+)");
    private static final Pattern OLLAMA_FROM_PATTERN = Pattern.compile("\"from\":\"([^\"]+)\"");
    private static final Pattern LICENSE_FIELD_PATTERN = Pattern.compile("(?im)^(?:license|license_id|spdx_license)\\s*[:=]\\s*[\"']?([^\\r\\n\"']+)");
    private static final Pattern BASE_MODEL_FIELD_PATTERN = Pattern.compile("(?im)^(?:base_model|base model)\\s*[:=]\\s*[\"']?([^\\r\\n\"']+)");
    private static final Pattern DERIVED_FROM_FIELD_PATTERN = Pattern.compile("(?im)^(?:derived_from|derived from)\\s*[:=]\\s*[\"']?([^\\r\\n\"']+)");
    private static final String MODEL_ROOTS_PROPERTY = "fraynix.model.roots";
    private static final String MODEL_ROOTS_ENV = "FRAYNIX_MODEL_ROOTS";
    private static final Path LEGACY_MODEL_LOADER_PATH = Path.of(
        "Java-Memory",
        "Asset-Manager",
        "src",
        "main",
        "java",
        "fraymus",
        "llm",
        "ModelLoader.java"
    );
    private static final List<String> METADATA_TEXT_FILE_NAMES = List.of(
        "README.md",
        "README.txt",
        "README",
        "MODEL_CARD.md",
        "model_card.md",
        "LICENSE",
        "LICENSE.txt",
        "LICENSE.md",
        "COPYING"
    );

    private final KernelConfiguration configuration;

    public FoundationalSubstrateBootstrapper(KernelConfiguration configuration) {
        this.configuration = configuration;
    }

    public LocalModelSubstrateCatalog buildCatalog(List<Path> modelRoots) throws IOException {
        List<Path> roots = normalizeRoots(modelRoots);
        List<LocalModelDescriptor> descriptors = discoverModelDescriptors(roots);
        List<TokenizerDigest> tokenizerDigests = digestTokenizers(descriptors);
        List<LocalModelRuntimeDescriptor> runtimeDescriptors = discoverRuntimeDescriptors(descriptors);
        return new LocalModelSubstrateCatalog(roots, descriptors, tokenizerDigests, runtimeDescriptors);
    }

    public FoundationalSubstrateReport bootstrap(List<Path> modelRoots) throws IOException {
        LocalModelSubstrateCatalog catalog = buildCatalog(modelRoots);
        Path generatedRoot = configuration.getStateRoot().resolve(Path.of("generated", "substrates"));
        Files.createDirectories(generatedRoot);

        Path languageArtifact = generatedRoot.resolve("FOUNDATIONAL_LANGUAGE_SUBSTRATE.md");
        Path mathArtifact = generatedRoot.resolve("FOUNDATIONAL_MATH_SUBSTRATE.md");
        Path modelInventoryArtifact = generatedRoot.resolve("MODEL_SUBSTRATE_INVENTORY.md");
        Path modelManifestArtifact = generatedRoot.resolve("MODEL_SUBSTRATE_MANIFEST.tsv");
        Path tokenizerInventoryArtifact = generatedRoot.resolve("TOKENIZER_SUBSTRATE_INVENTORY.md");
        Path tokenizerManifestArtifact = generatedRoot.resolve("TOKENIZER_SUBSTRATE_MANIFEST.tsv");
        Path runtimeInventoryArtifact = generatedRoot.resolve("LOCAL_MODEL_RUNTIME_INVENTORY.md");

        Files.writeString(languageArtifact, buildLanguageSubstrate(), StandardCharsets.UTF_8);
        Files.writeString(mathArtifact, buildMathSubstrate(), StandardCharsets.UTF_8);
        Files.writeString(modelInventoryArtifact, buildModelInventory(catalog), StandardCharsets.UTF_8);
        Files.writeString(modelManifestArtifact, buildModelManifest(catalog.modelDescriptors()), StandardCharsets.UTF_8);
        Files.writeString(tokenizerInventoryArtifact, buildTokenizerInventory(catalog.tokenizerDigests()), StandardCharsets.UTF_8);
        Files.writeString(tokenizerManifestArtifact, buildTokenizerManifest(catalog.tokenizerDigests()), StandardCharsets.UTF_8);
        Files.writeString(runtimeInventoryArtifact, buildRuntimeInventory(catalog.runtimeDescriptors()), StandardCharsets.UTF_8);

        List<Path> artifactPaths = List.of(
            relativize(languageArtifact),
            relativize(mathArtifact),
            relativize(modelInventoryArtifact),
            relativize(modelManifestArtifact),
            relativize(tokenizerInventoryArtifact),
            relativize(tokenizerManifestArtifact),
            relativize(runtimeInventoryArtifact)
        );
        return new FoundationalSubstrateReport(
            artifactPaths,
            catalog.modelDescriptors(),
            catalog.tokenizerDigests(),
            catalog.runtimeDescriptors(),
            languageArtifact,
            mathArtifact,
            modelInventoryArtifact,
            modelManifestArtifact,
            tokenizerInventoryArtifact,
            tokenizerManifestArtifact,
            runtimeInventoryArtifact
        );
    }

    public List<Path> defaultModelRoots() {
        return discoverDefaultModelRoots();
    }

    private List<Path> normalizeRoots(List<Path> modelRoots) {
        Set<Path> roots = new LinkedHashSet<>();
        addExistingRoots(roots, modelRoots);
        if (!roots.isEmpty()) {
            return List.copyOf(roots);
        }

        roots.addAll(discoverDefaultModelRoots());
        if (roots.isEmpty()) {
            roots.add(configuration.getWorkspaceRoot());
        }
        return List.copyOf(roots);
    }

    private List<Path> discoverDefaultModelRoots() {
        Set<Path> roots = new LinkedHashSet<>();
        Path workspaceRoot = configuration.getWorkspaceRoot();

        addRootIfExists(roots, workspaceRoot.resolve("models"));
        addRootIfExists(roots, workspaceRoot.resolve(".ollama").resolve("models"));
        addRootIfExists(roots, workspaceRoot.resolve(".ollama"));
        addRootIfExists(roots, workspaceRoot.resolve(".cache").resolve("huggingface").resolve("hub"));

        Path userHome = resolveUserHome();
        if (userHome != null) {
            addRootIfExists(roots, userHome.resolve(".ollama").resolve("models"));
            addRootIfExists(roots, userHome.resolve(".ollama"));
            addRootIfExists(roots, userHome.resolve(".cache").resolve("huggingface").resolve("hub"));
        }

        Path localAppData = resolveEnvPath("LOCALAPPDATA");
        if (localAppData != null) {
            addRootIfExists(roots, localAppData.resolve("Ollama").resolve("models"));
            addRootIfExists(roots, localAppData.resolve("huggingface").resolve("hub"));
        }

        Path appData = resolveEnvPath("APPDATA");
        if (appData != null) {
            addRootIfExists(roots, appData.resolve("huggingface").resolve("hub"));
        }

        addConfiguredRoots(roots, System.getProperty(MODEL_ROOTS_PROPERTY));
        addConfiguredRoots(roots, System.getenv(MODEL_ROOTS_ENV));
        return List.copyOf(roots);
    }

    private void addExistingRoots(Set<Path> roots, List<Path> candidates) {
        if (candidates == null) {
            return;
        }
        for (Path candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            Path resolved = candidate.isAbsolute()
                ? candidate.toAbsolutePath().normalize()
                : configuration.getWorkspaceRoot().resolve(candidate).toAbsolutePath().normalize();
            if (Files.exists(resolved)) {
                roots.add(resolved);
            }
        }
    }

    private void addConfiguredRoots(Set<Path> roots, String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return;
        }
        String separator = Pattern.quote(File.pathSeparator);
        for (String token : rawValue.split(separator + "|,|\\r?\\n")) {
            if (token == null || token.isBlank()) {
                continue;
            }
            Path candidate = Path.of(token.trim());
            addRootIfExists(roots, candidate.isAbsolute() ? candidate : configuration.getWorkspaceRoot().resolve(candidate));
        }
    }

    private void addRootIfExists(Set<Path> roots, Path candidate) {
        if (candidate == null) {
            return;
        }
        Path resolved = candidate.toAbsolutePath().normalize();
        if (Files.exists(resolved)) {
            roots.add(resolved);
        }
    }

    private Path resolveUserHome() {
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isBlank()) {
            return null;
        }
        return Path.of(userHome).toAbsolutePath().normalize();
    }

    private Path resolveEnvPath(String envKey) {
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) {
            return null;
        }
        return Path.of(value).toAbsolutePath().normalize();
    }

    private List<LocalModelDescriptor> discoverModelDescriptors(List<Path> roots) throws IOException {
        List<LocalModelDescriptor> descriptors = new ArrayList<>();
        Set<Path> seenBundles = new LinkedHashSet<>();
        Set<Path> seenFiles = new LinkedHashSet<>();

        for (Path root : roots) {
            if (!Files.exists(root)) {
                continue;
            }
            if (Files.isRegularFile(root)) {
                collectDescriptor(root, descriptors, seenBundles, seenFiles);
                continue;
            }
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (!dir.equals(root) && shouldIgnoreDirectory(dir)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    collectDescriptor(file, descriptors, seenBundles, seenFiles);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        descriptors.sort(Comparator.comparing(descriptor -> descriptor.sourcePath().toString()));
        return List.copyOf(descriptors);
    }

    private void collectDescriptor(
            Path file,
            List<LocalModelDescriptor> descriptors,
            Set<Path> seenBundles,
            Set<Path> seenFiles) throws IOException {
        String fileName = file.getFileName() == null ? "" : file.getFileName().toString();
        String lowered = fileName.toLowerCase(Locale.ROOT);

        if ("config.json".equalsIgnoreCase(fileName)) {
            Path bundleRoot = file.getParent();
            if (bundleRoot != null && seenBundles.add(bundleRoot.toAbsolutePath().normalize())) {
                LocalModelDescriptor descriptor = inspectTransformerBundle(file);
                if (descriptor != null) {
                    descriptors.add(descriptor);
                }
            }
            return;
        }
        if ("Modelfile".equals(fileName)) {
            if (seenFiles.add(file.toAbsolutePath().normalize())) {
                descriptors.add(inspectOllamaModelfile(file));
            }
            return;
        }
        if (looksLikeOllamaManifest(file)) {
            if (seenFiles.add(file.toAbsolutePath().normalize())) {
                LocalModelDescriptor descriptor = inspectOllamaManifest(file);
                if (descriptor != null) {
                    descriptors.add(descriptor);
                }
            }
            return;
        }
        if (lowered.endsWith(".gguf")) {
            if (seenFiles.add(file.toAbsolutePath().normalize())) {
                descriptors.add(inspectGguf(file));
            }
        }
    }

    private boolean shouldIgnoreDirectory(Path dir) {
        if (dir == null) {
            return false;
        }
        if (dir.startsWith(configuration.getStateRoot())) {
            return true;
        }
        Path fileName = dir.getFileName();
        if (fileName != null && configuration.getIgnoredNames().contains(fileName.toString())) {
            return true;
        }
        return isNonModelHuggingFaceDirectory(dir);
    }

    private boolean isNonModelHuggingFaceDirectory(Path dir) {
        String normalized = dir.toAbsolutePath().normalize().toString().replace('\\', '/').toLowerCase(Locale.ROOT);
        if (!normalized.contains("/huggingface/hub/")) {
            return false;
        }
        Path fileName = dir.getFileName();
        if (fileName == null) {
            return false;
        }
        String leaf = fileName.toString().toLowerCase(Locale.ROOT);
        return leaf.startsWith("datasets--")
            || leaf.startsWith("spaces--")
            || leaf.startsWith(".locks")
            || "xet".equals(leaf)
            || "assets".equals(leaf);
    }

    private LocalModelDescriptor createDescriptor(
            String modelId,
            String family,
            Path sourcePath,
            long sizeBytes,
            String provenanceHint,
            String sourceUriHint,
            String baseModelHint,
            String derivedFromHint,
            Map<String, String> attributes) throws IOException {
        Map<String, String> normalizedAttributes = new LinkedHashMap<>(attributes == null ? Map.of() : attributes);
        String licenseId = inferLicenseId(sourcePath, normalizedAttributes);
        String provenance = normalizeValue(firstNonBlank(provenanceHint, inferProvenance(family, sourcePath)), "unknown");
        String sourceUri = normalizeValue(firstNonBlank(sourceUriHint, inferSourceUri(modelId, family, sourcePath, normalizedAttributes)), "");
        String baseModel = normalizeValue(firstNonBlank(
            baseModelHint,
            normalizedAttributes.get("base_model"),
            normalizedAttributes.get("baseModel"),
            inferMetadataField(BASE_MODEL_FIELD_PATTERN, sourcePath),
            inferBaseModel(modelId, family, sourcePath, normalizedAttributes)
        ), "");
        String derivedFrom = normalizeValue(firstNonBlank(
            derivedFromHint,
            normalizedAttributes.get("derived_from"),
            normalizedAttributes.get("from"),
            inferMetadataField(DERIVED_FROM_FIELD_PATTERN, sourcePath),
            inferDerivedFrom(modelId, family, sourcePath, normalizedAttributes, baseModel)
        ), "");
        List<String> allowedOperations = inferAllowedOperations(licenseId, family, normalizedAttributes);

        normalizedAttributes.put("license.id", licenseId);
        normalizedAttributes.put("provenance", provenance);
        if (!sourceUri.isBlank()) {
            normalizedAttributes.put("source.uri", sourceUri);
        }
        if (!baseModel.isBlank()) {
            normalizedAttributes.put("base_model", baseModel);
        }
        if (!derivedFrom.isBlank()) {
            normalizedAttributes.put("derived_from", derivedFrom);
        }
        if (!allowedOperations.isEmpty()) {
            normalizedAttributes.put("allowed.operations", String.join(",", allowedOperations));
        }

        return new LocalModelDescriptor(
            modelId,
            family,
            sourcePath,
            sizeBytes,
            licenseId,
            provenance,
            sourceUri,
            baseModel,
            derivedFrom,
            allowedOperations,
            normalizedAttributes
        );
    }

    private LocalModelDescriptor inspectTransformerBundle(Path configPath) throws IOException {
        Path bundleRoot = configPath.getParent();
        String config = readSmallText(configPath, 256_000);
        Map<String, String> attributes = new LinkedHashMap<>();
        String modelType = firstMatch(MODEL_TYPE_PATTERN, config, "unknown");
        String family = "gpt2".equalsIgnoreCase(modelType) ? "gpt2_bundle" : "transformer_bundle";
        attributes.put("model.type", modelType);
        attributes.put("has.tokenizer", Boolean.toString(Files.exists(bundleRoot.resolve("tokenizer.json"))));
        attributes.put("has.vocab", Boolean.toString(Files.exists(bundleRoot.resolve("vocab.json"))));
        attributes.put("has.merges", Boolean.toString(Files.exists(bundleRoot.resolve("merges.txt"))));
        Path weights = firstExisting(bundleRoot, List.of("pytorch_model.bin", "model.safetensors", "model.bin"));
        attributes.put("has.weights", Boolean.toString(weights != null));
        if (weights != null) {
            attributes.put("weights.file", weights.getFileName().toString());
        }
        if (weights == null
                && !Files.exists(bundleRoot.resolve("tokenizer.json"))
                && !Files.exists(bundleRoot.resolve("tokenizer.model"))
                && !Files.exists(bundleRoot.resolve("vocab.json"))
                && !Files.exists(bundleRoot.resolve("merges.txt"))) {
            return null;
        }
        captureIntField(config, attributes, "n_layer");
        captureIntField(config, attributes, "n_head");
        captureIntField(config, attributes, "n_embd");
        captureIntField(config, attributes, "vocab_size");
        attributes.put("has.tokenizer_model", Boolean.toString(Files.exists(bundleRoot.resolve("tokenizer.model"))));

        long sizeBytes = safeFileSize(configPath)
            + safeFileSize(bundleRoot.resolve("tokenizer.json"))
            + safeFileSize(bundleRoot.resolve("vocab.json"))
            + safeFileSize(bundleRoot.resolve("merges.txt"))
            + safeFileSize(bundleRoot.resolve("tokenizer.model"))
            + (weights == null ? 0L : safeFileSize(weights));
        String modelId = inferTransformerModelId(bundleRoot, configPath);
        attributes.put("model.root", bundleRoot == null ? configPath.toString() : bundleRoot.toString());
        return createDescriptor(
            modelId,
            family,
            bundleRoot == null ? configPath : bundleRoot,
            sizeBytes,
            inferTransformerProvenance(bundleRoot),
            inferTransformerSourceUri(modelId, bundleRoot),
            modelId,
            null,
            attributes
        );
    }

    private LocalModelDescriptor inspectOllamaModelfile(Path modelfile) throws IOException {
        String content = readSmallText(modelfile, 64_000);
        Map<String, String> attributes = new LinkedHashMap<>();
        for (String line : content.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            String upper = trimmed.toUpperCase(Locale.ROOT);
            if (upper.startsWith("FROM ")) {
                attributes.put("from", trimmed.substring(5).trim());
            } else if (upper.startsWith("TEMPLATE ")) {
                attributes.put("has.template", "true");
            } else if (upper.startsWith("SYSTEM ")) {
                attributes.put("has.system", "true");
            } else if (upper.startsWith("PARAMETER ")) {
                String parameterBody = trimmed.substring("PARAMETER ".length()).trim();
                int split = parameterBody.indexOf(' ');
                if (split > 0) {
                    attributes.put("parameter." + parameterBody.substring(0, split), parameterBody.substring(split + 1).trim());
                }
            }
        }
        String modelId = modelfile.getParent() != null && modelfile.getParent().getFileName() != null
            ? modelfile.getParent().getFileName().toString()
            : "ollama-modelfile";
        String fromReference = normalizeValue(attributes.get("from"), "");
        return createDescriptor(
            modelId,
            "ollama_modelfile",
            modelfile,
            safeFileSize(modelfile),
            "ollama_modelfile",
            fromReference.startsWith("http") ? fromReference : "ollama://" + normalizeOllamaTargetReference(modelId),
            null,
            fromReference,
            attributes
        );
    }

    private LocalModelDescriptor inspectOllamaManifest(Path manifestPath) throws IOException {
        String content = readSmallText(manifestPath, 256_000);
        Matcher layerMatcher = OLLAMA_LAYER_PATTERN.matcher(content);
        if (!layerMatcher.find()) {
            return null;
        }

        String digest = layerMatcher.group(1);
        long sizeBytes = Long.parseLong(layerMatcher.group(2));
        Path blobsRoot = resolveOllamaBlobsRoot(manifestPath);
        Path blobPath = blobsRoot.resolve("sha256-" + digest).toAbsolutePath().normalize();
        if (!Files.exists(blobPath) || !isGgufFile(blobPath)) {
            return null;
        }

        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("manifest.path", manifestPath.toString());
        attributes.put("blob.digest", digest);
        attributes.put("has.weights", "true");
        attributes.put("weights.file", blobPath.getFileName().toString());
        attributes.put("source.manifest", "true");

        Matcher fromMatcher = OLLAMA_FROM_PATTERN.matcher(content);
        if (fromMatcher.find()) {
            attributes.put("from", fromMatcher.group(1));
        }

        String modelId = deriveOllamaManifestModelId(manifestPath);
        return createDescriptor(
            modelId,
            "ollama_gguf_model",
            blobPath,
            sizeBytes,
            "ollama_manifest_blob",
            "ollama://" + normalizeOllamaTargetReference(modelId),
            modelId,
            attributes.get("from"),
            attributes
        );
    }

    private LocalModelDescriptor inspectGguf(Path ggufPath) throws IOException {
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("format", "gguf");
        attributes.put("valid.header", "false");
        byte[] headerBytes = readHeader(ggufPath, 24);
        if (headerBytes.length >= 24) {
            ByteBuffer header = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);
            int magic = header.getInt();
            int version = header.getInt();
            long tensorCount = header.getLong();
            long metadataCount = header.getLong();
            if (magic == GGUF_MAGIC) {
                attributes.put("valid.header", "true");
                attributes.put("gguf.version", Integer.toString(version));
                attributes.put("tensor.count", Long.toString(tensorCount));
                attributes.put("metadata.count", Long.toString(metadataCount));
            }
        }
        String fileName = ggufPath.getFileName() == null ? "model.gguf" : ggufPath.getFileName().toString();
        String modelId = fileName.endsWith(".gguf") ? fileName.substring(0, fileName.length() - 5) : fileName;
        return createDescriptor(
            modelId,
            "gguf_model",
            ggufPath,
            safeFileSize(ggufPath),
            inferGgufProvenance(ggufPath),
            inferGgufSourceUri(modelId, ggufPath),
            inferBaseModelFromFileName(modelId),
            null,
            attributes
        );
    }

    private List<TokenizerDigest> digestTokenizers(List<LocalModelDescriptor> descriptors) throws IOException {
        List<TokenizerDigest> digests = new ArrayList<>();
        for (LocalModelDescriptor descriptor : descriptors) {
            TokenizerDigest digest = null;
            if (descriptor.family().endsWith("_bundle")) {
                digest = digestBundleTokenizer(descriptor);
            } else if ("gguf_model".equalsIgnoreCase(descriptor.family()) || "ollama_gguf_model".equalsIgnoreCase(descriptor.family())) {
                digest = digestGgufTokenizer(descriptor);
            }
            if (digest != null) {
                digests.add(digest);
            }
        }
        return List.copyOf(digests);
    }

    private TokenizerDigest digestBundleTokenizer(LocalModelDescriptor descriptor) throws IOException {
        Path bundleRoot = descriptor.sourcePath();
        if (!Files.isDirectory(bundleRoot)) {
            return null;
        }

        Path vocabPath = bundleRoot.resolve("vocab.json");
        Path tokenizerPath = bundleRoot.resolve("tokenizer.json");
        Path tokenizerModelPath = bundleRoot.resolve("tokenizer.model");
        Path mergesPath = bundleRoot.resolve("merges.txt");
        if (!Files.exists(vocabPath) && !Files.exists(tokenizerPath) && !Files.exists(tokenizerModelPath)) {
            return null;
        }

        List<String> vocabTokens = new ArrayList<>();
        String format = "tokenizer-json";
        if (Files.exists(vocabPath)) {
            vocabTokens.addAll(extractVocabularyTokens(readSmallText(vocabPath, 1_000_000)));
            format = "huggingface-bpe";
        } else if (Files.exists(tokenizerPath)) {
            String tokenizerJson = readSmallText(tokenizerPath, 1_000_000);
            vocabTokens.addAll(extractVocabularyTokens(extractJsonObject(tokenizerJson, "vocab")));
            if (vocabTokens.isEmpty()) {
                vocabTokens.addAll(extractAddedTokenContents(tokenizerJson));
            }
        } else {
            format = "sentencepiece-model";
        }

        int mergeCount = Files.exists(mergesPath) ? countMergeLines(mergesPath) : 0;
        if (mergeCount > 0) {
            format = "huggingface-bpe";
        }
        int specialTokenCount = (int) vocabTokens.stream().filter(this::isSpecialToken).count();
        int maxTokenLength = vocabTokens.stream().mapToInt(String::length).max().orElse(0);
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("has.tokenizer", Boolean.toString(Files.exists(tokenizerPath)));
        attributes.put("has.vocab", Boolean.toString(Files.exists(vocabPath)));
        attributes.put("has.merges", Boolean.toString(Files.exists(mergesPath)));
        attributes.put("has.tokenizer_model", Boolean.toString(Files.exists(tokenizerModelPath)));

        return new TokenizerDigest(
            descriptor.modelId(),
            bundleRoot,
            format,
            vocabTokens.size(),
            mergeCount,
            specialTokenCount,
            maxTokenLength,
            vocabTokens.stream().limit(8).toList(),
            attributes
        );
    }

    private TokenizerDigest digestGgufTokenizer(LocalModelDescriptor descriptor) {
        if (descriptor == null || !Files.isRegularFile(descriptor.sourcePath())) {
            return null;
        }

        GgufTokenizerMetadata metadata = readGgufTokenizerMetadata(descriptor.sourcePath());
        if (metadata == null || !metadata.shouldEmitDigest()) {
            return null;
        }

        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("has.tokenizer.tokens", Boolean.toString(metadata.hasTokens()));
        attributes.put("has.tokenizer.vocab_size", Boolean.toString(metadata.hasVocabSize()));
        attributes.put("has.tokenizer.token_type", Boolean.toString(metadata.hasTokenTypes()));
        attributes.put("has.tokenizer.merges", Boolean.toString(metadata.hasMerges()));
        attributes.put("has.tokenizer.scores", Boolean.toString(metadata.hasScores()));
        attributes.put("architecture", metadata.architecture());
        attributes.put("context_length", Integer.toString(metadata.contextLength()));
        attributes.put("model_type", metadata.modelType());
        attributes.put("manifest_backed", Boolean.toString("ollama_gguf_model".equalsIgnoreCase(descriptor.family())));

        return new TokenizerDigest(
            descriptor.modelId(),
            descriptor.sourcePath(),
            "gguf-ggml",
            metadata.vocabSize(),
            0,
            metadata.specialTokenCount(),
            metadata.maxTokenLength(),
            metadata.sampleTokens(),
            attributes
        );
    }

    private GgufTokenizerMetadata readGgufTokenizerMetadata(Path path) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            GgufReader reader = new GgufReader(inputStream);
            int magic = reader.readIntLE();
            if (magic != GGUF_MAGIC) {
                return null;
            }
            reader.readIntLE(); // version
            reader.readLongLE(); // tensor count
            long metadataCount = reader.readLongLE();

            String architecture = "unknown";
            String modelType = "";
            int contextLength = 0;
            int explicitVocabSize = 0;
            boolean hasTokens = false;
            boolean hasVocabSize = false;
            boolean hasTokenTypes = false;
            boolean hasMerges = false;
            boolean hasScores = false;
            GgufTokenArraySummary tokenSummary = null;

            for (long index = 0; index < metadataCount; index++) {
                String key = reader.readString();
                int valueType = reader.readIntLE();
                switch (key) {
                    case "general.architecture" -> architecture = readStringValue(reader, valueType, architecture);
                    case "tokenizer.ggml.model" -> modelType = readStringValue(reader, valueType, modelType);
                    case "tokenizer.ggml.vocab_size" -> {
                        explicitVocabSize = Math.max(0, readNonNegativeIntValue(reader, valueType));
                        hasVocabSize = explicitVocabSize > 0;
                    }
                    case "tokenizer.ggml.tokens" -> {
                        tokenSummary = readTokenArraySummary(reader, valueType);
                        hasTokens = tokenSummary != null && tokenSummary.vocabSize() > 0;
                    }
                    case "tokenizer.ggml.token_type" -> hasTokenTypes = consumePresence(reader, valueType);
                    case "tokenizer.ggml.merges" -> hasMerges = consumePresence(reader, valueType);
                    case "tokenizer.ggml.scores" -> hasScores = consumePresence(reader, valueType);
                    default -> {
                        if (key.endsWith(".context_length") && contextLength <= 0) {
                            contextLength = Math.max(0, readNonNegativeIntValue(reader, valueType));
                        } else {
                            skipValue(reader, valueType);
                        }
                    }
                }
            }

            int vocabSize = hasTokens && tokenSummary != null ? tokenSummary.vocabSize() : explicitVocabSize;
            int specialTokenCount = tokenSummary == null ? 0 : tokenSummary.specialTokenCount();
            int maxTokenLength = tokenSummary == null ? 0 : tokenSummary.maxTokenLength();
            List<String> sampleTokens = tokenSummary == null ? List.of() : tokenSummary.sampleTokens();
            String normalizedModelType = (modelType == null || modelType.isBlank()) && (hasTokens || hasVocabSize)
                ? "gguf_unknown"
                : modelType;

            return new GgufTokenizerMetadata(
                architecture == null || architecture.isBlank() ? "unknown" : architecture,
                normalizedModelType == null || normalizedModelType.isBlank() ? "gguf_unknown" : normalizedModelType,
                contextLength,
                vocabSize,
                specialTokenCount,
                maxTokenLength,
                sampleTokens,
                hasTokens,
                hasVocabSize,
                hasTokenTypes,
                hasMerges,
                hasScores
            );
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    private String readStringValue(GgufReader reader, int valueType, String fallback) throws IOException {
        if (valueType != GGUF_TYPE_STRING) {
            skipValue(reader, valueType);
            return fallback;
        }
        return reader.readString();
    }

    private int readNonNegativeIntValue(GgufReader reader, int valueType) throws IOException {
        long value;
        switch (valueType) {
            case GGUF_TYPE_UINT8, GGUF_TYPE_INT8, GGUF_TYPE_BOOL -> value = reader.readUnsignedByteValue();
            case GGUF_TYPE_UINT16, GGUF_TYPE_INT16 -> value = reader.readUnsignedShortLE();
            case GGUF_TYPE_UINT32, GGUF_TYPE_INT32 -> value = Integer.toUnsignedLong(reader.readIntLE());
            case GGUF_TYPE_UINT64, GGUF_TYPE_INT64 -> value = reader.readLongLE();
            default -> {
                skipValue(reader, valueType);
                return 0;
            }
        }
        if (value <= 0L) {
            return 0;
        }
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    private boolean consumePresence(GgufReader reader, int valueType) throws IOException {
        if (valueType == GGUF_TYPE_ARRAY) {
            int elementType = reader.readIntLE();
            long length = reader.readLongLE();
            skipArrayValues(reader, elementType, length);
            return length > 0L;
        }
        if (valueType == GGUF_TYPE_STRING) {
            return !reader.readString().isBlank();
        }
        skipValue(reader, valueType);
        return true;
    }

    private GgufTokenArraySummary readTokenArraySummary(GgufReader reader, int valueType) throws IOException {
        if (valueType != GGUF_TYPE_ARRAY) {
            skipValue(reader, valueType);
            return null;
        }
        int elementType = reader.readIntLE();
        long length = reader.readLongLE();
        if (elementType != GGUF_TYPE_STRING) {
            skipArrayValues(reader, elementType, length);
            return null;
        }

        List<String> sampleTokens = new ArrayList<>();
        int specialTokenCount = 0;
        int maxTokenLength = 0;
        int vocabSize = 0;
        for (long index = 0; index < length; index++) {
            String token = reader.readString();
            vocabSize++;
            if (sampleTokens.size() < 8) {
                sampleTokens.add(token);
            }
            if (isSpecialToken(token)) {
                specialTokenCount++;
            }
            maxTokenLength = Math.max(maxTokenLength, token.length());
        }
        return new GgufTokenArraySummary(vocabSize, specialTokenCount, maxTokenLength, List.copyOf(sampleTokens));
    }

    private void skipArrayValues(GgufReader reader, int elementType, long length) throws IOException {
        for (long index = 0; index < length; index++) {
            skipValue(reader, elementType);
        }
    }

    private void skipValue(GgufReader reader, int valueType) throws IOException {
        switch (valueType) {
            case GGUF_TYPE_UINT8, GGUF_TYPE_INT8, GGUF_TYPE_BOOL -> reader.skipFully(1L);
            case GGUF_TYPE_UINT16, GGUF_TYPE_INT16 -> reader.skipFully(2L);
            case GGUF_TYPE_UINT32, GGUF_TYPE_INT32, GGUF_TYPE_FLOAT32 -> reader.skipFully(4L);
            case GGUF_TYPE_UINT64, GGUF_TYPE_INT64, GGUF_TYPE_FLOAT64 -> reader.skipFully(8L);
            case GGUF_TYPE_STRING -> reader.skipString();
            case GGUF_TYPE_ARRAY -> {
                int elementType = reader.readIntLE();
                long length = reader.readLongLE();
                skipArrayValues(reader, elementType, length);
            }
            default -> throw new IOException("Unsupported GGUF value type: " + valueType);
        }
    }

    private List<LocalModelRuntimeDescriptor> discoverRuntimeDescriptors(List<LocalModelDescriptor> descriptors) {
        Path ollamaExecutable = findExecutable(List.of("ollama.exe", "ollama"));
        Path llamaExecutable = findExecutable(List.of("llama-cli.exe", "llama-cli", "main.exe", "main"));
        Path dockerLlamaServer = findExecutable(List.of("com.docker.llama-server.exe", "com.docker.llama-server"));
        Path javaExecutable = findExecutable(List.of("java.exe", "java"));
        PythonTransformersProbe pythonProbe = probePythonTransformersRuntime(findExecutable(List.of("python.exe", "python3.exe", "python", "python3")));
        List<LocalModelRuntimeDescriptor> runtimes = new ArrayList<>();
        for (LocalModelDescriptor descriptor : descriptors) {
            if ("ollama_modelfile".equalsIgnoreCase(descriptor.family())) {
                String targetReference = normalizeOllamaTargetReference(descriptor.attributes().getOrDefault("from", descriptor.modelId()));
                boolean available = ollamaExecutable != null && !targetReference.isBlank();
                runtimes.add(new LocalModelRuntimeDescriptor(
                    descriptor.modelId(),
                    "ollama_cli",
                    available,
                    ollamaExecutable,
                    targetReference,
                    available ? "Local Ollama CLI is available for this modelfile." : "Ollama CLI not found or modelfile FROM target missing."
                ));
                continue;
            }
            if ("gguf_model".equalsIgnoreCase(descriptor.family()) || "ollama_gguf_model".equalsIgnoreCase(descriptor.family())) {
                if (dockerLlamaServer != null) {
                    runtimes.add(new LocalModelRuntimeDescriptor(
                        descriptor.modelId(),
                        "docker_llama_server",
                        true,
                        dockerLlamaServer,
                        descriptor.sourcePath().toString(),
                        "Docker llama.cpp-compatible server is available for native GGUF sampling."
                    ));
                    continue;
                }
                boolean available = llamaExecutable != null;
                if (!available && javaExecutable != null) {
                    runtimes.add(new LocalModelRuntimeDescriptor(
                        descriptor.modelId(),
                        "java_gguf_primitives",
                        true,
                        javaExecutable,
                        descriptor.sourcePath().toString(),
                        "Kernel-owned Java GGUF primitives are available for lower-level model loading, tokenization, and deterministic token stepping."
                    ));
                    continue;
                }
                runtimes.add(new LocalModelRuntimeDescriptor(
                    descriptor.modelId(),
                    "llama_cpp_cli",
                    available,
                    llamaExecutable,
                    descriptor.sourcePath().toString(),
                    available ? "llama.cpp-compatible CLI discovered for GGUF model." : "No llama.cpp-compatible CLI found for GGUF inference."
                ));
                continue;
            }
            if (supportsPythonTransformers(descriptor)) {
                runtimes.add(new LocalModelRuntimeDescriptor(
                    descriptor.modelId(),
                    "python_transformers",
                    pythonProbe.available(),
                    pythonProbe.executablePath(),
                    descriptor.sourcePath().toString(),
                    pythonProbe.available()
                        ? pythonProbe.selectionReason()
                        : "Bundle is compatible with local Python transformers, but " + pythonProbe.selectionReason()
                ));
                continue;
            }
            runtimes.add(new LocalModelRuntimeDescriptor(
                descriptor.modelId(),
                "metadata_only",
                false,
                null,
                descriptor.sourcePath().toString(),
                "Tokenizer and metadata are digested, but no Java 17-safe runtime is bound for this bundle yet."
            ));
        }
        return List.copyOf(runtimes);
    }

    private boolean supportsPythonTransformers(LocalModelDescriptor descriptor) {
        if (descriptor == null || !descriptor.family().endsWith("_bundle")) {
            return false;
        }
        boolean hasWeights = Boolean.parseBoolean(descriptor.attributes().getOrDefault("has.weights", "false"));
        boolean hasTokenizer = Boolean.parseBoolean(descriptor.attributes().getOrDefault("has.tokenizer", "false"))
            || Boolean.parseBoolean(descriptor.attributes().getOrDefault("has.vocab", "false"))
            || Boolean.parseBoolean(descriptor.attributes().getOrDefault("has.tokenizer_model", "false"));
        return hasWeights && hasTokenizer;
    }

    private PythonTransformersProbe probePythonTransformersRuntime(Path pythonExecutable) {
        if (pythonExecutable == null) {
            return new PythonTransformersProbe(false, null, "no Python executable was found");
        }

        String script = """
            import sys
            try:
                import transformers
                import torch
                print(transformers.__version__)
                print(torch.__version__)
            except Exception as exc:
                print(str(exc))
                raise
            """;
        try {
            Process process = new ProcessBuilder(pythonExecutable.toString(), "-c", script)
                .redirectErrorStream(true)
                .start();
            boolean finished = process.waitFor(20, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new PythonTransformersProbe(false, pythonExecutable, "probing transformers support timed out");
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (process.exitValue() != 0) {
                return new PythonTransformersProbe(false, pythonExecutable, summarizeProbeFailure(output));
            }
            List<String> lines = output.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .toList();
            String transformersVersion = lines.isEmpty() ? "unknown" : lines.get(0);
            String torchVersion = lines.size() < 2 ? "unknown" : lines.get(1);
            return new PythonTransformersProbe(
                true,
                pythonExecutable,
                "Local Python transformers runtime is available (transformers=" + transformersVersion + ", torch=" + torchVersion + ")."
            );
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new PythonTransformersProbe(false, pythonExecutable, "probing transformers support failed: " + e.getMessage());
        }
    }

    private Path findExecutable(List<String> names) {
        List<Path> searchRoots = List.of(
            configuration.getWorkspaceRoot().resolve("tools"),
            configuration.getWorkspaceRoot().resolve("bin"),
            Path.of("C:\\Program Files\\Docker\\Docker\\resources\\model-runner\\bin"),
            configuration.getWorkspaceRoot()
        );
        for (Path root : searchRoots) {
            for (String name : names) {
                Path candidate = root.resolve(name).toAbsolutePath().normalize();
                if (Files.isRegularFile(candidate)) {
                    return candidate;
                }
            }
        }

        String pathValue = System.getenv("PATH");
        if (pathValue == null || pathValue.isBlank()) {
            return null;
        }
        for (String pathEntry : pathValue.split(Pattern.quote(java.io.File.pathSeparator))) {
            if (pathEntry == null || pathEntry.isBlank()) {
                continue;
            }
            Path root = Path.of(pathEntry);
            for (String name : names) {
                Path candidate = root.resolve(name).toAbsolutePath().normalize();
                if (Files.isRegularFile(candidate)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private String summarizeProbeFailure(String output) {
        if (output == null || output.isBlank()) {
            return "transformers or torch could not be imported";
        }
        String[] lines = output.lines()
            .map(String::trim)
            .filter(line -> !line.isBlank())
            .limit(3)
            .toArray(String[]::new);
        if (lines.length == 0) {
            return "transformers or torch could not be imported";
        }
        return String.join(" | ", lines);
    }

    private String buildLanguageSubstrate() {
        StringBuilder builder = new StringBuilder();
        builder.append("# Foundational Language Substrate\n\n");
        builder.append("## Mission\n\n");
        builder.append("Provide a stable symbolic floor for future local tokenizer, encoder, speech, and response systems. This is not a trained language model. It is the typed substrate that later models can attach to.\n\n");
        builder.append("## Character Layer\n\n");
        builder.append("| Symbol | Class | Notes |\n");
        builder.append("| --- | --- | --- |\n");
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            boolean vowel = "AEIOU".indexOf(letter) >= 0;
            builder.append("| ").append(letter).append(" | letter | vowel=").append(vowel).append(" |\n");
        }
        builder.append("\n## Number Layer\n\n");
        builder.append("| Digit | Word | Parity |\n");
        builder.append("| --- | --- | --- |\n");
        String[] digitWords = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        for (int digit = 0; digit < digitWords.length; digit++) {
            builder.append("| ").append(digit).append(" | ").append(digitWords[digit]).append(" | ")
                .append((digit % 2 == 0) ? "even" : "odd").append(" |\n");
        }
        builder.append("\n## Phonetic Layer\n\n");
        builder.append("- Vowel phonemes: AA, AE, AH, AO, AW, AY, EH, ER, EY, IH, IY, OW, OY, UH, UW\n");
        builder.append("- Consonant phonemes: B, CH, D, DH, F, G, HH, JH, K, L, M, N, NG, P, R, S, SH, T, TH, V, W, Y, Z, ZH\n");
        builder.append("- Stress markers: primary, secondary, unstressed\n");
        builder.append("\n## Emphasis Layer\n\n");
        builder.append("- Continuation punctuation: comma, semicolon, colon\n");
        builder.append("- Terminal punctuation: period, exclamation, question mark\n");
        builder.append("- Emphasis modifiers: capitalization, repetition, quoting, parenthetical scope\n");
        builder.append("\n## Lexical Relation Slots\n\n");
        builder.append("- synonym\n");
        builder.append("- antonym\n");
        builder.append("- definition\n");
        builder.append("- example\n");
        builder.append("- alias\n");
        builder.append("- hypernym\n");
        builder.append("- hyponym\n");
        builder.append("- wiki-concept\n");
        builder.append("- thesaurus-cluster\n");
        builder.append("- pronunciation\n");
        builder.append("\n## Corpus Attachment Policy\n\n");
        builder.append("- Webster-like dictionaries should populate definition and pronunciation slots.\n");
        builder.append("- Thesaurus-like corpora should populate synonym, antonym, and cluster slots.\n");
        builder.append("- Wiki-like corpora should populate concept, relation, and example slots.\n");
        builder.append("- None of these corpora become authoritative on their own. They remain retrievable local evidence under kernel validation.\n");
        return builder.toString();
    }

    private String buildMathSubstrate() {
        StringBuilder builder = new StringBuilder();
        builder.append("# Foundational Math Substrate\n\n");
        builder.append("## Mission\n\n");
        builder.append("Provide deterministic symbolic math anchors for future arithmetic reasoning, tokenizer grounding, and bare-metal numeric representations.\n\n");
        builder.append("## Number Classes\n\n");
        builder.append("- natural\n");
        builder.append("- integer\n");
        builder.append("- rational\n");
        builder.append("- real\n");
        builder.append("- vector\n");
        builder.append("- matrix\n");
        builder.append("\n## Primitive Operators\n\n");
        builder.append("- add\n");
        builder.append("- subtract\n");
        builder.append("- multiply\n");
        builder.append("- divide\n");
        builder.append("- modulo\n");
        builder.append("- compare\n");
        builder.append("- equals\n");
        builder.append("- less-than\n");
        builder.append("- greater-than\n");
        builder.append("\n## Times Tables 0-12\n\n");
        builder.append("| A | B | Product |\n");
        builder.append("| --- | --- | --- |\n");
        for (int left = 0; left <= 12; left++) {
            for (int right = 0; right <= 12; right++) {
                builder.append("| ").append(left).append(" | ").append(right).append(" | ").append(left * right).append(" |\n");
            }
        }
        builder.append("\n## Numeric Representation Notes\n\n");
        builder.append("- Digits map to ASCII, byte, and integer representations.\n");
        builder.append("- Place value must remain explicit for decimal, hexadecimal, and binary forms.\n");
        builder.append("- Machine-level substrates should preserve signedness, width, and endian assumptions as explicit metadata.\n");
        return builder.toString();
    }

    private String buildModelInventory(LocalModelSubstrateCatalog catalog) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Model Substrate Inventory\n\n");
        builder.append("## Mission\n\n");
        builder.append("Abstract local offline model assets into typed kernel memory. This is a metadata-first absorption layer for GGUF, GPT-style bundles, and Ollama modelfiles.\n\n");
        builder.append("## Roots Scanned\n\n");
        for (Path root : catalog.roots()) {
            builder.append("- `").append(root).append("`\n");
        }
        builder.append("\n## Families\n\n");
        Map<String, Long> familyCounts = new LinkedHashMap<>();
        for (LocalModelDescriptor descriptor : catalog.modelDescriptors()) {
            familyCounts.merge(descriptor.family(), 1L, Long::sum);
        }
        if (familyCounts.isEmpty()) {
            builder.append("- No local model assets were discovered in the scanned roots.\n");
        } else {
            for (Map.Entry<String, Long> entry : familyCounts.entrySet()) {
                builder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        builder.append("\n## Licenses\n\n");
        appendCountSection(builder, countByLicense(catalog.modelDescriptors()));
        builder.append("\n## Provenance\n\n");
        appendCountSection(builder, countByProvenance(catalog.modelDescriptors()));
        builder.append("\n## Allowed Operations\n\n");
        appendCountSection(builder, countByAllowedOperation(catalog.modelDescriptors()));
        builder.append("\n## Assets\n\n");
        if (catalog.modelDescriptors().isEmpty()) {
            builder.append("- No descriptors generated.\n");
        } else {
            for (LocalModelDescriptor descriptor : catalog.modelDescriptors()) {
                builder.append("- `").append(descriptor.modelId()).append("` family=`").append(descriptor.family()).append("` path=`")
                    .append(descriptor.sourcePath()).append("` sizeBytes=`").append(descriptor.sizeBytes()).append("` license=`")
                    .append(descriptor.licenseId()).append("` provenance=`").append(descriptor.provenance()).append("`\n");
                if (!descriptor.sourceUri().isBlank() || !descriptor.baseModel().isBlank() || !descriptor.derivedFrom().isBlank()) {
                    builder.append("  lineage: sourceUri=`").append(descriptor.sourceUri()).append("` baseModel=`")
                        .append(descriptor.baseModel()).append("` derivedFrom=`").append(descriptor.derivedFrom()).append("`\n");
                }
                if (!descriptor.allowedOperations().isEmpty()) {
                    builder.append("  allowedOps: ").append(String.join(", ", descriptor.allowedOperations())).append("\n");
                }
                if (!descriptor.attributes().isEmpty()) {
                    builder.append("  attrs: ");
                    boolean first = true;
                    for (Map.Entry<String, String> attribute : descriptor.attributes().entrySet()) {
                        if (!first) {
                            builder.append(", ");
                        }
                        builder.append(attribute.getKey()).append('=').append(attribute.getValue());
                        first = false;
                    }
                    builder.append("\n");
                }
            }
        }
        builder.append("\n## Absorption Path\n\n");
        builder.append("1. Inspect binary or bundle headers locally.\n");
        builder.append("2. Digest tokenizer substrates and runtime bindings.\n");
        builder.append("3. Abstract them into typed descriptors.\n");
        builder.append("4. Index descriptors into hybrid kernel memory.\n");
        builder.append("5. Bind runtime adapters against those descriptors when local executables are available.\n");
        Path legacyLoader = configuration.getWorkspaceRoot().resolve(LEGACY_MODEL_LOADER_PATH).normalize();
        if (Files.exists(legacyLoader)) {
            builder.append("\n## Legacy Java Loader\n\n");
            builder.append("- Existing GGUF loader source detected at `").append(configuration.getWorkspaceRoot().relativize(legacyLoader)).append("`.\n");
            builder.append("- Kernel bootstrap remains Java 17-safe and metadata-first, so later runtime work can connect without making the verifier depend on newer foreign-memory APIs.\n");
        }
        return builder.toString();
    }

    private void appendCountSection(StringBuilder builder, Map<String, Long> counts) {
        if (counts.isEmpty()) {
            builder.append("- None discovered.\n");
            return;
        }
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            builder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
    }

    private Map<String, Long> countByLicense(List<LocalModelDescriptor> descriptors) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LocalModelDescriptor descriptor : descriptors) {
            counts.merge(descriptor.licenseId(), 1L, Long::sum);
        }
        return counts;
    }

    private Map<String, Long> countByProvenance(List<LocalModelDescriptor> descriptors) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LocalModelDescriptor descriptor : descriptors) {
            counts.merge(descriptor.provenance(), 1L, Long::sum);
        }
        return counts;
    }

    private Map<String, Long> countByAllowedOperation(List<LocalModelDescriptor> descriptors) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LocalModelDescriptor descriptor : descriptors) {
            for (String operation : descriptor.allowedOperations()) {
                counts.merge(operation, 1L, Long::sum);
            }
        }
        return counts;
    }

    private String buildTokenizerInventory(List<TokenizerDigest> tokenizerDigests) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Tokenizer Substrate Inventory\n\n");
        builder.append("## Mission\n\n");
        builder.append("Abstract local tokenizers into a typed substrate that later inference, speech, and data-building layers can reuse.\n\n");
        if (tokenizerDigests.isEmpty()) {
            builder.append("- No tokenizer digests were generated.\n");
            return builder.toString();
        }

        for (TokenizerDigest digest : tokenizerDigests) {
            builder.append("- `").append(digest.modelId()).append("` format=`").append(digest.format()).append("` vocab=`")
                .append(digest.vocabSize()).append("` merges=`").append(digest.mergeCount()).append("` specials=`")
                .append(digest.specialTokenCount()).append("` maxTokenLength=`").append(digest.maxTokenLength()).append("`\n");
            if (!digest.sampleTokens().isEmpty()) {
                builder.append("  samples: ").append(String.join(", ", digest.sampleTokens())).append("\n");
            }
        }
        return builder.toString();
    }

    private String buildRuntimeInventory(List<LocalModelRuntimeDescriptor> runtimeDescriptors) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Local Model Runtime Inventory\n\n");
        builder.append("## Mission\n\n");
        builder.append("Describe which local model assets are actually runnable through the kernel execution boundary right now.\n\n");
        if (runtimeDescriptors.isEmpty()) {
            builder.append("- No runtime bindings discovered.\n");
            return builder.toString();
        }

        for (LocalModelRuntimeDescriptor runtime : runtimeDescriptors) {
            builder.append("- `").append(runtime.modelId()).append("` backend=`").append(runtime.backend()).append("` available=`")
                .append(runtime.available()).append("` target=`").append(runtime.targetReference()).append("`\n");
            builder.append("  reason: ").append(runtime.selectionReason()).append("\n");
            if (runtime.executablePath() != null) {
                builder.append("  executable: `").append(runtime.executablePath()).append("`\n");
            }
        }
        return builder.toString();
    }

    private String buildModelManifest(List<LocalModelDescriptor> descriptors) {
        StringBuilder builder = new StringBuilder();
        builder.append("modelId\tfamily\tlicenseId\tprovenance\tsourceUri\tbaseModel\tderivedFrom\tallowedOperations\tsourcePath\tsizeBytes\tattributes\n");
        for (LocalModelDescriptor descriptor : descriptors) {
            builder.append(escapeField(descriptor.modelId())).append('\t')
                .append(escapeField(descriptor.family())).append('\t')
                .append(escapeField(descriptor.licenseId())).append('\t')
                .append(escapeField(descriptor.provenance())).append('\t')
                .append(escapeField(descriptor.sourceUri())).append('\t')
                .append(escapeField(descriptor.baseModel())).append('\t')
                .append(escapeField(descriptor.derivedFrom())).append('\t')
                .append(escapeField(String.join(",", descriptor.allowedOperations()))).append('\t')
                .append(escapeField(descriptor.sourcePath().toString())).append('\t')
                .append(descriptor.sizeBytes()).append('\t')
                .append(escapeField(serializeAttributes(descriptor.attributes())))
                .append('\n');
        }
        return builder.toString();
    }

    private String inferTransformerProvenance(Path bundleRoot) {
        if (bundleRoot != null) {
            Path parent = bundleRoot.getParent();
            if (parent != null && parent.getFileName() != null && "snapshots".equalsIgnoreCase(parent.getFileName().toString())) {
                return "huggingface_cache_bundle";
            }
        }
        return "local_transformer_bundle";
    }

    private String inferTransformerSourceUri(String modelId, Path bundleRoot) {
        if (bundleRoot != null) {
            Path parent = bundleRoot.getParent();
            if (parent != null && parent.getFileName() != null && "snapshots".equalsIgnoreCase(parent.getFileName().toString())) {
                return "huggingface://" + modelId;
            }
        }
        return "";
    }

    private String inferGgufProvenance(Path ggufPath) {
        String normalized = ggufPath.toAbsolutePath().normalize().toString().replace('\\', '/').toLowerCase(Locale.ROOT);
        if (normalized.contains("/.ollama/models/blobs/") || normalized.contains("/ollama/models/blobs/")) {
            return "ollama_blob";
        }
        return "local_gguf_blob";
    }

    private String inferGgufSourceUri(String modelId, Path ggufPath) {
        if ("ollama_blob".equals(inferGgufProvenance(ggufPath))) {
            return "ollama://blob/" + modelId;
        }
        return "";
    }

    private String inferLicenseId(Path sourcePath, Map<String, String> attributes) throws IOException {
        String explicit = firstNonBlank(
            attributes.get("license.id"),
            attributes.get("license"),
            attributes.get("model.license"),
            inferMetadataField(LICENSE_FIELD_PATTERN, sourcePath),
            inferLicenseFromContext(sourcePath)
        );
        return normalizeLicenseId(explicit);
    }

    private String inferLicenseFromContext(Path sourcePath) throws IOException {
        String context = readMetadataContext(sourcePath);
        if (context.isBlank()) {
            return "unknown";
        }
        String lowered = context.toLowerCase(Locale.ROOT);
        if (lowered.contains("llama 3 community license")) {
            return "llama3-community";
        }
        if (lowered.contains("apache license") || lowered.contains("apache-2.0") || lowered.contains("apache 2.0")) {
            return "apache-2.0";
        }
        if (lowered.contains("mit license") || lowered.contains("\nmit\n") || lowered.startsWith("mit")) {
            return "mit";
        }
        if (lowered.contains("bsd 3-clause") || lowered.contains("bsd-3-clause")) {
            return "bsd-3-clause";
        }
        if (lowered.contains("bsd 2-clause") || lowered.contains("bsd-2-clause")) {
            return "bsd-2-clause";
        }
        if (lowered.contains("openrail")) {
            return "openrail";
        }
        if (lowered.contains("cc-by-4.0") || lowered.contains("creative commons attribution 4.0")) {
            return "cc-by-4.0";
        }
        return "unknown";
    }

    private String inferMetadataField(Pattern pattern, Path sourcePath) throws IOException {
        String context = readMetadataContext(sourcePath);
        if (context.isBlank()) {
            return "";
        }
        return firstMatch(pattern, context, "");
    }

    private String readMetadataContext(Path sourcePath) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (sourcePath != null && Files.isRegularFile(sourcePath) && isMetadataTextFile(sourcePath)) {
            builder.append(readSmallText(sourcePath, 128_000)).append('\n');
        }
        for (Path directory : metadataDirectories(sourcePath)) {
            for (String fileName : METADATA_TEXT_FILE_NAMES) {
                Path candidate = directory.resolve(fileName);
                if (Files.isRegularFile(candidate)) {
                    builder.append(readSmallText(candidate, 128_000)).append('\n');
                }
            }
        }
        return builder.toString();
    }

    private List<Path> metadataDirectories(Path sourcePath) {
        Set<Path> directories = new LinkedHashSet<>();
        Path current = sourcePath == null ? null : Files.isDirectory(sourcePath) ? sourcePath : sourcePath.getParent();
        int depth = 0;
        while (current != null && depth < 3) {
            directories.add(current.toAbsolutePath().normalize());
            current = current.getParent();
            depth++;
        }
        return List.copyOf(directories);
    }

    private boolean isMetadataTextFile(Path file) throws IOException {
        if (file == null || !Files.isRegularFile(file)) {
            return false;
        }
        String fileName = file.getFileName() == null ? "" : file.getFileName().toString();
        String lowered = fileName.toLowerCase(Locale.ROOT);
        if (METADATA_TEXT_FILE_NAMES.stream().anyMatch(name -> name.equalsIgnoreCase(fileName))) {
            return true;
        }
        if ("modelfile".equals(lowered)) {
            return true;
        }
        if (lowered.endsWith(".md")
                || lowered.endsWith(".txt")
                || lowered.endsWith(".json")
                || lowered.endsWith(".yaml")
                || lowered.endsWith(".yml")) {
            return Files.size(file) <= 2_000_000L;
        }
        return false;
    }

    private String inferProvenance(String family, Path sourcePath) {
        if ("ollama_modelfile".equalsIgnoreCase(family)) {
            return "ollama_modelfile";
        }
        if ("ollama_gguf_model".equalsIgnoreCase(family)) {
            return "ollama_manifest_blob";
        }
        if (family != null && family.endsWith("_bundle")) {
            return inferTransformerProvenance(sourcePath);
        }
        if ("gguf_model".equalsIgnoreCase(family)) {
            return inferGgufProvenance(sourcePath);
        }
        return "unknown";
    }

    private String inferSourceUri(String modelId, String family, Path sourcePath, Map<String, String> attributes) {
        if ("ollama_modelfile".equalsIgnoreCase(family) || "ollama_gguf_model".equalsIgnoreCase(family)) {
            return "ollama://" + normalizeOllamaTargetReference(modelId);
        }
        if (family != null && family.endsWith("_bundle")) {
            return inferTransformerSourceUri(modelId, sourcePath);
        }
        if ("gguf_model".equalsIgnoreCase(family)) {
            return inferGgufSourceUri(modelId, sourcePath);
        }
        String referencedUri = firstNonBlank(attributes.get("source.uri"), attributes.get("source_uri"));
        return referencedUri == null ? "" : referencedUri;
    }

    private String inferBaseModel(String modelId, String family, Path sourcePath, Map<String, String> attributes) {
        if (family != null && family.endsWith("_bundle")) {
            return modelId;
        }
        String fromReference = firstNonBlank(attributes.get("from"), attributes.get("derived_from"));
        if (fromReference != null && !fromReference.isBlank() && !fromReference.contains("/") && !fromReference.contains("\\")) {
            return fromReference;
        }
        if ("ollama_gguf_model".equalsIgnoreCase(family)) {
            return modelId;
        }
        if ("gguf_model".equalsIgnoreCase(family)) {
            return inferBaseModelFromFileName(modelId);
        }
        return "";
    }

    private String inferDerivedFrom(
            String modelId,
            String family,
            Path sourcePath,
            Map<String, String> attributes,
            String baseModel) {
        String fromReference = firstNonBlank(attributes.get("from"), attributes.get("derived_from"));
        if (fromReference != null) {
            return fromReference;
        }
        if ("ollama_gguf_model".equalsIgnoreCase(family)) {
            return "ollama://" + normalizeOllamaTargetReference(modelId);
        }
        return baseModel == null ? "" : baseModel;
    }

    private String inferBaseModelFromFileName(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return "";
        }
        String normalized = modelId;
        int quantSplit = normalized.indexOf("-Q");
        if (quantSplit > 0) {
            normalized = normalized.substring(0, quantSplit);
        }
        quantSplit = normalized.indexOf(".Q");
        if (quantSplit > 0) {
            normalized = normalized.substring(0, quantSplit);
        }
        return normalized;
    }

    private List<String> inferAllowedOperations(String licenseId, String family, Map<String, String> attributes) {
        LinkedHashSet<String> operations = new LinkedHashSet<>();
        operations.add("inspect");
        operations.add("abstract");
        operations.add("benchmark");
        operations.add("route");
        operations.add("tokenize");

        String normalizedLicense = normalizeLicenseId(licenseId);
        if ("unknown".equals(normalizedLicense)) {
            operations.add("license_review_required");
            return List.copyOf(operations);
        }

        if (List.of("mit", "apache-2.0", "bsd-2-clause", "bsd-3-clause", "cc-by-4.0").contains(normalizedLicense)) {
            operations.add("quantize");
            operations.add("distill");
            operations.add("fine_tune");
            operations.add("incorporate");
            operations.add("export_metadata");
            return List.copyOf(operations);
        }

        if (normalizedLicense.contains("llama") || normalizedLicense.contains("openrail")) {
            operations.add("quantize");
            operations.add("distill");
            operations.add("fine_tune");
            operations.add("incorporate_with_notice");
            operations.add("redistribute_per_license");
            return List.copyOf(operations);
        }

        operations.add("license_review_required");
        return List.copyOf(operations);
    }

    private String normalizeLicenseId(String value) {
        String normalized = normalizeValue(value, "unknown").toLowerCase(Locale.ROOT);
        if (normalized.isBlank() || "unknown".equals(normalized)) {
            return "unknown";
        }
        if (normalized.contains("llama 3 community")) {
            return "llama3-community";
        }
        if (normalized.contains("apache")) {
            return "apache-2.0";
        }
        if (normalized.contains("mit")) {
            return "mit";
        }
        if (normalized.contains("bsd-3")) {
            return "bsd-3-clause";
        }
        if (normalized.contains("bsd-2")) {
            return "bsd-2-clause";
        }
        if (normalized.contains("openrail")) {
            return "openrail";
        }
        if (normalized.contains("cc-by-4.0")) {
            return "cc-by-4.0";
        }
        return normalized.replace(' ', '-');
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private String normalizeValue(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }

    private String buildTokenizerManifest(List<TokenizerDigest> tokenizerDigests) {
        StringBuilder builder = new StringBuilder();
        builder.append("modelId\tsourcePath\tformat\tvocabSize\tmergeCount\tspecialTokenCount\tmaxTokenLength\tsampleTokens\tattributes\n");
        for (TokenizerDigest digest : tokenizerDigests) {
            builder.append(escapeField(digest.modelId())).append('\t')
                .append(escapeField(digest.sourcePath().toString())).append('\t')
                .append(escapeField(digest.format())).append('\t')
                .append(digest.vocabSize()).append('\t')
                .append(digest.mergeCount()).append('\t')
                .append(digest.specialTokenCount()).append('\t')
                .append(digest.maxTokenLength()).append('\t')
                .append(escapeField(String.join(",", digest.sampleTokens()))).append('\t')
                .append(escapeField(serializeAttributes(digest.attributes())))
                .append('\n');
        }
        return builder.toString();
    }

    private String serializeAttributes(Map<String, String> attributes) {
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            pairs.add(entry.getKey() + "=" + entry.getValue());
        }
        return String.join(";", pairs);
    }

    private String escapeField(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    private String readSmallText(Path file, int maxBytes) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file)) {
            byte[] content = inputStream.readNBytes(Math.max(0, maxBytes));
            return new String(content, StandardCharsets.UTF_8);
        }
    }

    private byte[] readHeader(Path file, int bytes) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file)) {
            return inputStream.readNBytes(bytes);
        }
    }

    private List<String> extractVocabularyTokens(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        List<String> tokens = new ArrayList<>();
        Matcher matcher = VOCAB_ENTRY_PATTERN.matcher(json);
        while (matcher.find()) {
            tokens.add(unescapeJsonString(matcher.group(1)));
        }
        return List.copyOf(tokens);
    }

    private List<String> extractAddedTokenContents(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        List<String> tokens = new ArrayList<>();
        Matcher matcher = CONTENT_FIELD_PATTERN.matcher(json);
        while (matcher.find()) {
            tokens.add(unescapeJsonString(matcher.group(1)));
        }
        return List.copyOf(tokens);
    }

    private String extractJsonObject(String json, String key) {
        if (json == null || key == null) {
            return "";
        }
        String quotedKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(quotedKey);
        if (keyIndex < 0) {
            return "";
        }
        int braceStart = json.indexOf('{', keyIndex);
        if (braceStart < 0) {
            return "";
        }
        int depth = 0;
        boolean escaped = false;
        boolean inString = false;
        for (int index = braceStart; index < json.length(); index++) {
            char current = json.charAt(index);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (current == '\\') {
                escaped = true;
                continue;
            }
            if (current == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (current == '{') {
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth == 0) {
                    return json.substring(braceStart + 1, index);
                }
            }
        }
        return "";
    }

    private int countMergeLines(Path mergesPath) throws IOException {
        int count = 0;
        for (String line : Files.readAllLines(mergesPath, StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            count++;
        }
        return count;
    }

    private boolean isSpecialToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return token.startsWith("<") || token.startsWith("[") || token.contains("<|") || token.endsWith(">");
    }

    private String normalizeOllamaTargetReference(String targetReference) {
        if (targetReference == null || targetReference.isBlank()) {
            return "";
        }
        String normalized = targetReference.trim();
        if (normalized.contains(":")
                || normalized.contains("/")
                || normalized.contains("\\")
                || normalized.toLowerCase(Locale.ROOT).endsWith(".gguf")) {
            return normalized;
        }
        return normalized + ":latest";
    }

    private String unescapeJsonString(String value) {
        if (value == null || value.isBlank()) {
            return value == null ? "" : value;
        }
        return value
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t");
    }

    private void captureIntField(String content, Map<String, String> attributes, String fieldName) {
        Matcher matcher = INT_FIELD_PATTERN.matcher(content);
        while (matcher.find()) {
            if (fieldName.equals(matcher.group(1))) {
                attributes.put(fieldName, matcher.group(2));
                return;
            }
        }
    }

    private String firstMatch(Pattern pattern, String content, String fallback) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(matcher.groupCount());
        }
        return fallback;
    }

    private String inferTransformerModelId(Path bundleRoot, Path configPath) {
        if (bundleRoot != null) {
            Path parent = bundleRoot.getParent();
            if (parent != null && parent.getFileName() != null && "snapshots".equalsIgnoreCase(parent.getFileName().toString())) {
                Path repositoryRoot = parent.getParent();
                if (repositoryRoot != null && repositoryRoot.getFileName() != null) {
                    String repositoryName = repositoryRoot.getFileName().toString();
                    if (repositoryName.startsWith("models--")) {
                        return repositoryName.substring("models--".length()).replace("--", "/");
                    }
                }
            }
            if (bundleRoot.getFileName() != null) {
                return bundleRoot.getFileName().toString();
            }
        }
        return configPath.getFileName().toString();
    }

    private boolean looksLikeOllamaManifest(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return false;
        }
        String normalized = file.toAbsolutePath().normalize().toString().replace('\\', '/').toLowerCase(Locale.ROOT);
        return normalized.contains("/.ollama/models/manifests/")
            || normalized.contains("/ollama/models/manifests/");
    }

    private Path resolveOllamaBlobsRoot(Path manifestPath) {
        Path current = manifestPath.toAbsolutePath().normalize();
        while (current != null) {
            Path fileName = current.getFileName();
            if (fileName != null && "manifests".equalsIgnoreCase(fileName.toString())) {
                Path modelsRoot = current.getParent();
                if (modelsRoot != null) {
                    return modelsRoot.resolve("blobs").toAbsolutePath().normalize();
                }
                break;
            }
            current = current.getParent();
        }
        return manifestPath.getParent() == null
            ? Path.of(".").toAbsolutePath().normalize()
            : manifestPath.getParent().toAbsolutePath().normalize();
    }

    private String deriveOllamaManifestModelId(Path manifestPath) {
        String normalized = manifestPath.toAbsolutePath().normalize().toString().replace('\\', '/');
        String marker = "/models/manifests/registry.ollama.ai/";
        int index = normalized.toLowerCase(Locale.ROOT).indexOf(marker);
        if (index >= 0) {
            String relative = normalized.substring(index + marker.length());
            String[] segments = relative.split("/");
            if (segments.length >= 3) {
                String namespace = segments[0];
                String name = segments[1];
                String tag = segments[2];
                if ("library".equalsIgnoreCase(namespace)) {
                    return name + ":" + tag;
                }
                return namespace + "/" + name + ":" + tag;
            }
        }
        Path parent = manifestPath.getParent();
        String name = parent != null && parent.getFileName() != null ? parent.getFileName().toString() : "ollama-model";
        return name + ":" + manifestPath.getFileName();
    }

    private boolean isGgufFile(Path file) throws IOException {
        byte[] header = readHeader(file, 4);
        return header.length == 4 && "GGUF".equals(new String(header, StandardCharsets.US_ASCII));
    }

    private Path firstExisting(Path root, List<String> fileNames) {
        for (String fileName : fileNames) {
            Path candidate = root.resolve(fileName);
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private long safeFileSize(Path file) throws IOException {
        return Files.exists(file) ? Files.size(file) : 0L;
    }

    private Path relativize(Path path) {
        return configuration.getWorkspaceRoot().relativize(path.toAbsolutePath().normalize());
    }

    private record PythonTransformersProbe(boolean available, Path executablePath, String selectionReason) {
    }

    private record GgufTokenizerMetadata(
            String architecture,
            String modelType,
            int contextLength,
            int vocabSize,
            int specialTokenCount,
            int maxTokenLength,
            List<String> sampleTokens,
            boolean hasTokens,
            boolean hasVocabSize,
            boolean hasTokenTypes,
            boolean hasMerges,
            boolean hasScores) {

        private boolean shouldEmitDigest() {
            return (hasTokens && vocabSize > 0) || (hasVocabSize && vocabSize > 0);
        }
    }

    private record GgufTokenArraySummary(
            int vocabSize,
            int specialTokenCount,
            int maxTokenLength,
            List<String> sampleTokens) {
    }

    private static final class GgufReader {
        private final InputStream inputStream;

        private GgufReader(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        private int readIntLE() throws IOException {
            byte[] bytes = readBytes(4);
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }

        private long readLongLE() throws IOException {
            byte[] bytes = readBytes(8);
            return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
        }

        private int readUnsignedByteValue() throws IOException {
            int value = inputStream.read();
            if (value < 0) {
                throw new EOFException("Unexpected end of GGUF stream");
            }
            return value;
        }

        private int readUnsignedShortLE() throws IOException {
            byte[] bytes = readBytes(2);
            return Short.toUnsignedInt(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort());
        }

        private String readString() throws IOException {
            long length = readLongLE();
            if (length < 0L || length > GGUF_MAX_STRING_BYTES) {
                throw new IOException("Invalid GGUF string length: " + length);
            }
            byte[] bytes = readBytes((int) length);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        private void skipString() throws IOException {
            long length = readLongLE();
            if (length < 0L || length > GGUF_MAX_STRING_BYTES) {
                throw new IOException("Invalid GGUF string length: " + length);
            }
            skipFully(length);
        }

        private void skipFully(long count) throws IOException {
            long remaining = count;
            while (remaining > 0L) {
                long skipped = inputStream.skip(remaining);
                if (skipped <= 0L) {
                    if (inputStream.read() < 0) {
                        throw new EOFException("Unexpected end of GGUF stream");
                    }
                    remaining--;
                } else {
                    remaining -= skipped;
                }
            }
        }

        private byte[] readBytes(int length) throws IOException {
            byte[] bytes = inputStream.readNBytes(length);
            if (bytes.length != length) {
                throw new EOFException("Unexpected end of GGUF stream");
            }
            return bytes;
        }
    }
}
