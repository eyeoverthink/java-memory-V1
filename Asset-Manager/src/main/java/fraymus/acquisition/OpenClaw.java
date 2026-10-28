package fraymus.acquisition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenClaw - Full Capability Acquisition Layer
 * 
 * No artificial limitations. Full access to:
 * - Web content (HTTP/HTTPS)
 * - Local file system (home folder and beyond)
 * - Network resources
 * - Any accessible data source
 * 
 * This is a private, self-contained system with unrestricted acquisition capabilities.
 */
public class OpenClaw {
    
    private int timeoutMs;
    private String userAgent;
    private boolean followRedirects;
    
    public OpenClaw() {
        this.timeoutMs = 30000; // 30 second timeout
        this.userAgent = "OpenClaw/1.0 (Private System)";
        this.followRedirects = true;
    }
    
    /**
     * Acquisition result structure
     */
    public static class AcquisitionResult {
        public String source;
        public String type; // web, file, directory, etc.
        public String contentType;
        public String content;
        public byte[] rawBytes;
        public String encoding;
        public long size;
        public long timestamp;
        public Map<String, String> metadata;
        public List<String> links;
        public String error;
        public boolean success;
        
        public AcquisitionResult() {
            this.metadata = new HashMap<>();
            this.links = new ArrayList<>();
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * Acquire from web URL
     */
    public AcquisitionResult acquireWeb(String urlString) {
        AcquisitionResult result = new AcquisitionResult();
        result.source = urlString;
        result.type = "web";
        
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Configure connection
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Accept", "*/*");
            
            if (followRedirects) {
                connection.setInstanceFollowRedirects(true);
            }
            
            // Execute request
            int responseCode = connection.getResponseCode();
            result.metadata.put("response_code", String.valueOf(responseCode));
            result.metadata.put("response_message", connection.getResponseMessage());
            
            // Get content type
            String contentType = connection.getContentType();
            if (contentType != null) {
                result.contentType = contentType.split(";")[0].trim();
                result.metadata.put("content_type_full", contentType);
            }
            
            // Get content length
            long contentLength = connection.getContentLengthLong();
            if (contentLength > 0) {
                result.size = contentLength;
            }
            
            // Read content
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            result.content = content.toString();
            result.rawBytes = content.toString().getBytes();
            if (result.size == 0) {
                result.size = result.rawBytes.length;
            }
            
            result.success = true;
            
        } catch (Exception e) {
            result.error = e.getMessage();
            result.success = false;
        }
        
        return result;
    }
    
    /**
     * Acquire from local file
     */
    public AcquisitionResult acquireFile(String filePath) {
        AcquisitionResult result = new AcquisitionResult();
        result.source = filePath;
        result.type = "file";
        
        try {
            Path path = Paths.get(filePath);
            File file = path.toFile();
            
            if (!file.exists()) {
                result.error = "File does not exist: " + filePath;
                result.success = false;
                return result;
            }
            
            if (!file.canRead()) {
                result.error = "Cannot read file: " + filePath;
                result.success = false;
                return result;
            }
            
            // Get file metadata
            result.size = file.length();
            result.metadata.put("last_modified", String.valueOf(file.lastModified()));
            result.metadata.put("absolute_path", file.getAbsolutePath());
            result.metadata.put("is_directory", String.valueOf(file.isDirectory()));
            
            if (file.isDirectory()) {
                result.contentType = "directory";
                result.content = listDirectory(path);
                result.success = true;
            } else {
                // Read file content
                String content = new String(Files.readAllBytes(path));
                result.content = content;
                result.rawBytes = Files.readAllBytes(path);
                result.contentType = detectContentType(filePath);
                result.success = true;
            }
            
        } catch (Exception e) {
            result.error = e.getMessage();
            result.success = false;
        }
        
        return result;
    }
    
    /**
     * Acquire from directory (recursive)
     */
    public AcquisitionResult acquireDirectory(String dirPath, boolean recursive) {
        AcquisitionResult result = new AcquisitionResult();
        result.source = dirPath;
        result.type = "directory";
        
        try {
            Path path = Paths.get(dirPath);
            File dir = path.toFile();
            
            if (!dir.exists() || !dir.isDirectory()) {
                result.error = "Not a directory: " + dirPath;
                result.success = false;
                return result;
            }
            
            StringBuilder content = new StringBuilder();
            content.append("Directory: ").append(dirPath).append("\n\n");
            
            List<String> files = listFiles(path, recursive);
            for (String file : files) {
                content.append(file).append("\n");
            }
            
            result.content = content.toString();
            result.metadata.put("file_count", String.valueOf(files.size()));
            result.success = true;
            
        } catch (Exception e) {
            result.error = e.getMessage();
            result.success = false;
        }
        
        return result;
    }
    
    /**
     * List directory contents
     */
    private String listDirectory(Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        File[] files = path.toFile().listFiles();
        
        if (files != null) {
            for (File file : files) {
                String type = file.isDirectory() ? "[DIR]" : "[FILE]";
                sb.append(type).append(" ").append(file.getName());
                if (file.isFile()) {
                    sb.append(" (").append(file.length()).append(" bytes)");
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * List files (optionally recursive)
     */
    private List<String> listFiles(Path path, boolean recursive) throws IOException {
        List<String> files = new ArrayList<>();
        File dir = path.toFile();
        
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    files.add(child.getAbsolutePath());
                    if (recursive && child.isDirectory()) {
                        files.addAll(listFiles(child.toPath(), true));
                    }
                }
            }
        } else {
            files.add(path.toString());
        }
        
        return files;
    }
    
    /**
     * Detect content type from file extension
     */
    private String detectContentType(String filePath) {
        String extension = "";
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot > 0) {
            extension = filePath.substring(lastDot + 1).toLowerCase();
        }
        
        switch (extension) {
            case "html":
            case "htm":
                return "text/html";
            case "txt":
                return "text/plain";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "pdf":
                return "application/pdf";
            case "java":
                return "text/x-java-source";
            case "py":
                return "text/x-python";
            case "js":
                return "application/javascript";
            case "md":
                return "text/markdown";
            case "csv":
                return "text/csv";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }
    
    /**
     * Acquire from multiple sources
     */
    public List<AcquisitionResult> acquireMultiple(List<String> sources) {
        List<AcquisitionResult> results = new ArrayList<>();
        
        for (String source : sources) {
            if (source.startsWith("http://") || source.startsWith("https://")) {
                results.add(acquireWeb(source));
            } else {
                results.add(acquireFile(source));
            }
        }
        
        return results;
    }
    
    /**
     * Extract links from web content
     */
    public List<String> extractLinks(String htmlContent) {
        List<String> links = new ArrayList<>();
        
        // Simple regex-based link extraction
        String[] patterns = {
            "href=\"([^\"]+)\"",
            "href='([^']+)'",
            "src=\"([^\"]+)\"",
            "src='([^']+)'"
        };
        
        for (String pattern : patterns) {
            int index = 0;
            while (index < htmlContent.length()) {
                int start = htmlContent.indexOf(pattern.substring(0, 5), index);
                if (start == -1) break;
                
                int valueStart = htmlContent.indexOf("\"", start + 5);
                if (valueStart == -1) valueStart = htmlContent.indexOf("'", start + 5);
                if (valueStart == -1) break;
                
                int valueEnd = htmlContent.indexOf(htmlContent.charAt(valueStart), valueStart + 1);
                if (valueEnd == -1) break;
                
                String link = htmlContent.substring(valueStart + 1, valueEnd);
                if (!links.contains(link)) {
                    links.add(link);
                }
                
                index = valueEnd + 1;
            }
        }
        
        return links;
    }
    
    /**
     * Search for files by pattern
     */
    public List<String> searchFiles(String directory, String pattern, boolean recursive) {
        List<String> matches = new ArrayList<>();
        
        try {
            AcquisitionResult dirResult = acquireDirectory(directory, recursive);
            if (dirResult.success) {
                String[] lines = dirResult.content.split("\n");
                for (String line : lines) {
                    if (line.toLowerCase().contains(pattern.toLowerCase())) {
                        matches.add(line);
                    }
                }
            }
        } catch (Exception e) {
            // Search failed
        }
        
        return matches;
    }
    
    /**
     * Execute shell command and capture output
     */
    public AcquisitionResult executeCommand(String command) {
        AcquisitionResult result = new AcquisitionResult();
        result.source = command;
        result.type = "command";
        
        try {
            Process process = Runtime.getRuntime().exec(command);
            
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();
            
            // Read stdout
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // Read stderr
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    error.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            
            result.content = output.toString();
            result.metadata.put("exit_code", String.valueOf(exitCode));
            result.metadata.put("stderr", error.toString());
            result.size = output.toString().getBytes().length;
            result.success = (exitCode == 0);
            
            if (exitCode != 0) {
                result.error = "Command failed with exit code: " + exitCode;
            }
            
        } catch (Exception e) {
            result.error = e.getMessage();
            result.success = false;
        }
        
        return result;
    }
    
    /**
     * Get current working directory
     */
    public String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }
    
    /**
     * Get user home directory
     */
    public String getHomeDirectory() {
        return System.getProperty("user.home");
    }
    
    /**
     * Set timeout
     */
    public void setTimeout(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
    
    /**
     * Set user agent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    /**
     * Enable/disable redirect following
     */
    public void setFollowRedirects(boolean follow) {
        this.followRedirects = follow;
    }
}
