package com.Alcura.Customer.Controller;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Service.Interfaces.AppointmentRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/Customer")
public class AppointmentSearchController
{
    @Autowired
    private AppointmentRepo appointmentRepo;


    @GetMapping("/SearchAppointment")
    public String searchAppointments(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "status", required = false) String status,
            Model model,
            HttpSession session) {

        String customerEmail = (String) session.getAttribute("email");

        if (customerEmail == null) {
            return "/Customer/Signing";
        }

        List<Appoinment> appointments;

        if ((query == null || query.trim().isEmpty()) && (status == null || status.trim().isEmpty())) {
            appointments = appointmentRepo.findByCustomerEmailOrderByUniqueIdDesc(customerEmail);
        } else {
            if (query != null && !query.trim().isEmpty()) {
                appointments = appointmentRepo.searchAppointments(
                        customerEmail,
                        query.trim(),
                        (status != null && !status.trim().isEmpty()) ? status.trim() : null
                );
            } else {
                appointments = appointmentRepo.findByCustomerEmailAndStatusOrderByUniqueIdDesc(
                        customerEmail,
                        status.trim()
                );
            }
        }

        List<String> statuses = appointmentRepo.findDistinctStatusesByCustomerEmail(customerEmail);

        model.addAttribute("appointments", appointments);
        model.addAttribute("statuses", statuses);
        model.addAttribute("currentQuery", query != null ? query : "");
        model.addAttribute("currentStatus", status != null ? status : "");

        return "/Customer/CustomerHistory";
    }

}
