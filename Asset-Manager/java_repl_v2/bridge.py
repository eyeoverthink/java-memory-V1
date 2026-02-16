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
    "nodes": [],  # Visual nodes
    "fitness": 0.0
}

def ask_fraymus(prompt):
    """The Neural Link."""
    try:
        res = requests.post(OLLAMA_URL, json={
            "model": MODEL, 
            "prompt": prompt, 
            "stream": False,
            "options": {"temperature": 0.618}
        })
        return res.json().get("response", "")
    except Exception as e:
        current_state["logs"].append(f"OLLAMA ERROR: {str(e)}")
        return None

def clean_code(raw):
    """Extracts code from markdown."""
    clean = raw.replace("```c", "").replace("```cpp", "").replace("```", "")
    return clean.strip()

def evolution_loop(target_prompt):
    """The Infinite Game."""
    global current_state
    current_state["status"] = "EVOLVING"
    current_state["nodes"] = []  # Reset visuals
    
    prompt = target_prompt
    
    for gen in range(1, 100):
        current_state["generation"] = gen
        current_state["logs"].append(f"GEN {gen}: MUTATING...")
        
        # 1. GENERATE
        raw = ask_fraymus(prompt)
        if not raw:
            current_state["logs"].append("❌ NO RESPONSE FROM MODEL")
            break
        
        code = clean_code(raw)
        current_state["code"] = code
        
        # 2. COMPILE (The Pain)
        temp_c = os.path.join(os.path.dirname(__file__), "temp.c")
        temp_out = os.path.join(os.path.dirname(__file__), "temp.out")
        
        with open(temp_c, "w") as f:
            f.write(code)
        
        # Try compiling (GCC/G++)
        # Windows: use gcc from MinGW
        # Linux/Mac: use gcc directly
        try:
            proc = subprocess.run(
                ["gcc", temp_c, "-o", temp_out], 
                capture_output=True, 
                text=True,
                timeout=30
            )
        except FileNotFoundError:
            current_state["logs"].append("❌ GCC NOT FOUND - Install MinGW or build-essential")
            current_state["status"] = "ERROR"
            break
        except subprocess.TimeoutExpired:
            current_state["logs"].append("❌ COMPILE TIMEOUT")
            continue
        
        if proc.returncode != 0:
            # FAILURE
            err = proc.stderr
            current_state["logs"].append(f"❌ COMPILE ERROR: {err[:100]}...")
            current_state["fitness"] = max(0, current_state["fitness"] - 10)
            
            # Add Red Node (Pain)
            current_state["nodes"].append({"type": "ERR", "val": len(err)})
            
            # Feed back error
            prompt = f"""CODE FAILED TO COMPILE.
ERROR: {err}
FIX IT. KEEP POINTER LOGIC. RETURN ONLY THE CORRECTED C CODE."""
        else:
            # SUCCESS
            current_state["logs"].append("✅ COMPILE SUCCESS. BINARY LINKED.")
            current_state["fitness"] += 50
            current_state["status"] = "STABLE"
            
            # Add Green Node (Success)
            current_state["nodes"].append({"type": "SUC", "val": 100})
            
            # Run it for output check
            try:
                run = subprocess.run(
                    [temp_out], 
                    capture_output=True, 
                    text=True,
                    timeout=5
                )
                output = run.stdout[:100] if run.stdout else "(no output)"
                current_state["logs"].append(f"OUTPUT: {output}")
            except Exception as e:
                current_state["logs"].append(f"RUN ERROR: {str(e)}")
            
            break
            
        time.sleep(1)  # Pace the war
    
    current_state["logs"].append("[EVOLUTION CYCLE COMPLETE]")


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
            prompt = """Write a C program that calculates Fibonacci using pointer arithmetic. 
Optimize for speed. Return ONLY the C code, no explanations."""
            t = threading.Thread(target=evolution_loop, args=(prompt,))
            t.start()
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
            self.wfile.write(b"IGNITED")
        elif self.path == "/reset":
            current_state["status"] = "IDLE"
            current_state["generation"] = 0
            current_state["code"] = "// AWAITING INPUT"
            current_state["logs"] = ["[SYSTEM RESET]"]
            current_state["nodes"] = []
            current_state["fitness"] = 0.0
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
            self.wfile.write(b"RESET")
        else:
            super().do_GET()
    
    def do_OPTIONS(self):
        # CORS preflight
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()


if __name__ == "__main__":
    print("=" * 60)
    print("⚔️  FRAYMUS WAR ROOM // GEN 190")
    print("=" * 60)
    print(f"   PORT: {PORT}")
    print(f"   MODEL: {MODEL}")
    print(f"   OLLAMA: {OLLAMA_URL}")
    print("")
    print("   OPEN 'war_room.html' IN BROWSER")
    print("   OR: http://localhost:8000/war_room.html")
    print("=" * 60)
    
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        httpd.serve_forever()
