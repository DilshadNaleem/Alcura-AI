package com.Alcura.Admin.Controller.Doctor;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import com.Alcura.Admin.Service.AdminAppointmentRescheduleEmailService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;

@Controller
@RequestMapping("/Admin")
public class ApproveDoctorPriceController
{
    @Autowired
    private DoctorPriceRepo doctorPriceRepo;
    Logger logger = LoggerFactory.getLogger(ApproveDoctorPriceController.class);
    private final AdminAppointmentRescheduleEmailService emailService;

    public ApproveDoctorPriceController(AdminAppointmentRescheduleEmailService emailService)
    {
        this.emailService = emailService;
    }



    @PostMapping("/ApproveDoctorPrice")
    public String approve(@RequestParam("id") int id,
                          @RequestParam("finalPrice") Float newPrice,
                          PrintWriter out,
                          HttpSession session)
    {
        try
        {
            String email = (String) session.getAttribute("email");
            if (email == null)
            {
                return "redirect:/Admin/Signing";
            }

            DoctorPrice doctorPrice = doctorPriceRepo.findById(id);
            doctorPrice.setStatus("Success");
            doctorPrice.setNewPrice(newPrice);
            doctorPriceRepo.save(doctorPrice);
            out.println("<script>");
            out.println("alert('Successfully Updated to Success');");
            out.println("window.location.href = '/Admin/Manage_Doctor/Price';");
            out.println("</script>");
            logger.info("Saved Data Id:{}", id);
            logger.info("Status updated to Success");

            String subject = "Your Price for per booking has been Approved";
            String body = String.format(
                    "Dear Doctor,\n\n" +
                            "Your Price Per Appointment has been Approved.\n\n" +
                            "Thank you, \n Alcura Team"
            );
            emailService.sendAppointmentStatusEmail(email,subject,body);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error: " , e.getMessage() );
        }

        return null;
    }
}
