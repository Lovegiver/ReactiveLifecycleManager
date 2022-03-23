package com.citizenweb.training.reactivelifecyclemanager.model;

import java.util.Set;

public interface LifecycleHelper {
    static int getMaxRank(Set<Task> tasks) {
        return tasks.stream()
                .mapToInt(Task::getRank)
                .max()
                .orElse(-1);
    }
}
