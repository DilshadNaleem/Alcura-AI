package com.Alcura.Doctor.Service;

import com.Alcura.Customer.Service.hashPassword;
import com.Alcura.Doctor.DTO.DRLoginRequest;
import com.Alcura.Doctor.Model.Doctor;
import com.Alcura.Doctor.Repository.DoctorRepository;
import com.Alcura.Doctor.Service.Interfaces.DoctorLoginAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DRLoginAuthServiceImpl implements DoctorLoginAuthService {
    private final DoctorRepository doctorRepository;
    private final hashPassword hashPassword;

    public DRLoginAuthServiceImpl(DoctorRepository doctorRepository,
                                  hashPassword hashPassword)
    {
        this.doctorRepository = doctorRepository;
        this.hashPassword = hashPassword;
    }

    @Override
    public ResponseEntity<String> loginCustomer(DRLoginRequest request, HttpSession session) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        String normalizedPassword = request.getSigningPassword().trim();

        Doctor doctor = doctorRepository.findByEmailAndStatus(normalizedEmail, 1);

        if (doctor == null)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Account not verified. Please verify your account to Login");
        }


        String hashedPassword = hashPassword.hashPassword(normalizedPassword);
        String storedPassword = doctor.getPassword().trim();

        System.out.println("Generated hashed Password: " + hashedPassword);
        System.out.println("Stored hashed Password: " + doctor.getPassword());

        if (storedPassword.equals(hashedPassword))
        {
            session.setAttribute("email", normalizedEmail);
            System.out.println("Sessioned Email: " + session.getAttribute("email"));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/Doctor/Dashboard");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
         else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
}
