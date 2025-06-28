package com.Alcura.Admin.Controller;

import com.Alcura.Admin.DTO.ViewAllDoctors;
import com.Alcura.Admin.Service.DoctorService;
import com.Alcura.Doctor.Model.Doctor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DoctorViewControler {
    private DoctorService doctorService;

    DoctorViewControler(DoctorService doctorService)
    {
        this.doctorService = doctorService;
    }

    @GetMapping("/Admin/ViewDoctors")
    public String ViewAllDoctors(Model model)
    {
        List<ViewAllDoctors> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "/Admin/ViewAllDoctors";
    }
}
