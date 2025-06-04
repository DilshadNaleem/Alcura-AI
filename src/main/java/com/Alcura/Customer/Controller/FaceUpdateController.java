package com.Alcura.Customer.Controller;

import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(value = "*")
@RequestMapping("/Customer")
public class FaceUpdateController {
    private static final Logger logger = LoggerFactory.getLogger(FaceUpdateController.class);
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FaceUpdateController(CustomerRepository customerRepository,
                                RestTemplate restTemplate,
                                ObjectMapper objectMapper) {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/UpdateFace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> updateFace(
            @RequestParam("faceImage") MultipartFile faceImage,
            HttpSession session) {

        Map<String, String> response = new HashMap<>();

        try {
            String email = (String) session.getAttribute("email");

            if (email == null || email.isEmpty()) {
                response.put("redirect", "/Customer/Signing.html");
                response.put("message", "Session Expired! Please Login Again.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (faceImage == null || faceImage.isEmpty()) {
                response.put("redirect", "/Customer/UpdateFace");
                response.put("message", "No image file uploaded");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Processing face update for: {}. File Size: {} bytes", email, faceImage.getSize());

            Path tempFile = Files.createTempFile("face_update", ".jpg");

            try {
                Files.copy(faceImage.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
                logger.debug("Saved temporary file: {}", tempFile.toString());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("image", new FileSystemResource(tempFile.toFile()));

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                logger.debug("Sending request to face recognition service for update");
                ResponseEntity<String> faceServiceResponse = restTemplate.postForEntity(
                        "http://localhost:5000/register_face",
                        requestEntity, String.class
                );

                logger.debug("Received response: {}", faceServiceResponse.getBody());

                JsonNode jsonNode = objectMapper.readTree(faceServiceResponse.getBody());

                if (jsonNode.get("success").asBoolean()) {
                    String faceEncoding = jsonNode.get("encoding").toString();

                    Customer customer = customerRepository.findByEmail(email);

                    if (customer != null) {
                        customer.setFaceData(faceEncoding);
                        customerRepository.save(customer);

                        logger.info("Successfully updated face data for: {}", email);
                        response.put("redirect", "/Customer/Dashboard");
                        response.put("message", "Face updated successfully!");
                        return ResponseEntity.ok(response);
                    } else {
                        logger.info("Customer not found for email: {}", email);
                        response.put("redirect", "/Customer/UpdateFace");
                        response.put("message", "Customer record not found!");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                } else {
                    String errorMsg = jsonNode.has("message")
                            ? jsonNode.get("message").asText()
                            : "Face recognition failed";
                    logger.warn("Face Service Error: {}", errorMsg);
                    response.put("redirect", "/Customer/UpdateFace");
                    response.put("message", errorMsg);
                    return ResponseEntity.badRequest().body(response);
                }
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (Exception e) {
            logger.error("Face update failed", e);
            response.put("redirect", "/Customer/UpdateFace");
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 