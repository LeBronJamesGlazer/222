package org.example;

import java.util.function.IntSupplier;
import java.lang.management.ManagementFactory;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

/**
 * Kadane algorithm (maximum subarray) with instrumentation.
 * Provides array-based and streaming-based variants.
 */
public final class KadaneAlgorithm {

    private KadaneAlgorithm() {}

    public static class Result {
        public final long maxSum;
        public final int startIndex; // inclusive
        public final int endIndex;   // inclusive; for streaming -1 if not available
        public final Metrics metrics;

        public Result(long maxSum, int startIndex, int endIndex, Metrics metrics) {
            this.maxSum = maxSum;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.metrics = metrics;
        }

        @Override
        public String toString() {
            return "Result[maxSum=" + maxSum + ", start=" + startIndex + ", end=" + endIndex + ", metrics=" + metrics + "]";
        }
    }

    /**
     * Run Kadane on an in-memory array, instrumented.
     */
    public static Result run(int[] arr) {
        Metrics m = new Metrics();
        m.inputSize = arr == null ? 0 : arr.length;
        m.depth = 1; // iterative
        m.recursiveCalls = 0;
        m.swaps = 0;

        System.gc();
        long beforeMem = usedMemoryStable();
        long beforeGcCount = getGcCount();

        long startTime = System.nanoTime();

        if (arr == null || arr.length == 0) {
            m.comparisons = 0;
            m.runTimeMs = toMs(System.nanoTime() - startTime);
            System.gc();
            long afterMem = usedMemoryStable();
            m.memoryUsageBytes = Math.max(0, afterMem - beforeMem);
            m.gcCountDelta = getGcCount() - beforeGcCount;
            return new Result(0, -1, -1, m);
        }

        long maxSoFar = Long.MIN_VALUE;
        long maxEndingHere = 0;
        int start = 0, end = 0, s = 0;

        for (int i = 0; i < arr.length; i++) {
            int x = arr[i];

            // comparison: decide whether to extend or start new subarray
            m.comparisons++;
            if (maxEndingHere + x < x) {
                maxEndingHere = x;
                s = i;
            } else {
                maxEndingHere += x;
            }

            m.comparisons++;
            if (maxSoFar < maxEndingHere) {
                maxSoFar = maxEndingHere;
                start = s;
                end = i;
            }
        }

        long duration = System.nanoTime() - startTime;

        System.gc();
        long afterMem = usedMemoryStable();
        long afterGcCount = getGcCount();

        m.runTimeMs = toMs(duration);
        m.memoryUsageBytes = Math.max(0, afterMem - beforeMem);
        m.gcCountDelta = afterGcCount - beforeGcCount;

        return new Result(maxSoFar, start, end, m);
    }

    /**
     * Run Kadane on a stream/generator of ints. This allows benchmarking sizes that would
     * not fit in memory. The generator should supply exactly 'size' ints, then stop.
     */
    public static Result runStream(IntSupplier generator, long size) {
        Metrics m = new Metrics();
        m.inputSize = (int) size;
        m.depth = 1;
        m.recursiveCalls = 0;
        m.swaps = 0;

        System.gc();
        long beforeMem = usedMemoryStable();
        long beforeGcCount = getGcCount();

        long startTime = System.nanoTime();

        if (size <= 0) {
            m.comparisons = 0;
            m.runTimeMs = toMs(System.nanoTime() - startTime);
            System.gc();
            long afterMem = usedMemoryStable();
            m.memoryUsageBytes = Math.max(0, afterMem - beforeMem);
            m.gcCountDelta = getGcCount() - beforeGcCount;
            return new Result(0, -1, -1, m);
        }

        long maxSoFar = Long.MIN_VALUE;
        long maxEndingHere = 0;
        int start = 0, end = -1, s = 0;
        long index = 0;

        for (; index < size; index++) {
            int x = generator.getAsInt();

            m.comparisons++;
            if (maxEndingHere + x < x) {
                maxEndingHere = x;
                s = (int) index;
            } else {
                maxEndingHere += x;
            }

            m.comparisons++;
            if (maxSoFar < maxEndingHere) {
                maxSoFar = maxEndingHere;
                start = s;
                end = (int) index;
            }
        }

        long duration = System.nanoTime() - startTime;

        System.gc();
        long afterMem = usedMemoryStable();
        long afterGcCount = getGcCount();

        m.runTimeMs = toMs(duration);
        m.memoryUsageBytes = Math.max(0, afterMem - beforeMem);
        m.gcCountDelta = afterGcCount - beforeGcCount;

        return new Result(maxSoFar, start, end, m);
    }

    private static long getGcCount() {
        long sum = 0;
        List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean b : beans) {
            long c = b.getCollectionCount();
            if (c >= 0) sum += c;
        }
        return sum;
    }

    private static long usedMemory() {
        Runtime r = Runtime.getRuntime();
        return r.totalMemory() - r.freeMemory();
    }

    private static long usedMemoryStable() {
        Runtime r = Runtime.getRuntime();
        System.gc();
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        return r.totalMemory() - r.freeMemory();
    }

    private static long toMs(long nanos) {
        return (nanos + 500_000L) / 1_000_000L;
    }
}