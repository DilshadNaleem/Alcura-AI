package com.Alcura.Doctor.Controller;

import com.Alcura.Admin.Service.AdminAppointmentRescheduleEmailService;
import com.Alcura.Admin.Service.AppointmentService;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepo;
import com.Alcura.Customer.Service.AppointmentEmailServiceImpl;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/Doctor")
public class AppointmentCompletedController
{
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AdminAppointmentRescheduleEmailService emailService;

    @Autowired
    AppointmentRepo appointmentRepo;
    @Autowired
    private DoctorRepository doctorRepository;

    Logger logger = LoggerFactory.getLogger(AppointmentCompletedController.class);

    @GetMapping("/ManageAppointment")
    public String form(Model model,
                       HttpSession session)
    {
        String email = (String)  session.getAttribute("email");
        if (email == null)
        {
            return "/Doctor/Signing";
        }

        Doctor doctor = doctorRepository.findByemail(email);
        String uniqueId = doctor.getUniqueId();
        logger.info("ID: {}", uniqueId);

        List<Appoinment> appoinments = appointmentService.getAllAppointmentsForDoctor(uniqueId);
        logger.info("Database queries {}", appoinments.size());
        model.addAttribute("app", appoinments);
        return "/Doctor/ManageAppointment";
    }


    @PostMapping("/CompleteAppointment/{id}")
    public String update (@PathVariable("id") String id, PrintWriter writer)
    {
        try
        {
            logger.info("Recieved: {}", id);
            if (id.isEmpty() || id == null)
            {
                writer.println("<script>");
                writer.println("alert('Id is Null');");
                writer.println("window.location.href = '/Doctor/ManageAppointment';");
                writer.println("</script>");
                logger.error("Id is null");
            }

            Appoinment appoinment = appointmentRepo.findByUnique_id(id);
            if (appoinment == null)
            {
                logger.error("Appontment is null");
                Exception RuntimeException = null;
                throw RuntimeException;
            }
            appoinment.setStatus("Completed");
            String customerEmail = appoinment.getCustomer_email();
            if (customerEmail == null)
            {
                logger.error("customer email is null");
            }

            String subject = "Thanks for the Booking";
            String body = String.format(
                    "Dear Patient, \n\n" +
                            "Thanks for the Appointment with: %s\n" +
                            "If You have Any Concerns Please Contact us via Alcura official website" +
                            "Thank You, \n Alcura Team",
                    appoinment.getDoctor_name()
            );
            emailService.sendAppointmentStatusEmail(customerEmail,subject,body);
            logger.error("Email Sent To:{}", customerEmail);
            appointmentRepo.save(appoinment);

            writer.println("<script>");
            writer.println("alert('Successfully Updated!');");
            writer.println("window.location.href ='/Doctor/ManageAppointment';");
            writer.println("</script>");


            logger.info("Saved to Database");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
