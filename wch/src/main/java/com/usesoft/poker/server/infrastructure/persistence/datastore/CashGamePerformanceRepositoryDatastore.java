package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;

public class CashGamePerformanceRepositoryDatastore implements CashGamePerformanceRepository
{

    private final class TaskFindPlayer extends TimerTask
    {
        private Entity playerEnt;
        private final CashGamePerformance performance;

        private TaskFindPlayer(Entity playerEnt, CashGamePerformance performance)
        {
            this.playerEnt = playerEnt;
            this.performance = performance;
        }

        @Override
        public void run()
        {
            playerEnt = playerRepo.findEntity(performance.getPlayer().getPlayerName());
        }
    }

    public static final CashGamePerformanceRepositoryDatastore INSTANCE = new CashGamePerformanceRepositoryDatastore();

    private static final Logger LOGGER = Logger.getLogger(CashGamePerformanceRepositoryDatastore.class.getName());

    private static final String PLAYER_KEY = "playerKey";

    private static final String PERIOD_KEY = "periodKey";

    private static final String STAKE = "stake";

    private static final String UPDATE = "lastUpdate";

    private static final String HANDS = "hands";

    private static final String BUY_INS = "buyIns";

    private static final String ID = "id";

    private DatastoreService datastore;

    private PeriodRepositoryDatastore periodStore;

    private PlayerRepositoryDatastore playerRepo;

    private static String getCashPerfTypeName()
    {
        return CashGamePerformance.class.getName().substring(CashGamePerformance.class.getName().lastIndexOf(".") + 1);
    }

    private CashGamePerformanceRepositoryDatastore()
    {
        datastore = DatastoreServiceFactory.getDatastoreService();
        periodStore = PeriodRepositoryDatastore.INSTANCE;
        playerRepo = PlayerRepositoryDatastore.INSTANCE;
    }

    @Override
    public Collection<CashGamePerformance> find(Period period, Stake stake)
    {
        try
        {
            PreparedQuery pq = getPerf(period, stake);
            return find(pq);
        } catch (EntityNotFoundException e)
        {
            // TODO @rqu improve this
            e.printStackTrace();
        }
        return new ArrayList<CashGamePerformance>();
    }

    @Override
    public Collection<CashGamePerformance> find(Player player)
    {
        try
        {
            return find(getPerf(player));
        } catch (EntityNotFoundException e)
        {
            // TODO @rqu improve this
            e.printStackTrace();
        }
        return new ArrayList<CashGamePerformance>();
    }

    @Override
    public CashGamePerformance find(Player player, Period period, Stake stake)
    {
        try
        {
            return find(getPerf(player, period, stake)).iterator().next();
        } catch (EntityNotFoundException e)
        {
            // TODO @rqu improve this
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<CashGamePerformance> find(Player player, Stake stake)
    {
        try
        {
            return find(getPerf(player, stake));
        } catch (EntityNotFoundException e)
        {
            // TODO @rqu improve this
            e.printStackTrace();
        }
        return new ArrayList<CashGamePerformance>();
    }

    @Override
    public Collection<CashGamePerformance> findAll()
    {
        ArrayList<CashGamePerformance> list = new ArrayList<CashGamePerformance>();

        Query q = new Query(getCashPerfTypeName());

        for (Entity e : datastore.prepare(q).asIterable())
        {
            try
            {
                list.add(buildPerfFromEntity(e));
            } catch (EntityNotFoundException e1)
            {
                e1.printStackTrace();
                // TODO @rqu improve this
            }
        }

        return list;
    }

    @Override
    public void remove(CashGamePerformance performance)
    {
        PreparedQuery pq = getPerf(performance.getPlayer(), performance.getPeriod(), performance.getStake());

        Iterable<Entity> asIterable = pq.asIterable();

        for (Entity e : asIterable)
        {
            datastore.delete(e.getKey());
            LOGGER.log(Level.FINE, "Deleted database entity;" + e);
        }
    }

    @Override
    public void store(final CashGamePerformance performance)
    {
        Validate.notNull(performance, "Performance cannot be null");
        Date startDate = performance.getPeriod().getStart();
        Date endDate = performance.getPeriod().getEnd();
        Validate.notNull(startDate);
        Validate.notNull(endDate);

        Entity periodEnt = periodStore.getDatastoreEntityFromFilter(periodStore.createFilterByDates(startDate, endDate));
        LOGGER.log(Level.INFO, "Period found in database;" + periodEnt);

        Entity playerEnt = playerRepo.findEntity(performance.getPlayer().getPlayerName());
        LOGGER.log(Level.INFO, "Player found in database;" + playerEnt);

        if (playerEnt == null)
        {
            findPlayerAgain(performance, playerEnt);
        }

        store(performance, periodEnt, playerEnt);
    }

    private void findPlayerAgain(final CashGamePerformance performance, Entity playerEnt)
    {
        LOGGER.log(Level.INFO, "Loop to find in database player;" + performance.getPlayer().getPlayerName());

        for (int i = 0; i < 100 && playerEnt == null; i++)
        {
            playerEnt = playerRepo.findEntity(performance.getPlayer().getPlayerName());
        }

    }

    private void store(CashGamePerformance performance, Entity periodEnt, Entity playerEnt)
    {
        Validate.notNull(periodEnt, "Period database entity cannot be null to save performance");
        Validate.notNull(playerEnt, "Player database entity cannot be null to save performance");

        Entity perfData = new Entity(getCashPerfTypeName());

        perfData.setProperty(HANDS, performance.getHands());
        perfData.setProperty(BUY_INS, performance.getBuyIns());
        perfData.setProperty(PLAYER_KEY, playerEnt.getKey());
        perfData.setProperty(PERIOD_KEY, periodEnt.getKey());
        perfData.setProperty(STAKE, performance.getStake().toString());
        perfData.setProperty(UPDATE, performance.getLastUpdate());
        perfData.setProperty(ID, performance.getId());

        datastore.put(perfData);
        LOGGER.log(Level.FINE, "Performance stored : " + perfData);
    }

    private CashGamePerformance buildPerfFromEntity(Entity e) throws EntityNotFoundException
    {
        Player player = playerRepo.findPlayer((Key) e.getProperty(PLAYER_KEY));
        Period period = periodStore.find((Key) e.getProperty(PERIOD_KEY));
        Stake stake = Stake.valueOf((String) e.getProperty(STAKE));
        Date lastUpdate = (Date) e.getProperty(UPDATE);
        UUID id = UUID.fromString((String) e.getProperty(ID));
        return new CashGamePerformance(player, period, stake, lastUpdate, id);
    }

    private Collection<CashGamePerformance> find(PreparedQuery pq) throws EntityNotFoundException
    {
        ArrayList<CashGamePerformance> list = new ArrayList<CashGamePerformance>();

        Iterable<Entity> asIterable = pq.asIterable();

        for (Entity e : asIterable)
        {
            CashGamePerformance entity = buildPerfFromEntity(e);
            list.add(entity);
        }

        return list;
    }

    private PreparedQuery getPerf(Period period, Stake stake)
    {
        Date startDate = period.getStart();
        Date endDate = period.getEnd();
        Validate.notNull(startDate);
        Validate.notNull(endDate);
        Entity periodEnt = periodStore.getDatastoreEntityFromFilter(periodStore.createFilterByDates(startDate, endDate));
        Filter periodFilter = new FilterPredicate(PERIOD_KEY, FilterOperator.EQUAL, periodEnt.getKey());
        Filter stakeFilter = new FilterPredicate(STAKE, FilterOperator.EQUAL, stake.toString());
        Filter compositeFilter = CompositeFilterOperator.and(periodFilter, stakeFilter);
        Query q = new Query(getCashPerfTypeName()).setFilter(compositeFilter);

        return datastore.prepare(q);
    }

    private PreparedQuery getPerf(Player player)
    {
        Entity playerEnt = playerRepo.findEntity(player.getPlayerName());
        Filter playerFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, playerEnt.getKey());
        return datastore.prepare(new Query(getCashPerfTypeName()).setFilter(playerFilter));
    }

    private PreparedQuery getPerf(Player player, Period period, Stake stake)
    {
        Entity playerEnt = playerRepo.findEntity(player.getPlayerName());
        Filter playerFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, playerEnt.getKey());
        Date startDate = period.getStart();
        Date endDate = period.getEnd();
        Validate.notNull(startDate);
        Validate.notNull(endDate);
        Entity periodEnt = periodStore.getDatastoreEntityFromFilter(periodStore.createFilterByDates(startDate, endDate));
        Filter periodFilter = new FilterPredicate(PERIOD_KEY, FilterOperator.EQUAL, periodEnt.getKey());
        Filter stakeFilter = new FilterPredicate(STAKE, FilterOperator.EQUAL, stake.toString());
        Filter compositeFilter = CompositeFilterOperator.and(periodFilter, stakeFilter);
        compositeFilter = CompositeFilterOperator.and(playerFilter, compositeFilter);
        return datastore.prepare(new Query(getCashPerfTypeName()).setFilter(compositeFilter));
    }

    private PreparedQuery getPerf(Player player, Stake stake)
    {
        Entity playerEnt = playerRepo.findEntity(player.getPlayerName());
        Filter playerFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, playerEnt.getKey());
        Filter stakeFilter = new FilterPredicate(STAKE, FilterOperator.EQUAL, stake.toString());
        Filter compositeFilter = CompositeFilterOperator.and(playerFilter, stakeFilter);
        return datastore.prepare(new Query(getCashPerfTypeName()).setFilter(compositeFilter));
    }

}
