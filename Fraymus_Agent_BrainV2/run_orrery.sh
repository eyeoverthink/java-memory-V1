#!/bin/bash
# Fraymus Orrery Launcher
# Requires Processing core.jar

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║  🌌 FRAYMUS ORRERY - UNIVERSE MODEL                        ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "\"The Orrery of Logic.\""
echo ""

# Check if Processing core.jar exists
if [ ! -f "lib/core.jar" ]; then
    echo "❌ ERROR: Processing core.jar not found!"
    echo ""
    echo "Please download Processing core library:"
    echo "  1. Visit: https://processing.org/download"
    echo "  2. Extract core.jar from Processing installation"
    echo "  3. Place in: lib/core.jar"
    echo ""
    echo "Or use Maven to download dependencies."
    echo ""
    exit 1
fi

echo "⚡ Compiling UniverseModel.java..."
javac -cp "lib/core.jar" -d out src/main/java/fraymus/sim/UniverseModel.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✅ Compilation successful!"
echo ""
echo "🚀 Launching Orrery..."
echo ""
java -cp "lib/core.jar:out" fraymus.sim.UniverseModel
