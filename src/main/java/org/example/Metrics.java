package org.example;

import java.time.Instant;

/**
 * Simple metric container for instrumentation. CSV-ready via toCsvLine.
 */
public class Metrics {
    public long comparisons = 0;
    public long swaps = 0;
    public int depth = 0;
    public long runTimeMs = 0;
    public long inputSize = 0;
    public long memoryUsageBytes = 0;
    public long gcCountDelta = 0;
    public int recursiveCalls = 0;
    public Instant timestamp = Instant.now();

    public String csvHeader() {
        return "timestamp;inputSize;runTimeMs;memoryUsageBytes;comparisons;swaps;depth;gcCountDelta;recursiveCalls";
    }

    public String toCsvLine() {
        return timestamp.toString() + ";" +
                inputSize + ";" +
                runTimeMs + ";" +
                memoryUsageBytes + ";" +
                comparisons + ";" +
                swaps + ";" +
                depth + ";" +
                gcCountDelta + ";" +
                recursiveCalls;
    }

    @Override
    public String toString() {
        return "Metrics[time=" + timestamp + ", input=" + inputSize + ", runMs=" + runTimeMs +
                ", mem=" + memoryUsageBytes + ", comps=" + comparisons + ", swaps=" + swaps +
                ", depth=" + depth + ", gc=" + gcCountDelta + ", recCalls=" + recursiveCalls + "]";
    }
}