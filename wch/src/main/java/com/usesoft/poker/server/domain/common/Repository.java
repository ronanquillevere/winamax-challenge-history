package com.usesoft.poker.server.domain.common;

import java.util.Collection;
import java.util.List;

public interface Repository<T>
{
    Collection<T> findAll();

    T findById(String id);

    void store(T entity);

    void store(List<T> entity);

    void remove(T entity);

    void remove(List<T> entity);
}
