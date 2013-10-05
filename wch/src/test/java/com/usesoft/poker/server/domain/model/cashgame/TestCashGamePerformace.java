package com.usesoft.poker.server.domain.model.cashgame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.usesoft.poker.server.domain.model.common.EntityUtil;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerName;
import com.usesoft.poker.server.domain.model.time.Period;

public class TestCashGamePerformace {
    @Test
    public void testInvariant() {
        Period period = new Period(new Date(), new Date());
        Player player = new Player(new PlayerName("Ronan"));
        Date d = new Date();
        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(null, period, Stake.Micro, d);
            fail("Player is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(player, null, Stake.Micro, d);
            fail("Period is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(player, period, null, d);
            fail("Stake is mandatory");
        } catch (Exception e) {
        }

        try {
            @SuppressWarnings("unused")
            CashGamePerformance p = new CashGamePerformance(player, period, Stake.Micro, null);
            fail("Last Update is mandatory");
        } catch (Exception e) {
        }

        CashGamePerformance p = new CashGamePerformance(player, period, Stake.Micro, d);

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
        
        Player player1 = new Player(new PlayerName("player"));
        Player player2 = new Player(new PlayerName("player"));
        Player player3 = new Player(new PlayerName("player3"));

        CashGamePerformance p = new CashGamePerformance(player1, period, Stake.Micro, new Date());
        CashGamePerformance p2 = new CashGamePerformance(player2, period2, Stake.Micro, new Date());
        CashGamePerformance p3 = new CashGamePerformance(player3, period3, Stake.Micro, new Date());

        EntityUtil.checkValues(p, p2, p3);
    }
    
    
    @Test
    public void test() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 10);
        Period period = new Period(start, end);
        Player player1 = new Player(new PlayerName("player"));

        CashGamePerformance p = new CashGamePerformance(player1, period, Stake.Micro, new Date());
        
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
