package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.service.TaskHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import reactor.core.publisher.Mono;

import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Task implements TaskHelper {
    /** Monitor is the part to be published to web page */
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Monitorable monitor;
    /** {@link Set} of {@link Task} to be done <b>before</b>> THIS one */
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Set<Task> predecessors;
    /** {@link Set} of {@link Task} to be done <b>after</b>> THIS one */
    @EqualsAndHashCode.Include
    @ToString.Include
    private Set<Task> successors;
    /** Domain logic encapsulated into THIS {@link Task} */
    private final ExecutableTask<?> executableTask;
    /** Array containing the result {@link Mono}s of all previous {@link Task}s */
    private final Mono<?>[] inputArray;
    /** The result produced by THIS {@link Task} */
    private final Mono<?> expectedResult;
    /** The initial rank for THIS {@link Task} */
    @EqualsAndHashCode.Include
    @ToString.Include
    private int rank = -1;

    public Task(String taskName, ExecutableTask<?> executableTask, Set<Task> predecessors) {
        this.monitor = new Monitor(EventType.TASK,taskName);
        this.executableTask = executableTask;
        this.predecessors = predecessors;
        this.inputArray = TaskHelper.getPreviousTasksResultsPublisher(this);
        this.expectedResult = executableTask.execute(this.inputArray);
    }

    public Mono<?> execute() {
        predecessors.forEach(task ->
                task.getExpectedResult().log().subscribe(System.out::println));
        return this.executableTask.execute(this.inputArray);
    }
}
