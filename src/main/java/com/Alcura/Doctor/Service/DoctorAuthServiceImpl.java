package com.Alcura.Doctor.Service;

import com.Alcura.Customer.Service.Interfaces.EmailService;
import com.Alcura.Customer.Service.Interfaces.OtpService;
import com.Alcura.Customer.Service.hashPassword;
import com.Alcura.Doctor.DTO.DoctorRegisterRequest;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.Doctor.Service.Interfaces.DoctorAuthService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DoctorAuthServiceImpl implements DoctorAuthService {
    private final DoctorRepository doctorRepository;
    private final hashPassword hashPassword;
    private final OtpService otpService;
    private final EmailService emailService;
    private final DoctorUniqueId doctorUniqueId;
    private static final Logger logger = LoggerFactory.getLogger(DoctorAuthServiceImpl.class);

    // Default image path in resources
    private static final String DEFAULT_PROFILE_IMAGE_PATH = "static/Customer/image/profile/image.jpeg";

    public DoctorAuthServiceImpl(DoctorRepository doctorRepository,
                                 OtpService otpService,
                                 EmailService emailService,
                                 hashPassword hashPassword,
                                 DoctorUniqueId doctorUniqueId) {
        this.doctorRepository = doctorRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.hashPassword = hashPassword;
        this.doctorUniqueId = doctorUniqueId;
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        try {
            Doctor doctor = doctorRepository.findByemail(email);
            if (doctor == null) {
                logger.error("Doctor email is empty");
                throw new RuntimeException("Doctor not found with email " + email);
            }

            String hashPsw = hashPassword.hashPassword(newPassword);
            doctor.setPassword(hashPsw);
            doctorRepository.save(doctor);
            logger.info("Admin Password Updated : {}", hashPsw);
        } catch (Exception e) {
            logger.error("Failed to update password for {}", email, e);
            throw new RuntimeException("Password Update Failed " + e.getMessage());
        }
    }

    @Override
    public List<Doctor> getAllDoctorWithFaceData() {
        return doctorRepository.findByFaceDataIsNotNull();
    }

    @Override
    public ResponseEntity<String> registerDoctor(DoctorRegisterRequest request, HttpSession session) {
        try {
            Doctor existingDoctor = doctorRepository.findByEmailAndStatus(request.getEmail().toLowerCase().trim(), 1);

            if (existingDoctor != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already Registered");
            }

            Doctor doctor = new Doctor();
            doctor.setFirstName(request.getFirstName());
            doctor.setLastName(request.getLastName());
            doctor.setPassword(hashPassword.hashPassword(request.getPassword()));
            doctor.setEmail(request.getEmail());
            doctor.setContactNumber(request.getContactNumber());
            doctor.setNic(request.getNIC());
            doctor.setFaceData(request.getFaceData());
            doctor.setDoctorType("Doctor");
            doctor.isFirstLogin(false);
            doctor.setStatus(0);

            // Set default image from resources
            byte[] defaultImage = loadDefaultProfileImage();
            if (defaultImage != null) {
                doctor.setImage(defaultImage);
            } else {
                logger.warn("Default profile image not found, setting image to null");
                doctor.setImage(null);
            }

            doctor = doctorUniqueId.createDoctor(doctor);
            String otp = otpService.generateOtp();
            otpService.storeOtp(session, doctor.getEmail(), otp);

            try {
                emailService.sendVerificationEmail(doctor.getEmail(), otp);
                logger.info("OTP sent successfully to {}", doctor.getEmail());

                return ResponseEntity.status(HttpStatus.CREATED).body(
                        "Registration Successful! Please verify your email"
                );
            } catch (Exception e) {
                logger.error("Error when sending OTP", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        "Registration Successful but failed to send OTP. Please contact support"
                );
            }
        } catch (Exception e) {
            logger.error("Registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "Registration failed due to server error. Please try again"
            );
        }
    }

    @Override
    public ResponseEntity<String> verifyOtp(String otp, HttpSession session) {
        String email = (String) session.getAttribute("verificationEmail");
        if (email == null) {
            logger.error("email is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("OTP session Expired or Invalid");
        }

        if (otpService.validateOtp(session, email, otp)) {
            Doctor doctor = doctorRepository.findByemail(email);
            if (doctor != null) {
                doctor.setStatus(1);
                doctorRepository.save(doctor);
                session.removeAttribute("verificationOtp");
                session.removeAttribute("verificationEmail");
                return ResponseEntity.ok("Account verified Successfully!");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not Found!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid OTP");
    }

    /**
     * Loads the default profile image from resources
     * @return byte array of the image or null if not found
     */
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