package com.usesoft.poker.server.domain.model.cashgame;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseEntity;
import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.time.Period;
import com.usesoft.poker.server.infrastructure.pattern.Filter;
import com.usesoft.poker.server.infrastructure.pattern.Filterable;

public class CashGamePerformance extends BaseEntity<CashGamePerformance>
implements Filterable<CashGamePerformance> {

    public CashGamePerformance(@JsonProperty("player") Player player, @JsonProperty("period") Period period, @JsonProperty("stake") Stake stake,
            @JsonProperty("lastUpdate") Date lastUpdate, @JsonProperty("buyIns") double buyIns, @JsonProperty("hands") long hands, @JsonProperty("id") UUID id)
    {
        super(id.toString());

        Validate.notNull(player);
        Validate.notNull(period);
        Validate.notNull(stake);
        Validate.notNull(lastUpdate);
        Validate.notNull(id);
        Validate.notNull(buyIns);
        Validate.notNull(hands);
        Validate.isTrue(hands >= 0);

        this.player = player;
        this.period = period;
        this.stake = stake;
        this.lastUpdate = lastUpdate;
        this.buyIns = buyIns;
        this.hands = hands;
    }

    @Override
    public String toString() {
        return "Cash game - " + id + " - " + stake.toString() + " - " + period.toString() + " - " + player.toString() + " - " + hands + " - " + buyIns;
    }

    @Override
    public boolean sameIdentityAs(CashGamePerformance other)
    {
        return other != null && getPlayer().sameIdentityAs(other.getPlayer()) && period.sameIdentityAs(other.period) && stake == other.stake
                && id.equals(other.id);
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

    @Override
    public boolean accept(Filter<CashGamePerformance> filter) {
        return filter.filter(this);
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    private final long hands;
    private final double buyIns;
    private final Player player;
    private final Period period;
    private final Stake stake;

    private Date lastUpdate;
}
