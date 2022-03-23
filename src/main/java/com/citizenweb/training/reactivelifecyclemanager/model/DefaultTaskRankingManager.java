package com.citizenweb.training.reactivelifecyclemanager.model;

import com.citizenweb.training.reactivelifecyclemanager.exception.TaskRankingException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultTaskRankingManager implements TaskRankingManager {

    @Override
    public Set<Task> computeTasksRanking(Set<Task> originalTasks) throws TaskRankingException {


        final List<Task> tasks = new ArrayList<>(originalTasks);
        final List<Task> rankedTasks = new ArrayList<>(originalTasks.size());
        AtomicInteger rankCounter = new AtomicInteger(1);

        /*
         * Tasks without 'predecessors' are the first ones to be spotted.
         * They will be set with 'rank = 1'
         */
        List<Task> firstRankTasks = tasks.stream()
                .filter(task -> task.getPredecessors().isEmpty())
                .toList();
        firstRankTasks.forEach(task -> {
            task.getMonitor().setRank(rankCounter.get());
            rankedTasks.add(task);
        });

        /*
         * Once rank #1 tasks have been identified, we can start ordering the 'followers'.
         * In case all received tasks would be ranked #1, this loop would never be called.
         */
        while (rankedTasks.size() < tasks.size()) {
            /* Tasks' rank is set to '-1' by default when Task object is instantiated */
            List<Task> followers = tasks.stream()
                    .filter(task -> task.getMonitor().getRank() == -1)
                    .toList();

            if (!followers.isEmpty()) {

                var currentRank = rankCounter.incrementAndGet();

                for (Task task : followers) {

                    List<Task> predecessors = new ArrayList<>(task.getPredecessors());
                    /*
                     * A 'follower' can be ranked iif all its 'predecessors' are ranked
                     */
                    boolean areAllPredecessorsRanked = areAllPredecessorsRanked(predecessors,currentRank);
                    if (areAllPredecessorsRanked) {
                        task.getMonitor().setRank(currentRank);
                        rankedTasks.add(task);
                    }
                }
            }
        }
        for (Task task : originalTasks) {
            if (!rankedTasks.contains(task)) {
                throw new TaskRankingException(String.format("The following task couldn't be ranked -> %s", task), task);
            }
        }
        return new HashSet<>(rankedTasks);
    }

    /**
     * Checks if all 'predecessors' objects are already ranked.<br>
     * In that case, the current Task will be ranked too.<br>
     * Otherwise, it means that all 'predecessors' of a given {@link Task} are not scheduled yet.<br>
     * @param predecessors all previous tasks of a given {@link Task}
     * @param currentRank the rank that is actually being built
     * @return TRUE if all 'predecessors' are already ranked, FALSE otherwise
     */
    private boolean areAllPredecessorsRanked(List<Task> predecessors, int currentRank) {
        return predecessors.stream()
                .allMatch(predecessor -> predecessor.getMonitor().getRank() >= 1
                        && predecessor.getMonitor().getRank() < currentRank);
    }
}
