package com.usesoft.poker.server.infrastructure;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.infrastructure.CrawlerUtil.LineRawData;

public class TestCrawlerUtil
{

    @Test
    public void testCleanDate()
    {
        Map<String, String> data = new HashMap<String, String>();

        data.put("Période du 27 Septembre 2013 au 03 Octobre 2013", "<b>Période du 27 sept. 2013 au 03 oct. 2013</b>");
        data.put("Période du 27 Octobre 2013 au 03 Novembre 2013", "<b>Période du 27 oct. 2013 au 03 nov. 2013</b>");

        for (String key : data.keySet())
        {
            assertEquals(key, CrawlerUtil.cleanDatePeriod(data.get(key)));
        }
    }

    @Test
    public void testParseStart() throws ParseException
    {
        String period = "Période du 27 Septembre 2013 au 03 Octobre 2013";
        Date date = CrawlerUtil.parseStartDate(period);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2013, 8, 27);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date date2 = calendar.getTime();
        assertEquals(date2, date);
    }

    @Test
    public void testParseEnd() throws ParseException
    {
        String period = "Période du 27 Septembre 2013 au 03 Octobre 2013";
        Date date = CrawlerUtil.parseEndDate(period);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2013, 9, 3);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MILLISECOND, 999);

        Date date2 = calendar.getTime();
        assertEquals(date2, date);
    }

    @Test
    public void test() throws ParseException, IOException, URISyntaxException
    {
        Document document = parseDocument("Challenge Cash Game - Micro limites - Winamax Poker.html");

        List<LineRawData> data = CrawlerUtil.extractData(document);

        List<Player> players = new ArrayList<>();
        List<CashGamePerformance> perfs = new ArrayList<>();
        Period period = CrawlerUtil.extractPeriod(document);

        for (LineRawData lineRawData : data)
        {
            Player player = CrawlerUtil.buildPlayerFromLineData(lineRawData);
            CashGamePerformance perf = CrawlerUtil.buildCashGamePerformanceFromLineData(Stake.Micro, new Date(), period, lineRawData, player);

            players.add(player);
            perfs.add(perf);
        }

        assertThat(data.size()).isEqualTo(100);

        System.out.println("test");
    }




    private Document parseDocument(String fileName) throws IOException, URISyntaxException
    {
        URL resource = getClass().getResource(fileName);
        File f = new File(resource.toURI().getPath());
        return Jsoup.parse(f, null);
    }
}
