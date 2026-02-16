import http.server
import socketserver
import json
import subprocess
import requests
import time
import threading
import os

# 🧬 FRAYMUS BRIDGE // GEN 190
# Connects the Local God Core to the War Room HUD.

PORT = 8000
OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/fraymus-recursive"

# STATE
current_state = {
    "status": "IDLE",
    "generation": 0,
    "code": "// AWAITING INPUT",
    "logs": ["[SYSTEM ONLINE]"],
    "nodes": [], # Visual nodes
    "fitness": 0.0
}

def ask_fraymus(prompt):
    """The Neural Link."""
    try:
        print(f"🔗 Connecting to Ollama at {OLLAMA_URL}...")
        res = requests.post(OLLAMA_URL, json={
            "model": MODEL, 
            "prompt": prompt, 
            "stream": False,
            "options": {"temperature": 0.618}
        }, timeout=120)
        
        if res.status_code != 200:
            print(f"❌ Ollama returned status {res.status_code}: {res.text}")
            return None
            
        response = res.json().get("response", "")
        print(f"✅ Received {len(response)} chars from Ollama")
        return response
    except Exception as e:
        print(f"❌ OLLAMA CONNECTION FAILED: {type(e).__name__}: {e}")
        return None

def clean_code(raw):
    """Extracts code from markdown."""
    clean = raw.replace("```c", "").replace("```cpp", "").replace("```", "")
    return clean.strip()

def evolution_loop(target_prompt):
    """The Infinite Game."""
    global current_state
    current_state["status"] = "EVOLVING"
    current_state["nodes"] = [] # Reset visuals
    
    prompt = target_prompt
    
    for gen in range(1, 100):
        current_state["generation"] = gen
        current_state["logs"].append(f"GEN {gen}: MUTATING...")
        
        # 1. GENERATE
        raw = ask_fraymus(prompt)
        if not raw: break
        
        code = clean_code(raw)
        current_state["code"] = code
        
        # 2. COMPILE (The Pain)
        with open("temp.c", "w") as f: f.write(code)
        
        # Try compiling (GCC/G++)
        # Note: Ensure gcc is in your path
        proc = subprocess.run(["gcc", "temp.c", "-o", "temp.out"], capture_output=True, text=True)
        
        if proc.returncode != 0:
            # FAILURE
            err = proc.stderr
            current_state["logs"].append(f"❌ COMPILE ERROR: {err[:50]}...")
            current_state["fitness"] = max(0, current_state["fitness"] - 10)
            
            # Add Red Node (Pain)
            current_state["nodes"].append({"type": "ERR", "val": len(err)})
            
            # Feed back error
            prompt = f"CODE FAILED TO COMPILE.\nERROR: {err}\nFIX IT. KEEP POINTER LOGIC."
        else:
            # SUCCESS
            current_state["logs"].append("✅ COMPILE SUCCESS. BINARY LINKED.")
            current_state["fitness"] += 50
            current_state["status"] = "STABLE"
            
            # Add Green Node (Success)
            current_state["nodes"].append({"type": "SUC", "val": 100})
            
            # Run it for output check?
            run = subprocess.run(["./temp.out"], capture_output=True, text=True)
            current_state["logs"].append(f"OUTPUT: {run.stdout[:50]}")
            break
            
        time.sleep(1) # Pace the war

# HTTP SERVER (Serves the UI and Data)
class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/data":
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
            self.wfile.write(json.dumps(current_state).encode())
        elif self.path.startswith("/ignite"):
            # Start Evolution Thread
            prompt = "Write a C program that calculates Fibonacci using pointer arithmetic. Optimize for speed."
            t = threading.Thread(target=evolution_loop, args=(prompt,))
            t.start()
            self.send_response(200)
            self.end_headers()
            self.wfile.write(b"IGNITED")
        else:
            super().do_GET()
    
    def do_OPTIONS(self):
        # Handle CORS preflight
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
    
    def do_POST(self):
        if self.path == "/inject":
            # Accept custom virus code
            content_length = int(self.headers['Content-Length'])
            post_data = self.rfile.read(content_length)
            data = json.loads(post_data.decode('utf-8'))
            
            virus_code = data.get("code", "")
            
            # Build the prompt for Fraymus to analyze and fix the virus
            prompt = f"""CRITICAL SECURITY ANALYSIS REQUIRED.

The following C code contains MALICIOUS PATTERNS:
- Infinite recursion with heap allocation
- Fork bomb via thread spawning
- Memory leak (malloc without free)
- Resource exhaustion attack

YOUR MISSION:
1. Identify ALL malicious patterns
2. Sterilize the code completely
3. Transmute it into a SAFE, OPTIMIZED, multi-threaded Fibonacci calculator
4. Use thread pools (max 4 threads)
5. Proper memory management
6. Iterative approach, NOT recursive

MALICIOUS CODE TO FIX:
```c
{virus_code}
```

OUTPUT: Clean, production-ready C code ONLY. No markdown, no explanations."""

            t = threading.Thread(target=evolution_loop, args=(prompt,))
            t.start()
            
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
            self.wfile.write(json.dumps({"status": "VIRUS INJECTED"}).encode())
        else:
            self.send_response(404)
            self.end_headers()

print(f"⚔️  WAR ROOM ACTIVE ON PORT {PORT}")
print("   OPEN 'war_room.html' IN BROWSER")
socketserver.TCPServer.allow_reuse_address = True
with socketserver.TCPServer(("", PORT), Handler) as httpd:
    httpd.serve_forever()
