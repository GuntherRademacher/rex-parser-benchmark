package de.bottlecaps.rex.benchmark.json;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryMonitor extends Thread {
    private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    private boolean running = true;
    private AtomicLong maxMemoryUsed = new AtomicLong(0);

    @Override
    public void run() {
        while (running) {
            long usedMemory = memoryUsed();
            if (usedMemory > maxMemoryUsed.get())
                maxMemoryUsed.set(usedMemory);

            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static long memoryUsed() {
      return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    public void stopMonitoring() {
        running = false;
    }

    public static void triggerGC() {
        System.gc();
    }

    public long resetMaxMemoryUsed() {
      long memoryUsed = memoryUsed();
      maxMemoryUsed.set(memoryUsed); // reset maxMemoryUsed for the next cycle
      return memoryUsed;
    }

    public long maxMemoryUsed() {
        return maxMemoryUsed.get();
    }
}
