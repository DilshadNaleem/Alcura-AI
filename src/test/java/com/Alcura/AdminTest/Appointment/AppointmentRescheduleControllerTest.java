package com.Alcura.AdminTest.Appointment;

import com.Alcura.Admin.Controller.Appointment.AppointmentRescheduleController;
import com.Alcura.Admin.Service.AdminAppointmentRescheduleEmailService;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentRescheduleControllerTest {

    @Mock
    private AppointmentRepo appointmentRepo;

    @Mock
    private AdminAppointmentRescheduleEmailService emailService;

    @InjectMocks
    private AppointmentRescheduleController controller;

    private AppointmentRescheduleController.RescheduleRequest validApproveRequest;
    private AppointmentRescheduleController.RescheduleRequest validRejectRequest;
    private AppointmentRescheduleController.RescheduleRequest invalidActionRequest;
    private AppointmentRescheduleController.RescheduleRequest missingDateApproveRequest;
    private Appoinment mockAppointment;

    @BeforeEach
    void setUp() {
        validApproveRequest = new AppointmentRescheduleController.RescheduleRequest();
        validApproveRequest.setAppointmentId("APPT-123");
        validApproveRequest.setAction("approve");
        validApproveRequest.setNewDate(LocalDate.of(2024, 1, 15));
        validApproveRequest.setNewTime("14:30");
        validApproveRequest.setAdminNotes("Approved with new timing");

        validRejectRequest = new AppointmentRescheduleController.RescheduleRequest();
        validRejectRequest.setAppointmentId("APPT-123");
        validRejectRequest.setAction("reject");
        validRejectRequest.setAdminNotes("Cannot accommodate requested time");

        invalidActionRequest = new AppointmentRescheduleController.RescheduleRequest();
        invalidActionRequest.setAppointmentId("APPT-123");
        invalidActionRequest.setAction("invalid");

        missingDateApproveRequest = new AppointmentRescheduleController.RescheduleRequest();
        missingDateApproveRequest.setAppointmentId("APPT-123");
        missingDateApproveRequest.setAction("approve");
        missingDateApproveRequest.setAdminNotes("Missing date and time");

        mockAppointment = new Appoinment();
        mockAppointment.setUnique_id("APPT-123");
        mockAppointment.setDoctor_name("Dr. Smith");
        mockAppointment.setAppointment_date(LocalDate.of(2024, 1, 10));
        mockAppointment.setAppointment_time("10:00");
        mockAppointment.setCustomer_email("patient@example.com");
        mockAppointment.setStatus("Pending");
    }

    @Test
    void handleReschedule_AppointmentNotFound_ShouldReturnBadRequest() {

        when(appointmentRepo.findByUniqueIdNative("APPT-123")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.handleReschedule(validApproveRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Appointment not found", response.getBody());
        verify(appointmentRepo).findByUniqueIdNative("APPT-123");
        verifyNoMoreInteractions(appointmentRepo);
        verifyNoInteractions(emailService);
    }

    @Test
    void handleReschedule_ApproveAction_ShouldRescheduleAppointmentAndSendEmail() {
        when(appointmentRepo.findByUniqueIdNative("APPT-123")).thenReturn(Optional.of(mockAppointment));
        when(appointmentRepo.save(any(Appoinment.class))).thenReturn(mockAppointment);
        ResponseEntity<?> response = controller.handleReschedule(validApproveRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("success", responseBody.get("status"));
        assertEquals("Appointment rescheduled successfully", responseBody.get("message"));

        verify(appointmentRepo).save(mockAppointment);
        assertEquals("Rescheduled", mockAppointment.getStatus());
        assertEquals(LocalDate.of(2024, 1, 15), mockAppointment.getAppointment_date());
        assertEquals("14:30", mockAppointment.getAppointment_time());
        assertEquals("Approved with new timing", mockAppointment.getAdminNotes());
        assertEquals("Reschedule approved by admin", mockAppointment.getReschedule_reason());

        String expectedSubject = "Your Appointment Has Been Rescheduled";
        String expectedBody = String.format(
                "Dear Patient,\n\n" +
                        "Your appointment with Dr. %s has been rescheduled.\n\n" +
                        "New Date: %s\n" +
                        "New Time: %s\n\n" +
                        "Admin Notes: %s\n\n" +
                        "Thank you,\nAlcura Team",
                "Dr. Smith",
                LocalDate.of(2024, 1, 15),
                "14:30",
                "Approved with new timing"
        );
        verify(emailService).sendAppointmentStatusEmail(
                eq("patient@example.com"),
                eq(expectedSubject),
                eq(expectedBody)
        );
    }

    @Test
    void handleReschedule_ApproveActionMissingDate_ShouldReturnBadRequest() {

        when(appointmentRepo.findByUniqueIdNative("APPT-123")).thenReturn(Optional.of(mockAppointment));

        ResponseEntity<?> response = controller.handleReschedule(missingDateApproveRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("New date and time are required for approval", response.getBody());
        verify(appointmentRepo).findByUniqueIdNative("APPT-123");
        verifyNoMoreInteractions(appointmentRepo);
        verifyNoInteractions(emailService);
    }

    @Test
    void handleReschedule_RejectAction_ShouldRejectAppointmentAndSendEmail() {
        when(appointmentRepo.findByUniqueIdNative("APPT-123")).thenReturn(Optional.of(mockAppointment));
        when(appointmentRepo.save(any(Appoinment.class))).thenReturn(mockAppointment);
        ResponseEntity<?> response = controller.handleReschedule(validRejectRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("success", responseBody.get("status"));
        assertEquals("Reschedule request rejected", responseBody.get("message"));

        verify(appointmentRepo).save(mockAppointment);
        assertEquals("Rejected", mockAppointment.getStatus());
        assertEquals("Cannot accommodate requested time", mockAppointment.getAdminNotes());
        assertEquals("Reschedule request rejected by admin", mockAppointment.getReschedule_reason());

        String expectedSubject = "Your Reschedule Request Has Been Rejected";
        String expectedBody = String.format(
                "Dear Patient,\n\n" +
                        "Your reschedule request for appointment with Dr. %s has been rejected.\n\n" +
                        "Original Date: %s\n" +
                        "Original Time: %s\n\n" +
                        "Reason: %s\n\n" +
                        "Thank you,\nAlcura Team",
                "Dr. Smith",
                LocalDate.of(2024, 1, 10),
                "10:00",
                "Cannot accommodate requested time"
        );
        verify(emailService).sendAppointmentStatusEmail(
                eq("patient@example.com"),
                eq(expectedSubject),
                eq(expectedBody)
        );
    }

    @Test
    void handleReschedule_InvalidAction_ShouldReturnBadRequest() {

        when(appointmentRepo.findByUniqueIdNative("APPT-123")).thenReturn(Optional.of(mockAppointment));


        ResponseEntity<?> response = controller.handleReschedule(invalidActionRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid action specified", response.getBody());
        verify(appointmentRepo).findByUniqueIdNative("APPT-123");
        verifyNoMoreInteractions(appointmentRepo);
        verifyNoInteractions(emailService);
    }

    @Test
    void handleReschedule_ExceptionDuringProcessing_ShouldReturnInternalServerError() {

        when(appointmentRepo.findByUniqueIdNative("APPT-123")).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = controller.handleReschedule(validApproveRequest);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error processing request: Database error"));
        verify(appointmentRepo).findByUniqueIdNative("APPT-123");
        verifyNoMoreInteractions(appointmentRepo);
        verifyNoInteractions(emailService);
    }

    @Test
    void rescheduleRequest_GettersAndSetters_ShouldWorkCorrectly() {
        AppointmentRescheduleController.RescheduleRequest request = new AppointmentRescheduleController.RescheduleRequest();
        LocalDate testDate = LocalDate.of(2024, 1, 20);

        request.setAppointmentId("TEST-123");
        request.setAction("test");
        request.setNewDate(testDate);
        request.setNewTime("15:00");
        request.setAdminNotes("Test notes");

        assertEquals("TEST-123", request.getAppointmentId());
        assertEquals("test", request.getAction());
        assertEquals(testDate, request.getNewDate());
        assertEquals("15:00", request.getNewTime());
        assertEquals("Test notes", request.getAdminNotes());
    }

    @Test
    void rescheduleRequest_ToString_ShouldContainAllFields() {
        // Arrange
        AppointmentRescheduleController.RescheduleRequest request = new AppointmentRescheduleController.RescheduleRequest();
        request.setAppointmentId("TEST-123");
        request.setAction("approve");
        request.setNewDate(LocalDate.of(2024, 1, 20));
        request.setNewTime("15:00");
        request.setAdminNotes("Test notes");

        // Act
        String toStringResult = request.toString();

        // Assert
        assertTrue(toStringResult.contains("TEST-123"));
        assertTrue(toStringResult.contains("approve"));
        assertTrue(toStringResult.contains("2024-01-20"));
        assertTrue(toStringResult.contains("15:00"));
        assertTrue(toStringResult.contains("Test notes"));
    }
}