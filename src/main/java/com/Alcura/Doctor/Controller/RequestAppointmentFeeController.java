package com.Alcura.Doctor.Controller;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import com.Alcura.Admin.Service.AdminAppointmentRescheduleEmailService;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Service.DoctorFeeUniqueId;
import jakarta.persistence.GeneratedValue;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;

@Controller
@RequestMapping("/Doctor")
public class RequestAppointmentFeeController
{
    @Autowired
    private DoctorPriceRepo doctorPriceRepo;
    Logger logger = LoggerFactory.getLogger(RequestAppointmentFeeController.class);



    @Autowired
    DoctorFeeUniqueId doctorFeeUniqueId;

    @GetMapping("/Request_Appointment_Fee")
    public String showform()
    {
        return "/Doctor/RequestAppointmentPrice";
    }


    @PostMapping("/RequestAppointmentPrice")
    public String updateForm(@RequestParam("price") Float price,
                             @RequestParam("description") String description,
                             HttpSession session,
                             HttpServletResponse response)
    {
    logger.info("Received : {}", price, description);
        try
        {
            PrintWriter writer = response.getWriter();
            response.setContentType("text/html");


            String email = (String) session.getAttribute("email");
            if (email == null)
            {
                return "/Doctor/Signing";
            }

            Double latestPrice = doctorPriceRepo.findTopByOrderByIdDesc().orElse(new DoctorPrice()).getHospital_price();

            DoctorPrice doctorPrice = new DoctorPrice();


            doctorPrice = doctorFeeUniqueId.createDoctorPrice(doctorPrice);
            doctorPrice.setPrice(price);
            doctorPrice.setDoctor_email(email);
            doctorPrice.setDescription(description);
            doctorPrice.setStatus("Pending");
            doctorPrice.setHospital_price(latestPrice);


            doctorPrice.setHospital_price(doctorPrice.getHospital_price());


            logger.info("Saving details to database: {}", doctorPrice);
            doctorPriceRepo.save(doctorPrice);

            writer.println("<script>");
            writer.println("alert('Form Submitted Successfully!');");
            writer.println("window.location.href ='/Doctor/Request_Appointment_Fee';");
            writer.println("</script>");
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
