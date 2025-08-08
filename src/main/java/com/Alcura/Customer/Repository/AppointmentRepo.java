package com.Alcura.Customer.Repository;

import com.Alcura.Admin.DTO.ProfitCalculationDTO;
import com.Alcura.Customer.Model.Appoinment;
import com.Alcura.Customer.Model.Customer;
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

    @Query("SELECT new com.Alcura.Admin.DTO.ProfitCalculationDTO(" +
            "a.unique_id, a.doctor, a.doctor_name, a.customer_email, a.status, " +
            "a.paymentId, a.created_at, d.uniqueId, d.email, " +
            "dc.hospital_price, dc.newPrice, dc.price, dc.doctor_email) " +
            "FROM Appoinment a, Doctor d, DoctorPrice dc " +
            "WHERE a.status = 'Completed' " +
            "AND a.doctor = d.uniqueId " +
            "AND dc.doctor_email = d.email")
    List<ProfitCalculationDTO> findAllAppointmentsWithDoctorInfo();

    @Query("SELECT a FROM Appoinment a WHERE a.unique_id = :uniqueId")
    Appoinment findByUnique_id(@Param("uniqueId") String uniqueId);

    @Query("SELECT a FROM Appoinment a WHERE a.doctor = :doctor AND a.status = :status ORDER BY a.appointment_date ASC, a.appointment_time ASC LIMIT 7")
    List<Appoinment> findByDoctorAndStatusOrderByAppointmentDateTimeAsc(
            @Param("doctor") String doctor,
            @Param("status") String status
    );

    @Query("SELECT a, c.image FROM Appoinment a " +
            "JOIN Customer c ON a.customer_email = c.email " +
            "JOIN Doctor d ON a.doctor = d.uniqueId " +
            "WHERE a.status = 'Completed' " +
            "AND d.email = :doctorEmail " +
            "ORDER BY a.appointment_date ASC, a.appointment_time ASC LIMIT 7")
    List<Object[]> findPendingAppointmentsWithCustomerImagesByDoctorEmail(
            @Param("doctorEmail") String doctorEmail);

    @Query("SELECT a, dp.price FROM Appoinment a JOIN DoctorPrice dp ON a.doctor = dp.doctor_email WHERE a.doctor = :doctor")
    List<Object[]> findByDoctorWithPrice(@Param("doctor") String doctor);

    @Query("SELECT dp.price, dp.doctor_email, a " +
            "FROM DoctorPrice dp " +
            "JOIN Doctor d ON dp.doctor_email = d.email " +
            "JOIN Appoinment a ON d.uniqueId = a.doctor " +
            "WHERE dp.doctor_email = :doctorEmail " +
            "AND a.doctor = :doctorId " +
            "AND a.status = 'Completed'")
    List<Object[]> findCompletedAppointmentsWithPrice(
            @Param("doctorEmail") String doctorEmail,
            @Param("doctorId") String doctorId);

    List<Appoinment> findByDoctor(String Doctor);

    @Query(value = "SELECT * FROM appointment WHERE customer_email = :email AND status = 'Pending' LIMIT 7", nativeQuery = true)
    List<Appoinment> findByCustomerEmailLimited(String email);

}