package com.rescuebites.api.config;

import com.rescuebites.api.security.entrypoint.GlobalAuthenticationEntryPoint;
import com.rescuebites.api.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final GlobalAuthenticationEntryPoint globalAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()

                        // AUTENTICACIÓN (Público)
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                        // GESTIÓN DE USUARIOS (Público)
                        .requestMatchers(HttpMethod.POST, "/api/users/*/verify-account").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/resend-verification-account").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/reset-password/email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/reset-password").permitAll()

                        // CLIENTS
                        // Crear cliente (público - después del registro)
                        .requestMatchers(HttpMethod.POST, "/api/v1/clients").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/clients/**").permitAll()

                        // Operaciones de cliente (solo CLIENT role + ownership)
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/*").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/clients/*").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/clients/*").hasRole("CLIENT")

                        // COMMERCES
                        // Crear comercio (público - después del registro)
                        .requestMatchers(HttpMethod.POST, "/api/v1/commerces").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/commerces/**").permitAll()

                        // Operaciones de comercio (solo COMMERCE role + ownership)
                        .requestMatchers(HttpMethod.GET, "/api/v1/commerces/*").hasRole("COMMERCE")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/commerces/*").hasRole("COMMERCE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/commerces/*").hasRole("COMMERCE")

                        // PRODUCTS
                        .requestMatchers("/api/v1/products/**").hasRole("COMMERCE")

                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(globalAuthenticationEntryPoint)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}