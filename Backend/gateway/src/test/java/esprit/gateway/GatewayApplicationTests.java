package esprit.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class GatewayApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void contextLoads() {
    }

    @Test
    void routesUseEurekaLoadBalancing() {
        Map<String, URI> routes = routeLocator.getRoutes()
                .collectMap(Route::getId, Route::getUri)
                .block();

        assertNotNull(routes);
        assertEquals(URI.create("lb://gestion-bagages"), routes.get("bagages"));
        assertEquals(URI.create("lb://recherche-consultation"), routes.get("recherche"));
        assertEquals(URI.create("lb://reservation-paiement"), routes.get("paiements"));
        assertEquals(URI.create("lb://reservation-service"), routes.get("reservations"));
        assertEquals(URI.create("lb://support-client-backend"), routes.get("support"));
    }
}
