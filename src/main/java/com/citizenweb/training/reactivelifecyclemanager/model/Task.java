package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.exception.TaskExecutionException;
import com.citizenweb.training.reactivelifecyclemanager.service.TaskHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
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
    private final Publisher<?>[] inputArray;
    /** The result produced by THIS {@link Task} */
    private final Publisher<?> expectedResult;
    /** The initial rank for THIS {@link Task} */
    @EqualsAndHashCode.Include
    @ToString.Include
    private int rank = -1;
    private final ConcurrentHashMap<Task,Boolean> previousTasksStates = new ConcurrentHashMap<>();

    public Task(String taskName, ExecutableTask<?> executableTask, Set<Task> predecessors) {
        this.monitor = new Monitor(EventType.TASK,taskName);
        this.executableTask = executableTask;
        this.predecessors = predecessors;
        this.inputArray = TaskHelper.getPreviousTasksResultsPublisher(this);
        this.expectedResult = executableTask.execute(this.inputArray);
    }

    public Publisher<?> execute() {
        Scheduler scheduler = Schedulers.fromExecutor(TaskExecutor.getExecutor());
        predecessors.parallelStream().forEach(task -> {
            Disposable disposable;
            //noinspection ReactiveStreamsUnusedPublisher
            if (task.getExpectedResult() instanceof Mono<?>) {
                disposable = Mono.from(task.getExpectedResult())
                        .log()
                        .subscribeOn(scheduler)
                        .subscribe(log::info);
            } else //noinspection ReactiveStreamsUnusedPublisher
                if (task.getExpectedResult() instanceof Flux<?>) {
                disposable = Flux.from(task.getExpectedResult())
                        .log()
                        .subscribeOn(scheduler)
                        .subscribe(log::info);
            } else {
                throw new TaskExecutionException("Neither Mono nor Flux",new Throwable());
            }
        });
        log.info("Previous tasks states -> " + this.previousTasksStates);
        return Flux.defer(() -> Flux.from(this.expectedResult));
    }

    public boolean areAllPreviousTasksDone() {
        return this.previousTasksStates.values().stream().allMatch(Boolean.TRUE::equals);
    }

}
