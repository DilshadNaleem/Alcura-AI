package com.Alcura.DoctorTest;

import com.Alcura.Admin.Service.AdminAppointmentRescheduleEmailService;
import com.Alcura.Admin.Service.AppointmentService;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepo;
import com.Alcura.Doctor.Controller.AppointmentCompletedController;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.ui.Model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorCompleteAppointmentTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AdminAppointmentRescheduleEmailService emailService;

    @Mock
    private AppointmentRepo appointmentRepo;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private Logger logger;

    @InjectMocks
    private AppointmentCompletedController controller;

    private Doctor testDoctor;
    private Appoinment testAppointment;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setEmail("doctor@example.com");
        testDoctor.setUniqueId("DOC123");

        testAppointment = new Appoinment();
        testAppointment.setUnique_id("APT456");
        testAppointment.setStatus("Scheduled");
        testAppointment.setCustomer_email("patient@example.com");
        testAppointment.setDoctor_name("Dr. Smith");

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }


    @Test
    void testForm_UserLoggedIn() {
        when(session.getAttribute("email")).thenReturn("doctor@example.com");
        when(doctorRepository.findByemail("doctor@example.com")).thenReturn(testDoctor);

        List<Appoinment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getAllAppointmentsForDoctor("DOC123")).thenReturn(appointments);

        String result = controller.form(model, session);

        assertEquals("/Doctor/ManageAppointment", result);
        verify(model).addAttribute("app", appointments);
        verify(session).getAttribute("email");
        verify(doctorRepository).findByemail("doctor@example.com");
        verify(appointmentService).getAllAppointmentsForDoctor("DOC123");
    }

    @Test
    void testUpdateAppointment_Success() throws Exception {
        when(appointmentRepo.findByUnique_id("APT456")).thenReturn(testAppointment);

        String result = controller.update("APT456", printWriter);
        printWriter.flush();

        assertNull(result);
        assertEquals("Completed", testAppointment.getStatus());
        verify(appointmentRepo).save(testAppointment);
        verify(emailService).sendAppointmentStatusEmail(
                eq("patient@example.com"),
                eq("Thanks for the Booking"),
                anyString()
        );
        assertTrue(stringWriter.toString().contains("alert('Successfully Updated!')"));
    }



    @Test
    void testUpdateAppointment_EmptyId() throws Exception {
        String result = controller.update("", printWriter);
        printWriter.flush();

        assertNull(result);
        assertTrue(stringWriter.toString().contains("alert('Id is Null')"));
        verify(appointmentRepo, never()).save(any());
        verify(emailService, never()).sendAppointmentStatusEmail(any(), any(), any());
    }



    @Test
    void testUpdateAppointment_NullCustomerEmail() throws Exception {
        testAppointment.setCustomer_email(null);
        when(appointmentRepo.findByUnique_id("APT456")).thenReturn(testAppointment);

        String result = controller.update("APT456", printWriter);
        printWriter.flush();

        assertNull(result);
        assertEquals("Completed", testAppointment.getStatus());
        verify(appointmentRepo).save(testAppointment);
        assertTrue(stringWriter.toString().contains("alert('Successfully Updated!')"));
    }
}