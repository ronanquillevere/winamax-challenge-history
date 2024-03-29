package com.usesoft.poker.server.interfaces;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;

import com.usesoft.poker.server.application.CashGameParser;
import com.usesoft.poker.server.domain.model.cashgame.Stake;

public class CashGameCrawler extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        LOGGER.info("Crawling data from winamax : Start");

        String requestURI = req.getRequestURI();

        try {
            CashGameParser.INSTANCE.parse(Jsoup.connect(getUrl(requestURI)).get(), getStake(requestURI));
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error while crawling data;" + e);
            resp.getWriter().println("An exception ocured while crawling micro cash game challenge. See log file.");
            return;
        }

        resp.getWriter().println("Micro cash game challenged successfuly crawled.");
        LOGGER.info("Crawling data from winamax : End");
    }


    private static String getUrl(String requestURI) {
        LOGGER.info("Choosing winanamax url from crawler url;" + requestURI);
        if (requestURI.contains("micro"))
            return MICRO_URl;
        else if (requestURI.contains("small"))
            return SMALL_URl;
        else if (requestURI.contains("medium"))
            return MEDIUM_URl;
        else if (requestURI.contains("high"))
            return HIGH_URl;
        return MICRO_URl;
    }

    private static Stake getStake(String requestURI) {
        LOGGER.log(Level.FINE, "Getting stake from crawler url;" + requestURI);
        if (requestURI.contains("micro"))
            return Stake.Micro;
        else if (requestURI.contains("small"))
            return Stake.Small;
        else if (requestURI.contains("medium"))
            return Stake.Medium;
        else if (requestURI.contains("high"))
            return Stake.High;

        return Stake.Micro;
    }

    private static final Logger LOGGER = Logger.getLogger(CashGameCrawler.class.getName());
    private static final long serialVersionUID = 1L;
    private static final String MICRO_URl = "https://www.winamax.fr/les-challenges-winamax_cash-game_classement-micro-limites";
    private static final String SMALL_URl = "https://www.winamax.fr/les-challenges-winamax_cash-game_classement-basses-limites";
    private static final String MEDIUM_URl = "https://www.winamax.fr/les-challenges-winamax_cash-game_classement-moyennes-limites";
    private static final String HIGH_URl = "https://www.winamax.fr/les-challenges-winamax_cash-game_classement-hautes-limites";
}
