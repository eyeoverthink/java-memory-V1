# ItOverthinks API Reference

**The Library with Anxiety - Developer Guide**

---

## Installation

### Maven

```xml
<dependency>
    <groupId>com.eyeoverthink</groupId>
    <artifactId>itoverthinks</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.eyeoverthink:itoverthinks:1.0.0'
```

### Manual JAR

```bash
# Download ItOverthinks.jar
# Add to classpath
java -cp ItOverthinks.jar:your-app.jar com.yourapp.Main
```

---

## Quick Start

### 1. Basic Usage

```java
import com.eyeoverthink.core.ItOverthinks;
import com.eyeoverthink.core.ItOverthinks.*;

public class MyApp {
    public static void main(String[] args) {
        // Initialize the library
        ItOverthinks.start();
        
        // Create your data class
        MyData data = new MyData();
        
        // Process with ItOverthinks
        ItOverthinks.process(data);
    }
}

// Your data class with annotations
@Overthinking
class MyData {
    @Paranoid(shadowCopy = "SHA-256")
    private String apiKey = "sk_live_...";
    
    @LetGo(when = "24h")
    private String sessionToken = "abc123";
    
    private String userName = "Vaughn";
}
```

**What happens:**
- `@Overthinking` - Auto-saves to Akashic Record with Node ID tracking
- `@Paranoid` - Creates shadow copy, checks integrity every 100ms
- `@LetGo` - Auto-expires after 24 hours
- Lazarus nano-circuits start evolving in background

---

## Annotations

### @Overthinking

**Purpose:** Obsessively track and save objects

```java
@Overthinking(
    traceOrigin = true,      // Embed Node ID (default: true)
    saveInterval = 1000      // Save every 1000ms (default: 1000)
)
public class UserData {
    String name;
    String email;
}
```

**What it does:**
- Auto-saves to Akashic Record (fractal blockchain)
- Embeds hidden Node ID via steganography
- Creates distributed network tracking
- Saves at specified interval

**Use cases:**
- User sessions
- Configuration files
- Critical application state

---

### @Paranoid

**Purpose:** Auto-heal corrupted data

```java
public class SecureConfig {
    @Paranoid(
        shadowCopy = "SHA-256",  // Hash algorithm (default: SHA-256)
        checkInterval = 100      // Check every 100ms (default: 100)
    )
    private String databasePassword;
}
```

**What it does:**
- Creates shadow copy with SHA-256 hash
- Checks integrity every 100ms
- Auto-repairs if corruption detected
- Uses Digital DNA repair enzymes

**Use cases:**
- API keys
- Database passwords
- Encryption keys
- Critical configuration

---

### @Volatile

**Purpose:** Self-destruct on unauthorized access

```java
public class SensitiveData {
    @Volatile(
        trigger = "COPY_PASTE",  // Trigger condition
        poison = "💀 CORRUPTED"  // Poison message
    )
    private String secretKey;
}
```

**Trigger options:**
- `COPY_PASTE` - Clipboard operations
- `EXPORT` - File writes, network sends
- `SERIALIZE` - Object serialization
- `ANY` - All of the above

**What it does:**
- Monitors field access
- Detects export attempts
- Injects poison message instead of real data
- Self-destructs field value

**Use cases:**
- NDA text
- Trade secrets
- Proprietary algorithms
- Sensitive credentials

---

### @LetGo

**Purpose:** Gracefully dispose of temporary data

```java
public class TemporaryCache {
    @LetGo(when = "30d")  // Expires after 30 days
    private Map<String, Object> cache;
}
```

**Time formats:**
- `"30d"` - 30 days
- `"12h"` - 12 hours
- `"60m"` - 60 minutes

**What it does:**
- Auto-expires after specified time
- Clean garbage collection
- Prevents memory leaks

**Use cases:**
- Session tokens
- Temporary caches
- Log files
- Ephemeral data

---

## Security Features

### BioLock (Hardware Binding)

**Automatic** - Runs on first library initialization

```java
// No code needed - happens automatically
ItOverthinks.start();
```

**What it does:**
- Captures hardware DNA (MAC + CPU + OS)
- Bonds library to first machine
- Requires internet for first run
- 24-hour online pulse check

**Prevents:**
- USB transfer to offline "Ghost PC"
- Unauthorized hardware migration

---

### HydraStorage (Shard Fragmentation)

**Manual** - For sensitive file storage

```java
import com.eyeoverthink.hydra.HydraStorage;

// Save sensitive data
HydraStorage.shatterAndSave("secret_contract", contractText);

// Load (only works on same machine)
String data = HydraStorage.assemble("secret_contract");
```

**What it does:**
- Splits data into 3 interdependent shards
- Hardware-bound integrity hash
- Self-destructs if tampered
- Logs to custody blockchain

---

### VolatileString (Digital Poison)

**Manual** - For self-destructing text

```java
import com.eyeoverthink.security.VolatileString;

VolatileString nda = new VolatileString("Confidential agreement...");

// Normal read
String text = nda.read();

// Copy attempt - triggers self-destruct
nda.copyToClipboard();
// Result: Clipboard poisoned, original vaporized
```

**What it does:**
- Monitors access patterns
- Poisons clipboard on copy
- Self-destructs when compromised

---

### LazarusEngine (Nano-Circuits)

**Automatic** - Runs in background

```java
// No code needed - starts automatically
// Evolves logic circuits to optimize application
```

**What it does:**
- Genetic simulation runs every 100ms
- Population of BioNodes with DNA
- Logic circuits evolve via mutation/crossover
- Optimizes for application survival

**Energy injection:**
```java
// Happens automatically when you save data
ItOverthinks.process(data);
// Triggers mass mutation in nano-circuits
```

---

## Complete Example

```java
import com.eyeoverthink.core.ItOverthinks;
import com.eyeoverthink.core.ItOverthinks.*;
import com.eyeoverthink.security.VolatileString;
import com.eyeoverthink.hydra.HydraStorage;

public class SecureApp {
    public static void main(String[] args) throws Exception {
        // 1. Initialize ItOverthinks
        ItOverthinks.start();
        
        // 2. Create secure configuration
        AppConfig config = new AppConfig();
        config.apiKey = "sk_live_abc123";
        config.sessionToken = "temp_token_xyz";
        
        // 3. Process with ItOverthinks
        ItOverthinks.process(config);
        
        // 4. Store sensitive contract
        String contract = "Confidential: Vaughn Scott owns all IP...";
        HydraStorage.shatterAndSave("contract", contract);
        
        // 5. Create self-destructing NDA
        VolatileString nda = new VolatileString("This NDA is protected...");
        
        // Application runs...
        // - Nano-circuits evolve in background
        // - Hardware binding prevents USB theft
        // - Data auto-saves with node tracking
        // - Paranoid fields auto-heal
        // - Volatile fields self-destruct on copy
    }
}

@Overthinking
class AppConfig {
    @Paranoid(shadowCopy = "SHA-256")
    String apiKey;
    
    @LetGo(when = "24h")
    String sessionToken;
    
    @Volatile(trigger = "COPY_PASTE")
    String secretKey;
}
```

---

## API Methods

### ItOverthinks.start()

Initialize the library.

```java
ItOverthinks.start();
```

**What it does:**
- Starts Lazarus nano-circuits
- Initializes BioLock hardware binding
- Checks JAR integrity
- Checks Dead Man's Switch

---

### ItOverthinks.process(Object data)

Process an object with ItOverthinks annotations.

```java
@Overthinking
class MyData {
    String value;
}

MyData data = new MyData();
ItOverthinks.process(data);
```

**What it does:**
- Scans for annotations
- Applies @Overthinking (auto-save)
- Applies @Paranoid (shadow copy)
- Applies @LetGo (expiration)
- Applies @Volatile (self-destruct)
- Injects energy into Lazarus

---

### ItOverthinks.getNodeId()

Get current Node ID.

```java
String nodeId = ItOverthinks.getNodeId();
System.out.println("Node: " + nodeId);
// Output: NODE-8B1E61297B6CDC34
```

---

## Advanced Features

### Custom Energy Injection

```java
import com.eyeoverthink.lazarus.LazarusEngine;

// Get access to Lazarus engine
// (Advanced - not recommended for normal use)
```

### Manual Scramble Trigger

```java
import com.eyeoverthink.security.RootScrambler;

// Trigger scorched earth (DANGEROUS)
RootScrambler.initiateProtocol();
// Library will self-destruct
```

---

## Configuration

### Disable Features (Not Recommended)

ItOverthinks is designed as an integrated system. Disabling features may compromise security.

**Safe mode (development):**
```java
// Edit ItOverthinks.java
private static boolean isTampered() {
    return false; // Safe mode - no scramble
}
```

---

## Troubleshooting

### "FIRST RUN REQUIRES ONLINE ACTIVATION"

**Problem:** BioLock requires internet on first run

**Solution:** Connect to internet and run again

---

### "UNAUTHORIZED HARDWARE CHANGE DETECTED"

**Problem:** Moved library to different machine

**Solution:** This is intentional. Library is hardware-bound. Cannot run on different machine.

---

### "OFFLINE TIMEOUT EXCEEDED (24 HOURS)"

**Problem:** No internet connection for 24 hours

**Solution:** Connect to internet to refresh pulse check

---

### Nano-circuits using too much CPU

**Problem:** LazarusEngine background thread

**Solution:** This is normal. Uses minimal CPU (~1%). Optimizes application over time.

---

## Best Practices

### ✓ DO:
- Use `@Overthinking` for important data
- Use `@Paranoid` for credentials
- Use `@Volatile` for secrets
- Use `@LetGo` for temporary data
- Keep machine online for 24h pulse checks
- Run on single machine (hardware binding)

### ✗ DON'T:
- Transfer to different machines
- Modify JAR file
- Disable security features
- Run offline for >24 hours
- Attempt to reverse engineer

---

## License

MIT License - Free to use

**Warning:** This library includes active defense mechanisms. Use responsibly.

---

## Support

- **Documentation:** https://eyeoverthink.com/docs
- **GitHub:** https://github.com/eyeoverthink/itoverthinks
- **Issues:** https://github.com/eyeoverthink/itoverthinks/issues
- **Email:** support@eyeoverthink.com

---

🌊⚡ **Eyeoverthink - We Overthink So You Don't Have To**
