package com.rescuebites.api.users.data.mappers;

import com.rescuebites.api.users.controllers.requests.UserRegistrationRequest;
import com.rescuebites.api.users.controllers.responses.UserResponse;
import com.rescuebites.api.users.data.models.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

    // Convierto el Dto (UserRegistrationRequest) a una entidad (User)
    public User toUser(UserRegistrationRequest userRegistrationRequest) {
        return User.builder()
                .userId(UUID.randomUUID())
                .email(userRegistrationRequest.email())
                .password(userRegistrationRequest.password())
                .role(userRegistrationRequest.role())
                .build();
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        );
    }

}