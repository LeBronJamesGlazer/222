package org.example;

import java.io.FileWriter;
import java.io.IOException;

public class MetricsRecorder {
    private final String filePath;

    public MetricsRecorder(String filePath) {
        this.filePath = filePath;
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Algorithm,InputSize,Comparisons,Swaps,RecursiveCalls,MaxDepth,Runtime(ms),MemoryUsed(bytes),GCCount\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize CSV file: " + e.getMessage());
        }
    }

    public void record(String algorithmName, AlgorithmMetrics m) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.append(String.format("%s,%d,%d,%d,%d,%d,%.3f,%d,%d\n",
                    algorithmName,
                    m.getInputSize(),
                    m.getComparisons(),
                    m.getSwaps(),
                    m.getRecursiveCalls(),
                    m.getMaxDepth(),
                    m.getRuntimeMs(),
                    m.getMemoryUsedBytes(),
                    m.getGcCountChange()
            ));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write metrics to CSV: " + e.getMessage());
        }
    }
}
