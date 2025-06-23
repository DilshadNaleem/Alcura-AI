package com.Alcura.Admin.Controller.MedicinePredictionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Admin")
public class MedicineDeleteController
{
    private final String API_BASE_URL = "http://localhost:5000/api/deletePill/";
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MedicineDeleteController.class);

    public MedicineDeleteController(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @DeleteMapping("/DeleteMedicine")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteMedicine(@RequestParam String medicineName)
    {
        String apiUrl = UriComponentsBuilder.fromHttpUrl(API_BASE_URL)
                .pathSegment(medicineName)
                .build()
                .toUriString();

        Map<String,String> response = new HashMap<>();

        try
        {
            logger.info("Attemptng to delete medicine: {}", medicineName);
            logger.debug("Making Delete request to: {}", apiUrl);

            ResponseEntity<Map<String, Object>> apiResponse = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if(apiResponse.getStatusCode().is2xxSuccessful()) {
                response.put("status", "success");
                response.put("message", "Successfully Deleted: " + medicineName);
                logger.info("Successfully deleted disease: {}" , medicineName);
                return ResponseEntity.ok(response);
            }

            else
            {
                Map<String, Object> apiBody = apiResponse.getBody();
                String errorMessage = apiBody != null ?
                        apiBody.getOrDefault("warning", apiBody.getOrDefault("error", "Unknown error")).toString() :
                        "No response Body";

                response.put("status", "error");
                response.put("message", errorMessage);
                logger.warn("Failed to delete disease: {}. Reason: {}", medicineName, errorMessage);
                return ResponseEntity.status(apiResponse.getStatusCode()).body(response);
            }

        }
        catch (Exception e)
        {
            String errorMsg = "Delete failed for " + medicineName + ": "  + e.getMessage();
            response.put("status", "error");
            response.put("message", errorMsg);
            logger.error(errorMsg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
