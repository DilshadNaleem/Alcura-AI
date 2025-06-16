package com.Alcura.Admin.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/Admin")
public class DeleteDiseaseController {
    private final String API_URL = "http://localhost:5000/api/disease";
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(DeleteDiseaseController.class);

    public DeleteDiseaseController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @DeleteMapping("/DeleteDisease")
    @ResponseBody  // Important for returning JSON
    public ResponseEntity<Map<String, String>> deleteDisease(@RequestParam String diseaseName) {
        String apiEndpoint = API_URL + "/" + URLEncoder.encode(diseaseName, StandardCharsets.UTF_8);
        Map<String, String> response = new HashMap<>();

        try {
            ResponseEntity<Map> apiResponse = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.DELETE,
                    null,
                    Map.class
            );

            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                response.put("status", "success");
                response.put("message", "Deleted: " + diseaseName);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", apiResponse.getBody().toString());
                return ResponseEntity.status(apiResponse.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Delete failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}