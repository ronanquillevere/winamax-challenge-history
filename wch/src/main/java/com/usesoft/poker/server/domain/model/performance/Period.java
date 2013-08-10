package com.usesoft.poker.server.domain.model.performance;

import java.util.Date;

import org.apache.commons.lang3.Validate;

import com.usesoft.poker.server.domain.common.ValueObject;

public class Period implements ValueObject<Period>{

    public Period(Date start, Date end) {
        Validate.notNull(start, "Start date is required");
        Validate.notNull(end, "Start date is required");
        this.start = start;
        this.end = end;
    }

    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Period other = (Period) o;

      return sameValueAs(other);
    }

    @Override
    public boolean sameValueAs(Period other) {
      return other != null 
              && this.start.equals(other.start) 
              && this.end.equals(other.end);
    }
    
    private final Date start;
    private final Date end;
    
    private static final long serialVersionUID = 1L;
}
