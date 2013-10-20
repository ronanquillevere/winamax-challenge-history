package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public abstract class GoogleDatastore<T>
{
    public static final String END_DATE = "endDate";
    public static final String START_DATE = "startDate";
    public static final String ID = "id";

    public static Filter createFilterByDates(Date start, Date end)
    {
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

    public T find(Key key) throws EntityNotFoundException
    {
        Entity entitity = datastore.get(key);
        return buildFromDatastoreEntityNotNull(entitity);
    }

    protected abstract String getEntityKind();

    protected abstract T buildFromDatastoreEntityNotNull(Entity entity);

    protected T buildFromDatastoreEntity(Entity foundEntity)
    {
        if (foundEntity == null)
            return null;

        T object = buildFromDatastoreEntityNotNull(foundEntity);
        LOGGER.log(Level.FINE, "Found;" + object);
        return object;
    }

    protected Entity getDatastoreEntityFromFilter(Filter filter)
    {
        PreparedQuery pq = prepareQuery(filter, getEntityKind());
        return getDatastoreEntity(pq);
    }

    protected T buildEntity(Filter f)
    {
        Entity findEntity = getDatastoreEntityFromFilter(f);
        return buildFromDatastoreEntity(findEntity);
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

    private static final Logger LOGGER = Logger.getLogger(GoogleDatastore.class.getName());
}
