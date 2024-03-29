package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.usesoft.poker.server.domain.common.BaseEntityReference;
import com.usesoft.poker.server.domain.common.EntityReference;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.period.Period;
import com.usesoft.poker.server.domain.model.player.Player;

public class CashGamePerformanceRepositoryDatastore extends GoogleDatastore<CashGamePerformance> implements CashGamePerformanceRepository
{
    public static final CashGamePerformanceRepositoryDatastore INSTANCE = new CashGamePerformanceRepositoryDatastore();

    @Override
    public CashGamePerformance find(Player player, Period period, Stake stake)
    {
        return buildEntity(createFilterByPeriodAndPlayerAndStake(player, period, stake));
    }

    @Override
    public List<CashGamePerformance> find(Period period, Stake stake)
    {
        return buildEntities(createFilterByDatesAndStake(period, stake));
    }

    @Override
    public List<CashGamePerformance> find(Player player, Stake stake)
    {
        return buildEntities(createFilterByPlayerAndStake(player, stake));
    }

    @Override
    public List<CashGamePerformance> find(Player player)
    {
        return buildEntities(createFilterByModel(PLAYER_KEY, player));
    }

    @Override
    public List<CashGamePerformance> findOutdated(Period period, Stake stake, Date timestamp)
    {
        Validate.notNull(stake);
        Validate.notNull(period);
        Filter compositeFilter = CompositeFilterOperator.and(createFilterByModel(PERIOD_KEY, period), createFilterByStake(stake));
        compositeFilter = CompositeFilterOperator.and(createFilterNotLastUpdated(timestamp), compositeFilter);
        return buildEntities(compositeFilter);
    }

    @Override
    protected void fillDBEntityFromModel(CashGamePerformance performance, Entity entity)
    {
        entity.setProperty(ID, performance.getId().toString());
        entity.setProperty(HANDS, performance.getHands());
        entity.setProperty(BUY_INS, performance.getBuyIns());
        entity.setProperty(PLAYER_KEY, getModelDBKey(performance.getPlayerReference()));
        entity.setProperty(PERIOD_KEY, getModelDBKey(performance.getPeriodReference()));
        entity.setProperty(STAKE, performance.getStake().toString());
        entity.setProperty(UPDATE, performance.getLastUpdate());
    }

    @Override
    protected String getEntityKind()
    {
        return CashGamePerformance.class.getSimpleName();
    }

    @Override
    protected CashGamePerformance buildFromDatastoreEntityNotNull(Entity e)
    {
        EntityReference playerReference;
        EntityReference periodReference;

        Key playerKey = (Key) e.getProperty(PLAYER_KEY);
        playerReference = new BaseEntityReference(playerKey.getName(), playerKey.getKind());

        Key periodKey = (Key) e.getProperty(PERIOD_KEY);
        periodReference = new BaseEntityReference(periodKey.getName(), periodKey.getKind());

        Stake stake = Stake.valueOf((String) e.getProperty(STAKE));
        Date lastUpdate = (Date) e.getProperty(UPDATE);
        UUID id = UUID.fromString((String) e.getProperty(ID));
        CashGamePerformance cashGamePerformance = new CashGamePerformance(playerReference, periodReference, stake, lastUpdate, (Double) e.getProperty(BUY_INS),
                (Long) e.getProperty(HANDS),
                id);
        return cashGamePerformance;
    }

    private Filter createFilterByPeriodAndPlayerAndStake(Player player, Period period, Stake stake)
    {
        Validate.notNull(stake);
        Validate.notNull(player);
        Filter compositeFilter = CompositeFilterOperator.and(createFilterByModel(PLAYER_KEY, player), createFilterByStake(stake));
        compositeFilter = CompositeFilterOperator.and(createFilterByModel(PERIOD_KEY, period), compositeFilter);
        return compositeFilter;
    }

    private Filter createFilterByDatesAndStake(Period period, Stake stake)
    {
        Validate.notNull(stake);
        Validate.notNull(period);
        Filter compositeFilter = CompositeFilterOperator.and(createFilterByModel(PERIOD_KEY, period), createFilterByStake(stake));
        return compositeFilter;
    }

    private Filter createFilterByPlayerAndStake(Player player, Stake stake)
    {
        Validate.notNull(stake);
        Validate.notNull(player);
        Filter compositeFilter = CompositeFilterOperator.and(createFilterByModel(PLAYER_KEY, player), createFilterByStake(stake));
        return compositeFilter;
    }

    private static Filter createFilterNotLastUpdated(Date timestamp)
    {
        Filter stakeFilter = new FilterPredicate(UPDATE, FilterOperator.NOT_EQUAL, timestamp);
        return stakeFilter;
    }
}
