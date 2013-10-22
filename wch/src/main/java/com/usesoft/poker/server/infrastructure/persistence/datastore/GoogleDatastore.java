package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;

public abstract class GoogleDatastore<T>
{
    public static final String END_DATE = "endDate";
    public static final String START_DATE = "startDate";
    public static final String ID = "id";
    public static final String PLAYER_KEY = "playerKey";
    public static final String PERIOD_KEY = "periodKey";
    public static final String STAKE = "stake";
    public static final String UPDATE = "lastUpdate";
    public static final String HANDS = "hands";
    public static final String BUY_INS = "buyIns";
    public static final String PLAYER_NAME = "playerName";

    public static Filter createFilterByDates(Date start, Date end)
    {
        Validate.notNull(start);
        Validate.notNull(end);
        Filter startFilter = new FilterPredicate(START_DATE, FilterOperator.EQUAL, start);
        Filter endFilter = new FilterPredicate(END_DATE, FilterOperator.EQUAL, end);
        Filter periodF = CompositeFilterOperator.and(startFilter, endFilter);
        return periodF;
    }

    public static Filter createFilterById(String id)
    {
        Filter idFilter = new FilterPredicate(ID, FilterOperator.EQUAL, id);
        return idFilter;
    }

    public static Filter createFilterByStake(Stake stake)
    {
        Filter stakeFilter = new FilterPredicate(STAKE, FilterOperator.EQUAL, stake.toString());
        return stakeFilter;
    }

    public static Filter createFilterByPlayerName(Player player)
    {
        Filter playerFilter = new FilterPredicate(PLAYER_NAME, FilterOperator.EQUAL, player.getPlayerName());
        return playerFilter;
    }


    public Collection<T> findAll()
    {
        ArrayList<T> list = new ArrayList<T>();

        Query q = new Query(getEntityKind());

        PreparedQuery pq = datastore.prepare(q);
        Iterator<Entity> it = pq.asIterable().iterator();

        while (it.hasNext())
        {
            list.add(buildFromDatastoreEntityNotNull(it.next()));
        }

        return list;
    }

    public T find(String id)
    {
        Validate.notNull(id);

        Filter f = createFilterById(id);
        return buildEntity(f);
    }

    public T find(Key key) throws EntityNotFoundException
    {
        Entity entitity = datastore.get(key);
        return buildFromDatastoreEntityNotNull(entitity);
    }

    protected abstract void storeToEntity(T model, Entity entity);

    protected abstract String getEntityKind();

    protected abstract T buildFromDatastoreEntityNotNull(Entity entity);

    protected void store(T model, Filter filter)
    {
        Entity entity = getDatastoreEntityFromFilter(filter);

        if (entity == null)
            entity = new Entity(getEntityKind());
        else
            LOGGER.log(Level.FINE, "Found already in database model entity;" + model);

        storeToEntity(model, entity);
    }

    protected T buildFromDatastoreEntity(Entity entity)
    {
        if (entity == null)
            return null;

        T object = buildFromDatastoreEntityNotNull(entity);
        LOGGER.log(Level.FINE, "Found;" + object);
        return object;
    }

    protected List<T> buildFromDatastoreEntites(List<Entity> entities)
    {
        ArrayList<T> out = new ArrayList<T>();
        for (Entity entity : entities)
        {
            out.add(buildFromDatastoreEntity(entity));
        }
        return out;
    }

    protected Entity getDatastoreEntityFromFilter(Filter filter)
    {
        PreparedQuery pq = prepareQuery(filter, getEntityKind());
        return getDatastoreEntity(pq);
    }

    protected List<Entity> getDatastoreEntitiesFromFilter(Filter filter)
    {
        PreparedQuery pq = prepareQuery(filter, getEntityKind());
        return getDatastoreEntities(pq);
    }

    protected T buildEntity(Filter f)
    {
        Entity findEntity = getDatastoreEntityFromFilter(f);
        return buildFromDatastoreEntity(findEntity);
    }

    protected Collection<T> buildEntities(Filter f)
    {
        List<Entity> findEntities = getDatastoreEntitiesFromFilter(f);
        return buildFromDatastoreEntites(findEntities);
    }

    protected DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private PreparedQuery prepareQuery(Filter filter, String kind)
    {
        return datastore.prepare(new Query(kind).setFilter(filter));
    }

    private Entity getDatastoreEntity(PreparedQuery pq)
    {
        return pq.asSingleEntity();
    }

    private List<Entity> getDatastoreEntities(PreparedQuery pq)
    {
        ArrayList<Entity> list = new ArrayList<Entity>();

        Iterator<Entity> it = pq.asIterable().iterator();

        while (it.hasNext())
        {
            list.add(it.next());
        }
        return list;
    }

    private static final Logger LOGGER = Logger.getLogger(GoogleDatastore.class.getName());
}
