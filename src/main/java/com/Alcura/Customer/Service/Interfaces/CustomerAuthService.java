package com.Alcura.Customer.Service.Interfaces;

import com.Alcura.Customer.DTO.LoginRequest;
import com.Alcura.Customer.DTO.RegisterRequest;
import com.Alcura.Customer.Model.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerAuthService
{
    ResponseEntity<String> registerCustomer (RegisterRequest request, HttpSession session);
    ResponseEntity<String> verifyOtp (String otp, HttpSession session);
    void updatePassword(String email, String newPassword);
    List<Customer> getAllCustomersWithFaceData();

}
