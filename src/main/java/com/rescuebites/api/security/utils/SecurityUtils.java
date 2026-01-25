package com.rescuebites.api.security.utils;

import com.rescuebites.api.exceptions.custom_exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No estás autenticado");
        }

        return authentication.getName();
    }

    public static void validateOwnership(String resourceOwnerEmail) {
        String authenticatedUserEmail = getAuthenticatedUserEmail();

        if (!authenticatedUserEmail.equals(resourceOwnerEmail)) {
            throw new UnauthorizedException("No tienes permiso para realizar esta acción");
        }
    }

    public static boolean isOwner(String resourceOwnerEmail) {
        try {
            String authenticatedUserEmail = getAuthenticatedUserEmail();
            return authenticatedUserEmail.equals(resourceOwnerEmail);
        } catch (UnauthorizedException e) {
            return false;
        }
    }

    public static String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No estás autenticado");
        }

        return authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElseThrow(() -> new UnauthorizedException("No se pudo determinar el rol del usuario"));
    }
}