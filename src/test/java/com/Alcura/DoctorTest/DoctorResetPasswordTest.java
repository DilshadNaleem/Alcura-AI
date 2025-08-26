package com.Alcura.DoctorTest;

import com.Alcura.Doctor.Controller.DRResetPasswordController;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.Customer.Service.hashPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorResetPasswordTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private hashPassword hashPassword;

    @InjectMocks
    private DRResetPasswordController drResetPasswordController;

    private MockHttpSession session;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
    }

    @Test
    void showResetPasswordForm_WhenTokenValid_ReturnsFormPage() throws IOException {
        String token = "valid-token";
        session.setAttribute("token", token);

        String result = drResetPasswordController.showResetPasswordForm(token, session, response);
        assertEquals("/Doctor/ResetPasswordForm", result);
    }

    @Test
    void showResetPasswordForm_WhenTokenInvalid_ShowsAlert() throws IOException {
        String token = "invalid-token";

        drResetPasswordController.showResetPasswordForm(token, session, response);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Invalid or expired token. Please try Again!');"));
        assertTrue(content.contains("window.location.href = '/Doctor/Signing';"));
    }

    @Test
    void processResetPassword_WhenPasswordsDontMatch_ShowsAlert() throws IOException {
        session.setAttribute("token", "valid-token");
        String password = "password123";
        String confirmPassword = "differentPassword";

        drResetPasswordController.processResetPassword(password, confirmPassword, session, response);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Password do not match');"));
        assertTrue(content.contains("window.location.href = '/Doctor/ResetPasswordForm?token=valid-token';"));
    }

    @Test
    void processResetPassword_WhenSessionExpired_ShowsAlert() throws IOException {

        session.setAttribute("token", "valid-token");
        String password = "password123";
        String confirmPassword = "password123";

        drResetPasswordController.processResetPassword(password, confirmPassword, session, response);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Session Expired! Please Try Again!');"));
        assertTrue(content.contains("window.location.href = '/Customer/ResetPasswordForm';"));
    }

    @Test
    void processResetPassword_WhenUserNotFound_ShowsAlert() throws IOException {
        String email = "nonExist@gmail.com";
        session.setAttribute("email", email);
        session.setAttribute("token", "valid-token");
        String password = "password123";
        String confirmPassword = "password123";

        when(doctorRepository.findByEmailAndStatus(anyString(), eq(1))).thenReturn(null);

        drResetPasswordController.processResetPassword(password, confirmPassword, session, response);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('User not Found');"));
        assertTrue(content.contains("window.location.href = '/Doctor/Signing';"));
    }

    @Test
    void processResetPassword_WhenSuccess_ShowsSuccessMessage() throws IOException {
        String email = "test@gmail.com";
        String token = "valid-token";
        String hashedPassword = "hashedPassword123";
        String password = "password123";
        String confirmPassword = "password123";

        session.setAttribute("email", email);
        session.setAttribute("token", token);
        Doctor doctor = new Doctor();
        doctor.setStatus(1);
        doctor.setEmail(email);

        when(doctorRepository.findByEmailAndStatus(anyString(), eq(1))).thenReturn(doctor);
        when(hashPassword.hashPassword(password)).thenReturn(hashedPassword);

        drResetPasswordController.processResetPassword(password, confirmPassword, session, response);

        assertEquals(hashedPassword, doctor.getPassword());
        assertNull(session.getAttribute("email"));
        assertNull(session.getAttribute("token"));
        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Password Updated Successfully!');"));
        assertTrue(content.contains("window.location.href = '/Doctor/Signing'"));
    }
}