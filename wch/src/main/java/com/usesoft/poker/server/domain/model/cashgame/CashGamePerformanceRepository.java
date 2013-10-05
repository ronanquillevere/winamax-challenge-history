package com.usesoft.poker.server.domain.model.cashgame;

import java.util.Collection;

import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;


public interface CashGamePerformanceRepository {

    CashGamePerformance find(Player player, Period period, Stake stake);

    Collection<CashGamePerformance> findAll();

    Collection<CashGamePerformance> find(Period period, Stake stake);

    Collection<CashGamePerformance> find(Player player, Stake stake);

    Collection<CashGamePerformance> find(Player player);

    void store(CashGamePerformance performance);

    void remove(CashGamePerformance performance);

}
