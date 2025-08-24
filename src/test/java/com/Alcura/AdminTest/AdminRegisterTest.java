package com.Alcura.AdminTest;

import com.Alcura.Admin.Controller.AdminRegisterController;
import com.Alcura.Admin.DTO.AdminRegisterRequest;
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
import reactor.netty.http.HttpOperations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AdminRegisterTest
{
    @Mock
    private RegistrationServiceFactory serviceFactory;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private AdminRegisterController registerController;


    private AdminRegisterRequest createRegisterRequest()
    {
        AdminRegisterRequest request = new AdminRegisterRequest();
        request.setEmail("test@gmail.com");
        request.setFirstname("Test");
        request.setLastname("User");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");
        return request;
    }

    @Test
    void successfulRegistration_ShouldShowSuccessScript() throws IOException
    {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AdminRegisterRequest request = createRegisterRequest();

        when(serviceFactory.getService(eq("admin"))).thenReturn(registrationService);
        when(registrationService.register(any(AdminRegisterRequest.class), any(HttpSession.class)))
                .thenReturn(new ResponseEntity<>("Registration successful", HttpStatus.CREATED));

        registerController.registerAdmin(request,session,response);

        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("alert('Registration successful! Please verify your email.');"));
        assertTrue(responseContent.contains("window.location.href = '/Admin/verification';"));
    }

    @Test
    void failedRegistration_ShouldShowError() throws IOException
    {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AdminRegisterRequest request = createRegisterRequest();

        when(serviceFactory.getService(eq("admin"))).thenReturn(registrationService);
        when(registrationService.register(any(AdminRegisterRequest.class), any(HttpSession.class)))
                .thenReturn(new ResponseEntity<>("Email is already exists", HttpStatus.BAD_REQUEST));

        registerController.registerAdmin(request,session,response);
        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("alert('Email is already exists');"));
        assertTrue(responseContent.contains("window.location.href = '/Admin/Signing';"));

    }
}
