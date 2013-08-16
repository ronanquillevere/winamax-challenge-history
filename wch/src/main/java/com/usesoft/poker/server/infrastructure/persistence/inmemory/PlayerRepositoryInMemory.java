package com.usesoft.poker.server.infrastructure.persistence.inmemory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;

public class PlayerRepositoryInMemory implements PlayerRepository {

    public static final PlayerRepository INSTANCE = new PlayerRepositoryInMemory();

    private PlayerRepositoryInMemory() {
    }

    @Override
    public Collection<Player> findAll() {
        return players.values();
    }

    @Override
    public Player find(String playerName) {
        return players.get(playerName);
    }

    @Override
    public void store(Player player) {
        players.put(player.getPlayerName().toString(), player);

    }
    
    public void clear(){
        players.clear();
    }

    private final Map<String, Player> players = new HashMap<String, Player>();

}
