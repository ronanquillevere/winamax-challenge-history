package com.usesoft.poker.server.domain.model.time;

import java.util.Collection;
import java.util.Date;

public interface PeriodRepository {

    Collection<Period> findAll();

    void store(Period period, boolean force);

    Period find(Date startDate, Date endDate);

    Period find(String id);
}
