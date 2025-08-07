package com.Alcura.Doctor.Controller;

import com.Alcura.Doctor.DTO.DRLoginRequest;
import com.Alcura.Doctor.Service.DRLoginAuthServiceImpl;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/Doctor")
public class DRLoginController {
    private static final Logger logger = LoggerFactory.getLogger(DRLoginController.class);
    private final DRLoginAuthServiceImpl drLoginAuthService;
    private final DoctorRepository doctorRepository;

    public DRLoginController(DRLoginAuthServiceImpl drLoginAuthService,
                             DoctorRepository doctorRepository) {
        this.drLoginAuthService = drLoginAuthService;
        this.doctorRepository = doctorRepository;
        logger.info("DRLoginController initialized");
    }

    @PostMapping("/Login")
    public void Login(@ModelAttribute DRLoginRequest request,
                      HttpServletResponse response,
                      HttpSession session) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type = 'text/javascript'>");

        try {
            logger.debug("Login attempt for email: {}", request.getEmail());
            ResponseEntity<String> result = drLoginAuthService.loginCustomer(request, session);

            if (result.getStatusCode() == HttpStatus.FOUND) {
                logger.info("Successful login for email: {}", request.getEmail());
                Doctor doctor = doctorRepository.findByEmailAndStatus(request.getEmail().toLowerCase().trim(), 1);

                if (doctor != null) {
                    logger.debug("Found doctor with ID: {}", doctor.getId());

                } else {
                    logger.warn("Doctor not found in database for email: {}", request.getEmail());
                }

                out.println("alert('Login Successful');");
                out.println("window.location.href = '/Doctor/Dashboard';");
            }
            else if (result.getStatusCode() == HttpStatus.FORBIDDEN) {
                String message = result.getBody() != null ?
                        result.getBody() : "Your Account is not verified. Please Verify!";
                logger.warn("Login forbidden for email: {} - Reason: {}", request.getEmail(), message);
                out.println("alert('" + message + "');");
                out.println("window.location.href = '/Doctor/Signing';");
            }
            else {
                logger.warn("Failed login attempt for email: {}", request.getEmail());
                out.println("alert('Invalid email or Password. Please Try Again!');");
                out.println("window.location.href = '/Doctor/Signing';");
            }
        }
        catch (Exception e) {
            logger.error("Error during login process for email: {}", request.getEmail(), e);
            out.println("alert('An Error Occurred during login please try later');");
            out.println("window.location.href = '/Doctor/Signing'");
        } finally {
            try {
                out.println("</script>");
                out.close();
                logger.debug("Response closed successfully");
            } catch (Exception e) {
                logger.error("Error closing response writer", e);
            }
        }
    }
}