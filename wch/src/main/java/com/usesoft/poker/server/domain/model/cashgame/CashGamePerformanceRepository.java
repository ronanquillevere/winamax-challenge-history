package com.usesoft.poker.server.domain.model.cashgame;

import java.util.Collection;

import com.usesoft.poker.server.domain.model.performance.Period;
import com.usesoft.poker.server.domain.model.performance.Stake;
import com.usesoft.poker.server.domain.model.player.Player;


public interface CashGamePerformanceRepository {
    
    CashGamePerformance find(Player player, Period period, Stake stake);
    
    Collection<CashGamePerformance> findAll();
    
    Collection<CashGamePerformance> find(Period period, Stake stake);

    Collection<CashGamePerformance> find(Player player, Stake stake);

    Collection<CashGamePerformance> find(Player player);
    
    void store(CashGamePerformance performance);

}
