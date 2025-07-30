package com.Alcura.Customer.Controller.AppointmentController;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepository;
import com.Alcura.Customer.Service.DoctorAvailabilityService;
import com.Alcura.Doctor.Model.DoctorAvailability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor")
public class DoctorAvailabilityApiController {

    private final DoctorAvailabilityService availabilityService;
    private final AppointmentRepository appointmentRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
    private static final Logger logger = LoggerFactory.getLogger(DoctorAvailabilityApiController.class);

    public DoctorAvailabilityApiController(
            DoctorAvailabilityService availabilityService,
            AppointmentRepository appointmentRepository) {
        this.availabilityService = availabilityService;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/{doctorId}/availability/{date}")
    public ResponseEntity<?> getAvailableTimeSlots(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        logger.info("Received request for doctorId: {}, date: {}", doctorId, date);

        try {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            List<DoctorAvailability> availabilities =
                    availabilityService.findValidAvailabilitiesForDate(doctorId, dayOfWeek, date);

            logger.debug("Found {} availability records", availabilities.size());

            List<String> timeSlots = new ArrayList<>();

            for (DoctorAvailability availability : availabilities) {
                Time startTime = (Time) availability.getStartTime();
                Time endTime = (Time) availability.getEndTime();

                if (startTime != null && endTime != null) {
                    LocalTime start = startTime.toLocalTime();
                    LocalTime end = endTime.toLocalTime();

                    logger.debug("Processing availability slot: {} to {}", start, end);

                    while (start.isBefore(end)) {
                        LocalTime slotEnd = start.plusMinutes(15);
                        if (slotEnd.isAfter(end)) {
                            slotEnd = end;
                        }

                        String slot = formatTime(start) + " - " + formatTime(slotEnd);
                        timeSlots.add(slot);
                        logger.trace("Added time slot: {}", slot);
                        start = slotEnd;
                    }
                } else {
                    logger.warn("Availability record with null start/end time found");
                }
            }

            logger.info("Returning {} time slots", timeSlots.size());
            return ResponseEntity.ok(timeSlots);

        } catch (Exception e) {
            logger.error("Error processing request", e);
            return ResponseEntity.internalServerError().body("Error processing request");
        }
    }

    @GetMapping("/{doctorId}/booked-slots/{date}")
    public ResponseEntity<List<String>> getBookedTimeSlots(
            @PathVariable String doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        logger.info("Fetching booked slots for doctor: {}, date: {}", doctorId, date);

        try {
            List<Appoinment> appointments = appointmentRepository.findByDoctorAndDate(doctorId, date);

            List<String> bookedSlots = appointments.stream()
                    .map(Appoinment::getAppointment_time)
                    .collect(Collectors.toList());

            logger.info("Found {} booked slots", bookedSlots.size());
            return ResponseEntity.ok(bookedSlots);

        } catch (Exception e) {
            logger.error("Error fetching booked slots", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }
}