package org.example;

public class KadaneAlgorithm {

    public static class Result {
        public final int maxSum;
        public final int start;
        public final int end;

        public Result(int maxSum, int start, int end) {
            this.maxSum = maxSum;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return String.format("MaxSum=%d, Start=%d, End=%d", maxSum, start, end);
        }
    }

    /**
     * Kadane's Algorithm (maximum subarray sum with position tracking)
     * Integrated with AlgorithmMetrics for runtime and comparisons.
     */
    public static Result findMaxSubarray(int[] arr, AlgorithmMetrics metrics) {
        metrics.start();

        if (arr == null || arr.length == 0) {
            metrics.stop();
            return new Result(0, -1, -1);
        }

        int maxSoFar = arr[0];
        int maxEndingHere = arr[0];
        int start = 0, end = 0, tempStart = 0;

        for (int i = 1; i < arr.length; i++) {
            metrics.incrementComparisons(); // compare sum reset
            if (maxEndingHere + arr[i] < arr[i]) {
                maxEndingHere = arr[i];
                tempStart = i;
            } else {
                maxEndingHere += arr[i];
            }

            metrics.incrementComparisons(); // compare max update
            if (maxEndingHere > maxSoFar) {
                maxSoFar = maxEndingHere;
                start = tempStart;
                end = i;
            }
        }

        metrics.stop();
        return new Result(maxSoFar, start, end);
    }
}