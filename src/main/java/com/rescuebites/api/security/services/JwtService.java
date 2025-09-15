package com.rescuebites.api.security.services;

import com.rescuebites.api.users.data.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long jwtExpiration;

    public String generateToken(final User user){
        return buildToken(user, jwtExpiration);
    }

    private String buildToken(User user, final long jwtExpiration) {
        return Jwts.builder()
                .claim("role", user.getRole())
                .setSubject(user.getEmail()) //Manera de identificar al usuario con el token
                .setIssuedAt(new Date(System.currentTimeMillis())) //Fecha de creacion del token
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
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

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String email = extractEmailFromToken(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); //Si la fecha de expiración es anterior (mayor) a la fecha actual, el token ha expirado -> devuelve true
    }

}
