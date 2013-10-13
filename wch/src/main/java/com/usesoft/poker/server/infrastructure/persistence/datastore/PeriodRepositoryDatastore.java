package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.domain.model.time.PeriodRepository;

public class PeriodRepositoryDatastore implements PeriodRepository
{

    public static final PeriodRepositoryDatastore INSTANCE = new PeriodRepositoryDatastore();

    private static final Logger LOGGER = Logger.getLogger(PeriodRepositoryDatastore.class.getName());

    private PeriodRepositoryDatastore()
    {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    @Override
    public Collection<Period> findAll()
    {
        ArrayList<Period> list = new ArrayList<Period>();

        Query q = new Query(getPeriodTypeName());

        for (Entity e : datastore.prepare(q).asIterable())
        {
            list.add(buildPeriodFromEntityNotNull(e));
        }

        return list;
    }

    @Override
    public void store(Period period, boolean force)
    {
        Entity findEntity = findEntity(period.getStart(), period.getEnd());

        if (findEntity == null)
        {
            storeEntity(period, new Entity(getPeriodTypeName()));
            return;
        }

        if (force)
        {
            storeEntity(period, findEntity);
            return;
        }

        LOGGER.log(Level.FINE, "Found already in database period;" + period);
    }

    private void storeEntity(Period period, Entity periodData)
    {
        periodData.setProperty(START_DATE, period.getStart());
        periodData.setProperty(END_DATE, period.getEnd());
        periodData.setProperty(ID, period.getId());

        datastore.put(periodData);
        LOGGER.log(Level.INFO, "Stored in database period;" + period);
    }

    @Override
    public Period find(String id)
    {
        Entity findEntity = findEntity(id);
        return buildPeriodFromEntity(findEntity);
    }

    @Override
    public Period find(Date startDate, Date endDate)
    {
        Entity findEntity = findEntity(startDate, endDate);
        return buildPeriodFromEntity(findEntity);
    }

    private Period buildPeriodFromEntity(Entity findEntity)
    {
        if (findEntity == null)
            return null;

        Period period = buildPeriodFromEntityNotNull(findEntity);
        LOGGER.log(Level.FINE, "Found period;" + period);
        return period;
    }

    private Period buildPeriodFromEntityNotNull(Entity p)
    {
        Date start = (Date) p.getProperty(START_DATE);
        Date end = (Date) p.getProperty(END_DATE);
        String id = (String) p.getProperty(ID);
        if (id == null)
            id = Period.generateId(start, end);
        return new Period(start, end, id);
    }

    private Entity findEntity(String id)
    {
        Validate.notNull(id);

        PreparedQuery pq = createPeriodQuery(id);
        LOGGER.log(Level.FINE, "Prepared query for period id;" + id);

        return getEntity(pq);
    }

    public Entity findEntity(Date startDate, Date endDate)
    {
        Validate.notNull(startDate);
        Validate.notNull(endDate);

        PreparedQuery pq = createPeriodQuery(startDate, endDate);
        LOGGER.log(Level.FINE, "Prepared query for period start;" + startDate + ";end;" + endDate);

        return getEntity(pq);
    }

    private Entity getEntity(PreparedQuery pq)
    {
        int count = pq.countEntities(FetchOptions.Builder.withDefaults());
        LOGGER.log(Level.FINE, "Found period count;" + count);

        if (count > 1)
        {
            throw new RuntimeException("Should not have more than one period with same values");
        }

        if (count == 0)
            return null;

        Entity next = pq.asIterable().iterator().next();
        return next;
    }

    public Period findPeriod(Key key) throws EntityNotFoundException
    {
        Entity entitity = datastore.get(key);
        if (entitity == null)
            return null;
        return buildPeriodFromEntityNotNull(entitity);
    }

    private static String getPeriodTypeName()
    {
        return Period.class.getName().substring(Period.class.getName().lastIndexOf(".") + 1);
    }

    private PreparedQuery createPeriodQuery(Date start, Date end)
    {
        Filter filter = createQueryFilterByDates(start, end);
        String queryName = getPeriodTypeName() + "_byDates";
        return prepareQuery(filter, queryName);
    }

    private PreparedQuery createPeriodQuery(String id)
    {
        Filter filter = createQueryFilterById(id);
        String queryName = getPeriodTypeName() + "_byId";

        return prepareQuery(filter, queryName);
    }

    private Filter createQueryFilterByDates(Date start, Date end)
    {
        Filter startFilter = new FilterPredicate(START_DATE, FilterOperator.EQUAL, start);
        Filter endFilter = new FilterPredicate(END_DATE, FilterOperator.EQUAL, end);
        Filter periodF = CompositeFilterOperator.and(startFilter, endFilter);
        return periodF;
    }

    private Filter createQueryFilterById(String id)
    {
        Filter idFilter = new FilterPredicate(ID, FilterOperator.EQUAL, id);
        return idFilter;
    }

    private PreparedQuery prepareQuery(Filter filter, String queryName)
    {
        return datastore.prepare(new Query(queryName).setFilter(filter));
    }

    private DatastoreService datastore;
    private static final String END_DATE = "endDate";
    private static final String START_DATE = "startDate";
    private static final String ID = "id";
}
