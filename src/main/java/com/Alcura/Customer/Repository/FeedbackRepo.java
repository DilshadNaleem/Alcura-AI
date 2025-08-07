package com.Alcura.Customer.Repository;

import com.Alcura.Customer.Model.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepo extends JpaRepository<FeedBack, Integer>
{
    Optional<FeedBack> findTopByOrderByIdDesc();
    FeedBack findByCustomerEmail(String CustomerEmail);
    FeedBack findByUniqueId(String uniqueId);
}
