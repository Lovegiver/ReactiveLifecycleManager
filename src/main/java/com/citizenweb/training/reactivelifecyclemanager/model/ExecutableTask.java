package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.exception.TaskExecutionException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * An {@link ExecutableTask} produces a {@link Mono} of T in exchange for a {@link Flux} of {@link Mono}.<br>
 * Of course, this {@link Flux} may be a {@link Flux#empty()}.<br>
 *
 * @param <T> the parameterized type of object produced by the {@link #execute(Mono[])} method
 */
@FunctionalInterface
public interface ExecutableTask<T> {
    Mono<T> execute(Mono<?>[] inputs) throws TaskExecutionException;
}
