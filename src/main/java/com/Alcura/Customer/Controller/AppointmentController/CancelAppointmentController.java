package com.Alcura.Customer.Controller.AppointmentController;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Service.AppointmentCancelEmailService;
import com.Alcura.Customer.Repository.AppointmentRepo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/Customer")
public class CancelAppointmentController {
    private final Logger logger = LoggerFactory.getLogger(CancelAppointmentController.class);

    private AppointmentRepo appointmentRepo;

    private AppointmentCancelEmailService appointmentEmailService;
    public CancelAppointmentController(AppointmentRepo appointmentRepo,
                                       AppointmentCancelEmailService appointmentEmailService)
    {
        this.appointmentEmailService = appointmentEmailService;
        this.appointmentRepo = appointmentRepo;
    }
    @PostMapping("/CancelAppointment")
    public void Cancel(HttpSession session, Model model, HttpServletResponse response,
                       @RequestParam("appointmentId") String appointmentId,
                       @RequestParam(value = "cancelReason", required = false) String cancelReason) throws IOException {

        System.out.println("DEBUG: Received appointmentId = " + appointmentId);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();

        try {
            String email = (String) session.getAttribute("email");
            logger.info("Session Email in Cancel Appointment: {}", email);

            if (email == null) {
                writer.println("<script type='text/javascript'>");
                writer.println("alert('You need to login first!');");
                writer.println("window.location='/Customer/Signing';");
                writer.println("</script>");
                return;
            }

            // Find the appointment by unique ID
            Optional<Appoinment> optionalAppointment = appointmentRepo.findByUniqueIdNative(appointmentId);
            if (optionalAppointment.isEmpty()) {
                writer.println("<script type='text/javascript'>");
                writer.println("alert('Appointment not found!');");
                writer.println("window.location='/Customer/MyHistory';");
                writer.println("</script>");
                return;
            }

            Appoinment appointment = optionalAppointment.get();

            // Verify the appointment belongs to the logged-in user
            if (!appointment.getCustomer_email().equals(email)) {
                writer.println("<script type='text/javascript'>");
                writer.println("alert('You can only cancel your own appointments!');");
                writer.println("window.location='/Customer/MyHistory';");
                writer.println("</script>");
                return;
            }

            // Update appointment status to "Canceled"
            appointment.setStatus("Canceled");
            appointment.setCancel_reason(cancelReason);
            if (cancelReason != null && !cancelReason.isEmpty()) {
                appointment.setSpecial_reasons(cancelReason);
            }
            appointmentRepo.save(appointment);

            // Send cancellation email
            LocalDate appointmentDate = appointment.getAppointment_date();
            String formattedDate = appointmentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

            appointmentEmailService.sendAppointmentCancellation(
                    appointment.getCustomer_email(),
                    formattedDate,
                    appointment.getDoctor_name(),
                    appointment.getUnique_id(),
                    appointment.getAppointment_time()
            );

            writer.println("<script type='text/javascript'>");
            writer.println("alert('Appointment cancelled successfully!');");
            writer.println("window.location='/Customer/MyHistory';");
            writer.println("</script>");

        } catch (Exception e) {
            logger.error("Error cancelling appointment: ", e);
            writer.println("<script type='text/javascript'>");
            writer.println("alert('Error cancelling appointment. Please try again.');");
            writer.println("window.location='/Customer/MyHistory';");
            writer.println("</script>");
        }
    }
}