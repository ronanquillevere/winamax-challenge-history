package com.usesoft.poker.server.application;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;
import org.jsoup.nodes.Document;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerName;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.domain.model.time.PeriodRepository;
import com.usesoft.poker.server.domain.model.time.Stake;
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
        LOGGER.warning("Parsing Cash Game Performance : Start");
        LOGGER.warning("Found Stake : " + stake);

        Period period = parsePeriod(document);

        int numberOfRows = CrawlerUtil.getNumberOfRows(document);

        parsePerformances(document, stake, period, numberOfRows);

        LOGGER.warning("Parsing Cash Game Performance : End");
    }

    private CashGamePerformance parsePerformance(Stake stake, Period period, Date now, int nbHands, double buyIns, Player player)
    {
        CashGamePerformance perf = new CashGamePerformance(player, period, stake, now);
        perf.setHands(nbHands);
        perf.setBuyIns(buyIns);
        perfRepository.store(perf);
        LOGGER.warning("Performance stored : " + perf);
        return perf;
    }

    private void parsePerformances(Document document, Stake stake, Period period, int nbOfRows)
    {
        Date now = new Date();

        for (int i = 1; i <= nbOfRows; i++)
        {
            int nbHands = CrawlerUtil.getNumberOfHands(document, i);
            double buyIns = CrawlerUtil.getBuyIns(document, i);

            Player player = parsePlayer(document, i);

            parsePerformance(stake, period, now, nbHands, buyIns, player);
        }
    }

    private Period parsePeriod(Document document) throws ParseException
    {
        String periodAsString = CrawlerUtil.extractDatePeriod(document);

        Date start = CrawlerUtil.parseStartDate(periodAsString);
        Date end = CrawlerUtil.parseEndDate(periodAsString);
        Period period1 = new Period(start, end);
        LOGGER.warning("Found Period : " + period1);
        Period period = period1;
        periodRepo.store(period);
        return period;
    }

    private Player parsePlayer(Document document, int i)
    {
        String playerName = CrawlerUtil.getPlayerName(document, i);
        Player player = new Player(new PlayerName(playerName));
        playerRepo.store(player);
        return player;
    }
}
