package com.usesoft.poker.server.domain.common;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;


public abstract class BaseEntity<T> implements Entity<T> {

    public BaseEntity(String id)
    {
        Validate.notNull(id);
        if (!isIdValid())
            throw new IllegalArgumentException("Not a valid id;" + id);
        this.id = id;
    }

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
    
    public boolean isIdValid(){
        return true;
    }
    
    public String getId()
    {
        return id;
    }

    public String getType()
    {
        return this.getClass().getSimpleName();
    }

    @JsonProperty("id")
    protected final String id;
}
