package com.Alcura.Customer.Repository;

import com.Alcura.Customer.Model.Appoinment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appoinment, Integer> {
    @Query("SELECT a FROM Appoinment a WHERE a.status = 'Pending' ORDER BY a.created_at DESC")
    List<Appoinment> findRecentAppointments();

    @Query("SELECT a FROM Appoinment a WHERE a.doctor = :doctor AND a.appointment_date = :date")
    List<Appoinment> findByDoctorAndDate(
            @Param("doctor") String doctor,
            @Param("date") LocalDate date);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appoinment a WHERE a.doctor = :doctor " +
            "AND a.appointment_date = :date " +
            "AND a.appointment_time = :time")
    boolean existsByDoctorAndDateAndTime(
            @Param("doctor") String doctor,
            @Param("date") LocalDate date,
            @Param("time") String time);

    @Query("SELECT a FROM Appoinment a WHERE a.doctor = :doctorId")
    List<Appoinment> findByDoctor(@Param("doctorId") String doctorId);
}