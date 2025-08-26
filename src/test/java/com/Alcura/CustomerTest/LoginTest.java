package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.LoginController;
import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.Service.CusLoginAuthServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginTest {

    @Mock
    private CusLoginAuthServiceImpl customerAuthService;

    @InjectMocks
    private LoginController loginController;

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
    void successfulLogin_WritesSuccessScript() throws Exception {
        loginRequest.setEmail("test@example.com");
        loginRequest.setSigninPassword("password123");

        doAnswer(invocation -> {
            HttpSession actualSession = invocation.getArgument(1);
            actualSession.setAttribute("email", loginRequest.getEmail());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/Customer/Dashboard");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }).when(customerAuthService).loginCustomer(any(LoginRequest.class), any(HttpSession.class));

        loginController.loginCustomer(loginRequest, response, session);
        verify(customerAuthService, times(1)).loginCustomer(any(LoginRequest.class), eq(session));

        assertEquals("test@example.com", session.getAttribute("email"));

        String responseContent = response.getContentAsString();
        System.out.println("Response Content:\n" + responseContent);
        assertTrue(responseContent.contains("Login successful"));
        assertTrue(responseContent.contains("window.location.href = '/Customer/Dashboard';"));
        assertTrue(responseContent.contains("<script type='text/javascript'>"));
        assertTrue(responseContent.contains("</script>"));
    }


    @Test
    void invalidLogin_WritesFailureScript() throws Exception {
        loginRequest.setEmail("invalid@example.com");
        loginRequest.setSigninPassword("wrongpassword");


        ResponseEntity<String> failureResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
        when(customerAuthService.loginCustomer(any(LoginRequest.class), any(MockHttpSession.class)))
                .thenReturn(failureResponse);

        loginController.loginCustomer(loginRequest, response, session);

        assertNull(session.getAttribute("email"));

        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("Invalid email or password. Please try again."));
        assertTrue(responseContent.contains("window.location.href = '/Customer/Signing';"));
    }


    @Test
    void unverifiedAccount_WritesForbiddenScript() throws Exception {
        loginRequest.setEmail("unverified@example.com");
        loginRequest.setSigninPassword("password123");


        ResponseEntity<String> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Your account is not yet verified. Please check your email.");
        when(customerAuthService.loginCustomer(any(LoginRequest.class), any(MockHttpSession.class)))
                .thenReturn(forbiddenResponse);

        loginController.loginCustomer(loginRequest, response, session);

        assertNull(session.getAttribute("email"));

        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("Your account is not yet verified. Please check your email."));
        assertTrue(responseContent.contains("window.location.href = '/Customer/Signing';"));
    }
}