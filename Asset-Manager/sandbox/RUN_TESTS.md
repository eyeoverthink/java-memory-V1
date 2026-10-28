# How to Run Tests

## Option 1: Using Gradle (Recommended)

### Prerequisites
- Java 17 or higher installed
- Gradle installed (or use the parent project's Gradle)

### From the sandbox directory:

```bash
cd F:\Full-AI-System\Java-Memory\Asset-Manager\sandbox
```

### Run all tests:
```bash
gradle test
```

Or if using the parent project's Gradle wrapper:
```bash
cd F:\Full-AI-System\Java-Memory\Asset-Manager
gradlew :sandbox:test
```

### Run specific test class:
```bash
gradle test --tests QueryAnalyzerTest
gradle test --tests BasicRouterTest
gradle test --tests EvolutionEngineTest
gradle test --tests ResultSynthesizerTest
gradle test --tests BackendDiscoveryTest
gradle test --tests SelfHealingTest
gradle test --tests OrganicGrowthTest
```

### Run tests with detailed output:
```bash
gradle test --info
```

---

## Option 2: Using IDE (IntelliJ IDEA / Eclipse)

### IntelliJ IDEA:
1. Open the sandbox directory as a module
2. Right-click on `src/test/java/sandbox/tests/`
3. Select "Run All Tests"
4. Or right-click on individual test classes and select "Run"

### Eclipse:
1. Import the sandbox directory as a Gradle project
2. Right-click on test classes
3. Run as JUnit Test

---

## Option 3: Manual Compilation and Execution

### Compile:
```bash
cd F:\Full-AI-System\Java-Memory\Asset-Manager\sandbox
javac -d build/classes -cp "lib/*" src/main/java/sandbox/**/*.java src/test/java/sandbox/tests/**/*.java
```

### Run tests with JUnit:
```bash
java -cp "build/classes:lib/*" org.junit.platform.console.ConsoleLauncher --scan-class-path
```

---

## Expected Results

All 47 tests should pass:
- QueryAnalyzerTest: 10 tests
- BasicRouterTest: 9 tests
- EvolutionEngineTest: 8 tests
- ResultSynthesizerTest: 5 tests
- BackendDiscoveryTest: 9 tests
- SelfHealingTest: 12 tests
- OrganicGrowthTest: 3 tests

---

## Troubleshooting

### If Gradle is not found:
```bash
# Install Gradle or use the parent project's wrapper
cd F:\Full-AI-System\Java-Memory\Asset-Manager
gradlew :sandbox:test
```

### If Java version is wrong:
```bash
# Check Java version
java -version

# Set JAVA_HOME if needed
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

### If dependencies are missing:
```bash
gradle dependencies
```

---

## Test Output Location

Test results will be in:
```
sandbox/build/reports/tests/test/index.html
```

Open this file in a browser to see detailed test results.
