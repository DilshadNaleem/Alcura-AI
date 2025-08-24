package com.Alcura.AdminTest.Doctor;

import com.Alcura.Admin.Controller.Doctor.RejectDoctorPrice;
import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import com.Alcura.Admin.Service.AdminAppointmentRescheduleEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RejectDoctorPriceController {

    @Mock
    private DoctorPriceRepo doctorPriceRepo;

    @Mock
    private AdminAppointmentRescheduleEmailService emailService;

    @InjectMocks
    private RejectDoctorPrice rejectDoctorPrice;

    private MockHttpServletResponse response;
    private MockHttpSession session;
    private PrintWriter printWriter;

    private final int id = 1;
    private final Float newPrice = 100.0f;
    private final String adminNotes = "adminNotes";
    private final String adminEmail = "admin@gmail.com";
    private final String doctorEmail = "doctor@example.com";
    private final Float originalPrice = 150.0f;

    @BeforeEach
    void setUp() throws UnsupportedEncodingException {
        session = new MockHttpSession();
        response = new MockHttpServletResponse();
        printWriter = response.getWriter();
    }

    @Test
    void RejectDoctorPrice_NoSession_ShouldShowError() throws Exception {
        session.removeAttribute("email");

        String result = rejectDoctorPrice.reject(id, newPrice, adminNotes, session, printWriter);
        assertEquals("redirect:/Admin/Signing", result);

        verifyNoInteractions(doctorPriceRepo);
        verifyNoInteractions(emailService);
    }

    @Test
    void RejectDoctorPrice_ValidSession_ShouldProcessRequest() throws Exception {
        session.setAttribute("email", adminEmail);

        // Create a properly configured mock DoctorPrice
        DoctorPrice mockDoctorPrice = new DoctorPrice();
        mockDoctorPrice.setId(id);
        mockDoctorPrice.setPrice(originalPrice);
        mockDoctorPrice.setNewPrice(newPrice);
        mockDoctorPrice.setAdminNotes(adminNotes);
        mockDoctorPrice.setDoctor_email(doctorEmail);
        mockDoctorPrice.setStatus("Pending");

        when(doctorPriceRepo.findById(id)).thenReturn(mockDoctorPrice);
        when(doctorPriceRepo.save(any(DoctorPrice.class))).thenReturn(mockDoctorPrice);

        String result = rejectDoctorPrice.reject(id, newPrice, adminNotes, session, printWriter);

        assertEquals(null, result);

        // Verify interactions
        verify(doctorPriceRepo).findById(id);
        verify(doctorPriceRepo).save(any(DoctorPrice.class));

        // Verify email was sent with correct parameters
        String expectedSubject = "Your Price Has been Rejected";
        String expectedBody = String.format(
                "Dear Doctor,\n\n" +
                        "Your Price Range Rs. %s has been rejected.\n" +
                        "Due to Reason: %s \n"+
                        "We Are adjusted with this range Rs. %s.\n\n" +
                        "Thank you, \n Alcura Team",
                originalPrice, // Should be the original price, not newPrice
                adminNotes,
                newPrice
        );

        verify(emailService).sendAppointmentStatusEmail(eq(doctorEmail), eq(expectedSubject), eq(expectedBody));
    }
}