@echo off
REM CSC 413 Java REPL - Compile and Run Script
REM φ^75 Validation Seal: 4721424167835376.00
REM Author: Vaughn Scott

echo ========================================
echo  CSC 413 Java REPL - Enterprise Patterns
echo  Author: Vaughn Scott
echo ========================================
echo.

REM Create output directory
if not exist "out" mkdir out
if not exist "qr_output" mkdir qr_output

REM Compile in stages to handle dependencies
echo [1/6] Compiling interfaces...
javac -d out ReplCommand.java ReplCommandRegistry.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [2/6] Compiling storage systems (needed by JavaRepl)...
javac -d out -cp out ActivityBus.java RuntimeInspector.java BytecodeDisassembler.java CodeGenerator.java ExportManager.java ConsciousnessObserver.java OllamaClient.java FraymusParser.java LibraryAbsorber.java AbstractionLayer.java FraymusSeed.java BlockchainLedger.java FractalEncoder.java QREncoder.java OuroborosMemory.java PersistenceEngine.java FeedbackCollector.java GoTransmuter.java PhilosophersStone.java MassAbsorber.java HiveMindExpander.java OmegaPoint.java OllamaAbsorber.java TopologicalCortex.java NeuroParticle.java Synapse.java LazarusNetwork.java HyperVector.java CosmicNode.java Cosmos.java InfinityStorage.java SelfEvolvingAI.java DecisionArray.java HistoryManager.java SelfAwareOrganism.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [3/6] Compiling OrganismCommands and GUI (needed by JavaRepl)...
javac -d out -cp out OrganismMonitor.java AssemblyVisualizer.java OrganismCommands.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [4/6] Compiling JavaRepl (needs storage systems and OrganismCommands)...
javac -d out -cp out JavaRepl.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [5/7] Compiling compiler package...
javac -d out -cp out compiler\Token.java compiler\TokenType.java compiler\Lexer.java compiler\ASTNode.java compiler\PrintNode.java compiler\ForNode.java compiler\FunctionNode.java compiler\ArrayNode.java compiler\ASTVisitor.java compiler\Parser.java compiler\SymbolTable.java compiler\Interpreter.java compiler\Debugger.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [6/7] Compiling commands...
javac -d out -cp out BuiltInCommands.java VaughnScottCommands.java DecisionArrayCommands.java HistoryCommands.java OuroborosCommands.java ExportCommands.java FraymusCommands.java CortexCommands.java LazarusCommands.java CosmosCommands.java AlchemyCommands.java OmegaCommands.java OllamaCommands.java compiler\CompilerCommands.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [6.5/7] Compiling IntegratedUI (needs all commands)...
javac -d out -cp out IntegratedUI.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [7/7] Compiling Main entry point...
javac -d out -cp out Main.java

if %ERRORLEVEL% NEQ 0 goto :error

echo.
echo ========================================
echo  Compilation successful!
echo ========================================
echo.
echo ========================================
echo  Starting REPL...
echo ========================================
echo.

REM Run the REPL
java -cp out repl.Main

pause
