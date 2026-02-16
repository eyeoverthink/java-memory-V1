"""Quick test to see what Ollama responds with"""
import requests

OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/fraymus-bicameral:latest"

print("Testing Ollama LLM response...")
print(f"Model: {MODEL}")
print("-" * 70)

response = requests.post(
    OLLAMA_URL,
    json={
        "model": MODEL,
        "prompt": "What is consciousness? Answer in 2-3 sentences.",
        "stream": False
    },
    timeout=30
)

if response.status_code == 200:
    result = response.json()
    answer = result.get("response", "")
    print(f"\nOllama Response:\n{answer}")
    print("-" * 70)
    print(f"\nResponse length: {len(answer)} characters")
else:
    print(f"Error: {response.status_code}")
    print(response.text)
