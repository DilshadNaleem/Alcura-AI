package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Service.DoctorManageService;
import com.Alcura.Doctor.Model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/Admin")
public class DoctorManagementController
{
    @Autowired
    DoctorManageService doctorManageService;

    @GetMapping("/Manage_Doctors")
    public String viewAllDoctors(Model model)
    {
        List<Doctor> doctors = doctorManageService.getDoctors();
        model.addAttribute("doctors", doctors);
        return "/Admin/Manage_Doctors";
    }
}
