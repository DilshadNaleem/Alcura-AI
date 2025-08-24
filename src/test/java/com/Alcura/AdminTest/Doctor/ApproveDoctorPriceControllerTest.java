package com.Alcura.AdminTest.Doctor;

import com.Alcura.Admin.Controller.Doctor.ApproveDoctorPriceController;
import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import com.Alcura.Admin.Service.PriceApprovalNotifier;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.print.Doc;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;


@ExtendWith(MockitoExtension.class)
public class ApproveDoctorPriceControllerTest
{
 @Mock
 private DoctorPriceRepo doctorPriceRepo;

 @Mock
    private PriceApprovalNotifier emailService;

 @Mock
    private DoctorRepository doctorRepo;

 @InjectMocks
 private ApproveDoctorPriceController doctorPriceController;

 private String email = "test@gmail.com";
 private Float finalPrice = 0.5f;
 private String doctorEmail = "testdoctor@gmail.com";

 private MockHttpSession session;
 private MockHttpServletResponse response;
 private StringWriter stringWriter;
 private PrintWriter printWriter;

 @BeforeEach
    void setUp()
 {
     session = new MockHttpSession();
     response = new MockHttpServletResponse();
     stringWriter = new StringWriter();
     printWriter = new PrintWriter(stringWriter);
 }


 @Test
    void ApproveDoctorPrice_AdminEmailEmpty_ShouldShowError() throws IOException
 {
     int id = 1;

     String result = doctorPriceController.approve(id,finalPrice,doctorEmail,printWriter,session);

     assertNull("Session email should be null", session.getAttribute("email"));
     assertEquals("Show Error Message",result,"redirect:/Admin/Signing");

     verifyNoInteractions(emailService);
     verifyNoInteractions(doctorRepo);
     verifyNoInteractions(doctorPriceRepo);
 }


    @Test
    void ApproveDoctorPrice_EmptyDoctorEmail_ShouldReturnNull() throws IOException {

        session.setAttribute("email", email);

        when(doctorRepo.findByemail(doctorEmail)).thenReturn(null);
        int id = 1;

        String result = doctorPriceController.approve(id, finalPrice, doctorEmail, printWriter, session);

        assertNull("The controller should return null when a doctor is not found.", result);

        verify(doctorRepo).findByemail(doctorEmail);

        verify(doctorPriceRepo, never()).save(any());

        verify(emailService, never()).notifyObservers(any(), any(), any());
    }

    @Test
    void ApproveDoctorPrice_SuccessUpdate_SuccessMessage() throws IOException
    {
        session.setAttribute("email", email);

        Doctor mockdoctor = new Doctor();
        mockdoctor.setEmail(doctorEmail);

        DoctorPrice mockDoctorPrice = new DoctorPrice();
        mockDoctorPrice.setId(1);
        mockDoctorPrice.setDoctor_email("Pending");

        when(doctorRepo.findByemail(doctorEmail)).thenReturn(mockdoctor);
        when(doctorPriceRepo.findById(anyInt())).thenReturn(mockDoctorPrice);

        String result = doctorPriceController.approve(1,finalPrice,doctorEmail,printWriter,session);

        verify(doctorRepo).findByemail(doctorEmail);
        verify(doctorPriceRepo).findById(1);
        verify(doctorPriceRepo).save(mockDoctorPrice);
        verify(emailService).notifyObservers(eq(doctorEmail),anyString(),anyString());

        String output = stringWriter.toString();
        assertTrue("Show Success Message", output.contains("alert('Successfully Updated to Success');"));
        assertTrue("Redirecting", output.contains("window.location.href = '/Admin/Manage_Doctor/Price';"));

        assertNull("The controller should return null when writing to PrintWriter", result);
    }

}
