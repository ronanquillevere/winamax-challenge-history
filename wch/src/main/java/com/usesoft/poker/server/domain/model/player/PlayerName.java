package com.usesoft.poker.server.domain.model.player;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseValueObject;

public class PlayerName extends BaseValueObject<PlayerName> {

    public PlayerName(@JsonProperty("name") String name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public boolean sameValueAs(PlayerName other) {
          return other != null && this.getName().equals(other.getName());
    }

    @Override
    public String toString() {
        return name;
    }
    
    public String getName() {
        return name;
    }

    private final String name;
}
