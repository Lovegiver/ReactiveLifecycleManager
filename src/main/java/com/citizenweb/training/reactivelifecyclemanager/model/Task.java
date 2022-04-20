package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.exception.TaskExecutionException;
import com.citizenweb.training.reactivelifecyclemanager.service.TaskHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@Log4j2
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Task implements TaskHelper {
    public final static Predicate<Task> isFirst = task -> task.getPredecessors().isEmpty();
    public final static Predicate<Task> isLast = task -> task.getSuccessors().isEmpty();
    /**
     * Monitor is the part to be published to web page
     */
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Monitorable monitor;
    /**
     * {@link Set} of {@link Task} to be done <b>before</b>> THIS one
     */
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Set<Task> predecessors;
    /**
     * Domain logic encapsulated into THIS {@link Task}
     */
    private final ExecutableTask<?> executableTask;
    /**
     * Array containing the result {@link Mono}s of all previous {@link Task}s
     */
    private final Publisher<?>[] inputArray;
    /**
     * The result produced by THIS {@link Task}
     */
    private final Publisher<?> expectedResult;
    /**
     * {@link Set} of {@link Task} to be done <b>after</b> THIS one
     */
    private Set<Task> successors = new HashSet<>();

    public Task(String taskName, ExecutableTask<?> executableTask,
                Set<Task> predecessors) {
        this.monitor = new Monitor(EventType.TASK, taskName);
        this.executableTask = executableTask;
        this.predecessors = predecessors;
        this.inputArray = TaskHelper.getPreviousTasksResultsPublisher(this);
        this.expectedResult = executableTask.execute(this.inputArray);
        TaskHelper.setThisAsSuccessor(this);
    }

    public boolean isDone() {
        return EventStatus.DONE.equals(this.getMonitor().getStatus());
    }

    public Publisher<?> execute() {
        Scheduler scheduler = Schedulers.fromExecutor(TaskExecutor.getExecutor());
        predecessors
                .stream().filter(task -> EventStatus.NEW.equals(task.getMonitor().getStatus()))
                .forEach(task -> {
                    var monitor = task.getMonitor();
                    monitor.setStatus(EventStatus.RUNNING);
                    monitor.setStartingTime(System.currentTimeMillis());
                    log.info(String.format("Task [ %s ] to be started", monitor.getName()));
                    //noinspection ReactiveStreamsUnusedPublisher
                    if (task.getExpectedResult() instanceof Mono<?>) {
                        Mono.from(task.getExpectedResult())
                                .log()
                                .subscribeOn(scheduler)
                                .doOnError(throwable -> {
                                    TaskHelper.updateTaskOnError.accept(task);
                                    log.error(throwable.getMessage());
                                })
                                .subscribe(o -> log.info(String.format("Subscribing to Task [ %s ]", monitor.getName())));
                    } else //noinspection ReactiveStreamsUnusedPublisher
                        if (task.getExpectedResult() instanceof Flux<?>) {
                            Flux.from(task.getExpectedResult())
                                    .log()
                                    .subscribeOn(scheduler)
                                    .doOnError(throwable -> {
                                        TaskHelper.updateTaskOnError.accept(task);
                                        log.error(throwable.getMessage());
                                    })
                                    .subscribe();
                        } else {
                            throw new TaskExecutionException("Neither Mono nor Flux", new Throwable());
                        }
                    monitor.setStatus(EventStatus.DONE);
                    monitor.setEndingTime(System.currentTimeMillis());
                    monitor.setDurationMillis(this.monitor.getEndingTime()-this.monitor.getStartingTime());
                    log.info(String.format("Task [ %s ] succeeded", monitor.getName()));
                });
        return Flux.defer(() -> Flux.from(this.expectedResult));
    }

    public int computeScore() {
        int score;
        if (isFirst.test(this)) {
            score = this.getSuccessors().size();
        } else if (isLast.test(this)) {
            score = this.getPredecessors().size();
        } else {
            score = this.getSuccessors().size() * this.getPredecessors().size();
        }
        return score;
    }

}
