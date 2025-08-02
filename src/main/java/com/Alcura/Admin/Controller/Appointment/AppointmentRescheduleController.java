package com.Alcura.Admin.Controller.Appointment;

import com.Alcura.Admin.Service.AdminAppointmentRescheduleEmailService;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/Admin/Appointment")
public class AppointmentRescheduleController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentRescheduleController.class);
    private final AppointmentRepo appointmentRepo;
    private final AdminAppointmentRescheduleEmailService emailService;

    @Autowired
    public AppointmentRescheduleController(AppointmentRepo appointmentRepo,
                                           AdminAppointmentRescheduleEmailService emailService) {
        this.appointmentRepo = appointmentRepo;
        this.emailService = emailService;
    }

    @PostMapping("/Reschedule")
    public ResponseEntity<?> handleReschedule(@RequestBody RescheduleRequest request) {
        try {
            logger.info("Received reschedule request: {}", request);

            Optional<Appoinment> appointmentOpt = appointmentRepo.findByUniqueIdNative(request.getAppointmentId());
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Appointment not found");
            }

            Appoinment appointment = appointmentOpt.get();

            if ("approve".equalsIgnoreCase(request.getAction())) {
                return handleApproval(request, appointment);
            } else if ("reject".equalsIgnoreCase(request.getAction())) {
                return handleRejection(request, appointment);
            } else {
                return ResponseEntity.badRequest().body("Invalid action specified");
            }

        } catch (Exception e) {
            logger.error("Error processing reschedule request", e);
            return ResponseEntity.internalServerError().body("Error processing request: " + e.getMessage());
        }
    }

    private ResponseEntity<?> handleApproval(RescheduleRequest request, Appoinment appointment) {
        if (request.getNewDate() == null || request.getNewTime() == null) {
            return ResponseEntity.badRequest().body("New date and time are required for approval");
        }

        // Update appointment
        appointment.setStatus("Rescheduled");
        appointment.setAppointment_date(request.getNewDate());
        appointment.setAppointment_time(request.getNewTime());
        appointment.setAdminNotes(request.getAdminNotes());
        appointment.setReschedule_reason("Reschedule approved by admin");
        appointmentRepo.save(appointment);

        // Send email notification
        String subject = "Your Appointment Has Been Rescheduled";
        String body = String.format(
                "Dear Patient,\n\n" +
                        "Your appointment with Dr. %s has been rescheduled.\n\n" +
                        "New Date: %s\n" +
                        "New Time: %s\n\n" +
                        "Admin Notes: %s\n\n" +
                        "Thank you,\nAlcura Team",
                appointment.getDoctor_name(),
                request.getNewDate(),
                request.getNewTime(),
                request.getAdminNotes()
        );
        logger.info("Email send {}", appointment.getCustomer_email());
        emailService.sendAppointmentStatusEmail(
                appointment.getCustomer_email(),
                subject,
                body
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Appointment rescheduled successfully"
        ));
    }

    private ResponseEntity<?> handleRejection(RescheduleRequest request, Appoinment appointment) {
        appointment.setStatus("Rejected");
        appointment.setAdminNotes(request.getAdminNotes());
        appointment.setReschedule_reason("Reschedule request rejected by admin");
        appointmentRepo.save(appointment);

        // Send email notification
        String subject = "Your Reschedule Request Has Been Rejected";
        String body = String.format(
                "Dear Patient,\n\n" +
                        "Your reschedule request for appointment with Dr. %s has been rejected.\n\n" +
                        "Original Date: %s\n" +
                        "Original Time: %s\n\n" +
                        "Reason: %s\n\n" +
                        "Thank you,\nAlcura Team",
                appointment.getDoctor_name(),
                appointment.getAppointment_date(),
                appointment.getAppointment_time(),
                request.getAdminNotes()
        );


        logger.info("Email send {}", appointment.getCustomer_email());
        emailService.sendAppointmentStatusEmail(
                appointment.getCustomer_email(),
                subject,
                body
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Reschedule request rejected"
        ));
    }

    // RescheduleRequest inner class remains the same
    public static class RescheduleRequest {
        private String appointmentId;
        private String action;
        private LocalDate newDate;
        private String newTime;
        private String adminNotes;

        // Getters and Setters
        public String getAppointmentId() { return appointmentId; }
        public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public LocalDate getNewDate() { return newDate; }
        public void setNewDate(LocalDate newDate) { this.newDate = newDate; }
        public String getNewTime() { return newTime; }
        public void setNewTime(String newTime) { this.newTime = newTime; }
        public String getAdminNotes() { return adminNotes; }
        public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

        @Override
        public String toString() {
            return "RescheduleRequest{" +
                    "appointmentId='" + appointmentId + '\'' +
                    ", action='" + action + '\'' +
                    ", newDate=" + newDate +
                    ", newTime='" + newTime + '\'' +
                    ", adminNotes='" + adminNotes + '\'' +
                    '}';
        }
    }
}