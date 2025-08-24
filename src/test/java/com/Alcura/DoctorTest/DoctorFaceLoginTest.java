package com.Alcura.DoctorTest;

import com.Alcura.Doctor.Controller.DrFaceLoginController;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorFaceLoginTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DrFaceLoginController faceLoginController;

    private MockHttpSession session;
    private MockMultipartFile mockFaceImage;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        mockFaceImage = new MockMultipartFile(
                "faceImage",
                "face.jpg",
                "image/jpeg",
                "test image data".getBytes()
        );
        doctor = new Doctor();
        doctor.setId(1);
        doctor.setEmail("doctor@example.com");
        doctor.setFaceData("test_face_data");
    }

    @Test
    void faceLogin_Success_ShouldReturnOk() throws Exception {
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(doctor);

        String successJson = "{\"success\": true, \"match\": true}";
        ResponseEntity<String> successResponse = new ResponseEntity<>(successJson, HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(successResponse);

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(successJson)).thenReturn(mockJsonNode);
        when(mockJsonNode.get("success")).thenReturn(mockJsonNode);
        when(mockJsonNode.asBoolean()).thenReturn(true);
        when(mockJsonNode.get("match")).thenReturn(mockJsonNode);
        when(mockJsonNode.asBoolean()).thenReturn(true);

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().get("success"));
        assertEquals("Face verification Successful!", result.getBody().get("message"));
        assertEquals(doctor.getEmail(), session.getAttribute("email"));
    }

    @Test
    void faceLogin_Mismatch_ShouldReturnUnauthorized() throws Exception {
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(doctor);

        String mismatchJson = "{\"success\": true, \"match\": false}";
        ResponseEntity<String> mismatchResponse = new ResponseEntity<>(mismatchJson, HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mismatchResponse);

        // Use real ObjectMapper to parse the JSON instead of mocking
        JsonNode actualJsonNode = new ObjectMapper().readTree(mismatchJson);
        when(objectMapper.readTree(mismatchJson)).thenReturn(actualJsonNode);

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Face does not match!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_NoFaceData_ShouldReturnNotFound() {
        doctor.setFaceData(null);
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(doctor);

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face data registered for this email", result.getBody().get("message"));
    }

    @Test
    void faceLogin_DoctorNotFound_ShouldReturnNotFound() {
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(null);

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face data registered for this email", result.getBody().get("message"));
    }

    @Test
    void faceLogin_EmptyEmail_ShouldReturnBadRequest() {
        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                "", mockFaceImage, session
        );

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Email is required", result.getBody().get("message"));
    }

    @Test
    void faceLogin_NullEmail_ShouldReturnBadRequest() {
        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                null, mockFaceImage, session
        );

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Email is required", result.getBody().get("message"));
    }

    @Test
    void faceLogin_EmptyFaceImage_ShouldReturnBadRequest() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "faceImage",
                "face.jpg",
                "image/jpeg",
                new byte[0]
        );

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), emptyFile, session
        );

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face image provided", result.getBody().get("message"));
    }

    @Test
    void faceLogin_NullFaceImage_ShouldReturnBadRequest() {
        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), null, session
        );

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face image provided", result.getBody().get("message"));
    }

    @Test
    void faceLogin_EyesNotDetected_ShouldReturnBadRequest() throws Exception {
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(doctor);

        String eyesErrorJson = "{\"success\": false, \"message\": \"Eyes not detected in the image\"}";
        ResponseEntity<String> errorResponse = new ResponseEntity<>(eyesErrorJson, HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(errorResponse);

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(eyesErrorJson)).thenReturn(mockJsonNode);
        when(mockJsonNode.get("success")).thenReturn(mockJsonNode);
        when(mockJsonNode.asBoolean()).thenReturn(false);
        when(mockJsonNode.has("message")).thenReturn(true);
        when(mockJsonNode.get("message")).thenReturn(mockJsonNode);
        when(mockJsonNode.asText()).thenReturn("Eyes not detected in the image");

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Eyes not detected.Please open your eyes!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_VerificationFailed_ShouldReturnBadRequest() throws Exception {
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(doctor);

        String errorJson = "{\"success\": false, \"message\": \"Face detection failed\"}";
        ResponseEntity<String> errorResponse = new ResponseEntity<>(errorJson, HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(errorResponse);

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(errorJson)).thenReturn(mockJsonNode);
        when(mockJsonNode.get("success")).thenReturn(mockJsonNode);
        when(mockJsonNode.asBoolean()).thenReturn(false);
        when(mockJsonNode.has("message")).thenReturn(true);
        when(mockJsonNode.get("message")).thenReturn(mockJsonNode);
        when(mockJsonNode.asText()).thenReturn("Face detection failed");

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Face detection failed", result.getBody().get("message"));
    }

    @Test
    void faceLogin_ServerError_ShouldReturnInternalServerError() throws Exception {
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(doctor);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Server error"));

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Server Error!. Please try Again Later!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_InvalidJsonResponse_ShouldReturnInternalServerError() throws Exception {
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(doctor);

        String invalidJson = "invalid json";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidJson, HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        when(objectMapper.readTree(invalidJson)).thenThrow(new RuntimeException("JSON parsing error"));

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Server Error!. Please try Again Later!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_VerificationSuccessButNoMatchField_ShouldReturnBadRequest() throws Exception {
        when(doctorRepository.findByemail(doctor.getEmail())).thenReturn(doctor);

        String incompleteJson = "{\"success\": true}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(incompleteJson, HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(incompleteJson)).thenReturn(mockJsonNode);
        when(mockJsonNode.get("success")).thenReturn(mockJsonNode);
        when(mockJsonNode.asBoolean()).thenReturn(true);
        when(mockJsonNode.get("match")).thenReturn(null);

        ResponseEntity<Map<String, Object>> result = faceLoginController.facelogin(
                doctor.getEmail(), mockFaceImage, session
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
    }
}