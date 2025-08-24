package com.Alcura.AdminTest.DiseasePredictionModel;

import com.Alcura.Admin.Controller.DiseasePredictionModel.AddImagesDiseaseModal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddImagesDiseaseModal.class)
public class AddImagesDiseaseModalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void uploadImages_SuccessfulUpload_ShouldReturnSuccessResponse() throws Exception {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile("images", "test1.jpg", "image/jpeg", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("images", "test2.jpg", "image/jpeg", "content2".getBytes());

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenReturn(ResponseEntity.ok("Success"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/Admin/UploadImages")
                        .file(file1)
                        .file(file2)
                        .param("diseaseName", "COVID-19")
                        .param("imageType", "train")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Images uploaded successfully"));
    }


    @Test
    void addImageForm_ShouldReturnCorrectViewAndModel() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/Admin/DiseaseAddImages")
                        .param("diseaseName", "COVID-19")
                )
                .andExpect(status().isOk());
    }
}