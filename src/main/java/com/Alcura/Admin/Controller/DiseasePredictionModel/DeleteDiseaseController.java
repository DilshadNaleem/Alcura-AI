package com.Alcura.Admin.Controller.DiseasePredictionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class DeleteDiseaseController {
    private final String API_BASE_URL = "http://localhost:5000/api/disease";
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(DeleteDiseaseController.class);

    public DeleteDiseaseController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @DeleteMapping("/DeleteDisease")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteDisease(@RequestParam String diseaseName) {
        // Properly encode the disease name and build the URL
        String apiUrl = UriComponentsBuilder.fromHttpUrl(API_BASE_URL)
                .pathSegment(diseaseName)  // Automatically encodes each path segment
                .build()
                .toUriString();

        Map<String, String> response = new HashMap<>();

        try {
            logger.info("Attempting to delete disease: {}", diseaseName);
            logger.debug("Making DELETE request to: {}", apiUrl);

            ResponseEntity<Map<String, Object>> apiResponse = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                response.put("status", "success");
                response.put("message", "Successfully deleted: " + diseaseName);
                logger.info("Successfully deleted disease: {}", diseaseName);
                return ResponseEntity.ok(response);
            } else {
                // Handle non-2xx responses from the API
                Map<String, Object> apiBody = apiResponse.getBody();
                String errorMessage = apiBody != null ?
                        apiBody.getOrDefault("warning", apiBody.getOrDefault("error", "Unknown error")).toString() :
                        "No response body";

                response.put("status", "error");
                response.put("message", errorMessage);
                logger.warn("Failed to delete disease: {}. Reason: {}", diseaseName, errorMessage);
                return ResponseEntity.status(apiResponse.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            String errorMsg = "Delete failed for " + diseaseName + ": " + e.getMessage();
            response.put("status", "error");
            response.put("message", errorMsg);
            logger.error(errorMsg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}