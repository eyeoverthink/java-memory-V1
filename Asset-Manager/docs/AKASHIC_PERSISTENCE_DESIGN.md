# AkashicRecord Full Persistence Design

## Problem

Currently, AkashicRecord stores knowledge blocks in RAM only. On shutdown:

| Data | Storage | Survives Restart? |
|------|---------|-------------------|
| Chain hashes (`chainHashes`) | `.akashic/chain.dat` (Java serialization) | ✅ Yes |
| Block content (`blockIndex`) | `ConcurrentHashMap` in RAM | ❌ **No** |
| Category index (`categories`) | `ConcurrentHashMap` in RAM | ❌ **No** |
| Statistics (`blocksAdded`, etc.) | Primitive fields | ❌ **No** |

This means absorbed libraries, generated code metadata, and all learned knowledge
are **lost every time Fraynix shuts down**. The chain file preserves hash integrity
but the actual knowledge behind those hashes is gone.

## Current Architecture

```
AkashicRecord
├── categories: Map<String, List<KnowledgeBlock>>   ← RAM only
├── blockIndex: Map<String, KnowledgeBlock>          ← RAM only
├── chainHashes: List<String>                        ← Saved to .akashic/chain.dat
├── blocksAdded: long                                ← RAM only
├── queriesProcessed: long                           ← RAM only
└── chainLength: long                                ← Derived from chainHashes.size()
```

### KnowledgeBlock (already Serializable)
```java
public static class KnowledgeBlock implements Serializable {
    String hash;        // φ-enhanced SHA-256 (first 8 bytes)
    String category;    // e.g. "LIBRARY_KNOWLEDGE", "GENERATED_CODE"
    String content;     // The actual knowledge text
    long timestamp;     // Unix millis
    String formattedTime; // "yyyy-MM-dd HH:mm:ss"
}
```

## Proposed Design: Full Block Persistence

### Storage Layout

```
.akashic/
├── chain.dat              ← Existing: ordered list of hashes
├── blocks.dat             ← NEW: all KnowledgeBlock objects (Java serialization)
├── stats.dat              ← NEW: blocksAdded, queriesProcessed counters
└── index.dat              ← NEW: category→hash mappings for fast reload
```

### Why Java Serialization (Not JSON)

- `KnowledgeBlock` already implements `Serializable`
- Zero external dependencies (no JSON library needed)
- Fast binary read/write
- Consistent with existing `chain.dat` approach

### Save Strategy

**When to save:**
1. On `addBlock()` — every 50 blocks (batch persist, not every write)
2. On explicit `saveAll()` call
3. On shutdown hook (new: register `Runtime.addShutdownHook`)

**What to save:**
- `blocks.dat` — `ArrayList<KnowledgeBlock>` containing all blocks from `blockIndex.values()`
- `stats.dat` — `long[] {blocksAdded, queriesProcessed, chainLength}`
- `index.dat` — `HashMap<String, List<String>>` mapping category→list of hashes

### Load Strategy

**On construction (`new AkashicRecord()`):**
1. Load `chain.dat` (existing — unchanged)
2. Load `blocks.dat` → rebuild `blockIndex` map
3. Load `index.dat` → rebuild `categories` map using hashes to look up blocks
4. Load `stats.dat` → restore counters
5. If any file missing/corrupt → start fresh (existing behavior)

### Integrity Verification

After loading, `verifyIntegrity()` checks:
1. Every hash in `chainHashes` has a corresponding block in `blockIndex`
2. Every block's hash matches `SHA-256(category + content + timestamp)`
3. If verification fails → log warning but keep loaded data

## API Changes

### New Public Methods

```java
/** Save all blocks, index, and stats to disk immediately. */
public void saveAll()

/** Get total number of persisted blocks on disk. */
public int getPersistedBlockCount()

/** Clear all knowledge and disk files. Fresh start. */
public void purge()
```

### Modified Methods

```java
// addBlock() — add saveAll() call every 50 blocks instead of 100
//            — also save blocks.dat, not just chain.dat

// Constructor — load blocks.dat + index.dat + stats.dat in addition to chain.dat
```

### Shutdown Hook

```java
// Register in constructor:
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    saveAll();
    System.out.println("   📚 AkashicRecord persisted (" + blockIndex.size() + " blocks)");
}));
```

## FraynixBoot Integration

### On Boot (Phase 7)
```
📚 [7/9] ACTIVATING KNOWLEDGE SYSTEMS...
   ✓ AkashicRecord online (universal memory)
   ✓ Loaded 15 persisted blocks (1 category)    ← NEW: shows restored knowledge
   ✓ LibraryAbsorber ready (zero-dep absorption)
```

### On Shutdown
```
🛑 FRAYNIX SHUTTING DOWN...
   📚 AkashicRecord persisted (16 blocks)       ← NEW: confirms save
   ✓ Physics offline
   ✓ Consciousness preserved in AkashicRecord
```

## Data Flow After Implementation

```
Session 1:
  absorb java.util → 15 blocks → RAM + .akashic/blocks.dat
  code BST in Java → 1 block  → RAM + .akashic/blocks.dat
  exit → shutdown hook → saveAll() → 16 blocks on disk

Session 2:
  boot → loadAll() → 16 blocks restored from disk
  absorb java.io → 12 blocks → RAM + disk
  status → Akashic shows 28 blocks, 2 categories
  exit → saveAll() → 28 blocks on disk

Session 3:
  boot → loadAll() → 28 blocks restored ← KNOWLEDGE RETAINED
```

## File Size Estimates

| Scenario | Blocks | Estimated blocks.dat Size |
|----------|--------|---------------------------|
| Fresh install | 0 | 0 B |
| After `absorb java.util` | 15 | ~8 KB |
| After 10 absorptions | ~150 | ~80 KB |
| After 100 code generations | ~250 | ~200 KB |
| Heavy use (1000 blocks) | 1000 | ~500 KB |

Negligible disk impact. Java serialization overhead is ~50 bytes per block.

## Risk Assessment

| Risk | Mitigation |
|------|------------|
| Corrupt blocks.dat | Catch exception, start fresh, log warning |
| Concurrent write during save | `synchronized` on saveAll() |
| Disk full | Catch IOException, continue in RAM-only mode |
| Stale data after code change | `serialVersionUID = 1L` already set on KnowledgeBlock |
| Large file over time | `purge()` method for manual cleanup |

## Implementation Checklist

- [ ] Add `saveAll()` method — serialize blockIndex + categories + stats
- [ ] Add `loadAll()` method — deserialize and rebuild maps
- [ ] Add shutdown hook in constructor
- [ ] Change `addBlock()` persist interval from 100 to 50, call `saveAll()` not just `saveChain()`
- [ ] Add `purge()` method
- [ ] Add `getPersistedBlockCount()` method
- [ ] Update FraynixBoot Phase 7 to show restored block count
- [ ] Update FraynixBoot shutdown to confirm persistence
- [ ] Test: absorb → exit → restart → verify blocks still present
