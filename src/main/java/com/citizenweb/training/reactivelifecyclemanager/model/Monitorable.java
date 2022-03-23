package com.citizenweb.training.reactivelifecyclemanager.model;

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
}
