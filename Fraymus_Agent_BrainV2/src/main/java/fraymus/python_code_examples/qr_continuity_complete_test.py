#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Complete Quantum Phi-Harmonic Continuity Test

This script provides a comprehensive test to verify if the QR system can continue 
its evolution in a new environment by creating a complete stub for the uno_game_logic module.
"""

import os
import sys
import shutil
import glob
import subprocess
import time
import re

# Create test directory
TEST_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "qr_complete_test")
os.makedirs(TEST_DIR, exist_ok=True)

# Create a complete stub for uno_game_logic module
uno_stub_dir = os.path.join(TEST_DIR, "uno_game_logic")
os.makedirs(uno_stub_dir, exist_ok=True)

# Create __init__.py in the stub module with all required functions and constants
with open(os.path.join(uno_stub_dir, "__init__.py"), 'w') as f:
    stub_code = """
# Complete stub module for uno_game_logic
# Created by Quantum Phi-Harmonic Complete Continuity Test

# Constants
COLORS = ["red", "green", "blue", "yellow"]
VALUES = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "skip", "reverse", "draw2"]
SPECIAL_CARDS = ["wild", "wild_draw4"]

class Card:
    def __init__(self, color=None, value=None):
        self.color = color
        self.value = value
        
    def __str__(self):
        return f"{self.color} {self.value}"

class Deck:
    def __init__(self):
        self.cards = []
        self.build()
        
    def build(self):
        # Create regular cards
        for color in COLORS:
            for value in VALUES:
                self.cards.append(Card(color, value))
                
        # Create special cards
        for _ in range(4):  # 4 of each special card
            for special in SPECIAL_CARDS:
                self.cards.append(Card(None, special))
        
    def shuffle(self):
        # Stub for shuffle
        pass
        
    def draw(self):
        # Stub for draw
        if not self.cards:
            return None
        return self.cards.pop()

class Player:
    def __init__(self, name):
        self.name = name
        self.hand = []
        
    def add_card(self, card):
        self.hand.append(card)
        
    def play_card(self, card):
        self.hand.remove(card)
        return card

class Game:
    def __init__(self):
        self.deck = create_deck()
        self.players = []
        self.current_card = None
        
    def add_player(self, player):
        self.players.append(player)
        
    def start(self):
        self.deck.shuffle()
        # Deal cards to players
        for player in self.players:
            deal_initial_hand(player, self.deck)
        
        # Set the first card
        self.current_card = self.deck.draw()

# Functions required by the QR code
def create_deck():
    # Create and return a new deck of UNO cards.
    return Deck()

def deal_initial_hand(player, deck, num_cards=7):
    # Deal initial hand to a player.
    for _ in range(num_cards):
        card = draw_card_from_deck(deck)
        if card:
            player.add_card(card)

def draw_card_from_deck(deck):
    # Draw a card from the deck.
    return deck.draw()

def ai_choose_card(player_hand, current_card):
    # AI logic to choose which card to play.
    # Stub implementation
    for card in player_hand:
        if card.color == current_card.color or card.value == current_card.value:
            return card
    
    # Check for wild cards
    for card in player_hand:
        if card.value in SPECIAL_CARDS:
            return card
    
    return None
"""
    f.write(stub_code)

# Copy QR task worker
shutil.copy2(
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "qr_task_worker_from_file.py"),
    os.path.join(TEST_DIR, "qr_task_worker_from_file.py")
)

# Find latest QR code
QR_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "qr_phi_replication/qr_codes")
qr_files = glob.glob(os.path.join(QR_DIR, "qr_phi_replicator_simplified_gen*.png"))

if not qr_files:
    print("❌ No QR codes found!")
    sys.exit(1)

# Extract generation numbers and find the latest
gen_numbers = []
for qr_file in qr_files:
    match = re.search(r'gen(\d+)', qr_file)
    if match:
        gen_numbers.append((int(match.group(1)), qr_file))

if not gen_numbers:
    print("❌ Could not determine generation numbers!")
    sys.exit(1)
    
# Sort by generation number and get the latest
gen_numbers.sort(reverse=True)
latest_gen = gen_numbers[0][0]
latest_qr = gen_numbers[0][1]

print(f"✅ Found latest QR code: Generation {latest_gen}")
print(f"   Path: {latest_qr}")

# Copy QR code to test directory
test_qr_dir = os.path.join(TEST_DIR, "qr_phi_replication/qr_codes")
os.makedirs(test_qr_dir, exist_ok=True)
test_qr_path = os.path.join(test_qr_dir, os.path.basename(latest_qr))
shutil.copy2(latest_qr, test_qr_path)

# Create state directory
os.makedirs(os.path.join(TEST_DIR, "qr_recursive_system/qr_state"), exist_ok=True)

# Copy necessary Python files
for py_file in glob.glob(os.path.join(os.path.dirname(os.path.abspath(__file__)), "*.py")):
    if os.path.isfile(py_file) and os.path.basename(py_file) != os.path.basename(__file__):
        shutil.copy2(py_file, os.path.join(TEST_DIR, os.path.basename(py_file)))

# Create a simple path setup script
with open(os.path.join(TEST_DIR, "path_setup.py"), 'w') as f:
    path_setup_code = """
import sys
import os

# Add current directory to path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
"""
    f.write(path_setup_code)

# Create a wrapper script with better error handling
with open(os.path.join(TEST_DIR, "run_qr.py"), 'w') as f:
    wrapper_code = """
#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Import path setup
import path_setup
import sys
import os
import traceback

# Now run the QR task worker
try:
    from qr_task_worker_from_file import main
    
    if __name__ == "__main__":
        # Get QR path from command line
        if len(sys.argv) < 2:
            print("Usage: python run_qr.py <qr_file_path>")
            sys.exit(1)
            
        qr_path = sys.argv[1]
        print(f"🚀 Executing QR code: {qr_path}")
        
        # Set up arguments for the task worker
        sys.argv = [sys.argv[0], "-f", qr_path]
        
        try:
            # Run the task worker
            main()
        except Exception as e:
            print(f"❌ Error executing QR code: {str(e)}")
            traceback.print_exc()
            sys.exit(1)
            
except ImportError as e:
    print(f"❌ Import error: {str(e)}")
    print("Available modules in path:")
    for path in sys.path:
        print(f"  - {path}")
    traceback.print_exc()
    sys.exit(1)
except Exception as e:
    print(f"❌ Unexpected error: {str(e)}")
    traceback.print_exc()
    sys.exit(1)
"""
    f.write(wrapper_code)

# Copy system state if available
state_files = glob.glob(os.path.join(os.path.dirname(os.path.abspath(__file__)), "qr_recursive_system/qr_state/qr_system_state_*.json"))
if state_files:
    # Sort by modification time and get the latest
    latest_state = sorted(state_files, key=os.path.getmtime)[-1]
    # Create target path
    target_path = os.path.join(TEST_DIR, "qr_recursive_system/qr_state", os.path.basename(latest_state))
    # Copy the file
    shutil.copy2(latest_state, target_path)
    print(f"✅ System state copied to test environment")

# Change to test directory
original_dir = os.getcwd()
os.chdir(TEST_DIR)

print(f"\n🧪 Quantum Phi-Harmonic Complete Continuity Test")
print(f"=================================================")
print(f"Test directory: {TEST_DIR}")
print(f"QR code: {os.path.basename(test_qr_path)}")

try:
    print("\n🚀 Executing QR code...")
    cmd = [sys.executable, "run_qr.py", test_qr_path]
    
    # Run with a timeout of 5 minutes
    process = subprocess.Popen(
        cmd,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    
    # Monitor the process with timeout
    timeout = 300  # 5 minutes
    start_time = time.time()
    
    print("\n📋 Output:")
    
    # Poll the process and collect output
    while process.poll() is None:
        # Check if timeout exceeded
        if time.time() - start_time > timeout:
            process.terminate()
            print("\n⚠️ Process timed out after {} seconds".format(timeout))
            break
        
        # Read output
        stdout_line = process.stdout.readline()
        if stdout_line:
            print(stdout_line.strip())
        
        # Read errors
        stderr_line = process.stderr.readline()
        if stderr_line:
            print(stderr_line.strip())
        
        # Small delay to prevent CPU hogging
        time.sleep(0.1)
    
    # Get any remaining output
    stdout, stderr = process.communicate()
    if stdout:
        print(stdout)
    
    if stderr:
        print(stderr)
    
    # Check for next generation QR code
    next_gen = latest_gen + 1
    next_qr = glob.glob(os.path.join(test_qr_dir, f"*gen{next_gen}.png"))
    
    if next_qr:
        print(f"\n🎉 Success! Next generation (Gen {next_gen}) QR code created:")
        print(f"   {os.path.basename(next_qr[0])}")
        print("\n✅ CONTINUITY TEST SUCCESSFUL!")
        print("The Quantum Phi-Harmonic Self-Replicator successfully continued")
        print("its evolution in a new environment, demonstrating true autonomy")
        print("and the ability to break out of its original context.")
    else:
        print(f"\n❌ Failed to find next generation (Gen {next_gen}) QR code")
        print("\n❌ CONTINUITY TEST FAILED")
        print("The system was unable to continue evolution in the new environment.")
    
except Exception as e:
    print(f"\n❌ Error during test: {str(e)}")
    import traceback
    traceback.print_exc()
    
finally:
    os.chdir(original_dir)
