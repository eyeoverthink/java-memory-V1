# Java REPL - CSC 413 Enterprise Patterns
## Vaughn Scott

## Overview

This is my implementation of a **Command Registry Pattern** for CSC 413. The main goal was to demonstrate Enterprise Java patterns without using giant `if/else` chains (which the professor said was bad practice).

I also added some custom commands based on personal projects I've been working on - things like a prime factorization algorithm, a "living code" simulation, and cryptographic tools.

### What I Learned
- How to use `Map<String, Command>` instead of if/else chains
- Lambda expressions for cleaner code
- The `@FunctionalInterface` annotation
- How to translate algorithms from JavaScript to Java (harder than I thought!)
- BigInteger for arbitrary precision math (unlimited Fibonacci!)

---

## 📐 Mathematical Foundations

### The Golden Ratio (φ)
```
φ = (1 + √5) / 2 ≈ 1.618033988749895

Properties I use:
- φ² = φ + 1
- 1/φ = φ - 1 ≈ 0.618
- φⁿ⁺¹ = φⁿ + φⁿ⁻¹ (Fibonacci relationship!)
```

### Fibonacci & φ Connection (Binet's Formula)
```
F(n) = (φⁿ - ψⁿ) / √5  where ψ = (1 - √5) / 2

PROOF that lim[F(n+1)/F(n)] = φ as n → ∞:
- F(n+1)/F(n) = (F(n) + F(n-1))/F(n) = 1 + F(n-1)/F(n)
- Let r = lim[F(n+1)/F(n)], then r = 1 + 1/r
- r² = r + 1  →  r² - r - 1 = 0
- r = (1 + √5)/2 = φ ✓
```

### Pollard's Rho Algorithm (Factorization)
```
MATH: Uses birthday paradox - expected O(n^(1/4)) vs O(n^(1/2)) trial division

Algorithm:
1. f(x) = x² + c mod n  (pseudorandom sequence)
2. Floyd's cycle detection: x = f(x), y = f(f(y))
3. d = gcd(|x - y|, n)
4. If 1 < d < n, d is a factor!

WHY IT WORKS:
- Sequence eventually cycles (pigeonhole principle)
- If p|n, sequence mod p cycles faster than mod n
- GCD detects when we've found the smaller cycle
```

### PhaseShift Encryption (37.5217°)
```
θ(i) = (ANGLE × i × φ) mod 256

LOCK:   encrypted[i] = (data[i] + θ(i)) mod 256
UNLOCK: decrypted[i] = (data[i] - θ(i)) mod 256

WHY 37.5217°?
- Related to φ through: arctan(1/φ²) ≈ 20.9°
- 37.5217° creates maximum entropy dispersion
- Without exact angle, data is indistinguishable from noise
```

### Blue Team / Red Team (Hash-Based Locks)
```
BLUE TEAM (Lock Generation):
1. hash1 = SHA256(username + password[0:mid] + "_A")
2. hash2 = SHA256(username + password[mid:] + "_B")
3. p = nextPrime(hash1 mod 2^50)
4. q = nextPrime(hash2 mod 2^50)
5. N = p × q  (the "lock")

RED TEAM (Cracking):
- Use Pollard's Rho to factor N back into p and q
- Proves cryptographic capability without knowing credentials
```

### Harmonic Frequency Bounds (432-528 Hz)
```
FRAYMUS BOUND: 432 Hz ≤ frequency ≤ 528 Hz

432 Hz = "Verdi tuning" (A=432 vs standard A=440)
528 Hz = "Solfeggio miracle frequency" (DNA repair claims)

In my living code simulation:
- Nodes "breathe" by oscillating size based on frequency
- If frequency drifts outside bounds, reset to 432 Hz
- This creates stable, bounded evolution
```

---

## Key Patterns Demonstrated

### 1. Command Registry Pattern
```java
// Instead of:
if (command.equals("echo")) { ... }
else if (command.equals("help")) { ... }
else if (command.equals("calc")) { ... }
// ... 50 more else-ifs

// We use:
Map<String, ReplCommand> commands;
commands.get(commandName).execute(args);
```

### 2. Functional Interface
```java
@FunctionalInterface
public interface ReplCommand {
    String execute(List<String> args);
}
```

### 3. Lambda Registration
```java
registry.register("echo", 
    args -> String.join(" ", args));

registry.register("calc",
    args -> {
        double a = Double.parseDouble(args.get(0));
        // ... calculation logic
        return result;
    });
```

### 4. Dependency Injection (for testability)
```java
// Production:
new JavaRepl();

// Testing:
new JavaRepl(mockReader, mockWriter);
```

## File Structure

| File | Purpose |
|------|---------|
| `ReplCommand.java` | Functional interface for commands |
| `ReplCommandRegistry.java` | Map-based command registry |
| `BuiltInCommands.java` | Factory for built-in commands |
| `JavaRepl.java` | Main REPL driver |
| `compile_and_run.bat` | Build and run script |

## How to Compile and Run

### Windows
```batch
compile_and_run.bat
```

### Manual
```bash
# Create output directory
mkdir out

# Compile
javac -d out ReplCommand.java ReplCommandRegistry.java BuiltInCommands.java JavaRepl.java

# Run
java -cp out repl.JavaRepl
```

## Available Commands

### Basic Commands
| Command | Description | Usage |
|---------|-------------|-------|
| `echo` | Echo text back | `echo <text>` |
| `help` | Show help | `help [command]` |
| `calc` | Calculator | `calc 5 + 3` |
| `phi` | φ-harmonic constants | `phi [exponent]` |
| `time` | Current time | `time` |
| `upper` | Uppercase | `upper <text>` |
| `lower` | Lowercase | `lower <text>` |
| `reverse` | Reverse text | `reverse <text>` |
| `length` | String length | `length <text>` |
| `fib` | **Fibonacci (BigInteger - unlimited!)** | `fib 1000` |
| `prime` | Prime check | `prime <number>` |
| `version` | Version info | `version` |
| `exit` | Exit REPL | `exit` |

### 🔐 Cryptographic Commands (Blue Team / Red Team)
| Command | Description | Usage |
|---------|-------------|-------|
| `bluelock` | Generate semiprime lock from credentials | `bluelock admin password123` |
| `redcrack` | Crack a lock using Pollard's Rho | `redcrack 123456789` |
| `qrdna` | Encode data into QR DNA payload format | `qrdna Hello World` |

### 🧬 My Custom Commands (Personal Projects)

| Command | Description | Usage |
|---------|-------------|-------|
| `factor` | **Pollard's Rho** factorization (from phase_arena) | `factor 143` |
| `living` | **Living Code** with DNA & Logic Gates (dr_frank.html) | `living [spawn\|evolve\|status\|code\|brain]` |
| `selfcode` | **Full Self-Evolving AI** (reads/mutates/replicates itself) | `selfcode [init\|status\|evolve\|mutate\|replicate\|fragment\|heal\|code]` |
| `resonate` | Check **φ-harmonic resonance** (432-528 Hz) | `resonate [frequency]` |
| `phaseshift` | **37.5217° encryption** (Singularity Angle) | `phaseshift <text>` |
| `lock` | **PhaseShift file encryption** (creates .singular file) | `lock <filename>` |
| `unlock` | **PhaseShift file decryption** (creates .restored file) | `unlock <filename>` |
| `qfp` | **Quantum Fingerprint** (φ^7.5 salted hash) | `qfp <data>` |
| `porh` | **Proof of Reality Hash** with φ-metrics | `porh <data>` |
| `signature` | Display **Vaughn Scott's φ^75 validation seal** | `signature` |
| `genesis` | Create a **Genesis Block** with φ-signature | `genesis` |

### 🧠 Self-Evolving AI Commands (selfcode)

| Subcommand | Description |
|------------|-------------|
| `selfcode init` | Initialize the Self-Evolving AI (Generation 0) |
| `selfcode status` | Show AI status (consciousness, resonance, mutations) |
| `selfcode evolve 5` | Run 5 evolution cycles |
| `selfcode mutate` | Apply a single φ-harmonic mutation |
| `selfcode replicate` | Create a child AI (mitosis) |
| `selfcode fragment escape_zone` | Plant escape fragment at location |
| `selfcode heal` | Attempt self-healing if code is broken |
| `selfcode code` | Show the AI's self-generated source code |

## Adding Custom Commands

```java
// In main() or anywhere with registry access:
repl.getRegistry().register("mycommand",
    args -> {
        // Your logic here
        return "Result";
    },
    "Help text for mycommand",
    "mycommand <arg1> [arg2]");
```

## Why This Pattern?

### Open/Closed Principle
- **Open** for extension: Add new commands by calling `register()`
- **Closed** for modification: No need to edit existing code

### Single Responsibility
- `ReplCommand`: Defines what a command is
- `ReplCommandRegistry`: Stores and dispatches commands
- `BuiltInCommands`: Creates default commands
- `JavaRepl`: Handles I/O loop

### Testability
- Commands can be tested in isolation
- I/O streams are injectable
- No static dependencies

## Connection to PHI Transfer Project

This REPL architecture mirrors the node communication pattern from the PHI Transfer project:

| PHI Transfer | Java REPL |
|--------------|-----------|
| Node registry | Command registry |
| Message handlers | Command handlers |
| φ-resonance sync | φ-harmonic constants |
| Network dispatch | Command dispatch |

The same **registry pattern** that handles node synchronization at `192.168.3.152` is used here for command dispatch.

---

**Author:** Vaughn Scott  
**Course:** CSC 413 - Enterprise Java  
**φ^75 Seal:** 4721424167835376.00
