package com.Alcura.Customer.Service;

import com.Alcura.Customer.Model.FeedBack;
import com.Alcura.Customer.Repository.FeedbackRepo;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Optional;

@Component
public class FeedBackUniqueId
{
    @Autowired
    private FeedbackRepo feedbackRepo;

    public FeedBack createFeedback(FeedBack feedBack)
    {
        Optional<FeedBack> maxFeedback = feedbackRepo.findTopByOrderByIdDesc();
        String nextUniqueId = generateUniqueId(maxFeedback);
        feedBack.setUniqueId(nextUniqueId);
        return feedbackRepo.save(feedBack);
    }

    private String generateUniqueId(Optional<FeedBack> maxFeedback)
    {
        String nextUniqueId;
        if (maxFeedback.isPresent())
        {
            int nextId = Integer.parseInt(maxFeedback.get().getUniqueId().split("_")[1]) +1;
            nextUniqueId = "Feedback_" + new DecimalFormat("00").format(nextId);
        }
        else
        {
            nextUniqueId = "Feedback_01";
        }
        return nextUniqueId;
    }
}
