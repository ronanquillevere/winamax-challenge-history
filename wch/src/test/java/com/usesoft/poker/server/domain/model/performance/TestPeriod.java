package com.usesoft.poker.server.domain.model.performance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import com.usesoft.poker.server.domain.model.common.EntityUtil;
import com.usesoft.poker.server.domain.model.time.Period;

public class TestPeriod {

    @Test
    public void testInvariant() {
        try {
            new Period(null, new Date());
            fail("Start date should be mandatory");
        } catch (Exception e) {
        }

        try {
            new Period(new Date(), null);
            fail("End date should be mandatory");
        } catch (Exception e) {
        }
    }

    @Test
    public void test() throws InterruptedException {
        Date startDate = new Date();
        long time = startDate.getTime();
        Date startDate2 = new Date(time +10 );
        Date endDate = new Date(time + 20);
        Date endDate2 = new Date(time + 30);

        assertFalse(startDate.equals(startDate2));
        assertFalse(endDate.equals(endDate2));

        Period p = new Period(startDate, endDate);
        Period p2 = new Period(startDate, endDate);
        Period p3 = new Period(startDate2, endDate2);

        EntityUtil.checkValues(p, p2, p3);

    }
}
