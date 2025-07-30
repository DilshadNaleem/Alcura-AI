package com.Alcura.Doctor.Repository;

import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability,Long>
{
    List<DoctorAvailability> findByDoctorUniqueId(String doctorUniqueId);

    List<DoctorAvailability> findByDoctorUniqueIdAndDayOfWeekAndStatus(String doctorUniqueId, DayOfWeek dayOfWeek, String status);

    List<DoctorAvailability> findByDoctorUniqueIdAndIsAvailableTrue(String doctorUniqueId);

    List<DoctorAvailability> findByDoctorAndValidFromBetween(Doctor doctor, LocalDate startDate, LocalDate endDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.validFrom BETWEEN :startDate AND :endDate")
    void deleteByDoctorAndValidFromBetween(
            @Param("doctor") Doctor doctor,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.isAvailable = true")
    List<DoctorAvailability> findByDoctorAndDayOfWeekAndIsAvailableTrue(
            @Param("doctor") Doctor doctor,
            @Param("dayOfWeek") DayOfWeek dayOfWeek);


}
