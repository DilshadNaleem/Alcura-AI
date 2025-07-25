package com.Alcura.Admin.Service;

import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService
{
    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Appoinment> getAllAppointments()
    {
        return appointmentRepository.findAll();
    }
}
