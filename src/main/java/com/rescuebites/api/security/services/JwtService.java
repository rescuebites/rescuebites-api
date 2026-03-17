package com.rescuebites.api.security.services;

import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.security.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long jwtExpiration;

    private final ICommerceRepository commerceRepository;
    private final IClientRepository clientRepository;

    public String generateToken(final User user){
        return buildToken(user, jwtExpiration);
    }

    private String buildToken(User user, final long jwtExpiration) {
        var builder = Jwts.builder()
                .setSubject(user.getEmail()) //Manera de identificar al usuario con el token
                .claim("role", user.getRole()) // Agregamos el rol como un claim
                .setIssuedAt(new Date(System.currentTimeMillis())) //Fecha de creacion del token
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)); // Fecha de expiracion del token

        // Si el usuario es un comercio, intentamos agregar commerceId y commerceType como claims
        if (user.getRole() == Role.COMMERCE) {
            Optional<Commerce> commerceOpt = commerceRepository.findByUserId(user.getUserId());
            if (commerceOpt.isPresent()) {
                Commerce commerce = commerceOpt.get();
                if (commerce.getCommerceId() != null) {
                    builder.claim("commerceId", commerce.getCommerceId().toString());
                }
                if (commerce.getCommerceTypes() != null && !commerce.getCommerceTypes().isEmpty()) {
                    // Tomamos el primer tipo como tipo principal
                    builder.claim("commerceType", commerce.getCommerceTypes().get(0).getName().name());
                }
            }
        } else if (user.getRole() == Role.CLIENT) {
            clientRepository.findByUserId(user.getUserId())
                    .ifPresent(client -> builder.claim("clientId", client.getClientId()));
        }

        return builder
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact(); //Genera el token en formato String
    }

    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    Lógica para extraer un claim específico del token JWT.
     */

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() //Validamos y deserializamos el token.
                .setSigningKey(getSignInKey()) //Configura la clave de firma para validar el token.
                .build()
                .parseClaimsJws(token) //Toma el token y lo descompone en sus partes (header, payload, signature).
                .getBody(); //Obtiene el payload del token, que contiene los claims.
    }

    /*
    Extraemos los claims del token necesarios para la autenticación. En este caso,
    el email del usuario (subject) y la fecha de expiración del token, teniendo en
    cuenta si el token ha expirado o no.
     */

    public String extractEmailFromToken(String token) {
        // Logic to extract username from the JWT token
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public UUID extractCommerceIdFromToken(String token) {
        String val = extractClaim(token, claims -> claims.get("commerceId", String.class));
        return val == null ? null : UUID.fromString(val);
    }

    public String extractCommerceTypeFromToken(String token) {
        return extractClaim(token, claims -> claims.get("commerceType", String.class));
    }

    public UUID extractClientIdFromToken(String token) {
        String val = extractClaim(token, claims -> claims.get("clientId", String.class));
        return val == null ? null : UUID.fromString(val);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String email = extractEmailFromToken(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); //Si la fecha de expiración es anterior (mayor) a la fecha actual, el token ha expirado -> devuelve true
    }

}
