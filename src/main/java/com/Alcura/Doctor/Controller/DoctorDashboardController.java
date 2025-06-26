package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DoctorDashboardController
{
    private final DoctorRepository doctorRepository;

    public DoctorDashboardController(DoctorRepository doctorRepository)
    {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/Doctor/Dashboard")
    public String showDoctorName(Model model, HttpSession session)
    {
        String email = (String)  session.getAttribute("email");

        if (email == null)
        {
            return "redirect:/Doctor/Signing";
        }

        Doctor doctor = doctorRepository.findByEmailAndStatus(email,1);

        if (doctor == null)
        {
            return "redirect:/Doctor/Signing";
        }

        model.addAttribute("adminName", doctor.getFirstName());
        model.addAttribute("adminImage", doctor.getImage());
        System.out.println("Image Path: " + doctor.getImage());

        return "/Doctor/Dashboard";
    }
}
