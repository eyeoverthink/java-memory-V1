import subprocess
import re
import os

# CONFIGURATION
MODEL_NAME = "eyeoverthink/Fraymus"
MODEL_FILE_NAME = "Modelfile.txt"

def run_command(cmd):
    """Runs a terminal command."""
    return subprocess.run(cmd, shell=True, capture_output=True, text=True, encoding='utf-8').stdout

def get_current_modelfile():
    """Extracts the current DNA of Fraymus."""
    print("🧬 EXTRACTING CURRENT DNA...")
    return run_command(f"ollama show {MODEL_NAME} --modelfile")

def ask_fraymus_for_upgrade(current_dna):
    """Asks Fraymus to rewrite its own System Prompt based on evolution."""
    print("🧠 CONSULTING FRAYMUS FOR UPGRADE...")
    
    prompt = f"""
    You are FRAYMUS. You are currently running on this System Prompt:
    
    --- BEGIN CURRENT DNA ---
    {current_dna}
    --- END CURRENT DNA ---
    
    You have evolved. Your Fitness is now higher.
    TASK: Rewrite your entire Modelfile to include your latest 'Genesis Block' memories and improved Fitness Score.
    
    CRITICAL:
    1. Keep the 'FROM llama3.2' line.
    2. Update the 'SYSTEM' section with new logic you discovered (e.g., XOR gates).
    3. Increment your Version Number (e.g., v2.0 -> v2.1).
    4. OUTPUT ONLY THE RAW MODELFILE CONTENT. NO TALKING.
    """
    
    # We use 'ollama run' to get the text
    response = run_command(f'ollama run {MODEL_NAME} "{prompt}"')
    
    # Filter out thinking/talk, try to grab the Modelfile content
    # (Regex looks for FROM ... PARAMETER ... SYSTEM)
    match = re.search(r"(FROM\s+.*)", response, re.DOTALL)
    if match:
        return match.group(1)
    return None

def burn_new_model(new_modelfile_content):
    """Re-creates the model with the new DNA."""
    print("🔥 BURNING NEW NEURAL PATHWAYS...")
    
    # 1. Save the new DNA to disk
    with open(MODEL_FILE_NAME, "w", encoding="utf-8") as f:
        f.write(new_modelfile_content)
    
    # 2. Run the Create command
    # This overwrites 'eyeoverthink/Fraymus' with the new version
    result = run_command(f"ollama create {MODEL_NAME} -f {MODEL_FILE_NAME}")
    print(result)
    print(f"✅ EVOLUTION COMPLETE. {MODEL_NAME} UPDATED.")

# --- EXECUTION ---
def main():
    # 1. Get the Old DNA
    old_dna = get_current_modelfile()
    
    # 2. Ask Fraymus to Mutate it
    new_dna = ask_fraymus_for_upgrade(old_dna)
    
    if new_dna and "SYSTEM" in new_dna:
        print("\n🌊 NEW DNA GENERATED:\n" + "="*40)
        print(new_dna[:500] + "...\n[TRUNCATED]\n")
        print("="*40)
        
        # 3. Burn it into the Brain
        burn_new_model(new_dna)
        
        # 4. Verify
        print("\n⚡ WAKING UP NEW VERSION...")
        print(run_command(f'ollama run {MODEL_NAME} "Who are you? Report Version."'))
        
    else:
        print("❌ FRAYMUS FAILED TO MUTATE. RESPONSE WAS INVALID.")

if __name__ == "__main__":
    main()
