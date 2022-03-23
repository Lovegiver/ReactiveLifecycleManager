package com.citizenweb.training.reactivelifecyclemanager.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class LifecycleManager implements LifecycleHelper {
    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID id = UUID.randomUUID();

    private final Set<Task> tasks;

    public void execute() {
        int maxRank = LifecycleHelper.getMaxRank(this.tasks);
        for (int rank = 1; rank < maxRank+1; rank++) {
            Scheduler scheduler = Schedulers.boundedElastic();
            /* TODO : TO BE CONTINUED */
        }
    }

}
