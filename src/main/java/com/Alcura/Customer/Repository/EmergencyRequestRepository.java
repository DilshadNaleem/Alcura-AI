package com.Alcura.Customer.Repository;

import com.Alcura.Customer.DTO.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {

    List<EmergencyRequest> findByStatus (String status);
    EmergencyRequest findByTrackingId(String trackingId);

}
