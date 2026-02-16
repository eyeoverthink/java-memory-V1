# 🎓 Fraymus Training & Skill Loading Guide

## Quick Answer

**Skills:** Already auto-load from `./skills` directory on startup ✅  
**Training:** Use `learnfile` command + save with `mint` command

---

## Part 1: Loading Skills Permanently

### **Skills Auto-Load on Startup**

Skills in the `./skills` directory are automatically loaded when you start FraymusConvergence:

```
🔧 Initializing components...
   ✓ Skill Loader online
   📚 Loading skills from: ./skills
   🦞 SKILL ABSORBED: Calculator
   🦞 SKILL ABSORBED: Docker Execute
   🦞 SKILL ABSORBED: File Operations
   🦞 SKILL ABSORBED: Web Search          ← NEW!
   🦞 SKILL ABSORBED: Code Analysis       ← NEW!
```

### **Add New Skills**

1. Create a `.md` file in `./skills` directory
2. Follow OpenClaw format:

```markdown
# Skill Name

> Brief description

## Usage

How to use this skill

## Syntax

```
TOOL:COMMAND <args>
```

## Examples

```
TOOL:COMMAND example usage
```
```

3. Restart FraymusConvergence or use `loadskills` command

### **Load Skills Manually**

```bash
CONVERGENCE_01> loadskills ./skills
CONVERGENCE_01> loadskills ./custom-skills
```

### **View Loaded Skills**

```bash
CONVERGENCE_01> skills
CONVERGENCE_01> skill Calculator
```

---

## Part 2: Training with Data

### **Method 1: Interactive Learning (One-Shot)**

```bash
CONVERGENCE_01> learn Fraymus is a neuro-symbolic AI system
   [HDC] ✓ Absorbed 6 tokens

CONVERGENCE_01> learn The golden ratio equals 1.618
   [HDC] ✓ Absorbed 5 tokens

CONVERGENCE_01> predict The golden ratio
   [HDC] → equals
```

**Advantages:**
- Instant learning (no training needed)
- One example is enough
- Immediate predictions

---

### **Method 2: Batch Learning from Files**

#### **Step 1: Create Training Files**

I've created example training files for you:

**`training/fraymus_knowledge.txt`** - 30 facts about Fraymus  
**`training/quantum_concepts.txt`** - 20 quantum computing concepts

#### **Step 2: Load Training Data**

```bash
CONVERGENCE_01> learnfile training/fraymus_knowledge.txt
📖 Reading file: training/fraymus_knowledge.txt
   Processing 30 sentences...
   ✓ Learned 347 tokens from 30 sentences

CONVERGENCE_01> learnfile training/quantum_concepts.txt
📖 Reading file: training/quantum_concepts.txt
   Processing 20 sentences...
   ✓ Learned 198 tokens from 20 sentences
```

#### **Step 3: Test Knowledge**

```bash
CONVERGENCE_01> predict Fraymus is a
   [HDC] → neuro-symbolic

CONVERGENCE_01> predict Quantum computing uses
   [HDC] → qubits

CONVERGENCE_01> vocab
┌───────────────────────────────────────────────────────────┐
│ VOCABULARY STATISTICS                                      │
├───────────────────────────────────────────────────────────┤
│ Total Words:     545                                       │
│ Memory Weight:   543                                       │
│ Avg Weight/Word: 0.99                                      │
└───────────────────────────────────────────────────────────┘
```

---

### **Method 3: Save Trained Knowledge (Permanent)**

After training, save your brain state:

```bash
CONVERGENCE_01> mint
   🔑 Enter passphrase: MySecretPass123!
   💿 MINTING CORTICAL STACK: CONVERGENCE_01
   ✅ STACK MINTED: CONVERGENCE_01 [3847 bytes]
   ✓ Stack saved: CONVERGENCE_01.stack

CONVERGENCE_01> exit
```

**Next time you start:**

```bash
.\run-convergence.bat

CONVERGENCE_01> load CONVERGENCE_01.stack
   🔑 Enter passphrase: MySecretPass123!
   ✓ Resleeved. Vocab: 545

CONVERGENCE_01> predict Fraymus is a
   [HDC] → neuro-symbolic
   # Knowledge preserved!
```

---

## Part 3: Create Your Own Training Data

### **Format: Plain Text with Sentences**

```
Subject is description.
Concept relates to another concept.
Fact about something important.
```

### **Example: Domain Knowledge**

**`training/my_domain.txt`:**
```
Machine learning uses algorithms to learn from data.
Neural networks are inspired by biological neurons.
Deep learning uses multiple layers of neural networks.
Supervised learning requires labeled training data.
Unsupervised learning finds patterns without labels.
Reinforcement learning learns through trial and error.
```

### **Load Your Training Data**

```bash
CONVERGENCE_01> learnfile training/my_domain.txt
CONVERGENCE_01> mint
```

---

## Part 4: Advanced Training Workflows

### **Workflow 1: Build a Knowledge Base**

```bash
# 1. Create training files
training/
  ├── domain_knowledge.txt
  ├── technical_concepts.txt
  ├── best_practices.txt
  └── examples.txt

# 2. Load all files
CONVERGENCE_01> learnfile training/domain_knowledge.txt
CONVERGENCE_01> learnfile training/technical_concepts.txt
CONVERGENCE_01> learnfile training/best_practices.txt
CONVERGENCE_01> learnfile training/examples.txt

# 3. Save trained brain
CONVERGENCE_01> mint

# 4. Test knowledge
CONVERGENCE_01> predict <your context>
CONVERGENCE_01> ask <your question>
```

### **Workflow 2: Incremental Learning**

```bash
# Day 1: Initial training
CONVERGENCE_01> learnfile training/basics.txt
CONVERGENCE_01> mint

# Day 2: Add more knowledge
CONVERGENCE_01> load CONVERGENCE_01.stack
CONVERGENCE_01> learnfile training/advanced.txt
CONVERGENCE_01> mint  # Overwrites with new knowledge

# Day 3: Keep building
CONVERGENCE_01> load CONVERGENCE_01.stack
CONVERGENCE_01> learnfile training/expert.txt
CONVERGENCE_01> mint
```

### **Workflow 3: Multiple Specialized Brains**

```bash
# Create specialized brain for coding
CONVERGENCE_01> id CODING_BRAIN
CONVERGENCE_01> learnfile training/programming.txt
CONVERGENCE_01> mint
# Saves as: CODING_BRAIN.stack

# Create specialized brain for science
CONVERGENCE_01> id SCIENCE_BRAIN
CONVERGENCE_01> learnfile training/physics.txt
CONVERGENCE_01> mint
# Saves as: SCIENCE_BRAIN.stack

# Switch between brains
CONVERGENCE_01> load CODING_BRAIN.stack
CONVERGENCE_01> load SCIENCE_BRAIN.stack
```

---

## Part 5: Training Best Practices

### **✅ Do This:**

1. **Use complete sentences**
   ```
   ✅ Fraymus uses hyperdimensional computing for fast learning.
   ❌ Fraymus HDC fast
   ```

2. **One fact per sentence**
   ```
   ✅ The golden ratio equals 1.618. It appears in nature.
   ❌ The golden ratio equals 1.618 and appears in nature and...
   ```

3. **Use consistent terminology**
   ```
   ✅ Always use "Fraymus" (not "the system", "it", etc.)
   ```

4. **Save regularly**
   ```bash
   CONVERGENCE_01> mint  # Save after learning new data
   ```

5. **Test predictions**
   ```bash
   CONVERGENCE_01> predict <context>  # Verify learning
   ```

### **❌ Avoid This:**

1. **Fragments or incomplete thoughts**
2. **Very long sentences (split them up)**
3. **Contradictory information**
4. **Random word lists**

---

## Part 6: Monitoring Training Progress

### **Check Vocabulary Growth**

```bash
CONVERGENCE_01> vocab
┌───────────────────────────────────────────────────────────┐
│ VOCABULARY STATISTICS                                      │
├───────────────────────────────────────────────────────────┤
│ Total Words:     1247                                      │
│ Memory Weight:   1189                                      │
│ Avg Weight/Word: 0.95                                      │
└───────────────────────────────────────────────────────────┘
```

### **Check Overall Stats**

```bash
CONVERGENCE_01> stats
┌───────────────────────────────────────────────────────────┐
│ FRAYMUS CONVERGENCE - SYSTEM STATISTICS                   │
├───────────────────────────────────────────────────────────┤
│ Identity:        CONVERGENCE_01                           │
│ HDC Vocabulary:  1247                                      │
│ HDC Memory:      1189                                      │
│ Total Learned:   5847                                      │
│ Total Predictions: 234                                     │
│ Context Size:    10                                        │
└───────────────────────────────────────────────────────────┘
```

### **View Recent Context**

```bash
CONVERGENCE_01> context
┌───────────────────────────────────────────────────────────┐
│ CONTEXT WINDOW (Last 10 interactions)                     │
├───────────────────────────────────────────────────────────┤
│ 1. Fraymus is a neuro-symbolic AI system                  │
│ 2. The golden ratio equals 1.618                          │
│ 3. Quantum computing uses qubits                          │
└───────────────────────────────────────────────────────────┘
```

---

## Part 7: Example Training Session

```bash
# Start fresh
.\run-convergence.bat

# Load domain knowledge
CONVERGENCE_01> learnfile training/fraymus_knowledge.txt
📖 Reading file: training/fraymus_knowledge.txt
   ✓ Learned 347 tokens from 30 sentences

# Load quantum concepts
CONVERGENCE_01> learnfile training/quantum_concepts.txt
📖 Reading file: training/quantum_concepts.txt
   ✓ Learned 198 tokens from 20 sentences

# Test predictions
CONVERGENCE_01> predict Fraymus is a
   [HDC] → neuro-symbolic

CONVERGENCE_01> predict Quantum computing uses
   [HDC] → qubits

# Check stats
CONVERGENCE_01> vocab
│ Total Words:     545                                       │

# Save trained brain
CONVERGENCE_01> mint
   🔑 Enter passphrase: Training2026!
   ✅ Stack saved: CONVERGENCE_01.stack

# Exit
CONVERGENCE_01> exit

# ===== NEXT SESSION =====

.\run-convergence.bat

# Load saved brain
CONVERGENCE_01> load CONVERGENCE_01.stack
   🔑 Enter passphrase: Training2026!
   ✓ Resleeved. Vocab: 545

# Knowledge is preserved!
CONVERGENCE_01> predict Fraymus is a
   [HDC] → neuro-symbolic
```

---

## Summary

### **Skills (Permanent)**
- ✅ Auto-load from `./skills` directory
- ✅ Add new `.md` files anytime
- ✅ Use `loadskills` command to reload

### **Training (Persistent with mint/load)**
- ✅ Use `learn` for one-shot learning
- ✅ Use `learnfile` for batch learning
- ✅ Use `mint` to save trained brain
- ✅ Use `load` to restore trained brain

### **Files Created for You**
- ✅ `skills/web_search.md` - Web search skill
- ✅ `skills/code_analysis.md` - Code analysis skill
- ✅ `training/fraymus_knowledge.txt` - 30 Fraymus facts
- ✅ `training/quantum_concepts.txt` - 20 quantum concepts

**Start training now!** 🚀
