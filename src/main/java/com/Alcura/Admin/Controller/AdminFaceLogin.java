package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AdminFaceLogin
{
    private static final Logger logger = LoggerFactory.getLogger(AdminFaceLogin.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AdminRepository adminRepository;



    public AdminFaceLogin(RestTemplate restTemplate,
                          ObjectMapper objectMapper,
                          AdminRepository adminRepository)
    {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.adminRepository = adminRepository;
    }

    @PostMapping(value = "Admin-face-login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> facelogin(
            @RequestParam("email") String email,
            @RequestParam("faceImage") MultipartFile faceImage,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            session.setAttribute("email", email);

            if (email == null || email.isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (faceImage == null || faceImage.isEmpty()) {
                response.put("success", false);
                response.put("message", "No face image provided");
                return ResponseEntity.badRequest().body(response);
            }

            Admin admin = adminRepository.findByEmail(email);
            if (admin == null || admin.getFaceData() == null) {
                response.put("success", false);
                response.put("message", "No face data registered for this email");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Path tempFile = Files.createTempFile("face_verify_", ".jpg");
            try {
                Files.copy(faceImage.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("image", new FileSystemResource(tempFile.toFile()));
                body.add("stored_encoding", admin.getFaceData());

                HttpEntity<MultiValueMap<String, Object>> requestEntity =
                        new HttpEntity<>(body, headers);

                ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                        "http://localhost:5000/verify_face",
                        requestEntity,
                        String.class
                );

                JsonNode jsonResponse = objectMapper.readTree(responseEntity.getBody());
                if (jsonResponse.get("success").asBoolean()) {
                    boolean match = jsonResponse.get("match").asBoolean();
                    if (match) {
                        response.put("success", true);
                        response.put("message", "Face verification Successful!");
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("success", false);
                        response.put("message", "Face does not match!");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                    }
                } else {
                    String message = jsonResponse.has("message")
                            ? jsonResponse.get("message").asText()
                            : "Face Verification failed!";

                    if (message.toLowerCase().contains("eyes")) {
                        response.put("success", false);
                        response.put("message", "Eyes not detected.Please open your eyes!");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }

                    response.put("success", false);
                    response.put("message", message);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } finally {
                Files.deleteIfExists(tempFile);
            }

        } catch (Exception e)
        {
            logger.error("Face Login Error", e);
            response.put("success", false);
            response.put("message","Server Error!. Please try Again Later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }

    }
}
