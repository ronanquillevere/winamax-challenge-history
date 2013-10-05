package com.usesoft.poker.server.domain.common;

public abstract class BaseValueObject<T> implements ValueObject<T> {

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        @SuppressWarnings("unchecked")
        T other = (T) o;

        return sameValueAs(other);
    }

}
