"""Test Ollama with streaming API"""
import requests
import json

OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/Fraymus:latest"

print(f"Testing Ollama with streaming API...")
print(f"Model: {MODEL}")
print("-" * 70)

response = requests.post(
    OLLAMA_URL,
    json={
        "model": MODEL,
        "prompt": "What is consciousness? Answer in 2-3 sentences.",
        "stream": True
    },
    stream=True,
    timeout=60
)

print("\nOllama Response:")
full_response = ""

for line in response.iter_lines():
    if line:
        try:
            data = json.loads(line)
            if "response" in data:
                chunk = data["response"]
                print(chunk, end="", flush=True)
                full_response += chunk
        except json.JSONDecodeError:
            pass

print("\n" + "-" * 70)
print(f"Total response length: {len(full_response)} characters")
