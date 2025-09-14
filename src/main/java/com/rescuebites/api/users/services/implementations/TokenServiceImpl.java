package com.rescuebites.api.users.services.implementations;

import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.users.repositories.ITokenRepository;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.rescuebites.api.users.utils.Constants.HOURS_LIMIT;
import static com.rescuebites.api.users.utils.Constants.MAX_TOKENS_PER_HOUR;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements ITokenService {

    private final ITokenRepository tokenRepository;

    @Override
    public Token saveUserToken(User newUser) {
        Token token = Token.builder()
                .tokenId(UUID.randomUUID())
                .user(newUser)
                .build();
        return tokenRepository.save(token);
    }

    @Override
    public Token findByTokenOrThrowException(UUID token) {
        return tokenRepository.findByTokenId(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "token", token));
    }

    @Override
    public boolean canResendToken(User user) {
        LocalDateTime hoursLimit = LocalDateTime.now().minusHours(HOURS_LIMIT);
        long tokensInLastHours = tokenRepository.countByUserAndCreatedAtAfter(user, hoursLimit);

        return tokensInLastHours < MAX_TOKENS_PER_HOUR;
    }

    //POR EL MOMENTO NO SE UTILIZA, PERO SE DEJA POR SI SE NECESITA
    /*
    @Override
    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }

     */
}