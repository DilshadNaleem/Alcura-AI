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
public class RejectDoctorPrice
{

    Logger logger = LoggerFactory.getLogger(RejectDoctorPrice.class);
    @Autowired
    private DoctorPriceRepo doctorPriceRepo;

    @Autowired
    AdminAppointmentRescheduleEmailService emailService;

    @PostMapping("/RejectDoctorPrice")
    public String reject(@RequestParam("id") int id,
                         @RequestParam("newPrice") Float newPrice,
                         @RequestParam("adminNotes") String adminNotes,
                         HttpSession session,
                         PrintWriter out)
    {
        try
        {

            String email = (String) session.getAttribute("email");
            if (email == null)
            {
                return "redirect:/Admin/Signing";
            }

            logger.error("Recieved :{}", id, newPrice,adminNotes);
            DoctorPrice doctorPrice = doctorPriceRepo.findById(id);
            doctorPrice.setStatus("Rescheduled");
            doctorPrice.setAdminNotes(adminNotes);
            doctorPrice.setNewPrice(newPrice);
            doctorPriceRepo.save(doctorPrice);

            out.println("<script>");
            out.println("alert('Successfully Updated');");
            out.println("window.location.href = '/Doctor/Manage_Doctor/Price';");
            out.println("</script>");

            logger.info("Saving to Database: {}", newPrice, adminNotes);

            String subject = "Your Price Has been Rejected";
            String body = String.format(
                    "Dear Doctor,\n\n" +
                            "Your Price Range Rs. %s has been rejected.\n" +
                            "Due to Reason: %s \n"+
                            "We Are adjusted with this range Rs. %s.\n\n" +
                            "Thank you, \n Alcura Team",

                    doctorPrice.getPrice(),
                    adminNotes,
                    newPrice
            );

            logger.info("Set Price as {}", doctorPrice.getPrice());

            emailService.sendAppointmentStatusEmail(email, subject, body);
            logger.info("Email send to: {}", email);
        }
        catch (Exception e)
        {
            e.getMessage();
            e.printStackTrace();
            logger.error("Error: {}", e.getMessage());
        }
        return null;
    }
}
