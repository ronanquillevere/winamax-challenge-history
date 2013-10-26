package com.usesoft.poker.server.infrastructure.persistence.datastore;

import com.google.appengine.api.datastore.Entity;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;

public class PlayerRepositoryDatastore extends GoogleDatastore<Player> implements PlayerRepository
{
    public static final PlayerRepositoryDatastore INSTANCE = new PlayerRepositoryDatastore();

    @Override
    protected void fillDBEntityFromModel(Player player, Entity entity)
    {
        entity.setProperty(ID, player.getId());
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
