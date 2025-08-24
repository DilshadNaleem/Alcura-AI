package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.AppointmentController.CancelAppointmentController;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepo;
import com.Alcura.Customer.Service.AppointmentCancelEmailService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CancelAppointmentControllerTest
{
    @Mock
    private AppointmentRepo appointmentRepo;

    @Mock
    private AppointmentCancelEmailService appointmentCancelEmailService;

    @InjectMocks
    private CancelAppointmentController cancelAppointmentController;

    private MockHttpServletResponse response;
    private MockHttpSession session;

    @BeforeEach
    void setUp()
    {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
    }

    @Test
    void SendCancelAppointment_WhenNotLoggedIn_ShouldShowError() throws IOException
    {
        session.clearAttributes();
        Model model = mock(Model.class);
        String canceltId =  "1234";
        String notes = "Testing Cancel";

        cancelAppointmentController.Cancel(session,model,response,canceltId, notes);
        String content = response.getContentAsString();
        assertTrue(content.contains("alert('You need to login first!');"));
        assertTrue(content.contains("window.location='/Customer/Signing';"));
    }

    @Test
    void SendCancelAppointment_ShouldShowSuccessMessage() throws IOException
    {
        String cancelId = "cancelId";
        String cancelNotes =  "Cancel";
        String email = "test@gmail.com";
        session.setAttribute("email", email);
        Model model = mock(Model.class);

        Appoinment mockAppointment = new Appoinment();
        mockAppointment.setUnique_id(cancelId);
        mockAppointment.setCancel_reason(cancelNotes);
        mockAppointment.setCustomer_email(email);
        mockAppointment.setAppointment_date(LocalDate.now());
        mockAppointment.setAppointment_time("10:00");
        mockAppointment.setDoctor_name("Dr Smith");

        when(appointmentRepo.findByUniqueIdNative(cancelId)).thenReturn(Optional.of(mockAppointment));
        when(appointmentRepo.save(any(Appoinment.class))).thenReturn(mockAppointment);

        doNothing().when(appointmentCancelEmailService).sendAppointmentCancellation(
                anyString(),
                anyString(),
                any(String.class),
                anyString(),
                anyString());

        cancelAppointmentController.Cancel(session,model,response,cancelId,cancelNotes);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Appointment cancelled successfully!');"));
        assertTrue(content.contains("window.location='/Customer/MyHistory';"));
    }

    @Test
    void SendCancelRequest_AppointmentNotFound_ShouldShowError() throws IOException
    {
        String appointmentId =  "appointment_id";
        String customerEmail = "test@gmail.com";
        session.setAttribute("email", customerEmail);
        Model model = mock(Model.class);
        String notes = "Cancel";

        when(appointmentRepo.findByUniqueIdNative(appointmentId)).thenReturn(Optional.empty());
        cancelAppointmentController.Cancel(session,model,response,appointmentId,notes);

        String content = response.getContentAsString();
        assertTrue(content.contains("alert('Appointment not found!');"));
        assertTrue(content.contains("window.location='/Customer/MyHistory';"));

        verify(appointmentRepo,never()).save(any());
        verify(appointmentCancelEmailService, never()).sendAppointmentCancellation(anyString(),anyString(),anyString(),anyString(),anyString());
    }
}
