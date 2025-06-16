package com.Alcura.Admin.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class DiseaseTrainingController {

    private static final String API_URL = "http://localhost:5000/api/train";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/train")
    public String showTrainingPage() {
        return "/Admin/DiseaseTraining";
    }

    @PostMapping("/start-training")
    public String startTraining(Model model) {
        try {
            // Call the Flask API
            Map<String, Object> response = restTemplate.postForObject(API_URL, null, Map.class);

            // Add the response data to the model
            model.addAttribute("trainingResult", response);
            model.addAttribute("success", true);
            model.addAttribute("message", response.get("message"));

            // Add chart data if available
            if (response.containsKey("chart")) {
                model.addAttribute("chartData", response.get("chart"));
            }

        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Training failed: " + e.getMessage());
        }

        return "/Admin/DiseaseTraining";
    }
}