package com.Alcura.Customer.Service.Interfaces;

import com.Alcura.Customer.Model.Appoinment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepo extends JpaRepository<Appoinment, Integer> {
    Optional<Appoinment> findTopByOrderByIdDesc();

    @Query("SELECT a FROM Appoinment a WHERE a.customer_email = :customerEmail ORDER BY a.unique_id DESC")
    List<Appoinment> findByCustomerEmailOrderByUniqueIdDesc(String customerEmail);

    @Query(value = "SELECT * FROM appointment WHERE unique_id = :appointmentId", nativeQuery = true)
    Optional<Appoinment> findByUniqueIdNative(@Param("appointmentId") String unique_id);

    @Query("SELECT DISTINCT a.status FROM Appoinment a WHERE a.customer_email = :customerEmail")
    List<String> findDistinctStatusesByCustomerEmail(@Param("customerEmail") String customerEmail);

    // Corrected search method with proper parentheses
    @Query("SELECT a FROM Appoinment a WHERE " +
            "a.customer_email = :customerEmail " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (" +
            "   LOWER(a.unique_id) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(a.doctor_name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR CAST(a.appointment_date AS string) LIKE CONCAT('%', :query, '%') " +
            "   OR LOWER(a.appointment_time) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(a.special_reasons) LIKE LOWER(CONCAT('%', :query, '%')) " +
            ") " +
            "ORDER BY a.unique_id DESC")
    List<Appoinment> searchAppointments(
            @Param("customerEmail") String customerEmail,
            @Param("query") String query,
            @Param("status") String status);

    @Query("SELECT a FROM Appoinment a WHERE a.customer_email = :customerEmail " +
            "AND a.status = :status " +
            "ORDER BY a.unique_id DESC")
    List<Appoinment> findByCustomerEmailAndStatusOrderByUniqueIdDesc(
            @Param("customerEmail") String customerEmail,
            @Param("status") String status);
}