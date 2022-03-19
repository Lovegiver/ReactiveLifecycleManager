package com.citizenweb.training.reactivelifecyclemanager.exception;

public class TaskExecutionException extends RuntimeException {
    public TaskExecutionException(String errMessage, Throwable cause) {
        super(errMessage,cause);
    }
}
