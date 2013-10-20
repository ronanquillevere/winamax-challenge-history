package com.usesoft.poker.server.application;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;
import org.jsoup.nodes.Document;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerName;
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
        Validate.notNull(document, "Document is required");
        Validate.notNull(stake, "Stake is required");

        Period period = parsePeriod(document, stake);

        int numberOfRows = CrawlerUtil.extractNumberOfRows(document);

        parsePerformances(document, stake, period, numberOfRows);

        LOGGER.log(Level.INFO, "Parsed Cash Game Performances for stake;" + stake);
    }

    private Period parsePeriod(Document document, Stake stake) throws ParseException
    {
        Period period = extractPeriod(document);
        LOGGER.log(Level.INFO, "Period extracted;" + period);

        Period found = periodRepo.find(period.getStart(), period.getEnd());

        if (found != null)
        {
            LOGGER.log(Level.INFO, "Period already in db;" + period);

            clearPerformances(period, stake);
            LOGGER.log(Level.INFO, "Related performaces cleared");

            return period;
        }

        LOGGER.log(Level.INFO, "Period not found in db;" + period);
        periodRepo.store(period, false);

        return period;
    }

    private void clearPerformances(Period period, Stake stake)
    {
        Collection<CashGamePerformance> perfs = perfRepository.find(period, stake);
        LOGGER.log(Level.INFO, "Found old performances to clear for period;" + period + ";stake;" + stake + ";number;" + perfs.size());

        for (CashGamePerformance p : perfs)
        {
            perfRepository.remove(p);
            LOGGER.log(Level.INFO, "Old performance cleared;" + p);
        }
    }

    private CashGamePerformance parsePerformance(Stake stake, Period period, Date now, int nbHands, double buyIns, Player player)
    {
        CashGamePerformance perf = new CashGamePerformance(player, period, stake, now);
        perf.setHands(nbHands);
        perf.setBuyIns(buyIns);
        LOGGER.log(Level.INFO, "Found performance;" + perf);
        perfRepository.store(perf);
        return perf;
    }

    private void parsePerformances(Document document, Stake stake, Period period, int nbOfRows)
    {
        Date now = CrawlerUtil.getParisTime();

        for (int i = 1; i <= nbOfRows; i++)
        {
            int nbHands = CrawlerUtil.extractNumberOfHands(document, i);
            double buyIns = CrawlerUtil.extractBuyIns(document, i);

            Player player = parsePlayer(document, i);

            parsePerformance(stake, period, now, nbHands, buyIns, player);
        }
    }



    private Period extractPeriod(Document document) throws ParseException
    {
        String periodAsString = CrawlerUtil.extractDatePeriod(document);
        Date start = CrawlerUtil.parseStartDate(periodAsString);
        Date end = CrawlerUtil.parseEndDate(periodAsString);

        return new Period(start, end, Period.generateId(start, end));
    }

    private Player parsePlayer(Document document, int i)
    {
        String playerName = CrawlerUtil.extractPlayerName(document, i);
        Player player = new Player(new PlayerName(playerName));
        LOGGER.log(Level.INFO, "Found performance for player;" + player);
        playerRepo.store(player);
        return player;
    }
}
