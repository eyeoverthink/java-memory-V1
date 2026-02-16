#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
FRAYMUS SELF-IMPROVEMENT ENGINE
The system analyzes and fixes its own codebase using Ollama.
"""

import requests
import subprocess
import os
import json
import sys
from pathlib import Path

# Fix Windows console encoding
if sys.platform == 'win32':
    sys.stdout.reconfigure(encoding='utf-8')
    sys.stderr.reconfigure(encoding='utf-8')

OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/fraymus-recursive"
SRC_DIR = Path("src/main/java")

def ask_fraymus(prompt, temperature=0.618):
    """Query Ollama for code analysis/generation."""
    try:
        print(f"🔗 Asking Fraymus...")
        res = requests.post(OLLAMA_URL, json={
            "model": MODEL,
            "prompt": prompt,
            "stream": False,
            "options": {"temperature": temperature, "num_predict": 4096}
        }, timeout=180)
        
        if res.status_code == 200:
            response = res.json().get("response", "")
            print(f"✅ Received {len(response)} chars")
            return response
        else:
            print(f"❌ Ollama error: {res.status_code}")
            return None
    except Exception as e:
        print(f"❌ Connection failed: {e}")
        return None

def analyze_file(filepath):
    """Have Fraymus analyze a Java file for issues."""
    with open(filepath, 'r', encoding='utf-8') as f:
        code = f.read()
    
    prompt = f"""Analyze this Java file for bugs, optimizations, or incomplete code:

FILE: {filepath}

```java
{code[:2000]}  # First 2000 chars
```

Provide:
1. Issues found (bugs, incomplete methods, syntax errors)
2. Optimization opportunities
3. Suggested fixes

Be concise. Focus on critical issues."""

    return ask_fraymus(prompt)

def fix_file(filepath, issue_description):
    """Have Fraymus generate a fix for a specific issue."""
    with open(filepath, 'r', encoding='utf-8') as f:
        code = f.read()
    
    prompt = f"""Fix this Java file:

FILE: {filepath}
ISSUE: {issue_description}

CURRENT CODE:
```java
{code[:3000]}
```

Provide ONLY the fixed code section. No explanations."""

    return ask_fraymus(prompt, temperature=0.3)  # Lower temp for precise fixes

def compile_project():
    """Attempt to compile the project."""
    print("🔨 Compiling project...")
    try:
        result = subprocess.run(
            ["gradle", "compileJava"],
            capture_output=True,
            text=True,
            timeout=60
        )
        
        if result.returncode == 0:
            print("✅ Compilation successful")
            return True, ""
        else:
            print("❌ Compilation failed")
            return False, result.stderr
    except Exception as e:
        print(f"❌ Compile error: {e}")
        return False, str(e)

def main():
    print("🧬 FRAYMUS SELF-IMPROVEMENT ENGINE")
    print("=" * 60)
    
    # Step 1: Try to compile
    success, errors = compile_project()
    
    if success:
        print("\n✅ System compiles successfully!")
        print("🔍 Scanning for optimization opportunities...")
        
        # Analyze key files for optimizations
        key_files = [
            "src/main/java/fraymus/llm/LlamaEngine.java",
            "src/main/java/fraymus/llm/NativeBridge.java",
            "src/main/java/fraymus/core/OllamaBridge.java"
        ]
        
        for filepath in key_files:
            if os.path.exists(filepath):
                print(f"\n📄 Analyzing: {filepath}")
                analysis = analyze_file(filepath)
                if analysis:
                    print(f"\n{analysis[:500]}...")  # Show first 500 chars
    else:
        print("\n❌ Compilation errors detected:")
        print(errors[:1000])  # Show first 1000 chars of errors
        
        # Ask Fraymus to analyze the errors
        print("\n🧠 Asking Fraymus to diagnose...")
        prompt = f"""The Fraymus Java project has compilation errors:

```
{errors[:2000]}
```

Identify:
1. The root cause
2. Which files need fixing
3. Suggested fix approach

Be concise."""
        
        diagnosis = ask_fraymus(prompt)
        if diagnosis:
            print(f"\n🔬 DIAGNOSIS:\n{diagnosis}")
            
            # Save diagnosis
            with open("DIAGNOSIS.md", "w") as f:
                f.write(f"# Fraymus Self-Diagnosis\n\n")
                f.write(f"## Compilation Errors\n```\n{errors}\n```\n\n")
                f.write(f"## Analysis\n{diagnosis}\n")
            
            print("\n💾 Diagnosis saved to DIAGNOSIS.md")

if __name__ == "__main__":
    main()
