import os
import re
import sys

class JanusEngine:
    def __init__(self, src_root, dest_root, mode="auto"):
        self.src_root = src_root
        self.dest_root = dest_root
        self.mode = mode # 'c2j' (C++ to Java) or 'j2c' (Java to C++)
        
        # 1. THE C++ TO JAVA DICTIONARY
        self.cpp_to_java_types = {
            r'\bstd::string\b': 'String',
            r'\bstring\b': 'String',
            r'\bstd::vector<(.+?)>': r'ArrayList<\1>',
            r'\bvector<(.+?)>': r'ArrayList<\1>',
            r'\bstd::cout\s*<<\s*(.*?)\s*<<\s*std::endl': r'System.out.println(\1)',
            r'\bstd::cout\s*<<\s*(.*)': r'System.out.print(\1)',
            r'\bconst\b': 'final',
            r'\bbool\b': 'boolean',
            r'\bauto\b': 'var',
            r'\b#include\s+<iostream>': 'import java.io.*;',
            r'\b#include\s+<vector>': 'import java.util.ArrayList;',
            r'\b#include\s+<string>': '',
            r'\busing namespace std;': '',
            r'->': '.',
            r'::': '.',
            r'\bint main\(\)': 'public static void main(String[] args)'
        }

        # 2. THE JAVA TO C++ DICTIONARY
        self.java_to_cpp_types = {
            r'\bString\b': 'std::string',
            r'\bArrayList<(.+?)>': r'std::vector<\1>',
            r'\bSystem.out.println\((.*)\)': r'std::cout << \1 << std::endl',
            r'\bSystem.out.print\((.*)\)': r'std::cout << \1',
            r'\bboolean\b': 'bool',
            r'\bfinal\b': 'const',
            r'\bimport\s+java.util.*;': '#include <vector>\n#include <algorithm>',
            r'\bimport\s+java.io.*;': '#include <iostream>',
            r'\bpublic static void main\(String\[\] args\)': 'int main()',
            r'\bpackage\s+[\w.]+;': '', # C++ uses namespaces, usually handled manually
            r'\bObject\b': 'void*', # Dangerous but accurate
        }

    def run(self):
        print(f"⚡ JANUS ENGINE ACTIVE: {self.src_root} -> {self.dest_root}")
        
        for root, dirs, files in os.walk(self.src_root):
            for file in files:
                # DETECT MODE
                if file.endswith(".cpp") or file.endswith(".h") or file.endswith(".hpp"):
                    if self.mode in ["auto", "c2j"]:
                        self.transmute_c2j(root, file)
                
                elif file.endswith(".java"):
                    if self.mode in ["auto", "j2c"]:
                        self.transmute_j2c(root, file)

    def get_dest_path(self, root, filename):
        rel_path = os.path.relpath(root, self.src_root)
        dest_folder = os.path.join(self.dest_root, rel_path)
        os.makedirs(dest_folder, exist_ok=True)
        return os.path.join(dest_folder, filename)

    # ==========================================
    # MODE A: C++ (Raw Metal) -> JAVA (JVM)
    # ==========================================
    def transmute_c2j(self, root, filename):
        new_filename = filename.replace(".cpp", ".java").replace(".hpp", ".java").replace(".h", ".java")
        dest_path = self.get_dest_path(root, new_filename)
        src_path = os.path.join(root, filename)
        
        print(f"   🔵 [C++ -> JAVA] {filename} -> {new_filename}")
        
        with open(src_path, 'r', encoding='utf-8') as f:
            code = f.read()

        # 1. Class Wrapper Logic (C++ files often lack a class wrapper)
        class_name = new_filename.replace(".java", "")
        lines = code.split('\n')
        java_lines = []
        
        java_lines.append(f"public class {class_name} {{")
        
        for line in lines:
            # Strip Pointers (The dangerous part)
            if "*" in line and "import" not in line and "//" not in line:
                line = line.replace("*", "") + " // WARN: Pointer stripped";
            
            # Apply Regex Mappings
            for cpp, java in self.cpp_to_java_types.items():
                line = re.sub(cpp, java, line)
            
            # Remove public:/private: sections (Java uses modifiers per line)
            if "public:" in line or "private:" in line or "protected:" in line:
                continue 
                
            java_lines.append("    " + line) # Indent everything into the class
            
        java_lines.append("}")
        
        with open(dest_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(java_lines))

    # ==========================================
    # MODE B: JAVA (JVM) -> C++ (Raw Metal)
    # ==========================================
    def transmute_j2c(self, root, filename):
        new_filename = filename.replace(".java", ".cpp")
        dest_path = self.get_dest_path(root, new_filename)
        src_path = os.path.join(root, filename)
        
        print(f"   🟠 [JAVA -> C++] {filename} -> {new_filename}")
        
        with open(src_path, 'r', encoding='utf-8') as f:
            code = f.read()

        cpp_lines = []
        cpp_lines.append("#include <iostream>")
        cpp_lines.append("#include <vector>")
        cpp_lines.append("#include <string>")
        cpp_lines.append("#include <memory>") # For smart pointers
        cpp_lines.append("using namespace std;")
        cpp_lines.append("")

        lines = code.split('\n')
        
        for line in lines:
            line = line.strip()
            
            # Skip Imports/Package (Handled by includes)
            if line.startswith("import") or line.startswith("package"):
                continue

            # Class Declaration
            if "class " in line:
                line = line.replace("public class", "class")
                line += " { \npublic:" # C++ default is private, Java is package-private
            
            # Apply Regex Mappings
            for java, cpp in self.java_to_cpp_types.items():
                line = re.sub(java, cpp, line)
                
            # New/Delete Logic (Garbage Collection Simulation)
            # Java: MyObj x = new MyObj();
            # C++:  auto x = make_shared<MyObj>();
            if "new " in line:
                # Regex to capture "Type var = new Type(args);"
                match = re.search(r'(\w+)\s+(\w+)\s*=\s*new\s+(\w+)\((.*)\);', line)
                if match:
                    type_name, var_name, const_name, args = match.groups()
                    line = f"std::shared_ptr<{type_name}> {var_name} = std::make_shared<{const_name}>({args});"

            cpp_lines.append(line)

        # Ensure main returns 0
        if "int main()" in '\n'.join(cpp_lines):
             # Find the last brace and insert 'return 0;' before it
             pass 

        with open(dest_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(cpp_lines))

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python janus.py <src_dir> <dest_dir> [c2j|j2c]")
        sys.exit(1)

    src = sys.argv[1]
    dest = sys.argv[2]
    mode = sys.argv[3] if len(sys.argv) > 3 else "auto"
    
    engine = JanusEngine(src, dest, mode)
    engine.run()
