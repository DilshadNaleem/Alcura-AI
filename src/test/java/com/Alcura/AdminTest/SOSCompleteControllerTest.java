package com.Alcura.AdminTest;

import com.Alcura.Admin.Controller.SOSCompelteController;
import com.Alcura.Customer.DTO.EmergencyRequest;
import com.Alcura.Customer.Repository.EmergencyRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SOSCompleteControllerTest
{
    @Autowired
    @Mock
    private EmergencyRequestRepository emergencyRequestRepository;

    private String trackingId = "TrackingId";
    private MockHttpServletResponse response;
    private PrintWriter writer;

    @InjectMocks
    private SOSCompelteController sosCompelteController;

    @BeforeEach
    void setUp() throws IOException
    {
        response = new MockHttpServletResponse();
        writer = new PrintWriter(response.getWriter());
    }

    @Test
    void SOSComplete_Success_ShowSuccessMessage() throws IOException
    {
        EmergencyRequest emergencyRequest = new EmergencyRequest();
        emergencyRequest.setTrackingId(trackingId);

        when(emergencyRequestRepository.findByTrackingId(trackingId)).thenReturn(emergencyRequest);

        sosCompelteController.completeSOS(trackingId,writer);

        verify(emergencyRequestRepository).findByTrackingId(trackingId);

        assertTrue("Status should be completed", emergencyRequest.getStatus().equals("COMPLETED"));
        emergencyRequestRepository.save(emergencyRequest);

        String content = response.getContentAsString();

        assertTrue("Success Message", content.contains("alert('SOS Request Completed');"));
        assertTrue("Redirecting", content.contains("window.location.href = '/Admin/Dashboard';"));
    }
}
