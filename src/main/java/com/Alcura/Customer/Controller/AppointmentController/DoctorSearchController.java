package com.Alcura.Customer.Controller.AppointmentController;

import com.Alcura.Customer.DTO.ViewDoctorForm;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DoctorSearchController {

    private final DoctorRepository doctorRepository;
    private final Logger logger = LoggerFactory.getLogger(DoctorSearchController.class);

    public DoctorSearchController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // Handler to show all or searched doctors
    @GetMapping("/Customer/SearchDoctor/")
    public String searchDoctor(
            @RequestParam(value = "doctorName", required = false) String doctorName,
            Model model) {

        logger.info("Search request received for doctor name: {}", doctorName);

        List<ViewDoctorForm> doctors;

        if (doctorName == null || doctorName.trim().isEmpty()) {
            doctors = doctorRepository.viewDoctorinCustomerForm(); // Fetch all
        } else {
            // Filter using first or last name + status = 1 (active)
            doctors = doctorRepository.viewDoctorinCustomerForm()
                    .stream()
                    .filter(doc -> (doc.getFirst_name() + " " + doc.getLast_name()).toLowerCase().contains(doctorName.toLowerCase()))
                    .toList();
        }

        model.addAttribute("doctors", doctors);
        return "/Customer/Appointment_Booking"; // The name of your Thymeleaf template
    }
}