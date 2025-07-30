package com.Alcura.Doctor.Repository;

import com.Alcura.Admin.DTO.ViewAllDoctors;
import com.Alcura.Customer.DTO.ViewDoctorForm;
import com.Alcura.Doctor.Model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer>
{
    Doctor findByemail(String email);
    List<Doctor> findByFirstNameContainingOrLastNameContainingAndStatus(String firstName, String lastName, int status);
    Doctor findByEmailAndStatus (String email, int status);
    Optional<Doctor> findTopByOrderByIdDesc();
    List<Doctor> findByFaceDataIsNotNull();
    List<Doctor> findByUniqueId(String uniqueId);
    List<Doctor> findAll();
    @Query("SELECT new com.Alcura.Admin.DTO.ViewAllDoctors(" +
            "d.uniqueId, d.firstName, d.lastName, " +
            "d.contactNumber, d.email, " +
            "d.government_Hospitals, d.nic, " +
            "d.other_specialization, d.specialist, " +
            "d.special_note, CAST(d.status AS string), d.specialist_info,d.experience) " +
            "FROM Doctor d")
    List<ViewAllDoctors> findAllDoctors();


    @Query("SELECT new com.Alcura.Customer.DTO.ViewDoctorForm(" +
            "d.firstName, d.lastName, d.experience, d.specialist, d.specialist_info, d.image, d.email, d.uniqueId, d. doctor_availablility) " +
            "FROM Doctor d")
    List<ViewDoctorForm> viewDoctorinCustomerForm();

    @Query("SELECT d FROM Doctor d ORDER BY d.createdAt DESC LIMIT 10")
    List<Doctor> findRecentDoctors();
    List<Doctor> findByEmail(String email);
    Doctor findByuniqueId(String uniqueId);
    @Query("SELECT d FROM Doctor d WHERE d.uniqueId = :uniqueId AND SIZE(d.availabilities) > 0")
    Optional<Doctor> findByUniqueIdforAvailabeStatus(String uniqueId);
}

