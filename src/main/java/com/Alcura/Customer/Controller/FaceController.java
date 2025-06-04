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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleInfoNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class FaceController
{
    private static final Logger logger = LoggerFactory.getLogger(FaceController.class);
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private FaceController(CustomerRepository customerRepository,
                           RestTemplate restTemplate,
                           ObjectMapper objectMapper)
    {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/Customer/EnrollFace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> enrollface (
            @RequestParam("faceImage")MultipartFile faceImage,
            HttpSession session)
    {
        Map<String,Object> response = new HashMap<>();

        try
        {
            String email = (String) session.getAttribute("email");
            if (email == null)
            {
                response.put("status", "error");
                response.put("message", "Session Expired! Please login again");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if(faceImage == null || faceImage.isEmpty())
            {
                response.put("status", "error");
                response.put("message", "No image file uploaded");
                return ResponseEntity.badRequest().body(response);
            }

            Path tempfile = Files.createTempFile("face_",".jpg");

            try
            {
                Files.copy(faceImage.getInputStream(), tempfile, StandardCopyOption.REPLACE_EXISTING);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
                body.add("image", new FileSystemResource(tempfile.toFile()));

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body,headers);

                ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                        "http://localhost:5000/register_face",
                        requestEntity,
                        String.class
                );

                JsonNode jsonResponse = objectMapper.readTree(responseEntity.getBody());

                if (jsonResponse.get("success").asBoolean())
                {
                    String faceEncoding = jsonResponse.get("encoding").toString();

                    Customer customer = customerRepository.findByEmail(email);
                    if (customer != null)
                    {
                        customer.setFaceData(faceEncoding);
                        customerRepository.save(customer);

                        response.put("status","success");
                        response.put("message","Face Enrolled Successfully");
                        return ResponseEntity.ok(response);
                    }
                    else
                    {
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
                Files.deleteIfExists(tempfile);
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
