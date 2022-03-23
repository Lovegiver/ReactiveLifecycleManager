package com.citizenweb.training.reactivelifecyclemanager.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Monitor implements Monitorable {
    /** Unique ID built from {@link UUID} */
    @Getter
    @EqualsAndHashCode.Include
    private final String id;
    /** Event must have a human-readable name */
    @Getter @EqualsAndHashCode.Include
    private final String name;
    /** Event is injected into another object whom type must be defined */
    @Getter @EqualsAndHashCode.Include
    private final String type;
    /** {@link EventStatus} of the Event */
    @Getter @Setter
    private EventStatus status = EventStatus.NEW;
    /**
     * This value will be set by the {@link LifecycleManager} when building its execution model.
     * It represents the position in execution plan :
     * @implSpec Rank #1 means the task has to be done first, before any other tasks that do have 'predecessors'.
     * Rank #1 tasks have no predecessors.<br>
     * Rank #2 means the task will be executed immediately after all Rank #1 tasks have been processed.<br>
     */
    @Getter @Setter
    private int rank = -1;
    /** Event starting time */
    @Getter @Setter
    private long startingTime;
    /** Event ending time */
    @Getter @Setter
    private long endingTime;
    /** Event duration */
    @Getter @Setter
    private long durationMillis;

    public Monitor(EventType type, String name) {
        this.type = type.toString();
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }
}
