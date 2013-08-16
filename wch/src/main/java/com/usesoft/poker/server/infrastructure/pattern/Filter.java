package com.usesoft.poker.server.infrastructure.pattern;

public interface Filter<T> {
    boolean filter (T t);
}
