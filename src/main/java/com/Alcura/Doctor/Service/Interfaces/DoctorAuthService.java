package com.Alcura.Doctor.Service.Interfaces;

import com.Alcura.Doctor.DTO.DoctorRegisterRequest;
import com.Alcura.Doctor.Model.Doctor;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DoctorAuthService
{
    ResponseEntity<String> registerDoctor(DoctorRegisterRequest request, HttpSession session);
    ResponseEntity<String> verifyOtp (String otp, HttpSession session);
    void updatePassword(String email, String newPassword);
    List<Doctor> getAllDoctorWithFaceData();
}
