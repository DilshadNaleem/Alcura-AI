package com.Alcura.DoctorTest;

import com.Alcura.Doctor.Controller.DRLoginController;
import com.Alcura.Doctor.DTO.DRLoginRequest;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.Doctor.Service.DRLoginAuthServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorLoginTest {

    @Mock
    private DRLoginAuthServiceImpl drLoginAuthService;

    @Mock
    private DoctorRepository doctorRepository;


    @InjectMocks
    private DRLoginController loginController;

    private MockHttpServletResponse response;
    private MockHttpSession session;
    private DRLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        loginRequest = new DRLoginRequest();
    }


    @Test
    void successfulLogin_WriteSuccessScript() throws Exception {
        loginRequest.setEmail("test@example.com");
        loginRequest.setSigningPassword("password123");

        doAnswer(invocation -> {
            HttpSession actualSession = invocation.getArgument(1);
            actualSession.setAttribute("email", loginRequest.getEmail());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/Doctor/Dashboard");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }).when(drLoginAuthService).loginCustomer(any(DRLoginRequest.class), any(HttpSession.class));

        loginController.Login(loginRequest, response, session);

        verify(drLoginAuthService, times(1)).loginCustomer(any(DRLoginRequest.class), eq(session));
        assertEquals("test@example.com", session.getAttribute("email"));
        String responseContent = response.getContentAsString();
        System.out.println("Response Content:\n" + responseContent);
        assertTrue(responseContent.contains("Login Successful!"));
        assertTrue(responseContent.contains("window.location.href = '/Doctor/Dashboard';"));
        assertTrue(responseContent.contains("<script type='text/javascript'>"));
        assertTrue(responseContent.contains("</script>"));
    }

    @Test
    void invalidLogin_FailureScript() throws Exception {
        loginRequest.setEmail("test@example.com");
        loginRequest.setSigningPassword("wrongpassword");

        ResponseEntity<String> failureResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");

        when(drLoginAuthService.loginCustomer(any(DRLoginRequest.class), any(MockHttpSession.class)))
                .thenReturn(failureResponse);

        loginController.Login(loginRequest, response, session);

        assertNull(session.getAttribute("email"));
        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("Invalid email or Password. Please Try Again!"));
        assertTrue(responseContent.contains("window.location.href = '/Doctor/Signing';"));
    }

    @Test
    void unverifiedAccount_WriteForbiddenScript() throws Exception {
        loginRequest.setEmail("unverified@example.com");
        loginRequest.setSigningPassword("password123");

        ResponseEntity<String> forbiddenResponse = ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Account not verified. Please verify your account to Login");

        when(drLoginAuthService.loginCustomer(any(DRLoginRequest.class), any(MockHttpSession.class)))
                .thenReturn(forbiddenResponse);

        loginController.Login(loginRequest, response, session);

        assertNull(session.getAttribute("email"));
        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("Account not verified. Please verify your account to Login"));
        assertTrue(responseContent.contains("window.location.href = '/Doctor/Signing';"));
    }
}