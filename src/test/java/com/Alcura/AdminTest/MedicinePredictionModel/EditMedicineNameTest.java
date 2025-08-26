package com.Alcura.AdminTest.MedicinePredictionModel;

import com.Alcura.Admin.Controller.MedicinePredictionModel.EditMedicineNameController;
import com.Alcura.Admin.DTO.MedicineInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EditMedicineNameTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private EditMedicineNameController controller;

    @Test
    void testEditMedicine_Success() {

        String medicineName = "Aspirin";
        MedicineInfo mockMedicine = new MedicineInfo();
        mockMedicine.setClassName("Aspirin");

        ResponseEntity<MedicineInfo> responseEntity = new ResponseEntity<>(mockMedicine, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(MedicineInfo.class)))
                .thenReturn(responseEntity);

        String result = controller.editMedicine(medicineName, model);

        assertEquals("/Admin/MedicinePredictionModel/Edit-Medicine", result);
        verify(model).addAttribute(eq("medicine"), eq(mockMedicine));
        verify(model).addAttribute(eq("oldName"), eq(medicineName));
    }

    @Test
    void testEditMedicine_NotFound() {

        String medicineName = "UnknownMedicine";

        ResponseEntity<MedicineInfo> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.getForEntity(anyString(), eq(MedicineInfo.class)))
                .thenReturn(responseEntity);

        String result = controller.editMedicine(medicineName, model);


        assertEquals("redirect:/Admin/MedicinePredictionModel/ViewAllMedicines?error=Medicine+UnknownMedicine+not+found", result);
    }

    @Test
    void testEditMedicine_Exception() {
        String medicineName = "Aspirin";

        when(restTemplate.getForEntity(anyString(), eq(MedicineInfo.class)))
                .thenThrow(new RuntimeException("Connection error"));
        String result = controller.editMedicine(medicineName, model);

        assertEquals("redirect:/Admin/MedicinePredictionModel/ViewAllMedicines?error=Medicine+Aspirin+not+found", result);
    }

    @Test
    void testUpdateMedicine_Success() {

        MedicineInfo updatedMedicine = new MedicineInfo();
        updatedMedicine.setClassName("NewAspirin");
        updatedMedicine.setAdministration("Oral");
        updatedMedicine.setScientificName("Acetylsalicylic acid");
        updatedMedicine.setDosage("500mg");
        updatedMedicine.setContraindications("None");
        updatedMedicine.setDosageForm("Tablet");
        updatedMedicine.setIndications("Pain relief");
        updatedMedicine.setMaxDose("4000mg");
        updatedMedicine.setPrecautions("Take with food");
        updatedMedicine.setPrice("10.99");
        updatedMedicine.setSeriousEffects("Bleeding");
        updatedMedicine.setSideEffects("Upset stomach");
        updatedMedicine.setSourceOfInformation("FDA");
        updatedMedicine.setUse("Analgesic");
        String oldName = "Aspirin";
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        String result = controller.updateMedicine(updatedMedicine, oldName, redirectAttributes);


        assertEquals("redirect:/Admin/medicineDescription", result);
        verify(redirectAttributes).addFlashAttribute(eq("success"), eq("Medicine Updated Successfully!"));
    }

    @Test
    void testUpdateMedicine_ErrorResponse() {

        MedicineInfo updatedMedicine = new MedicineInfo();
        updatedMedicine.setClassName("NewAspirin");
        String oldName = "Aspirin";

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        String result = controller.updateMedicine(updatedMedicine, oldName, redirectAttributes);

        assertEquals("redirect:/Admin/medicineDescription", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Failed to update medicine"));
    }

    @Test
    void testUpdateMedicine_Exception() {
        // Arrange
        MedicineInfo updatedMedicine = new MedicineInfo();
        updatedMedicine.setClassName("NewAspirin");
        String oldName = "Aspirin";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        // Act
        String result = controller.updateMedicine(updatedMedicine, oldName, redirectAttributes);

        // Assert
        assertEquals("redirect:/Admin/medicineDescription", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Error updating medicine"));
    }
}