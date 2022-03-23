package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.exception.TaskRankingException;

import java.util.Set;

public interface TaskRankingManager {

    /**
     * Takes a {@link Set} of {@link Task}s and orders them by determining their
     * execution rank. When the ordering operation ends, all submitted tasks will
     * have their 'rank' value set with an {@link Integer} value.<br>
     * @param tasks the {@link Task} collection to order
     * @return ordered tasks
     */
    Set<Task> computeTasksRanking(final Set<Task> tasks) throws TaskRankingException;

}
