package com.Alcura.Admin.Controller;

import com.Alcura.Admin.Service.FeedbackService;
import com.Alcura.Customer.Model.FeedBack;
import com.Alcura.Customer.Repository.FeedbackRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/Admin")
public class FeedbackController
{
    @Autowired
    private FeedbackRepo feedbackRepo;

    @Autowired
    private FeedbackService feedbackService;

    Logger logger = LoggerFactory.getLogger(FeedbackController.class);


    @GetMapping("/Feedbacks")
    public String form(Model model)
    {
        List<FeedBack> feedback = feedbackRepo.findAll();
        model.addAttribute("feedbacks", feedback);
        return "/Admin/FeedBackManagement";
    }

    @PostMapping("/DeleteFeedback/{id}")
    public String update(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        logger.info("Received : {}", id);

        if (id == null) {
            logger.info("Id Is Null");
            redirectAttributes.addFlashAttribute("error", "Invalid ID");
        } else {
            feedbackService.deleteRecords(id);
            redirectAttributes.addFlashAttribute("success", "Deleted Successfully!");
        }

        return "redirect:/Admin/Feedbacks";
    }


}
