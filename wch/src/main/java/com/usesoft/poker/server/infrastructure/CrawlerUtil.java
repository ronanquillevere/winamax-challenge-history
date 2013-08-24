package com.usesoft.poker.server.infrastructure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CrawlerUtil {
    private static final SimpleDateFormat SDF = new SimpleDateFormat(
            "dd MMMM yyyy", Locale.FRANCE);
    private static GregorianCalendar calendar = new GregorianCalendar();

    private CrawlerUtil() {
    }

    public static Date getStartDate(Document document) throws ParseException {
        String periode = unescapeDatePeriod(document);
        String start = periode.substring(14, 26);

        return SDF.parse(start);
    }

    public static Date getEndDate(Document document) throws ParseException {
        String periode = unescapeDatePeriod(document);
        String end = periode.substring(30, 42);

        Date date = SDF.parse(end);
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    private static String unescapeDatePeriod(Document document) {
        String value = document.select(".table_container").get(0)
                .getAllElements().get(1).toString();
        String escape = StringEscapeUtils.unescapeHtml4(value);
        return escape;
    }

    public static int getNumberOfRows(Document document) {
        Elements lines = document.select(".leaderb").get(0)
                .getElementsByTag("tr");
        return lines.size() - 2; // header + footer
    }

    // public static String getLine(Document document, int perfIndex){
    // return
    // document.select(".leaderb").get(0).getElementsByTag("tr").get(perfIndex).toString();
    // }

    public static String getPlayerName(Document document, int perfIndex) {
        int lookupIndex = 3;
        if (perfIndex > 20)
            lookupIndex = 2;
        String value = getCellValue(document, perfIndex, lookupIndex);
        value = StringEscapeUtils.unescapeHtml4(value);
        return value;
    }

    private static String getCellValue(Document document, int perfIndex,
            int cellIndex) {
        String v = document.select(".leaderb").get(0).getElementsByTag("tr")
                .get(perfIndex).getAllElements().get(cellIndex)
                .getElementsByTag("td").html().replaceFirst("<b>", "")
                .replaceFirst("</b>", "");
        v = StringEscapeUtils.unescapeHtml4(v).replaceAll(" ", "");
        return v;
    }

    public static int getNumberOfHands(Document document, int perfIndex) {
        int lookupIndex = 5;
        if (perfIndex > 20)
            lookupIndex = 3;
        String value = getCellValue(document, perfIndex, lookupIndex);
        return Integer.valueOf(value);
    }

    public static double getBuyIns(Document document, int perfIndex) {
        int lookupIndex = 7;
        if (perfIndex > 20)
            lookupIndex = 4;
        String value = getCellValue(document, perfIndex, lookupIndex)
                .replaceAll(",", ".");
        return Double.valueOf(value);
    }
}
