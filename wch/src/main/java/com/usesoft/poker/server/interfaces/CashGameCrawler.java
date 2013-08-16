package com.usesoft.poker.server.interfaces;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.usesoft.poker.server.application.CashGameParser;
import com.usesoft.poker.server.domain.model.performance.Stake;

public class CashGameCrawler extends HttpServlet {

    private static final String MICRO_URl = "http://127.0.0.1:8888/mock/mockcashgamemicro.html";
	private static final String SMALL_URl = "http://127.0.0.1:8888/mock/mockcashgamemicro.html";
	private static final String MEDIUM_URl = "http://127.0.0.1:8888/mock/mockcashgamemicro.html";
	private static final String HIGH_URl = "http://127.0.0.1:8888/mock/mockcashgamemicro.html";
    
    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	   
        LOGGER.debug("Crawling data from winamax : Start");
        
		String requestURI = req.getRequestURI();
		
        try {
            CashGameParser.INSTANCE.parse(Jsoup.connect(getUrl(requestURI)).get(), getStake(requestURI));
        } catch (ParseException e) {
            LOGGER.error("Error while crawling data", e);
            resp.getWriter().println("An exception ocured while crawling micro cash game challenge. See log file.");
            return;
        }
		
        resp.getWriter().println("Micro cash game challenged successfuly crawled.");
        LOGGER.debug("Crawling data from winamax : End");
	}
    

    private static String getUrl(String requestURI) {
        LOGGER.debug("Choosing winanamax url from crawler url : {}", requestURI);
        if (requestURI.contains("micro"))
            return MICRO_URl;
        return MICRO_URl;
    }
    
    private static Stake getStake(String requestURI) {
        LOGGER.debug("Getting stake from crawler url : {}", requestURI);
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
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CashGameCrawler.class);
}
