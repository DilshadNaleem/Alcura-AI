package com.Alcura.Customer.Service;

import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorAvailabilityService {

    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    // Existing methods remain unchanged
    public List<DoctorAvailability> getDoctorAvailability(String doctorUniqueId) {
        return doctorAvailabilityRepository.findByDoctorUniqueId(doctorUniqueId);
    }

    public List<DoctorAvailability> findByDoctorUniqueIdAndDayOfWeek(String doctorId, DayOfWeek dayOfWeek) {
        return doctorAvailabilityRepository.findByDoctorUniqueIdAndDayOfWeekAndStatus(doctorId, dayOfWeek,"Confirmed");
    }

    public List<DoctorAvailability> getCurrentAvailableSlots(String doctorUniqueId) {
        return doctorAvailabilityRepository.findByDoctorUniqueIdAndIsAvailableTrue(doctorUniqueId);
    }

    // New method to support the controller
    public List<DoctorAvailability> findByDoctorUniqueIdAndDayOfWeekAndIsAvailableTrue(String doctorId, DayOfWeek dayOfWeek) {
        return findByDoctorUniqueIdAndDayOfWeek(doctorId, dayOfWeek).stream()
                .filter(DoctorAvailability::isAvailable)
                .collect(Collectors.toList());
    }

    public boolean isTimeSlotAvailable(String doctorId, DayOfWeek dayOfWeek, String timeSlot) {
        List<DoctorAvailability> availabilities = findByDoctorUniqueIdAndDayOfWeek(doctorId, dayOfWeek);
        return availabilities.stream()
                .anyMatch(availability -> {
                    String availableTime = formatTimeRange(availability.getStartTime(), availability.getEndTime());
                    return availableTime.contains(timeSlot);
                });
    }

    private String formatTimeRange(Date startTime, Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(startTime) + " - " + sdf.format(endTime);
    }

    public List<DoctorAvailability> findValidAvailabilitiesForDate(String doctorId, DayOfWeek dayOfWeek, LocalDate date) {
        // Get all availabilities for this doctor on this day of week
        List<DoctorAvailability> availabilities =
                doctorAvailabilityRepository.findByDoctorUniqueIdAndDayOfWeekAndStatus(doctorId, dayOfWeek, "Confirmed");

        // Filter by date range and availability
        return availabilities.stream()
                .filter(availability ->
                        availability.isAvailable() &&
                                isDateInRange(date, availability.getValidFrom(), availability.getValidTo()))
                .collect(Collectors.toList());
    }

    private boolean isDateInRange(LocalDate date, LocalDate validFrom, LocalDate validTo) {
        if (validFrom == null && validTo == null) {
            return true; // No date restrictions
        }
        if (validFrom == null) {
            return date.isBefore(validTo) || date.isEqual(validTo);
        }
        if (validTo == null) {
            return date.isAfter(validFrom) || date.isEqual(validFrom);
        }
        return (date.isAfter(validFrom) || date.isEqual(validFrom)) &&
                (date.isBefore(validTo) || date.isEqual(validTo));
    }
}