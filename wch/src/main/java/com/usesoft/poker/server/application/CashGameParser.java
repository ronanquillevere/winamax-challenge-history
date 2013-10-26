package com.usesoft.poker.server.application;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
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
        for (Player player : players)
        {
            playerRepo.store(player);
        }

        LOGGER.log(Level.INFO, "Stored players number;" + players.size());
    }

    private void storePeriod(Period period)
    {
        periodRepo.store(period);
        LOGGER.log(Level.INFO, "Stored period;" + period);
    }

    private void storePerfs(List<CashGamePerformance> perfs)
    {
        for (CashGamePerformance p : perfs)
        {
            perfRepository.store(p);
        }
        LOGGER.log(Level.INFO, "Stored perfs number;" + perfs.size());
    }

    private void clean(Stake stake, Period period, Date timestamp)
    {
        Collection<CashGamePerformance> perfs = perfRepository.find(period, stake);

        for (CashGamePerformance cashGamePerformance : perfs)
        {
            if (!cashGamePerformance.getLastUpdate().equals(timestamp))
            {
                perfRepository.remove(cashGamePerformance);
                LOGGER.log(Level.INFO, "Removed performace;" + cashGamePerformance);
            }
        }
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

    // private Period parsePeriod(Document document, Stake stake) throws ParseException
    // {
    // Period period = extractPeriod(document);
    // LOGGER.log(Level.INFO, "Period extracted;" + period);
    //
    // Period found = periodRepo.find(period.getStart(), period.getEnd());
    //
    // if (found != null)
    // {
    // LOGGER.log(Level.INFO, "Period already in db;" + period);
    //
    // clearPerformances(period, stake);
    // LOGGER.log(Level.INFO, "Related performaces cleared");
    //
    // return period;
    // }
    //
    // LOGGER.log(Level.INFO, "Period not found in db;" + period);
    // periodRepo.store(period);
    //
    // return period;
    // }

    // private Player parsePlayer(Document document, int i)
    // {
    // Player player = extractPlayer(document, i);
    // LOGGER.log(Level.INFO, "Found performance for player;" + player);
    // playerRepo.store(player);
    // return player;
    // }
    //
    // private CashGamePerformance parsePerformance(Stake stake, Period period, Date now, int nbHands, double buyIns, Player player)
    // {
    // CashGamePerformance perf = new CashGamePerformance(player, period, stake, now, UUID.randomUUID());
    // perf.setHands(nbHands);
    // perf.setBuyIns(buyIns);
    // LOGGER.log(Level.INFO, "Found performance;" + perf);
    // perfRepository.store(perf);
    // return perf;
    // }

    // private void parsePerformances(Document document, Stake stake, Period period, int nbOfRows)
    // {
    // Date now = CrawlerUtil.getParisTime();
    //
    // for (int i = 1; i <= nbOfRows; i++)
    // {
    // int nbHands = CrawlerUtil.extractNumberOfHands(document, i);
    // double buyIns = CrawlerUtil.extractBuyIns(document, i);
    //
    // Player player = parsePlayer(document, i);
    //
    // parsePerformance(stake, period, now, nbHands, buyIns, player);
    // }
    // }

    // private void clearPerformances(Period period, Stake stake)
    // {
    // Collection<CashGamePerformance> perfs = perfRepository.find(period, stake);
    // LOGGER.log(Level.INFO, "Found old performances to clear for period;" + period + ";stake;" + stake + ";number;" + perfs.size());
    //
    // for (CashGamePerformance p : perfs)
    // {
    // perfRepository.remove(p);
    // LOGGER.log(Level.INFO, "Old performance cleared;" + p);
    // }
    // }
}
