#!/usr/bin/env python3
"""
DNA Cloaking System
Based on phase_V8.html cryptographic cloaking
Splits code into DNA A/B, hashes to primes, creates N
"""

import hashlib
import random
from typing import Tuple, Optional

PHI = 1.618033988749895

def text_to_prime(text: str) -> int:
    """Convert text to prime number via SHA-256 hash"""
    # Hash the text
    hash_bytes = hashlib.sha256(text.encode()).digest()
    hash_hex = hash_bytes.hex()
    
    # Convert to large integer
    SCALE = 1125899906842624  # 2^50
    p = (int(hash_hex, 16) % SCALE) | 1  # Make odd
    
    # Find next prime
    while not is_prime(p):
        p += 2
    
    return p

def is_prime(n: int) -> bool:
    """Check if number is prime"""
    if n < 2:
        return False
    if n % 2 == 0:
        return n == 2
    i = 3
    while i * i <= n:
        if n % i == 0:
            return False
        i += 2
    return True

def pollards_rho(n: int, max_iterations: int = 1000000) -> Optional[int]:
    """Factor n using Pollard's Rho algorithm"""
    if n % 2 == 0:
        return 2
    if n == 1:
        return None
    if is_prime(n):
        return n
    
    x, y, d = 2, 2, 1
    c = random.randint(1, n - 1)
    
    def f(v):
        return (v * v + c) % n
    
    def gcd(a, b):
        while b:
            a, b = b, a % b
        return a
    
    iterations = 0
    while d == 1 and iterations < max_iterations:
        x = f(x)
        y = f(f(y))
        d = gcd(abs(x - y), n)
        iterations += 1
        
        # Restart with different c if stuck
        if iterations % 10000 == 0 and d == 1:
            c = random.randint(1, n - 1)
            x, y = 2, 2
    
    # Return valid factor (not 1 or n)
    if d != 1 and d != n:
        return d
    return None

class DNACloaking:
    """
    Cloak code using DNA split + prime factorization
    Based on phase_V8.html implementation
    """
    
    def __init__(self):
        self.dna_a = None
        self.dna_b = None
        self.prime_a = None
        self.prime_b = None
        self.n_lock = None
    
    def cloak(self, code: str, identifier: str = "code") -> dict:
        """
        Cloak code by splitting into DNA A/B and creating prime lock
        
        Returns:
            dict with 'n_lock', 'dna_a', 'dna_b', 'prime_a', 'prime_b'
        """
        # Split code into two halves
        mid = len(code) // 2
        
        # Create DNA A and DNA B
        self.dna_a = f"{identifier}_{code[:mid]}_A"
        self.dna_b = f"{identifier}_{code[mid:]}_B"
        
        # Hash to primes
        self.prime_a = text_to_prime(self.dna_a)
        self.prime_b = text_to_prime(self.dna_b)
        
        # Create lock
        self.n_lock = self.prime_a * self.prime_b
        
        return {
            'n_lock': self.n_lock,
            'dna_a': self.dna_a,
            'dna_b': self.dna_b,
            'prime_a': self.prime_a,
            'prime_b': self.prime_b,
            'cloaked': True
        }
    
    def break_lock(self, n: int) -> Optional[int]:
        """
        Break the lock by factoring N
        Returns recovered prime factor
        """
        return pollards_rho(n)
    
    def verify_identity(self, recovered_prime: int) -> dict:
        """
        Verify which DNA the recovered prime came from
        
        Returns:
            dict with 'matched', 'dna', 'verified'
        """
        if not self.dna_a or not self.dna_b:
            return {'matched': False, 'dna': None, 'verified': False}
        
        # Recalculate primes from DNA
        calc_a = text_to_prime(self.dna_a)
        calc_b = text_to_prime(self.dna_b)
        
        if calc_a == recovered_prime:
            return {
                'matched': True,
                'dna': self.dna_a,
                'which': 'A',
                'verified': True
            }
        elif calc_b == recovered_prime:
            return {
                'matched': True,
                'dna': self.dna_b,
                'which': 'B',
                'verified': True
            }
        else:
            return {
                'matched': False,
                'dna': None,
                'verified': False,
                'expected_a': calc_a,
                'expected_b': calc_b,
                'received': recovered_prime
            }
    
    def uncloak(self, n: int) -> dict:
        """
        Complete uncloaking: break lock and verify identity
        
        Returns:
            dict with verification results
        """
        # Break the lock
        factor = self.break_lock(n)
        
        if not factor:
            return {
                'success': False,
                'error': 'Could not factor N'
            }
        
        # Verify identity
        verification = self.verify_identity(factor)
        
        return {
            'success': verification['verified'],
            'factor': factor,
            'verification': verification
        }


def demo_cloaking():
    """Demonstrate DNA cloaking system"""
    print("="*70)
    print("DNA CLOAKING DEMONSTRATION")
    print("="*70)
    
    # Sample code to cloak
    code = "def hello(): return 'Hello World'"
    
    print(f"\n[ORIGINAL CODE]")
    print(f"  {code}")
    print(f"  Length: {len(code)} chars")
    
    # Initialize cloaking system
    cloaker = DNACloaking()
    
    # Cloak the code
    print(f"\n[CLOAKING]")
    result = cloaker.cloak(code, "test")
    print(f"  DNA A: {result['dna_a'][:50]}...")
    print(f"  DNA B: {result['dna_b'][:50]}...")
    print(f"  Prime A: {result['prime_a']}")
    print(f"  Prime B: {result['prime_b']}")
    print(f"  N Lock: {result['n_lock']}")
    print(f"  ✓ Code is now CLOAKED in N")
    
    # Break the lock
    print(f"\n[BREAKING LOCK]")
    factor = cloaker.break_lock(result['n_lock'])
    print(f"  Recovered factor: {factor}")
    
    # Verify identity
    print(f"\n[VERIFYING IDENTITY]")
    verification = cloaker.verify_identity(factor)
    if verification['verified']:
        print(f"  ✓ IDENTITY VERIFIED")
        print(f"  Matched DNA {verification['which']}: {verification['dna'][:50]}...")
    else:
        print(f"  ✗ VERIFICATION FAILED")
    
    print(f"\n{'='*70}")
    print(f"DNA CLOAKING: {'SUCCESS' if verification['verified'] else 'FAILED'}")
    print(f"{'='*70}\n")


if __name__ == "__main__":
    demo_cloaking()
