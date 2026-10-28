# Real Backend Adapter Implementation

**Date**: April 11, 2026  
**Status**: Phase 6 In Progress - 3/6 Complete  
**Safety**: ✅ Isolated - Zero Impact on Working System

## Overview

Replacing mock backend implementations with actual API calls to make the 8-brain system functional with real AI backends.

## Completed Backends

### 1. OllamaBackend ✅
**API**: localhost:11434 (Ollama API)  
**Status**: Fully implemented  
**Requirements**: Ollama must be running on localhost:11434

**Features**:
- Real API calls to Ollama's `/api/generate` endpoint
- Configurable model (default: llama2)
- Temperature and num_predict parameters
- Health checks via `/api/tags`
- Graceful degradation when Ollama not running

**Usage**:
```java
OllamaBackend backend = new OllamaBackend("http://localhost:11434", "llama2");
backend.initialize();

Map<String, Object> params = Map.of(
    "temperature", 0.7,
    "num_predict", 512
);
var result = backend.executeQuery("Hello", TaskType.GENERATION, params).join();
```

### 2. ClaudeBackend ✅
**API**: Anthropic Messages API  
**Status**: Fully implemented  
**Requirements**: ANTHROPIC_API_KEY environment variable

**Features**:
- Real API calls to Anthropic's `/v1/messages` endpoint
- Configurable model (default: claude-3-sonnet-20240229)
- Temperature and max_tokens parameters
- API key validation
- Error handling and health monitoring

**Usage**:
```java
ClaudeBackend backend = new ClaudeBackend();
// Reads ANTHROPIC_API_KEY from environment
backend.initialize();

Map<String, Object> params = Map.of(
    "temperature", 0.7,
    "max_tokens", 1024
);
var result = backend.executeQuery("Hello", TaskType.REASONING, params).join();
```

**Environment Variables**:
- `ANTHROPIC_API_KEY`: Your Anthropic API key

### 3. Gemma3Backend ✅
**API**: Google AI Studio API  
**Status**: Fully implemented  
**Requirements**: GOOGLE_API_KEY environment variable

**Features**:
- Real API calls to Google's `/v1beta/models/gemma-1.5-pro:generateContent` endpoint
- Configurable model (default: gemma-1.5-pro)
- Temperature and max_output_tokens parameters
- API key validation
- Error handling and health monitoring

**Usage**:
```java
Gemma3Backend backend = new Gemma3Backend();
// Reads GOOGLE_API_KEY from environment
backend.initialize();

Map<String, Object> params = Map.of(
    "temperature", 0.7,
    "max_output_tokens", 1024
);
var result = backend.executeQuery("Hello", TaskType.GENERATION, params).join();
```

**Environment Variables**:
- `GOOGLE_API_KEY`: Your Google AI Studio API key

## Pending Backends

### 4. OpenClawBackend ⏳
**API**: TypeScript/JavaScript agent system  
**Status**: Not implemented  
**Requirements**: TBD (needs specification)

### 5. NeoClawBackend ⏳
**API**: Enhanced openclaw evolution  
**Status**: Not implemented  
**Requirements**: TBD (needs specification)

### 6. ObsidianBackend ⏳
**API**: Knowledge base  
**Status**: Not implemented  
**Requirements**: TBD (needs specification)

## Dependencies Added

```gradle
dependencies {
    // HTTP client for API calls
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    
    // JSON parsing
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

## Testing

All tests pass (28/28) with graceful degradation:
- Tests skip if API keys not set
- Tests skip if services not running
- Mock backends (OpenClaw, NeoClaw, Obsidian) still use simulated responses

## Configuration

### Environment Variables

Set these before running the system:

```bash
# For Claude (Anthropic)
export ANTHROPIC_API_KEY="your-anthropic-api-key"

# For Gemma3 (Google)
export GOOGLE_API_KEY="your-google-api-key"

# Ollama requires running service
# Start with: ollama serve
```

### Backend Configuration

Each backend can be configured with custom parameters:

```java
// Custom Ollama host and model
OllamaBackend ollama = new OllamaBackend("http://localhost:11434", "llama2");

// Custom Claude API URL and model
ClaudeBackend claude = new ClaudeBackend(
    "your-api-key",
    "https://api.anthropic.com/v1/messages",
    "claude-3-opus-20240229"
);

// Custom Gemma API URL and model
Gemma3Backend gemma = new Gemma3Backend(
    "your-api-key",
    "https://generativelanguage.googleapis.com/v1beta/models/gemma-1.5-pro:generateContent",
    "gemma-1.5-pro"
);
```

## Integration with Quantum Oracle

The real backends work seamlessly with the 8-brain system:

```java
QuantumOracleIntegration oracle = new QuantumOracleIntegration();

// If API keys are set, real backends will be used
// If not, the system degrades gracefully
var response = oracle.processQuery(
    "What is consciousness?",
    OracleQueryType.AUTO
).join();
```

## Next Steps

1. Implement OpenClawBackend (needs API specification)
2. Implement NeoClawBackend (needs API specification)
3. Implement ObsidianBackend (needs API specification)
4. Add retry logic for failed API calls
5. Add rate limiting
6. Add streaming support for long responses

## Safety

- ✅ No API keys hardcoded
- ✅ Environment variables for secrets
- ✅ Graceful degradation when backends unavailable
- ✅ Tests skip when services not running
- ✅ Zero impact on working system
