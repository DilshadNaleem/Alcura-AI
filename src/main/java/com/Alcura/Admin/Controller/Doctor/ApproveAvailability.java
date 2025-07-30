package com.Alcura.Admin.Controller.Doctor;

import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class ApproveAvailability {

    private final DoctorAvailabilityRepository availabilityRepository;

    @Autowired
    public ApproveAvailability(DoctorAvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @PostMapping("/Admin/ApproveAvailability")
    public String approveSelectedAvailabilities(
            @RequestParam("selectedIds") String selectedIds,
            @RequestParam("doctorId") String doctorId) {

        // Convert comma-separated string to list of Long IDs
        List<Long> ids = Arrays.stream(selectedIds.split(","))
                .map(Long::parseLong)
                .toList();

        // Find all availabilities by IDs
        List<DoctorAvailability> availabilities = availabilityRepository.findAllById(ids);

        // Update status to "APPROVED" for each availability
        availabilities.forEach(availability -> {
            availability.setStatus("Confirmed");
            availability.setAvailable(true); // Also set as available
        });

        // Save all updated availabilities
        availabilityRepository.saveAll(availabilities);

        return "redirect:/Admin/Manage_Availability"; // Redirect back to management page
    }
}