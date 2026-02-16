package fraynix.core;

/**
 * KERNEL SERVICE: Contract for all system services.
 * 
 * Every subsystem (Brain, FS, Swarm, Genesis, etc.) implements this.
 * Provides consistent lifecycle management and health monitoring.
 */
public interface KernelService {

    /**
     * Service identity
     */
    String getName();
    String getVersion();

    /**
     * Lifecycle
     */
    void start();
    void stop();
    void restart();

    /**
     * Status
     */
    ServiceStatus getStatus();
    HealthReport getHealth();

    /**
     * Metrics
     */
    ServiceMetrics getMetrics();

    enum ServiceStatus {
        STOPPED,
        STARTING,
        RUNNING,
        DEGRADED,    // Running but with issues
        STOPPING,
        FAILED
    }

    record HealthReport(
        ServiceStatus status,
        boolean healthy,
        String message,
        long uptimeMs,
        java.util.Map<String, Object> details
    ) {
        public static HealthReport healthy(ServiceStatus status, long uptimeMs) {
            return new HealthReport(status, true, "OK", uptimeMs, java.util.Map.of());
        }

        public static HealthReport unhealthy(ServiceStatus status, String message, long uptimeMs) {
            return new HealthReport(status, false, message, uptimeMs, java.util.Map.of());
        }

        public static HealthReport degraded(String message, long uptimeMs, java.util.Map<String, Object> details) {
            return new HealthReport(ServiceStatus.DEGRADED, false, message, uptimeMs, details);
        }
    }

    record ServiceMetrics(
        long requestsTotal,
        long requestsSuccess,
        long requestsFailed,
        long avgLatencyMs,
        long p99LatencyMs,
        double cpuPercent,
        long memoryBytes,
        java.util.Map<String, Long> customCounters
    ) {
        public static ServiceMetrics empty() {
            return new ServiceMetrics(0, 0, 0, 0, 0, 0.0, 0, java.util.Map.of());
        }
    }
}
