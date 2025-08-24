package com.Alcura.AdminTest.DiseasePredictionModel;

import com.Alcura.Admin.Controller.DiseasePredictionModel.EditDiseaseNameController;
import com.Alcura.Admin.DTO.DiseaseInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EditDiseaseNameTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EditDiseaseNameController editDiseaseNameController;

    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redirectAttributes = new RedirectAttributesModelMap();
    }


    @Test
    void updateDisease_Success() {

        when(restTemplate.exchange(
                eq("http://localhost:5000/api/disease/old_disease_name"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>("Disease updated successfully", HttpStatus.OK));

        DiseaseInfo updatedDisease = new DiseaseInfo();
        updatedDisease.setDisease("new_disease_name");
        String oldName = "old_disease_name";

        String viewName = editDiseaseNameController.updateDisease(updatedDisease, oldName, redirectAttributes);

        assertEquals("redirect:/Admin/diseases", viewName);
        assertEquals("Disease updated successfully!", redirectAttributes.getFlashAttributes().get("success"));
    }


    @Test
    void updateDisease_Failure() {

        when(restTemplate.exchange(
                eq("http://localhost:5000/api/disease/old_disease_name"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>("Failed to update disease: Invalid data", HttpStatus.BAD_REQUEST));


        DiseaseInfo updatedDisease = new DiseaseInfo();
        updatedDisease.setDisease("new_disease_name");
        String oldName = "old_disease_name";

        String viewName = editDiseaseNameController.updateDisease(updatedDisease, oldName, redirectAttributes);

        assertEquals("redirect:/Admin/diseases", viewName);
        assertEquals("Failed to update disease: Failed to update disease: Invalid data", redirectAttributes.getFlashAttributes().get("error"));
    }
}