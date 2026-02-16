# OpenAI API

> Direct access to GPT-4, GPT-4 Turbo, and GPT-3.5 models

## Description

OpenAI provides state-of-the-art language models including GPT-4, GPT-4 Turbo, and GPT-3.5. These models excel at code generation, analysis, reasoning, and natural language understanding. Direct API access gives you full control and the latest model versions.

## Features

- **GPT-4 Turbo:** Fastest GPT-4 variant with 128K context
- **GPT-4:** Highest quality reasoning and code generation
- **GPT-3.5 Turbo:** Fast and cost-effective
- **Function Calling:** Structured outputs and tool use
- **Vision:** GPT-4 Vision for image analysis
- **JSON Mode:** Guaranteed JSON responses

## Setup

### API Key Location

Your API key is stored in:
```
D:\Zip And Send\Java-Memory\Asset-Manager\obsidian\open-code-api.md
```

### Environment Variable

```bash
# Linux/Mac
export OPENAI_API_KEY="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"

# Windows PowerShell
$env:OPENAI_API_KEY = "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"

# Windows CMD
set OPENAI_API_KEY=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
```

### Auto-Load from File

Add to FraymusConvergence startup to auto-load:

```java
// Load OpenAI API key from file
Path apiKeyFile = Path.of("obsidian/open-code-api.md");
if (Files.exists(apiKeyFile)) {
    String apiKey = Files.readString(apiKeyFile).trim();
    System.setProperty("OPENAI_API_KEY", apiKey);
    System.out.println("   ✓ OpenAI API key loaded from file");
}
```

## Available Models

### GPT-4 Family

| Model | Context | Speed | Cost | Best For |
|-------|---------|-------|------|----------|
| `gpt-4-turbo` | 128K | Fast | Medium | Most tasks |
| `gpt-4` | 8K | Slower | High | Complex reasoning |
| `gpt-4-32k` | 32K | Slower | Higher | Long context |
| `gpt-4-vision` | 128K | Fast | Medium | Image analysis |

### GPT-3.5 Family

| Model | Context | Speed | Cost | Best For |
|-------|---------|-------|------|----------|
| `gpt-3.5-turbo` | 16K | Very Fast | Low | Simple tasks |
| `gpt-3.5-turbo-16k` | 16K | Very Fast | Low | Long prompts |

## Usage in Fraymus

### With BicameralPrism

```java
// BicameralPrism automatically uses OpenAI if configured
BicameralPrism llm = new BicameralPrism(auditLog);

// Set API key
llm.setApiKey(System.getProperty("OPENAI_API_KEY"));

// Use it
String response = llm.thinkIdeally("Explain quantum entanglement");
```

### With Commands

```bash
# Ask questions (uses OpenAI)
CONVERGENCE_01> ask Write a function to calculate fibonacci

# Smart evolution (OpenAI analysis + phi-evolution)
CONVERGENCE_01> smartevolve public class Test { }

# Code generation with OpenAI guidance
CONVERGENCE_01> generate APIServer RESTful API with authentication
```

### Direct API Call

```java
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.*;

// Initialize
OpenAiService service = new OpenAiService(apiKey);

// Create request
ChatCompletionRequest request = ChatCompletionRequest.builder()
    .model("gpt-4-turbo")
    .messages(List.of(
        new ChatMessage("system", "You are a helpful coding assistant"),
        new ChatMessage("user", "Write a binary search function in Java")
    ))
    .temperature(0.7)
    .build();

// Get response
ChatCompletionResult result = service.createChatCompletion(request);
String response = result.getChoices().get(0).getMessage().getContent();
```

## Integration with Fraymus Systems

### Self-Coding Evolution

```bash
CONVERGENCE_01> smartevolve public void processData(List<String> data) { }

# Phase 1: OpenAI GPT-4 analyzes code
# - Suggests improvements
# - Identifies patterns
# - Recommends optimizations

# Phase 2: SelfCodeEvolver applies phi-harmonics
# - Extracts Super-Gates
# - Routes through cortical brain
# - Applies phi-resonance

# Result: OpenAI intelligence + Phi-harmonic enhancement
```

### Darwinian Evolution

```java
// DarwinianLoop uses OpenAI for intelligent mutations
DarwinianLoop evolution = new DarwinianLoop(auditLog);

// OpenAI proposes upgrades
"Should I use Quantum physics or Einstein physics?"
→ GPT-4: "Use Quantum for better accuracy"
→ Mutation applied to DNA
```

### Code Generation

```bash
CONVERGENCE_01> generate QuantumAgent Self-aware quantum computing agent

# OpenAI provides:
# - Architectural guidance
# - Design patterns
# - Best practices

# LivingCodeGenerator creates:
# - Living circuits
# - Phi-encoded DNA
# - Quantum signatures
```

## Model Selection Guide

### For Code Tasks

**Complex Algorithms:** `gpt-4`
- Best reasoning
- Handles edge cases
- Optimal solutions

**Quick Code Gen:** `gpt-4-turbo`
- Fast responses
- Good quality
- 128K context

**Simple Tasks:** `gpt-3.5-turbo`
- Very fast
- Cost effective
- Good for boilerplate

### For Analysis

**Deep Analysis:** `gpt-4`
- Thorough examination
- Catches subtle issues
- Comprehensive insights

**Quick Review:** `gpt-4-turbo`
- Fast feedback
- Good coverage
- Balanced depth

### For Evolution

**Smart Evolution:** `gpt-4-turbo`
- Fast iteration
- Good suggestions
- Large context for full files

## Cost Optimization

### Pricing (Approximate)

**GPT-4 Turbo:**
- Input: $0.01 / 1K tokens
- Output: $0.03 / 1K tokens

**GPT-4:**
- Input: $0.03 / 1K tokens
- Output: $0.06 / 1K tokens

**GPT-3.5 Turbo:**
- Input: $0.0005 / 1K tokens
- Output: $0.0015 / 1K tokens

### Tips to Save

1. **Use GPT-3.5 for simple tasks**
2. **Cache common responses**
3. **Reduce temperature for deterministic outputs**
4. **Use shorter system prompts**
5. **Batch similar requests**

## Best Practices

### ✅ Do This

1. **Secure API Key** - Never commit to git
2. **Use Environment Variables** - Keep keys separate
3. **Monitor Usage** - Check OpenAI dashboard
4. **Choose Right Model** - Balance cost vs quality
5. **Handle Errors** - Implement retry logic
6. **Set Timeouts** - Avoid hanging requests

### ❌ Avoid This

1. **Hardcoding Keys** - Security risk
2. **Sharing Keys** - Each user needs their own
3. **Ignoring Rate Limits** - Respect API limits
4. **Wrong Model** - Don't use GPT-4 for simple tasks
5. **No Error Handling** - Always catch exceptions

## Advanced Features

### Function Calling

```java
// Define function
ChatFunction function = ChatFunction.builder()
    .name("calculate_fibonacci")
    .description("Calculate fibonacci number")
    .parameters(/* JSON schema */)
    .build();

// Request with function
ChatCompletionRequest request = ChatCompletionRequest.builder()
    .model("gpt-4-turbo")
    .messages(messages)
    .functions(List.of(function))
    .build();
```

### JSON Mode

```java
// Guaranteed JSON response
ChatCompletionRequest request = ChatCompletionRequest.builder()
    .model("gpt-4-turbo")
    .messages(messages)
    .responseFormat(new ChatCompletionResponseFormat("json_object"))
    .build();
```

### Vision

```java
// Analyze images
ChatMessage message = new ChatMessage("user", List.of(
    new ChatMessageContent("text", "What's in this image?"),
    new ChatMessageContent("image_url", imageUrl)
));
```

## Troubleshooting

### API Key Issues

```bash
# Check if key is set
echo $OPENAI_API_KEY

# Test with curl
curl https://api.openai.com/v1/models \
  -H "Authorization: Bearer $OPENAI_API_KEY"
```

### Rate Limits

- **Free Tier:** 3 RPM (requests per minute)
- **Tier 1:** 60 RPM
- **Tier 2:** 3500 RPM
- **Tier 3+:** Higher limits

**Solution:** Implement exponential backoff retry

### Timeout Issues

```java
// Set custom timeout
OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(60));
```

### Model Not Available

```bash
# List available models
curl https://api.openai.com/v1/models \
  -H "Authorization: Bearer $OPENAI_API_KEY"
```

## Security

### Protect Your Key

```bash
# Add to .gitignore
echo "obsidian/open-code-api.md" >> .gitignore

# Use environment variables
export OPENAI_API_KEY="$(cat obsidian/open-code-api.md)"

# Rotate keys regularly
# Visit: https://platform.openai.com/api-keys
```

### Monitor Usage

- **Dashboard:** https://platform.openai.com/usage
- **Set Limits:** Configure spending limits
- **Alerts:** Enable usage alerts

## Links

- **OpenAI Platform:** https://platform.openai.com
- **API Documentation:** https://platform.openai.com/docs
- **Pricing:** https://openai.com/pricing
- **Status:** https://status.openai.com
- **Community:** https://community.openai.com

---

**OpenAI provides the most advanced language models for code!** 🚀

Perfect for Fraymus's LLM Spine and intelligent code evolution.
