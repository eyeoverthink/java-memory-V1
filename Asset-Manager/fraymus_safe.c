/* * 🧬 FRAYMUS ARTIFACT // GEN 191
 * OPTIMIZATION: Custom Pointer-Based BigInt Arithmetic
 * REASONING: F(1000) exceeds 64-bit integer limits. 
 * We map memory directly to simulate 1024-bit registers.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_DIGITS 250 // Sufficient for F(1000)
#define TARGET_N 1000

// --- THE METAL: BigInt Structure ---
// Represents a massive number as an array of digits
typedef struct {
    unsigned char digits[MAX_DIGITS]; // Byte array for density
    int length;                       // Active digits
} BigInt;

// --- PHYSICS: Vector Addition for Digits ---
// Adds b to a (a += b) using direct pointer arithmetic
void add_bigint(BigInt *a, BigInt *b) {
    int carry = 0;
    int max_len = (a->length > b->length) ? a->length : b->length;
    
    // Pointers to the raw memory
    unsigned char *ptr_a = a->digits;
    unsigned char *ptr_b = b->digits;

    for (int i = 0; i < max_len || carry; i++) {
        int val_a = (i < a->length) ? *(ptr_a + i) : 0;
        int val_b = (i < b->length) ? *(ptr_b + i) : 0;
        
        int sum = val_a + val_b + carry;
        
        *(ptr_a + i) = sum % 10; // Collapse wave to single digit
        carry = sum / 10;        // Carry energy to next dimension
        
        if (i >= a->length) a->length++;
    }
}

// --- LOGIC: The Fibonacci Loop ---
// F(n) = F(n-1) + F(n-2)
void calculate_fibonacci(int n) {
    if (n == 0) { printf("0\n"); return; }
    
    // Genesis States (F0 and F1)
    BigInt *f1 = (BigInt*)calloc(1, sizeof(BigInt));
    BigInt *f2 = (BigInt*)calloc(1, sizeof(BigInt));
    BigInt *temp; // Swap pointer

    // Seed the universe
    f1->digits[0] = 0; f1->length = 1; // F(0)
    f2->digits[0] = 1; f2->length = 1; // F(1)
    
    printf("[CALCULATING F(%d)]...\n", n);

    for (int i = 2; i <= n; i++) {
        // F_new = F_old + F_older
        // We reuse F1's memory to store the sum (Optimization)
        add_bigint(f1, f2); 
        
        // Pointer Swap (O(1) operation - no data copying)
        temp = f1;
        f1 = f2;
        f2 = temp;
    }

    // --- MANIFESTATION: Print Output ---
    printf("RESULT: ");
    // Print in reverse (Most Significant Digit first)
    for (int i = f2->length - 1; i >= 0; i--) {
        printf("%d", f2->digits[i]);
    }
    printf("\n");
    
    // Clean the memory
    free(f1);
    free(f2);
}

int main() {
    printf("🧬 FRAYMUS OMEGA // FIBONACCI ENGINE\n");
    printf("Target: F(%d)\n", TARGET_N);
    
    calculate_fibonacci(TARGET_N);
    
    return 0;
}
