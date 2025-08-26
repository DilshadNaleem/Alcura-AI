package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.VerifyOtpController;
import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.Service.Interfaces.CustomerAuthService;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class OTPValidationTest
{
    @Mock
    private CustomerAuthService customerAuthService;

    @InjectMocks
    private VerifyOtpController verifyOtpController;

    private MockHttpServletResponse response;
    private MockHttpSession session;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        loginRequest = new LoginRequest();
    }

    @Test
    void SuccessfulOTPValidation_SuccessScript() throws IOException {

        String OTP = "123456";

        when(customerAuthService.verifyOtp(any(String.class), any(MockHttpSession.class)))
                .thenReturn(ResponseEntity.ok("Account verified successfully!"));

        verifyOtpController.verifyOtp(OTP, session, response);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        String content = response.getContentAsString();

        assertTrue(content.contains("alert('Account verified successfully! Please login.');"));
        assertTrue(content.contains("window.location.href = '/Customer/Signing';"));
        assertEquals("text/html", response.getContentType());
    }

    @Test
    void UnSuccessfulOTPValidation_SuccessScript() throws IOException
    {
        String OTP = "123456";
        when(customerAuthService.verifyOtp(any(String.class), any(MockHttpSession.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP"));

        verifyOtpController.verifyOtp(OTP,session,response);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        String content = response.getContentAsString();

        assertTrue(content.contains("alert('Invalid OTP');"));
        assertTrue(content.contains("window.location.href = '/Customer/verification';"));
        assertEquals("text/html", response.getContentType());

    }
}
