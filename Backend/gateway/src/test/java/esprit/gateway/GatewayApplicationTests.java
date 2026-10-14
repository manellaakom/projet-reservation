package esprit.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class GatewayApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void contextLoads() {
    }

    @Test
    void routesExposeRealReservationServices() {
        Map<String, URI> routes = routeLocator.getRoutes()
                .collectMap(Route::getId, Route::getUri)
                .block();

        assertNotNull(routes);
        assertEquals(URI.create("lb://gestion-bagages"), routes.get("bagages"));
        assertEquals(URI.create("http://localhost:8003"), routes.get("reservations"));
        assertEquals(2, routes.size());
    }
}
