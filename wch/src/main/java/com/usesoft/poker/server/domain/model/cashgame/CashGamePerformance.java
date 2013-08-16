package com.usesoft.poker.server.domain.model.cashgame;

import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseEntity;
import com.usesoft.poker.server.domain.model.performance.Period;
import com.usesoft.poker.server.domain.model.performance.Stake;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.infrastructure.pattern.Filter;
import com.usesoft.poker.server.infrastructure.pattern.Filterable;

public class CashGamePerformance extends BaseEntity<CashGamePerformance>
        implements Filterable<CashGamePerformance> {

    public CashGamePerformance(@JsonProperty("player") Player player, @JsonProperty("period") Period period, @JsonProperty("stake") Stake stake,
            @JsonProperty("lastUpdate") Date lastUpdate) {
        Validate.notNull(player, "Player is required.");
        Validate.notNull(period, "Period is required.");
        Validate.notNull(stake, "Stake is required.");
        Validate.notNull(lastUpdate, "Last update is required.");
        this.player = player;
        this.period = period;
        this.stake = stake;
        this.setLastUpdate(lastUpdate);
    }

    @Override
    public String toString() {
        return "Cash game - " + stake.toString() + " - " + period.toString() + " - " + player.toString() + " - " +   hands + " - " + buyIns;
    }

    @Override
    public boolean sameIdentityAs(CashGamePerformance other) {
        return other != null && getPlayer().sameIdentityAs(other.getPlayer())
                && period.sameValueAs(other.period) && stake == other.stake;
    }

    public Period getPeriod() {
        return period;
    }

    public long getHands() {
        return hands;
    }

    public double getBuyIns() {
        return buyIns;
    }

    public Stake getStake() {
        return stake;
    }

    public Player getPlayer() {
        return player;
    }

    public void setHands(long hands) {
        Validate.isTrue(hands >= 0, "Number of hands must be positive.");
        this.hands = hands;
    }

    public void setBuyIns(double buyIns) {
        this.buyIns = buyIns;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    private long hands = 0;
    private double buyIns = 0;
    private final Player player;
    private final Period period;
    private final Stake stake;

    private Date lastUpdate;

    @Override
    public boolean accept(Filter<CashGamePerformance> filter) {
        return filter.filter(this);
    }
}
