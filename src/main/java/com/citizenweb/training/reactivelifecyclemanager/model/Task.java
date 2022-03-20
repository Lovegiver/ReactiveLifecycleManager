package com.citizenweb.training.reactivelifecyclemanager.model;

import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Task {
    private final ExecutableTask<?> executableTask;
    private final Set<Task> previousTasksSet;
    private final Flux<Mono<?>> inputFlux;
    private final Mono<?> expectedResult;
    private final String taskName;

    public Task(String taskName, ExecutableTask<?> executableTask, Set<Task> previousTasksSet) {
        this.taskName = taskName;
        this.executableTask = executableTask;
        this.previousTasksSet = previousTasksSet;
        Set<Mono<?>> previousTasksResults = previousTasksSet.stream()
                .map(Task::getExpectedResult)
                .collect(Collectors.toSet());
        Mono<?>[] resultArray = new Mono[previousTasksResults.size()];
        Flux<Mono<?>> expectedInputs = Flux.just(previousTasksResults.toArray(resultArray));
        this.inputFlux = expectedInputs;
        this.expectedResult = executableTask.execute(expectedInputs);
    }

    public Mono<?> execute() {
        previousTasksSet.forEach(task ->
                task.getExpectedResult().log().subscribe(System.out::println));
        return this.executableTask.execute(this.inputFlux);
    }
}
