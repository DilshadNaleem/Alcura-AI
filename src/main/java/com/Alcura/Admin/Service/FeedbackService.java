package com.Alcura.Admin.Service;

import com.Alcura.Customer.Model.FeedBack;
import com.Alcura.Customer.Repository.FeedbackRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;

@Service
public class FeedbackService
{
    @Autowired
    private FeedbackRepo feedbackRepo;
    Logger logger = LoggerFactory.getLogger(FeedbackService.class);


    public void deleteRecords(String feedbackId) {
        try {
            FeedBack feedBack = feedbackRepo.findByUniqueId(feedbackId);
            if (feedBack != null) {
                feedbackRepo.delete(feedBack);
            }
        } catch (Exception e) {
            logger.error("Delete unsuccessful", e);
            throw e; // Or handle it appropriately
        }
    }

}
