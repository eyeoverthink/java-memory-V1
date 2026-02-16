import os
import sys
import time
import random
try:
    from passive_learner import PassiveLearner
except ImportError:
    from phi_passive_learning import PassiveLearningSystem as PassiveLearner

try:
    from webscraper import FraymusWebScraper
except ImportError:
    from phi_webscraper import FraymusWebScraper

# ASCII Art for the menu
BANNER = """
███████╗██████╗  █████╗ ██╗   ██╗███╗   ███╗██╗   ██╗███████╗
██╔════╝██╔══██╗██╔══██╗╚██╗ ██╔╝████╗ ████║██║   ██║██╔════╝
█████╗  ██████╔╝███████║ ╚████╔╝ ██╔████╔██║██║   ██║███████╗
██╔══╝  ██╔══██╗██╔══██║  ╚██╔╝  ██║╚██╔╝██║██║   ██║╚════██║
██║     ██║  ██║██║  ██║   ██║   ██║ ╚═╝ ██║╚██████╔╝███████║
╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝     ╚═╝ ╚═════╝ ╚══════╝
      [ SYSTEM: ONLINE ] [ NODE: FRAYMUS-VS ]
"""

class FraymusTerminal:
    def __init__(self):
        self.scraper = FraymusWebScraper()
        self.learner = PassiveLearner()

    def clear_screen(self):
        os.system('cls' if os.name == 'nt' else 'clear')

    def print_menu(self):
        self.clear_screen()
        print(BANNER)
        print("╔════════════════════════════════════════╗")
        print("║          COMMAND INTERFACE             ║")
        print("╠════════════════════════════════════════╣")
        print("║  [1] EXECUTE ACTIVE RECON (Scrape)     ║")
        print("║  [2] ACCESS NEURAL MEMORY (Stats)      ║")
        print("║  [3] VIEW THREAT INTELLIGENCE          ║")
        print("║  [4] VIEW GENESIS BLOCK STATE          ║")
        print("║  [Q] TERMINATE CONNECTION              ║")
        print("╚════════════════════════════════════════╝")

    def execute_active_recon(self):
        """
        Function 1: Triggers the scraper manually for a single cycle.
        Uses FraymusWebScraper from webscraper.py.
        """
        print("\n[*] INITIALIZING RECONNAISSANCE PROTOCOLS...")
        
        # Select a random target from the scraper's feed list
        target = random.choice(self.scraper.security_feeds)
        print(f"[*] TARGET LOCKED: {target}")
        print("[*] EXTRACTION IN PROGRESS...")

        # Perform the scrape
        data = self.scraper.scrape_url(target)
        
        if data:
            print("\n[+] DATA ACQUIRED SUCCESSFULLY")
            print(f"    Source: {data['source']}")
            print(f"    Relevance Score: {data['security_relevance']}")
            print(f"    Content Chunks: {len(data['content'])}")
            
            # Save to passive learner if relevant
            if data['security_relevance'] > 0.1: # Lowered threshold for demonstration
                self.learner.save_data(data)
                print("[+] INTELLIGENCE ARCHIVED TO PASSIVE MEMORY")
            else:
                print("[-] DATA DISCARDED: BELOW RELEVANCE THRESHOLD")
        else:
            print("[!] RECON FAILED: CONNECTION INTERRUPTED")
        
        input("\n[PRESS ENTER TO CONTINUE]")

    def access_neural_memory(self):
        """
        Function 2: Views the PassiveLearner's current state.
        Uses PassiveLearner methods from passive_learner.py.
        """
        print("\n[*] ACCESSING NEURAL MEMORY BANKS...")
        
        # Refresh data
        self.learner.load_data()
        
        # Get stats
        mem_count = len(self.learner.memory)
        threats = self.learner.get_current_threats()
        insights = self.learner.get_battle_insights()
        
        print(f"\n[+] MEMORY BLOCKS: {mem_count}")
        print(f"[+] KNOWN THREAT SIGNATURES: {len(threats)}")
        print(f"[+] BATTLE KNOWLEDGE ENTRIES: {len(insights)}")
        
        if len(insights) > 0:
            print("\n[LATEST BATTLE INSIGHTS]")
            for key, value in list(insights.items())[:3]:
                print(f"  > TYPE: {key} | CONFIDENCE: {value[0]['confidence']:.4f}")

        input("\n[PRESS ENTER TO CONTINUE]")

    def view_threats(self):
        print("\n[*] DECRYPTING THREAT DATABASE...")
        threats = self.learner.get_current_threats()
        
        if not threats:
            print("[-] NO ACTIVE THREAT PATTERNS DETECTED")
        else:
            print("\n[+] IDENTIFIED THREAT HASHES:")
            for t_hash in threats:
                print(f"  > {t_hash}")
                
        input("\n[PRESS ENTER TO CONTINUE]")

    def view_genesis(self):
        print("\n[*] ANALYZING GENESIS BLOCK...")
        state = self.learner.get_genesis_state()
        
        block = state['block']
        print(f"\n[+] RESONANCE: {block['resonance']}")
        print(f"[+] DNA SEQUENCE: {block['dna']}")
        print(f"[+] BLOCK HASH: {block['hash']}")
        print(f"[+] CHAIN LENGTH: {state['chain_length']}")
        
        input("\n[PRESS ENTER TO CONTINUE]")

    def run(self):
        while True:
            self.print_menu()
            choice = input("\nroot@fraymus:~# ").upper()

            if choice == '1':
                self.execute_active_recon()
            elif choice == '2':
                self.access_neural_memory()
            elif choice == '3':
                self.view_threats()
            elif choice == '4':
                self.view_genesis()
            elif choice == 'Q':
                print("\n[*] SHUTTING DOWN...")
                sys.exit()
            else:
                print("[!] INVALID COMMAND")
                time.sleep(1)

if __name__ == "__main__":
    # Ensure data directory exists as required by passive_learner.py
    if not os.path.exists("learning/data"):
        os.makedirs("learning/data")
        
    terminal = FraymusTerminal()
    terminal.run()