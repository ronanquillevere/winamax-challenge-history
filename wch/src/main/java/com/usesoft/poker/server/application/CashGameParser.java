package com.usesoft.poker.server.application;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;
import org.jsoup.nodes.Document;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.domain.model.time.PeriodRepository;
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

        Date timestamp = CrawlerUtil.getParisTime();
        // Extraction phase
        List<CashGamePerformance> perfs = new ArrayList<CashGamePerformance>();
        List<Player> players = new ArrayList<Player>();
        Period period = extractPeriod(document);
        extract(document, stake, period, perfs, players, timestamp);

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

    private void extract(Document document, Stake stake, Period period, List<CashGamePerformance> perfs, List<Player> players, Date timestamp)
    {
        int numberOfRows = CrawlerUtil.extractNumberOfRows(document);

        for (int i = 1; i <= numberOfRows; i++)
        {
            CashGamePerformance perf = extractCashGamePerf(document, i, stake, period, players, timestamp);
            perfs.add(perf);
        }
    }

    private CashGamePerformance extractCashGamePerf(Document document, int index, Stake stake, Period period, List<Player> players, Date now)
    {
        Player player = extractPlayer(document, index);
        players.add(player);
        int nbHands = CrawlerUtil.extractNumberOfHands(document, index);
        double buyIns = CrawlerUtil.extractBuyIns(document, index);

        CashGamePerformance perf = new CashGamePerformance(player, period, stake, now, UUID.randomUUID());
        perf.setHands(nbHands);
        perf.setBuyIns(buyIns);
        return perf;
    }

    private Period extractPeriod(Document document) throws ParseException
    {
        String periodAsString = CrawlerUtil.extractDatePeriod(document);
        Date start = CrawlerUtil.parseStartDate(periodAsString);
        Date end = CrawlerUtil.parseEndDate(periodAsString);

        return new Period(start, end);
    }

    private Player extractPlayer(Document document, int i)
    {
        String playerName = CrawlerUtil.extractPlayerName(document, i);
        Player player = new Player(playerName);
        return player;
    }
}
