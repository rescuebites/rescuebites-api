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
                                                .requestMatchers("/subscribe/**").permitAll()

                                                // AUTENTICACIÓN (Público)
                                                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                                                // GESTIÓN DE USUARIOS (Público)
                                                .requestMatchers(HttpMethod.POST, "/api/users/*/verify-account")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/users/resend-verification-account")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/users/reset-password/email")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/users/reset-password")
                                                .permitAll()

                                                // CLIENTS
                                                // Crear cliente (público - después del registro)
                                                .requestMatchers(HttpMethod.POST, "/api/v1/clients").permitAll()

                                                // Operaciones de cliente (solo CLIENT role + ownership)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/clients/*").hasRole("CLIENT")
                                                .requestMatchers(HttpMethod.PATCH, "/api/v1/clients/*")
                                                .hasRole("CLIENT")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/clients/*")
                                                .hasRole("CLIENT")
                                                .requestMatchers(HttpMethod.GET, "/api/v1/clients/*/search")
                                                .hasRole("CLIENT")

                                                // CART (Solo clientes)
                                                .requestMatchers("/api/v1/clients/*/cart/**").hasRole("CLIENT")

                                                // PRODUCTS - Client (preferencias)
                                                .requestMatchers("/api/v1/clients/*/products/**").hasRole("CLIENT")

                                                // ORDERS - Client
                                                .requestMatchers("/api/v1/clients/*/orders/**").hasRole("CLIENT")

                                                // COMMERCES
                                                // Crear comercio (público - después del registro)
                                                .requestMatchers(HttpMethod.POST, "/api/v1/commerces").permitAll()

                                                // Validación previa al registro (público - antes de registrar usuario)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/commerces/identity/availability").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/commerces/validate-registration").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/commerces/validate-business-hours").permitAll()

                                                // Operaciones de comercio (solo COMMERCE role + ownership)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/commerces/*")
                                                .hasRole("COMMERCE")
                                                .requestMatchers(HttpMethod.PATCH, "/api/v1/commerces/*")
                                                .hasRole("COMMERCE")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/commerces/*")
                                                .hasRole("COMMERCE")

                                                // Productos de comercio
                                                .requestMatchers(HttpMethod.POST, "/api/v1/commerces/*/products")
                                                .hasRole("COMMERCE")
                                                .requestMatchers(HttpMethod.PATCH, "/api/v1/commerces/*/products/*")
                                                .hasRole("COMMERCE")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/commerces/*/products/*")
                                                .hasRole("COMMERCE")

                                                // Búsqueda del home del comercio
                                                .requestMatchers(HttpMethod.GET, "/api/v1/commerces/*/search").hasRole("COMMERCE")
                                                .requestMatchers(HttpMethod.GET, "/api/v1/commerces/*/search/suggestions").hasRole("COMMERCE")

                                                // Reportes del comercio
                                                .requestMatchers(HttpMethod.GET, "/api/v1/commerces/*/reports/**").hasRole("COMMERCE")

                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/images/*").authenticated()

                                                // Operaciones del HOME del cliente (Públicas)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/public/commerces").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/public/commerces/*")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/public/commerces/type/*")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/public/products/commerce/*/ordered-by-stock").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/public/products/commerce/*/expiring").permitAll()

                                                // SEARCH (Búsqueda pública)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/search").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/search/*").permitAll()

                                                // PAYMENTS
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/payments/orders/*/create-preference")
                                                .hasRole("CLIENT")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook")
                                                .permitAll() 
                                                .requestMatchers(HttpMethod.POST, "/api/v1/payments/orders/*/confirm").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/payments/**").permitAll()

                                                .anyRequest().authenticated())
                                .sessionManagement(sessionManagement -> sessionManagement
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .httpBasic(httpBasic -> httpBasic
                                                .authenticationEntryPoint(globalAuthenticationEntryPoint))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}