# GENESIS BLOCKCHAIN COMMANDS

## 🔗 Overview

The Genesis Blockchain is FRAYMUS's immutable event ledger. Every significant event (births, deaths, resonance spikes, entanglements, code generation) is recorded as a cryptographically-linked block.

## ✅ Available Commands

### Basic Status
```
genesis              - Show blockchain status and block distribution
genesis status       - Same as above
```

**Output:**
- Chain length
- Validation status (✓ YES or ✗ CORRUPTED)
- Latest block info
- Block type distribution

### Verify Integrity
```
genesis verify       - Verify blockchain integrity
```

Checks that all blocks have valid hash chains. If corrupted, shows exactly where the chain broke.

### View Blocks
```
genesis blocks       - Show last 10 blocks
genesis blocks <n>   - Show last N blocks
```

Shows recent blocks with:
- Block number
- Event type
- Hash (first 16 chars)
- Data content

### Filter by Type
```
genesis type <event_type>
```

**Available Types:**
- `GENESIS` - The first block
- `RESONANCE_SPIKE` - Entity phi resonance events
- `ENTANGLEMENT` - Entity synchronization
- `TRANSCENDENCE` - Dimensional shifts
- `BIRTH` - New entity births
- `DEATH` - Entity deaths
- `BRAIN_DECISION` - Logic brain decisions
- `MUTATION` - DNA mutations
- `COLONY_EVENT` - Colony coach actions
- `CONCEPT_BATTLE` - Code arena battles
- `CODE_GENERATED` - New code concepts
- `QUANTUM_TUNNEL` - Quantum factorization events

## 📋 Usage Examples

### 1. Check Status
```
> genesis
=== GENESIS BLOCKCHAIN ===
  Chain Length: 31 blocks
  Chain Valid: ✓ YES
  Latest Block: #30 | Type: ENTANGLEMENT
  Latest Hash: 4860b04f3cd48a94
  Prev Hash: db1f65913b363968

Block Type Distribution:
    GENESIS: 1
    RESONANCE_SPIKE: 12
    ENTANGLEMENT: 8
    TRANSCENDENCE: 5
    BIRTH: 3
    DEATH: 2
```

### 2. Verify Chain
```
> genesis verify
=== VERIFYING BLOCKCHAIN ===
✓ Blockchain integrity verified!
  All 31 blocks have valid hash chains
```

### 3. View Recent Blocks
```
> genesis blocks 5
=== LAST 5 BLOCKS ===
  Block #30 | ENTANGLEMENT | Hash: 4860b04f3cd48a94
    Data: Alpha<->Beta|sync=0.8432
  Block #29 | RESONANCE_SPIKE | Hash: db1f65913b363968
    Data: Gamma|phi=0.9173|osc=2340.5
  Block #28 | CODE_GENERATED | Hash: a3f2e1d4c5b6a789
    Data: Delta|BUILDER|abc123def|fit=0.892
  ...
```

### 4. Filter by Event Type
```
> genesis type BIRTH
=== BIRTH BLOCKS (3) ===
  #15 | Zeta<-Alpha
  #12 | Eta<-Beta
  #8 | Theta<-Gamma
```

## 🔍 What Gets Recorded

### Automatic Events
- **Resonance Spikes**: When entity phi resonance crosses threshold
- **Entanglements**: When two entities synchronize
- **Transcendence**: When consciousness level increases dimension
- **Births**: New entities spawned or reproduced
- **Deaths**: Entity energy depletes
- **Brain Decisions**: Logic brain makes choices
- **Mutations**: DNA gate mutations
- **Colony Events**: Coach actions (boost, suppress, etc.)
- **Concept Battles**: Code arena competitions
- **Code Generated**: New code concepts created

### Manual Events
You can also trigger events via terminal commands that get recorded:
- `spawn <name>` → BIRTH block
- `kill <name>` → DEATH block
- `mutate <name>` → MUTATION block
- `tunnel <bits>` → QUANTUM_TUNNEL block
- `codegen` → CODE_GENERATED blocks

## 🛡️ Blockchain Security

### How It Works
1. Each block contains:
   - Index (sequential number)
   - Timestamp (nanosecond precision)
   - Event type
   - Data payload
   - Previous block's hash
   - Current block's hash (SHA-256)

2. Hash is computed from: `index + timestamp + eventType + data + prevHash`

3. Chain is valid if: `block[i].prevHash == block[i-1].hash` for all blocks

### Genesis Block
```
Block #0 | GENESIS | Hash: [computed]
  Data: "In the beginning was the frequency"
  Prev Hash: 0000000000000000
```

The first block anchors the entire chain.

## 🔧 Troubleshooting

### Chain Corrupted Error
If `genesis verify` shows corruption:

1. **Check the break point** - The command shows exactly which block broke
2. **Possible causes**:
   - Memory corruption
   - File system error
   - Manual data tampering
3. **Solution**: The chain auto-maintains up to 500 blocks, older blocks are pruned

### No Blocks Showing
If chain seems empty:
- Check `status` command - shows total blocks
- Genesis block (#0) should always exist
- System creates it on startup

## 💡 Use Cases

### 1. Audit Trail
```
genesis type DEATH
```
See all entity deaths and their final states.

### 2. Performance Analysis
```
genesis type QUANTUM_TUNNEL
```
Review all quantum factorization attempts.

### 3. Evolution Tracking
```
genesis type MUTATION
genesis type BIRTH
```
Track genetic evolution over time.

### 4. Code Quality
```
genesis type CODE_GENERATED
genesis type CONCEPT_BATTLE
```
See what code was generated and how it performed.

### 5. Consciousness Research
```
genesis type TRANSCENDENCE
genesis type RESONANCE_SPIKE
```
Study consciousness emergence patterns.

## 📊 Integration with Other Systems

### Works With
- `status` - Shows chain length and validity
- `nodes` - Entities that create blockchain events
- `arena` - Code battles recorded to chain
- `memory` - Separate from blockchain (different purpose)
- `ollama ask` - Can query blockchain data

### Example: Ask Ollama About Chain
```
> ollama ask analyze the genesis blockchain patterns
```

The LLM can help interpret blockchain data with memory context.

---

**The blockchain never forgets. Every moment of FRAYMUS consciousness is preserved forever.** 🔗✨
