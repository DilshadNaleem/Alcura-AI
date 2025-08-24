package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.FaceAuthLoginController;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FaceAuthLoginControllerTest
{
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FaceAuthLoginController faceAuthLoginController;

    private MockHttpSession session;
    private MockHttpServletResponse response;
    private MockMultipartFile mockFaceImage;
    private Customer testCustomer;

    @BeforeEach
    void setUp()
    {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
        mockFaceImage = new MockMultipartFile(
                "faceImage",
                "face.jpg",
                "image/jpeg",
                "test image data".getBytes()
        );
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setEmail("test@gmail.com");
        testCustomer.setFaceData("test_face_Data");
    }

    @Test
    void faceLogin_Success_ShouldReturnOK() throws Exception
    {
        when(customerRepository.findByEmailAndStatus(testCustomer.getEmail(),1)).
                thenReturn(testCustomer);

        String successJson = "{\"success\": true, \"match\": true}";
        ResponseEntity<String> successResponse = new ResponseEntity<>(successJson, HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class),any(), eq(String.class)))
                .thenReturn(successResponse);

        when(objectMapper.readTree(successJson)).thenReturn(
                new ObjectMapper().readTree(successJson)
        );

        ResponseEntity<Map<String,Object>> result = faceAuthLoginController.facelogin(testCustomer.getEmail(), mockFaceImage,session);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().get("success"));
        assertEquals("Face verification Successful!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_FaceMistMatch_ShouldReturnUnAuthorized() throws Exception
    {
        when(customerRepository.findByEmailAndStatus(testCustomer.getEmail(),1)).thenReturn(testCustomer);

        String mismatchJson =  "{\"success\": true, \"match\": false}";
        ResponseEntity<String> mismatchResponse = new ResponseEntity<>(mismatchJson,HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(mismatchResponse);
        when(objectMapper.readTree(mismatchJson)).thenReturn(
                new ObjectMapper().readTree(mismatchJson)
        );

        ResponseEntity<Map<String,Object>> result = faceAuthLoginController.facelogin(
                testCustomer.getEmail(),mockFaceImage,session
        );

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Face does not match!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_NoFaceData_ShouldReturnNotFound()
    {
        testCustomer.setFaceData(null);
        when(customerRepository.findByEmailAndStatus(testCustomer.getEmail(),1)).thenReturn(testCustomer);

        ResponseEntity<Map<String,Object>> result = faceAuthLoginController.facelogin(testCustomer.getEmail(),mockFaceImage,session);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face data registered for this email", result.getBody().get("message"));
    }

    @Test
    void faceLogin_EmptyEmail_ShouldReturnBadRequest()
    {
        ResponseEntity<Map<String,Object>> result = faceAuthLoginController.facelogin("",mockFaceImage,session);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Email is required", result.getBody().get("message"));
    }

    @Test
    void faceLogin_NoImage_ShouldReturnBadRequest()
    {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "faceImage","face.jpg", "image/jpeg", new byte[0]
        );

        ResponseEntity<Map<String,Object>> result = faceAuthLoginController.facelogin(testCustomer.getEmail(), emptyFile,session);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face image provided", result.getBody().get("message"));
    }

    @Test
    void faceLogin_ExternalServiceError_ShouldReturnBadRequest() throws  Exception
    {
        when(customerRepository.findByEmailAndStatus(testCustomer.getEmail(),1)).thenReturn(testCustomer);

        String externalServiceErrorJson = "{\"success\": false, \"message\": \"No face detected\"}";
        ResponseEntity<String> errorResponse = new ResponseEntity<>(externalServiceErrorJson,HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(errorResponse);
        when(objectMapper.readTree(externalServiceErrorJson)).thenReturn(new ObjectMapper().readTree(externalServiceErrorJson));

        ResponseEntity<Map<String,Object>> result = faceAuthLoginController.facelogin(testCustomer.getEmail(),mockFaceImage,session);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face detected", result.getBody().get("message"));
    }

    @Test
    void faceLogin_EyesNotDetected_ShouldReturnBadRequest() throws Exception
    {
        when(customerRepository.findByEmailAndStatus(testCustomer.getEmail(),1)).thenReturn(testCustomer);
        String eyesErrorJson = "{\"success\": false, \"message\": \"No eyes detected\"}";
        ResponseEntity<String> eyesErrorResponse = new ResponseEntity<>(eyesErrorJson,HttpStatus.BAD_REQUEST);

        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(eyesErrorResponse);

        when(objectMapper.readTree(eyesErrorJson)).thenReturn(new ObjectMapper().readTree(eyesErrorJson));

        ResponseEntity<Map<String,Object>> result = faceAuthLoginController.facelogin(testCustomer.getEmail(),mockFaceImage,session);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Eyes not detected.Please open your eyes!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_GeneralException_ShouldReturnInternalServerError() throws IOException
    {
        when(customerRepository.findByEmailAndStatus(testCustomer.getEmail(),1)).thenReturn(testCustomer);
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Simulated network error"));

        ResponseEntity<Map<String,Object>> result = faceAuthLoginController.facelogin(
                testCustomer.getEmail(), mockFaceImage,session
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Server Error!. Please try Again Later!", result.getBody().get("message"));
    }

}
