package com.citizenweb.training.reactivelifecyclemanager.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class LifecycleManager implements LifecycleHelper {
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Monitorable monitor;
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Set<Task> tasks;
    private final ConcurrentHashMap<Task,Integer> scoreMap = new ConcurrentHashMap<>();

    public LifecycleManager(String lifecycleName, Set<Task> tasks) {
        this.monitor = new Monitor(EventType.LIFECYCLE,lifecycleName);
        this.tasks = tasks;
    }

    public void execute() {
        Set<Task> tasksWithoutSuccessors = LifecycleHelper.getLastTasks(this.tasks);
        List<Set<Task>> allPaths = new ArrayList<>();
        tasksWithoutSuccessors.forEach(task -> {
            for (Task previousTask : task.getPredecessors()) {
                Set<Task> taskPath = new HashSet<>();
                taskPath.add(task);
                LifecycleHelper.computeAllPaths(allPaths,taskPath,previousTask);
            }
        });
        Map<Task,Integer> tasksScores = LifecycleHelper.computeTasksScores(this.tasks);
        Map<Set<Task>,Integer> pathsScores = LifecycleHelper.computePathsScores(tasksScores,allPaths);
        pathsScores.forEach( (path,pathWeight) -> {
            Set<String> tasksNames = LifecycleHelper.translateFromTaskToString(path);
            System.out.printf("Path : %s -> weight = %d\n",tasksNames,pathWeight);
        });
    }

}
