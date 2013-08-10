package com.usesoft.poker.server.domain.model.player;

import org.apache.commons.lang3.Validate;

import com.usesoft.poker.server.domain.common.BaseValueObject;

public class PlayerName extends BaseValueObject<PlayerName> {

    public PlayerName(String name) {
        Validate.notNull(name);
        this.name = name;
    }

    public boolean sameValueAs(PlayerName other) {
          return other != null && this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
    
    private final String name;
    private static final long serialVersionUID = 1L;
}
