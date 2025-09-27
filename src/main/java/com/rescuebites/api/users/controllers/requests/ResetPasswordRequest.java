package com.rescuebites.api.users.controllers.requests;

import java.util.UUID;

public record ResetPasswordRequest (
        UUID token,
        String newPassword,
        String confirmNewPassword
){}