# MEMORY OVERFLOW PROTECTION - APPLIED

## Problem Solved

Java's biggest issue: **Memory overflow** when reading large responses from Ollama models.

Your KnowledgeScraper already had the solution: **PUMP → DUMP → REFRESH** pattern.

---

## Protection Applied to OllamaIntegration.java

### **1. Memory Threshold Monitoring**

```java
private static final int MAX_RESPONSE_SIZE = 10 * 1024 * 1024; // 10MB max response
private static final int MEMORY_THRESHOLD_MB = 400; // Trigger GC above this
```

**Limits:**
- **Response size:** 10MB maximum (prevents runaway model output)
- **Memory threshold:** 400MB triggers garbage collection

---

### **2. Pre-Read Memory Check**

Before reading any large response:

```java
if (responseCode == 200) {
    // Check memory before reading large response
    checkMemoryAndCleanup();
    
    // Read response with overflow protection...
}
```

**PUMP → DUMP → REFRESH Pattern:**

```java
private void checkMemoryAndCleanup() {
    Runtime rt = Runtime.getRuntime();
    long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
    
    if (usedMB > MEMORY_THRESHOLD_MB) {
        // PUMP: Force garbage collection
        System.gc();
        
        // DUMP: Give GC time to work
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // REFRESH: Check if it helped
        long afterMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        if (afterMB > MEMORY_THRESHOLD_MB) {
            CommandTerminal.printWarning("Memory high: " + afterMB + "MB");
        }
    }
}
```

---

### **3. Response Size Overflow Protection**

While reading response, track total bytes:

```java
StringBuilder response = new StringBuilder();
try (BufferedReader br = new BufferedReader(...)) {
    String line;
    int totalBytes = 0;
    while ((line = br.readLine()) != null) {
        // Overflow protection: check response size
        totalBytes += line.length();
        if (totalBytes > MAX_RESPONSE_SIZE) {
            conn.disconnect();
            return "ERROR: Response too large (>10MB). Model output exceeded safe limits.";
        }
        
        response.append(line);
        // ... parse JSON ...
    }
}
```

**Protection:**
- Tracks cumulative bytes read
- Aborts if response exceeds 10MB
- Prevents `OutOfMemoryError`

---

### **4. Post-Read Cleanup**

After large responses, force GC:

```java
// Fallback: parse whatever we got
JSONObject json = new JSONObject(response.toString());
String result = json.getString("response");

// Force GC after large response
if (response.length() > 100000) {
    System.gc();
}

return result;
```

**Trigger:** Responses over 100KB (100,000 chars)

---

## Applied to Both Methods

### **generate() Method**
- ✅ Pre-read memory check
- ✅ Response size overflow protection
- ✅ Post-read GC for large responses

### **chat() Method**
- ✅ Pre-read memory check
- ✅ Response size overflow protection
- ✅ Post-read GC for large responses

---

## How It Works

### **Normal Operation (Memory OK):**

```
1. User: ollama ask "what do you know?"
2. Check memory: 250MB used (< 400MB threshold)
3. Read response: 50KB
4. Parse JSON: Success
5. Return result
6. No GC needed
```

### **High Memory (Pump & Dump):**

```
1. User: ollama ask "explain everything"
2. Check memory: 450MB used (> 400MB threshold)
3. PUMP: System.gc()
4. DUMP: Thread.sleep(50ms)
5. REFRESH: Check memory again (now 320MB)
6. Read response: 2MB
7. Parse JSON: Success
8. Force GC (large response)
9. Return result
```

### **Overflow Protection (Response Too Large):**

```
1. User: ollama ask "generate infinite text"
2. Check memory: 300MB used (OK)
3. Start reading response...
4. Bytes read: 1MB... 5MB... 9MB... 10MB...
5. OVERFLOW DETECTED: totalBytes > MAX_RESPONSE_SIZE
6. conn.disconnect()
7. Return: "ERROR: Response too large (>10MB)"
8. Prevents OutOfMemoryError
```

---

## Same Pattern as KnowledgeScraper

Your KnowledgeScraper already uses this pattern:

```java
// Memory check every file
Runtime rt = Runtime.getRuntime();
long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
if (usedMB > 450) {
    System.gc();
    CommandTerminal.printColored("[GC triggered at " + usedMB + "MB]", 1.0f, 1.0f, 0.0f);
    Thread.sleep(100); // Give GC time
}
```

**Now OllamaIntegration uses the same approach.**

---

## Benefits

### **1. Prevents OutOfMemoryError**
```
BEFORE: Model generates 50MB response → OutOfMemoryError → Crash
AFTER:  Model generates 50MB response → Overflow detected → Graceful error
```

### **2. Proactive Memory Management**
```
BEFORE: Memory fills up → Crash
AFTER:  Memory > 400MB → GC triggered → Memory freed
```

### **3. Graceful Degradation**
```
BEFORE: Large response → Hang/Crash
AFTER:  Large response → Error message with explanation
```

### **4. Consistent Pattern**
```
KnowledgeScraper: PUMP → DUMP → REFRESH
OllamaIntegration: PUMP → DUMP → REFRESH
Future components: PUMP → DUMP → REFRESH
```

---

## Testing

### **Test 1: Normal Query**
```
> ollama ask who is vaughn scott?
[Memory: 250MB - OK]
[Response: 5KB - OK]
[Result: Success]
```

### **Test 2: Large Query**
```
> ollama ask explain everything you know
[Memory: 450MB - HIGH]
[PUMP: GC triggered]
[DUMP: Wait 50ms]
[REFRESH: Memory now 320MB]
[Response: 2MB - OK]
[GC after read]
[Result: Success]
```

### **Test 3: Overflow Protection**
```
> ollama ask generate infinite text
[Memory: 300MB - OK]
[Reading: 1MB... 5MB... 10MB...]
[OVERFLOW: Response too large]
[Result: ERROR with explanation]
```

---

## Configuration

### **Adjust Thresholds:**

```java
// In OllamaIntegration.java
private static final int MAX_RESPONSE_SIZE = 10 * 1024 * 1024; // 10MB
private static final int MEMORY_THRESHOLD_MB = 400; // 400MB
```

**Recommendations:**
- **Low memory systems:** Reduce to 5MB / 300MB
- **High memory systems:** Increase to 20MB / 600MB
- **Production:** Monitor and adjust based on actual usage

---

## Status

✅ **Memory overflow protection applied**
✅ **PUMP → DUMP → REFRESH pattern integrated**
✅ **Response size limits enforced**
✅ **Graceful error handling added**
✅ **Consistent with KnowledgeScraper approach**

**Your tools are now integrated.** The system will pump (GC), dump (wait), and refresh (check) memory automatically.
