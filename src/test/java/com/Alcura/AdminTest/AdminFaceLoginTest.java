package com.Alcura.AdminTest;

import com.Alcura.Admin.Controller.AdminFaceLogin;
import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminFaceLoginTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminFaceLogin faceLogin;

    private MockHttpSession session;
    private MockHttpServletResponse response;
    private MockMultipartFile mockFaceImage;
    private Admin admin;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
        mockFaceImage = new MockMultipartFile(
                "faceImage",
                "face.jpg",
                "image/jpeg",
                "test image data".getBytes()
        );
        admin = new Admin();
        admin.setId(1);
        admin.setEmail("test@gmail.com");
        admin.setFaceData("test_face_data");
        admin.setStatus(1);
    }

    @Test
    void faceLogin_Success_ShouldReturnOk() throws Exception {
        when(adminRepository.findByEmailAndStatus(admin.getEmail(), 1)).thenReturn(admin);

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

        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(admin.getEmail(), mockFaceImage, session);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().get("success"));
        assertEquals("Face verification Successful!", result.getBody().get("message"));
        assertEquals(admin.getEmail(), session.getAttribute("email"));
    }

    @Test
    void faceLogin_Mismatch_ShouldReturnUnauthorized() throws Exception {
        when(adminRepository.findByEmailAndStatus(admin.getEmail(),1)).thenReturn(admin);
        String mismatchJson =  "{\"success\": true, \"match\": false}";
        ResponseEntity<String> mismatchResponse = new ResponseEntity<>(mismatchJson,HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(mismatchResponse);
        when(objectMapper.readTree(mismatchJson)).thenReturn(
                new ObjectMapper().readTree(mismatchJson)
        );

        ResponseEntity<Map<String,Object>> result = faceLogin.facelogin(
                admin.getEmail(),mockFaceImage,session
        );

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Face does not match!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_NoFaceData_ShouldReturnNotFound() {
        admin.setFaceData(null);
        when(adminRepository.findByEmailAndStatus(admin.getEmail(), 1)).thenReturn(admin);

        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(admin.getEmail(), mockFaceImage, session);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face data registered for this email", result.getBody().get("message"));
    }

    @Test
    void faceLogin_AdminNotFound_ShouldReturnNotFound() {
        when(adminRepository.findByEmailAndStatus(admin.getEmail(), 1)).thenReturn(null);

        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(admin.getEmail(), mockFaceImage, session);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face data registered for this email", result.getBody().get("message"));
    }

    @Test
    void faceLogin_EmptyEmail_ShouldReturnBadRequest() {
        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin("", mockFaceImage, session);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Email is required", result.getBody().get("message"));
    }

    @Test
    void faceLogin_NullEmail_ShouldReturnBadRequest() {
        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(null, mockFaceImage, session);

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

        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(admin.getEmail(), emptyFile, session);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("No face image provided", result.getBody().get("message"));
    }


    @Test
    void faceLogin_EyesNotDetected_ShouldReturnBadRequest() throws Exception {
        when(adminRepository.findByEmailAndStatus(admin.getEmail(), 1)).thenReturn(admin);

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

        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(admin.getEmail(), mockFaceImage, session);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Eyes not detected.Please open your eyes!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_VerificationFailed_ShouldReturnBadRequest() throws Exception {
        when(adminRepository.findByEmailAndStatus(admin.getEmail(), 1)).thenReturn(admin);

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

        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(admin.getEmail(), mockFaceImage, session);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Face detection failed", result.getBody().get("message"));
    }

    @Test
    void faceLogin_ServerError_ShouldReturnInternalServerError() throws Exception {
        when(adminRepository.findByEmailAndStatus(admin.getEmail(), 1)).thenReturn(admin);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Server error"));

        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(admin.getEmail(), mockFaceImage, session);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Server Error!. Please try Again Later!", result.getBody().get("message"));
    }

    @Test
    void faceLogin_InvalidJsonResponse_ShouldReturnInternalServerError() throws Exception {
        when(adminRepository.findByEmailAndStatus(admin.getEmail(), 1)).thenReturn(admin);

        String invalidJson = "invalid json";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidJson, HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        when(objectMapper.readTree(invalidJson)).thenThrow(new RuntimeException("JSON parsing error"));

        ResponseEntity<Map<String, Object>> result = faceLogin.facelogin(admin.getEmail(), mockFaceImage, session);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertFalse((Boolean) result.getBody().get("success"));
        assertEquals("Server Error!. Please try Again Later!", result.getBody().get("message"));
    }
}