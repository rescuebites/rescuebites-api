package com.rescuebites.api.commerce.utils;

import com.rescuebites.api.commerce.data.enums.CommerceScheduleStatus;
import com.rescuebites.api.commerce.data.models.BusinessHours;
import com.rescuebites.api.commerce.data.models.Commerce;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BusinessHoursUtils {

    private BusinessHoursUtils() {
    }

    public static CommerceScheduleStatus getCommerceStatus(Commerce commerce, LocalDateTime dateTime) {
        BusinessHours hours = findTodayHours(commerce, dateTime.getDayOfWeek());

        if (hours.isClosed()) {
            return CommerceScheduleStatus.closedForDay();
        }

        LocalTime currentTime = dateTime.toLocalTime();

        if (hours.isOpenAt(currentTime)) {
            return CommerceScheduleStatus.open(getCurrentCloseTime(hours, currentTime));
        }

        return resolveClosedStatus(hours, currentTime);
    }

    private static BusinessHours findTodayHours(Commerce commerce, DayOfWeek day) {
        return commerce.getBusinessHours().stream()
                .filter(bh -> bh.getDayOfWeek().equals(day))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró horario para el día " + day + " del comercio '" + commerce.getName() + "'"));
    }

    private static LocalTime getCurrentCloseTime(BusinessHours hours, LocalTime currentTime) {
        boolean isInAfternoonShift = hours.hasSplitSchedule()
                && !currentTime.isBefore(hours.getAfternoonOpenTime());
        return isInAfternoonShift ? hours.getAfternoonCloseTime() : hours.getCloseTime();
    }

    private static CommerceScheduleStatus resolveClosedStatus(BusinessHours hours, LocalTime currentTime) {
        if (currentTime.isBefore(hours.getOpenTime())) {
            return CommerceScheduleStatus.closedReopensLater(
                    hours.getOpenTime(), hours.getCloseTime());
        }

        if (hours.willReopenLater(currentTime)) {
            return CommerceScheduleStatus.closedReopensLater(
                    hours.getAfternoonOpenTime(), hours.getAfternoonCloseTime());
        }

        return CommerceScheduleStatus.closedForDay();
    }
}
