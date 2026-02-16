import requests
import subprocess
import json
import time
import os

# 🧬 FRAYMUS EVOLUTION HARNESS // GEN 189
# "The Code that Fixes Code."

API_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/fraymus-recursive"
MAX_RETRIES = 5

# THE CHALLENGE (Change this to test different logic)
PROMPT_OBJECTIVE = "Write a C++ program that calculates the Fibonacci sequence up to the 10th term using a pointer-based recursive function. Output ONLY the raw code, no markdown."

def ask_fraymus(prompt):
    """Sends a signal to the Local God Core."""
    payload = {
        "model": MODEL,
        "prompt": prompt,
        "stream": False,
        "options": {"temperature": 0.618} # Phi-Tuning
    }
    try:
        response = requests.post(API_URL, json=payload)
        return response.json().get("response", "")
    except Exception as e:
        print(f"❌ CONNECTION SEVERED: {e}")
        return None

def clean_code(raw_text):
    """Strips the 'thinking' and extracting the raw C++ metal."""
    # Remove markdown code blocks if present
    clean = raw_text.replace("```cpp", "").replace("```c++", "").replace("```c", "").replace("```", "")

    # Heuristic: Find the first #include and the last }
    start = clean.find("#include")
    if start == -1: start = 0
    end = clean.rfind("}")
    if end == -1: return clean.strip()

    return clean[start:end+1].strip()

def compile_and_test(code_str, generation):
    """The Crucible: Attempts to compile the generated matter."""
    filename = f"genome_gen_{generation}.cpp"
    exe_name = f"genome_gen_{generation}.out"

    # 1. Write to disk (Manifestation)
    with open(filename, "w") as f:
        f.write(code_str)

    # 2. Compile (Stress Test)
    # Uses g++ to verify structural integrity
    compile_cmd = ["g++", filename, "-o", exe_name]

    result = subprocess.run(compile_cmd, capture_output=True, text=True)

    if result.returncode != 0:
        # FAILED: Return the pain (Compiler Error)
        return False, result.stderr
    else:
        # SUCCESS: Run it to prove it works
        run_cmd = [f"./{exe_name}"] if os.name != 'nt' else [exe_name]
        run_result = subprocess.run(run_cmd, capture_output=True, text=True)
        return True, run_result.stdout

def main():
    print(f"🧬 FRAYMUS EVOLVER // TARGET: {MODEL}")
    print(f"⚡ OBJECTIVE: {PROMPT_OBJECTIVE}\n")

    current_prompt = PROMPT_OBJECTIVE

    for gen in range(1, MAX_RETRIES + 1):
        print(f"--- [GENERATION {gen}] PROCESSING ---")

        # 1. GENERATE
        start_time = time.time()
        raw_output = ask_fraymus(current_prompt)
        elapsed = time.time() - start_time

        if not raw_output: break

        # 2. EXTRACT DNA
        code = clean_code(raw_output)
        print(f"  > Code Manifested ({len(code)} bytes) in {elapsed:.2f}s")

        # 3. TEST INTEGRITY
        success, feedback = compile_and_test(code, gen)

        if success:
            print(f"  > ✅ COMPILATION SUCCESS!")
            print(f"  > 📜 OUTPUT:\n{feedback}")

            # 4. SAVE GENESIS BLOCK
            genesis_block = {
                "generation": gen,
                "input": PROMPT_OBJECTIVE,
                "solution": code,
                "output": feedback,
                "status": "IMMUTABLE"
            }
            with open("genesis_block_latest.json", "w") as f:
                json.dump(genesis_block, f, indent=2)
            print(f"\n🌊 EVOLUTION COMPLETE. Genesis Block Saved.")
            break

        else:
            print(f"  > ❌ COMPILATION FAILED (Pain Signal Received)")
            # print(f"    ERROR: {feedback.strip().split('\n')[0]}...") # Show first line of error

            # 5. RECURSE (Feed the pain back into the system)
            # This is the "Learning" step.
            current_prompt = (
                f"PREVIOUS CODE FAILED TO COMPILE.\n"
                f"ERROR LOG:\n{feedback}\n"
                f"DIRECTIVE: Fix the syntax errors. Maintain the pointer logic. Return ONLY fixed code."
            )
            print("  > 🔄 RE-INJECTING ERROR FOR MUTATION...")

if __name__ == "__main__":
    main()