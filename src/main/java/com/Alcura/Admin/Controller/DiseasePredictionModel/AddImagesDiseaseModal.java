package com.Alcura.Admin.Controller.DiseasePredictionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class AddImagesDiseaseModal {

    private static final Logger logger = LoggerFactory.getLogger(AddImagesDiseaseModal.class);
    private static final String FLASK_API_URL = "http://localhost:5000/api/images/";

    @GetMapping("/DiseaseAddImages")
    public String addImageForm(@RequestParam String diseaseName, Model model) {
        logger.info("Displaying image upload form for disease: {}", diseaseName);
        model.addAttribute("diseaseName", diseaseName);
        return "/Admin/DiseasePredictionModel/AddImages";
    }

    @PostMapping("/UploadImages")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadImages(
            @RequestParam String diseaseName,
            @RequestParam String imageType,
            @RequestParam("images") MultipartFile[] files) {

        Map<String, Object> response = new HashMap<>();

        if (files.length == 0) {
            response.put("success", false);
            response.put("error", "Please select at least one image to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("dataset_type", imageType);

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    };
                    body.add("images", resource);
                }
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    FLASK_API_URL + diseaseName,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                response.put("success", true);
                response.put("message", "Images uploaded successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to upload images");
                return ResponseEntity.status(responseEntity.getStatusCode()).body(response);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while uploading images for disease: {}", diseaseName, e);
            response.put("success", false);
            response.put("error", "Failed to upload images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}