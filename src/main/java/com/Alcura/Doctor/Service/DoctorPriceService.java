package com.Alcura.Doctor.Service;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorPriceService
{
    @Autowired
    private DoctorPriceRepo doctorPriceRepo;

    public List<DoctorPrice> getAllPriceList()
    {
        return doctorPriceRepo.findAll();
    }
}
