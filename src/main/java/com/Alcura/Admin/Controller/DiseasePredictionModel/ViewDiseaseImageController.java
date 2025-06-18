package com.Alcura.Admin.Controller.DiseasePredictionModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class ViewDiseaseImageController {
    private final RestTemplate restTemplate;
    private final String API_URL = "http://localhost:5000/api/images/";

    public ViewDiseaseImageController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/ViewImages")
    public String viewImages(@RequestParam("diseaseName") String diseaseName,
                             @RequestParam("imageType") String imageType,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            // Get API response
            String encodedDiseaseName = URLEncoder.encode(diseaseName, StandardCharsets.UTF_8.toString());
            Map<String, Object> response = restTemplate.getForObject(
                    API_URL + encodedDiseaseName + "/" + imageType,
                    Map.class
            );

            if (response != null && response.containsKey("folder_variants_checked")) {
                List<String> variants = (List<String>) response.get("folder_variants_checked");
                if (variants != null && !variants.isEmpty()) {
                    model.addAttribute("dataname", variants);
                } else {
                    model.addAttribute("dataname", Collections.emptyList());
                }
            } else {
                model.addAttribute("dataname", Collections.emptyList());
            }

            if (response != null && response.containsKey("images")) {
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> imagesList = mapper.convertValue(
                        response.get("images"),
                        new TypeReference<List<Map<String, Object>>>() {}
                );

                if (!imagesList.isEmpty()) {
                    Map<String, Object> firstImage = imagesList.get(0);
                    model.addAttribute("data", imagesList);
                    model.addAttribute("dataset", firstImage.get("dataset"));
                    model.addAttribute("mime_type", firstImage.get("mime_type"));
                    model.addAttribute("name", firstImage.get("name"));

                    System.out.println("Fetched " + imagesList.size() + " images for " + diseaseName + " / " + imageType);

                } else {
                    model.addAttribute("data", null);
                }
            } else {
                model.addAttribute("data", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error: " + e.getMessage());
        }

        return "/Admin/DiseasePredictionModel/ViewImages";
    }

}