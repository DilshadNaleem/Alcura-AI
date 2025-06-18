package com.Alcura.Admin.Controller.DiseasePredictionModel;

import com.Alcura.Admin.DTO.DiseaseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class AddDiseaseController {
    private static final Logger logger = LoggerFactory.getLogger(AddDiseaseController.class);
    private final String apiUrl = "http://localhost:5000/api/disease";
    private final RestTemplate restTemplate;

    public AddDiseaseController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/diseaseForm")
    public String showForm(Model model) {
        model.addAttribute("diseaseInfo", new DiseaseInfo());
        return "disease-form";
    }

    @PostMapping(value = "/addDisease", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> addDisease(
            @ModelAttribute DiseaseInfo diseaseInfo,
            @RequestParam(value = "trainImages", required = false) MultipartFile[] trainImages,
            @RequestParam(value = "valImages", required = false) MultipartFile[] valImages) {

        Map<String, String> response = new HashMap<>();

        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Prepare multipart request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("diseaseData", diseaseInfo);

            if (trainImages != null) {
                for (MultipartFile file : trainImages) {
                    if (!file.isEmpty()) {
                        body.add("trainImages", new MultipartInputStreamFileResource(
                                file.getInputStream(),
                                file.getOriginalFilename()
                        ));
                    }
                }
            }

            if (valImages != null) {
                for (MultipartFile file : valImages) {
                    if (!file.isEmpty()) {
                        body.add("valImages", new MultipartInputStreamFileResource(
                                file.getInputStream(),
                                file.getOriginalFilename()
                        ));
                    }
                }
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Call Flask API
            ResponseEntity<String> flaskResponse = restTemplate.postForEntity(
                    apiUrl,
                    requestEntity,
                    String.class
            );

            response.put("status", "success");
            response.put("message", "Disease information and images saved successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing disease", e);
            response.put("status", "error");
            response.put("message", "Error saving disease: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper class for file resource
    private static class MultipartInputStreamFileResource extends org.springframework.core.io.InputStreamResource {
        private final String filename;

        public MultipartInputStreamFileResource(java.io.InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // we don't know the length
        }
    }
}