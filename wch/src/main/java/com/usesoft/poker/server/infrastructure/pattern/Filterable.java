package com.usesoft.poker.server.infrastructure.pattern;

public interface Filterable<T> {
    boolean accept(Filter<T> filter);
}
