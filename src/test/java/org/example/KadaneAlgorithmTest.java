package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;;

import java.util.function.IntSupplier;

public class KadaneAlgorithmTest {

    @Test
    public void testEmpty() {
        KadaneAlgorithm.Result r = KadaneAlgorithm.run(new int[]{});
        Assertions.assertEquals(0, r.metrics.inputSize);
        Assertions.assertEquals(0, r.maxSum);
        Assertions.assertEquals(-1, r.startIndex);
    }

    @Test
    public void testSinglePositive() {
        KadaneAlgorithm.Result r = KadaneAlgorithm.run(new int[]{5});
        Assertions.assertEquals(5, r.maxSum);
        Assertions.assertEquals(0, r.startIndex);
        Assertions.assertEquals(0, r.endIndex);
    }

    @Test
    public void testSingleNegative() {
        KadaneAlgorithm.Result r = KadaneAlgorithm.run(new int[]{-7});
        Assertions.assertEquals(-7, r.maxSum);
        Assertions.assertEquals(0, r.startIndex);
        Assertions.assertEquals(0, r.endIndex);
    }

    @Test
    public void testAllNegative() {
        int[] a = {-3, -1, -7, -4};
        KadaneAlgorithm.Result r = KadaneAlgorithm.run(a);
        Assertions.assertEquals(-1, r.maxSum);
        Assertions.assertEquals(1, r.startIndex);
        Assertions.assertEquals(1, r.endIndex);
    }

    @Test
    public void testMixed() {
        int[] a = { -2, -3, 4, -1, -2, 1, 5, -3 };
        KadaneAlgorithm.Result r = KadaneAlgorithm.run(a);
        Assertions.assertEquals(7, r.maxSum); // 4 + -1 + -2 + 1 + 5 = 7
        Assertions.assertEquals(2, r.startIndex);
        Assertions.assertEquals(6, r.endIndex);
    }

    @Test
    public void testDuplicates() {
        int[] a = {2,2,2,2};
        KadaneAlgorithm.Result r = KadaneAlgorithm.run(a);
        Assertions.assertEquals(8, r.maxSum);
        Assertions.assertEquals(0, r.startIndex);
        Assertions.assertEquals(3, r.endIndex);
    }

    @Test
    public void testLargeValuesNoOverflow() {
        int[] a = {Integer.MAX_VALUE, -1, Integer.MAX_VALUE};
        KadaneAlgorithm.Result r = KadaneAlgorithm.run(a);
        Assertions.assertEquals((long)Integer.MAX_VALUE - 1 + (long)Integer.MAX_VALUE, r.maxSum);
    }

    @Test
    public void testStreamEquivalentToArray() {
        int[] a = { -2, 1, -3, 4, -1, 2, 1, -5, 4 };
        KadaneAlgorithm.Result r1 = KadaneAlgorithm.run(a);

        // Implement our own IntSupplier for the stream version
        class ArraySupplier implements IntSupplier {
            private int idx = 0;
            private final int[] arr;
            ArraySupplier(int[] arr) { this.arr = arr; }

            @Override
            public int getAsInt() {
                if (idx >= arr.length)
                    throw new IllegalStateException("Supplier called too many times");
                return arr[idx++];
            }
        }

        KadaneAlgorithm.Result r2 = KadaneAlgorithm.runStream(new ArraySupplier(a), a.length);

        Assertions.assertEquals(r1.maxSum, r2.maxSum);
        Assertions.assertEquals(r1.startIndex, r2.startIndex);
        Assertions.assertEquals(r1.endIndex, r2.endIndex);
    }
}