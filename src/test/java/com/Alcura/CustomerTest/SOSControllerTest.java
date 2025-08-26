package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.SOSController;
import com.Alcura.Customer.DTO.EmergencyRequest;
import com.Alcura.Customer.DTO.SOSRequest;
import com.Alcura.Customer.DTO.TrackingSession;
import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.HospitalRepository;
import com.Alcura.Customer.Repository.EmergencyRequestRepository;
import com.Alcura.Customer.RestControllerAdvice.HospitalNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SOSControllerTest {

    @Mock
    private HospitalRepository hospitalRepo;

    @Mock
    private EmergencyRequestRepository emergencyRequestRepo;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private SOSController sosController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startSOSTracking_Success() {
        SOSRequest request = new SOSRequest();
        request.setLatitude(10.0);
        request.setLongitude(20.0);

        Hospital mockHospital = new Hospital();
        mockHospital.setId(1L);
        mockHospital.setName("Test Hospital");
        mockHospital.setLatitude(BigDecimal.valueOf(11.0));
        mockHospital.setLongitude(BigDecimal.valueOf(21.0));
        mockHospital.setContactEmail("test@hospital.com");
        mockHospital.setAddress("123 Test St");
        mockHospital.setPhoneNumber("123-456-7890");
        mockHospital.setDescription("A test hospital");

        when(hospitalRepo.findNearest(anyDouble(), anyDouble())).thenReturn(Optional.of(mockHospital));
        when(emergencyRequestRepo.save(any(EmergencyRequest.class))).thenReturn(new EmergencyRequest());
        ResponseEntity<?> response = sosController.startSOSTracking(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(hospitalRepo).findNearest(10.0, 20.0);
        verify(emergencyRequestRepo).save(any(EmergencyRequest.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/admin/emergencies"), anyMap());
    }

    @Test
    void startSOSTracking_NoHospitalsFound_ThrowsException() {

        SOSRequest request = new SOSRequest();
        request.setLatitude(10.0);
        request.setLongitude(20.0);

        when(hospitalRepo.findNearest(anyDouble(), anyDouble())).thenReturn(Optional.empty());

        assertThrows(HospitalNotFoundException.class, () -> sosController.startSOSTracking(request));
        verify(hospitalRepo).findNearest(10.0, 20.0);
        verify(emergencyRequestRepo, never()).save(any(EmergencyRequest.class));
        verify(messagingTemplate, never()).convertAndSend(anyString(), anyMap());
    }


    @Test
    void getActiveSessions_ReturnsOk() {
        ResponseEntity<?> response = sosController.getActiveSessions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void getHospitalImage_Success() {
        // Mock data
        String hospitalName = "Test Hospital";
        byte[] imageData = "test image data".getBytes();
        Hospital mockHospital = new Hospital();
        mockHospital.setImage(imageData);

        when(hospitalRepo.findByName(hospitalName)).thenReturn(Optional.of(mockHospital));

        // Call the method
        ResponseEntity<byte[]> response = sosController.getHospitalImage(hospitalName);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(imageData, response.getBody());
        verify(hospitalRepo).findByName(hospitalName);
    }

    @Test
    void getHospitalImage_HospitalNotFound() {
        String hospitalName = "NonExistent Hospital";
        when(hospitalRepo.findByName(hospitalName)).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = sosController.getHospitalImage(hospitalName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(hospitalRepo).findByName(hospitalName);
    }

}