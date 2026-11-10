package com.travel.paiement.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Faux Paymee pour le développement local (paymee.mock=true, profil "mock").
 * Reproduit create / check / page de paiement / webhook signé.
 */
@RestController
@RequestMapping("/mock/paymee")
@ConditionalOnProperty(name = "paymee.mock", havingValue = "true")
public class MockPaymeeController {

    private record Session(BigDecimal amount, String returnUrl, String webhookUrl, boolean[] paid) {
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final PaymeeProperties properties;
    private final RestClient restClient = RestClient.create();

    public MockPaymeeController(PaymeeProperties properties) {
        this.properties = properties;
    }

    @PostMapping("/api/v2/payments/create")
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        String token = UUID.randomUUID().toString().replace("-", "");
        sessions.put(token, new Session(
                new BigDecimal(String.valueOf(body.get("amount"))),
                String.valueOf(body.get("return_url")),
                String.valueOf(body.get("webhook_url")),
                new boolean[]{false}));
        return Map.of("status", true, "code", 200, "message", "OK", "data", Map.of(
                "token", token,
                "payment_url", properties.getBaseUrl() + "/pay/" + token));
    }

    @GetMapping("/api/v2/payments/{token}/check")
    public ResponseEntity<Map<String, Object>> check(@PathVariable String token) {
        Session s = sessions.get(token);
        if (s == null) {
            return ResponseEntity.status(404).body(Map.of("status", false));
        }
        return ResponseEntity.ok(Map.of("status", true, "data", Map.of(
                "token", token,
                "payment_status", s.paid()[0],
                "amount", s.amount(),
                "transaction_id", "MOCK-" + token.substring(0, 8))));
    }

    /** Simule la page de paiement : marque payé, appelle le webhook, redirige vers le front. */
    @GetMapping("/pay/{token}")
    public ResponseEntity<Void> pay(@PathVariable String token) {
        Session s = sessions.get(token);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        s.paid()[0] = true;
        String checkSum = md5(token + "1" + properties.getApiKey());
        restClient.post().uri(s.webhookUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("token", token, "payment_status", true,
                        "check_sum", checkSum, "transaction_id", "MOCK-" + token.substring(0, 8)))
                .retrieve().toBodilessEntity();
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, URI.create(s.returnUrl()).toString()).build();
    }

    private static String md5(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("MD5").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
