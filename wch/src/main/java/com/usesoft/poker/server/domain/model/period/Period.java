package com.usesoft.poker.server.domain.model.period;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseEntity;
import com.usesoft.poker.server.domain.common.BaseEntityVisitor;

public class Period extends BaseEntity<Period>{

    private static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat ID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    private static String generateId(Date start, Date end)
    {
        Validate.notNull(start, "Start date is required");
        Validate.notNull(end, "End date is required");
        String startId = ID_DATE_FORMAT.format(start);
        String endId = ID_DATE_FORMAT.format(end);
        return startId + endId;
    }

    public Period(@JsonProperty("start") Date start, @JsonProperty("end") Date end)
    {
        super(generateId(start, end));

        Validate.notNull(start);
        Validate.notNull(end);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean sameIdentityAs(Period other) {
        return other != null && this.getStart().equals(other.getStart()) && this.getEnd().equals(other.getEnd()) && this.getId().equals(other.getId());
    }

    @Override
    public String toString() {
        return logDateFormat.format(start) + " -> " + logDateFormat.format(end) + " id : " + (id == null ? "" : id);
    }

    //Return a copy to avoid modification without permission
    public Date getStart() {
        return new Date(start.getTime());
    }

    //Return a copy to avoid modification without permission
    public Date getEnd() {
        return new Date(end.getTime());
    }

    @Override
    public <IN, OUT> OUT accept(BaseEntityVisitor<IN, OUT> visitor, IN in)
    {
        return visitor.visit(this, in);
    }

    private final Date start;
    private final Date end;
}
