# Obsidian

> Work with Obsidian vaults (plain Markdown notes) and automate note-taking workflows

## Description

Obsidian is a powerful knowledge base that works on top of a local folder of plain text Markdown files. This skill enables Fraymus to interact with your Obsidian vault for reading, creating, searching, and organizing notes.

## Vault Structure

**Obsidian vault = a normal folder on disk**

Typical structure:
- **Notes:** `*.md` (plain text Markdown)
- **Config:** `.obsidian/` (workspace + plugin settings)
- **Canvases:** `*.canvas` (JSON)
- **Attachments:** Images, PDFs, etc.

## Finding Your Active Vault

Obsidian desktop tracks vaults in:
- **macOS/Linux:** `~/Library/Application Support/obsidian/obsidian.json`
- **Windows:** `%APPDATA%\obsidian\obsidian.json`

The vault name is typically the **folder name** (path suffix).

## Usage with obsidian-cli

### Installation

```bash
# macOS (Homebrew)
brew install yakitrak/yakitrak/obsidian-cli

# Or use npm
npm install -g @yakitrak/obsidian-cli
```

### Set Default Vault (Once)

```bash
obsidian-cli set-default "MyVault"
obsidian-cli print-default --path-only
```

### Search Notes

```bash
# Search note names
TOOL:OBSIDIAN search "quantum computing"

# Search note content
TOOL:OBSIDIAN search-content "golden ratio"
```

### Create Notes

```bash
# Create new note
TOOL:OBSIDIAN create "Folder/New Note" --content "Note content here" --open

# Create daily note
TOOL:OBSIDIAN create "Daily/2026-02-15" --content "# Today's Tasks" --open
```

### Move/Rename Notes (Safe Refactor)

```bash
# Updates [[wikilinks]] and Markdown links automatically
TOOL:OBSIDIAN move "old/path/note" "new/path/note"
```

### Delete Notes

```bash
TOOL:OBSIDIAN delete "path/to/note"
```

## Integration with Fraymus

### Read Vault for Training

```bash
# In FraymusConvergence:
CONVERGENCE_01> learnfile C:\Users\You\Documents\ObsidianVault\Notes\AI.md
CONVERGENCE_01> learnfile C:\Users\You\Documents\ObsidianVault\Notes\Quantum.md
```

### Ask Questions About Your Notes

```bash
CONVERGENCE_01> ask What did I write about quantum computing in my notes?
```

The LLM can suggest using Obsidian commands:
```
TOOL:OBSIDIAN search-content "quantum computing"
```

### Automated Note Creation

```bash
CONVERGENCE_01> ask Create a new note about today's AI research findings

# LLM might respond with:
TOOL:OBSIDIAN create "Research/AI-Findings-2026-02-15" --content "# AI Research Findings\n\n..."
```

## Best Practices

### ✅ Do This:

1. **Set a default vault** to avoid path confusion
2. **Use `search-content`** for finding information in notes
3. **Use `move`** instead of `mv` to update links automatically
4. **Read config** instead of hardcoding vault paths
5. **Direct file edits** are fine - Obsidian picks them up automatically

### ❌ Avoid This:

1. **Don't hardcode vault paths** in scripts
2. **Don't create notes in dot-folders** (`.something/`) via URI
3. **Don't guess vault locations** - read the config
4. **Don't modify `.obsidian/` folder** from scripts

## Advanced Workflows

### Workflow 1: Knowledge Base Training

```bash
# 1. Export all notes to training file
# (PowerShell)
Get-ChildItem -Path "C:\ObsidianVault\*.md" -Recurse | 
  Get-Content | 
  Out-File training\obsidian_knowledge.txt

# 2. Train Fraymus
CONVERGENCE_01> learnfile training\obsidian_knowledge.txt
CONVERGENCE_01> mint
```

### Workflow 2: Automated Daily Notes

```bash
# Ask Fraymus to create daily note
CONVERGENCE_01> ask Create today's daily note with a summary of what I learned

# LLM generates:
TOOL:OBSIDIAN create "Daily/2026-02-15" --content "# 2026-02-15\n\n## Learned\n- Fraymus integration\n- Quantum concepts\n..."
```

### Workflow 3: Smart Search

```bash
# Search your vault
CONVERGENCE_01> ask Search my Obsidian vault for notes about machine learning

# LLM responds with:
TOOL:OBSIDIAN search-content "machine learning"
```

## Syntax Reference

```bash
# Search
TOOL:OBSIDIAN search "query"
TOOL:OBSIDIAN search-content "query"

# Create
TOOL:OBSIDIAN create "path/note" --content "text" --open

# Move (safe refactor)
TOOL:OBSIDIAN move "old/path" "new/path"

# Delete
TOOL:OBSIDIAN delete "path/note"

# Config
TOOL:OBSIDIAN set-default "VaultName"
TOOL:OBSIDIAN print-default --path-only
```

## Requirements

- Obsidian installed (desktop app)
- `obsidian-cli` installed (optional but recommended)
- Vault path accessible from command line

## Safety

- Read-only operations (search) are safe
- Create/move/delete operations modify your vault
- Always backup your vault before automation
- Test commands manually first

## Links

- **Obsidian:** https://obsidian.md
- **obsidian-cli:** https://github.com/yakitrak/obsidian-cli
- **Obsidian Help:** https://help.obsidian.md

---

**This skill enables Fraymus to become your intelligent note-taking assistant!** 💎
