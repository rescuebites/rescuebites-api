package com.rescuebites.api.exceptions;

import java.time.LocalTime;

public record CommerceClosedError(
        int status,
        String message,
        String detail,
        LocalTime nextOpenTime,
        LocalTime nextCloseTime
) {}
