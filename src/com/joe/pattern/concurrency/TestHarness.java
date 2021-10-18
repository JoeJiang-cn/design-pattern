package com.joe.pattern.concurrency;

import java.util.concurrent.CountDownLatch;

/**
 * @author Joe
 * 统计n个线程并发执行某任务的时间
 * 2021/10/17 10:53
 */
public class TestHarness {
    public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        startGate.await(); // 目的是为了让主线程能同时释放所有工作线程
                        try {
                            task.run();
                        } finally {
                            endGate.countDown();
                        }
                    } catch (InterruptedException ignored) {

                    }
                }
            };
            t.start();
        }

        long start = System.nanoTime();
        startGate.countDown();
        endGate.await(); // 直到所有工作线程执行完后放行
        long end = System.nanoTime();
        return end - start;
    }
}
