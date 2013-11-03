package com.usesoft.poker.server.domain.common;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.period.Period;
import com.usesoft.poker.server.domain.model.player.Player;

public class ReferenceService implements BaseEntityVisitor<Object, EntityReference>
{
    @Override
    public EntityReference visit(Player player, Object in)
    {
        return getReference(player);
    }

    @Override
    public EntityReference visit(Period period, Object in)
    {
        return getReference(period);
    }

    @Override
    public EntityReference visit(CashGamePerformance cashGamePerformance, Object in)
    {
        return getReference(cashGamePerformance);
    }

    private BaseEntityReference getReference(BaseEntity<?> entity)
    {
        return new BaseEntityReference(entity.getId(), entity.getType());
    }
}
