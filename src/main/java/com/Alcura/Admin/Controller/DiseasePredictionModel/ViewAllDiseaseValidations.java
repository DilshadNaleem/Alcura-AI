package com.Alcura.Admin.Controller.DiseasePredictionModel;

import com.Alcura.Admin.Configuration.RestTemplateUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    public String getViewAll(
            @RequestParam(name = "class", required = false) String className,
            @RequestParam(name = "is_correct", required = false) String isCorrect,
            Model model) {

        final String BASE_API_URL = "http://localhost:5000/api/validation_results";
        String apiUrl = BASE_API_URL;

        try {
            // Build URL based on search parameters
            if ((className != null && !className.isEmpty()) ||
                    (isCorrect != null && !isCorrect.isEmpty())) {
                apiUrl = BASE_API_URL + "/search";
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);

                if (className != null && !className.isEmpty()) {
                    builder.queryParam("class", className);
                }
                if (isCorrect != null && !isCorrect.isEmpty()) {
                    builder.queryParam("is_correct", isCorrect);
                }

                apiUrl = builder.toUriString();
            }

            // Make API call
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    List.class
            );

            // Get response body safely
            List<Map<String, Object>> results = Collections.emptyList();
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
                        result.put("image", result.get("image"));
                    }
                });
            }

            // Add attributes to model
            model.addAttribute("results", results);
            model.addAttribute("searchClass", className);
            model.addAttribute("searchCorrect", isCorrect);

            return "Admin/DiseasePredictionModel/ViewAllValidations";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("results", Collections.emptyList());
            model.addAttribute("error", "Error loading validation results: " + e.getMessage());
            return "Admin/DiseasePredictionModel/ViewAllValidations";
        }
    }
}