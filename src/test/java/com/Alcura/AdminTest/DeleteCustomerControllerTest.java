package com.Alcura.AdminTest;

import com.Alcura.Admin.Controller.DeleteCustomerControl;
import com.Alcura.Admin.Service.DeleteCustomerService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;


@ExtendWith(MockitoExtension.class)
public class DeleteCustomerControllerTest
{
    @Mock
    private DeleteCustomerService deleteCustomerService;

    @InjectMocks
    private DeleteCustomerControl deleteCustomerControl;

    private MockHttpServletResponse response;
    private MockHttpSession session;
    private String Email = "test@gmail.com";
    private String id = "sampleId";

    @BeforeEach
    void setUp()
    {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
    }


    @Test
    void DeleteCustomer_WithoutSession_ShouldShowError() throws IOException
    {
        deleteCustomerControl.DeleteCustomer(id,response,session);
        String content = response.getContentAsString();
        assertNotNull("Response content should not be null", content);
        assertTrue("Response should contain session expired alert", content.contains("alert('Session Expired Please Login');"));
        assertTrue("Response should contain redirect to login", content.contains("window.location.href='/Admin/Signing';"));
    }

    @Test
    void DeleteCustomer_Valid_ShouldShowSuccess() throws IOException
    {
        session.setAttribute("email", Email);
        PrintWriter writer = response.getWriter();
        deleteCustomerControl.DeleteCustomer(id,response, session);
        verify(deleteCustomerService).deleteCustomerByUniqueId(eq(id), any(PrintWriter.class));
    }
}
