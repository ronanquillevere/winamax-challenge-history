package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;

public class CashGamePerformanceRepositoryDatastore extends GoogleDatastore<CashGamePerformance> implements CashGamePerformanceRepository
{
    public static final CashGamePerformanceRepositoryDatastore INSTANCE = new CashGamePerformanceRepositoryDatastore();

    private static final Logger LOGGER = Logger.getLogger(CashGamePerformanceRepositoryDatastore.class.getName());

    private PeriodRepositoryDatastore periodRepository;

    private PlayerRepositoryDatastore playerRepository;

    public CashGamePerformanceRepositoryDatastore()
    {
        periodRepository = PeriodRepositoryDatastore.INSTANCE;
        playerRepository = PlayerRepositoryDatastore.INSTANCE;
    }

    @Override
    public CashGamePerformance find(Player player, Period period, Stake stake)
    {
        return buildEntity(createFilterByPeriodAndPlayerAndStake(player, period, stake));
    }

    @Override
    public Collection<CashGamePerformance> find(Period period, Stake stake)
    {
        return buildEntities(createFilterByDatesAndStake(period, stake));
    }

    @Override
    public Collection<CashGamePerformance> find(Player player, Stake stake)
    {
        return buildEntities(createFilterByPlayerAndStake(player, stake));
    }

    @Override
    public Collection<CashGamePerformance> find(Player player)
    {
        return buildEntities(createFilterByModel(PLAYER_KEY, player));
    }

    @Override
    public void store(CashGamePerformance performance)
    {
        store(performance, createFilterByPeriodAndPlayerAndStake(performance.getPlayer(), performance.getPeriod(), performance.getStake()));
    }

    @Override
    protected void storeToEntity(CashGamePerformance performance, Entity entity)
    {
        entity.setProperty(ID, performance.getId().toString());
        entity.setProperty(HANDS, performance.getHands());
        entity.setProperty(BUY_INS, performance.getBuyIns());
        entity.setProperty(PLAYER_KEY, getModelDBKey(performance.getPlayer()));
        entity.setProperty(PERIOD_KEY, getModelDBKey(performance.getPeriod()));
        entity.setProperty(STAKE, performance.getStake().toString());
        entity.setProperty(UPDATE, performance.getLastUpdate());

        datastore.put(entity);
        LOGGER.log(Level.INFO, "Stored/updated in database performance;" + performance);
    }

    @Override
    protected String getEntityKind()
    {
        return CashGamePerformance.class.getSimpleName();
    }

    @Override
    protected CashGamePerformance buildFromDatastoreEntityNotNull(Entity e)
    {
        Player player;
        Period period;
        try
        {
            player = playerRepository.find((Key) e.getProperty(PLAYER_KEY));
            period = periodRepository.find((Key) e.getProperty(PERIOD_KEY));
        } catch (EntityNotFoundException e1)
        {
            throw new RuntimeException(e1);
        }

        if (period == null || player == null)
            return null;

        Stake stake = Stake.valueOf((String) e.getProperty(STAKE));
        Date lastUpdate = (Date) e.getProperty(UPDATE);
        UUID id = UUID.fromString((String) e.getProperty(ID));
        return new CashGamePerformance(player, period, stake, lastUpdate, id);
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

    // private Filter createFilterByPlayerDBEntity(Player player)
    // {
    // Entity playerEnt = getPlayerEntity(player);
    // Filter playerFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, playerEnt.getKey());
    // return playerFilter;
    // }

    // private Entity getPlayerEntity(Player player)
    // {
    // return playerRepository.getDatastoreEntityFromFilter(createFilterById(player.getId()));
    // }

    // private Filter createFilterByPeriodDBEntity(Period period)
    // {
    // Entity periodEnt = getPeriodEntity(period);
    // Filter periodFilter = new FilterPredicate(PERIOD_KEY, FilterOperator.EQUAL, periodEnt.getKey());
    // return periodFilter;
    // }
    //
    // private Entity getPeriodEntity(Period period)
    // {
    // return periodRepository.getDatastoreEntityFromFilter(createFilterByDates(period.getStart(), period.getEnd()));
    // }
}
