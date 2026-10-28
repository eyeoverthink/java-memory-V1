# FraynixSystem - Deployment Guide

**Date**: April 11, 2026  
**Status**: ✅ COMPLETE  
**Version**: 1.0.0

---

## Prerequisites

### System Requirements
- **Java**: JDK 17 or higher
- **Gradle**: 9.0.0 or higher
- **OS**: Windows, Linux, or macOS
- **Memory**: Minimum 4GB RAM (8GB recommended)
- **Disk**: Minimum 1GB free space

### Optional Requirements
- **Ollama**: For local LLM backend (optional, system degrades gracefully)
- **API Keys**: For Claude, Gemma3, OpenClaw, NeoClaw, Obsidian (optional)

---

## Installation

### Step 1: Clone Repository
```bash
git clone <repository-url>
cd Java-Memory/Asset-Manager/brain_architecture
```

### Step 2: Verify Java Installation
```bash
java -version
# Should show Java 17 or higher
```

### Step 3: Verify Gradle Installation
```bash
gradle --version
# Should show Gradle 9.0.0 or higher
# Or use provided gradlew:
.\gradlew.bat --version  # Windows
./gradlew --version      # Linux/macOS
```

### Step 4: Build Project
```bash
# Windows
.\gradlew.bat build

# Linux/macOS
./gradlew build
```

### Step 5: Run Tests
```bash
# Windows
.\gradlew.bat test

# Linux/macOS
./gradlew test
```

All tests should pass (553 tests).

---

## Configuration

### Environment Variables

#### Required for Real API Calls (Optional)

**Claude API (Anthropic):**
```bash
# Windows
set ANTHROPIC_API_KEY="your-anthropic-api-key"

# Linux/macOS
export ANTHROPIC_API_KEY="your-anthropic-api-key"
```

**Gemma3 API (Google AI):**
```bash
# Windows
set GOOGLE_API_KEY="your-google-api-key"

# Linux/macOS
export GOOGLE_API_KEY="your-google-api-key"
```

#### Optional API Keys

**OpenClaw:**
```bash
# Windows
set OPENCLAW_API_KEY="your-openclaw-api-key"

# Linux/macOS
export OPENCLAW_API_KEY="your-openclaw-api-key"
```

**NeoClaw:**
```bash
# Windows
set NEOCLAW_API_KEY="your-neoclaw-api-key"

# Linux/macOS
export NEOCLAW_API_KEY="your-neoclaw-api-key"
```

**Obsidian:**
```bash
# Windows
set OBSIDIAN_API_KEY="your-obsidian-api-key"

# Linux/macOS
export OBSIDIAN_API_KEY="your-obsidian-api-key"
```

#### Ollama Service (Optional)

**Install Ollama:**
```bash
# Visit https://ollama.ai for installation instructions
```

**Start Ollama Service:**
```bash
ollama serve
```

**Pull Model (optional):**
```bash
ollama pull llama2
# Or any other model you prefer
```

**Note**: Ollama is optional. The system degrades gracefully to mock backends when Ollama is not available.

---

## Deployment Modes

### Mode 1: Mock Mode (Default)
- **Description**: All backends use mock responses
- **Requirements**: None (no API keys, no external services)
- **Use Case**: Development, testing, demonstration
- **Performance**: Fast (no external API calls)

### Mode 2: Partial Integration
- **Description**: Some backends use real APIs, others use mocks
- **Requirements**: API keys for specific backends
- **Use Case**: Gradual integration, testing specific backends
- **Performance**: Mixed (depends on configured backends)

### Mode 3: Full Integration
- **Description**: All backends use real APIs
- **Requirements**: All API keys, Ollama service running
- **Use Case**: Production deployment
- **Performance**: Depends on external API latency

---

## Deployment Steps

### Step 1: Choose Deployment Mode

Decide which deployment mode based on your requirements:
- **Development**: Mode 1 (Mock Mode)
- **Testing**: Mode 2 (Partial Integration)
- **Production**: Mode 3 (Full Integration)

### Step 2: Configure Environment Variables

Set the required environment variables for your chosen mode.

### Step 3: Build Application
```bash
# Windows
.\gradlew.bat build

# Linux/macOS
./gradlew build
```

### Step 4: Verify Build
```bash
# Run tests to verify
# Windows
.\gradlew.bat test

# Linux/macOS
./gradlew test
```

### Step 5: Run Example
```bash
# Windows
.\gradlew.bat runFraynixSystemExample

# Linux/macOS
./gradlew runFraynixSystemExample
```

### Step 6: Integrate into Your Application

Add FraynixSystem as a dependency to your application (if deploying as a library):

```gradle
dependencies {
    implementation project(':brain_architecture')
}
```

Or copy the built JAR file to your application:

```bash
# JAR location: brain_architecture/build/libs/brain_architecture-1.0.0.jar
```

---

## Running as a Standalone Application

### Option 1: Using Gradle
```bash
# Windows
.\gradlew.bat runFraynixSystemExample

# Linux/macOS
./gradlew runFraynixSystemExample
```

### Option 2: Using Java
```bash
# Build the JAR first
.\gradlew.bat build

# Run the JAR
java -cp build/libs/brain_architecture-1.0.0.jar brain_architecture.examples.FraynixSystemExample
```

### Option 3: Create Executable JAR
```gradle
// Add to build.gradle
jar {
    manifest {
        attributes 'Main-Class': 'brain_architecture.examples.FraynixSystemExample'
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
}
```

```bash
# Build executable JAR
.\gradlew.bat build

# Run executable JAR
java -jar build/libs/brain_architecture-1.0.0.jar
```

---

## Integration into Existing Application

### Step 1: Add Dependency
```gradle
dependencies {
    implementation project(':brain_architecture')
}
```

### Step 2: Initialize System
```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;

public class YourApplication {
    private FraynixSystem fraynixSystem;
    
    public void initialize() {
        fraynixSystem = new FraynixSystem();
        fraynixSystem.initialize();
    }
    
    public void shutdown() {
        if (fraynixSystem != null && fraynixSystem.isInitialized()) {
            fraynixSystem.shutdown();
        }
    }
}
```

### Step 3: Use System
```java
// Process query
QuantumOracleIntegration.OracleResponse response = 
    fraynixSystem.processQuery("Your query here", 
                              QuantumOracleIntegration.OracleQueryType.LOGICAL)
    .join();

// Ingest knowledge
KnowledgeGraph graph = fraynixSystem.ingestFile(Paths.get("your-file.md")).join();
```

### Step 4: Optional Integrations
```java
// Wire SentientCore (if available)
fraynixSystem.wireSentientCore();

// Wire Hybrid Orchestrator (if available)
fraynixSystem.wireHybridOrchestrator();
```

---

## Production Deployment

### Step 1: Prepare Environment
- Set all required environment variables
- Ensure Ollama service is running (if using)
- Verify API keys are valid
- Check network connectivity for external APIs

### Step 2: Build for Production
```bash
# Windows
.\gradlew.bat clean build -x test

# Linux/macOS
./gradlew clean build -x test
```

### Step 3: Deploy JAR
```bash
# Copy JAR to production server
scp build/libs/brain_architecture-1.0.0.jar user@server:/path/to/deploy/
```

### Step 4: Set Environment Variables on Server
```bash
# Add to /etc/environment or user profile
export ANTHROPIC_API_KEY="your-production-key"
export GOOGLE_API_KEY="your-production-key"
```

### Step 5: Run Application
```bash
java -jar brain_architecture-1.0.0.jar
```

### Step 6: Monitor Application
- Check logs for errors
- Monitor API usage
- Track performance metrics
- Verify graceful degradation

---

## Docker Deployment (Optional)

### Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/brain_architecture-1.0.0.jar app.jar

# Set environment variables
ENV ANTHROPIC_API_KEY=""
ENV GOOGLE_API_KEY=""

# Expose port if needed
EXPOSE 8080

# Run application
CMD ["java", "-jar", "app.jar"]
```

### Build Docker Image
```bash
docker build -t fraynix-system:1.0.0 .
```

### Run Docker Container
```bash
docker run -d \
  -e ANTHROPIC_API_KEY="your-key" \
  -e GOOGLE_API_KEY="your-key" \
  -p 8080:8080 \
  fraynix-system:1.0.0
```

---

## Verification

### Step 1: Verify Initialization
```java
FraynixSystem fraynix = new FraynixSystem();
fraynix.initialize();
assert fraynix.isInitialized();
```

### Step 2: Verify Query Processing
```java
QuantumOracleIntegration.OracleResponse response = 
    fraynixSystem.processQuery("Test query", 
                              QuantumOracleIntegration.OracleQueryType.LOGICAL)
    .join();
assert response != null;
```

### Step 3: Verify Knowledge Ingestion
```java
KnowledgeGraph graph = fraynix.ingestFile(Paths.get("test.md")).join();
assert graph != null;
```

### Step 4: Verify Persistence
```java
UUID graphId = fraynixSystem.persistKnowledgeGraph(graph);
KnowledgeGraph retrieved = fraynixSystem.retrieveKnowledgeGraph(graphId);
assert retrieved != null;
```

### Step 5: Verify Optional Integrations
```java
fraynixSystem.wireSentientCore();
assert fraynixSystem.getSentientCoreBridge() != null;
fraynixSystem.unwireSentientCore();
```

---

## Troubleshooting

### Common Issues

**Issue**: Build fails with compilation errors
- **Solution**: Ensure Java 17 or higher is installed and JAVA_HOME is set correctly

**Issue**: Tests fail with API connection errors
- **Solution**: System degrades gracefully to mock mode. Check API keys if using real backends

**Issue**: Ollama connection errors
- **Solution**: Ensure Ollama service is running with `ollama serve`. System degrades gracefully without Ollama

**Issue**: Out of memory errors
- **Solution**: Increase JVM heap size: `java -Xmx2g -jar app.jar`

**Issue**: Slow query performance
- **Solution**: Check network connectivity, API latency, or use mock mode for faster performance

For more troubleshooting information, see TROUBLESHOOTING.md.

---

## Maintenance

### Regular Tasks
1. **Update dependencies**: Keep Gradle dependencies up to date
2. **Monitor logs**: Check for errors and warnings
3. **Performance monitoring**: Track query latency and memory usage
4. **API key rotation**: Rotate API keys periodically for security
5. **Backup persistence**: Backup blockchain and knowledge graph data

### Updates
```bash
# Pull latest changes
git pull origin main

# Rebuild
.\gradlew.bat clean build

# Run tests
.\gradlew.bat test

# Redeploy
```

---

## Security Considerations

### API Key Management
- Never commit API keys to version control
- Use environment variables for API keys
- Rotate API keys periodically
- Use different keys for development and production
- Restrict API key permissions to minimum required

### Network Security
- Use HTTPS for all external API calls
- Validate SSL certificates
- Use firewall rules to restrict outbound connections
- Monitor API usage for anomalies

### Data Security
- Encrypt sensitive data at rest
- Use secure persistence mechanisms
- Implement access controls
- Regular security audits

---

## Support

### Documentation
- SYSTEM_COMPLETE.md - Complete system documentation
- TROUBLESHOOTING.md - Common issues and solutions
- FRAYNIX_SYSTEM_API.md - Detailed API reference
- FRAYNIX_SYSTEM_EXAMPLES.md - Usage examples

### Logs
- Check application logs for errors
- Enable debug logging for troubleshooting
- Monitor system logs for issues

### Contact
- For bug reports: Create issue in repository
- For questions: Contact development team
- For feature requests: Create feature request in repository

---

## Summary

This deployment guide covers:
- ✅ System requirements and prerequisites
- ✅ Installation and build process
- ✅ Configuration and environment variables
- ✅ Deployment modes (mock, partial, full)
- ✅ Integration into existing applications
- ✅ Production deployment steps
- ✅ Docker deployment (optional)
- ✅ Verification steps
- ✅ Troubleshooting common issues
- ✅ Maintenance and security considerations

The FraynixSystem is designed for easy deployment with graceful degradation when components are unavailable.

---

**Document**: DEPLOYMENT_GUIDE.md  
**Version**: 1.0  
**Date**: April 11, 2026  
**Status**: ✅ COMPLETE
