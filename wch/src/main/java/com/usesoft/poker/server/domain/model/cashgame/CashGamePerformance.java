package com.usesoft.poker.server.domain.model.cashgame;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseEntity;
import com.usesoft.poker.server.domain.common.BaseEntityVisitor;
import com.usesoft.poker.server.domain.common.EntityReference;
import com.usesoft.poker.server.infrastructure.pattern.Filter;
import com.usesoft.poker.server.infrastructure.pattern.Filterable;

public class CashGamePerformance extends BaseEntity<CashGamePerformance>
implements Filterable<CashGamePerformance> {

    public CashGamePerformance(@JsonProperty("playerReference") EntityReference playerReference,
            @JsonProperty("periodReference") EntityReference periodReference, @JsonProperty("stake") Stake stake,
            @JsonProperty("lastUpdate") Date lastUpdate, @JsonProperty("buyIns") double buyIns, @JsonProperty("hands") long hands, @JsonProperty("id") UUID id)
    {
        super(id.toString());

        Validate.notNull(playerReference);
        Validate.notNull(periodReference);
        Validate.notNull(stake);
        Validate.notNull(lastUpdate);
        Validate.notNull(id);
        Validate.notNull(buyIns);
        Validate.notNull(hands);
        Validate.isTrue(hands >= 0);

        this.playerReference = playerReference;
        this.periodReference = periodReference;
        this.stake = stake;
        this.lastUpdate = lastUpdate;
        this.buyIns = buyIns;
        this.hands = hands;
    }

    @Override
    public String toString() {
        return "Cash game - " + id + " - " + stake.toString() + " - " + periodReference.getId().toString() + " - " + playerReference.getId().toString() + " - "
                + hands + " - " + buyIns;
    }

    @Override
    public boolean sameIdentityAs(CashGamePerformance other)
    {
        return other != null && id.equals(other.id);
    }

    public EntityReference getPeriodReference()
    {
        return periodReference;
    }

    public EntityReference getPlayerReference()
    {
        return playerReference;
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

    @Override
    public boolean accept(Filter<CashGamePerformance> filter) {
        return filter.filter(this);
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public <IN, OUT> OUT accept(BaseEntityVisitor<IN, OUT> visitor, IN in)
    {
        return visitor.visit(this, in);
    }

    private final long hands;
    private final double buyIns;
    private final EntityReference playerReference;
    private final EntityReference periodReference;
    private final Stake stake;

    private Date lastUpdate;
}
