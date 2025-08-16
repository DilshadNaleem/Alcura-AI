package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.RegisterController;
import com.Alcura.Customer.DTO.RegisterRequest;
import com.Alcura.FactoryPattern.Service.RegistrationService;
import com.Alcura.FactoryPattern.ServiceFactory.RegistrationServiceFactory;
import jakarta.servlet.http.HttpServletResponse;
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
public class RegisterTest {

    @Mock
    private RegistrationServiceFactory serviceFactory;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegisterController registerController;

    @Test
    void successfulRegistration_shouldWriteSuccessScriptAndRedirect() throws IOException {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RegisterRequest request = createRegisterRequest();

        when(serviceFactory.getService(eq("customer"))).thenReturn(registrationService);
        when(registrationService.register(any(RegisterRequest.class), any(HttpSession.class)))
                .thenReturn(new ResponseEntity<>("Registration successful!", HttpStatus.CREATED));

        // Act
        registerController.registerCustomer(request, session, response);

        // Assert
        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("alert('Registration successful! Please verify your email.');"));
        assertTrue(responseContent.contains("window.location.href = '/Customer/verification';"));
    }

    @Test
    void failedRegistration_shouldWriteFailureScriptAndRedirect() throws IOException {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RegisterRequest request = createRegisterRequest();

        when(serviceFactory.getService(eq("customer"))).thenReturn(registrationService);
        when(registrationService.register(any(RegisterRequest.class), any(HttpSession.class)))
                .thenReturn(new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST));

        // Act
        registerController.registerCustomer(request, session, response);

        // Assert
        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("alert('Email already exists');"));
        assertTrue(responseContent.contains("window.location.href = '/Customer/Signing';"));
    }

    // Helper method to create a RegisterRequest object
    private RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstname("Test");
        request.setLastname("User");
        request.setEmail("test@example.com");
        request.setPassword("testpassword");
        request.setConfirmpassword("testpassword");
        return request;
    }
}