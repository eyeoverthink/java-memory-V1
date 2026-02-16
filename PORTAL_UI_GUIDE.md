# 🌀 PORTAL UI - Visual Knowledge Intake System

## Overview

Portal now has a **visual ImGui interface** for drag-and-drop style knowledge ingestion into Fraymus.

## Features

### 🎯 Drop Zone
- **Multi-line text input** for pasting paths, URLs, package names, class names
- **Smart classification** - automatically detects input type
- **One-click ingestion** with visual feedback

### ⚡ Quick Actions
Pre-configured buttons for common Java packages:
- `java.lang.Math` - Mathematical functions
- `java.util.*` - Collections and utilities
- `java.io.*` - File I/O operations
- `java.nio.*` - New I/O APIs

### 📊 Live Ingestion Log
- Color-coded messages (green = success, red = error, cyan = info)
- Auto-scroll to latest entries
- Shows real-time absorption progress

### 📈 Statistics Panel
- **Items Ingested** - Total count of absorbed items
- **Bytes Consumed** - Total data processed
- **Last Input** - Most recent ingestion details
- **Full Stats** button - Opens detailed Portal statistics

### 🎨 Visual Effects
- **Animated vortex indicator** - Pulsing portal status
- **Progress bar** - Shows ingestion in real-time
- **Color-coded status** - Green (hungry), Yellow (ingesting)

## How to Use

### 1. Open Portal UI
Press **F10** to toggle the Portal panel on/off

### 2. Awaken NEXUS Organism (if not already running)
```
nexus awaken
```
This initializes the Portal system.

### 3. Ingest Knowledge

#### Method A: Drop Zone
1. Paste any of these into the text area:
   - JAR file path: `C:\libs\quantum.jar`
   - Java source: `C:\project\MyClass.java`
   - Package name: `java.util`
   - Class name: `java.lang.Math`
   - URL: `https://example.com/api`
   - Directory: `C:\project\src`

2. Click **🌀 INGEST** button

3. Watch the log for confirmation

#### Method B: Quick Actions
Click any quick action button:
- `java.lang.Math` → Absorbs all Math methods (sqrt, abs, sin, cos, etc.)
- `java.util.*` → Absorbs ArrayList, HashMap, etc.

### 4. Query Absorbed Knowledge
After ingestion, you can query via CommandTerminal:
```
absorb query sqrt
```

Or execute absorbed skills:
```java
portal.blackHole.executeSkill("sqrt", 25.0);  // Returns 5.0
```

## Input Types Supported

| Type | Example | What Gets Absorbed |
|------|---------|-------------------|
| **JAR File** | `library.jar` | All classes and methods |
| **Java Source** | `MyClass.java` | Class/method signatures |
| **Package** | `java.util` | All classes in package |
| **Class** | `java.lang.Math` | All methods as executable skills |
| **URL** | `https://...` | Web content via URLAbsorber |
| **Directory** | `C:\src` | Recursive file ingestion |
| **Text File** | `notes.txt` | Concept extraction |

## Visual Indicators

### Status Colors
- **Green [HUNGRY]** - Ready to ingest
- **Yellow [INGESTING...]** - Processing input
- **Cyan** - Info messages
- **Red** - Errors

### Progress Bar
- Appears during ingestion
- Fills from 0% to 100%
- Cyan glow effect

## Keyboard Shortcuts

- **F10** - Toggle Portal UI
- **F8** - Toggle Self Code Panel
- **F9** - Toggle Miving Brain Visualizer

## Integration with NEXUS Organism

Portal is the **"Mouth"** organ of the NEXUS organism:

```
HUMAN ORGAN          FRAYMUS COMPONENT
─────────────────────────────────────
Mouth (Intake)    →  Portal
Stomach (Digest)  →  BlackHoleProtocol
Brain (Think)     →  MivingBrain
Hands (Create)    →  RealityForge
DNA (Genetics)    →  LazarusEngine
```

## Example Workflow

### Absorb Java Math Library
```
1. Press F10 (open Portal UI)
2. Click "java.lang.Math" quick action
3. Watch log: "🌀 INGESTING: java.lang.Math"
4. See: "✓ INGESTION COMPLETE"
5. Stats update: Items Ingested: 1
```

### Query and Execute
```
In CommandTerminal:
> absorb query sqrt
Found: sqrt(double) → Returns square root

> Execute via code:
portal.blackHole.executeSkill("sqrt", 25.0)
Result: 5.0
```

### Absorb Custom JAR
```
1. Type in drop zone: C:\mylib\quantum.jar
2. Click 🌀 INGEST
3. Portal absorbs all classes
4. Query: absorb query quantum
5. Execute absorbed methods
```

## Technical Details

### File Location
`D:\Zip And Send\Java-Memory\Asset-Manager\src\main\java\fraymus\ui\PortalPanel.java`

### Dependencies
- **Portal.java** - Core absorption engine
- **BlackHoleProtocol.java** - Skill storage
- **URLAbsorber.java** - Web scraping
- **NEXUS_Organism.java** - Biological integration

### Initialization
Portal UI auto-initializes when you run:
```
nexus awaken
```

This creates the NEXUS organism and connects Portal as the "Mouth" organ.

## Advanced Usage

### Programmatic Access
```java
// Get Portal from NEXUS organism
Portal portal = nexusOrganism.getMouth();

// Drop anything
portal.drop("java.lang.Math");
portal.drop(new File("library.jar"));
portal.drop("https://api.example.com");

// Query absorbed knowledge
Object result = portal.ask("sqrt");

// Execute skills
portal.blackHole.executeSkill("sqrt", 25.0);

// View statistics
portal.printStats();
portal.showHistory();
```

### CommandTerminal Commands
```
absorb java.lang.Math          → Ingest Math class
absorb C:\libs\quantum.jar     → Ingest JAR file
absorb https://example.com     → Scrape web content
absorb query sqrt              → Search absorbed skills
```

## Troubleshooting

### Portal UI Not Showing
- Press **F10** to toggle visibility
- Ensure NEXUS organism is awakened: `nexus awaken`

### "Portal not initialized" Error
- Run `nexus awaken` first
- This creates the organism and initializes Portal

### Ingestion Failed
- Check file paths are correct
- Ensure JAR files are readable
- Verify URLs are accessible
- Check logs for specific error messages

## Philosophy

**"It learns by eating."**

Portal is a universal intake valve that converts **anything** into executable knowledge:
- Drop a JAR → Gain library capabilities
- Drop source code → Extract patterns
- Drop a URL → Absorb web knowledge
- Drop a package → Acquire all methods

The system doesn't just store data - it **absorbs it as executable skills** that can be queried and invoked.

---

*Generated by Fraymus Neural System*  
*"A swirling vortex. Drop anything in. Watch it become knowledge."*
