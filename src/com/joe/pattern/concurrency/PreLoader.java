package com.joe.pattern.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Joe
 * 使用FutureTask提前加载稍后需要的数据
 * 2021/10/17 11:04
 */
public class PreLoader {
    private final FutureTask<ProductInfo> future = new FutureTask<ProductInfo>(new Callable<ProductInfo>() {
        @Override
        public ProductInfo call() throws Exception {
            return loadProductInfo();
        }
    });

    private final Thread thread = new Thread(future);

    public void start() {
        thread.start();
    }

    public ProductInfo get() throws Exception {
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw e;
        }
    }

    private static class ProductInfo {

    }

    private ProductInfo loadProductInfo() {
        return new ProductInfo();
    }
}
