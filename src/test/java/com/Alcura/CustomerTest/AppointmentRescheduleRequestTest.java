package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.AppointmentController.AppointmentRescheduleRequestController;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepo;
import com.Alcura.Customer.Service.AppointmentRescheduleEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentRescheduleRequestTest {
    @Mock
    private AppointmentRepo appointmentRepo;

    @Mock
    private AppointmentRescheduleEmailService rescheduleEmailService;

    @InjectMocks
    private AppointmentRescheduleRequestController rescheduleRequestController;

    private MockHttpServletResponse response;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
    }

    @Test
    void SendRescheduleRequest_WhenNotLoggedIn_ShouldShowError() throws IOException {
        session.clearAttributes();
        Model model = mock(Model.class);
        String appointmentId = "12345";
        String notes = "Testing Reschedule";

        rescheduleRequestController.Reschedule(session, model, response, appointmentId, notes);
        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Login in First')"));
        assertTrue(content.contains("window.location='/Customer/Signing';"));
    }

    @Test
    void SendRescheduleRequest_ShouldShowSuccessMessage() throws IOException {
        String appointmentId = "app01";
        String notes = "Testing reschedule for success case";
        String customerEmail = "testuser@example.com";
        session.setAttribute("email", customerEmail);
        Model model = mock(Model.class);
        Appoinment mockAppointment = new Appoinment();
        mockAppointment.setUnique_id(appointmentId);
        mockAppointment.setCustomer_email(customerEmail);
        mockAppointment.setAppointment_date(LocalDate.now());
        mockAppointment.setAppointment_time("10:00");
        mockAppointment.setDoctor_name("Dr. Smith");
        mockAppointment.setStatus("Scheduled");

        when(appointmentRepo.findByUniqueIdNative(appointmentId))
                .thenReturn(Optional.of(mockAppointment));
        when(appointmentRepo.save(any(Appoinment.class))).thenReturn(mockAppointment);
        doNothing().when(rescheduleEmailService).sendEmail(anyString(), anyString(), any(), anyString(), anyString(), anyString());
        rescheduleRequestController.Reschedule(session, model, response, appointmentId, notes);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Appointment Rescheduled Successful!');"));
        assertTrue(content.contains("window.location='/Customer/MyHistory';"));
    }

    @Test
    void SendRescheduleRequest_AppointmentNotFound_ShowError() throws IOException
    {
        String appointmentId = "nonexistent_id";
        String customerEmail = "testuser@gmail.com";
        session.setAttribute("email", customerEmail);
        Model model = mock(Model.class);
        String notes = "Test notes";

        when(appointmentRepo.findByUniqueIdNative(appointmentId)).thenReturn(Optional.empty());

        rescheduleRequestController.Reschedule(session,model,response,appointmentId,notes);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Appointment not found!');"));
        assertTrue(content.contains("window.location='/Customer/MyHistory';"));

        verify(appointmentRepo,never()).save(any());
        verify(rescheduleEmailService,never()).sendEmail(anyString(),anyString(),any(),anyString(),anyString(),anyString());
    }
}