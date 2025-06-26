package com.Alcura.Doctor.Service;

import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Optional;

@Service
public class DoctorUniqueId
{
    @Autowired
    private DoctorRepository doctorRepository;

    public Doctor createDoctor(Doctor doctor)
    {
        Optional<Doctor> maxDoctor = doctorRepository.findTopByOrderByIdDesc();
        String nextUniqueId = generateUniqueId(maxDoctor);
        doctor.setUniqueId(nextUniqueId);
        return doctorRepository.save(doctor);
    }

    private String generateUniqueId(Optional<Doctor> maxDoctor)
    {
        String nextUniqueId ;
        if(maxDoctor.isPresent())
        {
            int nextId = Integer.parseInt(maxDoctor.get().getUniqueId().split("_")[1]) +1;
            nextUniqueId = "Doctor_" + new DecimalFormat("00").format(nextId);
        }
        else
        {
            nextUniqueId = "Doctor_01";
        }
        return nextUniqueId;
    }
}
