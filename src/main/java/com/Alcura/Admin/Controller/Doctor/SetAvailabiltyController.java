package com.Alcura.Admin.Controller.Doctor;

import com.Alcura.Admin.Service.DoctorService;
import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SetAvailabiltyController
{
    private DoctorRepository doctorRepository;
    private DoctorService doctorService;

    SetAvailabiltyController(DoctorService doctorService)
    {
        this.doctorService = doctorService;
    }


    @GetMapping("/Admin/Manage_Availability")
    public String ViewAllAvailability(Model model)
    {
        List<DoctorAvailability> doctors = doctorService.getAllViewDoctorforAvailability();
        model.addAttribute("doctors", doctors);
        return "/Admin/Doctor/Manage_Availability";
    }
}
