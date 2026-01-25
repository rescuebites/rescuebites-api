package com.rescuebites.api.users.services.implementations;

import com.rescuebites.api.users.controllers.requests.LoginRequest;
import com.rescuebites.api.users.controllers.requests.UserRegistrationRequest;
import com.rescuebites.api.users.controllers.responses.AuthResponse;
import com.rescuebites.api.users.data.mappers.UserMapper;
import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.exceptions.custom_exceptions.*;
import com.rescuebites.api.users.events.PasswordResetRequestedEvent;
import com.rescuebites.api.users.events.ResendConfirmationEvent;
import com.rescuebites.api.users.events.UserRegisteredEvent;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.repositories.IUserRepository;
import com.rescuebites.api.security.services.JwtService;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ITokenService tokenService;
    private final IEmailService emailService;
    private final JwtService jwtService;
    private final IUserFacade userFacade;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void saveUser(UserRegistrationRequest userRegistrationRequest) {
        userFacade.ifEmailAlreadyExistsThrowException(userRegistrationRequest.email());
        userFacade.verifyIfPasswordsMatch(userRegistrationRequest.password(), userRegistrationRequest.confirmPassword());

        User newUser = userMapper.toUser(userRegistrationRequest);
        newUser.setPassword(passwordEncoder.encode(userRegistrationRequest.password()));
        userRepository.save(newUser);

        // Generamos el token de confirmación y enviamos el email
        UUID confirmationToken = tokenService.saveUserToken(newUser).getTokenId();
        eventPublisher.publishEvent(new UserRegisteredEvent(newUser, confirmationToken));
    }

    @Override
    public User findByIdOrThrowException(UUID userId) {
        return userRepository.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    public User findUserByEmailOrThrowException(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    @Transactional
    public void verifyNewUser(UUID userId, UUID tokenValue) {
        Token token = tokenService.findByTokenOrThrowException(tokenValue);
        userFacade.validateTokenNotExpired(token);

        User user = token.getUser();
        userFacade.ifUserIsEnabledThrowException(user);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resendConfirmationEmail(String email) {
        User user = findUserByEmailOrThrowException(email);
        userFacade.ifUserIsEnabledThrowException(user);
        userFacade.validateResendLimit(user);

        // Generamos nuevo token y enviamos email
        Token token = tokenService.saveUserToken(user);
        UUID newToken = token.getTokenId();
        eventPublisher.publishEvent(new ResendConfirmationEvent(user, newToken));
    }

    @Override
    public AuthResponse verifyUser(LoginRequest loginRequest) {
        User user = findUserByEmailOrThrowException(loginRequest.email());
        userFacade.validatePasswordOrThrowException(loginRequest.password(), user);
        userFacade.ifUserIsNotEnabledThrowException(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getUserId(), user.getEmail(), token, user.getRole());
    }

    @Override
    @Transactional
    public void resetPassword(UUID token, String newPassword, String confirmNewPassword) {
        userFacade.verifyIfPasswordsMatch(newPassword, confirmNewPassword);
        Token resetToken = tokenService.findByTokenOrThrowException(token);
        userFacade.validateTokenNotExpired(resetToken);

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenService.deleteTokensByUser(user);
    }

    @Override
    @Transactional
    public void sendResetPasswordEmail(String email) {
        User user = findUserByEmailOrThrowException(email);
        userFacade.validateResendLimit(user);

        // Generamos token de reseteo y enviamos email
        Token token = tokenService.saveUserToken(user);
        UUID resetToken = token.getTokenId();
        eventPublisher.publishEvent(new PasswordResetRequestedEvent(user, resetToken));
    }
}