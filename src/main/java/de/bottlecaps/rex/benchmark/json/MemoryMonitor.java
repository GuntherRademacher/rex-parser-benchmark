package de.bottlecaps.rex.benchmark.json;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServer;

import com.sun.management.HotSpotDiagnosticMXBean;

public class MemoryMonitor extends Thread {
    private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    private boolean running = true;
    private boolean dumped = false;
    private AtomicLong maxMemoryUsed = new AtomicLong(0);
    private AtomicLong heapDumpAt = new AtomicLong(0);

    public MemoryMonitor(Long heapDumpAt) {
      this.heapDumpAt.set(heapDumpAt == null ? 0 : heapDumpAt.longValue());
    }

    @Override
    public void run() {
      while (running) {
        long maxMemory = updateMaxMemoryUsed();
        if (!dumped) {
          long limit = heapDumpAt.get();
          if (limit > 0 && maxMemory >= limit) {
            dumped = true;
            File file = new File("java_" + ProcessHandle.current().pid() + ".hprof");
            if (file.exists())
              file.delete();
            String filename = file.getAbsolutePath();
            try {
              dumpHeap(filename, true);
            }
            catch (IOException e) {
              System.err.println("failed to write heap dump:");
              e.printStackTrace(System.err);
            }
            System.err.println("heap memory limit reached - dumped heap to " + filename);
          }
        }
        try {
          Thread.sleep(100);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    private long updateMaxMemoryUsed() {
      long usedMemory = memoryUsed();
      long maxMemory = maxMemoryUsed.get();
      if (usedMemory > maxMemory) {
        maxMemory = usedMemory;
        maxMemoryUsed.set(maxMemory);
      }
      return maxMemory;
    }

    public void setHeapDumpAt(Long size) {
      heapDumpAt.set(size == null ? 0 : size);
    }

    public static void dumpHeap(String filePath, boolean live) throws IOException {
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(server,
          "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
      mxBean.dumpHeap(filePath, live);
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
      return updateMaxMemoryUsed();
    }
}
