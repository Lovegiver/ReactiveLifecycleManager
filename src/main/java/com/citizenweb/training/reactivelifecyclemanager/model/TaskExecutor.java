package com.citizenweb.training.reactivelifecyclemanager.model;

import java.util.concurrent.*;

public class TaskExecutor {
    private final static ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(4, 8,
            1L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1));
    public static ExecutorService getExecutor() { return EXECUTOR_SERVICE; }
}
