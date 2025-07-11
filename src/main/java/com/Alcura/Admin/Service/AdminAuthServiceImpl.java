package com.Alcura.Admin.Service;

import com.Alcura.Admin.DTO.AdminRegisterRequest;
import com.Alcura.Admin.Model.Admin;
import com.Alcura.Admin.Repository.AdminRepository;
import com.Alcura.Admin.Service.Interfaces.AdminAuthService;
import com.Alcura.Customer.Service.Interfaces.EmailService;
import com.Alcura.Customer.Service.Interfaces.OtpService;
import com.Alcura.Customer.Service.hashPassword;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {
    private final AdminRepository adminRepository;
    private final hashPassword hashPassword;
    private final OtpService otpService;
    private final EmailService emailService;
    private final AdminUniqueId adminUniqueId;
    private static final Logger logger = LoggerFactory.getLogger(AdminAuthServiceImpl.class);

    private static final String DEFAULT_PROFILE_IMAGE_PATH = "static/Customer/image/profile/image.jpeg";

    public AdminAuthServiceImpl(AdminRepository adminRepository, hashPassword hashPassword, OtpService otpService,
                                EmailService emailService, AdminUniqueId adminUniqueId) {
        this.adminRepository = adminRepository;
        this.hashPassword = hashPassword;
        this.adminUniqueId = adminUniqueId;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        try {
            Admin admin = adminRepository.findByEmail(email);
            if (admin == null) {
                throw new RuntimeException("Admin not found with email: " + email);
            }
            String hashedPassword = hashPassword.hashPassword(newPassword);
            admin.setPassword(hashedPassword);
            adminRepository.save(admin);
        } catch (Exception e) {
            logger.error("Failed to update password for {}", email, e);
            throw new RuntimeException("Password Update Failed", e);
        }
    }

    @Override
    public List<Admin> getAllAdminWithFaceData() {
        return adminRepository.findByFaceDataIsNotNull();
    }

    @Override
    public ResponseEntity<String> registerAdmin(AdminRegisterRequest request, HttpSession session) {
        try {


            Admin existingAdmin = adminRepository.findByEmailAndStatus(request.getEmail().toLowerCase().trim(),1);
            if (existingAdmin != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
            }

            Admin admin = new Admin();
            admin.setFirstName(request.getFirstname());
            admin.setLastName(request.getLastname());
            admin.setPassword(hashPassword.hashPassword(request.getPassword()));
            admin.setEmail(request.getEmail());
            admin.setContactNumber(request.getContactNumber());
            admin.setNic(request.getNic());
            admin.setFaceData(request.getFaceData());
            admin.setAdminType("Admin");
            admin.setStatus(0);

            byte[] defaultImage = loadDefaultProfileImage();
            if (defaultImage != null) {
                admin.setImage(defaultImage);
            } else {
                logger.warn("Default profile image not found, setting image to null");
                admin.setImage(null);
            }

            admin = adminUniqueId.createAdmin(admin);

            String otp = otpService.generateOtp();
            otpService.storeOtp(session, admin.getEmail(), otp);

            try {
                emailService.sendVerificationEmail(admin.getEmail(), otp);
                logger.info("OTP sent successfully to {}", admin.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED).body(
                        "Registration successful! Please verify your email"
                );
            } catch (Exception e) {
                logger.error("Failed to send OTP email to {}", admin.getEmail(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Registration successful but failed to send OTP. Please contact support.");
            }
        } catch (Exception e) {
            logger.error("Registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed due to server error. Please try again later.");
        }
    }

    @Override
    public ResponseEntity<String> verifyOtp(String otp, HttpSession session) {
        String email = (String) session.getAttribute("verificationEmail");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("OTP session expired or invalid");
        }

        if (otpService.validateOtp(session, email, otp)) {
            Admin admin = adminRepository.findByEmail(email);
            if (admin != null) {
                admin.setStatus(1);
                adminRepository.save(admin);
                session.removeAttribute("verificationOtp");
                session.removeAttribute("verificationEmail");
                return ResponseEntity.ok("Account verified successfully!");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Admin not found");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid OTP");
    }

    private byte[] loadDefaultProfileImage() {
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_PROFILE_IMAGE_PATH);
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    return inputStream.readAllBytes();
                }
            } else {
                logger.error("Default profile image not found at: {}", DEFAULT_PROFILE_IMAGE_PATH);
                return null;
            }
        } catch (IOException e) {
            logger.error("Error loading default profile image", e);
            return null;
        }
    }
}