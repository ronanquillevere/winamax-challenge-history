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
import com.usesoft.poker.server.domain.common.BaseEntity;
import com.usesoft.poker.server.domain.common.EntityReference;
import com.usesoft.poker.server.domain.model.cashgame.Stake;

public abstract class GoogleDatastore<T extends BaseEntity<T>>
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

    protected <M extends BaseEntity<M>> Filter createFilterByModel(String ForeignKey, M model)
    {
        Filter filter = new FilterPredicate(ForeignKey, FilterOperator.EQUAL, getModelDBKey(model));
        return filter;
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

    public T findById(String id)
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

    public void store(T model)
    {
        Entity entity = new Entity(model.getType(), model.getId());
        fillDBEntityFromModel(model, entity);
        datastore.put(entity);
        LOGGER.log(Level.INFO, "Stored/updated in database model;" + model);
    }

    public void store(List<T> entities)
    {
        List<Entity> dbEntities = new ArrayList<Entity>();

        for (T p : entities)
        {
            Entity e = new Entity(p.getType(), p.getId());
            fillDBEntityFromModel(p, e);
            dbEntities.add(e);
        }

        datastore.put(dbEntities);
    }

    protected abstract String getEntityKind();

    protected abstract T buildFromDatastoreEntityNotNull(Entity entity);

    protected abstract void fillDBEntityFromModel(T model, Entity dbEntity);


    protected <M extends BaseEntity<M>> Key getModelDBKey(M model)
    {
        Entity dbEntity = new Entity(model.getType(), model.getId());
        Key key = dbEntity.getKey();
        return key;
    }

    protected Key getModelDBKey(EntityReference reference)
    {
        Entity dbEntity = new Entity(reference.getType(), reference.getId());
        Key key = dbEntity.getKey();
        return key;
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
        if (entities == null)
            return out;

        for (Entity entity : entities)
        {
            out.add(buildFromDatastoreEntity(entity));
        }
        return out;
    }

    protected Entity getDatastoreEntityFromFilter(Filter filter)
    {
        PreparedQuery pq = prepareQuery(filter, getEntityKind());
        Entity entity = getDatastoreEntity(pq);
        LOGGER.log(Level.FINE, "Found in database model entity;" + entity);
        return entity;
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

    protected List<T> buildEntities(Filter f)
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

    public void remove(T modelEntity)
    {
        Entity fake = new Entity(modelEntity.getType(), modelEntity.getId());
        datastore.delete(fake.getKey());
        LOGGER.log(Level.INFO, "Deleted database entity;" + modelEntity);
    }

    public void remove(List<T> modelEntities)
    {
        List<Key> dbKeys = new ArrayList<Key>();

        for (T m : modelEntities)
        {
            Entity e = new Entity(m.getType(), m.getId());
            dbKeys.add(e.getKey());
            LOGGER.log(Level.INFO, "Add model entity to delete;" + m);
        }

        datastore.delete(dbKeys);
        LOGGER.log(Level.INFO, "Deleted database entities size;" + dbKeys.size());
    }

    private static final Logger LOGGER = Logger.getLogger(GoogleDatastore.class.getName());
}
