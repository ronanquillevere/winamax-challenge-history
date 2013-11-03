package com.usesoft.poker.server.domain.common;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.period.Period;
import com.usesoft.poker.server.domain.model.player.Player;

public interface BaseEntityVisitor<IN, OUT>
{
    OUT visit(Player player, IN in);

    OUT visit(Period period, IN in);

    OUT visit(CashGamePerformance cashGamePerformance, IN in);

}
