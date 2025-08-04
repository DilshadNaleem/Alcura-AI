package com.Alcura.Admin.Repository;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Doctor.Model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorPriceRepo extends JpaRepository<DoctorPrice, Integer>
{
    Optional<DoctorPrice> findFirstByOrderByIdDesc();
    DoctorPrice findById(int id);
    Optional<DoctorPrice> findTopByOrderByIdDesc();

}
