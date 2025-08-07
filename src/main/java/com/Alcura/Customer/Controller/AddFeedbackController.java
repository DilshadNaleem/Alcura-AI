package com.Alcura.Customer.Controller;

import com.Alcura.Customer.Model.FeedBack;
import com.Alcura.Customer.Repository.FeedbackRepo;
import com.Alcura.Customer.Service.FeedBackService;
import com.Alcura.Customer.Service.FeedBackUniqueId;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;

@RequestMapping("/Customer")
@Controller
public class AddFeedbackController
{
    @Autowired
    private FeedBackService feedBackService;
    @Autowired
    private FeedbackRepo feedbackRepo;
    @Autowired
    private FeedBackUniqueId feedBackUniqueId;

    Logger logger = LoggerFactory.getLogger(AddFeedbackController.class);


    @GetMapping("/Feedback")
    public String form()
    {
        return "/Customer/AddFeedback";
    }


    @PostMapping("/AddFeedback")
    public String update(HttpSession session,
                         PrintWriter writer,
                         @RequestParam("details") String details) {
        try {
            logger.error("Recieved : {}", details);

            String email = (String) session.getAttribute("email");
            if (email.isEmpty() || email == null) {
                return "/";
            }


            FeedBack feedBack = new FeedBack();
                feedBack = feedBackUniqueId.createFeedback(feedBack);
                feedBack.setCustomerEmail(email);
                feedBack.setDetails(details);
                feedbackRepo.save(feedBack);

                logger.info("Saved to database : {}", feedBack);

                writer.println("<script>");
                writer.println("alert('FeedBack Submitted');");
                writer.println("window.location.href ='/Customer/Dashboard';");
                writer.println("</script>");


        }
        catch (Exception e)
        {
            e.printStackTrace();
            e.getMessage();
            logger.error("Error: {}", e.getMessage());
        }
        return null;
    }
}
