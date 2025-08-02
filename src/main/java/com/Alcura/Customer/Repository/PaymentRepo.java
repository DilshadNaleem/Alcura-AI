package com.Alcura.Customer.Repository;

import com.Alcura.Customer.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Integer>
{
    Optional<Payment> findTopByOrderByIdDesc();
}
