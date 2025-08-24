package com.Alcura.Admin.Controller.DiseasePredictionModel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Controller
public class DiseaseTrainingController {

    private static final String API_URL = "http://localhost:5000/api/train";
    private  RestTemplate restTemplate;

    public DiseaseTrainingController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/train")
    public String showTrainingPage() {
        return "/Admin/DiseasePredictionModel/DiseaseTraining";
    }

    @PostMapping("/start-training")
    public String startTraining(
            @RequestParam(name = "epochs", defaultValue = "10") int epochs,
            Model model) {

        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Prepare request parameters
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("epochs", String.valueOf(epochs));

            // Create request entity
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Make POST request to Flask API
            Map<String, Object> response = restTemplate.postForObject(
                    API_URL,
                    request,
                    Map.class
            );

            // Check for error in response
            if (response.containsKey("error")) {
                throw new RuntimeException((String) response.get("error"));
            }

            // Process successful response
            model.addAttribute("trainingResult", response);
            model.addAttribute("success", true);
            model.addAttribute("message", response.get("message"));
            model.addAttribute("epochs", epochs);

            if (response.containsKey("chart")) {
                model.addAttribute("chartData", response.get("chart"));
            }

            if(response.containsKey("history")) {
                Map<String,Object> history = (Map<String, Object>) response.get("history");
                model.addAttribute("accuracy", history.get("accuracy"));
                model.addAttribute("loss", history.get("loss"));
                model.addAttribute("valAccuracy", history.get("val_accuracy"));
                model.addAttribute("valLoss", history.get("val_loss"));
            }

        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Training failed: " + e.getMessage());
            model.addAttribute("epochs", epochs);
            // Log the error for debugging
            e.printStackTrace();
        }

        return "/Admin/DiseasePredictionModel/DiseaseTraining";
    }
}