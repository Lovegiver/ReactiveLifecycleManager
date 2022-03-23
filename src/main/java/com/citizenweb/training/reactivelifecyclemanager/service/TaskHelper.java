package com.citizenweb.training.reactivelifecyclemanager.service;

import com.citizenweb.training.reactivelifecyclemanager.model.Task;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

public interface TaskHelper {
    static Mono<?>[] getPreviousTasksResultsPublisher(Task task) {
        Mono<?>[] previousTasksArray = {};
        Set<Mono<?>> previousTasksSet = task.getPredecessors().stream()
                .map(Task::getExpectedResult)
                .collect(Collectors.toSet());
        return previousTasksSet.toArray(previousTasksArray);
    }
}
