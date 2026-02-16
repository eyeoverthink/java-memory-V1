@echo off
echo ========================================
echo TESTING EVOLUTION COMMANDS IN FRAYMUS
echo ========================================
echo.

echo Test 1: Evolve XOR gate
echo Command: evolve xor
echo.
java -cp build/classes/java/main fraymus.CommandProcessor "evolve xor"
echo.
echo ========================================
echo.

echo Test 2: Show library
echo Command: library show
echo.
java -cp build/classes/java/main fraymus.CommandProcessor "library show"
echo.
echo ========================================
echo.

echo Test 3: Generate living code
echo Command: generate java "test circuit"
echo.
java -cp build/classes/java/main fraymus.CommandProcessor "generate java test circuit"
echo.
echo ========================================
echo TESTS COMPLETE
echo ========================================
