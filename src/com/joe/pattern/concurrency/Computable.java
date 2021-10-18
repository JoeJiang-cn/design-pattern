package com.joe.pattern.concurrency;

public interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
