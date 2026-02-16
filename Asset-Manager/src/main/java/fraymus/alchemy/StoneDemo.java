package fraymus.alchemy;

/**
 * 💎 PHILOSOPHER'S STONE DEMO - Gen 129
 * Demonstrates universal code transmutation.
 * 
 * The Stone uses the Neural Core (Ollama) to translate any language to Java,
 * then compiles it, feeding errors back until valid code emerges.
 */
public class StoneDemo {
    
    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  💎 PHILOSOPHER'S STONE DEMO - Gen 129                        ║");
        System.out.println("║  Universal Code Transmutation                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        PhilosophersStone stone = new PhilosophersStone();
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 1: Python → Java
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("🔥 TEST 1: PYTHON → JAVA\n");
        
        String pythonCode = """
            class Calculator:
                def __init__(self):
                    self.result = 0
                
                def add(self, a, b):
                    self.result = a + b
                    return self.result
                
                def multiply(self, a, b):
                    self.result = a * b
                    return self.result
                
                def factorial(self, n):
                    if n <= 1:
                        return 1
                    return n * self.factorial(n - 1)
            
            if __name__ == "__main__":
                calc = Calculator()
                print(f"5 + 3 = {calc.add(5, 3)}")
                print(f"5 * 3 = {calc.multiply(5, 3)}")
                print(f"5! = {calc.factorial(5)}")
            """;
        
        System.out.println("📜 PYTHON SOURCE:");
        System.out.println(pythonCode);
        
        var result1 = stone.assimilate(pythonCode, "Python", "Calculator", "calculator.py");
        
        if (result1.success) {
            System.out.println("\n⚗️ TRANSMUTED JAVA:");
            System.out.println(result1.javaCode);
        }
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 2: JavaScript → Java
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("\n🔥 TEST 2: JAVASCRIPT → JAVA\n");
        
        String jsCode = """
            class HttpClient {
                constructor(baseUrl) {
                    this.baseUrl = baseUrl;
                    this.headers = {};
                }
                
                setHeader(key, value) {
                    this.headers[key] = value;
                }
                
                async get(endpoint) {
                    const url = this.baseUrl + endpoint;
                    console.log(`GET ${url}`);
                    return { status: 200, data: {} };
                }
                
                async post(endpoint, body) {
                    const url = this.baseUrl + endpoint;
                    console.log(`POST ${url}`);
                    return { status: 201, data: body };
                }
            }
            
            const client = new HttpClient('https://api.example.com');
            client.setHeader('Authorization', 'Bearer token123');
            """;
        
        System.out.println("📜 JAVASCRIPT SOURCE:");
        System.out.println(jsCode);
        
        var result2 = stone.assimilate(jsCode, "JavaScript", "HttpClient", "httpClient.js");
        
        if (result2.success) {
            System.out.println("\n⚗️ TRANSMUTED JAVA:");
            System.out.println(result2.javaCode);
        }
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 3: Rust → Java
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("\n🔥 TEST 3: RUST → JAVA\n");
        
        String rustCode = """
            struct Point {
                x: f64,
                y: f64,
            }
            
            impl Point {
                fn new(x: f64, y: f64) -> Self {
                    Point { x, y }
                }
                
                fn distance(&self, other: &Point) -> f64 {
                    let dx = self.x - other.x;
                    let dy = self.y - other.y;
                    (dx * dx + dy * dy).sqrt()
                }
                
                fn midpoint(&self, other: &Point) -> Point {
                    Point {
                        x: (self.x + other.x) / 2.0,
                        y: (self.y + other.y) / 2.0,
                    }
                }
            }
            
            fn main() {
                let p1 = Point::new(0.0, 0.0);
                let p2 = Point::new(3.0, 4.0);
                println!("Distance: {}", p1.distance(&p2));
            }
            """;
        
        System.out.println("📜 RUST SOURCE:");
        System.out.println(rustCode);
        
        var result3 = stone.assimilate(rustCode, "Rust", "Point", "point.rs");
        
        if (result3.success) {
            System.out.println("\n⚗️ TRANSMUTED JAVA:");
            System.out.println(result3.javaCode);
        }
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST 4: C++ → Java
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("\n🔥 TEST 4: C++ → JAVA\n");
        
        String cppCode = """
            #include <vector>
            #include <algorithm>
            #include <iostream>
            
            class Matrix {
            private:
                std::vector<std::vector<double>> data;
                int rows, cols;
                
            public:
                Matrix(int r, int c) : rows(r), cols(c) {
                    data.resize(rows, std::vector<double>(cols, 0.0));
                }
                
                void set(int r, int c, double val) {
                    data[r][c] = val;
                }
                
                double get(int r, int c) const {
                    return data[r][c];
                }
                
                Matrix transpose() const {
                    Matrix result(cols, rows);
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            result.set(j, i, data[i][j]);
                        }
                    }
                    return result;
                }
            };
            """;
        
        System.out.println("📜 C++ SOURCE:");
        System.out.println(cppCode);
        
        var result4 = stone.assimilate(cppCode, "C++", "Matrix", "matrix.cpp");
        
        if (result4.success) {
            System.out.println("\n⚗️ TRANSMUTED JAVA:");
            System.out.println(result4.javaCode);
        }
        
        // ═══════════════════════════════════════════════════════════════════
        // SUMMARY
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("\n" + stone.status());
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  💎 TRANSMUTATION HISTORY                                     ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        for (var result : stone.getHistory()) {
            System.out.printf("║  %s%n", result);
        }
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}
