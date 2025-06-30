package com.Alcura.Customer.Controller;

import com.Alcura.Admin.DTO.ViewAllDoctors;
import com.Alcura.Admin.Service.DoctorService;
import com.Alcura.Doctor.Model.Doctor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DoctorProfileController
{
    private DoctorService doctorService;

    DoctorProfileController(DoctorService doctorService)
    {
        this.doctorService = doctorService;
    }

    @GetMapping("/Customer/Appoinment/doctorProfile/{unique_id}")
    public String viewDoctorDetails(@PathVariable("unique_id") String unique_id,
                                                    Model model)
    {
        List<Doctor> doctors = doctorService.getDoctorForCustomerForm(unique_id);
        model.addAttribute("doctors", doctors);
            return "/Customer/ViewDoctorProfile";
    }
}
