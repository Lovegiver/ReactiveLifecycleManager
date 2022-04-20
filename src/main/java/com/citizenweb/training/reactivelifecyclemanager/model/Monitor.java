package com.citizenweb.training.reactivelifecyclemanager.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;
import java.util.function.Consumer;

@Log4j2
@ToString(onlyExplicitlyIncluded = true)
public class Monitor implements Monitorable {
    /** Unique ID built from {@link UUID} */
    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String id;
    /** Event must have a human-readable name */
    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String name;
    /** Event is injected into another object whom type must be defined */
    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String type;
    /** {@link EventStatus} of the Event */
    @Getter @Setter
    @ToString.Include
    private EventStatus status = EventStatus.NEW;
    /**
     * This value will be set by the {@link LifecycleManager} when building its execution model.
     * It represents the position in execution plan :
     * @implSpec Rank #1 means the task has to be done first, before any other tasks that do have 'predecessors'.
     * Rank #1 tasks have no predecessors.<br>
     * Rank #2 means the task will be executed immediately after all Rank #1 tasks have been processed.<br>
     */
    @Getter @Setter
    @ToString.Include
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

    @Override
    public void updateStatus(Consumer<Monitorable> consumer) {
        consumer.accept(this);
    }
}
