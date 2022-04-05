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

    static void computeAllPaths(List<Set<Task>> allPaths, Set<Task> path, Task task) {
        path.add(task);
        if (!isFirst.test(task)) {
            for (Task t : task.getPredecessors()) {
                Set<Task> pathCopy = new LinkedHashSet<>(path);
                computeAllPaths(allPaths, pathCopy, t);
            }
        } else {
            allPaths.add(path);
        }
    }

    static Map<Set<Task>,Integer> computePathsScores(List<Set<Task>> paths) {
        Map<Set<Task>,Integer> pathsWeight = new HashMap<>(paths.size());
        paths.forEach(path -> {
            int pathWeight = path.stream()
                    .filter(task -> !task.isDone())
                    .mapToInt(Task::computeScore)
                    .sum();
            pathsWeight.put(path, pathWeight);
        });
        return pathsWeight.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
    }

}
