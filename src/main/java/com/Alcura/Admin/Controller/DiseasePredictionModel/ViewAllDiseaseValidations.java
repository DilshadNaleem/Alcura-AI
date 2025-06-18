package com.Alcura.Admin.Controller.DiseasePredictionModel;

import com.Alcura.Admin.Configuration.RestTemplateUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class ViewAllDiseaseValidations {
    private final RestTemplate restTemplate;

    public ViewAllDiseaseValidations() {
        this.restTemplate = RestTemplateUtils.createRestTemplateWithNanSupport();
    }


    @GetMapping("/ViewAllValidations")
    public String getViewAll(Model model) {
        final String API_URL = "http://localhost:5000/api/validation_results";

        try {
            // Initialize empty list first
            List<Map<String, Object>> results = Collections.emptyList();

            // Make API call
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    API_URL,
                    HttpMethod.GET,
                    null,
                    List.class
            );

            // Get response body safely
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                results = responseEntity.getBody();
            }

            // Process results if not empty
            if (!results.isEmpty()) {
                results.forEach(result -> {
                    Double confidence = result.containsKey("confidence") ?
                            ((Number) result.get("confidence")).doubleValue() * 100 : 0.0;
                    result.put("formattedConfidence", String.format("%.2f%%", confidence));
                    result.put("confidence", confidence);

                    if (result.containsKey("image")) {
                        result.put("image",  result.get("image"));
                    }
                });
            }

            // Always add results to model, even if empty
            model.addAttribute("results", results);
            return "Admin/DiseasePredictionModel/ViewAllValidations";

        } catch (Exception e) {
            e.printStackTrace();
            // Add empty list to model even in error case
            model.addAttribute("results", Collections.emptyList());
            model.addAttribute("error", "Error loading validation results: " + e.getMessage());
            return "Admin/DiseasePredictionModel/ViewAllValidations";
        }
    }
}