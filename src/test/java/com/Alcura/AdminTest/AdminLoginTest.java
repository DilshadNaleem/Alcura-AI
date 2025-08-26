package com.Alcura.AdminTest;

import com.Alcura.Admin.Controller.AdminLoginController;
import com.Alcura.Admin.DTO.AdminLoginRequest;
import com.Alcura.Admin.Service.AdminLoginAuthImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminLoginTest
{
    @Mock
    private AdminLoginAuthImpl adminLoginAuth;

    @InjectMocks
    private AdminLoginController adminLoginController;

    private MockHttpServletResponse response;
    private MockHttpSession session;
    private AdminLoginRequest loginRequest;

    @BeforeEach
    void setUp()
    {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        loginRequest = new AdminLoginRequest();
    }

    @Test
    void successfulLogin_WriteSuccessScript() throws Exception
    {
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        doAnswer(invocation -> {
            HttpSession actualSession = invocation.getArgument(1);
            actualSession.setAttribute("email", loginRequest.getEmail());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location","/Admin/Dashboard");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        }).when(adminLoginAuth).loginAdmin(any(AdminLoginRequest.class), any(HttpSession.class));

        adminLoginController.loginAdmin(loginRequest,session,response);

        verify(adminLoginAuth, times(1)).loginAdmin(any(AdminLoginRequest.class), eq(session));
        assertEquals("test@example.com", session.getAttribute("email"));
        String responseContent = response.getContentAsString();
        System.out.println("Response Content:\n" + responseContent); // Print for debugging
        assertTrue(responseContent.contains("Login Successful!"));
        assertTrue(responseContent.contains("window.location.href = '/Admin/Dashboard';"));
        assertTrue(responseContent.contains("<script type='text/javascript'>"));
        assertTrue(responseContent.contains("</script>"));
    }

    @Test
    void invalidLogin_FailureScript() throws Exception
    {
        loginRequest.setPassword("password123");
        loginRequest.setEmail("test@gmail.com");

        ResponseEntity<String> failureResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        when(adminLoginAuth.loginAdmin(any(AdminLoginRequest.class), any(MockHttpSession.class)))
                .thenReturn(failureResponse);

        adminLoginController.loginAdmin(loginRequest,session,response);

        assertNull(session.getAttribute("email"));
        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("Invalid email or Password. Please Try Again!"));
        assertTrue(responseContent.contains("window.location.href = '/Admin/Signing';"));

    }

    @Test
    void unverifiedAccount_WriteForbiddenScript() throws IOException
    {
        loginRequest.setEmail("unverified@example.com");
        loginRequest.setPassword("password123");

        ResponseEntity<String> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Account not verified. Please verify your account to Login");

        when(adminLoginAuth.loginAdmin(any(AdminLoginRequest.class), any(MockHttpSession.class))).thenReturn(forbiddenResponse);

        adminLoginController.loginAdmin(loginRequest,session,response);

        assertNull(session.getAttribute("email"));

        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("Account not verified. Please verify your account to Login"));
        assertTrue(responseContent.contains("window.location.href = '/Admin/Signing';"));
    }
 }
