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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AdminFaceController
{
    private static final Logger logger = LoggerFactory.getLogger(AdminFaceController.class);
    private final AdminRepository adminRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private AdminFaceController(AdminRepository adminRepository,
                                RestTemplate restTemplate,
                                ObjectMapper objectMapper) {
        this.adminRepository = adminRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/Admin/EnrollFace",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> enrollFace(@RequestParam("faceImage") MultipartFile faceImage, HttpSession session)
    {
        Map<String,Object> response = new HashMap<>();
        try
        {
            logger.info("Starting face enrollment process");
            String email = (String) session.getAttribute("email");
            logger.info("Session email: {}", email);

            if (email == null)
            {
                logger.warn("No email in session - session expired");
                response.put("status", "error");
                response.put("message", "Session Expired! Please login again");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if(faceImage == null || faceImage.isEmpty())
            {
                logger.warn("No image file uploaded");
                response.put("status", "error");
                response.put("message", "No image file uploaded");
                return ResponseEntity.badRequest().body(response);
            }

            Path tempfile = Files.createTempFile("face_",".jpg");
            logger.info("Created temp file: {}", tempfile);

            try
            {
                Files.copy(faceImage.getInputStream(), tempfile, StandardCopyOption.REPLACE_EXISTING);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
                body.add("image", new FileSystemResource(tempfile.toFile()));

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body,headers);

                logger.info("Sending request to face recognition service");
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                        "http://localhost:5000/register_face",
                        requestEntity,
                        String.class
                );

                logger.info("Received response from face recognition service: {}", responseEntity.getBody());
                JsonNode jsonResponse = objectMapper.readTree(responseEntity.getBody());

                if (jsonResponse.get("success").asBoolean())
                {
                    String faceEncoding = jsonResponse.get("encoding").toString();
                    logger.info("Face encoding received, length: {}", faceEncoding.length());

                    Admin admin = adminRepository.findByEmail(email);
                    if (admin != null)
                    {
                        logger.info("Found admin record for email: {}", email);
                        admin.setFaceData(faceEncoding);
                        Admin savedAdmin = adminRepository.save(admin);
                        logger.info("Admin record saved with face data. ID: {}", savedAdmin.getId());

                        response.put("status","success");
                        response.put("message","Face Enrolled Successfully");
                        return ResponseEntity.ok(response);
                    }
                    else
                    {
                        logger.error("No admin record found for email: {}", email);
                        response.put("status","error");
                        response.put("message", "Admin record not found!");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                }
                else
                {
                    String errorMsg = jsonResponse.has("message")
                            ? jsonResponse.get("message").asText()
                            : "Face recognition Failed";
                    logger.error("Face recognition failed: {}", errorMsg);
                    response.put("status", "error");
                    response.put("message",errorMsg);
                    return ResponseEntity.badRequest().body(response);
                }
            } finally {
                Files.deleteIfExists(tempfile);
                logger.info("Temp file deleted");
            }
        }
        catch (Exception e)
        {
            logger.error("Error in face enrollment: ", e);
            response.put("status","error");
            response.put("message","Internal Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
