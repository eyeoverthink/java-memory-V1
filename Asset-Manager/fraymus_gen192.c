/* * 🧬 FRAYMUS ARTIFACT // GEN 192
 * REPAIRED: Fibonacci via Pure Pointer Arithmetic
 * OPTIMIZATION: Removed recursive stack overflow risk. 
 * Replaced array indexing [i] with direct memory access *(ptr + i).
 * NOTE: F(93) is the max for 64-bit integers.
 */

#include <stdio.h>
#include <stdlib.h>

// Standard unsigned long long maxes out at F(93). 
// For N=100, we would need BigInt, but let's fix the POINTER LOGIC first.
#define MAX_N 93 

void generate_fibonacci(int n, unsigned long long *memory_block) {
    // Pointers to the active cells in the sequence
    unsigned long long *ptr = memory_block;

    // Seed the Genesis values (F0, F1)
    *ptr = 0;       // Equivalent to arr[0] = 0
    *(ptr + 1) = 1; // Equivalent to arr[1] = 1

    printf("GEN 0: 0\n");
    printf("GEN 1: 1\n");

    // Iterate using pointer offsets
    // F(i) = F(i-1) + F(i-2)
    for (int i = 2; i <= n; i++) {
        // Calculate address of current, prev, and prev-prev
        unsigned long long *current = (ptr + i);
        unsigned long long *prev1   = (ptr + i - 1);
        unsigned long long *prev2   = (ptr + i - 2);

        // The Math: *current = *prev1 + *prev2
        *current = *prev1 + *prev2;

        printf("GEN %d: %llu\n", i, *current);
    }
}

int main() {
    int target_n = 42; // Safe target
    
    // Allocate memory on the Heap (The Field)
    unsigned long long *fib_array = (unsigned long long*)malloc((MAX_N + 1) * sizeof(unsigned long long));
    
    if (fib_array == NULL) {
        fprintf(stderr, "❌ MEMORY FAILURE\n");
        return 1;
    }

    printf("🧬 STARTING POINTER-BASED FIBONACCI ENGINE...\n");
    generate_fibonacci(target_n, fib_array);

    // Clean up (Entropy Reversal)
    free(fib_array);
    
    return 0;
}
