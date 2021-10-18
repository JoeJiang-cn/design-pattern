package com.joe.pattern.concurrency;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * @author Joe
 * 使用Semaphore为容器设置边界
 * 2021/10/17 12:15
 */
public class BoundedHashSet<T> {
    private final Set<T> set;
    private final Semaphore sem;

    public BoundedHashSet(int bound) {
        this.set = Collections.synchronizedSet(new HashSet<>());
        // sem的许可数量 == 容器的边界
        sem = new Semaphore(bound);
    }

    public boolean add(T o) throws InterruptedException {
        // 向set添加一个元素前，首先要获取一个许可
        // 否则会阻塞
        sem.acquire();
        boolean wasAdded = false;
        try {
            // 这个地方会出现多线程的竞争，因此set要设置为同步
            wasAdded = set.add(o);
            return wasAdded;
        } finally {
            if (!wasAdded) {
                // 如果add没有添加任何元素，立即释放许可
                sem.release();
            }
        }
    }

    public boolean remove(Object o) {
        boolean wasRemoved = set.remove(o);
        if (wasRemoved) {
            sem.release();
        }
        return wasRemoved;
    }
}
