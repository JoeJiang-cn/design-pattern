package com.joe.pattern.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Joe
 * 增强了并发性，但有重复计算的问题
 * 2021/10/17 13:36
 */
public class Memoizer2<A, V> implements Computable<A, V> {
    private final Map<A, V> cache = new ConcurrentHashMap<>();
    private final Computable<A ,V> c;

    public Memoizer2(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}
