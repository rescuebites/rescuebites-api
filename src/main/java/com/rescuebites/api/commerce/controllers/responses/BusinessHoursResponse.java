package com.rescuebites.api.commerce.controllers.responses;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record BusinessHoursResponse(
        DayOfWeek dayOfWeek,
        boolean closed,
        LocalTime openTime,
        LocalTime closeTime,
        LocalTime afternoonOpenTime,
        LocalTime afternoonCloseTime
) {}
