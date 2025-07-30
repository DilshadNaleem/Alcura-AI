package com.Alcura.Customer.Controller.AppointmentController;

import com.Alcura.Admin.Service.DoctorService;
import com.Alcura.Customer.Service.DoctorAvailabilityService;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Model.DoctorAvailability;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DoctorProfileController {
    private final DoctorService doctorService;
    private final DoctorAvailabilityService doctorAvailabilityService;

    public DoctorProfileController(DoctorService doctorService,
                                   DoctorAvailabilityService doctorAvailabilityService) {
        this.doctorService = doctorService;
        this.doctorAvailabilityService = doctorAvailabilityService;
    }

    @GetMapping("/Customer/Appoinment/doctorProfile/{unique_id}")
    public String viewDoctorDetails(@PathVariable("unique_id") String unique_id,
                                    Model model) {
        List<Doctor> doctors = doctorService.getDoctorForCustomerForm(unique_id);

        // Group availability by day
        Map<DayOfWeek, List<DoctorAvailability>> availabilityByDay =
                doctorAvailabilityService.getCurrentAvailableSlots(unique_id)
                        .stream()
                        .collect(Collectors.groupingBy(DoctorAvailability::getDayOfWeek));

        model.addAttribute("doctors", doctors);
        model.addAttribute("availabilityByDay", availabilityByDay);
        return "/Customer/ViewDoctorProfile";
    }
}