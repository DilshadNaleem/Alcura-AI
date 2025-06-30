package com.Alcura.Customer.Repository;

import com.Alcura.Customer.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer>
{
    Customer findByEmail (String email);
    Customer findByEmailAndStatus (String email, int status);
    Optional <Customer> findTopByOrderByIdDesc();
    List<Customer> findByFaceDataIsNotNull();

}

