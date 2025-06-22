package com.Alcura.Admin.Controller.DiseasePredictionModel;

import com.Alcura.Admin.DTO.DiseaseInfo;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.Alcura.Admin.Configuration.RestTemplateUtils.createRestTemplateWithNanSupport;

@Controller
@RequestMapping("/Admin")
public class GetAllDiseaseDescriptionController {

    private static final Logger logger = LoggerFactory.getLogger(GetAllDiseaseDescriptionController.class);
    private final RestTemplate restTemplate;

    public GetAllDiseaseDescriptionController() {
        this.restTemplate = createRestTemplateWithNanSupport();
    }



    @GetMapping("/diseases")
    public String getDiseaseDescription(Model model) {
        final String apiUrl = "http://localhost:5000/api/AllDiseases";

        try {
            // First get raw response for debugging
            ResponseEntity<String> rawResponse = restTemplate.getForEntity(apiUrl, String.class);
            logger.info("Raw API Response: {}", rawResponse.getBody());

            // Then try to parse it
            DiseaseInfo[] diseasesArray = restTemplate.getForObject(apiUrl, DiseaseInfo[].class);

            if (diseasesArray != null && diseasesArray.length > 0) {
                List<DiseaseInfo> diseases = Arrays.asList(diseasesArray);
                model.addAttribute("diseases", diseases);
                logger.info("Successfully parsed {} diseases", diseases.size());

            } else {
                logger.warn("Received empty or null response from API");
                model.addAttribute("error", "No disease data available");
            }
        } catch (Exception e) {
            logger.error("Failed to fetch diseases: {}", e.getMessage());
            logger.error("Stack trace: ", e);
            model.addAttribute("error", "Failed to load disease data. Please try again later.");
        }

        return "/Admin/DiseasePredictionModel/ViewAllDiseases";
    }

    @GetMapping("/diseases/search")
    public String searchDisease (@RequestParam String diseaseName, Model model, HttpSession session)
    {
        final String apiUrl = "http://localhost:5000/api/disease/" + diseaseName;
        session.setAttribute("diseaseName", diseaseName);


        try
        {
            DiseaseInfo diseaseInfo = restTemplate.getForObject(apiUrl, DiseaseInfo.class);

            if (diseaseInfo != null)
            {
                model.addAttribute("diseases", Collections.singleton(diseaseInfo));
            } else
            {
                model.addAttribute("error", "Disease not found!");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Failed to fetch diseae {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "/Admin/DiseasePredictionModel/ViewAllDiseases";
    }
}