package com.joe.pattern.concurrency;

import java.util.concurrent.*;

/**
 * @author Joe
 * 缓存计算结果，最终实现
 * 2021/10/17 13:08
 */
public class Memoizer<A, V> implements Computable<A, V> {
    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if (f == null) {
            Callable<V> eval = new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return c.compute(arg);
                }
            };
            FutureTask<V> ft = new FutureTask<>(eval);
            // 实现原子性的若没有则添加
            f = cache.putIfAbsent(arg, ft);
            if (f == null) {
                // 说明是新加入缓存的，执行
                f = ft;
                ft.run();
            }
        }
        try {
            // 这个地方阻塞直到返回结果
            return f.get();
        } catch (ExecutionException e) {
            throw new InterruptedException();
        }
    }
}


