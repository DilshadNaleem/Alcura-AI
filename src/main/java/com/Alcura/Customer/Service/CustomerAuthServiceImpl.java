package com.Alcura.Customer.Service;

import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.DTO.RegisterRequest;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.Alcura.Customer.Service.Interfaces.CustomerAuthService;
import com.Alcura.Customer.Service.Interfaces.EmailService;
import com.Alcura.Customer.Service.Interfaces.OtpService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CustomerAuthServiceImpl implements CustomerAuthService
{
    private final CustomerRepository customerRepository;
    private final hashPassword hashPassword;
    private final CusUniqueID cusUniqueID;
    private final OtpService otpService;
    private  final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerAuthService.class);

    public CustomerAuthServiceImpl (CustomerRepository customerRepository,
                                hashPassword hashPassword, CusUniqueID cusUniqueID,
                                OtpService otpService, EmailService emailService)
    {
        this.customerRepository = customerRepository;
        this.hashPassword = hashPassword;
        this.cusUniqueID = cusUniqueID;
        this.otpService = otpService;
        this.emailService = emailService;
    }

     @Override
    public void updatePassword (String email, String newPassword)
     {
         try
         {
             Customer customer = customerRepository.findByEmail(email);
             String hashedPassword = hashPassword.hashPassword(newPassword);
             customer.setPassword(hashedPassword);
             customerRepository.save(customer);
         } catch (Exception e) {
             logger.error("Failed to update password for {}", email,e);
             throw new RuntimeException("Password update failed", e);
         }
         {
             throw new RuntimeException("Customer not found with email: " + email);
         }
     }

    @Override
    public List<Customer> getAllCustomersWithFaceData() {
        return customerRepository.findByFaceDataIsNotNull();
    }

    @Override
    public ResponseEntity<String> registerCustomer(RegisterRequest request, HttpSession session) {
        try
        {
            if(!request.getPassword().equals(request.getConfirmpassword()))
            {
                return ResponseEntity.badRequest().body("Passwords dont match");
            }

            Customer existingCustomer = customerRepository.findByEmail(request.getEmail().toLowerCase().trim());
            if (existingCustomer != null)
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email is already exists");
            }

            Customer customer = new Customer();
            customer.setFirstName(request.getFirstname());
            customer.setLastName(request.getLastname());
            customer.setPassword(hashPassword.hashPassword(request.getPassword()));
            customer.setEmail(request.getEmail());
            customer.setContactNumber(request.getContact_number());
            customer.setNic(request.getNic());
            customer.setFaceData(request.getFaceData());
            customer.setCustomer_type("Customer");
            customer.setStatus(0);
            customer.setImage("/Customer/image/profile/image.jpeg");

            customer = cusUniqueID.createCustomer(customer);

            String otp = otpService.generateOtp();
            otpService.storeOtp(session,customer.getEmail(),otp);


            try
            {
                emailService.sendVerificationEmail(customer.getEmail(),otp);
                logger.info("OTP send Successfully to {}", customer.getEmail());
                return ResponseEntity .status(HttpStatus.CREATED)
                        .body("Registration Successfull. Please check your email for OTP");
            }
            catch (Exception e)
            {
                logger.error("Failed to send OTP email to {}", customer.getEmail(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Registration successful but failed to send OTP email. Please contact support.");
            }
        }
        catch (Exception e)
        {
            logger.error("Registration failed",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed due to to server error!");
        }
    }

    public ResponseEntity<String> verifyOtp (String otp, HttpSession session)
    {
        String email = (String) session.getAttribute("verificationEmail");
        if(email == null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("OTP Session expired or invalid");
        }

        if(otpService.validateOtp(session,email,otp))
        {
            Customer customer = customerRepository.findByEmail(email);
            if (customer != null)
            {
                customer.setStatus(1);
                customerRepository.save(customer);
                session.removeAttribute("verificationOtp");
                session.removeAttribute("verificationEmail");
                return ResponseEntity.ok("Account Verified Successfully!");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid OTP");
    }

    public byte[] getDefaultImageBytes() throws IOException
    {
        Path imagePath = Paths.get("src/main/image.jpeg");
        return Files.readAllBytes(imagePath);
    }

}
