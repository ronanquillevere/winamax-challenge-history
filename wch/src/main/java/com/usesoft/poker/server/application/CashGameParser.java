package com.usesoft.poker.server.application;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformanceRepository;
import com.usesoft.poker.server.domain.model.performance.Period;
import com.usesoft.poker.server.domain.model.performance.PeriodRepository;
import com.usesoft.poker.server.domain.model.performance.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerName;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;
import com.usesoft.poker.server.infrastructure.CrawlerUtil;
import com.usesoft.poker.server.infrastructure.persistence.datastore.PeriodRepositoryDatastore;
import com.usesoft.poker.server.infrastructure.persistence.inmemory.CashGamePerformanceRepositoryInMemory;
import com.usesoft.poker.server.infrastructure.persistence.inmemory.PlayerRepositoryInMemory;

public class CashGameParser {
    //TODO @rqu the implementation should be injected
    public static final CashGameParser INSTANCE = new CashGameParser(CashGamePerformanceRepositoryInMemory.INSTANCE,
            PeriodRepositoryDatastore.INSTANCE, PlayerRepositoryInMemory.INSTANCE);
    
    private CashGameParser(CashGamePerformanceRepository repository, PeriodRepository periodRepo, PlayerRepository playerRepo) {
        this.perfRepository = repository;
        this.periodRepo = periodRepo;
        this.playerRepo = playerRepo;
    }
    
    public void parse(Document document, Stake stake) throws ParseException{
        Validate.notNull(document, "Document is required");
        Validate.notNull(stake, "Stake is required");
        LOGGER.debug("Parsing Cash Game Performance : Start");
        LOGGER.debug("Found Stake : {}", stake);
        
        Date now = new Date();
        Period period = new Period(CrawlerUtil.getStartDate(document), CrawlerUtil.getEndDate(document));
        LOGGER.debug("Found Period : {}", period);
        
        
        periodRepo.store(period);
        
        int numberOfRows = CrawlerUtil.getNumberOfRows(document);
        
        LOGGER.debug("Found Number of rows : {} " + numberOfRows);
        
        for (int i = 1; i <= numberOfRows; i++) {
            String playerName = CrawlerUtil.getPlayerName(document, i);
            int nbHands = CrawlerUtil.getNumberOfHands(document, i);
            double buyIns = CrawlerUtil.getBuyIns(document, i);
            
            Player player = new Player(new PlayerName(playerName));
            
            playerRepo.store(player);
            
            CashGamePerformance perf = new CashGamePerformance(player, period, stake, now);
            perf.setHands(nbHands);
            perf.setBuyIns(buyIns);
            
            
            perfRepository.store(perf);
            LOGGER.debug("Performance stored : {} ", perf);
        }
        
        LOGGER.debug("Parsing Cash Game Performance : End");
    }

    private CashGamePerformanceRepository perfRepository;

    private PlayerRepository playerRepo;

    private PeriodRepository periodRepo;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CashGameParser.class);
}
