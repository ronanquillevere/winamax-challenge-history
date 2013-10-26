package com.usesoft.poker.server.infrastructure.persistence.inmemory;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.domain.model.time.PeriodRepository;

public class TestPeriodRepository {

    private PeriodRepository repo = PeriodRepositoryInMemory.INSTANCE;

    @Before
    public void beforeTest(){
        ((PeriodRepositoryInMemory) repo).clear();
    }

    @Test
    public void test(){

        assertEquals(0,repo.findAll().size());

        Date d1 = new Date();
        Date d2 = new Date(d1.getTime()+10);

        Date d1b = new Date(d1.getTime());
        Date d2b = new Date(d2.getTime());

        Period period1 = new Period(d1, d2);

        repo.store(period1);

        assertEquals(repo.find(d1b,d2b), period1);
        assertEquals(1,repo.findAll().size());

        repo.store(period1);
        assertEquals(1,repo.findAll().size());

        Date start = new Date();
        Date end = new Date();
        repo.store(new Period(start, end));
        assertEquals(2,repo.findAll().size());
    }
}
