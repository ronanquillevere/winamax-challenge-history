package com.usesoft.poker.server.domain.model.performance;

import org.apache.commons.lang3.Validate;

import com.usesoft.poker.server.domain.common.Entity;

public class CashGamePerformance implements Entity<CashGamePerformance> {

    public CashGamePerformance(PerformanceId id, Period period) {
        Validate.notNull(id, "Id is required.");
        Validate.notNull(period, "Period is required.");
        this.id = id;
        this.period = period;
    }

    @Override
    public boolean sameIdentityAs(CashGamePerformance other) {
        return other != null && id.sameValueAs(other.id);
    }

    public Period getPeriod() {
        return period;
    }

    public long getHands() {
        return hands;
    }

    public void setHands(long hands) {
        Validate.isTrue(hands >= 0, "Number of hands must be positive.");

        this.hands = hands;
    }

    public double getBuyIns() {
        return buyIns;
    }

    public void setBuyIns(double buyIns) {
        this.buyIns = buyIns;
    }

    private final PerformanceId id;
    private final Period period;
    private long hands = 0;
    private double buyIns = 0;

}
