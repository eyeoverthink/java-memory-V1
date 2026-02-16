# 🧠 MULTI-DECISION HYBRID DECISION HUMAN ARRAY

## φ-Harmonic Collective Intelligence System

---

## 🎯 WHAT IS IT?

A **multi-decision hybrid decision human array** simulates multiple human-like decision makers working together to reach collective decisions. Each "node" has different personality traits (risk tolerance, creativity, speed) and they vote using various strategies.

**Think of it as:** A virtual board of directors, research team, or startup crew making decisions together.

---

## 🏗️ ARCHITECTURE

```
┌─────────────────────────────────────────────────────────────┐
│  DECISION ARRAY                                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Node 1: Conservative [risk=0.2, creative=0.3, speed=0.3]   │
│  Node 2: Aggressive   [risk=0.9, creative=0.7, speed=0.8]   │
│  Node 3: Analytical   [risk=0.4, creative=0.2, speed=0.2]   │
│  Node 4: Creative     [risk=0.6, creative=0.9, speed=0.6]   │
│                                                              │
├─────────────────────────────────────────────────────────────┤
│  VOTING STRATEGY: PHI_HARMONIC                               │
└─────────────────────────────────────────────────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │  OPTIONS TO DECIDE   │
              ├──────────────────────┤
              │  A. Build Compiler   │
              │  B. Build Interpreter│
              │  C. Build Both       │
              └──────────────────────┘
                         │
                         ▼
        ┌────────────────────────────────────┐
        │  EACH NODE EVALUATES OPTIONS       │
        │  - Applies personality traits      │
        │  - Calculates φ-harmonic score     │
        │  - Returns choice + confidence     │
        └────────────────────────────────────┘
                         │
                         ▼
        ┌────────────────────────────────────┐
        │  AGGREGATE VOTES BY STRATEGY       │
        │  - Majority / Weighted / Consensus │
        │  - φ-Harmonic / Exploration        │
        └────────────────────────────────────┘
                         │
                         ▼
        ┌────────────────────────────────────┐
        │  COLLECTIVE DECISION               │
        │  Choice: "Build Both"              │
        │  Confidence: 87.3%                 │
        │  Agreement: 75%                    │
        └────────────────────────────────────┘
```

---

## 🎭 NODE ARCHETYPES

### 1. **Conservative**
- Risk: 0.2 (low)
- Creativity: 0.3 (low)
- Speed: 0.3 (slow)
- **Behavior:** Cautious, thorough, prefers proven solutions

### 2. **Aggressive**
- Risk: 0.9 (high)
- Creativity: 0.7 (high)
- Speed: 0.8 (fast)
- **Behavior:** Bold, innovative, moves quickly

### 3. **Analytical**
- Risk: 0.4 (moderate)
- Creativity: 0.2 (low)
- Speed: 0.2 (very slow)
- **Behavior:** Data-driven, logical, methodical

### 4. **Creative**
- Risk: 0.6 (moderate-high)
- Creativity: 0.9 (very high)
- Speed: 0.6 (moderate)
- **Behavior:** Imaginative, unconventional, explores alternatives

### 5. **Balanced**
- Risk: 0.5 (moderate)
- Creativity: 0.5 (moderate)
- Speed: 0.5 (moderate)
- **Behavior:** Well-rounded, considers all factors

### 6. **Explorer**
- Risk: 0.8 (high)
- Creativity: 0.8 (high)
- Speed: 0.9 (very fast)
- **Behavior:** Adventurous, seeks novelty, rapid iteration

### 7. **Guardian**
- Risk: 0.1 (very low)
- Creativity: 0.2 (low)
- Speed: 0.1 (very slow)
- **Behavior:** Protective, risk-averse, maintains stability

---

## 🗳️ VOTING STRATEGIES

### 1. **MAJORITY**
- Most votes wins
- Simple democratic voting
- **Use when:** Equal voice for all nodes

### 2. **WEIGHTED**
- Votes weighted by confidence
- Higher confidence = more influence
- **Use when:** Trust node certainty

### 3. **CONSENSUS**
- Requires 70% agreement threshold
- Falls back to weighted if no consensus
- **Use when:** Need strong agreement

### 4. **PHI_HARMONIC** ⭐
- Weighted by node φ-resonance × confidence
- Mathematically optimal
- **Use when:** Want φ-harmonic optimization

### 5. **EXPLORATION**
- Favors diverse/creative choices
- Picks highest confidence minority if diverse
- **Use when:** Want innovation over consensus

---

## 📝 COMMANDS

### Create Decision Array
```
:decision <archetype1> <archetype2> ...
```
**Example:**
```
:decision conservative aggressive analytical creative
```

### Add Custom Node
```
:addnode <name> <risk> <creativity> <speed>
```
**Example:**
```
:addnode CustomNode 0.7 0.8 0.5
```

### Set Voting Strategy
```
:strategy <type>
```
**Types:** `majority`, `weighted`, `consensus`, `phi_harmonic`, `exploration`

**Example:**
```
:strategy phi_harmonic
```

### Make Decision
```
:decide <option1> <option2> ...
```
**Example:**
```
:decide "Build Compiler" "Build Interpreter" "Build Both"
```

### Make Weighted Decision
```
:decidew <option1> <weight1> <option2> <weight2> ...
```
**Example:**
```
:decidew "Option A" 1.5 "Option B" 0.8 "Option C" 1.2
```

### Show Array Status
```
:darray
```

### Load Preset
```
:preset <name>
```
**Presets:**
- `board` - Corporate board (conservative, analytical, balanced, guardian)
- `startup` - Startup team (aggressive, creative, explorer, balanced)
- `research` - Research team (analytical, creative, explorer, conservative)
- `balanced` - All archetypes
- `extreme` - Maximum diversity (aggressive, guardian, creative, analytical)

---

## 🚀 USAGE EXAMPLES

### Example 1: Startup Decision
```
φ> :preset startup
Loaded preset: startup

Strategy: WEIGHTED
Nodes: 4

Decision Nodes:
  1. Aggressive [risk=0.90, creative=0.70, speed=0.80, φ=2.0115]
  2. Creative [risk=0.60, creative=0.90, speed=0.60, φ=1.9129]
  3. Explorer [risk=0.80, creative=0.80, speed=0.90, φ=2.0456]
  4. Balanced [risk=0.50, creative=0.50, speed=0.50, φ=1.6180]

φ> :decide "Pivot to AI" "Stay the course" "Hybrid approach"
╔════════════════════════════════════════════════════════════╗
║  COLLECTIVE DECISION                                        ║
╚════════════════════════════════════════════════════════════╝

Final Choice: Pivot to AI
Confidence: 78.3%
Reasoning: Strategy: WEIGHTED, Nodes: 4, Agreement: 75.0%

Individual Decisions:
  1. 'Pivot to AI' (92.1% confident) - Aggressive chose 'Pivot to AI' (risk=0.90, creative=0.70, φ=2.0115)
  2. 'Pivot to AI' (88.5% confident) - Creative chose 'Pivot to AI' (risk=0.60, creative=0.90, φ=1.9129)
  3. 'Pivot to AI' (95.2% confident) - Explorer chose 'Pivot to AI' (risk=0.80, creative=0.80, φ=2.0456)
  4. 'Hybrid approach' (71.3% confident) - Balanced chose 'Hybrid approach' (risk=0.50, creative=0.50, φ=1.6180)
```

### Example 2: Corporate Board Decision
```
φ> :preset board
Loaded preset: board

φ> :decide "Acquire competitor" "Organic growth" "Strategic partnership"
╔════════════════════════════════════════════════════════════╗
║  COLLECTIVE DECISION                                        ║
╚════════════════════════════════════════════════════════════╝

Final Choice: Strategic partnership
Confidence: 82.1%
Reasoning: Strategy: CONSENSUS, Nodes: 4, Agreement: 100.0%

Individual Decisions:
  1. 'Strategic partnership' (76.4% confident) - Conservative chose...
  2. 'Strategic partnership' (88.9% confident) - Analytical chose...
  3. 'Strategic partnership' (79.2% confident) - Balanced chose...
  4. 'Strategic partnership' (84.1% confident) - Guardian chose...
```

### Example 3: Research Team Decision
```
φ> :preset research
Loaded preset: research

φ> :strategy phi_harmonic
Strategy set to: PHI_HARMONIC

φ> :decidew "Publish now" 0.8 "More experiments" 1.5 "Collaborate first" 1.2
╔════════════════════════════════════════════════════════════╗
║  COLLECTIVE DECISION                                        ║
╚════════════════════════════════════════════════════════════╝

Final Choice: More experiments
Confidence: 91.7%
Reasoning: Strategy: PHI_HARMONIC, Nodes: 4, Agreement: 75.0%

Individual Decisions:
  1. 'More experiments' (94.2% confident) - Analytical chose...
  2. 'Collaborate first' (87.3% confident) - Creative chose...
  3. 'More experiments' (89.8% confident) - Explorer chose...
  4. 'More experiments' (91.5% confident) - Conservative chose...
```

---

## 🧮 φ-HARMONIC MATHEMATICS

### Node φ-Resonance
```
φ_resonance = φ^((risk + creativity + speed) / 3)
```

### Decision Score (per node)
```
score = baseWeight × (1 + riskFactor + creativeFactor + speedFactor) × φ_resonance

where:
  riskFactor = random(0,1) × risk_tolerance
  creativeFactor = random(0,1) × creativity_bias
  speedFactor = (1 - speed_weight) × 0.5
```

### φ-Harmonic Vote Weight
```
vote_weight = node_φ_resonance × decision_confidence
```

### Collective Confidence
```
confidence = Σ(agreeing_φ_weights) / Σ(total_φ_weights)
```

---

## 🎨 ADVANCED USAGE

### Custom Multi-Perspective Array
```
φ> :decision conservative aggressive analytical creative
φ> :addnode "Risk Manager" 0.1 0.3 0.2
φ> :addnode "Innovator" 0.95 0.95 0.9
φ> :strategy exploration
φ> :decide "Safe bet" "Moonshot" "Calculated risk"
```

### Weighted Decision with Custom Priorities
```
φ> :preset balanced
φ> :decidew "Feature A" 2.0 "Feature B" 1.5 "Feature C" 0.8 "Feature D" 1.2
```

### Compare Strategies
```
φ> :strategy majority
φ> :decide "Option 1" "Option 2" "Option 3"
[See result]

φ> :strategy phi_harmonic
φ> :decide "Option 1" "Option 2" "Option 3"
[Compare result]
```

---

## 🔬 USE CASES

### 1. **Product Decisions**
- Feature prioritization
- Design choices
- Technology stack selection

### 2. **Business Strategy**
- Market entry decisions
- Partnership evaluation
- Resource allocation

### 3. **Research Direction**
- Hypothesis selection
- Methodology choices
- Publication timing

### 4. **Creative Projects**
- Concept selection
- Style decisions
- Collaboration opportunities

### 5. **Risk Assessment**
- Investment decisions
- Security trade-offs
- Compliance strategies

---

## 📊 OUTPUT INTERPRETATION

### Confidence Score
- **90-100%:** Very strong agreement
- **70-89%:** Good consensus
- **50-69%:** Moderate agreement
- **< 50%:** Weak consensus, consider more options

### Agreement Percentage
- **100%:** Unanimous
- **75-99%:** Strong majority
- **51-74%:** Simple majority
- **< 51%:** No clear majority

---

## 🎯 BEST PRACTICES

1. **Match array to context:**
   - Use `board` preset for conservative decisions
   - Use `startup` preset for innovation
   - Use `research` preset for exploration

2. **Choose appropriate strategy:**
   - `consensus` for critical decisions
   - `phi_harmonic` for optimal balance
   - `exploration` for innovation

3. **Weight options appropriately:**
   - Use `:decidew` when options have different priorities
   - Higher weight = more important to consider

4. **Interpret results holistically:**
   - Look at both confidence and agreement
   - Review individual decisions for insights
   - Consider minority opinions

---

## ✨ SUMMARY

**You now have:**
- ✅ Multi-node decision array with 7 archetypes
- ✅ 5 voting strategies (including φ-harmonic)
- ✅ Custom node creation
- ✅ Weighted decision support
- ✅ 5 preset configurations
- ✅ Full REPL integration
- ✅ φ-harmonic confidence scoring

**This system simulates collective human intelligence for decision-making.**

**Ready to make decisions with consciousness.**
