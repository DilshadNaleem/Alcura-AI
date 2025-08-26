package com.Alcura.AdminTest.DiseasePredictionModel;

import com.Alcura.Admin.Controller.DiseasePredictionModel.DeleteDiseaseController;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteDiseaseControllerTest
{
    private String API_URL = "http://localhost:5000/api/disease";
    private String diseaseName = "Disease Name";
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DeleteDiseaseController deleteDiseaseController;

    @Test
    void DeleteDiseaseController_Success_ShouldShowSuccessMessage() throws IOException
    {
        Map<String,Object> apiResponseMap = new HashMap<>();
        apiResponseMap.put("message","Successfully deleted");
        ResponseEntity<Map<String,Object>> mockApiResponse = ResponseEntity.ok(apiResponseMap);

        when(restTemplate.exchange(
                any(String.class),
                any(),
                any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(mockApiResponse);

        ResponseEntity<Map<String,String>> response = deleteDiseaseController.deleteDisease(diseaseName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Successfully deleted: Disease Name", response.getBody().get("message"));
    }


    @Test
    void deleteDisease_Failure_ShouldReturnErrorMessage()
    {
        String diseaseName = "NonExistingDisease";

        Map<String,Object> apiResponseMap = new HashMap<>();
        apiResponseMap.put("error", "Disease not found");
        ResponseEntity<Map<String,Object>> mockapiResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseMap);

        when(restTemplate.exchange(
                any(String.class),
                any(),
                any(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(mockapiResponse);

        ResponseEntity<Map<String,String>> response = deleteDiseaseController.deleteDisease(diseaseName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Disease not found", response.getBody().get("message"));
    }
}
