package com.rescuebites.api.services.implementations;

import com.rescuebites.api.data.models.Token;
import com.rescuebites.api.data.models.User;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.repositories.ITokenRepository;
import com.rescuebites.api.services.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.rescuebites.api.repositories.Constants.HOURS_LIMIT;
import static com.rescuebites.api.repositories.Constants.MAX_TOKENS_PER_HOUR;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements ITokenService {

    private final ITokenRepository tokenRepository;

    @Override
    public Token saveUserToken(User newUser) {
        Token token = Token.builder()
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