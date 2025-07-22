package com.Alcura.Admin.Service;

import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalService
{
    @Autowired
    private HospitalRepository hospitalRepository;

    public List<Hospital> getAllHospitals()
    {
        return hospitalRepository.findAll();
    }
    public Hospital findByUniqueId(String uniqueId)
    {
        return hospitalRepository.findByUniqueId(uniqueId);
    }
}
