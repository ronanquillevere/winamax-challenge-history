package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.Filter;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.domain.model.time.PeriodRepository;

public class PeriodRepositoryDatastore extends GoogleDatastore<Period> implements PeriodRepository
{
    public static final PeriodRepositoryDatastore INSTANCE = new PeriodRepositoryDatastore();

    private static final Logger LOGGER = Logger.getLogger(PeriodRepositoryDatastore.class.getName());

    @Override
    public void store(Period period)
    {
        store(period, createFilterByDates(period.getStart(), period.getEnd()));
    }

    @Override
    protected void storeToEntity(Period period, Entity dbEntity)
    {
        dbEntity.setProperty(ID, period.getId());
        dbEntity.setProperty(START_DATE, period.getStart());
        dbEntity.setProperty(END_DATE, period.getEnd());

        datastore.put(dbEntity);
        LOGGER.log(Level.INFO, "Stored/updated in database period;" + period);
    }

    @Override
    public Period find(Date startDate, Date endDate)
    {
        Validate.notNull(startDate);
        Validate.notNull(endDate);

        Filter f = createFilterByDates(startDate, endDate);
        return buildEntity(f);
    }

    @Override
    protected Period buildFromDatastoreEntityNotNull(Entity e)
    {
        Date start = (Date) e.getProperty(START_DATE);
        Date end = (Date) e.getProperty(END_DATE);
        return new Period(start, end);
    }

    @Override
    protected String getEntityKind()
    {
        return Period.class.getSimpleName();
    }

    @Override
    public void remove(Period entity)
    {
        // TODO Auto-generated method stub
    }
}
