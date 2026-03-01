package com.rescuebites.api.commerce.data.enums;

import java.time.LocalTime;

public record CommerceScheduleStatus(
        Status status,
        LocalTime nextOpenTime,
        LocalTime nextCloseTime
) {
    public enum Status {
        OPEN,
        CLOSED_REOPENS_LATER,
        CLOSED_FOR_DAY
    }

    public static CommerceScheduleStatus open(LocalTime closeTime) {
        return new CommerceScheduleStatus(Status.OPEN, null, closeTime);
    }

    public static CommerceScheduleStatus closedReopensLater(LocalTime nextOpen, LocalTime nextClose) {
        return new CommerceScheduleStatus(Status.CLOSED_REOPENS_LATER, nextOpen, nextClose);
    }

    public static CommerceScheduleStatus closedForDay() {
        return new CommerceScheduleStatus(Status.CLOSED_FOR_DAY, null, null);
    }

    public boolean isOpen() {
        return status == Status.OPEN;
    }

    public boolean isClosedForDay() {
        return status == Status.CLOSED_FOR_DAY;
    }
}
