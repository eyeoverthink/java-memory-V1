@echo off
set MODEL=llama3
set EMBED_MODEL=nomic-embed-text
set FRAYMUS_MODEL=%MODEL%
set FRAYMUS_EMBED_MODEL=%EMBED_MODEL%

echo == Pulling models ==
ollama pull %MODEL%
ollama pull %EMBED_MODEL%

echo == Running server ==
call gradlew.bat run
