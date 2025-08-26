package com.Alcura.AdminTest;

import com.Alcura.Admin.Controller.AdminResetPasswordController;
import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
import com.Alcura.Customer.Service.hashPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AdminResetPasswordControllerTest
{
    @Mock
    private AdminRepository adminRepository;

    @Mock
    private hashPassword hashPassword;

    @InjectMocks
    private AdminResetPasswordController adminResetPasswordController;

    private MockHttpSession session;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp()
    {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
    }


    @Test
    void Successful_ShowForm() throws IOException
    {
        String token = "valid-token";
        session.setAttribute("token", token);

        String result = adminResetPasswordController.showResetPassword(token,session,response);
        assertEquals("/Admin/AdminResetPassword",result);
    }

    @Test
    void processResetPassword_WhenUserNotFound_ShowsAlert() throws IOException
    {
       session.setAttribute("email", "nonExist@gmail.com");
       session.setAttribute("token", "valid-token");

       String newPassword = "password123";
       String confirmPassword = "password123";

       when(adminRepository.findByEmailAndStatus(anyString(), eq(1))).thenReturn(null);

       adminResetPasswordController.processResetPassowrd(newPassword,confirmPassword,session,response);

       String content = response.getContentAsString();
       assertTrue(content.contains("alert('User not found!');"));
        assertTrue(content.contains("window.location.href = '/Admin/Signing'"));
    }

    @Test
    void processResetPassword_WhenSuccess_ShowSuccessMessage() throws IOException
    {
        String email = "test@gmail.com";
        String token = "token";
        String hashedPassword = "password123";
        String newPassword = "password123";
        String confirmPassword = "password123";
        session.setAttribute("email", email);
        session.setAttribute("token", token);

        Admin admin = new Admin();
        admin.setStatus(1);
        admin.setEmail(email);
        when(adminRepository.findByEmailAndStatus(anyString(), eq(1))).thenReturn(admin);
        when(hashPassword.hashPassword(newPassword)).thenReturn(hashedPassword);
        adminResetPasswordController.processResetPassowrd(newPassword,confirmPassword,session,response);

        assertEquals(hashedPassword,admin.getPassword());
        assertNull(session.getAttribute("email"));
        assertNull(session.getAttribute("token"));

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Password Updated Successfully!');"));
        assertTrue(content.contains("window.location.href = '/Admin/Signing';"));
    }
}
