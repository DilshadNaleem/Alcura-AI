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

        verify(doctorPriceRepo).findById(id);
        verify(doctorPriceRepo).save(any(DoctorPrice.class));

        String expectedSubject = "Your Price Has been Rejected";
        String heading = "Alcura Doctor Price Rejected";
        String expectedBody = String.format(
                "<div style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                        + "<div style='max-width: 600px; margin: auto; background-color: #ffffff; border: 1px solid #ddd; border-radius: 8px; padding: 20px;'>"
                        + "<h2 style='color: #C0392B;'>Alcura Price Rejection Notice</h2>"
                        + "<p style='font-size: 16px; color: #333;'>Dear Doctor,</p>"
                        + "<p style='font-size: 15px;'>We regret to inform you that your requested consultation price has been <strong style='color: red;'>rejected</strong>.</p>"

                        + "<table style='width: 100%%; margin: 20px 0; border-collapse: collapse;'>"
                        + "<tr>"
                        + "<td style='padding: 10px; background-color: #fce4e4; font-weight: bold;'>Requested Price:</td>"
                        + "<td style='padding: 10px;'>Rs. %s</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style='padding: 10px; background-color: #fce4e4; font-weight: bold;'>Adjusted Price:</td>"
                        + "<td style='padding: 10px;'>Rs. %s</td>"
                        + "</tr>"
                        + "</table>"

                        + "<div style='margin-top: 20px;'>"
                        + "<p style='font-weight: bold; color: #555;'>Admin Notes:</p>"
                        + "<div style='background-color: #fcf8e3; border-left: 4px solid #f0ad4e; padding: 10px; border-radius: 4px;'>"
                        + "%s"
                        + "</div>"
                        + "</div>"

                        + "<p style='margin-top: 30px; font-size: 14px; color: #888;'>Thank you,<br><strong>Alcura Team</strong></p>"
                        + "</div>"
                        + "</div>",
                originalPrice,
                newPrice,
                adminNotes
        );

        verify(emailService).sendAppointmentStatusEmail(eq(doctorEmail), eq(expectedSubject), eq(expectedBody), eq(heading));
    }
}