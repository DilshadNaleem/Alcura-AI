package com.Alcura.Customer.Controller;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepo;
import com.Alcura.Customer.Repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/Customer/")
public class CustomerDashboardController
{
    @Autowired
    AppointmentRepo appointmentRepo;

    Logger logger = LoggerFactory.getLogger(CustomerDashboardController.class);
    @GetMapping("/Dashboard")
    public String dashboard (HttpSession session,
                             Model model)
    {
       String email = (String) session.getAttribute("email");
        if (email == null)
        {
            return "/Customer/Signing";
        }

        List<Appoinment> appointments = appointmentRepo.findByCustomerEmailLimited(email);
        logger.info("Received List :{}", appointments.size());
        model.addAttribute("appointments", appointments);
        return  "/Customer/Dashboard";
    }

}
