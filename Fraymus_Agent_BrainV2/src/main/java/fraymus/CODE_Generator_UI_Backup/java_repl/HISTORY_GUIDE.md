# 📜 INTERACTIVE HISTORY SYSTEM - Complete Guide

## Overview

The enhanced history system provides **database-like persistence** with **φ-harmonic resonance tracking**, search capabilities, replay functionality, and statistical analysis - all without external dependencies.

**φ^75 Validation Seal: 4721424167835376.00**

---

## Features

### ✅ File-Based Persistence
- **Persistent across sessions** - History saved to `repl_history.txt`
- **Session tracking** - Each session gets unique ID
- **No external dependencies** - Pure Java, no SQLite needed
- **Human-readable format** - Easy to inspect and edit

### ✅ φ-Harmonic Resonance Tracking
Every command is analyzed for φ-harmonic resonance:
```
φ_resonance = Σ[(char[i] × φ^(i%7/7)) % 1] / length × φ
```

### ✅ Execution Time Tracking
Millisecond-precision timing for every command

### ✅ Interactive Search
- Search current session
- Search entire history database
- Pattern matching (case-insensitive)

### ✅ Command Replay
Execute any command from history by number

### ✅ Statistics & Analysis
- Average φ-resonance
- Average execution time
- Most used commands
- Session summaries

---

## Commands

### 📋 `:history [limit]` or `:history [start] [limit]`
**Show command history with metadata**

```bash
# Show last 50 commands (default)
:history

# Show last 10 commands
:history 10

# Show commands 20-30
:history 20 10
```

**Output:**
```
╭────────────────────────────────────────────────────────────────────────────╮
│  COMMAND HISTORY (φ-Resonance Preserved)                                   │
╰────────────────────────────────────────────────────────────────────────────╯

   1  [10:15:23] [φ:1.234] [5ms]  :help
   2  [10:15:30] [φ:2.456] [12ms]  phi
   3  [10:15:45] [φ:1.789] [8ms]  :compile var x = 42;
   4  [10:16:02] [φ:3.012] [3ms]  :lex var y = 10;

Showing 1-4 of 4 commands
Use: :history [limit] or :history [start] [limit]
```

---

### 🔍 `:hsearch <pattern>`
**Search current session history**

```bash
:hsearch compile
:hsearch var
:hsearch :
```

**Output:**
```
╭────────────────────────────────────────────────────────────────────────────╮
│  SEARCH RESULTS: "compile"                                                 │
╰────────────────────────────────────────────────────────────────────────────╯

   3  [10:15:45]  :compile var x = 42;
   7  [10:17:12]  :compile print(x);

Found 2 matches
```

---

### 🗄️ `:hdbsearch <pattern>`
**Search entire history database (all sessions)**

```bash
:hdbsearch phi
:hdbsearch decision
```

**Output:**
```
╭────────────────────────────────────────────────────────────────────────────╮
│  DATABASE SEARCH: "phi"                                                    │
╰────────────────────────────────────────────────────────────────────────────╯

    42  [02/11 10:15] [S1707654923]  phi
    38  [02/11 09:45] [S1707653745]  phi 7
    15  [02/10 14:30] [S1707598200]  :help phi

Found 3 matches (showing last 100)
```

---

### 🔄 `:hreplay <number>`
**Replay command from history**

```bash
# Replay command #5
:hreplay 5

# Output:
Replaying: phi
φ (Golden Ratio) = 1.618033988749895
```

**Use Cases:**
- Re-run complex commands without retyping
- Test command variations
- Reproduce previous results

---

### 📊 `:hstats`
**Show statistics and φ-harmonic analysis**

```bash
:hstats
```

**Output:**
```
╭────────────────────────────────────────────────────────────────────────────╮
│  HISTORY STATISTICS (φ-Harmonic Analysis)                                  │
╰────────────────────────────────────────────────────────────────────────────╯

Session ID:          1707654923
Total Commands:      25
Avg φ-Resonance:     1.8234
Avg Execution Time:  8 ms
Most Used Command:   :help (7 times)
```

---

### 💾 `:hexport [filename]`
**Export history to file**

```bash
# Export to default file
:hexport

# Export to custom file
:hexport my_session.txt
```

**Output File Format:**
```
# REPL Command History Export
# Session ID: 1707654923
# Exported: 2026-02-11T10:30:45
# φ^75 Validation Seal: 4721424167835376.00

# 2026-02-11T10:15:23
# φ-Resonance: 1.2345
# Execution: 5ms
:help

# 2026-02-11T10:15:30
# φ-Resonance: 2.4567
# Execution: 12ms
phi
```

---

### 🗑️ `:hclear`
**Clear session history**

```bash
:hclear
```

**Note:** Only clears current session. Database file is preserved.

---

### 📏 `:hdbsize`
**Show database size and status**

```bash
:hdbsize
```

**Output:**
```
╭────────────────────────────────────────────────────────────────────────────╮
│  HISTORY DATABASE STATUS                                                   │
╰────────────────────────────────────────────────────────────────────────────╯

Session History:   25 commands
Database Total:    342 commands
Database File:     repl_history.txt
```

---

## File Format

### `repl_history.txt`
Pipe-delimited format for easy parsing:
```
sessionId|command|timestamp|result|phiResonance|executionTime
1707654923|:help|2026-02-11T10:15:23|...|1.2345|5
1707654923|phi|2026-02-11T10:15:30|1.618033988749895|2.4567|12
```

### `repl_session.txt`
Session summaries:
```
# Session 1707654923 ended at 2026-02-11T10:30:45 - 25 commands
# Session 1707655123 ended at 2026-02-11T11:15:30 - 18 commands
```

---

## φ-Harmonic Resonance Explained

Every command generates a unique φ-harmonic signature:

```
For command "phi":
  p (112) × φ^(0/7) % 1 = 0.112
  h (104) × φ^(1/7) % 1 = 0.456
  i (105) × φ^(2/7) % 1 = 0.789
  
  Average = (0.112 + 0.456 + 0.789) / 3 = 0.452
  φ-Resonance = 0.452 × φ = 0.731
```

**Higher resonance** = More mathematically significant command

---

## Use Cases

### 1. **Development Workflow**
```bash
# Try different approaches
:compile var x = 10;
:compile var x = 20;
:compile var x = 30;

# Search for best version
:hsearch compile

# Replay the one that worked
:hreplay 5
```

### 2. **Learning & Documentation**
```bash
# Export session for study
:hexport fibonacci_session.txt

# Review what commands were used
:hstats
```

### 3. **Debugging**
```bash
# Find all compiler commands
:hsearch :compile

# Replay failing command
:hreplay 12

# Check execution times
:hstats
```

### 4. **Cross-Session Analysis**
```bash
# Find all uses of a function across all sessions
:hdbsearch fibonacci

# See total command count
:hdbsize
```

---

## Advanced Features

### Automatic Persistence
Every command is **automatically saved** to `repl_history.txt` with:
- Session ID
- Timestamp
- Command text
- Result (truncated to 200 chars)
- φ-resonance
- Execution time

### Session Isolation
Each REPL session gets a unique ID based on start time:
```
sessionId = currentTimeMillis() / 1000
```

This allows tracking commands across multiple sessions while keeping them organized.

### Intelligent Search
Search is **case-insensitive** and matches **partial strings**:
```bash
:hsearch HELP    # Matches :help, :hstats, :hexport
:hsearch var     # Matches all variable declarations
:hsearch :       # Matches all colon commands
```

---

## Comparison: Old vs New

### Old `:history`
```
╭────────────────────────────────────────────────────────────╮
│  COMMAND HISTORY (φ-Resonance Preserved)                   │
╰────────────────────────────────────────────────────────────╯

   1  :help
   2  phi
   3  :compile var x = 42;

Total: 3 commands
```

### New `:history`
```
╭────────────────────────────────────────────────────────────────────────────╮
│  COMMAND HISTORY (φ-Resonance Preserved)                                   │
╰────────────────────────────────────────────────────────────────────────────╯

   1  [10:15:23] [φ:1.234] [5ms]  :help
   2  [10:15:30] [φ:2.456] [12ms]  phi
   3  [10:15:45] [φ:1.789] [8ms]  :compile var x = 42;

Showing 1-3 of 3 commands
Use: :history [limit] or :history [start] [limit]

+ 7 new commands: :hsearch, :hdbsearch, :hreplay, :hstats, :hexport, :hclear, :hdbsize
+ File persistence (repl_history.txt)
+ φ-resonance tracking
+ Execution time tracking
+ Search capabilities
+ Replay functionality
+ Statistics & analysis
```

---

## Integration with Other Systems

### Works With Compiler
```bash
:compile var x = 42; print(x);
:hsearch compile
:hreplay 1
```

### Works With Decision Array
```bash
:decide "Option A" "Option B" "Option C"
:hsearch decide
:hstats
```

### Works With All Commands
Every command in the REPL is tracked, including:
- Mathematical commands (phi, fib, calc)
- Cryptographic commands (bluelock, redcrack)
- Living code commands (living spawn, selfcode init)
- Infinity storage commands (infinity store)
- Compiler commands (:compile, :lex, :parse)
- Decision array commands (:decide, :preset)

---

## Best Practices

### 1. **Regular Exports**
Export important sessions for documentation:
```bash
:hexport project_milestone_1.txt
```

### 2. **Use Search for Discovery**
Find patterns in your work:
```bash
:hdbsearch fibonacci
:hdbsearch encryption
```

### 3. **Replay for Testing**
Test variations without retyping:
```bash
:hreplay 5
# Modify and try again
```

### 4. **Monitor Performance**
Check execution times:
```bash
:hstats
```

### 5. **Clean Sessions**
Start fresh when needed:
```bash
:hclear
```

---

## Technical Details

### Storage Format
- **File:** `repl_history.txt`
- **Encoding:** UTF-8
- **Delimiter:** Pipe (`|`)
- **Escaping:** Pipes in commands/results are escaped as `\|`

### Performance
- **Load time:** O(n) where n = total history entries
- **Search time:** O(n) linear scan (fast enough for thousands of entries)
- **Save time:** O(1) append operation

### Limits
- **Result truncation:** 200 characters per entry
- **Search results:** Max 100 matches
- **No hard limit on total entries**

---

## Troubleshooting

### History not saving?
Check file permissions in REPL directory.

### Can't find old commands?
Use `:hdbsearch` instead of `:hsearch` to search all sessions.

### Want to start fresh?
Delete `repl_history.txt` and `repl_session.txt` files.

### Export not working?
Ensure you have write permissions in the current directory.

---

## Future Enhancements

Potential additions:
- [ ] History compression for large files
- [ ] Command tagging system
- [ ] Favorite commands bookmarking
- [ ] History visualization graphs
- [ ] Export to JSON/CSV formats
- [ ] Import from other REPLs

---

**Your history is now interactive, persistent, and φ-harmonically resonant!**

Type `:help` to see all history commands in the main menu.
