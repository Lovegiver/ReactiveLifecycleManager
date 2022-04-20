package com.citizenweb.training.reactivelifecyclemanager.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        Set<Task> allTriggeringTasks = LifecycleHelper.getTerminalTasks(this.tasks, EventStatus.ALL);
        while (allTriggeringTasks.stream().anyMatch(task -> EventStatus.NEW.equals(task.getMonitor().getStatus()))) {
            Set<Task> toBeTriggeredTasks = LifecycleHelper.getTerminalTasks(this.tasks, EventStatus.NEW);
            ConcurrentHashMap<Task,Integer> scoredPaths = new ConcurrentHashMap<>();
            toBeTriggeredTasks.forEach(task -> {
                Set<Task> pathForTask = new HashSet<>();
                LifecycleHelper.findAllTasksFromTree(pathForTask, task);
                log.info("Task [ {} ] sub-tasks -> [ {} ]", task.getMonitor().getName(), pathForTask.stream()
                        .map(t -> t.getMonitor().getName()).distinct().toArray());
                int pathWeight = pathForTask.stream()
                        .filter(t -> EventStatus.NEW.equals(t.getMonitor().getStatus()))
                        .mapToInt(Task::computeScore)
                        .sum();
                scoredPaths.put(task, pathWeight);
                log.info("Task [ {} ] -> score [ {} ]", task.getMonitor().getName(), pathWeight);
            });
            Map.Entry<Task,Integer> entry = Collections.max(scoredPaths.entrySet(), Map.Entry.comparingByValue());
            Task taskToExecute = entry.getKey();
            taskToExecute.getMonitor().setStatus(EventStatus.RUNNING);
            //noinspection ReactiveStreamsUnusedPublisher
            if (taskToExecute.getExpectedResult() instanceof Mono<?>) {
                Mono.from(taskToExecute.execute()).log().subscribe();
            } else {
                Flux.from(taskToExecute.execute()).log().subscribe();
            }
            taskToExecute.getMonitor().setStatus(EventStatus.DONE);
        }

        for (Task task : this.tasks) {
            var monitor = task.getMonitor();
            log.info("Task [ {} ] -> {}", monitor.getName(), monitor);
        }
    }

}
