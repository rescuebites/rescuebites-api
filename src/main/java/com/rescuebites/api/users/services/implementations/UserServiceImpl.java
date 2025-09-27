package com.rescuebites.api.users.services.implementations;

import com.rescuebites.api.users.controllers.requests.LoginRequest;
import com.rescuebites.api.users.controllers.requests.RegisterRequest;
import com.rescuebites.api.users.controllers.requests.ResetPasswordRequest;
import com.rescuebites.api.users.controllers.responses.AuthResponse;
import com.rescuebites.api.users.data.mappers.UserMapper;
import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.exceptions.custom_exceptions.*;
import com.rescuebites.api.users.repositories.IUserRepository;
import com.rescuebites.api.security.services.JwtService;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import com.rescuebites.api.shared.EmailBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final EmailBuilder emailBuilder;

    @Override
    public void saveUser(RegisterRequest registerRequest) {
        verifyIfEmailAlreadyExists(registerRequest.email());
        verifyIfPasswordsMatch(registerRequest.password(), registerRequest.confirmPassword());

        User newUser = userMapper.toUser(registerRequest);
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        userRepository.save(newUser);

        //Generamos el token de confirmación y enviamos el email
        UUID confirmationToken = tokenService.saveUserToken(newUser).getTokenId();
        String confirmAccountHtml = emailBuilder.buildConfirmAccount(newUser.getEmail(), confirmationToken);
        emailService.sendEmail(newUser.getEmail(), "Confirm your registration ✔", confirmAccountHtml);
    }

    private void verifyIfPasswordsMatch(String password, String confirmPassword) {
        if(!password.equals(confirmPassword)){
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
    }

    @Override
    public User findByIdOrThrowException(UUID userId)  {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    public User findUserByEmailOrThrowException(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public void verifyIfEmailAlreadyExists(String email) {
        if (userRepository.existsByEmail(email)){
            throw new DuplicateResourceException("User", "email");
        }
    }

    @Override
    public void verifyNewUser(UUID tokenValue) {
        Token token = tokenService.findByTokenOrThrowException(tokenValue);
        ifTokenIsExpiredThrowException(token);

        User user = token.getUser();
        ifUserIsEnabledThrowException(user);
        user.setEnabled(true);
        userRepository.save(user);

        //Eliminamos el token una vez que se ha verificado el usuario
        //tokenService.deleteToken(token);
    }

    @Override
    public void resendConfirmationEmail(String email) {
        User user = findUserByEmailOrThrowException(email);
        ifUserIsEnabledThrowException(user);
        ifResendLimitExceededThrowException(user);

        // Generamos nuevo token y enviamos email
        Token token = tokenService.saveUserToken(user);
        UUID newToken = token.getTokenId();
        String resendConfirmAccountHtml = emailBuilder.buildResendConfirmAccount(user.getEmail(), newToken);
        emailService.sendEmail(user.getEmail(), "Confirm your registration ✔", resendConfirmAccountHtml);
    }

    @Override
    public AuthResponse verifyUser(LoginRequest loginRequest) {
        User user = findUserByEmailOrThrowException(loginRequest.email());
        validatePasswordOrThrowException(loginRequest, user);
        ifUserIsNotEnabledThrowException(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getUserId(), user.getEmail(), token, user.getRole());
    }

    @Override
    public void resetPassword(UUID token, String newPassword, String confirmNewPassword) {
        verifyIfPasswordsMatch(newPassword, confirmNewPassword);
        Token resetToken = tokenService.findByTokenOrThrowException(token);
        ifTokenIsExpiredThrowException(resetToken);

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenService.deleteToken(resetToken);
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        User user = findUserByEmailOrThrowException(email);
        ifResendLimitExceededThrowException(user);

        // Generamos token de reseteo y enviamos email
        Token token = tokenService.saveUserToken(user);
        UUID resetToken = token.getTokenId();
        String resetPasswordHtml = emailBuilder.buildResendConfirmAccount(user.getEmail(), resetToken);
        emailService.sendEmail(user.getEmail(), "Reset your password ✔", resetPasswordHtml);
    }

    private void ifUserIsNotEnabledThrowException(User user) {
        if (!user.isEnabled()) {
            throw new RuntimeException("Debes confirmar tu cuenta antes de iniciar sesión");
        }
    }

    private void validatePasswordOrThrowException(LoginRequest loginRequest, User user) {
        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())){
            throw new RuntimeException("Contraseña inválida.");
        }
    }

    private void ifResendLimitExceededThrowException(User user) {
        if (!tokenService.canResendToken(user)) {
            throw new TooManyRequestsException("Has superado el límite de reenvíos. Intenta nuevamente más tarde");
        }
    }

    private void ifTokenIsExpiredThrowException(Token token) {
        if (token.getTokenExpirationDate() == null || token.getTokenExpirationDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("El token ha expirado. Solicita un nuevo enlace");
        }
    }

    private void ifUserIsEnabledThrowException(User user) {
        if(user.isEnabled()){
            throw new EmailAlreadyVerifiedException("El usuario ya ha sido verificado");
        }
    }
}