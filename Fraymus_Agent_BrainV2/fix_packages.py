import os
import glob

folder = r'd:\Zip And Send\Java-Memory\Fraymus_Agent_BrainV2\src\main\java\fraymus\CODE_Generator_UI_Backup\java_repl'
count = 0

for filepath in glob.glob(os.path.join(folder, '*.java')):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if 'package repl;' in content:
        new_content = content.replace('package repl;', 'package fraymus.CODE_Generator_UI_Backup.java_repl;')
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        count += 1
        print(f"Fixed: {os.path.basename(filepath)}")

print(f"\nTotal files fixed: {count}")
