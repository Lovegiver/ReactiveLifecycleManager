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
    private final UUID id = UUID.randomUUID();
    private final ExecutableTask<?> executableTask;
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Set<Task> previousTasksSet;
    private final Flux<Mono<?>> inputFlux;
    private final Mono<?> expectedResult;
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String taskName;

    public Task(String taskName, ExecutableTask<?> executableTask, Set<Task> previousTasksSet) {
        this.taskName = taskName;
        this.executableTask = executableTask;
        this.previousTasksSet = previousTasksSet;
        Set<Mono<?>> previousTasksResults = TaskHelper.getPreviousTasksResultsMonos(this);
        Flux<Mono<?>> expectedInputs = Flux.fromIterable(previousTasksResults);
        this.inputFlux = expectedInputs;
        this.expectedResult = executableTask.execute(expectedInputs);
    }

    public Mono<?> execute() {
        previousTasksSet.forEach(task ->
                task.getExpectedResult().log().subscribe(System.out::println));
        return this.executableTask.execute(this.inputFlux);
    }
}
