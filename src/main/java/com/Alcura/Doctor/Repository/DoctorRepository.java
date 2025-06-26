package com.Alcura.Doctor.Repository;

import com.Alcura.Doctor.Model.Doctor;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer>
{
    Doctor findByemail(String email);
    Doctor findByEmailAndStatus (String email, int status);
    Optional<Doctor> findTopByOrderByIdDesc();
    List<Doctor> findByFaceDataIsNotNull();
}
