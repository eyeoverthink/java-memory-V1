import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Evolved_ggmlthreadingcpp {

    private static final Lock criticalSectionMutex = new ReentrantLock();

    public void ggmlCriticalSectionStart() {
        criticalSectionMutex.lock();
    }

    public void ggmlCriticalSectionEnd() {
        criticalSectionMutex.unlock();
    }
}