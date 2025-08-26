package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.ResetPasswordController;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.Alcura.Customer.Service.hashPassword;
import jakarta.servlet.http.HttpSession;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private hashPassword hashPassword;

    @InjectMocks
    private ResetPasswordController resetPasswordController;

    private MockHttpServletResponse response;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
    }



    @Test
    void showResetPasswordForm_WhenTokenInvalid_ShowsAlert() throws IOException {

        String token = "invalid-token";
        session.setAttribute("token", "different-token");

        resetPasswordController.showResetPasswordForm(token, session, response);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Invalid or expired token Please try again!')"));
        assertTrue(content.contains("window.location.href = '/Customer/Signing'"));
    }

    @Test
    void processResetPassword_WhenPasswordsDontMatch_ShowsAlert() throws IOException {

        session.setAttribute("token", "valid-token");
        String newPassword = "password123";
        String confirmPassword = "different123";

        resetPasswordController.processResetPassword(newPassword, confirmPassword, session, response);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Password do not match')"));
        assertTrue(content.contains("window.location.href = 'reset-password?token=valid-token'"));
    }

    @Test
    void processResetPassword_WhenSessionExpired_ShowsAlert() throws IOException {

        String newPassword = "password123";
        String confirmPassword = "password123";

        resetPasswordController.processResetPassword(newPassword, confirmPassword, session, response);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Session Expired!. Please Try again!')"));
        assertTrue(content.contains("window.location.href = '/Customer/recover_psw.html'"));
    }

    @Test
    void processResetPassword_WhenUserNotFound_ShowsAlert() throws IOException {

        session.setAttribute("email", "nonexistent@test.com");
        session.setAttribute("token", "valid-token");
        String newPassword = "password123";
        String confirmPassword = "password123";

        when(customerRepository.findByEmailAndStatus(anyString(), eq(1))).thenReturn(null);

        resetPasswordController.processResetPassword(newPassword, confirmPassword, session, response);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('User not found')"));
        assertTrue(content.contains("window.location.href = '/Customer/Signing'"));
    }

    @Test
    void processResetPassword_WhenValid_UpdatesPassword() throws IOException {
        String email = "test@example.com";
        String token = "valid-token";
        String newPassword = "password123";
        String confirmPassword = "password123";
        String hashedPassword = "hashedPassword123";

        session.setAttribute("email", email);
        session.setAttribute("token", token);
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setStatus(1);

        when(customerRepository.findByEmailAndStatus(email, 1)).thenReturn(customer);
        when(hashPassword.hashPassword(newPassword)).thenReturn(hashedPassword);

        resetPasswordController.processResetPassword(newPassword, confirmPassword, session, response);
        verify(customerRepository).save(customer);
        assertEquals(hashedPassword, customer.getPassword());
        assertNull(session.getAttribute("token"));
        assertNull(session.getAttribute("email"));

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Password updated Successfully!')"));
        assertTrue(content.contains("window.location.href = '/Customer/Signing'"));
    }
}