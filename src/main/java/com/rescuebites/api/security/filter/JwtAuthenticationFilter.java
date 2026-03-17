package com.rescuebites.api.security.filter;

import com.rescuebites.api.security.dto.JwtAuthenticationDetails;
import com.rescuebites.api.security.services.JwtService;
import io.jsonwebtoken.ClaimJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static com.rescuebites.api.security.utils.SecurityConstants.WHITELIST;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PathMatcher pathMatcher;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        try{
            /*
             Extraigo el token del Header Authorization de la request.
             */

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwtToken = authHeader.substring(7);

            final String userEmail = jwtService.extractEmailFromToken(jwtToken); //Dado que el subject es el email

            /*
             Evita re-autenticar si hay un Authentication ya en el contexto de seguridad.
             */
            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwtToken, userDetails)) {

                    // Si el token es válido, se crea un objeto de autenticación
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Extraer claims opcionales y guardarlos en details
                    UUID commerceId = jwtService.extractCommerceIdFromToken(jwtToken);
                    String commerceType = jwtService.extractCommerceTypeFromToken(jwtToken);
                    UUID clientId = jwtService.extractClientIdFromToken(jwtToken);

                    authentication.setDetails(new JwtAuthenticationDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request),
                            commerceId,
                            commerceType,
                            clientId
                    ));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new BadCredentialsException("JWT inválido o expirado");
                }
            }

            //Continua con la cadena de filtros
            filterChain.doFilter(request, response);
        } catch (ClaimJwtException e) {

            // Manejo de excepciones, por ejemplo, si el token es inválido o ha expirado
            handlerExceptionResolver.resolveException(request, response, null, e);

        } catch (BadCredentialsException e) {
            // Manejo de excepciones de credenciales inválidas
            handlerExceptionResolver.resolveException(request, response, null, e);
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return Arrays.stream(WHITELIST).anyMatch(p -> pathMatcher.match(p, path));
    }

}
