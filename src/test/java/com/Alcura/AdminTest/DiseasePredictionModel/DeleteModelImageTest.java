package com.Alcura.AdminTest.DiseasePredictionModel;

import com.Alcura.Admin.Controller.DiseasePredictionModel.DeleteModelImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteModelImageTest
{
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DeleteModelImage deleteModelImage;

    @Mock
    private Model model;

    private final String datasetType = "COVID-19";
    private final String API_URL_BASE = "http://localhost:5000/api/images/";


    @Test
    void deleteSelectedImages_Success_ShouldReturnSuccessMessage()
    {
        List<String> selectedImages = Arrays.asList(
                "image1.jpg|train",
                "image2.jpg|train"
        );

        doNothing().when(restTemplate).delete(anyString());

        String viewName = deleteModelImage.deleteSelectedImages(selectedImages,datasetType,model);
        assertEquals("Admin/DiseasePredictionModel/ViewImages", viewName);
        verify(restTemplate, times(2)).delete(anyString());
        verify(model).addAttribute(eq("success"), eq("Successfully deleted 2 images"));
    }

    @Test
    void deleteSelectedImages_PartialFailure_ShouldReturnErrorMessage()
    {
        List<String> selectedImages = Arrays.asList(
                "image1.jpg|train",
                "image2.jpg|train",
                "image3.jpg|train"
        );

        String url1 = API_URL_BASE + "COVID-19/train/image1.jpg";
        String url2 = API_URL_BASE + "COVID-19/train/image2.jpg";
        String url3 = API_URL_BASE + "COVID-19/train/image3.jpg";


        doNothing().when(restTemplate).delete(url1);
        doNothing().when(restTemplate).delete(url3);

        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .when(restTemplate).delete(url2);

        String viewName = deleteModelImage.deleteSelectedImages(selectedImages, datasetType, model);

        assertEquals("Admin/DiseasePredictionModel/ViewImages", viewName);
        verify(restTemplate).delete(url1);
        verify(restTemplate).delete(url2);
        verify(restTemplate).delete(url3);

        String expectedError = "Deleted 2 images, but failed to delete 1: image2.jpg (Not found)";
        verify(model).addAttribute(eq("error"), eq(expectedError));
        verify(model).addAttribute(eq("dataname"), eq(List.of(datasetType)));

    }
}
