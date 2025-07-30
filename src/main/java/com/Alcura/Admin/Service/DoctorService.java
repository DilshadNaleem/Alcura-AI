package com.Alcura.Admin.Service;

import com.Alcura.Admin.DTO.ViewAllDoctors;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Model.DoctorAvailability;
import com.Alcura.Doctor.Repository.DoctorAvailabilityRepository;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepositoryAdmin;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    public DoctorService(DoctorRepository doctorRepositoryAdmin,
                         DoctorAvailabilityRepository doctorAvailabilityRepository)
    {
        this.doctorRepositoryAdmin = doctorRepositoryAdmin;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }

    public List<ViewAllDoctors> getAllDoctors()
    {
        return doctorRepositoryAdmin.findAllDoctors();
    }

    public List<Doctor> getDoctorForCustomerForm(String uniqueId)
    {
       return doctorRepositoryAdmin.findByUniqueId(uniqueId);
    }

    public List<Doctor> getDoctorForImage(String email)
    {
        return doctorRepositoryAdmin.findByEmail(email);
    }

    public Doctor getDoctorByUniqueId(String uniqueId)
    {
        Optional<Doctor> doctorOptional = doctorRepositoryAdmin.findByUniqueIdforAvailabeStatus(uniqueId);
        return doctorOptional.orElseThrow(() ->
        new RuntimeException("Doctor not found with unique Id " + uniqueId));
    }

    public List<DoctorAvailability> getAllViewDoctorforAvailability()
    {
        return doctorAvailabilityRepository.findAll();
    }
}
