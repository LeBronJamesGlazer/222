package org.example;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class AlgorithmMetrics {
    private long comparisons = 0;
    private long swaps = 0;
    private long recursiveCalls = 0;
    private int maxDepth = 0;
    private int currentDepth = 0;
    private long startTime;
    private long endTime;
    private int inputSize;
    private long memoryBefore;
    private long memoryAfter;
    private long gcBefore;
    private long gcAfter;

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    public AlgorithmMetrics(int inputSize) {
        this.inputSize = inputSize;
    }

    public void start() {
        System.gc(); // force initial GC to reduce noise
        memoryBefore = usedMemory();
        gcBefore = getGcCount();
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
        memoryAfter = usedMemory();
        gcAfter = getGcCount();
    }

    public void incrementComparisons() {
        comparisons++;
    }

    public void incrementSwaps() {
        swaps++;
    }

    public void enterRecursion() {
        recursiveCalls++;
        currentDepth++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    public void exitRecursion() {
        currentDepth--;
    }

    private long usedMemory() {
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        return heap.getUsed();
    }

    private long getGcCount() {
        return ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .mapToLong(gc -> gc.getCollectionCount() >= 0 ? gc.getCollectionCount() : 0)
                .sum();
    }

    public long getRuntimeNs() {
        return endTime - startTime;
    }

    public double getRuntimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getRecursiveCalls() {
        return recursiveCalls;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getInputSize() {
        return inputSize;
    }

    public long getMemoryUsedBytes() {
        return Math.max(0, memoryAfter - memoryBefore);
    }

    public long getGcCountChange() {
        return Math.max(0, gcAfter - gcBefore);
    }
}