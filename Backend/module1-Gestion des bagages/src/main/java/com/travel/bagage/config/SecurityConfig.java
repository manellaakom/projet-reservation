package com.travel.bagage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://localhost:8180/realms/skyvoyage/protocol/openid-connect/certs}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Endpoints publics : Documentation OpenAPI, Swagger UI et Actuator (monitoring)
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/actuator/**"
                ).permitAll()

                // 2. Statistiques : réservé exclusivement au rôle admin
                .requestMatchers(HttpMethod.GET, "/api/bagages/statistics", "/api/bagages/statistiques").hasAnyRole("admin", "ADMIN")

                // 3. Consultation des bagages (liste, détail, recherche réservation, tests Feign) : accessible aux utilisateurs et administrateurs
                .requestMatchers(HttpMethod.GET, "/api/bagages/**").hasAnyRole("user", "USER", "admin", "ADMIN")

                // 4. Opérations d'écriture, modification et suppression : réservées à l'administrateur
                .requestMatchers(HttpMethod.POST, "/api/bagages/**").hasAnyRole("admin", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/bagages/**").hasAnyRole("admin", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/bagages/**").hasAnyRole("admin", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/bagages/**").hasAnyRole("admin", "ADMIN")

                // 5. Tout autre endpoint nécessite une authentification
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
            new JwtTimestampValidator(),
            jwt -> {
                String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : "";
                if (issuer.contains("/realms/skyvoyage")) {
                    return OAuth2TokenValidatorResult.success();
                }
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Issuer non reconnu : " + issuer, null));
            }
        );

        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }
}
