package com.Alcura.Admin.Service;

import com.Alcura.Admin.DTO.ViewAllDoctors;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepositoryAdmin;

    public DoctorService(DoctorRepository doctorRepositoryAdmin)
    {
        this.doctorRepositoryAdmin = doctorRepositoryAdmin;
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

}
