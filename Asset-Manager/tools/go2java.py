#!/usr/bin/env python3
"""
🧬 GO2JAVA TRANSPILER - Gen 132
Single File Application (SFA)

Eats a folder of Go code, excretes a folder of Java code.

Usage:
    python go2java.py <source_folder> <destination_folder>
    python go2java.py ./ollama-main ./fraymus-absorbed

Features:
    - Recursive folder walking
    - Package structure mirroring
    - Type mapping (string → String, int64 → long, etc.)
    - Struct → Inner Class conversion
    - Function signature translation
    - Variable declaration conversion (:= → var)

Architect's Note:
    This is the CHASSIS. Complex Go patterns (channels, goroutines, select)
    need the PhilosophersStone AI refinement pass.

"The bridge between worlds."
"""

import os
import re
import sys

class GoTranspiler:
    def __init__(self, src_root, dest_root, base_package="fraymus.absorbed"):
        self.src_root = src_root
        self.dest_root = dest_root
        self.base_package = base_package
        self.files_converted = 0
        self.structs_found = 0
        self.functions_found = 0
        
        # 1. THE DICTIONARY (Regex Mappings)
        self.type_map = {
            r'\bstring\b': 'String',
            r'\bint\b': 'int',
            r'\bint8\b': 'byte',
            r'\bint16\b': 'short',
            r'\bint32\b': 'int',
            r'\bint64\b': 'long',
            r'\buint\b': 'int',
            r'\buint8\b': 'byte',
            r'\buint16\b': 'short',
            r'\buint32\b': 'int',
            r'\buint64\b': 'long',
            r'\bfloat32\b': 'float',
            r'\bfloat64\b': 'double',
            r'\bbool\b': 'boolean',
            r'\bbyte\b': 'byte',
            r'\brune\b': 'char',
            r'\berror\b': 'Exception',
            r'\binterface\{\}': 'Object',
            r'\bany\b': 'Object',
            r'\b\[\]byte\b': 'byte[]',
            r'\b\[\]string\b': 'String[]',
            r'\b\[\]int\b': 'int[]',
            r'\bmap\[string\]string\b': 'Map<String, String>',
            r'\bmap\[string\]interface\{\}\b': 'Map<String, Object>',
            r'\bmap\[string\]any\b': 'Map<String, Object>',
            r'\bnil\b': 'null',
            r'\btrue\b': 'true',
            r'\bfalse\b': 'false',
        }
        
        # Function call mappings
        self.func_map = {
            r'fmt\.Println\(': 'System.out.println(',
            r'fmt\.Printf\(': 'System.out.printf(',
            r'fmt\.Sprintf\(': 'String.format(',
            r'fmt\.Errorf\(': 'new Exception(String.format(',
            r'len\((\w+)\)': r'\1.length',
            r'append\((\w+),\s*(.+)\)': r'\1.add(\2)',
            r'make\(\[\](\w+),\s*(\d+)\)': r'new ArrayList<>()',
            r'make\(map\[(\w+)\](\w+)\)': r'new HashMap<>()',
            r'strings\.Contains\(': 'contains(',
            r'strings\.HasPrefix\(': 'startsWith(',
            r'strings\.HasSuffix\(': 'endsWith(',
            r'strings\.Split\(': 'split(',
            r'strings\.Join\(': 'String.join(',
            r'strings\.ToLower\(': 'toLowerCase(',
            r'strings\.ToUpper\(': 'toUpperCase(',
            r'strings\.TrimSpace\(': 'trim(',
            r'strconv\.Atoi\(': 'Integer.parseInt(',
            r'strconv\.Itoa\(': 'String.valueOf(',
            r'time\.Now\(\)': 'System.currentTimeMillis()',
            r'time\.Sleep\(': 'Thread.sleep(',
            r'os\.Getenv\(': 'System.getenv(',
            r'os\.Exit\(': 'System.exit(',
            r'panic\(': 'throw new RuntimeException(',
        }

    def run(self):
        print("╔═══════════════════════════════════════════════════════════════╗")
        print("║  🧬 GO2JAVA TRANSPILER - Gen 132                              ║")
        print("║  Single File Application                                      ║")
        print("╚═══════════════════════════════════════════════════════════════╝")
        print()
        print(f"⚡ SOURCE:      {self.src_root}")
        print(f"⚡ DESTINATION: {self.dest_root}")
        print(f"⚡ PACKAGE:     {self.base_package}")
        print()
        
        for root, dirs, files in os.walk(self.src_root):
            # Skip hidden and test directories
            dirs[:] = [d for d in dirs if not d.startswith('.') and d != 'testdata']
            
            for file in files:
                if file.endswith(".go") and not file.endswith("_test.go"):
                    go_path = os.path.join(root, file)
                    
                    # Calculate relative path for package structure
                    rel_path = os.path.relpath(root, self.src_root)
                    if rel_path == ".":
                        java_package = self.base_package
                        dest_folder = os.path.join(self.dest_root, self.base_package.replace(".", "/"))
                    else:
                        # Clean package name (remove dashes, special chars)
                        clean_path = rel_path.replace("-", "_").replace(" ", "_")
                        java_package = f"{self.base_package}.{clean_path.replace(os.sep, '.')}"
                        dest_folder = os.path.join(self.dest_root, self.base_package.replace(".", "/"), clean_path)

                    os.makedirs(dest_folder, exist_ok=True)
                    
                    # Convert filename (main.go -> Main.java, types.go -> Types.java)
                    base_name = file.replace(".go", "")
                    class_name = self.to_class_name(base_name)
                    java_filename = class_name + ".java"
                    dest_path = os.path.join(dest_folder, java_filename)
                    
                    self.transmute_file(go_path, dest_path, java_package, class_name)

        print()
        print("╔═══════════════════════════════════════════════════════════════╗")
        print(f"║  ✅ TRANSMUTATION COMPLETE                                    ║")
        print(f"║     Files converted: {self.files_converted:4d}                                  ║")
        print(f"║     Structs found:   {self.structs_found:4d}                                  ║")
        print(f"║     Functions found: {self.functions_found:4d}                                  ║")
        print("╚═══════════════════════════════════════════════════════════════╝")

    def to_class_name(self, name):
        """Convert snake_case or lowercase to PascalCase"""
        parts = name.replace("-", "_").split("_")
        return "".join(p.capitalize() for p in parts)

    def transmute_file(self, go_path, dest_path, package_name, class_name):
        print(f"   ⚗️  {os.path.basename(go_path)} → {class_name}.java")
        self.files_converted += 1
        
        with open(go_path, 'r', encoding='utf-8', errors='ignore') as f:
            go_code = f.read()

        java_lines = []
        java_lines.append(f"package {package_name};")
        java_lines.append("")
        java_lines.append("import java.util.*;")
        java_lines.append("import java.util.concurrent.*;")
        java_lines.append("import java.io.*;")
        java_lines.append("import java.nio.*;")
        java_lines.append("import java.nio.file.*;")
        java_lines.append("")
        java_lines.append("/**")
        java_lines.append(f" * Transpiled from: {os.path.basename(go_path)}")
        java_lines.append(" * Generated by go2java.py (Gen 132)")
        java_lines.append(" */")
        java_lines.append(f"public class {class_name} {{")

        # 2. THE PARSING ENGINE
        lines = go_code.split('\n')
        in_struct = False
        in_interface = False
        in_func = False
        brace_depth = 0
        current_struct = None
        
        i = 0
        while i < len(lines):
            line = lines[i]
            stripped = line.strip()
            i += 1
            
            # Skip package declaration and imports
            if stripped.startswith("package ") or stripped.startswith("import"):
                continue
            if stripped == "import (" or stripped == ")":
                continue
            if stripped.startswith('"') and stripped.endswith('"'):
                continue  # Import line
                
            # Skip blank lines and comments (preserve some structure)
            if not stripped:
                if not in_struct and not in_interface:
                    java_lines.append("")
                continue
            if stripped.startswith("//"):
                java_lines.append(f"    {stripped}")
                continue

            # DETECT: Interface definitions
            interface_match = re.search(r'type\s+(\w+)\s+interface\s*\{', stripped)
            if interface_match:
                interface_name = interface_match.group(1)
                java_lines.append(f"\n    public interface {interface_name} {{")
                in_interface = True
                continue
            
            # DETECT: Structs -> Inner Classes
            struct_match = re.search(r'type\s+(\w+)\s+struct\s*\{', stripped)
            if struct_match:
                struct_name = struct_match.group(1)
                java_lines.append(f"\n    public static class {struct_name} {{")
                in_struct = True
                current_struct = struct_name
                self.structs_found += 1
                continue
            
            # DETECT: Type aliases
            type_alias = re.search(r'type\s+(\w+)\s+(\w+)$', stripped)
            if type_alias and not in_struct:
                alias_name = type_alias.group(1)
                base_type = self.map_type(type_alias.group(2))
                java_lines.append(f"\n    // Type alias: {alias_name} = {base_type}")
                continue
            
            # Close struct/interface
            if (in_struct or in_interface) and stripped == "}":
                java_lines.append("    }")
                in_struct = False
                in_interface = False
                current_struct = None
                continue

            # DETECT: Interface methods
            if in_interface:
                method_match = re.search(r'(\w+)\((.*?)\)\s*(\(.*?\)|\w+)?', stripped)
                if method_match:
                    method_name = method_match.group(1)
                    args_raw = method_match.group(2)
                    return_raw = method_match.group(3) or ""
                    
                    java_args = self.convert_args(args_raw)
                    java_return = self.convert_return_type(return_raw)
                    
                    java_lines.append(f"        {java_return} {method_name}({java_args});")
                continue

            # DETECT: Struct Fields
            if in_struct:
                field_match = re.search(r'(\w+)\s+(\S+)(\s+`.*`)?', stripped)
                if field_match:
                    name = field_match.group(1)
                    go_type = field_match.group(2)
                    java_type = self.map_type(go_type)
                    java_lines.append(f"        public {java_type} {name};")
                continue

            # DETECT: Method with receiver (Go method on struct)
            method_match = re.search(r'func\s+\((\w+)\s+\*?(\w+)\)\s+(\w+)\((.*?)\)\s*(\(.*?\)|\w+)?\s*\{', stripped)
            if method_match:
                receiver_name = method_match.group(1)
                receiver_type = method_match.group(2)
                method_name = method_match.group(3)
                args_raw = method_match.group(4)
                return_raw = method_match.group(5) or ""
                
                java_args = self.convert_args(args_raw)
                java_return = self.convert_return_type(return_raw)
                
                java_lines.append(f"\n    // Method on {receiver_type}")
                java_lines.append(f"    public {java_return} {method_name}({java_args}) {{")
                in_func = True
                brace_depth = 1
                self.functions_found += 1
                continue

            # DETECT: Main Function
            if "func main()" in stripped:
                java_lines.append("\n    public static void main(String[] args) throws Exception {")
                in_func = True
                brace_depth = 1
                self.functions_found += 1
                continue

            # DETECT: init function
            if "func init()" in stripped:
                java_lines.append("\n    static {")
                java_lines.append("        // Go init() function")
                in_func = True
                brace_depth = 1
                continue

            # DETECT: Standard Functions
            func_match = re.search(r'func\s+(\w+)\((.*?)\)\s*(\(.*?\)|\w+)?\s*\{', stripped)
            if func_match:
                func_name = func_match.group(1)
                args_raw = func_match.group(2)
                return_raw = func_match.group(3) or ""
                
                java_args = self.convert_args(args_raw)
                java_return = self.convert_return_type(return_raw)
                
                java_lines.append(f"\n    public static {java_return} {func_name}({java_args}) throws Exception {{")
                in_func = True
                brace_depth = 1
                self.functions_found += 1
                continue

            # Track brace depth in functions
            if in_func:
                brace_depth += stripped.count('{') - stripped.count('}')
                
                if brace_depth <= 0:
                    java_lines.append("    }")
                    in_func = False
                    brace_depth = 0
                    continue

            # GENERAL LOGIC TRANSLATION
            if in_func or True:  # Translate all remaining lines
                translated_line = self.translate_logic(stripped)
                if translated_line:
                    indent = "        " if in_func else "    "
                    java_lines.append(f"{indent}{translated_line}")

        java_lines.append("}") # Close Class

        with open(dest_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(java_lines))

    def map_type(self, go_type):
        if not go_type:
            return "void"
        
        # Handle pointers
        go_type = go_type.lstrip('*')
        
        # Handle slices
        if go_type.startswith("[]"):
            inner = go_type[2:]
            inner_java = self.map_type(inner)
            return f"List<{self.box_type(inner_java)}>"
        
        # Handle maps
        map_match = re.match(r'map\[(\w+)\](\w+)', go_type)
        if map_match:
            key = self.map_type(map_match.group(1))
            val = self.map_type(map_match.group(2))
            return f"Map<{self.box_type(key)}, {self.box_type(val)}>"
        
        # Apply regex mappings
        for go, java in self.type_map.items():
            if re.fullmatch(go.replace(r'\b', ''), go_type):
                return java
        
        return go_type  # Return as-is (likely a custom type)

    def box_type(self, t):
        """Convert primitive types to boxed versions for generics"""
        box_map = {
            'int': 'Integer',
            'long': 'Long',
            'double': 'Double',
            'float': 'Float',
            'boolean': 'Boolean',
            'byte': 'Byte',
            'short': 'Short',
            'char': 'Character',
        }
        return box_map.get(t, t)

    def convert_return_type(self, return_raw):
        if not return_raw:
            return "void"
        
        return_raw = return_raw.strip()
        
        # Multiple return values: (int, error) -> int /* error */
        if return_raw.startswith("(") and return_raw.endswith(")"):
            types = return_raw[1:-1].split(",")
            if len(types) >= 1:
                return self.map_type(types[0].strip())
        
        return self.map_type(return_raw)

    def convert_args(self, args_raw):
        if not args_raw or not args_raw.strip():
            return ""
        
        # Go: "a int, b string" -> Java: "int a, String b"
        # Go: "a, b int" -> Java: "int a, int b"
        parts = args_raw.split(',')
        java_args = []
        last_type = "Object"
        
        # Process in reverse to handle "a, b int" pattern
        processed = []
        for part in reversed(parts):
            p = part.strip().split()
            if len(p) == 2:
                last_type = self.map_type(p[1])
                processed.insert(0, (p[0], last_type))
            elif len(p) == 1:
                processed.insert(0, (p[0], last_type))
        
        for name, jtype in processed:
            java_args.append(f"{jtype} {name}")
        
        return ", ".join(java_args)

    def translate_logic(self, line):
        original = line
        
        # Skip certain Go-specific constructs
        if line.startswith("go "):
            return f"// TODO: goroutine - {line}"
        if "<-" in line:
            return f"// TODO: channel - {line}"
        if "select {" in line:
            return "// TODO: select statement"
        if "defer " in line:
            return f"// TODO: defer - {line}"
        
        # Apply function call mappings
        for go, java in self.func_map.items():
            line = re.sub(go, java, line)
        
        # Apply type mappings
        for go, java in self.type_map.items():
            line = re.sub(go, java, line)
        
        # Variable declaration ":="
        if ":=" in line:
            line = line.replace(":=", "=")
            # Prepend 'var' for local type inference (Java 10+)
            if not line.strip().startswith("var "):
                line = "var " + line.strip()
        
        # Range loops: "for i, v := range items" -> "for (var item : items)"
        range_match = re.search(r'for\s+(\w+)\s*,?\s*(\w+)?\s*:?=\s*range\s+(\w+)', line)
        if range_match:
            idx = range_match.group(1)
            val = range_match.group(2)
            collection = range_match.group(3)
            if val:
                line = f"for (var {val} : {collection}) {{ // index: {idx}"
            else:
                line = f"for (var {idx} : {collection}) {{"
        
        # Regular for loops: "for i := 0; i < n; i++" -> "for (int i = 0; i < n; i++)"
        for_match = re.search(r'for\s+(\w+)\s*:=\s*(\d+);\s*(\w+)\s*([<>=!]+)\s*(\w+);\s*(\w+)([\+\-]+)', line)
        if for_match:
            var = for_match.group(1)
            init = for_match.group(2)
            cond_var = for_match.group(3)
            op = for_match.group(4)
            limit = for_match.group(5)
            inc_var = for_match.group(6)
            inc_op = for_match.group(7)
            line = f"for (int {var} = {init}; {cond_var} {op} {limit}; {inc_var}{inc_op}) {{"
        
        # Infinite for loop: "for {" -> "while (true) {"
        if re.match(r'^\s*for\s*\{', line):
            line = "while (true) {"
        
        # If err != nil -> if (err != null)
        line = re.sub(r'if\s+err\s*!=\s*nil', 'if (err != null)', line)
        line = re.sub(r'if\s+(\w+)\s*!=\s*nil', r'if (\1 != null)', line)
        line = re.sub(r'if\s+(\w+)\s*==\s*nil', r'if (\1 == null)', line)
        
        # Add parentheses to if statements
        if_match = re.match(r'^if\s+(.+?)\s*\{', line)
        if if_match and '(' not in if_match.group(1):
            cond = if_match.group(1)
            line = f"if ({cond}) {{"
        
        # else if -> } else if
        line = re.sub(r'\}\s*else\s+if\s+(.+?)\s*\{', r'} else if (\1) {', line)
        
        # Add semicolons
        if line and not line.rstrip().endswith((';', '{', '}', ',')):
            if not line.strip().startswith(('if', 'for', 'while', 'switch', '//')):
                line = line.rstrip() + ';'
        
        return line


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("╔═══════════════════════════════════════════════════════════════╗")
        print("║  🧬 GO2JAVA TRANSPILER - Gen 132                              ║")
        print("╚═══════════════════════════════════════════════════════════════╝")
        print()
        print("Usage: python go2java.py <source_folder> <destination_folder> [package]")
        print()
        print("Examples:")
        print("  python go2java.py ./ollama-main ./fraymus-absorbed")
        print("  python go2java.py ./ollama-main ./output fraymus.ollama")
        print()
        sys.exit(1)

    src = sys.argv[1]
    dest = sys.argv[2]
    package = sys.argv[3] if len(sys.argv) > 3 else "fraymus.absorbed"
    
    if not os.path.exists(src):
        print(f"⚠️ Source folder not found: {src}")
        sys.exit(1)
    
    converter = GoTranspiler(src, dest, package)
    converter.run()
