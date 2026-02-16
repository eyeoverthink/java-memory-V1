# FRAYMUS UNIVERSAL INTERFACE - Makefile
# Build + Run + Index + Open UI

SHELL := /bin/bash
PROJECT := fraymus

ifeq ($(OS),Windows_NT)
  GRADLEW := gradlew.bat
  OPEN_CMD := start
  MKDIR := mkdir
  RM := rmdir /s /q
else
  GRADLEW := ./gradlew
  UNAME_S := $(shell uname -s)
  ifeq ($(UNAME_S),Darwin)
    OPEN_CMD := open
  else
    OPEN_CMD := xdg-open
  endif
  MKDIR := mkdir -p
  RM := rm -rf
endif

.PHONY: help setup build run ui clean index reindex

help:
	@echo "Targets:"
	@echo "  make setup                 # create folders"
	@echo "  make build                 # compile"
	@echo "  make run                   # run websocket brain (ws://localhost:8887)"
	@echo "  make ui                    # open web/FraymusChat.html"
	@echo "  make index path=./docs     # index files into vault/"
	@echo "  make reindex path=./docs   # wipe vault and reindex"
	@echo "  make clean                 # clean build"

setup:
	@$(MKDIR) memory vault generated web

build:
	$(GRADLEW) clean build

run: setup
	$(GRADLEW) runUniversal

ui:
	$(OPEN_CMD) FraymusChat.html

index: setup
	$(GRADLEW) runUniversal --args="--index $(path)"

reindex: setup
	@$(RM) vault
	@$(MKDIR) vault
	$(GRADLEW) runUniversal --args="--index $(path)"

clean:
	$(GRADLEW) clean
