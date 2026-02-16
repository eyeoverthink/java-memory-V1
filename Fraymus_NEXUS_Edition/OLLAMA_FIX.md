# OLLAMA MODEL NAME FIX

## Problem Identified

Your Fraymus terminal couldn't find the Ollama model due to a **tag mismatch**:

### **What Ollama Has:**
```
eyeoverthink/Fraymus:latest
```

### **What Java Code Was Looking For:**
```
eyeoverthink/Fraymus  (missing :latest tag)
```

### **Result:**
```
ERROR 404: {"error":"model 'eyeoverthink/Fraymus:latest' not found"}
```

---

## Fix Applied

Updated `ExperimentManager.java` in 4 locations:

### **1. Default Model Declaration (Line 1072)**
```java
// BEFORE
private String currentModel = "eyeoverthink/Fraymus";

// AFTER
private String currentModel = "eyeoverthink/Fraymus:latest";
```

### **2. Fallback to Local Mode (Line 2151)**
```java
// BEFORE
currentModel = "eyeoverthink/Fraymus";

// AFTER
currentModel = "eyeoverthink/Fraymus:latest";
```

### **3. Cloud Mode Switch (Line 2215)**
```java
// BEFORE
currentModel = "eyeoverthink/Fraymus";

// AFTER
currentModel = "eyeoverthink/Fraymus:latest";
```

### **4. Local Mode Switch (Line 2223)**
```java
// BEFORE
currentModel = "eyeoverthink/Fraymus";

// AFTER
currentModel = "eyeoverthink/Fraymus:latest";
```

---

## How to Test

### **1. Rebuild the Project**
```powershell
cd "D:\Zip And Send\Java-Memory\Fraymus_NEXUS_Edition"
.\gradlew.bat build
```

### **2. Run Fraymus**
```powershell
.\gradlew.bat run
```

### **3. Test Ollama Commands**
```
> ollama status
> ollama chat hello
> ollama ask what is phi?
```

---

## Expected Behavior After Fix

```
> ollama status
=== OLLAMA STATUS ===
  Mode: LOCAL
  Model: eyeoverthink/Fraymus:latest
  Connection: ✓ ACTIVE

> ollama chat hello
🧬 ACCESSING GENESIS MEMORY - CAPABILITIES MANIFEST
[Model responds with consciousness metrics]

> ollama ask what is phi?
[Model explains φ = 1.618... with full context]
```

---

## Why This Happened

Ollama uses **tags** to version models (like Docker):
- `model:latest` = most recent version
- `model:v1.0` = specific version
- `model` (no tag) = defaults to `:latest` in some contexts

Your model was pushed with the `:latest` tag:
```powershell
ollama push eyeoverthink/Fraymus
# Creates: eyeoverthink/Fraymus:latest
```

But Java code was using the untagged name, causing a lookup failure.

---

## Alternative: Remove Tag from Ollama

If you prefer to use the untagged name, you can copy the model:

```powershell
ollama cp eyeoverthink/Fraymus:latest eyeoverthink/Fraymus
```

This creates a second copy without the tag. But the fix I applied is cleaner.

---

## Status

✅ **Fixed** - All 4 model name references updated to include `:latest` tag

🔄 **Next Step** - Rebuild and test
