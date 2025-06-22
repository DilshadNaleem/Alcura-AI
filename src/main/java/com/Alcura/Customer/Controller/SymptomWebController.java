package com.Alcura.Customer.Controller;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Important: For Thymeleaf
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Customer")
public class SymptomWebController {

    private final SymptomAPIController symptomAPIController;

    public SymptomWebController(SymptomAPIController symptomAPIController) {
        this.symptomAPIController = symptomAPIController;
    }

    @PostMapping("/SymptomChecker")
    public String classify(@RequestParam("message") String message, Model model) {
        try {
            // Call the backend Flask classifier through your API controller
            ResponseEntity<String> response = symptomAPIController.classifyMedicine(message);

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse JSON response
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
                Map<String, Object> responseMap = mapper.readValue(response.getBody(), Map.class);

                // Extract the list of matches (each is a disease info block)
                List<Map<String, Object>> matches = (List<Map<String, Object>>) responseMap.get("matches");

                // Pass the matches to the frontend
                model.addAttribute("matches", matches);
                model.addAttribute("originalMessage", message);

                // Forward to Thymeleaf template
                return "/Customer/DiseaseSymptom";

            } else {
                model.addAttribute("error", "No matching disease found or error occurred.");
                return "errorPage";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Internal server error: " + e.getMessage());
            return "errorPage";
        }
    }
}
