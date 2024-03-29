package com.usesoft.poker.server.domain.model.period;

import java.util.Date;

import com.usesoft.poker.server.domain.common.Repository;

public interface PeriodRepository extends Repository<Period>
{
    Period find(Date startDate, Date endDate);
}
