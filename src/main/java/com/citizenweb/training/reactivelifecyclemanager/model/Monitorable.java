package com.citizenweb.training.reactivelifecyclemanager.model;

import java.util.function.Consumer;

public interface Monitorable {
    String getId();

    String getName();

    String getType();

    EventStatus getStatus();

    void setStatus(EventStatus status);

    int getRank();

    void setRank(int rank);

    long getStartingTime();

    void setStartingTime(long time);

    long getEndingTime();

    void setEndingTime(long time);

    long getDurationMillis();

    void setDurationMillis(long duration);

    void updateStatus(Consumer<Monitorable> consumer);

    Consumer<Monitorable> running = monitor -> {
        monitor.setStatus(EventStatus.RUNNING);
        monitor.setStartingTime(System.currentTimeMillis());
    };

    Consumer<Monitorable> completing = monitor -> {
        monitor.setStatus(EventStatus.DONE);
        monitor.setEndingTime(System.currentTimeMillis());
        monitor.setDurationMillis(monitor.getEndingTime() - monitor.getStartingTime());
    };
}
