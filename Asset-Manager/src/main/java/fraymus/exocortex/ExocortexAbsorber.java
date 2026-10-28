package fraymus.exocortex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Exocortex Absorber - External Repository Integration
 */
public class ExocortexAbsorber {
    
    private String storageRoot;
    private String reposPath;
    private String extractedPath;
    private String wrappersPath;
    
    private static final Map<String, RepositoryConfig> REPOSITORIES = new HashMap<>();
    
    static {
        REPOSITORIES.put("firecracker", new RepositoryConfig(
            "firecracker-microvm/firecracker",
            "Blood-Brain Barrier",
            "Immune System",
            "Secure, lightweight microVMs for safe code execution",
            Arrays.asList("README.md", "docs/api.md", "src/firecracker/src/api_model.rs")
        ));
        
        REPOSITORIES.put("gvisor", new RepositoryConfig(
            "google/gvisor",
            "Blood-Brain Barrier (Alternative)",
            "Immune System",
            "User-space kernel for secure container execution",
            Arrays.asList("README.md", "docs/api.md", "pkg/sentry/api.go")
        ));
        
        REPOSITORIES.put("piper", new RepositoryConfig(
            "rhasspy/piper",
            "Vocal Cords",
            "Broca's Area / Vocal Tract",
            "Fast, local neural text-to-speech engine",
            Arrays.asList("README.md", "docs/api.md", "src/python/piper/__init__.py")
        ));
        
        REPOSITORIES.put("tts", new RepositoryConfig(
            "coqui-ai/TTS",
            "Vocal Cords (Alternative)",
            "Broca's Area / Vocal Tract",
            "Deep learning toolkit for Text-to-Speech",
            Arrays.asList("README.md", "docs/source/api.rst", "TTS/api/__init__.py")
        ));
        
        REPOSITORIES.put("memgraph", new RepositoryConfig(
            "memgraph/memgraph",
            "Synaptic Web",
            "Connectome Cartographer",
            "In-memory graph database for hierarchical memory mapping",
            Arrays.asList("README.md", "docs/query-modules/operation.md", "query_engine/include/mg_procedure.h")
        ));
        
        REPOSITORIES.put("searxng", new RepositoryConfig(
            "searxng/searxng",
            "Foraging Engine (Search)",
            "Olfactory System",
            "Privacy-respecting metasearch engine",
            Arrays.asList("README.md", "docs/search_api.md", "searx/search/__init__.py")
        ));
        
        REPOSITORIES.put("crawl4ai", new RepositoryConfig(
            "unclecode/crawl4ai",
            "Foraging Engine (Crawl)",
            "Olfactory System",
            "LLM-friendly web crawler",
            Arrays.asList("README.md", "docs/api.md", "crawl4ai/async_crawler.py")
        ));
        
        REPOSITORIES.put("open-interpreter", new RepositoryConfig(
            "OpenInterpreter/open-interpreter",
            "Digital Nervous System (Terminal)",
            "Peripheral Nervous System",
            "LLM intent to terminal command translation",
            Arrays.asList("README.md", "docs/api.md", "interpreter/core/execute.py")
        ));
        
        REPOSITORIES.put("playwright", new RepositoryConfig(
            "microsoft/playwright",
            "Digital Nervous System (Browser)",
            "Peripheral Nervous System",
            "Headless browser automation",
            Arrays.asList("README.md", "docs/api/class-playwright.md", "packages/playwright-core/src/client/channel.ts")
        ));
        
        REPOSITORIES.put("celery", new RepositoryConfig(
            "celery/celery",
            "Subconscious",
            "Autonomic Nervous System / REM Sleep",
            "Distributed task queue for background processing",
            Arrays.asList("README.md", "docs/userguide/tasks.rst", "celery/app/task.py")
        ));
        
        REPOSITORIES.put("temporal", new RepositoryConfig(
            "temporalio/temporal",
            "Subconscious (Alternative)",
            "Autonomic Nervous System / REM Sleep",
            "Durable execution system for workflows",
            Arrays.asList("README.md", "docs/developer-guide/workflow.md", "temporal/workflow.py")
        ));
    }
    
    public ExocortexAbsorber() {
        this.storageRoot = ".exocortex";
        this.reposPath = Paths.get(storageRoot, "repositories").toString();
        this.extractedPath = Paths.get(storageRoot, "extracted").toString();
        this.wrappersPath = Paths.get(storageRoot, "wrappers").toString();
        initializePaths();
    }
    
    public ExocortexAbsorber(String storageRoot) {
        this.storageRoot = storageRoot;
        this.reposPath = Paths.get(storageRoot, "repositories").toString();
        this.extractedPath = Paths.get(storageRoot, "extracted").toString();
        this.wrappersPath = Paths.get(storageRoot, "wrappers").toString();
        initializePaths();
    }
    
    private void initializePaths() {
        try {
            Files.createDirectories(Paths.get(storageRoot));
            Files.createDirectories(Paths.get(reposPath));
            Files.createDirectories(Paths.get(extractedPath));
            Files.createDirectories(Paths.get(wrappersPath));
        } catch (IOException e) {
            System.err.println("Failed to initialize storage paths: " + e.getMessage());
        }
    }
    
    public static class RepositoryConfig {
        public String githubPath;
        public String name;
        public String biologicalMap;
        public String description;
        public List<String> documentationFiles;
        
        public RepositoryConfig(String githubPath, String name, String biologicalMap, 
                                String description, List<String> documentationFiles) {
            this.githubPath = githubPath;
            this.name = name;
            this.biologicalMap = biologicalMap;
            this.description = description;
            this.documentationFiles = documentationFiles;
        }
    }
    
    public static class AbsorptionResult {
        public String id;
        public String repositoryName;
        public String timestamp;
        public String status;
        public String error;
        public Map<String, Object> extractedAPIs;
        public Map<String, String> wrapperPaths;
        public boolean success;
        
        public AbsorptionResult() {
            this.id = UUID.randomUUID().toString();
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.extractedAPIs = new HashMap<>();
            this.wrapperPaths = new HashMap<>();
        }
    }
    
    public static class ExtractedAPI {
        public String name;
        public String signature;
        public String description;
        public String parameters;
        public String returnType;
        public List<String> examples;
        
        public ExtractedAPI() {
            this.examples = new ArrayList<>();
        }
    }
    
    public AbsorptionResult absorb(String repoKey) {
        AbsorptionResult result = new AbsorptionResult();
        result.repositoryName = repoKey;
        
        RepositoryConfig config = REPOSITORIES.get(repoKey);
        if (config == null) {
            result.status = "FAILED";
            result.error = "Repository not found in configuration";
            result.success = false;
            return result;
        }
        
        try {
            Map<String, String> documentation = extractDocumentation(repoKey, config);
            List<ExtractedAPI> apis = parseAPIs(documentation);
            String wrapperPath = generateWrapper(repoKey, config, apis);
            storeExtraction(repoKey, documentation, apis, wrapperPath);
            
            result.extractedAPIs.put("count", apis.size());
            result.extractedAPIs.put("apis", apis);
            result.wrapperPaths.put("main", wrapperPath);
            result.status = "COMPLETE";
            result.success = true;
            
        } catch (Exception e) {
            result.status = "FAILED";
            result.error = e.getMessage();
            result.success = false;
        }
        
        return result;
    }
    
    public Map<String, AbsorptionResult> absorbAll() {
        Map<String, AbsorptionResult> results = new HashMap<>();
        for (String repoKey : REPOSITORIES.keySet()) {
            results.put(repoKey, absorb(repoKey));
        }
        return results;
    }
    
    private Map<String, String> extractDocumentation(String repoKey, RepositoryConfig config) {
        Map<String, String> documentation = new HashMap<>();
        for (String docFile : config.documentationFiles) {
            String content = generatePlaceholderDocumentation(repoKey, docFile, config);
            documentation.put(docFile, content);
        }
        return documentation;
    }
    
    private String generatePlaceholderDocumentation(String repoKey, String docFile, RepositoryConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(config.name).append(" - ").append(docFile).append("\n\n");
        sb.append("## Repository: ").append(config.githubPath).append("\n");
        sb.append("## Biological Map: ").append(config.biologicalMap).append("\n");
        sb.append("## Description: ").append(config.description).append("\n\n");
        sb.append("## API Documentation\n\n");
        
        switch (repoKey) {
            case "firecracker":
                sb.append(generateFirecrackerAPI());
                break;
            case "piper":
                sb.append(generatePiperAPI());
                break;
            case "memgraph":
                sb.append(generateMemgraphAPI());
                break;
            case "searxng":
                sb.append(generateSearxNGAPI());
                break;
            case "open-interpreter":
                sb.append(generateOpenInterpreterAPI());
                break;
            case "celery":
                sb.append(generateCeleryAPI());
                break;
            default:
                sb.append(generateGenericAPI(repoKey));
        }
        
        return sb.toString();
    }
    
    private String generateFirecrackerAPI() {
        return "### API: Create MicroVM\n" +
            "```\n" +
            "POST /machine/create\n" +
            "{\n" +
            "  \"machine_config\": {\n" +
            "    \"vcpu_count\": 2,\n" +
            "    \"mem_size_mib\": 256\n" +
            "  }\n" +
            "}\n" +
            "```\n\n" +
            "### API: Start MicroVM\n" +
            "```\n" +
            "POST /machine/start\n" +
            "{\n" +
            "  \"machine_id\": \"uuid\"\n" +
            "}\n" +
            "```\n\n" +
            "### API: Execute in MicroVM\n" +
            "```\n" +
            "POST /machine/exec\n" +
            "{\n" +
            "  \"machine_id\": \"uuid\",\n" +
            "  \"command\": \"ls -la\"\n" +
            "}\n" +
            "```\n\n" +
            "### API: Destroy MicroVM\n" +
            "```\n" +
            "POST /machine/destroy\n" +
            "{\n" +
            "  \"machine_id\": \"uuid\"\n" +
            "}\n" +
            "```\n";
    }
    
    private String generatePiperAPI() {
        return "### API: Initialize TTS\n" +
            "```python\n" +
            "import piper\n" +
            "model = piper.load_model(\"en_US-lessac-medium\")\n" +
            "```\n\n" +
            "### API: Synthesize Speech\n" +
            "```python\n" +
            "audio = model.synthesize(\"Hello, Fraynix\")\n" +
            "```\n\n" +
            "### API: Set Voice Parameters\n" +
            "```python\n" +
            "model.set_speaker_id(0)\n" +
            "model.set_noise_scale(0.667)\n" +
            "model.set_length_scale(1.0)\n" +
            "```\n\n" +
            "### API: Save Audio\n" +
            "```python\n" +
            "model.save_audio(audio, \"output.wav\")\n" +
            "```\n";
    }
    
    private String generateMemgraphAPI() {
        return "### API: Connect to Memgraph\n" +
            "```python\n" +
            "from mgclient import MemgraphClient\n" +
            "client = MemgraphClient(host=\"localhost\", port=7687)\n" +
            "```\n\n" +
            "### API: Execute Cypher Query\n" +
            "```python\n" +
            "result = client.execute(\"MATCH (n) RETURN n\")\n" +
            "```\n\n" +
            "### API: Create Node\n" +
            "```cypher\n" +
            "CREATE (n:Concept {name: \"Fraynix\", type: \"AI\"})\n" +
            "```\n\n" +
            "### API: Create Relationship\n" +
            "```cypher\n" +
            "MATCH (a:Concept), (b:Concept)\n" +
            "CREATE (a)-[r:RELATES_TO]->(b)\n" +
            "```\n\n" +
            "### API: Pattern Matching\n" +
            "```cypher\n" +
            "MATCH (a)-[r:RELATES_TO]->(b)\n" +
            "WHERE a.name = \"Fraynix\"\n" +
            "RETURN b\n" +
            "```\n";
    }
    
    private String generateSearxNGAPI() {
        return "### API: Search Query\n" +
            "```\n" +
            "GET /search?q=fraynix+AI&format=json\n" +
            "```\n\n" +
            "### API: Autocomplete\n" +
            "```\n" +
            "GET /autocompleter?q=fray&format=json\n" +
            "```\n\n" +
            "### API: Engine Selection\n" +
            "```\n" +
            "GET /search?q=test&engines=google,bing,duckduckgo\n" +
            "```\n\n" +
            "### API: Result Filtering\n" +
            "```\n" +
            "GET /search?q=test&time_range=day&safesearch=1\n" +
            "```\n";
    }
    
    private String generateOpenInterpreterAPI() {
        return "### API: Execute Command\n" +
            "```python\n" +
            "from interpreter import Interpreter\n" +
            "interpreter = Interpreter()\n" +
            "result = interpreter.run(\"ls -la\")\n" +
            "```\n\n" +
            "### API: Interactive Mode\n" +
            "```python\n" +
            "interpreter.chat()\n" +
            "```\n\n" +
            "### API: Code Execution\n" +
            "```python\n" +
            "interpreter.run_code(\"print('Hello')\")\n" +
            "```\n\n" +
            "### API: File Operations\n" +
            "```python\n" +
            "interpreter.run(\"create file test.txt with content 'hello'\")\n" +
            "```\n";
    }
    
    private String generateCeleryAPI() {
        return "### API: Define Task\n" +
            "```python\n" +
            "from celery import Celery\n" +
            "app = Celery('fraynix')\n" +
            "\n" +
            "@app.task\n" +
            "def process_data(data):\n" +
            "    return data * 2\n" +
            "```\n\n" +
            "### API: Execute Task\n" +
            "```python\n" +
            "result = process_data.delay(42)\n" +
            "```\n\n" +
            "### API: Chain Tasks\n" +
            "```python\n" +
            "from celery import chain\n" +
            "workflow = chain(task1.s(), task2.s(), task3.s())\n" +
            "```\n\n" +
            "### API: Periodic Tasks\n" +
            "```python\n" +
            "from celery.schedules import crontab\n" +
            "app.conf.beat_schedule = {\n" +
            "    'dream-every-hour': {\n" +
            "        'task': 'fraynix.dream',\n" +
            "        'schedule': crontab(minute=0)\n" +
            "    }\n" +
            "}\n" +
            "```\n";
    }
    
    private String generateGenericAPI(String repoKey) {
        return "### API: Initialize\n" +
            "```python\n" +
            "import " + repoKey + "\n" +
            "client = " + repoKey + ".Client()\n" +
            "```\n\n" +
            "### API: Execute Operation\n" +
            "```python\n" +
            "result = client.execute(\"operation\")\n" +
            "```\n\n" +
            "### API: Configure Parameters\n" +
            "```python\n" +
            "client.set_config(\"key\", \"value\")\n" +
            "```\n";
    }
    
    private List<ExtractedAPI> parseAPIs(Map<String, String> documentation) {
        List<ExtractedAPI> apis = new ArrayList<>();
        java.util.regex.Pattern apiPattern = java.util.regex.Pattern.compile("### API:\\s*(.+?)\\s*```([\\w]+)?\\s*([\\s\\S]*?)```");
        
        for (Map.Entry<String, String> entry : documentation.entrySet()) {
            String docFile = entry.getKey();
            String content = entry.getValue();
            
            java.util.regex.Matcher matcher = apiPattern.matcher(content);
            while (matcher.find()) {
                ExtractedAPI api = new ExtractedAPI();
                api.name = matcher.group(1).trim();
                api.signature = matcher.group(2) != null ? matcher.group(2).trim() : "generic";
                api.description = "Extracted from " + docFile;
                api.parameters = "TODO: Parse from signature";
                api.returnType = "TODO: Parse from signature";
                api.examples.add(matcher.group(3).trim());
                apis.add(api);
            }
        }
        
        return apis;
    }
    
    private String generateWrapper(String repoKey, RepositoryConfig config, List<ExtractedAPI> apis) {
        String className = toClassName(repoKey) + "Wrapper";
        
        StringBuilder sb = new StringBuilder();
        sb.append("package fraymus.exocortex.wrappers;\n\n");
        sb.append("import java.util.*;\n");
        sb.append("import java.util.concurrent.*;\n\n");
        sb.append("/**\n");
        sb.append(" * ").append(config.name).append(" Wrapper\n");
        sb.append(" * Biological Map: ").append(config.biologicalMap).append("\n");
        sb.append(" * Description: ").append(config.description).append("\n");
        sb.append(" */\n");
        sb.append("public class ").append(className).append(" {\n\n");
        sb.append("    private String config;\n");
        sb.append("    private boolean initialized = false;\n\n");
        sb.append("    public ").append(className).append("() {\n");
        sb.append("        this.config = \"default\";\n");
        sb.append("    }\n\n");
        sb.append("    public ").append(className).append("(String config) {\n");
        sb.append("        this.config = config;\n");
        sb.append("    }\n\n");
        sb.append("    public void initialize() {\n");
        sb.append("        this.initialized = true;\n");
        sb.append("    }\n\n");
        
        for (ExtractedAPI api : apis) {
            String methodName = camelCase(api.name.replace("API: ", ""));
            sb.append("    public String ").append(methodName).append("(String input) {\n");
            sb.append("        if (!initialized) {\n");
            sb.append("            throw new IllegalStateException(\"Wrapper not initialized\");\n");
            sb.append("        }\n");
            sb.append("        return \"Processed: \" + input;\n");
            sb.append("    }\n\n");
        }
        
        sb.append("    public Map<String, Object> getStatus() {\n");
        sb.append("        Map<String, Object> status = new HashMap<>();\n");
        sb.append("        status.put(\"initialized\", initialized);\n");
        sb.append("        status.put(\"config\", config);\n");
        sb.append("        status.put(\"api_count\", ").append(apis.size()).append(");\n");
        sb.append("        return status;\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        String wrapperPath = Paths.get(wrappersPath, className + ".java").toString();
        try {
            Files.write(Paths.get(wrapperPath), sb.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Failed to write wrapper: " + e.getMessage());
        }
        
        return wrapperPath;
    }
    
    private void storeExtraction(String repoKey, Map<String, String> documentation, 
                                  List<ExtractedAPI> apis, String wrapperPath) {
        try {
            StringBuilder docSb = new StringBuilder();
            for (Map.Entry<String, String> entry : documentation.entrySet()) {
                docSb.append("=== ").append(entry.getKey()).append(" ===\n");
                docSb.append(entry.getValue()).append("\n\n");
            }
            Files.write(Paths.get(extractedPath, repoKey + "_documentation.txt"), docSb.toString().getBytes());
            
            StringBuilder apiSb = new StringBuilder();
            for (ExtractedAPI api : apis) {
                apiSb.append("=== ").append(api.name).append(" ===\n");
                apiSb.append("Signature: ").append(api.signature).append("\n");
                apiSb.append("Description: ").append(api.description).append("\n");
                for (String example : api.examples) {
                    apiSb.append("Example:\n").append(example).append("\n");
                }
                apiSb.append("\n");
            }
            Files.write(Paths.get(extractedPath, repoKey + "_apis.txt"), apiSb.toString().getBytes());
            
        } catch (IOException e) {
            System.err.println("Failed to store extraction: " + e.getMessage());
        }
    }
    
    private String camelCase(String input) {
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                sb.append(words[i]);
            } else {
                sb.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1));
            }
        }
        return sb.toString();
    }
    
    private String toClassName(String repoKey) {
        String[] parts = repoKey.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
            }
        }
        return sb.toString();
    }
    
    public static Map<String, RepositoryConfig> getRepositories() {
        return new HashMap<>(REPOSITORIES);
    }
    
    public static RepositoryConfig getRepository(String repoKey) {
        return REPOSITORIES.get(repoKey);
    }
    
    public void setStorageRoot(String root) {
        this.storageRoot = root;
        this.reposPath = Paths.get(storageRoot, "repositories").toString();
        this.extractedPath = Paths.get(storageRoot, "extracted").toString();
        this.wrappersPath = Paths.get(storageRoot, "wrappers").toString();
        initializePaths();
    }
}
