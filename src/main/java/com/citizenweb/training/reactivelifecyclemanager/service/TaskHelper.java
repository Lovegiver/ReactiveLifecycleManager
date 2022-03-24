package com.citizenweb.training.reactivelifecyclemanager.service;

import com.citizenweb.training.reactivelifecyclemanager.model.Task;
import org.reactivestreams.Publisher;

import java.util.Set;
import java.util.stream.Collectors;

public interface TaskHelper {
    static Publisher<?>[] getPreviousTasksResultsPublisher(Task task) {
        Publisher<?>[] previousTasksArray = {};
        Set<Publisher<?>> previousTasksSet = task.getPredecessors().stream()
                .map(Task::getExpectedResult)
                .collect(Collectors.toSet());
        return previousTasksSet.toArray(previousTasksArray);
    }
}
