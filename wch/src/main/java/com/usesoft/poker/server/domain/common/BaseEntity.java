package com.usesoft.poker.server.domain.common;

import java.net.URISyntaxException;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.interfaces.resources.URLConstants;


public abstract class BaseEntity<T> implements Entity<T>, Identifiable
{

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

    @JsonIgnore
    public boolean isIdValid(){
        return true;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getType()
    {
        return this.getClass().getSimpleName();
    }

    // TODO @rqu this should be handle by network dto, not in the model. The model should not know about its representation/uri in a REST infrastructure. Model
    // should stay pure.

    @Override
    public String getUri() throws URISyntaxException
    {
        return BaseEntityReference.buildUri(buildPath());
    }

    // TODO @rqu duplicate code between BaseEntity, BaseEntityReference, Identifiable ...

    private String buildPath()
    {
        return URLConstants.API + URLConstants.API_VERSION_1 + getType().toLowerCase() + "s" + "/" + getId();
    }

    public abstract <IN, OUT> OUT accept(BaseEntityVisitor<IN, OUT> visitor, IN in);

    @JsonProperty("id")
    protected final String id;
}
