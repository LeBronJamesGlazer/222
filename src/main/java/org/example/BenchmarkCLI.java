package org.example;

import java.util.Random;
import java.util.Scanner;
import java.util.function.IntSupplier;

public class BenchmarkCLI {

    /**
     *
     *  Run automatic benchmark (sizes 10² to 10⁸ 3 iterations):
     *  mvn compile exec:java -Dexec.mainClass="org.example.BenchmarkCLI" \-Dexec.args="--mode array --out metrics.csv"
     *
     *  Run in interactive mode (enter sizes manually):
     *  mvn compile exec:java -Dexec.mainClass="org.example.BenchmarkCLI" \-Dexec.args="--interactive --mode array --out manual_metrics.csv"
     */

    public static void main(String[] args) throws Exception {
        int minExp = 2;
        int maxExp = 8;
        int iterations = 3;
        String out = "metrics.csv";
        String mode = "stream";
        long seed = System.currentTimeMillis();
        boolean interactive = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--minExp": minExp = Integer.parseInt(args[++i]); break;
                case "--maxExp": maxExp = Integer.parseInt(args[++i]); break;
                case "--iterations": iterations = Integer.parseInt(args[++i]); break;
                case "--out": out = args[++i]; break;
                case "--mode": mode = args[++i]; break;
                case "--seed": seed = Long.parseLong(args[++i]); break;
                case "--interactive": interactive = true; break;
                default:
                    System.err.println("Unknown arg: " + args[i]);
            }
        }

        CsvWriter csv = new CsvWriter(out);
        Metrics template = new Metrics();
        csv.writeHeader(template.csvHeader());

        if (interactive) {
            runInteractive(csv, mode, seed);
        } else {
            runAutomatic(csv, mode, seed, minExp, maxExp, iterations);
        }
    }

    private static void runAutomatic(CsvWriter csv, String mode, long seed,
                                     int minExp, int maxExp, int iterations) throws Exception {
        Random rng = new Random(seed);
        System.out.printf("Benchmark start: 10^%d .. 10^%d, iterations=%d, mode=%s%n",
                minExp, maxExp, iterations, mode);

        for (int e = minExp; e <= maxExp; e++) {
            long size = (long) Math.pow(10, e);
            for (int iter = 0; iter < iterations; iter++) {
                runOnce(csv, mode, rng, size, e, iter);
            }
        }
        System.out.println("Automatic benchmark finished.");
    }

    private static void runInteractive(CsvWriter csv, String mode, long seed) throws Exception {
        Scanner sc = new Scanner(System.in);
        Random rng = new Random(seed);
        System.out.println("Interactive Benchmark Mode (Kadane Algorithm)");
        System.out.println("Type input size (number of elements), or 0 to exit.");

        while (true) {
            System.out.print("Enter input size: ");
            long size = sc.nextLong();
            if (size <= 0) break;
            runOnce(csv, mode, rng, size, -1, 1);
            System.gc();
        }
        System.out.println("Interactive benchmark finished. Metrics saved to CSV.");
    }

    private static void runOnce(CsvWriter csv, String mode, Random rng, long size, int exp, int iter) throws Exception {
        String label = (exp >= 0 ? "10^" + exp : String.valueOf(size));
        System.out.printf("Running size=%d (%s) iter=%d ...%n", size, label, iter);

        KadaneAlgorithm.Result result;

        if ("array".equalsIgnoreCase(mode)) {
            if (size > Integer.MAX_VALUE) {
                System.err.println("Size > Integer.MAX_VALUE — switch to stream mode!");
                return;
            }
            int n = (int) size;

            // --- timing start ---
            long startTime = System.nanoTime();

            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rng.nextInt(101) - 50;

            KadaneAlgorithm.Result r = KadaneAlgorithm.run(arr);

            long endTime = System.nanoTime();
            // --- timing end ---

            r.metrics.runTimeMs = endTime - startTime;

            long estimatedArrayMem = (long) n * Integer.BYTES + 16;
            long algorithmOverhead = 1024 * 50;
            r.metrics.memoryUsageBytes = estimatedArrayMem + algorithmOverhead;

            result = r;
        } else {
            IntSupplier gen = new IntSupplier() {
                private final Random local = new Random(rng.nextLong());
                @Override
                public int getAsInt() { return local.nextInt(101) - 50; }
            };

            KadaneAlgorithm.Result r = KadaneAlgorithm.runStream(gen, size);

            long estimatedStreamMem = (long) size * 4 + 16;
            long streamOverhead = 1024 * 30; // ~30 KB
            r.metrics.memoryUsageBytes = estimatedStreamMem + streamOverhead;

            result = r;
        }

        csv.appendLine(result.metrics.toCsvLine());
        System.out.println(" → " + result);
    }
}