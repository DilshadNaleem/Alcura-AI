package com.Alcura.Customer.Service.Interfaces;

import com.Alcura.Customer.Model.Appoinment;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentEmailService
{
    void sendAppointmentConfirmaation(String toEmail, LocalDate AppointmentDate, String  doctorName, String appointmentId,String time);

}
