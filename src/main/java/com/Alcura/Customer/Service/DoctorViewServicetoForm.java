package com.Alcura.Customer.Service;

import com.Alcura.Customer.DTO.ViewDoctorForm;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorViewServicetoForm
{
    private final DoctorRepository doctorRepository;

    public DoctorViewServicetoForm(DoctorRepository doctorRepository)
    {
        this.doctorRepository = doctorRepository;
    }

    public List<ViewDoctorForm> getViewDoctor()
    {
        return doctorRepository.viewDoctorinCustomerForm();
    }
}
