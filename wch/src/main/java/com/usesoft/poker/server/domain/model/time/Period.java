package com.usesoft.poker.server.domain.model.time;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseEntity;

public class Period extends BaseEntity<Period>{

    private static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat ID_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static String generateId(Date start, Date end)
    {
        Validate.notNull(start, "Start date is required");
        Validate.notNull(end, "End date is required");
        String startId = ID_DATE_FORMAT.format(start);
        String endId = ID_DATE_FORMAT.format(end);
        return startId + endId;
    }

    public Period(@JsonProperty("start") Date start, @JsonProperty("end") Date end, @JsonProperty("id") String id)
    {
        Validate.notNull(start, "Start date is required");
        Validate.notNull(end, "End date is required");
        Validate.notNull(id, "Id is required");
        this.start = start;
        this.end = end;
        this.id = id;
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

    public String getId()
    {
        return id;
    }

    private final String id;
    private final Date start;
    private final Date end;
}
