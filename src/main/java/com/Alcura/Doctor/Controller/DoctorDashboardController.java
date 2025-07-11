package com.Alcura.Doctor.Controller;

import com.Alcura.Admin.Service.DoctorService;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
        model.addAttribute("adminLastName", doctor.getLastName());
        model.addAttribute("adminImage", doctor.getImage());
        System.out.println("Image Path: " + doctor.getImage());

        return "/Doctor/Dashboard";
    }

    @GetMapping("/Doctor/Image")
    public ResponseEntity<byte[]> getDoctorImage (HttpSession session)
    {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.notFound().build();
        }

        Doctor doctor = doctorRepository.findByEmailAndStatus(email, 1);
        if (doctor == null || doctor.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(doctor.getImage(), headers, HttpStatus.OK);
    }
}
