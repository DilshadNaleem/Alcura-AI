package com.Alcura.Admin.Controller.Hospital;

import com.Alcura.Admin.Service.HospitalService;
import com.Alcura.Customer.Model.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Admin")
public class ManageHospitalController
{
    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/ManageHospitals")
    public String viewAllHospitals(Model model)
    {
        List<Hospital> hospitals = hospitalService.getAllHospitals();
        model.addAttribute("hospitals", hospitals);
        return "/Admin/Hospital/ManageHospitals";
    }


    @GetMapping("/ViewHospitalImage/{uniqueId}")
    public ResponseEntity<byte[]> viewHospitalImage(@PathVariable String uniqueId) {
        // 1. Fetch the hospital by uniqueId
        Hospital hospital = hospitalService.findByUniqueId(uniqueId);

        // 2. Check if hospital exists and has an image
        if (hospital == null || hospital.getImage() == null) {
            // Return a default image or 404
            return ResponseEntity.notFound().build();
        }

        // 3. Return the image data with appropriate content type
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // or whatever type you're using
                .body(hospital.getImage());
    }
}
