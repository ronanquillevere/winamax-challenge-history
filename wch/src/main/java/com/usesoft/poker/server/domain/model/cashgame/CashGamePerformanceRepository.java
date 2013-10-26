package com.usesoft.poker.server.domain.model.cashgame;

import java.util.Collection;

import com.usesoft.poker.server.domain.common.Repository;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;


public interface CashGamePerformanceRepository extends Repository<CashGamePerformance>
{
    CashGamePerformance find(Player player, Period period, Stake stake);

    Collection<CashGamePerformance> find(Period period, Stake stake);

    Collection<CashGamePerformance> find(Player player, Stake stake);

    Collection<CashGamePerformance> find(Player player);
}
