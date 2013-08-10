package com.usesoft.poker.server.domain.model.player;

import org.apache.commons.lang3.Validate;

import com.usesoft.poker.server.domain.common.BaseEntity;

public class Player extends BaseEntity<Player>{
	
	public Player(PlayerName playerName) {
	    Validate.notNull(playerName, "PlayerName is required");
	    this.playerName = playerName;
	}

	public boolean sameIdentityAs(Player other) {
        return other != null && playerName.sameValueAs(other.playerName);
    }
   
	@Override
	public String toString() {
	    return playerName.toString();
	}
	
    private final PlayerName playerName;
    
}
