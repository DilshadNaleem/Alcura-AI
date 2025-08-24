package com.Alcura.AdminTest.MedicinePredictionModel;

import com.Alcura.Admin.Controller.MedicinePredictionModel.MedicineTrainingController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicineTrainingControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Model model;

    @InjectMocks
    private MedicineTrainingController medicineTrainingController;

    private final String API_URL = "http://localhost:5000/api/Medicinetrain";

    @Test
    void testStartTraining_Success() {
        // Arrange
        int epochs = 10;
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("message", "Training started successfully");
        mockResponse.put("chart", "chart_data_here");
        Map<String, Object> history = new HashMap<>();
        history.put("accuracy", "0.95");
        history.put("loss", "0.10");
        history.put("val_accuracy", "0.92");
        history.put("val_loss", "0.15");
        mockResponse.put("history", history);

        // Mock the RestTemplate to return a successful response
        when(restTemplate.postForObject(
                eq(API_URL),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        // Act
        String viewName = medicineTrainingController.startTraining(epochs, model);

        // Assert
        assertEquals("/Admin/MedicinePredictionModel/MedicineTraining", viewName);
        verify(model).addAttribute("trainingResult", mockResponse); // Verify that the entire response object is added
        verify(model).addAttribute("success", true);
        verify(model).addAttribute("message", "Training started successfully");
        verify(model).addAttribute("epochs", epochs);
        verify(model).addAttribute("chartData", "chart_data_here");
        verify(model).addAttribute("accuracy", "0.95");
        verify(model).addAttribute("loss", "0.10");
        verify(model).addAttribute("valAccuracy", "0.92");
        verify(model).addAttribute("valLoss", "0.15");
    }

    @Test
    void testStartTraining_Failure() {
        // Arrange
        int epochs = 10;
        String errorMessage = "Failed to connect to API";

        when(restTemplate.postForObject(
                eq(API_URL),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException(errorMessage));

        // Act
        String viewName = medicineTrainingController.startTraining(epochs, model);

        // Assert
        assertEquals("/Admin/MedicinePredictionModel/MedicineTraining", viewName);
        verify(model).addAttribute("success", false);
        verify(model).addAttribute("message", "Training failed: " + errorMessage);
        verify(model).addAttribute("epochs", epochs);
    }
}