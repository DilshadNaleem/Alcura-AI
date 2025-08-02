package com.Alcura.Admin.Controller.Doctor;

import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorAvailabilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.time.DayOfWeek;


@Controller
public class EditAvailabilityController {
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    Logger logger = LoggerFactory.getLogger(EditAvailabilityController.class);

    public EditAvailabilityController(DoctorAvailabilityRepository doctorAvailabilityRepository) {
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }
    @PostMapping("/Admin/Manage_Availability/update")
    public String editAvailability(@RequestParam("id") Long id,
                                   @RequestParam("startTime") String startTimeStr,
                                   @RequestParam("endTime") String endTimeStr,
                                   @RequestParam("status") String status,
                                   RedirectAttributes redirectAttributes) {

        logger.info("Received update request - id: {}, startTime: {}, endTime: {}, status: {}",
                id, startTimeStr, endTimeStr, status);

        try {
            DoctorAvailability availability = doctorAvailabilityRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid availability id: " + id));

            // Set status first
            availability.setStatus(status);
            availability.setAvailable("Available".equalsIgnoreCase(status));

            // Handle time fields based on status
            if ("Available".equalsIgnoreCase(status)) {
                // Parse time only when status is Available
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                availability.setStartTime(timeFormat.parse(startTimeStr));
                availability.setEndTime(timeFormat.parse(endTimeStr));
            } else {
                // Set to null when Unavailable
                availability.setStartTime(null);
                availability.setEndTime(null);
            }

            doctorAvailabilityRepository.save(availability);

            redirectAttributes.addFlashAttribute("successMessage", "Availability updated successfully!");
            return "redirect:/Admin/Manage_Availability";
        } catch (Exception e) {
            logger.error("Error updating availability", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update availability: " + e.getMessage());
            return "redirect:/Admin/Manage_Availability";
        }
    }
}