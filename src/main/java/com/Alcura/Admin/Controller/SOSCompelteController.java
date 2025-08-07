package com.Alcura.Admin.Controller;

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
@RequestMapping("/Admin")
public class SOSCompelteController
{
    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    Logger logger = LoggerFactory.getLogger(SOSCompelteController.class);

    @PostMapping("/CompleteSOS")
    public String completeSOS(@RequestParam("trackingId") String trackingId,
                              PrintWriter writer)
    {
        try {
            logger.info("Received: {}", trackingId);
            EmergencyRequest emergencyRequest = emergencyRequestRepository.findByTrackingId(trackingId);
            emergencyRequest.setStatus("COMPLETED");
            emergencyRequestRepository.save(emergencyRequest);

            writer.println("<script>");
            writer.println("alert('SOS Request Completed');");
            writer.println("window.location.href = '/Admin/Dashboard';");
            writer.println("</script>");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            e.getMessage();
        }
        return null;
    }
}
