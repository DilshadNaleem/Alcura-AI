package com.Alcura.Customer.Controller;

import com.Alcura.Customer.DTO.EmergencyRequest;
import com.Alcura.Customer.Repository.EmergencyRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;

@Controller
@RequestMapping("/Customer")
public class SOSCancelController
{
    @Autowired
    EmergencyRequestRepository emergencyRequestRepository;
    Logger logger = LoggerFactory.getLogger(SOSCancelController.class);

    @PostMapping("/CancelSOS")
    public String cancelSOS(@RequestParam("trackingId") String trackingId, PrintWriter writer)
    {
        try {
            logger.info("Received Data :{}", trackingId);
            EmergencyRequest emergencyRequest = emergencyRequestRepository.findByTrackingId(trackingId);
            emergencyRequest.setStatus("CANCELLED");
            emergencyRequestRepository.save(emergencyRequest);

            writer.println("<script>");
            writer.println("alert('Successfully Canceled the Request');");
            writer.println("window.location.href='/Customer/SOS';");
            writer.println("</script>");

        }
        catch (Exception e)
        {
            e.getMessage();
            e.printStackTrace();
            logger.error("Errror: {}", e.getMessage());
        }
        return null;
    }
}
