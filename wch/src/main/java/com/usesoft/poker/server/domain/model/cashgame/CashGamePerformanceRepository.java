package com.usesoft.poker.server.domain.model.cashgame;

import java.util.Date;
import java.util.List;

import com.usesoft.poker.server.domain.common.Repository;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;


public interface CashGamePerformanceRepository extends Repository<CashGamePerformance>
{
    CashGamePerformance find(Player player, Period period, Stake stake);

    List<CashGamePerformance> find(Period period, Stake stake);

    List<CashGamePerformance> find(Player player, Stake stake);

    List<CashGamePerformance> findOutdated(Period period, Stake stake, Date timestamp);

    List<CashGamePerformance> find(Player player);
}
