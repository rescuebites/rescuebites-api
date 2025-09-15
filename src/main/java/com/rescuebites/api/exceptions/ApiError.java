package com.rescuebites.api.exceptions;

public record ApiError (
        int status,
        String message,
        String detail
){}