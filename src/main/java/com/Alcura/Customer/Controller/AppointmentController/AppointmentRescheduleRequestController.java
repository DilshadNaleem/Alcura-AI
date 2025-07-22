package com.Alcura.Customer.Controller.AppointmentController;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Service.AppointmentRescheduleEmailService;
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
public class AppointmentRescheduleRequestController
{
    @Autowired
    private final AppointmentRescheduleEmailService appointmentRescheduleEmailService;
    private final Logger logger = LoggerFactory.getLogger(AppointmentRescheduleRequestController.class);
    @Autowired
    private AppointmentRepo appointmentRepo;

    public AppointmentRescheduleRequestController(AppointmentRescheduleEmailService appointmentRescheduleEmailService)
    {
        this.appointmentRescheduleEmailService = appointmentRescheduleEmailService;
    }

    @PostMapping("/AppointmentReSchedule")
    public void Reschedule(HttpSession session, Model model, HttpServletResponse response,
                           @RequestParam("Id") String appointmentId,
                           @RequestParam("notes") String notes) throws IOException {
        System.out.println("DEBUG");
        System.out.println("Appoitnment Id = " + appointmentId);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();

        try {
            String email = (String) session.getAttribute("email");
            logger.info("Recieved Reschedule Email {}", email);

            if (email == null) {
                writer.println("<script>");
                writer.println("alert('Login in First')");
                writer.println("window.location='/Customer/Signing';");
                writer.println("</script>");
                return;
            }

            Optional<Appoinment> optionalAppointment = appointmentRepo.findByUniqueIdNative(appointmentId);
            if (optionalAppointment.isEmpty()) {
                writer.println("<script type='text/javascript'>");
                writer.println("alert('Appointment not found!');");
                writer.println("window.location='/Customer/MyHistory';");
                writer.println("</script>");
                return;
            }

            Appoinment appoinment = optionalAppointment.get();

            if (!appoinment.getCustomer_email().equals(email)) {
                writer.println("<script type='text/javascript'>");
                writer.println("alert('You can only cancel your own appointments!');");
                writer.println("window.location='/Customer/MyHistory';");
                writer.println("</script>");
                return;
            }

            appoinment.setStatus("Rescheduled");
            appoinment.setReschedule_reason(notes);
            appointmentRepo.save(appoinment);

            LocalDate apppointmentDate = appoinment.getAppointment_date();
            String newDate = apppointmentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

            appointmentRescheduleEmailService.sendEmail(
                    appoinment.getCustomer_email(),
                    newDate,
                    appoinment.getAppointment_time(),
                    appoinment.getUnique_id(),
                    appoinment.getDoctor_name(),
                    notes
            );

            writer.println("<script type='text/javascript'>");
            writer.println("alert('Appointment Rescheduled Successful!');");
            writer.println("window.location='/Customer/MyHistory';");
            writer.println("</script>");
        } catch (Exception e) {
            logger.error("Error Rescheduling appointment: ", e);
            writer.println("<script type='text/javascript'>");
            writer.println("alert('Error Rescheduling appointment. Please try again.');");
            writer.println("window.location='/Customer/MyHistory';");
            writer.println("</script>");
        }
    }
}
