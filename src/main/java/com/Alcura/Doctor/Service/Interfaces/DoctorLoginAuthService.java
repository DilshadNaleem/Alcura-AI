package com.Alcura.Doctor.Service.Interfaces;

import com.Alcura.Doctor.DTO.DRLoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DoctorLoginAuthService
{
    ResponseEntity<String> loginCustomer (DRLoginRequest request , HttpSession session);
}
