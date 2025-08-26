package com.Alcura.AdminTest.MedicinePredictionModel;

import com.Alcura.Admin.Controller.MedicinePredictionModel.DeleteMedicineImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteMedicineModelImageTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Model model;

    @InjectMocks
    private DeleteMedicineImage controller;

    private List<String> selectedImages;
    private String datasetType;

    @BeforeEach
    void setUp() {
        datasetType = "Aspirin";
    }

    @Test
    void testDeleteImages_Success() {

        selectedImages = List.of("image1.jpg|train", "image2.png|validation");
        String url1 = "http://localhost:5000/api/images/Aspirin/train/image1.jpg";
        String url2 = "http://localhost:5000/api/images/Aspirin/validation/image2.png";

        doNothing().when(restTemplate).delete(eq(url1));
        doNothing().when(restTemplate).delete(eq(url2));

        String viewName = controller.deleteImages(selectedImages, datasetType, model);

        verify(restTemplate, times(1)).delete(url1);
        verify(restTemplate, times(1)).delete(url2);
        verify(model).addAttribute("success", "Successfully deleted 2 images");
        verify(model).addAttribute("dataname", Collections.singletonList(datasetType));
        assertEquals("/Admin/MedicinePredictionModel/ViewMedicineImages", viewName);
    }

    @Test
    void testDeleteImages_Failure() {

        selectedImages = List.of("image1.jpg|train", "image2.png|validation");
        String url1 = "http://localhost:5000/api/images/Aspirin/train/image1.jpg";
        String url2 = "http://localhost:5000/api/images/Aspirin/validation/image2.png";

        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .when(restTemplate).delete(eq(url1));
        doNothing().when(restTemplate).delete(eq(url2));

        String viewName = controller.deleteImages(selectedImages, datasetType, model);

        verify(restTemplate, times(1)).delete(url1);
        verify(restTemplate, times(1)).delete(url2);
        verify(model).addAttribute(eq("error"), anyString());
        verify(model).addAttribute("dataname", Collections.singletonList(datasetType));
        assertEquals("/Admin/MedicinePredictionModel/ViewMedicineImages", viewName);
    }
}