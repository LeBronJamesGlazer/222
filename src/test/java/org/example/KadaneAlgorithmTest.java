package org.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class KadaneAlgorithmTest {

    private void printMetrics(String testName, AlgorithmMetrics m) {
        System.out.println("----------------------------------------------------");
        System.out.println("Test: " + testName);
        System.out.printf("Input size: %d%n", m.getInputSize());
        System.out.printf("Comparisons: %d%n", m.getComparisons());
        System.out.printf("Swaps: %d%n", m.getSwaps());
        System.out.printf("Recursive calls: %d%n", m.getRecursiveCalls());
        System.out.printf("Max depth: %d%n", m.getMaxDepth());
        System.out.printf("Runtime: %.3f ms%n", m.getRuntimeMs());
        System.out.printf("Memory used: %d bytes%n", m.getMemoryUsedBytes());
        System.out.printf("GC count delta: %d%n", m.getGcCountChange());
        System.out.println("----------------------------------------------------\n");
    }

    @Test
    public void testTypicalArray() {
        int[] arr = {-2, -3, 4, -1, -2, 1, 5, -3};
        AlgorithmMetrics m = new AlgorithmMetrics(arr.length);
        KadaneAlgorithm.Result r = KadaneAlgorithm.findMaxSubarray(arr, m);

        assertEquals(7, r.maxSum);
        assertEquals(2, r.start);
        assertEquals(6, r.end);
        printMetrics("Typical Array", m);
    }

    @Test
    public void testAllNegative() {
        int[] arr = {-8, -3, -6, -2, -5, -4};
        AlgorithmMetrics m = new AlgorithmMetrics(arr.length);
        KadaneAlgorithm.Result r = KadaneAlgorithm.findMaxSubarray(arr, m);

        assertEquals(-2, r.maxSum);
        assertEquals(3, r.start);
        assertEquals(3, r.end);
        printMetrics("All Negative", m);
    }

    @Test
    public void testAllPositive() {
        int[] arr = {1, 2, 3, 4};
        AlgorithmMetrics m = new AlgorithmMetrics(arr.length);
        KadaneAlgorithm.Result r = KadaneAlgorithm.findMaxSubarray(arr, m);

        assertEquals(10, r.maxSum);
        assertEquals(0, r.start);
        assertEquals(3, r.end);
        printMetrics("All Positive", m);
    }

    @Test
    public void testSingleElement() {
        int[] arr = {5};
        AlgorithmMetrics m = new AlgorithmMetrics(arr.length);
        KadaneAlgorithm.Result r = KadaneAlgorithm.findMaxSubarray(arr, m);

        assertEquals(5, r.maxSum);
        assertEquals(0, r.start);
        assertEquals(0, r.end);
        printMetrics("Single Element", m);
    }

    @Test
    public void testEmptyArray() {
        int[] arr = {};
        AlgorithmMetrics m = new AlgorithmMetrics(arr.length);
        KadaneAlgorithm.Result r = KadaneAlgorithm.findMaxSubarray(arr, m);

        assertEquals(0, r.maxSum);
        assertEquals(-1, r.start);
        assertEquals(-1, r.end);
        printMetrics("Empty Array", m);
    }

    @Test
    public void testWithDuplicates() {
        int[] arr = {2, 2, -1, 2, 2, -1, 2};
        AlgorithmMetrics m = new AlgorithmMetrics(arr.length);
        KadaneAlgorithm.Result r = KadaneAlgorithm.findMaxSubarray(arr, m);

        assertEquals(8, r.maxSum);
        assertEquals(0, r.start);
        assertEquals(6, r.end);
        printMetrics("With Duplicates", m);
    }

    @Test
    public void testMetricsCollected() {
        int[] arr = {1, -2, 3, 5, -1};
        AlgorithmMetrics m = new AlgorithmMetrics(arr.length);
        KadaneAlgorithm.findMaxSubarray(arr, m);

        assertTrue(m.getComparisons() > 0);
        assertTrue(m.getRuntimeNs() > 0);
        assertEquals(arr.length, m.getInputSize());
        printMetrics("Metrics Collected", m);
    }
}