package com.Alcura.CustomerTest;

import com.Alcura.Customer.Controller.AppointmentController.AppointmentController;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Model.Payment;
import com.Alcura.Customer.Repository.AppointmentRepository;
import com.Alcura.Customer.Repository.PaymentRepo;
import com.Alcura.Customer.Service.DoctorAvailabilityService;
import com.Alcura.Customer.Service.Interfaces.AppoinmentService;
import com.Alcura.Customer.Service.PaymentService;
import com.Alcura.Customer.Service.PaymentUniqueId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentControllerTest {

    @Mock
    private AppoinmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private PaymentService paymentService;

    @Mock
    private DoctorAvailabilityService doctorAvailabilityService;

    @Mock
    private PaymentUniqueId paymentUniqueId;

    @InjectMocks
    private AppointmentController appointmentController;

    private MockHttpSession session;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        // Reset all mocks before each test
        reset(appointmentService, appointmentRepository, paymentRepo,
                paymentService, doctorAvailabilityService, paymentUniqueId);
    }

    @Test
    void createAppointment_WhenNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        session.clearAttributes();

        appointmentController.createAppointment(
                "doc1", "Dr. Smith", LocalDate.now(), "10:00",
                null, 100.0f, "CARD", session, response);

        String result = response.getContentAsString();
        assertTrue(result.contains("You need to login first!"));
        assertTrue(result.contains("/Customer/Signing"));
    }

    @Test
    void createAppointment_WhenSlotBooked_ShouldShowError() throws Exception {
        session.setAttribute("email", "patient@example.com");
        when(appointmentRepository.existsByDoctorAndDateAndTime(any(), any(), any())).thenReturn(true);

        appointmentController.createAppointment(
                "doc1", "Dr. Smith", LocalDate.now(), "10:00",
                null, 100.0f, "CARD", session, response);

        String result = response.getContentAsString();
        assertTrue(result.contains("This time slot is already booked!"));
        assertTrue(result.contains("/Customer/AppointmentBooking"));
    }

    @Test
    void createAppointment_WhenValid_ShouldCreateAppointment() throws Exception {
        // Setup session
        session.setAttribute("email", "patient@example.com");

        // Mock repository behavior
        when(appointmentRepository.existsByDoctorAndDateAndTime(any(), any(), any())).thenReturn(false);

        // Mock appointment creation
        Appoinment mockAppointment = new Appoinment();
        mockAppointment.setUnique_id("app123");
        when(appointmentService.createAppointment(
                any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(mockAppointment);

        // Mock payment creation
        Payment mockPayment = new Payment();
        mockPayment.setUniqueId("pay123");
        when(paymentUniqueId.createPayment(any())).thenReturn(mockPayment);
       when(paymentService.processPayment(any(Payment.class), anyString())).thenReturn(mockPayment);
        appointmentController.createAppointment(
                "doc1", "Dr. Smith", LocalDate.now(), "10:00",
                "Special reason", 100.0f, "CARD", session, response);

        // Verify response
        String result = response.getContentAsString();
        assertTrue(result.contains("Appointment created successfully!"));
        assertTrue(result.contains("/Customer/AppointmentBooking"));

        // Verify interactions
        verify(paymentService).processPayment(any(Payment.class), eq("CARD"));
        verify(paymentRepo).save(any(Payment.class));
        verify(appointmentRepository).save(any(Appoinment.class));
    }

    @Test
    void createAppointment_WhenException_ShouldShowError() throws Exception {
        session.setAttribute("email", "patient@example.com");
        when(appointmentRepository.existsByDoctorAndDateAndTime(any(), any(), any())).thenReturn(false);
        when(appointmentService.createAppointment(
                any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Test exception"));

        appointmentController.createAppointment(
                "doc1", "Dr. Smith", LocalDate.now(), "10:00",
                null, 100.0f, "CARD", session, response);

        String result = response.getContentAsString();
        assertTrue(result.contains("Error creating appointment"));
        assertTrue(result.contains("Test exception"));
    }
}