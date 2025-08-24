package com.Alcura.DoctorTest;

import com.Alcura.Doctor.Controller.DoctorVerifyOtp;
import com.Alcura.Doctor.Service.Interfaces.DoctorAuthService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorOTPValidationTest {

    @Mock
    private DoctorAuthService doctorAuthService;

    @InjectMocks
    private DoctorVerifyOtp doctorVerifyOtp;

    private MockHttpSession session;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
    }

    @Test
    void successfulOTPValidation_ShouldRedirectToSigning() throws IOException {
        String otp = "123456";

        when(doctorAuthService.verifyOtp(anyString(), any(MockHttpSession.class)))
                .thenReturn(ResponseEntity.ok("Success"));

        doctorVerifyOtp.verifyOtp(otp, session, response);

        String content = response.getContentAsString();

        assertTrue(content.contains("Account verified Successfully! Please Login"));
        assertTrue(content.contains("/Doctor/Signing"));
        assertEquals("text/html", response.getContentType());
        assertEquals(200, response.getStatus());
    }

    @Test
    void unsuccessfulOTPValidation_ShouldShowErrorAndRedirect() throws IOException {
        String otp = "123456";

        when(doctorAuthService.verifyOtp(anyString(), any(MockHttpSession.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP"));

        doctorVerifyOtp.verifyOtp(otp, session, response);

        String content = response.getContentAsString();

        assertTrue(content.contains("Invalid OTP"));
        assertTrue(content.contains("/Doctor/Signing"));
        assertEquals("text/html", response.getContentType());
        assertEquals(400, response.getStatus());
    }

    @Test
    void nullResponseBody_ShouldShowGenericErrorMessage() throws IOException {
        String otp = "123456";

        when(doctorAuthService.verifyOtp(anyString(), any(MockHttpSession.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));

        doctorVerifyOtp.verifyOtp(otp, session, response);

        String content = response.getContentAsString();

        assertTrue(content.contains("Account verification failed."));
        assertTrue(content.contains("/Doctor/Signing"));
        assertEquals("text/html", response.getContentType());
        assertEquals(400, response.getStatus());
    }

}