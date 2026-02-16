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
    
    def process_all_pdfs(self, force_reprocess=False):
        """Process all PDFs in the directory and extract physics knowledge"""
        # Check if we need to process PDFs
        if not force_reprocess and os.path.exists(self.knowledge_base_path):
            # Knowledge base already exists, no need to reprocess
            return self.knowledge_base
        
        # Get all PDF files
        pdf_paths = []
        
        # Check learning_docs directory first
        if self.learning_docs_dir and os.path.exists(self.learning_docs_dir):
            pdf_paths.extend(glob.glob(os.path.join(self.learning_docs_dir, "**", "*.pdf"), recursive=True))
            
        # Then check the default PDF directory
        if os.path.exists(self.pdf_dir):
            pdf_paths.extend(glob.glob(os.path.join(self.pdf_dir, "**", "*.pdf"), recursive=True))
        
        if not pdf_paths:
            print("No PDF files found.")
            return self.knowledge_base
        
        print(f"Found {len(pdf_paths)} PDF files to process.")
        
        # Process each PDF
        for pdf_path in pdf_paths:
            try:
                self._process_pdf(pdf_path)
            except Exception as e:
                print(f"Error processing PDF {pdf_path}: {e}")
        
        # Save the updated knowledge base
        self._save_knowledge_base()
        
        return self.knowledge_base
    
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
                
                # Check if this equation already exists
                exists = False
                for equation in self.knowledge_base["equations"]:
                    if equation["equation"] == equation_text:
                        exists = True
                        break
                
                # Add new equation if it doesn't exist
                if not exists:
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
