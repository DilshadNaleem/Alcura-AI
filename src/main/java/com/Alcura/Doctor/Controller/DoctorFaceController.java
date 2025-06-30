package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
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
public class DoctorFaceController
{
    private static final Logger logger = LoggerFactory.getLogger(DoctorFaceController.class);
    private final DoctorRepository doctorRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DoctorFaceController(DoctorRepository doctorRepository,
                                RestTemplate restTemplate,
                                ObjectMapper objectMapper)
    {
        this.doctorRepository = doctorRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/Doctor/EnrollFace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> enrollface(@RequestParam("faceImage")MultipartFile faceImage,
                                                         HttpSession session)
    {
        Map<String,Object> response = new HashMap<>();

        try
        {
            String email = (String) session.getAttribute("email");
            if (email == null)
            {
                response.put("status","error");
                response.put("message", "Session expired! Please Login again");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (faceImage.isEmpty() || faceImage == null)
            {
                response.put("status", "error");
                response.put("message", "No Image file uploaded");
                return ResponseEntity.badRequest().body(response);
            }

            Path tempFile = Files.createTempFile("face_",".jpg");

            try
            {
                Files.copy(faceImage.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
                body.add("image", new FileSystemResource(tempFile.toFile()));

                HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(body, headers);

                ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                        "http://localhost:5000/register_face",
                        requestEntity,
                        String.class
                );

                JsonNode jsonResponse = objectMapper.readTree(responseEntity.getBody());
                if (jsonResponse.get("success").asBoolean())
                {
                    String faceEncoding = jsonResponse.get("encoding").toString();

                    Doctor doctor = doctorRepository.findByemail(email);

                    if (doctor != null)
                    {
                        doctor.setFaceData(faceEncoding);
                        doctorRepository.save(doctor);

                        response.put("status", "success");
                        response.put("message", "Face Enrolled Successfully!");
                        return ResponseEntity.ok(response);
                    }
                    else {
                        response.put("status","error");
                        response.put("message", "Customer record not found!");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                }
                else
                {
                    String errorMsg = jsonResponse.has("message")
                            ? jsonResponse.get("message").asText()
                            : "Face recoginition Failed";
                    response.put("status", "error");
                    response.put("message",errorMsg);
                    return ResponseEntity.badRequest().body(response);
                }
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }
        catch (Exception e)
        {
            response.put("status","error");
            response.put("message","Internal Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
