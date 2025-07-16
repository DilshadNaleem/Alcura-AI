package com.Alcura.Customer.Controller.AppointmentController;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Service.Interfaces.AppointmentRepo;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Customer")
public class MyHistoryController {
    private static final Logger logger = LoggerFactory.getLogger(MyHistoryController.class);
    private final AppointmentRepo appointmentRepo;

    public MyHistoryController(AppointmentRepo appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    @GetMapping("/MyHistory")
    public String GetHistory(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null || email.isEmpty()) {
            return "/Customer/Signing";
        }

        List<Appoinment> appointments = appointmentRepo.findByCustomerEmailOrderByUniqueIdDesc(email);
        model.addAttribute("appointments", appointments);

        logger.info("Found {} appointments for email: {}", appointments.size(), email);
        return "/Customer/CustomerHistory";
    }
}