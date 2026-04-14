package com.rescuebites.api.commerce.controllers.responses;

public record CommerceIdentityCheckResponse(boolean available, String error) {

    public static CommerceIdentityCheckResponse ok() {
        return new CommerceIdentityCheckResponse(true, null);
    }

    public static CommerceIdentityCheckResponse taken() {
        return new CommerceIdentityCheckResponse(false,
                "Ya existe un comercio con el mismo nombre, dirección y localidad");
    }
}
