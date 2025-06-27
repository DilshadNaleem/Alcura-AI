package com.Alcura.Doctor;

import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.persistence.Column;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;

@Controller
public class sample {
    private final DoctorRepository doctorRepository;

    public sample(DoctorRepository doctorRepository)
    {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping(value = "/doctor/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] showImage(@PathVariable int id) {
        return doctorRepository.findById(id)
                .map(Doctor::getImage)
                .orElse(new byte[0]);
    }

    // Endpoint to display a page with the image
    @GetMapping("/doctor/{id}/view")
    public String viewDoctorImage(@PathVariable Long id, Model model) {
        model.addAttribute("doctorId", id);
        return "/Doctor/sample";
    }
}
