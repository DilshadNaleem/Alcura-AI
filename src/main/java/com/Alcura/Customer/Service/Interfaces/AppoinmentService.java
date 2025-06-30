package com.Alcura.Customer.Service.Interfaces;

import com.Alcura.Customer.Model.Appoinment;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppoinmentService {
    Appoinment createAppointment(
            String doctorId,
            String doctorName,
            String time,
            LocalDate date,
            String specialReason,
            String customerEmail
    );
}
