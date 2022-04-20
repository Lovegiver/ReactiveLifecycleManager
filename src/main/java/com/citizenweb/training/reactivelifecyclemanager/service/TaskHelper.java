package com.citizenweb.training.reactivelifecyclemanager.service;

import com.citizenweb.training.reactivelifecyclemanager.model.EventStatus;
import com.citizenweb.training.reactivelifecyclemanager.model.Task;
import org.reactivestreams.Publisher;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface TaskHelper {

    static Publisher<?>[] getPreviousTasksResultsPublisher(Task task) {
        Publisher<?>[] previousTasksArray = {};
        Set<Publisher<?>> previousTasksSet = task.getPredecessors().stream()
                .map(Task::getExpectedResult)
                .collect(Collectors.toSet());
        return previousTasksSet.toArray(previousTasksArray);
    }

    static void setThisAsSuccessor(Task task) {
        task.getPredecessors().forEach(p -> p.getSuccessors().add(task));
    }

    Consumer<Task> updateTaskOnError = task -> {
        task.getMonitor().setStatus(EventStatus.IN_ERROR);
    };

}
