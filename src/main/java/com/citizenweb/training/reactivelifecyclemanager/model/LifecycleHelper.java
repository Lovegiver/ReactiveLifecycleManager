package com.citizenweb.training.reactivelifecyclemanager.model;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface LifecycleHelper {

    Predicate<Task> isFirst = task -> task.getPredecessors().isEmpty();
    Predicate<Task> isLast = task -> task.getSuccessors().isEmpty();

    static Set<Task> getTerminalTasks(Set<Task> allTasks, EventStatus status) {
        if (EventStatus.ALL.equals(status)) {
            return allTasks.stream()
                    .filter(isLast)
                    .collect(Collectors.toSet());
        } else {
            return allTasks.stream()
                    .filter(isLast)
                    .filter(task -> status.equals(task.getMonitor().getStatus()))
                    .collect(Collectors.toSet());
        }
    }

    static void findAllTasksFromTree(Set<Task> path, Task task) {
        path.add(task);
        if (!isFirst.test(task)) {
            for (Task t : task.getPredecessors()) {
                findAllTasksFromTree(path, t);
            }
        }
    }

}
