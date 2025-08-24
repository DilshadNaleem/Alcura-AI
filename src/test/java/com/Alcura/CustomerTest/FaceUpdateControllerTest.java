package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.FaceUpdateController;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FaceUpdateControllerTest
{
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FaceUpdateController faceUpdateController;

    private MockHttpSession session;
    private MockHttpServletResponse response;
    private MultipartFile mockFaceImage;
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
    void updateFace_Success_ShouldReturnOK() throws IOException
    {
        session.setAttribute("email", testCustomer.getEmail());

        String successResponseJson =  "{\"success\": true, \"encoding\": \"new_face_data\"}";
        ResponseEntity<String> mockFaceServiceResponse = new ResponseEntity<>(successResponseJson, HttpStatus.OK);

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.get("success")).thenReturn(mock(JsonNode.class));
        when(mockJsonNode.get("success").asBoolean()).thenReturn(true);
        when(mockJsonNode.get("encoding")).thenReturn(mock(JsonNode.class));
        when(mockJsonNode.get("encoding").toString()).thenReturn("\"new_face_data\"");

        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(mockFaceServiceResponse);
        when(objectMapper.readTree(successResponseJson)).thenReturn(mockJsonNode);
        when(customerRepository.findByEmail(testCustomer.getEmail())).thenReturn(testCustomer);

        ResponseEntity<Map<String,String>> result = faceUpdateController.updateFace(mockFaceImage,session);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Face updated successfully!", result.getBody().get("message"));
        assertEquals("/Customer/Dashboard", result.getBody().get("redirect"));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateFace_SessionExpired_ShouldReturnUnauthorized() throws IOException
    {

        ResponseEntity<Map<String,String>> result = faceUpdateController.updateFace(mockFaceImage, session);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Session Expired! Please Login Again.", result.getBody().get("message"));
        assertEquals("/Customer/Signing", result.getBody().get("redirect"));
    }

    @Test
    void updateFace_NoImageProvided_ShouldReturnBadRequest()
    {
        session.setAttribute("email", testCustomer.getEmail());
        MockMultipartFile emptyFile = new MockMultipartFile(
                "faceImage", "face.jpeg","image/jpeg", new byte[0]
        );

        ResponseEntity<Map<String,String>> result = faceUpdateController.updateFace(emptyFile,session);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("No image file uploaded", result.getBody().get("message"));
        assertEquals("/Customer/UpdateFace", result.getBody().get("redirect"));
    }

    @Test
    void updateFace_CustomerNotFound_ShouldReturnNotFound() throws IOException
    {
        session.setAttribute("email", testCustomer.getEmail());
        String successResponseJson = "{\"success\": true, \"encoding\": \"new_face_data\"}";
        ResponseEntity<String> mockFaceServiceResponse = new ResponseEntity<>(successResponseJson,HttpStatus.OK);

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(mockJsonNode.get("success")).thenReturn(mock(JsonNode.class));
        when(mockJsonNode.get("success").asBoolean()).thenReturn(true);
        when(mockJsonNode.get("encoding")).thenReturn(mock(JsonNode.class));
        when(mockJsonNode.get("encoding").toString()).thenReturn("\"new_face_data\"");

        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(mockFaceServiceResponse);
        when(objectMapper.readTree(successResponseJson)).thenReturn(mockJsonNode);
        when(customerRepository.findByEmail(testCustomer.getEmail())).thenReturn(null);

        ResponseEntity<Map<String,String>> result = faceUpdateController.updateFace(mockFaceImage,session);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Customer record not found!", result.getBody().get("message"));
        assertEquals("/Customer/UpdateFace", result.getBody().get("redirect"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateFace_FaceServiceFailure_ShouldReturnBadRequest() throws IOException
    {
        session.setAttribute("email", testCustomer.getEmail());
        String failureResponseJson = "{\"success\": false, \"message\": \"No face detected\"}";
        ResponseEntity<String> mockFaceServiceResponse = new ResponseEntity<>(failureResponseJson,HttpStatus.BAD_REQUEST);

        ObjectMapper realObjectMapper = new ObjectMapper();
        JsonNode mockJsonNode = realObjectMapper.readTree(failureResponseJson);

        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(mockFaceServiceResponse);
        when(objectMapper.readTree(failureResponseJson)).thenReturn(mockJsonNode);

        ResponseEntity<Map<String,String>> result = faceUpdateController.updateFace(mockFaceImage,session);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("No face detected", result.getBody().get("message"));
        assertEquals("/Customer/UpdateFace", result.getBody().get("redirect"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateFace_GeneralException_ShouldReturnInternalServerError () throws IOException
    {
        session.setAttribute("email", testCustomer.getEmail());
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Simulated network error"));

        ResponseEntity<Map<String, String>> result = faceUpdateController.updateFace(mockFaceImage, session);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody().get("message").contains("Simulated network error"));
        assertEquals("/Customer/UpdateFace", result.getBody().get("redirect"));
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
