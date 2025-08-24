package com.Alcura.Admin.Controller.Doctor;

import com.Alcura.Admin.Model.DoctorPrice;
import com.Alcura.Admin.Repository.DoctorPriceRepo;
import com.Alcura.Admin.Service.AdminAppointmentRescheduleEmailService;
import com.Alcura.Admin.Service.PriceApprovalNotifier;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
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
public class ApproveDoctorPriceController {

    private DoctorPriceRepo doctorPriceRepo;
    Logger logger = LoggerFactory.getLogger(ApproveDoctorPriceController.class);
    private final PriceApprovalNotifier emailService;
    private final DoctorRepository doctorRepository;

    public ApproveDoctorPriceController(PriceApprovalNotifier emailService,
                                        DoctorRepository doctorRepository,
                                        DoctorPriceRepo doctorPriceRepo) {
        this.emailService = emailService;
        this.doctorRepository = doctorRepository;
        this.doctorPriceRepo = doctorPriceRepo;
    }

    @PostMapping("/ApproveDoctorPrice")
    public String approve(@RequestParam("id") int id,
                          @RequestParam("finalPrice") Float newPrice,
                          @RequestParam("doctorEmail") String doctorEmail,
                          PrintWriter out,
                          HttpSession session) {
        logger.info("Received request to approve doctor price - ID: {}, New Price: {}, Doctor Email: {}",
                id, newPrice, doctorEmail);

        try {
            String email = (String) session.getAttribute("email");
            if (email == null) {
                logger.warn("Session email is null - redirecting to signing page");
                return "redirect:/Admin/Signing";
            }

            logger.debug("Session email found: {}", email);

            Doctor doctor = doctorRepository.findByemail(doctorEmail);
            if (doctor == null) {
                logger.error("Doctor not found for email: {}", doctorEmail);
                throw new RuntimeException("Doctor not found for email: " + doctorEmail);
            }
            logger.debug("Doctor found: {}", doctor.toString());

            DoctorPrice doctorPrice = doctorPriceRepo.findById(id);
            if (doctorPrice == null) {
                logger.error("DoctorPrice not found for ID: {}", id);
                throw new RuntimeException("DoctorPrice not found for ID: " + id);
            }
            logger.debug("Current DoctorPrice status: {}", doctorPrice.getStatus());

            doctorPrice.setStatus("Success");
            doctorPrice.setNewPrice(newPrice);
            doctorPriceRepo.save(doctorPrice);
            logger.info("DoctorPrice updated successfully - ID: {}, New Status: Success, New Price: {}",
                    id, newPrice);

            out.println("<script>");
            out.println("alert('Successfully Updated to Success');");
            out.println("window.location.href = '/Admin/Manage_Doctor/Price';");
            out.println("</script>");

            String subject = "Your Price for per booking has been Approved";
            String body = String.format(
                    "Dear Doctor,\n\n" +
                            "Your Price Per Appointment has been Approved.\n\n" +
                            "Thank you, \n Alcura Team"
            );

            logger.debug("Preparing to send email - Recipient: {}, Subject: {}, Body: {}",
                    doctorEmail, subject, body);

            emailService.notifyObservers(doctorEmail, subject, body);

            logger.info("Email notification triggered for doctor: {}", doctorEmail);
            logger.debug("Verifying if any observers are registered...");

            // Additional debug to check if observers are registered
            if (emailService instanceof PriceApprovalNotifier) {
                PriceApprovalNotifier notifier = (PriceApprovalNotifier) emailService;
                logger.debug("Number of registered observers: {}", notifier.getObserverCount());
            }
        } catch (Exception e) {
            logger.error("Error in approveDoctorPrice - ID: {}, Doctor Email: {}", id, doctorEmail, e);
            e.printStackTrace();
        }

        return null;
    }
}