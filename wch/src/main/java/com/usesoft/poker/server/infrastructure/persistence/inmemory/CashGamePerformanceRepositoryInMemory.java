package com.usesoft.poker.server.infrastructure.persistence.inmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.infrastructure.pattern.Filter;

public class CashGamePerformanceRepositoryInMemory implements
CashGamePerformanceRepository {

    public static final CashGamePerformanceRepositoryInMemory INSTANCE = new CashGamePerformanceRepositoryInMemory();

    private CashGamePerformanceRepositoryInMemory() {
        perfs = new ArrayList<CashGamePerformance>();
    }

    @Override
    public Collection<CashGamePerformance> findAll() {
        return perfs;
    }

    @Override
    public CashGamePerformance find(Player player, Period period, Stake stake) {
        List<Filter<CashGamePerformance>> filters = new ArrayList<Filter<CashGamePerformance>>();
        filters.add(new PlayerFilter(player));
        filters.add(new PeriodFilter(period));
        filters.add(new StakeFilter(stake));
        List<CashGamePerformance> out = find(filters);

        Validate.isTrue(out.size() <= 1, "Something went wrong, should not find more than one result.");
        if (out.size() == 1)
            return out.get(0);

        return null;
    }

    @Override
    public List<CashGamePerformance> find(Period period, Stake stake)
    {
        List<Filter<CashGamePerformance>> filters = new ArrayList<Filter<CashGamePerformance>>();
        filters.add(new PeriodFilter(period));
        filters.add(new StakeFilter(stake));
        return find(filters);
    }

    @Override
    public List<CashGamePerformance> find(Player player, Stake stake)
    {
        List<Filter<CashGamePerformance>> filters = new ArrayList<Filter<CashGamePerformance>>();
        filters.add(new PlayerFilter(player));
        filters.add(new StakeFilter(stake));
        return find(filters);
    }

    @Override
    public List<CashGamePerformance> find(Player player)
    {
        List<Filter<CashGamePerformance>> filters = new ArrayList<Filter<CashGamePerformance>>();
        filters.add(new PlayerFilter(player));
        return find(filters);
    }

    @Override
    public void store(CashGamePerformance performance) {
        CashGamePerformance exist = find(performance.getPlayer(), performance.getPeriod(), performance.getStake());
        if (exist != null)
            perfs.remove(exist);

        perfs.add(performance);
    }

    @Override
    public void remove(CashGamePerformance performance)
    {
        CashGamePerformance perf = find(performance.getPlayer(), performance.getPeriod(), performance.getStake());
        if (perf == null)
            return;

        perfs.remove(perf);

    }

    public void clear() {
        perfs.clear();
    }


    private List<CashGamePerformance> find(List<Filter<CashGamePerformance>> filters) {
        List<CashGamePerformance> out = new ArrayList<CashGamePerformance>();

        for (CashGamePerformance p : perfs) {
            boolean rejected = false;

            for (Iterator<Filter<CashGamePerformance>> iterator = filters.iterator(); iterator.hasNext() && !rejected;) {
                if (!p.accept(iterator.next()))
                    rejected = true;
            }

            if (!rejected)
                out.add(p);
        }
        return out;
    }

    private final List<CashGamePerformance> perfs;

    private static class PlayerFilter implements Filter<CashGamePerformance> {

        public PlayerFilter(Player player) {
            this.player = player;
        }

        @Override
        public boolean filter(CashGamePerformance perf) {
            if (perf.getPlayer().equals(player))
                return true;
            return false;
        }

        private final Player player;
    }

    private static class StakeFilter implements Filter<CashGamePerformance> {

        public StakeFilter(Stake stake) {
            this.stake = stake;
        }

        @Override
        public boolean filter(CashGamePerformance perf) {
            if (perf.getStake() == stake)
                return true;
            return false;
        }

        private Stake stake;
    }

    private static class PeriodFilter implements Filter<CashGamePerformance> {

        public PeriodFilter(Period period) {
            this.period = period;
        }

        @Override
        public boolean filter(CashGamePerformance perf) {
            if (perf.getPeriod().equals(period))
                return true;
            return false;
        }

        private Period period;
    }

    @Override
    public CashGamePerformance findById(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void store(List<CashGamePerformance> entity)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(List<CashGamePerformance> entity)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<CashGamePerformance> findOutdated(Period period, Stake stake, Date timestamp)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
