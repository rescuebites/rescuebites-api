package com.rescuebites.api.commerce.data.mappers;

import com.rescuebites.api.commerce.controllers.requests.BusinessHoursRequest;
import com.rescuebites.api.commerce.controllers.responses.BusinessHoursResponse;
import com.rescuebites.api.commerce.data.models.BusinessHours;
import com.rescuebites.api.commerce.data.models.Commerce;

import java.util.Collection;
import java.util.List;

public class BusinessHoursMapper {

    private BusinessHoursMapper() {}

    public static BusinessHours toBusinessHours(BusinessHoursRequest request, Commerce commerce) {
        return BusinessHours.builder()
                .commerce(commerce)
                .dayOfWeek(request.getDayOfWeek())
                .closed(request.isClosed())
                .openTime(request.getOpenTime())
                .closeTime(request.getCloseTime())
                .afternoonOpenTime(request.getAfternoonOpenTime())
                .afternoonCloseTime(request.getAfternoonCloseTime())
                .build();
    }

    public static List<BusinessHours> toBusinessHoursList(List<BusinessHoursRequest> requests, Commerce commerce) {
        return requests.stream()
                .map(request -> toBusinessHours(request, commerce))
                .toList();
    }

    public static BusinessHoursResponse toBusinessHoursResponse(BusinessHours businessHours) {
        return new BusinessHoursResponse(
                businessHours.getDayOfWeek(),
                businessHours.isClosed(),
                businessHours.getOpenTime(),
                businessHours.getCloseTime(),
                businessHours.getAfternoonOpenTime(),
                businessHours.getAfternoonCloseTime()
        );
    }

    public static List<BusinessHoursResponse> toBusinessHoursResponseList(Collection<BusinessHours> businessHours) {
        return businessHours.stream()
                .map(BusinessHoursMapper::toBusinessHoursResponse)
                .toList();
    }
}
