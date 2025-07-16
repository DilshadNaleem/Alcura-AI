package com.Alcura.Customer.Service.Interfaces;

import com.Alcura.Customer.Model.Appoinment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepo extends JpaRepository<Appoinment,Integer>
{
    Optional<Appoinment> findTopByOrderByIdDesc();

    @Query("SELECT a FROM Appoinment a WHERE a.customer_email = :customerEmail ORDER BY a.unique_id DESC")
    List<Appoinment> findByCustomerEmailOrderByUniqueIdDesc(String customerEmail);

    @Query(value = "SELECT * FROM appointment WHERE unique_id = :appointmentId", nativeQuery = true)
    Optional<Appoinment> findByUniqueIdNative(@Param("appointmentId") String unique_id);

    @Query("SELECT DISTINCT a.status FROM Appoinment a WHERE a.customer_email = :customerEmail")
    List<String> findDistinctStatusesByCustomerEmail(@Param("customerEmail") String customerEmail);
}
