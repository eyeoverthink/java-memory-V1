#!/usr/bin/env python3
"""
🧬 JANUS ENGINE - Gen 138
Bidirectional C++ ↔ Java Transpiler

The two-faced god who looks both ways:
    C++ (Raw Metal) → Java (JVM)
    Java (JVM) → C++ (Raw Metal)

Usage:
    python janus.py <src_dir> <dest_dir> [c2j|j2c|auto]

Examples:
    python janus.py ./cpp_libs ./java_absorbed c2j
    python janus.py ./fraymus_java ./fraymus_cpp j2c
    python janus.py ./mixed_code ./output auto

Features:
    - Smart pointer generation (shared_ptr for GC simulation)
    - STL ↔ Java Collections mapping
    - Pointer stripping with warnings
    - Namespace ↔ Package conversion
    - Preserves folder structure

"I speak both tongues. High and Low."
"""

import os
import re
import sys

class JanusEngine:
    def __init__(self, src_root, dest_root, mode="auto", namespace="fraymus"):
        self.src_root = src_root
        self.dest_root = dest_root
        self.mode = mode  # 'c2j' (C++ to Java), 'j2c' (Java to C++), 'auto'
        self.namespace = namespace
        
        self.files_converted = 0
        self.classes_found = 0
        self.functions_found = 0
        
        # ═══════════════════════════════════════════════════════════════════
        # 1. THE C++ TO JAVA DICTIONARY
        # ═══════════════════════════════════════════════════════════════════
        self.cpp_to_java_types = {
            # STL Types
            r'\bstd::string\b': 'String',
            r'\bstring\b': 'String',
            r'\bstd::vector<(.+?)>': r'ArrayList<\1>',
            r'\bvector<(.+?)>': r'ArrayList<\1>',
            r'\bstd::map<(.+?),\s*(.+?)>': r'HashMap<\1, \2>',
            r'\bmap<(.+?),\s*(.+?)>': r'HashMap<\1, \2>',
            r'\bstd::set<(.+?)>': r'HashSet<\1>',
            r'\bstd::unordered_map<(.+?),\s*(.+?)>': r'HashMap<\1, \2>',
            r'\bstd::unique_ptr<(.+?)>': r'\1',
            r'\bstd::shared_ptr<(.+?)>': r'\1',
            r'\bstd::optional<(.+?)>': r'Optional<\1>',
            
            # Primitive Types
            r'\bbool\b': 'boolean',
            r'\bconst\b': 'final',
            r'\bauto\b': 'var',
            r'\bsize_t\b': 'int',
            r'\buint8_t\b': 'byte',
            r'\buint16_t\b': 'short',
            r'\buint32_t\b': 'int',
            r'\buint64_t\b': 'long',
            r'\bint8_t\b': 'byte',
            r'\bint16_t\b': 'short',
            r'\bint32_t\b': 'int',
            r'\bint64_t\b': 'long',
            r'\bfloat\b': 'float',
            r'\bdouble\b': 'double',
            r'\bvoid\*': 'Object',
            r'\bnullptr\b': 'null',
            r'\bNULL\b': 'null',
            
            # I/O
            r'\bstd::cout\s*<<\s*(.+?)\s*<<\s*std::endl\s*;': r'System.out.println(\1);',
            r'\bstd::cout\s*<<\s*(.+?)\s*;': r'System.out.print(\1);',
            r'\bstd::cerr\s*<<\s*(.+?)\s*<<\s*std::endl\s*;': r'System.err.println(\1);',
            r'\bstd::cin\s*>>\s*(\w+)\s*;': r'\1 = scanner.nextLine();',
            r'\bprintf\(': 'System.out.printf(',
            
            # Preprocessor
            r'#include\s*<iostream>': 'import java.io.*;',
            r'#include\s*<vector>': 'import java.util.ArrayList;',
            r'#include\s*<map>': 'import java.util.HashMap;',
            r'#include\s*<set>': 'import java.util.HashSet;',
            r'#include\s*<string>': '',
            r'#include\s*<memory>': '',
            r'#include\s*<algorithm>': 'import java.util.Collections;',
            r'#include\s*<cmath>': 'import static java.lang.Math.*;',
            r'#include\s*<fstream>': 'import java.nio.file.*;',
            r'#include\s*".*?"': '',  # Local includes
            r'#pragma\s+.*': '',
            r'#ifndef\s+.*': '',
            r'#define\s+.*': '',
            r'#endif.*': '',
            r'using namespace std;': '',
            
            # Operators
            r'->': '.',
            r'::': '.',
            
            # Main function
            r'\bint\s+main\s*\(\s*\)': 'public static void main(String[] args)',
            r'\bint\s+main\s*\(\s*int\s+argc\s*,\s*char\s*\*\s*argv\s*\[\s*\]\s*\)': 'public static void main(String[] args)',
        }

        # ═══════════════════════════════════════════════════════════════════
        # 2. THE JAVA TO C++ DICTIONARY
        # ═══════════════════════════════════════════════════════════════════
        self.java_to_cpp_types = {
            # Collections
            r'\bString\b': 'std::string',
            r'\bArrayList<(.+?)>': r'std::vector<\1>',
            r'\bList<(.+?)>': r'std::vector<\1>',
            r'\bHashMap<(.+?),\s*(.+?)>': r'std::unordered_map<\1, \2>',
            r'\bMap<(.+?),\s*(.+?)>': r'std::map<\1, \2>',
            r'\bHashSet<(.+?)>': r'std::unordered_set<\1>',
            r'\bSet<(.+?)>': r'std::set<\1>',
            r'\bOptional<(.+?)>': r'std::optional<\1>',
            
            # Primitive Types
            r'\bboolean\b': 'bool',
            r'\bfinal\b': 'const',
            r'\bInteger\b': 'int',
            r'\bLong\b': 'long',
            r'\bDouble\b': 'double',
            r'\bFloat\b': 'float',
            r'\bByte\b': 'uint8_t',
            r'\bObject\b': 'void*',
            r'\bnull\b': 'nullptr',
            
            # I/O
            r'\bSystem\.out\.println\((.*?)\)': r'std::cout << \1 << std::endl',
            r'\bSystem\.out\.print\((.*?)\)': r'std::cout << \1',
            r'\bSystem\.err\.println\((.*?)\)': r'std::cerr << \1 << std::endl',
            r'\bSystem\.out\.printf\(': 'printf(',
            
            # Imports/Package
            r'import\s+java\.util\.\*;': '#include <vector>\n#include <map>\n#include <set>\n#include <algorithm>',
            r'import\s+java\.io\.\*;': '#include <iostream>\n#include <fstream>',
            r'import\s+java\.nio\.file\.\*;': '#include <filesystem>',
            r'import\s+[\w.]+;': '',
            r'package\s+[\w.]+;': '',
            
            # Main function
            r'public\s+static\s+void\s+main\s*\(\s*String\s*\[\s*\]\s*args\s*\)': 'int main(int argc, char* argv[])',
            
            # Exceptions
            r'\bthrow\s+new\s+(\w+)\((.*?)\)': r'throw std::runtime_error(\2)',
            r'\btry\s*\{': 'try {',
            r'\bcatch\s*\(\s*(\w+)\s+(\w+)\s*\)': r'catch (const std::exception& \2)',
        }

    def run(self):
        print("╔═══════════════════════════════════════════════════════════════╗")
        print("║  🧬 JANUS ENGINE - Gen 138                                    ║")
        print("║  Bidirectional C++ ↔ Java Transpiler                          ║")
        print("╚═══════════════════════════════════════════════════════════════╝")
        print()
        print(f"⚡ SOURCE:      {self.src_root}")
        print(f"⚡ DESTINATION: {self.dest_root}")
        print(f"⚡ MODE:        {self.mode}")
        print()
        
        for root, dirs, files in os.walk(self.src_root):
            # Skip hidden directories
            dirs[:] = [d for d in dirs if not d.startswith('.')]
            
            for file in files:
                # C++ to Java
                if file.endswith((".cpp", ".cc", ".cxx", ".h", ".hpp", ".hxx")):
                    if self.mode in ["auto", "c2j"]:
                        self.transmute_c2j(root, file)
                
                # Java to C++
                elif file.endswith(".java"):
                    if self.mode in ["auto", "j2c"]:
                        self.transmute_j2c(root, file)

        print()
        print("╔═══════════════════════════════════════════════════════════════╗")
        print(f"║  ✅ JANUS TRANSFORMATION COMPLETE                             ║")
        print(f"║     Files converted: {self.files_converted:4d}                                  ║")
        print(f"║     Classes found:   {self.classes_found:4d}                                  ║")
        print(f"║     Functions found: {self.functions_found:4d}                                  ║")
        print("╚═══════════════════════════════════════════════════════════════╝")

    def get_dest_path(self, root, filename):
        rel_path = os.path.relpath(root, self.src_root)
        dest_folder = os.path.join(self.dest_root, rel_path)
        os.makedirs(dest_folder, exist_ok=True)
        return os.path.join(dest_folder, filename)

    def to_class_name(self, name):
        """Convert filename to PascalCase class name"""
        name = re.sub(r'[^a-zA-Z0-9_]', '_', name)
        parts = name.split('_')
        return ''.join(p.capitalize() for p in parts if p)

    # ═══════════════════════════════════════════════════════════════════════
    # MODE A: C++ (Raw Metal) → JAVA (JVM)
    # ═══════════════════════════════════════════════════════════════════════
    def transmute_c2j(self, root, filename):
        base_name = re.sub(r'\.(cpp|cc|cxx|h|hpp|hxx)$', '', filename)
        class_name = self.to_class_name(base_name)
        new_filename = class_name + ".java"
        dest_path = self.get_dest_path(root, new_filename)
        src_path = os.path.join(root, filename)
        
        print(f"   🔵 [C++ → Java] {filename} → {new_filename}")
        self.files_converted += 1
        
        try:
            with open(src_path, 'r', encoding='utf-8', errors='ignore') as f:
                code = f.read()
        except Exception as e:
            print(f"      ⚠️ Error reading: {e}")
            return

        java_lines = []
        java_lines.append(f"package {self.namespace};")
        java_lines.append("")
        java_lines.append("import java.util.*;")
        java_lines.append("import java.io.*;")
        java_lines.append("")
        java_lines.append("/**")
        java_lines.append(f" * Transpiled from C++: {filename}")
        java_lines.append(" * Generated by Janus Engine (Gen 138)")
        java_lines.append(" */")
        java_lines.append(f"public class {class_name} {{")

        lines = code.split('\n')
        in_class = False
        brace_depth = 0
        
        for line in lines:
            original = line
            stripped = line.strip()
            
            # Skip preprocessor (already handled)
            if stripped.startswith('#'):
                # Apply mappings to #include
                for cpp, java in self.cpp_to_java_types.items():
                    line = re.sub(cpp, java, line)
                if line.strip():
                    java_lines.insert(3, line.strip())
                continue
            
            # Skip using namespace
            if 'using namespace' in stripped:
                continue
            
            # Detect C++ class
            class_match = re.search(r'\bclass\s+(\w+)', stripped)
            if class_match:
                cpp_class = class_match.group(1)
                self.classes_found += 1
                java_lines.append(f"\n    public static class {cpp_class} {{")
                in_class = True
                continue
            
            # Skip access specifiers
            if stripped in ['public:', 'private:', 'protected:']:
                continue
            
            # Detect functions
            func_match = re.search(r'(\w+)\s+(\w+)\s*\(([^)]*)\)\s*\{?', stripped)
            if func_match and '{' in stripped:
                self.functions_found += 1
            
            # Strip pointers with warning
            if '*' in line and '//' not in line and '#' not in line:
                line = line.replace('*', '')
                line = line.rstrip() + ' // JANUS: pointer stripped'
            
            # Apply type mappings
            for cpp, java in self.cpp_to_java_types.items():
                line = re.sub(cpp, java, line)
            
            # Remove & references
            line = re.sub(r'&(\w+)', r'\1', line)
            
            # Convert for loops: for (int i = 0; i < n; i++)
            # Already valid in Java, just clean up
            
            # Add to output
            indent = "        " if in_class else "    "
            java_lines.append(f"{indent}{line.strip()}")
            
            # Track braces for class closing
            brace_depth += stripped.count('{') - stripped.count('}')
            if in_class and brace_depth <= 0:
                in_class = False
                brace_depth = 0

        java_lines.append("}")  # Close main class

        with open(dest_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(java_lines))

    # ═══════════════════════════════════════════════════════════════════════
    # MODE B: JAVA (JVM) → C++ (Raw Metal)
    # ═══════════════════════════════════════════════════════════════════════
    def transmute_j2c(self, root, filename):
        new_filename = filename.replace(".java", ".cpp")
        header_filename = filename.replace(".java", ".h")
        dest_path = self.get_dest_path(root, new_filename)
        header_path = self.get_dest_path(root, header_filename)
        src_path = os.path.join(root, filename)
        
        print(f"   🟠 [Java → C++] {filename} → {new_filename}")
        self.files_converted += 1
        
        try:
            with open(src_path, 'r', encoding='utf-8', errors='ignore') as f:
                code = f.read()
        except Exception as e:
            print(f"      ⚠️ Error reading: {e}")
            return

        cpp_lines = []
        header_lines = []
        
        # Header file
        guard = filename.replace(".java", "_H").upper()
        header_lines.append(f"#ifndef {guard}")
        header_lines.append(f"#define {guard}")
        header_lines.append("")
        header_lines.append("#include <iostream>")
        header_lines.append("#include <vector>")
        header_lines.append("#include <map>")
        header_lines.append("#include <string>")
        header_lines.append("#include <memory>")
        header_lines.append("#include <optional>")
        header_lines.append("")
        
        # Source file
        cpp_lines.append(f'#include "{header_filename}"')
        cpp_lines.append("")
        cpp_lines.append("using namespace std;")
        cpp_lines.append("")

        lines = code.split('\n')
        current_class = None
        
        for line in lines:
            stripped = line.strip()
            
            # Skip imports/package
            if stripped.startswith('import') or stripped.startswith('package'):
                continue
            
            # Detect class
            class_match = re.search(r'(?:public\s+)?class\s+(\w+)', stripped)
            if class_match:
                current_class = class_match.group(1)
                self.classes_found += 1
                header_lines.append(f"class {current_class} {{")
                header_lines.append("public:")
                continue
            
            # Apply type mappings
            for java, cpp in self.java_to_cpp_types.items():
                line = re.sub(java, cpp, line)
            
            # Handle 'new' keyword -> smart pointers
            new_match = re.search(r'(\w+)\s+(\w+)\s*=\s*new\s+(\w+)\((.*?)\);', line)
            if new_match:
                type_name, var_name, const_name, args = new_match.groups()
                cpp_type = self.apply_mappings(type_name, self.java_to_cpp_types)
                line = f"auto {var_name} = std::make_shared<{cpp_type}>({args});"
            
            # Handle ArrayList/List initialization
            list_match = re.search(r'(\w+)<(.+?)>\s+(\w+)\s*=\s*new\s+\w+<.*?>\(\);', line)
            if list_match:
                coll_type, inner, var_name = list_match.groups()
                if 'ArrayList' in coll_type or 'List' in coll_type:
                    cpp_inner = self.apply_mappings(inner, self.java_to_cpp_types)
                    line = f"std::vector<{cpp_inner}> {var_name};"
            
            # Detect methods
            method_match = re.search(r'(?:public|private|protected)?\s*(?:static)?\s*(\w+)\s+(\w+)\s*\(([^)]*)\)', stripped)
            if method_match:
                self.functions_found += 1
            
            # Add to output
            if current_class:
                header_lines.append(f"    {stripped}")
            else:
                cpp_lines.append(line)

        header_lines.append("};")
        header_lines.append("")
        header_lines.append(f"#endif // {guard}")

        # Write files
        with open(dest_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(cpp_lines))
        
        with open(header_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(header_lines))

    def apply_mappings(self, text, mappings):
        for pattern, replacement in mappings.items():
            text = re.sub(pattern, replacement, text)
        return text


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("╔═══════════════════════════════════════════════════════════════╗")
        print("║  🧬 JANUS ENGINE - Gen 138                                    ║")
        print("║  Bidirectional C++ ↔ Java Transpiler                          ║")
        print("╚═══════════════════════════════════════════════════════════════╝")
        print()
        print("Usage: python janus.py <src_dir> <dest_dir> [mode] [namespace]")
        print()
        print("Modes:")
        print("  c2j   - C++ to Java only")
        print("  j2c   - Java to C++ only")
        print("  auto  - Detect based on file extension (default)")
        print()
        print("Examples:")
        print("  python janus.py ./cpp_libs ./java_absorbed c2j")
        print("  python janus.py ./fraymus_java ./fraymus_cpp j2c")
        print("  python janus.py ./mixed_code ./output auto fraymus.native")
        print()
        sys.exit(1)

    src = sys.argv[1]
    dest = sys.argv[2]
    mode = sys.argv[3] if len(sys.argv) > 3 else "auto"
    namespace = sys.argv[4] if len(sys.argv) > 4 else "fraymus.native"
    
    if not os.path.exists(src):
        print(f"⚠️ Source folder not found: {src}")
        sys.exit(1)
    
    engine = JanusEngine(src, dest, mode, namespace)
    engine.run()
