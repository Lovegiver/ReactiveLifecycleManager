package com.citizenweb.training.reactivelifecyclemanager.exception;

import com.citizenweb.training.reactivelifecyclemanager.model.Task;

public class TaskRankingException extends Exception {
    public TaskRankingException(String errMessage, Task task) {
        super(String.format("Task [ %s ] -> [ %s ]",
                task, errMessage));
    }
}
