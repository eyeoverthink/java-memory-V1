package fraymus.factory;

import java.util.*;

/** Deterministic spatial baseline. Bounds are broad-phase AABBs, not collision proofs. */
public final class SpatialIndex2D {
    public record Bounds(double minX, double minY, double maxX, double maxY) {
        public Bounds {
            if (!Double.isFinite(minX) || !Double.isFinite(minY) || !Double.isFinite(maxX) || !Double.isFinite(maxY)
                    || minX > maxX || minY > maxY) throw new IllegalArgumentException("Invalid spatial bounds");
        }
        public boolean intersects(Bounds other) {
            return minX <= other.maxX && maxX >= other.minX && minY <= other.maxY && maxY >= other.minY;
        }
        public double distance(double x, double y) {
            return Math.hypot(Math.max(0, Math.max(minX - x, x - maxX)), Math.max(0, Math.max(minY - y, y - maxY)));
        }
    }
    public record Item(UUID entityId, Bounds bounds) {}
    public record Hit(UUID entityId, Bounds bounds, double distance) {}
    private final List<Item> items;
    public SpatialIndex2D(List<Item> items) {
        this.items = List.copyOf(items);
        var ids = new HashSet<UUID>();
        for (var item : items) if (!ids.add(item.entityId())) throw new IllegalArgumentException("Duplicate spatial identity");
    }
    public List<Item> items() { return items; }
    public List<Hit> overlap(Bounds bounds) {
        return items.stream().filter(item -> item.bounds().intersects(bounds))
            .sorted(Comparator.comparing(item -> item.entityId().toString()))
            .map(item -> new Hit(item.entityId(), item.bounds(), 0)).toList();
    }
    public List<Hit> nearest(double x, double y, double radius, int limit) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(radius) || radius < 0 || limit < 1)
            throw new IllegalArgumentException("Invalid spatial query");
        return items.stream().map(item -> new Hit(item.entityId(), item.bounds(), item.bounds().distance(x, y)))
            .filter(hit -> hit.distance() <= radius)
            .sorted(Comparator.comparingDouble(Hit::distance).thenComparing(hit -> hit.entityId().toString())).limit(limit).toList();
    }
}
