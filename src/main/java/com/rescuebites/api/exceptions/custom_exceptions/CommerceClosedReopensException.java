package com.rescuebites.api.exceptions.custom_exceptions;

import java.time.LocalTime;

public class CommerceClosedReopensException extends RuntimeException {

    private final LocalTime nextOpenTime;
    private final LocalTime nextCloseTime;

    public CommerceClosedReopensException(String commerceName, LocalTime nextOpenTime, LocalTime nextCloseTime) {
        super("El comercio '" + commerceName + "' está cerrado en este momento, " +
                "pero reabre de " + nextOpenTime + " a " + nextCloseTime);
        this.nextOpenTime = nextOpenTime;
        this.nextCloseTime = nextCloseTime;
    }

    public LocalTime getNextOpenTime() {
        return nextOpenTime;
    }

    public LocalTime getNextCloseTime() {
        return nextCloseTime;
    }
}
