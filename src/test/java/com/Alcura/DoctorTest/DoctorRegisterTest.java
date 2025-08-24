package com.Alcura.DoctorTest;

import com.Alcura.Doctor.Controller.DoctorRegisterController;
import com.Alcura.Doctor.DTO.DoctorRegisterRequest;
import com.Alcura.FactoryPattern.Service.RegistrationService;
import com.Alcura.FactoryPattern.ServiceFactory.RegistrationServiceFactory;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorRegisterTest {

    @Mock
    private RegistrationServiceFactory serviceFactory;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private DoctorRegisterController doctorRegisterController;

    private DoctorRegisterRequest createRegisterRequest() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setEmail("doctor.test@gmail.com");
        request.setFirstName("Doctor");
        request.setLastName("Test");
        request.setPassword("doctorPassword");

        // Add any doctor-specific fields if they exist
        return request;
    }

    @Test
    void successfulDoctorRegistration_ShouldShowSuccessScript() throws IOException {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        DoctorRegisterRequest request = createRegisterRequest();

        when(serviceFactory.getService(eq("doctor"))).thenReturn(registrationService);
        when(registrationService.register(any(DoctorRegisterRequest.class), any(HttpSession.class)))
                .thenReturn(new ResponseEntity<>("Registration successful", HttpStatus.CREATED));

        doctorRegisterController.registerDoctor(request, session, response);

        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("alert('Registration successful! Please verify your email.');"));
        assertTrue(responseContent.contains("window.location.href = '/Doctor/Verification';"));
    }

    @Test
    void failedDoctorRegistration_ShouldShowError() throws IOException {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        DoctorRegisterRequest request = createRegisterRequest();

        when(serviceFactory.getService(eq("doctor"))).thenReturn(registrationService);
        when(registrationService.register(any(DoctorRegisterRequest.class), any(HttpSession.class)))
                .thenReturn(new ResponseEntity<>("Email is already exists", HttpStatus.BAD_REQUEST));

        doctorRegisterController.registerDoctor(request, session, response);

        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("alert('Email is already exists');"));
        assertTrue(responseContent.contains("window.location.href = '/Doctor/Signing';"));
    }

    @Test
    void doctorRegistrationWithMismatchedPasswords_ShouldShowError() throws IOException {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        DoctorRegisterRequest request = createRegisterRequest();


        when(serviceFactory.getService(eq("doctor"))).thenReturn(registrationService);
        when(registrationService.register(any(DoctorRegisterRequest.class), any(HttpSession.class)))
                .thenReturn(new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST));

        doctorRegisterController.registerDoctor(request, session, response);

        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("alert('Passwords do not match');"));
        assertTrue(responseContent.contains("window.location.href = '/Doctor/Signing';"));
    }
}