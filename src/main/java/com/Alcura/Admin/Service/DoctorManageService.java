package com.Alcura.Admin.Service;

import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorManageService
{
    @Autowired
    private DoctorRepository doctorRepository;

    public List<Doctor> getDoctors()
    {
        return doctorRepository.findAll();
    }
}
