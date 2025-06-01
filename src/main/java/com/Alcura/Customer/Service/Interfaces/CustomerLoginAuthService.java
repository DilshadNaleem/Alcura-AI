package com.Alcura.Customer.Service.Interfaces;

import com.Alcura.Customer.DTO.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CustomerLoginAuthService
{
    ResponseEntity<String> loginCustomer(LoginRequest request, HttpSession session);
}
