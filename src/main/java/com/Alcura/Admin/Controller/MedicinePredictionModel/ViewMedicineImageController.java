package com.Alcura.Admin.Controller.MedicinePredictionModel;

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
public class ViewMedicineImageController {
    private final RestTemplate restTemplate;
    private final String API_URL = "http://localhost:5000/api/images/";

    public ViewMedicineImageController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/ViewMedicineImages")
    public String viewImages(@RequestParam("medicineName") String medicineName,
                             @RequestParam("ImageType") String imageType,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            String encodedMedicineName = URLEncoder.encode(medicineName, StandardCharsets.UTF_8.toString());
            Map<String, Object> response = restTemplate.getForObject(
                    API_URL + encodedMedicineName + "/" + imageType,
                    Map.class
            );

            if (response != null) {
                // Handle folder variants
                if (response.containsKey("folder_variants_checked")) {
                    List<String> variants = (List<String>) response.get("folder_variants_checked");
                    model.addAttribute("dataname", variants != null && !variants.isEmpty() ? variants : Collections.emptyList());
                } else {
                    model.addAttribute("dataname", Collections.emptyList());
                }

                // Handle images
                if (response.containsKey("images")) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<Map<String, Object>> imageList = mapper.convertValue(
                            response.get("images"),
                            new TypeReference<List<Map<String, Object>>>() {}
                    );

                    if (!imageList.isEmpty()) {
                        Map<String, Object> firstImage = imageList.get(0);
                        model.addAttribute("data", imageList);
                        model.addAttribute("dataset", firstImage.get("dataset"));
                        model.addAttribute("mime_type", firstImage.get("mime_type"));
                        model.addAttribute("name", firstImage.get("name"));
                        System.out.println("Fetched " + imageList.size() + " images for " + medicineName + "/" + imageType);
                    } else {
                        model.addAttribute("data", Collections.emptyList());
                    }
                } else {
                    model.addAttribute("data", Collections.emptyList());
                }
            } else {
                model.addAttribute("dataname", Collections.emptyList());
                model.addAttribute("data", Collections.emptyList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "ERROR: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to fetch images: " + e.getMessage());
            return "redirect:/Admin/medicineDescription";
        }
        return "/Admin/MedicinePredictionModel/ViewMedicineImages";
    }
}