package com.Alcura.Admin.Controller.Appointment;

import com.Alcura.Admin.Service.AppointmentService;
import com.Alcura.Customer.Model.Appoinment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Admin")
public class ManageAppointmentController
{
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/Manage_Appointments")
    public String ViewAppointments(Model model)
    {
        List<Appoinment> appointments  = appointmentService.getAllAppointments();
        model.addAttribute("app", appointments);
        return "/Admin/Appointment/ManageAppointments";
    }

}
