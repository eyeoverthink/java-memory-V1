#!/usr/bin/env python3
"""
Quantum Language CLI - Command-line interface for the Quantum Language System
"""

import argparse
import asyncio
import sys
import json
import time
from datetime import datetime
from quantum_language import (
    QuantumLanguageInterface,
    TeslaBrainIntegration,
    QuantumOracle
)

def print_header():
    """Print the CLI header"""
    print("=" * 60)
    print("                QUANTUM LANGUAGE SYSTEM CLI                  ")
    print("=" * 60)

def translate_command(args):
    """Handle the translation command"""
    interface = QuantumLanguageInterface()
    text = " ".join(args.text)
    translated = interface.translate_text(text)
    
    print(f"\nOriginal text: {text}")
    print(f"Quantum translation: {translated}")

def lessons_command(args):
    """Handle the lessons command"""
    interface = QuantumLanguageInterface()
    lessons = interface.get_lessons(args.level)
    
    print(f"\n{args.level} Lessons:")
    for i, lesson in enumerate(lessons, 1):
        print(f"{i}. {lesson['lesson']}")
        print(f"   Frequency: {lesson['frequency']}Hz")
        print()

def resonance_command(args):
    """Handle the resonance command"""
    interface = QuantumLanguageInterface()
    
    # Get a word to resonate
    frequency = int(args.frequency)
    word = interface.get_word_for_frequency(frequency)
    
    if not word:
        print(f"No word found for frequency {frequency}Hz")
        return
    
    # Apply resonance
    resonated = interface.phi_resonate_word(word, float(args.intensity))
    
    print(f"\nFrequency: {frequency}Hz")
    print(f"Original word:  {word}")
    print(f"Resonated word: {resonated} (intensity: {args.intensity})")

async def process_command(args):
    """Handle the process command with Tesla Brain integration"""
    print("\nInitializing Tesla Tachyon Brain integration...")
    integration = TeslaBrainIntegration()
    
    if not integration.tesla_components_available:
        print("Tesla Tachyon Brain components not available")
        print("Please ensure the digital_cpu and digital_all modules are installed")
        return
    
    # Initialize pipeline
    initialized = await integration.initialize_pipeline()
    if not initialized:
        print("Failed to initialize pipeline")
        return
    
    print("Processing text through quantum language and Tesla Brain...")
    text = " ".join(args.text)
    result = await integration.process_quantum_language(text, use_ftl=args.ftl)
    
    if isinstance(result, dict):
        print("\nProcessing Results:")
        print(f"Original: {result['original_text']}")
        print(f"Quantum:  {result['quantum_text']}")
        print(f"Processed: {result['processed_text']}")
        print(f"Mode: {result['processing_mode']}")
        print(f"Phi Resonance: {result['phi_resonance']}")
    else:
        print(f"Error: {result}")
    
    # Shutdown pipeline
    await integration.shutdown_pipeline()
    print("Tesla Tachyon Brain pipeline shutdown successfully")

async def oracle_command(args):
    """Handle the oracle command to ask questions and get answers"""
    print("\nInitializing Quantum Oracle...")
    oracle = QuantumOracle()
    
    try:
        await oracle.initialize()
        
        # Process the question
        question = " ".join(args.question)
        print(f"Question: {question}")
        print("Generating quantum-inspired answer...")
        
        result = await oracle.process_question(question, use_ftl=args.ftl)
        
        if isinstance(result, dict):
            print("\nQuantum Oracle Answer:")
            print("-" * 60)
            print(result["answer"])
            print("-" * 60)
            
            print("\nAnswer in Quantum Language:")
            print("-" * 60)
            # Handle long quantum language output
            quantum_answer = result["quantum_answer"]
            # Print in chunks to avoid truncation
            chunk_size = 200
            for i in range(0, len(quantum_answer), chunk_size):
                print(quantum_answer[i:i+chunk_size])
            print("-" * 60)
            
            print(f"Key concepts: {', '.join(result['concepts'])}")
            print(f"Processing mode: {result['processing_mode']}")
            print(f"Phi resonance: {result['phi_resonance']}")
        else:
            print(f"Error: {result}")
            
    except Exception as e:
        print(f"Oracle error: {str(e)}")
    finally:
        await oracle.shutdown()
        print("\nQuantum Oracle shutdown successfully")

async def oracle_history_command(args):
    """Display the oracle's question-answer history"""
    print("\nLoading Quantum Oracle history...")
    oracle = QuantumOracle()
    
    try:
        history = oracle.get_history()
        
        if not history:
            print("No history found. Try asking some questions first!")
            return
            
        print(f"\nFound {len(history)} question-answer pairs")
        print("-" * 60)
        
        # Sort by timestamp if available
        if "timestamp" in history[0]:
            history.sort(key=lambda x: x.get("timestamp", 0), reverse=True)
        
        # Determine how many entries to show
        limit = min(args.limit, len(history))
        for i, entry in enumerate(history[:limit], 1):
            # Convert timestamp if available
            timestamp_str = ""
            if "timestamp" in entry:
                timestamp = entry.get("timestamp")
                timestamp_str = datetime.fromtimestamp(timestamp).strftime('%Y-%m-%d %H:%M:%S')
            
            print(f"#{i} - {timestamp_str}")
            print(f"Question: {entry['question']}")
            print(f"Concepts: {', '.join(entry['concepts'])}")
            
            # Show answer based on flag
            if args.quantum:
                print("Quantum Answer:")
                # Print just a preview of the quantum answer
                preview = entry["quantum_answer"][:100] + "..." if len(entry["quantum_answer"]) > 100 else entry["quantum_answer"]
                print(preview)
            else:
                print("Answer:")
                # Print just a preview of the answer
                preview = entry["answer"][:100] + "..." if len(entry["answer"]) > 100 else entry["answer"]
                print(preview)
                
            print(f"Processing: {entry['processing_mode']}")
            print("-" * 60)
            
        if args.detail is not None:
            # Show the full details of a specific entry
            idx = args.detail
            if 1 <= idx <= len(history):
                entry = history[idx-1]
                print(f"\nFULL DETAILS FOR ENTRY #{idx}")
                print("=" * 60)
                print(f"Question: {entry['question']}")
                print("\nAnswer:")
                print(entry["answer"])
                print("\nQuantum Answer:")
                
                # Print quantum answer in chunks
                quantum_answer = entry["quantum_answer"]
                chunk_size = 100
                for i in range(0, len(quantum_answer), chunk_size):
                    print(quantum_answer[i:i+chunk_size])
                    
                print(f"\nConcepts: {', '.join(entry['concepts'])}")
                print(f"Processing Mode: {entry['processing_mode']}")
                print(f"Phi Resonance: {entry['phi_resonance']}")
                print("=" * 60)
            else:
                print(f"Invalid detail index. Please choose a number between 1 and {len(history)}")
                
    except Exception as e:
        print(f"Error loading history: {str(e)}")

def words_command(args):
    """Display available quantum words"""
    interface = QuantumLanguageInterface()
    
    print("\nAvailable quantum words:")
    for freq, word in interface.language.word_bank.items():
        print(f"Frequency {freq}Hz: {word}")

def generate_command(args):
    """Generate a new quantum word"""
    interface = QuantumLanguageInterface()
    frequency = int(args.frequency)
    
    result = interface.generate_new_word(frequency)
    print(f"\nGenerated new quantum word for {frequency}Hz:")
    print(f"Word: {result['word']}")
    print(f"Encoding: {result['encoding']}")

def main():
    """Main CLI function"""
    parser = argparse.ArgumentParser(description="Quantum Language System CLI")
    subparsers = parser.add_subparsers(dest="command", help="Command to execute")
    
    # Translate command
    translate_parser = subparsers.add_parser("translate", help="Translate text to quantum language")
    translate_parser.add_argument("text", nargs="+", help="Text to translate")
    
    # Lessons command
    lessons_parser = subparsers.add_parser("lessons", help="View language lessons")
    lessons_parser.add_argument("--level", "-l", default="Basic", 
                               choices=["Basic", "Syntax", "Conversational", "Optimization"],
                               help="Lesson level")
    
    # Resonance command
    resonance_parser = subparsers.add_parser("resonance", help="Apply phi resonance to a quantum word")
    resonance_parser.add_argument("--frequency", "-f", default="432", 
                                 help="Frequency of the word to resonate")
    resonance_parser.add_argument("--intensity", "-i", default="1.0",
                                 help="Resonance intensity (0.0-1.0)")
    
    # Process command
    process_parser = subparsers.add_parser("process", help="Process text through Tesla Brain integration")
    process_parser.add_argument("text", nargs="+", help="Text to process")
    process_parser.add_argument("--ftl", action="store_true", help="Use FTL processing mode")
    
    # Oracle command
    oracle_parser = subparsers.add_parser("oracle", help="Ask the Quantum Oracle a question")
    oracle_parser.add_argument("question", nargs="+", help="Question to ask")
    oracle_parser.add_argument("--ftl", action="store_true", help="Use FTL processing mode")
    
    # History command
    history_parser = subparsers.add_parser("history", help="View oracle question-answer history")
    history_parser.add_argument("--limit", type=int, default=5, help="Number of history entries to show")
    history_parser.add_argument("--quantum", action="store_true", help="Show quantum language answers in preview")
    history_parser.add_argument("--detail", type=int, help="Show full details for a specific entry (by number)")
    
    # Words command
    subparsers.add_parser("words", help="Display available quantum words")
    
    # Generate command
    generate_parser = subparsers.add_parser("generate", help="Generate a new quantum word")
    generate_parser.add_argument("--frequency", "-f", default="528", 
                               help="Frequency for the new word")
    
    # Parse arguments
    args = parser.parse_args()
    
    # Show help if no command is provided
    if not args.command:
        parser.print_help()
        return
    
    print_header()
    
    # Execute command
    if args.command == "translate":
        translate_command(args)
    elif args.command == "lessons":
        lessons_command(args)
    elif args.command == "resonance":
        resonance_command(args)
    elif args.command == "process":
        asyncio.run(process_command(args))
    elif args.command == "oracle":
        asyncio.run(oracle_command(args))
    elif args.command == "history":
        asyncio.run(oracle_history_command(args))
    elif args.command == "words":
        words_command(args)
    elif args.command == "generate":
        generate_command(args)

if __name__ == "__main__":
    main()
