package com.usesoft.poker.server.infrastructure;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestCrawlerUtil {

    @Test
    public void testClean(){
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
}
