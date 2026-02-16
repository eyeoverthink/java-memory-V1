# FRAYMUS UNIVERSAL INTERFACE - Makefile
# Build + Run + Index + Open UI

SHELL := /bin/bash
PROJECT := fraymus

ifeq ($(OS),Windows_NT)
  GRADLEW := gradlew.bat
  OPEN_CMD := start
else
  GRADLEW := ./gradlew
  UNAME_S := $(shell uname -s)
  ifeq ($(UNAME_S),Darwin)
    OPEN_CMD := open
  else
    OPEN_CMD := xdg-open
  endif
endif

.PHONY: help setup build run ui clean index reindex doctor pull

help:
	@echo "Targets:"
	@echo "  make setup                 # create folders"
	@echo "  make build                 # compile"
	@echo "  make run                   # run websocket brain (ws://localhost:8887)"
	@echo "  make ui                    # open web/FraymusChat.html"
	@echo "  make index path=./docs     # index files into vault/"
	@echo "  make reindex path=./docs   # wipe vault and reindex"
	@echo "  make clean                 # clean build"
	@echo "  make pull                  # pull Ollama models"

doctor:
	@command -v ollama >/dev/null 2>&1 || (echo "Ollama not found in PATH" && exit 1)
	@java -version

pull:
	ollama pull llama3
	ollama pull nomic-embed-text

setup:
	@mkdir -p memory vault generated web

build:
	$(GRADLEW) clean build

run: setup
	$(GRADLEW) run

ui:
	$(OPEN_CMD) web/FraymusChat.html

index: setup
	$(GRADLEW) run --args="--index $(path)"

reindex: setup
	@rm -rf vault/*
	$(GRADLEW) run --args="--index $(path)"

clean:
	$(GRADLEW) clean
