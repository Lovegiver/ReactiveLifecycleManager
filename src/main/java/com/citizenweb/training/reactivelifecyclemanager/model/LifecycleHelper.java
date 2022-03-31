package com.citizenweb.training.reactivelifecyclemanager.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface LifecycleHelper {

    Predicate<Task> isFirst = task -> task.getPredecessors().isEmpty();
    Predicate<Task> isLast = task -> task.getSuccessors().isEmpty();

    static Set<Task> getLastTasks(Set<Task> allTasks) {
        return allTasks.stream()
                .filter(isLast)
                .collect(Collectors.toSet());
    }

    /*static LinkedHashSet<Task> orderTasks(Set<Task> tasks,
          Set<Task> tasksWithoutSuccessors, ConcurrentHashMap<Task, Integer> scoreMap) {
        LinkedHashSet<Task> orderedTasks = new LinkedHashSet<>(tasks.size());
        ConcurrentHashMap<Task,Set<Task>> paths = new ConcurrentHashMap<>();
        for (Task task : tasksWithoutSuccessors) {
            ConcurrentHashMap<Task,Integer> path = computePath(new ConcurrentHashMap<>(),task);
            System.out.printf("Path for [ %s ] -> [ %s ]%n",task.getMonitor().getName(),path);
        }
        return null;
    }

    private void scoreTask(ConcurrentHashMap<Task, Integer> scoreMap, Task task) {
        if (scoreMap.get(task) == null) {
            scoreMap.put(task,1);
        } else {
            int value = scoreMap.get(task);
            scoreMap.put(task,++value);
        }
    }*/

    static ConcurrentHashMap<Task,Integer> computePath(ConcurrentHashMap<Task,Integer> path, Task task) {
        path.merge(task, 1, Integer::sum);
        for (Task t : task.getPredecessors()) {
            computePath(path,t);
        }
        return path;
    }

    static List<Set<Task>> computeAllPaths(List<Set<Task>> allPaths, Set<Task> path, Task task) {
        path.add(task);
        if (!isFirst.test(task)) {
            for (Task t : task.getPredecessors()) {
                Set<Task> pathCopy = new LinkedHashSet<>(path);
                computeAllPaths(allPaths,pathCopy,t);
            }
        } else {
            allPaths.add(path);
        }
        return allPaths;
    }

    static Map<Task,Integer> computeTasksScores(Set<Task> tasks) {
        Map<Task,Integer> tasksScores = new ConcurrentHashMap<>();
        tasks.forEach(task -> {
            int score;
            if (isFirst.test(task)) {
                score = task.getSuccessors().size();
            } else if (isLast.test(task)) {
                score = task.getPredecessors().size();
            } else {
                score = task.getSuccessors().size() * task.getPredecessors().size();
            }
            tasksScores.put(task,score);
        });
        return tasksScores;
    }

    static Map<Set<Task>,Integer> computePathsScores(Map<Task,Integer> tasksScore, List<Set<Task>> paths) {
        Map<Set<Task>,Integer> pathsWeight = new HashMap<>(paths.size());
        for (Set<Task> path : paths) {
            int pathWeight = 0;
            for (Task task : path) {
                pathWeight += tasksScore.get(task);
            }
            pathsWeight.put(path,pathWeight);
        }
        return pathsWeight.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
    }

    static Set<String> translateFromTaskToString(Set<Task> tasks) {
        return tasks.stream()
                .map(task -> task.getMonitor().getName())
                .collect(Collectors.toSet());
    }

}
