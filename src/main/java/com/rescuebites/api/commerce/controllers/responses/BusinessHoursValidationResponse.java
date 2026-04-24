package com.rescuebites.api.commerce.controllers.responses;

import java.util.List;

public record BusinessHoursValidationResponse(boolean valid, List<String> errors) {

    public static BusinessHoursValidationResponse ok() {
        return new BusinessHoursValidationResponse(true, List.of());
    }

    public static BusinessHoursValidationResponse withErrors(List<String> errors) {
        return new BusinessHoursValidationResponse(false, errors);
    }

    public static BusinessHoursValidationResponse of(List<String> errors) {
        return errors.isEmpty() ? ok() : withErrors(errors);
    }
}
