package com.Alcura.AdminTest.DiseasePredictionModel;

import com.Alcura.Admin.Controller.DiseasePredictionModel.DiseaseTrainingController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiseaseTrainingControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Model model;

    @InjectMocks
    private DiseaseTrainingController diseaseTrainingController;

    @Test
    void startTraining_SuccessfulTraining_AddsSuccessAttributesToModel() {

        int epochs = 10;
        Map<String, Object> mockHistory = new HashMap<>();
        mockHistory.put("accuracy", new double[]{0.8, 0.9});
        mockHistory.put("loss", new double[]{0.2, 0.1});
        mockHistory.put("val_accuracy", new double[]{0.7, 0.85});
        mockHistory.put("val_loss", new double[]{0.3, 0.15});

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("message", "Training started successfully");
        mockResponse.put("chart", "chart-data-string");
        mockResponse.put("history", mockHistory);
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(mockResponse);

        String viewName = diseaseTrainingController.startTraining(epochs, model);

        assertEquals("/Admin/DiseasePredictionModel/DiseaseTraining", viewName);
        verify(model).addAttribute("success", true);
        verify(model).addAttribute("message", "Training started successfully");
        verify(model).addAttribute("epochs", epochs);
        verify(model).addAttribute("trainingResult", mockResponse);
        verify(model).addAttribute("chartData", "chart-data-string");
        verify(model).addAttribute("accuracy", mockHistory.get("accuracy"));
        verify(model).addAttribute("loss", mockHistory.get("loss"));
        verify(model).addAttribute("valAccuracy", mockHistory.get("val_accuracy"));
        verify(model).addAttribute("valLoss", mockHistory.get("val_loss"));
    }

    @Test
    void startTraining_APIThrowsException_AddsFailureAttributesToModel() {
        int epochs = 10;
        String errorMessage = "Connection refused";

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException(errorMessage));

        String viewName = diseaseTrainingController.startTraining(epochs, model);

        assertEquals("/Admin/DiseasePredictionModel/DiseaseTraining", viewName);
        verify(model).addAttribute("success", false);
        verify(model).addAttribute(eq("message"), contains("Training failed: " + errorMessage));
        verify(model).addAttribute("epochs", epochs);
    }

    @Test
    void startTraining_ResponseContainsError_AddsFailureAttributesToModel() {

        int epochs = 10;
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("error", "Training arguments invalid");

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(mockResponse);

        String viewName = diseaseTrainingController.startTraining(epochs, model);

        assertEquals("/Admin/DiseasePredictionModel/DiseaseTraining", viewName);
        verify(model).addAttribute("success", false);
        verify(model).addAttribute(eq("message"), contains("Training failed: Training arguments invalid"));
        verify(model).addAttribute("epochs", epochs);
    }

}