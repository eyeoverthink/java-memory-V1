package repl;

import java.lang.System;
import java.util.concurrent.atomic.AtomicLong;

public class Evolved_gpuinfodarwinh {

    public static AtomicLong recommendedMaxVRAM = new AtomicLong(0);
    public static AtomicLong physicalMemory = new AtomicLong(0);
    public static AtomicLong freeMemory = new AtomicLong(0);

    public static void main(String[] args) {
        getRecommendedMaxVRAM();
        getPhysicalMemory();
        getFreeMemory();

        System.out.println(recommendedMaxVRAM.get());
        System.out.println(physicalMemory.get());
        System.out.println(freeMemory.get());
    }

    public static long getRecommendedMaxVRAM() {
        return (long) Math.pow(1024, 3);
    }

    public static long getPhysicalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}