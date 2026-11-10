package esprit.gateway;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        String defaultHost = System.getenv().getOrDefault("GATEWAY_TARGET_HOST", "host.docker.internal");
        String rechercheUri = System.getenv().getOrDefault("RECHERCHE_URI", "http://" + defaultHost + ":8082");
        String bagagesUri = System.getenv().getOrDefault("BAGAGES_URI", "http://" + defaultHost + ":8081");
        String paiementsUri = System.getenv().getOrDefault("PAIEMENTS_URI", "http://" + defaultHost + ":8084");
        String supportUri = System.getenv().getOrDefault("SUPPORT_URI", "http://" + defaultHost + ":8000");

        return builder.routes()
                .route("root", route -> route.path("/")
                        .filters(f -> f.redirect(302, "/api/recherche/voyages"))
                        .uri(rechercheUri))
                .route("swagger", route -> route.path("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_LAST")
                                       .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_LAST"))
                        .uri(rechercheUri))
                .route("recherche", route -> route.path("/api/recherche/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_LAST")
                                       .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_LAST"))
                        .uri(rechercheUri))
                .route("bagages", route -> route.path("/api/bagages/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_LAST")
                                       .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_LAST"))
                        .uri(bagagesUri))
                .route("paiements", route -> route.path("/api/paiements/**", "/mock-paymee/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_LAST")
                                       .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_LAST"))
                        .uri(paiementsUri))
                .route("reservations", route -> route.path("/api/v1/**")
                        .uri("http://" + defaultHost + ":8003"))
                .route("support", route -> route.path("/support/**", "/api/support-requests/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_LAST")
                                       .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_LAST"))
                        .uri(supportUri))
                .build();
    }

    @org.springframework.web.bind.annotation.RestController
    static class GatewayActuatorFallbackController {
        @org.springframework.web.bind.annotation.GetMapping("/actuator/info")
        public reactor.core.publisher.Mono<java.util.Map<String, Object>> info() {
            return reactor.core.publisher.Mono.just(java.util.Map.of(
                    "app", "gateway",
                    "service", "api-gateway",
                    "status", "UP",
                    "port", 8080
            ));
        }

        @org.springframework.web.bind.annotation.GetMapping("/actuator/health")
        public reactor.core.publisher.Mono<java.util.Map<String, String>> health() {
            return reactor.core.publisher.Mono.just(java.util.Map.of("status", "UP"));
        }
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
