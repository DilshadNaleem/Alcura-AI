package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.DiseaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        try {
            // Fetch the disease data
            ResponseEntity<DiseaseInfo> response = restTemplate.getForEntity(
                    API_URL + diseaseName,
                    DiseaseInfo.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                model.addAttribute("disease", response.getBody());
                model.addAttribute("oldName", diseaseName); // Store original name for reference
                return "/Admin/edit-disease";
            }
        } catch (Exception e) {
            logger.error("Error fetching disease data for editing", e);
        }
        return "redirect:/Admin/diseases?error=Disease+not+found";
    }

    @PostMapping("/Edit")
    public String updateDisease(
            @ModelAttribute("disease") DiseaseInfo updatedDisease,
            @RequestParam String oldName,
            RedirectAttributes redirectAttributes) {

        try {
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request entity
            HttpEntity<DiseaseInfo> requestEntity = new HttpEntity<>(updatedDisease, headers);

            // Make PUT request to update
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL + oldName,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success", "Disease updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update disease");
            }
        } catch (Exception e) {
            logger.error("Error updating disease", e);
            redirectAttributes.addFlashAttribute("error", "Error updating disease: " + e.getMessage());
        }

        return "redirect:/Admin/diseases";
    }
}