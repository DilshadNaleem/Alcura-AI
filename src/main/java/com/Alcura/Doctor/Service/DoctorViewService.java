package com.Alcura.Doctor.Service;

import com.Alcura.Customer.DTO.ViewDoctorForm;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorViewService
{
    private final DoctorRepository doctorRepository;

    public DoctorViewService(DoctorRepository doctorRepository)
    {
        this.doctorRepository = doctorRepository;
    }

    public List<ViewDoctorForm> viewFullform()
    {
        return doctorRepository.viewDoctorinCustomerForm();
    }
}
