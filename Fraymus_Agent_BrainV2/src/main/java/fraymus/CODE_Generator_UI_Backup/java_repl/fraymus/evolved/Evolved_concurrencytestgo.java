import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Evolved_concurrencytestgo {

    private static final int DEFAULT_NUM_THREADS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_NUM_THREADS);

        Runnable testConcurrentChat = new TestConcurrentChat();
        Runnable multiModelStressTest = new MultiModelStressTest();

        Runnable[] tasks = new Runnable[]{testConcurrentChat, multiModelStressTest};
        for (Runnable task : tasks) {
            executorService.submit(task);
        }

        try {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                executorService.awaitTermination(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdownNow();
        }
    }

    private static class TestConcurrentChat implements Runnable {

        @Override
        public void run() {
            // your test code here
        }
    }

    private static class MultiModelStressTest implements Runnable {

        @Override
        public void run() {
            // your test code here
        }
    }
}