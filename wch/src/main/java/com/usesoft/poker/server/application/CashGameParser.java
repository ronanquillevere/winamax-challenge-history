package com.usesoft.poker.server.application;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;
import org.jsoup.nodes.Document;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.period.Period;
import com.usesoft.poker.server.domain.model.period.PeriodRepository;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;
import com.usesoft.poker.server.infrastructure.CrawlerUtil;
import com.usesoft.poker.server.infrastructure.persistence.datastore.CashGamePerformanceRepositoryDatastore;
import com.usesoft.poker.server.infrastructure.persistence.datastore.PeriodRepositoryDatastore;
import com.usesoft.poker.server.infrastructure.persistence.datastore.PlayerRepositoryDatastore;

public class CashGameParser
{
    // TODO @rqu the implementation should be injected
    public static final CashGameParser INSTANCE = new CashGameParser(CashGamePerformanceRepositoryDatastore.INSTANCE, PeriodRepositoryDatastore.INSTANCE,
            PlayerRepositoryDatastore.INSTANCE);

    private CashGamePerformanceRepository perfRepository;

    private PlayerRepository playerRepo;

    private PeriodRepository periodRepo;

    private static final Logger LOGGER = Logger.getLogger(CashGameParser.class.getName());

    private CashGameParser(CashGamePerformanceRepository repository, PeriodRepository periodRepo, PlayerRepository playerRepo)
    {
        this.perfRepository = repository;
        this.periodRepo = periodRepo;
        this.playerRepo = playerRepo;
    }

    public void parse(Document document, Stake stake) throws ParseException
    {
        Validate.notNull(document);
        Validate.notNull(stake);

        List<Player> players = new ArrayList<>();
        List<CashGamePerformance> perfs = new ArrayList<>();

        Date timestamp = CrawlerUtil.getParisTime();
        Period period = CrawlerUtil.extractPeriod(document);

        CrawlerUtil.fillPlayersAndPerfs(document, stake, period, timestamp, perfs, players);

        // Storing phase
        storePeriod(period);
        storePlayers(players);
        storePerfs(perfs);

        // Cleaning phase
        clean(stake, period, timestamp);

        LOGGER.log(Level.INFO, "Parsed Cash Game Performances for stake;" + stake);
    }



    private void storePlayers(List<Player> players)
    {
        playerRepo.store(players);
        LOGGER.log(Level.INFO, "Stored players number;" + players.size());
    }

    private void storePeriod(Period period)
    {
        periodRepo.store(period);
        LOGGER.log(Level.INFO, "Stored period;" + period);
    }

    private void storePerfs(List<CashGamePerformance> perfs)
    {
        perfRepository.store(perfs);
        LOGGER.log(Level.INFO, "Stored perfs number;" + perfs.size());
    }

    private void clean(Stake stake, Period period, Date timestamp)
    {
        List<CashGamePerformance> perfs = perfRepository.findOutdated(period, stake, timestamp);
        perfRepository.remove(perfs);
    }
}
