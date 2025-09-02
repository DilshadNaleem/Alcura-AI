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
            doctorPrice.setDoctor_email(doctorPrice.getDoctor_email());
            String doctorEmail = doctorPrice.getDoctor_email();
            doctorPriceRepo.save(doctorPrice);

            out.println("<script>");
            out.println("alert('Successfully Updated');");
            out.println("window.location.href = '/Admin/Manage_Doctor/Price';");
            out.println("</script>");

            logger.info("Saving to Database: {}", newPrice, adminNotes);

            String subject = "Your Price Has been Rejected";
            String heading = "Alcura Doctor Price Rejected";
            String body = String.format(
                    "<div style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                            + "<div style='max-width: 600px; margin: auto; background-color: #ffffff; border: 1px solid #ddd; border-radius: 8px; padding: 20px;'>"
                            + "<h2 style='color: #C0392B;'>Alcura Price Rejection Notice</h2>"
                            + "<p style='font-size: 16px; color: #333;'>Dear Doctor,</p>"
                            + "<p style='font-size: 15px;'>We regret to inform you that your requested consultation price has been <strong style='color: red;'>rejected</strong>.</p>"

                            + "<table style='width: 100%%; margin: 20px 0; border-collapse: collapse;'>"
                            + "<tr>"
                            + "<td style='padding: 10px; background-color: #fce4e4; font-weight: bold;'>Requested Price:</td>"
                            + "<td style='padding: 10px;'>Rs. %s</td>"
                            + "</tr>"
                            + "<tr>"
                            + "<td style='padding: 10px; background-color: #fce4e4; font-weight: bold;'>Adjusted Price:</td>"
                            + "<td style='padding: 10px;'>Rs. %s</td>"
                            + "</tr>"
                            + "</table>"

                            + "<div style='margin-top: 20px;'>"
                            + "<p style='font-weight: bold; color: #555;'>Admin Notes:</p>"
                            + "<div style='background-color: #fcf8e3; border-left: 4px solid #f0ad4e; padding: 10px; border-radius: 4px;'>"
                            + "%s"
                            + "</div>"
                            + "</div>"

                            + "<p style='margin-top: 30px; font-size: 14px; color: #888;'>Thank you,<br><strong>Alcura Team</strong></p>"
                            + "</div>"
                            + "</div>",
                    doctorPrice.getPrice(),
                    newPrice,
                    adminNotes
            );


            logger.info("Set Price as {}", doctorPrice.getPrice());

            emailService.sendAppointmentStatusEmail(doctorEmail, subject, body,heading);
            logger.info("Email send to: {}", doctorEmail);
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
