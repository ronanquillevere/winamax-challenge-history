package com.usesoft.poker.server.domain.model.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseEntity;
import com.usesoft.poker.server.domain.common.BaseEntityVisitor;

public class Player extends BaseEntity<Player>{

    public Player(@JsonProperty("playerName") String id)
    {
        super(id);
    }

    @Override
    public boolean sameIdentityAs(Player other) {
        return other != null && other.id.equals(this.id);
    }

    @Override
    public String toString() {
        return getId().toString();
    }

    @Override
    public <IN, OUT> OUT accept(BaseEntityVisitor<IN, OUT> visitor, IN in)
    {
        return visitor.visit(this, in);
    }
}
