package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.exception.TaskExecutionException;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * An {@link ExecutableTask} produces a {@link Mono} of T in exchange for an {@link java.lang.reflect.Array} of {@link Mono}.<br>
 * Of course, this {@link java.lang.reflect.Array} may be empty.<br>
 *
 * @param <T> the parameterized type of object produced by the {@link #execute(Publisher[])} method
 */
@FunctionalInterface
public interface ExecutableTask<T> {
    Publisher<T> execute(Publisher<?>[] inputs) throws TaskExecutionException;
}
