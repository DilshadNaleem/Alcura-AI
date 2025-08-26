package com.Alcura.AdminTest.Hospital;

import com.Alcura.Admin.Controller.Hospital.DeleteHospitalController;
import com.Alcura.Admin.Service.DeleteCustomerService;
import com.Alcura.Admin.Service.DeleteHospitalService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DeleteHospitalControllerTest
{
    @Mock
    private DeleteHospitalService deleteCustomerService;

    @InjectMocks
    private DeleteHospitalController deleteHospitalController;

    private MockHttpSession session;
    private MockHttpServletResponse response;
    private PrintWriter writer;
    private String id = "HospitalId";

    @BeforeEach
    void setUp() throws IOException
    {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
        writer = new PrintWriter(response.getWriter());
    }

    @Test
    void DeleteHospital_NotFoundSession_ShouldShowError() throws IOException
    {
        session.removeAttribute("email");
        deleteHospitalController.DeleteHospital(id,response,session);
        String content = response.getContentAsString();

        assertTrue("Error Message", content.contains("alert('Login First');"));
        assertTrue("Redirect", content.contains("window.location.href = '/Admin/Signing';"));

        verifyNoInteractions(deleteCustomerService);
    }

    @Test
    void DeleteHospital_Success_ShouldShowSuccessMessage() throws IOException
    {
        String email = "test@gmail.com";
        session.setAttribute("email",email );
        deleteHospitalController.DeleteHospital(id,response,session);
        verify(deleteCustomerService).deleteHospitalByUniqueId(id,response.getWriter());

    }
}
