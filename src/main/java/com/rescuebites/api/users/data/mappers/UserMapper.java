package com.rescuebites.api.users.data.mappers;

import com.rescuebites.api.users.controllers.requests.RegisterRequest;
import com.rescuebites.api.users.data.models.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

    // Convierto el Dto (RegisterRequest) a una entidad (User)
    public User toUser(RegisterRequest registerRequest) {
        return User.builder()
                .userId(UUID.randomUUID())
                //.username(registerRequest.firstName())
                .email(registerRequest.email())
                .password(registerRequest.password())
                .role(registerRequest.role())
                .build();
    }
}