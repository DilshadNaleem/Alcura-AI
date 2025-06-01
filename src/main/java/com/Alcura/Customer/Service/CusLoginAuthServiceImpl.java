package com.Alcura.Customer.Service;

import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.Model.Customer;
import com.Alcura.Customer.Repository.CustomerRepository;
import com.Alcura.Customer.Service.Interfaces.CustomerLoginAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CusLoginAuthServiceImpl implements CustomerLoginAuthService {

    private final CustomerRepository customerRepository;
    private final hashPassword hashPassword;

    public CusLoginAuthServiceImpl(CustomerRepository customerRepository,
                                   hashPassword hashPassword)
    {
        this.customerRepository = customerRepository;
        this.hashPassword = hashPassword;
    }

    @Override
    public ResponseEntity<String> loginCustomer(LoginRequest request, HttpSession session) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        String normalizedPassword = request.getSigninPassword().trim();

        Customer customer = customerRepository.findByEmail(normalizedEmail);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        // Check if customer is active (status == 1)
        if (customer.getStatus() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Account not verified. Please verify your account first.");
        }

        String hashedPassword = hashPassword.hashPassword(normalizedPassword);
        String storedPassword = customer.getPassword().trim();
        System.out.println("Generated hashed password: " + hashedPassword);
        System.out.println("Stored hashed password: " + customer.getPassword().trim());
        if (storedPassword.equals(hashedPassword)) {
            session.setAttribute("email", normalizedEmail);
            System.out.println("Sesioned Email " + session.getAttribute("email"));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/Customer/Dashboard");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
}
