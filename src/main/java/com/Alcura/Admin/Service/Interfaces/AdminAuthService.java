package com.Alcura.Admin.Service.Interfaces;

import com.Alcura.Admin.DTO.AdminRegisterRequest;
import com.Alcura.Admin.Model.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminAuthService
{
    ResponseEntity<String> registerAdmin (AdminRegisterRequest request, HttpSession session);
    ResponseEntity<String> verifyOtp (String otp, HttpSession session);
    void updatePassword(String email, String newPassword);
    List<Admin> getAllAdminWithFaceData();
}
