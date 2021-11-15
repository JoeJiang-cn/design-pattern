package com.joe.pattern.concurrency;

import java.util.*;
import java.util.concurrent.*;

/**
 * 并行桶排序
 */
public class BucketSort {
    private final ExecutorService es = Executors.newFixedThreadPool(5);
    private ConcurrentSkipListMap<Integer, List<Integer>> bucket = new ConcurrentSkipListMap<>();

    /**
     * 返回排序后的数组
     * @param original
     * @param capacity
     * @return
     */
    public int[] calculate(int[] original, int capacity) throws InterruptedException {

        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original.length; j = j + capacity) {
                addToBucket(original[i], j, capacity);
            }
        }

        final CountDownLatch countDownLatch = new CountDownLatch(bucket.size());

        for (int i = 0; i < original.length; i = i + capacity) {
            final int index = i;
            es.submit(new Callable<List<Integer>>() {
                /**
                 * sort the list
                 * @return
                 * @throws Exception
                 */
                @Override
                public List<Integer> call() throws Exception {
                    List<Integer> l = bucket.get(index);
                    Collections.sort(l);
                    bucket.put(index, l);
                    countDownLatch.countDown();
                    return null;
                }
            });
        }

        countDownLatch.await();
        es.shutdown();

        List<Integer> result = new LinkedList<>();
        bucket.forEach((index, list) -> {
            result.addAll(list);
        });
        return result.stream().mapToInt(i -> i).toArray();
    }

    private void addToBucket(int a, int s, int capacity) {
        if (a >= s && a < s + capacity) {
            List<Integer> l = bucket.get(s);
            if (l == null) {
                l = new LinkedList<>();
                l.add(a);
                bucket.put(s, l);
            } else {
                l.add(a);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int[] original = new int[2000];
        Random rand = new Random();
        for (int i = 0; i < 2000; i++) {
            original[i] = rand.nextInt(1000);
            System.out.print(" " + original[i]);
        }

        System.out.println();
        System.out.println("After Sort...");

        int[] calculated = new BucketSort().calculate(original, 100);
        for (int i = 0; i < 2000; i++) {
            System.out.print(" " + calculated[i]);
        }
    }
}
