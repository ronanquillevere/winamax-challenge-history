package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;

public class PlayerRepositoryDatastore extends GoogleDatastore<Player> implements PlayerRepository
{
    public static final PlayerRepositoryDatastore INSTANCE = new PlayerRepositoryDatastore();

    private static final Logger LOGGER = Logger.getLogger(PlayerRepositoryDatastore.class.getName());

    @Override
    public void store(Player player)
    {
        store(player, createFilterById(player.getId()));
    }

    @Override
    protected void storeToEntity(Player player, Entity entity)
    {
        entity.setProperty(ID, player.getId());
        datastore.put(entity);
        LOGGER.log(Level.INFO, "Stored/updated in database player;" + player);
    }

    @Override
    protected String getEntityKind()
    {
        return Player.class.getSimpleName();
    }

    @Override
    protected Player buildFromDatastoreEntityNotNull(Entity entity)
    {
        return new Player((String) entity.getProperty(ID));
    }

    @Override
    public void remove(Player entity)
    {
        // TODO Auto-generated method stub
    }
}
