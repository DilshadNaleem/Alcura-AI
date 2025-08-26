package com.Alcura.AdminTest.MedicinePredictionModel;

import com.Alcura.Admin.Controller.MedicinePredictionModel.AddImagesController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddImagesController.class)
public class AddImagesMedicineModelTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void testAddImageForm_Success() throws Exception {
        String medicineName = "Aspirin";
        mockMvc.perform(get("/Admin/MedicineAddImages")
                        .param("medicineName", medicineName))
                .andExpect(status().isOk())
                .andExpect(view().name("/Admin/MedicinePredictionModel/MedicineAddImages"))
                .andExpect(model().attribute("medicineName", medicineName));
    }

    @Test
    public void testUploadImages_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "images",
                "test_image.jpg",
                "image/jpeg",
                "test data".getBytes(StandardCharsets.UTF_8)
        );

        when(restTemplate.exchange(
                eq("http://localhost:5000/api/Medicine/images/Aspirin"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"message\":\"Images Upload Successfully\"}", HttpStatus.OK));

        mockMvc.perform(multipart("/Admin/MedicineUploadImages")
                        .file(mockFile)
                        .param("medicineName", "Aspirin")
                        .param("imageType", "train"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Images Upload Successfully"));
    }


    @Test
    public void testUploadImages_NoFiles() throws Exception {
        MockMultipartFile[] emptyFiles = new MockMultipartFile[0];

        mockMvc.perform(multipart("/Admin/MedicineUploadImages")
                        .file("images", new byte[0]) // Try this approach
                        .param("medicineName", "Aspirin")
                        .param("imageType", "train"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Please select at least one images to upload"));
    }

    @Test
    public void testUploadImages_FlaskApiFailure() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "images",
                "test_image.jpg",
                "image/jpeg",
                "test data".getBytes(StandardCharsets.UTF_8)
        );

        when(restTemplate.exchange(
                eq("http://localhost:5000/api/Medicine/images/Aspirin"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));
        mockMvc.perform(multipart("/Admin/MedicineUploadImages")
                        .file(mockFile)
                        .param("medicineName", "Aspirin")
                        .param("imageType", "train"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Failed to upload images"));
    }
}