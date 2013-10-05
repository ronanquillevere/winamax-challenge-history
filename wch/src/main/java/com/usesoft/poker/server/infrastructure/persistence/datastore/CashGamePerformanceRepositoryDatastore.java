package com.usesoft.poker.server.infrastructure.persistence.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.domain.model.time.Stake;

public class CashGamePerformanceRepositoryDatastore implements
        CashGamePerformanceRepository {

    public static final CashGamePerformanceRepositoryDatastore INSTANCE = new CashGamePerformanceRepositoryDatastore();
    
    private CashGamePerformanceRepositoryDatastore() {
        datastore = DatastoreServiceFactory.getDatastoreService();
        periodStore = PeriodRepositoryDatastore.INSTANCE;
        playerRepo = PlayerRepositoryDatastore.INSTANCE;
    }
    
    @Override
    public CashGamePerformance find(Player player, Period period, Stake stake) {
        try {
            return find(getPerf(player, period, stake)).iterator().next();
        } catch (EntityNotFoundException e) {
            // TODO @rqu improve this
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<CashGamePerformance> findAll() {
        ArrayList<CashGamePerformance> list = new ArrayList<CashGamePerformance>();
        
        Query q = new Query(getCashPerfTypeName());
        
        for (Entity e : datastore.prepare(q).asIterable()) {
            try {
                list.add(buildPerfFromEntity(e));
            } catch (EntityNotFoundException e1) {
                e1.printStackTrace();
                //TODO @rqu improve this
            }
          }
        
        return list;
    }

    @Override
    public Collection<CashGamePerformance> find(Period period, Stake stake) {
        try {
            return find(getPerf(period,stake));
        } catch (EntityNotFoundException e) {
            // TODO @rqu improve this
            e.printStackTrace();
        }
        return new ArrayList<CashGamePerformance>();
    }

    @Override
    public Collection<CashGamePerformance> find(Player player, Stake stake) {
        try {
            return find(getPerf(player, stake));
        } catch (EntityNotFoundException e) {
            // TODO @rqu improve this
            e.printStackTrace();
        }
        return new ArrayList<CashGamePerformance>();
    }

    @Override
    public Collection<CashGamePerformance> find(Player player) {
        try {
            return find(getPerf(player));
        } catch (EntityNotFoundException e) {
            // TODO @rqu improve this
            e.printStackTrace();
        }
        return new ArrayList<CashGamePerformance>();
    }
    
    @Override
    public void store(CashGamePerformance performance) {
        
        Entity periodEnt = periodStore.findEntity(performance.getPeriod().getStart(), performance.getPeriod().getEnd());
        Entity playerEnt = playerRepo.findEntity(performance.getPlayer().getPlayerName().getName());
        Entity perfData = new Entity(getCashPerfTypeName());
        
        perfData.setProperty(HANDS, performance.getHands());
        perfData.setProperty(BUY_INS, performance.getBuyIns());
        perfData.setProperty(PLAYER_KEY, playerEnt.getKey());
        perfData.setProperty(PERIOD_KEY, periodEnt.getKey());
        perfData.setProperty(STAKE, performance.getStake().toString());
        perfData.setProperty(UPDATE, performance.getLastUpdate());
    
        datastore.put(perfData);
    }

    private Collection<CashGamePerformance> find(PreparedQuery pq) throws EntityNotFoundException{
        ArrayList<CashGamePerformance> list = new ArrayList<CashGamePerformance>();
        
        for (Entity e : pq.asIterable()) {
            list.add(buildPerfFromEntity(e));
        }
        
        return list;
    }
    
    
    private PreparedQuery getPerf(Player player) {
        Entity playerEnt = playerRepo.findEntity(player.getPlayerName().getName());
        Filter playerFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, playerEnt.getKey());
        return datastore.prepare(new Query(getCashPerfTypeName()).setFilter(playerFilter));
    }

    private PreparedQuery getPerf(Player player, Stake stake) {
        Entity playerEnt = playerRepo.findEntity(player.getPlayerName().getName());
        Filter playerFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, playerEnt.getKey());
        Filter stakeFilter = new FilterPredicate(STAKE, FilterOperator.EQUAL, stake.toString());
        Filter compositeFilter = CompositeFilterOperator.and(playerFilter, stakeFilter);
        return datastore.prepare(new Query(getCashPerfTypeName()).setFilter(compositeFilter));
    }
    
    private PreparedQuery getPerf(Period period, Stake stake) {
        Entity periodEnt = periodStore.findEntity(period.getStart(), period.getEnd());
        Filter periodFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, periodEnt.getKey());
        Filter stakeFilter = new FilterPredicate(STAKE, FilterOperator.EQUAL, stake.toString());
        Filter compositeFilter = CompositeFilterOperator.and(periodFilter, stakeFilter);
        return datastore.prepare(new Query(getCashPerfTypeName()).setFilter(compositeFilter));
    }

    private PreparedQuery getPerf(Player player, Period period, Stake stake) {
        Entity playerEnt = playerRepo.findEntity(player.getPlayerName().getName());
        Filter playerFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, playerEnt.getKey());
        Entity periodEnt = periodStore.findEntity(period.getStart(), period.getEnd());
        Filter periodFilter = new FilterPredicate(PLAYER_KEY, FilterOperator.EQUAL, periodEnt.getKey());
        Filter stakeFilter = new FilterPredicate(STAKE, FilterOperator.EQUAL, stake.toString());
        Filter compositeFilter = CompositeFilterOperator.and(periodFilter, stakeFilter);
        compositeFilter = CompositeFilterOperator.and(playerFilter, compositeFilter);
        return datastore.prepare(new Query(getCashPerfTypeName()).setFilter(compositeFilter));
    }

    private static String getCashPerfTypeName() {
        return CashGamePerformance.class.getName().substring(CashGamePerformance.class.getName().lastIndexOf(".")+1);
    }


    private CashGamePerformance buildPerfFromEntity(Entity p) throws EntityNotFoundException {      
        Player player = playerRepo.findPlayer((Key) p.getProperty(PLAYER_KEY));
        Period period = periodStore.findPeriod((Key) p.getProperty(PERIOD_KEY));
        Stake stake = Stake.valueOf((String) p.getProperty(STAKE));
        Date lastUpdate = (Date) p.getProperty(UPDATE);
        return new CashGamePerformance(player, period, stake, lastUpdate);
    }
    
    private DatastoreService datastore;
    private static final String HANDS = "hands";
    private static final String BUY_INS = "buyIns";
    private static final String PLAYER_KEY = "playerKey";
    private static final String PERIOD_KEY = "periodKey";
    private static final String STAKE = "stake";
    private static final String UPDATE = "lastUpdate";
    private PeriodRepositoryDatastore periodStore;

    private PlayerRepositoryDatastore playerRepo;

}
