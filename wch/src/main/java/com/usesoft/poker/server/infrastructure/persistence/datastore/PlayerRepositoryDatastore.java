package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerName;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;

public class PlayerRepositoryDatastore implements PlayerRepository {

    private static final String PLAYER_NAME = "playerName";
    public static final PlayerRepositoryDatastore INSTANCE = new PlayerRepositoryDatastore();

    private static final Logger LOGGER = Logger.getLogger(PlayerRepositoryDatastore.class.getName());

    @Override
    public Collection<Player> findAll() {
        ArrayList<Player> list = new ArrayList<Player>();

        Query q = new Query(getPlayerTypeName());

        for (Entity e : datastore.prepare(q).asIterable()) {
            list.add(buildPlayerFromEntity(e));
        }

        return list;
    }

    @Override
    public Player find(String playerName) {
        Entity findEntity = findEntity(playerName);
        if (findEntity == null)
            return null;
        return buildPlayerFromEntity(findEntity);
    }

    public Entity findEntity(String playerName){
        PreparedQuery pq = getPlayer(playerName);

        int count = pq.countEntities(FetchOptions.Builder.withDefaults());

        if (count > 1) {
            throw new RuntimeException("Should not have more than one player with the same name");
        }

        if (count == 0)
            return null;

        return pq.asIterable().iterator().next();
    }

    public Player findPlayer(Key key) throws EntityNotFoundException{
        return buildPlayerFromEntity(datastore.get(key));
    }


    @Override
    public void store(Player player) {
        PreparedQuery pq = getPlayer(player.getPlayerName().getName());

        int count = pq.countEntities(FetchOptions.Builder.withDefaults());

        if (count == 1){
            LOGGER.log(Level.FINE, "Already in database player;" + player);
            return;
        }

        if (count > 1) {
            throw new RuntimeException("Should not have more than one player with the same dates");
        }

        Entity playerData = new Entity(getPlayerTypeName());

        playerData.setProperty(PLAYER_NAME, player.getPlayerName().getName());

        Key key = datastore.put(playerData);

        LOGGER.log(Level.INFO, "Stored player;" + player + ";key;" + key);
    }

    private PlayerRepositoryDatastore() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    private Player buildPlayerFromEntity(Entity p) {
        return new Player(new PlayerName((String) p.getProperty(PLAYER_NAME)));
    }

    private static String getPlayerTypeName() {
        return Player.class.getName().substring(Player.class.getName().lastIndexOf(".")+1);
    }

    private PreparedQuery getPlayer(String playerName) {

        Filter playerNameFilter = new FilterPredicate(PLAYER_NAME, FilterOperator.EQUAL, playerName);

        return datastore.prepare(new Query(getPlayerTypeName()).setFilter(playerNameFilter));
    }

    private DatastoreService datastore;

}
