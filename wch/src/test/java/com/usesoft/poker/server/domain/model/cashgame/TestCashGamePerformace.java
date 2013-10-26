package com.usesoft.poker.server.domain.model.cashgame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.usesoft.poker.server.domain.model.common.EntityUtil;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;

public class TestCashGamePerformace {
    @Test
    public void testInvariant() {
        Date start = new Date();
        Date end = new Date();
        Period period = new Period(start, end);
        Player player = new Player("Ronan");
        Date d = new Date();
        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(null, period, Stake.Micro, d, UUID.randomUUID());
            fail("Player is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(player, null, Stake.Micro, d, UUID.randomUUID());
            fail("Period is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(player, period, null, d, UUID.randomUUID());
            fail("Stake is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(player, period, Stake.Micro, null, UUID.randomUUID());
            fail("Last Update is mandatory");
        } catch (Exception e) {
        }

        try
        {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(player, period, Stake.Micro, d, null);
            fail("Id is mandatory");
        } catch (Exception e)
        {
        }

        CashGamePerformance p = new CashGamePerformance(player, period, Stake.Micro, d, UUID.randomUUID());

        try {
            p.setHands(-2);
            fail("Number of hands should be positive");
        } catch (Exception e) {
        }
    }

    @Test
    public void testEquals() {

        Date startDate = new Date();
        long time = startDate.getTime();
        Date startDate2 = new Date(time +10 );
        Date endDate = new Date(time + 20);
        Date endDate2 = new Date(time + 30);

        Period period = new Period(startDate, endDate);
        Period period2 = new Period(startDate, endDate);
        Period period3 = new Period(startDate2, endDate2);

        Player player1 = new Player("player");
        Player player2 = new Player("player");
        Player player3 = new Player("player3");

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.fromString(uuid1.toString());
        UUID uuid3 = UUID.randomUUID();

        CashGamePerformance p = new CashGamePerformance(player1, period, Stake.Micro, new Date(), uuid1);
        CashGamePerformance p2 = new CashGamePerformance(player2, period2, Stake.Micro, new Date(), uuid2);
        CashGamePerformance p3 = new CashGamePerformance(player3, period3, Stake.Micro, new Date(), uuid3);

        EntityUtil.checkValues(p, p2, p3);
    }


    @Test
    public void test() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 10);
        Period period = new Period(start, end);
        Player player1 = new Player("player");

        CashGamePerformance p = new CashGamePerformance(player1, period, Stake.Micro, new Date(), UUID.randomUUID());

        assertEquals(start, p.getPeriod().getStart());
        assertEquals(end, p.getPeriod().getEnd());
        assertEquals(Stake.Micro, p.getStake());

        assertEquals(0, p.getHands());
        assertEquals(0d, p.getBuyIns(), 0);

        p.setHands(25);
        p.setBuyIns(12.32d);

        assertEquals(25, p.getHands());
        assertEquals(12.32d, p.getBuyIns(), 0);

        p.setBuyIns(-52.32d);
        assertEquals(-52.32d, p.getBuyIns(), 0);
    }

}
