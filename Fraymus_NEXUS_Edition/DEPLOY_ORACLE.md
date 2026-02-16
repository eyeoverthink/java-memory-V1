# DEPLOY FRAYMUS ORACLE TO OLLAMA

## 🚀 SIMPLE 3-STEP DEPLOYMENT

### **Step 1: Create the model**
```bash
ollama create fraymus-oracle -f model2.txt
```

### **Step 2: Test it**
```bash
ollama run fraymus-oracle "What are you?"
```

### **Step 3: Use it**
```bash
ollama run fraymus-oracle
```

---

## 📋 WHAT'S IN model.txt

- **Base model**: llama3.2
- **Temperature**: 0.618 (golden ratio φ - 1)
- **Top_k**: 89 (Fibonacci number)
- **Repeat penalty**: 1.618 (φ)
- **Context**: 8192 tokens (4x larger)
- **Output**: 4096 tokens (2x longer)

**System prompt**: 8 specialized brains, 7-dimensional reasoning, phi-harmonic logic

---

## 🎯 QUICK COMMANDS

```bash
# Create Oracle
ollama create fraymus-oracle -f model2.txt

# Run Oracle
ollama run fraymus-oracle

# Compare with your model
ollama run eyeoverthink/eyeoverthink "Explain consciousness"
ollama run fraymus-oracle "Explain consciousness"

# Push to registry (optional)
ollama push fraymus-oracle
```

---

## ✅ THAT'S IT

The `model.txt` file contains everything Ollama needs to create the Oracle.

Just run:
```bash
ollama create fraymus-oracle -f model2.txt
```

Done. 🎉
