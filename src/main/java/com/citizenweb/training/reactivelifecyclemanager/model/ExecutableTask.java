package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.exception.TaskExecutionException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * An {@link ExecutableTask} produces a flux of T in exchange for an array of {@link Object}.<br>
 * Of course, this array may be empty.<br>
 *
 * @param <T> the parameterized type of object produced by the {@link #execute(Flux)} method
 */
@FunctionalInterface
public interface ExecutableTask<T> {
    Mono<T> execute(Flux<Mono<?>> inputs) throws TaskExecutionException;
}
