package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.PasswordController;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.Alcura.Customer.Service.Interfaces.EmailService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordController passwordController;

    private MockHttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
    }

    @Test
    void processForgotPassword_WhenEmailNotFoundOrInactive_ShouldShowAlert() throws IOException {
        // Arrange
        String email = "nonexistent@test.com";
        when(customerRepository.findByEmailAndStatus(email, 1)).thenReturn(null);

        // Act
        passwordController.processForgotPassword(email, session, response);

        // Assert
        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Email not found or account inactive.')"));
        assertTrue(content.contains("window.location.href = '/Customer/recover_psw.html'"));
    }

    @Test
    void processForgotPassword_WhenEmailExists_ShouldSendResetLink() throws IOException {
        // Arrange
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setStatus(1);

        when(customerRepository.findByEmailAndStatus(email, 1)).thenReturn(customer);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // Act
        passwordController.processForgotPassword(email, session, response);

        // Assert
        // Verify email was sent
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());

        // Verify session attributes were set
        assertNotNull(session.getAttribute("token"));
        assertEquals(email, session.getAttribute("email"));

        // Verify response
        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Password reset Link send to the email')"));
        assertTrue(content.contains("window.location.href = '/Customer/Signing.html'"));
    }

    @Test
    void processForgotPassword_WhenEmailExists_ShouldGenerateValidToken() throws IOException {
        // Arrange
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setStatus(1);

        when(customerRepository.findByEmailAndStatus(email, 1)).thenReturn(customer);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // Act
        passwordController.processForgotPassword(email, session, response);

        // Assert
        String token = (String) session.getAttribute("token");
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertDoesNotThrow(() -> UUID.fromString(token));
    }

    @Test
    void processForgotPassword_WhenEmailExists_ShouldIncludeTokenInResetLink() throws IOException {
        // Arrange
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setStatus(1);

        when(customerRepository.findByEmailAndStatus(email, 1)).thenReturn(customer);
        doNothing().when(emailService).sendPasswordResetEmail(eq(email), anyString());

        // Act
        passwordController.processForgotPassword(email, session, response);

        // Assert
        String token = (String) session.getAttribute("token");
        verify(emailService).sendPasswordResetEmail(eq(email),
                argThat(link -> link.contains("http://localhost:8081/reset-password?token=" + token)));
    }
}