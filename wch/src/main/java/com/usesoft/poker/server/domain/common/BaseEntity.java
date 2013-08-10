package com.usesoft.poker.server.domain.common;


public abstract class BaseEntity<T> implements Entity<T> {

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;

        @SuppressWarnings("unchecked")
        final T other = (T) object;
        return sameIdentityAs(other);
    }
}
