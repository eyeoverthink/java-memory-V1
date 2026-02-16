#!/usr/bin/env python3
"""
Physics Knowledge Extractor - Extracts physics concepts from PDF documents
"""

import os
import re
import json
import glob
import time
import random
import numpy as np
from typing import Dict, Any, List, Optional, Union
import sys
import importlib.util

# Try to import PDF processing libraries
try:
    import PyPDF2
    PDF_AVAILABLE = True
except ImportError:
    PDF_AVAILABLE = False
    print("Warning: PyPDF2 not available. PDF processing will be limited.")

# Try to import NLP libraries
try:
    import nltk
    from nltk.tokenize import word_tokenize
    from nltk.corpus import stopwords
    
    # Download required NLTK data
    try:
        nltk.data.find('tokenizers/punkt')
    except LookupError:
        nltk.download('punkt')
        
    try:
        nltk.data.find('corpora/stopwords')
    except LookupError:
        nltk.download('stopwords')
        
    NLP_AVAILABLE = True
except ImportError:
    NLP_AVAILABLE = False
    print("Warning: NLTK not available. NLP processing will be limited.")

# Define physics categories and keywords
PHYSICS_CATEGORIES = {
    'quantum': ['quantum', 'wave function', 'superposition', 'entanglement', 'qubit', 'quantum field', 'quantum mechanics'],
    'relativity': ['relativity', 'spacetime', 'gravity', 'gravitational', 'curvature', 'einstein', 'lorentz'],
    'particles': ['particle', 'boson', 'fermion', 'quark', 'lepton', 'hadron', 'photon', 'gluon']
}

# Define code categories and keywords
CODE_CATEGORIES = {
    'python': ['def ', 'class ', 'import ', 'if __name__', 'for ', 'while ', 'try:', 'except:', 'with ', 'return ', 'yield ', 'async ', 'await '],
    'java': ['public class', 'private', 'protected', 'void', 'static', 'extends', 'implements', 'new ', 'return ', 'import ', 'package '],
    'algorithms': ['function', 'algorithm', 'complexity', 'big o', 'recursion', 'iteration', 'sort', 'search', 'tree', 'graph', 'linked list']
}

class PhysicsKnowledgeExtractor:
    """Extracts and processes physics knowledge from PDFs for quantum learning"""
    
    def __init__(self, pdf_dir=None, knowledge_dir=None):
        """Initialize the knowledge extractor"""
        # Set up paths
        base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        
        if pdf_dir is None:
            self.pdf_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "physics_pdfs")
        else:
            self.pdf_dir = pdf_dir
            
        # Check if learning_docs directory exists and use it as primary source
        learning_docs_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "learning_docs")
        if os.path.exists(learning_docs_dir) and os.path.isdir(learning_docs_dir):
            self.learning_docs_dir = learning_docs_dir
            print(f"Found learning_docs directory: {self.learning_docs_dir}")
        else:
            self.learning_docs_dir = None
        
        # Check for AGI_Material/pdf_files directory (new centralized PDF location)
        agi_pdf_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "AGI_Material", "pdf_files")
        if os.path.exists(agi_pdf_dir) and os.path.isdir(agi_pdf_dir):
            self.agi_pdf_dir = agi_pdf_dir
            print(f"Found AGI_Material PDF directory: {self.agi_pdf_dir}")
        else:
            self.agi_pdf_dir = None
        
        # Additional PDF directories to scan (all found in project)
        self.additional_pdf_dirs = []
        additional_paths = [
            os.path.join(os.path.dirname(os.path.abspath(__file__)), "EmojiMaker-Oracle", "Sign-Sculptor", "attached_assets", "fraymus_extracted", "Fraymus", "attached_assets"),
            os.path.join(os.path.dirname(os.path.abspath(__file__)), "Quantum_Oracle-main"),  # Nested copy
        ]
        for path in additional_paths:
            if os.path.exists(path) and os.path.isdir(path):
                self.additional_pdf_dirs.append(path)
                print(f"Found additional PDF directory: {path}")
        
        if knowledge_dir is None:
            self.knowledge_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "knowledge_base")
        else:
            self.knowledge_dir = knowledge_dir
            
        # Create directories if they don't exist
        os.makedirs(self.pdf_dir, exist_ok=True)
        os.makedirs(self.knowledge_dir, exist_ok=True)
        
        # Initialize NLP components first to ensure phi is available for knowledge base creation
        self.phi = 1.618033988749895  # Golden ratio for resonance calculations
        
        # Initialize knowledge base
        self.knowledge_base_path = os.path.join(self.knowledge_dir, "physics_knowledge_base.json")
        self.knowledge_base = self._load_knowledge_base()
        
    def _load_knowledge_base(self):
        """Load the knowledge base from disk or create a new one"""
        if os.path.exists(self.knowledge_base_path):
            try:
                with open(self.knowledge_base_path, 'r', encoding='utf-8') as f:
                    knowledge_base = json.load(f)
                print(f"Loaded knowledge base with {len(knowledge_base.get('concepts', {}))} concepts, {len(knowledge_base.get('equations', []))} equations, and {len(knowledge_base.get('theories', []))} theories.")
                return knowledge_base
            except Exception as e:
                print(f"Error loading knowledge base: {e}")
                return self._create_empty_knowledge_base()
        else:
            return self._create_empty_knowledge_base()
    
    def _create_empty_knowledge_base(self):
        """Create an empty knowledge base structure"""
        return {
            "concepts": {},
            "equations": [],
            "theories": [],
            "code_snippets": [],
            "code_patterns": {},
            "meta": {
                "last_updated": time.time(),
                "version": "1.0",
                "phi_resonance": self.phi
            }
        }
    
    def _save_knowledge_base(self):
        """Save the knowledge base to disk"""
        try:
            os.makedirs(os.path.dirname(self.knowledge_base_path), exist_ok=True)
            with open(self.knowledge_base_path, 'w', encoding='utf-8') as f:
                json.dump(self.knowledge_base, f, indent=2)
            print(f"Saved knowledge base with {sum(len(concepts) for concepts in self.knowledge_base['concepts'].values())} concepts, "
                  f"{len(self.knowledge_base['equations'])} equations, {len(self.knowledge_base['theories'])} theories, and "
                  f"{len(self.knowledge_base.get('code_snippets', []))} code snippets.")
            return True
        except Exception as e:
            print(f"Error saving knowledge base: {e}")
            return False
    
    def delete_knowledge_base(self):
        """Delete the existing knowledge base to force a refresh"""
        try:
            if os.path.exists(self.knowledge_base_path):
                os.remove(self.knowledge_base_path)
                print(f"Deleted existing knowledge base at {self.knowledge_base_path}")
                # Reset the knowledge base in memory
                self.knowledge_base = self._create_empty_knowledge_base()
                return True
            else:
                print("No existing knowledge base to delete")
                return False
        except Exception as e:
            print(f"Error deleting knowledge base: {e}")
            return False
            
    def knowledge_base_exists(self):
        """Check if the knowledge base file exists"""
        return os.path.exists(self.knowledge_base_path)
    
    def process_pdf(self, pdf_path):
        """Process a single PDF file and update the knowledge base"""
        if not os.path.exists(pdf_path):
            print(f"PDF file not found: {pdf_path}")
            return False
            
        try:
            # Process the PDF
            self._process_pdf(pdf_path)
            
            # Save the updated knowledge base
            self._save_knowledge_base()
            
            print(f"Successfully processed PDF: {os.path.basename(pdf_path)}")
            return True
        except Exception as e:
            print(f"Error processing PDF {pdf_path}: {e}")
            return False
    
    def process_all_documents(self, force_reprocess=False):
        """Process all PDFs and Markdown files to extract knowledge"""
        # Check if we need to process
        if not force_reprocess and os.path.exists(self.knowledge_base_path):
            return self.knowledge_base
        
        # Get all document files (PDFs and Markdown)
        pdf_paths = []
        md_paths = []
        
        # Directories to search
        search_dirs = []
        if self.agi_pdf_dir and os.path.exists(self.agi_pdf_dir):
            search_dirs.append(self.agi_pdf_dir)
        if self.learning_docs_dir and os.path.exists(self.learning_docs_dir):
            search_dirs.append(self.learning_docs_dir)
        if os.path.exists(self.pdf_dir):
            search_dirs.append(self.pdf_dir)
        
        # Add all additional PDF directories
        if hasattr(self, 'additional_pdf_dirs'):
            for add_dir in self.additional_pdf_dirs:
                if os.path.exists(add_dir):
                    search_dirs.append(add_dir)
        
        # Also search the base Quantum_Oracle-main directory for .md files
        base_dir = os.path.dirname(os.path.abspath(__file__))
        search_dirs.append(base_dir)
        
        # Search all knowledge_base directories too
        for kb_dir in ['knowledge_base', 'ISOLATED_ORACLE_PACKAGE/knowledge_base', 
                       'Oracle-And-Sign-Maker/knowledge_base', 'Quantum_Oracle-main/knowledge_base']:
            kb_path = os.path.join(base_dir, kb_dir)
            if os.path.exists(kb_path):
                search_dirs.append(kb_path)
        
        for search_dir in search_dirs:
            pdf_paths.extend(glob.glob(os.path.join(search_dir, "**", "*.pdf"), recursive=True))
            md_paths.extend(glob.glob(os.path.join(search_dir, "**", "*.md"), recursive=True))
        
        # Remove duplicates
        pdf_paths = list(set(pdf_paths))
        md_paths = list(set(md_paths))
        
        print(f"Found {len(pdf_paths)} PDF files and {len(md_paths)} Markdown files to process.")
        
        # Initialize equation set for fast duplicate checking
        self._equation_set = set(eq["equation"] for eq in self.knowledge_base.get("equations", []))
        
        # Process PDFs with progress
        for i, pdf_path in enumerate(pdf_paths):
            try:
                if i % 10 == 0:
                    print(f"  [{i+1}/{len(pdf_paths)}] Processing PDFs...")
                self._process_pdf(pdf_path)
            except Exception as e:
                print(f"Error processing PDF {pdf_path}: {e}")
        
        print(f"  ✓ Finished {len(pdf_paths)} PDFs")
        
        # Process Markdown files with progress
        for i, md_path in enumerate(md_paths):
            try:
                if i % 20 == 0:
                    print(f"  [{i+1}/{len(md_paths)}] Processing Markdown...")
                self._process_markdown(md_path)
            except Exception as e:
                print(f"Error processing Markdown {md_path}: {e}")
        
        # Save the updated knowledge base
        self._save_knowledge_base()
        
        return self.knowledge_base
    
    def process_all_pdfs(self, force_reprocess=False):
        """Process all PDFs in the directory and extract physics knowledge (legacy method)"""
        return self.process_all_documents(force_reprocess)
    
    def _process_markdown(self, md_path):
        """Process a Markdown file and extract knowledge"""
        try:
            with open(md_path, 'r', encoding='utf-8', errors='ignore') as f:
                text = f.read()
            
            source = os.path.basename(md_path)
            print(f"Processing Markdown: {source}")
            
            # Extract physics concepts
            self._extract_physics_concepts(text, source)
            
            # Extract equations (Markdown often has LaTeX)
            self._extract_equations(text, source)
            
            # Extract theories
            self._extract_theories(text, source)
            
            # Extract code snippets (Markdown has fenced code blocks)
            self._extract_code_snippets(text, source)
            
            # Extract Markdown-specific code blocks (```python, ```java, etc.)
            self._extract_markdown_code_blocks(text, source)
            
            return True
        except Exception as e:
            print(f"Error processing Markdown {md_path}: {e}")
            return False
    
    def _extract_markdown_code_blocks(self, text, source):
        """Extract fenced code blocks from Markdown (```language ... ```)"""
        # Match fenced code blocks with optional language specifier
        pattern = r'```(\w*)\n(.*?)```'
        matches = re.findall(pattern, text, re.DOTALL)
        
        for lang, code in matches:
            if len(code.strip()) > 20:  # Skip tiny snippets
                lang = lang.lower() if lang else 'unknown'
                
                # Determine category
                if lang in ['python', 'py']:
                    category = 'python'
                elif lang in ['java', 'javascript', 'js']:
                    category = 'java'
                elif lang in ['asm', 'assembly', 'x86']:
                    category = 'assembly'
                else:
                    category = 'algorithms'
                
                # Initialize category if needed
                if "code_patterns" not in self.knowledge_base:
                    self.knowledge_base["code_patterns"] = {}
                if category not in self.knowledge_base["code_patterns"]:
                    self.knowledge_base["code_patterns"][category] = []
                
                # Add the code snippet
                snippet = {
                    "code": code.strip(),
                    "language": lang,
                    "source": source,
                    "phi_resonance": self.phi * len(code) / 1000
                }
                
                # Avoid duplicates
                exists = any(s.get("code") == snippet["code"] for s in self.knowledge_base["code_patterns"][category])
                if not exists:
                    self.knowledge_base["code_patterns"][category].append(snippet)
    
    def _process_pdf(self, pdf_path):
        """Process a PDF file and extract physics knowledge and code patterns"""
        if not PDF_AVAILABLE:
            print("PyPDF2 not available. Cannot process PDF.")
            return False
            
        try:
            # Extract text from PDF
            with open(pdf_path, 'rb') as f:
                pdf_reader = PyPDF2.PdfReader(f)
                text = ""
                for page in pdf_reader.pages:
                    page_text = page.extract_text()
                    if page_text:
                        text += page_text + "\n"
                        
            # Extract physics concepts
            self._extract_physics_concepts(text, os.path.basename(pdf_path))
            
            # Extract equations
            self._extract_equations(text, os.path.basename(pdf_path))
            
            # Extract theories
            self._extract_theories(text, os.path.basename(pdf_path))
            
            # Extract code snippets and patterns
            self._extract_code_snippets(text, os.path.basename(pdf_path))
            
            return True
        except Exception as e:
            print(f"Error processing PDF {pdf_path}: {e}")
            return False
    
    def _extract_physics_concepts(self, text, source):
        """Extract physics concepts from text"""
        # Process for each category
        for category, keywords in PHYSICS_CATEGORIES.items():
            # Initialize category in knowledge base if it doesn't exist
            if category not in self.knowledge_base["concepts"]:
                self.knowledge_base["concepts"][category] = []
            
            # Look for keywords in text
            for keyword in keywords:
                # Find all occurrences of the keyword
                pattern = r'\b' + re.escape(keyword) + r'\b'
                matches = re.finditer(pattern, text.lower())
                
                for match in matches:
                    # Get context around the keyword (100 chars before and after)
                    start = max(0, match.start() - 100)
                    end = min(len(text), match.end() + 100)
                    context = text[start:end]
                    
                    # Check if this concept already exists
                    exists = False
                    for concept in self.knowledge_base["concepts"][category]:
                        if concept["concept"] == keyword:
                            # Update context if this one is better (longer)
                            if len(context) > len(concept["context"]):
                                concept["context"] = context
                                concept["phi_resonance"] = self._calculate_concept_resonance(keyword, context)
                            exists = True
                            break
                    
                    # Add new concept if it doesn't exist
                    if not exists:
                        self.knowledge_base["concepts"][category].append({
                            "concept": keyword,
                            "context": context,
                            "phi_resonance": self._calculate_concept_resonance(keyword, context)
                        })
    
    def _extract_equations(self, text, source):
        """Extract equations from text"""
        # Look for patterns that might indicate equations
        equation_patterns = [
            r'(\w+)\s*=\s*([^,;\n]+)',  # Simple equations like E = mc^2
            r'(\w+)\s*≈\s*([^,;\n]+)',  # Approximations
            r'(\w+)\s*∝\s*([^,;\n]+)',  # Proportionality
            r'\\begin\{equation\}(.*?)\\end\{equation\}',  # LaTeX equations
            r'\$\$(.*?)\$\$',  # LaTeX display equations
            r'\$(.*?)\$'  # LaTeX inline equations
        ]
        
        for pattern in equation_patterns:
            matches = re.finditer(pattern, text, re.DOTALL)
            
            for match in matches:
                # Get the equation and context
                equation_text = match.group(0)
                
                # Get context around the equation (100 chars before and after)
                start = max(0, match.start() - 100)
                end = min(len(text), match.end() + 100)
                context = text[start:end]
                
                # Check if this equation already exists (use set for O(1) lookup)
                if not hasattr(self, '_equation_set'):
                    self._equation_set = set(eq["equation"] for eq in self.knowledge_base["equations"])
                
                # Add new equation if it doesn't exist
                if equation_text not in self._equation_set:
                    self._equation_set.add(equation_text)
                    self.knowledge_base["equations"].append({
                        "equation": equation_text,
                        "context": context,
                        "phi_resonance": self._calculate_equation_resonance(equation_text)
                    })
    
    def _extract_theories(self, text, source):
        """Extract physics theories from text"""
        # Simple pattern matching for theory descriptions
        theory_patterns = [
            r"([A-Z][\w\s]+) theory states that ([^.]+)",
            r"([A-Z][\w\s]+) principle states that ([^.]+)",
            r"According to ([A-Z][\w\s]+) theory,\s*([^.]+)",
            r"The theory of ([A-Z][\w\s]+) states that ([^.]+)"
        ]
        
        for pattern in theory_patterns:
            matches = re.finditer(pattern, text)
            for match in matches:
                try:
                    theory_name = match.group(1).strip()
                    description = match.group(2).strip()
                    
                    # Skip if too short
                    if len(theory_name) < 3 or len(description) < 10:
                        continue
                        
                    # Create theory entry
                    theory = {
                        "name": theory_name,
                        "description": description,
                        "source": source,
                        "confidence": 0.8,  # Default confidence
                        "phi_resonance": self._calculate_phi_resonance(theory_name + description)
                    }
                    
                    # Add to knowledge base if not duplicate
                    if not any(t["name"] == theory_name for t in self.knowledge_base["theories"]):
                        self.knowledge_base["theories"].append(theory)
                except Exception as e:
                    print(f"Error extracting theory: {e}")
                    
    def _extract_code_snippets(self, text, source):
        """Extract code snippets and patterns from text"""
        # Initialize code_snippets if it doesn't exist
        if "code_snippets" not in self.knowledge_base:
            self.knowledge_base["code_snippets"] = []
            
        if "code_patterns" not in self.knowledge_base:
            self.knowledge_base["code_patterns"] = {}
        
        # Python code pattern matching
        python_patterns = [
            # Function definitions
            r"def\s+([\w_]+)\s*\(([^)]*)\)\s*:([^#]+)",
            # Class definitions
            r"class\s+([\w_]+)(?:\s*\(([^)]*)\))?\s*:([^#]+)",
            # Import statements
            r"(from\s+[\w_.]+\s+import\s+[\w_.,\s]+|import\s+[\w_.,\s]+)",
            # Complete code blocks with indentation
            r"((?:^[ \t]*(?:def|class|if|for|while|try|with)[ \t]+[^\n]+\n(?:^[ \t]+[^\n]+\n)+)+)"
        ]
        
        # Java code pattern matching
        java_patterns = [
            # Method definitions
            r"(?:public|private|protected)\s+(?:static\s+)?[\w<>\[\]]+\s+([\w_]+)\s*\(([^)]*)\)\s*\{([^}]*)\}",
            # Class definitions
            r"(?:public|private|protected)\s+class\s+([\w_]+)(?:\s+extends\s+[\w_]+)?(?:\s+implements\s+[\w_,\s]+)?\s*\{([^}]*)\}",
            # Import statements
            r"(import\s+[\w_.]+(?:\.[*])?;)"
        ]
        
        # Process Python patterns
        for pattern in python_patterns:
            matches = re.finditer(pattern, text, re.MULTILINE | re.DOTALL)
            for match in matches:
                try:
                    snippet = match.group(0).strip()
                    
                    # Skip if too short or looks like noise
                    if len(snippet) < 10 or not self._is_valid_code(snippet, 'python'):
                        continue
                    
                    # Apply φ-dimensional reality protection to code
                    phi_resonance = self._calculate_phi_resonance(snippet)
                    
                    # Create code snippet entry
                    code_entry = {
                        "language": "python",
                        "code": snippet,
                        "source": source,
                        "phi_resonance": phi_resonance,
                        "extracted_at": time.time()
                    }
                    
                    # Extract function or class name if available
                    if match.lastindex >= 1 and pattern.startswith("def") or pattern.startswith("class"):
                        code_entry["name"] = match.group(1)
                    
                    # Add to knowledge base if not duplicate
                    if not any(c.get("code") == snippet for c in self.knowledge_base["code_snippets"]):
                        self.knowledge_base["code_snippets"].append(code_entry)
                        
                        # Extract patterns from code
                        self._extract_code_patterns(snippet, "python")
                except Exception as e:
                    print(f"Error extracting Python code: {e}")
        
        # Process Java patterns
        for pattern in java_patterns:
            matches = re.finditer(pattern, text, re.MULTILINE | re.DOTALL)
            for match in matches:
                try:
                    snippet = match.group(0).strip()
                    
                    # Skip if too short or looks like noise
                    if len(snippet) < 10 or not self._is_valid_code(snippet, 'java'):
                        continue
                    
                    # Apply φ-dimensional reality protection to code
                    phi_resonance = self._calculate_phi_resonance(snippet)
                    
                    # Create code snippet entry
                    code_entry = {
                        "language": "java",
                        "code": snippet,
                        "source": source,
                        "phi_resonance": phi_resonance,
                        "extracted_at": time.time()
                    }
                    
                    # Extract method or class name if available
                    if match.lastindex >= 1:
                        code_entry["name"] = match.group(1)
                    
                    # Add to knowledge base if not duplicate
                    if not any(c.get("code") == snippet for c in self.knowledge_base["code_snippets"]):
                        self.knowledge_base["code_snippets"].append(code_entry)
                        
                        # Extract patterns from code
                        self._extract_code_patterns(snippet, "java")
                except Exception as e:
                    print(f"Error extracting Java code: {e}")
    
    def _is_valid_code(self, snippet, language):
        """Check if a code snippet is valid and not just random text"""
        # Basic validation for Python
        if language == 'python':
            # Check for Python keywords and syntax
            python_indicators = ['def ', 'class ', 'import ', 'if ', 'for ', 'while ', 'try:', 'except:', 'return ', '    ']
            return any(indicator in snippet for indicator in python_indicators)
        
        # Basic validation for Java
        elif language == 'java':
            # Check for Java keywords and syntax
            java_indicators = ['public ', 'private ', 'class ', 'void ', 'int ', 'String ', 'return ', '{', '}', ';']
            return any(indicator in snippet for indicator in java_indicators)
        
        return False
    
    def _extract_code_patterns(self, snippet, language):
        """Extract reusable patterns from code snippets"""
        # Initialize language in code_patterns if needed
        if language not in self.knowledge_base["code_patterns"]:
            self.knowledge_base["code_patterns"][language] = []
        
        patterns = []
        
        if language == 'python':
            # Extract function patterns
            function_match = re.search(r"def\s+([\w_]+)\s*\(([^)]*)\)\s*:([^#]+)", snippet, re.DOTALL)
            if function_match:
                function_name = function_match.group(1)
                params = function_match.group(2)
                body = function_match.group(3).strip()
                
                # Create a pattern entry
                pattern = {
                    "type": "function",
                    "name": function_name,
                    "params": params,
                    "body_template": body,
                    "phi_resonance": self._calculate_phi_resonance(body),
                    "abstraction_level": self._calculate_abstraction_level(body)
                }
                patterns.append(pattern)
            
            # Extract class patterns
            class_match = re.search(r"class\s+([\w_]+)(?:\s*\(([^)]*)\))?\s*:([^#]+)", snippet, re.DOTALL)
            if class_match:
                class_name = class_match.group(1)
                inheritance = class_match.group(2) if class_match.group(2) else ""
                body = class_match.group(3).strip()
                
                # Create a pattern entry
                pattern = {
                    "type": "class",
                    "name": class_name,
                    "inheritance": inheritance,
                    "body_template": body,
                    "phi_resonance": self._calculate_phi_resonance(body),
                    "abstraction_level": self._calculate_abstraction_level(body)
                }
                patterns.append(pattern)
        
        elif language == 'java':
            # Extract method patterns
            method_match = re.search(r"(?:public|private|protected)\s+(?:static\s+)?[\w<>\[\]]+\s+([\w_]+)\s*\(([^)]*)\)\s*\{([^}]*)\}", snippet, re.DOTALL)
            if method_match:
                return_type = method_match.group(1)
                method_name = method_match.group(2)
                params = method_match.group(3)
                body = method_match.group(4).strip()
                
                # Create a pattern entry
                pattern = {
                    "type": "method",
                    "return_type": return_type,
                    "name": method_name,
                    "params": params,
                    "body_template": body,
                    "phi_resonance": self._calculate_phi_resonance(body),
                    "abstraction_level": self._calculate_abstraction_level(body)
                }
                patterns.append(pattern)
            
            # Extract class patterns
            class_match = re.search(r"(?:public|private|protected)\s+class\s+([\w_]+)(?:\s+extends\s+([\w_]+))?(?:\s+implements\s+([\w_,\s]+))?\s*\{([^}]*)\}", snippet, re.DOTALL)
            if class_match:
                class_name = class_match.group(1)
                extends = class_match.group(2) if class_match.group(2) else ""
                implements = class_match.group(3) if class_match.group(3) else ""
                body = class_match.group(4).strip()
                
                # Create a pattern entry
                pattern = {
                    "type": "class",
                    "name": class_name,
                    "extends": extends,
                    "implements": implements,
                    "body_template": body,
                    "phi_resonance": self._calculate_phi_resonance(body),
                    "abstraction_level": self._calculate_abstraction_level(body)
                }
                patterns.append(pattern)
        
        # Add unique patterns to knowledge base
        for pattern in patterns:
            if not any(p.get("type") == pattern["type"] and 
                      p.get("name") == pattern["name"] and 
                      p.get("body_template") == pattern["body_template"]
                      for p in self.knowledge_base["code_patterns"].get(language, [])):
                self.knowledge_base["code_patterns"].setdefault(language, []).append(pattern)
    
    def _calculate_phi_resonance(self, text):
        """Calculate phi resonance for any text based on φ-dimensional principles"""
        if not text:
            return self.phi
        
        # Base resonance on text complexity
        text_length = len(text)
        complexity = min(1.0, text_length / 500)
        
        # Count meaningful elements
        word_count = len(text.split())
        unique_chars = len(set(text))
        
        # Calculate resonance based on golden ratio (phi)
        resonance = self.phi * (0.5 + (complexity / 2) + (unique_chars / 256) / 2)
        
        # Add quantum uncertainty
        resonance += random.uniform(-0.05, 0.05)
        
        return max(0.1, min(resonance, 2.0))  # Clamp between 0.1 and 2.0
    
    def _calculate_abstraction_level(self, code_body):
        """Calculate the abstraction level of code based on φ-dimensional principles"""
        # Count meaningful code elements
        lines = code_body.split('\n')
        line_count = len(lines)
        comment_count = sum(1 for line in lines if '#' in line or '//' in line)
        function_call_count = len(re.findall(r'\b[a-zA-Z_][a-zA-Z0-9_]*\s*\(', code_body))
        variable_count = len(set(re.findall(r'\b([a-zA-Z_][a-zA-Z0-9_]*)\s*=', code_body)))
        
        # Calculate abstraction metrics
        comment_ratio = comment_count / max(line_count, 1)
        function_density = function_call_count / max(line_count, 1)
        variable_density = variable_count / max(line_count, 1)
        
        # Apply φ-dimensional scaling
        abstraction_level = (self.phi * comment_ratio + 
                            self.phi ** 2 * function_density + 
                            self.phi ** 0.5 * variable_density) / 3
        
        return min(max(abstraction_level, 0.1), 1.0)  # Normalize between 0.1 and 1.0
    
    def _calculate_concept_resonance(self, concept, context):
        """Calculate phi resonance for a concept"""
        # Base resonance on concept complexity and context richness
        complexity = len(concept) / 10  # Longer concepts are more complex
        richness = min(1.0, len(context) / 500)  # Context richness capped at 1.0
        
        # Calculate resonance based on golden ratio (phi)
        resonance = self.phi * (0.5 + (complexity * richness) / 2)
        
        # Add some quantum uncertainty
        resonance += random.uniform(-0.1, 0.1)
        
        return resonance
    
    def _calculate_equation_resonance(self, equation):
        """Calculate phi resonance for an equation"""
        # Base resonance on equation complexity
        complexity = len(equation) / 20  # Longer equations are more complex
        
        # Check for special symbols that indicate higher complexity
        special_symbols = ['∫', '∂', '∇', '∑', '∏', '√', '^', '_', '\\frac', '\\sqrt']
        symbol_count = sum(1 for symbol in special_symbols if symbol in equation)
        
        # Calculate resonance based on golden ratio (phi)
        resonance = self.phi * (0.6 + (complexity / 3) + (symbol_count / 10))
        
        # Add some quantum uncertainty
        resonance += random.uniform(-0.1, 0.1)
        
        return resonance
    
    def _calculate_theory_resonance(self, theory_name, context):
        """Calculate phi resonance for a theory"""
        # Base resonance on theory name recognition and context richness
        name_length = len(theory_name) / 15  # Normalized name length
        richness = min(1.0, len(context) / 1000)  # Context richness capped at 1.0
        
        # Calculate resonance based on golden ratio (phi)
        resonance = self.phi * (0.7 + (name_length / 4) + (richness / 2))
        
        # Add some quantum uncertainty
        resonance += random.uniform(-0.1, 0.1)
        
        # Default to golden ratio if no resonances found
        return resonance
            
    def get_knowledge_for_query(self, query, max_results=5):
        """Get relevant knowledge for a query"""
        query_lower = query.lower()
        results = []
        
        # Check concepts
        for category, concepts in self.knowledge_base['concepts'].items():
            for concept in concepts:
                if concept['concept'] in query_lower:
                    results.append({
                        'type': 'concept',
                        'category': category,
                        'content': concept['concept'],
                        'context': concept['context'],
                        'phi_resonance': concept.get('phi_resonance', 1.0),  # Use get() to avoid KeyError
                        'relevance': 1.0  # Direct match
                    })
                    
        # Check equations (simple keyword matching)
        if NLP_AVAILABLE:
            equation_keywords = word_tokenize(query_lower)
            stop_words = set(stopwords.words('english'))
            equation_keywords = [word for word in equation_keywords if word not in stop_words]
        else:
            equation_keywords = query_lower.split()
        
        for equation in self.knowledge_base['equations']:
            equation_text = equation['equation'].lower() + ' ' + equation['context'].lower()
            matches = sum(1 for keyword in equation_keywords if keyword in equation_text)
            if matches > 0:
                relevance = matches / len(equation_keywords)
                results.append({
                    'type': 'equation',
                    'content': equation['equation'],
                    'context': equation['context'],
                    'phi_resonance': equation.get('phi_resonance', 1.0),
                    'relevance': relevance
                })
                
        # Check theories
        for theory in self.knowledge_base['theories']:
            theory_text = theory['name'].lower() + ' ' + theory['context'].lower()
            matches = sum(1 for keyword in equation_keywords if keyword in theory_text)
            if matches > 0:
                relevance = matches / len(equation_keywords)
                results.append({
                    'type': 'theory',
                    'content': theory['name'],
                    'context': theory['context'],
                    'phi_resonance': theory.get('phi_resonance', 1.0),
                    'relevance': relevance
                })
                
        # Sort by relevance and phi_resonance
        results.sort(key=lambda x: (x['relevance'], x['phi_resonance']), reverse=True)
        
        # Return top results
        return results[:max_results]

# Example usage
# if __name__ == "__main__":
#     extractor = PhysicsKnowledgeExtractor()
#     extractor.process_all_pdfs()
#     
#     # Test query
#     test_query = "quantum entanglement and consciousness"
#     results = extractor.get_knowledge_for_query(test_query)
#     
#     print(f"\nResults for query: '{test_query}'")
#     for i, result in enumerate(results, 1):
#         print(f"\n{i}. {result['type'].upper()}: {result['content']}")
#         print(f"   Context: {result['context'][:100]}...")
#         print(f"   Phi Resonance: {result['phi_resonance']:.4f}")
#         print(f"   Relevance: {result['relevance']:.4f}")
