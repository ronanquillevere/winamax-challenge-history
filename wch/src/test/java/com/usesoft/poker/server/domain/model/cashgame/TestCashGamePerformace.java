package com.usesoft.poker.server.domain.model.cashgame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.usesoft.poker.server.domain.common.EntityReference;
import com.usesoft.poker.server.domain.common.ReferenceService;
import com.usesoft.poker.server.domain.model.common.EntityUtil;
import com.usesoft.poker.server.domain.model.period.Period;
import com.usesoft.poker.server.domain.model.player.Player;

public class TestCashGamePerformace {
    @Test
    public void testInvariant() {
        Date start = new Date();
        Date end = new Date();
        Period period = new Period(start, end);
        Player player = new Player("Ronan");
        Date d = new Date();
        ReferenceService service = new ReferenceService();
        EntityReference playerRef = player.accept(service, null);
        EntityReference periodRef = period.accept(service, null);

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(null, periodRef, Stake.Micro, d, 0, 0, UUID.randomUUID());
            fail("Player is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(playerRef, null, Stake.Micro, d, 0, 0, UUID.randomUUID());
            fail("Period is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(playerRef, periodRef, null, d, 0, 0, UUID.randomUUID());
            fail("Stake is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(playerRef, periodRef, Stake.Micro, null, 0, 0, UUID.randomUUID());
            fail("Last Update is mandatory");
        } catch (Exception e) {
        }

        try
        {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(playerRef, periodRef, Stake.Micro, d, 0, 0, null);
            fail("Id is mandatory");
        } catch (Exception e)
        {
        }

        try {
            new CashGamePerformance(playerRef, periodRef, Stake.Micro, d, 0, -2, UUID.randomUUID());
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

        ReferenceService service = new ReferenceService();
        EntityReference playerRef1 = player1.accept(service, null);
        EntityReference playerRef2 = player2.accept(service, null);
        EntityReference playerRef3 = player3.accept(service, null);
        EntityReference periodRef = period.accept(service, null);
        EntityReference periodRef2 = period2.accept(service, null);
        EntityReference periodRef3 = period3.accept(service, null);

        CashGamePerformance p = new CashGamePerformance(playerRef1, periodRef, Stake.Micro, new Date(), 0, 0, uuid1);
        CashGamePerformance p2 = new CashGamePerformance(playerRef2, periodRef2, Stake.Micro, new Date(), 0, 0, uuid2);
        CashGamePerformance p3 = new CashGamePerformance(playerRef3, periodRef3, Stake.Micro, new Date(), 0, 0, uuid3);

        EntityUtil.checkValues(p, p2, p3);
    }


    @Test
    public void test() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 10);
        Period period = new Period(start, end);
        Player player1 = new Player("player");

        ReferenceService service = new ReferenceService();
        EntityReference playerRef1 = player1.accept(service, null);
        EntityReference periodRef = period.accept(service, null);

        CashGamePerformance p = new CashGamePerformance(playerRef1, periodRef, Stake.Micro, new Date(), 0, 0, UUID.randomUUID());

        assertEquals(period.getId(), p.getPeriodReference().getId());
        assertEquals(period.getType(), p.getPeriodReference().getType());
        assertEquals(Stake.Micro, p.getStake());

        assertEquals(0, p.getHands());
        assertEquals(0d, p.getBuyIns(), 0);

        p = new CashGamePerformance(playerRef1, periodRef, Stake.Micro, new Date(), 12.32d, 25, UUID.randomUUID());

        assertEquals(25, p.getHands());
        assertEquals(12.32d, p.getBuyIns(), 0);

        p = new CashGamePerformance(playerRef1, periodRef, Stake.Micro, new Date(), -52.32d, 25, UUID.randomUUID());

        assertEquals(-52.32d, p.getBuyIns(), 0);
    }

}
