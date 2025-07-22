package com.Alcura.Customer.Repository;

import com.Alcura.Customer.Model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital,Long>
{
    @Query(value = "SELECT *, " +
            "(6371 * acos(cos(radians(?1)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(?2)) + sin(radians(?1)) * " +
            "sin(radians(latitude)))) AS distance " +
            "FROM hospitals " +
            "ORDER BY distance ASC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Hospital> findNearest(double latitude, double longitude);
    Optional<Hospital> findTopByOrderByUniqueIdDesc();
    boolean existsByContactEmail(String email);
    boolean existsByPhoneNumber(String PhoneNumber);
    Optional<Hospital> findByName(String name);
    Hospital findByUniqueId(String uniqueId);
}
