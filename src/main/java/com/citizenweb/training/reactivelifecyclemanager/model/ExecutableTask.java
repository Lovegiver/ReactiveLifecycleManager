package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.exception.TaskExecutionException;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * An {@link ExecutableTask} produces a {@link Publisher} of T in exchange for
 * an {@link java.lang.reflect.Array} of {@link Publisher} known for being its <b>predecessors</b>.<br>
 * Of course, this {@link java.lang.reflect.Array} may be empty, what should be
 * signaled by the use of {@link ExecutableTask#FIRST_TASK_NO_ARGS} argument.<br>
 *
 * @param <T> the parameterized type of object produced by the {@link #execute(Publisher[])} method
 */
@FunctionalInterface
public interface ExecutableTask<T> {
    /** Contains all the logic to be applied when an {@link ExecutableTask}, or its
     * wrapper, a {@link Task}, is executed.
     */
    Publisher<T> execute(Publisher<?>[] inputs) throws TaskExecutionException;
    /** First tasks in the whole chain don't have any <b>predecessors</b>,
     * what is signaled by the use of an empty array as argument.
     */
    Mono<?>[] FIRST_TASK_NO_ARGS = {};
}
