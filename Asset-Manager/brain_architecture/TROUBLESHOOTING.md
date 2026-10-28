# FraynixSystem - Troubleshooting Guide

**Date**: April 11, 2026  
**Status**: ✅ COMPLETE  
**Version**: 1.0.0

---

## Overview

This guide provides solutions to common issues when using FraynixSystem. The system is designed with graceful degradation, so many issues will result in fallback behavior rather than complete failure.

---

## Quick Reference

| Issue | Likely Cause | Quick Fix |
|-------|-------------|-----------|
| Build fails | Java version wrong | Install Java 17+ |
| Tests fail | API keys missing | Use mock mode or add keys |
| Ollama errors | Ollama not running | Run `ollama serve` |
| Slow queries | Network/API latency | Use mock mode or check network |
| Out of memory | Heap size too small | Increase JVM heap |
| File not found | Wrong path | Check file path |

---

## Build and Installation Issues

### Issue: Gradle build fails with "Unsupported class file major version"

**Symptoms:**
- Build fails during compilation
- Error mentions class file version

**Cause:**
- Java version is too old (< 17)

**Solution:**
```bash
# Check Java version
java -version

# Install Java 17 or higher
# Windows: Download from oracle.com
# Linux: sudo apt install openjdk-17-jdk
# macOS: brew install openjdk@17

# Set JAVA_HOME
# Windows: set JAVA_HOME=C:\Program Files\Java\jdk-17
# Linux/macOS: export JAVA_HOME=/usr/lib/jvm/java-17
```

### Issue: Gradle wrapper not found

**Symptoms:**
- `gradlew` or `gradlew.bat` not found
- Error: "gradlew: command not found"

**Cause:**
- Gradle wrapper files missing or not executable

**Solution:**
```bash
# Windows: Ensure gradlew.bat exists
dir gradlew.bat

# Linux/macOS: Make gradlew executable
chmod +x gradlew

# If missing, regenerate wrapper
gradle wrapper --gradle-version 9.0.0
```

### Issue: Dependencies fail to download

**Symptoms:**
- Build fails during dependency resolution
- Error: "Could not resolve dependency"

**Cause:**
- Network connectivity issues
- Maven Central down
- Proxy configuration needed

**Solution:**
```bash
# Check network connectivity
ping maven-central.org

# Configure proxy (if needed)
# Add to gradle.properties:
systemProp.http.proxyHost=your-proxy-host
systemProp.http.proxyPort=your-proxy-port
systemProp.https.proxyHost=your-proxy-host
systemProp.https.proxyPort=your-proxy-port

# Try again
.\gradlew.bat build --refresh-dependencies
```

---

## Runtime Issues

### Issue: IllegalStateException - "FraynixSystem not initialized"

**Symptoms:**
- Exception when calling processQuery or other methods
- Error: "FraynixSystem not initialized. Call initialize() first."

**Cause:**
- Forgot to call initialize()
- Called shutdown() before using methods

**Solution:**
```java
// Correct usage
FraynixSystem fraynix = new FraynixSystem();
fraynix.initialize();  // Must call this first

// Use system
QuantumOracleIntegration.OracleResponse response = 
    fraynix.processQuery("query", OracleQueryType.LOGICAL).join();

// Shutdown when done
fraynix.shutdown();
```

### Issue: CompletableFuture - InterruptedException or ExecutionException

**Symptoms:**
- Exception when calling .join() or .get()
- InterruptedException or ExecutionException

**Cause:**
- Query was interrupted
- Backend threw an exception
- Network timeout

**Solution:**
```java
try {
    QuantumOracleIntegration.OracleResponse response = 
        fraynix.processQuery("query", OracleQueryType.LOGICAL)
        .join();
} catch (InterruptedException e) {
    // Query was interrupted
    Thread.currentThread().interrupt();
} catch (CompletionException e) {
    // Backend threw an exception
    Throwable cause = e.getCause();
    System.err.println("Query failed: " + cause.getMessage());
}
```

### Issue: NullPointerException

**Symptoms:**
- NullPointerException when accessing components
- Null response from queries

**Cause:**
- Component not initialized
- Backend returned null
- Integration not wired

**Solution:**
```java
// Check initialization
if (!fraynix.isInitialized()) {
    fraynix.initialize();
}

// Check for null responses
QuantumOracleIntegration.OracleResponse response = 
    fraynix.processQuery("query", OracleQueryType.LOGICAL).join();
if (response == null) {
    System.err.println("Backend returned null response");
}

// Check integration before using
if (fraynix.getSentientCoreBridge() != null) {
    // Use SentientCore
}
```

---

## API and Backend Issues

### Issue: API connection errors (Claude, Gemma3, etc.)

**Symptoms:**
- Errors connecting to external APIs
- Authentication failures
- Rate limiting errors

**Cause:**
- API key missing or invalid
- Network connectivity issues
- API service down
- Rate limit exceeded

**Solution:**
```bash
# Verify API key is set
echo $ANTHROPIC_API_KEY  # Linux/macOS
echo %ANTHROPIC_API_KEY%  # Windows

# Test API connectivity
curl https://api.anthropic.com/v1/messages

# System degrades gracefully to mock mode
# No action required if mock mode is acceptable
```

### Issue: Ollama connection errors

**Symptoms:**
- Errors connecting to localhost:11434
- "Connection refused" errors

**Cause:**
- Ollama service not running
- Ollama not installed
- Wrong port

**Solution:**
```bash
# Check if Ollama is running
curl http://localhost:11434/api/tags

# Start Ollama service
ollama serve

# Install Ollama if missing
# Visit https://ollama.ai for installation

# Pull a model
ollama pull llama2

# System degrades gracefully without Ollama
# No action required if mock mode is acceptable
```

### Issue: Slow query performance

**Symptoms:**
- Queries take > 1 second
- High latency on multi-brain queries

**Cause:**
- Network latency to external APIs
- Backend API slow response
- Too many concurrent queries
- System under heavy load

**Solution:**
```java
// Use mock mode for faster performance
// (no external API calls)

// Reduce concurrent queries
// Process queries sequentially instead of in parallel

// Check network connectivity
ping api.anthropic.com

// Monitor backend health
Map<String, Object> status = fraynix.getSystemStatus();
System.out.println(status);
```

---

## File and Knowledge Ingestion Issues

### Issue: FileNotFoundException

**Symptoms:**
- Error ingesting file
- FileNotFoundException

**Cause:**
- File path incorrect
- File does not exist
- No read permissions

**Solution:**
```java
// Check file exists
Path filePath = Paths.get("README.md");
if (!Files.exists(filePath)) {
    System.err.println("File not found: " + filePath);
    return;
}

// Use absolute path
Path absolutePath = filePath.toAbsolutePath();

// Check permissions
if (!Files.isReadable(filePath)) {
    System.err.println("File not readable: " + filePath);
    return;
}
```

### Issue: Knowledge ingestion returns empty graph

**Symptoms:**
- ingestFile returns KnowledgeGraph with 0 concepts
- No concepts extracted from file

**Cause:**
- File format not supported
- File is empty
- Parser error

**Solution:**
```java
KnowledgeGraph graph = fraynix.ingestFile(filePath).join();
System.out.println("Concepts extracted: " + graph.getConcepts().size());

if (graph.getConcepts().isEmpty()) {
    System.err.println("No concepts extracted. Check file format.");
    System.err.println("Supported formats: markdown, code, text");
}
```

### Issue: Directory ingestion fails

**Symptoms:**
- Error ingesting directory
- Permission denied

**Cause:**
- Directory does not exist
- No read permissions
- Not a directory

**Solution:**
```java
Path dirPath = Paths.get("./docs");

// Check if directory exists
if (!Files.exists(dirPath)) {
    System.err.println("Directory not found: " + dirPath);
    return;
}

// Check if it's a directory
if (!Files.isDirectory(dirPath)) {
    System.err.println("Not a directory: " + dirPath);
    return;
}

// Check permissions
if (!Files.isReadable(dirPath)) {
    System.err.println("Directory not readable: " + dirPath);
    return;
}
```

---

## Persistence Issues

### Issue: Knowledge graph not found

**Symptoms:**
- retrieveKnowledgeGraph returns null
- Graph ID not found

**Cause:**
- Graph ID incorrect
- Graph was deleted
- Persistence layer not initialized

**Solution:**
```java
UUID graphId = fraynix.persistKnowledgeGraph(graph);

// Verify graph ID is not null
if (graphId == null) {
    System.err.println("Failed to persist graph");
    return;
}

// Retrieve graph
KnowledgeGraph retrieved = fraynix.retrieveKnowledgeGraph(graphId);
if (retrieved == null) {
    System.err.println("Graph not found: " + graphId);
    // Graph may have been deleted or ID is incorrect
}
```

### Issue: Blockchain integrity verification fails

**Symptoms:**
- verifyBlockchainIntegrity returns false
- Data corruption suspected

**Cause:**
- Blockchain corrupted
- Data modified externally
- Persistence layer issue

**Solution:**
```java
boolean integrity = fraynix.verifyBlockchainIntegrity();
if (!integrity) {
    System.err.println("Blockchain integrity check failed");
    System.err.println("Possible data corruption");
    System.err.println("Check persistence layer logs");
    // May need to restore from backup
}
```

---

## Memory and Performance Issues

### Issue: OutOfMemoryError

**Symptoms:**
- OutOfMemoryError during runtime
- JVM crashes

**Cause:**
- Heap size too small
- Memory leak
- Processing large files

**Solution:**
```bash
# Increase JVM heap size
java -Xmx2g -Xms512m -jar app.jar

# For Gradle tasks
# Add to gradle.properties:
org.gradle.jvmargs=-Xmx2g -Xms512m

# Monitor memory usage
Runtime runtime = Runtime.getRuntime();
long usedMemory = runtime.totalMemory() - runtime.freeMemory();
System.out.println("Used memory: " + usedMemory / 1024 / 1024 + "MB");
```

### Issue: High memory usage over time

**Symptoms:**
- Memory usage increases continuously
- Possible memory leak

**Cause:**
- Objects not garbage collected
- Caches not cleared
- Large knowledge graphs retained

**Solution:**
```java
// Clear caches if available
// (System automatically manages memory)

// Shutdown when done to release resources
fraynix.shutdown();

// Force garbage collection (for testing)
System.gc();

// Monitor memory usage
Runtime runtime = Runtime.getRuntime();
long usedMemory = runtime.totalMemory() - runtime.freeMemory();
System.out.println("Used memory: " + usedMemory / 1024 / 1024 + "MB");
```

### Issue: Slow startup

**Symptoms:**
- System initialization takes > 10 seconds
- Long delay before first query

**Cause:**
- Loading large knowledge graphs
- Initializing many backends
- Network latency during initialization

**Solution:**
```java
// Initialization time is normal for first run
// Subsequent runs should be faster

// Monitor initialization time
long startTime = System.currentTimeMillis();
fraynix.initialize();
long endTime = System.currentTimeMillis();
System.out.println("Initialization time: " + (endTime - startTime) + "ms");

// If consistently slow, check:
// - Network connectivity
// - Backend health
// - Knowledge graph size
```

---

## Integration Issues

### Issue: SentientCore integration fails

**Symptoms:**
- wireSentientCore() fails or has no effect
- getSentientCoreBridge() returns null

**Cause:**
- SentientCore not available
- Integration not configured
- Classpath issues

**Solution:**
```java
// Check if SentientCore is available
fraynix.wireSentientCore();

if (fraynix.getSentientCoreBridge() == null) {
    System.err.println("SentientCore not available");
    System.err.println("Using stub implementation for testing");
    // System degrades gracefully
}

// System continues to work without SentientCore
// Using native brain architecture
```

### Issue: Hybrid Orchestrator integration fails

**Symptoms:**
- wireHybridOrchestrator() fails
- Backend discovery not working

**Cause:**
- Orchestrator not available
- Backend configuration issues
- Network connectivity

**Solution:**
```java
// Check if Orchestrator is available
fraynix.wireHybridOrchestrator();

if (fraynix.getOrchestratorBridge() == null) {
    System.err.println("Orchestrator not available");
    System.err.println("Using basic backend routing");
    // System degrades gracefully
}

// System continues to work without Orchestrator
// Using basic backend routing
```

---

## Testing Issues

### Issue: Tests fail with compilation errors

**Symptoms:**
- Test compilation fails
- Type mismatch errors

**Cause:**
- API changed
- Dependencies not updated
- Test code outdated

**Solution:**
```bash
# Clean and rebuild
.\gradlew.bat clean build

# Update dependencies
.\gradlew.bat build --refresh-dependencies

# Check test code against current API
# Ensure method signatures match
```

### Issue: Tests fail at runtime

**Symptoms:**
- Tests pass compilation but fail at runtime
- Assertions fail

**Cause:**
- Test data incorrect
- Environment not set up
- External services unavailable

**Solution:**
```bash
# Run specific test with verbose output
.\gradlew.bat test --tests "YourTest" --info

# Check test logs
# Location: build/reports/tests/test/index.html

# Ensure mock mode is used if external services unavailable
# Tests should work without external dependencies
```

### Issue: Flaky tests

**Symptoms:**
- Tests sometimes pass, sometimes fail
- Non-deterministic behavior

**Cause:**
- Race conditions
- Timing issues
- External service instability

**Solution:**
```java
// Add retries for flaky tests
// Use proper synchronization
// Avoid time-based assertions

// Run tests multiple times to identify flakiness
for (int i = 0; i < 5; i++) {
    .\gradlew.bat test
}
```

---

## Logging and Debugging

### Enable Debug Logging

```java
// Add to your application startup
System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

// Or use logback configuration
// Create logback.xml in src/main/resources/
```

### Check System Status

```java
Map<String, Object> status = fraynix.getSystemStatus();
System.out.println("System Status:");
System.out.println(status);
```

### Enable Query Logging

```java
// Log queries before and after
String query = "Your query here";
System.out.println("Processing query: " + query);

QuantumOracleIntegration.OracleResponse response = 
    fraynix.processQuery(query, OracleQueryType.LOGICAL).join();

System.out.println("Query response: " + response.getContent());
System.out.println("Confidence: " + response.getConfidence());
System.out.println("Brain used: " + response.getBrainType());
```

### Monitor Backend Health

```java
Map<BrainType, QuantumBrain> brains = fraynix.getBrains();
for (Map.Entry<BrainType, QuantumBrain> entry : brains.entrySet()) {
    BrainType type = entry.getKey();
    QuantumBrain brain = entry.getValue();
    System.out.println("Brain: " + type + ", Healthy: " + brain.isHealthy());
}
```

---

## Getting Help

### Check Documentation
- SYSTEM_COMPLETE.md - Complete system documentation
- DEPLOYMENT_GUIDE.md - Deployment instructions
- FRAYNIX_SYSTEM_API.md - API reference
- FRAYNIX_SYSTEM_EXAMPLES.md - Usage examples

### Check Logs
- Application logs for errors
- Gradle build logs for build issues
- Test reports for test failures

### Common Solutions
1. **System not working**: Ensure initialize() is called
2. **API errors**: Use mock mode or check API keys
3. **File errors**: Check file paths and permissions
4. **Memory issues**: Increase JVM heap size
5. **Performance issues**: Use mock mode or check network

### Report Issues
- Create issue in repository with:
  - Error message
  - Stack trace
  - System information (OS, Java version)
  - Steps to reproduce
  - Expected vs actual behavior

---

## Summary

This troubleshooting guide covers:
- ✅ Build and installation issues
- ✅ Runtime issues
- ✅ API and backend issues
- ✅ File and knowledge ingestion issues
- ✅ Persistence issues
- ✅ Memory and performance issues
- ✅ Integration issues
- ✅ Testing issues
- ✅ Logging and debugging

The FraynixSystem is designed with graceful degradation, so many issues will result in fallback behavior rather than complete failure.

---

**Document**: TROUBLESHOOTING.md  
**Version**: 1.0  
**Date**: April 11, 2026  
**Status**: ✅ COMPLETE
