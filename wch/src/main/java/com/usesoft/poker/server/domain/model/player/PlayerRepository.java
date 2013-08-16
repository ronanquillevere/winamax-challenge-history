package com.usesoft.poker.server.domain.model.player;

import java.util.Collection;

public interface PlayerRepository {
    
    Collection<Player> findAll();
    
    Player find(String playerName);
    
    void store(Player player);
}
