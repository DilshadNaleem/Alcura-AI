package com.Alcura.Admin.Controller.DiseasePredictionModel;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.File;
import java.util.Map;

@Controller
@MultipartConfig
public class DiseasePredict {
    private static final String API_URL = "http://localhost:5000/api/predict";
    private final RestTemplate restTemplate;

    public DiseasePredict() {
        this.restTemplate = createRestTemplateWithNonNumericNumberSupport();
    }

    private RestTemplate createRestTemplateWithNonNumericNumberSupport() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        restTemplate.getMessageConverters().add(0, converter);

        return restTemplate;
    }

    @PostMapping("/ImagePredict")
    public String predictDisease(@RequestParam("image") MultipartFile multipartFile, Model model) {
        try {
            // Validate file
            if (multipartFile.isEmpty()) {
                model.addAttribute("error", "Please select an image file");
                return "/Admin/DiseasePredictionModel/PredictDisease";
            }

            // Create temp file
            File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);
            tempFile.deleteOnExit();

            // Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(tempFile));

            // Call API
            Map<String, Object> response = restTemplate.postForObject(
                    API_URL,
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            // Handle response
            if (response == null) {
                throw new RuntimeException("Empty response from API");
            }

            if (response.containsKey("error")) {
                throw new RuntimeException(response.get("error").toString());
            }

            // Add basic prediction info
            model.addAttribute("predictedClass", response.getOrDefault("predicted_class", "Unknown"));
            model.addAttribute("confidence", response.getOrDefault("confidence", 0.0));
            model.addAttribute("isRecognized", response.getOrDefault("is_recognized", false));

            // Add disease info if available
            if (response.containsKey("disease_info")) {
                model.addAttribute("disease_info", response.get("disease_info"));
                Map<String, Object> diseaseInfo = (Map<String, Object>) response.get("disease_info");
                model.addAttribute("description", diseaseInfo.get("Description"));
                model.addAttribute("SourceOfInformation", diseaseInfo.get("Source_of_information"));
                model.addAttribute("cause", diseaseInfo.get("cause"));
                model.addAttribute("duration", diseaseInfo.get("duration"));
                model.addAttribute("firstAidAdvice", diseaseInfo.get("first_aid_advice"));
                model.addAttribute("is_contagious", diseaseInfo.get("is_contagious"));
                model.addAttribute("medications", diseaseInfo.get("medications"));
                model.addAttribute("prevention", diseaseInfo.get("prevention"));
                model.addAttribute("risk_factors", diseaseInfo.get("risk_factors"));
                model.addAttribute("scientific_name", diseaseInfo.get("scientific_name"));
                model.addAttribute("severity", diseaseInfo.get("severity"));
                model.addAttribute("side_effects", diseaseInfo.get("side_effects"));
                model.addAttribute("symptoms", diseaseInfo.get("symptoms"));
                model.addAttribute("treatment", diseaseInfo.get("treatment"));
            }

            return "/Admin/DiseasePredictionModel/PredictDisease";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error processing image: " + e.getMessage());
            return "/Admin/DiseasePredictionModel/PredictDisease";
        }
    }
}