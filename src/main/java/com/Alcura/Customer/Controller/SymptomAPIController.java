package com.Alcura.Customer.Controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/diseaseClassifier")
public class SymptomAPIController {

    private final String FLASK_API_URL = "http://localhost:5000/analyze";

    @PostMapping("/classify")
    public ResponseEntity<String> classifyMedicine(@RequestParam("message") String message) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Construct JSON body with key "symptoms" matching Flask API expectation
            Map<String, Object> body = new HashMap<>();
            body.put("symptoms", message);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // POST request to Flask API
            ResponseEntity<String> response = restTemplate.postForEntity(
                    FLASK_API_URL,
                    requestEntity,
                    String.class
            );

            System.out.println("Response from flask: " + response.getBody());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"status\":\"error\",\"message\":\"Failed to process request: " + e.getMessage() + "\"}");
        }
    }
}
