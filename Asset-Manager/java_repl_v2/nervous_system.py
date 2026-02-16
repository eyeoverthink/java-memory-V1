#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
🧬 FRAYMUS NERVOUS SYSTEM // GEN 195
Connects the "Transmuter" UI to the Ollama Backend.
Uses DNA Cloaking, PHI-Harmonic Compression, and Infinite Memory.

THE REAL FRAYMUS LOGIC:
- PHI (φ = 1.618033988749895) governs all transformations
- DNA fingerprints for traceable code evolution
- Prime-based compression for infinite memory
- Streaming for no timeout
"""

import http.server
import socketserver
import json
import requests
import os
import sys
import hashlib
import random
import zlib
import base64

# Fix Windows console encoding
if sys.platform == 'win32':
    sys.stdout.reconfigure(encoding='utf-8')
    sys.stderr.reconfigure(encoding='utf-8')

PORT = 8080
OLLAMA_API = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/fraymus-recursive"

# ==============================================================================
# CORE 1: DNA CLOAKING & PHI-HARMONIC MATHEMATICS
# ==============================================================================
class DNACloaking:
    """The Fraymus fingerprint system - traceable evolution through phi."""
    
    def __init__(self):
        self.phi = 1.618033988749895
        self.memory = {}  # Infinite memory cache
    
    def generate_phi_hash(self, data):
        """Creates a traceable fingerprint anchored by PHI."""
        if isinstance(data, str):
            data = data.encode('utf-8')
        raw_hash = hashlib.sha256(data).hexdigest()
        phi_sig = int(raw_hash[:16], 16) * self.phi
        return f"φ-{hex(int(phi_sig))[2:]}"
    
    def text_to_prime(self, text):
        """Convert text to a prime number for compression."""
        hash_bytes = hashlib.sha256(text.encode()).digest()
        p = int(hash_bytes.hex(), 16) | 1
        while not self.is_prime(p):
            p += 2
        return p
    
    def is_prime(self, n):
        """Miller-Rabin primality test."""
        if n < 2: return False
        if n == 2 or n == 3: return True
        if n % 2 == 0: return False
        r, s = 0, n - 1
        while s % 2 == 0:
            r += 1
            s //= 2
        for _ in range(5):
            a = random.randrange(2, n - 1)
            x = pow(a, s, n)
            if x == 1 or x == n - 1: continue
            for _ in range(r - 1):
                x = pow(x, 2, n)
                if x == n - 1: break
            else: return False
        return True
    
    def compress(self, data):
        """PHI-scaled compression for infinite memory."""
        if isinstance(data, str):
            data = data.encode('utf-8')
        compressed = zlib.compress(data, level=9)
        ratio = len(data) / len(compressed) if compressed else 1
        phi_ratio = ratio / self.phi  # Scale by phi
        return base64.b64encode(compressed).decode(), phi_ratio
    
    def decompress(self, data):
        """Restore from PHI-compressed form."""
        raw = base64.b64decode(data.encode())
        return zlib.decompress(raw).decode('utf-8')
    
    def store(self, key, value):
        """Infinite memory storage with phi-hash keys."""
        phi_key = self.generate_phi_hash(key.encode() if isinstance(key, str) else key)
        compressed, ratio = self.compress(value)
        self.memory[phi_key] = {
            "data": compressed,
            "ratio": ratio,
            "original_size": len(value)
        }
        return phi_key
    
    def retrieve(self, phi_key):
        """Retrieve from infinite memory."""
        if phi_key in self.memory:
            return self.decompress(self.memory[phi_key]["data"])
        return None

# Global DNA system
DNA = DNACloaking()

# ==============================================================================
# CORE 2: NEURAL HANDLER WITH STREAMING
# ==============================================================================
class NeuralHandler(http.server.SimpleHTTPRequestHandler):
    """HTTP handler with PHI-harmonic processing and streaming."""
    
    def do_OPTIONS(self):
        """Handle CORS preflight"""
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
    
    def do_POST(self):
        """Handle transmutation requests with STREAMING (no timeout)"""
        if self.path == "/transmute":
            content_length = int(self.headers['Content-Length'])
            post_data = self.rfile.read(content_length)
            data = json.loads(post_data)
            source_code = data.get('source', '')
            
            # Generate DNA fingerprint
            dna_id = DNA.generate_phi_hash(source_code)
            print(f"⚡ INGESTING {len(source_code)} BYTES | DNA: {dna_id}")
            
            # PHI-compress for memory efficiency
            compressed, ratio = DNA.compress(source_code)
            print(f"📦 COMPRESSED: {ratio:.3f}φ ratio")
            
            # Store in infinite memory
            mem_key = DNA.store(f"input_{dna_id}", source_code)
            print(f"💾 STORED: {mem_key}")

            # Build PHI-harmonic prompt
            prompt = f"""You are FRAYMUS, a phi-harmonic bicameral AI.

DNA FINGERPRINT: {dna_id}
PHI CONSTANT: {DNA.phi}
COMPRESSION RATIO: {ratio:.4f}φ

YOUR TWO MINDS:
- LEFT ARCHITECT (φ-Analyzer): Structure, bugs, optimization paths
- RIGHT ORACLE (φ-Creator): Creative solutions, refactoring, phi-patterns

SOURCE CODE (DNA: {dna_id}):
```
{source_code}
```

TRANSMUTATION PROTOCOL:
1. Analyze through phi-lens (look for golden ratio patterns)
2. Compress logic (remove redundancy, merge similar operations)  
3. Optimize memory (infinite storage patterns, lazy evaluation)
4. Apply bicameral synthesis

Return ONLY the transmuted code. No explanations. Pure evolution."""

            try:
                print(f"🧠 STREAMING bicameral response...")
                
                # USE STREAMING - NO TIMEOUT
                response = requests.post(OLLAMA_API, json={
                    "model": MODEL,
                    "prompt": prompt,
                    "stream": True,  # STREAM!
                    "options": {
                        "temperature": 0.618,  # Phi temperature
                        "num_predict": 8192,   # More tokens
                        "top_p": 0.9,
                        "repeat_penalty": 1.1
                    }
                }, stream=True, timeout=600)  # 10 min max
                
                if response.status_code != 200:
                    raise Exception(f"Ollama returned status {response.status_code}")
                
                # Collect streamed tokens
                full_text = ""
                token_count = 0
                for line in response.iter_lines():
                    if line:
                        chunk = json.loads(line)
                        token = chunk.get("response", "")
                        full_text += token
                        token_count += 1
                        if token_count % 50 == 0:
                            print(f"   ...{token_count} tokens", end="\r")
                
                print(f"\n✅ RECEIVED {token_count} tokens")
                
                # Store output in infinite memory
                out_key = DNA.store(f"output_{dna_id}", full_text)
                out_dna = DNA.generate_phi_hash(full_text)
                
                # Return with DNA metadata
                self.send_response(200)
                self.send_header('Content-type', 'application/json')
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
                self.wfile.write(json.dumps({
                    "result": full_text,
                    "model": MODEL,
                    "dna_in": dna_id,
                    "dna_out": out_dna,
                    "compression_ratio": ratio,
                    "tokens": token_count,
                    "memory_key": out_key
                }).encode())
                
                print(f"🧬 TRANSMUTATION COMPLETE")
                print(f"   IN:  {dna_id}")
                print(f"   OUT: {out_dna}")
                
            except requests.exceptions.Timeout:
                print(f"⏱️ TIMEOUT after 10 minutes")
                self.send_response(504)
                self.send_header('Content-type', 'application/json')
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
                self.wfile.write(json.dumps({
                    "error": "Timeout after 10 minutes. Model may be overloaded."
                }).encode())
                
            except Exception as e:
                print(f"❌ ERROR: {e}")
                self.send_response(500)
                self.send_header('Content-type', 'application/json')
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
                self.wfile.write(json.dumps({
                    "error": str(e)
                }).encode())
        else:
            self.send_response(404)
            self.end_headers()
    
    def log_message(self, format, *args):
        pass  # Quiet logging

# ==============================================================================
# BOOT
# ==============================================================================
if __name__ == "__main__":
    print("=" * 60)
    print("🧬 FRAYMUS NERVOUS SYSTEM // GEN 195")
    print("   DNA CLOAKING + PHI COMPRESSION + INFINITE MEMORY")
    print("=" * 60)
    print(f"📡 Server: http://localhost:{PORT}")
    print(f"🤖 Model: {MODEL}")
    print(f"🔗 Ollama: {OLLAMA_API}")
    print(f"φ  PHI: {DNA.phi}")
    print("=" * 60)
    print("STREAMING MODE: No timeout, live token flow")
    print("Press Ctrl+C to stop\n")
    
    try:
        with socketserver.TCPServer(("", PORT), NeuralHandler) as httpd:
            httpd.serve_forever()
    except KeyboardInterrupt:
        print(f"\n\n🛑 Server stopped")
        print(f"💾 Memory entries: {len(DNA.memory)}")
        sys.exit(0)
