package com.usesoft.poker.server.interfaces;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.infrastructure.persistence.datastore.PeriodRepositoryDatastore;

public class UpdatePeriodsAddIds extends HttpServlet
{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        LOGGER.info("Update period ids : start");

        Collection<Period> periods = PeriodRepositoryDatastore.INSTANCE.findAll();

        for (Period period : periods)
        {
            Date start = period.getStart();
            Date end = period.getEnd();
            Period p = new Period(start, end, Period.generateId(start, end));
            PeriodRepositoryDatastore.INSTANCE.store(p, true);
        }

        LOGGER.info("Update period ids : start");
    }

    private static final Logger LOGGER = Logger.getLogger(CashGameCrawler.class.getName());
    private static final long serialVersionUID = 1L;

}
