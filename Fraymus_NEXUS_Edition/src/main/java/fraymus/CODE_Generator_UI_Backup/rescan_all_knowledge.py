
#!/usr/bin/env python3
"""
RESCAN ALL KNOWLEDGE - Force rebuild of knowledge base from all PDFs and Markdown files
Run this to populate the knowledge base with all available documents.
"""

import os
import sys

# Add parent directory to path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from pdf_knowledge_extractor import PhysicsKnowledgeExtractor

def main():
    print("=" * 70)
    print("KNOWLEDGE BASE RESCAN - Scanning all PDFs and Markdown files")
    print("=" * 70)
    
    # Initialize extractor
    extractor = PhysicsKnowledgeExtractor()
    
    # Add Python programming textbook
    python_textbook = r"C:\Users\eyeka\OneDrive\Documents\Quantum_Oracle-main-20260125T094704Z-1-001\Quantum_Oracle-main\Quantum_Oracle-main\Emailing Python Programming-An Introduction to Computer Science 2nd edition-John Zelle 2010.pdf"
    knowledge_base_2 = r"C:\Users\eyeka\OneDrive\Documents\Quantum_Oracle-main-20260125T094704Z-1-001\Quantum_Oracle-main\knowledge_base_2"
    
    # Add to additional directories if not already there
    if not hasattr(extractor, 'additional_pdf_dirs'):
        extractor.additional_pdf_dirs = []
    
    if os.path.exists(python_textbook):
        print(f"\n✓ Found Python textbook: {os.path.basename(python_textbook)}")
        # Process this specific PDF
        extractor.additional_pdfs = [python_textbook]
    
    if os.path.exists(knowledge_base_2):
        print(f"✓ Found knowledge_base_2 directory")
        if knowledge_base_2 not in extractor.additional_pdf_dirs:
            extractor.additional_pdf_dirs.append(knowledge_base_2)
    
    print(f"\nConfigured directories:")
    print(f"  PDF dir: {extractor.pdf_dir}")
    print(f"  AGI PDF dir: {extractor.agi_pdf_dir}")
    print(f"  Learning docs: {extractor.learning_docs_dir}")
    if hasattr(extractor, 'additional_pdf_dirs'):
        for d in extractor.additional_pdf_dirs:
            print(f"  Additional: {d}")
    print(f"  Knowledge dir: {extractor.knowledge_dir}")
    
    if hasattr(extractor, 'additional_pdfs'):
        print(f"\nAdditional PDFs:")
        for pdf in extractor.additional_pdfs:
            print(f"  - {os.path.basename(pdf)}")
    
    # Force reprocess all documents
    print("\n" + "=" * 70)
    print("PROCESSING ALL DOCUMENTS (force_reprocess=True)")
    print("=" * 70)
    
    kb = extractor.process_all_documents(force_reprocess=True)
    
    # Print summary
    print("\n" + "=" * 70)
    print("KNOWLEDGE BASE SUMMARY")
    print("=" * 70)
    
    concepts = kb.get('concepts', {})
    theories = kb.get('theories', [])
    equations = kb.get('equations', [])
    code_patterns = kb.get('code_patterns', {})
    code_snippets = kb.get('code_snippets', [])
    
    print(f"  Concepts: {sum(len(v) if isinstance(v, list) else 1 for v in concepts.values())} items")
    print(f"  Theories: {len(theories)} items")
    print(f"  Equations: {len(equations)} items")
    print(f"  Code patterns: {sum(len(v) if isinstance(v, list) else 1 for v in code_patterns.values())} items")
    print(f"  Code snippets: {len(code_snippets)} items")
    
    print("\n✓ Knowledge base rebuilt successfully!")
    print(f"  Saved to: {extractor.knowledge_base_path}")

if __name__ == "__main__":
    main()
