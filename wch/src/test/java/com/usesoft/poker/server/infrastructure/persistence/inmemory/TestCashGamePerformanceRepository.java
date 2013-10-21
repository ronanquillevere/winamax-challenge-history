package com.usesoft.poker.server.infrastructure.persistence.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;

public class TestCashGamePerformanceRepository
{

    private CashGamePerformanceRepository repo = CashGamePerformanceRepositoryInMemory.INSTANCE;

    @Before
    public void beforeTest()
    {
        ((CashGamePerformanceRepositoryInMemory) repo).clear();
    }

    @Test
    public void test()
    {

        assertEquals(0, repo.findAll().size());

        Date d1 = new Date();
        Date d2 = new Date(d1.getTime() + 20);

        Date d3 = new Date(d1.getTime() + 30);
        Date d4 = new Date(d1.getTime() + 40);

        Period period1 = new Period(d1, d2, Period.generateId(d1, d2));
        String ronanFN = "ronan";
        Player ronan = new Player(ronanFN);

        CashGamePerformance performance = new CashGamePerformance(ronan, period1, Stake.Micro, new Date(), UUID.randomUUID());
        performance.setHands(30000);
        performance.setBuyIns(5.20);

        Period period2 = new Period(d3, d4, Period.generateId(d3, d4));
        String kimFN = "kim22_12";
        Player kim = new Player(kimFN);

        CashGamePerformance performance2 = new CashGamePerformance(kim, period2, Stake.Small, new Date(), UUID.randomUUID());
        performance2.setHands(10000);
        performance2.setBuyIns(1.50);

        repo.store(performance);
        repo.store(performance2);

        assertEquals(performance, repo.find(ronan).iterator().next());
        assertEquals(30000, repo.find(ronan).iterator().next().getHands());
        assertEquals(5.20, repo.find(ronan).iterator().next().getBuyIns(), 0);
        assertEquals(performance2, repo.find(kim).iterator().next());

        assertEquals(performance, repo.find(period1, Stake.Micro).iterator().next());
        assertEquals(performance2, repo.find(period2, Stake.Small).iterator().next());

        assertEquals(performance, repo.find(ronan, Stake.Micro).iterator().next());
        assertEquals(performance2, repo.find(kim, Stake.Small).iterator().next());

        assertEquals(performance, repo.find(ronan, period1, Stake.Micro));
        assertEquals(performance2, repo.find(kim, period2, Stake.Small));

        assertEquals(2, repo.findAll().size());

        performance.setHands(35000);
        performance.setBuyIns(10.00);

        repo.store(performance);
        assertEquals(2, repo.findAll().size());

        assertEquals(1, repo.find(ronan).size());
        assertEquals(35000, repo.find(ronan).iterator().next().getHands());
        assertEquals(10.00, repo.find(ronan).iterator().next().getBuyIns(), 0);

        assertEquals(0, repo.find(ronan, Stake.Small).size());
        assertTrue(repo.find(ronan, period2, Stake.Micro) == null);
    }
}
