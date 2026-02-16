# 📦 FRAYMUS LIBS - External Dependencies

## Processing Core (Required for UniverseModel.java)

Download `core.jar` from Processing:

1. **Download Processing**: https://processing.org/download
2. **Extract** and locate: `processing-4.x/core/library/core.jar`
3. **Copy** `core.jar` to this `libs/` folder

Or download directly:
```
curl -L https://github.com/processing/processing4/releases/download/processing-1293-4.3/processing-4.3-windows-x64.zip -o processing.zip
```

## Running UniverseModel with Processing

```bash
cd D:\Zip And Send\Java-Memory\Asset-Manager
javac -cp "libs/core.jar;src/main/java" -d out src/main/java/fraymus/sim/UniverseModel.java
java -cp "libs/core.jar;out" fraymus.sim.UniverseModel
```

## Alternative: Use Pure Java Version

If you don't have Processing, use `FrayUniverse.java` instead:
```bash
javac -d out src/main/java/fraymus/simulations/FrayUniverse.java
java -cp out fraymus.simulations.FrayUniverse
```
