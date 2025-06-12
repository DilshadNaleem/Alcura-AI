package com.Alcura.Admin.Service.Interfaces;

import com.Alcura.Admin.DTO.AdminLoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AdminLoginAuth {
    ResponseEntity<String> loginAdmin (AdminLoginRequest loginRequest, HttpSession session);
}
