package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.DTO.AvailabilityDTO;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorAvailabilityRepository;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/Doctor")
public class DoctorAvailabilityController {

    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private static final Logger logger = LoggerFactory.getLogger(DoctorAvailabilityController.class);

    @GetMapping("/SetAvailability")
    public String viewSetAvailabilityForm(HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null || email.isEmpty()) {
            return "redirect:/Doctor/Signing";
        }
        return "/Doctor/SetAvailability";
    }

    @GetMapping("/GetAvailability")
    @ResponseBody
    public ResponseEntity<List<AvailabilityDTO>> getAvailability(HttpSession session) {
        try {
            String email = (String) session.getAttribute("email");
            if (email == null) {
                return ResponseEntity.badRequest().build();
            }

            Doctor doctor = doctorRepository.findByemail(email);
            if (doctor == null) {
                return ResponseEntity.notFound().build();
            }

            LocalDate[] nextWeekDates = getNextWeekDates();
            List<DoctorAvailability> availabilities = doctorAvailabilityRepository
                    .findByDoctorAndValidFromBetween(doctor, nextWeekDates[0], nextWeekDates[6]);

            List<AvailabilityDTO> dtos = availabilities.stream()
                    .map(avail -> new AvailabilityDTO(
                            avail.getDayOfWeek().toString(),
                            avail.isAvailable(),
                            avail.getStartTime() != null ? avail.getStartTime().toString().substring(0, 5) : null,
                            avail.getEndTime() != null ? avail.getEndTime().toString().substring(0, 5) : null,
                            avail.getValidFrom().toString()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error fetching availability", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/SaveAvailability")
    @Transactional
    public String saveAvailability(
            @RequestParam("uniqueId") String uniqueId,
            @RequestParam Map<String, String> allParams,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            String email = (String) session.getAttribute("email");
            if (email == null) {
                return "redirect:/Doctor/Signing";
            }

            Doctor doctor = doctorRepository.findByemail(email);
            if (doctor == null) {
                logger.error("Doctor not found with email: {}", email);
                redirectAttributes.addAttribute("error", "Doctor not found");
                return "redirect:/Doctor/SetAvailability";
            }

            // First, delete any existing availability for the next week
            LocalDate[] nextWeekDates = getNextWeekDates();
            doctorAvailabilityRepository.deleteByDoctorAndValidFromBetween(
                    doctor, nextWeekDates[0], nextWeekDates[6]);

            boolean isGloballyAvailable = allParams.containsKey("available") &&
                    "on".equals(allParams.get("available"));

            String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

            for (int i = 0; i < days.length; i++) {
                String day = days[i];
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
                LocalDate date = nextWeekDates[i];

                DoctorAvailability availability = new DoctorAvailability();
                availability.setDoctor(doctor);
                availability.setDayOfWeek(dayOfWeek);
                availability.setValidFrom(date);
                availability.setValidTo(date);
                availability.setStatus("Pending");

                boolean isDayUnavailable = allParams.containsKey(day + "Unavailable") &&
                        "on".equals(allParams.get(day + "Unavailable"));

                if (!isGloballyAvailable || isDayUnavailable) {
                    availability.setAvailable(false);
                    availability.setStartTime(null);
                    availability.setEndTime(null);
                } else {
                    String startTimeStr = allParams.get(day + "Start");
                    String endTimeStr = allParams.get(day + "End");

                    if (startTimeStr != null && !startTimeStr.isEmpty() &&
                            endTimeStr != null && !endTimeStr.isEmpty()) {
                        LocalTime startTime = LocalTime.parse(startTimeStr);
                        LocalTime endTime = LocalTime.parse(endTimeStr);

                        availability.setAvailable(true);
                        availability.setStartTime(java.sql.Time.valueOf(startTime));
                        availability.setEndTime(java.sql.Time.valueOf(endTime));
                    } else {
                        availability.setAvailable(false);
                        availability.setStartTime(null);
                        availability.setEndTime(null);
                    }
                }

                doctorAvailabilityRepository.save(availability);
                logger.info("Saved availability for {} ({}): available={}",
                        day, date, availability.isAvailable());
            }

            logger.info("Successfully saved availability for doctor: {}", email);
            redirectAttributes.addAttribute("success", "Availability saved successfully!");
            return "redirect:/Doctor/SetAvailability";

        } catch (Exception e) {
            logger.error("Error saving availability: {}", e.getMessage(), e);
            redirectAttributes.addAttribute("error", "Error saving availability: " + e.getMessage());
            return "redirect:/Doctor/SetAvailability";
        }
    }

    private LocalDate[] getNextWeekDates() {
        LocalDate today = LocalDate.now();
        LocalDate nextMonday = today.plusDays(8 - today.getDayOfWeek().getValue());
        LocalDate[] dates = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            dates[i] = nextMonday.plusDays(i);
        }
        return dates;
    }
}