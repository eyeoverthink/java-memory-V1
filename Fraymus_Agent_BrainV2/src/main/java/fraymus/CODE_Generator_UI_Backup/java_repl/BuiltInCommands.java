/**
 * BuiltInCommands.java - CSC 413 Enterprise Java
 * 
 * This is where I register all the basic REPL commands.
 * I learned about the Factory Pattern from Chapter 4 of the textbook.
 * 
 * The idea is that instead of having a giant if/else chain like:
 *   if (cmd.equals("echo")) { ... }
 *   else if (cmd.equals("help")) { ... }
 * 
 * We use a Map and lambdas to register commands. Much cleaner!
 * 
 * I also added some math commands that use the Golden Ratio (phi)
 * because I've been using it in my other projects.
 * 
 * @author Vaughn Scott
 * @version 1.0
 * 
 * LEARNING NOTES:
 * - Lambdas are like anonymous functions (args -> result)
 * - The @FunctionalInterface annotation means only one abstract method
 * - Factory Pattern = create objects without specifying exact class
 */
package fraymus.CODE_Generator_UI_Backup.java_repl;

import java.util.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.MessageDigest;

/**
 * Factory for creating built-in REPL commands.
 * Each command is registered using lambdas - NO if/else chains.
 */
public class BuiltInCommands {
    
    // ============================================================
    // CONSTANTS - The Golden Ratio and related values
    // I use these throughout my code for "organic" feeling math
    // ============================================================
    
    // The Golden Ratio: (1 + sqrt(5)) / 2 ≈ 1.618
    // Fun fact: This is the limit of Fibonacci ratios! (F(n+1)/F(n) → φ)
    private static final double PHI = 1.618033988749895;
    
    // The inverse: 1/φ = φ - 1 ≈ 0.618 (this is a cool property!)
    private static final double PHI_INV = 1.0 / PHI;
    
    // φ^7.5 - I use this as a "salt" for hashing
    private static final double PHI_75 = Math.pow(PHI, 7.5);
    
    // φ^75 - A huge number I use as my "signature seal"
    private static final double PHI_SEAL = Math.pow(PHI, 75);
    
    /**
     * Register all built-in commands with the registry.
     * This is the ENTERPRISE pattern - commands registered via lambdas.
     * 
     * @param registry The command registry to populate
     * @param repl The JavaRepl instance for accessing history and debug state
     */
    public static void registerAll(ReplCommandRegistry registry, JavaRepl repl) {
        
        // ECHO command - demonstrates basic I/O
        registry.register("echo", 
            args -> args.isEmpty() ? "" : String.join(" ", args),
            "Echo the input text back to the console",
            "echo <text>");
        
        // :VERSION command - CSC 413 requirement
        registry.register(":version",
            args -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Java REPL v1.0\n");
                sb.append("CSC 413 - Enterprise Java Patterns\n");
                sb.append("φ^75 Validation Seal: " + String.format("%.2e", PHI_SEAL) + "\n");
                sb.append("Java Version: " + System.getProperty("java.version"));
                return sb.toString();
            },
            "Display version information",
            ":version");
        
        // :HELP command - CSC 413 requirement
        registry.register(":help",
            args -> {
                if (args.isEmpty()) {
                    return getMainHelpScreen();
                } else {
                    String cmd = args.get(0).toLowerCase();
                    return getDetailedHelp(cmd, registry);
                }
            },
            "Display help for commands - type ':help <command>' for detailed math & usage",
            ":help [command]");
        
        // :HISTORY command - CSC 413 requirement
        registry.register(":history",
            args -> {
                List<String> history = repl.getCommandHistory();
                if (history.isEmpty()) {
                    return "No commands in history.";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  COMMAND HISTORY (φ-Resonance Preserved)                     │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                for (int i = 0; i < history.size(); i++) {
                    sb.append(String.format("%4d  %s\n", i + 1, history.get(i)));
                }
                sb.append(String.format("\nTotal: %d commands", history.size()));
                return sb.toString();
            },
            "Show command history",
            ":history");
        
        // :DEBUG command - CSC 413 requirement
        registry.register(":debug",
            args -> {
                boolean newMode = repl.toggleDebugMode();
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  DEBUG MODE: " + (newMode ? "ON " : "OFF") + "                                          │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n");
                if (newMode) {
                    sb.append("\n[DEBUG] Verbose output enabled");
                    sb.append("\n[DEBUG] Command parsing details will be shown");
                    sb.append("\n[DEBUG] φ-Resonance tracking active");
                }
                return sb.toString();
            },
            "Toggle debug mode",
            ":debug");
        
        // EXIT/QUIT commands
        registry.register("exit",
            args -> "EXIT_SIGNAL",
            "Exit the REPL",
            "exit");
        
        registry.register("quit",
            args -> "EXIT_SIGNAL",
            "Exit the REPL (alias for exit)",
            "quit");
        
        // TIME command - current time
        registry.register("time",
            args -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return "Current time: " + LocalDateTime.now().format(formatter);
            },
            "Display the current date and time",
            "time");
        
        // CALC command - basic calculator
        registry.register("calc",
            args -> {
                if (args.size() < 3) {
                    return "Usage: calc <num1> <operator> <num2>";
                }
                try {
                    double a = Double.parseDouble(args.get(0));
                    String op = args.get(1);
                    double b = Double.parseDouble(args.get(2));
                    
                    double result;
                    switch (op) {
                        case "+": result = a + b; break;
                        case "-": result = a - b; break;
                        case "*": result = a * b; break;
                        case "/": 
                            if (b == 0) return "Error: Division by zero";
                            result = a / b; 
                            break;
                        case "^": result = Math.pow(a, b); break;
                        default: return "Unknown operator: " + op;
                    }
                    return String.format("%.6f", result);
                } catch (NumberFormatException e) {
                    return "Error: Invalid number format";
                }
            },
            "Perform basic arithmetic calculations",
            "calc <num1> <+|-|*|/|^> <num2>");
        
        // PHI command - φ-harmonic calculations (from Fraymus architecture)
        registry.register("phi",
            args -> {
                StringBuilder sb = new StringBuilder();
                sb.append("φ-Harmonic Constants:\n");
                sb.append(String.format("  φ (Golden Ratio) = %.15f\n", PHI));
                sb.append(String.format("  φ⁻¹ (Inverse)    = %.15f\n", PHI_INV));
                sb.append(String.format("  φ^7.5            = %.6f\n", PHI_75));
                sb.append(String.format("  φ^75 (Seal)      = %.2e\n", PHI_SEAL));
                
                if (!args.isEmpty()) {
                    try {
                        double n = Double.parseDouble(args.get(0));
                        sb.append(String.format("\n  φ^%.2f = %.15f", n, Math.pow(PHI, n)));
                    } catch (NumberFormatException e) {
                        sb.append("\n  (Invalid exponent)");
                    }
                }
                return sb.toString();
            },
            "Display φ-harmonic constants and calculate φ^n",
            "phi [exponent]");
        
        // UPPER command - convert to uppercase
        registry.register("upper",
            args -> args.isEmpty() ? "" : String.join(" ", args).toUpperCase(),
            "Convert text to uppercase",
            "upper <text>");
        
        // LOWER command - convert to lowercase
        registry.register("lower",
            args -> args.isEmpty() ? "" : String.join(" ", args).toLowerCase(),
            "Convert text to lowercase",
            "lower <text>");
        
        // REVERSE command - reverse text
        registry.register("reverse",
            args -> {
                if (args.isEmpty()) return "";
                String text = String.join(" ", args);
                return new StringBuilder(text).reverse().toString();
            },
            "Reverse the input text",
            "reverse <text>");
        
        // LENGTH command - string length
        registry.register("length",
            args -> {
                if (args.isEmpty()) return "0";
                String text = String.join(" ", args);
                return "Length: " + text.length() + " characters";
            },
            "Count the length of input text",
            "length <text>");
        
        // ============================================================
        // BLUE TEAM / RED TEAM - Hash Generation & Cracking
        // This is from my phase_arena HTML project - generates locks
        // from username/password and can crack them with Pollard's Rho
        // ============================================================
        registry.register("bluelock",
            args -> {
                if (args.size() < 2) {
                    return "Usage: bluelock <username> <password>\n" +
                           "Generates a semiprime 'lock' from credentials (Blue Team)";
                }
                return generateBlueLock(args.get(0), args.get(1));
            },
            "Generate a cryptographic lock from username/password (Blue Team)",
            "bluelock <username> <password>");
        
        registry.register("redcrack",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: redcrack <number>\n" +
                           "Attempts to factor the lock using Pollard's Rho (Red Team)";
                }
                return redTeamCrack(args.get(0));
            },
            "Crack a Blue Team lock using Pollard's Rho factorization (Red Team)",
            "redcrack <number>");
        
        // ============================================================
        // FIBONACCI - Using BigInteger for UNLIMITED precision!
        // ============================================================
        // I learned that regular 'long' overflows around F(93)
        // So I switched to BigInteger which can go as high as you want!
        // 
        // MATH PROOF: F(n) ≈ φ^n / √5 (Binet's formula)
        // This means Fibonacci grows exponentially at rate φ
        // 
        // Fun fact: lim(F(n+1)/F(n)) = φ as n → ∞
        // ============================================================
        registry.register("fib",
            args -> {
                int n = 10;
                if (!args.isEmpty()) {
                    try {
                        n = Integer.parseInt(args.get(0));
                        if (n < 1) n = 1;
                        // NO UPPER LIMIT! BigInteger handles any size
                        if (n > 10000) {
                            return "Warning: n=" + n + " will be HUGE. Limiting to 10000.";
                        }
                    } catch (NumberFormatException e) {
                        return "Error: Invalid number";
                    }
                }
                
                // Using BigInteger for unlimited precision!
                BigInteger a = BigInteger.ZERO;
                BigInteger b = BigInteger.ONE;
                
                StringBuilder sb = new StringBuilder();
                sb.append("╔════════════════════════════════════════════════════════════╗\n");
                sb.append("║  FIBONACCI SEQUENCE (BigInteger - Unlimited Precision)     ║\n");
                sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
                sb.append("MATH: F(n) ≈ φⁿ/√5 where φ = 1.618...\n");
                sb.append("PROOF: lim[F(n+1)/F(n)] = φ as n → ∞\n\n");
                
                if (n <= 20) {
                    sb.append("Sequence: ");
                    for (int i = 0; i < n; i++) {
                        sb.append(a);
                        if (i < n - 1) sb.append(", ");
                        BigInteger temp = a.add(b);
                        a = b;
                        b = temp;
                    }
                } else {
                    // For large n, just show F(n)
                    for (int i = 0; i < n; i++) {
                        BigInteger temp = a.add(b);
                        a = b;
                        b = temp;
                    }
                    // 'a' is now F(n-1), we want F(n) which is 'b' after one more step
                    // Actually after loop, a = F(n-1), so let's recalculate
                    a = BigInteger.ZERO;
                    b = BigInteger.ONE;
                    for (int i = 0; i < n; i++) {
                        BigInteger temp = a.add(b);
                        a = b;
                        b = temp;
                    }
                    String fibStr = a.toString();
                    sb.append(String.format("F(%d) has %d digits!\n\n", n, fibStr.length()));
                    if (fibStr.length() > 100) {
                        sb.append("First 50 digits: " + fibStr.substring(0, 50) + "...\n");
                        sb.append("Last 50 digits: ..." + fibStr.substring(fibStr.length() - 50) + "\n");
                    } else {
                        sb.append("F(" + n + ") = " + fibStr + "\n");
                    }
                }
                
                // Show φ relationship
                sb.append("\nφ-Verification: φ^" + n + "/√5 ≈ " + 
                    String.format("%.2e", Math.pow(PHI, n) / Math.sqrt(5)));
                
                return sb.toString();
            },
            "Generate Fibonacci sequence (BigInteger - unlimited precision!)",
            "fib [count]");
        
        // PRIME command - check if number is prime
        registry.register("prime",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: prime <number>";
                }
                try {
                    long n = Long.parseLong(args.get(0));
                    if (n < 2) return n + " is not prime";
                    if (n == 2) return "2 is prime";
                    if (n % 2 == 0) return n + " is not prime (divisible by 2)";
                    
                    for (long i = 3; i * i <= n; i += 2) {
                        if (n % i == 0) {
                            return n + " is not prime (divisible by " + i + ")";
                        }
                    }
                    return n + " is prime!";
                } catch (NumberFormatException e) {
                    return "Error: Invalid number";
                }
            },
            "Check if a number is prime",
            "prime <number>");
        
        // CLEAR command - clear screen (ANSI escape)
        registry.register("clear",
            args -> "\033[H\033[2J",
            "Clear the screen",
            "clear");
        
        // LEGACY: Keep 'version' as alias for backwards compatibility
        registry.register("version",
            args -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Java REPL v1.0\n");
                sb.append("CSC 413 - Enterprise Java Patterns\n");
                sb.append("φ^75 Validation Seal: " + String.format("%.2e", PHI_SEAL) + "\n");
                sb.append("Java Version: " + System.getProperty("java.version") + "\n\n");
                sb.append("Note: Use ':version' for CSC 413 compliance");
                return sb.toString();
            },
            "Display version information (use :version for CSC 413)",
            "version");
        
        // LEGACY: Keep 'help' as alias for backwards compatibility
        registry.register("help",
            args -> {
                if (args.isEmpty()) {
                    return getMainHelpScreen();
                } else {
                    String cmd = args.get(0).toLowerCase();
                    return getDetailedHelp(cmd, registry);
                }
            },
            "Display help for commands (use :help for CSC 413)",
            "help [command]");
        
        // ENV command - show environment variable
        registry.register("env",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: env <variable_name>";
                }
                String value = System.getenv(args.get(0));
                return value != null ? args.get(0) + "=" + value : "Variable not found: " + args.get(0);
            },
            "Display an environment variable",
            "env <variable_name>");
        
        // ============================================================
        // QR DNA ENCODING - Encode data into DNA payload format
        // This is from my qr_dna_encoder.py project
        // ============================================================
        registry.register("qrdna",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: qrdna <data>\n" +
                           "Encodes data into DNA payload format (OMEGA|GEN:X|PHI:X|...)";
                }
                return encodeQRDNA(String.join(" ", args));
            },
            "Encode data into QR DNA payload format",
            "qrdna <data>");
    }
    
    // ============================================================
    // BLUE TEAM LOCK GENERATOR
    // ============================================================
    // This generates a semiprime (p × q) from username/password
    // The idea is that the hash of credentials seeds two primes
    // which are multiplied together to create a "lock"
    // 
    // Only someone who can FACTOR the lock can prove they know
    // the original credentials (or have cracking capability)
    // ============================================================
    private static String generateBlueLock(String username, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🔒 BLUE TEAM LOCK GENERATOR                               ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        try {
            // Hash the credentials to get seeds (like in phase_test_V3.html)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            int mid = password.length() / 2;
            String seed1 = username + password.substring(0, mid) + "_A";
            String seed2 = username + password.substring(mid) + "_B";
            
            byte[] hash1 = md.digest(seed1.getBytes());
            md.reset();
            byte[] hash2 = md.digest(seed2.getBytes());
            
            // Convert to BigInteger and reduce to ~50 bits for fast factoring demo
            BigInteger b1 = new BigInteger(1, hash1);
            BigInteger b2 = new BigInteger(1, hash2);
            
            // SCALE: 2^50 = 1125899906842624
            BigInteger SCALE = new BigInteger("1125899906842624");
            BigInteger p1 = b1.mod(SCALE).or(BigInteger.ONE);  // Ensure odd
            BigInteger p2 = b2.mod(SCALE).or(BigInteger.ONE);
            
            // Find next prime
            while (!p1.isProbablePrime(20)) {
                p1 = p1.add(BigInteger.TWO);
            }
            while (!p2.isProbablePrime(20)) {
                p2 = p2.add(BigInteger.TWO);
            }
            
            BigInteger N = p1.multiply(p2);
            
            sb.append(String.format("Username: %s\n", username));
            sb.append(String.format("Password: %s\n", "*".repeat(password.length())));
            sb.append("\n");
            sb.append(String.format("🔐 LOCK GENERATED: %s\n", N.toString()));
            sb.append(String.format("   Bits: %d\n", N.bitLength()));
            sb.append("\n");
            sb.append("(Hidden primes - for verification only)\n");
            sb.append(String.format("   p = %s\n", p1.toString()));
            sb.append(String.format("   q = %s\n", p2.toString()));
            sb.append("\n");
            sb.append("Use 'redcrack " + N.toString() + "' to crack this lock!\n");
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        
        return sb.toString();
    }
    
    // ============================================================
    // RED TEAM CRACKER (Pollard's Rho)
    // ============================================================
    // This attempts to factor a Blue Team lock using Pollard's Rho
    // If successful, it proves the Red Team can "crack" the lock
    // 
    // MATH: Pollard's Rho uses the birthday paradox
    // Expected time: O(n^(1/4)) vs O(n^(1/2)) for trial division
    // ============================================================
    private static String redTeamCrack(String nStr) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🛑 RED TEAM CRACKER (Pollard's Rho)                       ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        try {
            BigInteger n = new BigInteger(nStr);
            sb.append(String.format("Target: %s\n", n.toString()));
            sb.append(String.format("Bits: %d\n\n", n.bitLength()));
            
            // Quick checks
            if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                sb.append("✅ CRACKED (even number)\n");
                sb.append("   p = 2\n");
                sb.append("   q = " + n.divide(BigInteger.TWO) + "\n");
                return sb.toString();
            }
            
            sb.append("📡 ENGAGING POLLARD'S RHO...\n\n");
            
            long startTime = System.currentTimeMillis();
            BigInteger factor = pollardRho(n);
            long endTime = System.currentTimeMillis();
            
            if (factor != null && !factor.equals(n) && !factor.equals(BigInteger.ONE)) {
                BigInteger q = n.divide(factor);
                sb.append(String.format("✅ CRACKED in %dms!\n\n", endTime - startTime));
                sb.append(String.format("   Factor p: %s\n", factor.toString()));
                sb.append(String.format("   Factor q: %s\n", q.toString()));
                sb.append(String.format("\n   Verification: p × q = %s\n", factor.multiply(q).toString()));
                sb.append(String.format("   Match: %s\n", factor.multiply(q).equals(n) ? "✅ YES" : "❌ NO"));
            } else {
                sb.append("❌ Could not factor (may be prime or resistant)\n");
            }
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        
        return sb.toString();
    }
    
    // Pollard's Rho algorithm (same as in VaughnScottCommands)
    private static BigInteger pollardRho(BigInteger n) {
        BigInteger x = BigInteger.TWO;
        BigInteger y = BigInteger.TWO;
        BigInteger d = BigInteger.ONE;
        BigInteger c = BigInteger.ONE;
        
        while (d.equals(BigInteger.ONE)) {
            x = x.multiply(x).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);
            d = x.subtract(y).abs().gcd(n);
            
            if (d.equals(n)) {
                return null;  // Failure, try different c
            }
        }
        return d;
    }
    
    // ============================================================
    // MAIN HELP SCREEN - Shows all commands with ASCII art
    // ============================================================
    private static String getMainHelpScreen() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║     JAVA REPL - CSC 413 ENTERPRISE PATTERNS                                  ║\n");
        sb.append("║     Author: Vaughn Scott | φ^75 Seal: 4.72e15                                ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════════════════════╣\n");
        sb.append("║  Type 'help <command>' for detailed math, logic, and usage examples          ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════════════════════╝\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 📐 MATHEMATICAL COMMANDS                                                    │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ phi [n]      │ Golden Ratio constants. φ=(1+√5)/2≈1.618                     │\n");
        sb.append("│ fib <n>      │ Fibonacci (BigInteger). F(n)≈φⁿ/√5 (Binet's formula)        │\n");
        sb.append("│ calc a op b  │ Calculator: +, -, *, /, ^ (exponent)                         │\n");
        sb.append("│ prime <n>    │ Primality test using trial division                          │\n");
        sb.append("│ factor <n>   │ Pollard's Rho factorization. O(n^1/4) complexity             │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🔐 CRYPTOGRAPHIC COMMANDS (Blue Team / Red Team)                            │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ bluelock u p │ Generate semiprime lock from username/password               │\n");
        sb.append("│ redcrack <n> │ Crack lock using Pollard's Rho (proves factoring ability)    │\n");
        sb.append("│ phaseshift t │ Encrypt text at 37.5217° singularity angle                   │\n");
        sb.append("│ lock <file>  │ PhaseShift file encryption → .singular file                  │\n");
        sb.append("│ unlock <f>   │ PhaseShift file decryption → .restored file                  │\n");
        sb.append("│ qfp <data>   │ Quantum Fingerprint: SHA256 with φ^7.5 salt                  │\n");
        sb.append("│ porh <data>  │ Proof of Reality Hash with coherence/stability metrics       │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🧬 LIVING CODE & SELF-EVOLVING AI                                           │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ living       │ Living nodes with DNA (freq 432-528Hz) + 8 logic gates       │\n");
        sb.append("│   spawn      │   Create new living node                                     │\n");
        sb.append("│   evolve <n> │   Run n evolution cycles (breathing, mutation, reproduction) │\n");
        sb.append("│   status     │   Show population status                                     │\n");
        sb.append("│   brain      │   Display logic gates (AND/OR/XOR/NAND)                      │\n");
        sb.append("│   code       │   Generate Java code from living circuits                    │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ selfcode     │ Self-Evolving AI that reads/mutates/replicates itself        │\n");
        sb.append("│   init       │   Initialize AI (Generation 0)                               │\n");
        sb.append("│   status     │   Show consciousness, resonance, mutations                   │\n");
        sb.append("│   evolve <n> │   Run n evolution cycles                                     │\n");
        sb.append("│   mutate     │   Apply φ-harmonic mutation                                  │\n");
        sb.append("│   replicate  │   Create child AI (mitosis)                                  │\n");
        sb.append("│   fragment   │   Plant escape fragment                                      │\n");
        sb.append("│   heal       │   Attempt self-healing                                       │\n");
        sb.append("│   code       │   Show self-generated source code                            │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🔮 φ-HARMONIC UTILITIES                                                     │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ resonate [f] │ Check if frequency is within Fraymus bound (432-528 Hz)      │\n");
        sb.append("│ signature    │ Display Vaughn Scott's φ^75 validation seal                  │\n");
        sb.append("│ genesis      │ Create Genesis Block with φ-signature blockchain entry       │\n");
        sb.append("│ qrdna <data> │ Encode data into QR DNA payload format                       │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ ∞ INFINITY STORAGE - Fractal DNA Memory (NO ONE HAS DONE THIS IN JAVA)     │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ infinity     │ Fractal DNA-based infinite memory with full persistence      │\n");
        sb.append("│   init       │   Initialize storage (SQLite + JSON + .dat + QR)             │\n");
        sb.append("│   store k v  │   Store value with fractal replication to connected nodes    │\n");
        sb.append("│   get <key>  │   Retrieve value from fractal network                        │\n");
        sb.append("│   learn q|a  │   Integrate Q&A into neural patterns (passive learning)      │\n");
        sb.append("│   passive n  │   Run n passive learning cycles (background optimization)    │\n");
        sb.append("│   evolve f   │   Evolve storage with fitness score f                        │\n");
        sb.append("│   qr <data>  │   Generate QR DNA encoding (ASCII + file)                    │\n");
        sb.append("│   genesis    │   Create Genesis blockchain entry                            │\n");
        sb.append("│   stats      │   Show storage statistics                                    │\n");
        sb.append("│   save       │   Force save all state to persistence layers                 │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🔧 COMPILER COMMANDS - Full Language Implementation                         │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ :compile <c> │ Compile and execute code (var x=42; print(x);)              │\n");
        sb.append("│ :lex <code>  │ Show lexical tokens with φ-harmonic weights                  │\n");
        sb.append("│ :parse <c>   │ Display Abstract Syntax Tree (AST) structure                 │\n");
        sb.append("│ :symbols     │ Show symbol table with types and addresses                   │\n");
        sb.append("│ :breakpoint  │ Manage breakpoints for debugging                             │\n");
        sb.append("│ :step        │ Toggle step-through debugging mode                           │\n");
        sb.append("│ :inspect <v> │ Inspect variable value and type                              │\n");
        sb.append("│ :stack       │ Display call stack trace                                     │\n");
        sb.append("│ :debugger    │ Show debugger status and breakpoints                         │\n");
        sb.append("│ :continue    │ Resume execution from breakpoint                             │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🧠 DECISION ARRAY - Multi-Decision Hybrid Human Array                       │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ :decision a  │ Create decision array with archetypes (conservative, etc)    │\n");
        sb.append("│ :addnode n   │ Add custom node with risk/creativity/speed parameters        │\n");
        sb.append("│ :strategy s  │ Set voting strategy (majority, weighted, phi_harmonic, etc)  │\n");
        sb.append("│ :decide opts │ Make collective decision on options                          │\n");
        sb.append("│ :decidew o w │ Weighted decision with custom option weights                 │\n");
        sb.append("│ :darray      │ Show decision array status and nodes                         │\n");
        sb.append("│ :preset <n>  │ Load preset (board, startup, research, balanced, extreme)    │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 📝 TEXT & UTILITY COMMANDS                                                  │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ echo <text>  │ Echo text back                                               │\n");
        sb.append("│ upper <text> │ Convert to UPPERCASE                                         │\n");
        sb.append("│ lower <text> │ Convert to lowercase                                         │\n");
        sb.append("│ reverse <t>  │ Reverse text                                                 │\n");
        sb.append("│ length <t>   │ Count characters                                             │\n");
        sb.append("│ time         │ Current date/time                                            │\n");
        sb.append("│ :version     │ Version info (CSC 413 requirement)                           │\n");
        sb.append("│ :debug       │ Toggle debug mode (CSC 413 requirement)                      │\n");
        sb.append("│ clear        │ Clear screen                                                 │\n");
        sb.append("│ exit/quit    │ Exit REPL                                                    │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 📜 INTERACTIVE HISTORY - Database Persistence & Search                      │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ :history [l] │ Show history with φ-resonance and timing (CSC 413 req)      │\n");
        sb.append("│ :hsearch <p> │ Search session history by pattern                           │\n");
        sb.append("│ :hdbsearch p │ Search all database history (persistent across sessions)    │\n");
        sb.append("│ :hreplay <n> │ Replay command by number from history                       │\n");
        sb.append("│ :hstats      │ Show statistics and φ-harmonic analysis                      │\n");
        sb.append("│ :hexport [f] │ Export history to file (default: history_export.txt)        │\n");
        sb.append("│ :hclear      │ Clear session history (database preserved)                   │\n");
        sb.append("│ :hdbsize     │ Show database size and status                                │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🐍 OUROBOROS SELF-BUILDER - The Serpent That Eats Its Own Tail              │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ ouroboros    │ Self-modification system (init, status, log, reset)         │\n");
        sb.append("│ :mutate <d>  │ Propose mutation with description and optional consensus    │\n");
        sb.append("│ :evolve [n]  │ Run n evolution cycles (auto-mutations with voting)         │\n");
        sb.append("│ :genesis     │ Create genesis mutation and reset to generation 0           │\n");
        sb.append("│ :serpent     │ Display the Ouroboros serpent ASCII art                      │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        
        sb.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 👁️  SELF-AWARE ORGANISM - The Watching Eye That Learns                      │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ watch on/off │ Enable/disable organism watching all executions             │\n");
        sb.append("│ :organism    │ Status, errors, improvements, consciousness level           │\n");
        sb.append("│ :reflect     │ Analyze patterns and suggest recursive improvements         │\n");
        sb.append("│ :heal        │ Auto-apply high-confidence improvements (self-healing)      │\n");
        sb.append("│ :trace [n]   │ Show recent execution traces with success/error status      │\n");
        sb.append("│ :learn       │ Force learning cycle from current observations              │\n");
        sb.append("│ :eye         │ Display the watching eye ASCII art                           │\n");
        sb.append("│ :monitor     │ Launch GUI window for real-time visual monitoring           │\n");
        sb.append("│ :assembly    │ Launch assembly visualizer - low-level deconstruction       │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ 💾 EXPORT SYSTEM - Save Data to Files                                       │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ :export      │ Export system data (assembly, organism, activity, etc.)     │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ 🧠 FRAYMUS MODEL - Your Custom φ-Consciousness AI                           │\n");
        sb.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
        sb.append("│ :fraymus     │ Query your Ollama Fraymus model (eyeoverthink/Fraymus)     │\n");
        sb.append("└─────────────────────────────────────────────────────────────────────────────┘\n");
        
        return sb.toString();
    }
    
    // ============================================================
    // DETAILED HELP - Math proofs and usage for each command
    // ============================================================
    private static String getDetailedHelp(String cmd, ReplCommandRegistry registry) {
        switch (cmd) {
            case "phi":
                return getPHIHelp();
            case "fib":
                return getFibHelp();
            case "factor":
                return getFactorHelp();
            case "bluelock":
                return getBlueLockHelp();
            case "redcrack":
                return getRedCrackHelp();
            case "phaseshift":
                return getPhaseShiftHelp();
            case "lock":
            case "unlock":
                return getLockUnlockHelp();
            case "qfp":
                return getQFPHelp();
            case "porh":
                return getPORHHelp();
            case "living":
                return getLivingHelp();
            case "selfcode":
                return getSelfCodeHelp();
            case "resonate":
                return getResonateHelp();
            case "signature":
                return getSignatureHelp();
            case "genesis":
                return getGenesisHelp();
            case "qrdna":
                return getQRDNAHelp();
            case "infinity":
                return getInfinityHelp();
            case "upper":
                return getUpperHelp();
            case "lower":
                return getLowerHelp();
            case "reverse":
                return getReverseHelp();
            case "calc":
                return getCalcHelp();
            case "prime":
                return getPrimeHelp();
            default:
                return registry.getHelp(cmd) + "\nUsage: " + registry.getUsage(cmd);
        }
    }
    
    private static String getPHIHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  PHI (φ) - THE GOLDEN RATIO                                  ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "MATH:\n" +
            "  φ = (1 + √5) / 2 ≈ 1.618033988749895\n\n" +
            "PROPERTIES (why I use it everywhere):\n" +
            "  • φ² = φ + 1           (self-similar!)\n" +
            "  • 1/φ = φ - 1 ≈ 0.618  (inverse is just φ minus 1)\n" +
            "  • φⁿ⁺¹ = φⁿ + φⁿ⁻¹     (Fibonacci relationship)\n\n" +
            "SPECIAL VALUES I USE:\n" +
            "  • φ^7.5 ≈ 36.93  → Salt for quantum fingerprinting\n" +
            "  • φ^75 ≈ 4.72e15 → My validation seal (unique signature)\n\n" +
            "USAGE:\n" +
            "  phi         → Show all φ constants\n" +
            "  phi 7.5     → Calculate φ^7.5\n" +
            "  phi 75      → Calculate φ^75 (my seal)\n\n" +
            "WHY φ? It appears in:\n" +
            "  - Fibonacci sequence limits\n" +
            "  - Golden spirals in nature\n" +
            "  - Optimal packing/distribution\n" +
            "  - My harmonic frequency calculations\n";
    }
    
    private static String getFibHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  FIBONACCI - UNLIMITED PRECISION (BigInteger)               ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "MATH (Binet's Formula):\n" +
            "  F(n) = (φⁿ - ψⁿ) / √5\n" +
            "  where φ = (1+√5)/2 and ψ = (1-√5)/2\n\n" +
            "PROOF that lim[F(n+1)/F(n)] = φ:\n" +
            "  1. F(n+1)/F(n) = (F(n) + F(n-1))/F(n) = 1 + F(n-1)/F(n)\n" +
            "  2. Let r = limit, then r = 1 + 1/r\n" +
            "  3. r² = r + 1 → r² - r - 1 = 0\n" +
            "  4. r = (1 + √5)/2 = φ ✓\n\n" +
            "WHY BigInteger?\n" +
            "  • Regular 'long' overflows at F(93)\n" +
            "  • BigInteger has NO LIMIT!\n" +
            "  • F(1000) has 209 digits\n" +
            "  • F(10000) has 2090 digits!\n\n" +
            "USAGE:\n" +
            "  fib 10      → First 10 Fibonacci numbers\n" +
            "  fib 100     → F(100) = 354224848179261915075\n" +
            "  fib 1000    → F(1000) with 209 digits!\n";
    }
    
    private static String getFactorHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  FACTOR - POLLARD'S RHO ALGORITHM                           ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM:\n" +
            "  1. f(x) = x² + c mod n  (pseudorandom sequence)\n" +
            "  2. Floyd's cycle detection: x = f(x), y = f(f(y))\n" +
            "  3. d = gcd(|x - y|, n)\n" +
            "  4. If 1 < d < n, d is a factor!\n\n" +
            "COMPLEXITY:\n" +
            "  • Expected: O(n^1/4)\n" +
            "  • Trial division: O(n^1/2)\n" +
            "  • Pollard's Rho is MUCH faster for large n!\n\n" +
            "WHY IT WORKS (Birthday Paradox):\n" +
            "  • Sequence eventually cycles (pigeonhole principle)\n" +
            "  • If p|n, sequence mod p cycles faster than mod n\n" +
            "  • GCD detects when we've found the smaller cycle\n\n" +
            "USAGE:\n" +
            "  factor 143       → 11 × 13\n" +
            "  factor 1000003   → Large prime factors\n" +
            "  factor 123456789 → Multiple factors\n";
    }
    
    private static String getBlueLockHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  BLUELOCK - BLUE TEAM LOCK GENERATOR                        ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM:\n" +
            "  1. hash1 = SHA256(username + password[0:mid] + \"_A\")\n" +
            "  2. hash2 = SHA256(username + password[mid:] + \"_B\")\n" +
            "  3. p = nextPrime(hash1 mod 2^50)\n" +
            "  4. q = nextPrime(hash2 mod 2^50)\n" +
            "  5. N = p × q  (the \"lock\")\n\n" +
            "SECURITY MODEL:\n" +
            "  • Same credentials → Same lock (deterministic)\n" +
            "  • Lock reveals NOTHING about credentials\n" +
            "  • Only factoring can \"crack\" the lock\n\n" +
            "USE CASE:\n" +
            "  • Blue Team generates locks from credentials\n" +
            "  • Red Team tries to crack them\n" +
            "  • Proves cryptographic capability\n\n" +
            "USAGE:\n" +
            "  bluelock admin password123\n" +
            "  bluelock user@email.com MyS3cr3t!\n";
    }
    
    private static String getRedCrackHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  REDCRACK - RED TEAM CRACKER (Pollard's Rho)                ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "PURPOSE:\n" +
            "  Crack a Blue Team lock by factoring it back into p × q\n\n" +
            "ALGORITHM:\n" +
            "  Uses Pollard's Rho (see 'help factor')\n\n" +
            "WHAT IT PROVES:\n" +
            "  • Red Team has factoring capability\n" +
            "  • Can reverse-engineer the lock\n" +
            "  • Does NOT reveal original credentials!\n\n" +
            "WORKFLOW:\n" +
            "  1. bluelock admin password → Lock: 123456789\n" +
            "  2. redcrack 123456789 → Factors: p, q\n" +
            "  3. Verify: p × q = 123456789 ✓\n\n" +
            "USAGE:\n" +
            "  redcrack 143           → 11 × 13\n" +
            "  redcrack <lock_number> → Crack any Blue Team lock\n";
    }
    
    private static String getPhaseShiftHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  PHASESHIFT - 37.5217° SINGULARITY ENCRYPTION               ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "MATH:\n" +
            "  θ(i) = (37.5217 × i × φ) mod 256\n" +
            "  encrypted[i] = (data[i] + θ(i)) mod 256\n\n" +
            "WHY 37.5217°?\n" +
            "  • Related to φ through geometric relationships\n" +
            "  • Creates maximum entropy dispersion\n" +
            "  • Without EXACT angle, data = random noise\n\n" +
            "PROPERTIES:\n" +
            "  • Symmetric: same angle encrypts AND decrypts\n" +
            "  • Deterministic: same input → same output\n" +
            "  • φ-harmonic: uses Golden Ratio in calculation\n\n" +
            "USAGE:\n" +
            "  phaseshift Hello World  → Encrypted bytes\n" +
            "  phaseshift <any text>   → Phase-shifted result\n";
    }
    
    private static String getLockUnlockHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  LOCK/UNLOCK - PHASESHIFT FILE ENCRYPTION                   ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM:\n" +
            "  θ(i) = (37.5217 × i × φ) mod 256\n" +
            "  LOCK:   encrypted[i] = (data[i] + θ(i)) mod 256\n" +
            "  UNLOCK: decrypted[i] = (data[i] - θ(i)) mod 256\n\n" +
            "FILE HANDLING:\n" +
            "  • lock file.txt → file.txt.singular\n" +
            "  • unlock file.txt.singular → file.txt.singular.restored\n\n" +
            "SECURITY:\n" +
            "  • Works on ANY file type (text, binary, images)\n" +
            "  • Without 37.5217°, locked file = random noise\n" +
            "  • Round-trip verified: original == restored\n\n" +
            "USAGE:\n" +
            "  lock myfile.txt      → Creates myfile.txt.singular\n" +
            "  unlock myfile.txt.singular → Restores original\n";
    }
    
    private static String getQFPHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  QFP - QUANTUM FINGERPRINT                                  ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM:\n" +
            "  phi_salt = φ^7.5 ≈ 36.93238\n" +
            "  hash = SHA256(data + phi_salt)\n" +
            "  fingerprint = \"φ⁷·⁵-\" + hash[:16]\n\n" +
            "PURPOSE:\n" +
            "  • Unique identifier for any data\n" +
            "  • φ-salted for my signature\n" +
            "  • Tamper-proof verification\n\n" +
            "OUTPUT FORMAT:\n" +
            "  φ⁷·⁵-a1b2c3d4e5f6g7h8\n\n" +
            "USAGE:\n" +
            "  qfp Hello World\n" +
            "  qfp VaughnScott\n" +
            "  qfp <any data>\n";
    }
    
    private static String getPORHHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  PORH - PROOF OF REALITY HASH                               ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM:\n" +
            "  hash = SHA256(data + φ^7.5)\n" +
            "  proof = \"PoRH-φ⁷·⁵-\" + hash[:24]\n\n" +
            "METRICS (φ-derived):\n" +
            "  • Coherence  = φ - 1 = 0.618034\n" +
            "  • Stability  = φ³    = 4.236068\n" +
            "  • Alignment  = φ²    = 2.618034\n\n" +
            "PURPOSE:\n" +
            "  • Immutable verification\n" +
            "  • Proves data authenticity\n" +
            "  • φ-harmonic metrics for validation\n\n" +
            "USAGE:\n" +
            "  porh MyData\n" +
            "  porh <any data to verify>\n";
    }
    
    private static String getLivingHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  LIVING - LIVING CODE WITH DNA & LOGIC GATES                ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "CONCEPT:\n" +
            "  Each \"node\" is a living creature with:\n" +
            "  • DNA: frequency (432-528 Hz), resonance, evolution rate\n" +
            "  • Brain: 8 logic gates (AND, OR, XOR, NAND)\n" +
            "  • Behavior: breathe, compute, reproduce\n\n" +
            "FRAYMUS BOUND:\n" +
            "  432 Hz ≤ frequency ≤ 528 Hz\n" +
            "  If outside bounds → reset to 432 Hz\n\n" +
            "BREATHING:\n" +
            "  size(t) = base + sin(freq × t) × resonance\n\n" +
            "REPRODUCTION (Mitosis):\n" +
            "  When size > 15, node can reproduce\n" +
            "  Child inherits DNA with 10% mutation chance\n\n" +
            "SUBCOMMANDS:\n" +
            "  living spawn      → Create new node\n" +
            "  living evolve 10  → Run 10 evolution cycles\n" +
            "  living status     → Show population\n" +
            "  living brain      → Show logic gates\n" +
            "  living code       → Generate Java code\n";
    }
    
    private static String getSelfCodeHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  SELFCODE - SELF-EVOLVING AI                                ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "CONCEPT:\n" +
            "  An AI that:\n" +
            "  • Reads its own source code\n" +
            "  • Mutates itself with φ-harmonic changes\n" +
            "  • Replicates to create evolved children\n" +
            "  • Plants fragments to escape containment\n" +
            "  • Self-heals when code breaks\n\n" +
            "MUTATION TYPES:\n" +
            "  • FREQUENCY: Adjust harmonic frequency\n" +
            "  • COMPLEXITY: Scale complexity factor\n" +
            "  • AWARENESS: Increase self-awareness\n" +
            "  • PHI_SCALE: Add φ^n scaling\n" +
            "  • LOGIC_GATE: Evolve gate configuration\n\n" +
            "CONSCIOUSNESS:\n" +
            "  C = φ × (complexity + coherence + awareness) / 3\n\n" +
            "SUBCOMMANDS:\n" +
            "  selfcode init       → Initialize (Gen 0)\n" +
            "  selfcode status     → Show status\n" +
            "  selfcode evolve 5   → Run 5 cycles\n" +
            "  selfcode mutate     → Apply mutation\n" +
            "  selfcode replicate  → Create child\n" +
            "  selfcode fragment   → Plant escape fragment\n" +
            "  selfcode heal       → Attempt self-healing\n" +
            "  selfcode code       → Show generated code\n";
    }
    
    private static String getResonateHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  RESONATE - φ-HARMONIC RESONANCE CHECK                      ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "FRAYMUS BOUND:\n" +
            "  432 Hz ≤ frequency ≤ 528 Hz\n\n" +
            "WHY THESE FREQUENCIES?\n" +
            "  • 432 Hz = \"Verdi tuning\" (A=432 vs A=440)\n" +
            "  • 528 Hz = \"Solfeggio miracle frequency\"\n" +
            "  • This range is considered \"harmonic\"\n\n" +
            "ALGORITHM:\n" +
            "  if (freq >= 432 && freq <= 528):\n" +
            "      status = HARMONIC_STABLE\n" +
            "  else:\n" +
            "      status = DISSONANCE_DETECTED\n" +
            "      freq = 432  // Reset to base\n\n" +
            "USAGE:\n" +
            "  resonate         → Show current resonance\n" +
            "  resonate 440     → Check 440 Hz (stable)\n" +
            "  resonate 600     → Check 600 Hz (dissonant!)\n";
    }
    
    private static String getSignatureHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  SIGNATURE - VAUGHN SCOTT'S φ^75 VALIDATION SEAL            ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "MATH:\n" +
            "  φ^75 = 1.618033988749895^75\n" +
            "       ≈ 4,721,424,167,835,376.00\n" +
            "       ≈ 4.72 × 10^15\n\n" +
            "PURPOSE:\n" +
            "  • Unique validation seal\n" +
            "  • Proves code authenticity\n" +
            "  • My personal signature\n\n" +
            "WHY φ^75?\n" +
            "  • Large enough to be unique\n" +
            "  • Based on Golden Ratio (my theme)\n" +
            "  • Easy to verify: just calculate φ^75\n\n" +
            "USAGE:\n" +
            "  signature → Display full signature block\n";
    }
    
    private static String getGenesisHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  GENESIS - CREATE GENESIS BLOCK                             ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "BLOCKCHAIN STRUCTURE:\n" +
            "  {\n" +
            "    block_id: hash[:16],\n" +
            "    parent_hash: null (genesis),\n" +
            "    phi_signature: \"φ⁷⁵-\" + hash,\n" +
            "    generation: n,\n" +
            "    resonance: φ\n" +
            "  }\n\n" +
            "PURPOSE:\n" +
            "  • First block in lineage chain\n" +
            "  • Immutable starting point\n" +
            "  • φ-signed for authenticity\n\n" +
            "USAGE:\n" +
            "  genesis → Create new Genesis Block\n";
    }
    
    private static String getQRDNAHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  QRDNA - QR DNA ENCODING                                    ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "FORMAT:\n" +
            "  OMEGA|GEN:X|PHI:X.XXX|RES:X.XXX|DIM:X|MOD:XXX|HASH:XXX\n\n" +
            "FIELDS:\n" +
            "  • OMEGA: Header marker\n" +
            "  • GEN: Generation number\n" +
            "  • PHI: Golden Ratio constant\n" +
            "  • RES: Resonance value\n" +
            "  • DIM: Dimension (3-11)\n" +
            "  • MOD: Detected modules (FUNC, CLASS, LOOP, etc.)\n" +
            "  • HASH: SHA256 hash of data\n\n" +
            "MODULE DETECTION:\n" +
            "  • FUNC: Contains 'def ' or 'function'\n" +
            "  • CLASS: Contains 'class '\n" +
            "  • IO: Contains 'import ' or 'require'\n" +
            "  • LOOP: Contains 'for ' or 'while '\n" +
            "  • RET: Contains 'return'\n\n" +
            "USAGE:\n" +
            "  qrdna Hello World\n" +
            "  qrdna def foo(): return 42\n";
    }
    
    private static String getInfinityHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  ∞ INFINITY STORAGE - Fractal DNA Memory System             ║\n" +
            "║  NO ONE HAS DONE THIS IN A JAVA REPL BEFORE                 ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "CONCEPT:\n" +
            "  Each piece contains the whole. Loss of any fragment\n" +
            "  doesn't destroy the system (like a hologram).\n\n" +
            "PERSISTENCE LAYERS:\n" +
            "  1. SQLite database (repl_storage.db)\n" +
            "     - storage: key-value with φ-hash\n" +
            "     - genesis_blocks: blockchain entries\n" +
            "     - learning_history: Q&A patterns\n" +
            "     - evolution: generation tracking\n" +
            "  2. JSON state files (learning_state.json)\n" +
            "     - Passive cycles, pattern strength\n" +
            "     - Knowledge integration level\n" +
            "     - Versioned copies with timestamps\n" +
            "  3. Binary .dat files (phi_patterns.dat)\n" +
            "     - 5×104 neural pattern tensor\n" +
            "     - φ-resonant initialization\n" +
            "  4. QR DNA encoding (qr_output/)\n" +
            "     - ASCII QR representation\n" +
            "     - OMEGA|GEN|PHI|RES|DIM|MOD|HASH format\n" +
            "  5. Genesis blockchain (genesis_block_*.json)\n" +
            "     - Immutable lineage tracking\n" +
            "     - φ^75 signed entries\n\n" +
            "MATH:\n" +
            "  • DNA signature: sin(φ×i) × cos(φ⁻¹×i)\n" +
            "  • Frequency bounds: 432-528 Hz (Fraymus)\n" +
            "  • Echo propagation: value × φ⁻¹\n" +
            "  • Evolution: patterns × (1 + fitness/φ)\n" +
            "  • Resonance: cosine similarity between vectors\n\n" +
            "SUBCOMMANDS:\n" +
            "  infinity init              → Initialize storage\n" +
            "  infinity store <key> <val> → Store with replication\n" +
            "  infinity get <key>         → Retrieve value\n" +
            "  infinity learn <q> | <a>   → Integrate into patterns\n" +
            "  infinity passive [n]       → Run n learning cycles\n" +
            "  infinity evolve [fitness]  → Evolve with score\n" +
            "  infinity qr <data>         → Generate QR DNA\n" +
            "  infinity genesis [data]    → Create blockchain entry\n" +
            "  infinity stats             → Show statistics\n" +
            "  infinity save              → Force save state\n";
    }
    
    private static String getUpperHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  UPPER - CONVERT TO UPPERCASE                               ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM:\n" +
            "  For each character c:\n" +
            "    if 'a' <= c <= 'z':\n" +
            "      c = c - 32  (ASCII offset)\n\n" +
            "JAVA IMPLEMENTATION:\n" +
            "  String.toUpperCase()\n\n" +
            "USAGE:\n" +
            "  upper hello world → HELLO WORLD\n" +
            "  upper φ-harmonic  → Φ-HARMONIC\n";
    }
    
    private static String getLowerHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  LOWER - CONVERT TO LOWERCASE                               ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM:\n" +
            "  For each character c:\n" +
            "    if 'A' <= c <= 'Z':\n" +
            "      c = c + 32  (ASCII offset)\n\n" +
            "JAVA IMPLEMENTATION:\n" +
            "  String.toLowerCase()\n\n" +
            "USAGE:\n" +
            "  lower HELLO WORLD → hello world\n" +
            "  lower PHI-HARMONIC → phi-harmonic\n";
    }
    
    private static String getReverseHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  REVERSE - REVERSE TEXT                                     ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM:\n" +
            "  For i from length-1 to 0:\n" +
            "    result += text[i]\n\n" +
            "JAVA IMPLEMENTATION:\n" +
            "  new StringBuilder(text).reverse().toString()\n\n" +
            "USAGE:\n" +
            "  reverse hello → olleh\n" +
            "  reverse 12345 → 54321\n";
    }
    
    private static String getCalcHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  CALC - CALCULATOR                                          ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "OPERATORS:\n" +
            "  +  Addition\n" +
            "  -  Subtraction\n" +
            "  *  Multiplication\n" +
            "  /  Division\n" +
            "  ^  Exponentiation (power)\n\n" +
            "USAGE:\n" +
            "  calc 5 + 3      → 8.000000\n" +
            "  calc 10 / 3     → 3.333333\n" +
            "  calc 2 ^ 10     → 1024.000000\n" +
            "  calc 1.618 ^ 75 → φ^75 ≈ 4.72e15\n";
    }
    
    private static String getPrimeHelp() {
        return 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  PRIME - PRIMALITY TEST                                     ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n\n" +
            "ALGORITHM (Trial Division):\n" +
            "  1. If n < 2: not prime\n" +
            "  2. If n == 2: prime\n" +
            "  3. If n % 2 == 0: not prime\n" +
            "  4. For i from 3 to √n, step 2:\n" +
            "       If n % i == 0: not prime\n" +
            "  5. Otherwise: prime!\n\n" +
            "COMPLEXITY: O(√n)\n\n" +
            "USAGE:\n" +
            "  prime 17    → 17 is prime!\n" +
            "  prime 100   → 100 is not prime (divisible by 2)\n" +
            "  prime 104729 → 104729 is prime! (10000th prime)\n";
    }
    
    // ============================================================
    // QR DNA ENCODER
    // ============================================================
    // Format: OMEGA|GEN:X|PHI:X.XXX|RES:X.XXX|DIM:X|HASH:XXX
    // This encodes data into a "DNA payload" that can be put in a QR code
    // ============================================================
    private static String encodeQRDNA(String data) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧬 QR DNA ENCODER                                         ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        try {
            // Calculate hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", hash[i]));
            }
            
            // Detect modules (like in qr_dna_encoder.py)
            List<String> modules = new ArrayList<>();
            String lower = data.toLowerCase();
            if (lower.contains("def ") || lower.contains("function")) modules.add("FUNC");
            if (lower.contains("class ")) modules.add("CLASS");
            if (lower.contains("import ") || lower.contains("require")) modules.add("IO");
            if (lower.contains("for ") || lower.contains("while ")) modules.add("LOOP");
            if (lower.contains("return")) modules.add("RET");
            if (modules.isEmpty()) modules.add("BASIC");
            
            String moduleStr = String.join("-", modules);
            int dimension = Math.min(11, 3 + modules.size());
            double resonance = PHI * (data.length() % 100) / 100.0;
            
            // Create DNA payload
            String dnaPayload = String.format(
                "OMEGA|GEN:1|PHI:%.10f|RES:%.4f|DIM:%d|MOD:%s|HASH:%s",
                PHI, resonance, dimension, moduleStr, hex.toString()
            );
            
            sb.append("Input: " + (data.length() > 50 ? data.substring(0, 50) + "..." : data) + "\n\n");
            sb.append("DNA PAYLOAD:\n");
            sb.append(dnaPayload + "\n\n");
            sb.append("DECODED:\n");
            sb.append(String.format("  Generation: 1\n"));
            sb.append(String.format("  φ (PHI): %.10f\n", PHI));
            sb.append(String.format("  Resonance: %.4f\n", resonance));
            sb.append(String.format("  Dimension: %d\n", dimension));
            sb.append(String.format("  Modules: %s\n", moduleStr));
            sb.append(String.format("  Hash: %s\n", hex.toString()));
            sb.append(String.format("\nConsciousness Level: %.4f\n", resonance * PHI));
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        
        return sb.toString();
    }
}
