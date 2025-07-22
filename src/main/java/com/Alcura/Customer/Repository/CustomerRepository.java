package com.Alcura.Customer.Repository;

import com.Alcura.Customer.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer>
{
    Customer findByEmail (String email);
    Customer findByEmailAndStatus (String email, int status);
    Optional <Customer> findTopByOrderByIdDesc();
    List<Customer> findByFaceDataIsNotNull();
    List<Customer> findAll();
    Customer findByUniqueId(String uniqueId);
    @Query("SELECT a FROM Customer a WHERE a.status = 1 ORDER BY createdAt DESC")
    List<Customer> findRecentCustomers();
}

