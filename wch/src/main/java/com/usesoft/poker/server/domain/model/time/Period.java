package com.usesoft.poker.server.domain.model.time;

import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.usesoft.poker.server.domain.common.BaseEntity;

public class Period extends BaseEntity<Period>{

    public Period(@JsonProperty("start")Date start, @JsonProperty("end")Date end) {
        Validate.notNull(start, "Start date is required");
        Validate.notNull(end, "Start date is required");
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean sameIdentityAs(Period other) {
      return other != null 
              && this.getStart().equals(other.getStart()) 
              && this.getEnd().equals(other.getEnd());
    }
    
    @Override
    public String toString() {
        return start.toString() + " -> " + end.toString();
    }
    
    //Return a copy to avoid modification without permission
    public Date getStart() {
        return new Date(start.getTime());
    }
    
    //Return a copy to avoid modification without permission
    public Date getEnd() {
        return new Date(end.getTime());
    }
    

    private final Date start;
    private final Date end;
}
