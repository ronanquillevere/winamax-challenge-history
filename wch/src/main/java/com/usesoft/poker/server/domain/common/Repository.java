package com.usesoft.poker.server.domain.common;

import java.util.Collection;

public interface Repository<T>
{
    Collection<T> findAll();

    T findById(String id);

    void store(T entity);

    void remove(T entity);
}
