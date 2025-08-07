package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.FeedBack;
import com.Alcura.Customer.Repository.FeedbackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedBackService
{
    @Autowired
    FeedbackRepo feedbackRepo;

    public FeedBack RegisterFeedback(String email)
    {
        return feedbackRepo.findByCustomerEmail(email);
    }
}
