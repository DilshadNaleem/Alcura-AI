package com.Alcura.AdminTest.DiseasePredictionModel;

import com.Alcura.Admin.Controller.DiseasePredictionModel.AddDiseaseController;
import com.Alcura.Admin.DTO.DiseaseInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AddDiseaseControllerTest
{
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AddDiseaseController addDiseaseController;

    private DiseaseInfo diseaseInfo;
    private MockMultipartFile[] trainImages;
    private MockMultipartFile[] valImages;


    @BeforeEach
    void setUp()
    {
        diseaseInfo = new DiseaseInfo();
        diseaseInfo.setDisease("Test Disease");
        diseaseInfo.setDescription("Test Description");

        trainImages = new MockMultipartFile[]{
                new MockMultipartFile("trainImage1", "train1.jpg", "image/jpeg", "train image content".getBytes()),
                new MockMultipartFile("trainImage2", "train2.jpg", "image/jpeg", "train image content 2".getBytes())
        };

        valImages = new MockMultipartFile[]{
                new MockMultipartFile("valImage1", "val1.jpg", "image/jpeg", "val image content".getBytes()),
                new MockMultipartFile("valImage2", "val2.jpg", "image/jpeg", "val image content 2".getBytes())
        };
    }


    @Test
    void addDisease_WithAllData_ShouldCallFlaskApiSuccessfully()
    {
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))) .thenReturn(mockResponse);

        ResponseEntity<Map<String,String>> response = addDiseaseController.addDisease(diseaseInfo,trainImages
        ,valImages);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Disease information and images saved successfully!", response.getBody().get("message"));

        verify(restTemplate, times(1)).postForEntity(
                eq("http://localhost:5000/api/disease"),
                any(HttpEntity.class),
                eq(String.class));
    }


    @Test
    void addDisease_WithNoImages_ShouldCallFlaskApiSuccessfully()
    {
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        ResponseEntity<Map<String,String>> response = addDiseaseController.addDisease(diseaseInfo, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));

        verify(restTemplate, times(1)).postForEntity(any(String.class), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void addDisease_WhenFlaskApiReturnsError_ShouldShowErrorResponse()
    {
        when(restTemplate.postForEntity(any(String.class),any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Flask API error"));

        ResponseEntity<Map<String, String>> response = addDiseaseController.addDisease(
                diseaseInfo, trainImages, valImages);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
        assertTrue(response.getBody().get("message").contains("Flask API error"));

        verify(restTemplate, times(1)).postForEntity(any(String.class), any(HttpEntity.class), eq(String.class));
    }
}
