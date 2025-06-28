package com.Alcura.Customer.Controller;

import com.Alcura.Admin.DTO.ViewAllDoctors;
import com.Alcura.Admin.Service.DoctorService;
import com.Alcura.Customer.DTO.ViewDoctorForm;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.Doctor.Service.DoctorViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class ViewDoctorController
{
    private Logger logger = LoggerFactory.getLogger(ViewDoctorController.class);
    private final DoctorViewService doctorService;
    private final DoctorRepository doctorRepository;

    public ViewDoctorController(DoctorViewService doctorService,
                                DoctorRepository doctorRepository)
    {
        this.doctorService = doctorService;
        this.doctorRepository = doctorRepository;
    }



    @GetMapping(value = "/Customer/AppointmentBooking", produces = MediaType.IMAGE_JPEG_VALUE)
    public String ViewAllDoctorinForm(Model model)
    {
        List<ViewDoctorForm> doctorViewServices = doctorService.viewFullform();
        model.addAttribute("doctors", doctorViewServices);
       logger.info("Size: " + doctorViewServices.size());
        return "/Customer/Appointment_Booking";
    }

    @GetMapping(value = "/doctor/image/{email}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getDoctorById(@PathVariable String email)
    {
        Doctor doctor = doctorRepository.findByemail(email);
        if(doctor != null && doctor.getImage() != null)
        {
            return ResponseEntity.ok().body(doctor.getImage());
        }
        return ResponseEntity.notFound().build();
    }
}
