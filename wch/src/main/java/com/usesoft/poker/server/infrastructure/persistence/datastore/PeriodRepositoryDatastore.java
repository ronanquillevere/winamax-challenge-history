package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.domain.model.time.PeriodRepository;

public class PeriodRepositoryDatastore implements PeriodRepository {

    public static final PeriodRepositoryDatastore INSTANCE = new PeriodRepositoryDatastore();

    private static final Logger LOGGER = Logger.getLogger(PeriodRepositoryDatastore.class.getName());

    private PeriodRepositoryDatastore() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }


    @Override
    public Collection<Period> findAll() {
        ArrayList<Period> list = new ArrayList<Period>();

        Query q = new Query(getPeriodTypeName());

        for (Entity e : datastore.prepare(q).asIterable()) {
            list.add(buildPeriodFromEntity(e));
        }

        return list;
    }

    @Override
    public void store(Period period) {

        PreparedQuery pq = getPeriod(period.getStart(), period.getEnd());

        int count = pq.countEntities(FetchOptions.Builder.withDefaults());

        if (count == 1){
            return;
        }

        if (count > 1) {
            throw new RuntimeException("Should not have more than one period with the same dates");
        }

        Entity periodData = new Entity(getPeriodTypeName());

        periodData.setProperty(START_DATE, period.getStart());
        periodData.setProperty(END_DATE, period.getEnd());

        datastore.put(periodData);
        LOGGER.log(Level.INFO, "Crawled period;" + period);
    }


    @Override
    public Period find(Date startDate, Date endDate) {
        Period period = buildPeriodFromEntity(findEntity(startDate, endDate));
        LOGGER.log(Level.INFO, "Found period;" + period);
        return period;
    }

    public Entity findEntity(Date startDate, Date endDate){
        PreparedQuery pq = getPeriod(startDate, endDate);
        LOGGER.log(Level.INFO, "Prepared query for period start;" + startDate + ";end;" + endDate);

        int count = pq.countEntities(FetchOptions.Builder.withDefaults());
        LOGGER.log(Level.INFO, "Count;" + count);

        if (count > 1) {
            throw new RuntimeException("SHould not have more than one period with the same dates");
        }

        if (count == 0)
            return null;

        Entity next = pq.asIterable().iterator().next();
        return next;
    }

    public Period find(Key key) throws EntityNotFoundException{
        return buildPeriodFromEntity(datastore.get(key));
    }

    public Period findPeriod(Key key) throws EntityNotFoundException {
        return buildPeriodFromEntity(datastore.get(key));
    }


    private static String getPeriodTypeName() {
        return Period.class.getName().substring(Period.class.getName().lastIndexOf(".")+1);
    }


    private Period buildPeriodFromEntity(Entity p) {
        return new Period((Date) p.getProperty(START_DATE), (Date) p.getProperty(END_DATE));
    }

    private PreparedQuery getPeriod(Date start, Date end) {
        Filter startFilter = new FilterPredicate(START_DATE, FilterOperator.EQUAL, start);
        Filter endFilter = new FilterPredicate(END_DATE, FilterOperator.EQUAL, end);
        Filter periodF = CompositeFilterOperator.and(startFilter, endFilter);

        return datastore.prepare(new Query(getPeriodTypeName()).setFilter(periodF));
    }

    private DatastoreService datastore;
    private static final String END_DATE = "endDate";
    private static final String START_DATE = "startDate";
}
