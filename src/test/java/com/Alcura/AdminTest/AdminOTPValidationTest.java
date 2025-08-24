package com.Alcura.AdminTest;

import com.Alcura.Admin.Controller.AdminVerifyController;
import com.Alcura.Admin.DTO.AdminLoginRequest;
import com.Alcura.Admin.Service.Interfaces.AdminAuthService;
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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class AdminOTPValidationTest
{
    @Mock
    private AdminAuthService adminAuthService;

    @InjectMocks
    private AdminVerifyController adminVerifyController;

    private MockHttpSession session;
    private MockHttpServletResponse response;
    private AdminLoginRequest loginRequest;

    @BeforeEach
    void setUp()
    {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        loginRequest = new AdminLoginRequest();
    }

    @Test
    void SuccessfulOTPValidation_Success() throws IOException
    {
        String OTP = "123456";

        when(adminAuthService.verifyOtp(any(String.class), any(MockHttpSession.class)))
                .thenReturn(ResponseEntity.ok("Account verified successfully!"));


        adminVerifyController.verifyOtp(OTP,session,response);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        String content = response.getContentAsString();

        assertTrue(content.contains("alert('Account verified Successfully! Please Login');"));
        assertTrue(content.contains("window.location.href = '/Admin/Signing';"));
        assertEquals("text/html", response.getContentType());
    }

    @Test
    void UnSuccessfulOTPValidation_ShouldShowError() throws IOException
    {
        String OTP = "123456";

        when(adminAuthService.verifyOtp(any(String.class), any(MockHttpSession.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP"));

        adminVerifyController.verifyOtp(OTP,session,response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        String content = response.getContentAsString();

        assertTrue(content.contains("alert('Invalid OTP');"));
        assertTrue(content.contains("window.location.href = '/Admin/verification';"));
        assertEquals("text/html", response.getContentType());
    }
}
