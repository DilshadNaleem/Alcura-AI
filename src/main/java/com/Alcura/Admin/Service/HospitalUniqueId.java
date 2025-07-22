package com.Alcura.Admin.Service;

import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Optional;

@Component
public class HospitalUniqueId {
    @Autowired
    private HospitalRepository hospitalRepository;

    public Hospital createHospital(Hospital hospital) {
        Optional<Hospital> maxHospital = hospitalRepository.findTopByOrderByUniqueIdDesc();
        String nextUniqueId = generateUniqueId(maxHospital);
        hospital.setUniqueId(nextUniqueId);
        return hospitalRepository.save(hospital);
    }

    private String generateUniqueId(Optional<Hospital> maxHospital) {
        if (maxHospital.isPresent()) {
            String currentId = maxHospital.get().getUniqueId();
            // Handle case where currentId is null
            if (currentId == null || !currentId.startsWith("Hospital_")) {
                return "Hospital_01";
            }
            try {
                int nextId = Integer.parseInt(currentId.split("_")[1]) + 1;
                return "Hospital_" + new DecimalFormat("00").format(nextId);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                return "Hospital_01";
            }
        }
        return "Hospital_01";
    }
}