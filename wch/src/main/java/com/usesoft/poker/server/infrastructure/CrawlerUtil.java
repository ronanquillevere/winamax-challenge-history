package com.usesoft.poker.server.infrastructure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CrawlerUtil
{
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
    private static GregorianCalendar calendar = new GregorianCalendar();

    private static final String datePattern = "(\\d{1,2} \\w+ \\d{4})";
    private static final Pattern pattern = Pattern.compile(datePattern);

    private static final Logger LOGGER = Logger.getLogger(CrawlerUtil.class.getName());

    public static double extractBuyIns(Document document, int perfIndex)
    {
        int lookupIndex = 7;
        if (perfIndex > 20)
            lookupIndex = 4;
        String value = getCellValue(document, perfIndex, lookupIndex).replaceAll(",", ".");
        return Double.valueOf(value);
    }

    public static int extractNumberOfHands(Document document, int perfIndex)
    {
        int lookupIndex = 5;
        if (perfIndex > 20)
            lookupIndex = 3;
        String value = getCellValue(document, perfIndex, lookupIndex);
        return Integer.valueOf(value);
    }

    public static int extractNumberOfRows(Document document)
    {
        Elements lines = document.select(".leaderb").get(0).getElementsByTag("tr");
        int count = lines.size() - 2;
        LOGGER.log(Level.INFO, "Found Number of rows; " + count);
        return count; // header + footer
    }

    public static String extractPlayerName(Document document, int perfIndex)
    {
        int lookupIndex = 3;
        if (perfIndex > 20)
            lookupIndex = 2;
        String value = getCellValue(document, perfIndex, lookupIndex);
        value = StringEscapeUtils.unescapeHtml4(value);
        return value;
    }

    public static String extractDatePeriod(Document document)
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

    static String cleanDatePeriod(String value)
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

    private static String getCellValue(Document document, int perfIndex, int cellIndex)
    {
        String v = document.select(".leaderb").get(0).getElementsByTag("tr").get(perfIndex).getAllElements().get(cellIndex).getElementsByTag("td").html()
                .replaceFirst("<b>", "").replaceFirst("</b>", "");
        v = StringEscapeUtils.unescapeHtml4(v).replaceAll(" ", "");
        return v;
    }

    // TODO enrich if needed ...
    private static String replaceMonth(String date)
    {
        date = date.replace("janv.", "Janvier");
        date = date.replace("fev.", "Février");
        date = date.replace("sept.", "Septembre");
        date = date.replace("oct.", "Octobre");
        date = date.replace("nov.", "Novembre");
        date = date.replace("dec.", "Décembre");

        LOGGER.log(Level.FINE, "Month abreviation replaced;" + date);
        return date;
    }

    private CrawlerUtil()
    {
    }
}
