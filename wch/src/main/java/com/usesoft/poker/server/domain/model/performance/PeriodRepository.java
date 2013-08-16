package com.usesoft.poker.server.domain.model.performance;

import java.util.Collection;
import java.util.Date;

public interface PeriodRepository {
    
    Collection<Period> findAll();
    
    void store(Period period);
    
    Period find(Date startDate, Date endDate);
}
