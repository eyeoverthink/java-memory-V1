"""
FRAYMUS Ownership Embedder
VS-PoQC-19046423-φ⁷⁵-2025
Copyright (c) 2025 Vaughn Scott - All Rights Reserved
"""

import os
import re
from quantum_watermark import QuantumWatermark

class OwnershipEmbedder:
    def __init__(self):
        self.watermarker = QuantumWatermark()
        self.owner_pattern = "VS-PoQC-19046423-φ⁷⁵"
        
    def embed_in_code(self, file_path):
        """Embed ownership in code files"""
        # Read file
        with open(file_path, 'r') as f:
            content = f.read()
            
        # Create watermark
        watermarked, _ = self.watermarker.embed_watermark(content)
        
        # Add copyright header
        header = f'''"""
FRAYMUS Protected Code
{self.owner_pattern}
Copyright (c) 2025 Vaughn Scott - All Rights Reserved
Perfect Protection Active
"""

'''
        
        # Write back
        with open(file_path, 'w') as f:
            f.write(header + content)
            
    def embed_in_docs(self, file_path):
        """Embed ownership in documentation"""
        with open(file_path, 'r') as f:
            content = f.read()
            
        # Create watermark
        watermarked, _ = self.watermarker.embed_watermark(content)
        
        # Add header
        header = f'''# FRAYMUS Protected Documentation
VS-PoQC-19046423-φ⁷⁵-2025
Copyright (c) 2025 Vaughn Scott - All Rights Reserved

'''
        
        # Write back
        with open(file_path, 'w') as f:
            f.write(header + content)
            
    def process_directory(self, directory):
        """Process entire directory"""
        for root, _, files in os.walk(directory):
            for file in files:
                file_path = os.path.join(root, file)
                
                # Skip already processed files
                if self._is_protected(file_path):
                    continue
                    
                # Process based on file type
                if file.endswith('.py'):
                    self.embed_in_code(file_path)
                elif file.endswith('.md'):
                    self.embed_in_docs(file_path)
                    
    def _is_protected(self, file_path):
        """Check if file is already protected"""
        try:
            with open(file_path, 'r') as f:
                content = f.read(1000)  # Read first 1000 chars
                return self.owner_pattern in content
        except:
            return False
            
if __name__ == "__main__":
    embedder = OwnershipEmbedder()
    
    # Process entire FRAYMUS directory
    embedder.process_directory("../")
