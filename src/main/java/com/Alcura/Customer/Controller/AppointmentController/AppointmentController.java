package com.Alcura.Customer.Controller.AppointmentController;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Service.Interfaces.AppoinmentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

@RestController

public class AppointmentController {
    private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppoinmentService appointmentService;


    public AppointmentController(AppoinmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/Customer/Appointment")
    public void createAppointment(
            @RequestParam("doctorId") String doctorId,
            @RequestParam("doctorName") String doctorName,
            @RequestParam("appointment_date") LocalDate date,
            @RequestParam("doctorAvailability") String time,
            @RequestParam(value = "specialReasons", required = false) String specialReason,
            HttpSession session,
            HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();

        try {
            String email = (String) session.getAttribute("email");
            logger.info("Sessioned Email: {}", email);
            logger.info("Doctor name: {} {}", doctorName, doctorId);

            if (email == null) {
                writer.println("<script type='text/javascript'>");
                writer.println("alert('You need to login first!');");
                writer.println("window.location='/Customer/Signing';");
                writer.println("</script>");
                return;
            }

            Appoinment appointment = appointmentService.createAppointment(
                    doctorId,
                    doctorName,
                    time,
                    date,
                    specialReason,
                    email
            );

            writer.println("<script type='text/javascript'>");
            writer.println("alert('Appointment created successfully!');");
            writer.println("window.location='/Customer/AppointmentBooking';"); // Redirect to success page
            writer.println("</script>");

        } catch (Exception e) {
            logger.error("Error creating appointment: ", e);
            writer.println("<script type='text/javascript'>");
            writer.println("alert('Error creating appointment: " + e.getMessage().replace("'", "\\'") + "');");
            writer.println("window.location='/Customer/AppointmentBooking';"); // Redirect back to appointment page
            writer.println("</script>");
        } finally {
            writer.close();
        }
    }
}