package com.Alcura.Customer.Service.Interfaces;

import com.Alcura.Customer.Model.Appoinment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepo extends JpaRepository<Appoinment,Integer>
{
    Optional<Appoinment> findTopByOrderByIdDesc();
}
