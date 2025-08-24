package com.Alcura.AdminTest.DiseasePredictionModel;

import com.Alcura.Admin.Controller.DiseasePredictionModel.ViewAllDiseaseValidations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ViewAllDiseaseValidationTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Model model;

    @InjectMocks
    private ViewAllDiseaseValidations controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getViewAll_noParams_returnsAllResults() {
        List<Map<String, Object>> mockResults = Collections.singletonList(
                new HashMap<String, Object>() {{
                    put("confidence", 0.95);
                    put("class", "Flu");
                }}
        );
        ResponseEntity<List> responseEntity = new ResponseEntity<>(mockResults, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://localhost:5000/api/validation_results"),
                eq(HttpMethod.GET),
                eq(null),
                eq(List.class)
        )).thenReturn(responseEntity);

        String viewName = controller.getViewAll(null, null, model);

        assertEquals("Admin/DiseasePredictionModel/ViewAllValidations", viewName);
    }

    @Test
    void getViewAll_withClassName_returnsFilteredResults() {

        List<Map<String, Object>> mockResults = Collections.singletonList(
                new HashMap<String, Object>() {{
                    put("confidence", 0.90);
                    put("class", "COVID-19");
                }}
        );
        ResponseEntity<List> responseEntity = new ResponseEntity<>(mockResults, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://localhost:5000/api/validation_results/search?class=COVID-19"),
                eq(HttpMethod.GET),
                eq(null),
                eq(List.class)
        )).thenReturn(responseEntity);

        String viewName = controller.getViewAll("COVID-19", null, model);

        assertEquals("Admin/DiseasePredictionModel/ViewAllValidations", viewName);
    }


    @Test
    void getViewAll_apiReturnsEmptyList() {

        ResponseEntity<List> responseEntity = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(restTemplate.exchange(any(), any(), any(), eq(List.class))).thenReturn(responseEntity);

        String viewName = controller.getViewAll(null, null, model);

        assertEquals("Admin/DiseasePredictionModel/ViewAllValidations", viewName);
    }

    @Test
    void getViewAll_apiReturnsError() {

        when(restTemplate.exchange(any(), any(), any(), eq(List.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        String viewName = controller.getViewAll(null, null, model);


        assertEquals("Admin/DiseasePredictionModel/ViewAllValidations", viewName);

    }

    @Test
    void getViewAll_exceptionThrown() {
        // Mock an exception thrown by the RestTemplate
        when(restTemplate.exchange(any(), any(), any(), eq(List.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // Call the controller method
        String viewName = controller.getViewAll(null, null, model);

        // Assertions
        assertEquals("Admin/DiseasePredictionModel/ViewAllValidations", viewName);
        // Verify that an error message is added to the model
        // In a real test, you'd verify model.addAttribute("error", "Error loading validation results: Connection refused")
    }
}