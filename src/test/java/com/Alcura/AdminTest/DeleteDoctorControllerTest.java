package com.Alcura.AdminTest;

import com.Alcura.Admin.Controller.DeleteDoctorController;
import com.Alcura.Admin.Service.DeleteDoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@ExtendWith(MockitoExtension.class)
public class DeleteDoctorControllerTest
{
    @Mock
    private DeleteDoctorService doctorService;

    @InjectMocks
    private DeleteDoctorController deleteDoctorController;

    private MockHttpSession session;
    private MockHttpServletResponse response;
    private String email = "test@gmail.com";
    private String id = "sampleID";

    @BeforeEach
    void setUp()
    {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
    }

    @Test
    void DeleteDoctor_WithoutSession_ShouldShowError() throws IOException
    {
        deleteDoctorController.DeleteDoctor(id, response,session);
        String content = response.getContentAsString();
        assertNotNull("Response content should not be null", content);
        assertTrue("Response should contain session expired alert", content.contains("alert ('Session Expired'); "));
        assertTrue("Response should redirect", content.contains("window.location.href = '/Admin/Signing';"));
    }

    @Test
    void DeleteDoctor_Valid_ShouldShowSuccess() throws IOException
    {
        session.setAttribute("email", email);
        PrintWriter out = response.getWriter();
        deleteDoctorController.DeleteDoctor(id,response,session);
        verify(doctorService).deleteDoctorByUniqueId(eq(id),any(PrintWriter.class));
    }
}
