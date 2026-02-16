# OpenCode Zen

> Access curated coding-friendly models through OpenCode Zen API

## Description

OpenCode Zen provides a **curated list of models** recommended by the OpenCode team specifically for coding agents. It's a hosted model access path that uses an API key and offers high-quality models optimized for code generation, analysis, and debugging.

## Features

- **Curated Models:** Hand-picked models optimized for coding tasks
- **API Access:** Simple API key authentication
- **Multiple Providers:** Access to Claude, GPT-4, and other top models
- **Pay-per-request:** Flexible billing model
- **Beta Access:** Early access to cutting-edge coding models

## Setup

### CLI Setup

```bash
# Interactive setup
openclaw onboard --auth-choice opencode-zen

# Non-interactive setup
openclaw onboard --opencode-zen-api-key "$OPENCODE_API_KEY"
```

### Configuration

Add to your OpenClaw config:

```json
{
  "env": { 
    "OPENCODE_API_KEY": "sk-..." 
  },
  "agents": { 
    "defaults": { 
      "model": { 
        "primary": "opencode/claude-opus-4-6" 
      } 
    } 
  }
}
```

### Environment Variables

```bash
# Primary (recommended)
export OPENCODE_API_KEY="sk-your-api-key-here"

# Alternative
export OPENCODE_ZEN_API_KEY="sk-your-api-key-here"
```

## Available Models

OpenCode Zen provides access to:

- **Claude Opus 4-6** - `opencode/claude-opus-4-6` (Best for complex reasoning)
- **Claude Sonnet** - `opencode/claude-sonnet` (Balanced performance)
- **GPT-4 Turbo** - `opencode/gpt-4-turbo` (Fast and capable)
- **GPT-4** - `opencode/gpt-4` (High quality)
- **Codex Models** - Specialized for code generation

## Usage in Fraymus

### With OpenClaw Integration

```bash
# Set your API key
export OPENCODE_API_KEY="sk-your-key"

# Start FraymusConvergence
.\run-convergence.bat

# Use with LLM commands
CONVERGENCE_01> ask Write a function to calculate fibonacci

# The BicameralPrism will use OpenCode Zen models
```

### Direct Integration

```java
// Configure BicameralPrism to use OpenCode
BicameralPrism llm = new BicameralPrism(auditLog);
llm.setProvider("opencode");
llm.setModel("opencode/claude-opus-4-6");
llm.setApiKey(System.getenv("OPENCODE_API_KEY"));

// Use it
String response = llm.thinkIdeally("Explain quantum computing");
```

## Billing

- **Pay-per-request:** Only pay for what you use
- **Dashboard:** Monitor usage at OpenCode dashboard
- **Transparent Pricing:** Clear per-request costs
- **No Subscriptions:** No monthly fees

## Getting Your API Key

1. Visit OpenCode Zen website
2. Sign up for beta access
3. Add billing details
4. Copy your API key from dashboard
5. Set `OPENCODE_API_KEY` environment variable

## Advantages

### ✅ Benefits

- **Curated Quality:** Only the best coding models
- **Easy Setup:** Single API key for multiple models
- **Flexible:** Switch between models easily
- **Optimized:** Models fine-tuned for code tasks
- **Reliable:** Hosted infrastructure

### 🎯 Use Cases

1. **Code Generation** - Generate functions, classes, modules
2. **Code Review** - Analyze and suggest improvements
3. **Debugging** - Find and fix bugs
4. **Refactoring** - Improve code structure
5. **Documentation** - Generate comments and docs
6. **Testing** - Create unit tests

## Integration with Fraymus Systems

### With Self-Coding Systems

```bash
# Use OpenCode for smart evolution
CONVERGENCE_01> smartevolve public class Test { }

# OpenCode Zen analyzes code
# Then SelfCodeEvolver applies phi-harmonic enhancement
```

### With Skills

```bash
# Load coding skill
CONVERGENCE_01> skill coding-agent

# Ask OpenCode-powered LLM
CONVERGENCE_01> ask Generate a REST API for user management
```

### With Code Generation

```bash
# Generate living code with OpenCode guidance
CONVERGENCE_01> generate APIServer RESTful API server

# OpenCode Zen provides architectural guidance
# LivingCodeGenerator creates the entity
```

## Comparison

### OpenCode Zen vs Local Ollama

| Feature | OpenCode Zen | Local Ollama |
|---------|--------------|--------------|
| **Quality** | Top-tier models | Good models |
| **Speed** | Fast (cloud) | Depends on hardware |
| **Privacy** | Cloud-based | 100% local |
| **Cost** | Pay-per-request | Free (hardware cost) |
| **Setup** | API key only | Install + models |
| **Models** | Curated list | Many options |

**Use OpenCode Zen when:**
- You need the best quality
- You want easy setup
- You don't mind cloud processing

**Use Local Ollama when:**
- Privacy is critical
- You want offline capability
- You have good hardware

## Troubleshooting

### API Key Issues

```bash
# Check if key is set
echo $OPENCODE_API_KEY

# Test connection
openclaw doctor

# Re-authenticate
openclaw onboard --auth-choice opencode-zen
```

### Model Not Available

```bash
# List available models
openclaw models list --provider opencode

# Use default model
# (removes specific model from config)
```

### Rate Limits

- OpenCode Zen has rate limits
- Check dashboard for limits
- Upgrade plan if needed
- Implement retry logic

## Best Practices

### ✅ Do This:

1. **Secure API Key** - Never commit to git
2. **Use Environment Variables** - Keep keys separate
3. **Monitor Usage** - Check dashboard regularly
4. **Choose Right Model** - Balance cost vs quality
5. **Cache Results** - Avoid redundant requests

### ❌ Avoid This:

1. **Hardcoding Keys** - Security risk
2. **Sharing Keys** - Each user needs their own
3. **Ignoring Limits** - Respect rate limits
4. **Wrong Model** - Use coding-optimized models

## Advanced Configuration

### Multiple Models

```json
{
  "agents": {
    "defaults": {
      "model": {
        "primary": "opencode/claude-opus-4-6",
        "fallback": "opencode/gpt-4-turbo"
      }
    }
  }
}
```

### Custom Endpoints

```json
{
  "env": {
    "OPENCODE_API_KEY": "sk-...",
    "OPENCODE_BASE_URL": "https://custom.opencode.ai"
  }
}
```

## Links

- **OpenCode Zen:** https://opencode.ai/zen
- **Documentation:** https://docs.opencode.ai
- **Dashboard:** https://dashboard.opencode.ai
- **Support:** https://support.opencode.ai

---

**OpenCode Zen provides enterprise-grade AI models optimized for code!** 💎

Perfect for production Fraymus deployments requiring top-tier reasoning.
