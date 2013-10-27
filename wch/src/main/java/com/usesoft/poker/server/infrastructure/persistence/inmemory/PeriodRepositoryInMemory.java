package com.usesoft.poker.server.infrastructure.persistence.inmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.domain.model.time.PeriodRepository;

public class PeriodRepositoryInMemory implements PeriodRepository {

    public static final PeriodRepositoryInMemory INSTANCE = new PeriodRepositoryInMemory();

    private PeriodRepositoryInMemory() {
    }

    @Override
    public Collection<Period> findAll() {
        return periods;
    }

    @Override
    public void store(Period period)
    {
        Period exist = find(period.getStart(), period.getEnd());
        if (exist == null)
            periods.add(period);

    }

    @Override
    public Period find(Date startDate, Date endDate) {
        for (Period p : periods) {
            if (p.getStart().equals(startDate) && p.getEnd().equals(endDate))
                return p;
        }
        return null;
    }

    @Override
    public Period findById(String id)
    {
        for (Period p : periods)
        {
            if (p.getId().equals(id))
                return p;
        }
        return null;
    }

    public void clear() {
        periods.clear();
    }

    private final List<Period> periods = new ArrayList<Period>();

    @Override
    public void remove(Period entity)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void store(List<Period> entity)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(List<Period> entity)
    {
        // TODO Auto-generated method stub

    }
}
