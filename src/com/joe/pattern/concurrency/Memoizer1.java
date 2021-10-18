package com.joe.pattern.concurrency;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joe
 * 确保了线程安全性，但是每次只有一个线程能够执行compute
 * 2021/10/17 13:29
 */
public class Memoizer1<A, V> implements Computable<A, V>{
    private final Map<A, V> cache = new HashMap<>();
    private final Computable<A, V> c;

    public Memoizer1(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public synchronized V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}
