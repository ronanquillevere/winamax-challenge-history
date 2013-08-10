package com.usesoft.poker.server.domain.model.performance;

import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.usesoft.poker.server.domain.common.BaseValueObject;

public class PerformanceId extends BaseValueObject<PerformanceId> {

    public PerformanceId(UUID id) {
        Validate.notNull(id, "Id is required.");
        this.id = id;
    }

    @Override
    public boolean sameValueAs(PerformanceId other) {
        return other != null && this.id.equals(other.id);
    }

    private static final long serialVersionUID = 1L;
    private final UUID id;

}
