package com.Alcura.AdminTest.MedicinePredictionModel;

import com.Alcura.Admin.Controller.MedicinePredictionModel.MedicineDeleteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteMedicineControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MedicineDeleteController medicineDeleteController;

    private String medicineName;
    private String apiUrl;
    private final String API_BASE_URL = "http://localhost:5000/api/deletePill/";

    @BeforeEach
    void setUp() {
        medicineName = "Aspirin";
        apiUrl = API_BASE_URL + medicineName;
    }

    @Test
    void testDeleteMedicine_Success() {

        Map<String, Object> apiResponseBody = new HashMap<>();
        apiResponseBody.put("message", "Successfully Deleted: " + medicineName);

        ResponseEntity<Map<String, Object>> apiResponse = new ResponseEntity<>(apiResponseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.DELETE),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(apiResponse);

        ResponseEntity<Map<String, String>> response = medicineDeleteController.deleteMedicine(medicineName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Successfully Deleted: " + medicineName, response.getBody().get("message"));
    }

    @Test
    void testDeleteMedicine_Failure() {

        Map<String, Object> apiResponseBody = new HashMap<>();
        apiResponseBody.put("error", "Medicine not found");

        ResponseEntity<Map<String, Object>> apiResponse = new ResponseEntity<>(apiResponseBody, HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(
                eq(apiUrl),
                eq(HttpMethod.DELETE),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(apiResponse);

        ResponseEntity<Map<String, String>> response = medicineDeleteController.deleteMedicine(medicineName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Medicine not found", response.getBody().get("message"));
    }
}