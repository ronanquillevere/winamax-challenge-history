package com.usesoft.poker.server.domain.model.player;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseEntity;

public class Player extends BaseEntity<Player>{

    public Player(@JsonProperty("playerName") String playerName)
    {
        Validate.notNull(playerName, "PlayerName is required");
        this.playerName = playerName;
    }

    @Override
    public boolean sameIdentityAs(Player other) {
        return other != null && other.playerName.equals(this.playerName);
    }

    @Override
    public String toString() {
        return getPlayerName().toString();
    }

    public String getPlayerName()
    {
        return playerName;
    }

    private final String playerName;
}
