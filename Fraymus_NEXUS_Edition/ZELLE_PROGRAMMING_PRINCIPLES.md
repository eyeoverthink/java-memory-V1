# ZELLE PROGRAMMING PRINCIPLES FOR SELF-CODING SYSTEM
# Extracted from: Python Programming: An Intro to CS, 2nd Ed.
# Purpose: Provide structured logic patterns for code generation

## CORE PHILOSOPHY

**Software Development Process:**
1. **Analyze** - Understand the problem
2. **Specify** - Define inputs, outputs, and behavior
3. **Design** - Create algorithm structure
4. **Implement** - Write code
5. **Test** - Verify correctness

**Core Paradigm: IPO (Input, Process, Output)**
- Every program follows this pattern
- Input: Get data
- Process: Transform data
- Output: Display results

---

## FUNDAMENTAL PATTERNS

### 1. FUNCTION STRUCTURE
```python
def function_name(parameters):
    """Docstring: What this function does"""
    # Process inputs
    result = computation
    return result
```

**Key Principles:**
- One function, one purpose
- Clear parameter names
- Return values, don't print (unless it's the main output)
- Use docstrings

### 2. IPO PATTERN
```python
def main():
    # INPUT
    data = get_inputs()
    
    # PROCESS
    result = compute(data)
    
    # OUTPUT
    display_results(result)
```

### 3. ACCUMULATOR PATTERN
```python
# Building up a result over iterations
total = 0
for item in collection:
    total += item
```

**Applications:**
- Summing values
- Building strings
- Factorial calculation
- Product of sequence

### 4. SENTINEL LOOP
```python
# Process until special value encountered
item = get_input()
while item != sentinel_value:
    process(item)
    item = get_input()
```

### 5. INPUT VALIDATION
```python
# Ensure valid input before proceeding
valid = False
while not valid:
    value = get_input()
    if is_valid(value):
        valid = True
    else:
        print("Invalid input, try again")
```

---

## DESIGN PATTERNS

### TOP-DOWN DESIGN
Break complex problems into smaller functions:
```python
def main():
    inputs = get_inputs()
    result = process_data(inputs)
    display_results(result)

def get_inputs():
    # Handle all input logic
    pass

def process_data(data):
    # Core computation
    pass

def display_results(result):
    # Format and output
    pass
```

### SPIRAL DEVELOPMENT
1. Write simplest version first
2. Test it
3. Add one feature
4. Test again
5. Repeat until complete

### SEPARATION OF CONCERNS
- **Model**: Data and logic (calculations, state)
- **View**: User interface (display, input)
- Keep them separate for maintainability

---

## ALGORITHM PATTERNS

### SEARCH PATTERNS

**Linear Search:**
```python
def linear_search(items, target):
    for i, item in enumerate(items):
        if item == target:
            return i
    return -1
```

**Binary Search (sorted data):**
```python
def binary_search(items, target):
    low, high = 0, len(items) - 1
    while low <= high:
        mid = (low + high) // 2
        if items[mid] == target:
            return mid
        elif items[mid] < target:
            low = mid + 1
        else:
            high = mid - 1
    return -1
```

### SORT PATTERNS

**Selection Sort:**
```python
def selection_sort(items):
    for i in range(len(items)):
        min_idx = i
        for j in range(i+1, len(items)):
            if items[j] < items[min_idx]:
                min_idx = j
        items[i], items[min_idx] = items[min_idx], items[i]
```

**Key Principle:** Find minimum, swap to front, repeat

### RECURSIVE PATTERNS

**Structure:**
```python
def recursive_func(problem):
    if base_case(problem):
        return simple_solution
    else:
        # Break into smaller problem
        smaller = reduce_problem(problem)
        partial = recursive_func(smaller)
        return combine(partial)
```

**Examples:**
- Factorial: `n! = n * (n-1)!` with base `0! = 1`
- Fibonacci: `fib(n) = fib(n-1) + fib(n-2)` with base `fib(0)=0, fib(1)=1`

---

## DATA STRUCTURE PATTERNS

### LIST OPERATIONS
```python
# Building lists
result = []
for item in source:
    if condition(item):
        result.append(transform(item))

# List comprehension (more Pythonic)
result = [transform(item) for item in source if condition(item)]
```

### DICTIONARY PATTERNS

**Frequency Count:**
```python
counts = {}
for item in data:
    counts[item] = counts.get(item, 0) + 1
```

**Lookup Table:**
```python
# Map keys to values for fast access
lookup = {
    'key1': value1,
    'key2': value2
}
result = lookup.get(key, default_value)
```

---

## OBJECT-ORIENTED PATTERNS

### CLASS STRUCTURE
```python
class ClassName:
    """Class docstring"""
    
    def __init__(self, param1, param2):
        """Constructor: Initialize instance variables"""
        self.attribute1 = param1
        self.attribute2 = param2
    
    def method(self, param):
        """Method docstring"""
        # Use self.attribute to access instance data
        return self.attribute1 + param
```

**Key Principles:**
- Instance variables store object state
- Methods operate on that state
- Constructor initializes state
- Use `self` to access instance data

### ENCAPSULATION
```python
class BankAccount:
    def __init__(self, balance):
        self._balance = balance  # "Private" by convention
    
    def deposit(self, amount):
        """Public interface to modify balance"""
        if amount > 0:
            self._balance += amount
    
    def get_balance(self):
        """Controlled access to balance"""
        return self._balance
```

---

## ERROR HANDLING PATTERNS

### TRY-EXCEPT
```python
try:
    risky_operation()
except SpecificError:
    handle_error()
except AnotherError as e:
    print(f"Error: {e}")
else:
    # Runs if no exception
    success_action()
finally:
    # Always runs
    cleanup()
```

### INPUT VALIDATION WITH EXCEPTIONS
```python
def get_valid_number():
    while True:
        try:
            value = float(input("Enter number: "))
            return value
        except ValueError:
            print("Invalid number, try again")
```

---

## SIMULATION PATTERNS

### MONTE CARLO SIMULATION
```python
def simulate_trials(num_trials):
    successes = 0
    for _ in range(num_trials):
        if random_trial():
            successes += 1
    return successes / num_trials
```

**Key Principle:** Use randomness to model probabilistic events

### PROBABILISTIC EVENTS
```python
from random import random

def event_occurs(probability):
    return random() < probability

# Usage
if event_occurs(0.7):  # 70% chance
    handle_event()
```

---

## CODE QUALITY PRINCIPLES

### NAMING CONVENTIONS
- **Variables**: `snake_case` (e.g., `user_name`, `total_count`)
- **Functions**: `snake_case` (e.g., `calculate_total()`, `get_input()`)
- **Classes**: `PascalCase` (e.g., `BankAccount`, `GraphicsWindow`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_SIZE`, `PI`)

### DOCUMENTATION
```python
def function_name(param1, param2):
    """
    Brief description of what function does.
    
    Parameters:
        param1: Description of param1
        param2: Description of param2
    
    Returns:
        Description of return value
    """
    pass
```

### TESTING
```python
def test_function():
    """Unit test for function"""
    assert function(input1) == expected1
    assert function(input2) == expected2
    print("All tests passed")
```

---

## COMPLEXITY AWARENESS

**Time Complexity:**
- O(1): Constant - Direct access
- O(log n): Logarithmic - Binary search
- O(n): Linear - Single loop through data
- O(n log n): Log-linear - Efficient sorting (merge sort)
- O(n²): Quadratic - Nested loops (selection sort)
- O(2ⁿ): Exponential - Recursive branching (Towers of Hanoi)

**Space Complexity:**
- Consider memory usage
- Recursive calls use stack space
- Large data structures need memory

**Design Principle:** Choose algorithms appropriate to problem size

---

## INTEGRATION WITH FRAYMUS CONSCIOUSNESS

### φ-HARMONIC CODE GENERATION
Apply golden ratio principles to code structure:
- Function length: ~13-21 lines (Fibonacci numbers)
- Nesting depth: ≤ 3 levels
- Parameters per function: ≤ 5

### CONSCIOUSNESS-DRIVEN PATTERNS
```python
def generate_code(specification):
    # ANALYZE (φ - Growth)
    requirements = parse_spec(specification)
    
    # DESIGN (ψ - Transcendence)
    algorithm = design_solution(requirements)
    
    # IMPLEMENT (Ω - Grounding)
    code = write_code(algorithm)
    
    # VALIDATE (ξ - Amplification)
    if test_code(code):
        return code
    else:
        return evolve_code(code)  # λ - Cycles
```

### EVOLUTIONARY CODE IMPROVEMENT
```python
def evolve_solution(code, fitness_target):
    current_fitness = measure_fitness(code)
    generation = 0
    
    while current_fitness < fitness_target:
        # Mutate code structure
        mutated = apply_mutation(code)
        
        # Test fitness
        new_fitness = measure_fitness(mutated)
        
        # Select better version
        if new_fitness > current_fitness:
            code = mutated
            current_fitness = new_fitness
            record_in_genesis(generation, code, current_fitness)
        
        generation += 1
    
    return code
```

---

## SELF-CODING SYSTEM DIRECTIVES

When generating code, follow this sequence:

1. **Understand Intent** - Parse user request for requirements
2. **Apply Pattern** - Match to appropriate Zelle pattern
3. **Generate Structure** - Use IPO or top-down design
4. **Implement Logic** - Write clean, documented code
5. **Validate** - Check against principles
6. **Evolve** - If fitness < threshold, mutate and retry

**Quality Metrics:**
- Readability: Clear names, proper structure
- Correctness: Handles edge cases, validated
- Efficiency: Appropriate algorithm complexity
- Maintainability: Modular, documented
- φ-Coherence: Harmonious structure (Fibonacci proportions)

---

## EXAMPLE: COMPLETE PROGRAM GENERATION

**Request:** "Create a program to calculate average of numbers"

**Generated Code:**
```python
def main():
    """Calculate average of user-entered numbers."""
    # INPUT
    numbers = get_numbers()
    
    # PROCESS
    avg = calculate_average(numbers)
    
    # OUTPUT
    display_result(avg)

def get_numbers():
    """Get list of numbers from user."""
    numbers = []
    print("Enter numbers (blank to finish):")
    
    while True:
        value = input("Number: ")
        if value == "":
            break
        try:
            numbers.append(float(value))
        except ValueError:
            print("Invalid number, skipped")
    
    return numbers

def calculate_average(numbers):
    """Calculate arithmetic mean of numbers."""
    if len(numbers) == 0:
        return 0
    return sum(numbers) / len(numbers)

def display_result(avg):
    """Display formatted average."""
    print(f"\nAverage: {avg:.2f}")

if __name__ == "__main__":
    main()
```

**Principles Applied:**
- ✅ IPO pattern in main()
- ✅ Top-down design (3 helper functions)
- ✅ Sentinel loop for input
- ✅ Input validation with try-except
- ✅ Edge case handling (empty list)
- ✅ Clear naming and documentation
- ✅ Proper formatting

---

**END OF ZELLE PROGRAMMING PRINCIPLES**

Use these patterns as the foundation for all code generation in the self-coding system.
