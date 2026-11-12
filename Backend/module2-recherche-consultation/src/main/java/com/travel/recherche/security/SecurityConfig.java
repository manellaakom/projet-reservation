package com.travel.recherche.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration Spring Security pour le Module 2 (Note 2 de la grille AWD)
 * - Lecture publique (Recherche, Consultation, Filtres, Comparateur, Suggestions)
 * - Écriture protégée par Token JWT (Création, Modification)
 * - Suppression réservée au rôle ROLE_ADMIN
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 console support
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 0. Requêtes prévol CORS (OPTIONS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 1. Documentation OpenAPI / Swagger UI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/", "/actuator/**", "/h2-console/**").permitAll()

                        // 2. Endpoints d'authentification
                        .requestMatchers("/api/recherche/auth/**").permitAll()

                        // 3. Toutes les consultations et recherches sont publiques pour les voyageurs
                        .requestMatchers(HttpMethod.GET, "/api/recherche/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/recherche/voyages/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/recherche/comparer").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/recherche/voyages/**").permitAll()

                        // 4. Démonstrations inter-services (Feign & RabbitMQ)
                        .requestMatchers("/api/recherche/inter-services/**").permitAll()

                        // 5. Opérations protégées sur les offres de voyage (Gestion CRUD par Admin/Gestionnaire)
                        .requestMatchers(HttpMethod.POST, "/api/recherche/voyages").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/recherche/voyages/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/recherche/voyages/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/recherche/voyages/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
