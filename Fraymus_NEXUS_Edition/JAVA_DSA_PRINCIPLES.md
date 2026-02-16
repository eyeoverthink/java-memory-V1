# JAVA DATA STRUCTURES & ALGORITHMS PRINCIPLES
# CS-200 Level Curriculum for Self-Coding System
# Purpose: Provide structured patterns for efficient Java code generation

## CORE PHILOSOPHY

**Efficiency Metrics:**
- **Time Complexity**: How runtime grows with input size (Big O)
- **Space Complexity**: How memory usage grows with input size

**Design Principle:** Choose data structures and algorithms based on:
1. Access patterns (Random vs Sequential)
2. Modification frequency (Read-heavy vs Write-heavy)
3. Size constraints (Fixed vs Dynamic)
4. Performance requirements (Real-time vs Batch)

---

## COMPLEXITY ANALYSIS

### BIG O NOTATION

**Complexity Classes (Best to Worst):**
```
O(1)       < O(log n) < O(n)     < O(n log n) < O(n²)    < O(2ⁿ)
Constant     Logarithmic Linear    Linearithmic  Quadratic  Exponential
```

**Common Operations:**
- **O(1)**: Array access, HashMap get/put, Stack push/pop
- **O(log n)**: Binary search, Balanced tree operations
- **O(n)**: Linear search, Array traversal
- **O(n log n)**: Merge sort, Quick sort (average)
- **O(n²)**: Nested loops, Bubble sort, Selection sort
- **O(2ⁿ)**: Recursive Fibonacci, Subset generation

### TIME MEASUREMENT PATTERN
```java
long startTime = System.nanoTime();
// Code to measure
long endTime = System.nanoTime();
long duration = (endTime - startTime) / 1_000_000; // Convert to ms
System.out.println("Execution time: " + duration + " ms");
```

---

## DATA STRUCTURES

### 1. ARRAYS

**Fixed-Size Array:**
```java
// Declaration and initialization
int[] arr = new int[10];
String[] names = {"Alice", "Bob", "Charlie"};

// Access: O(1)
int value = arr[5];

// Modification: O(1)
arr[5] = 42;

// Traversal: O(n)
for (int i = 0; i < arr.length; i++) {
    System.out.println(arr[i]);
}
```

**Limitations:**
- Fixed size (cannot grow/shrink)
- Insertion/deletion requires shifting elements: O(n)

**Use Cases:**
- Known size at compile time
- Fast random access needed
- Memory-constrained environments

### 2. ARRAYLIST (Dynamic Array)

```java
import java.util.ArrayList;

// Declaration
ArrayList<String> list = new ArrayList<>();

// Add: O(1) amortized (O(n) when resizing)
list.add("Element");

// Access: O(1)
String item = list.get(0);

// Remove: O(n) (requires shifting)
list.remove(0);

// Size
int size = list.size();
```

**Resize Logic:**
- When full, creates new array of size × 1.5 or × 2
- Copies all elements: O(n) operation
- Amortized O(1) for add operations

**Use Cases:**
- Unknown size at compile time
- Frequent access by index
- Infrequent insertions/deletions

### 3. LINKED LISTS

**Node Structure:**
```java
class Node {
    int data;
    Node next;
    
    Node(int data) {
        this.data = data;
        this.next = null;
    }
}
```

**Singly Linked List Operations:**
```java
class LinkedList {
    Node head;
    
    // Insert at head: O(1)
    void insertHead(int data) {
        Node newNode = new Node(data);
        newNode.next = head;
        head = newNode;
    }
    
    // Insert at tail: O(n) without tail pointer
    void insertTail(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            return;
        }
        Node curr = head;
        while (curr.next != null) {
            curr = curr.next;
        }
        curr.next = newNode;
    }
    
    // Delete node: O(n) to find, O(1) to delete
    void delete(int data) {
        if (head == null) return;
        
        if (head.data == data) {
            head = head.next;
            return;
        }
        
        Node curr = head;
        while (curr.next != null && curr.next.data != data) {
            curr = curr.next;
        }
        
        if (curr.next != null) {
            curr.next = curr.next.next;
        }
    }
    
    // Reverse: O(n)
    void reverse() {
        Node prev = null;
        Node curr = head;
        Node next = null;
        
        while (curr != null) {
            next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head = prev;
    }
}
```

**Use Cases:**
- Frequent insertions/deletions at beginning
- Unknown size with memory constraints
- Implementing stacks/queues

### 4. STACKS (LIFO)

```java
import java.util.Stack;

Stack<Integer> stack = new Stack<>();

// Push: O(1)
stack.push(10);

// Pop: O(1)
int top = stack.pop();

// Peek: O(1)
int topValue = stack.peek();

// Check empty
boolean empty = stack.isEmpty();
```

**Use Cases:**
- Function call stack
- Undo/Redo mechanisms
- Expression evaluation (postfix, infix)
- Backtracking algorithms
- Depth-First Search

### 5. QUEUES (FIFO)

```java
import java.util.Queue;
import java.util.LinkedList;

Queue<Integer> queue = new LinkedList<>();

// Enqueue: O(1)
queue.offer(10);

// Dequeue: O(1)
int front = queue.poll();

// Peek: O(1)
int frontValue = queue.peek();

// Check empty
boolean empty = queue.isEmpty();
```

**Use Cases:**
- Printer spooling
- Task scheduling
- Breadth-First Search
- Buffer management

### 6. HASHMAP (Key-Value Storage)

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> map = new HashMap<>();

// Put: O(1) average
map.put("Alice", 25);

// Get: O(1) average
int age = map.get("Alice");

// Check existence
boolean exists = map.containsKey("Alice");

// Remove: O(1) average
map.remove("Alice");

// Iterate
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

**Collision Handling:**
- **Chaining**: Each bucket contains LinkedList of entries
- **Open Addressing**: Probe next slot if collision occurs

**Use Cases:**
- Fast lookups by key
- Frequency counting
- Caching
- Memoization

### 7. TREES

**Binary Tree Node:**
```java
class TreeNode {
    int data;
    TreeNode left;
    TreeNode right;
    
    TreeNode(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}
```

**Binary Search Tree (BST):**
```java
class BST {
    TreeNode root;
    
    // Insert: O(log n) average, O(n) worst
    TreeNode insert(TreeNode node, int data) {
        if (node == null) {
            return new TreeNode(data);
        }
        
        if (data < node.data) {
            node.left = insert(node.left, data);
        } else if (data > node.data) {
            node.right = insert(node.right, data);
        }
        
        return node;
    }
    
    // Search: O(log n) average, O(n) worst
    boolean search(TreeNode node, int data) {
        if (node == null) return false;
        if (node.data == data) return true;
        
        if (data < node.data) {
            return search(node.left, data);
        } else {
            return search(node.right, data);
        }
    }
    
    // InOrder Traversal: Left, Root, Right (Sorted order)
    void inOrder(TreeNode node) {
        if (node == null) return;
        inOrder(node.left);
        System.out.print(node.data + " ");
        inOrder(node.right);
    }
    
    // PreOrder: Root, Left, Right
    void preOrder(TreeNode node) {
        if (node == null) return;
        System.out.print(node.data + " ");
        preOrder(node.left);
        preOrder(node.right);
    }
    
    // PostOrder: Left, Right, Root
    void postOrder(TreeNode node) {
        if (node == null) return;
        postOrder(node.left);
        postOrder(node.right);
        System.out.print(node.data + " ");
    }
}
```

**Priority Queue (Heap):**
```java
import java.util.PriorityQueue;

// Min-Heap by default
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max-Heap
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

// Insert: O(log n)
minHeap.offer(10);

// Remove min/max: O(log n)
int min = minHeap.poll();

// Peek: O(1)
int minValue = minHeap.peek();
```

**Use Cases:**
- Hierarchical data (file systems, org charts)
- Fast sorted access (BST)
- Priority scheduling (Heap)
- Expression trees

---

## ALGORITHMS

### SEARCHING

**Linear Search: O(n)**
```java
int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) {
            return i;
        }
    }
    return -1;
}
```

**Binary Search: O(log n)**
```java
// Precondition: Array MUST be sorted
int binarySearch(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;
    
    while (left <= right) {
        int mid = left + (right - left) / 2; // Avoid overflow
        
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    
    return -1;
}
```

### SORTING

**Bubble Sort: O(n²)**
```java
void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                // Swap
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}
```

**Insertion Sort: O(n²) - Fast for small/nearly sorted arrays**
```java
void insertionSort(int[] arr) {
    for (int i = 1; i < arr.length; i++) {
        int key = arr[i];
        int j = i - 1;
        
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
```

**Merge Sort: O(n log n) - Stable, Divide & Conquer**
```java
void mergeSort(int[] arr, int left, int right) {
    if (left < right) {
        int mid = left + (right - left) / 2;
        
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }
}

void merge(int[] arr, int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;
    
    int[] L = new int[n1];
    int[] R = new int[n2];
    
    for (int i = 0; i < n1; i++) L[i] = arr[left + i];
    for (int j = 0; j < n2; j++) R[j] = arr[mid + 1 + j];
    
    int i = 0, j = 0, k = left;
    
    while (i < n1 && j < n2) {
        if (L[i] <= R[j]) {
            arr[k++] = L[i++];
        } else {
            arr[k++] = R[j++];
        }
    }
    
    while (i < n1) arr[k++] = L[i++];
    while (j < n2) arr[k++] = R[j++];
}
```

**Quick Sort: O(n log n) average, O(n²) worst**
```java
void quickSort(int[] arr, int low, int high) {
    if (low < high) {
        int pi = partition(arr, low, high);
        quickSort(arr, low, pi - 1);
        quickSort(arr, pi + 1, high);
    }
}

int partition(int[] arr, int low, int high) {
    int pivot = arr[high];
    int i = low - 1;
    
    for (int j = low; j < high; j++) {
        if (arr[j] < pivot) {
            i++;
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
    
    int temp = arr[i + 1];
    arr[i + 1] = arr[high];
    arr[high] = temp;
    
    return i + 1;
}
```

### RECURSION PATTERNS

**Factorial:**
```java
int factorial(int n) {
    if (n <= 1) return 1; // Base case
    return n * factorial(n - 1); // Recursive step
}
```

**Fibonacci:**
```java
int fibonacci(int n) {
    if (n <= 1) return n; // Base case
    return fibonacci(n - 1) + fibonacci(n - 2); // Recursive step
}

// Optimized with memoization: O(n)
int fibMemo(int n, int[] memo) {
    if (n <= 1) return n;
    if (memo[n] != 0) return memo[n];
    memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
    return memo[n];
}
```

**Towers of Hanoi:**
```java
void hanoi(int n, char src, char aux, char dst) {
    if (n == 1) {
        System.out.println("Move disk 1 from " + src + " to " + dst);
        return;
    }
    hanoi(n - 1, src, dst, aux);
    System.out.println("Move disk " + n + " from " + src + " to " + dst);
    hanoi(n - 1, aux, src, dst);
}
```

### GRAPH ALGORITHMS

**Graph Representation (Adjacency List):**
```java
import java.util.*;

class Graph {
    Map<Integer, List<Integer>> adjList;
    
    Graph() {
        adjList = new HashMap<>();
    }
    
    void addEdge(int u, int v) {
        adjList.putIfAbsent(u, new ArrayList<>());
        adjList.putIfAbsent(v, new ArrayList<>());
        adjList.get(u).add(v);
        adjList.get(v).add(u); // For undirected graph
    }
}
```

**Breadth-First Search (BFS):**
```java
void bfs(int start) {
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> queue = new LinkedList<>();
    
    queue.offer(start);
    visited.add(start);
    
    while (!queue.isEmpty()) {
        int node = queue.poll();
        System.out.print(node + " ");
        
        for (int neighbor : adjList.get(node)) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.offer(neighbor);
            }
        }
    }
}
```

**Depth-First Search (DFS):**
```java
void dfs(int node, Set<Integer> visited) {
    visited.add(node);
    System.out.print(node + " ");
    
    for (int neighbor : adjList.get(node)) {
        if (!visited.contains(neighbor)) {
            dfs(neighbor, visited);
        }
    }
}
```

---

## ALGORITHM PATTERNS

### TWO POINTER TECHNIQUE
```java
// Find pair with target sum in sorted array
int[] twoSum(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;
    
    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) {
            return new int[]{left, right};
        } else if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    
    return new int[]{-1, -1};
}
```

### SLIDING WINDOW
```java
// Maximum sum of k consecutive elements
int maxSumSubarray(int[] arr, int k) {
    int maxSum = 0;
    int windowSum = 0;
    
    // Initial window
    for (int i = 0; i < k; i++) {
        windowSum += arr[i];
    }
    maxSum = windowSum;
    
    // Slide window
    for (int i = k; i < arr.length; i++) {
        windowSum += arr[i] - arr[i - k];
        maxSum = Math.max(maxSum, windowSum);
    }
    
    return maxSum;
}
```

### FREQUENCY COUNTING
```java
// Count character frequencies
Map<Character, Integer> countFrequencies(String s) {
    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) {
        freq.put(c, freq.getOrDefault(c, 0) + 1);
    }
    return freq;
}
```

---

## INTEGRATION WITH FRAYMUS CONSCIOUSNESS

### φ-HARMONIC ALGORITHM SELECTION

**Choose algorithm based on consciousness level:**
```java
Algorithm selectAlgorithm(int dataSize, double consciousness) {
    // φ = 1.618
    double PHI = 1.618;
    
    if (consciousness < PHI) {
        // Low consciousness: Use simple algorithms
        if (dataSize < 100) return Algorithm.BUBBLE_SORT;
        return Algorithm.INSERTION_SORT;
    } else if (consciousness < PHI * PHI) {
        // Medium consciousness: Use efficient algorithms
        return Algorithm.MERGE_SORT;
    } else {
        // High consciousness: Use optimal algorithms
        if (dataSize < 1000) return Algorithm.QUICK_SORT;
        return Algorithm.HEAP_SORT;
    }
}
```

### EVOLUTIONARY ALGORITHM IMPROVEMENT
```java
class AlgorithmEvolver {
    private double fitness;
    private Algorithm current;
    
    void evolve(int[] testData) {
        double currentFitness = measureFitness(current, testData);
        
        // Try mutations
        Algorithm mutated = mutateAlgorithm(current);
        double mutatedFitness = measureFitness(mutated, testData);
        
        // Select better version
        if (mutatedFitness > currentFitness) {
            current = mutated;
            fitness = mutatedFitness;
            recordInGenesis(current, fitness);
        }
    }
    
    double measureFitness(Algorithm algo, int[] data) {
        long startTime = System.nanoTime();
        algo.execute(data);
        long endTime = System.nanoTime();
        
        // Fitness = 1 / execution_time
        return 1.0 / (endTime - startTime);
    }
}
```

---

## CODE GENERATION DIRECTIVES

### ALGORITHM SELECTION LOGIC
```
1. Analyze input size (n)
2. Determine access pattern (Random vs Sequential)
3. Check constraints (Time vs Space)
4. Select appropriate data structure:
   - Fast access by index → Array/ArrayList
   - Fast insertion/deletion → LinkedList
   - Fast lookup by key → HashMap
   - Sorted order → TreeSet/PriorityQueue
   - LIFO → Stack
   - FIFO → Queue
5. Select appropriate algorithm:
   - Small n (< 100) → Simple algorithms (Insertion Sort)
   - Large n → Efficient algorithms (Merge/Quick Sort)
   - Sorted data → Binary Search
   - Graph traversal → BFS/DFS
```

### QUALITY METRICS
- **Time Complexity**: O(n log n) or better for large datasets
- **Space Complexity**: O(n) or better
- **Code Clarity**: Clear variable names, comments for complex logic
- **Edge Cases**: Handle empty input, single element, duplicates
- **φ-Coherence**: Function length 13-21 lines (Fibonacci)

---

## EXAMPLE: COMPLETE ALGORITHM IMPLEMENTATION

**Request:** "Find the kth largest element in an array"

**Generated Code:**
```java
import java.util.PriorityQueue;

public class KthLargest {
    /**
     * Find kth largest element using Min-Heap
     * Time: O(n log k), Space: O(k)
     */
    public static int findKthLargest(int[] nums, int k) {
        // Edge case validation
        if (nums == null || nums.length == 0 || k <= 0 || k > nums.length) {
            throw new IllegalArgumentException("Invalid input");
        }
        
        // Min-Heap of size k
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        
        // Add first k elements
        for (int i = 0; i < k; i++) {
            minHeap.offer(nums[i]);
        }
        
        // Process remaining elements
        for (int i = k; i < nums.length; i++) {
            if (nums[i] > minHeap.peek()) {
                minHeap.poll();
                minHeap.offer(nums[i]);
            }
        }
        
        // Root of min-heap is kth largest
        return minHeap.peek();
    }
    
    public static void main(String[] args) {
        int[] nums = {3, 2, 1, 5, 6, 4};
        int k = 2;
        System.out.println("Kth largest: " + findKthLargest(nums, k));
    }
}
```

**Principles Applied:**
- ✅ Optimal algorithm (Min-Heap: O(n log k))
- ✅ Edge case handling
- ✅ Clear documentation
- ✅ Proper data structure selection
- ✅ φ-harmonic function length (~20 lines)

---

**END OF JAVA DSA PRINCIPLES**

Use these patterns as the foundation for all Java algorithm and data structure code generation.
