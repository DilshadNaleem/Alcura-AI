package com.Alcura.Admin.Controller.DiseasePredictionModel;

import com.Alcura.Admin.DTO.DiseaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class EditDiseaseNameController {
    private static final Logger logger = LoggerFactory.getLogger(EditDiseaseNameController.class);
    private final String API_URL = "http://localhost:5000/api/disease/";
    private final RestTemplate restTemplate;

    public EditDiseaseNameController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/Edit")
    public String showEditForm(@RequestParam String diseaseName, Model model) {
        String requestUrl = API_URL + diseaseName;
        logger.info("Sending GET request to Flask API - URL: {}", requestUrl);

        try {
            ResponseEntity<DiseaseInfo> response = restTemplate.getForEntity(
                    requestUrl,
                    DiseaseInfo.class);

            logger.info("Received response from Flask API - Status: {}, Body: {}",
                    response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                model.addAttribute("disease", response.getBody());
                model.addAttribute("oldName", diseaseName);
                return "/Admin/DiseasePredictionModel/edit-disease";
            }
        } catch (Exception e) {
            logger.error("Error fetching disease data for editing - URL: {}, Error: {}",
                    requestUrl, e.getMessage(), e);
        }

        String encodedName = diseaseName.replace(" ", "+");
        String redirectUrl = "redirect:/Admin/diseases?error=Disease+"+ encodedName +"+not+found";
        logger.info("Redirecting to: {}", redirectUrl);
        return redirectUrl;
    }

    @PostMapping("/Edit")
    public String updateDisease(
            @ModelAttribute("disease") DiseaseInfo updatedDisease,
            @RequestParam String oldName,
            RedirectAttributes redirectAttributes) {

        String requestUrl = API_URL + oldName;
        logger.info("Sending POST request to Flask API - URL: {}", requestUrl);

        try {
            // Prepare the complete request body expected by Flask
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("new_name", updatedDisease.getDisease());
            requestBody.put("Description", updatedDisease.getDescription());
            requestBody.put("scientific_name", updatedDisease.getScientificName());
            requestBody.put("symptoms", updatedDisease.getSymptoms());
            requestBody.put("cause", updatedDisease.getCause());
            requestBody.put("treatment", updatedDisease.getTreatment());
            requestBody.put("medications", updatedDisease.getMedications());
            requestBody.put("prevention", updatedDisease.getPrevention());
            requestBody.put("is_contagious", updatedDisease.getContagious());
            requestBody.put("severity", updatedDisease.getSeverity());
            requestBody.put("common_age_group", updatedDisease.getCommonAgeGroup());
            requestBody.put("duration", updatedDisease.getDuration());
            requestBody.put("first_aid_advice", updatedDisease.getFirstAidAdvice());
            requestBody.put("risk_factors", updatedDisease.getRiskFactors());
            requestBody.put("side_effects", updatedDisease.getSideEffects());
            requestBody.put("Source_of_information", updatedDisease.getSourceOfInformation());

            logger.info("Request body being sent: {}", requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            logger.info("Received response from Flask API - Status: {}, Body: {}",
                    response.getStatusCode(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success", "Disease updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update disease: " + response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error updating disease - URL: {}, Error: {}",
                    requestUrl, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error updating disease: " + e.getMessage());
        }

        return "redirect:/Admin/diseases";
    }
}