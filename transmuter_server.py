#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
🧬 FRAYMUS NERVOUS SYSTEM // GEN 194
Connects the "Transmuter" UI to the Ollama Backend.
Neural Bridge between HTML interface and Fraymus Bicameral model.
"""

import http.server
import socketserver
import json
import requests
import os
import sys

# Fix Windows console encoding
if sys.platform == 'win32':
    sys.stdout.reconfigure(encoding='utf-8')
    sys.stderr.reconfigure(encoding='utf-8')

PORT = 8080
OLLAMA_API = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/fraymus-recursive"  # Using your actual model

class NeuralHandler(http.server.SimpleHTTPRequestHandler):
    """HTTP handler that serves files and processes transmutation requests"""
    
    def do_OPTIONS(self):
        """Handle CORS preflight"""
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'POST, GET, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
    
    def do_POST(self):
        """Handle transmutation requests"""
        if self.path == "/transmute":
            # Enable CORS
            self.send_header('Access-Control-Allow-Origin', '*')
            
            # 1. INGEST RAW SOURCE
            content_length = int(self.headers['Content-Length'])
            post_data = self.rfile.read(content_length)
            data = json.loads(post_data)
            source_code = data.get('source', '')

            print(f"⚡ INGESTING {len(source_code)} BYTES...")

            # 2. TRIGGER THE BICAMERAL MIND
            # We ask the Architect (Blue) and Oracle (Red) to transmute it.
            prompt = (
                f"You are Fraymus, a bicameral AI with two minds:\n"
                f"- LEFT ARCHITECT (Blue): Analyzes structure, finds bugs, plans optimization\n"
                f"- RIGHT ORACLE (Red): Implements creative solutions, refactors code\n\n"
                f"SOURCE CODE TO OPTIMIZE:\n```\n{source_code}\n```\n\n"
                f"DIRECTIVE: Transmute this code through bicameral processing.\n"
                f"1. LEFT BRAIN: Identify bugs, inefficiencies, and structural issues\n"
                f"2. RIGHT BRAIN: Apply creative refactoring and optimization\n"
                f"3. Return ONLY the fully optimized code block, no explanations.\n"
            )

            try:
                # Call Ollama
                print(f"🧠 Activating bicameral processing...")
                response = requests.post(OLLAMA_API, json={
                    "model": MODEL,
                    "prompt": prompt,
                    "stream": False,
                    "options": {
                        "temperature": 0.618,  # Phi-harmonic temperature
                        "num_predict": 4096
                    }
                }, timeout=180)
                
                if response.status_code != 200:
                    raise Exception(f"Ollama returned status {response.status_code}")
                
                # Extract Response
                full_text = response.json().get("response", "")
                
                # 3. RETURN EVOLVED CODE
                self.send_response(200)
                self.send_header('Content-type', 'application/json')
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
                self.wfile.write(json.dumps({
                    "result": full_text,
                    "model": MODEL,
                    "bytes_processed": len(source_code),
                    "bytes_generated": len(full_text)
                }).encode())
                
                print(f"✅ TRANSMUTATION COMPLETE: {len(full_text)} bytes generated")
                
            except requests.exceptions.Timeout:
                print(f"⏱️ TIMEOUT: Ollama took too long to respond")
                self.send_response(504)
                self.send_header('Content-type', 'application/json')
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
                self.wfile.write(json.dumps({
                    "error": "Timeout: Ollama processing took too long (>180s)"
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
            # Unknown endpoint
            self.send_response(404)
            self.end_headers()
    
    def log_message(self, format, *args):
        """Custom logging with colors"""
        print(f"🔗 {args[0]} - {args[1]}")

# START SERVER
if __name__ == "__main__":
    print("=" * 60)
    print("🧬 FRAYMUS TRANSMUTER NEURAL BRIDGE")
    print("=" * 60)
    print(f"📡 Server: http://localhost:{PORT}")
    print(f"🤖 Model: {MODEL}")
    print(f"🔗 Ollama: {OLLAMA_API}")
    print("=" * 60)
    print("Ready to transmute code through bicameral processing...")
    print("Press Ctrl+C to stop\n")
    
    try:
        with socketserver.TCPServer(("", PORT), NeuralHandler) as httpd:
            httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n\n🛑 Server stopped by user")
        sys.exit(0)
