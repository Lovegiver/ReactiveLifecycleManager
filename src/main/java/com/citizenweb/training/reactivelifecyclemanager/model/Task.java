package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.service.TaskHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Task implements TaskHelper {
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Monitorable monitor;
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Set<Task> predecessors;
    @EqualsAndHashCode.Include
    @ToString.Include
    private Set<Task> successors;
    private final ExecutableTask<?> executableTask;
    private final Flux<Mono<?>> inputFlux;
    private final Mono<?> expectedResult;
    @EqualsAndHashCode.Include
    @ToString.Include
    private int rank = -1;

    public Task(String taskName, ExecutableTask<?> executableTask, Set<Task> predecessors) {
        this.monitor = new Monitor(EventType.TASK,taskName);
        this.executableTask = executableTask;
        this.predecessors = predecessors;
        Set<Mono<?>> previousTasksResults = TaskHelper.getPreviousTasksResultsPublisher(this);
        Flux<Mono<?>> expectedInputs = Flux.fromIterable(previousTasksResults);
        this.inputFlux = expectedInputs;
        this.expectedResult = executableTask.execute(expectedInputs);
    }

    public Mono<?> execute() {
        predecessors.forEach(task ->
                task.getExpectedResult().log().subscribe(System.out::println));
        return this.executableTask.execute(this.inputFlux);
    }
}
