package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Service.Interfaces.AppointmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Optional;
@Component
public class AppointmentId {
    @Autowired
    private AppointmentRepo appointmentRepo;

    public String createAppointment() {  // Remove the Appoinment parameter
        Optional<Appoinment> maxAppointment = appointmentRepo.findTopByOrderByIdDesc();
        return generateUniqueID(maxAppointment);
    }

    private String generateUniqueID(Optional<Appoinment> maxAppointment) {
        if(maxAppointment.isPresent()) {
            int nextId = Integer.parseInt(maxAppointment.get().getUnique_id().split("_")[1]) + 1;
            return "Appointment_" + new DecimalFormat("00").format(nextId);
        } else {
            return "Appointment_01";
        }
    }
}
