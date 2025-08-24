package com.Alcura.AdminTest.MedicinePredictionModel;

import com.Alcura.Admin.Controller.MedicinePredictionModel.AddMedicineController;
import com.Alcura.Admin.DTO.MedicineInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddMedicineControllerTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AddMedicineController addMedicineController;

    private MedicineInfo medicineInfo;
    private MultipartFile[] trainImages;
    private MultipartFile[] valImages;

    @BeforeEach
    public void setUp() throws Exception {
        medicineInfo = new MedicineInfo();
        medicineInfo.setClassName("Aspirin");

        // Create mock files with proper input stream
        trainImages = new MultipartFile[]{mock(MultipartFile.class)};
        when(trainImages[0].isEmpty()).thenReturn(false);
        when(trainImages[0].getOriginalFilename()).thenReturn("train_image1.jpg");
        when(trainImages[0].getInputStream()).thenReturn(new ByteArrayInputStream("test image content".getBytes()));

        valImages = new MultipartFile[]{mock(MultipartFile.class)};
        when(valImages[0].isEmpty()).thenReturn(false);
        when(valImages[0].getOriginalFilename()).thenReturn("val_image1.jpg");
        when(valImages[0].getInputStream()).thenReturn(new ByteArrayInputStream("test image content".getBytes()));

        when(objectMapper.writeValueAsString(any(MedicineInfo.class))).thenReturn("{\"className\":\"Aspirin\"}");
    }

    @Test
    void addPill_success() throws IOException {
        // Mock the successful response from the Flask API
        when(restTemplate.postForEntity(
                any(String.class),
                any(),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>("{\"message\":\"success\"}", HttpStatus.OK));

        // Call the method under test
        ResponseEntity<Map<String, String>> responseEntity = addMedicineController.addPill(medicineInfo, trainImages, valImages);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, String> responseBody = responseEntity.getBody();
        assertEquals("success", responseBody.get("status"));
        assertEquals("Medicine information & images saved successfully!", responseBody.get("message"));
    }

    @Test
    void addPill_unsuccessful() throws IOException {
        // Mock an exception to simulate an unsuccessful API call
        when(restTemplate.postForEntity(
                any(String.class),
                any(),
                eq(String.class))
        ).thenThrow(new RuntimeException("API connection failed"));

        // Call the method under test
        ResponseEntity<Map<String, String>> responseEntity = addMedicineController.addPill(medicineInfo, trainImages, valImages);

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Map<String, String> responseBody = responseEntity.getBody();
        assertEquals("error", responseBody.get("status"));
        assertEquals("Error saving medicine: API connection failed", responseBody.get("message"));
    }
}