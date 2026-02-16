package fraymus.generated;

public class MathEvolution {
    public MathEvolution() {
        System.out.println("🧬 Math Evolution initialized");
    }

    public int fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n-1) + fibonacci(n-2);
    }

    public void test() {
        System.out.println("   Testing self-generated Fibonacci:");
        for (int i = 0; i < 10; i++) {
            System.out.println("   fib(" + i + ") = " + fibonacci(i));
        }
    }

    @Override
    public String toString() {
        return "MathEvolution[Fibonacci Calculator]";
    }
}
