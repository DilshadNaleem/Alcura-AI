package com.Alcura.Admin.Service;



import com.Alcura.Customer.Model.Hospital;
import com.Alcura.Customer.Repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class AddHospitalService
{
    private final HospitalRepository hospitalRepository;
    private final HospitalUniqueId hospitalUniqueId;



    public AddHospitalService(HospitalRepository hospitalRepository,
                              HospitalUniqueId hospitalUniqueId)
    {
        this.hospitalRepository = hospitalRepository;
        this.hospitalUniqueId = hospitalUniqueId;

    }

    public Hospital registerHospital(String name, BigDecimal latitude, BigDecimal longitude,
                                     String contactEmail, String address, String phoneNumber,
                                     String description, MultipartFile images) throws IOException, IOException {

        Hospital hospital = new Hospital();
        hospital.setName(name);
        hospital.setLatitude(latitude);
        hospital.setLongitude(longitude);
        hospital.setContactEmail(contactEmail);
        hospital.setAddress(address);
        hospital.setPhoneNumber(phoneNumber);
        hospital.setDescription(description);
        hospital.setImage(images.getBytes());

        // Generate unique ID
        hospital = hospitalUniqueId.createHospital(hospital);

        // Save images and get paths


        return hospitalRepository.save(hospital);
    }

    public boolean emailExists(String email)
    {
        return hospitalRepository.existsByContactEmail(email);
    }

    public boolean phoneNumberExists (String phoneNumber)
    {
        return hospitalRepository.existsByPhoneNumber(phoneNumber);
    }
}
