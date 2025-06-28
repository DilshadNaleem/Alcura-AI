package com.Alcura.Doctor.Repository;

import com.Alcura.Admin.DTO.ViewAllDoctors;
import com.Alcura.Customer.DTO.ViewDoctorForm;
import com.Alcura.Doctor.Model.Doctor;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer>
{
    Doctor findByemail(String email);
    Doctor findByEmailAndStatus (String email, int status);
    Optional<Doctor> findTopByOrderByIdDesc();
    List<Doctor> findByFaceDataIsNotNull();

    @Query("SELECT new com.Alcura.Admin.DTO.ViewAllDoctors(" +
            "d.uniqueId, d.firstName, d.lastName, " +
            "d.contactNumber, d.email, " +
            "d.government_Hospitals, d.nic, " +
            "d.other_specialization, d.specialist, " +
            "d.special_note, CAST(d.status AS string), d.specialist_info) " +
            "FROM Doctor d")
    List<ViewAllDoctors> findAllDoctors();


    @Query("SELECT new com.Alcura.Customer.DTO.ViewDoctorForm(" +
            "d.firstName, d.lastName, d.experience, d.specialist, d.specialist_info, d.image, d.email) " +
            "FROM Doctor d")
    List<ViewDoctorForm> viewDoctorinCustomerForm();

}

