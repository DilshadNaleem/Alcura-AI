package com.Alcura.Customer.Repository;

import com.Alcura.Customer.Model.Appoinment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AppointmentRepository extends JpaRepository<Appoinment , Integer>
{
    @Query("SELECT a FROM Appoinment a WHERE a.status = 'Pending' ORDER BY a.created_at DESC")
    List<Appoinment> findRecentAppointments();
}
