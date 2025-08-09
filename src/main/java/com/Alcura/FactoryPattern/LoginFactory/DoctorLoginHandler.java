package com.Alcura.FactoryPattern.LoginFactory;

import com.Alcura.Doctor.DTO.DRLoginRequest;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.Doctor.Service.DRLoginAuthServiceImpl;
import com.Alcura.FactoryPattern.ServiceFactory.BaseLoginHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class DoctorLoginHandler extends BaseLoginHandler
{
    private final DRLoginAuthServiceImpl authService;
    private final DRLoginRequest request;
    private final DoctorRepository doctorRepository;
    private static final Logger logger = LoggerFactory.getLogger(DoctorLoginHandler.class);

    public DoctorLoginHandler(DRLoginAuthServiceImpl authService, DRLoginRequest request,
                              DoctorRepository doctorRepository,
                              HttpServletResponse response, HttpSession session) throws IOException {
        super(response, session, "/Doctor/Dashboard", "/Doctor/Signing");
        this.authService = authService;
        this.request = request;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public void handleLogin(HttpServletResponse response, HttpSession session) throws Exception {
        try {
            logger.debug("Login attempt for email: {}", request.getEmail());
            ResponseEntity<String> result = authService.loginCustomer(request, session);

            if (result.getStatusCode() == HttpStatus.FOUND) {
                logger.info("Successful login for email: {}", request.getEmail());
                Doctor doctor = doctorRepository.findByEmailAndStatus(request.getEmail().toLowerCase().trim(), 1);

                if (doctor != null) {
                    logger.debug("Found doctor with ID: {}", doctor.getId());
                } else {
                    logger.warn("Doctor not found in database for email: {}", request.getEmail());
                }
                handleSuccess();
            } else if (result.getStatusCode() == HttpStatus.FORBIDDEN) {
                String message = result.getBody() != null ?
                        result.getBody() : "Your Account is not verified. Please Verify!";
                logger.warn("Login forbidden for email: {} - Reason: {}", request.getEmail(), message);
                handleForbidden(message);
            } else {
                logger.warn("Failed login attempt for email: {}", request.getEmail());
                handleFailure("Invalid email or Password. Please Try Again!");
            }
        } catch (Exception e) {
            logger.error("Error during login process for email: {}", request.getEmail(), e);
            handleError();
        } finally {
            close();
        }
    }
}
