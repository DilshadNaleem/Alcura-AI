package com.Alcura.AdminTest.MedicinePredictionModel;

import com.Alcura.Admin.Controller.MedicinePredictionModel.ViewMedicineImageController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewAllMedicineValidationTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ViewMedicineImageController controller;

    @Test
    void testViewImages_SuccessWithImages() throws Exception {

        String medicineName = "Aspirin";
        String imageType = "tablet";

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("folder_variants_checked", Arrays.asList("variant1", "variant2"));

        List<Map<String, Object>> images = Arrays.asList(
                createImageMap("image1.jpg", "dataset1", "image/jpeg"),
                createImageMap("image2.png", "dataset2", "image/png")
        );
        mockResponse.put("images", images);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        String result = controller.viewImages(medicineName, imageType, model, redirectAttributes);

        assertEquals("/Admin/MedicinePredictionModel/ViewMedicineImages", result);
        verify(model).addAttribute(eq("dataname"), eq(Arrays.asList("variant1", "variant2")));
        verify(model).addAttribute(eq("data"), eq(images));
        verify(model).addAttribute(eq("dataset"), eq("dataset1"));
        verify(model).addAttribute(eq("mime_type"), eq("image/jpeg"));
        verify(model).addAttribute(eq("name"), eq("image1.jpg"));
    }

    @Test
    void testViewImages_SuccessNoImages() throws Exception {
        // Arrange
        String medicineName = "Paracetamol";
        String imageType = "capsule";

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("folder_variants_checked", Collections.emptyList());
        mockResponse.put("images", Collections.emptyList());

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        String result = controller.viewImages(medicineName, imageType, model, redirectAttributes);

        assertEquals("/Admin/MedicinePredictionModel/ViewMedicineImages", result);
        verify(model).addAttribute(eq("dataname"), eq(Collections.emptyList()));
        verify(model).addAttribute(eq("data"), eq(Collections.emptyList()));
    }

    @Test
    void testViewImages_NoFolderVariants() throws Exception {
        String medicineName = "Ibuprofen";
        String imageType = "syrup";

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("images", Collections.emptyList());

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        String result = controller.viewImages(medicineName, imageType, model, redirectAttributes);

        // Assert
        assertEquals("/Admin/MedicinePredictionModel/ViewMedicineImages", result);
        verify(model).addAttribute(eq("dataname"), eq(Collections.emptyList()));
        verify(model).addAttribute(eq("data"), eq(Collections.emptyList()));
    }

    @Test
    void testViewImages_NullResponse() throws Exception {

        String medicineName = "UnknownMedicine";
        String imageType = "tablet";

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        String result = controller.viewImages(medicineName, imageType, model, redirectAttributes);

        assertEquals("/Admin/MedicinePredictionModel/ViewMedicineImages", result);
        verify(model).addAttribute(eq("dataname"), eq(Collections.emptyList()));
        verify(model).addAttribute(eq("data"), eq(Collections.emptyList()));
    }

    @Test
    void testViewImages_Exception() throws Exception {

        String medicineName = "Aspirin";
        String imageType = "tablet";

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        String result = controller.viewImages(medicineName, imageType, model, redirectAttributes);

        assertEquals("redirect:/Admin/medicineDescription", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Failed to fetch images"));
        verify(model).addAttribute(eq("error"), contains("ERROR: Connection failed"));
    }

    @Test
    void testViewImages_EmptyImagesList() throws Exception {

        String medicineName = "VitaminC";
        String imageType = "tablet";

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("folder_variants_checked", Arrays.asList("variant1"));
        mockResponse.put("images", Collections.emptyList());

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        String result = controller.viewImages(medicineName, imageType, model, redirectAttributes);

        assertEquals("/Admin/MedicinePredictionModel/ViewMedicineImages", result);
        verify(model).addAttribute(eq("dataname"), eq(Arrays.asList("variant1")));
        verify(model).addAttribute(eq("data"), eq(Collections.emptyList()));
    }

    private Map<String, Object> createImageMap(String name, String dataset, String mimeType) {
        Map<String, Object> image = new HashMap<>();
        image.put("name", name);
        image.put("dataset", dataset);
        image.put("mime_type", mimeType);
        return image;
    }
}