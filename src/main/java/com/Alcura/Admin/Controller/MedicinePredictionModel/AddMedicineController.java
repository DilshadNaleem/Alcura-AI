package com.Alcura.Admin.Controller.MedicinePredictionModel;

import com.Alcura.Admin.DTO.MedicineInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class AddMedicineController {
    private static final Logger logger = LoggerFactory.getLogger(AddMedicineController.class);
    private final String apiUrl = "http://localhost:5000/api/AddPill";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AddMedicineController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/AddMedicine")
    public String showForm(Model model) {
        model.addAttribute("medicineInfo", new MedicineInfo());
        return "/Admin/MedicinePredictionModel/Add_Medicine";
    }

    @PostMapping(value = "/AddMedicine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String,String>> addPill(
            @ModelAttribute MedicineInfo medicineInfo,
            @RequestParam(value = "trainImages", required = false) MultipartFile[] trainImages,
            @RequestParam(value = "valImages", required = false) MultipartFile[] valImages) {

        Map<String, String> response = new HashMap<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();

            // Serialize medicineInfo to JSON
            String medicineJson = objectMapper.writeValueAsString(medicineInfo);
            body.add("medicineData", medicineJson);

            // Handle file uploads
            if (trainImages != null) {
                for (MultipartFile file : trainImages) {
                    if(!file.isEmpty()) {
                        body.add("trainImages", new MultipartInputStreamFileResource(
                                file.getInputStream(),
                                file.getOriginalFilename()
                        ));
                    }
                }
            }

            if (valImages != null) {
                for(MultipartFile file : valImages) {
                    if(!file.isEmpty()) {
                        body.add("valImages", new MultipartInputStreamFileResource(
                                file.getInputStream(),
                                file.getOriginalFilename()
                        ));
                    }
                }
            }

            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> flaskResponse = restTemplate.postForEntity(
                    apiUrl,
                    requestEntity,
                    String.class
            );

            response.put("status", "success");
            response.put("message", "Medicine information & images saved successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing medicine", e);
            response.put("status", "error");
            response.put("message", "Error saving medicine: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

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
            return -1;
        }
    }
}