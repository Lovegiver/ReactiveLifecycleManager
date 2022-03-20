package com.citizenweb.training.reactivelifecyclemanager.service;

import com.citizenweb.training.reactivelifecyclemanager.model.Task;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

public interface TaskHelper {
    static Set<Mono<?>> getPreviousTasksResultsMonos(Task task) {
        return task.getPreviousTasksSet().stream()
                .map(Task::getExpectedResult)
                .collect(Collectors.toSet());
    }
}
