package com.usesoft.poker.server.infrastructure;

import static org.fest.assertions.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.domain.model.cashgame.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;

public class CrawlerUtil
{
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
    private static GregorianCalendar calendar = new GregorianCalendar();

    private static final String datePattern = "(\\d{1,2} \\w+ \\d{4})";
    private static final Pattern pattern = Pattern.compile(datePattern);

    private static final Logger LOGGER = Logger.getLogger(CrawlerUtil.class.getName());


    public static Period extractPeriod(Document document) throws ParseException
    {
        String periodAsString = CrawlerUtil.extractDatePeriodAsString(document);
        Date start = CrawlerUtil.parseStartDate(periodAsString);
        Date end = CrawlerUtil.parseEndDate(periodAsString);

        return new Period(start, end);
    }

    public static String extractDatePeriodAsString(Document document)
    {
        String value = convertPeriodElementToString(document);
        String cleaned = cleanDatePeriod(value);
        return cleaned;
    }

    public static Date parseEndDate(String period) throws ParseException
    {
        Matcher matcher = pattern.matcher(period);
        matcher.find();
        matcher.find();
        String end = matcher.group(1);
        LOGGER.log(Level.FINE, "Matched end date;" + end);

        Date date = SDF.parse(end);
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public static Date parseStartDate(String period) throws ParseException
    {
        Matcher matcher = pattern.matcher(period);
        matcher.find();
        String start = matcher.group(1);

        LOGGER.log(Level.FINE, "Matched start date;" + start);
        return SDF.parse(start);
    }

    public static Date getParisTime()
    {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date now = calendar.getTime();
        return now;
    }

    public static String cleanDatePeriod(String value)
    {
        String escaped = StringEscapeUtils.unescapeHtml4(value);
        String cleaned = removeHTMLMarkup(escaped);
        cleaned = replaceMonth(cleaned);
        return cleaned;
    }

    private static String convertPeriodElementToString(Document document)
    {
        return document.select(".table_container").get(0).getAllElements().get(1).toString();
    }

    private static String removeHTMLMarkup(String value)
    {
        return value.replace("<b>", "").replace("</b>", "").trim();
    }


    // TODO enrich if needed ...
    private static String replaceMonth(String date)
    {
        date = date.replace("janv.", "Janvier");
        date = date.replace("fev.", "Février");
        date = date.replace("juil.", "Juillet");
        date = date.replace("sept.", "Septembre");
        date = date.replace("oct.", "Octobre");
        date = date.replace("nov.", "Novembre");
        date = date.replace("dec.", "Décembre");

        LOGGER.log(Level.FINE, "Month abreviation replaced;" + date);
        return date;
    }

    // ******************* V2 *******************

    public static void fillPlayersAndPerfs(Document document, Stake stake, Period period, Date timestamp, List<CashGamePerformance> perfs, List<Player> players)
            throws ParseException
            {
        Validate.notNull(document);
        Validate.notNull(stake);
        Validate.notNull(timestamp);
        Validate.notNull(period);
        Validate.notNull(perfs);
        Validate.notNull(players);
        assertThat(perfs.size()).isEqualTo(0);
        assertThat(players.size()).isEqualTo(0);

        List<LineRawData> data = CrawlerUtil.extractData(document);
        for (LineRawData lineRawData : data)
        {
            Player player = CrawlerUtil.buildPlayerFromLineData(lineRawData);
            CashGamePerformance perf = CrawlerUtil.buildCashGamePerformanceFromLineData(stake, timestamp, period, lineRawData, player);

            players.add(player);
            perfs.add(perf);
        }
            }

    private static Element getLeaderBoard(Document document)
    {
        Element element = document.select(".leaderb").get(0);
        return element;
    }

    private static Elements extractLines(Element leaderBoard)
    {
        Elements lines = leaderBoard.getElementsByClass("line2");
        Elements oddLines = leaderBoard.getElementsByClass("line1");

        Iterator<Element> it = oddLines.iterator();

        while (it.hasNext())
        {
            lines.add(it.next());
        }
        return lines;
    }

    public static List<LineRawData> extractData(Document document)
    {
        Element leaderBoard = CrawlerUtil.getLeaderBoard(document);
        Elements lines = CrawlerUtil.extractLines(leaderBoard);

        Iterator<Element> lineIt = lines.iterator();
        List<LineRawData> data = new ArrayList<>();

        while (lineIt.hasNext())
        {
            LineRawData lineData = extractLineData(lineIt.next());
            data.add(lineData);
        }
        return data;
    }

    private static LineRawData extractLineData(Element line)
    {
        Elements cells = line.getElementsByTag("td");
        assertThat(cells.size()).isEqualTo(4);
        LineRawData lineData = new LineRawData();

        for (int i = 0; i < 4; i++)
        {
            Element cell = cells.get(i);
            String content = getCellContentAsString(cell);

            if (i == 0 || i == 2 || i == 3)
                content = cleanNumberFromString(content);

            if (i == 0)
                lineData.setIndexFromString(content);
            else if (i == 1)
                lineData.setPLayerNameFromString(content);
            else if (i == 2)
                lineData.setHandsFromString(content);
            else if (i == 3)
                lineData.setBuyInsFromString(content);
        }
        return lineData;
    }

    private static String cleanNumberFromString(String content)
    {
        return content.replaceAll(" ", "").replace(",", ".");
    }

    private static String getCellContentAsString(Element cell)
    {
        String content = null;
        Elements bold = cell.getElementsByTag("b");
        if (bold == null || bold.size() == 0)
        {
            content = cell.html();
        } else
        {
            content = bold.html();
        }
        return content;
    }

    public static CashGamePerformance buildCashGamePerformanceFromLineData(Stake stake, Date timestamp, Period period, LineRawData lineRawData, Player player)
    {
        return new CashGamePerformance(player, period, stake, timestamp, lineRawData.getBuyIns(), lineRawData.getHands(), UUID.randomUUID());
    }

    public static Player buildPlayerFromLineData(LineRawData lineRawData)
    {
        Player player = new Player(lineRawData.getPlayerName());
        return player;
    }

    private CrawlerUtil()
    {
    }

    public static class LineRawData
    {
        private int index;
        private String playerName;
        private long hands;
        private double buyIns;

        public int getIndex()
        {
            return index;
        }

        public void setIndex(int index)
        {
            this.index = index;
        }

        public String getPlayerName()
        {
            return playerName;
        }

        public void setPlayerName(String playerName)
        {
            this.playerName = playerName;
        }

        public long getHands()
        {
            return hands;
        }

        public void setHands(long hands)
        {
            this.hands = hands;
        }

        public double getBuyIns()
        {
            return buyIns;
        }

        public void setBuyIns(double buyIns)
        {
            this.buyIns = buyIns;
        }

        public void setIndexFromString(String index)
        {
            this.index = Integer.valueOf(index);
        }

        public void setPLayerNameFromString(String playerName)
        {
            this.playerName = StringEscapeUtils.unescapeHtml4(playerName);
        }

        public void setHandsFromString(String hands)
        {
            this.hands = Long.valueOf(hands);
        }

        public void setBuyInsFromString(String buyIns)
        {
            this.buyIns = Double.valueOf(buyIns);
        }
    }
}
