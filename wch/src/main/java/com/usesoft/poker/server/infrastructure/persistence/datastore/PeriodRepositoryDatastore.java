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
    public void store(Period period, boolean force)
    {
        Date startDate = period.getStart();
        Date endDate = period.getEnd();
        Validate.notNull(startDate);
        Validate.notNull(endDate);
        Entity foundEntity = getDatastoreEntityFromFilter(createFilterByDates(startDate, endDate));

        if (foundEntity == null)
        {
            updateEntity(period, new Entity(getEntityKind()));
            return;
        }

        if (force)
        {
            updateEntity(period, foundEntity);
            return;
        }

        LOGGER.log(Level.FINE, "Found already in database period;" + period);
    }

    @Override
    public Period find(String id)
    {
        Validate.notNull(id);

        Filter f = createFilterById(id);
        return buildEntity(f);
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
        return new Period((Date) e.getProperty(START_DATE), (Date) e.getProperty(END_DATE), (String) e.getProperty(ID));
    }

    @Override
    protected String getEntityKind()
    {
        return Period.class.getName().substring(Period.class.getName().lastIndexOf(".") + 1);
    }

    private void updateEntity(Period period, Entity periodData)
    {
        periodData.setProperty(START_DATE, period.getStart());
        periodData.setProperty(END_DATE, period.getEnd());
        periodData.setProperty(ID, period.getId());
    
        datastore.put(periodData);
        LOGGER.log(Level.INFO, "Stored/updated in database period;" + period);
    }
}
