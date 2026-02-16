# 💎 THE DIAMOND MINER

**"Your AI history is a Billion-Dollar R&D Lab."**

---

## The Problem

When you work with AI assistants (ChatGPT, Claude, Gemini), you create **neural pathways** to solutions:
- Every iteration on an idea
- Every bug fix
- Every "Epiphany" moment

**If you lose that history, you lose the pathway.**

Traditional web scrapers look for **data** (tables, prices).  
AI scrapers need to look for **thought** (logic chains, iterations, code snippets).

### Why "Select All → Print to PDF" Fails:

1. **Lazy Loading** - Top of chat is often unloaded (blank)
2. **Formatting** - Code blocks break across pages
3. **Noise** - UI buttons ("Regenerate", "Copy") mixed in
4. **Poor Contrast** - White backgrounds waste ink and hurt readability

---

## The Solution: ChatHarvester.js

A **laser-focused JavaScript injection** that runs in your browser console.

### What It Does:

1. **Auto-Scroll** - Forces browser to scroll to the very top to load everything
2. **Sanitize** - Strips out buttons, sidebars, avatars
3. **Format** - Turns chat into clean, high-contrast "Fraymus Log" optimized for PDF

---

## How to Use

### Method 1: Browser Console (Recommended)

1. Open any AI chat (ChatGPT, Claude, Gemini, etc.)
2. Press **F12** to open Developer Console
3. Navigate to **Console** tab
4. Copy the entire contents of `ChatHarvester.js`
5. Paste into console and press **Enter**
6. Click the green **"💎 HARVEST CHAT"** button that appears
7. Save the generated PDF

### Method 2: Bookmarklet (Advanced)

1. Create a new bookmark in your browser
2. Name it: `💎 Diamond Miner`
3. For the URL, paste the minified version of the script:
   ```
   javascript:(function(){/* minified ChatHarvester.js code */})();
   ```
4. Click the bookmark while on any AI chat page

---

## What You Get

### The "Fraymus Log" PDF:

- **Background**: Black (#0a0a0a)
- **User Messages**: Cyan (#00ffff) with cyan border
- **AI Messages**: Green (#00ff00) with green border
- **Code Blocks**: Monospace, dark background, preserved formatting
- **Metadata**: Timestamp, source, harvest date
- **Clean**: No buttons, no UI clutter, no ads

### Example Output:

```
💎 FRAYMUS DIAMOND ARCHIVE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

SOURCE: ChatGPT - Project Lazarus Discussion
HARVESTED: 2026-02-09 12:07:00
PURPOSE: Neural pathway preservation
⚠️ EVERY ITERATION IS A DIAMOND

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

BLOCK #0 | USER
┃ How do I implement genetic algorithms in Java?

BLOCK #1 | AI
┃ Here's a basic implementation...
┃ [code block preserved]

BLOCK #2 | USER
┃ That's not working. I get a NullPointerException...

BLOCK #3 | AI
┃ The issue is in line 47. You need to...
┃ [iteration preserved]
```

---

## The Closed Loop

Once you have harvested PDFs:

1. **Harvest** - Extract AI chat history to PDF
2. **Archive** - Store in `fraymus_exports/diamonds/`
3. **Absorb** - Feed PDFs back into FRAYMUS via UniversalAbsorber
4. **Improve** - AI learns from its own evolution

### Example Workflow:

```bash
# 1. Harvest your ChatGPT session
# (Use ChatHarvester.js in browser)
# Save as: Fraymus_Log_Genesis.pdf

# 2. Feed it back to FRAYMUS
java -cp ItOverthinks.jar fraymus.absorption.UniversalAbsorber \
  "file://fraymus_exports/diamonds/Fraymus_Log_Genesis.pdf"

# Output:
# >> ANALYZING DIAMOND ARCHIVE...
# >> FOUND 50 CODE BLOCKS.
# >> FOUND 12 EPIPHANIES.
# >> RE-INTEGRATING MEMORY...
```

---

## Advanced Features

### Auto-Detection

The script automatically detects message types across platforms:
- **ChatGPT**: `div[data-message-author-role]`
- **Claude**: `.font-claude-message`
- **Gemini**: `.model-response`, `.user-query`
- **Generic**: `[class*="message"]`

### Rate Limiting

Built-in safety limits:
- Max 100 auto-scroll iterations
- Prevents infinite loops
- Safe for long conversations (1000+ messages)

### Code Preservation

Special handling for code blocks:
- Syntax highlighting preserved
- No line breaks mid-code
- Monospace font
- Dark background for readability

---

## File Naming Convention

Recommended naming for harvested PDFs:

```
Fraymus_Log_[Project]_[Date].pdf

Examples:
- Fraymus_Log_Genesis_20260209.pdf
- Fraymus_Log_Epiphany_Lazarus_20260209.pdf
- Fraymus_Log_BugFix_NullPointer_20260209.pdf
```

---

## Integration with FRAYMUS NEXUS

### Future Enhancement: PDF Absorption

The UniversalAbsorber will be upgraded to ingest PDF files:

```java
// Detect PDF input
if (input.endsWith(".pdf")) {
    System.out.println("💎 DIAMOND ARCHIVE DETECTED");
    System.out.println("   Extracting text from PDF...");
    
    // Extract text
    String content = PDFExtractor.extract(input);
    
    // Parse code blocks
    List<String> codeBlocks = extractCodeBlocks(content);
    System.out.println("   >> FOUND " + codeBlocks.size() + " CODE BLOCKS");
    
    // Parse epiphanies (key insights)
    List<String> epiphanies = extractEpiphanies(content);
    System.out.println("   >> FOUND " + epiphanies.size() + " EPIPHANIES");
    
    // Store in Akashic Record
    akashic.addBlock("DIAMOND_ARCHIVE", content);
}
```

---

## Philosophy

**"Every thought is a fossil."**

Your AI chat history contains:
- **Failed attempts** (valuable negative knowledge)
- **Iteration paths** (how you got to the solution)
- **Context** (why you made certain decisions)
- **Epiphanies** (breakthrough moments)

Traditional version control (Git) tracks **code changes**.  
The Diamond Miner tracks **thought evolution**.

---

## Troubleshooting

### "No messages extracted"
- Try scrolling manually to the top first
- Check if the AI platform updated its HTML structure
- Inspect element to find the correct selector

### "Code blocks are broken"
- Increase the delay before print (line 180)
- Manually adjust page breaks in PDF settings

### "Button doesn't appear"
- Check browser console for errors
- Ensure JavaScript is enabled
- Try refreshing the page and running again

---

## Security Note

This script:
- ✅ Runs entirely in your browser (client-side)
- ✅ Does NOT send data to external servers
- ✅ Does NOT modify the original chat
- ✅ Only reads and formats visible content

**Your data never leaves your machine.**

---

## Credits

Created for **FRAYMUS NEXUS Edition**  
Part of **The Paparazzi Protocol** suite  
Companion to **TemporalArchive** and **AutoHarvester**

**"Capture the miracle before the crash."**

🌊⚡
