package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.DiseaseListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.*;

@Controller
@RequestMapping("/Admin")
public class ViewAllDiseaseController {

    private static final Logger logger = LoggerFactory.getLogger(ViewAllDiseaseController.class);
    private final RestTemplate restTemplate;

    public ViewAllDiseaseController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/ViewAllDisease")
    public String getDiseasesView2(Model model) {
        final String apiUrl = "http://localhost:5000/api/diseases";
        try {
            ResponseEntity<DiseaseListResponse> response = restTemplate.getForEntity(apiUrl, DiseaseListResponse.class);
            if (response.getBody() != null && response.getBody().getDiseases() != null) {
                model.addAttribute("diseases", response.getBody().getDiseases());
            } else {
                model.addAttribute("error", "No diseases data available");
            }
        } catch (RestClientException e) {
            logger.error("Error fetching diseases", e);
            model.addAttribute("error", "Failed to fetch diseases: " + e.getMessage());
        }
        return "Admin/Disease_List";
    }

}